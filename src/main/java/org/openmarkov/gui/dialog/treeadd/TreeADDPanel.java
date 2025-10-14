/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.dialog.treeadd;

import org.openmarkov.core.action.core.SetPotentialEdit;
import org.openmarkov.core.exception.ConstraintViolatedException;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.gui.dialog.common.PotentialPanel;
import org.openmarkov.gui.dialog.common.PotentialPanelPlugin;

import java.awt.*;

@SuppressWarnings("serial") @PotentialPanelPlugin(potentialType = "Tree/ADD") public class TreeADDPanel
        extends PotentialPanel {
    /**
     * The builder object of Tree - ADDs
     */
    private TreeADDEditorPanel treeADDController;
    /**
     * The node edited
     */
    private Node node;
    
    public TreeADDPanel(Node node) {
        super();
        setData(node);
    }
    
    @Override public boolean saveChanges() throws ConstraintViolatedException {
        SetPotentialEdit setPotentialEdit = new SetPotentialEdit(node, treeADDController.getTreePotential());
        ProbNet probNet = node.getProbNet();
        setPotentialEdit.doEdit(probNet);
        return true;
    }
    
    @Override public void setData(Node node) {
        setLayout(new BorderLayout());
        this.node = node;
        treeADDController = new TreeADDEditorPanel(new TreeADDCellRenderer(node.getProbNet()), node);
        removeAll();
        // treeADDController.setMaximumSize(new Dimension(10, 10));
        // treeADDController.setPreferredSize(new Dimension(10, 10));
        add(treeADDController, BorderLayout.CENTER);
        setName("nodeTreeADDPotentialPanel");
        setBackground(Color.blue);
        // nodeADDPotentialPanel.setNewNode(newNode);
        // nodeADDPotentialPanel.setNodeProperties(node);
    }
    
    /**
     * @param readOnly the readOnly to set
     */
    @Override public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        treeADDController.setReadOnly(readOnly);
    }
    
    @Override public void close() {
        // TODO Auto-generated method stub
    }
}
