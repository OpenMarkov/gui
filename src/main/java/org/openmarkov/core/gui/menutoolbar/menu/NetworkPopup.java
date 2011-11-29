package org.openmarkov.core.gui.menutoolbar.menu;


import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JMenuItem;

import org.openmarkov.core.gui.localize.LocalizedMenuItem;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.gui.menutoolbar.common.ActionCommands;
import org.openmarkov.core.gui.menutoolbar.common.MenuItemNames;




/**
 * This class implements a popup menu that shows when a user click on tha
 * background of a network panel.
 * 
 * @author jmendoza
 * @author jlgozalo 
 * @version 1.1 jlgozalo - Add change locale management setting the item names.
 */
class NetworkPopup extends PopupMenuBasic {

	/**
	 * Static field for serializable class.
	 */
	private static final long serialVersionUID = 3673127766586232832L;

	/**
	 * Object that represents the item 'Paste'.
	 */
	private JMenuItem pasteMenuItem = null;

	/**
	 * Object that represents the item 'Network adittionalProperties'.
	 */
	private JMenuItem networkPropertiesMenuItem = null;

	/**
	 * String resource.
	 */
	private StringResource stringResource = null;

	/**
	 * This constructor creates a new instance.
	 * 
	 * @param newListener
	 *            object that listens to the menu events.
	 */
	public NetworkPopup(ActionListener newListener) {

		super(newListener);

		initialize();

	}

	/**
	 * This method initializes this instance.
	 */
	private void initialize() {

		stringResource =
			StringResourceLoader.getUniqueInstance().getBundleMenus();
		add(getPasteMenuItem());
		addSeparator();
		add(getNetworkPropertiesMenuItem());

	}

	/**
	 * This method initializes pasteMenuItem.
	 * 
	 * @return a new 'Paste' menu item.
	 */
	private JMenuItem getPasteMenuItem() {

		if (pasteMenuItem == null) {
            pasteMenuItem = new LocalizedMenuItem (
                                                   MenuItemNames.EDIT_PASTE_MENUITEM,
                                                   ActionCommands.CLIPBOARD_PASTE);
			pasteMenuItem.setEnabled(false);
			pasteMenuItem.addActionListener(listener);
		}

		return pasteMenuItem;

	}

	/**
	 * This method initializes networkPropertiesMenuItem.
	 * 
	 * @return a new 'Network adittionalProperties' menu item.
	 */
	private JMenuItem getNetworkPropertiesMenuItem() {

		if (networkPropertiesMenuItem == null) {
            networkPropertiesMenuItem = new LocalizedMenuItem (
                                                               MenuItemNames.FILE_NETWORKPROPERTIES_MENUITEM,
                                                               ActionCommands.NETWORK_PROPERTIES);
			networkPropertiesMenuItem.addActionListener(listener);
		}

		return networkPropertiesMenuItem;

	}

	/**
	 * Returns the component that corresponds to an action command.
	 * 
	 * @param actionCommand
	 *            action command that identifies the component.
	 * @return a components identified by the action command.
	 */
	@Override
	protected JComponent getJComponentActionCommand(String actionCommand) {

		JComponent component = null;

		if (actionCommand.equals(ActionCommands.CLIPBOARD_PASTE)) {
			component = pasteMenuItem;
		} else if (actionCommand.equals(ActionCommands.NETWORK_PROPERTIES)) {
			component = networkPropertiesMenuItem;
		}

		return component;

	}
}
