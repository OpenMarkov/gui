package openmarkov.core.gui.development.handler;


import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;

import openmarkov.core.gui.help.HelpViewer;
import openmarkov.core.gui.menutoolbar.sax.MenuItemDevAdapter;



/**
 * Handles the dynamic activation inside the File New Action in the menu
 * 
 * @author jlgozalo
 * @version 1.0 05/04/2009
 */
public class HelpTopicsActionHandler extends MenuItemDevAdapter {

	private HelpViewer aHelpTopic = null;

	/**
	 * FileNewActionHandler default constructor
	 */
	public HelpTopicsActionHandler() {

	}

	/**
	 * This method is called when the File New Action in the Menu is activated.
	 */
	public void itemActivated(JMenuItem item, ActionEvent event, String sCommand) {

		System.out.println("Menu item " + item.getName()
			+ " activated dynamically!");
		System.out.println("Command = '" + sCommand + "'");
		showHelpTopic();
	}

	/**
	 * show the AboutBox dialog
	 */
	private void showHelpTopic() {

		if (aHelpTopic == null) {
			aHelpTopic = HelpViewer.getUniqueInstance();
		}
		aHelpTopic.setVisible(true);
	}
}