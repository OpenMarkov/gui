/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.localize;

import org.openmarkov.gui.loader.element.IconBind;

import javax.swing.*;

@SuppressWarnings("serial") public class LocalizedCheckBoxMenuItem extends JCheckBoxMenuItem {

	public LocalizedCheckBoxMenuItem(String name, String actionCommand, boolean useMnemonic) {
		this.setName(name);
		this.setText(MenuLocalizer.getLabel(name));
		if (useMnemonic) {
			this.setMnemonic(MenuLocalizer.getMnemonic(name).charAt(0));
		}
		this.setActionCommand(actionCommand);
	}

	public LocalizedCheckBoxMenuItem(String name, String actionCommand) {
		this(name, actionCommand, false);
	}
    
    public LocalizedCheckBoxMenuItem(String name, String actionCommand, IconBind iconBind, boolean useMnemonic) {
		this(name, actionCommand, useMnemonic);
        this.setIcon(iconBind.icon());
	}
    
    public LocalizedCheckBoxMenuItem(String name, String actionCommand, IconBind iconBind) {
        this(name, actionCommand, iconBind, true);
	}

}
