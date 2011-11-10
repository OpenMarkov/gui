package org.openmarkov.core.gui.loader.menu;


import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;


/*
 * Handles the Dynamic activation inside a menu that is loaded by name at
 * runtime @author jlgozalo
 * 
 * @version 1.0
 */
public class DynamicMenuItemHandler extends MenuItemAdapter {

	/**
	 * DynamicMenuItemHandler constructor comment.
	 */
	public DynamicMenuItemHandler() {

	}

	/**
	 * This method is called when a MenuItem is activated.
	 */
	public void itemActivated(JMenuItem item, ActionEvent event, String sCommand) {

		System.out.println("Menu item " + item.getName()
			+ " activated dynamically!");
		System.out.println("Command = '" + sCommand + "'");
	}
}