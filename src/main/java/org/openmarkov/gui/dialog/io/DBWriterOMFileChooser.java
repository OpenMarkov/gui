/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.gui.dialog.io;

import org.openmarkov.gui.configuration.LocalPreferences;

import java.io.File;
import java.util.HashMap;

@SuppressWarnings("serial") public class DBWriterOMFileChooser extends CommonDBOMFileChooser {
 
	public DBWriterOMFileChooser(boolean acceptAllFiles) {
		super(acceptAllFiles);
		HashMap<String, String> writersInfo = caseDbManager.getAllWriters();
		for (String extension : writersInfo.keySet()) {
			addChoosableFileFilter(new FileFilterAll(extension, writersInfo.get(extension)));
		}
		setFileFilter(LocalPreferences.LATEST_SAVED_DATASET_FORMAT.get());
		File currentDirectory = LocalPreferences.LATEST_SAVED_DATASET_DIRECTORY.get();
        setCurrentDirectory(currentDirectory);
	}
	
}
