package openmarkov.core.gui.menutoolbar.sax;


import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import javax.swing.JMenuItem;

import openmarkov.core.gui.development.environment.OpenMarkovDevEnvFrame;
import openmarkov.core.gui.development.environment.OpenMarkovDevEnvProperties;



/*
 * This class will do the work when the items were activated/deselected/checked
 * @author jlgozalo
 * 
 * @version 1.0
 */
public class MenuItemDevAdapter extends MenuItemAdapter {

	/**
	 * the adittionalProperties structure
	 */
	private OpenMarkovDevEnvProperties aOpenMarkovDevEnvProperties = null;
	/**
	 * /** the parent Frame where this menu item is located
	 */
	private OpenMarkovDevEnvFrame aParentFrame = null;

	/**
	 * constructor
	 */
	public MenuItemDevAdapter() {

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
	 * @return the aOpenMarkovDevEnvProperties
	 */
	public OpenMarkovDevEnvProperties getAOpenMarkovDevEnvProperties() {

		return aOpenMarkovDevEnvProperties;
	}

	/**
	 * @param openMarkovDevEnvProperties
	 *            the aOpenMarkovDevEnvProperties to set
	 */
	public void setAOpenMarkovDevEnvProperties(
						OpenMarkovDevEnvProperties openMarkovDevEnvProperties) {

		aOpenMarkovDevEnvProperties = openMarkovDevEnvProperties;
	}

	/**
	 * @return the aParentFrame
	 */
	public OpenMarkovDevEnvFrame getAParentFrame() {

		return this.aParentFrame;
	}

	/**
	 * @param parentFrame
	 *            the aParentFrame to set
	 */
	public void setAParentFrame(OpenMarkovDevEnvFrame parentFrame) {

		this.aParentFrame = parentFrame;
	}

}