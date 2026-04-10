/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.window.edition.mode;

import org.openmarkov.core.action.core.AddNodeEdit;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.model.network.*;
import org.openmarkov.gui.util.GUIDefaultStates;
import org.openmarkov.gui.util.GUIUtils;
import org.openmarkov.gui.window.edition.networkEditorPanel.NetworkEditorPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.List;

/**
 * Edition mode for creating nodes of a given {@link NodeType}.
 * One instance per node type is registered in {@link EditionModeManager};
 * there is no need for per-type subclasses.
 */
public class NodeEditionMode extends EditionMode {
    private final NodeType nodeType;

    public NodeEditionMode(NetworkEditorPanel networkEditorPanel, ProbNet probNet, NodeType nodeType) {
        super(networkEditorPanel, probNet);
        this.nodeType = nodeType;
    }
    
    @Override
    public void mousePressed(MouseEvent e, Point2D.Double position, Graphics2D g) throws DoEditException {
        if (!(SwingUtilities.isLeftMouseButton(e) && GUIUtils.noMouseModifiers(e))) {
            return;
        }
        if (visualNetwork.getElementInPosition(position, g) != null) {
            return;
        }
        probNet.getPNESupport().setWithUndo(true);
        HashSet<String> existingNames = new HashSet<>();
        for (Node node : probNet.getNodes()) {
            String name = node.getName();
            if (name.contains("[")) {
                existingNames.add(name.substring(0, name.indexOf(" [")));
            } else {
                existingNames.add(node.getName());
            }
        }
        String nodeName = Util.getNextNodeName(nodeType, existingNames);
        State[] states = DefaultStates.getStatesNodeType(nodeType, probNet.getDefaultStates());
        for (int i = 0; i < states.length; i++) {
            states[i] = new State(GUIDefaultStates.getString(states[i].getName()));
        }
        Variable variable = new Variable(nodeName, states);
        if (probNet.onlyTemporal()) {
            // default value
            variable.setBaseName(nodeName);
            variable.setTimeSlice(0);
        }
        List<Criterion> decisionCriteria = probNet.getDecisionCriteria();
        if (nodeType == NodeType.UTILITY && decisionCriteria != null) {
            variable.setDecisionCriterion(decisionCriteria.getFirst());
        }
        
        AddNodeEdit addNodeEdit = new AddNodeEdit(probNet, variable, nodeType, position);
        addNodeEdit.executeEdit();
        networkEditorPanel.adjustPanelDimension();
        networkEditorPanel.repaint();
    }
    
    @Override public void mouseReleased(MouseEvent e, Point2D.Double cursorPosition, Graphics2D g) {
        // TODO Auto-generated method stub
    }
    
    @Override public void mouseDragged(MouseEvent e, Point2D.Double position, double diffX, double diffY,
                                       Graphics2D g) {
        // TODO Auto-generated method stub
    }
    
    @Override public void keyTyped(KeyEvent e) {
    
    }
    
    @Override public void keyPressed(KeyEvent e) {
    
    }
    
    @Override public void keyReleased(KeyEvent e) {
    
    }
}
