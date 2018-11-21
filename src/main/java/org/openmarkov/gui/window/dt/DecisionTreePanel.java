/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.window.dt;

import org.openmarkov.core.dt.DecisionTreeBranch;
import org.openmarkov.core.dt.DecisionTreeBuilder;
import org.openmarkov.core.dt.DecisionTreeElement;
import org.openmarkov.core.dt.DecisionTreeNode;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.type.DecisionAnalysisNetworkType;
import org.openmarkov.core.model.network.type.InfluenceDiagramType;
import org.openmarkov.gui.menutoolbar.menu.ContextualMenuFactory;
import org.openmarkov.gui.menutoolbar.common.ActionCommands;
import org.openmarkov.gui.menutoolbar.menu.TreeContextualMenu;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DANDecisionTreeEvaluation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

@SuppressWarnings("serial") public class DecisionTreePanel extends JScrollPane {
	protected DecisionTree jTree;
	private ContextualMenuFactory contextualMenuFactory;
	private TreePanelListener listener;

	public DecisionTreePanel(ProbNet probNet) {
		listener = new TreePanelListener();
		contextualMenuFactory = new ContextualMenuFactory(listener);

		//DecisionTreeElement root = DecisionTreeBuilder.buildDecisionTree (probNet);
		DecisionTreeElement root = buildDecisionTreeDAN(probNet);
		DecisionTreeModel model = new DecisionTreeModel(root);
		jTree = new DecisionTree(model);
		jTree.addMouseListener(listener);
		for (int i = 0; i < jTree.getRowCount(); i++) {
			jTree.expandRow(i);
		}
		setViewportView(jTree);
		setBackground(Color.white);

	}

	public static DecisionTreeElement buildDecisionTreeDAN(ProbNet probNet) {
		DecisionTreeElement root = null;
		if (probNet.getNetworkType() instanceof InfluenceDiagramType) {
			root = DecisionTreeBuilder.buildDecisionTreeFromID(probNet);
		} else if (probNet.getNetworkType() instanceof DecisionAnalysisNetworkType) {
			root = new DecisionTreeBranch(probNet);
			DecisionTreeNode child = null;
			try {
				child = new DANDecisionTreeEvaluation(probNet).getDecisionTree();
			} catch (NotEvaluableNetworkException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			((DecisionTreeBranch) root).setChild(child);
		}
		return root;
	}

	/**
	 * Returns the zoom.
	 *
	 * @return the zoom.
	 */
	protected double getZoom() {
		return jTree.getZoom();
	}

	/**
	 * Sets the zoom.
	 *
	 * @param zoom the zoom to set.
	 */
	protected void setZoom(Double zoom) {
		jTree.setZoom(zoom);
		repaint();
	}

	private class TreePanelListener implements ActionListener, MouseListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String actionCommand = e.getActionCommand();
			switch (actionCommand) {
				case ActionCommands.TREE_EXPAND_NEXT:
					System.out.println("Expanding some levels");
					// Expand N levels
					break;
				case ActionCommands.TREE_EXPAND_ALL:
					System.out.println("Expanding all levels");
					// Expand all levels
					break;
				case ActionCommands.TREE_OPEN_NETWORK:
					System.out.println("Opening associated network");
					// Expand all levels
					break;
				case ActionCommands.TREE_EXTRA_OPTION:
					System.out.println("Doing something wonderful");
					// Expand all levels
					break;
				default:

			}
		}

		/* Listener methods */
		// Open tree contextual menu on right click
		@Override
		public void mouseClicked(MouseEvent e) {

			if (SwingUtilities.isRightMouseButton(e)) {

				int row = jTree.getClosestRowForLocation(e.getX(), e.getY());
				jTree.setSelectionRow(row); // Select the right-clicked component

				/* Show menu only if the tree element:
					1. is a node
					2. is a chance or decision one
				*/
				Object selectedComponent = jTree.getLastSelectedPathComponent();
				if (selectedComponent instanceof DecisionTreeNodePanel) {
					NodeType nodeType = ((DecisionTreeNodePanel) selectedComponent).getNodeType();
					if (nodeType == NodeType.CHANCE || nodeType == NodeType.DECISION) {
						// Get menu from the contextualMenuFactory
						TreeContextualMenu treeMenu = (TreeContextualMenu) contextualMenuFactory.getTreeContextualMenu();
						treeMenu.show(e.getComponent(), e.getX(), e.getY());
					}
				}

			}
		}

		public void mousePressed(MouseEvent mouseEvent) { }
		public void mouseReleased(MouseEvent mouseEvent) { }
		public void mouseEntered(MouseEvent mouseEvent) { }
		public void mouseExited(MouseEvent mouseEvent) { }
	}
}
