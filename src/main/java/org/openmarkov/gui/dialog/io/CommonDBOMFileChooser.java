/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.gui.dialog.io;

import org.apache.commons.io.FilenameUtils;
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
        autoPickFilter();
        int result = super.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            LocalPreferences.LATEST_OPEN_DATASET_DIRECTORY.set(getSelectedFile().getAbsoluteFile());
            LocalPreferences.LATEST_SAVED_DATASET_EXTENSION.set(FilenameUtils.getExtension(getSelectedFile().getAbsoluteFile()
                                                                                                            .getName()));
        }
        return result;
    }
    
    @Override
    public int showSaveDialog(Component parent) {
        setCurrentDirectory(LocalPreferences.LATEST_OPEN_DATASET_DIRECTORY.get());
        autoPickFilter();
        int result = super.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            LocalPreferences.LATEST_SAVED_DATASET_EXTENSION.set(FilenameUtils.getExtension(getSelectedFile().getAbsoluteFile()
                                                                                                            .getName()));
            LocalPreferences.LATEST_SAVED_DATASET_DIRECTORY.set(getSelectedFile().getParentFile());
        }
        return result;
    }
    
    private void autoPickFilter() {
        File selectedFile = getRawSelectedFile();
        if (selectedFile != null) {
            var extension = FilenameUtils.getExtension(selectedFile.getName());
            for (var filter : getChoosableFileFilters()) {
                if (filter instanceof FileFilterAll fileFilterAll) {
                    if (fileFilterAll.getFilterExtension().equals(extension)) {
                        setFileFilter(filter);
                        return;
                    }
                }
            }
        }
        for (var filter : getChoosableFileFilters()) {
            if (filter instanceof FileFilterAll fileFilterAll) {
                if (fileFilterAll.getFilterExtension().equals(LocalPreferences.LATEST_SAVED_DATASET_EXTENSION.get())) {
                    setFileFilter(filter);
                    return;
                }
            }
        }
    }
    
}
