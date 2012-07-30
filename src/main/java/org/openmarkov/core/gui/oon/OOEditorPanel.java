/*
 * Copyright 2012 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.core.gui.oon;

import org.openmarkov.core.gui.window.edition.EditorPanel;
import org.openmarkov.core.gui.window.edition.NetworkPanel;

@SuppressWarnings("serial")
public class OOEditorPanel extends EditorPanel
{

    public OOEditorPanel (NetworkPanel networkPanel)
    {
        super (networkPanel);
        visualNetwork = new VisualOONetwork (probNet, this);
    }
}
