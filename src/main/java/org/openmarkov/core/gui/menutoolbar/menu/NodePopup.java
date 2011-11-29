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
 * This class implements a popup menu that is displayes when the user clicks on
 * a node.
 * 
 * @author jmendoza
 * @author jlgozalo 
 * @version 1.1 jlgozalo - Add change locale management setting the item names.
 */
class NodePopup extends PopupMenuBasic {

	/**
	 * Static field for serializable class.
	 */
	private static final long serialVersionUID = 8556550568033250304L;

	/**
	 * Object that represents the item 'Cut'.
	 */
	private JMenuItem cutMenuItem = null;

	/**
	 * Object that represents the item 'Copy'.
	 */
	private JMenuItem copyMenuItem = null;

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

	private JMenuItem relationMenuItem;

	/**
	 * Object that represents the item 'Expand'.
	 */
	private JMenuItem expandMenuItem = null;
	
	/**
	 * Object that represents the item 'Contract'.
	 */
	private JMenuItem contractMenuItem = null;
	
	/**
	 * Object that represents the item 'addFinding'.
	 */
	private JMenuItem addFindingMenuItem = null;
	
	/**
	 * Object that represents the item 'removeFinding'.
	 */
	private JMenuItem removeFindingMenuItem = null;

	private JMenuItem logMenuItem;
	
	/**
	 * This constructor creates a new instance.
	 * 
	 * @param newListener
	 *            object that listens to the menu events.
	 */
	public NodePopup(ActionListener newListener) {

		super(newListener);

		initialize();

	}

	/**
	 * This method initialises this instance.
	 */
	private void initialize() {

		stringResource =
			StringResourceLoader.getUniqueInstance().getBundleMenus();
		add(getCutMenuItem());
		add(getCopyMenuItem());
		addSeparator();
		add(getRemoveMenuItem());
		addSeparator();
		add(getPropertiesMenuItem());
		add(getRelationMenuItem());
		addSeparator();
		add(getExpandMenuItem());
		add(getContractMenuItem());
		addSeparator();
		add(getAddFindingMenuItem());
		add(getRemoveFindingMenuItem());
		addSeparator();
		add(getLogMenuItem());
		
	}

	private JMenuItem getLogMenuItem() {
		if (logMenuItem == null) {
            logMenuItem = new LocalizedMenuItem (MenuItemNames.EDIT_LOG_MENUITEM,
                                                 ActionCommands.LOG);
		logMenuItem.addActionListener(listener);
	}

	return logMenuItem;
	}

	/**
	 * This method initialises cutMenuItem.
	 * 
	 * @return a new 'Cut' menu item.
	 */
	private JMenuItem getCutMenuItem() {

		if (cutMenuItem == null) {
            cutMenuItem = new LocalizedMenuItem (MenuItemNames.EDIT_CUT_MENUITEM,
                                                 ActionCommands.CLIPBOARD_CUT);
			cutMenuItem.addActionListener(listener);
		}

		return cutMenuItem;

	}

