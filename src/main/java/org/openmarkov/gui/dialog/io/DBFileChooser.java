/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.gui.dialog.io;

import org.openmarkov.core.io.database.plugin.CaseDatabaseManager;
import org.openmarkov.gui.configuration.OpenMarkovPreferences;
import org.openmarkov.gui.configuration.OpenMarkovPreferencesKeys;

import javax.swing.*;
import java.awt.*;
import java.io.File;

@SuppressWarnings("serial") public class DBFileChooser extends FileChooser {
	protected static CaseDatabaseManager caseDbManager = new CaseDatabaseManager();

	public DBFileChooser(boolean acceptAllFiles) {
		super(acceptAllFiles);
		File currentDirectory = new File(OpenMarkovPreferences
                                                 .get(OpenMarkovPreferencesKeys.LATEST_OPEN_DATASET_DIRECTORY, OpenMarkovPreferences.OPENMARKOV_DIRECTORIES, "."));
		setCurrentDirectory(currentDirectory);
	}

	@Override public int showOpenDialog(Component parent) {
		int result = super.showOpenDialog(parent);
		if (result == JFileChooser.APPROVE_OPTION) {
            OpenMarkovPreferences.set(OpenMarkovPreferencesKeys.LATEST_OPEN_DATASET_DIRECTORY, getSelectedFile().getAbsolutePath(),
                                      OpenMarkovPreferences.OPENMARKOV_DIRECTORIES);
		}
		return result;
	}

	@Override public int showSaveDialog(Component parent) {
		int result = super.showSaveDialog(parent);
		if (result == JFileChooser.APPROVE_OPTION) {
            OpenMarkovPreferences.set(OpenMarkovPreferencesKeys.LATEST_SAVED_DATASET_FORMAT, ((FileFilterAll) getFileFilter()).getFileDescription(),
                                      OpenMarkovPreferences.OPENMARKOV_FORMATS);
            OpenMarkovPreferences.set(OpenMarkovPreferencesKeys.LATEST_SAVED_DATASET_DIRECTORY, ((FileFilterAll) getFileFilter()).getFileDescription(),
                                      OpenMarkovPreferences.OPENMARKOV_FORMATS);
		}
		return result;
	}

}
