package openmarkov.core.gui.development.handler;


import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;

import openmarkov.core.gui.development.environment.OpenMarkovDevEnv;
import openmarkov.core.gui.menutoolbar.sax.MenuItemDevAdapter;



/**
 * Handles the dynamic activation inside the File New Action in the menu
 * 
 * @author jlgozalo
 * @version 1.0 05/04/2009
 */
public class FileNewActionHandler extends MenuItemDevAdapter {

	/**
	 * FileNewActionHandler default constructor
	 */
	public FileNewActionHandler() {

	}

	/**
	 * This method is called when the File New Action in the Menu is activated.
	 */
	public void itemActivated(JMenuItem item, ActionEvent event, String sCommand) {

		System.out.println("Menu item " + item.getName()
			+ " activated dynamically!");
		System.out.println("Command = '" + sCommand + "'");
		setAParentFrame(OpenMarkovDevEnv.getOpenMarkovDevEnvFrame());
		setAOpenMarkovDevEnvProperties(OpenMarkovDevEnv.getOpenMarkovDevEnvProperties());
	}
}