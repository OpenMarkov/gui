/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.action;

import java.util.ArrayList;

import org.openmarkov.core.action.SimplePNEdit;
import org.openmarkov.core.action.StateAction;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.gui.util.GUIDefaultStates;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.operation.PotentialOperations;


/**
 * <code>NodeStateEdit</code> is a simple edit that allow modify
 * the states of one node.
 *  @author Miguel Palacios   
 * @version 1.0 21/12/10
 *
 */
public class NodeStateEdit extends SimplePNEdit {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4325259909756103849L;
	
	
	/**
	 * The new state
	 */
	private State newState;
	/**
	 * The last state before the edition
	 */
	private State lastState = new State("");
	/**
	 * index of the state selected in the view
	 */
	private int stateSelected;
	/**
	 * The node that the stats belongs to 
	 */
	private ProbNode probNode = null;
	/**
	 * 	The last potential before the edition
	 */
	private ArrayList<Potential> lastPotential;
	/**
	 * The action to carry out
	 */
	private StateAction stateAction;
	/**
	 * 	The last partitioned interval before the edition
	 */
	private PartitionedInterval currentPartitionedInterval;
	/**
	 * 	The last states before the edition
	 */
	private State[] lastStates;
	/**
	 * Creates a new <code>NodeStateEdit</code> to carry out the specified 
	 * action on the specified state.
	 * 
	 * @param probNode the node that will be edited.
	 * @param stateAction the action to carry out
	 * @param indexState the index (in the table) associated to the state
	 * 	 to edit 
	 * @param newState a new string for the state edited if the action is ADD. 
	 */
	public NodeStateEdit ( ProbNode probNode,
			StateAction stateAction, int indexState, String newState){
		super(probNode.getProbNet());
		this.probNode = probNode;
		this.newState = new State (newState);
		this.stateSelected = probNode.getVariable().getNumStates() - (
				indexState + 1 ) ;
		this.lastPotential = probNode.getPotentials();
		this.stateAction = stateAction;
		this.currentPartitionedInterval = probNode.getVariable().
			getPartitionedInterval();
		this.lastStates = probNode.getVariable().getStates().clone();
	}
	
	
	@Override
	public void doEdit() throws DoEditException {
		State [] newObjectState = null;
		ArrayList<Node> nodes;
		Potential  uniformPotential;
		ArrayList<Potential> potentials;
		switch (stateAction){
		case ADD:
			//assume that the new state is added in last position
			newObjectState = new State [probNode.getVariable().
			                                     getNumStates()+1];
			newObjectState[0] = newState;
			for (int i = 1; i < newObjectState.length; i++ ) {
				newObjectState[i] = probNode.getVariable().getStates()[i-1];
			}
			
			
			probNode.getVariable().setStates(newObjectState);
			
			//set uniform potential for the edited node and children
			uniformPotential = PotentialOperations.
				getUniformPotential(probNet, probNode.getVariable(), 
					probNode.getNodeType());
			potentials = new ArrayList<Potential>();
			potentials.add(uniformPotential);
			
			probNode.setPotentials(potentials);
			
			nodes = probNode.getNode().getChildren();
			for (Node node:nodes){
				potentials = new ArrayList<Potential>();
				ProbNode child = (ProbNode)node.getObject();
				uniformPotential = PotentialOperations.
					getUniformPotential(probNet, child.getVariable(), 
					child.getNodeType());
				potentials.add(uniformPotential);
				child.setPotentials(potentials);
			}
			//if the node is dicretized add a new row in partitionedInterval 
			//field of the node
			if (probNode.getVariable().getVariableType() == VariableType.
					DISCRETIZED){
				PartitionedInterval newPartitionedInterval = 
					getNewPartitionedInterval();
				probNode.getVariable().setPartitionedInterval(
						newPartitionedInterval);
			}
			stateSelected ++;
			break;
		case REMOVE:
				newObjectState = new State [probNode.getVariable().
			                                    getNumStates()-1];
				int i1=0;
				boolean found = false;
				for (State states: probNode.getVariable().getStates()){
					if (i1 != stateSelected || found == true){
						newObjectState[i1] = states;
						i1++;
					}else
						found = true;
				}
				probNode.getVariable().setStates(newObjectState);
			
				
				//set uniform potential for the edited node and children
				uniformPotential = PotentialOperations.
					getUniformPotential(probNet, probNode.getVariable(), 
						probNode.getNodeType());
				potentials = new ArrayList<Potential>();
				potentials.add(uniformPotential);
				
				probNode.setPotentials(potentials);
				
				nodes = probNode.getNode().getChildren();
				for (Node node:nodes){
					potentials = new ArrayList<Potential>();
					ProbNode child = (ProbNode)node.getObject();
					uniformPotential = PotentialOperations.
						getUniformPotential(probNet, child.getVariable(), 
						child.getNodeType());
					potentials.add(uniformPotential);
					child.setPotentials(potentials);
				}
				
				//change current partitioned interval
				if (probNode.getVariable().getVariableType() == VariableType.NUMERIC 
						||probNode.getVariable().getVariableType() == VariableType.DISCRETIZED  ) {
					
					double []oldLimits = currentPartitionedInterval.getLimits();
					boolean []oldBelongs = currentPartitionedInterval.getBelongsToLeftSide();
					
					int positionToRemove = (oldLimits.length -1) -stateSelected ;
					
					ArrayList<Double> newLimits = new ArrayList<Double>(oldLimits.length-1);
					ArrayList<Boolean> newBelongs = new ArrayList<Boolean>(oldLimits.length-1);
					
					int length = oldLimits.length;
					for (int j = 0; j < oldLimits.length; j++) {
						if (j != positionToRemove) {
							newLimits.add(oldLimits[j]);
							newBelongs.add(oldBelongs[j]);
						}
					}
					double[]limits = new double[oldBelongs.length-1];
					boolean []belongs = new boolean[oldBelongs.length-1];
					for (int j = 0; j < newLimits.size();j++) {
						limits[j] = newLimits.get(j);
						belongs[j] = newBelongs.get(j);
					}
					
					probNode.getVariable().setPartitionedInterval(new PartitionedInterval(limits, belongs));
					
				}
				
				break;
		case DOWN:
			if (stateSelected > 0){
				State state = probNode.getVariable().getStates()[stateSelected-1];
				probNode.getVariable().getStates()[stateSelected-1] = 
					probNode.getVariable().getStates()[stateSelected];
				probNode.getVariable().getStates()[stateSelected] = state;
				
			}
			break;
		case UP:
			if (stateSelected < probNode.getVariable().getNumStates()){
				State state = probNode.getVariable().getStates()[stateSelected+1];
				probNode.getVariable().getStates()[stateSelected+1] = 
					probNode.getVariable().getStates()[stateSelected];
				probNode.getVariable().getStates()[stateSelected] = state;
				
			}
				
			break;
		case RENAME:
			if (stateSelected >= 0 && stateSelected < probNode.getVariable().
					getNumStates()){
				newObjectState = new State [probNode.getVariable().
	                                     getNumStates()];
				int j=0;
				for (State states: probNode.getVariable().getStates()){
					newObjectState[j] = states;
					j++;
				}
				newObjectState[stateSelected] = newState;
				probNode.getVariable().setStates(newObjectState);
			
			}
			break;
			
		}
		
		
	}
	
	
	@Override
	public void undo() {
		super.undo();
		ArrayList<Node> nodes;
		probNode.getVariable().setStates(lastStates);
		probNode.setUniformPotential();
		//Update children information
		nodes = probNode.getNode().getChildren();
		for (Node node:nodes){
			ProbNode child = (ProbNode)node.getObject();
			child.setUniformPotential();
		}
		
		if (probNode.getVariable().getVariableType() == VariableType.
				DISCRETIZED){
				probNode.getVariable().setPartitionedInterval(
						currentPartitionedInterval);
		}
			
	}
		
