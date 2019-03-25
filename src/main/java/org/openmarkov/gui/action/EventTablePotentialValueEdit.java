/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.action;

import org.openmarkov.core.action.PotentialChangeEdit;
import org.openmarkov.core.action.SimplePNEdit;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.Util;
import org.openmarkov.core.model.network.potential.EventTablePotential;
import org.openmarkov.core.model.network.potential.EventTimeTablePotential;
import org.openmarkov.core.model.network.potential.ExactDistrPotential;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.gui.component.PotentialsTablePanelOperations;

import java.awt.*;
import java.util.Iterator;
import java.util.List;

//import org.openmarkov.core.model.network.Variable;
//import org.openmarkov.core.model.network.eventTablePotential.Potential;
//import org.openmarkov.core.model.network.eventTablePotential.PotentialRole;

/**
 * <code>NodePotentialEdit</code> is a simple edit that allows to modify the
 * node's <code>Potential</code> values. It is implemented for TablePotential
 * Only
 *
 * @author mpalacios
 * @version 1.1 28/05/2016 - cyago - Eliminated the different treatment of the utility nodes and introduces the behaviour of ExactDistrPotential
 * - adding the attribute getEventTablePotential
 */
@SuppressWarnings("serial") public class EventTablePotentialValueEdit extends SimplePNEdit {
	/**
	 * The column of the table where is the eventTablePotential
	 */
	private int col;
	/**
	 * The row of the table where is the eventTablePotential
	 */
	private int row;
	/**
	 * The new value of the eventTablePotential
	 */
	private Double newValue;
	/**
	 * A list that store the edition order
	 */
	private List<Integer> priorityList;
	/**
	 * The index of the value selected in the graphic table
	 */
	private int indexSelected;
	/**
	 * Index of the value selected
	 */
	private int potentialSelected;

	/**
	 * For doEdit
	 *
	 */

	private TablePotential tablePotential;
	private TablePotential oldTablePotential;

	private EventTablePotential oldEventTablePotential;
	private EventTablePotential eventTablePotential;

	/**
	 * the increment to get the real position of the value modified
	 */
	private int increment;

	/**
	 * Pseudo-util class with common operations used  in eventTablePotential tables
	 */
	private PotentialsTablePanelOperations tablePotentialsPanelOperations;

	/**
	 * the table eventTablePotential
	 */
	private double[] newTable;
	// private List<Variable> orderVariables = new ArrayList<Variable>();
	// private List<Variable> newOrderVariables = new ArrayList<Variable>();
	private Object[][] notEditablePostitions = new Object[0][0];
	private Node node;

	// Constructor

	/**
	 * Creates a new <codeZEventTablePotentialEdit</code> specifying the node to be
	 * edited, the new value of the eventTablePotential, the row and column where is the
	 * value to be modified and a priority list for potentials updating.
	 *
	 * @param node                 the node to be edited
	 * @param newValue             the new value
	 * @param col                  the column in the edited table
	 * @param row                  the row in the edited table
	 * @param priorityList         the priority lists for potentials update.
	 * @param notEditablePositions two dimensional array with the information about editable
	 *                             positions.
	 *                             cyago added the new initialisation of getEventTablePotential
	 */
	public EventTablePotentialValueEdit(Node node, Double newValue, int row, int col, List<Integer> priorityList,
                                        Object[][] notEditablePositions) {
		super(node.getProbNet());
		this.node = node;

		try {
			this.oldEventTablePotential = (EventTablePotential) node.getPotentials().get(0);
			this.oldTablePotential = oldEventTablePotential.getTablePotential();
		} catch (Exception e) {
			e.printStackTrace();
/* TODO
			JOptionPane.showMessageDialog(this,
					stringDatabase.getValuesInAString(e.getMessage()),
					stringDatabase.getValuesInAString(e.getMessage()),
					JOptionPane.ERROR_MESSAGE);
			return;		
*/
		}
		this.row = row;
		this.col = col;
		this.tablePotentialsPanelOperations = new PotentialsTablePanelOperations();
		this.newValue = newValue;
		this.priorityList = priorityList;
		this.notEditablePostitions = notEditablePositions;
		this.indexSelected = tablePotentialsPanelOperations.calculateLastEditableRow(oldTablePotential) - row;
		this.increment = tablePotentialsPanelOperations.getPotentialStartIndexOfColumn(col, oldTablePotential);

		//TODO Clone
		if (oldEventTablePotential instanceof EventTimeTablePotential){
			this.eventTablePotential = new EventTimeTablePotential(oldEventTablePotential.getVariables(),
					oldEventTablePotential.getPotentialRole());
		} else {
			this.eventTablePotential = new EventTablePotential(oldEventTablePotential.getVariables(),
					oldEventTablePotential.getPotentialRole());
		}



		this.tablePotential = (TablePotential) (oldEventTablePotential.getTablePotential().copy());
		this.eventTablePotential.setTablePotential(this.tablePotential);

		this.newTable = this.tablePotential.getValues();

		// Get the eventTablePotential index
		this.potentialSelected = tablePotentialsPanelOperations.getPotentialIndex(row, col, oldTablePotential);

	}

