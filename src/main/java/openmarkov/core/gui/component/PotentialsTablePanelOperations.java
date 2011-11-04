/**
 * OpenMarkov - PotentialsTablePanelOperations.java
 */
package openmarkov.core.gui.component;

import java.util.ArrayList;

import openmarkov.exceptions.NullListPotentialsException;
import openmarkov.graphs.Node;
import openmarkov.networks.NodeType;
import openmarkov.networks.ProbNode;
import openmarkov.networks.Variable;
import openmarkov.networks.potentials.Potential;
import openmarkov.networks.potentials.PotentialRole;
import openmarkov.networks.potentials.TablePotential;



/** Auxiliar methods for PotentialsTablePanel class
 * @author jlgozalo
 * @author marias
 * @version 1.0 */
public class PotentialsTablePanelOperations {


	/** To check if the list of <code>Potential</code>s must be changed when
	 *  parents or states have been changed
	 * @param listPotentials - current list of potentials
	 * @param adittionalProperties - adittionalProperties related to this
	 *  variable
	 * @return new list of potentials for the variable with the changes applied
	 */
	public static ArrayList<Potential> checkIfPotentialsMustBeChanged (
			ArrayList<Potential> listPotentials,
			ProbNode properties)  {
		ArrayList<Potential> newListPotentials = listPotentials;
		if (listPotentials != null) {
			if (listPotentials.get( 0 ) != null) {
				ArrayList<Variable> variablesPotential = 
					listPotentials.get(0).getVariables();
				ArrayList<Node> parents = properties.getNode().getParents();
				if ((variablesPotential.size()-1) > parents.size()) {
					newListPotentials = 
						doDeleteParent (listPotentials, properties);		
				} else if ((variablesPotential.size()-1) < parents.size()) {
					newListPotentials = 
						doAddParent (listPotentials, properties);		
				} 
			}
		}
		return newListPotentials;
	}

	/** Method to generate a new ArrayList of <code>Potential</code> by adding 
	 * a new parent to the previous ones
	 * @param listPotentials - previous list of potentials
	 * @param adittionalProperties - adittionalProperties related to the
	 * variable in use that contains a new parent
	 * @return new list of Potentials with the new parent add */
	private static ArrayList<Potential> doAddParent (
			ArrayList<Potential> listPotentials,
			ProbNode properties)  {
		ArrayList<Potential> newListPotentials = new ArrayList<Potential> ();
		ArrayList<Variable> variables = new ArrayList<Variable> ();
        // first, this variable. The potentials is not null
		Variable thisVariable =listPotentials.get( 0 ).getVariable( 0 ); 
		variables.add(thisVariable ); //this variable
		int numOfCellsInTable = thisVariable.getNumStates();
		double initialValue = 1 / (new Double(numOfCellsInTable));
		if (properties.getNodeType()==NodeType.UTILITY) {
			initialValue = 0;
		}
		    // add now all the parents 
		for (Node node: properties.getNode().getParents()) {
			variables.add( ((ProbNode)node.getObject()).getVariable());
			numOfCellsInTable *= ((ProbNode)node.getObject()).getVariable().
			getNumStates();
		}
		// sets a new table with new columns and with all the same values
		double[] table = new double[numOfCellsInTable] ;
		for (int i=0; i<numOfCellsInTable; i++) {
			table[i] = initialValue;
		}
		// and finally, create the potential and the list of potentials
		TablePotential tablePotential = new TablePotential(
				variables, PotentialRole.CONDITIONAL_PROBABILITY,table);
		newListPotentials.add( tablePotential );
		return newListPotentials;
	}
	

	/** Method to generate a new ArrayList of <code>Potential</code> by removing
	 * a parent from the previous ones
	 * @param listPotentials - previous list of potentials
	 * @param adittionalProperties - adittionalProperties related to the 
	 * variable in use that contains the parent
	 * @return new list of Potentials with the parent removed */
	private static ArrayList<Potential> doDeleteParent (
			ArrayList<Potential> listPotentials,
			ProbNode properties)  {
		ArrayList<Potential> newListPotentials = new ArrayList<Potential> ();
		ArrayList<Variable> variables = new ArrayList<Variable> ();
        // first, this variable. The potentials is not null
		Variable thisVariable =listPotentials.get( 0 ).getVariable( 0 ); 
		variables.add(thisVariable ); //this variable
		int numOfCellsInTable = thisVariable.getNumStates();
		double initialValue = 1 / (new Double(numOfCellsInTable));
		    // add now all the parents 
		for (Node node: properties.getNode().getParents()) {
			variables.add( ((ProbNode)node.getObject()).getVariable());
			numOfCellsInTable *= ((ProbNode)node.getObject()).getVariable().
			getNumStates();
		}
		// sets a new table with new columns and with all the same values
		double[] table = new double[numOfCellsInTable] ;
		for (int i=0; i<numOfCellsInTable; i++) {
			table[i] = initialValue;
		}
		// and finally, create the potential and the list of potentials
		TablePotential tablePotential = new TablePotential(
				variables, PotentialRole.CONDITIONAL_PROBABILITY,table);
		newListPotentials.add( tablePotential );
		return newListPotentials;
	}

	/**
	 * calculate the first editable Row of the table, based upon:
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
	public static int calculateFirstEditableRow(
			ArrayList<Potential> listPotentials,
			ProbNode properties) {
		int row = 0;
		if (listPotentials != null) {
			if (properties.getNodeType() == NodeType.UTILITY) {
				row = listPotentials.get( 0 ).getNumVariables() ;
			} else {
				row = listPotentials.get( 0 ).getNumVariables() - 1;
			}
		} else {
			row = 0;
		}

		return row;
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
	public static int calculateLastEditableRow(
			ArrayList<Potential> listPotentials,
			ProbNode properties) {
		int row = 0;
		if (listPotentials != null) {
			row = listPotentials.get( 0 ).getNumVariables() - 1;
			if (properties.getNodeType() == NodeType.UTILITY) {
				row += 1;
			} else {
				row += properties.getVariable().getStates().length;
			}
		} else {
			row = 0;
		}

		return row;
	}

	/**
	 * determine if a list of potentials is empty or not
	 * @param listPotentials - the list of potentials to check
	 */
	public static void checkIfNoPotential(ArrayList<Potential> listPotentials)
					throws NullListPotentialsException{

		if (listPotentials == null) {
			throw new NullListPotentialsException( "" );
		} else {
			try {
				listPotentials.get( 0 );
			} catch (IndexOutOfBoundsException ex) {
				throw new NullListPotentialsException( "" );
			}
		}
	}

}
