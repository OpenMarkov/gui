/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/
package org.openmarkov.core.gui.dialog.costeffectiveness;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.model.network.NetsFactory;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.TablePotentialTest;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;

/**
 * @author manolo
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
	
	
	@Test
	public void testExpansionSimpleSMM() {

		ProbNet network = NetsFactory.createSMMWithoutStateVariable();
		double discount = 1.0;
		FactoryExpandedSMM expandedNetFactory = null;
		try {
			expandedNetFactory = new FactoryExpandedSMM(network, 1, null, 200.0);
			expandedNetFactory.applyDiscountToUtilityNodes(discount);
		} catch (NotEnoughMemoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ProbNet expandedNetwork = expandedNetFactory.getExtendedNet();
		ArrayList<Potential> utilityPotentials = expandedNetwork
				.getPotentialsRole(PotentialRole.UTILITY);

		TablePotential globalPotential = null;
		try {
			globalPotential = DiscretePotentialOperations
					.sum(utilityPotentials);
		} catch (NotEnoughMemoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TablePotentialTest.checkEqualPotentials(globalPotential,globalPotential,maxError);

	}

	
	

}
