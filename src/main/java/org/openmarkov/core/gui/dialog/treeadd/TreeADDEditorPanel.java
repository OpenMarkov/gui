/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.treeadd;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.gui.dialog.node.NodePropertiesDialog;
import org.openmarkov.core.gui.dialog.node.PotentialEditDialog;
import org.openmarkov.core.gui.util.Utilities;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.UniformPotential;
import org.openmarkov.core.model.network.potential.treeadd.Threshold;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDBranch;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDPotential;

/**
 * <code>JScrollPane<code> for creating and modifying <code>TreeADDModel<code>s
 * 
 * @author jfernandez
 * @author myebra
 */
public class TreeADDEditorPanel extends JScrollPane implements ActionListener {
    private static final long serialVersionUID = -6230911169585766424L;
    protected JPopupMenu popupMenu = new JPopupMenu();
    // menu to start painting the treeADD with the panel in blank
    protected JMenu submenuAddStartTree = new JMenu();
    // when clicking a branch you can set a potential or add a subtree to that
    // branch
    protected JMenu addSubtreeADD = new JMenu();
    protected JMenu submenuChangeTopVariable = new JMenu();
    protected JMenuItem editPotential = new JMenuItem("Edit Potential");
    protected JMenuItem joinBranches = new JMenuItem("Add States to Branch");
    protected JMenuItem dissociateStates = new JMenuItem("Remove States");
    protected JMenuItem removeVariables = new JMenuItem("Remove Variables");
    protected JMenuItem removeTreeADD = new JMenuItem("Remove Subtree");
    protected JMenuItem addVariables2Potential = new JMenuItem();
    protected JMenuItem splitInterval = new JMenuItem("Split Interval");
    protected JMenuItem changeInterval = new JMenuItem("Change Interval");
    protected TreeADDPotential rootTreeADDPotential;
    protected JTree jTree;
    protected boolean readOnlyMode;
    // Variables of the treeADDPotential root of the tree
    protected List<Variable> treeVariables;
    // Mouse event detection
    private int xx, yy;

    /**
     * Shows the tree in read only mode
     * 
     * @param probNet
     * @param treeADDPotential
     */
    public TreeADDEditorPanel(TreeADDCellRenderer cellRenderer, TreeADDPotential treeADDPotential) {
        // A copy of the potential
        this.rootTreeADDPotential = new TreeADDPotential(treeADDPotential);
        readOnlyMode = false;
        setupUserInterface(cellRenderer);
    }

    private void setupUserInterface(TreeADDCellRenderer cellRenderer) {
        TreeADDModel model = new TreeADDModel(rootTreeADDPotential);
        jTree = new JTree(model);
        jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTree.addTreeExpansionListener(new TreeADDViewer_treeExpansionAdapter(this));
        jTree.addTreeWillExpandListener(new TreeADDViewer_treeWillExpandAdapter(this));
        // Allows JTree nodes to accept CR/LF codes
        jTree.setShowsRootHandles(true);
        jTree.setRowHeight(0);
        jTree.setCellRenderer(cellRenderer);
        jTree.setUI(new TreeADDUserInterface());
        for (int i = 0; i < jTree.getRowCount(); i++) {
            jTree.expandRow(i);
        }
        setViewportView(jTree);
        // Menu text initialization
        if (!readOnlyMode) {
            jTree.addMouseListener(new TreeADDViewer_mouseAdapter(this));
            popupMenu.setInvoker(jTree);
            // menu to create the root treeADD start painting the tree
            submenuAddStartTree.setText("Set start node");
            // menu to add a subtree to a branch
            addSubtreeADD.setText("Add subtree");
            submenuChangeTopVariable.setText("Change Variable");
            addVariables2Potential.setText("Add Variables to Potential");
            addVariables2Potential.addActionListener(this);
            editPotential.addActionListener(this);
            joinBranches.addActionListener(this);
            dissociateStates.addActionListener(this);
            removeVariables.addActionListener(this);
            removeTreeADD.addActionListener(this);
            splitInterval.addActionListener(this);
            changeInterval.addActionListener(this);
        }
    }

    public TreeADDPotential getTreePotential() {
        return rootTreeADDPotential;
    }

    public void tree_treeExpanded(TreeExpansionEvent event) {
        TreePath treepath = event.getPath();
        Object tn1 = treepath.getLastPathComponent();
        TreeADDModel model = (TreeADDModel) jTree.getModel();
        if (!model.isLeaf(tn1)) {// the object is a treeADDPotential
            for (int i = 0; i < model.getChildCount(tn1); i++) {// Expand path
                                                                // adding
                                                                // branches to
                                                                // the treeADD
                                                                // path
                Object child = model.getChild(tn1, i);// this must be a branch
                TreePath tp2 = treepath.pathByAddingChild(child);
                jTree.expandPath(tp2);
            }
        }
    }

