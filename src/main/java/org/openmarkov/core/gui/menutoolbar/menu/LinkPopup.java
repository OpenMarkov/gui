/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.menutoolbar.menu;

import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JMenuItem;

import org.openmarkov.core.gui.localize.LocalizedMenuItem;
import org.openmarkov.core.gui.menutoolbar.common.ActionCommands;
import org.openmarkov.core.gui.menutoolbar.common.MenuItemNames;

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
	 * Object that represents the item 'Add restriction'.
	 */
	private JMenuItem linkRestrictionEnableMenuItem = null;

	/**
	 * Object that represents the item 'Remove restriction'.
	 */
	private JMenuItem linkRestrictionDisableMenuItem = null;

	/**
	 * Object that represents the item 'Add revelation arc'.
	 */
	private JMenuItem revelationArcMenuItem = null;

	/**
	 * Object that represents the item 'Properties'.
	 */
	private JMenuItem propertiesMenuItem = null;

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

		add(getRemoveMenuItem());
		/*
		 * This item must be added to the menu when is active the possibility of
		 * editing the adittionalProperties of a link in future versions.
		 */
		// addSeparator();
		// add(getPropertiesMenuItem());
		add(getLinkRestrictionEnableMenuItem());
		add(getLinkRestrictionDisableMenuItem());
		add(getRevelationArcMenuItem());
		getPropertiesMenuItem();

	}

	/**
	 * This method initialises removeMenuItem.
	 * 
	 * @return a new 'Remove' menu item.
	 */
	private JMenuItem getRemoveMenuItem() {

		if (removeMenuItem == null) {
			removeMenuItem = new LocalizedMenuItem(
					MenuItemNames.EDIT_REMOVE_MENUITEM,
					ActionCommands.OBJECT_REMOVAL);
			removeMenuItem.addActionListener(listener);
		}

		return removeMenuItem;

	}

	/**
	 * This method initialises the enableLinkRestriction menu item.
	 * 
	 * @return a new 'LinkRestrictionEnable' menu item.
	 */
	private JMenuItem getLinkRestrictionEnableMenuItem() {

		if (linkRestrictionEnableMenuItem == null) {
			linkRestrictionEnableMenuItem = new LocalizedMenuItem(
					MenuItemNames.EDIT_LINKRESTRICTION_ENABLE_MENUITEM,
					ActionCommands.LINK_RESTRICTION_ENABLE_PROPERTIES);
			linkRestrictionEnableMenuItem.addActionListener(listener);
		}

		return linkRestrictionEnableMenuItem;

	}

	/**
	 * This method initialises the disableLinkRestriction menu item.
	 * 
	 * @return a new 'LinkRestrictionDisable' menu item.
	 */
	private JMenuItem getLinkRestrictionDisableMenuItem() {

		if (linkRestrictionDisableMenuItem == null) {
			linkRestrictionDisableMenuItem = new LocalizedMenuItem(
					MenuItemNames.EDIT_LINKRESTRICTION_DISABLE_MENUITEM,
					ActionCommands.LINK_RESTRICTION_DISABLE_PROPERTIES);
			linkRestrictionDisableMenuItem.addActionListener(listener);
		}

		return linkRestrictionDisableMenuItem;

	}

	/**
	 * This method initialises the revelationArc menu item.
	 * 
	 * @return a new 'revelationArc' menu item.
	 */

	private JMenuItem getRevelationArcMenuItem() {

		if (revelationArcMenuItem == null) {
			revelationArcMenuItem = new LocalizedMenuItem(
					MenuItemNames.EDIT_LINKREVELATIONARC_MENUITEM,
					ActionCommands.LINK_REVELATIONARC_PROPERTIES);
			revelationArcMenuItem.addActionListener(listener);
		}

		return revelationArcMenuItem;

	}

	/**
	 * This method initialises propertiesMenuItem.
	 * 
	 * @return a new 'Properties' menu item.
	 */
	private JMenuItem getPropertiesMenuItem() {

		if (propertiesMenuItem == null) {
			propertiesMenuItem = new LocalizedMenuItem(
					MenuItemNames.EDIT_LINKPROPERTIES_MENUITEM,
					ActionCommands.LINK_PROPERTIES);
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
		} else if (actionCommand
				.equals(ActionCommands.LINK_RESTRICTION_ENABLE_PROPERTIES)) {
			component = linkRestrictionEnableMenuItem;
		} else if (actionCommand
				.equals(ActionCommands.LINK_RESTRICTION_DISABLE_PROPERTIES)) {
			component = linkRestrictionDisableMenuItem;
		} else if (actionCommand
				.equals(ActionCommands.LINK_REVELATIONARC_PROPERTIES)) {
			component = revelationArcMenuItem;
		}

		return component;

	}

}
