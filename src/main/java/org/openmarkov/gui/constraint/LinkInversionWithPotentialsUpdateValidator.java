/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.constraint;

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
			Node node1 = link.getNode1();
			Node node2 = link.getNode2();
			valid = validPotentials(node1, node2) && validNewLinks(node1, node2);
		}
		return valid;
	}

	private static boolean validNewLinks(Node node1, Node node2) {
		
		ProbNet probNet = node1.getProbNet();
		Variable variable1 = node1.getVariable();
		Variable variable2 = node2.getVariable();
		
		ProbNet newProbNet = probNet.copy();
		Node newNode1 = newProbNet.getNode(variable1);
		Node newNode2 = newProbNet.getNode(variable2);
		List<Node> parentsNode1 = newNode1.getParents();
		for (Node node : parentsNode1) {
			newProbNet.addLink(node, newNode2, true);
		}
		List<Node> parentsNode2 = newNode2.getParents();
		for (Node node : parentsNode2) {
			newProbNet.addLink(node, newNode1, true);
		}
		newProbNet.removeLink(variable1, variable2, true);
		newProbNet.addLink(newNode2, newNode1, true);

		PNConstraint noCycle = new NoCycle();
		return noCycle.checkProbNet(newProbNet);
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
