/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.dialog.common;

import java.awt.Dimension;

import javax.swing.JPanel;

public class PanelResizeEvent {
    private JPanel source;
    private Dimension newDimension;
    
    public PanelResizeEvent(JPanel source, Dimension newDimension) {
        super();
        this.source = source;
        this.newDimension = newDimension;
    }

    public JPanel getSource() {
        return source;
    }

    public Dimension getNewDimension() {
        return newDimension;
    }
    
}
