/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.gui.component;

import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.canonical.ICIPotential;
import org.openmarkov.gui.configuration.GUIColors;

import javax.swing.*;
import java.util.List;

/**
 * This class is used for painting and coloring the ICItable and the headers
 *
 * @author jlgozalo
 * @version 1.0 15/08/2009
 */
@SuppressWarnings("serial") public class ICIValuesTableCellRenderer extends ValuesTableCellRenderer {
	private final List<Variable> variables;
	private final int[] numColumnsParents;
    private final int[] acummulativeColumns;

	public ICIValuesTableCellRenderer(int firstEditableRow, boolean[] uncertaintyInColumns, ICIPotential iciPotential) {
		super(firstEditableRow, uncertaintyInColumns);
		this.variables = iciPotential.getVariables();
		this.numColumnsParents = new int[variables.size()];
		for (int i = 1; i < variables.size(); ++i) {
			numColumnsParents[i - 1] = variables.get(i).getNumStates();
		}
		numColumnsParents[variables.size() - 1] = 1;
		acummulativeColumns = new int[variables.size()];
		acummulativeColumns[0] = numColumnsParents[0];
		for (int i = 1; i < numColumnsParents.length; ++i) {
			acummulativeColumns[i] = numColumnsParents[i] + acummulativeColumns[i - 1];
		}
	}

	@Override protected void setCellColors(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
			int column) {
		if ((column < ValuesTable.FIRST_EDITABLE_COLUMN) & (row < firstEditableRow)) { // PARENTS CELLS
			// set alternate colors
			// column = 0 row = 0 o 1
            setBackground(GUIColors.Tables.HEADER_BACKGROUND.getColor());
            setForeground(GUIColors.Tables.FROZEN_CELL_FOREGROUND.getColor());
		}
		// NEW
		if ((column < ValuesTable.FIRST_EDITABLE_COLUMN) & (row >= firstEditableRow)) { // NODE STATES CELLS
            setBackground(GUIColors.Tables.FROZEN_CELL_BACKGROUND.getColor());
            setForeground(GUIColors.Tables.FROZEN_CELL_FOREGROUND.getColor());
		}
		if ((column >= ValuesTable.FIRST_EDITABLE_COLUMN) & (row < firstEditableRow)) { // headers cells
			if (row == 0) {// FIRST ROW
				for (int i = 0; i < acummulativeColumns.length; i++) {
					if (i == 0) {
						if (column <= acummulativeColumns[i]) {
                            setBackground(GUIColors.Tables.HEADER_BACKGROUND.getColor());
                            setForeground(GUIColors.Tables.HEADER_FOREGROUND_COLORS.getFirst().getColor());
							break;
						}
					} else if (acummulativeColumns[i - 1] < column && column <= acummulativeColumns[i]) {
                        setBackground(GUIColors.Tables.HEADER_BACKGROUND.getColor());
                        setForeground(GUIColors.Tables.HEADER_FOREGROUND_COLORS.get(i % GUIColors.Tables.HEADER_FOREGROUND_COLORS.size())
                                                                               .getColor());
						break;
					}
				}
			}
			if (row == 1) {// SECOND ROW
				// setBackground( new Color(220,220,220));
                setBackground(GUIColors.Tables.HEADER_BACKGROUND.getColor());
                setForeground(GUIColors.Tables.HEADER_FOREGROUND_COLORS.get(column % GUIColors.Tables.HEADER_FOREGROUND_COLORS.size())
                                                                       .getColor());
			}
		}
		if ((column >= ValuesTable.FIRST_EDITABLE_COLUMN) && firstEditableRow >= 0 && (row >= firstEditableRow)) {
            setBackground(GUIColors.Tables.EDITABLE_CELL_BACKGROUND.getColor());
            setForeground(GUIColors.Tables.EDITABLE_CELL_FOREGROUND.getColor());
		}
	}
}
