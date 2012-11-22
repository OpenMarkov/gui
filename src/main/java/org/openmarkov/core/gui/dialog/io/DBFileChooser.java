/*
 * Copyright 2012 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.core.gui.dialog.io;

import org.openmarkov.core.io.database.plugin.CaseDatabaseManager;

@SuppressWarnings("serial")
public class DBFileChooser extends FileChooser
{
    protected static CaseDatabaseManager caseDbManager = new CaseDatabaseManager ();

    public DBFileChooser (boolean acceptAllFiles)
    {
        super (acceptAllFiles);
    }
}
