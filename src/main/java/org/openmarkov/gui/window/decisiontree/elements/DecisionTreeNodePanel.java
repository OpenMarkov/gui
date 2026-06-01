/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.gui.window.decisiontree.elements;

import org.openmarkov.core.exception.UnreachableCodeException;
import org.openmarkov.core.model.decisiontree.DecisionTreeNode;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.gui.graphic.VisualNode;
import org.openmarkov.gui.window.decisiontree.format.DecisionTreeUtilityFormatters;

import javax.swing.*;
import java.awt.*;

/**
 * GUI representation of a logical decision tree node.
 * It manages node-specific icons and displays utility values for terminal nodes.
 *
 * @author CISIAD, UNED
 * @version 2019
 */
@SuppressWarnings("serial")
public non-sealed class DecisionTreeNodePanel extends DecisionTreeElementPanel {
    
    /** The underlying logical tree node. */
    private final DecisionTreeNode treeNode;
    
    /**
     * Constructs a panel for a specific decision tree node and initializes its icon.
     *
     * @param treeNode The logical node to be represented.
     */
    public DecisionTreeNodePanel(DecisionTreeNode treeNode) {
        this.treeNode = treeNode;
        super.initialize();
    }
    
    @Override public JComponent makeSummary() {
        var visualNode = switch (treeNode.getNodeType()) {
            case CHANCE, EVENT, DECISION, UTILITY -> new VisualNode(new Node(null, new Variable(treeNode.getVariable()
                                                                              .getName()), treeNode.getNodeType()), null);
            default -> throw new UnreachableCodeException("Only kinds of node enabled in Decision Tree are Chance, Decision, Utility and Event");
        };
        JPanel panel = new JPanel() {
            @Override public void paint(Graphics g) {
                visualNode.paint((Graphics2D) g);
            }
        };
        Shape nodeDimensions = visualNode.getShape((Graphics2D) panel.getGraphics());
        double width = nodeDimensions.getBounds2D().getWidth();
        double height = nodeDimensions.getBounds2D().getHeight();
        //TODO: Fix missalignments
        visualNode.setTemporalPosition(new org.openmarkov.core.model.network.Point2D.Double(2, 0));
        panel.setSize(new Dimension((int) width + 2 + 2, (int) height + 2));
        panel.setMinimumSize(new Dimension((int) width + 2 + 2, (int) height + 2));
        panel.setPreferredSize(new Dimension((int) width + 2 + 2, (int) height + 2));
        return panel;
    }
    
    /**
     * Gets the associated logical tree node.
     *
     * @return The {@link DecisionTreeNode} linked to this panel.
     */
    public DecisionTreeNode getTreeNode() {
        return treeNode;
    }
    
    
    /**
     * Updates the right label with formatted utility data if the node is a utility type.
     * {@inheritDoc}
     */
    @Override
    public void update(boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (treeNode.getNodeType() == NodeType.UTILITY) {
            descriptionLabel.setText(DecisionTreeUtilityFormatters.format(treeNode.getUtility(), df, false));
        }
    }
    
    /**
     * Gets the type of the underlying node.
     *
     * @return The {@link NodeType} of the associated tree node.
     */
    public NodeType getNodeType() {
        return treeNode.getNodeType();
    }
}
