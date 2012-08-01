/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/
package org.openmarkov.core.gui.dialog.costeffectiveness;

import java.util.ArrayList;
import org.junit.Test;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.network.NetsFactory;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.TablePotentialTest;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;

/**
 * @author mluque
 *
 */
public class FactoryExpandedSMMTest {
	/**
	 * Maximum error allowed in tests. It could be modified by subclasses
	 * if it is necessary (for example, approximate inference methods).
	 */
	protected double maxError = 1E-6;

	

	/*@Test
	public void test() {
		fail("Not yet implemented");
	}*/
	
	
	/**
	 * Test a SMM with three variables: Treatment, CostOfTreatment and QoL (temporal variable)
	 * It performs a battery of tests: for numSlices = 1, numSlices = 2, ..., numSlices = 100
	 */
	@Test
	public void testExpansionSimpleSMM() {
		double qoLTreat;
		double qoLNoTreat;
		double costTreat;
		double costNoTreat;
		int maximumNumSlices;

		maximumNumSlices = 100;

		qoLTreat = 1.0;
		qoLNoTreat = 0.9;
		costTreat = -2;
		costNoTreat = 0;

		for (int numSlices = 1; numSlices <= maximumNumSlices; numSlices++) {
			
			//Create the SMM and expand it
			ProbNet network = NetsFactory.createSMMWithoutStateVariable(qoLTreat, qoLNoTreat,
					costTreat, costNoTreat);
			double discount = 0.01;
			FactoryExpandedSMM expandedNetFactory = null;
			try {
				expandedNetFactory = new FactoryExpandedSMM(network, numSlices, null, 200.0);
				expandedNetFactory.applyDiscountToUtilityNodes(discount);
			} catch (NotEnoughMemoryException e) {
				e.printStackTrace();
			}
			ProbNet expandedNetwork = expandedNetFactory.getExtendedNet();
			
			//Sum the utility potentials of the expanded network
			ArrayList<Potential> utilityPotentials = expandedNetwork
					.getPotentialsRole(PotentialRole.UTILITY);

			ArrayList<TablePotential> tablePotentials;
			tablePotentials = new ArrayList<>();
			for (Potential auxPotential : utilityPotentials) {
				try {
					tablePotentials.addAll(auxPotential.tableProject(null, null));
				} catch (NotEnoughMemoryException | NonProjectablePotentialException
						| WrongCriterionException e) {
					e.printStackTrace();
				}
			}

			TablePotential globalPotential = null;
			try {
				globalPotential = DiscretePotentialOperations.sum(tablePotentials);
			} catch (NotEnoughMemoryException e) {
				e.printStackTrace();
			}
			
			//Create a potential with the expected results
			double ratio = 1.0 / (1.0 + discount);
			double sumQoLTreatTerms = sumTermsGeometricProgression(qoLTreat, ratio, numSlices);
			double sumQoLNoTreatTerms = sumTermsGeometricProgression(qoLNoTreat, ratio, numSlices);
			ArrayList<Variable> variablesUtil;

			variablesUtil = new ArrayList<>();
			try {
				variablesUtil.add(expandedNetwork.getVariable("Treatment"));
			} catch (ProbNodeNotFoundException e) {
				e.printStackTrace();
			}
			variablesUtil.add(expandedNetwork.decisionCriteria);
			TablePotential expectedPotential = null;
			try {
				expectedPotential = new TablePotential(variablesUtil, PotentialRole.UTILITY);
			} catch (NotEnoughMemoryException e) {
				e.printStackTrace();
			}
			// TODO We should consider here the order of the states of
			// DecisionCriteria variable
			double values[] = { costTreat, costNoTreat, sumQoLTreatTerms, sumQoLNoTreatTerms };
			expectedPotential.setValues(values);
			
			//Compare the global utility potential of the expanded network with the expected results
			TablePotentialTest.checkEqualPotentials(globalPotential, globalPotential, maxError);
		}

	}
	
	/**
	 * @param firstTerm
	 * @param ratio
	 * @param numTerms
	 * @return The sum of 'numTerms' terms of a geometric progression whose first term is 'firsTerm'
	 * and its ratio is 'ratio'
	 */
	private double sumTermsGeometricProgression(double firstTerm,double ratio,int numTerms){
		return (firstTerm-firstTerm*Math.pow(ratio, numTerms))/(1.0-ratio);
	}

	
	

}
