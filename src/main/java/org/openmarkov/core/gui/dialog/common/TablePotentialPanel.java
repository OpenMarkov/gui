/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.common;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.openmarkov.core.action.UncertainValuesEdit;
import org.openmarkov.core.action.UncertainValuesRemoveEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NullListPotentialsException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.component.PotentialsTablePanelOperations;
import org.openmarkov.core.gui.component.ValuesTable;
import org.openmarkov.core.gui.component.ValuesTableCellRenderer;
import org.openmarkov.core.gui.component.ValuesTableModel;
import org.openmarkov.core.gui.component.ValuesTableOptimalPolicyCellRenderer;
import org.openmarkov.core.gui.component.ValuesTableWithLinkRestrictionCellRenderer;
import org.openmarkov.core.gui.dialog.node.UncertainValuesDialog;
import org.openmarkov.core.gui.menutoolbar.common.ActionCommands;
import org.openmarkov.core.gui.menutoolbar.menu.UncertaintyContextualMenu;
import org.openmarkov.core.gui.util.Utilities;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.PolicyType;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.UtilStrings;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;
import org.openmarkov.core.model.network.potential.operation.LinkRestrictionPotentialOperations;

/**
 * This class implements a Table potential table with the following features:
 * <li>Its elements, except the first column, are modifiable.</li> <li>New
 * elements can be added, creating a new key row with empty data.</li> <li>The
 * key data (first column) consist of a key string following of the index of the
 * row and it is used for internal purposes only.</li> <li>The key data is
 * hidden.</li> <li>The information of a row (except the first column) can not
 * be taken up or down.</li> <li>The rows can not be removed.</li> <li>The first
 * editable row is the one that has the values of the potentials.</li> <li>The
 * rows between 0 and the first editable row are ocuppied by the values of the
 * states of the parents of the variable.</li> <li>The header of columns is
 * hidden.</li>
 * 
 * @author jlgozalo
 * @author myebra
 * 
 */
@SuppressWarnings("serial")
@PotentialPanelPlugin(potentialType = "Table")
public class TablePotentialPanel extends ProbabilityTablePanel {

	protected Logger logger;
	/**
	 * JTable where show the values.
	 */
	protected ValuesTable valuesTable = null;
	/**
	 * Indicates if the data of the table is modifiable.
	 */
	private boolean modifiable;

	/**
	 * Panel to scroll the table.
	 */
	protected JScrollPane valuesTableScrollPane = null;

	protected ProbNode probNode;

	protected boolean hasLinkRestriction;

	/**
	 * Constructor use by CPTablePanel
	 * 
	 * @param probNode
	 */

