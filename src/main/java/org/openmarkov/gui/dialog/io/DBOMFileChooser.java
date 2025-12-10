/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.gui.dialog.io;

import org.openmarkov.core.io.database.plugin.CaseDatabaseManager;
import org.openmarkov.gui.configuration.LocalPreferences;

import javax.swing.*;
import java.awt.*;
import java.io.File;

@SuppressWarnings("serial") public class DBOMFileChooser extends OMFileChooser {
	protected static CaseDatabaseManager caseDbManager = new CaseDatabaseManager();
    
    public DBOMFileChooser(boolean acceptAllFiles) {
        super();
        setAcceptAllFileFilterUsed(acceptAllFiles);
        rescanCurrentDirectory();
        File currentDirectory = LocalPreferences.LATEST_OPEN_DATASET_DIRECTORY.get();
		setCurrentDirectory(currentDirectory);
	}

	@Override public int showOpenDialog(Component parent) {
		int result = super.showOpenDialog(parent);
		if (result == JFileChooser.APPROVE_OPTION) {
            LocalPreferences.LATEST_OPEN_DATASET_DIRECTORY.set(getSelectedFile().getAbsoluteFile());
		}
		return result;
	}

	@Override public int showSaveDialog(Component parent) {
		int result = super.showSaveDialog(parent);
		if (result == JFileChooser.APPROVE_OPTION) {
            LocalPreferences.LATEST_SAVED_DATASET_FORMAT.set(((FileFilterAll) getFileFilter()).getFileDescription());
            LocalPreferences.LATEST_SAVED_DATASET_DIRECTORY.set(getSelectedFile().getParentFile());
		}
		return result;
	}

}
