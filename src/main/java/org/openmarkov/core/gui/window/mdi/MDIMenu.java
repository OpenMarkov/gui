/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.window.mdi;


import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;




/**
 * This class fills and handle the menu associated with the MDI class.
 * 
 * @author jmendoza
 * @version 1.0
 * @version 1.1 jlgozalo - sets the class to public
 */
public class MDIMenu extends Component{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1403517409754891762L;

	/**
	 * Constant that defines the menu 'Window'.
	 */
	private static final String WINDOW_MENU = "Window";

	/**
	 * Constant that defines the item 'Window - MinimizeAll'.
	 */
	public static final String WINDOW_MINIMIZEALL_MENUITEM =
		WINDOW_MENU + ".MinimizeAll";

	/**
	 * Constant that defines the item 'Window - RestoreAll'.
	 */
	public static final String WINDOW_RESTOREALL_MENUITEM =
		WINDOW_MENU + ".RestoreAll";

	/**
	 * Constant that defines the item 'Window - Cascade'.
	 */
	public static final String WINDOW_CASCADE_MENUITEM =
		WINDOW_MENU + ".Cascade";

	/**
	 * Constant that defines the item 'Window - Mosaic'.
	 */
	public static final String WINDOW_MOSAIC_MENUITEM = WINDOW_MENU + ".Mosaic";

	/**
	 * Constant that defines the item 'Window - Previous'.
	 */
	public static final String WINDOW_PREVIOUS_MENUITEM =
		WINDOW_MENU + ".Previous";

	/**
	 * Constant that defines the item 'Window - Next'.
	 */
	public static final String WINDOW_NEXT_MENUITEM = WINDOW_MENU + ".Next";

	/**
	 * Object that represents the menu 'Window'.
	 */
	private JMenu windowMenu = null;

	/**
	 * Object that represents the item 'Window - Cascade'.
	 */
	private JMenuItem windowCascadeMenuItem = null;

	/**
	 * Object that represents the item 'Window - Mosaic'.
	 */
	private JMenuItem windowMosaicMenuItem = null;

	/**
	 * Object that represents the item 'Window - MinimizeAll'.
	 */
	private JMenuItem windowMinimizeAllMenuItem = null;

	/**
	 * Object that represents the item 'Window - RestoreAll'.
	 */
	private JMenuItem windowRestoreAllMenuItem = null;

	/**
	 * Object that represents the item 'Window - Previous'.
	 */
	private JMenuItem windowPreviousMenuItem = null;

	/**
	 * Object that represents the item 'Window - Next'.
	 */
	private JMenuItem windowNextMenuItem = null;

	/**
	 * Menu separator that separates the panel list from the rest of the menu
	 * items.
	 */
	private JSeparator jSeparator = null;

	/**
	 * Object used to make autoexclusive the menu item to select the panels.
	 */
	private ButtonGroup groupSelectionPanels = new ButtonGroup();

	/**
	 * Table that relates the menu items to the panels to which they represent.
	 */
	private HashMap<JCheckBoxMenuItem, JPanel> menuItemToPanel =
		new HashMap<JCheckBoxMenuItem, JPanel>();

	/**
	 * Table that relates the panels to the menu items which represent them.
	 */
	private HashMap<JPanel, JCheckBoxMenuItem> panelToMenuItem =
		new HashMap<JPanel, JCheckBoxMenuItem>();

	/**
	 * Menus string resource.
	 */
	private StringResource stringResource;

	/**
	 * Listener of the user's actions.
	 */
	private ActionListener listener = null;

	/**
	 * This is the default constructor. Beside initialiting this object, it
	 * saves a reference to the main menu.
	 * 
	 * @param newWindowMenu
	 *            menu where configure the specific options.
	 * @param newListener
	 *            object that listen to the menu events.
	 */
	public MDIMenu(JMenu newWindowMenu, ActionListener newListener) {

		windowMenu = newWindowMenu;
		listener = newListener;
		initialize();

	}

	/**
	 * This method initializes this
	 */
	private void initialize() {

		stringResource =
			StringResourceLoader.getUniqueInstance().getBundleMenus();
		configureWindowMenu();

	}

	/**
	 * This method configures windowMenu.
	 */
	private void configureWindowMenu() {

		windowMenu.setName(WINDOW_MENU);
		windowMenu.setText(stringResource.getString(WINDOW_MENU + ".Label"));
		windowMenu.setMnemonic(stringResource.getString(
			WINDOW_MENU + ".Mnemonic").charAt(0));
		windowMenu.setActionCommand(WINDOW_MENU);
		windowMenu.add(getWindowCascadeMenuItem());
		windowMenu.add(getWindowMosaicMenuItem());
		windowMenu.add(getWindowMinimizeAllMenuItem());
		windowMenu.add(getWindowRestoreAllMenuItem());
		windowMenu.addSeparator();
		windowMenu.add(getWindowPreviousMenuItem());
		windowMenu.add(getWindowNextMenuItem());

	}

