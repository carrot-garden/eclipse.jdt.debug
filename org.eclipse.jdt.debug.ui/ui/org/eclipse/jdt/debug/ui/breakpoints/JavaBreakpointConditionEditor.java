/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.debug.ui.breakpoints;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.debug.core.IJavaLineBreakpoint;
import org.eclipse.jdt.internal.debug.ui.BreakpointUtils;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.JDISourceViewer;
import org.eclipse.jdt.internal.debug.ui.breakpoints.AbstractJavaBreakpointEditor;
import org.eclipse.jdt.internal.debug.ui.contentassist.IJavaDebugContentAssistContext;
import org.eclipse.jdt.internal.debug.ui.contentassist.JavaDebugContentAssistProcessor;
import org.eclipse.jdt.internal.debug.ui.contentassist.TypeContext;
import org.eclipse.jdt.internal.debug.ui.display.DisplayViewerConfiguration;
import org.eclipse.jdt.internal.debug.ui.propertypages.PropertyPageMessages;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.TextViewerUndoManager;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

/**
 * Controls to edit a breakpoint's conditional expression, condition enabled state,
 * and suspend policy (suspend when condition is <code>true</code> or when the value of the
 * conditional expression changes).
 * <p>
 * The controls are intended to be embedded in a composite provided by the client - for
 * example, in a dialog. Clients must call {@link #createControl(Composite)} as the first
 * life cycle method after instantiation. Clients may then call {@link #setInput(Object)}
 * with the breakpoint object to be displayed/edited. Changes are not applied to the
 * breakpoint until {@link #doSave()} is called. The method {@link #isDirty()} may be used
 * to determine if any changes have been made in the editor, and {@link #getStatus()} may
 * be used to determine if the editor settings are valid. Clients can register for
 * property change notification ({@link #addPropertyListener(IPropertyListener)}). The editor
 * will fire a property change each time a setting is modified. The same editor can be
 * used to display different breakpoints by calling {@link #setInput(Object)} with different
 * breakpoint objects. 
 * </p>
 * 
 * @since 3.5
 */
public final class JavaBreakpointConditionEditor extends AbstractJavaBreakpointEditor {
	
	private Button fConditional;
	private Combo fSuspendBehavior;
	private JDISourceViewer fViewer;
	private IContentAssistProcessor fCompletionProcessor;	
	private IJavaLineBreakpoint fBreakpoint;
	private IHandlerService fHandlerService;
	private IHandler fHandler;
	private IHandlerActivation fActivation;
    private IDocumentListener fDocumentListener;	
    
    /**
     * Property id for breakpoint condition expression.
     */
    public static final int PROP_CONDITION = 0x1001;
    
    /**
     * Property id for breakpoint condition enabled state.
     */
    public static final int PROP_CONDITION_ENABLED = 0x1002;
    
    /**
     * Property id for breakpoint condition suspend policy.
     */
    public static final int PROP_CONDITION_SUSPEND_POLICY = 0x1003;
	
	/**
	 * Adds the given property listener to this editor. Property changes
	 * are reported on the breakpoint being edited. Property identifiers
	 * are breakpoint attribute keys.
	 * 
	 * @param listener listener
	 */
	public void addPropertyListener(IPropertyListener listener) {
		super.addPropertyListener(listener);
	}
	
	/**
	 * Removes the property listener from this editor.
	 * 
	 * @param listener listener
	 */
	public void removePropertyListener(IPropertyListener listener) {
		super.removePropertyListener(listener);
	}
	
	/**
	 * Sets the breakpoint to editor or <code>null</code> if none.
	 * 
	 * @param input breakpoint or <code>null</code>
	 * @throws CoreException if unable to access breakpoint attributes
	 */
	public void setInput(Object input) throws CoreException {
		if (input instanceof IJavaLineBreakpoint) {
			setBreakpoint((IJavaLineBreakpoint)input);
		} else {
			setBreakpoint(null);
		}
	}
	
