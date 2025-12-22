/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.validator;

import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.constraint.NoCycle;
import org.openmarkov.core.model.network.constraint.PNConstraint;
import org.openmarkov.core.model.network.potential.AugmentedTable;
import org.openmarkov.core.model.network.potential.AugmentedTablePotential;
import org.openmarkov.core.model.network.potential.BinomialPotential;
import org.openmarkov.core.model.network.potential.FunctionPotential;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.SameAsPrevious;
import org.openmarkov.core.model.network.potential.UnivariateDistrPotential;

import java.util.List;

/******
 * This class validates if a link can be inverted arc-reversal style
 *
 * @author iagoparís - summer
 * @author Manuel Arias
 */
public class LinkInversionWithPotentialsUpdateValidator {

	/**
	 * A link can be inverted when two conditions are met:<ol>
	 * <li>Each potential attached to its two nodes is a TablePotential or can be projected to it.</li>
	 * <li>The new links created do not create a cycle.</li>
	 * </ol>
	 * @return boolean
	 */
	public static boolean validate(Link<Node> link) {

		boolean valid = false;
		if (link.isDirected()) {
            Node node1 = link.getFrom();
            Node node2 = link.getTo();
			valid = validNode(node1) && validNode(node2) && validNewLinks(node1, node2);
		}
		return valid;
	}

	private static boolean validNewLinks(Node node1, Node node2) {

		// Make a probNet copy in order to not change the original network.
		ProbNet probNet = node1.getProbNet();
		ProbNet newProbNet = probNet.copy();

		// Remove and create the links related to arc inversion.
		Variable variable1 = node1.getVariable();
		Variable variable2 = node2.getVariable();
		newProbNet.removeLink(variable1, variable2, true);
		Node newNode1 = newProbNet.getNode(variable1);
		Node newNode2 = newProbNet.getNode(variable2);
		newProbNet.addLink(newNode2, newNode1, true);
        newNode1.getParents().
			forEach(parentsNode1 -> newProbNet.addLink(parentsNode1, newNode2, true));
        newNode2.getParents().
			forEach(parentsNode2 -> newProbNet.addLink(parentsNode2, newNode1, true));

		// Check cycles
		PNConstraint noCycle = new NoCycle();
        return noCycle.isMetBy(newProbNet);
	}

	/**
	 * A node is valid when is a chance node and it contains a valid potential type.
	 * @return boolean
	 */
	private static boolean validNode(Node node) {

		boolean validNode = false;
		if (node.getNodeType() == NodeType.CHANCE) {
			List<Potential> potentials = node.getPotentials();
			validNode = !potentials.isEmpty() && validPotentialType(potentials.get(0));
		}
		return validNode;
	}

	/**
	 * A potential is valid when can be projected to a TablePotential.
	 * @return boolean
	 */
	private static boolean validPotentialType(Potential potential) {

		return (!(potential instanceof AugmentedTable ||
				potential instanceof AugmentedTablePotential ||
				potential instanceof BinomialPotential ||
				potential instanceof FunctionPotential ||
				potential instanceof SameAsPrevious ||
				potential instanceof UnivariateDistrPotential));
	}

    /**
     *
     * @param node1
     * @param node2
     * @return boolean
     */
    private static boolean validPotentials(Node node1, Node node2) {

        boolean validPotentials = false;
        if (node1.getNodeType() == NodeType.CHANCE && node2.getNodeType() == NodeType.CHANCE) {
            List<Potential> potentials1 = node1.getPotentials();
            List<Potential> potentials2 = node2.getPotentials();
            if (!potentials1.isEmpty() && !potentials1.isEmpty()) {
                validPotentials = validPotential(potentials1.get(0)) && validPotential(potentials2.get(0));
            }
        }
        return validPotentials;
    }

    private static boolean validPotential(Potential potential) {

        return (!(potential instanceof AugmentedTable ||
                potential instanceof AugmentedTablePotential ||
                potential instanceof BinomialPotential ||
                potential instanceof FunctionPotential ||
                potential instanceof SameAsPrevious ||
                potential instanceof UnivariateDistrPotential));
    }

}
