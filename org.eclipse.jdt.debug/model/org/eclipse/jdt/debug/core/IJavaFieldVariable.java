/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.debug.core;


import org.eclipse.debug.core.DebugException;

/**
 * A variable that contains the value of an instance or class variable.
 * <p>
 * Clients are not intended to implement this interface.
 * </p>
 * @see org.eclipse.debug.core.model.IVariable
 * @since 2.0
 */
public interface IJavaFieldVariable extends IJavaVariable {
	
	/**
	 * Returns whether this variable is declared as transient.
	 *
	 * @return whether this variable has been declared as transient
	 * @exception DebugException if this method fails.  Reasons include:
	 * <ul><li>Failure communicating with the VM.  The DebugException's
	 * status code contains the underlying exception responsible for
	 * the failure.</li></ul>
	 */
	public boolean isTransient() throws DebugException;
	
	/**
	 * Returns whether this variable is declared as volatile.
	 * 
	 * @return whether this variable has been declared as volatile
	 * @exception DebugException if this method fails.  Reasons include:
	 * <ul><li>Failure communicating with the VM.  The DebugException's
	 * status code contains the underlying exception responsible for
	 * the failure.</li></ul>
	 */
	public boolean isVolatile() throws DebugException;
		
	/**
	 * Returns the type that declares this variable.
	 * 
	 * @return the type that decalares this variable
	 */
	public IJavaType getDeclaringType();	
	
	/**
	 * Returns the object that contains this field variable, or <code>null</code>
	 * if no object contains this field variable (static field variable). 
	 * 
	 * @return the object that contains this field variable
	 * @since 3.0
	 */
	public IJavaObject getReceiver();
	
	/**
	 * Returns the type that contains this field variable.
	 * 
	 * @return the type that contains this field variable
	 * @since 3.0
	 */
	public IJavaReferenceType getReceivingType();
	
}

