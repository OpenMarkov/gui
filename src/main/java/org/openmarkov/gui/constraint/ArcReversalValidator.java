/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.constraint;

import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.TablePotential;

/******
 * This class validates if a link can be reverted
 *
 * @author iago
 *
 */
public class ArcReversalValidator {

	/******
	 * Links can be reverted if each one of its nodes has a table potential.
	 *
	 * @return <code>true</code> if it is so.
	 */
	public static boolean validate(Link<Node> link) {

		Potential potential1 = link.getNode1().getPotentials().get(0);
		Potential potential2 = link.getNode2().getPotentials().get(0);

		return (potential1 instanceof TablePotential && potential2 instanceof TablePotential);
	}
}
