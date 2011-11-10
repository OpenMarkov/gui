/**
 * 
 */
package org.openmarkov.core.gui.component;


import javax.swing.table.AbstractTableModel;


/**
 * NodePotentialFixedFirstColumnTableModel defines the model
 * to be used to display the first column of the table with no horizontal scroll
 * @author jlgozalo
 * @version 1.0 28 Ene 2010
 */
public class NodePotentialFixedFirstColumnTableModel extends AbstractTableModel {

	/**
	 * serialVersionUID 
	 */
	private static final long serialVersionUID = -6559329014620464207L;
	/**
	 * data for the model
	 */
	Object[][] data = null;
	/**
	 * columns for the model
	 */
	String[] columns = null;
	

	/**
	 * @param data - data to be set in the model
	 * @param columns - column names for the model
	 */
	public NodePotentialFixedFirstColumnTableModel(Object[][] data, String[] columns) {

		this.data = data;
		this.columns = columns;
	}

	public boolean isCellEditable(int row, int col) {
		return false;
	}

    public int getColumnCount() {
        return 1;
      }

      public int getRowCount() {
        return data.length;
      }

      public String getColumnName(int col) {
        return columns[col];
      }

      public Object getValueAt(int row, int col) {
        return data[row][col];
      }
}
