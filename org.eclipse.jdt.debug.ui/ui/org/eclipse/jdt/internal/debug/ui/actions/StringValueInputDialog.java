/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.debug.ui.actions;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * A dialog which prompts the user to enter a new value for a
 * String variable. The user is given the option of entering the
 * literal value that they want assigned to the String or entering
 * an expression for evaluation.
 */
public class StringValueInputDialog extends ExpressionInputDialog {

    private Label fTextLabel;
    private TextViewer fTextViewer;
    private Button fTextButton;
    private Button fEvaluationButton;
    private Button fWrapText;
    private Group fTextGroup;
    
    private boolean fUseLiteralValue= true;
    private static final String PREF_PREFIX = "STRING_VALUE_DIALOG"; //$NON-NLS-1$
    private static final String PREF_USE_EVALUATION = PREF_PREFIX + ".USE_EVALUATION"; //$NON-NLS-1$
    private static final String PREF_WRAP_TEXT = PREF_PREFIX + "STRING_VALUE"; //$NON-NLS-1$

    /**
     * @param parentShell
     * @param variable
     */
    protected StringValueInputDialog(Shell parentShell, IJavaVariable variable) {
        super(parentShell, variable);
    }
    
    /**
     * Override superclass method to insert toggle buttons
     * immediately after the input area.
     */
    protected void createInputArea(Composite parent) {
        super.createInputArea(parent);
        createRadioButtons(parent);
    }

    /**
     * Override superclass method to create the appropriate viewer
     * (source viewer or simple text viewer) in the input area.
     */
    protected void populateInputArea() {
        boolean useEvaluation = JDIDebugUIPlugin.getDefault().getPreferenceStore().getBoolean(PREF_USE_EVALUATION);
        if (useEvaluation) {
            createSourceViewer();
            fUseLiteralValue= false;
            fEvaluationButton.setSelection(true);
        } else {
            createTextViewer();
            fTextButton.setSelection(true);
        }
    }

    /**
     * Creates the text viewer that allows the user to enter a new String
     * value.
     */
    private void createTextViewer() {
        fTextLabel= new Label(fInputArea, SWT.WRAP);
        fTextLabel.setText(ActionMessages.getString("StringValueInputDialog.0")); //$NON-NLS-1$
        
        fTextGroup= new Group(fInputArea, SWT.NONE);
        fTextGroup.setLayout(new GridLayout());
        fTextGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
        Composite parent= fTextGroup; 
        
        fTextViewer= new TextViewer(parent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        fTextViewer.setDocument(new Document());
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.widthHint= 300;
        gridData.heightHint= 150;
        fTextViewer.getTextWidget().setLayoutData(gridData);
        try {
            String valueString = fVariable.getValue().getValueString();
            fTextViewer.getDocument().set(valueString);
        } catch (DebugException e) {
            DebugUIPlugin.log(e);
        }
        fWrapText= new Button(parent, SWT.CHECK);
        fWrapText.setText(ActionMessages.getString("StringValueInputDialog.4")); //$NON-NLS-1$
        fWrapText.setSelection(JDIDebugUIPlugin.getDefault().getPreferenceStore().getBoolean(PREF_WRAP_TEXT));
        updateWordWrap();
        fWrapText.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                updateWordWrap();
            }
        });
    }
    
    private void updateWordWrap() {
        fTextViewer.getTextWidget().setWordWrap(fWrapText.getSelection());
    }

    /**
     * Creates the radio buttons that allow the user to choose between
     * simple text mode and evaluation mode.
     */
    protected void createRadioButtons(Composite parent) {
        fTextButton= new Button(parent, SWT.RADIO);
        fTextButton.setText(ActionMessages.getString("StringValueInputDialog.1")); //$NON-NLS-1$
        fTextButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                handleRadioSelectionChanged();
            }
        });
        fEvaluationButton= new Button(parent, SWT.RADIO);
        fEvaluationButton.setText(ActionMessages.getString("StringValueInputDialog.2")); //$NON-NLS-1$
    }
    
    /**
     * The radio button selection has changed update the input widgetry
     * to reflect the user's selection.
     */
    private void handleRadioSelectionChanged() {
        fUseLiteralValue= fTextButton.getSelection();
        if (fUseLiteralValue) {
            disposeSourceViewer();
            createTextViewer();
        } else {
            // Evaluation button selected
            disposeTextViewer();
            createSourceViewer();
        }
        fInputArea.layout(true, true);
    }
    
    /**
     * Disposes of the text viewer and associated widgets.
     */
    protected void disposeTextViewer() {
        if (fTextGroup != null) {
            fTextGroup.dispose();
            fTextGroup= null;
        }
        if (fTextLabel != null) {
		    fTextLabel.dispose();
		    fTextLabel= null;
        }
        if (fTextViewer != null) {
            StyledText textWidget = fTextViewer.getTextWidget();
            if (textWidget != null) {
                textWidget.dispose();
            }
            fTextViewer= null;
        }
        if (fWrapText != null) {
            fWrapText.dispose();
            fWrapText= null;
        }
    }
    
    /**
     * Updates the error message based on the user's input.
     */
    protected void refreshValidState() {
        if (fSourceViewer != null) {
            super.refreshValidState();
            return;
        }
	    String errorMessage= null;
		String text= getText();
		boolean valid= text != null && text.trim().length() > 0;
		if (!valid) {
			errorMessage= ActionMessages.getString("StringValueInputDialog.3"); //$NON-NLS-1$
		}
		setErrorMessage(errorMessage);
	}
    
    /**
     * Override superclass method to persist user's evaluation/literal mode
     * selection.
     */
    protected void okPressed() {
        IPreferenceStore store = JDIDebugUIPlugin.getDefault().getPreferenceStore();
        store.setValue(PREF_USE_EVALUATION, fEvaluationButton.getSelection());
        store.setValue(PREF_WRAP_TEXT, fWrapText.getSelection());
        super.okPressed();
    }
    
    /**
     * Returns <code>true</code> if this dialog's result should be interpreted
     * as a literal value and <code>false</code> if the result should be interpreted
     * as an expression for evaluation.
     * 
     * @return whether or not this dialog's result is a literal value.
     */
    public boolean isUseLiteralValue() {
        return fUseLiteralValue;
    }
    
    /**
     * Override superclass method to return text from the simple text
     * viewer if appropriate.
     * @see ExpressionInputDialog#getText()
     */
    protected String getText() {
        if (fTextButton.getSelection()) {
            return fTextViewer.getDocument().get();
        }
        return super.getText();
    }
    
    /**
     * Override superclass method to dispose of the simple text viewer
     * if appropriate.
     * @see ExpressionInputDialog#dispose()
     */
    protected void dispose() {
        if (fTextButton.getSelection()) {
            disposeTextViewer();
        } else {
            super.dispose();
        }
    }
    
    protected String getHeightPreferenceKey() {
        return "STRING_VALUE_DIALOG_HEIGHT"; //$NON-NLS-1$
    }
    protected String getWidthPreferenceKey() {
        return "STRING_VALUE_DIALOG_WIDTH"; //$NON-NLS-1$
    }
}