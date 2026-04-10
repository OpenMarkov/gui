/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.gui.window.edition.mode;

import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.UnrecoverableException;
import org.openmarkov.core.model.network.Point2D;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.gui.action.MoveNodeEdit;
import org.openmarkov.gui.graphic.VisualNode;
import org.openmarkov.gui.window.edition.networkEditorPanel.NetworkEditorPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EditionState(name = "Edit.Mode.Selection", icon = "selection.gif")
public class SelectionEditionMode extends EditionMode {
    
    private static final int NODE_SPEED_ON_ARROW_PRESS = 2;
    
    private SelectionState selectionState;
    
    private boolean currentlyHoldingMouse;
    private final Set<Integer> currentlyHeldKeys;
    
    public SelectionEditionMode(NetworkEditorPanel networkEditorPanel, ProbNet probNet) {
        super(networkEditorPanel, probNet);
        this.selectionState = SelectionState.NOTHING;
        this.currentlyHoldingMouse = false;
        this.currentlyHeldKeys = new HashSet<>();
    }
    
    @Override public void mousePressed(MouseEvent e, Point2D.Double position, Graphics2D g) {
        this.currentlyHoldingMouse = true;
        if (this.selectionState != SelectionState.NOTHING || !SwingUtilities.isLeftMouseButton(e)) {
            return;
        }
        if (e.isControlDown() || e.isShiftDown()) {
            this.visualNetwork.addToSelection(position, g);
            return;
        }
        if (this.visualNetwork.selectElementInPosition(position, g) == null) {
            this.visualNetwork.startSelectionRectangle(position);
            this.setSelectionState(SelectionState.SELECTING);
        }
    }
    
    @Override public void mouseDragged(MouseEvent e, Point2D.Double position, double diffX, double diffY,
                                       Graphics2D g) {
        if (!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }
        if (this.selectionState == SelectionState.SELECTING) {
            this.visualNetwork.updateSelectionRectangle(diffX, diffY);
        } else if (this.selectionState == SelectionState.MOVING ||
                (this.selectionState == SelectionState.NOTHING && !this.visualNetwork.getSelectedNodes().isEmpty())) {
            this.setSelectionState(SelectionState.MOVING);
            this.visualNetwork.moveSelectedElements(diffX, diffY);
        }
        this.networkEditorPanel.repaint();
    }
    
    @Override
    public void mouseReleased(MouseEvent e, Point2D.Double position, Graphics2D g) throws DoEditException {
        this.currentlyHoldingMouse = false;
        switch (this.selectionState) {
            case SelectionState.NOTHING -> {
            }
            case SelectionState.MOVING -> this.tryFinishNodesMovements();
            case SelectionState.SELECTING -> {
                this.visualNetwork.finishSelectionRectangle(position);
                this.setSelectionState(SelectionState.NOTHING);
            }
        }
        this.networkEditorPanel.repaint();
    }
    
    private void tryFinishNodesMovements() throws DoEditException {
        if (this.isMovingNodes()) {
            return;
        }
        List<VisualNode> movedNodes = this.visualNetwork.fillVisualNodesSelected();
        new MoveNodeEdit(movedNodes).executeEdit();
        this.networkEditorPanel.adjustPanelDimension();
        this.setSelectionState(SelectionState.NOTHING);
    }
    
    /**
     * Changes the state of the selection and carries out the necessary actions
     * in each case.
     *
     * @param newState new mouse state.
     */
    private void setSelectionState(SelectionState newState) {
        this.networkEditorPanel.setCursor(newState.getCursor());
        this.selectionState = newState;
    }
    
    @Override public void keyTyped(KeyEvent e) {
    
    }
    
    
    @Override public void keyPressed(KeyEvent e) {
        this.currentlyHeldKeys.add(e.getKeyCode());
        if (this.visualNetwork.getSelectedNodes().isEmpty()) {
            return;
        }
        if (this.selectionState != SelectionState.MOVING && this.selectionState != SelectionState.NOTHING) {
            return;
        }
        int diffX = 0, diffY = 0;
        for (var key : this.currentlyHeldKeys) {
            switch (key) {
                case KeyEvent.VK_UP -> diffY -= SelectionEditionMode.NODE_SPEED_ON_ARROW_PRESS;
                case KeyEvent.VK_RIGHT -> diffX += SelectionEditionMode.NODE_SPEED_ON_ARROW_PRESS;
                case KeyEvent.VK_DOWN -> diffY += SelectionEditionMode.NODE_SPEED_ON_ARROW_PRESS;
                case KeyEvent.VK_LEFT -> diffX -= SelectionEditionMode.NODE_SPEED_ON_ARROW_PRESS;
            }
        }
        if (diffX == 0 && diffY == 0) {
            return;
        }
        this.setSelectionState(SelectionState.MOVING);
        this.visualNetwork.moveSelectedElements(diffX, diffY);
        this.networkEditorPanel.repaint();
    }
    
    @Override public void keyReleased(KeyEvent e) {
        boolean wasHoldingAnArrow = this.isHoldingAnArrow();
        this.currentlyHeldKeys.remove(e.getKeyCode());
        if (wasHoldingAnArrow && !this.isHoldingAnArrow()) {
            try {
                this.tryFinishNodesMovements();
            } catch (DoEditException ex) {
                throw new UnrecoverableException(ex);
            }
        }
    }
    
    public boolean isMovingNodes() {
        return this.selectionState == SelectionState.MOVING && (this.currentlyHoldingMouse || isHoldingAnArrow());
    }
    
    private boolean isHoldingAnArrow() {
        return this.currentlyHeldKeys.contains(KeyEvent.VK_UP)
                || this.currentlyHeldKeys.contains(KeyEvent.VK_RIGHT)
                || this.currentlyHeldKeys.contains(KeyEvent.VK_DOWN)
                || this.currentlyHeldKeys.contains(KeyEvent.VK_LEFT);
    }
    
}
