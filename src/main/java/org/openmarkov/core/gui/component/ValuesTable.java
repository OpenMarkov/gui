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


import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.ToolTipManager;
import javax.swing.event.UndoableEditEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.undo.UndoableEdit;

import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DeterministicValueNotAllowedException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.ProbabilisticValueNotAllowedException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.action.TablePotentialValueEdit;
import org.openmarkov.core.gui.dialog.common.KeyTable;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.action.PNUndoableEditEvent;
import org.openmarkov.core.action.PNUndoableEditListener;
import org.openmarkov.core.action.UncertainValuesEdit;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;




/**
 * This table implementation is responsible for the graphical and data model
 * manipulation of the Node Potentials (either in a general family or in a
 * canonical family potential). This table also shows the data in several ways,
 * depending upon the type of user selection:
 * <ul>
 * <Li>Probabilities or states values</li>
 * <li>Probabilistic or Deterministic values allowed</li>
 * <li>All parameters or Only independent parameters</li>
 * <li>TPC or canonical parameters(for the Canonical families)</li>
 * <li>Net or Compound values (for the Canonical families)</li>
 * 
 * @author jlgozalo
 * @author mpalacios
 * @version 1.0 7 Jul 2009
 * @version 1.1 15/Nov 2009 - sets the attributes for behaviour: deterministic
 *          (yes/no)
 * @version 2.0 20 Jan 2010 - sets all the behaviours and use of single data
 *          model
 */
public class ValuesTable extends KeyTable implements PNUndoableEditListener {

	/**
	 * default serial ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * first editable Column
	 */
	public static final int FIRST_EDITABLE_COLUMN = 1;
	/**
	 * table model
	 */
	protected ValuesTableModel tableModel;

	/**
	 * boolean data model (to know if a value has been changed)
	 */
	boolean[][] dataModified = null;

	/**
	 * Table Row Sorter/Filter
	 */
	protected TableRowSorter<ValuesTableModel> tableRowSorter = null;

	/**
	 * variable that use the instance of this object (table)
	 */
	protected Variable variable = null;

	/**
	 * type of node for this variable
	 */
	protected NodeType nodeType = null;

	/**
	 * number of decimals positions to be used for calculations and display
	 */
	protected static int decimalPositions = 2; // by default;

	/**
	 * last editable row. By default, it is zero until runtime initialization
	 */
	protected int lastEditableRow = 0;

	/**
	 * define if the model is deterministic or probabilistic. By default, the
	 * model is probabilistic (false)
	 */
	protected boolean deterministic = false;

	/**
	 * define if the table is using General or Canonical Potentials
	 * <ul>
	 * <li> if index = 0 then Using General Potential </li>
	 * <li> if index = 1,2,3 then Using Canonical Potential (family OR)</li>
	 * <li> if index = 4,5,6 then Using Canonical Potential (famili AND)</li>
	 */
	protected int indexPotential = 0; // General Potential by default

	/**
	 * define if the table shows all parameters or only independent parameters
	 */
	protected boolean showingAllParameters = false;

	/**
	 * define if the table shows probabilities values or state name
	 */
	protected boolean showingProbabilitiesValues = false;

	/**
	 * define if the table shows TPC values or canonical values
	 */
	protected boolean showingTPCvalues = false;

	/**
	 * define if the table shows Optimal Decision
	 */
	protected boolean showingOptimal = false;

	/**
	 * Dialog string resource.
	 */
	private StringResource dialogStringResource;

	protected ProbNode probNode;
	/**
	 * Define the last column of the table that was modified
	 */
	private int lastCol = -1;
	
	/**
	 * Define the priority list when potential values are edited
	 */
	
	protected LinkedList<Integer> priorityList = new LinkedList<Integer> ();

	/**
	 * default constructor
	 */
	public ValuesTable() {
		//TODO verify the use of this class in NodeUtilityTable
		//call to constructor is null for probNode parameter
		this( null, new ValuesTableModel(), false );
	}

	
	/**
	 * default constructor with parameters
	 */
	public ValuesTable(ProbNode probNode, ValuesTableModel tableModel,
								final boolean modifiable) {

		super( tableModel, modifiable, true, true );
		//probNode.getProbNet().getPNESupport().removeUndoableEditListener(this);
		probNode.getProbNet().getPNESupport().addUndoableEditListener(this);
		this.tableModel = tableModel;
		this.probNode = probNode;
		dialogStringResource =
			StringResourceLoader.getUniqueInstance().getBundleDialogs();
		if (modifiable) {
			int numRowsModel =tableModel.getRowCount();
			int numColumsModel = tableModel.getColumnCount();
			this.dataModified =
				new boolean[ numRowsModel ][ numColumsModel ];
			initializeDataModified( false );
		}

	}

	/**
	 * @return the dialogStringResource
	 */
	protected StringResource getDialogStringResource() {

		return dialogStringResource;
	}

	/**
	 * @return the dataModified
	 */
	public boolean[][] getDataModified() {

		if (dataModified == null) {
			dataModified =
				new boolean[ getTableModel().getRowCount() ][ getTableModel()
					.getColumnCount() ];//adding one column for leak potential
		}
		
		/*new boolean[ getTableModel().getRowCount() ][ getTableModel()
		                          					.getColumnCount() +1 ];//le acabo de quitar ese 1*/
		return dataModified;
	}

