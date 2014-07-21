/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.network;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.gui.dialog.common.OkCancelHorizontalDialog;
import org.openmarkov.core.gui.dialog.treeadd.TreeADDCellRenderer;
import org.openmarkov.core.gui.dialog.treeadd.TreeADDEditorPanel;
import org.openmarkov.core.inference.InferenceAlgorithm;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;

@SuppressWarnings("serial")
public class OptimalStrategyDialog extends OkCancelHorizontalDialog{

	public OptimalStrategyDialog(Window owner, ProbNet probNet, InferenceAlgorithm inferenceAlgorithm) throws IncompatibleEvidenceException, UnexpectedInferenceException {
		super(owner);
		TreeADDCellRenderer cellRenderer = new TreeADDCellRenderer(probNet);
		ProbNet dummyProbNet = new ProbNet();
		Node dummyNode = new Node(dummyProbNet, new Variable("Global utility"), NodeType.UTILITY);
		dummyNode.setPotential(inferenceAlgorithm.getOptimalStrategy());
		TreeADDEditorPanel treeADDEditorPanel = new TreeADDEditorPanel(cellRenderer, dummyNode);
		
		setMinimumSize(new Dimension(500, 500));
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		treeADDEditorPanel.setMinimumSize(new Dimension(450, 400));
		panel.add(treeADDEditorPanel, BorderLayout.CENTER);
		
		add(panel);
		pack();
        
        Toolkit toolkit = Toolkit.getDefaultToolkit ();
        Dimension screenSize = toolkit.getScreenSize ();
        int x = (int) (screenSize.getWidth() - getSize().getWidth()) / 2;
        int y = (int) (screenSize.getHeight() - getSize().getHeight()) / 2;
        setLocation(new Point(x, y));
        setTitle(stringDatabase.getString("Decision.ShowOptimalStrategy.Title"));
        setResizable(true);
	}
	
	

}
