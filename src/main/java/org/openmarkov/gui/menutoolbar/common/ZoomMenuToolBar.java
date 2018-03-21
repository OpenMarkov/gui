/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.menutoolbar.common;

/**
 * This interface defines the method that menus and toolbars must implement to
 * manage zoom.
 *
 * @author jmendoza
 */
public interface ZoomMenuToolBar {

	/**
	 * This method makes that the corresponding field show the zoom value.
	 *
	 * @param value
	 */
	public void setZoom(double value);
}
