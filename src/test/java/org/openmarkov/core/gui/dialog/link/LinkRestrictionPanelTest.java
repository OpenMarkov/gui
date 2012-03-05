package org.openmarkov.core.gui.dialog.link;

import java.util.ArrayList;

import org.junit.Before;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.model.graph.Graph;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;

public class LinkRestrictionPanelTest {
	LinkRestrictionPanel panel = null;

	@Before
	public void setUp() throws Exception {

		Graph graph = new Graph();
		State[] stateA = new State[] { new State("A1"), new State("A2"),
				new State("A3") };
		State[] stateB = new State[] { new State("B1"), new State("B2") };
		Variable varA = new Variable("A", stateA);
		Variable varB = new Variable("B", stateB);
		ArrayList<Variable> variables = new ArrayList();
		variables.add(varA);
		variables.add(varB);
		ProbNet net = new ProbNet();
		Node nodeA = new Node(graph, new ProbNode(net, varA, NodeType.CHANCE));
		ProbNode node = new ProbNode(net, varB, NodeType.CHANCE);
		Node nodeB = new Node(graph, node);
		Link link = new Link(nodeA, nodeB, true);
		try {
			link.initializesRestrictionsPotential();
			link.setCompatibilityValue(stateA[1], stateB[0], 0);
		} catch (NotEnoughMemoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		panel = new LinkRestrictionPanel(link);
//		JFrame frame = new JFrame();
//		frame.add(panel);
//		frame.setSize(600, 400);
//		frame.show();
		
	}

	
}
