/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.dialog.common;

import org.apache.logging.log4j.Logger;
import org.openmarkov.core.action.UncertainTteEdit;
import org.openmarkov.core.action.UncertainTteRemoveEdit;
import org.openmarkov.core.action.UncertainValuesRemoveEdit;
import org.openmarkov.core.exception.*;
import org.openmarkov.core.model.network.*;
import org.openmarkov.core.model.network.potential.EventTablePotential;
import org.openmarkov.core.model.network.potential.EventTimeTablePotential;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.operation.LinkRestrictionPotentialOperations;
import org.openmarkov.gui.component.*;
import org.openmarkov.gui.dialog.node.AssignUncertainTteDialog;
import org.openmarkov.gui.dialog.node.UncertainValuesDialog;
import org.openmarkov.gui.menutoolbar.common.ActionCommands;
import org.openmarkov.gui.menutoolbar.menu.UncertaintyContextualMenu;
import org.openmarkov.gui.util.Utilities;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * This class implements a Table eventTablePotential table.
 * Transition class to be merged with the new structure of tables
 * @version 1.0 - cyago - 24/03/2019
 */
@SuppressWarnings("serial") @PotentialPanelPlugin( potentialType = "Event") public class EventTablePotentialPanel
		extends ProbabilityTablePanel {
	protected Logger logger;
	/**
	 * JTable where show the values.
	 */
	protected EventValuesTable eventValuesTable = null;
	/**
	 * Indicates if the data of the table is modifiable.
	 */
	protected boolean modifiable;
	/**
	 * Panel to scroll the table.
	 */
	protected JScrollPane valuesTableScrollPane = null;
	protected Node node;

	/**
	 * First eventTablePotential of node;  its class  should be  org.openmarkov.core.model.network.eventTablePotential.EventTablePotential or
	 *
	 */
	protected EventTablePotential eventTablePotential = null;

	/**
	 * TablePotential of EventTablePotential
	 */

	protected TablePotential tablePotential = null;

	/**
	 * Variables of the TablePotential of EventTablePotential
	 */
	protected List<Variable> tableVariables = null;


	/**
	 * True if some parent has a link restriction to the node
	 *
	 * @author carmenyago
	 */
	protected boolean hasLinkRestriction;

	/**
	 * UNCLEAR-->We calculate the uncertainty. This is calculated several times; i have to check if calculations are repeated unnecessarily
	 */
	protected boolean[] uncertaintyInColumns;

	/**
	 * Pseudo-util class with common operations used in eventTablePotential tables
	 */
	protected PotentialsTablePanelOperations tablePotentialsPanelOperations;

	/**
	 * ContextualMenu to assign/remove uncertainty.
	 * <p>
	 * This method creates the evidenceCase object when the user do right click on the table.
	 */

	protected UncertaintyContextualMenu uncertaintyContextualMenu;

	public EventTablePotentialPanel() {
		super();
	}

	/**
	 * Constructor used by CPTablePanel
	 * This method creates, initialises, and displays a EventValuesTable object for the first eventTablePotential of the node
	 * <p>
	 * When there is no eventTablePotential NullListPotentialException
	 * <p>
	 * @param node : node whose first eventTablePotential is a TablePotential or a TableDeltaPotential
	 * @author carmenyago : adaptation to TableDeltaPotential
	 */
	public EventTablePotentialPanel(Node node) {
		super();

		this.tablePotentialsPanelOperations = new PotentialsTablePanelOperations();

		// If there is no eventTablePotential
		try {
			tablePotentialsPanelOperations.checkIfNoPotential(node.getPotentials());
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, stringDatabase.getString(e.getMessage()),
					stringDatabase.getString(e.getMessage()), JOptionPane.ERROR_MESSAGE);
			return;
		}
		this.node = node;

		eventTablePotential = (EventTablePotential) node.getPotentials().get(0);
		boolean yes =eventTablePotential instanceof EventTimeTablePotential;



		tablePotential =  eventTablePotential.getTablePotential();

		// The list of variables of eventTablePotential
		variables = eventTablePotential.getVariables();

		// The list of variables of the tablePotential of EventTablePotential
		tableVariables = tablePotential.getVariables();



		// Creating the table; class EventValuesTable
		eventValuesTable = new EventValuesTable(node, getTableModel(), modifiable);
		eventValuesTable.setName("EventPotentialsTablePanel.eventValuesTable");
		eventValuesTable.setVisible(true);

		modifiable = true;

		setTableSpecificListeners();

		setData();

		setLayout(new BorderLayout());

		// If the ScrollPane is not created, initialise it and set the Viewport.
		// Then add the element to the Layout.
		add(getValuesTableScrollPane(), BorderLayout.CENTER);

		repaint();
	}

	/**
	 * Sets a new table model with new data and new columns in eventValuesTable
	 *
	 * @param newData    new data for the table
	 * @param newColumns new columns for the table
	 * @author carmenyago
	 * revised--> minor changes
	 * Previously named setData; I find this name confusing because coincides with setData()
	 */
	public void setDataInValuesTable(Object[][] newData, String[] newColumns) {

		// Table data
		data = newData.clone();
		// Table columns
		columns = newColumns.clone();

		// resets the eventValuesTableModel
		eventValuesTable.resetModel();

		// Sets the eventValuesTable eventValuesTableModel with columns, data
		eventValuesTable.setModel(new EventValuesTableModel(data, columns, firstEditableRow));

		// Initialises a false an array which tells which data are modified
		eventValuesTable.initializeDataModified(false);

		eventValuesTable.setLastEditableRow(lastEditableRow);

		//show/hide rows based on the showingAllParameters attribute using a RowFilter mechanism.
		eventValuesTable.setShowingAllParameters(true);

		eventValuesTable.setNodeType(node.getNodeType());
	}

	/**
	 * It is necessary to implement setData(Node node)
	 * Here I deal with eventTablePotential = null or eventTablePotential =0;
	 * <p>
	 * UNCLEAR--> Called in PotentialEditDialog.showFields(Node)
	 *
	 * @author carmenyago
	 */
	public void setData(Node node) {
		this.node = node;

		try {
			tablePotentialsPanelOperations.checkIfNoPotential(node.getPotentials());

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, stringDatabase.getString(e.getMessage()),
					stringDatabase.getString(e.getMessage()), JOptionPane.ERROR_MESSAGE);
			return;
		}
		setData();
	}

	/**
	 * Sets a new table model with new data and new columns based on three
	 * items: <li>list of Potentials of the variable</li> <li>states of the
	 * variable</li> <li>parents of the variable</li>
	 * This method obtains if the node has link restrictions and store it in hasLinkrestriction,
	 * stores the probNet in EventValuesTable
	 * fills the tableData (tableData consists of headers + data),
	 * sets the columns name in a Excel mode (A,B,C,...AA,AB...),
	 * sets the eventValuesTableModel in eventValuesTable( tableData + column names),
	 * sets uncertaintyInColumns with the columns with uncertainty,
	 * sets the cell renders according to the type of node, and
	 * in the tableMoel, sets the not editable cells due to links restrictions and uncertainty in columns.
	 * Finally, this method adjust the size of the cells in eventValuesTable
	 *
	 * @author carmenyago
	 */
	// Using node sets in variable node
	// What to do with the exception
	public void setData() {

		// true
		hasLinkRestriction = LinkRestrictionPotentialOperations.hasLinkRestriction(node);
		// Sets the probNet in the table

		eventValuesTable.setData(node);

		Object[][] tableData = null;
		uncertaintyInColumns = null;
		String[] newColumns = null;

		// tableData contains the table to be displayed in EventValuesTable
		tableData = convertListPotentialsToTableFormat();

		// Sets the column names in Excel style: A, B, C,....AA,AB...
		// These column names aren't displayed
		newColumns = EventValuesTable.getColumnsIdsSpreadSheetStyle(tableData[0].length);

		//Calculated in convertListPotentialsToTableFormat-->createEmptyTable()
		//setFirstEditableRow(tablePotentialsPanelOperations.calculateFirstEditableRow(node));
		//setLastEditableRow(tablePotentialsPanelOperations.calculateLastEditableRow(node));

		//Sets the table model in eventValuesTable
		setDataInValuesTable(tableData, newColumns);

		//uncertaintyInColums indicates the data columns which have uncertainty
		uncertaintyInColumns = getUncertaintyInColumns();

		// set the Cell Renders according to NodeType (a different renderer for some DECISON nodes) and the uncertainty
		setCellRenderers(uncertaintyInColumns);

		// getNotEditablePositions checked if there is any link restriction
		// which make the correspondent cells no editable and returns an array with the size of the table
		// with the not editable cells set to 1
		this.getTableModel().setNotEditablePositions(getNotEditablePositions());

		// Establish the column width
		eventValuesTable.fitColumnsWidthToContent();
	}

	/**
	 * Sets the columns that have uncertainty a true in a boolean array
	 * To do that, this method extracts the uncertainty for every column configuration (parents state set)
	 * <p>
	 * UNCLEAR-->When we reach this method eventTablePotential!=null
	 *
	 * @return Boolean array that represents the columns (true = the column has
	 * an uncertainty, false = the column has not an uncertainty). This array only contains the data columns
	 * @author carmenyago
	 */
	protected boolean[] getUncertaintyInColumns() {

		int size = eventValuesTable.getColumnCount();

		// Column 0 contains the name of the states
		boolean[] newUncertaintyInColumns = new boolean[size - 1];

		for (int i = 1; i < size; i++) {
			boolean hasUncertainty = false;
			try {
				// Returns an evidence case with one finding for every parent variable and its state in the column
				EvidenceCase configuration = getConfiguration(i);
				// If the column configuration has uncertainty hasUncertainty= true
				hasUncertainty = tablePotential.hasUncertainty(configuration);
			} catch (InvalidStateException | IncompatibleEvidenceException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, stringDatabase.getString(e.getMessage()),
						stringDatabase.getString(e.getMessage()), JOptionPane.ERROR_MESSAGE);
			}
			// Indicates whether this column has uncertainty or not
			newUncertaintyInColumns[i - 1] = hasUncertainty;
		}
		return newUncertaintyInColumns;
	}

	/**
	 * calculate the number of rows of the table based on the parents and  states of the node variable
	 * Last row with the name of the variable when TablePotential REMOVED
	 *
	 * @author carmenyago
	 */
	protected int howManyRows(Node n) {
		return n.getParents().size() + n.getVariable().getStates().length;
	}

	/**
	 * Creates an array[number_of_rows][number_of_columns] with the objects displayed in the cells of valueTable
	 * Considers the eventTablePotential is not null
	 *
	 * @return the table data to be set
	 * @author carmenyago
	 */
	protected Object[][] convertListPotentialsToTableFormat() {
		Object[][] values = null;

		// Empty array values[number_of_rows][number_of_colums]
		values = createEmptyTable();

		// Sets the number of the parent variables
		values = setParentsNameInUpperLeftCornerArea(values);

		// Set the states of the parents on  the top of the table
		// UNCLEAR--> what happens when the parent variable is continuous????
		values = setParentsStatesInTopArea(values);
		// Set the states of the node variable on the left column
		values = setNodeStatesInLeftArea(values);

		// Set the TablePotential/TableDeltaPotential Data on values
		values = setPotentialDataInCentreArea(values);

		// The variable position stores the number o data cells
		setNumberOfPostions();
		return values;
	}

	/**
	 * Creates and empty array of empty objects with the [number_of_rows][number_of_columns] of the eventValuesTable
	 * Considers the eventTablePotential is not null
	 * UNCLEAR --> setBaseIndexForCoordinates
	 *
	 * @author carmenyago
	 * <p>
	 * Continuous variables have only one state
	 * tableSize is always >0
	 */
	protected Object[][] createEmptyTable() {

		// If there is no eventTablePotential
		try {
			tablePotentialsPanelOperations.checkIfNoPotential(node.getPotentials());
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, stringDatabase.getString(e.getMessage()),
					stringDatabase.getString(e.getMessage()), JOptionPane.ERROR_MESSAGE);
			return null;
		}
		int numRows = 0;
		int numColumns = 1; // Variables column

		// First editable row coincides with the number of parents
		firstEditableRow = tablePotentialsPanelOperations.calculateFirstEditableRow(tablePotential);

		// The baseIndexForCoordinates is the first editable row-->What for-->UNCLEAR
		// The property baseIndexForCoordinates is not Visible. baseIndexForCoordinates= row
		setBaseIndexForCoordinates(firstEditableRow);


		// Number of data elements of tablePotential
		int tableSize = tablePotential
				.getTableSize();//-->UNCLEAR What happens when there is no parent (f.e. when Tree/ADD )

		// Number of states of the variable of the node; if isTableDeltaPotential numDimensions=1
		int numDimensions = 1;
		numDimensions = tablePotential.getDimensions()[0];

		// Parent variables + states of node variable
		numRows = firstEditableRow + numDimensions;
		lastEditableRow = numRows - 1;

		/*if (!isTableDeltaPotential) numRows++;*/ //--> UNCLEAR Last row with the name of the variable and the state with '1' is REMOVED
		numColumns = numColumns + tableSize / numDimensions;

		// create the array of arrays
		return new Object[numRows][numColumns];
	}

	/**
	 * This methods fills the Upper Left corner of the table with the name of
	 * the parents of the node
	 *
	 * @param oldValues - the table that is being modified
	 * @author carmenyago
	 */
	protected Object[][] setParentsNameInUpperLeftCornerArea(Object[][] oldValues) {
		Object[][] values = oldValues;
		// Adding the parent
		// The first variable is always the node variable
		for (int i = 1; i < tableVariables.size(); i++) {
			values[i - 1][0] = tableVariables.get(i);
		}
		return values;
	}

	/**
	 * Sets the states of the parents in the top of the table
	 * Potential is not null
	 *
	 * @param oldValues - the table that is being modified. oldValues !=null and oldValues.lenght is always > 0
	 * @author carmenyago
	 */
	protected Object[][] setParentsStatesInTopArea(Object[][] oldValues) {
		Object[][] values = oldValues;

		int numColumns = values[0].length;

		// Initialise the variable with the number of data columns
		int numRepetitions = numColumns - 1;
		int numParentVariables = tableVariables.size() - 1;
		State[] states;

		for (int row = 0; row < numParentVariables; row++) {

			states = tableVariables.get(row + 1).getStates();
			numRepetitions = numRepetitions / states.length;

			for (int column = 1; column < numColumns; column++) {
				// Find the index of the state. We start in zero position of the
				// array of states, and thus we need to substract a unit to
				// column
				// The ratio divides the table in sections and the module
				// get the position relative to the section.
				int stateIndex = ((column - 1) / numRepetitions) % states.length;
				State state = states[stateIndex];
				values[row][column] = state.getName();
			}

		}
		return values;
	}

	/**
	 * this method sets the first row with the values of the states of the node
	 * (if it is a node chance) or the name of the variable of the node (if it
	 * is a utility node)
	 *
	 * @param oldValues - the table that is being modified
	 */
	protected Object[][] setNodeStatesInLeftArea(Object[][] oldValues) {
		Object[][] values = oldValues;
        Variable leftVariable=tableVariables.get(0);
		int length = lastEditableRow;
		if (leftVariable.getVariableType()==VariableType.EVENT){
			values[length][0] =leftVariable.getName() ;

		} else {

			for (State state : leftVariable.getStates()) {
				values[length--][0] = state.getName();
			}
		}
		return values;
	}

	/**
	 * Sets the data table from eventTablePotential in oldValues
	 *
	 * @param oldValues
	 * @return an array filled with the date table from tablePotential or tableDeltaPotential filled with the data values
	 * from tablePotential or tableDeltaPotential in the correct positions to be displayed by EventValuesTable
	 */
	protected Object[][] setPotentialDataInCentreArea(Object[][] oldValues) {
		Object[][] values = oldValues;

		int numColumns = values[0].length;

		// rounding initial values
		double[] initialValues = tablePotential.getValues();
		double[] roundedValues = new double[initialValues.length];
		int maxDecimals = 10;
		double epsilon;
		epsilon = Math.pow(10, -(maxDecimals + 2));
		for (int i = 0; i < initialValues.length; i++) {
			roundedValues[i] = Util.roundAndReduce(initialValues[i], epsilon, maxDecimals);
		}
		// UNCLEAR-->What for??
		//tablePotential.setValues(roundedValues);

		for (int j = 1; j <= numColumns - 1; j++) {

			// put the values on the table
			for (int i = getLastEditableRow(); i >= getFirstEditableRow(); i--) {
				int potentialIndex = tablePotentialsPanelOperations.getPotentialIndex(i, j, tablePotential);
				double value = roundedValues[potentialIndex];
				values[i][j] = value;
			}
		}
		return values;
	}

	/**
	 * This method calculates the number of data cells and stores it in the attribute positions.
	 * The number of data cell is the product of the number of states of all variables
	 *
	 * @author carmenyago
	 * minor changes
	 */
	protected int setNumberOfPostions() {
		int numPositions = 1;
		try {
			for (Variable variable : tableVariables) {
				numPositions = numPositions * variable.getNumStates();
			}
		} catch (NullPointerException exception) {
			numPositions = 0;
			logger.error("not enough memory");
		}
		setPosition(numPositions);
		return numPositions;
	}

	/**
	 * Calculates the position on eventValuesTable for a state combination
	 *
	 * @param stateIndices - indexes of the states
	 * @return an array containing the row at the first position and the column
	 * at the second position.
	 * revised--> only changed the code between CMI, CMF
	 */
	protected int[] getRowAndColumnForStateCombination(int[] stateIndices, TablePotential potential) {
		int numStates = node.getVariable().getNumStates();
		int position = potential.getPosition(stateIndices);
		int tempMultiplier = tablePotential.getTableSize() / numStates;
		int tempColumnPosition = 0;

		// We start at index 1 because the state of the node is irrelevant for
		// obtain the column (only is relevant for the row)
		// We multiply the number of columns above each state and the index of
		// this variable (in wich state is)
		for (int i = 1; i < potential.getVariables().size(); i++) {
			Variable var = potential.getVariables().get(i);
			tempMultiplier = tempMultiplier / var.getNumStates();
			tempColumnPosition += stateIndices[i] * tempMultiplier;
		}

		// The column will be the column in the data structure plus one row at
		// the beginning
		int column = tempColumnPosition + 1;
		// The row will be the last row in the table minus the relative position
		// in the node state
		int row = getLastEditableRow() - (position % numStates);
		return new int[] { row, column };
	}

	/****
	 * Calculates the positions of the table which are not editable due to a
	 * link restriction or uncertainty in the columns.
	 * If the position is not editable the position in the return array is set to 1, otherwise it contains a null value.
	 *
	 * @return a two dimensional array with the size of the table containing the
	 *         information about the editable positions.
	 *
	 * UNCLEAR--> Can a utility Node have nodes with restriction and what to do?
	 * @author carmenyago
	 *
	 */
	protected Object[][] getNotEditablePositions() {
		Object[][] notEditablePositions = createEmptyTable();
		//CMI Bug #162 Applying restriction to utility Nodes
		//if (!isTableDeltaPotential && hasLinkRestriction){
		if (hasLinkRestriction) {
			//CMF
			List<int[]> statesWithRestriction = LinkRestrictionPotentialOperations
					.getStateCombinationsWithLinkRestriction(node);

			for (int[] state : statesWithRestriction) {
				int[] position = getRowAndColumnForStateCombination(state, tablePotential);
				int row = position[0];
				int column = position[1];
				notEditablePositions[row][column] = 1;
			}
		}
		// I suppose it is calculated previously
		//	uncertaintyInColumns = getUncertaintyInColumns();
		for (int row = firstEditableRow; row < notEditablePositions.length; ++row) {
			for (int column = 1; column < notEditablePositions[0].length; ++column) {
				if (uncertaintyInColumns[column - 1]) {
					notEditablePositions[row][column] = 1;
				}
			}
		}
		return notEditablePositions;
	}

	/**
	 * This method generates the evidenceCase based on the column selected on
	 * the <code>eventValuesTable</code> object.
	 * The evidence case has a finding for every parent of the node and its state in column
	 * <p>
	 * UNCLEAR When is the parents list reordered???
	 *
	 * @param col The column selected. Never is 0 , because the column 0 is the
	 *            states column
	 * @return An evidence case object
	 * @throws InvalidStateException
	 * @throws IncompatibleEvidenceException
	 */
	protected EvidenceCase getConfiguration(int col) throws InvalidStateException, IncompatibleEvidenceException {
	//I don't know what to do
		List<Variable> parents = tableVariables.subList(1, tablePotential.getNumVariables());

		EvidenceCase evidence = new EvidenceCase();

		int[] parentsConfiguration = new int[parents.size()];

		/*
		 * If there is no eventTablePotential, an exception is shown (caught) and startPosition=0
		 */
		int startPosition = tablePotentialsPanelOperations.getPotentialStartIndexOfColumn(col, tablePotential);

		// gets the configuration of startPosition--> the data position in tablePotential corresponding to
		// the beginning of the column
		// I suppose configuration=[Node Variable, parent_1,----,parent_n]
		int[] configuration = tablePotential.getConfiguration(startPosition);

		// Extracts the configuration of the parents from configuration
		// It is the same for every cell of the selected column

		for (int i = configuration.length - 1; i > 0; i--) {
			parentsConfiguration[i - 1] = configuration[i];
		}

		// Gets the evidence
		int j = 0;
		// Adds to evidence a finding containing the parent and its configuration
		Finding finding;
		for (Variable var : parents) {
			finding = new Finding(var, parentsConfiguration[j]);
			evidence.addFinding(finding);
			j++;
		}
		return evidence;
	}

	/**
	 * This method gets the Evidence Case from the selected column
	 *
	 * @return Evidence case
	 */
	public EvidenceCase getEvidenceCaseFromSelectedColumn() {
		EvidenceCase evi = null;
		try {
			evi = getConfiguration(selectedColumn);
		} catch (InvalidStateException | IncompatibleEvidenceException e) {
			e.printStackTrace();
		}
		return evi;
	}

	/**
	 * Creates and shows the UncertainValuesDialog object
	 *
	 * @throws WrongCriterionException revised-->minor changes
	 */
	public void showUncertaintyDialog() throws WrongCriterionException {
//		 Generates the evidenceCase based on the column
//		 selected on the JTable object
		//TODO REVIEW
		int position = tablePotentialsPanelOperations.getPotentialStartIndexOfColumn(selectedColumn, tablePotential);

		AssignUncertainTteDialog uncertDialog = new AssignUncertainTteDialog(Utilities.getOwner(this), (EventTimeTablePotential) eventTablePotential, position);

		int button = uncertDialog.requestUncertainValues();
		if (button == UncertainValuesDialog.OK_BUTTON) {
			UncertainTteEdit uncertEdit = null;
			try {
				uncertEdit = new UncertainTteEdit(node, uncertDialog.getUncertainColumn(),
						uncertDialog.getValuesColumn(), position, selectedColumn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				node.getProbNet().doEdit(uncertEdit);
				if (selectedColumn > 0) {
					(
							(ValuesTableCellRenderer) getEventValuesTable().getDefaultRenderer(Double.class)
					).setMark(selectedColumn - 1);
					getEventValuesTable().repaint();
					this.getTableModel().setNotEditablePositions(getNotEditablePositions());
				}
			} catch (ConstraintViolationException | NonProjectablePotentialException | DoEditException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, stringDatabase.getString(e.getMessage()),
						stringDatabase.getString(e.getMessage()), JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * This method initialises eventValuesTable and defines that first two columns cannot be selected
	 *
	 * @return a new values table.
	 * revised-->not changed
	 */
	public EventValuesTable getEventValuesTable() {
		if (eventValuesTable == null) {
			eventValuesTable = new EventValuesTable(node, getTableModel(), modifiable);
			eventValuesTable.setName("PotentialsTablePanel.eventValuesTable");
		}
		return eventValuesTable;
	}

	/**
	 * This method initialises valuesTableScrollPane.
	 *
	 * @return a new values table scroll pane.
	 * revised-->not changed
	 */
	protected JScrollPane getValuesTableScrollPane() {
		if (valuesTableScrollPane == null) {
			valuesTableScrollPane = new JScrollPane();
			valuesTableScrollPane.setName("TablePotentialPanel.valuesTableScrollPane");
			valuesTableScrollPane.setViewportView(getEventValuesTable());
		}
		return valuesTableScrollPane;
	}

	/**
	 * special method to show/hide the values table
	 * revised-->not changed
	 */
	public void showValuesTable(final boolean visible) {
		getEventValuesTable().setVisible(visible);
	}

	/**
	 * This method returns the eventValuesTableModel of eventValuesTable. If eventValuesTable has not a eventValuesTableModel, this method creates one.
	 *
	 * @return the eventValuesTableModel of eventValuesTable.
	 * @see EventValuesTable
	 * revised-->minor changes
	 */
	protected EventValuesTableModel getTableModel() {
		EventValuesTableModel tableModel = null;
		if ((eventValuesTable == null) || (eventValuesTable.getEventValuesTableModel() == null))
			tableModel = new EventValuesTableModel(data, columns, firstEditableRow);
		else
			tableModel = (EventValuesTableModel) eventValuesTable.getModel();

		return tableModel;
	}

	/**
	 * Show/Hide all the parameters
	 *
	 * @param showAllParameters the showAllParameters to set
	 */
	public void setShowAllParameters(boolean showAllParameters) {
		this.showAllParameters = showAllParameters;
		eventValuesTable.setShowingAllParameters(showAllParameters);
	}

	/**
	 * Handles an action performed
	 * revised-->not changed
	 */
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if (actionCommand.equals(ActionCommands.UNCERTAINTY_ASSIGN) || actionCommand
				.equals(ActionCommands.UNCERTAINTY_EDIT)) {
			try {
				showUncertaintyDialog();
			} catch (WrongCriterionException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this, stringDatabase.getString(e1.getMessage()),
						stringDatabase.getString(e1.getMessage()), JOptionPane.ERROR_MESSAGE);
			}
		} else if (actionCommand.equals(ActionCommands.UNCERTAINTY_REMOVE)) {
			try {
				removeUncertainty();
			} catch (WrongCriterionException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this, stringDatabase.getString(e1.getMessage()),
						stringDatabase.getString(e1.getMessage()), JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Method for removing the uncertain values for a certain configuration
	 *
	 * @throws WrongCriterionException revised-->minor changes; only changed the call to getNotEditablePositions
	 */
	public void removeUncertainty() throws WrongCriterionException {
		evidenceCase = getEvidenceCaseFromSelectedColumn();
		UncertainTteRemoveEdit uncertEdit = new UncertainTteRemoveEdit(node, evidenceCase);
		try {
			node.getProbNet().doEdit(uncertEdit);
			if (selectedColumn > 0) {
				(
						(ValuesTableCellRenderer) getEventValuesTable().getDefaultRenderer(Double.class)
				).unMark(selectedColumn - 1);
				getEventValuesTable().repaint();
				this.getTableModel().setNotEditablePositions(getNotEditablePositions());
			}
		} catch (ConstraintViolationException | NonProjectablePotentialException | DoEditException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, stringDatabase.getString(e.getMessage()),
					stringDatabase.getString(e.getMessage()), JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Method for update the options showed in the contextual menu
	 * revised-->not changed
	 */
	protected void updateContextualMenuOptions() {
		if (node.getPotentials().size() > 0 && node.getPotentials().get(0) instanceof TablePotential) {
			TablePotential tablePotential = (TablePotential) node.getPotentials().get(0);
			boolean hasUncertainty = tablePotential.hasUncertainty(getEvidenceCaseFromSelectedColumn());
			if (hasUncertainty) {
				getUncertaintyContextualMenu().getJComponentActionCommand(ActionCommands.UNCERTAINTY_ASSIGN.toString())
						.setEnabled(false);
				getUncertaintyContextualMenu().getJComponentActionCommand(ActionCommands.UNCERTAINTY_EDIT.toString())
						.setEnabled(true);
				getUncertaintyContextualMenu().getJComponentActionCommand(ActionCommands.UNCERTAINTY_REMOVE.toString())
						.setEnabled(true);
			} else {
				getUncertaintyContextualMenu().getJComponentActionCommand(ActionCommands.UNCERTAINTY_ASSIGN.toString())
						.setEnabled(true);
				getUncertaintyContextualMenu().getJComponentActionCommand(ActionCommands.UNCERTAINTY_EDIT.toString())
						.setEnabled(false);
				getUncertaintyContextualMenu().getJComponentActionCommand(ActionCommands.UNCERTAINTY_REMOVE.toString())
						.setEnabled(false);
			}
		}
	}

	/**
	 * Handles the double click in a cell
	 *
	 * @param evt
	 */
	protected void doubleClickEvent(MouseEvent evt) {
		if (node.getPotentials().size() > 0 && node.getPotentials().get(0) instanceof TablePotential) {
			TablePotential tablePotential = (TablePotential) node.getPotentials().get(0);

			EvidenceCase configuration = null;
			int selectedColumn = eventValuesTable.columnAtPoint(evt.getPoint());
			try {
				configuration = getConfiguration(selectedColumn);
			} catch (InvalidStateException | IncompatibleEvidenceException e) {
				e.printStackTrace();
			}
			boolean hasUncertainty = tablePotential.hasUncertainty(configuration);
			if (hasUncertainty) {
				try {
					showUncertaintyDialog();
				} catch (WrongCriterionException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, stringDatabase.getString(e1.getMessage()),
							stringDatabase.getString(e1.getMessage()), JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/**
	 * This method initialises uncertaintyContextualMenu.
	 *
	 * @return the node contextual menu.
	 * revised-->not changed
	 */
	protected UncertaintyContextualMenu getUncertaintyContextualMenu() {
		if (uncertaintyContextualMenu == null) {
			uncertaintyContextualMenu = new UncertaintyContextualMenu(this);
			uncertaintyContextualMenu.setName("uncertaintyContextualMenu");
		}
		return uncertaintyContextualMenu;
	}

	/**
	 * This method sets renders for the cells in the table. Only has to be called when it sets
	 * data.
	 * It is always used when eventTablePotential!=null
	 * <p>
	 * In a DECISION node a change is colored in green
	 * <p>
	 * UNCLEAR--> When ReadOnly is se?
	 * <p>
	 * NodeType.DECISION + policyType.OPTIMAL +!eventTablePotential.isUtility()
	 *
	 * @param uncertaintyInColumns
	 * @author carmenyago
	 */
	protected void setCellRenderers(boolean[] uncertaintyInColumns) {

		TableCellRenderer cellRenderer = null;

		if (node.getNodeType() != NodeType.DECISION) {
			// Creates the TableCellRenderer distinguishing if the node has or not link restrictions
			if (!hasLinkRestriction) {
				cellRenderer = new ValuesTableCellRenderer(firstEditableRow, uncertaintyInColumns);
			} else {
				cellRenderer = new ValuesTableWithLinkRestrictionCellRenderer(firstEditableRow, uncertaintyInColumns);
			}

		} else { // node.getNodeType() == NodeType.DECISION)
			if ((node.getPolicyType() == PolicyType.OPTIMAL) && (
					node.getPotentials().isEmpty() || (
							!node.getPotentials().get(0).isAdditive()
					)
			))

			{
				// UNCLEAR--> When ReadOnly is se?
				// A node has policy if is a decision node with a non uniform eventTablePotential
				boolean imposingPolicyByUser = node.hasPolicy() && !isReadOnly();
				cellRenderer = new ValuesTableOptimalPolicyCellRenderer(firstEditableRow, uncertaintyInColumns,
						imposingPolicyByUser);
			} else {
				boolean showingOptimalPolicy = node.getPotentials().get(0).isAdditive() && isReadOnly();
				if (!showingOptimalPolicy) {
					cellRenderer = new ValuesTableCellRenderer(firstEditableRow, uncertaintyInColumns);
				} else {
					// When showing the expected utility we want the color of the cells to be green
					cellRenderer = new ValuesTableOptimalPolicyCellRenderer(firstEditableRow, uncertaintyInColumns,
							true);
				}
			}
		}
		eventValuesTable.setDefaultRenderer(Double.class, cellRenderer);
		eventValuesTable.setDefaultRenderer(String.class, cellRenderer);
	}

	/**
	 * Method to define the specific listeners in this table (not defined in the
	 * common KeyTable hierarchy. This method creates the evidenceCase object
	 * when the user do right click on the table.
	 */
	protected void setTableSpecificListeners() {
		eventValuesTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int row = eventValuesTable.rowAtPoint(e.getPoint());
				int col = eventValuesTable.columnAtPoint(e.getPoint());
				selectedColumn = col;
				if (SwingUtilities.isLeftMouseButton(e)) {
					eventValuesTable
							.editCellAt(eventValuesTable.rowAtPoint(e.getPoint()), eventValuesTable.columnAtPoint(e.getPoint()),
									e);
				}
				if (SwingUtilities.isRightMouseButton(e)) {
					if ((row > -1) && (col > 0) && !isReadOnly()) {
						if (getUncertaintyContextualMenu() != null) {
							updateContextualMenuOptions();
							getUncertaintyContextualMenu().show(eventValuesTable, e.getX(), e.getY());
						}
					}
				}
			}

		});
		eventValuesTable.addMouseListener(new DoubleClickListener());
	}

	/**
	 * Close the table
	 * revised-->not changed
	 */
	@Override public void close() {
		getEventValuesTable().close();
	}

	/**
	 * This method sets the attributes this.readOnly= readOnly and modifiable = !readOnly to indicate
	 * if the table is read only (readOnly=true) or editable (readOnly = false).
	 * It also changes the cell renderer according to readOnly
	 *
	 * @param readOnly - if true, all the table cells become not editable, if false the data cells become editable
	 *                 revised-->minor changes; only changed the call to getUncertaintyInColumns
	 */
	@Override public void setReadOnly(boolean readOnly) {
		boolean wasReadOnly = super.isReadOnly();
		super.setReadOnly(readOnly);
		/*
		The read only attribute is set after the constructor is invoked and then,
		after the setData(node) method is called. Thus, the cell renderer may need to be changed.
		This is the case if the new read only value is different from the previous one.
		 */
		if (wasReadOnly != readOnly) {
			boolean[] uncertaintyInColumns = null;
			if (node.getPotentials() != null) {
				uncertaintyInColumns = getUncertaintyInColumns();
				setCellRenderers(uncertaintyInColumns);
			} else {
				setCellRenderers(uncertaintyInColumns);
			}
		}
		getEventValuesTable().setModifiable(!readOnly);
	}

	/**
	 * This class overrides the double click listener calling the
	 *
	 * @see DoubleClickListener
	 * revised-->not changed
	 */
	public class DoubleClickListener extends MouseAdapter {

		@Override public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				doubleClickEvent(e);
			}
		}
	}

}