	/**
	 * This method initializes windowMinimizeAllMenuItem.
	 * 
	 * @return a new item 'Window - MinimizeAll'.
	 */
	private JMenuItem getWindowMinimizeAllMenuItem() {

		if (windowMinimizeAllMenuItem == null) {
			windowMinimizeAllMenuItem = new JMenuItem();
			windowMenu.setName(WINDOW_MINIMIZEALL_MENUITEM);
			windowMinimizeAllMenuItem.setText(stringResource
				.getString(WINDOW_MINIMIZEALL_MENUITEM + ".Label"));
			windowMinimizeAllMenuItem.setMnemonic(stringResource.getString(
				WINDOW_MINIMIZEALL_MENUITEM + ".Mnemonic").charAt(0));
			windowMinimizeAllMenuItem
				.setActionCommand(WINDOW_MINIMIZEALL_MENUITEM);
			windowMinimizeAllMenuItem.addActionListener(listener);
			windowMinimizeAllMenuItem.setEnabled(false);
		}

		return windowMinimizeAllMenuItem;

	}

	/**
	 * This method initializes windowRestoreAllMenuItem.
	 * 
	 * @return a new item 'Window - RestoreAll'.
	 */
	private JMenuItem getWindowRestoreAllMenuItem() {

		if (windowRestoreAllMenuItem == null) {
			windowRestoreAllMenuItem = new JMenuItem();
			windowMenu.setName(WINDOW_RESTOREALL_MENUITEM);
			windowRestoreAllMenuItem.setText(stringResource
				.getString(WINDOW_RESTOREALL_MENUITEM + ".Label"));
			windowRestoreAllMenuItem.setMnemonic(stringResource.getString(
				WINDOW_RESTOREALL_MENUITEM + ".Mnemonic").charAt(0));
			windowRestoreAllMenuItem
				.setActionCommand(WINDOW_RESTOREALL_MENUITEM);
			windowRestoreAllMenuItem.addActionListener(listener);
			windowRestoreAllMenuItem.setEnabled(false);
		}

		return windowRestoreAllMenuItem;

	}

	/**
	 * This method initializes windowCascadeMenuItem.
	 * 
	 * @return a new item 'Window - Cascade'
	 */
	private JMenuItem getWindowCascadeMenuItem() {

		if (windowCascadeMenuItem == null) {
			windowCascadeMenuItem = new JMenuItem();
			windowMenu.setName(WINDOW_CASCADE_MENUITEM);
			windowCascadeMenuItem.setText(stringResource
				.getString(WINDOW_CASCADE_MENUITEM + ".Label"));
			windowCascadeMenuItem.setMnemonic(stringResource.getString(
				WINDOW_CASCADE_MENUITEM + ".Mnemonic").charAt(0));
			windowCascadeMenuItem.setActionCommand(WINDOW_CASCADE_MENUITEM);
			windowCascadeMenuItem.addActionListener(listener);
			windowCascadeMenuItem.setEnabled(false);
		}

		return windowCascadeMenuItem;

	}

	/**
	 * This method initializes windowMosaicMenuItem.
	 * 
	 * @return a new item 'Window - Mosaic'
	 */
	private JMenuItem getWindowMosaicMenuItem() {

		if (windowMosaicMenuItem == null) {
			windowMosaicMenuItem = new JMenuItem();
			windowMenu.setName(WINDOW_MOSAIC_MENUITEM);
			windowMosaicMenuItem.setText(stringResource
				.getString(WINDOW_MOSAIC_MENUITEM + ".Label"));
			windowMosaicMenuItem.setMnemonic(stringResource.getString(
				WINDOW_MOSAIC_MENUITEM + ".Mnemonic").charAt(0));
			windowMosaicMenuItem.setActionCommand(WINDOW_MOSAIC_MENUITEM);
			windowMosaicMenuItem.addActionListener(listener);
			windowMosaicMenuItem.setEnabled(false);
		}

		return windowMosaicMenuItem;

	}

	/**
	 * This method initializes windowPreviousMenuItem.
	 * 
	 * @return a new item 'Window - Previous'.
	 */
	private JMenuItem getWindowPreviousMenuItem() {

		if (windowPreviousMenuItem == null) {
			windowPreviousMenuItem = new JMenuItem();
			windowMenu.setName(WINDOW_PREVIOUS_MENUITEM);
			windowPreviousMenuItem.setText(stringResource
				.getString(WINDOW_PREVIOUS_MENUITEM + ".Label"));
			windowPreviousMenuItem.setMnemonic(stringResource.getString(
				WINDOW_PREVIOUS_MENUITEM + ".Mnemonic").charAt(0));
			windowPreviousMenuItem.setActionCommand(WINDOW_PREVIOUS_MENUITEM);
			windowPreviousMenuItem.addActionListener(listener);
			windowPreviousMenuItem.setEnabled(false);
		}

		return windowPreviousMenuItem;

	}

