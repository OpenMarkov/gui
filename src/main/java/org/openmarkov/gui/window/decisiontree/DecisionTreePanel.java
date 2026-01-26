/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.window.decisiontree;

import org.openmarkov.core.model.decisiontree.DecisionTreeBranch;
import org.openmarkov.core.model.decisiontree.DecisionTreeElement;
import org.openmarkov.core.model.decisiontree.DecisionTreeNode;
import org.openmarkov.core.exception.*;
import org.openmarkov.core.inference.MulticriteriaOptions.Type;
import org.openmarkov.core.model.network.CEP;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.type.DecisionAnalysisNetworkType;
import org.openmarkov.core.model.network.type.NetworkType;

import org.openmarkov.core.model.network.type.InfluenceDiagramType;
import org.openmarkov.gui.menutoolbar.menu.ContextualMenuFactory;
import org.openmarkov.gui.dialog.costeffectiveness.CEPDialog;
import org.openmarkov.gui.menutoolbar.common.ActionCommands;
import org.openmarkov.gui.menutoolbar.menu.TreeContextualMenu;
import org.openmarkov.gui.util.TreeNodeToDot;
import org.openmarkov.gui.window.MainGUI;
import org.openmarkov.inference.algorithm.decompositionIntoSymmetricDANs.DecompositionGenerateDecisionTree;

import javax.swing.*;
import javax.swing.tree.TreeModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

/**
 * A scrollable container that displays and manages a visual decision tree.
 * It handles tree generation, zoom levels, and dynamic expansion through inference.
 */
