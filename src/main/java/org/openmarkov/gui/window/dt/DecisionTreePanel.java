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
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.type.DecisionAnalysisNetworkType;
import org.openmarkov.core.model.network.type.InfluenceDiagramType;
import org.openmarkov.inference.decompositionIntoSymmetricDANs.DANDecisionTreeEvaluation;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial") public class DecisionTreePanel extends JScrollPane {
	protected DecisionTree jTree;

	public DecisionTreePanel(ProbNet probNet) {
		//DecisionTreeElement root = DecisionTreeBuilder.buildDecisionTree (probNet);
		DecisionTreeElement root = buildDecisionTreeDAN(probNet);
		DecisionTreeModel model = new DecisionTreeModel(root);
		jTree = new DecisionTree(model);
		for (int i = 0; i < jTree.getRowCount(); i++) {
			jTree.expandRow(i);
		}
		setViewportView(jTree);
		setBackground(Color.white);
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
}
