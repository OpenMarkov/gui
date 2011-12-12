/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.component;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


/**
 * Class to manage Discretize Render Table in columns "()" and "[]"
 * 
 * @author Alberto Ruiz
 * @version 1.0
 */
public class MyComboBoxRenderer extends JComboBox implements
				TableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * constructor
	 * @param items - the set of elements to be displayed by the combo box 
	 */
	public MyComboBoxRenderer(String[] items) {
		 
		super( items );

	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	public Component getTableCellRendererComponent(JTable table,
													Object value,
													boolean isSelected,
													boolean hasFocus,
													int row, int column) {

		if (isSelected) {
			setForeground( table.getSelectionForeground() );
			super.setBackground( table.getSelectionBackground() );
		} else {
			setForeground( table.getForeground() );
			setBackground( table.getBackground() );
		}

		// Select the current value
		setSelectedItem( value );
		return this;
	}
}