@SuppressWarnings("serial") 
 public class DecisionTreePanel extends JScrollPane {

    /** The visual tree component. */
	protected VisualDecisionTree jTree;
    
    /** Factory for creating contextual menus based on node types. */
    private ContextualMenuFactory contextualMenuFactory;

    /** Listener for mouse and action events. */
    private TreePanelListener listener;
    
    public VisualDecisionTree getJTree() {
        return jTree;
    }
    
    /**
     * Constructs a panel and builds the decision tree for the given network.
     * @param probNet The probabilistic network to represent.
     * 
     * @throws NotEvaluableNetworkException
     * @throws IncompatibleEvidenceException
     * @throws NonProjectablePotentialException
     * @throws PotentialOperationException.DifferentSizesInPotentialsAndStates
     * @throws NotSupportedOperationException
     */
    public DecisionTreePanel(ProbNet probNet) throws NotEvaluableNetworkException, IncompatibleEvidenceException, NonProjectablePotentialException, PotentialOperationException.DifferentSizesInPotentialsAndStates, NotSupportedOperationException {
        listener = new TreePanelListener();
        contextualMenuFactory = new ContextualMenuFactory(listener);
        
        //DecisionTreeElement root = DecisionTreeBuilder.buildDecisionTree (probNet);
        DecisionTreeElement root = buildDecisionTree(probNet);
        updateVisualInformation(root); 
    }
    
    /**
     * Rebuilds the visual model and refreshes the viewport.
     * @param root The root element of the decision tree.
     */
    private void updateVisualInformation(DecisionTreeElement root) {
        DecisionTreeModel model = new DecisionTreeModel(root);
        jTree = new VisualDecisionTree(model);
        jTree.addMouseListener(listener);
        for (int i = 0; i < jTree.getRowCount(); i++) {
            jTree.expandRow(i);
        }
        setViewportView(jTree);
        setBackground(Color.white);
        
    }
    
    private static final int DEFAULT_DEPTH = 5;
    
    /**
     * Builds a decision tree from a ProbNet with a default depth.
     * @see #buildDecisionTree(ProbNet, int, EvidenceCase)
     */
    public static DecisionTreeElement buildDecisionTree(ProbNet probNet) throws NotEvaluableNetworkException, IncompatibleEvidenceException, NonProjectablePotentialException, PotentialOperationException.DifferentSizesInPotentialsAndStates, NotSupportedOperationException {
        return buildDecisionTree(probNet, DEFAULT_DEPTH);
    }
    
    /**
	 * Builds a decision tree from a ProbNet up to a specified depth.
	 * @param probNet The probabilistic network.
	 * @param depth The maximum depth of the decision tree.
	 * @return The root element of the constructed decision tree.
	 * 
	 * @throws NotEvaluableNetworkException
	 * @throws IncompatibleEvidenceException
	 * @throws NonProjectablePotentialException
	 * @throws PotentialOperationException.DifferentSizesInPotentialsAndStates
	 * @throws NotSupportedOperationException
	 */
    public static DecisionTreeElement buildDecisionTree(ProbNet probNet, int depth) throws NotEvaluableNetworkException, IncompatibleEvidenceException, NonProjectablePotentialException, PotentialOperationException.DifferentSizesInPotentialsAndStates, NotSupportedOperationException {
        return buildDecisionTree(probNet, depth, new EvidenceCase());
    }
    
    /**
     * Builds a decision tree from a ProbNet up to a specified depth, considering given evidence.
     * @param probNet
     * @param depth
     * @param branchEvidence
     *
     * @return a decision tree branch with the decision tree built
     *
     * @throws NotEvaluableNetworkException
     */
    private static DecisionTreeBranch buildDecisionTree(ProbNet probNet, int depth, EvidenceCase branchEvidence) throws NotEvaluableNetworkException, IncompatibleEvidenceException, NonProjectablePotentialException, PotentialOperationException.DifferentSizesInPotentialsAndStates, NotSupportedOperationException {
        DecisionTreeBranch root = null;
        NetworkType networkType = probNet.getNetworkType();
        if (networkType instanceof InfluenceDiagramType || networkType instanceof DecisionAnalysisNetworkType) {
            root = new DecisionTreeBranch(probNet);
            DecompositionGenerateDecisionTree genDT = new DecompositionGenerateDecisionTree(probNet, depth);
            genDT.setPreResolutionEvidence(branchEvidence);
            root.setChild(genDT.getDecisionTree());
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
    
    /**
     * Expands the tree by one additional level of inference.
     */
    public void inferenceExpandNextLevel() throws NotEvaluableNetworkException, IncompatibleEvidenceException, NonProjectablePotentialException, PotentialOperationException.DifferentSizesInPotentialsAndStates, NotSupportedOperationException {
        inferenceExpandLevels(1);
    }
    
    public void inferenceExpandLevels(int n) throws NotEvaluableNetworkException, NonProjectablePotentialException, IncompatibleEvidenceException, PotentialOperationException.DifferentSizesInPotentialsAndStates, NotSupportedOperationException {
        DecisionTreeModel auxModel = (DecisionTreeModel) jTree.getModel();
        DecisionTreeBranchPanel root = (DecisionTreeBranchPanel) auxModel.getRoot();
        inferenceExpandLevels(root.getTreeBranch(), null, n, new EvidenceCase());
        updateVisualInformation(root.getTreeBranch());
    }
    
    /**
     * Expands the tree by N levels of inference and updates the view.
     * @param n Number of levels to expand.
     */
    private static void inferenceExpandLevels(DecisionTreeElement root, 
    		DecisionTreeNode parent, int n, EvidenceCase branchEvidence) 
    				throws NotEvaluableNetworkException, IncompatibleEvidenceException, NonProjectablePotentialException, PotentialOperationException.DifferentSizesInPotentialsAndStates, NotSupportedOperationException {
        if (root instanceof DecisionTreeBranch || ((DecisionTreeNode) root).getNodeType() != NodeType.UTILITY) {
            if (root instanceof DecisionTreeNode) {
                parent = (DecisionTreeNode) root;
            }
            for (DecisionTreeElement branch : root.getChildren()) {
                EvidenceCase newEvi;
                if (root instanceof DecisionTreeBranch) {
                    newEvi = createEvidenceBranchPath(branchEvidence, (DecisionTreeBranch) root);
                } else {
                    newEvi = branchEvidence;
                }
                inferenceExpandLevels(branch, parent, n, newEvi);
            }
        } else {
            DecisionTreeNode rootDT = (DecisionTreeNode) root;
            DecisionTreeNode auxRoot = buildDecisionTree(rootDT.getNetwork(), n, branchEvidence).getChild();
            if (parent != null) {
                if (parent.getNodeType() == NodeType.DECISION
                        || (!(parent.getVariable().getName().equalsIgnoreCase(auxRoot.getVariable().getName())))) {
                    rootDT.copy(auxRoot);
                }
            }
        }
    }
    
    
    private static EvidenceCase createEvidenceBranchPath(EvidenceCase branchEvidence, DecisionTreeBranch branch) {
        EvidenceCase newEvi = new EvidenceCase(branchEvidence);
        if (branch != null) {
            Variable branchVariable = branch.getBranchVariable();
            if (branchVariable != null
                    && (!branchVariable.getName().equalsIgnoreCase("OD"))
                    && !newEvi.contains(branchVariable)) {
                try {
                    newEvi.addFinding(new Finding(branchVariable, branch.getBranchState()));
                } catch (IncompatibleEvidenceException.EvidenceIsIncompatibleWithOther e) {
                    throw new UnreacheableException(e);
                }
            }
        }
        return newEvi;
    }
    
    
    public void inferenceExpandAllLevels() throws NotEvaluableNetworkException, IncompatibleEvidenceException, NonProjectablePotentialException, PotentialOperationException.DifferentSizesInPotentialsAndStates, NotSupportedOperationException {
        inferenceExpandLevels(Integer.MAX_VALUE);
    }
    
    
    /**
     * Internal listener to handle GUI actions and mouse interactions.
     */
    private class TreePanelListener implements ActionListener, MouseListener {
        
        /**
         * Dispatches commands for expansion, opening networks, or saving to Graphviz.
         * @param e The action event.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            String actionCommand = e.getActionCommand();
            switch (ActionCommands.of(actionCommand)) {
                case ActionCommands.TREE_EXPAND_NEXT:
                    System.out.println("Expanding some levels");
                    // Expand N levels
                    try {
                        inferenceExpandNextLevel();
                    } catch (NotEvaluableNetworkException | IncompatibleEvidenceException |
                             NonProjectablePotentialException |
                             PotentialOperationException.DifferentSizesInPotentialsAndStates |
                             NotSupportedOperationException ex) {
                        throw new UnrecoverableException(ex);
                    }
                    break;
                case ActionCommands.TREE_EXPAND_ALL:
                    System.out.println("Expanding all levels");
                    // Expand all levels
                    try {
                        inferenceExpandAllLevels();
                    } catch (NotEvaluableNetworkException | IncompatibleEvidenceException |
                             NonProjectablePotentialException |
                             PotentialOperationException.DifferentSizesInPotentialsAndStates |
                             NotSupportedOperationException ex) {
                        throw new UnrecoverableException(ex);
                    }
                    break;
                case ActionCommands.TREE_OPEN_NETWORK:
                    System.out.println("Opening associated network");
                    openAssociatedNetwork();
                    // Open tree
                    break;
                case ActionCommands.TREE_SHOW_CEP:
                    System.out.println("Opening associated CEP");
                    openAssociatedCEP();
                    
                    break;
                case ActionCommands.TREE_SAVE_GRAPHVIZ:
                    System.out.println("Doing something wonderful");
                    // Show CEP or utility
                    TreeNodeToDot tree2dot = new TreeNodeToDot();
                    Object selectedComponent = jTree.getLastSelectedPathComponent();
                    if (selectedComponent instanceof DecisionTreeNodePanel treeNodePanel) {
                        DecisionTreeNode treeNode = treeNodePanel.getTreeNode();
                        try {
                            tree2dot.paintDTNode(treeNode);
                        } catch (IOException ex) {
                            throw new UnrecoverableException(ex);
                        }
                    }
                    break;
                case null, default:
            }
        }
        
        
        private void openAssociatedCEP() {
            Object selectedComponent = jTree.getLastSelectedPathComponent();
            if (selectedComponent instanceof DecisionTreeNodePanel treeNodePanel) {
                DecisionTreeNode treeNode = treeNodePanel.getTreeNode();
                CEPDialog cepDialog = new CEPDialog(null, (CEP) (treeNode.getUtility()), treeNode.getNetwork());
                cepDialog.setVisible(true);
            }
        }
        
        
        private void openAssociatedNetwork() {
            
            Object selectedComponent = jTree.getLastSelectedPathComponent();
            if (selectedComponent instanceof DecisionTreeNodePanel) {
                MainGUI.INSTANCE.mainPanel
                         .getMainPanelListenerAssistant()
                         .openNetwork(getNetwork(selectedComponent));
            }
            
        }
        
        
        public static ProbNet getNetwork(Object selectedComponent) {
            
            DecisionTreeNodePanel treeNodePanel = (DecisionTreeNodePanel) selectedComponent;
            DecisionTreeNode treeNode = treeNodePanel.getTreeNode();
            return treeNode.getNetwork();
        }
        
        
        /* Listener methods */
        // Open tree contextual menu on right click
        /**
         * Handles right-click events to show contextual menus.
         * @param e The mouse event.
         */
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
                    //if (nodeType == NodeType.CHANCE || nodeType == NodeType.DECISION) {
                    if (nodeType == NodeType.CHANCE || nodeType == NodeType.DECISION || nodeType == NodeType.UTILITY) {
                        // Get menu from the contextualMenuFactory
                        Type type = getNetwork(selectedComponent).getInferenceOptions()
                                                                 .getMultiCriteriaOptions()
                                                                 .getMulticriteriaType();
                        TreeContextualMenu treeMenu = (TreeContextualMenu) contextualMenuFactory.getTreeContextualMenu(type == Type.COST_EFFECTIVENESS);
                        treeMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
                
            }
        }
        
        @Override public void mousePressed(MouseEvent mouseEvent) {
        }
        
        @Override public void mouseReleased(MouseEvent mouseEvent) {
        }
        
        @Override public void mouseEntered(MouseEvent mouseEvent) {
        }
        
        @Override public void mouseExited(MouseEvent mouseEvent) {
        }
    }
    
    /**
     * Retrieves the root logical node of the tree.
     * @return The {@link DecisionTreeNode} at the top of the hierarchy.
     */
    public DecisionTreeNode getDecisionTreeNode() {
        VisualDecisionTree dt = getJTree();
        TreeModel model = dt.getModel();
        DecisionTreeBranchPanel branchPanel = (DecisionTreeBranchPanel) model.getRoot();
        DecisionTreeBranch root = branchPanel.getTreeBranch();
        return root.getChild();
    }
}
