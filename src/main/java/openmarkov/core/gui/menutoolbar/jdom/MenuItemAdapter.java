package openmarkov.core.gui.menutoolbar.jdom;


import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import javax.swing.JMenuItem;


/*
 * This class will do the work when the items were activated/deselected/checked
 * @author jlgozalo
 * 
 * @version 1.0
 */
public class MenuItemAdapter implements MenuItemHandler {

	/**
	 * constructor
	 */
	public MenuItemAdapter() {

	}

	/**
	 * Called when a JMenuItem is activated.
	 */
	public void itemActivated(JMenuItem item, ActionEvent event, String sCommand) {

		System.out.println("que pasaaaaa");
	}

	/**
	 * Called when a CheckboxMenuItem is deselected.
	 */
	public void itemDeselected(JMenuItem item, ItemEvent event, String sCommand) {

	}

	/**
	 * Called when a CheckboxMenuItem is selected.
	 */
	public void itemSelected(JMenuItem item, ItemEvent event, String sCommand) {

	}
}