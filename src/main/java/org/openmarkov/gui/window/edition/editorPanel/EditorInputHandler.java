package org.openmarkov.gui.window.edition.editorPanel;

import org.jetbrains.annotations.Nullable;
import org.openmarkov.core.action.core.AddNodeEdit;
import org.openmarkov.core.exception.*;
import org.openmarkov.core.model.network.Point2D;
import org.openmarkov.gui.exception.NotEnoughtMemoryException;
import org.openmarkov.gui.exception.PreResolutionNodeInInferenceException;
import org.openmarkov.gui.graphic.VisualElement;
import org.openmarkov.gui.graphic.VisualLink;
import org.openmarkov.gui.graphic.VisualNode;
import org.openmarkov.gui.graphic.VisualState;
import org.openmarkov.gui.menutoolbar.menu.ContextualMenu;
import org.openmarkov.gui.menutoolbar.menu.ContextualMenuFactory;
import org.openmarkov.gui.util.GUIUtils;
import org.openmarkov.gui.window.edition.NetworkPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Optional;

class EditorInputHandler implements MouseListener, MouseMotionListener, KeyListener {
    private final EditorPanel editorPanel;
    
    EditorInputHandler(EditorPanel editorPanel) {
        this.editorPanel = editorPanel;
    }
    
    /**
     * Invoked when a mouse button has been clicked (pressed and released) on
     * the component.
     *
     * @param e mouse event information.
     */
    @Override public void mouseClicked(MouseEvent e) {
    }
    
    private int lastClickCount = 0;
    private boolean lastLeftClickProducedANode = false;
    
