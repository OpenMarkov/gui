package org.openmarkov.core.gui.menutoolbar.menu;


import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JMenuItem;

import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.gui.menutoolbar.common.ActionCommands;




/**
 * This class implements a popup menu that is displayes when the user clicks on
 * a link.
 * 
 * @author jmendoza
 * @author jlgozalo 
 * @version 1.1 jlgozalo - Add change locale management setting the item names.
 */
class LinkPopup extends PopupMenuBasic {

	/**
	 * Static field for serializable class.
	 */
	private static final long serialVersionUID = 5509407441152307200L;

	/**
	 * Object that represents the item 'Remove'.
	 */
	private JMenuItem removeMenuItem = null;

	/**
	 * Object that represents the item 'Properties'.
	 */
	private JMenuItem propertiesMenuItem = null;

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
	public LinkPopup(ActionListener newListener) {

		super(newListener);

		initialize();

	}

	/**
	 * This method initialises this instance.
	 */
	private void initialize() {

		stringResource =
			StringResourceLoader.getUniqueInstance().getBundleMenus();
		add(getRemoveMenuItem());
		/*
		 * This item must be added to the menu when is active the possibility of
		 * editing the adittionalProperties of a link in future versions.
		 */
		// addSeparator();
		// add(getPropertiesMenuItem());
		getPropertiesMenuItem();

	}

	/**
	 * This method initialises removeMenuItem.
	 * 
	 * @return a new 'Remove' menu item.
	 */
	private JMenuItem getRemoveMenuItem() {

		if (removeMenuItem == null) {
			removeMenuItem = new JMenuItem();
			removeMenuItem.setName("Edit.Remove");
			removeMenuItem.setText(stringResource
				.getString(MainMenu.EDIT_REMOVE_MENUITEM + LABEL_SUFFIX));
			removeMenuItem.setMnemonic(stringResource.getString(
				MainMenu.EDIT_REMOVE_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			removeMenuItem.setActionCommand(ActionCommands.OBJECT_REMOVAL);
			removeMenuItem.addActionListener(listener);
		}

		return removeMenuItem;

	}

	/**
	 * This method initialises propertiesMenuItem.
	 * 
	 * @return a new 'Properties' menu item.
	 */
	private JMenuItem getPropertiesMenuItem() {

		if (propertiesMenuItem == null) {
			propertiesMenuItem = new JMenuItem();
			propertiesMenuItem.setName("Edit.LinkProperties");
			propertiesMenuItem
				.setText(stringResource
					.getString(MainMenu.EDIT_LINKPROPERTIES_MENUITEM
						+ LABEL_SUFFIX));
			propertiesMenuItem.setMnemonic(stringResource.getString(
				MainMenu.EDIT_LINKPROPERTIES_MENUITEM + MNEMONIC_SUFFIX)
				.charAt(0));
			propertiesMenuItem.setActionCommand(ActionCommands.LINK_PROPERTIES);
			propertiesMenuItem.addActionListener(listener);
		}

		return propertiesMenuItem;

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

		if (actionCommand.equals(ActionCommands.OBJECT_REMOVAL)) {
			component = removeMenuItem;
		} else if (actionCommand.equals(ActionCommands.LINK_PROPERTIES)) {
			component = propertiesMenuItem;
		}

		return component;

	}

	
}
