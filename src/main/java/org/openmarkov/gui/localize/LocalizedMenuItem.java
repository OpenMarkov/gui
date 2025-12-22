/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.localize;

import org.openmarkov.gui.loader.element.IconBind;

import javax.swing.*;

@SuppressWarnings("serial") public class LocalizedMenuItem extends JMenuItem {
    
    public LocalizedMenuItem(String name, String actionCommand) {
        this.setName(name);
        this.setText(MenuLocalizer.getLabel(name));
        this.setMnemonic(MenuLocalizer.getMnemonic(name).charAt(0));
        this.setActionCommand(actionCommand);
	}
    
    public LocalizedMenuItem(String name, String actionCommand, IconBind iconBind, KeyStroke keyStroke) {
        this(name, actionCommand);
        if (iconBind != null) {
            this.setIcon(iconBind.icon());
        }
        if (keyStroke != null) {
            this.setAccelerator(keyStroke);
        }
	}

}
