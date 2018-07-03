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
		this.localizedTitle = StringDatabase.getUniqueInstance().getString("GenericException.title");
		this.localizedMessage = StringDatabase.getUniqueInstance().getString("GenericException.message");

		if (openMarkovException instanceof OpenMarkovException) {

			String token = ((OpenMarkovException) openMarkovException).getToken();
			String newTitle = String.format(StringDatabase.getUniqueInstance().getString(token + ".title"));
			String formattedString = StringDatabase.getUniqueInstance().getString(token + ".message");
			String newMessage = String.format(formattedString, ((OpenMarkovException) openMarkovException).getAttributes());
			if (newTitle != null) {
				this.localizedTitle = newTitle;
			}
			if (newMessage != null) {
				this.localizedMessage = newMessage;
			}
		} else {
			this.localizedTitle = openMarkovException.getMessage();
			this.localizedMessage = openMarkovException.getLocalizedMessage();
		}
	}

	public void showException(){
		JOptionPane.showMessageDialog(
				ownerWindow,
				localizedMessage,
				localizedTitle,
				JOptionPane.ERROR_MESSAGE);
	}

}
