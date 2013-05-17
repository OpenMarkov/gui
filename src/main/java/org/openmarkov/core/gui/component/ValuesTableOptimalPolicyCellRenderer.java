/*
 * Copyright 2012 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.component;

import java.awt.Color;

import javax.swing.JTable;

@SuppressWarnings("serial")
public class ValuesTableOptimalPolicyCellRenderer extends ValuesTableCellRenderer
{

    public ValuesTableOptimalPolicyCellRenderer (int firstEditableRow, boolean[] uncertaintyInColumns)
    {
        super (firstEditableRow, uncertaintyInColumns);
    }

    @Override
    protected void setCellColors (JTable table,
                                  Object value,
                                  boolean isSelected,
                                  boolean hasFocus,
                                  int row,
                                  int column)
    {
        super.setCellColors (table, value, isSelected, hasFocus, row, column);
        Color color = new java.awt.Color (255, 72, 72);
        if ((column >= ValuesTable.FIRST_EDITABLE_COLUMN) && ValuesTable.FIRST_EDITABLE_COLUMN >= 0
            && (row >= firstEditableRow))
        {
            boolean changeColor = true;
            for (int i = firstEditableRow; i < table.getRowCount (); i++)
            {
                if (i != row)
                {
                    if ((double) table.getValueAt (row, column) > (double) table.getValueAt (i,
                                                                                             column))
                    {
                        changeColor = true;
                    }
                    else
                    {
                        changeColor = false;
                        break;
                    }
                }
                else
                {
                    continue;
                }
            }
            if (changeColor)
            {
                setBackground (color);
            }
        }
    }
}
