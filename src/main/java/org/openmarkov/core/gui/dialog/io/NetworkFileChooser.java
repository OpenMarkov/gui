/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.io;

import java.util.HashMap;

import org.openmarkov.core.io.format.annotation.FormatManager;

/**
 * This class implements a file chooser dialog file to select OpenMarkov files.
 * 
 * @author ibermejo
 */
@SuppressWarnings("serial")
public class NetworkFileChooser extends FileChooser {

    /**
     * Creates a new file chooser that starts in the current directory,
     * filtering the files with the file filters.
     */
    public NetworkFileChooser(boolean acceptAllfile) {

       super(acceptAllfile);

        FormatManager formatManager = FormatManager.getInstance();
        HashMap<String, String> writers = formatManager.getWriters();

        for (String item : writers.keySet()) {
            addChoosableFileFilter(new FileFilterAll(writers.get(item), item));
        }
    }

    public NetworkFileChooser() {
        this(false);
    }
}
