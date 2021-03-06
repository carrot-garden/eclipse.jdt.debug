/*******************************************************************************
 * Copyright (c) 2016, 2017 Till Brychcy and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Till Brychcy - initial API and implementation
 *******************************************************************************/

package org.eclipse.jdt.internal.debug.core.model;

import com.sun.jdi.Method;
import com.sun.jdi.Value;

public class MethodResult {
	public enum ResultType {
		/** fValue is returned value after a step operation */
		returned,

		/** fValue is exception thrown after a step operation */
		threw,

		/** fValue is value being returned at a method exit breakpoint */
		returning,

		/** fValue is exception being thrown in a exception breakpoint */
		throwing
	}

	public MethodResult(Method method, int targetFrameCount, Value value, ResultType resultType) {
		this.fMethod = method;
		this.fTargetFrameCount = targetFrameCount;
		this.fValue = value;
		this.fResultType = resultType;
	}

	/**
	 * The method from which {@link #fValue} originates
	 */
	public final Method fMethod;

	/**
	 * If a step-return or step-over is in progress, this is the stack size at which the result value is expected. Otherwise ignored.
	 */
	public final int fTargetFrameCount;

	/**
	 * Return value or exception
	 */
	public final Value fValue;

	/**
	 * Whether {@link #fValue} was returned or thrown
	 */
	public final ResultType fResultType;
}