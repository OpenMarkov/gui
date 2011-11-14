package org.openmarkov.core.gui.menutoolbar.toolbar;


import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;

import org.openmarkov.core.gui.menutoolbar.common.MenuToolBarBasic;
import org.openmarkov.core.gui.menutoolbar.common.MenuToolBarBasicImpl;




/**
 * This class is used to set the common features of all toolbars of the
 * application.
 * 
 * @author jmendoza
 */
abstract class ToolBarBasic extends JToolBar implements MenuToolBarBasic {

    /**
     * Suffix to retrieve tooltip strings from a string resource.
     */
    String STRING_TOOLTIP_SUFFIX = ".ToolTip.Label";
    
    
	/**
	 * Object that listen to the user's actions.
	 */
	protected ActionListener listener;

	/**
	 * This method initialises this instance.
	 * 
	 * @param newListener
	 *            listener that listen to the user's actions.
	 */
	public ToolBarBasic(ActionListener newListener) {

		super();
		listener = newListener;
		initialize();
	}

	/**
	 * This method initialises this instance.
	 */
	private void initialize() {

		setFloatable(false);
		setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		setOrientation(JToolBar.HORIZONTAL);
		setRollover(false);
	}

	/**
	 * Returns the component that correspond to an action command.
	 * 
	 * @param actionCommand
	 *            action command that identifies the component.
	 * @return a components identified by the action command.
	 */
	protected abstract JComponent getJComponentActionCommand(
																String actionCommand);

	/**
	 * Enables or disabled an option identified by an action command.
	 * 
	 * @param actionCommand
	 *            action command that identifies the option.
	 * @param b
	 *            true to enable the option, false to disable.
	 */
	public void setOptionEnabled(String actionCommand, boolean b) {

		MenuToolBarBasicImpl.setOptionEnabled(
			getJComponentActionCommand(actionCommand), b);
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

		MenuToolBarBasicImpl.setOptionSelected(
			getJComponentActionCommand(actionCommand), b);
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
	@SuppressWarnings("unused")
	public void addOptionText(String actionCommand, String text) {

	}
	/**
	 * changes a text to the label of an option identified by an action command.
	 * 
	 * @param actionCommand
	 *            action command that identifies the option.
	 * @param text
	 *            text to set to Item. If null, nothing is
	 *            added.
	 */
	@SuppressWarnings("unused")
	public void setText(String actionCommand, String text) {

	}

}