    /**
     * @param event
     * @throws ExpandVetoException
     */
    public void tree_treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
        Object triedToExpand = event.getPath().getLastPathComponent();
        if (!(triedToExpand instanceof TreeADDPotential)
                && !(triedToExpand instanceof TreeADDBranch)) {
            throw new ExpandVetoException(event);// Exception used to stop and
                                                 // expand/collapse from
                                                 // happening.
        }
    }

    // When clicking on a tree only can change top variable. If the tree does
    // not
    // have subtrees trees are only permitted to change buttom up order
    protected void setPopupItemsTreeADD(MouseEvent e, TreeADDPotential treeADD) {
        popupMenu.removeAll();
        submenuChangeTopVariable.removeAll();
        // change topVariable
        List<TreeADDBranch> branches = treeADD.getBranches();
        boolean hasSubTrees = false;
        for (TreeADDBranch branch : branches) {
            if (branch.getPotential() instanceof TreeADDPotential) {
                hasSubTrees = true;
            }
        }
        Variable currentTopVariable = treeADD.getTopVariable();
        Variable conditionedOrUtilityVariable = null;
        if (treeADD.getPotentialRole() == PotentialRole.CONDITIONAL_PROBABILITY) {
            conditionedOrUtilityVariable = treeADD.getConditionedVariable();
        } else if (treeADD.getPotentialRole() == PotentialRole.UTILITY) {
            conditionedOrUtilityVariable = treeADD.getUtilityVariable();
        }
        List<Variable> newPosibleTopVariables = new ArrayList<Variable>();
        for (Variable variable : treeADD.getVariables()) {
            if (variable != currentTopVariable && variable != conditionedOrUtilityVariable) {
                newPosibleTopVariables.add(variable);
            }
        }
        if (!hasSubTrees && newPosibleTopVariables.size() != 0) {
            for (Variable variable : treeADD.getVariables()) {
                if (variable != currentTopVariable && variable != conditionedOrUtilityVariable) {
                    JMenuItem posibleTopVariable = new JMenuItem(variable.getName());
                    posibleTopVariable.addActionListener(this);
                    posibleTopVariable.setActionCommand("ChangeTopVariable");
                    submenuChangeTopVariable.add(posibleTopVariable);
                }
            }
            popupMenu.add(submenuChangeTopVariable);
        }
    }

    /**
     * @param e
     * @param branch
     */
    protected void setPopupItemBranch(MouseEvent e, TreeADDBranch branch, TreePath branchPath) {
        popupMenu.removeAll();
        addSubtreeADD.removeAll();
        addVariables2Potential.removeAll();
        editPotential.removeAll();
        joinBranches.removeAll();
        dissociateStates.removeAll();
        removeVariables.removeAll();
        removeTreeADD.removeAll();
        splitInterval.removeAll();
        changeInterval.removeAll();
        // Adding treeADD
        List<Variable> variables = branch.getParentVariables();
        if (branch.getPotential().getPotentialRole() == PotentialRole.UTILITY) {
            variables.add(branch.getPotential().getUtilityVariable());
        }
        Variable topVariable = branch.getTopVariable();
        List<Variable> possibleVariables = new ArrayList<Variable>();
        for (Variable variable : variables) {
            if (branch.getPotential().getPotentialRole() == PotentialRole.CONDITIONAL_PROBABILITY) {
                if (variable != topVariable && variable != variables.get(0)) {
                    possibleVariables.add(variable);
                }
            } else if (branch.getPotential().getPotentialRole() == PotentialRole.UTILITY) {
                if (variable != topVariable
                        && variable != branch.getPotential().getUtilityVariable()) {
                    possibleVariables.add(variable);
                }
            }
        }
        // Also it could be selected a top variable that has been appeared
        // previously in the tree but only if
        // in current path that variable groups different states
        TreePath parentPath = branchPath.getParentPath(); // treeADD
        while (parentPath.getLastPathComponent() != rootTreeADDPotential) {
            TreePath grandParentPath = parentPath.getParentPath();// branch
            if (grandParentPath.getLastPathComponent() instanceof TreeADDBranch) {
                // if a branch has more then one state is possible to offer, as
                // top variable, the top variable of this branch
                TreeADDBranch treeBranch = (TreeADDBranch) grandParentPath.getLastPathComponent();
                if (treeBranch.getTopVariable().getVariableType() == VariableType.FINITE_STATES) {
                    if (treeBranch.getBranchStates().size() > 1) {
                        possibleVariables.add(treeBranch.getTopVariable());
                    }
                }
            }
            parentPath = grandParentPath;
        }
        // Also it could be selected as a top variable a numeric variable that
        // has already appeared in the tree
        TreePath path = branchPath.getParentPath();
        while (path.getLastPathComponent() != rootTreeADDPotential) {
            if (path.getLastPathComponent() instanceof TreeADDBranch) {
                TreeADDBranch treeBranch = (TreeADDBranch) path.getLastPathComponent();
                if (treeBranch.getTopVariable().getVariableType() == VariableType.NUMERIC) {
                    if (treeBranch.getMinThreshold().getLimit() != treeBranch.getMaxThreshold()
                            .getLimit()) {
                        possibleVariables.add(treeBranch.getTopVariable());
                    }
                }
            }
            path = path.getParentPath();
        }
        // Potential Edition, any case it is possible to edit branch's potential
        if(!(branch.getPotential() instanceof TreeADDPotential))
        {
            JSeparator separator = new JSeparator();
            editPotential.setActionCommand("EditPotential");
            popupMenu.add(editPotential);
            popupMenu.add(separator);
        }

        JSeparator separatorAddSubtree = new JSeparator();
        // if {variables}-{topVariable}-{conditionedVariable} is not empty
        // so you can add also a subtree to the branch
        if (possibleVariables.size() != 0 && !(branch.getPotential() instanceof TreeADDPotential)) { 
            for (Variable variable : possibleVariables) {
                // To add possible topVariables popups
                JMenuItem posibleTopVariable = new JMenuItem(variable.getName());
                posibleTopVariable.addActionListener(this);
                posibleTopVariable.setActionCommand("AddTreeADD");
                addSubtreeADD.add(posibleTopVariable);
            }
            popupMenu.add(addSubtreeADD);
            popupMenu.add(separatorAddSubtree);
        }
        // remove subtree
        JSeparator addRemoveTree = new JSeparator();
        if (branch.getPotential() instanceof TreeADDPotential) {
            removeTreeADD.setActionCommand("RemoveTreeADD");
            popupMenu.add(removeTreeADD);
            popupMenu.add(addRemoveTree);
        } 
        // Adding Variables to potential
        JSeparator addRemoveVariables = new JSeparator();
        if (possibleVariables.size() != 0 && !(branch.getPotential() instanceof TreeADDPotential)) {
            addVariables2Potential.setActionCommand("AddVariables2Potential");
            popupMenu.add(addVariables2Potential);
        }
        // remove potential variables
        if (!(branch.getPotential() instanceof TreeADDPotential)) {
            if (branch.getPotential().getVariables().size() > 1) {
                removeVariables.setActionCommand("RemoveVariables");
                popupMenu.add(removeVariables);
                popupMenu.add(addRemoveVariables);
            } else {
                // popupMenu.add(addRemoveVariables);
            }
        }
        // dissociate branches
        if (branch.getTopVariable().getVariableType() == VariableType.FINITE_STATES) {
            // joining branches
            joinBranches.setActionCommand("JoinBranches");
            popupMenu.add(joinBranches);
            if (branch.getBranchStates().size() > 1) {
                dissociateStates.setActionCommand("DissociateStates");
                popupMenu.add(dissociateStates);
            }
        } else if (branch.getTopVariable().getVariableType() == VariableType.NUMERIC) {
            // Split intervals
            splitInterval.setActionCommand("SplitInterval");
            popupMenu.add(splitInterval);
            // Change interval
            double minDomainLimit = branch.getTopVariable().getPartitionedInterval().getMin();
            double maxDomainLimit = branch.getTopVariable().getPartitionedInterval().getMax();
            double min = branch.getMinThreshold().getLimit();
            double max = branch.getMaxThreshold().getLimit();
            if (minDomainLimit == min && maxDomainLimit == max) {
                // do not offer the possibility to change domain
            } else {
                changeInterval.setActionCommand("ChangeInterval");
                popupMenu.add(changeInterval);
            }
        }
    }

    /**
	 * 
	 */
    public void actionPerformed(ActionEvent ae) {
        String actionComand = ae.getActionCommand();
        TreePath path = jTree.getPathForLocation(xx, yy);
        Object node = path.getLastPathComponent();
        if (actionComand.equals("AddTreeADD")) {
            if(node instanceof Potential)
            {
                path = path.getParentPath();
                node = path.getLastPathComponent();
            }
            // node must be a branch
            actionAddTreeADD(ae, (TreeADDBranch) node, path);
        } else if (actionComand.equals("EditPotential")) {
            if(node instanceof Potential)
            {
                path = path.getParentPath();
                node = path.getLastPathComponent();
            }
            // node must be a branch
            actionEditPotential(ae, (TreeADDBranch) node, path);

        } else if (actionComand.equals("ChangeTopVariable")) {
            // node must be a TreeADDPotential
            actionChangeTopVariable(ae, (TreeADDPotential) node, path);
        } else if (actionComand.equals("JoinBranches")) {
            if(node instanceof Potential)
            {
                path = path.getParentPath();
                node = path.getLastPathComponent();
            }
            // node must be a branch
            actionJoinBranches(ae, (TreeADDBranch) node, path);
        } else if (actionComand.equals("RemoveTreeADD")) {
            if(node instanceof Potential)
            {
                path = path.getParentPath();
                node = path.getLastPathComponent();
            }
            // node must be a branch
            actionRemoveSubTree(ae, (TreeADDBranch) node, path);
        } else if (actionComand.equals("AddVariables2Potential")) {
            if(node instanceof Potential)
            {
                path = path.getParentPath();
                node = path.getLastPathComponent();
            }
            // node must be a branch
            actionAddVariablesToPotential(ae, (TreeADDBranch) node, path);
        } else if (actionComand.equals("DissociateStates")) {
            if(node instanceof Potential)
            {
                path = path.getParentPath();
                node = path.getLastPathComponent();
            }
            // node must be a branch
            actionDissociateStates(ae, (TreeADDBranch) node, path);
        } else if (actionComand.equals("RemoveVariables")) {
            if(node instanceof Potential)
            {
                path = path.getParentPath();
                node = path.getLastPathComponent();
            }
            // node must be a branch
            actionRemoveVariables(ae, (TreeADDBranch) node, path);
        } else if (actionComand.equals("SplitInterval")) {
            if(node instanceof Potential)
            {
                path = path.getParentPath();
                node = path.getLastPathComponent();
            }
            // node must be a branch
            actionSplitInterval(ae, (TreeADDBranch) node, path);
        } else if (actionComand.equals("ChangeInterval")) {
            if(node instanceof Potential)
            {
                path = path.getParentPath();
                node = path.getLastPathComponent();
            }
            // node must be a branch
            actionChangeInterval(ae, (TreeADDBranch) node, path);
        } else {
            throw new RuntimeException("Unexpected menu action found: " + actionComand);
        }
    }

    private void actionChangeInterval(ActionEvent ae, TreeADDBranch branch, TreePath path) {

        TreePath parentPath = path.getParentPath();
        TreeADDPotential parentTreeADD = (TreeADDPotential) ((TreePath) parentPath)
                .getLastPathComponent();
        ChangeIntervalDialog dialog = new ChangeIntervalDialog(Utilities.getOwner(this), branch);
        TreeADDModel model = (TreeADDModel) jTree.getModel();
        boolean minBelongsToLeft = false;
        boolean maxBelongsToLeft = false;
        Float minDomainLimit = (float) parentTreeADD.getTopVariable().getPartitionedInterval()
                .getMin();
        Float maxDomainLimit = (float) parentTreeADD.getTopVariable().getPartitionedInterval()
                .getMax();
        boolean isLeftClosed = branch.getTopVariable().getPartitionedInterval().isLeftClosed(); // true
                                                                                                // ->
                                                                                                // [)
        boolean isRightClosed = branch.getTopVariable().getPartitionedInterval().isRightClosed();
        boolean minBelongsToLeftDomain = !isLeftClosed;
        boolean maxBelongsToLeftDomain = isRightClosed;
        if (dialog.requestValues() == ChangeIntervalDialog.OK_BUTTON) {
            ChangeIntervalPanel panel = (ChangeIntervalPanel) dialog.getChangeIntervalPanel();
            Float minText = Float.parseFloat(panel.getMin().getText());
            Float maxText = Float.parseFloat(panel.getMax().getText());
            JComboBox<String> minLimit = panel.minBelongsToLeft();// ( or [
            if (minLimit.getSelectedItem() == "(") {
                minBelongsToLeft = true;
            } else if (minLimit.getSelectedItem() == "[") {
                minBelongsToLeft = false;
            }
            JComboBox<String> maxLimit = panel.maxBelongsToLeft();// ) or ]
            if (maxLimit.getSelectedItem() == ")") {
                maxBelongsToLeft = false;
            } else if (maxLimit.getSelectedItem() == "]") {
                maxBelongsToLeft = true;
            }
            List<TreeADDBranch> parentBranches = parentTreeADD.getBranches();
            int branchToChangeIndex = 0;
            for (int i = 0; i < parentBranches.size(); i++) {
                if (parentBranches.get(i).equals(branch)) {
                    branchToChangeIndex = i;
                }
            }
            // limit situations
            if (!(minText.floatValue() >= minDomainLimit.floatValue() && minText.floatValue() <= branch
                    .getMaxThreshold().getLimit())
                    || !(maxText.floatValue() >= branch.getMinThreshold().getLimit() && maxText
                            .floatValue() <= maxDomainLimit.floatValue())) {
                JOptionPane.showMessageDialog(this.getParent(), "Not permited values");
            } else if (maxText.floatValue() > maxDomainLimit.floatValue()
                    || minText.floatValue() < minDomainLimit) {
                JOptionPane.showMessageDialog(this.getParent(),
                        "Be careful, you are trying to change variable domain");
            } else if (maxText.floatValue() == maxDomainLimit.floatValue()
                    && !maxBelongsToLeftDomain && maxBelongsToLeft) {
                JOptionPane.showMessageDialog(this.getParent(),
                        "Be careful, you are trying to change variable domain");
            } else if (minText.floatValue() == minDomainLimit.floatValue()
                    && minBelongsToLeftDomain && !minBelongsToLeft) {
                JOptionPane.showMessageDialog(this.getParent(),
                        "Be careful, you are trying to change variable domain");
            } else {
                // max limit
                if (!(maxText.floatValue() == branch.getMaxThreshold().getLimit() && maxBelongsToLeft == branch
                        .getMaxThreshold().belongsToLeft())) {
                    ArrayList<TreeADDBranch> followingBranches = new ArrayList<TreeADDBranch>();
                    parentBranches.get(branchToChangeIndex).setThresholdMax(
                            new Threshold(maxText, maxBelongsToLeft));
                    if (branchToChangeIndex != parentBranches.size()) {// branch
                                                                       // selected
                                                                       // to
                                                                       // change
                                                                       // interval
                                                                       // is not
                                                                       // the
                                                                       // last
                                                                       // one
                        for (int i = branchToChangeIndex + 1; i < parentBranches.size(); i++) {
                            followingBranches.add(parentBranches.get(i));
                        }
                        for (int i = 0; i < followingBranches.size(); i++) {
                            if (maxText.equals(followingBranches.get(i).getMaxThreshold()
                                    .getLimit())
                                    && maxBelongsToLeft == followingBranches.get(i)
                                            .getMaxThreshold().belongsToLeft()) {
                                for (int j = branchToChangeIndex + 1; j <= i + branchToChangeIndex
                                        + 1; j++) {
                                    parentBranches.remove(branchToChangeIndex + 1);
                                }
                                break;
                            } else if (maxText.equals(followingBranches.get(i).getMaxThreshold()
                                    .getLimit())
                                    && !followingBranches.get(i).getMaxThreshold().belongsToLeft()
                                    && maxBelongsToLeft) {
                                for (int j = branchToChangeIndex + 1; j <= i + branchToChangeIndex
                                        + 1; j++) {
                                    parentBranches.remove(branchToChangeIndex + 1);
                                }
                                // Change the next
                                if (i + branchToChangeIndex + 1 <= parentBranches.size()) {
                                    parentBranches.get(branchToChangeIndex + 1).getMinThreshold()
                                            .setBelongsToLeft(true);
                                }
                                break;
                            } else if (maxText.equals(followingBranches.get(i).getMaxThreshold()
                                    .getLimit())
                                    && followingBranches.get(i).getMaxThreshold().belongsToLeft()
                                    && !maxBelongsToLeft) {
                                for (int j = branchToChangeIndex + 1; j <= i + branchToChangeIndex; j++) {
                                    parentBranches.remove(branchToChangeIndex + 1);
                                }
                                // Change the next
                                if (i + branchToChangeIndex + 1 <= parentBranches.size()) {
                                    parentBranches.get(branchToChangeIndex + 1).setThresholdMin(
                                            new Threshold(maxText, false));
                                }
                                break;
                            } else if (maxText.floatValue() < ((Float) followingBranches.get(i)
                                    .getMaxThreshold().getLimit()).floatValue()) {
                                for (int j = branchToChangeIndex + 1; j <= i + branchToChangeIndex; j++) {
                                    parentBranches.remove(branchToChangeIndex + 1);
                                }
                                parentBranches.get(branchToChangeIndex + 1).setThresholdMin(
                                        new Threshold(maxText, maxBelongsToLeft));
                                break;
                            }
                        }
                    }
                }
                // min limit
                if (!(minText.floatValue() == branch.getMinThreshold().getLimit() && minBelongsToLeft == branch
                        .getMinThreshold().belongsToLeft())) {
                    ArrayList<TreeADDBranch> previousBranches = new ArrayList<TreeADDBranch>();
                    parentBranches.get(branchToChangeIndex).setThresholdMin(
                            new Threshold(minText, minBelongsToLeft));
                    if (branchToChangeIndex != 0) {// branch selected to change
                                                   // interval is not the first
                                                   // one
                        for (int i = branchToChangeIndex - 1; i >= 0; i--) {
                            previousBranches.add(parentBranches.get(i));
                        }
                    }
                    ArrayList<TreeADDBranch> aux = new ArrayList<TreeADDBranch>();
                    int initialParentSize = parentBranches.size();
                    for (int i = parentBranches.size() - 1; i >= 0; i--) {
                        aux.add(parentBranches.get(i));
                    }
                    int auxIndex = parentBranches.size() - 1 - branchToChangeIndex;
                    for (int i = 0; i < previousBranches.size(); i++) {
                        if (minText.equals(previousBranches.get(i).getMinThreshold().getLimit())
                                && minBelongsToLeft == previousBranches.get(i).getMinThreshold()
                                        .belongsToLeft()) {
                            for (int j = auxIndex + 1; j <= i + (auxIndex + 1); j++) {
                                aux.remove(auxIndex + 1);
                            }
                            break;
                        } else if (minText.equals(previousBranches.get(i).getMinThreshold()
                                .getLimit())
                                && !previousBranches.get(i).getMinThreshold().belongsToLeft()
                                && minBelongsToLeft) {
                            for (int j = auxIndex + 1; j <= i + (auxIndex + 1); j++) {
                                aux.remove(auxIndex + 1);
                            }
                            // Change the next
                            if (i + auxIndex + 1 <= parentBranches.size()) {
                                aux.get(auxIndex + 1).getMaxThreshold().setBelongsToLeft(true);
                            }
                            break;
                        } else if (minText.equals(previousBranches.get(i).getMinThreshold()
                                .getLimit())
                                && previousBranches.get(i).getMinThreshold().belongsToLeft()
                                && !minBelongsToLeft) {
                            for (int j = auxIndex + 1; j <= i + (auxIndex + 1); j++) {
                                aux.remove(auxIndex + 1);
                            }
                            // Change the next
                            if (i + auxIndex + 1 <= parentBranches.size()) {
                                aux.get(auxIndex + 1).getMaxThreshold().setBelongsToLeft(false);
                            }
                            break;
                        } else if (minText.floatValue() > ((Float) previousBranches.get(i)
                                .getMinThreshold().getLimit()).floatValue()) {
                            for (int j = auxIndex + 1; j <= i + auxIndex; j++) {
                                aux.remove(auxIndex + 1);
                            }
                            aux.get(auxIndex + 1).setThresholdMax(
                                    new Threshold(minText, minBelongsToLeft));
                            break;
                        }
                    }
                    for (int i = aux.size() - 1; i >= 0; i--) {
                        parentBranches.set(i, aux.get(aux.size() - 1 - i));
                    }
                    for (int i = aux.size(); i < initialParentSize; i++) {
                        parentBranches.remove(aux.size());
                    }
                }
                model.notifyTreeStructureChanged((TreePath) parentPath);
            }
            for (int i = 0; i < jTree.getRowCount(); i++) {
                jTree.expandRow(i);
            }
        }
    }

    /**
     * Splits interval in a branch which top variable is continuous
     * 
     * @param ae
     * @param branch
     * @param path
     */
    private void actionSplitInterval(ActionEvent ae, TreeADDBranch branch, TreePath path) {
        TreePath parentPath = path.getParentPath();
        TreeADDPotential parentTreeADD = (TreeADDPotential) ((TreePath) parentPath)
                .getLastPathComponent();
        SplitIntervalDialog dialog = new SplitIntervalDialog(Utilities.getOwner(this));
        TreeADDModel model = (TreeADDModel) jTree.getModel();
        if (dialog.requestValues() == SplitIntervalDialog.OK_BUTTON) {
            boolean belongsToLeft = false;
            SplitIntervalPanel panel = (SplitIntervalPanel) dialog.getJPanelSplitInterval();
            if (panel.belongsToLeft().isSelected()) {
                belongsToLeft = true;
            } else if (panel.belongsToRight().isSelected()) {
                belongsToLeft = false;
            }
            Float introducedLimit = Float.parseFloat(panel.getLimit().getText());
            Threshold minFirstInterval = branch.getMinThreshold();
            Threshold maxSecondInterval = branch.getMaxThreshold();
            List<Variable> potentialVariables = new ArrayList<Variable>();
            if (parentTreeADD.getPotentialRole() == PotentialRole.CONDITIONAL_PROBABILITY) {
                potentialVariables.add(parentTreeADD.getVariables().get(0));
            } else if (parentTreeADD.getPotentialRole() == PotentialRole.UTILITY) {
                // potentialVariables.add(parentTreeADD.getUtilityVariable());
            }
            UniformPotential potential = new UniformPotential(potentialVariables, branch
                    .getPotential().getPotentialRole());
            if (parentTreeADD.getPotentialRole() == PotentialRole.UTILITY) {
                potential.setUtilityVariable(parentTreeADD.getUtilityVariable());
            }
            List<TreeADDBranch> parentBranches = parentTreeADD.getBranches();
            List<TreeADDBranch> newBranches = new ArrayList<TreeADDBranch>();
            // Top variable domain
            Float minDomainLimit = (float) parentTreeADD.getTopVariable().getPartitionedInterval()
                    .getMin();
            Float maxDomainLimit = (float) parentTreeADD.getTopVariable().getPartitionedInterval()
                    .getMax();
            boolean isLeftClosed = branch.getTopVariable().getPartitionedInterval().isLeftClosed(); // true
                                                                                                    // ->
                                                                                                    // [)
            boolean isRightClosed = branch.getTopVariable().getPartitionedInterval()
                    .isRightClosed();
            boolean minBelongsToLeftDomain = !isLeftClosed;
            boolean maxBelongsToLeftDomain = isRightClosed;
            if (minDomainLimit.floatValue() == minFirstInterval.getLimit()
                    && introducedLimit.floatValue() == minFirstInterval.getLimit()
                    && minBelongsToLeftDomain && belongsToLeft) {
                JOptionPane.showMessageDialog(this.getParent(),
                        "Be careful, you are trying to change variable domain");
            } else if (maxDomainLimit.floatValue() == maxSecondInterval.getLimit()
                    && introducedLimit.floatValue() == maxSecondInterval.getLimit()
                    && !maxBelongsToLeftDomain && !belongsToLeft) {
                JOptionPane.showMessageDialog(this.getParent(),
                        "Be careful, you are trying to change variable domain");
            } else if (minFirstInterval.getLimit() == introducedLimit.floatValue()
                    && minFirstInterval.belongsToLeft() && belongsToLeft) {
                JOptionPane.showMessageDialog(this.getParent(), "This is not a valid action");
            } else if (minFirstInterval.getLimit() == introducedLimit.floatValue()
                    && !minFirstInterval.belongsToLeft() && !belongsToLeft) {
                JOptionPane.showMessageDialog(this.getParent(), "This is not a valid action");
            } else if (maxSecondInterval.getLimit() == introducedLimit.floatValue()
                    && !maxSecondInterval.belongsToLeft() && !belongsToLeft) {
                JOptionPane.showMessageDialog(this.getParent(), "This is not a valid action");
            } else if (maxSecondInterval.getLimit() == introducedLimit.floatValue()
                    && maxSecondInterval.belongsToLeft() && belongsToLeft) {
                JOptionPane.showMessageDialog(this.getParent(), "This is not a valid action");
            } else if (minFirstInterval.getLimit() == introducedLimit.floatValue()
                    && !minFirstInterval.belongsToLeft() && belongsToLeft) {
                TreeADDBranch newBranch = new TreeADDBranch(minFirstInterval, new Threshold(
                        minFirstInterval.getLimit(), true), potential, branch.getTopVariable(),
                        branch.getParentVariables());
                minFirstInterval.setBelongsToLeft(true);
                for (TreeADDBranch parentBranch : parentBranches) {
                    if (branch == parentBranch) {
                        newBranches.add(newBranch);
                        newBranches.add(branch);
                    } else {
                        newBranches.add(parentBranch);
                    }
                }
                parentTreeADD.setBranches(newBranches);
                model.notifyTreeStructureChanged(parentPath);
            } else if (maxSecondInterval.getLimit() == introducedLimit.floatValue()
                    && maxSecondInterval.belongsToLeft() && !belongsToLeft) {
                maxSecondInterval.setBelongsToLeft(false);
                TreeADDBranch newBranch = new TreeADDBranch(new Threshold(
                        maxSecondInterval.getLimit(), false), new Threshold(
                        maxSecondInterval.getLimit(), true), potential, branch.getTopVariable(),
                        branch.getParentVariables());
                for (TreeADDBranch parentBranch : parentBranches) {
                    if (branch == parentBranch) {
                        newBranches.add(branch);
                        newBranches.add(newBranch);
                    } else {
                        newBranches.add(parentBranch);
                    }
                }
                parentTreeADD.setBranches(newBranches);
                model.notifyTreeStructureChanged(parentPath);
            } else if (minFirstInterval.isBelow(introducedLimit)
                    && maxSecondInterval.isAbove(introducedLimit)) {
                Threshold maxFirstInterval = new Threshold(introducedLimit, belongsToLeft);
                Threshold minSecondInterval = new Threshold(introducedLimit, belongsToLeft);
                TreeADDBranch firstBranch = new TreeADDBranch(minFirstInterval, maxFirstInterval,
                        potential, branch.getTopVariable(), branch.getParentVariables());
                TreeADDBranch secondBranch = new TreeADDBranch(minSecondInterval,
                        maxSecondInterval, potential, branch.getTopVariable(),
                        branch.getParentVariables());
                for (TreeADDBranch parentBranch : parentBranches) {
                    if (branch == parentBranch) {
                        newBranches.add(firstBranch);
                        newBranches.add(secondBranch);
                    } else {
                        newBranches.add(parentBranch);
                    }
                }
                parentTreeADD.setBranches(newBranches);
                model.notifyTreeStructureChanged(parentPath);
            } else {// a message to indicate that is not a permitted value
                JOptionPane.showMessageDialog(this.getParent(),
                        "Introduced value does not belong to the interval selected");
            }
            for (int i = 0; i < jTree.getRowCount(); i++) {
                jTree.expandRow(i);
            }
        }
    }

    /**
     * Removes a subtree from a branch
     * 
     * @param ae
     * @param branch
     * @param path
     */
    private void actionRemoveSubTree(ActionEvent ae, TreeADDBranch branch, TreePath path) {
        Object parentPath = path.getParentPath();
        TreeADDPotential parentTreeADD = (TreeADDPotential) ((TreePath) parentPath)
                .getLastPathComponent();
        Potential subPotential = branch.getPotential();
        if (!(subPotential instanceof TreeADDPotential)) {
            throw new RuntimeException("Expected TreeADDPotential class, found: "
                    + subPotential.getClass().getName());
        }
        List<Variable> potentialVariables = new ArrayList<Variable>();
        if (parentTreeADD.getPotentialRole() == PotentialRole.CONDITIONAL_PROBABILITY) {
            Variable conditionedVariable = branch.getParentVariables().get(0);
            potentialVariables.add(conditionedVariable);
        } else if (parentTreeADD.getPotentialRole() == PotentialRole.UTILITY) {
            // potentialVariables.add(parentTreeADD.getUtilityVariable());
        }
        UniformPotential newPotential = new UniformPotential(potentialVariables,
                parentTreeADD.getPotentialRole());
        if (parentTreeADD.getPotentialRole() == PotentialRole.UTILITY) {
            newPotential.setUtilityVariable(parentTreeADD.getUtilityVariable());
        }
        branch.setPotential(newPotential);
        TreeADDModel model = (TreeADDModel) jTree.getModel();
        model.notifyTreeStructureChanged(path);
        for (int i = 0; i < jTree.getRowCount(); i++) {
            jTree.expandRow(i);
        }
    }

    private void actionAddVariablesToPotential(ActionEvent ae, TreeADDBranch branch, TreePath path) {
        Object parentPath = path.getParentPath();
        TreeADDPotential parentTreeADD = (TreeADDPotential) ((TreePath) parentPath)
                .getLastPathComponent();
        AddVariablesDialog dialog = new AddVariablesDialog(Utilities.getOwner(this), branch,
                parentTreeADD);
        TreeADDModel model = (TreeADDModel) jTree.getModel();
        if (dialog.requestValues() == AddVariablesDialog.OK_BUTTON) {
            AddVariablesCheckBoxPanel panel = dialog.getJPanelVariables();
            List<JCheckBox> checkBoxes = panel.getCheckBoxes();
            Potential branchPotential = branch.getPotential();
            List<Variable> branchPotentialVariables = branchPotential.getVariables();
            List<Variable> newVariables = new ArrayList<Variable>();
            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    String variableName = checkBox.getText();
                    for (Variable variable : parentTreeADD.getVariables()) {
                        if (variable.getName() == variableName) {
                            newVariables.add(variable);
                        }
                    }
                }
            }
            for (Variable var : newVariables) {
                branchPotentialVariables.add(var);
            }
            UniformPotential potential = new UniformPotential(branchPotentialVariables,
                    parentTreeADD.getPotentialRole());
            if (parentTreeADD.getPotentialRole() == PotentialRole.UTILITY) {
                potential.setUtilityVariable(parentTreeADD.getUtilityVariable());
            }
            branch.setPotential(potential);
            model.notifyTreeInsert(path, potential);
            model.notifyTreeStructureChanged(path);
            jTree.expandPath(path);
        }
    }

    private void actionRemoveVariables(ActionEvent ae, TreeADDBranch branch, TreePath path) {
        Object parentPath = path.getParentPath();
        TreeADDPotential parentTreeADD = (TreeADDPotential) ((TreePath) parentPath)
                .getLastPathComponent();
        RemoveVariablesDialog dialog = new RemoveVariablesDialog(Utilities.getOwner(this), branch,
                parentTreeADD);
        TreeADDModel model = (TreeADDModel) jTree.getModel();
        if (dialog.requestValues() == RemoveVariablesDialog.OK_BUTTON) {
            List<JCheckBox> checkBoxes = ((RemoveVariablesCheckBoxPanel) dialog
                    .getJPanelVariables()).getCheckBoxes();
            List<Variable> variablesToEliminate = new ArrayList<Variable>();
            List<Variable> branchVariables = branch.getPotential().getVariables();
            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    String variableName = checkBox.getText();
                    for (Variable variable : branchVariables) {
                        if (variable.getName() == variableName) {
                            variablesToEliminate.add(variable);
                        }
                    }
                }
            }
            for (Variable variable : variablesToEliminate) {
                branchVariables.remove(variable);
            }
            UniformPotential newPotential = new UniformPotential(branchVariables,
                    parentTreeADD.getPotentialRole());
            if (parentTreeADD.getPotentialRole() == PotentialRole.UTILITY) {
                newPotential.setUtilityVariable(parentTreeADD.getUtilityVariable());
            }
            branch.setPotential(newPotential);
            model.notifyTreeInsert(path, newPotential);
            model.notifyTreeStructureChanged(path);
            jTree.expandPath(path);
        }
    }

    /**
     * @param ae
     * @param branch
     * @param path
     */
    private void actionDissociateStates(ActionEvent ae, TreeADDBranch branch, TreePath path) {
        if (!(branch instanceof TreeADDBranch)) {
            throw new RuntimeException("Expected TreeADDBranch class, found: "
                    + branch.getClass().getName());
        }
        Object parentPath = path.getParentPath();
        TreeADDPotential parentTreeADD = (TreeADDPotential) ((TreePath) parentPath)
                .getLastPathComponent();
        RemoveStatesDialog dialog = new RemoveStatesDialog(Utilities.getOwner(this), branch,
                parentTreeADD);
        TreeADDModel model = (TreeADDModel) jTree.getModel();
        if (dialog.requestValues() == RemoveStatesDialog.OK_BUTTON) {
            List<JCheckBox> checkBoxes = ((RemoveStatesCheckBoxPanel) dialog
                    .getJPanelRemoveStates()).getCheckBoxes();
            List<State> statesToEliminate = new ArrayList<State>();
            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    String stateName = checkBox.getText();
                    for (State state : parentTreeADD.getTopVariable().getStates()) {
                        if (state.getName() == stateName) {
                            statesToEliminate.add(state);
                        }
                    }
                }
            }
            // Check if all checkboxes has been selected, it is an inconsistency
            if (checkBoxes.size() == statesToEliminate.size()) {
                JOptionPane
                        .showMessageDialog(this.getParent(),
                                "You have selected all states to remove, you must leave at least one in each branch");
            } else {
                List<TreeADDBranch> newTreeADDBranches = new ArrayList<TreeADDBranch>();
                for (TreeADDBranch treeParentBranch : parentTreeADD.getBranches()) {
                    List<State> states = treeParentBranch.getBranchStates();
                    if (branch.getBranchStates().containsAll(states)) {
                        continue;
                    } else {
                        newTreeADDBranches.add(treeParentBranch);
                    }
                }
                // Updating branches
                List<State> branchStates = branch.getBranchStates();
                for (State state : statesToEliminate) {
                    branchStates.remove(state);
                }
                branch.setStates(branchStates);
                List<Variable> variables = new ArrayList<Variable>();
                if (parentTreeADD.getPotentialRole() == PotentialRole.UTILITY) {
                    variables.add(parentTreeADD.getUtilityVariable());
                } else if (parentTreeADD.getPotentialRole() == PotentialRole.CONDITIONAL_PROBABILITY) {
                    variables.add(parentTreeADD.getVariables().get(0));
                }
                UniformPotential potential = new UniformPotential(variables,
                        parentTreeADD.getPotentialRole());
                if (parentTreeADD.getPotentialRole() == PotentialRole.UTILITY) {
                    potential.setUtilityVariable(parentTreeADD.getUtilityVariable());
                }
                TreeADDBranch newBranch = new TreeADDBranch(statesToEliminate, potential,
                        branch.getTopVariable(), branch.getParentVariables());
                newTreeADDBranches.add(branch);
                newTreeADDBranches.add(newBranch);
                // Updating tree
                parentTreeADD.setBranches(newTreeADDBranches);
                model.notifyTreeStructureChanged((TreePath) parentPath);
                jTree.expandPath((TreePath) parentPath);
                for (int i = 0; i < jTree.getRowCount(); i++) {
                    jTree.expandRow(i);
                }
            }
        }
    }

    /**
     * @param ae
     * @param treeADD
     * @param path
     */
    private void actionJoinBranches(ActionEvent ae, TreeADDBranch branch, TreePath path) {
        Object parentPath = path.getParentPath();
        TreeADDPotential parentTreeADD = (TreeADDPotential) ((TreePath) parentPath)
                .getLastPathComponent();
        // BranchStatesCheckBoxPanel checkBoxPanel = new
        // BranchStatesCheckBoxPanel(treeADDBranch, parentTreeADD);
        AddStatesToBranchDialog dialog = new AddStatesToBranchDialog(Utilities.getOwner(this),
                branch, parentTreeADD);
        TreeADDModel model = (TreeADDModel) jTree.getModel();
        // This must be a treeADD
        if (dialog.requestValues() == AddStatesToBranchDialog.OK_BUTTON) {
            List<JCheckBox> checkBoxes = ((AddStatesCheckBoxPanel) dialog.getJPanelBranchStates())
                    .getCheckBoxes();
            List<State> newBranchStates = new ArrayList<State>();
            for (State state : branch.getBranchStates()) {
                newBranchStates.add(state);
            }
            for (JCheckBox checkBox : checkBoxes) {
                if (checkBox.isSelected()) {
                    String stateName = checkBox.getText();
                    for (State state : parentTreeADD.getTopVariable().getStates()) {
                        if (state.getName() == stateName) {
                            newBranchStates.add(state);
                        }
                    }
                }
            }
            // Reorder new states
            List<State> newOrderedStates = new ArrayList<State>();
            State[] correctOrderStates = parentTreeADD.getTopVariable().getStates();
            for (State state : correctOrderStates) {
                for (State newState : newBranchStates) {
                    if (state == newState) {
                        newOrderedStates.add(state);
                    }
                }
            }
            // Updating branches
            List<TreeADDBranch> newTreeADDBranches = new ArrayList<TreeADDBranch>();
            branch.setStates(newOrderedStates);
            newTreeADDBranches.add(branch);
            for (TreeADDBranch treeBranch : parentTreeADD.getBranches()) {
                List<State> states = treeBranch.getBranchStates();
                if (newBranchStates.containsAll(states)) {
                    continue;
                } else {
                    for (State state : newBranchStates) {
                        if (states.contains(state)) {
                            states.remove(state);
                        }
                    }
                    treeBranch.setStates(states);
                    newTreeADDBranches.add(treeBranch);
                }
            }
            // Updating tree
            parentTreeADD.setBranches(newTreeADDBranches);
            model.notifyTreeStructureChanged((TreePath) parentPath);
            jTree.expandPath(path);
            for (int i = 0; i < jTree.getRowCount(); i++) {
                jTree.expandRow(i);
            }
        }
    }

    /**
     * @param ae
     * @param treeADDPotential
     * @param path
     */
    private void actionChangeTopVariable(ActionEvent ae, TreeADDPotential treeADDPotential,
            TreePath path) {
        List<Variable> variables = treeADDPotential.getVariables();
        JMenuItem menuTopVariable = (JMenuItem) ae.getSource();
        // to get the variable
        Variable newTopVariable = null;
        for (Variable variable : variables) {
            if (variable.getName() == menuTopVariable.getText()) {
                newTopVariable = variable;
            }
        }
        List<Variable> potentialVariables = new ArrayList<Variable>();
        if (treeADDPotential.getPotentialRole() == PotentialRole.CONDITIONAL_PROBABILITY) {
            potentialVariables.add(treeADDPotential.getVariables().get(0));
        } else if (treeADDPotential.getPotentialRole() == PotentialRole.UTILITY) {
            // potentialVariables.add(treeADDPotential.getUtilityVariable());
        }
        UniformPotential potential = new UniformPotential(potentialVariables,
                treeADDPotential.getPotentialRole());
        if (treeADDPotential.getPotentialRole() == PotentialRole.UTILITY) {
            potential.setUtilityVariable(treeADDPotential.getUtilityVariable());
        }
        treeADDPotential.setTopVariable(newTopVariable);
        List<TreeADDBranch> newBranches = new ArrayList<TreeADDBranch>();
        if (newTopVariable.getVariableType() == VariableType.FINITE_STATES
                || newTopVariable.getVariableType() == VariableType.DISCRETIZED) {
            // for (State state : newTopVariable.getStates()) {
            for (int i = newTopVariable.getStates().length - 1; i >= 0; i--) {
                List<State> branchStates = new ArrayList<State>();
                // branchStates.add(state);
                branchStates.add(newTopVariable.getStates()[i]);
                newBranches.add(new TreeADDBranch(branchStates, potential, newTopVariable,
                        variables));
            }
        } else if (newTopVariable.getVariableType() == VariableType.NUMERIC) {
            // Top variable domain
            Float minDomainLimit = (float) newTopVariable.getPartitionedInterval().getMin();
            Float maxDomainLimit = (float) newTopVariable.getPartitionedInterval().getMax();
            // true -> [)
            boolean isLeftClosed = newTopVariable.getPartitionedInterval().isLeftClosed();
            boolean isRightClosed = newTopVariable.getPartitionedInterval().isRightClosed();
            boolean minBelongsToLeftDomain = !isLeftClosed;
            boolean maxBelongsToLeftDomain = isRightClosed;
            newBranches.add(new TreeADDBranch(
                    new Threshold(minDomainLimit, minBelongsToLeftDomain), new Threshold(
                            maxDomainLimit, maxBelongsToLeftDomain), potential, newTopVariable,
                    variables));
        }
        treeADDPotential.setBranches(newBranches);
        // treeADDPotential = newTree;
        TreeADDModel model = (TreeADDModel) jTree.getModel();
        // model.fireNodesChanged(path);
        model.notifyTreeStructureChanged(path);
        jTree.expandPath(path);
        for (int i = 0; i < jTree.getRowCount(); i++) {
            jTree.expandRow(i);
        }
    }

    // when clicking on a brach
    private void actionAddTreeADD(ActionEvent ae, TreeADDBranch branch, TreePath path) {
        List<Variable> parentVariables = branch.getParentVariables();
        Variable parentTopVariable = branch.getTopVariable();
        JMenuItem menuTopVariable = (JMenuItem) ae.getSource();
        // to get the variable
        Variable newTopVariable = null;
        for (Variable variable : parentVariables) {
            if (variable.getName() == menuTopVariable.getText()) {
                newTopVariable = variable;
            }
        }
        List<Variable> newTreeVariables = new ArrayList<Variable>();
        // initialize variables of the new tree
        for (Variable variable : parentVariables) {
            if (variable != parentTopVariable) {
                newTreeVariables.add(variable);
            }
        }
        if (newTopVariable == null) {
            // that means that is a previous variable somewhere in the path to
            // treeADDPotetentialRoot that grouped two or more states in a
            // previous branch
            // or that is a continuous variable that appears before in the tree
            for (Variable variable : rootTreeADDPotential.getVariables()) {
                if (variable.getName() == menuTopVariable.getText()) {
                    newTopVariable = variable;
                }
            }
            if (newTopVariable.getVariableType() == VariableType.FINITE_STATES
                    || newTopVariable.getVariableType() == VariableType.DISCRETIZED) {
                List<State> groupedStates = null;
                TreePath parentPath = path.getParentPath(); // treeADD
                while (parentPath.getLastPathComponent() != rootTreeADDPotential) {
                    TreePath grandParentPath = parentPath.getParentPath();// branch
                    if (grandParentPath.getLastPathComponent() instanceof TreeADDBranch) {
                        // if a branch has more then one state is possible to
                        // offer, as top variable, the top variable of this
                        // branch
                        TreeADDBranch treeADDBranch = (TreeADDBranch) grandParentPath
                                .getLastPathComponent();
                        TreePath greatGrandFatherPath = grandParentPath.getParentPath();
                        if (treeADDBranch.getBranchStates().size() > 1
                                && ((TreeADDPotential) greatGrandFatherPath.getLastPathComponent())
                                        .getTopVariable() == newTopVariable) {
                            groupedStates = treeADDBranch.getBranchStates();
                            break;
                        }
                    }
                    parentPath = grandParentPath;
                }
                State[] states = new State[groupedStates.size()];
                for (int i = 0; i < groupedStates.size(); i++) {
                    states[i] = groupedStates.get(i);
                }
                newTopVariable.setStates(states);
                newTreeVariables.add(newTopVariable);
            } else if (newTopVariable.getVariableType() == VariableType.NUMERIC) {
                PartitionedInterval partitionedInterval = null;
                TreePath parentPath = path.getParentPath(); // treeADD
                while (parentPath.getLastPathComponent() != rootTreeADDPotential) {
                    TreePath grandParentPath = parentPath.getParentPath();// branch
                    if (grandParentPath.getLastPathComponent() instanceof TreeADDBranch) {
                        // if a branch has more then one state is possible to
                        // offer, as top variable, the top variable of this
                        // branch
                        TreeADDBranch treeADDBranch = (TreeADDBranch) grandParentPath
                                .getLastPathComponent();
                        // TreePath greatGrandFatherPath =
                        // grandParentPath.getParentPath();
                        if (treeADDBranch.getTopVariable() == newTopVariable) {
                            Threshold min = treeADDBranch.getMinThreshold();
                            Threshold max = treeADDBranch.getMaxThreshold();
                            partitionedInterval = new PartitionedInterval(min.belongsToLeft(),
                                    (double) min.getLimit(), (double) max.getLimit(),
                                    max.belongsToLeft());
                            break;
                        }
                    }
                    parentPath = grandParentPath;
                }
                newTopVariable.setPartitionedInterval(partitionedInterval);
                newTreeVariables.add(newTopVariable);
            }
        }
        TreeADDPotential newTreeADD = new TreeADDPotential(newTreeVariables, newTopVariable,
                rootTreeADDPotential.getPotentialRole());
        if (rootTreeADDPotential.getPotentialRole() == PotentialRole.CONDITIONAL_PROBABILITY) {
            newTreeADD = new TreeADDPotential(newTreeVariables, newTopVariable,
                    rootTreeADDPotential.getPotentialRole());
        } else if (rootTreeADDPotential.getPotentialRole() == PotentialRole.UTILITY) {
            newTreeADD = new TreeADDPotential(newTreeVariables, newTopVariable,
                    rootTreeADDPotential.getPotentialRole(),
                    rootTreeADDPotential.getUtilityVariable());
        }
        // set the new tree to its owner branch
        branch.setPotential(newTreeADD);
        // update the tree recursively bottom-up
        Object parentPath = path.getParentPath();
        Object previousParentPath = path.getLastPathComponent();
        TreeADDModel model = (TreeADDModel) jTree.getModel();
        while (parentPath != null) {
            previousParentPath = parentPath;
            parentPath = ((TreePath) parentPath).getParentPath();
        }
        model.notifyTreeStructureChanged((TreePath) previousParentPath);
        for (int i = 0; i < jTree.getRowCount(); i++) {
            jTree.expandRow(i);
        }
        jTree.expandPath((TreePath) parentPath);
        model.notifyTreeInsert(path, newTreeADD);
        jTree.expandPath(path);
        jTree.expandPath(path.pathByAddingChild(newTreeADD));
    }

    /**
     * Action to edit a potential
     * 
     * @param ae
     * @param branch
     * @param path
     * @throws NotEnoughMemoryException
     */
    private void actionEditPotential(ActionEvent ae, TreeADDBranch branch, TreePath path) {
        Object parentPath = path.getParentPath();
        TreeADDPotential parentTreeADD = (TreeADDPotential) ((TreePath) parentPath)
                .getLastPathComponent();
        Potential potential = branch.getPotential();
        ProbNet dummyProbNet = new ProbNet();
        dummyProbNet.addPotential(potential);
        ProbNode dummy = null;
        if (potential.getPotentialRole() == PotentialRole.CONDITIONAL_PROBABILITY) {
            Variable conditionedVariable = parentTreeADD.getConditionedVariable();
            dummy = dummyProbNet.getProbNode(conditionedVariable);
            for (Variable variable : potential.getVariables()) {
                if (variable.equals(conditionedVariable)) {
                    continue;
                }
                try {
                    dummyProbNet.addLink(variable, conditionedVariable, true);
                } catch (NodeNotFoundException e) {
                    throw new RuntimeException("Node not found: " + e.getMessage());
                }
            }
        } else if (potential.getPotentialRole() == PotentialRole.UTILITY) {
            Variable utilityVariable = parentTreeADD.getUtilityVariable();
            dummy = dummyProbNet.getProbNode(utilityVariable);
            for (Variable variable : potential.getVariables()) {
                /*
                 * if (variable==utilityVariable) { continue; }
                 */
                try {
                    dummyProbNet.addLink(variable, utilityVariable, true);
                } catch (NodeNotFoundException e) {
                    throw new RuntimeException("Node not found: " + e.getMessage());
                }
            }
        }
        PotentialEditDialog dialog = new PotentialEditDialog(Utilities.getOwner(this), dummy, false);
        if (dialog.requestValues() == NodePropertiesDialog.OK_BUTTON) {
            Potential retPotential = dummy.getPotentials().get(0);
            if (potential.getPotentialRole() == PotentialRole.UTILITY) {
                retPotential.setUtilityVariable(parentTreeADD.getUtilityVariable());
            }
            if (parentTreeADD.getPotentialRole() != retPotential.getPotentialRole()) {
                throw new RuntimeException("Expected role " + parentTreeADD.getPotentialRole()
                        + ", found: " + retPotential.getPotentialRole());
            }
            branch.setPotential(retPotential);
            TreeADDModel model = (TreeADDModel) jTree.getModel();
            model.notifyTreeStructureChanged(path);
            jTree.expandPath(path);
            int i = jTree.getRowForPath(path);
            jTree.expandRow(i);
        }
    }

    @SuppressWarnings("unused")
    private void actionAddNewVariableToBranch(ActionEvent ae, TreeADDBranch branch, TreePath path) {
        Object parentPath = path.getParentPath();
        TreeADDPotential parentTreeADD = (TreeADDPotential) ((TreePath) parentPath)
                .getLastPathComponent();
        Potential branchPotential = branch.getPotential();
        PotentialRole role = branchPotential.getPotentialRole();
        JMenuItem menuPotentialVariable = (JMenuItem) ae.getSource();
        List<Variable> parentVariables = branch.getParentVariables();
        List<Variable> branchPotentialVariables = branchPotential.getVariables();
        Variable newPotentialVariable = null;
        for (Variable var : parentVariables) {
            if (var.getName() == menuPotentialVariable.getText()) {
                newPotentialVariable = var;
            }
        }
        branchPotentialVariables.add(newPotentialVariable);
        UniformPotential potential = new UniformPotential(branchPotentialVariables, role);
        if (role == PotentialRole.UTILITY) {
            potential.setUtilityVariable(parentTreeADD.getUtilityVariable());
        }
        branch.setPotential(potential);
        TreeADDModel model = (TreeADDModel) jTree.getModel();
        model.notifyTreeInsert(path, potential);
        model.notifyTreeStructureChanged(path);
        jTree.expandPath(path);
    }

    @SuppressWarnings("unused")
    private void actionAddNewTreeTopVariable(ActionEvent ae, Object branch, TreePath path,
            Object parentTreeBranch) {
        if (!(branch instanceof TreeADDBranch) || !(parentTreeBranch instanceof TreeADDPotential)) {
            throw new RuntimeException("Expected TreeADDBranch class, found: "
                    + branch.getClass().getName() + "Expected TreeADDPotential class, found: "
                    + parentTreeBranch.getClass().getName());
        }
        // find the top variable to create a new tree with that topVariable and
        // expand tree with new tree
        JMenuItem menuTopVariable = (JMenuItem) ae.getSource();// topVariable
        List<Variable> parentVariables = ((TreeADDPotential) parentTreeBranch).getVariables();
        Variable parentTopVariable = ((TreeADDPotential) parentTreeBranch).getTopVariable();
        List<Variable> variables = new ArrayList<Variable>();
        for (Variable var : parentVariables) {
            if (var == parentTopVariable) {
                continue;
            } else {
                variables.add(var);
            }
        }
        Variable newTreeTopVariable = null;
        for (Variable var : parentVariables) {
            if (var.getName() == menuTopVariable.getText()) {
                newTreeTopVariable = var;
            }
        }
        TreeADDPotential newTree = ((TreeADDPotential) parentTreeBranch);
        if (((TreeADDPotential) parentTreeBranch).getPotentialRole() == PotentialRole.CONDITIONAL_PROBABILITY) {
            newTree = new TreeADDPotential(variables, newTreeTopVariable,
                    rootTreeADDPotential.getPotentialRole());
        } else if (((TreeADDPotential) parentTreeBranch).getPotentialRole() == PotentialRole.UTILITY) {
            newTree = new TreeADDPotential(variables, newTreeTopVariable,
                    rootTreeADDPotential.getPotentialRole(),
                    ((TreeADDPotential) parentTreeBranch).getUtilityVariable());
        }
        TreeADDModel model = (TreeADDModel) jTree.getModel();
        model.notifyTreeInsert(path, newTree);
        jTree.expandPath(path.pathByAddingChild(newTree));
    }

    /**
     * TODO: Convert to Inner Class of the Viewer?
     */
    class TreeADDViewer_treeExpansionAdapter implements TreeExpansionListener {
        private TreeADDEditorPanel treeADDController;

        TreeADDViewer_treeExpansionAdapter(TreeADDEditorPanel treeADDControler) {
            this.treeADDController = treeADDControler;
        }

        public void treeExpanded(TreeExpansionEvent event) {
            treeADDController.tree_treeExpanded(event);
        }

        public void treeCollapsed(TreeExpansionEvent event) {
        }
    }

    /**
     * TODO: Convert to Inner Class of the Viewer?
     */
    class TreeADDViewer_treeWillExpandAdapter implements TreeWillExpandListener {
        private TreeADDEditorPanel treeADDController;

        TreeADDViewer_treeWillExpandAdapter(TreeADDEditorPanel treeADDControler) {
            this.treeADDController = treeADDControler;
        }

        public void treeWillExpand(TreeExpansionEvent event) {
        }

        public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
            treeADDController.tree_treeWillCollapse(event);
        }
    }

    /**
     * @author jfernandez
     * @author myebra
     */
    class TreeADDViewer_mouseAdapter extends MouseAdapter {
        private TreeADDEditorPanel treeADDController;

        TreeADDViewer_mouseAdapter(TreeADDEditorPanel adaptee) {
            this.treeADDController = adaptee;
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                treeADDController.xx = e.getX();
                treeADDController.yy = e.getY();
                TreePath path = treeADDController.jTree.getPathForLocation(treeADDController.xx,
                        treeADDController.yy);
                if (path != null) {
                    Object node = path.getLastPathComponent();
                    if (node instanceof TreeADDBranch) {
                        TreeADDBranch branch = (TreeADDBranch) node;
                        treeADDController.setPopupItemBranch(e, (TreeADDBranch) branch, path);
                    } else if (node instanceof Potential) {
                        if (node instanceof TreeADDPotential) {
                            treeADDController.setPopupItemsTreeADD(e, (TreeADDPotential) node);
                        } else {
                            TreePath parentPath = path.getParentPath();
                            Object parent = parentPath.getLastPathComponent();
                            if (parent instanceof TreeADDBranch) {
                                treeADDController.setPopupItemBranch(e, (TreeADDBranch) parent,
                                        parentPath);
                            }
                        }
                    }
                    treeADDController.popupMenu.show(e.getComponent(), treeADDController.xx,
                            treeADDController.yy);
                }
            }
        }
    }
}