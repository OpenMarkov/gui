package org.openmarkov.core.gui.component;

import java.awt.Color;

import javax.swing.JTable;

@SuppressWarnings("serial")
public class ValuesTableWithLinkRestrictionCellRenderer extends
		ValuesTableCellRenderer {

	private static Color INCOMPATIBILITY_COLOR = new Color(225, 100, 100);

	public ValuesTableWithLinkRestrictionCellRenderer(int firstEditableRow,
			boolean[] editableColumns) {
		super(firstEditableRow, editableColumns);

	}

	@Override
	protected void setCellColors(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		super.setCellColors(table, value, isSelected, hasFocus, row, column);
		if ((column >= ValuesTable.FIRST_EDITABLE_COLUMN)
				&& firstEditableRow >= 0 && (row >= firstEditableRow)) {
			if (!table.isCellEditable(row, column)) {
				setBackground(INCOMPATIBILITY_COLOR);
			}
		}
	}

}
