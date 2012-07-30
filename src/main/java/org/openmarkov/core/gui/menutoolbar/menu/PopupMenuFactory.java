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

import javax.swing.JPopupMenu;

import org.openmarkov.core.gui.graphic.VisualElement;
import org.openmarkov.core.gui.graphic.VisualLink;
import org.openmarkov.core.gui.graphic.VisualNode;
import org.openmarkov.core.gui.menutoolbar.common.MenuAssistant;
import org.openmarkov.core.gui.menutoolbar.common.MenuToolBarBasic;
import org.openmarkov.core.gui.oon.InstancePopup;
import org.openmarkov.core.gui.oon.VisualInstance;
import org.openmarkov.core.gui.window.edition.EditorPanel;




/**
 * This class has a reference to all the popup menus of the application.
 * 
 * @author jmendoza
 */
public class PopupMenuFactory implements MenuToolBarBasic {

	/**
	 * Constant that indentifies the network popup.
	 */
	public static final int NETWORK = 0;

	/**
	 * Constant that indentifies the node popup.
	 */
	public static final int NODE = 1;

	/**
	 * Constant that indentifies the link popup.
	 */
	public static final int LINK = 2;
	
	/**
	 * Constant that indentifies the instance popup.
	 */
	public static final int INSTANCE = 3;
	
	/**
	 * Popup menu that has the options of a whole network.
	 */
	private PopupMenuBasic networkPopup = null;

	/**
	 * Popup menu that has the options of a node.
	 */
	private PopupMenuBasic nodePopup = null;

	/**
	 * Popup menu that has the options of a link.
	 */
	private PopupMenuBasic linkPopup = null;
	
	/**
	 * Popup menu that has the options of an instance.
	 */
	private PopupMenuBasic instancePopup = null;	

	/**
	 * Assistant that manages all the popup menus.
	 */
	private MenuAssistant menuAssistant = null;

	/**
	 * Listener for all the popup menus.
	 */
	private ActionListener listener = null;
	
	/**
	 * Creates a new instance.
	 * 
	 * @param newListener
	 *            listener of the user's actions.
	 */
	public PopupMenuFactory(ActionListener newListener) {

		listener = newListener;
		initialize();
	}

	/**
	 * This method initialises the instance.
	 */
	private void initialize() {

		menuAssistant = new MenuAssistant();
	}

	/**
	 * This method initialises networkPopup.
	 * 
	 * @return the network panel popup menu.
	 */
	public JPopupMenu getNetworkPopup() {

		if (networkPopup == null) {
			networkPopup = new NetworkPopup(listener);
			networkPopup.setName("networkPopup");
			menuAssistant.addMenu (networkPopup);
		}
		return networkPopup;
	}

	/**
	 * This method initialises nodePopup.
	 * @param panel 
	 * @param selectedElement 
	 * 
	 * @return the node popup menu.
	 */
	private JPopupMenu getNodePopup(VisualNode selectedNode, EditorPanel panel) {

	    menuAssistant.removeMenu (nodePopup);
		nodePopup = new NodePopup(listener, selectedNode, panel);
		nodePopup.setName("nodePopup");
        menuAssistant.addMenu (nodePopup);
		return nodePopup;
	}

	/**
	 * This method initialises linkPopup.
	 * @param panel 
	 * @param selectedLink 
	 * 
	 * @return the link popup menu.
	 */
	private JPopupMenu getLinkPopup(VisualLink selectedLink, EditorPanel panel) {

        menuAssistant.removeMenu (linkPopup);
		linkPopup = new LinkPopup(listener, selectedLink, panel);
		linkPopup.setName("linkPopup");
        menuAssistant.addMenu (linkPopup);
		return linkPopup;
	}
	
	/**
	 * This method initialises linkPopup.
	 * @param panel 
	 * @param selectedInstance 
	 * 
	 * @return the link popup menu.
	 */
	//TODO OOBN start
	private JPopupMenu getInstancePopup(VisualInstance selectedInstance, EditorPanel panel) {

		if (instancePopup == null) {
			instancePopup = new InstancePopup(listener);
			instancePopup.setName("instancePopup");
            menuAssistant.addMenu (instancePopup);
		}
		return instancePopup;
	}
    //TODO OOBN end
	
	/**
	 * Enables or disabled an option identified by an action command.
	 * 
	 * @param actionCommand
	 *            action command that identifies the option.
	 * @param b
	 *            true to enable the option, false to disable.
	 */
	public void setOptionEnabled(String actionCommand, boolean b) {

		menuAssistant.setOptionEnabled(actionCommand, b);
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

		menuAssistant.setOptionSelected(actionCommand, b);
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
	public void addOptionText(String actionCommand, String text) {

		menuAssistant.addOptionText(actionCommand, text);
	}

	/**
	 *Changes the text of menu item
	 * 
	 * @param actionCommand
	 *            action command that identifies the option.
	 * @param text
	 *            text to set to the Item.
	 */
	public void setText(String actionCommand, String text) {

		menuAssistant.setText(actionCommand, text);
	}

	/**
	 * Returns an instance of a pop up menu given the class and some additional info
	 * @param popupClass
	 * @param panel
	 * @return
	 */
    public JPopupMenu getPopupMenu (VisualElement selectedElement, EditorPanel panel)
    {
        JPopupMenu popUpMenu = null;
        if (VisualNode.class.isAssignableFrom (selectedElement.getClass ())) {
            popUpMenu = getNodePopup((VisualNode)selectedElement, panel);
        }else if(VisualLink.class.isAssignableFrom (selectedElement.getClass ()))
        {
            popUpMenu = getLinkPopup((VisualLink)selectedElement, panel);
        }else if(VisualLink.class.isAssignableFrom (selectedElement.getClass ()))
        {
          //TODO OOBN 
          popUpMenu =  getInstancePopup((VisualInstance)selectedElement, panel);
        }
        return popUpMenu;
    }
}