	/**
	 * Sets the breakpoint to edit. Has no effect if the breakpoint responds
	 * <code>false</code> to {@link IJavaLineBreakpoint#supportsCondition()}.
	 * The same editor can be used iteratively for different breakpoints.
	 * 
	 * @param breakpoint the breakpoint to edit or <code>null</code> if none
	 * @exception CoreException if unable to access breakpoint attributes
	 */
	private void setBreakpoint(IJavaLineBreakpoint breakpoint) throws CoreException {
		fBreakpoint = breakpoint;
		if (fDocumentListener != null) {
			fViewer.getDocument().removeDocumentListener(fDocumentListener);
			fDocumentListener = null;
		}
		fViewer.unconfigure();
		IDocument document = new Document();
		JDIDebugUIPlugin.getDefault().getJavaTextTools().setupJavaDocumentPartitioner(document, IJavaPartitions.JAVA_PARTITIONING);
		fViewer.setInput(document);
		String condition = null;
		IType type = null;
		boolean enabled = false;
		boolean whenTrue = true;
		if (breakpoint != null) {
			if (breakpoint.supportsCondition()) {
				condition = breakpoint.getCondition();
				enabled = breakpoint.isConditionEnabled();
				whenTrue = breakpoint.isConditionSuspendOnTrue();
				type = BreakpointUtils.getType(breakpoint);
			}
		}
		IJavaDebugContentAssistContext context = null;
		if (type == null || breakpoint == null) {
			context = new TypeContext(null, -1);
		} else {
			String source = null;
			ICompilationUnit compilationUnit = type.getCompilationUnit();
			if (compilationUnit != null && compilationUnit.getJavaProject().getProject().exists()) {
				source = compilationUnit.getSource();
			} 
			else {
				IClassFile classFile = type.getClassFile();
				if (classFile != null) {
					source = classFile.getSource();
				}
			}
			int lineNumber = breakpoint.getMarker().getAttribute(IMarker.LINE_NUMBER, -1);
			int position= -1;
			if (source != null && lineNumber != -1) {
				try {
					position = new Document(source).getLineOffset(lineNumber - 1);
				} 
				catch (BadLocationException e) {JDIDebugUIPlugin.log(e);}
			}
			context = new TypeContext(type, position);
		}
		fCompletionProcessor = new JavaDebugContentAssistProcessor(context);
		fViewer.configure(new DisplayViewerConfiguration() {
			public IContentAssistProcessor getContentAssistantProcessor() {
					return fCompletionProcessor;
			}
		});
		document.set((condition == null ? "" : condition)); //$NON-NLS-1$
		fViewer.setUndoManager(new TextViewerUndoManager(10));
		fViewer.getUndoManager().connect(fViewer);
		fDocumentListener = new IDocumentListener() {
            public void documentAboutToBeChanged(DocumentEvent event) {
            }
            public void documentChanged(DocumentEvent event) {
            	setDirty(PROP_CONDITION);
            }
        };
		fViewer.getDocument().addDocumentListener(fDocumentListener);		
		fConditional.setSelection(enabled);
		if (whenTrue) {
			fSuspendBehavior.select(0);
		} else {
			fSuspendBehavior.select(1);
		}
		setEnabled(enabled && breakpoint != null && breakpoint.supportsCondition(), false);
		setDirty(false);
	}
	
