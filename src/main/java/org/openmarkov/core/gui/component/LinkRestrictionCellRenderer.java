package org.openmarkov.core.gui.component;

import java.awt.Color;

import javax.swing.JTable;

import org.openmarkov.core.model.network.potential.TablePotential;

@SuppressWarnings("serial")
public class LinkRestrictionCellRenderer extends ValuesTableCellRenderer {

	private static Color INCOMPATIBILITY_COLOR = new Color(225, 100, 100);
	private final String INCOMPATIBILITY_VALUE = "0";
	
	public LinkRestrictionCellRenderer(int firstEditableRow,
			boolean[] editableColumns, TablePotential potential) {
		super(firstEditableRow, editableColumns);

	}

	@Override
	protected void setCellColors(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		super.setCellColors(table, value, isSelected, hasFocus, row, column);

		if (value.toString().equalsIgnoreCase(INCOMPATIBILITY_VALUE)) {
			setBackground(INCOMPATIBILITY_COLOR);
		}
	}

}
