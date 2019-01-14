/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
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
import org.openmarkov.core.model.network.type.NetworkType;
import org.openmarkov.core.oopn.Instance;
import org.openmarkov.core.model.network.type.InfluenceDiagramType;
import org.openmarkov.gui.menutoolbar.menu.ContextualMenuFactory;
import org.openmarkov.gui.menutoolbar.common.ActionCommands;
import org.openmarkov.gui.menutoolbar.menu.TreeContextualMenu;
import org.openmarkov.gui.oopn.VisualInstance;
import org.openmarkov.gui.window.MainPanel;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DANDecisionTreeEvaluation;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.IDDecisionTreeEvaluation;

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
		DecisionTreeElement root = buildDecisionTree(probNet);
		updateVisualInformation(root);

	}

	private void updateVisualInformation(DecisionTreeElement root) {
		DecisionTreeModel model = new DecisionTreeModel(root);
		jTree = new DecisionTree(model);
		jTree.addMouseListener(listener);
		for (int i = 0; i < jTree.getRowCount(); i++) {
			jTree.expandRow(i);
		}
		setViewportView(jTree);
		setBackground(Color.white);

	}


	public static DecisionTreeElement buildDecisionTree(ProbNet probNet) {
		//TODO We are testing with an initial value of 1. The value should be something like 5 or 6
		return buildDecisionTree(probNet,1);
	}
	
	
	public static DecisionTreeElement buildDecisionTree(ProbNet probNet,int depth) {
		DecisionTreeElement root = null;
		NetworkType networkType = probNet.getNetworkType();
		if (networkType instanceof InfluenceDiagramType || networkType instanceof DecisionAnalysisNetworkType) {
			root = new DecisionTreeBranch(probNet);
			DecisionTreeNode child = null;
			try {
				child = (networkType instanceof InfluenceDiagramType?new IDDecisionTreeEvaluation(probNet,depth):new DANDecisionTreeEvaluation(probNet,depth)).getDecisionTree();
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
	
	public void inferenceExpandLevels(int n) {
		DecisionTreeModel auxModel = (DecisionTreeModel)jTree.getModel();
		DecisionTreeBranchPanel root = (DecisionTreeBranchPanel) auxModel.getRoot();
		inferenceExpandLevels(root.getTreeBranch(),null,n);
		updateVisualInformation(root.getTreeBranch());			
	}
	
	private void inferenceExpandLevels(DecisionTreeElement root,DecisionTreeNode parent, int n) {
		if (root instanceof DecisionTreeBranch || ((DecisionTreeNode)root).getNodeType()!= NodeType.UTILITY) {
			if (root instanceof DecisionTreeNode) {
				parent = (DecisionTreeNode) root;
			}
			for (DecisionTreeElement branch : root.getChildren()) {
				inferenceExpandLevels(branch,parent, n);						
			}
		}
		else {
			DecisionTreeNode rootDT = (DecisionTreeNode)root;
			DecisionTreeNode auxRoot = ((DecisionTreeBranch) buildDecisionTree(rootDT.getNetwork(), n)).getChild();				
			if (parent.getNodeType()==NodeType.DECISION || 
					(!(parent.getVariable().getName().equalsIgnoreCase(auxRoot.getVariable().getName())))){
				rootDT.copy(auxRoot);
			}
		}
	}


	public void inferenceExpandAllLevels() {
		inferenceExpandLevels(Integer.MAX_VALUE);		
	}


	private class TreePanelListener implements ActionListener, MouseListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String actionCommand = e.getActionCommand();
			switch (actionCommand) {
				case ActionCommands.TREE_EXPAND_NEXT:
					System.out.println("Expanding some levels");
					// Expand N levels
					inferenceExpandLevels(1);
					break;
				case ActionCommands.TREE_EXPAND_ALL:
					System.out.println("Expanding all levels");
					// Expand all levels
					inferenceExpandAllLevels();
					break;
				case ActionCommands.TREE_OPEN_NETWORK:
					System.out.println("Opening associated network");
					openAssociatedNetwork();
					// Open tree
					break;
				case ActionCommands.TREE_SHOW_CEP:
					System.out.println("Doing something wonderful");
					// Show CEP or utility
					break;
				default:

			}
		}
		

		private void openAssociatedNetwork() {
			
			Object selectedComponent = jTree.getLastSelectedPathComponent();
			if (selectedComponent instanceof DecisionTreeNodePanel) {
				DecisionTreeNodePanel treeNodePanel = (DecisionTreeNodePanel) selectedComponent;
				DecisionTreeNode treeNode = treeNodePanel.getTreeNode();
				MainPanel.getUniqueInstance().getMainPanelListenerAssistant().openNetwork(treeNode.getNetwork());	
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
