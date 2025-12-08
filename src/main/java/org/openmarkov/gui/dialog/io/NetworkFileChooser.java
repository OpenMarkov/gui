/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.dialog.io;

import org.openmarkov.core.exception.UnrecoverableException;
import org.openmarkov.gui.configuration.OpenMarkovLocalPreferences;
import org.openmarkov.core.io.format.annotation.FormatManager;
import org.xml.sax.SAXException;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * This class implements a file chooser dialog file to select OpenMarkov files.
 *
 * @author ibermejo
 */
@SuppressWarnings("serial") public class NetworkFileChooser extends FileChooser {
	/**
	 * Creates a new file chooser that starts in the current directory,
	 * filtering the files with the file filters.
	 *
	 * @param isOpening Indicates if the file chooser is for opening a file (isOpening=true) or for saving (isOpening=false)
	 */
	public NetworkFileChooser(boolean acceptAllfile, boolean isOpening) {
		super(acceptAllfile);
		FormatManager formatManager = FormatManager.getInstance();
		HashMap<String, String> parsersListForFilters = isOpening ?
				formatManager.getReaders() :
				formatManager.getWriters();
		List<String> extensionList = new ArrayList<String>();
		// for (String item : parsersListForFilters.keySet ())
        List<String> descriptions = new ArrayList<>(parsersListForFilters.keySet());
		Collections.sort(descriptions);
		for (String item : descriptions)
		{
        	/*
        	addChoosableFileFilter (new FileFilterAll (parsersListForFilters.get (item), item));
        	*/
			String itemExtension = parsersListForFilters.get(item);

			addChoosableFileFilter(new FileFilterAll(itemExtension, item));

		}
		File currentDirectory = null;
        /*
        setFileFilter (OpenMarkovPreferences.get (OpenMarkovPreferences.LAST_OPENED_FORMAT,
                                                  OpenMarkovPreferences.OPENMARKOV_FORMATS, "pgmx"));
        */
		//UNCLEAR Where is set pgmx? By default LAST_OPENED_FORMAT=pgmx

		if (isOpening) {
            currentDirectory = OpenMarkovLocalPreferences.LATEST_OPEN_DIRECTORY.get();
            setFileFilter("OpenMarkov");
		} else {
            setFileFilter(OpenMarkovLocalPreferences.LATEST_SAVED_NETWORK_FORMAT.get());
		}

        setCurrentDirectory(currentDirectory);
    }

	public NetworkFileChooser() {
		this(false, true);
	}

	@Override public int showOpenDialog(Component parent) {
		int result = super.showOpenDialog(parent);
		if (result == JFileChooser.APPROVE_OPTION) {
            OpenMarkovLocalPreferences.LATEST_OPEN_DIRECTORY.set(getSelectedFile());
            /*
            OpenMarkovPreferences.set (OpenMarkovPreferences.LAST_OPENED_FORMAT,
                                       ((FileFilterBasic) getFileFilter ()).getFilterExtension (),
                                       OpenMarkovPreferences.OPENMARKOV_FORMATS);
            */
            try {
                OpenMarkovLocalPreferences.LATEST_NETWORK_FORMAT.set(getPgmxFileFormat());
            } catch (SAXException | IOException e) {
                throw new UnrecoverableException(e);
            }
        }
		return result;
	}

	@Override public int showSaveDialog(Component parent) {
		int result = super.showSaveDialog(parent);
		if (result == JFileChooser.APPROVE_OPTION) {
            OpenMarkovLocalPreferences.LATEST_OPEN_DIRECTORY.set(getSelectedFile());
            /*
            OpenMarkovPreferences.set (OpenMarkovPreferences.LAST_OPENED_FORMAT,
                                       ((FileFilterBasic) getFileFilter ()).getFilterExtension (),
                                       OpenMarkovPreferences.OPENMARKOV_FORMATS);
            */
            OpenMarkovLocalPreferences.LATEST_SAVED_NETWORK_FORMAT.set(((FileFilterAll) getFileFilter()).getFileDescription());
		}
		return result;
	}

	@Override public void approveSelection() {
		if (getDialogType() == SAVE_DIALOG) {
			File selectedFile = getSelectedFile();
			if ((selectedFile != null) && selectedFile.exists()) {
				int response = JOptionPane.showConfirmDialog(this, "The file " + selectedFile.getName()
								+ " already exists. Do you want to replace the existing file?", "Ovewrite file",
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (response != JOptionPane.YES_OPTION)
					return;
			}
		}
		super.approveSelection();
	}
}