	public TablePotentialPanel(ProbNode probNode) {
		super();
		this.probNode = probNode;
		modifiable = true;
		showValuesTable(true);
		setTableSpecificListeners();
		setData(probNode);
		setLayout(new BorderLayout());
		add(getValuesTableScrollPane(), BorderLayout.CENTER);
		add(getCommentHTMLScrollPaneNodeDefinitionComment(), BorderLayout.SOUTH);
		repaint();
		//add(getCommentHTMLScrollPaneNodeDefinitionComment(),BorderLayout.SOUTH);
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Method to define the specific listeners in this table (not defined in the
	 * common KeyTable hierarchy. This method creates the evidenceCase object
	 * when the user do right click on the table.
	 */

	private UncertaintyContextualMenu uncertaintyContextualMenu;

	/**
	 * Sets a new table model with new data.
	 * 
	 * @param newData
	 *            new data for the table.
	 */
	public void setData(Object[][] newData) {

		setData(newData, columns, 0, 0, NodeType.CHANCE);
	}

	/**
	 * Sets a new table model with new data and new columns
	 * 
	 * @param newData
	 *            new data for the table
	 * @param newColumns
	 *            new columns for the table
	 */
	public void setData(Object[][] newData, String[] newColumns,
			int firstEditableRow, int lastEditableRow, NodeType nodeType) {

		showValuesTable(true);
		data = newData.clone();
		columns = newColumns.clone();
		this.firstEditableRow = firstEditableRow;
		this.lastEditableRow = lastEditableRow;
		valuesTable.resetModel();

		// valuesTable.setVariable(probNode.getPotentials().get( 0
		// ).getVariable( 0 ));

		valuesTable.setModel(getTableModel());
		valuesTable.initializeDataModified(false);
		((ValuesTableModel) valuesTable.getModel())
				.setFirstEditableRow(firstEditableRow);
		valuesTable.setLastEditableRow(lastEditableRow);
		valuesTable.setShowingAllParameters(true);
		valuesTable.setNodeType(nodeType);

	}

	/**
	 * Sets a new table model with new data and new columns based on three
	 * items: <li>list of Potentials of the variable</li> <li>states of the
	 * variable</li> <li>parents of the variable</li>
	 * 
	 * @param listPotentials
	 *            - the list of potentials of the variable
	 * @param variableName
	 *            - name of the variable
	 * @param variableStates
	 *            - states of the variable
	 * @param parents
	 *            - parents of the variable
	 */
	public void setData(ProbNode probNode) {
		this.probNode = probNode;
		hasLinkRestriction = LinkRestrictionPotentialOperations
				.hasLinkRestriction(probNode);
		valuesTable.setData(probNode);
		Object[][] tableData = null;
		String[] newColumns = null;
		if (probNode.getPotentials() != null) {
			// listPotentials =
			// PotentialsTablePanelOperations.checkIfPotentialsMustBeChanged(listPotentials,
			// additionalProperties);
			// setListPotentials(probNode.getPotentials());
			// tableData =
			// convertListPotentialsToTableFormat( listPotentials,
			// additionalProperties );
			tableData = convertListPotentialsToTableFormat(probNode);
			newColumns = ValuesTable.getColumnsIdsSpreedSheetStyle(ValuesTable
					.howManyColumns(probNode));
			setFirstEditableRow(PotentialsTablePanelOperations
					.calculateFirstEditableRow(probNode.getPotentials(),
							probNode));
			setLastEditableRow(PotentialsTablePanelOperations
					.calculateLastEditableRow(probNode.getPotentials(),
							probNode));
			setData(tableData, newColumns, firstEditableRow, lastEditableRow,
					probNode.getNodeType());
			setCellRenderers();
			if (hasLinkRestriction) {
				if(probNode.getNodeType()==NodeType.CHANCE)
				this.getTableModel().setNotEditablePositions(
						getNotEditablePositions());
			}
		} else {
			tableData = new Object[0][0];
			setFirstEditableRow(0);
			setData(tableData);
			setCellRenderers();
		}
	}

	/**
	 * calculate the number of rows of the table based on the type of the node,
	 * the number of parents and the number of states of the variable
	 * 
	 * @param additionalProperties
	 *            - node additionalProperties
	 * @return the number of rows of this Potentials Table
	 */
	protected int howManyRows(ProbNode properties) {

		int numRows = 0;
		if (properties.getNode().getParents() != null) {
			numRows = properties.getNode().getParents().size();
		}
		if (properties.getNodeType() == NodeType.UTILITY) {
			numRows += 1;
		} else {
			if (properties.getVariable().getStates() != null) {
				numRows = numRows + properties.getVariable().getStates().length;
			}
		}
		return numRows;
	}

	/**
	 * Set a blank data table
	 * 
	 * @param additionalProperties
	 *            - to obtain the required number of rows and columns
	 * @return the blank data table
	 */
	private Object[][] setBlankTable(ProbNode properties) {

		Object[][] blankTable = null;
		int numRows = howManyRows(properties);
		int numColumns = ValuesTable.howManyColumns(properties);
		blankTable = new Object[numRows][numColumns];
		// TODO seria mas practico hacer un potential y luego ejecutar
		// el resto del metodo pero esto funciona
		for (int i = 0; i < properties.getVariable().getStates().length; i++) {

		}

		return blankTable;
	}

	/**
	 * to retrieve the ListPotentials corresponding to the data in the table
	 * 
	 * @return
	 */
	public ArrayList<Potential> getListPotentialsFromData() {

		ArrayList<Potential> result = null;
		result = convertTableFormatToListPotentials(valuesTable);
		// setListPotentials(result);
		return result;
	}

	private TablePotential getThisPotential(List<Potential> listPotentials) {

	    TablePotential aPotential = null;
		try {
			aPotential = ((TablePotential) listPotentials.get(0));
		} catch (Exception ex) {
			// ExceptionsHandler.handleException(
			// ex, "no Potential.get(0) !!!", false );
			logger.error("no Potential.get(0) !!!");

		}
		return aPotential;
	}

	/**
	 * Prepare the table data from the <code>Potential</code>s and States.
	 * <p>
	 * If the Potential is null, then the information is taken from the
	 * <code>NodeProperties</code>
	 * 
	 * @param listPotentials
	 *            - potentials of the table
	 * @param states
	 *            - states of the variable of this node
	 * @param parents
	 *            - <code>NodeWrapper</code> list of the parents
	 * @return the table data to be set
	 */
	protected Object[][] convertListPotentialsToTableFormat(ProbNode properties) {
		Object[][] values = null;
		try {
			// mpal
			PotentialsTablePanelOperations.checkIfNoPotential(properties
					.getPotentials());
			values = setValuesTableSize(values, properties);
			values = setParentsNameInUpperLeftCornerArea(values, properties);
			values = setParentsStatesInTopArea(values, properties);
			values = setNodeStatesInLeftArea(values, properties);
			values = setPotentialDataInCentreArea(values, properties);
			if (probNode.getNodeType() != NodeType.UTILITY) {
				values = setVariableNameInLowerLeftCornerArea(values,
						properties);
				values = setVariableStatesInBottomArea(values, properties);
			}
			setPosition(setNumberOfPostions(properties.getPotentials()));
		} catch (NullListPotentialsException ex) {
			values = setBlankTable(properties);
		}
		return values;
	}

	/**
	 * set values table size for the potential
	 * 
	 * @param values
	 *            - the table that is being modified
	 * @param listPotentials
	 *            - the list of potentials of the node
	 * @param additionalProperties
	 *            - the additionalProperties of the node
	 */

	private Object[][] setValuesTableSize(Object[][] oldValues,
			ProbNode properties) {
		Object[][] values = oldValues;
		int numRows = 0;
		int numColumns = 1; // at least, there is one column for the node names
		int row = PotentialsTablePanelOperations.calculateFirstEditableRow(
				properties.getPotentials(), properties);
		setBaseIndexForCoordinates(row);
		setFirstEditableRow(row);
		TablePotential tablePotential = getThisPotential(properties.getPotentials());
		List<Variable> variablesBeforeReorder = tablePotential.getVariables();

		setVariables(variablesBeforeReorder);
		if (properties.getNodeType() == NodeType.UTILITY) {
			setBaseIndexForCoordinates(row - 1);
			numRows = getVariables().size();
			setLastEditableRow(numRows - 1);
			// numRows++;
			if (tablePotential.getTableSize() == 0)
				numColumns++;
			else
				numColumns += tablePotential.getTableSize();
		} else {
			int numDimensions = tablePotential.getDimensions()[0];// number of
																	// states of
																	// the
																	// conditioned
																	// variable
			numRows = getVariables().size() - 1 + numDimensions; // parents +
																	// variableStates
			setLastEditableRow(numRows - 1);
			numRows = numRows + 1; // + 1 for variableValues (when used in show
									// as Values
			if (numDimensions == 0) {
				// do nothing??
			} else { // all table div by variable states
				numColumns = numColumns
						+ (tablePotential.getTableSize() / numDimensions);
			}
		}
		// create the array of arrays
		values = new Object[numRows][numColumns];
		return values;
	}

	private void setVariables(List<Variable> variables) {
		// TODO update this statement, when constructor of this class with
		// potential as parameter is implemented
		if (probNode != null && probNode.getNodeType() == NodeType.UTILITY) {
			this.variables = new ArrayList<Variable>();
			this.variables.add(probNode.getVariable());
			for (Variable variable : variables)
				this.variables.add(variable);
		} else

			this.variables = variables;

	}

	/**
	 * This methods fills the Upper Left corner of the table with the name of
	 * the parents of the node
	 * 
	 * @param values
	 *            - the table that is being modified
	 * @param additionalProperties
	 *            - the additionalProperties of the node
	 */
	private Object[][] setParentsNameInUpperLeftCornerArea(
			Object[][] oldValues, ProbNode properties) {

		Object[][] values = oldValues;
		ArrayList<Variable> listParents = new ArrayList<Variable>();
		for (Variable variable : getVariables()) {
			if (!variable.getName().equals(properties.getName())) {
				listParents.add(variable);
			}
		}

		if ((listParents != null) && (listParents.size() > 0)) {
			for (int i = 0; i < listParents.size(); i++) {
				values[i][0] = listParents.get(i);
			}
		}
		return values;
	}

	/**
	 * @param values
	 *            - the table that is being modified
	 * @param listPotentials
	 *            - the list of potentials of the node
	 * @param additionalProperties
	 *            - the additionalProperties of the node
	 */
	private Object[][] setParentsStatesInTopArea(Object[][] oldValues,
			ProbNode properties) {

		Object[][] values = oldValues;
		TablePotential tablePotential = (TablePotential) getThisPotential(properties
				.getPotentials());
		ArrayList<Variable> variablesReordered = new ArrayList<Variable>();
		ListIterator<Variable> it = getVariables().listIterator(
				getVariables().size());
		while (it.hasPrevious()) {
			variablesReordered.add((Variable) it.previous());
		}
		/*
		 * try { tablePotential = DiscretePotentialOperations.reorder(
		 * tablePotential, variablesReordered ); } catch
		 * (NotEnoughMemoryException exception) {
		 * ExceptionsHandler.handleException( exception, "not enougth memory",
		 * true ); }
		 */

		int numColumns = (values.length == 0 ? 0 : values[0].length);
		State[] states;

		// 07/07/2010 mpalacios
		int accumulateStates = 1;
		int numStates;
		int numberOfVariables = variablesReordered.size();
		for (int row = 0; row < numberOfVariables - 1; row++) {
			numStates = variablesReordered.get(row).getNumStates();
			states = variablesReordered.get(row).getStates();
			// states = tablePotential.getVariable(row).getStates();
			int col = 1;
			while (col < numColumns) {
				for (State state : states) {
					for (int i = 1; i <= accumulateStates; i++) {
						values[numberOfVariables - row - 2][col] = state
								.getName();
						col++;
					}

				}
			}
			accumulateStates *= numStates;
		}

		return values;
	}

	/**
	 * @param values
	 *            - the table that is being modified
	 * @param listPotentials
	 *            - the list of potentials of the node
	 * @param additionalProperties
	 *            - the additionalProperties of the node
	 */
	private int setNumberOfPostions(List<Potential> listPotentials) {

		int numPositions = 1;
		try {
			for (Variable variable : listPotentials.get(0).getVariables()) {
				numPositions = numPositions * variable.getNumStates();
			}
		} catch (NullPointerException exception) {
			numPositions = 0;
			// ExceptionsHandler.handleException(
			// exception, "not enougth memory", false );
			logger.error("not enougth memory");
		}
		setPosition(numPositions);
		return numPositions;

	}

	/**
	 * this method sets the first row with the values of the states of the node
	 * (if it is a node chance) or the name of the variable of the node (if it
	 * is a utility node)
	 * 
	 * 
	 * @param values
	 *            - the table that is being modified
	 * @param listPotentials
	 *            - the list of potentials of the node
	 * @param additionalProperties
	 *            - the additionalProperties of the node
	 */
	private Object[][] setNodeStatesInLeftArea(Object[][] oldValues,
			ProbNode properties) {

		Object[][] values = oldValues;
		TablePotential tablePotential = (TablePotential) getThisPotential(properties
				.getPotentials());
		int row = getFirstEditableRow();
		if (properties.getNodeType() == NodeType.UTILITY) {
			values[row][0] = properties.getName();
		} else /* if (properties.getNodeType() == NodeType.CHANCE) */{
			// set first column values with the state names
			if (0 < tablePotential.getDimensions()[0]) {
				// int numOfTheState =
				// tablePotential.getVariable( 0 ).getNumStates() - 1;
				int length = values.length - 2;
				for (State state : tablePotential.getVariable(0).getStates()) {
					values[length--][0] = state.getName();
					// row++;
					// numOfTheState--;
				}
			}
		}
		return values;

	}

	/**
	 * @param values
	 *            - the table that is being modified
	 * @param listPotentials
	 *            - the list of potentials of the node
	 * @param additionalProperties
	 *            - the additionalProperties of the node
	 */
	private Object[][] setPotentialDataInCentreArea(Object[][] oldValues,
			ProbNode properties) {

		Object[][] values = oldValues;
		int position = 0;
		int numColumns = (values.length == 0 ? 0 : values[0].length);
		TablePotential tablePotential = (TablePotential) getThisPotential(properties
				.getPotentials());

		// rounding initial values
		double[] initialValues = tablePotential.getValues();
		double[] roundedValues = new double[initialValues.length];

		int maxDecimals = 10;
		double epsilon;
		epsilon = Math.pow(10, -(maxDecimals + 2));
		for (int i = 0; i < initialValues.length; i++) {
			roundedValues[i] = UtilStrings.roundAndReduce(initialValues[i],
					epsilon, maxDecimals);
		}

		tablePotential.setValues(roundedValues);

		List<Variable> newOrderVariables = new ArrayList<Variable>();
		List<Variable> variables = probNode.getPotentials().get(0).getVariables();
		// Collections.reverse(variables); // reorder the variables
		int end = -1;
		if (variables.size() > 0) {
			if (!(probNode.getNodeType() == NodeType.UTILITY)) {
				newOrderVariables.add(variables.get(0));
				end = 0;
			}
			for (int i = variables.size() - 1; i > end; i--) {
				newOrderVariables.add(variables.get(i));

			}

		}

		tablePotential = DiscretePotentialOperations.reorder(tablePotential,
				newOrderVariables);

		/*
		 * for (int i = getLastEditableRow(); i >= getFirstEditableRow(); i--) {
		 * for (int j = numColumns - 1; j >= 1; j--, position++) { double value
		 * = tablePotential.getTable()[position]; values[i][j] = value; } }
		 */
		int cont = getLastEditableRow();
		/*
		 * if (probNode.getNodeType() == NodeType.UTILITY ) cont =
		 * getLastEditableRow()-1; else cont = getLastEditableRow();
		 */

		for (int j = 1; j <= numColumns - 1; j++) {
			for (int i = cont; i >= getFirstEditableRow(); i--, position++) {
				double value = tablePotential.getValues()[position];

				values[i][j] = value;

			}
		}
		return values;
	}

	/****
	 * Calculates the position on the dataTable for a state combination
	 * 
	 * @param stateIndices
	 *            - indexes of the states
	 * 
	 * @return an array containing the row at the first position and the column
	 *         at the second position.
	 */

	private int[] getRowAndColumnForStateCombination(int[] stateIndices,
			TablePotential potential) {

		int numStates = probNode.getVariable().getNumStates();
		int position = potential.getPosition(stateIndices);
		int column = (position / numStates) + 1;
		int row = getLastEditableRow() - (position % numStates);
		return new int[] { row, column };
	}

	/****
	 * Calculates the positions of the table which are not editable due to a
	 * link restriction. If the position is not editable it contains the value
	 * 1, otherwise it contains a null value.
	 * 
	 * @return a two dimensional array with the size of the table containing the
	 *         information about the editable positions.
	 */
	private Object[][] getNotEditablePositions() {
		Object[][] notEditablePositions = null;
		notEditablePositions = setValuesTableSize(notEditablePositions,
				probNode);
		List<int[]> statesWithRestriction = LinkRestrictionPotentialOperations
				.getStateCombinationsWithLinkRestriction(probNode);

		TablePotential potential = (TablePotential) probNode.getPotentials()
				.get(0);
		List<Variable> newOrderVariables = new ArrayList<Variable>();
		List<Variable> variables = probNode.getPotentials().get(0)
				.getVariables();

		int end = -1;
		if (variables.size() > 0) {
			newOrderVariables.add(variables.get(0));
			end = 0;

			for (int i = variables.size() - 1; i > end; i--) {
				newOrderVariables.add(variables.get(i));
			}
		}
		potential = DiscretePotentialOperations.reorder(potential,
				newOrderVariables);

		for (int[] state : statesWithRestriction) {
			// reorder the variables
			int[] reordedState = new int[state.length];
			reordedState[0] = state[0];
			for (int i = 1; i < state.length; i++) {
				reordedState[state.length - i] = state[i];
			}

			int[] position = getRowAndColumnForStateCombination(reordedState,
					potential);
			int row = position[0];
			int column = position[1];
			notEditablePositions[row][column] = 1;
		}
		return notEditablePositions;

	}

	/**
	 * In the lower left corner area, the last row is reserved in the model for
	 * displaying the name of the variable
	 * 
	 * 
	 * @param values
	 *            - the table that is being modified
	 * @param listPotentials
	 *            - the list of potentials of the node
	 * @param additionalProperties
	 *            - the additionalProperties of the node
	 */
	private Object[][] setVariableNameInLowerLeftCornerArea(
			Object[][] oldValues, ProbNode properties) {
		Object[][] values = oldValues;
		values[getLastEditableRow() + 1][0] = properties.getName();
		return values;

	}

	/**
	 * In a discretize table model that shows only values (not probabilities),
	 * this area will store the name of the state that is required to display
	 * 
	 * @param values
	 *            - the table that is being modified
	 * @param additionalProperties
	 *            - the additionalProperties of the node
	 */
	private Object[][] setVariableStatesInBottomArea(Object[][] oldValues,
			ProbNode properties) {

		Object[][] values = oldValues;
		int position = 0;
		int numColumns = (values.length == 0 ? 0 : values[0].length);
		TablePotential tablePotential = (TablePotential) getThisPotential(properties
				.getPotentials());
		State[] states = tablePotential.getVariable(0).getStates();
		double max;
		for (int j = numColumns - 1; j >= 1; j--, position++) {
			max = (Double) values[getFirstEditableRow()][j];
			values[getLastEditableRow() + 1][j] = states[0].getName();
			for (int i = getFirstEditableRow() + 1; i <= getLastEditableRow(); i++) {
				if (((Double) values[i][j]) > max) {
					max = (Double) values[i][j];
					values[getLastEditableRow() + 1][j] = states[i
							- getFirstEditableRow()].getName();
				}
			}
		}
		return values;
	}

	/**
	 * Convert the table with the data in a List of Potentials to be saved
	 * 
	 * @param valuesTable
	 *            - the table with the data
	 * @return a list of Potentials
	 */
	private ArrayList<Potential> convertTableFormatToListPotentials(
			ValuesTable valuesTable) {

		ArrayList<Potential> listPotentials = new ArrayList<Potential>();
		if (getPosition() >= 0) { // it is not a Decision node
			double[] table = new double[getPosition()];
			TablePotential tablePotential = null;
			int position = 0;
			for (int j = valuesTable.getColumnCount() - 1; j > 0; j--) {
				for (int i = valuesTable.getLastEditableRow() - 1; i >= getFirstEditableRow(); i--, position++) {
					table[position] = (Double) valuesTable.getModel()
							.getValueAt(i, j);
				}
			}
			tablePotential = new TablePotential(getVariables(),
					PotentialRole.CONDITIONAL_PROBABILITY, table);
			listPotentials.add(tablePotential);
		}
		return listPotentials;
	}

	/**
	 * This method generates the evidenceCase based on the column selected on
	 * the <code>valuesTable</code> object.
	 * 
	 * @param tablePotential
	 *            The TablePotential object edited
	 * @param col
	 *            The column selected. Never is 0 , because the column 0 is the
	 *            states column
	 * @return An evidence case object
	 * @throws InvalidStateException
	 * @throws IncompatibleEvidenceException
	 */

	private EvidenceCase getConfiguration(TablePotential tablePotential, int col)
			throws InvalidStateException, IncompatibleEvidenceException {
		Variable variable = null;
		
		EvidenceCase evidence = new EvidenceCase();
		// configuration of all variables

		if (tablePotential.getPotentialRole() == PotentialRole.UTILITY) {
			variable = tablePotential.getUtilityVariable();
			variables = tablePotential.getVariables();

		} else if (tablePotential.getPotentialRole() == PotentialRole.CONDITIONAL_PROBABILITY
				|| tablePotential.getPotentialRole() == PotentialRole.JOINT_PROBABILITY) { // JoinProbability
																							// when
																							// is
																							// getted
																							// from
																							// a
																							// iciPotential
			variable = tablePotential.getVariable(0);
			variables = tablePotential.getVariables();
			variables.remove(0);
		}

		int[] parentsConfiguration = new int[variables.size()];
		// Gets the start position of a reordered potential
		int startPosition = UtilStrings.toPositionOnPotentialReordered(
				variable.getNumStates() + variables.size() - 1, col,
				variable.getNumStates(), variables.size());
		int finalPosition = startPosition + variable.getNumStates() - 1;

		// the source variables are reordered
		ArrayList<Variable> reorderedVariables = new ArrayList<Variable>();
		if (!(tablePotential.getPotentialRole() == PotentialRole.UTILITY)) {
			reorderedVariables.add(variable);
		}

		for (int i = variables.size() - 1; i >= 0; i--) {
			reorderedVariables.add(variables.get(i));
		}
		// gets the potential with variables and values table reordered
		TablePotential reorderedTablePotential = DiscretePotentialOperations.reorder(
					tablePotential, reorderedVariables);
		// gets the configuration selected
		int[] configuration = reorderedTablePotential
				.getConfiguration(startPosition);

		// back to the original order of variables configuration
		// first value of configuration matches the value of the first variable
		// in inverse order because the potential visualization is in inverse
		// order
		int j = 0;
		int end = 0;
		if (tablePotential.getPotentialRole() == PotentialRole.UTILITY) {
			end = -1;
		}
		for (int i = configuration.length - 1; i > end; i--) {
			parentsConfiguration[j++] = configuration[i];
		}
		// Gets the evidence
		j = 0;
		Finding finding;
		for (Variable var : variables) {
			finding = new Finding(var, parentsConfiguration[j]);
			evidence.addFinding(finding);
			j++;
		}

		return evidence;
	}

	public EvidenceCase getEvidenceCaseFromSelectedColumn() {

		EvidenceCase evi = null;
		try {
			evi = getConfiguration((TablePotential) probNode.getPotentials()
					.get(0), selectedColumn);
		} catch (InvalidStateException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
		} catch (IncompatibleEvidenceException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
		}
		return evi;
	}

	/**
	 * Creates and shows the UncertainValuesDialog object
	 * 
	 * @throws WrongCriterionException
	 */
	public void showUncertaintyDialog() throws WrongCriterionException {
		// Generates the evidenceCase based on the column
		// selected on the JTable object
		evidenceCase = getEvidenceCaseFromSelectedColumn();


			UncertainValuesDialog uncertDialog = new UncertainValuesDialog(
					Utilities.getOwner(this), evidenceCase,
					(TablePotential) probNode.getPotentials().get(0));
			int button = uncertDialog.requestUncertainValues();
			if (button == UncertainValuesDialog.OK_BUTTON) {
				UncertainValuesEdit uncertEdit = new UncertainValuesEdit(
						probNode, uncertDialog.getUncertainColumn(),
						uncertDialog.getValuesColumn(),
						uncertDialog.getPosBase(), selectedColumn,
						uncertDialog.isChanceVariable());

				try {
					probNode.getProbNet().doEdit(uncertEdit);

					if (selectedColumn > 0) {
						((ValuesTableCellRenderer) getValuesTable()
								.getDefaultRenderer(Double.class))
								.setMark(selectedColumn - 1);
						getValuesTable().repaint();
					}
			} catch (ConstraintViolationException | CanNotDoEditException
					| NonProjectablePotentialException | DoEditException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, stringResource
							.getString( e.getMessage() ),
							stringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				}

			}

	}

	/**
	 * This method initializes valuesTable and defines that first two columns
	 * are not selectable
	 * 
	 * @return a new values table.
	 */
	public ValuesTable getValuesTable() {

		if (valuesTable == null) {
			valuesTable = new ValuesTable(probNode, getTableModel(), modifiable);
			valuesTable.setName("PotentialsTablePanel.valuesTable");
		}
		return valuesTable;
	}

	/**
	 * This method initializes valuesTableScrollPane.
	 * 
	 * @return a new values table scroll pane.
	 */
	protected JScrollPane getValuesTableScrollPane() {

		if (valuesTableScrollPane == null) {
			valuesTableScrollPane = new JScrollPane();
			valuesTableScrollPane
					.setName("TablePotentialPanel.valuesTableScrollPane");
			valuesTableScrollPane.setViewportView(getValuesTable());
			
		}
		return valuesTableScrollPane;
	}

	/**
	 * special method to show/hide the values table
	 */
	public void showValuesTable(final boolean visible) {

		getValuesTable().setVisible(visible);
	}

	/**
	 * This method initializes tableModel.
	 * 
	 * @return a new tableModel.
	 */
	protected ValuesTableModel getTableModel() {

		ValuesTableModel tableModel = null;
		if (valuesTable == null) {
			tableModel = new ValuesTableModel(data, columns, firstEditableRow);
		} else if (valuesTable.getTableModel() == null) {
			tableModel = new ValuesTableModel(data, columns, firstEditableRow);
		} else {
			tableModel = (ValuesTableModel) valuesTable.getModel();
		}
		return tableModel;
	}

	/**
	 * This method handles the type of potential to be used for the model to be
	 * deterministic
	 */
	public void setDeterministicModel() {

		valuesTable.setDeterministic(true);
		setShowAllParameters(true);
	}

	/**
	 * This method handles the type of potential to be used for the model to be
	 * probabilistic
	 */
	public void setProbabilisticModel() {

		valuesTable.setDeterministic(false);
		setShowAllParameters(true);
	}

	/**
	 * This method handles the type of potential to be used for the model to be
	 * optimal (decision node)
	 */
	public void setOptimalModel() {

		valuesTable.setShowingOptimal(true);
	}

	/**
	 * This method handles the type of potential to be used for the model to be
	 * general (TablePotential)
	 */
	public void setGeneralModel(int familyIndex) {

		valuesTable.setUsingGeneralPotential(familyIndex);

	}

	/**
	 * This method handles the type of potential to be used for the model to be
	 * canonical (ICIPotential)
	 */
	public void setCanonicalModel(int familyIndex) {

		valuesTable.setUsingGeneralPotential(familyIndex);
	}

	/**
	 * @param showAllParameters
	 *            the showAllParameters to set
	 */
	public void setShowAllParameters(boolean showAllParameters) {

		this.showAllParameters = showAllParameters;
		valuesTable.setShowingAllParameters(showAllParameters);
	}

	/**
	 * @param showProbabilitiesValues
	 *            the showProbabilitiesValues to set
	 */
	public void setShowProbabilitiesValues(boolean showProbabilitiesValues) {

		this.showProbabilitiesValues = showProbabilitiesValues;
		valuesTable.setShowingProbabilitiesValues(showProbabilitiesValues);
	}

	/**
	 * @param showTPCvalues
	 *            the showTPCvalues to set
	 */
	public void setShowTPCvalues(boolean showTPCvalues) {

		this.showTPCvalues = showTPCvalues;
		valuesTable.setShowingTPCvalues(showTPCvalues);
	}

	public void doUpdateVariableName(String oldName, String newName) {
		if (oldName.equals(this.getVariables().get(0).getName())) {
			// replace variable name in ArrayListVariables
			this.getVariables().get(0).setName(newName);
		}
		if (oldName.equals(probNode.getPotentials().get(0).getVariables()
				.get(0).getName())) {
			// replace variable name in the TablePotential
			probNode.getPotentials().get(0).getVariables().get(0)
					.setName(newName);
		}
		// replace variable name in the NodePotentialTable
		if (this.getValuesTable().getVariable() != null) {
			if (oldName.equals(this.getValuesTable().getVariable().getName())) {
				this.getValuesTable().getVariable().setName(newName);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if (actionCommand.equals(ActionCommands.UNCERTAINTY_ASSIGN)) {
			try {
				showUncertaintyDialog();
			} catch (WrongCriterionException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this, stringResource
						.getString( e1.getMessage() ),
						stringResource.getString( e1.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			}
		} else if (actionCommand.equals(ActionCommands.UNCERTAINTY_EDIT)) {
			try {
				showUncertaintyDialog();
			} catch (WrongCriterionException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this, stringResource
						.getString( e1.getMessage() ),
						stringResource.getString( e1.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			}
		} else if (actionCommand.equals(ActionCommands.UNCERTAINTY_REMOVE)) {
			try {
				removeUncertainty();
			} catch (WrongCriterionException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this, stringResource
						.getString( e1.getMessage() ),
						stringResource.getString( e1.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			}

		}

	}

	/**
	 * Method for removing the uncertain values for a certain configuration
	 * 
	 * @throws WrongCriterionException
	 * @throws NotEnoughMemoryException
	 */
	public void removeUncertainty() throws WrongCriterionException {

		evidenceCase = getEvidenceCaseFromSelectedColumn();
		UncertainValuesRemoveEdit uncertEdit = new UncertainValuesRemoveEdit(
				probNode, evidenceCase);

		try {
			probNode.getProbNet().doEdit(uncertEdit);

			if (selectedColumn > 0) {
				((ValuesTableCellRenderer) getValuesTable().getDefaultRenderer(
						Double.class)).unMark(selectedColumn - 1);
				getValuesTable().repaint();
			}

		} catch (ConstraintViolationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, stringResource
					.getString( e.getMessage() ),
					stringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		} catch (CanNotDoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, stringResource
					.getString( e.getMessage() ),
					stringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		} catch (DoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, stringResource
					.getString( e.getMessage() ),
					stringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		} catch (NonProjectablePotentialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, stringResource
					.getString( e.getMessage() ),
					stringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		}

	}

	private void updateContextualMenuOptions() {
		if (probNode.getPotentials().size() > 0
				&& probNode.getPotentials().get(0) instanceof TablePotential) {
			TablePotential tablePotential = (TablePotential) probNode
					.getPotentials().get(0);
			boolean hasUncertainty = tablePotential
					.hasUncertainty(getEvidenceCaseFromSelectedColumn());
			if (hasUncertainty) {
				getUncertaintyContextualMenu().getJComponentActionCommand(
						ActionCommands.UNCERTAINTY_ASSIGN.toString())
						.setEnabled(false);
				getUncertaintyContextualMenu().getJComponentActionCommand(
						ActionCommands.UNCERTAINTY_EDIT.toString()).setEnabled(
						true);
				getUncertaintyContextualMenu().getJComponentActionCommand(
						ActionCommands.UNCERTAINTY_REMOVE.toString())
						.setEnabled(true);
			} else {
				getUncertaintyContextualMenu().getJComponentActionCommand(
						ActionCommands.UNCERTAINTY_ASSIGN.toString())
						.setEnabled(true);
				getUncertaintyContextualMenu().getJComponentActionCommand(
						ActionCommands.UNCERTAINTY_EDIT.toString()).setEnabled(
						false);
				getUncertaintyContextualMenu().getJComponentActionCommand(
						ActionCommands.UNCERTAINTY_REMOVE.toString())
						.setEnabled(false);
			}
		}
	}

	/**
	 * This method initializes uncertaintyContextualMenu.
	 * 
	 * @return the node contextual menu.
	 */
	private UncertaintyContextualMenu getUncertaintyContextualMenu() {

		if (uncertaintyContextualMenu == null) {
			uncertaintyContextualMenu = new UncertaintyContextualMenu(this);
			uncertaintyContextualMenu.setName("uncertaintyContextualMenu");
		}
		return uncertaintyContextualMenu;
	}

	/**
	 * set renders for the cells in the table. Only has to be called when set
	 * data.
	 */
    protected void setCellRenderers ()
    {
        int size = valuesTable.getColumnCount ();
        boolean[] aux = new boolean[size - 1];
        boolean hasUncertainty;
        if (probNode.getPotentials ().size () > 0
        /* && probNode.getNodeType() != NodeType.DECISION */)
        {
            if (probNode.getNodeType () != NodeType.DECISION)
            {
                TablePotential tablePotential = (TablePotential) probNode.getPotentials ().get (0);
                for (int i = 1; i < size; i++)
                {
                    hasUncertainty = false;
                    try
                    {
                        hasUncertainty = tablePotential.hasUncertainty (getConfiguration (tablePotential,
                                                                                          i));
                    }
                    catch (InvalidStateException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace ();
                        JOptionPane.showMessageDialog (this,
                                                       stringResource.getString (e.getMessage ()),
                                                       stringResource.getString (e.getMessage ()),
                                                       JOptionPane.ERROR_MESSAGE);
                    }
                    catch (IncompatibleEvidenceException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace ();
                        JOptionPane.showMessageDialog (this,
                                                       stringResource.getString (e.getMessage ()),
                                                       stringResource.getString (e.getMessage ()),
                                                       JOptionPane.ERROR_MESSAGE);
                    }
                    aux[i - 1] = hasUncertainty;
                }
                if (!hasLinkRestriction)
                {
                    valuesTable.setDefaultRenderer (Double.class,
                                                    new ValuesTableCellRenderer (
                                                                                 getFirstEditableRow (),
                                                                                 aux));
                    valuesTable.setDefaultRenderer (String.class,
                                                    new ValuesTableCellRenderer (
                                                                                 getFirstEditableRow (),
                                                                                 aux));
                }
                else
                {
                    valuesTable.setDefaultRenderer (Double.class,
                                                    new ValuesTableWithLinkRestrictionCellRenderer (
                                                                                                    getFirstEditableRow (),
                                                                                                    aux));
                    valuesTable.setDefaultRenderer (String.class,
                                                    new ValuesTableWithLinkRestrictionCellRenderer (
                                                                                                    getFirstEditableRow (),
                                                                                                    aux));
                }
            }
            else if (probNode.getNodeType () == NodeType.DECISION)
            {
                if (probNode.getPolicyType () == PolicyType.OPTIMAL)
                {
                    valuesTable.setDefaultRenderer (Double.class,
                                                    new ValuesTableOptimalPolicyCellRenderer (
                                                                                              getFirstEditableRow (),
                                                                                              aux));
                    valuesTable.setDefaultRenderer (String.class,
                                                    new ValuesTableOptimalPolicyCellRenderer (
                                                                                              getFirstEditableRow (),
                                                                                              aux));
                }
                else
                {
                    valuesTable.setDefaultRenderer (Double.class,
                                                    new ValuesTableCellRenderer (
                                                                                 getFirstEditableRow (),
                                                                                 aux));
                    valuesTable.setDefaultRenderer (String.class,
                                                    new ValuesTableCellRenderer (
                                                                                 getFirstEditableRow (),
                                                                                 aux));
                }
            }
        }
    }
		

	/**
	 * Method to define the specific listeners in this table (not defined in the
	 * common KeyTable hierarchy. This method creates the evidenceCase object
	 * when the user do right click on the table.
	 */
	protected void setTableSpecificListeners() {


		valuesTable.addMouseListener(new java.awt.event.MouseAdapter() {

			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					valuesTable.editCellAt(valuesTable.rowAtPoint(e.getPoint()), valuesTable.columnAtPoint(e.getPoint()), e);
				}
				
				if (SwingUtilities.isRightMouseButton(e)) {
					int row = valuesTable.rowAtPoint(e.getPoint());
					int col = valuesTable.columnAtPoint(e.getPoint());

					if ((row > -1) && (col > 0)) {

						if (getUncertaintyContextualMenu() != null) {
							selectedColumn = col;
							updateContextualMenuOptions();
							getUncertaintyContextualMenu().show(valuesTable, e.getX(),
									e.getY());

						}
					}
				}
			}

		});

	}

	@Override
	public void close() {
		getValuesTable().close();
	}
	
	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		getValuesTable().setModifiable(!readOnly);
	}	
}
