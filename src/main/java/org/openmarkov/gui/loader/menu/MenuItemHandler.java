/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.loader.menu;


/*
 * Interface
 */

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

/**
 * Menu XML uses this interface to notify client objects of MenuItem events.
 *
 * @author jlgozalo
 * @version 1.0
 */
public interface MenuItemHandler {

	/**
	 * Called when a JMenuItem is activated.
	 */
	public void itemActivated(JMenuItem item, ActionEvent event, String sCommand);

	/**
	 * Called when a CheckboxMenuItem is deselected.
	 */
	public void itemDeselected(JMenuItem item, ItemEvent event, String sCommand);

	/**
	 * Called when a CheckboxMenuItem is selected.
	 */
	public void itemSelected(JMenuItem item, ItemEvent event, String sCommand);
}