    /**
     * Invoked when a mouse button has been pressed on the component.
     *
     * @param e mouse event information.
     */
    @Override public void mousePressed(MouseEvent e) {
        // requestFocusInWindow(); Activate if nodes can't be moved by arrows.
        
        if (e.getClickCount() <= (this.lastClickCount + 1)) {
            this.lastLeftClickProducedANode = false;
            this.lastClickCount = Math.max(e.getClickCount() - 1, 0);
        } else {
            this.lastClickCount += 1;
        }
        // requestFocusInWindow(); Activate if nodes can't be moved by arrows.
        Graphics2D g = (Graphics2D) this.editorPanel.getGraphics();
        this.cursorPosition.setLocation(this.editorPanel.getZoomManager()
                                                        .screenToPanel(e.getX()), this.editorPanel.getZoomManager()
                                                                                                  .screenToPanel(e.getY()));
        // Specific functionality depending on the edition mode;
        try {
            var oldNodesCount = this.editorPanel.getNetworkPanel().getProbNet().getNodes().size();
            this.editorPanel.getEditionMode().mousePressed(e, this.cursorPosition, g);
            if (e.getClickCount() == 1) {
                int newNodesCount = this.editorPanel.getNetworkPanel().getProbNet().getNodes().size();
                this.lastLeftClickProducedANode = oldNodesCount < newNodesCount;
            }
        } catch (DoEditException ex) {
            throw new UnrecoverableException(ex);
        }
        // Generic functionality regardless of the edition mode
        if (SwingUtilities.isRightMouseButton(e)) {
            this.showContextualMenu(e, g);
            this.editorPanel.repaint();
            return;
        }
        if (!SwingUtilities.isLeftMouseButton(e)) {
            this.editorPanel.repaint();
            return;
        }
        VisualNode node;
        if (e.isAltDown() && e.getClickCount() != 2) {
            node = this.editorPanel.getVisualNetwork().whatNodeInPosition(this.cursorPosition, g);
            if (node != null) {
                if (!node.isSelected()) {
                    this.editorPanel.getVisualNetwork().setSelectedAllObjects(false);
                    this.editorPanel.getVisualNetwork().setSelectedNode(node, true);
                }
                try {
                    this.editorPanel.showPotentialDialog(this.editorPanel.getNetworkPanel()
                                                                         .getWorkingMode() != NetworkPanel.WorkingMode.EDITION);
                } finally {
                    this.editorPanel.repaint();
                    return;
                }
            }
        }
        if (!(e.getClickCount() == 2 && GUIUtils.noMouseModifiers(e))) {
            this.editorPanel.repaint();
            return;
        }
        if (this.editorPanel.getNetworkPanel().getWorkingMode() == NetworkPanel.WorkingMode.EDITION) {
            // If we are in Edition Mode a double click must open
            // the corresponding properties dialog (for node, link
            // or network)
            node = this.editorPanel.getVisualNetwork().whatNodeInPosition(this.cursorPosition, g);
            if (node != null) {
                try {
                    boolean userAcceptedChanges = this.editorPanel.changeNodeProperties(node, this.lastLeftClickProducedANode);
                    if (!userAcceptedChanges && this.lastLeftClickProducedANode) {
                        while (true) {
                            if (this.editorPanel.getNetworkPanel().getProbNet().getPNESupport()
                                                .undo()
                                                .stream()
                                                .anyMatch(edit -> edit instanceof AddNodeEdit)) {
                                break;
                            }
                        }
                        this.editorPanel.getNetworkPanel().getProbNet().getPNESupport().removeUndoneEdits();
                    }
                } catch (NotEvaluableNetworkException | NonProjectablePotentialException | NotEnoughtMemoryException |
                         IncompatibleEvidenceException | CannotNormalizePotentialException |
                         ConstraintViolatedException | NotSupportedOperationException ex) {
                    this.editorPanel.repaint();
                    throw new UnrecoverableException(ex);
                }
            } else {
                VisualLink link = this.editorPanel.getVisualNetwork().whatLinkInPosition(this.cursorPosition, g);
                if (link != null) {
                    this.editorPanel.changeLinkProperties(link);
                } else {
                    this.editorPanel.changeNetworkProperties();
                }
            }
            this.editorPanel.repaint();
            return;
        }
        
        if (this.editorPanel.getVisualNetwork().whatStateInPosition(this.cursorPosition, g) == null) {
            if ((this.editorPanel.getVisualNetwork().whatNodeInPosition(this.cursorPosition, g) != null) && (
                    this.editorPanel.getVisualNetwork().whatInnerBoxInPosition(this.cursorPosition, g) == null
            )) {
                try {
                    this.editorPanel.changeNodeProperties();
                } catch (NotEvaluableNetworkException | NonProjectablePotentialException | NotEnoughtMemoryException |
                         IncompatibleEvidenceException | CannotNormalizePotentialException |
                         ConstraintViolatedException | NotSupportedOperationException ex) {
                    throw new UnrecoverableException(ex);
                } finally {
                    this.editorPanel.repaint();
                }
            }
            this.editorPanel.repaint();
            return;
        }
        
        // If we are in Inference Mode a double click inside a
        // visual state of a node without pre-resolution finding
        // must introduce evidence in that node.
        // If the double click is inside a node but outside its
        // inner box (in its 'expanded external shape'), its
        // properties dialog should be open
        
        VisualNode visualNode = this.editorPanel.getVisualNetwork().whatNodeInPosition(this.cursorPosition, g);
        if (visualNode.isPreResolutionFinding()) {
            throw new UnrecoverableException(new PreResolutionNodeInInferenceException(visualNode));
        }
        VisualState visualState = this.editorPanel.getVisualNetwork().whatStateInPosition(this.cursorPosition, g);
        try {
            this.editorPanel.getEvidenceManager().toggleFinding(visualNode, visualState);
        } catch (IncompatibleEvidenceException | NotEvaluableNetworkException | NonProjectablePotentialException |
                 NotEnoughtMemoryException | CannotNormalizePotentialException | DoEditException ex) {
            throw new UnreacheableException(ex);
        }
        
        
    }
    
