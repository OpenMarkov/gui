/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.menutoolbar.toolbar;


import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import org.openmarkov.core.gui.window.MainPanel;
import org.openmarkov.core.gui.window.mdi.FrameContentPanel;
import org.openmarkov.core.gui.window.mdi.MDIListener;


/**
 * This class fills its combobox and listen to it to send action commands
 * defined in the class ActionCommands.
 * 
 * @author ibermejo
 */
public class ClassComboBox extends JComboBox implements MDIListener {

	/**
	 * Static field for serializable class.
	 */
	private static final long serialVersionUID = 5380198895714343936L;

	/**
	 * Object that listen to the user's actions.
	 */
	private ActionListener listener;

	/**
	 * Constructor that fills and initialize the combobox.
	 * 
	 * @param newListener
	 *            object that listens to the zoom values.
	 */
	public ClassComboBox(ActionListener newListener) {

		super();
		listener = newListener;
		initialize();
	}

	/**
	 * This method initialises this instance.
	 */
	private void initialize() {

		setEditable(false);
		setPreferredSize(new Dimension(120, 25));
		setMaximumSize(getPreferredSize());
		setMinimumSize(getPreferredSize());
		MainPanel.getUniqueInstance().getMdi().addFrameStateListener(this);
	}

//	/**
//	 * Invoked when an item has been selected.
//	 * 
//	 * @param e
//	 *            event information.
//	 */
//	public void selectedItemChanged(ItemEvent e) {
//		//TODO implement
//		listener.actionPerformed(new MenuActionEvent(this, 0, newActionCommand, this.selectedItemChanged());
//	}


	/**
	 * Enables the combo box so that items can be selected. When the combo box
	 * is disabled, items cannot be selected, values cannot be typed into its
	 * field and no elements are selected.
	 * 
	 * @param b
	 *            true enables the combobox and false disables it.
	 */
	@Override
	public void setEnabled(boolean b) {

		if (!b) {
			setSelectedIndex(-1);
		}
		super.setEnabled(b);
	}

	public void frameClosed(FrameContentPanel contentPanel) {
		// Remove from list
		this.removeItem(contentPanel.getTitle());
	}
	
	public void frameSelected(FrameContentPanel contentPanel) {
		// Do nothing
		
	}

	public void frameTitleChanged(FrameContentPanel contentPanel,
			String oldName, String newName) {
		// TODO Update list
		this.removeItem(oldName);
		this.addItem(newName);
	}

	public boolean frameClosing(FrameContentPanel contentPanel) {
		// Do nothing
		return true;
	}

	public void frameOpened(FrameContentPanel contentPanel) {
		//No need to add here as setTitle adds it before reaching here
		//this.addItem(contentPanel.getTitle());
	}
}
