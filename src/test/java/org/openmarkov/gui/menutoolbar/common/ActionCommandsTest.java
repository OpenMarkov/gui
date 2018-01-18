/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.gui.menutoolbar.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


/**
 * This class tests the class
 * {@link openmarkov.gui.menutoolbar.common.ActionCommands}.
 * 
 * @author jmendoza
 */
public class ActionCommandsTest {

	/**
	 * This method tests that the method isZoomActionCommand can recognize the
	 * zoom action commands.
	 */
	@Test
	public final void testIsZoomActionCommand() {
		assertTrue(ActionCommands.isZoomActionCommand("Zoom_115"));
		assertFalse(ActionCommands.isZoomActionCommand(" Zoom_115"));
		assertFalse(ActionCommands.isZoomActionCommand("Zoom_115 "));
		assertFalse(ActionCommands.isZoomActionCommand("ZOOM_115"));
		assertFalse(ActionCommands.isZoomActionCommand("Zoo115"));
		assertFalse(ActionCommands.isZoomActionCommand("Zoom115"));
		assertFalse(ActionCommands.isZoomActionCommand("Zoom 115"));
		assertFalse(ActionCommands.isZoomActionCommand("Zoom-115"));
		assertFalse(ActionCommands.isZoomActionCommand("Zoom.115"));
		assertFalse(ActionCommands.isZoomActionCommand("Zoom11.5"));
		assertFalse(ActionCommands.isZoomActionCommand("Zoom.115"));
		assertFalse(ActionCommands.isZoomActionCommand("Zoom.cien"));
		assertFalse(ActionCommands.isZoomActionCommand("Zoom.100c"));
		assertFalse(ActionCommands.isZoomActionCommand("Zoom.c100"));
	}


	/**
	 * This method tests how the method getValueZoomActionCommand can extract
	 * the zoom value from the action command string.
	 */
	@Test
	public final void testGetValueZoomActionCommand() {
		assertEquals(ActionCommands.getValueZoomActionCommand("Zoom_100"), 1.0, 0.1);
		assertEquals(ActionCommands.getValueZoomActionCommand("Zoom_200"), 2.0, 0.1);
		assertEquals(ActionCommands.getValueZoomActionCommand("Zoom_75"), 0.75, 0.1);
		assertEquals(ActionCommands.getValueZoomActionCommand("Zoom_1"), 0.01, 0.1);
		assertEquals(ActionCommands.getValueZoomActionCommand("Zoom_4357"),
				43.57, 0.1);
	}


	/**
	 * This method tests how the method getZoomActionCommandValue builds various
	 * zoom action commands from their values.
	 */
	@Test
	public final void testGetActionCommandZoomValue() {
		assertEquals(ActionCommands.getZoomActionCommandValue(1.0), "Zoom_100");
		assertEquals(ActionCommands.getZoomActionCommandValue(2.0), "Zoom_200");
		assertEquals(ActionCommands.getZoomActionCommandValue(0.75), "Zoom_75");
		assertEquals(ActionCommands.getZoomActionCommandValue(0.01), "Zoom_1");
		assertEquals(ActionCommands.getZoomActionCommandValue(43.57),
				"Zoom_4357");
	}
}