	/**
	 * This method initializes windowNextMenuItem.
	 * 
	 * @return a new item 'Window - Next'.
	 */
	private JMenuItem getWindowNextMenuItem() {

		if (windowNextMenuItem == null) {
			windowNextMenuItem = new JMenuItem();
			windowMenu.setName(WINDOW_NEXT_MENUITEM);
			windowNextMenuItem.setText(stringResource
				.getString(WINDOW_NEXT_MENUITEM + ".Label"));
			windowNextMenuItem.setMnemonic(stringResource.getString(
				WINDOW_NEXT_MENUITEM + ".Mnemonic").charAt(0));
			windowNextMenuItem.setActionCommand(WINDOW_NEXT_MENUITEM);
			windowNextMenuItem.addActionListener(listener);
			windowNextMenuItem.setEnabled(false);
		}

		return windowNextMenuItem;

	}

	/**
	 * This method initializes jSeparator
	 * 
	 * @return javax.swing.JSeparator
	 */
	private JSeparator getJSeparator() {

		if (jSeparator == null) {
			jSeparator = new JSeparator();
		}

		return jSeparator;

	}

	/**
	 * Adds a new menu item to the panel list that appears below the others menu
	 * options.
	 * 
	 * @param panel
	 *            panel that is represented by the menu.
	 * @param text
	 *            caption of the menu item.
	 */
	public void addPanelMenuItem(JPanel panel, String text) {

		JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(text);

		menuItem.addActionListener(listener);
		groupSelectionPanels.add(menuItem);
		if (panelToMenuItem.size() == 0) {
			windowMenu.add(getJSeparator());
		}
		panelToMenuItem.put(panel, menuItem);
		menuItemToPanel.put(menuItem, panel);
		windowMenu.add(menuItem);
		menuItem.addActionListener(listener);

	}

	/**
	 * Modifies the text of a panel menu item.
	 * 
	 * @param panel
	 *            panel that is represented by the menu.
	 * @param text
	 *            new caption of the menu item.
	 */
	public void modifyPanelMenuItem(JPanel panel, String text) {

		JCheckBoxMenuItem menuItem = panelToMenuItem.get(panel);

		if (menuItem != null) {
			menuItem.setText(text);
		}

	}

	/**
	 * Removes the menu item from the list associated with a panel.
	 * 
	 * @param panel
	 *            that is represented by the menu.
	 */
	public void removePanelMenuItem(JPanel panel) {

		JCheckBoxMenuItem menuItem = panelToMenuItem.get(panel);

		panelToMenuItem.remove(panel);
		menuItemToPanel.remove(menuItem);
		groupSelectionPanels.remove(menuItem);
		windowMenu.remove(menuItem);
		if (panelToMenuItem.size() == 0) {
			windowMenu.remove(getJSeparator());
		}

	}

	/**
	 * Returns the panel associated with the menu item.
	 * 
	 * @param menuItem
	 *            menu to which the menu is related.
	 * @return the panel associated with the menu.
	 */
	public JPanel getPanelByMenuItem(JCheckBoxMenuItem menuItem) {

		return menuItemToPanel.get(menuItem);

	}

	/**
	 * Selects the checkbox menu item using the panel related to it.
	 * 
	 * @param panel
	 *            panel related to the menu.
	 */
	public void selectMenuItemByPanel(JPanel panel) {

		panelToMenuItem.get(panel).setSelected(true);

	}

	/**
	 * This method enables all the menus.
	 */
	public void enableMenuItems() {

		windowMinimizeAllMenuItem.setEnabled(true);
		windowRestoreAllMenuItem.setEnabled(true);
		windowCascadeMenuItem.setEnabled(true);
		windowMosaicMenuItem.setEnabled(true);
		windowPreviousMenuItem.setEnabled(true);
		windowNextMenuItem.setEnabled(true);

	}

	/**
	 * This method disables all the menus.
	 */
	public void disableMenuItems() {

		windowMinimizeAllMenuItem.setEnabled(false);
		windowRestoreAllMenuItem.setEnabled(false);
		windowCascadeMenuItem.setEnabled(false);
		windowMosaicMenuItem.setEnabled(false);
		windowPreviousMenuItem.setEnabled(false);
		windowNextMenuItem.setEnabled(false);

	}
}
