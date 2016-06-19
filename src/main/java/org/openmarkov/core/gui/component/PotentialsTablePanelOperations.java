/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

/**
 * OpenMarkov - PotentialsTablePanelOperations.java
 */
package org.openmarkov.core.gui.component;

//import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

//import javax.swing.JOptionPane;

import org.openmarkov.core.exception.NullListPotentialsException;
import org.openmarkov.core.exception.NullPotentialException;
import org.openmarkov.core.model.network.Node;
//import org.openmarkov.core.model.network.NodeType;
//import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
//import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TableDeltaPotential;
import org.openmarkov.core.model.network.potential.TablePotential;

/**
 * Auxiliary methods for PotentialsTablePanel class
 * 
 * @author jlgozalo
 * @author marias
 * @author carmenyago
 * @version 1.0
 * @version 2.0 27/05/2016 by carmenyago
 */
public class PotentialsTablePanelOperations implements TableMethods {

//	/**
//	 * To check if the list of <code>Potential</code>s must be changed when
//	 * parents or states have been changed
//	 * 
//	 * @param listPotentials
//	 *            - current list of potentials
//	 * @param additionalProperties
//	 *            - additionalProperties related to this variable
//	 * @return new list of potentials for the variable with the changes applied
//	 */
//	public List<Potential> checkIfPotentialsMustBeChanged(
//			List<Potential> listPotentials, Node properties) {
//		List<Potential> newListPotentials = listPotentials;
//		if (listPotentials != null) {
//			if (listPotentials.get(0) != null) {
//				List<Variable> variablesPotential = listPotentials.get(0)
//						.getVariables();
//				List<Node> parents = properties.getParents();
//				if ((variablesPotential.size() - 1) > parents.size()) {
//					newListPotentials = doDeleteParent(listPotentials,
//							properties);
//				} else if ((variablesPotential.size() - 1) < parents.size()) {
//					newListPotentials = doAddParent(listPotentials, properties);
//				}
//			}
//		}
//		return newListPotentials;
//	}

//	/**
//	 * Method to generate a new ArrayList of <code>Potential</code> by adding a
//	 * new parent to the previous ones
//	 * 
//	 * @param listPotentials
//	 *            - previous list of potentials
//	 * @param additionalProperties
//	 *            - additionalProperties related to the variable in use that
//	 *            contains a new parent
//	 * @return new list of Potentials with the new parent add
//	 */
//	private List<Potential> doAddParent(List<Potential> listPotentials,
//			Node properties) {
//		List<Potential> newListPotentials = new ArrayList<Potential>();
//		List<Variable> variables = new ArrayList<Variable>();
//		// first, this variable. The potentials is not null
//		Variable thisVariable = listPotentials.get(0).getVariable(0);
//		variables.add(thisVariable); // this variable
//		int numOfCellsInTable = thisVariable.getNumStates();
//		double initialValue = 1 / (new Double(numOfCellsInTable));
//		if (properties.getNodeType() == NodeType.UTILITY) {
//			initialValue = 0;
//		}
//		// add now all the parents
//		for (Node node : properties.getParents()) {
//			variables.add(node.getVariable());
//			numOfCellsInTable *= node.getVariable().getNumStates();
//		}
//		// sets a new table with new columns and with all the same values
//		double[] table = new double[numOfCellsInTable];
//		for (int i = 0; i < numOfCellsInTable; i++) {
//			table[i] = initialValue;
//		}
//		// and finally, create the potential and the list of potentials
//		TablePotential tablePotential = new TablePotential(variables,
//				PotentialRole.CONDITIONAL_PROBABILITY, table);
//		newListPotentials.add(tablePotential);
//		return newListPotentials;
//	}

//	/**
//	 * Method to generate a new ArrayList of <code>Potential</code> by removing
//	 * a parent from the previous ones
//	 * 
//	 * @param listPotentials
//	 *            - previous list of potentials
//	 * @param additionalProperties
//	 *            - additionalProperties related to the variable in use that
//	 *            contains the parent
//	 * @return new list of Potentials with the parent removed
//	 */
//	private List<Potential> doDeleteParent(List<Potential> listPotentials,
//			Node properties) {
//		List<Potential> newListPotentials = new ArrayList<Potential>();
//		List<Variable> variables = new ArrayList<Variable>();
//		// first, this variable. The potentials is not null
//		Variable thisVariable = listPotentials.get(0).getVariable(0);
//		variables.add(thisVariable); // this variable
//		int numOfCellsInTable = thisVariable.getNumStates();
//		double initialValue = 1 / (new Double(numOfCellsInTable));
//		// add now all the parents
//		for (Node node : properties.getParents()) {
//			variables.add(node.getVariable());
//			numOfCellsInTable *= node.getVariable().getNumStates();
//		}
//		// sets a new table with new columns and with all the same values
//		double[] table = new double[numOfCellsInTable];
//		for (int i = 0; i < numOfCellsInTable; i++) {
//			table[i] = initialValue;
//		}
//		// and finally, create the potential and the list of potentials
//		TablePotential tablePotential = new TablePotential(variables,
//				PotentialRole.CONDITIONAL_PROBABILITY, table);
//		newListPotentials.add(tablePotential);
//		return newListPotentials;
//	}

//	/**
//	 * calculate the first editable Row of the table, based upon:
//	 * <p>
//	 * <ul>
//	 * <li>number of parents for the node</li>
//	 * <li>type of the node (utility or other)</li>
//	 * </ul>
//	 * 
//	 * @param potentials
//	 *            - potentials for the variable
//	 * @param additionalProperties
//	 *            - additionalProperties for this variable
//	 */
//	@Override
//	public int calculateFirstEditableRow(Node node) {
//		int row = 0;
//		if (node.getPotentials() != null) {
//			if (node.getNodeType() == NodeType.UTILITY) {
//				row = node.getPotentials().get(0).getNumVariables();
//			} else {
//				row = node.getPotentials().get(0).getNumVariables() - 1;
//			}
//		} else {
//			row = 0;
//		}
//
//		return row;
//	}

	
	
/**
 * calculate the first editable Row of the table, based upon the number of parents for the node. 
 * The first editable row equals the number of parents of the node 
 * 
 * @param node
 *            - node with contains the potentials 
 * carmenyago removed the dependency from the NodeType  
 *          
 * @author carmenyago   
 */
@Override
public int calculateFirstEditableRow(Node node) {
	try{
		checkIfNoPotential(node.getPotentials());
	} catch (Exception e){
		e.printStackTrace();
		return 0;
	}
	int row = 0;
	row = node.getPotentials().get(0).getNumVariables() -1;		
	return row;
}
	
	
		
//	/**
//	 * calculate the last editable Row of the table, based upon:
//	 * <p>
//	 * <ul>
//	 * <li>number of parents for the node</li>
//	 * <li>type of the node (utility or other)</li>
//	 * </ul>
//	 * 
//	 * @param listPotentials
//	 *            - potentials for the variable
//	 * @param additionalProperties
//	 *            - additionalProperties for this variable
//	 */
//	@Override
//	public int calculateLastEditableRow(Node node) {
//		int row = 0;
//		if (node.getPotentials() != null) {
//			// Get the number of parents
//			row = node.getPotentials().get(0).getNumVariables() - 1;
//			if (node.getNodeType() == NodeType.UTILITY) {
//				row += 1;
//			} else {
//				row += node.getVariable().getStates().length - 1;
//			}
//			/*
//			 * if (properties.getNodeType() == NodeType.UTILITY) { row += 1; }
//			 * else { row += properties.getVariable().getStates().length; }
//			 */
//		} else {
//			row = 0;
//		}
//
//		return row;
//	}


/**
 * This method calculates the last editable row of the table. 
 * The last editable row is (number_of_parents of the node + number_of_states of the variable node)
 * 
 * carmenyago removed the dependence with NodeType
 * 
 * @param Node node:  node who "owns" the table
 * @author carmenyago
 */
@Override
public int calculateLastEditableRow(Node node) {
	try{
		checkIfNoPotential(node.getPotentials());
	} catch (Exception e){
		e.printStackTrace();
		JOptionPane.showMessageDialog(null, "There is not a valid potential");
		return 0;
	}

	int row = 0;
	Potential potential=node.getPotentials().get(0);
	if (getIsTableDeltaPotential(potential)) 
		row =potential.getNumVariables()-1; 
	else
	// Number of parents + Number of variable states -1
		row = node.getPotentials().get(0).getNumVariables() - 1 + node.getVariable().getStates().length - 1;
	 		
	return row;
}

//	/**
//	 * determine if a list of potentials is empty or not
//	 * 
//	 * @param listPotentials
//	 *            - the list of potentials to check
//	 *            
//	 * 
//	 */
//	public void checkIfNoPotential(List<Potential> listPotentials)
//			throws NullListPotentialsException {
//		if (listPotentials == null) {
//			throw new NullListPotentialsException("");
//		} else {
//			try {
//				listPotentials.get(0);
//			} catch (IndexOutOfBoundsException ex) {
//				throw new NullListPotentialsException("");
//			}
//		}
//	}	
	
	
/**
 * This method determines if a list of potentials is empty or not. If the 
 * 
 * @param listPotentials
 *            - the list of potentials to check
 *            
 * @throws  <code>NullListPotentialsException</code> if listPotentials is null
 * @throws  <code>NullPotentialException</code> if listPotentials is empty        
 *            
 * carmenyago simplified the method and added           
 * @author carmenyago 
 * 
 */
public void checkIfNoPotential(List<Potential> listPotentials)
		throws NullListPotentialsException, NullPotentialException  {
	
	if (listPotentials == null ) throw new NullListPotentialsException("");
	if (listPotentials.isEmpty()) throw new NullPotentialException("");
}

//	@Override
//	public int getPotentialIndex(int row, int column, Node node) {
//
//		// First of all we get the start index of the column
//		int potentialIndex = getPotentialStartIndexOfColumn(column, node);
//
//		// We get the last editable row in the JTable
//		int lastRow = calculateLastEditableRow(node);
//
//		// Then we move a number of positions equals to the row (without the headers)
//		potentialIndex += (lastRow - row);
//		return potentialIndex;
//	}
	

/**
 * This method returns the potential index of the table of the first potential of the node corresponding to the (row, column) position 
 * in the Jtable 
 * If the class of the first potential is <code>TableDeltaPotential</code>, 
 * the method returns the index in its TablePotential 
 * @param row
 * 		- the index of the row of the JTable
 * @param column
 * 		- the index of the column of the JTable
 * @param node
 *  		- the node with the TablePotential
 *  
 * @return the index of the potential corresponding to the (row, column) cell in JTable  
 * carmenyago only added the exception handling
 * 
 * @author carmenyago
 */

/**
 * True if the class of the potential is TableDeltaPotential
 * @param potential
 * 		- The potential to check
 * @return true if the class of the potential is TableDeltaPotential; false otherwise
 */
public boolean getIsTableDeltaPotential(Potential potential){
	return potential.getClass().getName().equals("org.openmarkov.core.model.network.potential.TableDeltaPotential");   	
}


@Override
public int getPotentialIndex(int row, int column, Node node) {
	try{
		checkIfNoPotential(node.getPotentials());
	} catch (Exception e){
		e.printStackTrace();
		JOptionPane.showMessageDialog(null, "There is not a valid potential");
		return 0;
	}
	// First of all we get the start index of the column
	int potentialIndex = getPotentialStartIndexOfColumn(column, node);

	// We get the last editable row in the JTable
	int lastRow = calculateLastEditableRow(node);

		// Then we move a number of positions equals to the row (without the headers)
	potentialIndex += (lastRow - row);
	return potentialIndex;
}	
	

//	/**
//	 * Gets the index of the first potential of a column
//	 * 
//	 * @param column
//	 * @return index of the potential
//	 */
//	public int getPotentialStartIndexOfColumn(int column, Node node) {
//		TablePotential tablePotential = (TablePotential) node.getPotentials()
//				.get(0);
//		int position = 0;
//
//		// We use a temporal value to make the column 1 as the first (column 0)
//		int temp = column - 1;
//		if (tablePotential.getDimensions() != null) {
//			// In this code we get the coordinates (states index) of the
//			// variable and
//			// we calculate the position in the list of potentials. The position
//			// is the product of each state index and the respective offset
//			// s[0]*offset[0] + s[1]*offset[1] + ..... + s[n]*offset[n]
//			int numberOfDimensions = tablePotential.getDimensions().length - 1;
//			int lowerBound = 0;
//			if (node.getNodeType() == NodeType.UTILITY) {
//				// numberOfDimensions += 1;
//				lowerBound = -1;
//			}
//			for (int i = numberOfDimensions; i > lowerBound; i--) {
//				// Dimension of the first parent
//				int dimension = tablePotential.getDimensions()[i];
//
//				// In each iteration this code add the s[i]*offset[i] to the
//				// position
//				position += (temp % dimension) * tablePotential.getOffsets()[i];
//				temp = temp / dimension;
//			}
//
//		} else {
//			position = 0;
//		}
//		return position;
//	}

/**
 * Given the number of column of a JTable, 
 * this method calculates the index in the table of a first potential of a node 
 * corresponding to the first cell in the column. 
 * If there is no potential or the potential has no states, the method returns 0 (keeping the previous behaviour) 
 * 
 * @param column
 * 		- the index of a column
 * @param node
 * 		- the node with the potential
 * 
 * @return index of the potential.  
 * 
 * @author carmenyago
 */
public int getPotentialStartIndexOfColumn(int column, Node node) {
	/* 
	 * This code is here and in getPotentialIndex because this method is used not only in  getPotentialIndex
	 * but in org.openmarkov.core.gui.action.TablePotentialValueEdit
	 */
	try{
		checkIfNoPotential(node.getPotentials());
	} catch (Exception e){
		e.printStackTrace();
		JOptionPane.showMessageDialog(null, "There is not a valid potential");
		return 0;
	}
	
	Potential potential= node.getPotentials().get(0);
	TablePotential tablePotential=null;

	if (getIsTableDeltaPotential(potential))
		tablePotential=((TableDeltaPotential)potential).getTablePotential();
	else
		tablePotential = (TablePotential)potential;

	
	// Index in tablePotential of the beginning of the column
	int position=0;
	// Making the column 1 as the first (column 0)
	int temp = column - 1;
	
	// Supposing Dimensions >=1 UNCLEAR
	
	// In this code we get the coordinates (states index) of the variable and
	// we calculate the position in the list of potentials. The position
	// The position is the product of each state index and the respective offset
	// s[0]*offset[0] + s[1]*offset[1] + ..... + s[n]*offset[n]
	
	// Dimensions--> list with the states of each variable of the potential
	// 
	// Now there is no difference between CHANCE and UTILITY
	int[] dimensions =tablePotential.getDimensions();
	int numberOfDimensions=0;
	if (dimensions ==null){
		return 0;
	}else
		numberOfDimensions = dimensions.length - 1;
	
	int lowerBound = 0;
	if (getIsTableDeltaPotential(potential)) lowerBound = -1; 
	for (int i = numberOfDimensions; i > lowerBound; i--) {
		int dimension = dimensions[i];
		position += (temp % dimension) * tablePotential.getOffsets()[i];
		temp = temp / dimension;
	}
		return position;		
}




}

