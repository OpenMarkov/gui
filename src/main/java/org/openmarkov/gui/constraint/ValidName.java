/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.constraint;

import org.openmarkov.core.action.NodeNameEdit;
import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.constraint.PNConstraint;
import org.openmarkov.core.model.network.constraint.UtilConstraints;

import java.util.List;

/**
 * checks that the name field is filled and there isn't any node with the same
 * name.
 */
public class ValidName extends PNConstraint {
	// Attributes.
	private String message;

	@Override public boolean checkEdit(ProbNet probNet, PNEdit edit) {
		List<PNEdit> edits = UtilConstraints.getSimpleEditsByType(edit, NodeNameEdit.class);
		for (PNEdit simpleEdit : edits) {
			String name = ((NodeNameEdit) simpleEdit).getNewName();
			String currentName = ((NodeNameEdit) simpleEdit).getPreviousName();
			// if ((name == null) || (name.contentEquals(""))) {
			if (!checkName(name, currentName, probNet)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * This method checks that the name field is filled and there isn't any node
	 * with the same name.
	 *
	 * @return true, if the name field isn't empty and there isn't any node with
	 * this name; otherwise, false.
	 */
	public boolean checkName(String newName, String currentName, ProbNet probNet) {
		// boolean result = true;
        if ((newName == null) || newName.isEmpty()) {
			message = "NodeNameEmpty.Text.Label";
			return false;
        }
        if (!currentName.equals(newName) && existNode(newName.toUpperCase(), probNet)) {
            message = "ConstraintViolated.ValidName.Exists";
            return false;
        }
        /*
		 * if (!result) { jTextFieldNodeName.requestFocus(); return false; }
		 */
		return true;
	}

	/**
	 * This method checks if exists the specified node.
	 *
	 * @param name name of the node to search.
	 * @return true if the node exists; otherwise, false.
	 */
    public static boolean existNode(String name, ProbNet probNet) {
        probNet.getNode(name);
        return true;
    }

	@Override public boolean checkProbNet(ProbNet probNet) {
		List<Variable> variables = probNet.getVariables();
		for (Variable variable : variables) {
			String name = variable.getName();
			if ((name == null) || (name.contentEquals(""))) {
				return false;
			}
		}
		return true;
	}
 
}
