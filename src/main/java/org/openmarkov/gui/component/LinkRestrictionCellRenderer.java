/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.component;

import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.gui.configuration.GUIColors;

import javax.swing.*;

@SuppressWarnings("serial") public class LinkRestrictionCellRenderer extends ValuesTableCellRenderer {
    
    private static final String INCOMPATIBILITY_VALUE = "0";
    private static final String COMPATIBILITY_VALUE = "1";
    
    public LinkRestrictionCellRenderer(int firstEditableRow, boolean[] uncertaintyInColumns, TablePotential potential) {
        super(firstEditableRow, uncertaintyInColumns);
        
    }
    
    @Override protected void setCellColors(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                           int column) {
        super.setCellColors(table, value, isSelected, hasFocus, row, column);
        
        if ((column >= ValuesTable.FIRST_EDITABLE_COLUMN) && firstEditableRow >= 0 && (row >= firstEditableRow)) {
            if (value.toString().equalsIgnoreCase(INCOMPATIBILITY_VALUE)) {
                setBackground(GUIColors.Network.LinkRestriction.INCOMPATIBILITY_COLOR.getColor());
            } else if (value.toString().equalsIgnoreCase(COMPATIBILITY_VALUE)) {
                setBackground(GUIColors.Network.LinkRestriction.COMPATIBILITY_COLOR.getColor());
            }
        }
    }
    
}
