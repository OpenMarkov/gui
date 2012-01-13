/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

/**
 * 
 */
package org.openmarkov.core.gui.component;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


/**
 * @author Alberto Ruiz
 * @author jlgozalo
 * @version 1.0 albertoruiz
 * @version 1.1 jlgozalo 15/08/09 - Extracted from NodeValuesTab to this class
 * 				      and set a proper class name
 * 16 Aug 2009
 *
 */
public class DiscretizeLimitsComboBoxRenderer extends JComboBox implements TableCellRenderer {
    /**
	 * default serial ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * default constructor
	 * @param items the set of characters that is able to render
	 */
	public DiscretizeLimitsComboBoxRenderer(String[] items) {
        super(items);
       
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
    		
    	if (isSelected) {
            setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }

        // Select the current value
        setSelectedItem(value);
        return this;
    }
}
