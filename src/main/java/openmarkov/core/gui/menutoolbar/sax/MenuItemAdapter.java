package openmarkov.core.gui.menutoolbar.sax;


import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import javax.swing.JFrame;
import javax.swing.JMenuItem;


/*
 * This class will do the work when the items were activated/deselected/checked
 * @author jlgozalo
 * 
 * @version 1.0
 */
public class MenuItemAdapter implements MenuItemHandler {

	/**
	 * the parent Frame where this menu item is located
	 */
	private JFrame aParentFrame = null;

	/**
	 * constructor
	 */
	public MenuItemAdapter() {

	}

	/**
	 * Called when a JMenuItem is activated.
	 */
	public void itemActivated(JMenuItem item, ActionEvent event, String sCommand) {

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

	/**
	 * @return the aParentFrame
	 */
	public JFrame getAParentFrame() {

		return this.aParentFrame;
	}

	/**
	 * @param parentFrame
	 *            the aParentFrame to set
	 */
	public void setAParentFrame(JFrame parentFrame) {

		this.aParentFrame = parentFrame;
	}
}