/**
 * 
 */
package org.openmarkov.core.gui.menutoolbar.menu;

import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JMenuItem;

import org.openmarkov.core.gui.localize.LocalizedMenuItem;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.gui.menutoolbar.common.ActionCommands;


/**
 * @author mpalacios
 *
 */
public class UncertaintyPopup extends PopupMenuBasic{

	public UncertaintyPopup(ActionListener newListener) {
		super(newListener);
		// TODO Auto-generated constructor stub
		initialize();
	}

	/**
	 * Static field for serializable class.
	 */
	private static final long serialVersionUID = 8556550568033250304L;

	/**
	 * Object that represents the item 'Cut'.
	 */
	private JMenuItem assignMenuItem = null;

	/**
	 * Object that represents the item 'Copy'.
	 */
	private JMenuItem editMenuItem = null;

	/**
	 * Object that represents the item 'Remove'.
	 */
	private JMenuItem removeMenuItem = null;

	
	/**
	 * String resource.
	 */
	private StringResource stringResource = null;

	/**
	 * This method initializes this instance.
	 */
	private void initialize() {

		stringResource =
			StringResourceLoader.getUniqueInstance().getBundleMenus();
		add(getAssignMenuItem());
		add(getEditMenuItem());
		//addSeparator();
		add(getRemoveMenuItem());
		
	}

	/**
	 * This method initializes assignMenuItem.
	 * 
	 * @return a new 'Assign' menu item.
	 */
	private JMenuItem getAssignMenuItem() {

		if (assignMenuItem == null) {
            assignMenuItem = new LocalizedMenuItem (
                                                    MainMenu.UNCERTAINTY_ASSIGN_MENUITEM,
                                                    ActionCommands.UNCERTAINTY_ASSIGN);
			assignMenuItem.addActionListener(listener);
		}

		return assignMenuItem;

	}

	/**
	 * This method initializes editMenuItem.
	 * 
	 * @return a new 'Edit' menu item.
	 */
	private JMenuItem getEditMenuItem() {

		if (editMenuItem == null) {
            editMenuItem = new LocalizedMenuItem (
                                                  MainMenu.UNCERTAINTY_EDIT_MENUITEM,
                                                  ActionCommands.UNCERTAINTY_EDIT);
			editMenuItem.addActionListener(listener);
		}

		return editMenuItem;
	}
	

	/**
	 * This method initializes removeMenuItem.
	 * 
	 * @return a new 'Remove' menu item.
	 */
	private JMenuItem getRemoveMenuItem() {

		if (removeMenuItem == null) {
            removeMenuItem = new LocalizedMenuItem (
                                                    MainMenu.UNCERTAINTY_REMOVE_MENUITEM,
                                                    ActionCommands.UNCERTAINTY_REMOVE);
			removeMenuItem.addActionListener(listener);
		}

		return removeMenuItem;

	}

	/**
	 * Returns the component that corresponds to an action command.
	 * 
	 * @param actionCommand
	 *            action command that identifies the component.
	 * @return a components identified by the action command.
	 */
	@Override
	public JComponent getJComponentActionCommand(String actionCommand) {

		JComponent component = null;

		if (actionCommand.equals(ActionCommands.UNCERTAINTY_ASSIGN)) {
			component = assignMenuItem;
		} else if (actionCommand.equals(ActionCommands.UNCERTAINTY_EDIT)) {
			component = editMenuItem;
		} else if (actionCommand.equals(ActionCommands.UNCERTAINTY_REMOVE)) {
			component = removeMenuItem;
		} 

		return component;

	}
	

}

