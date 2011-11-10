package org.openmarkov.core.gui.menutoolbar.common;


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