	/**
	 * @param dataModified
	 *            the dataModified to set
	 */
	public void setDataModified(boolean[][] dataModified) {

		this.dataModified = dataModified;
	}

	/**
	 * Initialize the boolean status for each cell in the table
	 * 
	 * @param isModified -
	 *            initial value for the data in the table
	 */
	public void initializeDataModified(boolean isModified) {

		if (tableModel != null) {
			if (dataModified == null) {
				getDataModified();
			}
			for (int i = 0; i < tableModel.getRowCount(); i++) {
				for (int j = 0; j < tableModel.getColumnCount(); j++) {
					dataModified[i][j] = isModified;
				}
			}
		}

	}

	/**
	 * internal method to count how many rows are still not modified in a column
	 * 
	 * @param column -
	 *            the column
	 * @param firstEditableRow -
	 *            first editable row of data
	 * @param lastEditableRow -
	 *            last editable row of data
	 * @return number of rows that are still not modified
	 */
	private int countRowsNotModified(int column, int firstEditableRow,
										int lastEditableRow) {

		int value = 0;
		for (int i = firstEditableRow; i < lastEditableRow; i++) {
			if (!getDataModified()[i][column]) {
				value++;
			}
		}
		return value;
	}

	/**
	 * default configuration for this table
	 */
	@Override
	protected void defaultConfiguration() {
		boolean [] aux = { false, false}; 
		super.defaultConfiguration();
		setFirstColumnHidden( false ); // key prefix column is hidden
		setShowColumnHeader( false ); // no column header here
		setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		setRowSelectionAllowed(false);
		setColumnSelectionAllowed(false);
		setGridColor(Color.DARK_GRAY);
		setDefaultRenderer(
			Double.class, new ValuesTableCellRenderer( 0, aux ) );
		setDefaultRenderer(
			String.class, new ValuesTableCellRenderer( 0, aux) );

		// next two lines is a cool trick to enhance table performance
		ToolTipManager.sharedInstance().unregisterComponent( this );
		ToolTipManager.sharedInstance().unregisterComponent( getTableHeader() );

	}

	/**
	 * reset the Model in use
	 */
	public void resetModel() {

		tableModel = null;
		dataModified = null;
	}

	/**
	 * get the tableModel attribute
	 */
	public ValuesTableModel getTableModel() {

		return this.tableModel;
	}

