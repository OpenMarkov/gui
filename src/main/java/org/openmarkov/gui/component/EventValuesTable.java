/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */
/**
 *
 */

package org.openmarkov.gui.component;

import org.openmarkov.core.action.PNUndoableEditListener;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.network.*;
import org.openmarkov.core.model.network.potential.TableWithEvents;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.TableWithFunctions;
import org.openmarkov.gui.action.EventTablePotentialValueEdit;
import org.openmarkov.gui.dialog.common.KeyTable;
import org.openmarkov.gui.localize.StringDatabase;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.table.*;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoableEdit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * This table implementation is responsible for the graphical and data model
 * manipulation of <code>EventTablePotential</code>.
 *
 * Transition class to be merged with the new structure of tables
 * @author cyago
 * @version 1.0 - 24/03/2019 - cyago; adapted/copied from ValuesTable
 */
public class EventValuesTable extends KeyTable implements PNUndoableEditListener {
	/**
	 * first editable Column
	 */
	public static final int FIRST_EDITABLE_COLUMN = 1;
	/**
	 * default serial ID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * number of decimals positions to be used for calculations and display
	 */
	protected static int decimalPositions = 2;                                  // by
	/**
	 * table model
	 */
	protected EventValuesTableModel eventValuesTableModel;
	/**
	 * Boolean array with the rows and columns of the eventValuesTableModel.
	 * Each cell of the array is true if the data has been modified
	 * boolean data model (to know if a value has been changed)
	 */
	protected boolean[][] dataModified = null;
	/**
	 * Table Row Sorter/Filter
	 */
	protected TableRowSorter<EventValuesTableModel> tableRowSorter = null;
	/**
	 * type of node for this variable
	 */
	protected NodeType nodeType = null;
	// default;
	/**
	 * last editable row. By default, it is zero until runtime initialisation
	 */
	protected int lastEditableRow = 0;
	/**
	 * define if the table is using General or Canonical Potentials
	 * <ul>
	 * <li>if index = 0 then Using General Potential</li>
	 * <li>if index = 1,2,3 then Using Canonical Potential (family OR)</li>
	 * <li>if index = 4,5,6 then Using Canonical Potential (famili AND)</li>
	 */
	protected int indexPotential = 0;                                  // General
	/**
	 * define if the table shows all parameters or only independent parameters
	 */
	protected boolean showingAllParameters = false;
	// Potential
	// by
	// default
	/**
	 * define if the table shows probabilities values or state name
	 */

	protected boolean showingProbabilitiesValues = false;

	/**
	 * String database
	 */
	protected StringDatabase stringDatabase = StringDatabase.getUniqueInstance();
	protected Node node;
	/**
	 * First eventTablePotential of node
	 *
	 */
	protected TableWithEvents tableWithEvents = null;
	protected TablePotential tablePotential = null;
	protected TableWithFunctions tableWithFunctions = null;

	private ArrayList<Configuration> impossibleConfigurations;


	protected ProbNet probNet;
	/**
	 * Define the last column of the table that was modified
	 */

	protected int lastCol = -1;
	/**
	 * Define the priority list when eventTablePotential values are edited
	 */

	protected List<Integer> priorityList = new LinkedList<Integer>();
	protected boolean isSelectAllForMouseEvent = true;
	protected boolean isSelectAllForActionEvent = false;
	protected boolean isSelectAllForKeyEvent = false;
	/**
	 * first editable row. By default, it is zero until runtime initialisation
	 */
	private int firstEditableRow = 0;

	/**
	 * Default constructor
	 *
	 * @param node       - the node with the EvemtTablePotential
	 * @param eventValuesTableModel - the model of the EventTablePotential
	 * @param modifiable - true if the table can be edited and modified
	 */
	public EventValuesTable(Node node, EventValuesTableModel eventValuesTableModel, final boolean modifiable) {
		super(eventValuesTableModel, modifiable, true, true);
		node.getProbNet().getPNESupport().addUndoableEditListener(this);
		this.eventValuesTableModel = eventValuesTableModel;
		this.node = node;
		this.probNet = node.getProbNet();
		try {
			this.tableWithEvents = (TableWithEvents) node.getPotentials().get(0);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, stringDatabase.getString(e.getMessage()),
					stringDatabase.getString(e.getMessage()), JOptionPane.ERROR_MESSAGE);
		}
		//Adding the initialisation of getEventTablePotential

