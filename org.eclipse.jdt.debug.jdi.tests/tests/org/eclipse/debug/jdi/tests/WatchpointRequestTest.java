package org.eclipse.debug.jdi.tests;

/**********************************************************************
Copyright (c) 2000, 2002 IBM Corp.  All rights reserved.
This file is made available under the terms of the Common Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/cpl-v10.html
**********************************************************************/

import com.sun.jdi.request.WatchpointRequest;

/**
 * Tests for JDI com.sun.jdi.request.WatchpointRequest.
 */
public class WatchpointRequestTest extends AbstractJDITest {

	private WatchpointRequest fAccessWatchpointRequest,
		fModificationWatchpointRequest;
	/**
	 * Creates a new test .
	 */
	public WatchpointRequestTest() {
		super();
	}
	/**
	 * Init the fields that are used by this test only.
	 */
	public void localSetUp() {
		// Get an acces watchpoint request
		fAccessWatchpointRequest = getAccessWatchpointRequest();

		// Get a modification watchpoint request
		fModificationWatchpointRequest = getModificationWatchpointRequest();
	}
	/**
	 * Make sure the test leaves the VM in the same state it found it.
	 */
	public void localTearDown() {
		// Delete the watchpoint requests we created in this test
		fVM.eventRequestManager().deleteEventRequest(fAccessWatchpointRequest);
		fVM.eventRequestManager().deleteEventRequest(
			fModificationWatchpointRequest);
	}
	/**
	 * Run all tests and output to standard output.
	 */
	public static void main(java.lang.String[] args) {
		new WatchpointRequestTest().runSuite(args);
	}
	/**
	 * Gets the name of the test case.
	 */
	public String getName() {
		return "com.sun.jdi.request.WatchpointRequest";
	}
	/**
	 * Test JDI field().
	 */
	public void testJDIField() {
		assertEquals("1", getField("fBool"), fAccessWatchpointRequest.field());
		assertEquals(
			"2",
			getField("fBool"),
			fModificationWatchpointRequest.field());
	}
}