
/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/
package org.openmarkov.core.gui.dialog.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.openmarkov.core.exception.NullListPotentialsException;
import org.openmarkov.core.gui.component.PotentialsTablePanelOperations;
import org.openmarkov.core.gui.component.ValuesTable;
import org.openmarkov.core.gui.component.ValuesTableModel;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.canonical.ICIModelType;
import org.openmarkov.core.model.network.potential.canonical.ICIPotential;
import org.openmarkov.core.model.network.potential.canonical.MaxPotential;
//TODO review setData methods
public class ICIPotentialsTablePanel extends ProbabilityTablePanel {
	
	protected Logger logger;
	public ICIPotentialsTablePanel(ProbNode probNode) {
		super(probNode);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Sets a new table model with new data.
	 * 
	 * @param newData
	 *            new data for the table.
	 */
	public void setData(Object[][] newData) {

		setData( newData, columns, 0, 0 , NodeType.CHANCE);
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

		showValuesTable( true );
		data = newData.clone();
		columns = newColumns.clone();
		this.firstEditableRow = firstEditableRow;
		this.lastEditableRow = lastEditableRow;
		valuesTable.resetModel();
		
		//valuesTable.setVariable(probNode.getPotentials().get( 0 ).getVariable( 0 ));
		
		valuesTable.setModel( getTableModel() );
		valuesTable.initializeDataModified( false );
		((ValuesTableModel) valuesTable.getModel())
			.setFirstEditableRow( firstEditableRow );
		valuesTable.setLastEditableRow( lastEditableRow );
		valuesTable.setShowingAllParameters( true );
		valuesTable.setNodeType(nodeType);

	}
	/**
	 * Sets a new table model with new data and new columns based on three
	 * items:
	 * <li>list of Potentials of the variable</li>
	 * <li>states of the variable</li>
	 * <li>parents of the variable</li>
	 * 
	 * @param listPotentials -
	 *            the list of potentials of the variable
	 * @param variableName -
	 *            name of the variable
	 * @param variableStates -
	 *            states of the variable
	 * @param parents -
	 *            parents of the variable
	 */
	public void setData(ProbNode properties) {
		Object[][] tableData = null;
		String[] newColumns = null;
		if (properties.getPotentials() != null) {
			//listPotentials = PotentialsTablePanelOperations.checkIfPotentialsMustBeChanged(listPotentials, adittionalProperties);
			//setListPotentials(probNode.getPotentials());
			//tableData =
				//convertListPotentialsToTableFormat( listPotentials, adittionalProperties );
			tableData =
				convertListPotentialsToCanonicalTableFormat(properties );
			newColumns =
				ValuesTable
					.getColumnsIdsSpreedSheetStyle( ValuesTable
						.howManyCanonicalColumns( properties ) );
			setFirstEditableRow(calculateFirstEditableRow(probNode.getPotentials()));
			setLastEditableRow(calculateLastEditableRow(
				probNode.getPotentials()));
			setData( tableData, newColumns, firstEditableRow, lastEditableRow , properties.getNodeType() );
			//TODO setCellRenderes
			//setCellRenderers();
		} else {
			tableData = new Object[ 0 ][ 0 ];
			setFirstEditableRow( 0 );
			setData( tableData );
			//TODO setCellRenderes
			//setCellRenderers();
		}
	}
	/**
	 * calculate the last editable Row of the table, based upon:
	 * <p>
	 * <ul>
	 * <li>number of parents for the node</li>
	 * <li>type of the node (utility or other)</li>
	 * </ul>
	 * 
	 * @param listPotentials -
	 *            potentials for the variable
	 * @param adittionalProperties -
	 *            adittionalProperties for this variable
	 */
	public static int calculateLastEditableRow(ArrayList<Potential> listPotentials) {
		int row = 0;
		if (listPotentials != null) {
			row = listPotentials.get(0).getVariables().get(0).getNumStates()+2; 
			//numStates of the child variable plus one empty cell plus a cell for child the variable´s name
		} else {
			row = 0;
		}

		return row;
	}
	public static int calculateFirstEditableRow(ArrayList<Potential> listPotentials) {
		int row = 0;
		if (listPotentials != null) {
		
				row = 2; //In a canonical table there are always only two rows one for the parents names
							//and another for the parent´s states
			
		} else {
			row = 0;
		}

		return row;
	}
	/**
	 * calculate the number of rows of the canonical table based on the type of the node,
	 * the number of parents and the number of states of the variable for
	 * canonical models
	 * 
	 * @param adittionalProperties -
	 *            node adittionalProperties
	 * @return the number of rows of this Potentials Table
	 */
	protected int howManyCanonicalRows(ProbNode properties) {

		int numRows =2;//The first two rows are first for parent´s name and second one for parent´s states 
	
			if (properties.getVariable().getStates() != null) {//there is a row for each child state
				numRows = numRows + properties.getVariable().getStates().length;
			}
		
		return numRows;
	}
	/**
	 * Set a blank data table for canonical models
	 * 
	 * @param adittionalProperties -
	 *            to obtain the required number of rows and columns
	 * @return the blank data table
	 */
	private Object[][] setBlankCanonicalTable(ProbNode properties) {

		Object[][] blankTable = null;
		int numRows = howManyCanonicalRows( properties );
		int numColumns = ValuesTable.howManyCanonicalColumns( properties );
		blankTable = new Object[ numRows ][ numColumns ];
	    for (int i = 0; i < properties.getVariable().getStates().length; i++) {}

		return blankTable;
	}
	
	private Potential getThisICIPotential(ArrayList<Potential> listPotentials) {

		Potential aPotential = null;
		try {
			aPotential = ((ICIPotential) listPotentials.get( 0 ));
		} catch (Exception ex) {
			//ExceptionsHandler.handleException(
				//ex, "no Potential.get(0) !!!", false );
			logger.error("no Potential.get(0) !!!");
			
		}

		return aPotential;
	}
	
	/**
	 * Calculates number of positions in a canonical table 
	 * Number of canonical table positions: sum of the product of each parent variable states by the child variable states (conditioned)
	 * 
	 * @param values -
	 *            the table that is being modified
	 * @param listPotentials -
	 *            the list of potentials of the node
	 * @param additionalProperties -
	 *            the adittionalProperties of the node
	 */
	private int getNumberOfPostions( ArrayList<Potential> listPotentials) {
		
		int numPositions = 0;
		int numParentState;
		try {
			ArrayList<Variable> variables = listPotentials.get( 0 ).getVariables();
			int numChildStates= variables.get(0).getNumStates();
			for (int i = 1; i < variables.size() ; i++) {
				numParentState = variables.get(i).getNumStates();
				numPositions += numParentState * numChildStates;
			}
		
			numPositions +=  numChildStates; //for the leak column
		} catch (NullPointerException exception) {
			numPositions = 0;
			//ExceptionsHandler.handleException(
				//exception, "not enougth memory", false );
			logger.error("not enougth memory");
		}
		setPosition( numPositions);
		return numPositions;

	}
	/**
	 * Prepare the table data from the <code>Potential</code>s and States.
	 * <p>
	 * If the Potential is null, then the information is taken from the
	 * <code>NodeProperties</code>
	 * 
	 * @param listPotentials -
	 *            potentials of the table
	 * @param states -
	 *            states of the variable of this node
	 * @param parents -
	 *            <code>NodeWrapper</code> list of the parents
	 * @return the table data to be set
	 */
	protected Object[][] convertListPotentialsToCanonicalTableFormat(ProbNode properties) {
		Object[][] values = null;
		try {
			
			PotentialsTablePanelOperations.checkIfNoPotential( 
					properties.getPotentials());
			values = setCanonicalTableSize(values, properties);
			values = setFirstCanonicalColumn(values, properties);
			values = setFirstTwoCanonicalRows(values, properties);
			values = setCanonicalTableProbabilities(values, properties);
			
			
			setPosition(getNumberOfPostions(properties.getPotentials()));
			
		} catch (NullListPotentialsException ex) {
			values = setBlankCanonicalTable( properties );
		}
		return values;
	}
	/**
	 * set values table size for the potential of the canonical model
	 * 
	 * @param values -
	 *            the table that is being modified
	 *
	 * @param adittionalProperties -
	 *            the adittionalProperties of the node
	 */

	private Object[][] setCanonicalTableSize (Object [][]oldValues,
                                     ProbNode properties) {
		Object [][] values = oldValues;
		int numRows = 0;
		int numColumns = 1; //at least, there is one column for the child name and states
		//first editable row in a canonical table is always the third one
		//first one for the parent´s names and second one for parent´s states
		int row = 2; 
					
		setBaseIndexForCoordinates( row );
		setFirstEditableRow( row );
		ICIPotential iciPotential = (ICIPotential) getThisICIPotential(properties.getPotentials());
		ArrayList<Variable> variablesBeforeReorder = iciPotential.getVariables();
		ArrayList<TablePotential> subpotentials = iciPotential.getSubPotentials();//tablePotential per parent variable and leak potential
		
		setVariables( variablesBeforeReorder );
		
		numRows = getVariables().get(0).getNumStates() + row;
		setLastEditableRow(numRows-1);
		//numRows = numRows + 1 ; // + 1 for variableValues (when used in show as Values???
		//for (TablePotential subpotential : subpotentials) {
		for (int i = 0; i < subpotentials.size(); i++) {
			if (i == subpotentials.size() - 1 ) {
				numColumns += 1; //leak column
				break;
			}
			numColumns += subpotentials.get(i).getDimensions()[1]; //Parent states number
		}	
		
       // create the array of arrays
		values = new Object[ numRows ][ numColumns ];
		return values;
	}

	private Object[][] setFirstCanonicalColumn(	Object[][] oldValues, ProbNode properties) {
		
		Object[][] values = oldValues;
		ICIPotential iciPotential = (ICIPotential) getThisICIPotential(properties.getPotentials());
		Variable conditioned = iciPotential.getVariables().get(0);
		values [0][0] = ""; //First cell is empty
		values [1][0] = conditioned.getBaseName();// name of the conditioned variable
		State[] states = conditioned.getStates();
		for (int i = 0; i < states.length; i++) {
			values[i+2][0] = states[i].getName();
			}
		return values;
	}

	private Object[][] setFirstTwoCanonicalRows(Object[][] oldValues, ProbNode properties) {
		
		Object[][] values = oldValues;
		ICIPotential iciPotential = (ICIPotential) getThisICIPotential(properties.getPotentials());
				
		ArrayList<TablePotential> subpotentials = iciPotential.getSubPotentials();//tablePotential per parent variable and leak potential
		//A->D B->D C->D first subpotential would be P(D/A) then P(D/B) then P(D/C) and then the leak potential
		int offset = 0;
		for (int i = 0; i<subpotentials.size() ; i++) {
					
			if (i == subpotentials.size()-1) { //leak potential
				values [0][offset+1] = "Leak";
				values [1][offset+1] = "--";
				continue;
			}
			
			int [] dimensions = subpotentials.get(i).getDimensions();
			ArrayList<Variable> variables = subpotentials.get(i).getVariables();//[D,A]
			Variable conditioned = variables.get(0);//D
			for (int j = 0; j < variables.size();j++) {// variables = [D, A] dimensions = [2,2] => D and A have 2 states, always j=1
				if (variables.get(j) != conditioned) {
					for (int k = 0; k < dimensions[j] ; k++) {//offset= previous dimension
							values [0][offset+k+1] = variables.get(j).getName();
							values [1][offset+k+1] = variables.get(j).getStates()[k];
					}
				}
			}
			
			offset += dimensions[1];
		}
	return values;
	}
	
	
	private Object[][] setCanonicalTableProbabilities(Object[][] oldValues,
			ProbNode properties) {
		Object[][] values = oldValues;
		int position = 0;
		int numColumns = (values.length == 0 ? 0 : values[0].length);
		
		ICIPotential iciPotential = (ICIPotential) getThisICIPotential(properties.getPotentials());
		//ArrayList<TablePotential> subpotentials = iciPotential.getSubPotentials();
		int pos = getFirstEditableRow();
		for (TablePotential subpotential : iciPotential.getSubPotentials()) {
			double [] potentialValues = subpotential.getValues();
		}
		
		
		
		return values;
	}
}