		tablePotential = tableWithEvents.getTablePotential();
		tableWithFunctions = tableWithEvents.getTableWithFunctions();
		setImpossibleConfigurations(tableWithEvents.getImpossibleConfigurations());
		//
		if (modifiable) {
			int numRowsModel = eventValuesTableModel.getRowCount();
			int numColumsModel = eventValuesTableModel.getColumnCount();
			this.dataModified = new boolean[numRowsModel][numColumsModel];
			initializeDataModified(false);
		}
	}


	/**
	 * Default constructor
	 *
	 * @param node       - the node with the EventTablePotential
	 * @param eventValuesTableModel - the model of the EventTablePotential
	 * @param modifiable - true if the table can be edited and modified
	 */
	public EventValuesTable(Node node, TableWithEvents tableWithEvents, EventValuesTableModel eventValuesTableModel, final boolean modifiable) {
		super(eventValuesTableModel, modifiable, true, true);
		node.getProbNet().getPNESupport().addUndoableEditListener(this);
		this.eventValuesTableModel = eventValuesTableModel;
		this.node = node;
		this.probNet = node.getProbNet();
		try {
			this.tableWithEvents = tableWithEvents;
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, stringDatabase.getString(e.getMessage()),
					stringDatabase.getString(e.getMessage()), JOptionPane.ERROR_MESSAGE);
		}
		//Adding the initialisation of getEventTablePotential

		tablePotential = this.tableWithEvents.getTablePotential();
		setImpossibleConfigurations(this.tableWithEvents.getImpossibleConfigurations());
		//
		if (modifiable) {
			int numRowsModel = eventValuesTableModel.getRowCount();
			int numColumsModel = eventValuesTableModel.getColumnCount();
			this.dataModified = new boolean[numRowsModel][numColumsModel];
			initializeDataModified(false);
		}
	}










	/**
	 * Constructor for ValuesTable
	 */
	public EventValuesTable(EventValuesTableModel eventValuesTableModel, final boolean modifiable) {
		super(eventValuesTableModel, modifiable, true, true);
		this.eventValuesTableModel = eventValuesTableModel;
		if (modifiable) {
			int numRowsModel = eventValuesTableModel.getRowCount();
			int numColumsModel = eventValuesTableModel.getColumnCount();
			this.dataModified = new boolean[numRowsModel][numColumsModel];
			initializeDataModified(false);
		}
	}

	/**
	 * Sets a default id for the columns (Excel format)
	 *
	 * @param howManyColumns - number of columns of the table
	 */
	public static String[] getColumnsIdsSpreadSheetStyle(int howManyColumns) {
		String[] columnsId = new String[howManyColumns];
		String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		for (int columnPosition = 0; columnPosition < howManyColumns; columnPosition++) {
			String columnId = "";
			int firstLetterPosition = columnPosition % 26;
			int secondLetterPosition = columnPosition / 26 - 1;
			if (columnPosition >= (26 * 27)) {
			} else if (columnPosition >= 26) {
				columnId = columnId + ALPHABET.substring(secondLetterPosition, secondLetterPosition + 1) + ALPHABET
						.substring(firstLetterPosition, firstLetterPosition + 1);
			} else {
				columnId = columnId + ALPHABET.substring(firstLetterPosition, firstLetterPosition + 1);
			}
			columnsId[columnPosition] = columnId;
		}
		return columnsId;
	}

	/**
	 * @return the decimalPositions
	 */
	protected static int getDecimalPositions() {
		return decimalPositions;
	}

	/**
	 * @param newDecimalPositions the decimalPositions to set
	 */
	protected static void setDecimalPositions(int newDecimalPositions) {
		decimalPositions = newDecimalPositions;
	}

	/**
	 * This method returns the dataModified variable. If dataModified doesn't exist it is created
	 *
	 * @return dataModified
	 * @see #dataModified
	 * revised--> not changed
	 */
	public boolean[][] getDataModified() {
		if (dataModified == null) {
			int numRowsModel = eventValuesTableModel.getRowCount();
			int numColumsModel = eventValuesTableModel.getColumnCount();
			dataModified = new boolean[numRowsModel][numColumsModel];
		}
		return dataModified;
	}

	/**
	 * This method initialises all the cells of dataModified to the boolean value given by isModified
	 *
	 * @param isModified - initial value for the cells is dataModified
	 * @see #dataModified
	 * revised--> not changed
	 */
	public void initializeDataModified(boolean isModified) {
		if (eventValuesTableModel != null) {
			if (dataModified == null) {
				getDataModified();
			}
			for (int i = 0; i < eventValuesTableModel.getRowCount(); i++) {
				for (int j = 0; j < eventValuesTableModel.getColumnCount(); j++) {
					dataModified[i][j] = isModified;
				}
			}
		}
	}

	/**
	 * Default display configuration for this table
	 */
	@Override protected void defaultConfiguration() {
		super.defaultConfiguration();
		setFirstColumnHidden(false); // key prefix column is hidden
		setShowColumnHeader(false); // no column header here
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setRowSelectionAllowed(true);
		setColumnSelectionAllowed(true);
		setGridColor(Color.DARK_GRAY);
		setDefaultRenderer(Double.class, new ValuesTableCellRenderer(0));
		setDefaultRenderer(String.class, new ValuesTableCellRenderer(0));
		// next two lines is a cool trick to enhance table performance
		ToolTipManager.sharedInstance().unregisterComponent(this);
		ToolTipManager.sharedInstance().unregisterComponent(getTableHeader());
	}

	/**
	 * Resets the model in use
	 * revised-> not changed
	 */
	public void resetModel() {
		eventValuesTableModel = null;
		dataModified = null;
	}

	/**
	 * Gets the eventValuesTableModel attribute
	 * revised-->not changed
	 */
	public EventValuesTableModel getEventValuesTableModel() {
		return this.eventValuesTableModel;
	}

	/**
	 * Sets the data model for this table to newModel and registers with it for
	 * listener notifications from the new data model.
	 *
	 * @param newDataModel the new data source for this table.
	 * @throws IllegalArgumentException if newModel is null.
	 *                                  <p>
	 *                                  revised-->not changed
	 */
	public void setModel(EventValuesTableModel newDataModel) throws IllegalArgumentException {
		super.setModel(newDataModel);
		this.eventValuesTableModel = newDataModel;
		tableRowSorter = new TableRowSorter<EventValuesTableModel>(((EventValuesTableModel) getModel()));
		// not display the last row where the cells has states and not values
		// and it is only required when displaying states values
	}

	/**
	 *
	 * @see JTable#changeSelection(int, int, boolean, boolean)
	 * revised-->not changed
	 */
	@Override public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
		super.changeSelection(rowIndex, columnIndex, toggle, extend);
		if (columnIndex < FIRST_EDITABLE_COLUMN) { // not selectable
			super.changeSelection(rowIndex, columnIndex + 1, toggle, extend);
		} else {
			super.changeSelection(rowIndex, columnIndex, toggle, extend);
		}
	}

	/**
	 * Cancels the editing in any cell of the table, avoiding its new value is
	 * recorded.
	 * revised-->not changed
	 */
	public void cancelCellEditing() {
		TableCellEditor actualEditor = getCellEditor();
		if (actualEditor != null) {
			actualEditor.cancelCellEditing();
		}
	}

	/**
	 * Stops the editing in any cell of the table, recording the new value.
	 * revised-->not changed
	 */
	public void stopCellEditing() {
		TableCellEditor actualEditor = getCellEditor();
		if (actualEditor != null) {
			actualEditor.stopCellEditing();
		}
	}

	/**
	 * check the value to modify in the table and sets
	 * carmenyago removed the dependency with the utility type, the use of deterministic tables
	 * and checked if the new can be value converted to a double. She also deleted the use of checkUtilityVariable
	 *
	 * @author cyago
	 * @version 1.1 18/05/2022 - Changed to be used with numeric variables; currently discretized variables not considered
	 */
	public void setValueAt(Object newValue, int row, int col) {
		try {
			Object oldValue = getValueAt(row, col);
			// Not clear if I have to use equals
			if (oldValue.equals(newValue))
				return;

			//18/05/2022 - Changed to be used with numeric variables; currently discretized variables not considered
//			if (nodeType == NodeType.CHANCE || nodeType == NodeType.DECISION) {
			VariableType variableType = node.getVariable().getVariableType();
			if (variableType == VariableType.FINITE_STATES) {
				if (((Double) newValue) < 0) {
					newValue = oldValue;
					JOptionPane.showMessageDialog(this.getParent(), "Introduced value cannot be negative");
				}
				//if (nodeType == NodeType.CHANCE || nodeType == NodeType.DECISION)
				if (lastCol != col) {
					priorityList.clear();
					lastCol = col;
				}
			}


			// Chance, decision and utility

			EventTablePotentialValueEdit eventTablePotentialValueEdit = new EventTablePotentialValueEdit(node, tableWithEvents, newValue, row, col,
					priorityList);

//		try {
			probNet.doEdit(eventTablePotentialValueEdit);
//		} catch (ConstraintViolationException | NonProjectablePotentialException | WrongCriterionException | DoEditException e) {
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, stringDatabase.getString(e.getMessage()),
					stringDatabase.getString(e.getMessage()), JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Check if newValue is a String or a Double
	 *
	 * @param newValue - new value to validate
	 * @author carmenyago
	 */
	protected boolean castValue(Object newValue) {

		if (newValue instanceof String)
			try {
				Double.parseDouble((String) newValue);
				return true;
			} catch (Exception ex) {
				return false;
			}
		if (newValue instanceof Double)
			return true;
		return false;
	}

	/**
	 * show a error window message to the user with a specific msg
	 *
	 * @param msg - the error message to show to user
	 *            revised-->not changed
	 */
	protected void showNodePotentialTableErrorMsg(String msg) {
		JOptionPane.showMessageDialog(this, stringDatabase.getString(msg + ".Text"),
				stringDatabase.getString(msg + ".Title"), JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * @return the variable
	 */
	public Variable getVariable() {
		return node.getVariable();
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
	 * @param lastEditableRow the lastEditableRow to set
	 */
	public void setLastEditableRow(int lastEditableRow) {
		this.lastEditableRow = lastEditableRow;
	}

	/**
	 * @return the firstEditableRow
	 */
	public int getFirstEditableRow() {
		return firstEditableRow;
	}

	/**
	 * @param firstEditableRow the firstEditableRow to set
	 */
	public void setFirstEditableRow(int firstEditableRow) {
		this.firstEditableRow = firstEditableRow;
	}

	/**
	 * @return the usingGeneralPotential
	 */
	public boolean isUsingGeneralPotential() {
		return (indexPotential == 0 ? true : false);
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
	 * @param showingAllParameters if true, show all; if false, show only
	 *                             independent parameters
	 *                             <p>
	 *                             revised-->  minor changes
	 */
	public void setShowingAllParameters(boolean showingAllParameters) {
		this.showingAllParameters = showingAllParameters;
		tableRowSorter = new TableRowSorter<EventValuesTableModel>(((EventValuesTableModel) getModel()));
		if (showingAllParameters) {
			this.setRowSorter(null);
		} else {
			int lastRow = getModel().getRowCount() - 1 - 1;
			lastRow = (lastRow < 0 ? 0 : lastRow);
			LinkedList<RowFilter<Object, Object>> list = new LinkedList<RowFilter<Object, Object>>();
			list.add(RowFilter.notFilter(RowFilter.regexFilter((String) getModel().getValueAt(lastRow, 0), 0)));
			list.add(RowFilter.notFilter(RowFilter.regexFilter(getVariable().getName(), 0)));
			tableRowSorter.setRowFilter(RowFilter.andFilter(list));
			this.setRowSorter(tableRowSorter);
		}
	}

	/**
	 * Gets the regular expression for the temporal node
	 *
	 * @param name the name of the node
	 * @return the regular expression of the name of node. It returns namenode\\[number\\]
	 * <p>
	 * revised--> not changed
	 */
	protected String getRegExp(String name) {
		int cont1 = name.indexOf("[");
		String s1 = name.substring(0, cont1);
		int cont2 = name.indexOf("]");
		String s2 = name.substring(cont1, cont2);
		String s3 = name.substring(cont2, name.length());
		return s1 + "\\" + s2 + "\\" + s3;
	}

	/**
	 * Gets the regular expression for node names with parenthesis
	 *
	 * @param name the name of the node
	 * @return the regular expression of the name of node. This method returns
	 * the same name but substituting '(' and ')' by '\\(' and '\\)'
	 */
	protected String getRegExpParenthesis(String name) {
		if (name.contains("(")) {
			name = name.replace("(", "\\(");
		}
		if (name.contains(")")) {
			name = name.replace(")", "\\)");
		}
		return name;
	}

	/**
	 * @return the showingProbabilitiesValues
	 */
	protected boolean isShowingProbabilitiesValues() {
		return showingProbabilitiesValues;
	}


	/**
	 * print the NodePotentialTable

	 * @cyago minor changes
	 */
	public void printTable() {
		System.out.println("NodePotentialTable: ");
		if (getVariable() != null) {
			System.out.println("    variable = " + getVariable().getName());
		} else {
			System.out.println("    variable = not defined yet");
		}
		if (eventValuesTableModel != null) {
			System.out.println("    eventValuesTableModel.firstEditableRow = " + eventValuesTableModel.getFirstEditableRow());
			System.out.println("    eventValuesTableModel.rowCount = " + eventValuesTableModel.getRowCount());
			System.out.println("    eventValuesTableModel.columnCount = " + eventValuesTableModel.getColumnCount());
		} else {
			System.out.println("    eventValuesTableModel.firstEditableRow = not eventValuesTableModel yet");
		}
		System.out.println("    lastEditableRow = " + lastEditableRow);
		System.out.println("    usingGeneralPotencial = " + isUsingGeneralPotential());
		System.out.println("    showingAllParameters = " + isShowingAllParameters());
		System.out.println("    showingProbabilitiesValues = " + isShowingProbabilitiesValues());

	}

	public void undoableEditHappened(UndoableEditEvent event) {
		UndoableEdit edit = event.getEdit();
		if (edit instanceof EventTablePotentialValueEdit) {
			eventTablePotentialValueEditHappened((EventTablePotentialValueEdit) edit);

		}
	}



	public void eventTablePotentialValueEditHappened(EventTablePotentialValueEdit edit) {
		// postition in the table of TablePotential
		int position = 0;
		int rowPosition = edit.getRowPosition(position);
		int columnPosition = edit.getColumnPosition();
		TablePotential tablePotential = edit.getTablePotential();
		TableWithFunctions tableWithFunctions = edit.getTableWithEvents().getTableWithFunctions();
		if ((getNodeType()==NodeType.EVENT) || (getNodeType()==NodeType.UTILITY)){

			int potentialSelected = edit.getPotentialSelected();

			rowPosition = edit.getRowPosition(position);
			columnPosition = edit.getColumnPosition();
			try {
				if (tableWithFunctions ==null) {
					getModel()
							.setValueAt(tablePotential.getValues()[potentialSelected], edit.getRowPosition(), edit.getColumnPosition());
				} else{
					getModel()
							.setValueAt(tableWithFunctions.getFunctionValues()[potentialSelected], edit.getRowPosition(), edit.getColumnPosition());
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		} else{
				priorityList = edit.getPriorityList();
				ListIterator<Integer> listIterator = priorityList.listIterator();
				double[] values = tablePotential.getValues();
				while (listIterator.hasNext()) {
					position = (Integer) listIterator.next();
					rowPosition = edit.getRowPosition(position);
					columnPosition = edit.getColumnPosition();
					super.getModel().setValueAt(values[position], rowPosition, columnPosition);
				}
			}
	}

	/**
	 *
	 */
	public void undoableEditWillHappen(UndoableEditEvent event)
			throws ConstraintViolationException {
		// Ignore
	}

	/**
	*
	 */
	public void undoEditHappened(UndoableEditEvent event) {
		if (event.getEdit() instanceof EventTablePotentialValueEdit) {
			EventTablePotentialValueEdit edit = (EventTablePotentialValueEdit) event.getEdit();
			TablePotential editPotential = edit.getTablePotential();
			TableWithEvents transitionTablePotential = (TableWithEvents) edit.getTableWithEvents();

			priorityList = edit.getPriorityList();
			for (Integer position : priorityList) {
				super.getModel().setValueAt(editPotential.values[position], edit.getRowPosition(position),
							edit.getColumnPosition());
			}
		}
	}

	/**
	 * This method edits the cell at row, column.
	 * If isSelectAllForMouseEvent,isSelectAllForActionEvent, or isSelectAllForKeyEvent the entire cell is selected
	 * UNCLEAR isSelectAllForMouseEvent,isSelectAllForActionEvent, or isSelectAllForKeyEvent values never change
	 * Overrided to provide Select All editing functionality
	 *
	 * @param row    - the row of the edited cell
	 * @param column - the column of the edited cell
	 * @param e      - event to pass into shouldSelectCell;
	 */
	public boolean editCellAt(int row, int column, EventObject e) {
		boolean result = super.editCellAt(row, column, e);
		if (isSelectAllForMouseEvent || isSelectAllForActionEvent || isSelectAllForKeyEvent) {
			selectAll(e);
		}
		return result;

	}

	/**
	 * If the editor that is handling the editing session is not a JTextComponent, the method does nothing
	 * If the editor is a JTextComponent then:
	 * If e is and instance of KeyEvent, ActionEvent or MouseEvent, the method select all the text of the cell
	 *
	 * @param e: event which provoked the edition and selection
	 */
	private void selectAll(EventObject e) {
		// Returns the component that is handling the editing session.
		final Component editor = getEditorComponent();
		if (editor == null || !(editor instanceof JTextComponent))
			return;
		if (e == null) {
			((JTextComponent) editor).selectAll();
			return;
		}
		// Typing in the cell was used to activate the editor
		if (e instanceof KeyEvent && isSelectAllForKeyEvent) {
			((JTextComponent) editor).selectAll();
			return;
		}
		// F2 was used to activate the editor
		if (e instanceof ActionEvent && isSelectAllForActionEvent) {
			((JTextComponent) editor).selectAll();
			return;
		}
		// A mouse click was used to activate the editor.
		// Generally this is a double click and the second mouse click is
		// passed to the editor which would remove the text selection unless
		// we use the invokeLater()
		if (e instanceof MouseEvent && isSelectAllForMouseEvent) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					((JTextComponent) editor).selectAll();
				}
			});
		}
	}

	/**
	 * This method sets the variable probNet to the node probNet
	 *
	 * @param node: the node whose eventTablePotential is being displayed
	 */
	public void setData(Node node) {
		if (this.probNet.getPNESupport() != node.getProbNet().getPNESupport()) {
			this.probNet.getPNESupport().removeUndoableEditListener(this);
			node.getProbNet().getPNESupport().addUndoableEditListener(this);
		}
		this.probNet = node.getProbNet();
	}

	/**
	 * Closes this object and prepare it for disposal
	 */
	public void close() {
		probNet.getPNESupport().removeUndoableEditListener(this);
	}

	/**
	 * Adjusts columns width to its content
	 */
	public void fitColumnsWidthToContent() {
		JTableHeader header = getTableHeader();

		TableCellRenderer headerRenderer = null;

		if (header != null) {
			headerRenderer = header.getDefaultRenderer();
		}

		TableColumnModel columns = getColumnModel();
		TableModel tableModel = getModel();
		int margin = columns.getColumnMargin();
		int rowCount = tableModel.getRowCount();
		int columnCount = tableModel.getColumnCount();

		for (int columnIndex = 0; columnIndex < columnCount; ++columnIndex) {
			TableColumn column = columns.getColumn(columnIndex);
			column.setMinWidth(60);
			int width = -1;

			TableCellRenderer tableCellRenderer = column.getHeaderRenderer();

			if (tableCellRenderer == null) {
				tableCellRenderer = headerRenderer;
			}

			if (tableCellRenderer != null) {
				Component component = tableCellRenderer
						.getTableCellRendererComponent(this, column.getHeaderValue(), false, false, -1, columnIndex);

				width = component.getPreferredSize().width;
			}

			for (int rowIndex = 0; rowIndex < rowCount; ++rowIndex) {
				TableCellRenderer cellRenderer = getCellRenderer(rowIndex, columnIndex);

				Component c = cellRenderer
						.getTableCellRendererComponent(this, tableModel.getValueAt(rowIndex, columnIndex), false, false,
								rowIndex, columnIndex);

				width = Math.max(width, c.getPreferredSize().width);
			}

			if (width >= 0) {
				column.setMinWidth(width + margin);
			}
		}
	}

	public ArrayList<Configuration> getImpossibleConfigurations() {
		return impossibleConfigurations;
	}

	public void setImpossibleConfigurations(ArrayList<Configuration> impossibleConfigurations) {
		this.impossibleConfigurations = impossibleConfigurations;
	}
}