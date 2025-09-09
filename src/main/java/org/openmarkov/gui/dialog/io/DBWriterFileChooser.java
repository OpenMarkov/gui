/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.gui.dialog.io;

import org.openmarkov.gui.configuration.OpenMarkovPreferences;
import org.openmarkov.gui.configuration.OpenMarkovPreferencesKeys;

import java.io.File;
import java.util.HashMap;

@SuppressWarnings("serial") public class DBWriterFileChooser extends DBFileChooser {
	public DBWriterFileChooser(boolean acceptAllFiles) {
		super(acceptAllFiles);
		HashMap<String, String> writersInfo = caseDbManager.getAllWriters();
		for (String extension : writersInfo.keySet()) {
			addChoosableFileFilter(new FileFilterAll(extension, writersInfo.get(extension)));
		}
        setFileFilter(OpenMarkovPreferences
                              .get(OpenMarkovPreferencesKeys.LATEST_SAVED_DATASET_FORMAT, OpenMarkovPreferences.OPENMARKOV_FORMATS,
                                   FileChooser.DEFAULT_FILE_FORMAT));
        File currentDirectory = new File(OpenMarkovPreferences
                                                 .get(OpenMarkovPreferencesKeys.LATEST_SAVED_DATASET_DIRECTORY, OpenMarkovPreferences.OPENMARKOV_DIRECTORIES, "."));
        setCurrentDirectory(currentDirectory);
	}

	public DBWriterFileChooser() {
		this(false);
	}
}
