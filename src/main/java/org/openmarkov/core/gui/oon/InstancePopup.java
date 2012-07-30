/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.oon;

import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenuItem;

import org.openmarkov.core.gui.localize.LocalizedMenuItem;
import org.openmarkov.core.gui.localize.MenuLocalizer;
import org.openmarkov.core.gui.menutoolbar.common.ActionCommands;
import org.openmarkov.core.gui.menutoolbar.common.MenuItemNames;
import org.openmarkov.core.gui.menutoolbar.menu.PopupMenuBasic;

/**
 * This class implements a popup menu that is displays when the user right-clicks on
 * an instance.
 * 
 * @author ibermejo
 * @version 1.0 
 */
@SuppressWarnings("serial")
public class InstancePopup extends PopupMenuBasic {

	/**
	 * Object that represents the item 'Remove'.
	 */
	private JMenuItem removeMenuItem = null;

	/**
	 * Object that represents the item 'Properties'.
	 */
	private JMenuItem propertiesMenuItem = null;
	
	/**
	 * Object that represents the item 'Input'.
	 */
	private JMenuItem inputMenuItem = null;
	

	/**
	 * This constructor creates a new instance.
	 * 
	 * @param newListener
	 *            object that listens to the menu events.
	 */
	public InstancePopup(ActionListener newListener) {

		super(newListener);

		initialize();

	}

	/**
	 * This method initialises this instance.
	 */
	private void initialize() {

		add(getRemoveMenuItem());
		// addSeparator();
		// add(getPropertiesMenuItem());
		add(getInputMenuItem());

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
	 * This method initialises inputMenuItem.
	 * 
	 * @return a new 'Input' menu item.
	 */
	private JMenuItem getInputMenuItem() {

		if (inputMenuItem == null) {
			inputMenuItem  = new JCheckBoxMenuItem(MenuLocalizer.getLabel(MenuItemNames.EDIT_INSTANCEINPUT_MENUITEM));
			inputMenuItem.setActionCommand(ActionCommands.MARK_AS_INPUT);
			inputMenuItem .addActionListener(listener);
		}

		return inputMenuItem ;

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
		}else if (actionCommand.equals(ActionCommands.MARK_AS_INPUT)) {
			component = inputMenuItem;
		}

		return component;

	}

}
