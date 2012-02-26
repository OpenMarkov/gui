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
	private JMenuItem linkRestrictionMenuItem = null;
	
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
		add(getLinkRestrictionMenuItem());
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
            removeMenuItem = new LocalizedMenuItem (
                                                    MenuItemNames.EDIT_REMOVE_MENUITEM,
                                                    ActionCommands.OBJECT_REMOVAL);
			removeMenuItem.addActionListener(listener);
		}

		return removeMenuItem;

	}
	
	
	
	private JMenuItem getLinkRestrictionMenuItem()
	{
		
		
		if (linkRestrictionMenuItem == null) {
            linkRestrictionMenuItem = new LocalizedMenuItem (
                                                    MenuItemNames.EDIT_LINKRESTRICTION_MENUITEM,
                                                    ActionCommands.LINK_RESTRICTION_PROPERTIES);
			linkRestrictionMenuItem.addActionListener(listener);
		}

		return linkRestrictionMenuItem;
		
	}

	
	private JMenuItem getRevelationArcMenuItem()
	{
		
		
		if (revelationArcMenuItem == null) {
			revelationArcMenuItem = new LocalizedMenuItem (
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
            propertiesMenuItem = new LocalizedMenuItem (
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
		} else if (actionCommand.equals(ActionCommands.LINK_RESTRICTION_PROPERTIES)) {
			component = linkRestrictionMenuItem;
		}else if (actionCommand.equals(ActionCommands.LINK_REVELATIONARC_PROPERTIES)) {
			component = revelationArcMenuItem;
		}

		return component;

	}

	
}
