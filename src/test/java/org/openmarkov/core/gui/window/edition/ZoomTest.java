/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.window.edition;

import static org.junit.Assert.assertTrue;

import org.junit.Test;


/**
 * This class is used to test the class {@link openmarkov.gui.edition.zoom.Zoom}.
 * 
 * @author jmendoza
 */
public class ZoomTest {
	/**
	 * Test the default constructor and the constructor with a value 0 as
	 * parameter.
	 */
	@Test
	public final void testZoom() {
		Zoom zoom;

		zoom = new Zoom();
		assertTrue(zoom.getZoom() == 1.0);
		zoom = new Zoom(0);
		assertTrue(zoom.getZoom() == 1.0);
	}


	/**
	 * Test the the constructor with a value non 0 as parameter.
	 */
	@Test
	public final void testZoomDouble() {
		Zoom zoom;

		zoom = new Zoom(0.5);
		assertTrue(zoom.getZoom() == 0.5);
	}


	/**
	 * Test the limits of the zoom.
	 */
	@Test
	public final void testZoomLimits() {
		Zoom zoom;

		zoom = new Zoom(6.0);
		assertTrue(zoom.getZoom() == 5.0);
		zoom = new Zoom(0.01);
		assertTrue(zoom.getZoom() == 0.1);
	}


	/**
	 * This method tests the method setZoom.
	 */
	@Test
	public final void testSetZoom() {
		Zoom zoom = new Zoom();

		zoom.setZoom(4.5);
		assertTrue(zoom.getZoom() == 4.5);
	}


	/**
	 * This method tests the conversion of screen coordinates to panel
	 * coordinates.
	 */
	@Test
	public final void testScreenToPanel() {
		Zoom zoom = new Zoom(4);
		double value;

		value = zoom.screenToPanel(2162.0);
		assertTrue(value == 540.5);
	}


	/**
	 * This method tests the conversion of panel coordinates to screen
	 * coordinates.
	 */
	@Test
	public final void testPanelToScreen() {
		Zoom zoom = new Zoom(1.5);
		double value;

		value = zoom.panelToScreen(428.0);
		assertTrue(value == 642.0);
	}
}
