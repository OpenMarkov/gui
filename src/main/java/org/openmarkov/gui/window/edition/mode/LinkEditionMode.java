/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.gui.window.edition.mode;

import org.openmarkov.core.action.base.PNEdit;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.model.network.Point2D;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.gui.util.GUIUtils;
import org.openmarkov.gui.window.edition.networkEditorPanel.NetworkEditorPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

@EditionState(name = "Edit.Mode.Link", icon = "link.gif", cursor = "link.gif") public class LinkEditionMode
        extends EditionMode {
    
    public LinkEditionMode(NetworkEditorPanel networkEditorPanel, ProbNet probNet) {
        super(networkEditorPanel, probNet);
    }
    
    @Override public void mousePressed(MouseEvent e, Point2D.Double cursorPosition, Graphics2D g) {
        if (!(SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1 && GUIUtils.noMouseModifiers(e))) {
            return;
        }
        visualNetwork.startLinkCreation(cursorPosition, g);
    }
    
    @Override public void mouseReleased(MouseEvent e, Point2D.Double position, Graphics2D g) throws DoEditException {
        if (!SwingUtilities.isLeftMouseButton(e)) {
            return;
        }
        visualNetwork.finishLinkCreation(position, g);
        networkEditorPanel.repaint();
    }
    
    @Override public void mouseMoved(MouseEvent e, Point2D.Double cursorPosition, double diffX, double diffY,
                                     Graphics2D g) {
        visualNetwork.updateLinkCreation(cursorPosition);
        networkEditorPanel.repaint();
    }
    
    @Override public void keyTyped(KeyEvent e) {
    
    }
    
    @Override public void keyPressed(KeyEvent e) {
    
    }
    
    @Override public void keyReleased(KeyEvent e) {
    
    }
}
