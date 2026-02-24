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

public abstract class CommonDBOMFileChooser extends OMFileChooser {

	protected static CaseDatabaseManager caseDbManager = new CaseDatabaseManager();

	public CommonDBOMFileChooser(boolean acceptAllFiles) {
		super();
		setAcceptAllFileFilterUsed(acceptAllFiles);
		setCurrentDirectory(LocalPreferences.LATEST_OPEN_DATASET_DIRECTORY.get());
		rescanCurrentDirectory();
	}

	@Override
	public int showOpenDialog(Component parent) {
		setCurrentDirectory(LocalPreferences.LATEST_OPEN_DATASET_DIRECTORY.get());
		int result = super.showOpenDialog(parent);
		if (result == JFileChooser.APPROVE_OPTION) {
			LocalPreferences.LATEST_OPEN_DATASET_DIRECTORY.set(getSelectedFile().getAbsoluteFile());
		}
		return result;
	}

	@Override
	public int showSaveDialog(Component parent) {
		setCurrentDirectory(LocalPreferences.LATEST_OPEN_DATASET_DIRECTORY.get());
		int result = super.showSaveDialog(parent);
		if (result == JFileChooser.APPROVE_OPTION) {
			if (getFileFilter() instanceof FileFilterAll) {
				LocalPreferences.LATEST_SAVED_DATASET_FORMAT
						.set(((FileFilterAll) getFileFilter()).getFileDescription());
			}
			LocalPreferences.LATEST_SAVED_DATASET_DIRECTORY.set(getSelectedFile().getParentFile());
		}
		return result;
	}

}
