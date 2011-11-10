package org.openmarkov.core.gui.menutoolbar.menu;


import java.awt.event.ActionListener;

import javax.swing.JPopupMenu;

import org.openmarkov.core.gui.menutoolbar.common.MenuAssistant;
import org.openmarkov.core.gui.menutoolbar.common.MenuToolBarBasic;




/**
 * This class has a reference to all the popup menus of the application.
 * 
 * @author jmendoza
 */
public class PopupMenuFactory implements MenuToolBarBasic {

	/**
	 * Constant that indentifies the network popup.
	 */
	public static final int NETWORK = 0;

	/**
	 * Constant that indentifies the node popup.
	 */
	public static final int NODE = 1;

	/**
	 * Constant that indentifies the link popup.
	 */
	public static final int LINK = 2;
	
	/**
	 * Popup menu that has the options of a whole network.
	 */
	private PopupMenuBasic networkPopup = null;

	/**
	 * Popup menu that has the options of a node.
	 */
	private PopupMenuBasic nodePopup = null;

	/**
	 * Popup menu that has the options of a link.
	 */
	private PopupMenuBasic linkPopup = null;

	/**
	 * Assistant that manages all the popup menus.
	 */
	private MenuAssistant menuAssistant = null;

	/**
	 * Listener for all the popup menus.
	 */
	private ActionListener listener = null;
	
	/**
	 * Creates a new instance.
	 * 
	 * @param newListener
	 *            listener of the user's actions.
	 */
	public PopupMenuFactory(ActionListener newListener) {

		listener = newListener;
		initialize();
	}

	/**
	 * This method initialises the instance.
	 */
	private void initialize() {

		getNetworkPopup();
		getNodePopup();
		getLinkPopup();
		menuAssistant =
			new MenuAssistant(new PopupMenuBasic[] { networkPopup, nodePopup,
				linkPopup});
	}

	/**
	 * This method initialises networkPopup.
	 * 
	 * @return the network panel popup menu.
	 */
	private JPopupMenu getNetworkPopup() {

		if (networkPopup == null) {
			networkPopup = new NetworkPopup(listener);
			networkPopup.setName("networkPopup");
		}
		return networkPopup;
	}

	/**
	 * This method initialises nodePopup.
	 * 
	 * @return the node popup menu.
	 */
	private JPopupMenu getNodePopup() {

		if (nodePopup == null) {
			nodePopup = new NodePopup(listener);
			nodePopup.setName("nodePopup");
		}
		return nodePopup;
	}

	/**
	 * This method initialises linkPopup.
	 * 
	 * @return the link popup menu.
	 */
	private JPopupMenu getLinkPopup() {

		if (linkPopup == null) {
			linkPopup = new LinkPopup(listener);
			linkPopup.setName("linkPopup");
		}
		return linkPopup;
	}

	/**
	 * Retrieves the popup menu that corresponds to the parameter.
	 * 
	 * @param popup
	 *            popup menu to be returned.
	 * @return the popup menu corresponding the the parameter.
	 */
	public JPopupMenu getPopupMenu(int popup) {

		switch (popup) {
		case NETWORK: {
			return getNetworkPopup();
		}
		case NODE: {
			return getNodePopup();
		}
		case LINK: {
			return getLinkPopup();
		}
		default: {
			return null;
		}
		}
	}

	/**
	 * Enables or disabled an option identified by an action command.
	 * 
	 * @param actionCommand
	 *            action command that identifies the option.
	 * @param b
	 *            true to enable the option, false to disable.
	 */
	public void setOptionEnabled(String actionCommand, boolean b) {

		menuAssistant.setOptionEnabled(actionCommand, b);
	}

	/**
	 * Selects or unselects an option identified by an action command. Only
	 * selects or unselects the components that are AbstractButton.
	 * 
	 * @param actionCommand
	 *            action command that identifies the option.
	 * @param b
	 *            true to select the option, false to unselect.
	 */
	public void setOptionSelected(String actionCommand, boolean b) {

		menuAssistant.setOptionSelected(actionCommand, b);
	}

	/**
	 * Adds a text to the label of an option identified by an action command.
	 * Only adds a text to the components that are AbstractButton.
	 * 
	 * @param actionCommand
	 *            action command that identifies the option.
	 * @param text
	 *            text to add to the label of the options. If null, nothing is
	 *            added.
	 */
	public void addOptionText(String actionCommand, String text) {

		menuAssistant.addOptionText(actionCommand, text);
	}

	/**
	 *Changes the text of menu item
	 * 
	 * @param actionCommand
	 *            action command that identifies the option.
	 * @param text
	 *            text to set to the Item.
	 */
	public void setText(String actionCommand, String text) {

		menuAssistant.setText(actionCommand, text);
	}
}
