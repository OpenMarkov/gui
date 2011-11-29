package org.openmarkov.core.gui.action;


import static org.junit.Assert.assertEquals;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.gui.graphic.VisualChanceNode;
import org.openmarkov.core.gui.graphic.VisualDecisionNode;
import org.openmarkov.core.gui.graphic.VisualNode;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.type.InfluenceDiagramType;


/**
 * This class tests the action of undoing the movement of nodes.
 *
 * @author mpalacios
 */
public class MoveNodeEditTest {
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
	 * @throws Exception if an error occurrs.
	 */
	@Before
	public void setUp() throws Exception {
		
		probNet = new ProbNet( InfluenceDiagramType.getUniqueInstance() );
		probNet.setName("Influence diagram");
		probNet.setComment("Influence diagram for testing");
		variableA = new Variable("A");
		variableB = new Variable("B");
		
		node1 = probNet.addVariable(variableA, NodeType.CHANCE);
	
		node1.getNode().setCoordinateX(100.0);
		node1.getNode().setCoordinateY(150.0);
		node2 = probNet.addVariable(variableB, NodeType.DECISION);
		node2.getNode().setCoordinateX(57.0);
		node2.getNode().setCoordinateY(49.0);
		
		VisualChanceNode visualNodeA = new VisualChanceNode(node1,null);
		VisualDecisionNode visualNodeB = new VisualDecisionNode(node2,null);
		visualNodeA.setTemporalPosition(new Point2D.Double (21,160));
		visualNodeB.setTemporalPosition(new Point2D.Double (101,99));
		
		ArrayList<VisualNode> movedNodes = new ArrayList<VisualNode>(2);
		movedNodes.add(visualNodeA);
		movedNodes.add(visualNodeB);
		
		probNet.getPNESupport().setWithUndo(true);
		MoveNodeEdit moveNodeEdit = new MoveNodeEdit(movedNodes);
			
		probNet.getPNESupport().announceEdit(moveNodeEdit);
		probNet.getPNESupport().doEdit(moveNodeEdit);
	}


	/**
	 * This method undoes and redoes several times.
	 *
	 * @throws Exception if an error occurrs.
	 */
	@Test
	public final void testUndoRedo() throws Exception {
		
		assertEquals(node1.getNode().getCoordinateX(), 21.0);
		assertEquals(node1.getNode().getCoordinateY(), 160.0);
		assertEquals(node2.getNode().getCoordinateX(), 101.0);
		assertEquals(node2.getNode().getCoordinateY(), 99.0);
		probNet.getPNESupport().undo();
		assertEquals(node1.getNode().getCoordinateX(), 100.0);
		assertEquals(node1.getNode().getCoordinateY(), 150.0);
		assertEquals(node2.getNode().getCoordinateX(), 57.0);
		assertEquals(node2.getNode().getCoordinateY(), 49.0);
		probNet.getPNESupport().redo();
		assertEquals(node1.getNode().getCoordinateX(), 21.0);
		assertEquals(node1.getNode().getCoordinateY(), 160.0);
		assertEquals(node2.getNode().getCoordinateX(), 101.0);
		assertEquals(node2.getNode().getCoordinateY(), 99.0);
		probNet.getPNESupport().undo();
		assertEquals(node1.getNode().getCoordinateX(), 100.0);
		assertEquals(node1.getNode().getCoordinateY(), 150.0);
		assertEquals(node2.getNode().getCoordinateX(), 57.0);
		assertEquals(node2.getNode().getCoordinateY(), 49.0);
		probNet.getPNESupport().redo();
	}
}
