/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.dialog.node;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.constraint.NoCycle;
import org.openmarkov.core.model.network.constraint.OnlyDirectedLinks;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.ExactDistrPotential;
import org.openmarkov.core.model.network.potential.TablePotential;

/**
 * This class tests the TablePotentialPanelTest class (not the visual
 * behavior).
 * 
 * @author jlgozalo
 * @author carmenyago -->changed the tablePotential of U for a ExactDistrPotential; minor changes
 * @version 1.0
 */
public class ICIOptionsPanelTest {

	ICIOptionsPanel panel = null;

	private  ProbNet probNet = null;
	private  Node node = null;
	private Variable A;
	private Variable B;
	private Variable U;
	private Variable D;
	

	@Before
	public void setUp() throws Exception {
 
	
		panel = new ICIOptionsPanel(false);
		
		probNet = createSimpleProbNet();
		node = probNet.getNodes().get(0);
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
		//CMI Now the list is U, A, D
		//ArrayList<Variable> adVariables;
		ArrayList<Variable> uadVariables;
		//CMF
		TablePotential pA;
		TablePotential pBA;
		// CMI-->Now it is ExactDistrPotential
		// TablePotential pU;
		ExactDistrPotential pU;
		// CMF
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
		//CMI Filling the list for the node U
		/*
		adVariables = new ArrayList<Variable>(2);
		adVariables.add(A);
		adVariables.add(D);
		*/
		uadVariables = new ArrayList<Variable>(3);
		uadVariables.add(U);
		uadVariables.add(A);
		uadVariables.add(D);
		//CMF
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
		//CMI Creating the ExactDistrPotential for Utility nodes
		/*
		pU = new TablePotential(adVariables, 
				PotentialRole.CONDITIONAL_PROBABILITY);
		pU.setUtilityVariable(U);
		*/
	
		pU = new ExactDistrPotential(uadVariables,
				PotentialRole.CONDITIONAL_PROBABILITY);
		//CMF
		//CMI adding values to pU.getTablePotential
		/*
		pU.values[0] = 1;
		pU.values[1] = 2;
		pU.values[2] = 3;
		pU.values[3] = 4;
		*/
		pU.getTablePotential().values[0] = 1;
		pU.getTablePotential().values[1] = 2;
		pU.getTablePotential().values[2] = 3;
		pU.getTablePotential().values[3] = 4;
		//CMF
		simpleProbNet = new ProbNet();
		simpleProbNet.addConstraint(new NoCycle(), true);
		simpleProbNet.addConstraint(new OnlyDirectedLinks(), true);
		// add potentials and variables
		simpleProbNet.addPotential(pA); // add variable and potential
		simpleProbNet.addNode(D, NodeType.DECISION);
		simpleProbNet.addPotential(pU);
		simpleProbNet.addPotential(pBA);
		simpleProbNet.addLink(B, D, true);
        //network = new ProbNet(simpleProbNet);
		//additionalProperties.setNetwork(simpleProbNet);
		
		return simpleProbNet;
	}

}
