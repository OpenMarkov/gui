/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.localize;

import org.openmarkov.core.exception.OpenMarkovException;
import org.openmarkov.core.exception.ParserException;
import org.openmarkov.gui.plugin.PluginManager;
import org.openmarkov.gui.util.Utilities;
import org.openmarkov.plugin.PluginLoader;

import javax.swing.*;
import java.awt.*;

public class LocalizedException {

	private Exception openMarkovException;
	private Window ownerWindow;
	private String localizedMessage;
	private String localizedTitle;

	public LocalizedException (Exception openMarkovException, Window ownerWindow) {
		this.openMarkovException = openMarkovException;
		this.ownerWindow = ownerWindow;
		OpenMarkovException lAnnotation = openMarkovException.getClass().getAnnotation(OpenMarkovException.class);
		this.localizedTitle = StringDatabase.getUniqueInstance().getString(lAnnotation.name() + ".title");
		this.localizedMessage = StringDatabase.getUniqueInstance().getString(lAnnotation.name() + ".message");
	}

	public void showException(){
		JOptionPane.showMessageDialog(
				ownerWindow,
				localizedMessage,
				localizedTitle,
				JOptionPane.ERROR_MESSAGE);
	}

}
