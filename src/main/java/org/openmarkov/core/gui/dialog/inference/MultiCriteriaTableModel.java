package org.openmarkov.core.gui.dialog.inference;

import org.openmarkov.core.gui.dialog.inference.InferenceOptionsDialog;

import javax.swing.table.DefaultTableModel;

/**
 * Model for multicriteria table
 * 
 * @author Jorge
 *
 */
public class MultiCriteriaTableModel extends DefaultTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MultiCriteriaTableModel() {
		super();
	}

	@Override
	public boolean isCellEditable(int row, int column) {

		// The user wouldn't be able to edit the criteria
		if (column == InferenceOptionsDialog.CRITERION_COLUMN || row == 0) {
			return false;
		} else {
			return true;
		}

	}

}
