/*
 * Copyright 2013 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.costeffectiveness;

import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

@SuppressWarnings("serial")
public class CostEffectivenessSummaryPane extends JScrollPane
{
    public CostEffectivenessSummaryPane (List<Intervention> interventions)
    {
    	TableModel summaryTableModel = new DefaultTableModel(3, interventions.size() +1);
        JTable summaryTable = new JTable(summaryTableModel);
        add (summaryTable);
    }
}
