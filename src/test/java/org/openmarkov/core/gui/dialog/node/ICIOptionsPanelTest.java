/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog.node;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.constraint.NoCycle;
import org.openmarkov.core.model.network.constraint.OnlyDirectedLinks;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;

/**
 * This class tests the TablePotentialPanelTest class (not the visual
 * behaviour).
 * 
 * @author jlgozalo
 * @version 1.0
 */
public class ICIOptionsPanelTest {

	ICIOptionsPanel panel = null;

	private  ProbNet probNet = null;
	private  ProbNode probNode = null;
	private Variable A;
	private Variable B;
	private Variable U;
	private Variable D;
	

	@Before
	public void setUp() throws Exception {
 
	
		panel = new ICIOptionsPanel(false);
		
		probNet = createSimpleProbNet();
		probNode = probNet.getProbNodes().get(0);
	}

	/**
	 * test to verify the getter and setter methods (non visual elements)
	 */
	@Test
	public void testGetterAndSetters() {

		assertFalse(panel.isNewNode());
		panel.setNewNode(true);
		assertTrue(panel.isNewNode());
		//new parameters for setNodeProperties method
	}

	/**
	 * auxiliary class to create a simple ProbNet
	 * 
	 * @throws Exception
	 */
	public ProbNet createSimpleProbNet() throws Exception {

		ArrayList<Variable> aVariables;
		ArrayList<Variable> abVariables;
		ArrayList<Variable> adVariables;
		TablePotential pA;
		TablePotential pBA;
		TablePotential pU;
		ProbNet simpleProbNet;
		
		// create simpleProbNet
		// create variables
		A = new Variable("A", 2);
		B = new Variable("B", 3);
		D = new Variable("D", 4);
		U = new Variable("U");
		// create Arrays of variables used in potentials
		aVariables = new ArrayList<Variable>(1);
		aVariables.add(A);
		
		abVariables = new ArrayList<Variable>(2);
		abVariables.add(B);
		abVariables.add(A);
		adVariables = new ArrayList<Variable>(2);
		adVariables.add(A);
		adVariables.add(D);
		// create potentials
		pA = new TablePotential(aVariables,
				PotentialRole.CONDITIONAL_PROBABILITY);
		pA.values[0] = 0.9;
		pA.values[1] = 0.1;
		pBA = new TablePotential(abVariables, 
				PotentialRole.CONDITIONAL_PROBABILITY);
		pBA.values[0] = 0.2;
		pBA.values[1] = 0.8;
		pBA.values[2] = 0.9;
		pBA.values[3] = 0.1;
		pU = new TablePotential(adVariables, 
				PotentialRole.CONDITIONAL_PROBABILITY);
		pU.setUtilityVariable(U);
		pU.values[0] = 1;
		pU.values[1] = 2;
		pU.values[2] = 3;
		pU.values[3] = 4;
		simpleProbNet = new ProbNet();
		simpleProbNet.addConstraint(new NoCycle(), true);
		simpleProbNet.addConstraint(new OnlyDirectedLinks(), true);
		// add potentials and variables
		simpleProbNet.addPotential(pA); // add variable and potential
		simpleProbNet.addProbNode(D, NodeType.DECISION);
		simpleProbNet.addPotential(pU);
		simpleProbNet.addPotential(pBA);
		simpleProbNet.addLink(B, D, true);
        //network = new ProbNet(simpleProbNet);
		//additionalProperties.setNetwork(simpleProbNet);
		
		return simpleProbNet;
	}

}
