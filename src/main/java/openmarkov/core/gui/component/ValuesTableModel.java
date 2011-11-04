/**
 * 
 */
package openmarkov.core.gui.component;


import javax.swing.table.DefaultTableModel;


/**
 * NodePotentialTableModel defines the basic behaviour of the Table Model
 * 
 * @author jlgozalo
 * @version 1.0 7 Jul 2009
 */
public class ValuesTableModel extends DefaultTableModel {

	/**
	 * calculated serial ID
	 */
	private static final long serialVersionUID = 7010730473355625101L;

	/**
	 * first editable row. By default, all rows are editable (first 0)
	 */
	int firstEditableRow = 0;

	/**
	 * constructor
	 */
	public ValuesTableModel() {

		super();
	}

	/**
	 * constructor
	 */
	public ValuesTableModel(Object[][] data, String[] columns,
									int firstEditableRow) {

		super(data, columns);
		this.firstEditableRow = firstEditableRow;
	}

    /**
     * This method determines the default renderer/editor for each cell.
     * First column is a String class type and the others are double type.
     */
	public Class<?> getColumnClass(int c) {

		Double doubleExample = 0.0;
		String stringExample = "";

		if (c == 0) {
			return stringExample.getClass();
		} else {
			return doubleExample.getClass();
		}
	}

	/**
	 * This method determines if the cell is editable or not, considering :
	 * <li> all rows in the header are not editable</li>
	 * <li> column with the name of the parents and the values are not editable</li>
	 */
	public boolean isCellEditable(int row, int col) {

		if (row < firstEditableRow) {
			return false;
		}
		if (col < ValuesTable.FIRST_EDITABLE_COLUMN) { 
			// states names are not editable
			return false;
		}

		return true; // all other cells are editable
	}

	/**
	 * @return the firstEditableRow
	 */
	public int getFirstEditableRow() {

		return firstEditableRow;
	}

	/**
	 * @param firstEditableRow
	 *            the firstEditableRow to set
	 */
	public void setFirstEditableRow(int firstEditableRow) {

		this.firstEditableRow = firstEditableRow;
	}

}
