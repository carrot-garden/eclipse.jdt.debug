/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.debug.ui.actions;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.internal.ui.actions.IRunToLineTarget;
import org.eclipse.debug.internal.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.ui.IEditorPart;

/**
 * Creates adapters for retargettable actions in debug platform.
 * Contributed via <code>org.eclipse.core.runtime.adapters</code> 
 * extension point. 
 * 
 * @since 3.0
 */
public class RetargettableActionAdapterFactory implements IAdapterFactory {
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == IRunToLineTarget.class) {
			if (adaptableObject instanceof IEditorPart) {
				return new RunToLineAdapter();
			}
		}
		if (adapterType == IToggleBreakpointsTarget.class) {
			if (adaptableObject instanceof IEditorPart) {
				return new ToggleBreakpointAdapter();
			}
		} 
		return null;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	public Class[] getAdapterList() {
		return new Class[]{IRunToLineTarget.class, IToggleBreakpointsTarget.class};
	}
}
