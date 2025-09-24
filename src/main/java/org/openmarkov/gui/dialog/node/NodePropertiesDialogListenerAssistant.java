/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.dialog.node;

import org.openmarkov.core.exception.InvalidArgumentException;
import org.openmarkov.core.exception.UnrecoverableException;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.Util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class NodePropertiesDialogListenerAssistant implements ActionListener, ItemListener {
    private NodePropertiesDialog dialog;
    private NodeDefinitionPanel definitionPanel;
    private Node properties;
    
    /**
     * constructor
     */
    public NodePropertiesDialogListenerAssistant(NodePropertiesDialog dialog) {
        this.dialog = dialog;
        definitionPanel = (NodeDefinitionPanel) dialog.getNodeDefinitionPanel();
        properties = dialog.getNodeProperties();
    }
    
    /**
     * @return the dialog
     */
    protected NodePropertiesDialog getDialog() {
        return dialog;
    }
    
    /**
     * @param dialog the dialog to set
     */
    protected void setDialog(NodePropertiesDialog dialog) {
        this.dialog = dialog;
    }
    
    /**
     * Invoked when an action occurs.
     *
     * @param e event information.
     */
    @Override public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(definitionPanel.getJTextFieldNodeName())) {
            try {
                checkName();
            } catch (InvalidArgumentException ex) {
                definitionPanel.getJTextFieldNodeName().setText(properties.getName());
                throw new UnrecoverableException(ex);
            }
        }
    }
    
    /**
     * Invoked when an item has been selected.
     *
     * @param e event information.
     */
    @Override public void itemStateChanged(ItemEvent e) {
    }
    
    /**
     * This method checks that the name field is filled and there isn't any node
     * with the same name.
     *
     * @return true, if the name field isn't empty and there isn't any node with
     * this name; otherwise, false.
     */
    public void checkName() throws InvalidArgumentException {
        String name = definitionPanel.getJTextFieldNodeName().getText();
        if ((name == null) || name.isEmpty()) {
            definitionPanel.getJTextFieldNodeName().requestFocus();
            throw new InvalidArgumentException(name, "name", "cannot be empty");
        }
        if (!properties.getName().equals(name) && Util.existNode(properties.getProbNet(), name)) {
            definitionPanel.getJTextFieldNodeName().requestFocus();
            throw new InvalidArgumentException(name, "name", "already belongs to another node");
        }
    }
    
    /**
     * This method checks that the purpose field is filled if this field is
     * enabled.
     *
     * @return true, if the purpose field isn't empty; otherwise, false.
     */
    public static boolean checkPurpose() {
        return true;
    }
}
