/*
 * Copyright 2013 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.costeffectiveness;

import javax.swing.JScrollPane;

import org.openmarkov.core.gui.component.ValuesTable;
import org.openmarkov.core.gui.component.ValuesTableModel;

@SuppressWarnings("serial")
public class CostEffectivenessSummaryPane extends JScrollPane
{
    public CostEffectivenessSummaryPane ()
    {
        ValuesTableModel tableModel = new ValuesTableModel ();
        ValuesTable valuesTable = new ValuesTable (null, tableModel, false);
        add (valuesTable);
    }
}
