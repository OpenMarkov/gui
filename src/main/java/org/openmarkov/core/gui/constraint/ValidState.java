package org.openmarkov.core.gui.constraint;

import java.util.ArrayList;

import javax.swing.event.UndoableEditEvent;

import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.action.StateAction;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.action.NodeStateEdit;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.PropertyNames;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.constraint.PNConstraint;
import org.openmarkov.core.model.network.constraint.UtilConstraints;

/** Checks that the state field is filled and there isn't any node
 * with the same name. */
public class ValidState extends PNConstraint implements PropertyNames {

	// Attributes.
	private String message;
	
	public boolean checkEvent(UndoableEditEvent event) 
	throws NotEnoughMemoryException, NonProjectablePotentialException, 
	WrongCriterionException {
		ArrayList<PNEdit> edits =	UtilConstraints.getEditsType(event, 
				NodeStateEdit.class);
		for (PNEdit edit : edits) {
			State state = ((NodeStateEdit)edit).getNewState();
			State currentState = ((NodeStateEdit)edit).getLastState();
			ProbNode probNode =((NodeStateEdit)edit).getProbNode();
			StateAction stateAction =((NodeStateEdit)edit).getStateAction();
			//if ((name == null) || (name.contentEquals(""))) {
			if (!checkState(state.getName(), currentState.getName(), 
					probNode, stateAction)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * This method checks that the state field is filled and there isn't any node
	 * with the same name.
	 * 
	 * @return true, if the state field isn't empty and there isn't any node with
	 *         this name; otherwise, false.
	 */
	public boolean checkState(String newState, String currentState, 
			ProbNode probNode, StateAction stateAction) {
        
		switch (stateAction){
		 case RENAME:
			 
         case ADD:
        	 if ((newState == null) || newState.equals( "" )) {
     			message = "NodeStateEmpty.Text.Label";
     			
     			return false;
     		} else if	(existState( newState, probNode )) {
     						message = "DuplicatedState.Text.Label";
     						return false;
     					}
        	 break;
     	 case REMOVE:
     		 
        	 
         }
		
		return true;
		
	}

	/**
	 * This method checks if exists the state specified.
	 * 
	 * @param name
	 *            name of the node to search.
	 * @return true if the state exists; otherwise, false.
	 */
	public boolean existState(String state, ProbNode probNode) {
		
		for (State states: probNode.getVariable().getStates()){
			if (states.getName().toUpperCase().equals(state.toUpperCase())){
				return true;
			}
		}
		return false;
	}

	
	public boolean checkProbNet(ProbNet probNet) {
		ArrayList<Variable> variables = probNet.getVariables();
		for (Variable variable : variables) {
			String name = variable.getName(); 
			if ((name == null) || (name.contentEquals(""))) {
				return false;
			}
		}
		return true;
	}

    @Override
    protected String getMessage ()
    {
        return message;
    }

	
}