	/**
	 * Sets the data model for this table to newModel and registers with it for
	 * listener notifications from the new data model.
	 * 
	 * @param newDataModel
	 *            the new data source for this table.
	 * @throws IllegalArgumentException
	 *             if newModel is null.
	 */
	public void setModel(ValuesTableModel newDataModel)
					throws IllegalArgumentException {

		super.setModel( newDataModel );
		this.tableModel = newDataModel;
		tableRowSorter =
			new TableRowSorter<ValuesTableModel>(
				((ValuesTableModel) getModel()) );
		//not display the last row where the cells has states and not values
		//and it is only required when displaying states values
		/*tableRowSorter.setRowFilter(
			RowFilter.notFilter( 
				RowFilter.regexFilter( "Edad\\[\\d\\]", 0 ) ));
		this.setRowSorter( tableRowSorter);*/ 
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JTable#getCellRenderer(int, int)
	 */
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {

		return super.getCellRenderer( row, column );
	}

	/* (non-Javadoc)
	 * @see javax.swing.JTable#changeSelection(int, int, boolean, boolean)
	 */
	@Override
	public void changeSelection(int rowIndex, int columnIndex, boolean toggle,
								boolean extend) {

		if (columnIndex < FIRST_EDITABLE_COLUMN) { // not selectable
			super.changeSelection( rowIndex, columnIndex + 1, toggle, extend );
		} else {
			super.changeSelection( rowIndex, columnIndex, toggle, extend );
		}
	}

	/**
	 * Cancels the editing in any cell of the table, avoiding its new value is
	 * recorded.
	 */
	public void cancelCellEditing() {

		TableCellEditor actualEditor = getCellEditor();

		if (actualEditor != null) {
			actualEditor.cancelCellEditing();
		}
	}

	/**
	 * Stops the editing in any cell of the table, recording the new value.
	 */
	public void stopCellEditing() {

		TableCellEditor actualEditor = getCellEditor();

		if (actualEditor != null) {
			actualEditor.stopCellEditing();
		}
	}

	/**
	 * check the value to modify in the table and sets
	 */
	public void setValueAt(Object newValue, int row, int col) {
	 	Object oldValue = getValueAt( row, col );
	 	//TODO Verificar si la ubicación del siguiente código es 
		//adecuada
		if (!oldValue.equals( newValue )) {
			if (nodeType == NodeType.CHANCE || nodeType == NodeType.DECISION ) {
				if (deterministic) {
					checkDeterministic( oldValue, newValue, row, col );
				} else {
					
					if (lastCol != col){
						priorityList.clear();
						lastCol = col;
					}
					
					TablePotentialValueEdit nodePotentialEdit = new TablePotentialValueEdit(
						probNode, (Double)newValue, row, col, priorityList);
					try {
						probNode.getProbNet().getPNESupport().announceEdit(
								nodePotentialEdit);
						probNode.getProbNet().getPNESupport().doEdit(
							nodePotentialEdit);
						
					} catch (ConstraintViolationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (CanNotDoEditException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (DoEditException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NotEnoughMemoryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NonProjectablePotentialException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (WrongCriterionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				//checkProbabilistic( oldValue, newValue, row, col );
				}
			} else if (nodeType == NodeType.UTILITY ) {
				TablePotentialValueEdit nodePotentialEdit = new TablePotentialValueEdit(
						probNode, (Double)newValue, row, col, priorityList);
					try {
						probNode.getProbNet().getPNESupport().announceEdit(
								nodePotentialEdit);
						probNode.getProbNet().getPNESupport().doEdit(
							nodePotentialEdit);
						
					} catch (ConstraintViolationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (CanNotDoEditException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (DoEditException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NotEnoughMemoryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NonProjectablePotentialException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (WrongCriterionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				checkUtilityValue (oldValue, newValue, row, col) ;
			}
		} // else it is not required to update values
	}

	/**
	 * Check if the value is valid on an Utility node.
	 * <p>
	 * @param oldValue -
	 *            previous value in the cell
	 * @param newValue -
	 *            new value to validate
	 * @param row -
	 *            the row for the cell
	 * @param col -
	 *            the column for the cell
	 */
	public void checkUtilityValue (Object oldValue, Object newValue, int row,
									int col) {

		Double value = 0.0;
		try {
			if (newValue instanceof String) {
				value = Double.parseDouble( (String) newValue );
			} else if (newValue instanceof Double) {
				value = (Double) newValue;
			}
			super.getModel().setValueAt(value,row,col);
		} catch (Exception ex) {
			showNodePotentialTableErrorMsg( "Double conversion error" );
			super.getModel().setValueAt( oldValue, row, col );
		}
	}
	/**
	 * Check if the value is valid on a deterministic model.
	 * <p>
	 * In this model, the summa of the values of the column is 1 and only one of
	 * the values is 1 and all others are zeros.
	 * 
	 * @param oldValue -
	 *            previous value in the cell
	 * @param newValue -
	 *            new value to validate
	 * @param row -
	 *            the row for the cell
	 * @param col -
	 *            the column for the cell
	 */
	public void checkDeterministic(Object oldValue, Object newValue, int row,
									int col) {

		Double value = 0.0;
		try {
			if (newValue instanceof String) {
				value = Double.parseDouble( (String) newValue );
			} else if (newValue instanceof Double) {
				value = (Double) newValue;
			}
			checkZeroOrOneValues( value );
			assignNewDeterministicValuesToColumn( oldValue, value, row, col );
		} catch (DeterministicValueNotAllowedException ex) {
			showNodePotentialTableErrorMsg( "NodePotentialTable.Msg.DeterministicValueNotAllowed" );
			super.getModel().setValueAt( oldValue, row, col );
		}
	}

	/**
	 * Check if the value is valid on a probabilistic model
	 * <p>
	 * In this model, the sum of the values of the column is 1 but there are
	 * no restrictions to the individual values
	 * 
	 * @param oldValue -
	 *            previous value in the cell
	 * @param newValue -
	 *            new value to validate
	 * @param row -
	 *            the row for the cell
	 * @param col -
	 *            the column for the cell
	 */
	public void checkProbabilistic(Object oldValue, Object newValue, int row,
									int col) {

		Double value = 0.0;
		try {
			if (newValue instanceof String) {
				value = Double.parseDouble( (String) newValue );
			} else if (newValue instanceof Double) {
				value = (Double) newValue;
			}
			checkValueBetweenZeroAndOneValues( value );
			int rowsToModified = existsRowsToModify( col );
			if (rowsToModified > 0) {
				assignNewProbabilisticValuesToColumn(
					oldValue, value, row, col, rowsToModified );
			}
		} catch (ProbabilisticValueNotAllowedException ex) {
			showNodePotentialTableErrorMsg( "NodePotentialTable.Msg.ProbabilisticValueNotAllowed" );
			super.getModel().setValueAt( oldValue, row, col );
		}
	}

	/**
	 * show a error window message to the user with a specific msg
	 * 
	 * @param msg -
	 *            the error message to show to user
	 */
	private void showNodePotentialTableErrorMsg(String msg) {

		JOptionPane.showMessageDialog( this, dialogStringResource
			.getString( msg + ".Text" ), dialogStringResource.getString( msg
			+ ".Title" ), JOptionPane.ERROR_MESSAGE );

	}

	/**
	 * Check if a value is equal to 0 or 1 values
	 * 
	 * @param value -
	 *            the value to check
	 * @return true if value is compliance with the condition
	 */
	private boolean checkZeroOrOneValues(double value)
					throws DeterministicValueNotAllowedException {

		boolean result = false;
		if ((value == 1.0) || (value == 0.0)) {
			result = true;
		} else {
			throw new DeterministicValueNotAllowedException( "" );
		}
		return result;
	}

	/**
	 * Check if a value is between 0 and 1 values
	 * 
	 * @param value -
	 *            the value to check
	 * @return true if value is compliance with the condition
	 */
	private boolean checkValueBetweenZeroAndOneValues(double value)
					throws ProbabilisticValueNotAllowedException {

		boolean result = false;
		if ((value >= 0.0) & (value <= 1.0)) {
			result = true;
		} else {
			throw new ProbabilisticValueNotAllowedException( "" );
		}
		return result;
	}

	/**
	 * In a particular column, this method will tell if there is still values
	 * which have not been modified yet by the user
	 * @param column - the column to know status
	 * @return number of rows pending to be modified
	 */
	private int existsRowsToModify(int column) {

		int initialRow =
			((ValuesTableModel) getModel()).getFirstEditableRow();
		int numOfRowsToModify =
			countRowsNotModified( column, initialRow, lastEditableRow );
		if (numOfRowsToModify == 0) {
			if (askToResetDataModified()) {
				numOfRowsToModify = lastEditableRow - initialRow;
			}
		}
		return numOfRowsToModify;
	}

	/**
	 * assign new deterministic values to column with the following condition:
	 * <p>
	 * <ul>
	 * <li>if the new value is 1.0, then previous 1.0 cell is set to 0.0</li>
	 * <li>if the new value is 0.0, then previous cell is trying to be set to
	 * 1.0 except when it is in first editable row. In this case, next cell is
	 * set to 1.0</li>
	 * </ul>
	 * 
	 * @param oldValue -
	 *            previous value of the cell that is being edited
	 * @param value -
	 *            new value of the cell that is being edited
	 * @param row -
	 *            the row of the cell that is being edited
	 * @param col -
	 *            the column where the values are stored for the edited cell
	 */
	private void assignNewDeterministicValuesToColumn(Object oldValue,
														Object value, int row,
														int col) {

		int initialRow =
			((ValuesTableModel) getModel()).getFirstEditableRow();
		if (((Double) value) == 1.0) {
			for (int i = initialRow; i < lastEditableRow; i++) {
				if (((Double) super.getModel().getValueAt( i, col )) == 1.0) {
					super.getModel().setValueAt( 0.0, i, col );
					super.getModel().setValueAt( value, row, col );
					String stateValue = (String)super.getModel().getValueAt( row,0 );
					super.getModel().setValueAt( stateValue, lastEditableRow, col );
					break;
				}
			}
		} else if (((Double) value) == 0.0) {
			String stateValue = "";
			if (row == initialRow) {
				if (row == lastEditableRow) {
					super.getModel().setValueAt( 1.0, row, col );
					stateValue = (String)super.getModel().getValueAt( row, 0 );
				} else {
					super.getModel().setValueAt( 1.0, row + 1, col );
					stateValue = (String)super.getModel().getValueAt( row+1, 0 );
				}
			} else {
				super.getModel().setValueAt( 1.0, row - 1, col );
				stateValue = (String)super.getModel().getValueAt( row-1, 0 );

			}
			super.getModel().setValueAt( stateValue, lastEditableRow + 1 ,col );
			super.getModel().setValueAt( 0.0, row, col );
		}
	}

	/**
	 * AssignNewProbabilisticValuesToColumn(...) assign new probabilistic values
	 * to column by splitting the difference between the old and new value of the
	 * edited cell between the other not modified cells of the column, and
	 * ensuring that addition of all values in the column is equals to 1.
	 * <p>
	 * And setting the value of the edited cell to the new value.
	 * 
	 * @param oldValue -
	 *            previous value of the cell that is being edited
	 * @param value -
	 *            new value of the cell that is being edited
	 * @param row -
	 *            the row of the cell that is being edited
	 * @param col -
	 *            the column where the values are stored for the edited cell
	 */
	private void assignNewProbabilisticValuesToColumn(Object oldValue,
														Object value, int row,
														int col,
														int numRowsToModify) {

		int initialRow =
			((ValuesTableModel) getModel()).getFirstEditableRow();
		double delta = 0.0;
		double summa = 0.0;
		double newSumma = 0.0;
		double auxValue = 0.0;
		// option 1. Delta equal distribution but only in not modified nodes
		for (int i = initialRow; i < lastEditableRow; i++) {
			if (dataModified[i][col]) { // summarize all previous edited values
				summa =
					roundingDouble( summa + (Double) super.getModel().getValueAt(
							i, col ) );
			}
		}
		if (dataModified[row][col]) {
			summa = roundingDouble( summa - (Double) oldValue );
			numRowsToModify++;
		}
		newSumma = roundingDouble( summa + (Double) value );
		if (newSumma > 1.0) {
			showNodePotentialTableErrorMsg( "NodePotentialTable.Msg.SummaOfProbabilitiesHigherThanOne" );
			super.getModel().setValueAt( oldValue, row, col );
		} else { // probabilistic condition is valid
			super.getModel().setValueAt( value, row, col );
			summa = newSumma;
			dataModified[row][col] = true;
			numRowsToModify--;
			if (numRowsToModify != 0) {
				delta =
					((((Double) value).doubleValue() - ((Double) oldValue)
						.doubleValue()) / numRowsToModify);
				delta = roundingDouble( delta ); // only 2 decimals positions

				for (int i = initialRow; i < lastEditableRow; i++) {
					if (i != row && !dataModified[i][col]) {
						// change and summarize
						auxValue = (Double) super.getModel().getValueAt( i, col );
						auxValue = roundingDouble( auxValue - delta );
						newSumma = roundingDouble( summa + auxValue );
						if (newSumma > 1.0) {
							// ensure summa is not greater than 1
							auxValue =
								roundingDouble( auxValue - (newSumma - 1.0) );
						}
						summa = roundingDouble( summa + auxValue );
						super.getModel().setValueAt( auxValue, i, col );
					}
				}
			}
		}

	}

	/**
	 * @return the variable
	 */
	public Variable getVariable() {

		return probNode.getVariable();
	}

	/**
	 * @param variable
	 *            the variable to set
	 */
	public void setVariable(Variable variable) {

		this.variable = variable;
	}

	
	/**
	 * @return the nodeType
	 */
	public NodeType getNodeType() {
	
		return nodeType;
	}

	
	/**
	 * @param nodeType the nodeType to set
	 */
	public void setNodeType(NodeType nodeType) {
	
		this.nodeType = nodeType;
	}

	/**
	 * @return the lastEditableRow
	 */
	public int getLastEditableRow() {

		return lastEditableRow;
	}

	/**
	 * @param lastEditableRow
	 *            the lastEditableRow to set
	 */
	public void setLastEditableRow(int lastEditableRow) {

		this.lastEditableRow = lastEditableRow;
	}

	/**
	 * @return the usingGeneralPotential
	 */
	public boolean isUsingGeneralPotential() {

		return (indexPotential == 0 ? true : false);
	}

	/**
	 * @param usingGeneralPotential
	 *            the usingGeneralPotential to set
	 */
	public void setUsingGeneralPotential(int indexPotential) {

		this.indexPotential = indexPotential;
		if (isUsingGeneralPotential()) {//if indexPotential == 0
			if ("leak".equals( getValueAt( 0, getColumnCount() - 1 ) )) {
				// previous model=Optimal
				// remove the leakColumn
				int index = getColumnCount() - 1;
				TableColumn column = getColumnModel().getColumn( index );
				getColumnModel().removeColumn( column );
			} else {
				// do nothing
			}

		} else { // canonical models
			if ("leak".equals( getValueAt( 0, getColumnCount() - 1 ) )) {
				// previous model=Optimal
				// do nothing
			} else {
				int rowCount = getModel().getRowCount();
				int firstRow =
					((ValuesTableModel) getModel())
						.getFirstEditableRow();
				Object[] values = new Object[ rowCount ];
				values[0] = "leak";
				for (int i = 1; i < firstRow; i++) {
					values[i] = "-";
				}
				for (int i = firstRow; i < rowCount; i++) {
					values[i] = 0.0;
				}
				if (1 <= indexPotential & indexPotential <= 3) {
					// OR family
					values[rowCount - 1] = 1.0;
				} else if (4 <= indexPotential & indexPotential <= 6) {
					// AND family
					values[firstRow] = 1.0;
				} else {
					// error ????????
				}
				betterAddColumn( "leak", values );
			}

		}
	}

	/**
	 * @return the deterministic
	 */
	public boolean isDeterministic() {

		return deterministic;
	}

	/**
	 * Define the deterministic behaviour of the table
	 * 
	 * @param deterministic
	 *            the table behaviour as deterministic(true) or not
	 */
	public void setDeterministic(boolean deterministic) {

		this.deterministic = deterministic;

		if (isDeterministic()) {
			for (int k = FIRST_EDITABLE_COLUMN; k < getModel().getColumnCount(); k++) {
				double maxValue = -1;
				int rowPosition = -1;
				for (int i =
					((ValuesTableModel) getModel())
						.getFirstEditableRow(); i < getLastEditableRow(); i++) {
					if (((Double) ((ValuesTableModel) getModel())
						.getValueAt( i, k )) > maxValue) {
						maxValue =
							(Double) ((ValuesTableModel) getModel())
								.getValueAt( i, k );
						rowPosition = i;
					}
				}
				for (int i =
					((ValuesTableModel) getModel())
						.getFirstEditableRow(); i < getLastEditableRow(); i++) {
					if (i == rowPosition) {
						((ValuesTableModel) getModel()).setValueAt(
							1.0, i, k );
					} else {
						((ValuesTableModel) getModel()).setValueAt(
							0.0, i, k );
					}
				}

			}

			// and now, by default, show in the table, the name of the state
			// but not the values of the cells
			setShowingProbabilitiesValues( false );

		} else {
			setShowingProbabilitiesValues( true );
		}
		initializeDataModified( false );
	}

	/**
	 * @return the showingAllParameters
	 */
	public boolean isShowingAllParameters() {

		return showingAllParameters;
	}

	/**
	 * Method to show/hide rows based on the showingAllParameters attribute
	 * using a RowFilter mechanism.
	 * <ul>
	 * <li>If true, the table is shown completely with probabilities values
	 * which means that there is no active row filter</li>
	 * <li>If not, the row filter is set to show all rows except the one that
	 * has the state name equals to the last state name.</li>
	 * </ul>
	 * 
	 * @param showingAllParameters
	 *            if true, show all; if false, show only independent parameters
	 */
	public void setShowingAllParameters(boolean showingAllParameters) {

		this.showingAllParameters = showingAllParameters;
		tableRowSorter =
			new TableRowSorter<ValuesTableModel>(
				((ValuesTableModel) getModel()) );
		if (isShowingAllParameters()) {
			if ((getVariable()!=null)&&(getVariable().getName()!=null) &&
					probNode.getNodeType() !=  NodeType.UTILITY) {
				String name = getVariable().getName();
				if (getVariable().getTimeSlice() != Integer.MIN_VALUE){
					name = getRegExp(name);
				}
				tableRowSorter.setRowFilter(
				RowFilter.notFilter( 
					RowFilter.regexFilter( name, 0 ) ));
			this.setRowSorter( tableRowSorter); 
			} else {
				this.setRowSorter( null );
			}
		} else {
			int lastRow = getModel().getRowCount() - 1 - 1;
			lastRow = (lastRow <0 ? 0 : lastRow );
			LinkedList<RowFilter<Object,Object>> list = 
				new LinkedList<RowFilter<Object,Object>> ();
			list.add( RowFilter.notFilter(
		    	   RowFilter.regexFilter( (String) getModel().getValueAt( lastRow, 0 ), 0 ))) ;
			list.add( 				       RowFilter.notFilter(
		    	   RowFilter.regexFilter( getVariable().getName(), 0 ) ));
			tableRowSorter.setRowFilter( RowFilter.andFilter(list));
			this.setRowSorter( tableRowSorter );
			
		}

	}
/**
 * Gets the regular expression for the temporal node
 * @param name the name of the node
 * @return the regular expression of the name of node
 */
	private String getRegExp(String name) {
		
		int cont1 = name.indexOf("["); 
		String s1 = name.substring(0,cont1);
		int cont2 = name.indexOf("]");
		String s2 = name.substring(cont1,cont2);
		String s3 = name.substring(cont2,name.length());
		return  s1+"\\"+s2+"\\"+s3;
					
	}

	/**
	 * @return the showingProbabilitiesValues
	 */
	protected boolean isShowingProbabilitiesValues() {

		return showingProbabilitiesValues;
	}

	/**
	 * Method to show/hide rows based upon the showingProbabilitiesValues
	 * attribute If showingProbabilities, table shows numerical values for all
	 * the configurations but if showingValues, table shows the name of the
	 * state of the node corresponding to the maximum value in a deterministic
	 * model.
	 * <ul>
	 * <li>true = show probabilities</li>
	 * <li>false = show values</li>
	 * </ul>
	 * 
	 * @param showingProbabilitiesValues
	 *            the showingProbabilitiesValues to set
	 */
	public void setShowingProbabilitiesValues(boolean showingProbabilitiesValues) {

		this.showingProbabilitiesValues = showingProbabilitiesValues;
		tableRowSorter =
			new TableRowSorter<ValuesTableModel>(
				((ValuesTableModel) getModel()) );
		if (isShowingProbabilitiesValues()) {
			if ((getVariable() != null) && (getVariable().getName() != null)) {
				tableRowSorter.setRowFilter( RowFilter.notFilter( RowFilter
					.regexFilter( getVariable().getName(), 0 ) ) );
				this.setRowSorter( tableRowSorter );
			} else {
				this.setRowSorter( null );
			}
		} else {
			int lastRow = getModel().getRowCount() - 1 - 1;
			lastRow = (lastRow <0 ? 0 : lastRow );
			LinkedList<RowFilter<Object,Object>> list = 
				new LinkedList<RowFilter<Object,Object>> ();
			list.add( RowFilter.regexFilter( 
				(String) getModel().getValueAt( lastRow, 0 ), 0 ) );
			for (State state : getVariable().getStates()) {
				list.add(RowFilter.regexFilter( state.getName() ,0));
			}
			tableRowSorter.setRowFilter(
				RowFilter.notFilter(RowFilter.orFilter( list ))); 
			this.setRowSorter( tableRowSorter );
		}

	}

	/**
	 * @return the showingTPCvalues
	 */
	protected boolean isShowingTPCvalues() {

		return showingTPCvalues;
	}

	/**
	 * Method to show/hide rows based upon th showingProbabilitiesValues
	 * attribute If showingProbabilities, table shows numerical values for all
	 * the configurations but if showingValues, table shows the name of the
	 * state of the node corresponding to the maximum value in a deterministic
	 * model
	 * 
	 * @param showingProbabilitiesValues
	 *            the showingProbabilitiesValues to set
	 */
	public void setShowingTPCvalues(boolean showingTPCvalues) {

		this.showingTPCvalues = showingTPCvalues;
		if (isShowingTPCvalues()) {
			System.out
				.println( "NodePotentialTable. Showing TPC values coming soon..." );
		} else {
			System.out
				.println( "NodePotentialTable. Showing Canonical values coming soon..." );
		}

	}

	/**
	 * @return the showingOptimal
	 */
	public boolean isShowingOptimal() {

		return showingOptimal;
	}

	/**
	 * @param showingOptimal
	 *            the showingOptimal to set
	 */
	public void setShowingOptimal(boolean showingOptimal) {

		this.showingOptimal = showingOptimal;
	}

	/**
	 * Internal method to add a column without affecting the other columns in
	 * the table. If using directly addColumn() to the JTable will cause all
	 * columns will lost previous visual formats
	 * 
	 * @param columnHeaderName -
	 *            name of the column to be used
	 * @param values -
	 *            values to be set in the column
	 */
	public void betterAddColumn(Object columnHeaderName, Object[] values) {

		ValuesTableModel model = (ValuesTableModel) getModel();
		TableColumn col = new TableColumn( model.getColumnCount() );

		setAutoCreateColumnsFromModel( false );
		col.setHeaderValue( columnHeaderName );
		// model.addColumn(col);
		model.addColumn( columnHeaderName.toString(), values );
		setAutoCreateColumnsFromModel( true );
	}

	/**
	 * set the number of columns in the table adding one more for the variable's
	 * states and adding one more for the id column (hidden)
	 * 
	 * @param parents -
	 *            parents of the variable
	 * @return the number of columns in the table
	 */
	public static int howManyColumns(ProbNode properties) {

		int numColumns = 0;
		if (properties.getNode().getParents() != null) {
			int aux = 1;
			for (Node parent : properties.getNode().getParents()) {
				State[] parentStates = ((ProbNode)parent.getObject()).
					getVariable().getStates();
				aux = aux * parentStates.length;
			}
			numColumns = aux;
		} else {
			numColumns = 1;
		}
		numColumns = FIRST_EDITABLE_COLUMN + numColumns;

		return numColumns;
	}
	
	

	/**
	 * set a default id for the columns (Excel format)
	 */
	public static String[] getColumnsIdsSpreedSheetStyle(int howManyColumns) {

		String[] columnsId = new String[ howManyColumns ];
		String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		for (int columnPosition = 0; columnPosition < howManyColumns; columnPosition++) {
			String columnId = "";
			int firstLetterPosition = columnPosition % 26;
			int secondLetterPosition = columnPosition / 26 - 1;
			if (columnPosition >= (26 * 27)) {
			} else if (columnPosition >= 26) {
				columnId =
					columnId
						+ ALPHABET.substring(
							secondLetterPosition, secondLetterPosition + 1 )
						+ ALPHABET.substring(
							firstLetterPosition, firstLetterPosition + 1 );
			} else {
				columnId =
					columnId
						+ ALPHABET.substring(
							firstLetterPosition, firstLetterPosition + 1 );
			}
			columnsId[columnPosition] = columnId;
		}

		return columnsId;
	}

	/**
	 * Auxiliar method to ask user to confirm the reset of the control of the
	 * data already modified.
	 * 
	 * @return
	 * <ul>
	 * <li>true - if user has decided to reset edition controls and proceed;
	 * </li>
	 * <li>false - otherwise</li>
	 * </ul>
	 */
	private boolean askToResetDataModified() {

		int result = -1;
		result =
			JOptionPane
				.showConfirmDialog(
					this,
					this
						.getDialogStringResource()
						.getString(
							"NodePotentialTable.Msg.AllowAgainValuesToModified.Text" ),
					this
						.getDialogStringResource()
						.getString(
							"NodePotentialTable.Msg.AllowAgainValuesToModified.Title" ),
					JOptionPane.YES_NO_OPTION );

		if (result == JOptionPane.YES_OPTION) {
			initializeDataModified( true );
			return true;
		} else {
			return false;
		}

	}

	/**
	 * print the NodePotentialTable
	 */
	public void printTable() {

		System.out.println( "NodePotentialTable: " );
		if (variable != null) {
			System.out.println( "    variable = " + variable.getName() );
		} else {
			System.out.println( "    variable = not defined yet" );
		}
		if (tableModel != null) {
			System.out.println( "    tableModel.firstEditableRow = "
				+ tableModel.getFirstEditableRow() );
			System.out.println( "    tableModel.rowCount = "
				+ tableModel.getRowCount() );
			System.out.println( "    tableModel.columnCount = "
				+ tableModel.getColumnCount() );
		} else {
			System.out
				.println( "    tableModel.firstEditableRow = not tableModel yet" );
		}
		System.out.println( "    lastEditableRow = " + lastEditableRow );
		System.out.println( "    usingGeneralPotencial = "
			+ isUsingGeneralPotential() );
		System.out.println( "    deterministic = " + isDeterministic() );
		System.out.println( "    showingAllParameters = "
			+ isShowingAllParameters() );
		System.out.println( "    showingProbabilitiesValues = "
			+ isShowingProbabilitiesValues() );
		System.out.println( "    showingTPCvalues = " + isShowingTPCvalues() );

	}

	/**
	 * @return the decimalPositions
	 */
	protected static int getDecimalPositions() {

		return decimalPositions;
	}

	/**
	 * @param decimalPositions
	 *            the decimalPositions to set
	 */
	protected static void setDecimalPositions(int newDecimalPositions) {

		decimalPositions = newDecimalPositions;
	}

	/**
	 * roundDouble takes a double number and returns a new double with a certain
	 * number of decimals positions, using rounding mechanism
	 * 
	 * @param number -
	 *            double number to be rounded
	 * @return double with only n-decimals positions
	 */
	private static final double roundingDouble(double number) {

		double positions = Math.pow( 10, (double) decimalPositions );
		return Math.round( number * positions ) / positions;
	}

	
	
	

	public void undoableEditHappened(UndoableEditEvent arg0) {
		
		UndoableEdit edit = arg0.getEdit();
		if (edit instanceof TablePotentialValueEdit){
			auxUndoableEditHappenedNodePotentialEdit(arg0);
		}
		else if (edit instanceof UncertainValuesEdit){
			auxUndoableEditHappenedUncertainValuesEdit(arg0);
		
		}
		
					
	}
	
	private void auxUndoableEditHappenedUncertainValuesEdit(
			UndoableEditEvent arg0) {
		boolean isChance;
		int row;
		int positionInValues;
			
		UncertainValuesEdit edit = (UncertainValuesEdit) (arg0.
				getEdit());
		isChance = edit.isChanceVariable();
		TablePotential tablePotential = (TablePotential) edit.getProbNode().getPotentials().get(0);
		ArrayList<Variable> varsPotential = tablePotential.getVariables();
		int numVarsPotential = varsPotential.size();
		int numParents = (isChance)?numVarsPotential-1:numVarsPotential;
		int col = edit.getSelectedColumn();
		TableModel superModel = super.getModel();
		double[] values = tablePotential.values;
		int basePosition = edit.getBasePosition();
	
		if (isChance){
			int numStates = varsPotential.get(0).getNumStates();
			int startRow = numParents + (numStates-1);
			for (int i=0;i<numStates;i++){
				row = startRow - i;
				positionInValues = basePosition+i;
				superModel.setValueAt(values[positionInValues], row, col);
			}
		}
		else{
			row = numParents;
			positionInValues = col-1;
			superModel.setValueAt(values[positionInValues],row, col);
		}
	}

	public void auxUndoableEditHappenedNodePotentialEdit(UndoableEditEvent arg0){
	
		int position = 0;
		
		TablePotentialValueEdit edit = (TablePotentialValueEdit) arg0.
			getEdit();
		TablePotential tablePotential = edit.getPotential();
		switch (edit.getProbNode().getNodeType()){
		case DECISION:
		case CHANCE:
			
			priorityList = edit.getPriorityList();
			ListIterator<Integer> listIterator = priorityList.listIterator();
			
			ArrayList<Variable> newOrderVariables = new ArrayList<Variable>();
			ArrayList<Variable> orderVariables = probNode.getPotentials().
				get(0).getVariables();
			
			newOrderVariables.add(orderVariables.get(0));
			for (int i = orderVariables.size()-1; i>0; i--){
				newOrderVariables.add(orderVariables.get(i));
				
			}
			TablePotential auxPotential = null;
			try {
				auxPotential = DiscretePotentialOperations.reorder( 
						tablePotential, newOrderVariables);
			} catch (NotEnoughMemoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			while (listIterator.hasNext()== true){
				position = (Integer)listIterator.next();
				super.getModel().setValueAt( auxPotential.values[position], 
						edit.getRowPosition(position), edit.getColumnPosition());

			}
			break;
		case UTILITY:
			position = edit.getColumnPosition()-1;
			super.getModel().setValueAt( tablePotential.values[position], 
					edit.getRowPosition(), edit.getColumnPosition());
		}
	}


	public void undoableEditWillHappen(PNUndoableEditEvent event)
			throws ConstraintViolationException, CanNotDoEditException {
		// TODO Auto-generated method stub
		
	}

	
	public void undoEditHappened(PNUndoableEditEvent event) {
		int position = 0;
		if (event.getEdit() instanceof TablePotentialValueEdit){
			
			TablePotentialValueEdit edit = (TablePotentialValueEdit) event.
				getEdit();
			TablePotential tablePotential = edit.getPotential();
			switch (edit.getProbNode().getNodeType()){
			case CHANCE:
				
				priorityList = edit.getPriorityList();
				ListIterator<Integer> listIterator = priorityList.listIterator();
				
				while (listIterator.hasNext()== true){
					position = (Integer)listIterator.next();
					super.getModel().setValueAt( tablePotential.values[position], 
							edit.getRowPosition(position), edit.getColumnPosition());

				}
				break;
			case UTILITY:
				position = edit.getColumnPosition()-1;
				super.getModel().setValueAt( tablePotential.values[position], 
						edit.getRowPosition(), edit.getColumnPosition());
			}
		}
	}


	/**
	 * Sets probNode
	 * @param probNode
	 */
    public void setData (ProbNode probNode)
    {
      this.probNode = probNode;
    }

	


}