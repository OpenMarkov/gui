package openmarkov.core.gui.development.handler;


import java.awt.event.ActionEvent;

import javax.swing.JDialog;
import javax.swing.JMenuItem;

import openmarkov.core.gui.development.dialog.AboutBoxDevEnv;
import openmarkov.core.gui.loader.menu.MenuItemAdapter;



/**
 * Handles the dynamic activation inside the File New Action in the menu
 * 
 * @author jlgozalo
 * @version 1.0 05/04/2009
 */
public class HelpAboutActionHandler extends MenuItemAdapter {

	private JDialog anAboutBox = null;

	/**
	 * FileNewActionHandler default constructor
	 */
	public HelpAboutActionHandler() {

	}

	/**
	 * This method is called when the File New Action in the Menu is activated.
	 */
	public void itemActivated(JMenuItem item, ActionEvent event, String sCommand) {

		showAboutBox();
	}

	/**
	 * show the AboutBox dialog
	 */
	private void showAboutBox() {

		if (anAboutBox == null) {
			anAboutBox = new AboutBoxDevEnv();
		}
		anAboutBox.setVisible(true);
	}
}