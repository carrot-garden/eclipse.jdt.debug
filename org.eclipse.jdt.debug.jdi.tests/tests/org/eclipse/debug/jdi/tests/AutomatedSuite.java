/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.jdi.tests;

import junit.framework.TestSuite;

/**
 */
public class AutomatedSuite extends TestSuite {
	
	public static TestSuite suite() {
		return new AutomatedSuite();
	}
	
	public AutomatedSuite() {
		AbstractJDITest.parseArgs(new String[0]);

		addTest(new TestSuite(AccessibleTest.class));
		addTest(new TestSuite(ArrayReferenceTest.class));
		addTest(new TestSuite(ArrayTypeTest.class));
		addTest(new TestSuite(BooleanValueTest.class));
		addTest(new TestSuite(BreakpointRequestTest.class));
		addTest(new TestSuite(ByteValueTest.class));
		addTest(new TestSuite(CharValueTest.class));
		addTest(new TestSuite(ClassLoaderReferenceTest.class));
		addTest(new TestSuite(ClassPrepareEventTest.class));
		addTest(new TestSuite(ClassPrepareRequestTest.class));
		addTest(new TestSuite(ClassTypeTest.class));
		addTest(new TestSuite(DoubleValueTest.class));
		addTest(new TestSuite(EventRequestManagerTest.class));
		addTest(new TestSuite(EventRequestTest.class));
		addTest(new TestSuite(EventTest.class));
		addTest(new TestSuite(ExceptionEventTest.class));
		addTest(new TestSuite(ExceptionRequestTest.class));
		addTest(new TestSuite(FieldTest.class));
		addTest(new TestSuite(FloatValueTest.class));
		addTest(new TestSuite(HotCodeReplacementTest.class));
		addTest(new TestSuite(IntegerValueTest.class));
		addTest(new TestSuite(InterfaceTypeTest.class));
		addTest(new TestSuite(LocalVariableTest.class));
		addTest(new TestSuite(LocatableTest.class));
		addTest(new TestSuite(LocationTest.class));
		addTest(new TestSuite(LongValueTest.class));
		addTest(new TestSuite(MethodTest.class));
		addTest(new TestSuite(MethodEntryRequestTest.class));
		addTest(new TestSuite(MethodExitRequestTest.class));
		addTest(new TestSuite(MirrorTest.class));

		addTest(new TestSuite(ModificationWatchpointEventTest.class));
			
		addTest(new TestSuite(ObjectReferenceTest.class));
		addTest(new TestSuite(PrimitiveValueTest.class));
		addTest(new TestSuite(ReferenceTypeTest.class));
		addTest(new TestSuite(ShortValueTest.class));
		addTest(new TestSuite(StackFrameTest.class));
		addTest(new TestSuite(StepEventTest.class));
		addTest(new TestSuite(StringReferenceTest.class));
		addTest(new TestSuite(ThreadDeathEventTest.class));
		addTest(new TestSuite(ThreadGroupReferenceTest.class));
		addTest(new TestSuite(ThreadReferenceTest.class));
		addTest(new TestSuite(ThreadStartEventTest.class));
		addTest(new TestSuite(TypeComponentTest.class));
		addTest(new TestSuite(TypeTest.class));
		addTest(new TestSuite(ValueTest.class));

		addTest(new TestSuite(WatchpointEventTest.class));
		addTest(new TestSuite(WatchpointRequestTest.class));

		addTest(new TestSuite(VirtualMachineExitTest.class));
		addTest(new TestSuite(VMDisconnectEventTest.class));
		addTest(new TestSuite(VMDisposeTest.class));
		
	}
	
}