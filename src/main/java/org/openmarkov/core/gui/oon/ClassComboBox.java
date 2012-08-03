/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.oon;


import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JInternalFrame;

import org.openmarkov.core.gui.window.MainPanel;
import org.openmarkov.core.gui.window.edition.NetworkPanel;
import org.openmarkov.core.gui.window.mdi.FrameContentPanel;
import org.openmarkov.core.gui.window.mdi.MDIListener;


/**
 * This class fills its combobox and listen to it to send action commands
 * defined in the class ActionCommands.
 * 
 * @author ibermejo
 */
public class ClassComboBox extends JComboBox<String> implements MDIListener {

	/**
	 * Static field for serializable class.
	 */
	private static final long serialVersionUID = 5380198895714343936L;

	/**
	 * Object that listen to the user's actions.
	 */
	private ActionListener listener;
	
	/**
	 *  List of class names
	 */
	List<String> classNames;
	
	String currentFrameTitle;
	
	
	/**
	 * Constructor that fills and initialize the combobox.
	 * 
	 * @param newListener
	 *            object that listens to the zoom values.
	 */
	public ClassComboBox(ActionListener newListener) {

		super();
		listener = newListener;
		classNames = new ArrayList<>();
		MainPanel mainPanel = MainPanel.getUniqueInstance();
		if(mainPanel.getMainPanelMenuAssistant().getCurrentNetworkPanel() != null)
		{
			currentFrameTitle = mainPanel.getMainPanelMenuAssistant().getCurrentNetworkPanel().getTitle();
		}
		for(JInternalFrame frame : mainPanel.getMdi().getFrames ())
		{
		    if(frame.getContentPane () instanceof NetworkPanel)
		    {
		    	classNames.add(frame.getTitle ());
		    }
		}
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
		updateComboBoxData(classNames, currentFrameTitle);
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
		classNames.remove(contentPanel.getTitle());
		updateComboBoxData(classNames, currentFrameTitle);
	}
	
	public void frameSelected(FrameContentPanel contentPanel) {
		currentFrameTitle = contentPanel.getTitle();
		updateComboBoxData(classNames, currentFrameTitle);
	}

	public void frameTitleChanged(FrameContentPanel contentPanel, String oldName, String newName) {
		if(oldName.equals(currentFrameTitle))
		{
			currentFrameTitle = newName;
		}
		classNames.remove(oldName);
		classNames.add(newName);
		updateComboBoxData(classNames, currentFrameTitle);
		this.setSelectedItem (newName);
	}

	public boolean frameClosing(FrameContentPanel contentPanel) {
		// Do nothing
		return true;
	}

	public void frameOpened(FrameContentPanel contentPanel) {
		//No need to add here as setTitle adds it before reaching here
		//this.addItem(contentPanel.getTitle());
	}
	
	private void updateComboBoxData(List<String> classNames, String selectedFrame)
	{
		List<String> showableClassNames = new ArrayList<>(classNames);
		showableClassNames.remove(selectedFrame);
		updateComboBoxData(showableClassNames);
	}
	
	private void updateComboBoxData(List<String> classNames)
	{
		this.removeAllItems();
		for(String className : classNames)
		{
	        this.addItem(className);
		}
	}
}
