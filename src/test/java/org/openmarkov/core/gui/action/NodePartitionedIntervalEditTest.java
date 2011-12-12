/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.action;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.action.StateAction;
import org.openmarkov.core.action.VariableTypeEdit;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.type.InfluenceDiagramType;

/**
 * This class tests the action of undoing the changes in the node's name of nodes.
 *
 * @author mpalacios
 */
public class NodePartitionedIntervalEditTest {

	/**
	 * Network for testing.
	 */
	private ProbNet probNet = null;

	/**
	 * A whose position will be undone and redone.
	 */
	private ProbNode node1 = null;

	/**
	 * A whose position will be undone and redone.
	 */
	private ProbNode node2 = null;

	private Variable variableA;
	private Variable variableB;
	/**
	 * This method creates a network and various nodes and various links.
	 *
	 * @throws Exception if an error occurs.
	 */
	@Before
	public void setUp() throws Exception {

		probNet = new ProbNet( InfluenceDiagramType.getUniqueInstance() );
		probNet.setName("Influence diagram");
		probNet.setComment("Influence diagram for testing");
		State [] states = {new State("absent"), new State("present")};
		variableA = new Variable("A",states );
		variableB = new Variable("B");

		node1 = probNet.addVariable(variableA, NodeType.CHANCE);

		node1.getNode().setCoordinateX(100.0);
		node1.getNode().setCoordinateY(150.0);
		node2 = probNet.addVariable(variableB, NodeType.DECISION);
		node2.getNode().setCoordinateX(57.0);
		node2.getNode().setCoordinateY(49.0);

		probNet.getPNESupport().setWithUndo(true);
		
		VariableTypeEdit variableTypeEdit = new VariableTypeEdit (
				node1, VariableType.DISCRETIZED );
		
		probNet.getPNESupport().announceEdit(
				variableTypeEdit);
		probNet.getPNESupport().doEdit(
				variableTypeEdit);

		NodePartitionedIntervalEdit nodePartitionedIntervalEdit = 
			new NodePartitionedIntervalEdit(node1, 
					StateAction.MODIFYDELIMITERINTERVAL, 0, true);
		
		probNet.getPNESupport().announceEdit(
					nodePartitionedIntervalEdit);
		probNet.getPNESupport().doEdit(
					nodePartitionedIntervalEdit);
	}


	/**
	 * This method undoes and redoes several times the node's name.
	 *
	 * @throws Exception if an error occurs.
	 */
	@Test
	public final void testUndoRedo() throws Exception {

		assertEquals(node1.getVariable().getPartitionedInterval().
				getBelongsToLeftSide(0), false);
		probNet.getPNESupport().undo();

		assertEquals(node1.getVariable().getPartitionedInterval().
				getBelongsToLeftSide(0), true);
		probNet.getPNESupport().redo();
		
		assertEquals(node1.getVariable().getPartitionedInterval().
				getBelongsToLeftSide(0), false);
		probNet.getPNESupport().undo();

		assertEquals(node1.getVariable().getPartitionedInterval().
				getBelongsToLeftSide(0), true);
		probNet.getPNESupport().redo();


		
	}


}