	/**
	 * This method fills the new table of tablePotential with the new values calculated after the edition of a cell
	 * and updates the probNet
	 * In case the eventTablePotential is ExactDistrPotential...
	 *
	 * @throws <code>DoEditException</code> cyago only eliminated the different treatment for UTILITY role and introduced eventTablePotential
	 */
	@Override public void doEdit() throws DoEditException {
		PotentialChangeEdit changePotentialEdit = null;

		if (eventTablePotential instanceof EventTimeTablePotential){
			newTable[potentialSelected] = newValue;
			tablePotential.getValues()[potentialSelected] = newValue;
			changePotentialEdit = new PotentialChangeEdit(probNet, oldEventTablePotential, eventTablePotential);
		}

		else {
			//		if (!getEventTablePotential()) {
			if (priorityList.isEmpty()) {
				// User is editing a new column of potentials //node
				priorityList = getPriorityListInitialization();
			} else {
				// the user is editing a the same column of potentials that last
				// time
				priorityList.remove((Integer) potentialSelected);
				priorityList.add(potentialSelected);
			}
			Iterator<Integer> listIterator = priorityList.listIterator();
			Double sum = 0.0;
			Double rest = 0.0;
			int position = 0;
			int maxDecimals = 10;
			double epsilon;
			epsilon = Math.pow(10, -(maxDecimals + 2));
			newTable[potentialSelected] = Util.roundAndReduce(newValue, epsilon, maxDecimals);
			while (listIterator.hasNext()) {
				position = (Integer) listIterator.next();
				if (isEditablePosition(position)) {
					sum = Util.roundAndReduce(sum + newTable[position], epsilon, maxDecimals);
				}
				// sum += newTable[pos];
			}
			rest = Math.abs(Util.roundAndReduce(1 - sum, epsilon, maxDecimals));
			// rest = Math.abs( 1 - sum );
			if (sum > 1.0) {
				listIterator = priorityList.listIterator();
				while (listIterator.hasNext() && rest != 0) {
					position = (Integer) listIterator.next();
					if (this.isEditablePosition(position)) {
						rest = Util.roundAndReduce(rest - newTable[position], epsilon, maxDecimals);
						// rest = rest - newTable[pos];
						if (rest < 0) {// it is because the value of the table
							// is bigger than the rest
							// and now there's nothing left to reach
							// one
							newTable[position] = Math.abs(Util.roundAndReduce(rest, epsilon, maxDecimals));
							break;
						} else
							newTable[position] = 0.0;
					}
				}
			} else {// =< 1
				boolean updated = false;
				listIterator = priorityList.listIterator();
				while (listIterator.hasNext() && !updated) {
					position = (Integer) listIterator.next();
					if (this.isEditablePosition(position)) {
						newTable[position] = Util.roundAndReduce(newTable[position] + rest, epsilon, maxDecimals);
						updated = true;
					}
				}
			}
			changePotentialEdit = new PotentialChangeEdit(probNet, oldEventTablePotential, eventTablePotential);

		}


		try {
			probNet.doEdit(changePotentialEdit);
		} catch (ConstraintViolationException | NonProjectablePotentialException | WrongCriterionException e) {
			e.printStackTrace();
			throw new DoEditException(e);
		}
	}

	/**
	 * Gets the TablePotential of the node
	 *
	 * @return variable1 <code>Variable</code>
	 */
	public TablePotential getTablePotential() {
		return tablePotential;
	}


	/**
	 * Gets the TablePotential of the node
	 *
	 * @return variable1 <code>Variable</code>
	 */
	public EventTablePotential getEventTablePotential() {
		return eventTablePotential;
	}





	/**
	 * Gets the priority list initialisation.
	 * Creates a list with the positions in the table of tablePotential of the column containing the edited value.
	 * The list first contains the positions corresponding to the not edited cells
	 * and the last position corresponds to the edited cell
	 *
	 * @return the priority list initialised with the the value edited in the
	 * last place of the list
	 * <p>
	 * revised-->not changed
	 */
	private List<Integer> getPriorityListInitialization() {
		for (int i = 0; i < node.getVariable().getNumStates(); i++) {
			if (i != indexSelected)
				priorityList.add(i + increment);
		}
		priorityList.add(indexSelected + increment);
		return priorityList;
	}

	/**
	 * Gets the priority list
	 *
	 * @return the priority list
	 */
	public List<Integer> getPriorityList() {
		return priorityList;
	}

	/*
	 * private double roundingDouble(double number) { double positions =
	 * Math.pow( 10, (double) decimalPositions ); return Math.round( number *
	 * positions ) / positions; }
	 */

	/**
	 * Gets the row position associated to value edited if priorityList exists
	 *
	 * @param position position of the value in the array of values
	 * @return the position in the table
	 */
	public int getRowPosition(int position) {
		int lastRow = tablePotentialsPanelOperations.calculateLastEditableRow(tablePotential);
		return lastRow - position % tablePotential.getDimensions()[0];
	}

	/**
	 * Gets the row position associated to value edited if priorityList no
	 * exists
	 *
	 * @return the position in the table
	 */
	public int getRowPosition() {
		return row;
	}

	/**
	 * Gets the column where the value is edited
	 *
	 * @return the column edited
	 */
	public int getColumnPosition() {
		return col;
	}

	/***
	 * Checks if the position in the table of tablePotential corresponds to an editable cell if there is a priority list
	 * UNCLEAR--> Have I to change the behaviour; depends on doEdit()
	 * @param position
	 * @return true if the cell is editable
	 * revised-->not changed
	 */
	private boolean isEditablePosition(int position) {
		boolean editable = false;
		int row = getRowPosition(position);
		if (this.notEditablePostitions.length > row && this.notEditablePostitions[0].length > col) {
			if (this.notEditablePostitions[row][col] == null) {
				editable = true;
			}
		} else {
			editable = true;
		}
		return editable;
	}


}