    /**
     * Invoked when a mouse button is pressed on a component and then dragged.
     *
     * @param e mouse event information.
     */
    @Override public void mouseDragged(MouseEvent e) {
        Graphics2D g = (Graphics2D) this.editorPanel.getGraphics();
        Point2D.Double point = new Point2D.Double(this.editorPanel.getZoomManager()
                                                                  .screenToPanel(e.getX()), this.editorPanel.getZoomManager()
                                                                                                            .screenToPanel(e.getY()));
        double diffX = point.getX() - this.cursorPosition.getX();
        double diffY = point.getY() - this.cursorPosition.getY();
        this.cursorPosition.setLocation(point);
        this.editorPanel.getEditionMode().mouseDragged(e, point, diffX, diffY, g);
    }
    
    /**
     * Invoked when a mouse button has been released on the component.
     *
     * @param e mouse event information.
     */
    @Override public void mouseReleased(MouseEvent e) {
        Graphics2D g = (Graphics2D) this.editorPanel.getGraphics();
        Point2D.Double position = new Point2D.Double(this.editorPanel.getZoomManager()
                                                                     .screenToPanel(e.getX()), this.editorPanel.getZoomManager()
                                                                                                               .screenToPanel(e.getY()));
        try {
            this.editorPanel.getEditionMode().mouseReleased(e, position, g);
        } catch (DoEditException ex) {
            throw new UnrecoverableException(ex);
        }
    }
    
    /**
     * Invoked when the mouse button enters the component.
     *
     * @param e mouse event information.
     */
    @Override public void mouseEntered(MouseEvent e) {
    }
    
    /**
     * Invoked when the mouse button exits the component.
     *
     * @param e mouse event information.
     */
    @Override public void mouseExited(MouseEvent e) {
    }
    
    /**
     * Invoked when the mouse cursor has been moved onto a component but no
     * buttons have been pushed.
     *
     * @param e mouse event information.
     */
    @Override public void mouseMoved(MouseEvent e) {
    }
    
    @Override
    public void keyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_UP -> this.editorPanel.getVisualNetwork().moveSelectedElements(0, -2);
            case KeyEvent.VK_RIGHT -> this.editorPanel.getVisualNetwork().moveSelectedElements(2, 0);
            case KeyEvent.VK_DOWN -> this.editorPanel.getVisualNetwork().moveSelectedElements(0, 2);
            case KeyEvent.VK_LEFT -> this.editorPanel.getVisualNetwork().moveSelectedElements(-2, 0);
        }
        this.editorPanel.repaint();
    }
    
    @Override
    public void keyReleased(KeyEvent keyEvent) {
    
    }
    
    
    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }
    
    /**
     * Position of the mouse cursor when it is pressed.
     */
    private final Point2D.Double cursorPosition = new Point2D.Double();
    
    /**
     * Shows contextual menu
     *
     * @param e MouseEvent
     * @param g Graphics2D
     */
    private void showContextualMenu(MouseEvent e, Graphics2D g) {
        VisualElement selectedElement = this.editorPanel.getVisualNetwork()
                                                        .getElementInPosition(this.cursorPosition, g);
        ContextualMenu contextualMenu;
        if (selectedElement != null) {
            contextualMenu = this.getContextualMenu(selectedElement, this.editorPanel);
            this.editorPanel.getVisualNetwork().selectElement(selectedElement);
        } else {
            boolean canBeExpanded = this.editorPanel.getNetworkPanel().getProbNet().thereAreTemporalNodes();
            contextualMenu = this.contextualMenuFactory.getNetworkContextualMenu(canBeExpanded);
        }
        contextualMenu.show(this.editorPanel, e.getX(), e.getY());
    }
    
    /**
     * Object that creates the contextual menus.
     */
    private ContextualMenuFactory contextualMenuFactory = null;
    
    
    public void setContextualMenuFactory(ContextualMenuFactory contextualMenuFactory) {
        this.contextualMenuFactory = contextualMenuFactory;
    }
    
    /**
     * Retrieves the contextual menu that corresponds to the selectedElement.
     *
     * @return the contextual menu corresponding the the parameter.
     */
    private @Nullable ContextualMenu getContextualMenu(VisualElement selectedElement, EditorPanel panel) {
        return Optional.ofNullable(this.contextualMenuFactory)
                       .map(menuFactory -> menuFactory.getContextualMenu(selectedElement, panel))
                       .orElse(null);
    }
}
