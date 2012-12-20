package org.openmarkov.core.gui.component;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

public class ValuesTableOptimalPolicyCellRenderer extends
ValuesTableCellRenderer {
	
	private static Color OPTIMAL_POLICY_COLOR = new Color(255, 122, 122);

	private int firstEditableRow;
	public ValuesTableOptimalPolicyCellRenderer(int firstEditableRow,
			boolean[] editableColumns) {
		super(firstEditableRow, editableColumns);
		this.firstEditableRow = firstEditableRow;
	}
	@Override
	protected void setCellColors(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		super.setCellColors(table, value, isSelected, hasFocus, row, column);
		Color color = new java.awt.Color(255, 72, 72);
	//	Color background = table.getCellRenderer(row, column).getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column).getBackground();
		
		if ((column >= ValuesTable.FIRST_EDITABLE_COLUMN)
				&& ValuesTable.FIRST_EDITABLE_COLUMN >= 0 && (row >= firstEditableRow)) {
			
			boolean changeColor = true;
            for (int  i = firstEditableRow; i < table.getRowCount() ; i++ ) {
            	if (i != row) {
            		if ((double)table.getValueAt(row, column) > (double)table.getValueAt(i, column) ) {
            			changeColor = true;
            		} /*else if ((double)table.getValueAt(row, column) == (double)table.getValueAt(i, column) &&
            				!table.getCellRenderer(i, column).getTableCellRendererComponent(table, value, isSelected, hasFocus, i, column).getBackground().equals(color)) {
            			changeColor = true;
            		}*/
            		else {
            			changeColor = false;
            			break;
            		}
            	} else {
            		continue;
            	}
            }
            
            if (changeColor) {
            	setBackground(color);
            }
     	
    }
   
	}

}