	/**
	 * Creates the condition editor widgets and returns the top level
	 * control.
	 * 
	 * @param parent composite to embed the editor controls in
	 * @return top level control
	 */
	public Control createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginLeft = 8;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Composite controls = new Composite(composite, SWT.NONE);
		layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		controls.setLayout(layout);
		controls.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		fConditional = new Button(controls, SWT.CHECK);
		fConditional.setText(PropertyPageMessages.JavaBreakpointConditionEditor_0);
		fConditional.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		fConditional.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean checked = fConditional.getSelection();
				setEnabled(checked, true);
				setDirty(PROP_CONDITION_ENABLED);
			}
		});
		fSuspendBehavior = new Combo(controls, SWT.DROP_DOWN | SWT.READ_ONLY);
		fSuspendBehavior.setItems(new String[]{PropertyPageMessages.JavaBreakpointConditionEditor_1, PropertyPageMessages.JavaBreakpointConditionEditor_2});
		fSuspendBehavior.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		fSuspendBehavior.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setDirty(PROP_CONDITION_SUSPEND_POLICY);
			}
		});
				
		fViewer = new JDISourceViewer(composite, null, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.LEFT_TO_RIGHT);
		fViewer.setEditable(false);
		ControlDecoration decoration = new ControlDecoration(fViewer.getControl(), SWT.TOP | SWT.LEFT);
		decoration.setShowOnlyOnFocus(true);
		FieldDecoration dec = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL);
		decoration.setImage(dec.getImage());
		decoration.setDescriptionText(dec.getDescription());
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		// set height/width hints based on font
		GC gc = new GC(fViewer.getTextWidget());
		gc.setFont(fViewer.getTextWidget().getFont());
		FontMetrics fontMetrics = gc.getFontMetrics();
		//gd.heightHint = Dialog.convertHeightInCharsToPixels(fontMetrics, 10);
		gd.widthHint = Dialog.convertWidthInCharsToPixels(fontMetrics, 40);
		gc.dispose();
		fViewer.getControl().setLayoutData(gd);
		fHandler = new AbstractHandler() {
			public Object execute(ExecutionEvent event) throws org.eclipse.core.commands.ExecutionException {
				fViewer.doOperation(ISourceViewer.CONTENTASSIST_PROPOSALS);
				return null;
			}
		};
		fHandlerService = (IHandlerService) PlatformUI.getWorkbench().getAdapter(IHandlerService.class);
		fViewer.getControl().addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				activateContentAssist();
			}
			public void focusLost(FocusEvent e) {
				deactivateContentAssist();
			}				
		});
		composite.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				dispose();
			}
		});
		return composite;
	}
	
	/**
	 * Disposes this editor and its controls. Once disposed, the editor can no
	 * longer be used.
	 */
	protected void dispose() {
		super.dispose();
		deactivateContentAssist();
		if (fDocumentListener != null) {
			fViewer.getDocument().removeDocumentListener(fDocumentListener);
		}
		fViewer.dispose();	
	}
	
	/**
	 * Gives focus to an appropriate control in the editor.
	 */
	public void setFocus() {
		fViewer.getControl().setFocus();
	}
	
	/**
	 * Saves current settings to the breakpoint being edited. Has no
	 * effect if a breakpoint is not currently being edited or if this
	 * editor is not dirty.
	 * 
	 * @exception CoreException if unable to update the breakpoint.
	 */
	public void doSave() throws CoreException {
		if (fBreakpoint != null && isDirty()) {
			fBreakpoint.setCondition(fViewer.getDocument().get().trim());
			fBreakpoint.setConditionEnabled(fConditional.getSelection());
			fBreakpoint.setConditionSuspendOnTrue(fSuspendBehavior.getSelectionIndex() == 0);
			setDirty(false);
		}
	}
	
	/**
	 * Returns a status describing whether the condition editor is in
	 * a valid state. Returns an OK status when all is good. For example, an error
	 * status is returned when the conditional expression is empty but enabled.
	 * 
	 * @return editor status.
	 */
	public IStatus getStatus() {
		if (fBreakpoint != null && fBreakpoint.supportsCondition()) {
			if (fConditional.getSelection()) {
				if (fViewer.getDocument().get().trim().length() == 0) {
					return new Status(IStatus.ERROR, JDIDebugUIPlugin.getUniqueIdentifier(),  PropertyPageMessages.BreakpointConditionEditor_1);
				}
			}
		}
		return Status.OK_STATUS;
	}
	
	/**
	 * Returns whether the editor needs saving.
	 *  
	 * @return whether the editor needs saving
	 */
	public boolean isDirty() {
		return super.isDirty();
	}
	
	private void activateContentAssist() {
		fActivation = fHandlerService.activateHandler(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS, fHandler);
	}

	private void deactivateContentAssist() {
		if(fActivation != null) {
			fHandlerService.deactivateHandler(fActivation);
			fActivation = null;
		}
	}	
	
	/**
	 * Enables controls based on whether the breakpoint's condition is enabled.
	 * 
	 * @param enabled whether to enable
	 */
	private void setEnabled(boolean enabled, boolean focus) {
	    fViewer.setEditable(enabled);
	    fViewer.getTextWidget().setEnabled(enabled);
	    fSuspendBehavior.setEnabled(enabled);
		if (enabled) {
			fViewer.updateViewerColors();
			if (focus) {
				setFocus();
			}
		} else {
			Color color = fViewer.getControl().getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
			fViewer.getTextWidget().setBackground(color);			
		}
	}

	/**
	 * Returns the breakpoint being edited or <code>null</code> if none.
	 * 
	 * @return breakpoint or <code>null</code>
	 */
	public Object getInput() {
		return fBreakpoint;
	}
}