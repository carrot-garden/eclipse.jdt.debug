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
package org.eclipse.jdt.internal.debug.eval.ast.engine;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.debug.core.IJavaClassType;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.debug.core.IJavaVariable;
 
/**
 * The context in which an evaluation is to be performed. An
 * evaluation is performed in the context of an object or class.
 * The evaluation may be in the context of a method, in which case
 * there could be local variables.
 * <p>
 * Clients are intended to implement this interface.
 */

public interface IRuntimeContext {
	
	/**
	 * Returns the virtual machine in which to perform the
	 * evaluation.
	 * 
	 * @return virtual machine
	 */
	IJavaDebugTarget getVM();
	
	/**
	 * Returns the receiving object context in which to perform
	 * the evaluation - equivalent to 'this'. Returns <code>null</code>
	 * if the context of an evaluation is in a class rather than
	 * an object.
	 * 
	 * @return 'this', or <code>null</code>
	 * @exception EvaluationException if this method fails.  Reasons include:
	 * <ul><li>Failure communicating with the VM.  The exception's
	 * status code contains the underlying exception responsible for
	 * the failure.</li></ul>
	 */
	IJavaObject getThis() throws CoreException;
	
	/**
	 * Returns the receiving type context in which to perform 
	 * the evaluation. The type of 'this', or in the case of a 
	 * static context, the class in which the evaluation is being
	 * performed.
	 * 
	 * @return receiving class
	 * @exception EvaluationException if this method fails.  Reasons include:
	 * <ul><li>Failure communicating with the VM.  The exception's
	 * status code contains the underlying exception responsible for
	 * the failure.</li></ul>
	 */
	IJavaClassType getReceivingType() throws CoreException;
	
	/**
	 * Returns the local variables visible for the evaluation.
	 * This includes method arguments, if any. Does not return
	 * <code>null</code> returns an empty collection if there
	 * are no locals.
	 * 
	 * @return local variables
	 * @exception EvaluationException if this method fails.  Reasons include:
	 * <ul><li>Failure communicating with the VM.  The exception's
	 * status code contains the underlying exception responsible for
	 * the failure.</li></ul>
	 */
	IJavaVariable[] getLocals() throws CoreException;

	/**
	 * Returns the Java project context in which this expression
	 * should be compiled.
	 * 
	 * @return project
	 */
	IJavaProject getProject();
	
	/**
	 * Returns the thread in which message sends may be performed.
	 * 
	 * @return thread
	 */
	IJavaThread getThread();
	
	/**
	 * Returns whether the context of this evaluation is within
	 * a constructor.
	 * 
	 * @return whether the context of this evaluation is within
	 * a constructor
	 * @exception EvaluationException if this method fails.  Reasons include:
	 * <ul><li>Failure communicating with the VM.  The exception's
	 * status code contains the underlying exception responsible for
	 * the failure.</li></ul>
	 */
	public boolean isConstructor() throws CoreException;
}