	/**
	 * This method initialises copyMenuItem.
	 * 
	 * @return a new 'Copy' menu item.
	 */
	private JMenuItem getCopyMenuItem() {

		if (copyMenuItem == null) {
            copyMenuItem = new LocalizedMenuItem (MenuItemNames.EDIT_COPY_MENUITEM,
                                                  ActionCommands.CLIPBOARD_COPY);
			copyMenuItem.addActionListener(listener);
		}

		return copyMenuItem;

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

	/**
	 * This method initialises propertiesMenuItem.
	 * 
	 * @return a new 'Properties' menu item.
	 */
	private JMenuItem getPropertiesMenuItem() {

		if (propertiesMenuItem == null) {
            propertiesMenuItem = new LocalizedMenuItem (
                                                        MenuItemNames.EDIT_NODEPROPERTIES_MENUITEM,
                                                        ActionCommands.NODE_PROPERTIES);
			propertiesMenuItem.addActionListener(listener);
		}

		return propertiesMenuItem;

	}
	
	/**
	 * This method initialises tableMenuItem.
	 * 
	 * @return a new 'Table' menu item.
	 */
	private JMenuItem getRelationMenuItem() {

		if (relationMenuItem == null) {
			relationMenuItem = new LocalizedMenuItem(MenuItemNames.EDIT_NODERELATION_MENUITEM, ActionCommands.CHANGE_POTENTIAL);
			relationMenuItem.addActionListener(listener);
		}

		return relationMenuItem;

	}

	/**
	 * This method initialises expandMenuItem.
	 * 
	 * @return a new 'Expand' menu item.
	 */
	private JMenuItem getExpandMenuItem() {

		if (expandMenuItem == null) {
            expandMenuItem = new LocalizedMenuItem (
                                                    MenuItemNames.INFERENCE_EXPAND_NODE_MENUITEM,
                                                    ActionCommands.NODE_EXPANSION);
			expandMenuItem.addActionListener(listener);
		}

		return expandMenuItem;

	}

	/**
	 * This method initialises contractMenuItem.
	 * 
	 * @return a new 'Contract' menu item.
	 */
	private JMenuItem getContractMenuItem() {

		if (contractMenuItem == null) {
            contractMenuItem = new LocalizedMenuItem (
                                                      MenuItemNames.INFERENCE_CONTRACT_NODE_MENUITEM,
                                                      ActionCommands.NODE_CONTRACTION);
			contractMenuItem.addActionListener(listener);
		}

		return contractMenuItem;

	}
	
	/**
	 * This method initialises addFindingMenuItem.
	 * 
	 * @return a new 'addFinding' menu item.
	 */
	private JMenuItem getAddFindingMenuItem() {

		if (addFindingMenuItem == null) {
            addFindingMenuItem = new LocalizedMenuItem (
                                                        MenuItemNames.INFERENCE_ADD_FINDING_MENUITEM,
                                                        ActionCommands.NODE_ADD_FINDING);
			addFindingMenuItem.addActionListener(listener);
		}

		return addFindingMenuItem;

	}
	
	/**
	 * This method initialises removeFindingMenuItem.
	 * 
	 * @return a new 'removeFinding' menu item.
	 */
	private JMenuItem getRemoveFindingMenuItem() {

		if (removeFindingMenuItem == null) {
            removeFindingMenuItem = new LocalizedMenuItem (
                                                           MenuItemNames.INFERENCE_REMOVE_FINDING_MENUITEM,
                                                           ActionCommands.NODE_REMOVE_FINDING);
			removeFindingMenuItem.addActionListener(listener);
		}

		return removeFindingMenuItem;

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

		if (actionCommand.equals(ActionCommands.CLIPBOARD_CUT)) {
			component = cutMenuItem;
		} else if (actionCommand.equals(ActionCommands.CLIPBOARD_COPY)) {
			component = copyMenuItem;
		} else if (actionCommand.equals(ActionCommands.OBJECT_REMOVAL)) {
			component = removeMenuItem;
		} else if (actionCommand.equals(ActionCommands.NODE_PROPERTIES)) {
			component = propertiesMenuItem;
		} else if (actionCommand.equals(ActionCommands.CHANGE_POTENTIAL)) {
			component = relationMenuItem;
		} else if (actionCommand.equals(ActionCommands.NODE_EXPANSION)) {
			component = expandMenuItem;
		} else if (actionCommand.equals(ActionCommands.NODE_CONTRACTION)) {
			component = contractMenuItem;
		} else if (actionCommand.equals(ActionCommands.NODE_ADD_FINDING)) {
			component = addFindingMenuItem;
		} else if (actionCommand.equals(ActionCommands.NODE_REMOVE_FINDING)) {
			component = removeFindingMenuItem;
		} else if (actionCommand.equals(ActionCommands.LOG)) {
			component = logMenuItem;
		}

		return component;

	}
}
