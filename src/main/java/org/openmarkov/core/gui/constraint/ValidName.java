package org.openmarkov.core.gui.constraint;

import java.util.ArrayList;

import javax.swing.event.UndoableEditEvent;

import org.openmarkov.core.action.NodeNameEdit;
import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.action.PNUndoableEditEvent;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.constraint.PNConstraint;
import org.openmarkov.core.model.network.constraint.UtilConstraints;

/** checks that the name field is filled and there isn't any node with the same 
 * name. */
public class ValidName implements PNConstraint {

	// Attributes.
	private static ValidName constraint = null;
	private String message;
	
	// Constructor
	/** This constructor is private to not allow anyone to invoke it. */
	private ValidName() {
	}
	
	// Methods
	/** Singleton pattern.
	 * @return The unique instance. 
	 *  <code>DistinctVariableNames</code> */
	public static PNConstraint getUniqueInstance() {
		if (constraint == null) {
			constraint = new ValidName();
		}
		return constraint;
	}
	

	public boolean checkEvent(UndoableEditEvent event) 
	throws NonProjectablePotentialException, WrongCriterionException {
		ArrayList<PNEdit> edits;
		try {
			edits = UtilConstraints.getEditsType(event, 
					NodeNameEdit.class);
			for (PNEdit edit : edits) {
				String name = ((NodeNameEdit)edit).getNewName();
				String currentName = ((NodeNameEdit)edit).getPreviousName();
				ProbNet probNet =((NodeNameEdit)edit).getProbNet();
				//if ((name == null) || (name.contentEquals(""))) {
				if (!checkName(name, currentName, probNet)){
					return false;
				}
			}
		} catch (NotEnoughMemoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * This method checks that the name field is filled and there isn't any node
	 * with the same name.
	 * 
	 * @return true, if the name field isn't empty and there isn't any node with
	 *         this name; otherwise, false.
	 */
	public boolean checkName(String newName, String currentName, ProbNet probNet) {

		//boolean result = true;

		if ((newName == null) || newName.equals( "" )) {
			message = "NodeNameEmpty.Text.Label";
			
			return false;
		} else if (!currentName.equals( newName )
			&& existNode( newName.toUpperCase(), probNet )) {
			message = "ConstraintViolationException.ValidName.Exists";
			return false;
		}
		/*if (!result) {
			jTextFieldNodeName.requestFocus();
			return false;
		}*/
		return true;
	}

	
	public void undoableEditWillHappen(PNUndoableEditEvent event)
	throws ConstraintViolationException, CanNotDoEditException, 
	NonProjectablePotentialException, WrongCriterionException {
		if (!checkEvent(event)) {
			throw new ConstraintViolationException(message);
		}
	}

	
	public void undoableEditHappened(UndoableEditEvent arg0) {
	}
	
	/**
	 * This method checks if exists the specified node.
	 * 
	 * @param name
	 *            name of the node to search.
	 * @return true if the node exists; otherwise, false.
	 */
	public boolean existNode(String name, ProbNet probNet) {
		
		try {
			probNet.getProbNode(name);
			return true;
		} catch (ProbNodeNotFoundException e) {
			return false;
		}
	}


	public String toString() {
		return this.getClass().getName();
	}

	
	public void undoEditHappened(PNUndoableEditEvent event) {
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


	
}