	/**
	 * Gets the new state created if the action was ADD
	 * @return the new state
	 */
	public State getNewState(){
		return newState;
	}
	/**
	 * Gets the new state created if the action was ADD
	 * @return the new state
	 */
	public State getLastState(){
		return lastState;
	}
	public ProbNode getProbNode(){
		return probNode;
	}
	public StateAction getStateAction(){
		return stateAction;
	}
	/**
	 * This method add a new default subInterval, in the current PartitionedInterval 
	 * object
	 * @return
	 * 	The PartitionedInterval object with a new default subInterval
	 */		
	
	private PartitionedInterval getNewPartitionedInterval(){
		double limits [] = currentPartitionedInterval.getLimits();
		double newLimits [] = new double [limits.length + 1];
		boolean belongsToLeftSide [] = currentPartitionedInterval.
			getBelongsToLeftSide();
		boolean newBelongsToLeftSide [] = new boolean [limits.length + 1];
		for (int i = 0; i < limits.length; i++){
			newLimits[ i ] = limits[ i ];
			newBelongsToLeftSide [ i ] = belongsToLeftSide [ i ];
		}
		
		if (currentPartitionedInterval.getMax() == Double.POSITIVE_INFINITY) {
			newLimits[ limits.length -1] = newLimits[ limits.length -2] + probNode.getVariable().getPrecision();
			newLimits[ limits.length ] =  Double.POSITIVE_INFINITY;
		} else {
			newLimits[ limits.length ] = currentPartitionedInterval.getMax() + probNode.getVariable().getPrecision(); 
		}
		newBelongsToLeftSide [ limits.length ] = false;
		return 	new PartitionedInterval(newLimits, newBelongsToLeftSide);
	}
	
	/**
	 * This method gets the new row data when new state is inserted in a 
	 * discretized variable.
	 * @return The row data of the new state
	 */
	public Object[] getNewRowOfData(){
		String firstSymbol = null;
		String secondSymbol = null;
		double limits [] = null;
		boolean belongsToLeftSide [];
		if (stateAction == StateAction.ADD){
			limits = probNode.getVariable().getPartitionedInterval().
				getLimits();
			belongsToLeftSide = probNode.getVariable().
				getPartitionedInterval().getBelongsToLeftSide();
			firstSymbol = (belongsToLeftSide[limits.length-2]? "(" : "[");
			secondSymbol = (belongsToLeftSide[limits.length-1]? "]" : ")");
		} else if (stateAction == StateAction.REMOVE){
			limits = probNode.getVariable().getPartitionedInterval().
				getLimits();
			belongsToLeftSide = probNode.getVariable().
				getPartitionedInterval().getBelongsToLeftSide();
	
			firstSymbol = (belongsToLeftSide[stateSelected]? "(" : "[");
			secondSymbol = (belongsToLeftSide[stateSelected + 1]? "]" : ")");
					
		}
		return new Object[] {"", GUIDefaultStates.getString(probNode.getVariable().
 				getStates()[stateSelected].getName()) , firstSymbol, 
 				limits[ stateSelected], "," , limits[ stateSelected + 1], 
 				secondSymbol };
		
	}
		
}
