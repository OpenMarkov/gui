/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.window.edition;

import org.openmarkov.core.action.base.PNESupport;
import org.openmarkov.core.action.base.PNUndoableEditEvent;
import org.openmarkov.core.action.core.*;
import org.openmarkov.core.developmentStaticAnalysis.ToCheck;
import org.openmarkov.core.exception.*;

import org.openmarkov.core.inference.InferenceAlgorithm;
import org.openmarkov.core.inference.annotation.InferenceManager;
import org.openmarkov.core.inference.tasks.OptimalPolicies;
import org.openmarkov.core.inference.tasks.Propagation;
import org.openmarkov.core.inference.tasks.TaskUtilities;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.*;
import org.openmarkov.core.model.network.potential.*;
import org.openmarkov.gui.action.*;
import org.openmarkov.core.action.base.PNUndoableEditListener;
import org.openmarkov.core.action.base.linkEdits.InvertLinkAndUpdatePotentialsEdit;
import org.openmarkov.gui.dialog.PropagationOptionsDialog;
import org.openmarkov.gui.dialog.common.OkCancelHorizontalDialog;
import org.openmarkov.gui.dialog.inference.temporalevolution.TemporalEvolutionDialog;
import org.openmarkov.gui.dialog.link.LinkRestrictionEditDialog;
import org.openmarkov.gui.dialog.link.RevelationArcEditDialog;
import org.openmarkov.gui.dialog.network.NetworkPropertiesDialog;
import org.openmarkov.gui.dialog.node.*;
import org.openmarkov.gui.exception.*;
import org.openmarkov.gui.graphic.*;
import org.openmarkov.core.localize.StringDatabase;
import org.openmarkov.gui.menutoolbar.menu.ContextualMenu;
import org.openmarkov.gui.menutoolbar.menu.ContextualMenuFactory;
import org.openmarkov.gui.util.Utilities;
import org.openmarkov.gui.window.MainGUI;
import org.openmarkov.gui.window.MainPanelMenuAssistant;
import org.openmarkov.gui.window.edition.mode.EditionMode;
import org.openmarkov.gui.window.edition.mode.EditionModeManager;
import org.openmarkov.inference.algorithm.variableElimination.tasks.VEEvaluation;
import org.openmarkov.inference.algorithm.variableElimination.tasks.VEExpectedUtilityDecision;
import org.openmarkov.inference.algorithm.variableElimination.tasks.VEPropagation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class implements the behaviour of a panel where a network will be
 * edited.
 *
 * @author jmendoza
 * @author jlgozalo
 * @version 1.3 - asaez - Functionality added: - Explanation capabilities, -
 * Management of working modes (edition/inference), - Expansion and
 * contraction of nodes, - Introduction and elimination of evidence -
 * Management of multiple evidence cases.
 */
public class EditorPanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener, PNUndoableEditListener {
    /**
     * Static field for serializable class.
     */
    private static final long serialVersionUID = 2789011585460326400L;
    /**
     * Maximum width of the panel.
     */
    private static final double MAX_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 20;
    /**
     * Maximum height of the panel.
     */
    private static final double MAX_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 20;
    /**
     * Constant that indicates the value of the Expansion Threshold by default.
     */
    // This should be in a future a configuration option that should be read on
    // start
    private static final int DEFAULT_THRESHOLD_VALUE = 5;
    protected ProbNet probNet;
    /**
     * Object to convert coordinates of the screen to the panel and vice versa.
     */
    protected Zoom zoom;
    /**
     * Visual representation of the network
     */
    protected VisualNetwork visualNetwork = null;
    /**
     * Position of the mouse cursor when it is pressed.
     */
    protected Point2D.Double cursorPosition = new Point2D.Double();
    /**
     * String database
     */
    protected StringDatabase stringDatabase = StringDatabase.getUniqueInstance();
    /**
     * Object Dialog for potentials edition
     */
    PotentialEditDialog potentialsDialog = null;
    
    AddFindingDialog addFindingDialog = null;
    /****
     * Dialog for link restriction edition
     */
    LinkRestrictionEditDialog linkRestrictionDialog = null;
    /***
     * Dialog for revelation arc edition
     */
    RevelationArcEditDialog revelationArcDialog = null;
    
    /**
     * Maximum width of the panel.
     */
    private double currentWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 20;
    /**
     * Maximum height of the panel.
     */
    private double currentHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 20;
    /**
     * Current edition mode.
     */
    private EditionMode editionMode = null;
    /**
     * This variable indicates which is the expansion threshold of the network
     */
    private double currentExpansionThreshold = DEFAULT_THRESHOLD_VALUE;
    /**
     * Network panel associated to this editor panel
     */
    private NetworkPanel networkPanel;
    /**
     * Pre resolution evidence
     */
    private EvidenceCase preResolutionEvidence;
    /**
     * Array of Evidence cases treated for this editor panel
     */
    private List<EvidenceCase> postResolutionEvidence;
    /**
     * Each position of this array indicates if the corresponding evidence case
     * is currently compiled (if true) or not (if false)
     */
    private List<Boolean> evidenceCasesCompilationState;
    /**
     * Minimum value of the range of each utility node.
     */
    private HashMap<Variable, Double> minUtilityRange;
    /**
     * Maximum value of the range of each utility node.
     */
    private HashMap<Variable, Double> maxUtilityRange;
    /**
     * This variable indicates which is the evidence case that is currently
     * being treated
     */
    private int currentCase;
    /**
     * Inference manager
     */
    private InferenceManager inferenceManager = null;
    /**
     * Inference algorithm used to evaluate this network
     */
    private InferenceAlgorithm inferenceAlgorithm = null;
    /**
     * This variable indicates if the propagation mode is automatic or manual.
     */
    private boolean automaticPropagation;
    /**
     * This variable indicates if propagation should be done right now (if being
     * in Inference Mode).
     */
    private boolean propagationActive;
    /**
     * This variable indicates if it has been a change in the properties or in
     * the potential values in some node.
     */
    private boolean networkChanged = true;
    /**
     * Object that creates the contextual menus.
     */
    private ContextualMenuFactory contextualMenuFactory = null;
    /**
     * Object that assists this panel in the operations with the clipboard.
     */
    private final EditorPanelClipboardAssistant clipboardAssistant;
    private EditionModeManager editionModeManager;
    private boolean approximateInferenceWarningGiven = false;
    private boolean canBeExpanded = false;
    
    /**
     * Constructor that creates the instance.
     *
     * @param networkPanel network that will be edited.
     */
    public EditorPanel(NetworkPanel networkPanel, VisualNetwork visualNetwork) {
        zoom = new Zoom();
        // super();
        this.networkPanel = networkPanel;
        this.probNet = networkPanel.getProbNet();
        this.probNet.getPNESupport().addListener(this);
        this.visualNetwork = visualNetwork;
        automaticPropagation = true;
        propagationActive = true;
        preResolutionEvidence = new EvidenceCase();
        postResolutionEvidence = new ArrayList<EvidenceCase>(1);
        currentCase = 0;
        EvidenceCase evidenceCase = new EvidenceCase();
        postResolutionEvidence.add(currentCase, evidenceCase);
        evidenceCasesCompilationState = new ArrayList<Boolean>(1);
        evidenceCasesCompilationState.add(currentCase, false);
        minUtilityRange = new HashMap<Variable, Double>();
        maxUtilityRange = new HashMap<Variable, Double>();
        initialize();
        clipboardAssistant = new EditorPanelClipboardAssistant();
        inferenceManager = new InferenceManager();
        editionModeManager = new EditionModeManager(this, probNet);
        editionMode = editionModeManager.getDefaultEditionMode();
    }
    
    /**
     * This method initializes this instance.
     */
    private void initialize() {
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        //this.setBackground(Color.white);
        //adjustPanelDimension ();
        setZoomToFitNetwork();
    }
    
    public double getCurrentWidth() {
        return this.currentWidth;
    }
    
    public double getCurrentHeight() {
        return this.currentHeight;
    }
    
    /**
     * Returns the presentation mode of the text of the nodes.
     *
     * @return true if the title of the nodes is the name or false if it is the
     * name.
     */
    public boolean getByTitle() {
        return visualNetwork.getByTitle();
    }
    
    /**
     * Changes the presentation mode of the text of the nodes.
     *
     * @param value new value of the presentation mode of the text of the nodes.
     */
    public void setByTitle(boolean value) {
        visualNetwork.setByTitle(value);
        adjustPanelDimension();
        repaint();
    }
    
    /**
     * Overwrite 'paint' method to avoid to call it explicitly.
     *
     * @param g the graphics context in which to paint.
     */
    @Override public void paint(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        super.paint(g);
        g2D.scale(zoom.getZoom(), zoom.getZoom());
        visualNetwork.paint(g2D);
    }
    
    /**
     * Returns the edition mode.
     *
     * @return edition mode.
     */
    public EditionMode getEditionMode() {
        return editionMode;
    }
    
    /**
     * Changes the state of the edition and carries out the necessary actions in
     * each case.
     *
     * @param newEditionModeName new edition mode state.
     */
    public void setEditionMode(String newEditionModeName) {
        EditionMode newEditionMode = editionModeManager.getEditionMode(newEditionModeName);
        if (!editionMode.equals(newEditionMode)) {
            setCursor(editionModeManager.getCursor(newEditionModeName));
            visualNetwork.setSelectedAllObjects(false);
            editionMode = newEditionMode;
            repaint();
        }
    }
    
    /**
     * Selects all nodes and links.
     */
    public void selectAllObjects() {
        visualNetwork.setSelectedAllObjects(true);
        repaint();
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
    private boolean lastLeftClickProducedANode;
    
    /**
     * Invoked when a mouse button has been pressed on the component.
     *
     * @param e mouse event information.
     */
    @Override public void mousePressed(MouseEvent e) {
        // requestFocusInWindow(); Activate if nodes can't be moved by arrows.
        
        if (e.getClickCount() <= (lastClickCount + 1)) {
            lastLeftClickProducedANode = false;
            lastClickCount = Math.max(e.getClickCount() - 1, 0);
        } else {
            lastClickCount += 1;
        }
        // requestFocusInWindow(); Activate if nodes can't be moved by arrows.
        Graphics2D g = (Graphics2D) getGraphics();
        cursorPosition.setLocation(zoom.screenToPanel(e.getX()), zoom.screenToPanel(e.getY()));
        // Specific functionality depending on the edition mode;
        try {
            var oldNodesCount = probNet.getNodes().size();
            editionMode.mousePressed(e, cursorPosition, g);
            if (e.getClickCount() == 1) {
                int newNodesCount = probNet.getNodes().size();
                this.lastLeftClickProducedANode = oldNodesCount < newNodesCount;
            }
        } catch (Exception ex) {
            throw new UnrecoverableException(ex);
        }
        // Generic functionality regardless of the edition mode
        VisualNode node;
        VisualLink link;
        if (SwingUtilities.isRightMouseButton(e)) {
            showContextualMenu(e, g);
            repaint();
            return;
        }
        if (!SwingUtilities.isLeftMouseButton(e)) {
            repaint();
            return;
        }
        if (e.isAltDown() && e.getClickCount() != 2) {
            node = visualNetwork.whatNodeInPosition(cursorPosition, g);
            if (node != null) {
                if (!node.isSelected()) {
                    visualNetwork.setSelectedAllObjects(false);
                    visualNetwork.setSelectedNode(node, true);
                }
                try {
                    showPotentialDialog(networkPanel.getWorkingMode() != NetworkPanel.WorkingMode.EDITION);
                } catch (IncompatibleEvidenceException.EvidenceIsIncompatibleWithOther |
                         ThereIsNoPotentialsInNodeException ex) {
                    throw new UnrecoverableException(ex);
                } finally {
                    repaint();
                    return;
                }
            }
        }
        if (!(e.getClickCount() == 2 && Utilities.noMouseModifiers(e))) {
            repaint();
            return;
        }
        if (networkPanel.getWorkingMode() == NetworkPanel.WorkingMode.EDITION) {
            // If we are in Edition Mode a double click must open
            // the corresponding properties dialog (for node, link
            // or network)
            node = visualNetwork.whatNodeInPosition(cursorPosition, g);
            if (node != null) {
                try {
                    boolean userAcceptedChanges = changeNodeProperties(node, lastLeftClickProducedANode);
                    if (!userAcceptedChanges && lastLeftClickProducedANode) {
                        while (true) {
                            if (probNet.getPNESupport()
                                       .undoAndDelete()
                                       .stream()
                                       .anyMatch(edit -> edit instanceof AddNodeEdit)) {
                                break;
                            }
                        }
                    }
                } catch (NotEvaluableNetworkException | NonProjectablePotentialException | NotEnoughtMemoryException |
                         IncompatibleEvidenceException | CannotNormalizePotentialException |
                         ConstraintViolatedException ex) {
                    repaint();
                    throw new UnrecoverableException(ex);
                }
            } else {
                link = visualNetwork.whatLinkInPosition(cursorPosition, g);
                if (link != null) {
                    changeLinkProperties(link);
                } else {
                    changeNetworkProperties();
                }
            }
            repaint();
            return;
        }
        
        if (visualNetwork.whatStateInPosition(cursorPosition, g) == null) {
            if ((visualNetwork.whatNodeInPosition(cursorPosition, g) != null) && (
                    visualNetwork.whatInnerBoxInPosition(cursorPosition, g) == null
            )) {
                try {
                    changeNodeProperties();
                } catch (NotEvaluableNetworkException | NonProjectablePotentialException | NotEnoughtMemoryException |
                         IncompatibleEvidenceException | CannotNormalizePotentialException |
                         ConstraintViolatedException ex) {
                    throw new UnrecoverableException(ex);
                } finally {
                    repaint();
                }
            }
            repaint();
            return;
        }
        
        // If we are in Inference Mode a double click inside a
        // visual state of a node without pre-resolution finding
        // must introduce evidence in that node.
        // If the double click is inside a node but outside its
        // inner box (in its 'expanded external shape'), its
        // properties dialog should be open
        
        VisualNode visualNode = visualNetwork.whatNodeInPosition(cursorPosition, g);
        if (visualNode.isPreResolutionFinding()) {
            throw new UnrecoverableException(new PreResolutionNodeInInferenceException(visualNode));
        }
        VisualState visualState = visualNetwork.whatStateInPosition(cursorPosition, g);
        try {
            toggleFinding(visualNode, visualState);
        } catch (IncompatibleEvidenceException | NotEvaluableNetworkException | NonProjectablePotentialException |
                 NotEnoughtMemoryException | CannotNormalizePotentialException | DoEditException ex) {
            throw new UnreacheableException(ex);
        }
        
        
    }
    
    /**
     * Shows contextual menu
     *
     * @param e MouseEvent
     * @param g Graphics2D
     */
    private void showContextualMenu(MouseEvent e, Graphics2D g) {
        VisualElement selectedElement = visualNetwork.getElementInPosition(cursorPosition, g);
        ContextualMenu contextualMenu;
        if (selectedElement != null) {
            contextualMenu = getContextualMenu(selectedElement, this);
            visualNetwork.selectElement(selectedElement);
        } else {
            canBeExpanded = probNet.thereAreTemporalNodes();
            contextualMenu = contextualMenuFactory.getNetworkContextualMenu(canBeExpanded);
        }
        contextualMenu.show(this, e.getX(), e.getY());
    }
    
    /**
     * Invoked when a mouse button is pressed on a component and then dragged.
     *
     * @param e mouse event information.
     */
    @Override public void mouseDragged(MouseEvent e) {
        Graphics2D g = (Graphics2D) getGraphics();
        Point2D.Double point = new Point2D.Double(zoom.screenToPanel(e.getX()), zoom.screenToPanel(e.getY()));
        double diffX = point.getX() - cursorPosition.getX();
        double diffY = point.getY() - cursorPosition.getY();
        cursorPosition.setLocation(point);
        editionMode.mouseDragged(e, point, diffX, diffY, g);
    }
    
    /**
     * Invoked when a mouse button has been released on the component.
     *
     * @param e mouse event information.
     */
    @Override public void mouseReleased(MouseEvent e) {
        Graphics2D g = (Graphics2D) getGraphics();
        Point2D.Double position = new Point2D.Double(zoom.screenToPanel(e.getX()), zoom.screenToPanel(e.getY()));
        try {
            editionMode.mouseReleased(e, position, g);
        } catch (Exception ex) {
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
    
    /**
     * Return the height of the panel after applying the zoom.
     *
     * @return height of the panel after applying the zoom.
     */
    double getNewHeight() {
        return zoom.panelToScreen(currentHeight);
    }
    
    /**
     * Return the width of the panel after applying the zoom.
     *
     * @return width of the panel after applying the zoom.
     */
    double getNewWidth() {
        return zoom.panelToScreen(currentWidth);
    }
    
    /**
     * Returns the value of the zoom.
     *
     * @return actual value of zoom.
     */
    public double getZoom() {
        return zoom.getZoom();
    }
    
    /**
     * Changes the value of the zoom.
     *
     * @param value new zoom.
     */
    public void setZoom(double value) {
        if (Double.compare(zoom.getZoom(), value) != 0) { // jlgozalo. 24/08 fix condition to !=
            zoom.setZoom(value);
            Dimension newDimension = new Dimension((int) Math.round(getNewWidth()), (int) Math.round(getNewHeight()));
            setPreferredSize(newDimension);
            setSize(newDimension);
            adjustPanelDimension();
            repaint();
        }
    }
    
    /**
     * This method performs a undo or redo operation.
     *
     * @param undoOperation if true, an undo must be performed; if false, a redo
     *                      will be performed.
     *
     */
    private void undoRedo(boolean undoOperation) {
        visualNetwork.setSelectedAllObjects(false);
        PNESupport pneSupport = probNet.getPNESupport();
        if (undoOperation) {
            pneSupport.undo();
        } else {
            pneSupport.redo();
        }
        adjustPanelDimension();
        repaint();
    }
    
    /**
     * This method performs a undo operation.
     *
     */
    public void undo() {
        undoRedo(true);
    }
    
    /**
     * This method performs a redo operation.
     */
    public void redo() {
        undoRedo(false);
    }
    
    /**
     * Sets a new selection listener.
     *
     * @param listener listener to be set.
     */
    public void addSelectionListener(SelectionListener listener) {
        visualNetwork.addSelectionListener(listener);
    }
    
    /**
     * Sets a new contextual menu factory.
     *
     * @param newContextualMenuFactory contextual menu factory to be set.
     */
    public void setContextualMenuFactory(ContextualMenuFactory newContextualMenuFactory) {
        contextualMenuFactory = newContextualMenuFactory;
    }
    
    /**
     * Retrieves the contextual menu that corresponds to the selectedElement.
     *
     * @return the contextual menu corresponding the the parameter.
     */
    private ContextualMenu getContextualMenu(VisualElement selectedElement, EditorPanel panel) {
        return (contextualMenuFactory != null) ? contextualMenuFactory.getContextualMenu(selectedElement, panel) : null;
    }
    
    /**
     * Returns the number of selected nodes.
     *
     * @return number of selected nodes.
     */
    public int getSelectedNodesNumber() {
        return visualNetwork.getSelectedNodesNumber();
    }
    
    /**
     * Returns the number of selected links.
     *
     * @return number of selected links.
     */
    public int getSelectedLinksNumber() {
        return visualNetwork.getSelectedLinksNumber();
    }
    
    /**
     * Returns a list containing the selected nodes.
     *
     * @return a list containing the selected nodes.
     */
    public List<VisualNode> getSelectedNodes() {
        return visualNetwork.getSelectedNodes();
    }
    
    /**
     * Returns a list containing the selected links.
     *
     * @return a list containing the selected links.
     */
    public List<VisualLink> getSelectedLinks() {
        return visualNetwork.getSelectedLinks();
    }
    
    /**
     * Selects or deselects all nodes of the network.
     *
     * @param selected new selection state.
     */
    public void setSelectedAllNodes(boolean selected) {
        visualNetwork.setSelectedAllNodes(selected);
    }
    
    /**
     * Selects or deselects all objects of the network.
     *
     * @param selected new selection state.
     */
    public void setSelectedAllObjects(boolean selected) {
        visualNetwork.setSelectedAllObjects(selected);
    }
    
    /**
     * This method absorbs a node into the rest of the net arc-reversal style. This means updating the only utility
     * child it might have and removing it next.
     */
    public void absorbNode() throws DoEditException {
        Node node = getSelectedNode();
        AbsorbNodeEdit absorbNode = new AbsorbNodeEdit(probNet, node.getVariable());
        absorbNode.executeEdit();
        repaint();
    }
    
    private Node getSelectedNode() {
        List<VisualNode> selectedNodes = visualNetwork.getSelectedNodes();
        if (selectedNodes.size() != 1) { // This never happens
            throw new UnreacheableException(new NoSelectedNodeException(visualNetwork));
        }
        return selectedNodes.get(0).getNode();
    }
    
    /**
     * TODO: Fill as desired
     */
    public void absorbParents() throws DoEditException {
        Node node = getSelectedNode();
        AbsorbParentsEdit absorbParents = new AbsorbParentsEdit(probNet, node);
        absorbParents.executeEdit();
        repaint();
    }
    
    /**
     * This method shows a dialog box with the additionalProperties of a node.
     * If some property has changed, insert a new undo point into the network
     * undo manager.
     *
     * @param selectedNode
     *
     * @param newNode
     * @return
     */
    public boolean changeNodeProperties(VisualNode selectedNode, boolean newNode) throws NotEvaluableNetworkException, NonProjectablePotentialException, NotEnoughtMemoryException, IncompatibleEvidenceException, CannotNormalizePotentialException, ConstraintViolatedException {
        boolean userAcceptedChanges = requestNodePropertiesToUser2(Utilities.getOwner(this), selectedNode.getNode(), newNode);
        if (userAcceptedChanges) {
            adjustPanelDimension();
            selectedNode.update(postResolutionEvidence.size());
            repaint();
            networkChanged = true;
            removeNodeEvidenceInAllCases(selectedNode.getNode());
        } else {
            probNet.getPNESupport().undoAndDelete();
        }
        return userAcceptedChanges;
    }
    
    public void changeNodeProperties() throws NotEvaluableNetworkException, NonProjectablePotentialException, NotEnoughtMemoryException, IncompatibleEvidenceException, CannotNormalizePotentialException, ConstraintViolatedException {
        List<VisualNode> selectedNodes = visualNetwork.getSelectedNodes();
        if (selectedNodes.size() == 1) {
            changeNodeProperties(selectedNodes.getFirst(), false);
        }
    }
    
    /**
     *
     */
    public void showPotentialDialog(boolean readOnly) throws IncompatibleEvidenceException, ThereIsNoPotentialsInNodeException, NotEvaluableNetworkException, NonProjectablePotentialException, NotEnoughtMemoryException, CannotNormalizePotentialException, ConstraintViolatedException {
        List<VisualNode> selectedNodes = visualNetwork.getSelectedNodes();
        Node node = selectedNodes.get(0).getNode();
        
        /*
         * Potential oldPotential = node.getPotentials().get(0);
         * PotentialEditDialog dialog = new PotentialEditDialog(owner,
         * oldPotential, newElement); Potential newPotential =
         * dialog.getNewPotential(); if ( newPotential != null ) { new edit =
         * new ChangeNodePotentialEdit(newPotential);//sets the potential in the
         * node pNESupport.doedit //probnet PNESuport, inside panels
         * PNESupports will be owned by the edit dialog adjustPanelDimension();
         * repaint(); networkChanged = true; }
         */
        if (requestPotentialValues(Utilities.getOwner(this), node, false, readOnly)) {
            // if the user has selected the ok button when closing the dialog
            adjustPanelDimension();
            repaint();
            networkChanged = true;
            removeNodeEvidenceInAllCases(node);
        } else {
            cancelAction();
        }
    }
    
    /**
     * This method requests to the user the additionalProperties of a node.
     *
     * @param owner   owner window that shows the dialog box.
     * @param node    object that contains the additionalProperties of the node
     *                and where changes will be saved.
     * @param newNode specifies if the node whose additionalProperties are going
     *                to be edited is new.
     *
     * @return true, if the user save the changes on node; otherwise, false.
     */
    private static boolean requestNodePropertiesToUser2(Window owner, Node node, boolean newNode) {
        NodePropertiesDialog nodePropertiesDialog = new CommonNodePropertiesDialog(owner, node, newNode);
        if (owner instanceof MainGUI gui) {
            gui.freeze();
        }
        boolean result = nodePropertiesDialog.requestProperties() == OkCancelHorizontalDialog.OK_BUTTON;
        if (owner instanceof MainGUI gui) {
            gui.unfreeze();
        }
        return result;
    }
    
    private boolean requestPotentialValues(Window owner, Node node, boolean newNode, boolean readOnly) throws IncompatibleEvidenceException.EvidenceIsIncompatibleWithOther, ThereIsNoPotentialsInNodeException, NotEnoughtMemoryException {
        potentialsDialog = new PotentialEditDialog(owner, node, readOnly);
        visualNetwork.cancelLinkCreation(this);
        return (
                potentialsDialog.requestValues()// to know if the user has
                        // selected the ok button when
                        // closing the dialog
                        == OkCancelHorizontalDialog.OK_BUTTON
        );
    }
    
    /**
     * This method requests to the user the link restriction properties of a
     * link.
     *
     * @param owner owner window that shows the dialog box.
     * @param link  object that contains the link restriction properties of the
     *              link and where changes will be saved.
     *
     * @return true, if the user save the changes on node; otherwise, false.
     */
    private boolean requestLinkRestrictionValues(Window owner, Link<Node> link) {
        linkRestrictionDialog = new LinkRestrictionEditDialog(owner, link);
        return (linkRestrictionDialog.requestValues() == OkCancelHorizontalDialog.OK_BUTTON);
    }
    
    // private boolean requestCostEffectiveness(Window owner,
    // String suffixTypeAnalysis, boolean isProbabilistic) {
    // costEffectivenessDialog = new CostEffectivenessDialog(owner);
    // costEffectivenessDialog.showSimulationsNumberElements(isProbabilistic);
    // return (costEffectivenessDialog.requestData(probNet.getName(),
    // suffixTypeAnalysis) == CostEffectivenessDialog.OK_BUTTON);
    // }
    
    /**
     * This method requests to the user the revelation arc properties of a link.
     *
     * @param owner owner window that shows the dialog box.
     * @param link  object that contains the revelation arc properties of the
     *              link and where changes will be saved.
     *
     * @return true, if the user save the changes on node; otherwise, false.
     */
    private boolean requestRevelationArcValues(Window owner, Link<Node> link) {
        revelationArcDialog = new RevelationArcEditDialog(owner, link);
        return (revelationArcDialog.requestValues() == OkCancelHorizontalDialog.OK_BUTTON);
    }
    
    /**
     * This method shows a dialog box with the additionalProperties of a link.
     * If some property has changed, insert a new undo point into the network
     * undo manager.
     *
     * @param link
     */
    public void changeLinkProperties(VisualLink link) {
        /*
         * This method must be implemented to activate the possibility of
         * editing the additionalProperties of a link in future versions.
         */
    }
    
    /**
     * This method shows a dialog box with the additionalProperties of the
     * network. If some property has changed, insert a new undo point into the
     * network undo manager.
     */
    public void changeNetworkProperties() {
        // TODO be careful with local pNESupport and extern pNESupport
        Window owner = Utilities.getOwner(this);
        NetworkPropertiesDialog dialogProperties = new NetworkPropertiesDialog(owner, probNet);
        if (!(dialogProperties.showProperties() == OkCancelHorizontalDialog.OK_BUTTON)) {
            cancelAction();
        }
    }
    
    /**
     * This method copies the selected nodes to the clipboard.
     *
     * @param cut if true, the nodes copied to the clipboard are also removed.
     */
    public void exportToClipboard(boolean cut) {
        List<Node> selectedNodes = new ArrayList<Node>();
        for (VisualNode visualNode : visualNetwork.getSelectedNodes()) {
            selectedNodes.add(visualNode.getNode());
        }
        List<Link<Node>> selectedLinks = new ArrayList<>();
        for (VisualLink visualLink : visualNetwork.getSelectedLinks()) {
            selectedLinks.add(visualLink.getLink());
        }
        SelectedContent copiedContent = new SelectedContent(selectedNodes, selectedLinks);
        if (!copiedContent.isEmpty()) {
            clipboardAssistant.copyToClipboard(copiedContent);
            if (cut) {
                removeSelectedObjects();
            }
        }
    }
    
    /**
     * This method imports the content from the clipboard and creates it in the
     * network.
     */
    public void pasteFromClipboard() throws DoEditException {
        if (!clipboardAssistant.isThereDataStored()) {
            return;
        }
        visualNetwork.setSelectedAllObjects(false);
        SelectedContent clipboardContent = clipboardAssistant.paste();
        PasteEdit pasteEdit = new PasteEdit(probNet, clipboardContent);
        pasteEdit.executeEdit();
        // Set the nodes and links we just pasted as selected
        SelectedContent pastedContent = pasteEdit.getPastedContent();
        for (Node node : pastedContent.nodes()) {
            visualNetwork.setSelectedNode(node.getName(), true);
        }
        for (Link<Node> link : pastedContent.links()) {
            visualNetwork.setSelectedLink(link, true);
        }
        adjustPanelDimension();
        repaint();
    }
    
    /**
     * This method says if there is data stored in the clipboard.
     *
     * @return true if there is data stored in the clipboard; otherwise, false.
     */
    public boolean isThereDataStored() {
        return clipboardAssistant != null && clipboardAssistant.isThereDataStored();
    }
    
    /**
     * This method imposes a policy in a decision node.
     */
    public void imposePolicyInNode()
            throws IncompatibleEvidenceException.EvidenceIsIncompatibleWithOther, ThereIsNoPotentialsInNodeException, NotEnoughtMemoryException {
        VisualDecisionNode visualNode;
        List<VisualNode> selectedNode = visualNetwork.getSelectedNodes();
        if (selectedNode.size() == 1) {
            visualNode = (VisualDecisionNode) selectedNode.get(0);
            if (!requestImposePolicyValues(Utilities.getOwner(this), visualNode)) {
                // if user cancels policy imposition then no potential is
                // restored to the node
                cancelAction();
            }
        }
        setSelectedAllNodes(false);
        repaint();
    }
    
    /**
     * This method edits an imposed policy of a decision node.
     */
    public void editNodePolicy() throws IncompatibleEvidenceException.EvidenceIsIncompatibleWithOther, ThereIsNoPotentialsInNodeException, NotEnoughtMemoryException {
        VisualDecisionNode visualNode;
        List<VisualNode> selectedNode = visualNetwork.getSelectedNodes();
        if (selectedNode.size() == 1) {
            visualNode = (VisualDecisionNode) selectedNode.get(0);
            if (visualNode.getNode().getNodeType() == NodeType.DECISION) {
                //Node node = visualNode.getNode();
                // TODO manage other kind of policy types from the interface
                // node.setPolicyType(PolicyType.OPTIMAL);
                // Potential imposedPolicy = node.getPotentials ().get (0);
                if (!requestImposePolicyValues(Utilities.getOwner(this), visualNode)) {
                    cancelAction();
                    
                }
            }
        }
        setSelectedAllNodes(false);
        repaint();
    }
    
    /**
     * This method removes an imposed policy from a decision node.
     */
    public void removePolicyFromNode() throws DoEditException {
        VisualNode visualNode;
        List<VisualNode> selectedNode = visualNetwork.getSelectedNodes();
        if (selectedNode.size() == 1) {
            visualNode = selectedNode.get(0);
            if (visualNode.getNode().getNodeType() == NodeType.DECISION) {
                RemovePolicyEdit removePolicyEdit = new RemovePolicyEdit(visualNode.getNode());
                try {
                    removePolicyEdit.executeEdit();
                } catch (ConstraintViolatedException e) {
                    throw new UnreacheableException(e);
                }
            }
        }
        //setNetworkChangedWithOutEdit(true);
        setSelectedAllNodes(false);
        repaint();
    }
    
    private static boolean requestImposePolicyValues(Window owner, VisualDecisionNode visualNode) throws IncompatibleEvidenceException.EvidenceIsIncompatibleWithOther, ThereIsNoPotentialsInNodeException, NotEnoughtMemoryException {
        ImposePolicyDialog imposePolicyDialog = new ImposePolicyDialog(owner, visualNode);
        imposePolicyDialog.setTitle("ImposePolicydialog.Title");
        return (imposePolicyDialog.requestValues() == OkCancelHorizontalDialog.OK_BUTTON);
    }
    
    
    /**
     * This method shows the expected utility of a decision node.
     */
    public void showExpectedUtilityOfNode() throws IncompatibleEvidenceException.EvidenceIsIncompatibleWithOther, ThereIsNoPotentialsInNodeException, NonProjectablePotentialException, NotEvaluableNetworkException.NotApplicableNetwork, NotEvaluableNetworkException.UnsatisfiedContraints, NotEnoughtMemoryException, ConstraintViolatedException {
        VisualNode visualNode;
        List<VisualNode> selectedNode = visualNetwork.getSelectedNodes();
        if (selectedNode.size() == 1) {
            visualNode = selectedNode.get(0);
            Node node = visualNode.getNode();
            VEExpectedUtilityDecision veExpectedUtilityDecision
                    = new VEExpectedUtilityDecision(probNet, node.getVariable());
            Potential expectedUtility = veExpectedUtilityDecision.getExpectedUtility();
            Node dummyNode = new Node(new ProbNet(), node.getVariable(), node.getNodeType());
            dummyNode.setPotential(expectedUtility);
            PotentialEditDialog expectedUtilityDialog = new PotentialEditDialog(Utilities.getOwner(this), dummyNode, true);
            expectedUtilityDialog.setTitle("ExpectedUtilityDialog.Title");
            expectedUtilityDialog.requestValues();
        }
        networkChanged = false;
        setSelectedAllNodes(false);
        repaint();
        setSelectedAllNodes(false);
        repaint();
    }
    
    /**
     * This method shows the optimal policy for a decision node.
     */
    public void showOptimalPolicyOfNode() throws IncompatibleEvidenceException.EvidenceIsIncompatibleWithOther, ThereIsNoPotentialsInNodeException, NonProjectablePotentialException, NotEvaluableNetworkException.NotApplicableNetwork, NotEvaluableNetworkException.UnsatisfiedContraints, NotEnoughtMemoryException, ConstraintViolatedException {
        VisualNetwork n = getVisualNetwork();
        VisualNode visualNode;
        List<VisualNode> selectedNodes = visualNetwork.getSelectedNodes();
        if (selectedNodes.size() == 1) {
            visualNode = selectedNodes.get(0);
            ProbNet dummyProbNet = new ProbNet();
            Potential optimalPolicy = null;
            OptimalPolicies veOptimalPolicy = new VEEvaluation(probNet);
            optimalPolicy = veOptimalPolicy.getOptimalPolicy(visualNode.getNode().getVariable());
            dummyProbNet.addPotential(optimalPolicy);
            Variable conditionedVariable = optimalPolicy.getVariable(0);
            Node dummy = dummyProbNet.getNode(conditionedVariable);
            dummy.setNodeType(NodeType.DECISION);
            dummy.setPolicyType(PolicyType.OPTIMAL);
            for (Variable variable : optimalPolicy.getVariables()) {
                if (variable.equals(conditionedVariable)) {
                    continue;
                }
                dummyProbNet.addLink(variable, conditionedVariable, true);
            }
            PotentialEditDialog optimalPolicyDialog =
                    new PotentialEditDialog(Utilities.getOwner(this), dummy, true);
            optimalPolicyDialog.setTitle("OptimalPolicyDialog.Title");
            optimalPolicyDialog.requestValues();
        }
        networkChanged = false;
        setSelectedAllNodes(false);
        repaint();
        setSelectedAllNodes(false);
        repaint();
    }
    
    /**
     * This method expands a node.
     */
    public void expandNode() {
        VisualNode visualNode;
        List<VisualNode> selectedNodes = visualNetwork.getSelectedNodes();
        if (!selectedNodes.isEmpty()) {
            for (int i = 0; i < selectedNodes.size(); i++) {
                visualNode = selectedNodes.get(i);
                if (!(visualNode.isExpanded())) {
                    visualNode.setExpanded(true);
                    visualNetwork.setSelectedNode(visualNode, false);
                }
                repaint();
            }
        }
    }
    
    /**
     * This method contracts a node.
     */
    public void contractNode() {
        VisualNode visualNode;
        List<VisualNode> selectedNodes = visualNetwork.getSelectedNodes();
        if (!selectedNodes.isEmpty()) {
            for (int i = 0; i < selectedNodes.size(); i++) {
                visualNode = selectedNodes.get(i);
                if (visualNode.isExpanded()) {
                    visualNode.setExpanded(false);
                    visualNetwork.setSelectedNode(visualNode, false);
                }
                repaint();
            }
        }
    }
    
    /**
     * This method adds a finding in a node.
     */
    public void addFinding() {
        List<VisualNode> selectedNodes = visualNetwork.getSelectedNodes();
        VisualNode node = selectedNodes.get(0);
        EvidenceCase currentEvidence = (networkPanel.getWorkingMode() == NetworkPanel.WorkingMode.INFERENCE) ?
                getCurrentEvidenceCase() :
                preResolutionEvidence;
        Finding finding = currentEvidence.getFinding(node.getNode().getVariable());
        
        if (!requestAddFindingValues(Utilities.getOwner(this), node, finding)) {
            
            cancelAction();
        }
        repaint();
        setSelectedAllNodes(false);
        networkPanel.getMainPanel().getInferenceToolBar().setCurrentEvidenceCaseName(currentCase);
        networkPanel.getMainPanel().getMainPanelMenuAssistant().updateOptionsFindingsDependent(networkPanel);
    }
    
    private boolean requestAddFindingValues(Window owner, VisualNode node, Finding finding) {
        addFindingDialog = new AddFindingDialog(owner, node, finding, this);
        return (addFindingDialog.requestValues() == OkCancelHorizontalDialog.OK_BUTTON);
    }
    
    /**
     * This method removes findings from selected nodes.
     */
    public void removeFinding() throws PreResolutionNodeInInferenceException, DoEditException {
        setPropagationActive(isAutomaticPropagation());
        VisualNode visualNode;
        List<VisualNode> selectedNodes = visualNetwork.getSelectedNodes();
        for (int i = 0; i < selectedNodes.size(); i++) {
            visualNode = selectedNodes.get(i);
            Variable variable = visualNode.getNode().getVariable();
            switch (networkPanel.getWorkingMode()) {
                case EDITION -> {
                    if (visualNode.isPreResolutionFinding() && preResolutionEvidence.getFinding(variable) != null) {
                        RemoveFindingEdit removeFindingEdit = new RemoveFindingEdit(visualNode, preResolutionEvidence, variable);
                        try {
                            removeFindingEdit.executeEdit();
                        } catch (ConstraintViolatedException e) {
                            throw new UnreacheableException(e);
                        }
                    }
                }
                case INFERENCE -> {
                    if (visualNode.isPreResolutionFinding()) {
                        throw new PreResolutionNodeInInferenceException(visualNode);
                    }
                    if (visualNode.isPostResolutionFinding()
                            && postResolutionEvidence.get(currentCase).getFinding(variable) != null) {
                        postResolutionEvidence.get(currentCase).removeFinding(variable);
                        visualNode.setPostResolutionFinding(false);
                    }
                }
            }
        }
        try {
            if ((propagationActive) && (networkPanel.getWorkingMode() == NetworkPanel.WorkingMode.INFERENCE)) {
            /*
            23/10/2014
            Solving issue 226
            https://bitbucket.org/cisiad/org.openmarkov.issues/issue/226/remove-finding-in-inference-mode-displays
            The previously code was setting the propagation active to false when the propagation was actually success,
            and thus the propagation was not being performed
            */
                doPropagation(postResolutionEvidence.get(currentCase), currentCase);
            }
        } catch (NotEvaluableNetworkException | NonProjectablePotentialException | NotEnoughtMemoryException |
                 CannotNormalizePotentialException | IncompatibleEvidenceException e) {
            setPropagationActive(false);
            throw new UnreacheableException(e);
        } finally {
            networkPanel.getMainPanel().getInferenceToolBar().setCurrentEvidenceCaseName(currentCase);
            networkPanel.getMainPanel().getMainPanelMenuAssistant().updateOptionsFindingsDependent(networkPanel);
            setSelectedAllNodes(false);
            repaint();
        }
    }
    
    /**
     * This method returns the current Evidence Case.
     *
     * @return the current Evidence Case.
     */
    public EvidenceCase getCurrentEvidenceCase() {
        return postResolutionEvidence.get(currentCase);
    }
    
    /**
     * This method returns the Evidence Case.
     *
     * @param caseNumber the number of the case to be returned.
     *
     * @return the selected Evidence Case.
     */
    public EvidenceCase getEvidenceCase(int caseNumber) {
        return postResolutionEvidence.get(caseNumber);
    }
    
    /**
     * This method returns list of evidence cases
     *
     * @return the list of Evidence Cases.
     */
    public List<EvidenceCase> getEvidence() {
        List<EvidenceCase> evidence = new ArrayList<EvidenceCase>();
        for (EvidenceCase postResolutionEvidenceCase : postResolutionEvidence) {
            if (!postResolutionEvidenceCase.isEmpty()) {
                evidence.add(postResolutionEvidenceCase);
            }
        }
        if (!evidence.isEmpty() || !preResolutionEvidence.isEmpty()) {
            evidence.add(0, preResolutionEvidence);
        }
        return evidence;
    }
    
    /**
     * This method returns the number of the Evidence Case that is currently
     * selected
     *
     * @return the number of the current Evidence Case.
     */
    public int getCurrentCase() {
        return currentCase;
    }
    
    /**
     * This method sets which is the current evidence case.
     *
     * @param currentCase new value for the current evidence case.
     */
    public void setCurrentCase(int currentCase) {
        this.currentCase = currentCase;
    }
    
    public EvidenceCase getPreResolutionEvidence() {
        return preResolutionEvidence;
    }
    
    /**
     * This method returns the number of Evidence Cases that the ArrayList is
     * currently holding .
     *
     * @return the number of Evidence Cases in the ArrayList.
     */
    public int getNumberOfCases() {
        return postResolutionEvidence.size();
    }
    
    /**
     * This method returns a boolean indicating if the case number passed as
     * parameter is currently compiled.
     *
     * @param caseNumber number of the evidence case.
     *
     * @return the compilation state of the case.
     */
    public boolean getEvidenceCasesCompilationState(int caseNumber) {
        return evidenceCasesCompilationState.get(caseNumber);
    }
    
    /**
     * This method sets which is the compilation state of the case.
     *
     * @param caseNumber number of the evidence case to be set.
     * @param value      true if compiled; false otherwise.
     */
    public void setEvidenceCasesCompilationState(int caseNumber, boolean value) {
        this.evidenceCasesCompilationState.set(caseNumber, value);
    }
    
    /**
     * This method sets the list of evidence cases
     *
     * @param preResolutionEvidence   pre-resolution evidence.
     * @param postResolutionInference a list of evidence case.
     */
    public void setEvidence(EvidenceCase preResolutionEvidence, List<EvidenceCase> postResolutionInference) {
        this.postResolutionEvidence = (postResolutionInference == null) ?
                new ArrayList<EvidenceCase>() :
                postResolutionInference;
        this.preResolutionEvidence = (preResolutionEvidence == null) ? new EvidenceCase() : preResolutionEvidence;
        if (postResolutionEvidence.isEmpty()) {
            this.postResolutionEvidence.add(new EvidenceCase());
        }
        currentCase = this.postResolutionEvidence.size() - 1;
        // Update visual info on evidence
        for (VisualNode node : visualNetwork.getAllNodes()) {
            node.setPostResolutionFinding(false);
        }
        for (EvidenceCase evidenceCase : postResolutionEvidence) {
            for (Finding finding : evidenceCase.getFindings()) {
                for (VisualNode node : visualNetwork.getAllNodes()) {
                    if (node.getNode().getVariable().equals(finding.getVariable())) {
                        node.setPostResolutionFinding(true);
                    }
                }
            }
        }
        for (VisualNode node : visualNetwork.getAllNodes()) {
            node.setPreResolutionFinding(false);
        }
        for (Finding finding : preResolutionEvidence.getFindings()) {
            for (VisualNode node : visualNetwork.getAllNodes()) {
                if (node.getNode().getVariable().equals(finding.getVariable())) {
                    node.setPreResolutionFinding(true);
                }
            }
        }
        // Update evidenceCasesCompilationState
        evidenceCasesCompilationState.clear();
        for (int i = 0; i < postResolutionEvidence.size(); ++i) {
            evidenceCasesCompilationState.add(false);
        }
    }
    
    /**
     * This method returns true if propagation type currently set is automatic;
     * false if manual.
     *
     * @return true if the current propagation type is automatic.
     */
    public boolean isAutomaticPropagation() {
        return automaticPropagation;
    }
    
    /**
     * This method sets the current propagation type.
     *
     * @param automaticPropagation new value of the propagation type.
     */
    public void setAutomaticPropagation(boolean automaticPropagation) {
        this.automaticPropagation = automaticPropagation;
    }
    
    /**
     * This method returns the propagation status: true if propagation should be
     * done right now; false otherwise.
     *
     * @return true if propagation should be done right now.
     */
    public boolean isPropagationActive() {
        return propagationActive;
    }
    
    /**
     * This method sets the propagation status.
     *
     * @param propagationActive new value of the propagation status.
     */
    public void setPropagationActive(boolean propagationActive) {
        this.propagationActive = propagationActive;
        this.visualNetwork.setPropagationActive(propagationActive);
    }
    
    /**
     * This method returns the associated network panel.
     *
     * @return the associated network panel.
     */
    public NetworkPanel getNetworkPanel() {
        return networkPanel;
    }
    
    /**
     * This method returns the current expansion threshold.
     *
     * @return the value of the current expansion threshold.
     */
    public double getExpansionThreshold() {
        return currentExpansionThreshold;
    }
    
    /**
     * This method changes the current expansion threshold.
     *
     * @param expansionThreshold new value of the expansion threshold.
     */
    public void setExpansionThreshold(double expansionThreshold) {
        this.currentExpansionThreshold = expansionThreshold;
    }
    
    /**
     * This method updates the expansion state (expanded/contracted) of the
     * nodes. It is used in transitions from edition to inference mode and vice
     * versa, and also when the user modifies the current expansion threshold in
     * the Inference tool bar
     *
     * @param newWorkingMode new value of the working mode.
     */
    public void updateNodesExpansionState(NetworkPanel.WorkingMode newWorkingMode) {
        switch (newWorkingMode) {
            case EDITION -> {
                List<VisualNode> allNodes = visualNetwork.getAllNodes();
                if (!allNodes.isEmpty()) {
                    for (int i = 0; i < allNodes.size(); i++) {
                        VisualNode visualNode = allNodes.get(i);
                        if (visualNode.isExpanded()) {
                            visualNode.setExpanded(false);
                        }
                        repaint();
                    }
                    repaint();
                }
            }
            case INFERENCE -> {
                List<VisualNode> allNodes = visualNetwork.getAllNodes();
                if (!allNodes.isEmpty()) {
                    for (int i = 0; i < allNodes.size(); i++) {
                        VisualNode visualNode = allNodes.get(i);
                        visualNode.setExpanded(visualNode.getNode().getRelevance() >= getExpansionThreshold());
                        repaint();
                    }
                }
            }
        }
    }
    
    /**
     * This method updates the value of each state for each node in the network
     * with the current individual probabilities.
     */
    public void updateIndividualProbabilitiesAndUtilities() throws NotEvaluableNetworkException, NonProjectablePotentialException, NotEnoughtMemoryException, IncompatibleEvidenceException, CannotNormalizePotentialException, ConstraintViolatedException {
        // if some visualNode has a number of values different from the
        // number of evidence cases in memory, we need to recreate its
        // visual states and consider that the network has been changed.
        for (VisualNode visualNode : visualNetwork.getAllNodes()) {
            InnerBox innerBox = visualNode.getInnerBox();
            VisualState visualState;
            if (innerBox instanceof FSVariableBox || innerBox instanceof NumericVariableBox) {
                if (innerBox instanceof FSVariableBox) {
                    visualState = ((FSVariableBox) innerBox).getVisualState(0);
                } else { // (innerBox instanceof NumericVariableBox)
                    visualState = ((NumericVariableBox) innerBox).getVisualState();
                }
                updateVisualStateAndEvidence(innerBox, visualState);
            }
        }
        
        try {
            if ((propagationActive) && (networkPanel.getWorkingMode() == NetworkPanel.WorkingMode.INFERENCE)) {
                // if the network has been changed, propagation must be done in
                // each evidence case in memory. Otherwise, only propagation in
                // current case is needed.
            /*
             TODO - Always true, remove the or condition and set the network as changed when the criteria is modified
             for example, when the scale change
              */
                
                for (int i = 0; i < postResolutionEvidence.size(); i++) {
                    doPropagation(getEvidenceCase(i), i);
                }
                updateNodesFindingState(postResolutionEvidence.get(currentCase));
                networkChanged = false;
            /*
            if (networkChanged) {
                for (int i = 0; i < postResolutionEvidence.size(); i++) {
                    doPropagation(getEvidenceCase(i), i);
                }
                updateNodesFindingState(postResolutionEvidence.get(currentCase));
                networkChanged = false;
            } else {
                if (evidenceCasesCompilationState.get(currentCase) == false) {
                    if (!doPropagation(postResolutionEvidence.get(currentCase), currentCase))
                        setPropagationActive(false);
                }
            }
            */
            } else if (evidenceCasesCompilationState.get(currentCase) == false) {
                // Even if propagation mode is manual, a propagation should be
                // done the first time that inference mode is selected
                doPropagation(postResolutionEvidence.get(currentCase), currentCase);
            }
        } finally {
            updateAllVisualStates("", currentCase);
            repaint();
        }
        
    }
    
    /**
     * @param innerBox
     * @param visualState
     */
    private void updateVisualStateAndEvidence(InnerBox innerBox, VisualState visualState) {
        if (visualState.getNumberOfValues() != postResolutionEvidence.size()) {
            innerBox.update(postResolutionEvidence.size());
            networkChanged = true;
            for (int i = 0; i < postResolutionEvidence.size(); i++) {
                evidenceCasesCompilationState.set(i, false);
            }
        }
    }
    
    /**
     * This method removes all the findings established in the current evidence
     * case.
     */
    public void removeAllFindings() throws NotEvaluableNetworkException, NonProjectablePotentialException, NotEnoughtMemoryException, IncompatibleEvidenceException, CannotNormalizePotentialException, ConstraintViolatedException {
        setPropagationActive(isAutomaticPropagation());
        List<VisualNode> visualNodes = visualNetwork.getAllNodes();
        for (int i = 0; i < visualNodes.size(); i++) {
            visualNodes.get(i).setPostResolutionFinding(false);
        }
        List<Finding> findings = postResolutionEvidence.get(currentCase).getFindings();
        for (int i = 0; i < findings.size(); i++) {
            postResolutionEvidence.get(currentCase).removeFinding(findings.get(i).getVariable());
        }
        try {
            doPropagation(postResolutionEvidence.get(currentCase), currentCase);
        } catch (NotEvaluableNetworkException | NonProjectablePotentialException | NotEnoughtMemoryException |
                 CannotNormalizePotentialException | IncompatibleEvidenceException e) {
            setPropagationActive(false);
            throw e;
        } finally {
            networkPanel.getMainPanel().getInferenceToolBar().setCurrentEvidenceCaseName(currentCase);
            setSelectedAllNodes(false);
            networkPanel.getMainPanel().getMainPanelMenuAssistant().updateOptionsFindingsDependent(networkPanel);
        }
    }
    
    /**
     * This method removes the findings that a node could have in all the
     * evidence cases in memory. It is invoked when a change takes place in
     * properties or probabilities of a the node
     *
     * @param node the node in which to remove the findings.
     */
    public void removeNodeEvidenceInAllCases(Node node) throws NotEvaluableNetworkException, NonProjectablePotentialException, NotEnoughtMemoryException, IncompatibleEvidenceException, CannotNormalizePotentialException, ConstraintViolatedException {
        try {
            for (int i = 0; i < postResolutionEvidence.size(); i++) {
                List<Finding> findings = postResolutionEvidence.get(i).getFindings();
                for (int j = 0; j < findings.size(); j++) {
                    if (node.getVariable() == (findings.get(j).getVariable())) {
                        postResolutionEvidence.get(i).removeFinding(findings.get(j).getVariable());
                        if (isAutomaticPropagation() && (inferenceAlgorithm != null)) {
                            doPropagation(postResolutionEvidence.get(i), i);
                        }
                        if (i == currentCase) {
                            List<VisualNode> visualNodes = visualNetwork.getAllNodes();
                            for (int k = 0; k < visualNodes.size(); k++) {
                                if (visualNodes.get(k).getNode() == node) {
                                    visualNodes.get(k).setPostResolutionFinding(false);// ...asaez....PENDIENTE........
                                }
                            }
                        }
                    }
                }
            }
        } catch (NotEvaluableNetworkException | NonProjectablePotentialException | NotEnoughtMemoryException |
                 CannotNormalizePotentialException | IncompatibleEvidenceException e) {
            setPropagationActive(false);
            throw e;
        } finally {
            setSelectedAllNodes(false);
            networkPanel.getMainPanel().getMainPanelMenuAssistant().updateOptionsFindingsDependent(networkPanel);
            repaint();
        }
    }
    
    /**
     * This method returns true if there are any finding in the current evidence
     * case.
     *
     * @return true if the current evidence case has at least one finding.
     */
    public boolean areThereFindingsInCase() {
        boolean areFindings = false;
        List<Finding> findings = postResolutionEvidence.get(currentCase).getFindings();
        if (findings != null) {
            if (!findings.isEmpty()) {
                areFindings = true;
            }
        }
        return areFindings;
    }
    
    /**
     * This method returns the number of the Evidence Case that is currently
     * selected
     *
     * @param visualNode a node
     * @param state      the visual state in which the finding is going to be
     *                   set.
     */
    public void toggleFinding(VisualNode visualNode, VisualState state) throws IncompatibleEvidenceException, DoEditException, NotEvaluableNetworkException, NonProjectablePotentialException, NotEnoughtMemoryException, CannotNormalizePotentialException {
        setNewFinding(visualNode, null, new Finding(visualNode.getNode().getVariable(), state.getStateIndex()), true);
    }
    
    /**
     * This method returns the number of the Evidence Case that is currently
     * selected
     *
     * @param visualNode a node.
     * @param finding    a finding.
     * @param toggle     a boolean value.
     */
    public void setNewFinding(VisualNode visualNode, Finding previousFinding, Finding finding, boolean toggle) throws IncompatibleEvidenceException, DoEditException, NotEvaluableNetworkException, NonProjectablePotentialException, NotEnoughtMemoryException, CannotNormalizePotentialException {
        Variable variable = visualNode.getNode().getVariable();
        
        boolean isInferenceMode = networkPanel.getWorkingMode() == NetworkPanel.WorkingMode.INFERENCE;
        EvidenceCase evidenceCase = (isInferenceMode) ? postResolutionEvidence.get(currentCase) : preResolutionEvidence;
        setPropagationActive(isAutomaticPropagation());
        boolean alreadyHasFinding = evidenceCase.contains(variable);
        Finding oldFinding = null;
        if (alreadyHasFinding) {
            // There is already a finding. Remove it
            oldFinding = evidenceCase.removeFinding(variable);
        }
        // Add finding (unless we were toggling evidence)
        if (!alreadyHasFinding || !toggle || oldFinding.getState() != finding.getState()) {
            evidenceCase.addFinding(finding);
            if (isInferenceMode) {
                visualNode.setPostResolutionFinding(true);
            } else {
                AddFindingEdit addFindingEdit = new AddFindingEdit(visualNode, evidenceCase, previousFinding, finding);
                addFindingEdit.executeEdit();
            }
        }
        // Flag current case as not compiled
        if (isInferenceMode) {
            evidenceCasesCompilationState.set(currentCase, false);
        } else {
            for (int i = 0; i < evidenceCasesCompilationState.size(); ++i) {
                evidenceCasesCompilationState.set(i, false);
            }
        }
        setSelectedAllNodes(false);
        networkPanel.getMainPanel().getInferenceToolBar().setCurrentEvidenceCaseName(currentCase);
        
        
        try {
            // If propagation is active, do propagation
            if ((propagationActive) && (evidenceCasesCompilationState.get(currentCase) == false) && (isInferenceMode)) {
                doPropagation(evidenceCase, currentCase);
            }
        } catch (NonProjectablePotentialException | NotEnoughtMemoryException | NotEvaluableNetworkException |
                 CannotNormalizePotentialException | IncompatibleEvidenceException e) {
            evidenceCase.removeFinding(variable);
            if (alreadyHasFinding) {
                try {
                    evidenceCase.addFinding(oldFinding);
                } catch (IncompatibleEvidenceException.EvidenceIsIncompatibleWithOther ex) {
                    throw new UnreacheableException(ex);
                }
            }
            visualNode.setPostResolutionFinding(alreadyHasFinding);
            throw e;
        } finally {
            networkPanel.getMainPanel().getMainPanelMenuAssistant().updateOptionsFindingsDependent(networkPanel);
            networkPanel.getMainPanel().getMainPanelMenuAssistant()
                        .updateOptionsPropagationTypeDependent(networkPanel);// ..
            repaint();
        }
        
    }
    
    /**
     * Returns the inference algorithm assigned to the panel.
     *
     * @return the inference algorithm assigned to the panel.
     */
    public InferenceAlgorithm getInferenceAlgorithm() throws NotSupportedOperationException, NotEvaluableNetworkException {
        if (inferenceAlgorithm == null) {
            inferenceAlgorithm = inferenceManager.getDefaultInferenceAlgorithm(probNet);
            if (inferenceAlgorithm == null) {
                throw new NotSupportedOperationException("there is no associated inference algorithm for " + probNet.localize());
            }
        }
        return inferenceAlgorithm;
    }
    
    /**
     * Sets the inference algorithm assigned to the panel.
     *
     * @param inferenceAlgorithm the inference Algorithm to be assigned to the
     *                           panel.
     */
    public void setInferenceAlgorithm(InferenceAlgorithm inferenceAlgorithm) {
        this.inferenceAlgorithm = inferenceAlgorithm;
    }
    
    /**
     * This method does the propagation of the evidence in the network
     *
     * @param evidenceCase the evidence case with which the propagation must be
     *                     done.
     * @param caseNumber   number of this evidence case.
     */
    @SuppressWarnings("ThrowInsideCatchBlockWhichIgnoresCaughtException")
    public void doPropagation(EvidenceCase evidenceCase, int caseNumber) throws NonProjectablePotentialException, NotEnoughtMemoryException, NotEvaluableNetworkException, CannotNormalizePotentialException, IncompatibleEvidenceException, ConstraintViolatedException {
        Map<Variable, TablePotential> individualProbabilities = null;
        long start = System.currentTimeMillis();
        try {
            //inferenceAlgorithm = getInferenceAlgorithm();
            //inferenceAlgorithm.setPreResolutionEvidence(preResolutionEvidence);
            //inferenceAlgorithm.setPostResolutionEvidence(evidenceCase);
            
            calculateMinAndMaxUtilityRanges();
            //individualProbabilities = inferenceAlgorithm.getProbsAndUtilities ();
            Propagation vePosteriorValues = new VEPropagation(probNet);
            vePosteriorValues.setVariablesOfInterest(probNet.getVariables());
            vePosteriorValues.setPreResolutionEvidence(preResolutionEvidence);
            vePosteriorValues.setPostResolutionEvidence(evidenceCase);
            individualProbabilities = vePosteriorValues.getPosteriorValues();
        } catch (OutOfMemoryError e) {
            if (!approximateInferenceWarningGiven) {
                throw new NotEnoughtMemoryException(e);
            }
            inferenceAlgorithm = inferenceManager.getDefaultApproximateAlgorithm(probNet);
            // TODO - Check these lines
            // ((VEPropagation) inferenceAlgorithm).setPostResolutionEvidence(evidenceCase);
            //				individualProbabilities = inferenceAlgorithm.getProbsAndUtilities();
        }
        long elapsedTimeMillis = System.currentTimeMillis() - start;
        System.out.println("Inference took " + elapsedTimeMillis + " milliseconds.");
        updateNodesFindingState(evidenceCase);
        paintInferenceResults(caseNumber, individualProbabilities, evidenceCase);
        
        evidenceCasesCompilationState.set(caseNumber, true);
        repaint();
    }
    
    // This commented method computes the exact ranges of the utility functions.
    // However, we are using an approximation in the method currently offered by
    // this class.
    /*
     * Calculates minUtilityRange and maxUtilityRange fields.
     */
	/*
	/*
	 * private void () { TablePotential auxF; ArrayList<Variable>
	 * utilityVariables = probNet .getVariables(NodeType.UTILITY); for (Variable
	 * utility : utilityVariables) { auxF = probNet.getUtilityFunction(utility);
	 * minUtilityRange.put(utility, Tools.min(auxF.values));
	 * maxUtilityRange.put(utility, Tools.max(auxF.values)); } }
	 */
    
    /**
     * Calculates minUtilityRange and maxUtilityRange fields. It is an
     * approximate implementation. The correct computation is given by a method
     * with the same name, but commented above.
     *
     * @throws NonProjectablePotentialException
     */
    private void calculateMinAndMaxUtilityRanges() throws NonProjectablePotentialException {
        List<Variable> utilityVariables = probNet.getVariables(NodeType.UTILITY);
        for (Variable utility : utilityVariables) {
            ProbNet newNet = probNet.copy();
            newNet = TaskUtilities.extendPreResolutionEvidence(newNet, getPreResolutionEvidence());
            Node node = newNet.getNode(utility);
            //minUtilityRange.put(utility, node.getApproximateMaxMinimumUtilityFunction(false, preResolutionEvidence));
            //maxUtilityRange.put(utility, node.getApproximateMaxMinimumUtilityFunction(true, preResolutionEvidence));
            minUtilityRange.put(utility, node.getApproximateMinimumUtilityFunction());
            maxUtilityRange.put(utility, node.getApproximateMaximumUtilityFunction());
        }
    }
    
    /**
     * This method fills the visualStates with the proper values to be
     * represented after the evaluation of the evidence case
     *
     * @param caseNumber              number of this evidence case.
     * @param individualProbabilities the results of the evaluation for each
     *                                variable.
     */
    private void paintInferenceResults(int caseNumber, Map<Variable, TablePotential> individualProbabilities,
                                       EvidenceCase evidence) {
        for (VisualNode visualNode : visualNetwork.getAllNodes()) {
            Node node = visualNode.getNode();
            switch (node.getNodeType()) {
                case CHANCE, DECISION -> {
                    paintInferenceResultsChanceOrDecisionNode(caseNumber, individualProbabilities, evidence, visualNode);
                }
                case UTILITY -> {
                    paintInferenceResultsUtilityNode(caseNumber, individualProbabilities, visualNode);
                }
                case SV_SUM, SV_PRODUCT -> {
                }
            }
        }
        repaint();
    }
    
    /**
     * This method fills the visualStates of a utility node with the proper
     * values to be represented after the evaluation of the evidence case
     *
     * @param caseNumber              number of this evidence case.
     * @param individualProbabilities the results of the evaluation for each
     *                                variable.
     * @param visualNode              a node.
     */
    private void paintInferenceResultsUtilityNode(int caseNumber, Map<Variable, TablePotential> individualProbabilities,
                                                  VisualNode visualNode) {
        // It is a utility node
        Variable variable = visualNode.getNode().getVariable();
        NumericVariableBox innerBox = (NumericVariableBox) visualNode.getInnerBox();
        VisualState visualState = innerBox.getVisualState();
        visualState.setStateValue(caseNumber, individualProbabilities.get(variable).values[0]);
        innerBox.setMinValue(minUtilityRange.get(variable));
        innerBox.setMaxValue(maxUtilityRange.get(variable));
    }
    
    /**
     * This method fills the visualStates of a chance or decision node with the
     * proper values to be represented after the evaluation of the evidence case
     *
     * @param caseNumber              number of this evidence case.
     * @param individualProbabilities the results of the evaluation for each variable.
     * @param evidence                evidence.
     * @param visualNode              a node.
     */
    private void paintInferenceResultsChanceOrDecisionNode(int caseNumber,
                                                           Map<Variable, TablePotential> individualProbabilities, EvidenceCase evidence, VisualNode visualNode) {
        Variable variable = visualNode.getNode().getVariable();
        TablePotential tablePotential = individualProbabilities.get(variable);
        if (variable.getVariableType() != VariableType.NUMERIC) {
            switch (tablePotential.getNumVariables()) {
                case 0 -> {
                    if ((visualNode.getInnerBox()) instanceof FSVariableBox) {
                        FSVariableBox innerBox = (FSVariableBox) visualNode.getInnerBox();
                        for (int i = 0; i < innerBox.getNumStates(); i++) {
                            VisualState visualState = innerBox.getVisualState(i);
                            visualState.setStateValue(caseNumber, (1.0 / innerBox.getNumStates()));
                        }
                    }
                    visualNode.setPostResolutionFinding(false);
                    // END OF
                    // PROVISIONAL2.............asaez...Comprobar si es innecesario
                    // este Provisional2............
                }
                case 1 -> {
                    double[] values = tablePotential.getValues();
                    if ((visualNode.getInnerBox()) instanceof FSVariableBox) {
                        FSVariableBox innerBox = (FSVariableBox) visualNode.getInnerBox();
                        for (int i = 0; i < innerBox.getNumStates(); i++) {
                            VisualState visualState = innerBox.getVisualState(i);
                            visualState.setStateValue(caseNumber, values[i]);
                        }
                    }
                    // PROVISIONAL2: Currently the propagation
                    // algorithm is returning a TablePotential
                    // with 0 variables when the node has a Uniform
                    // relation
                }
                default -> throw new UnrecoverableException(new NotSupportedOperationException(
                        "Table potentials with more than 1 variables aren't supported yet, meaning potential "
                                + variable + " cannot be treated yet"));
            }
            
        } else  // if numeric variable
        {
            double value = (evidence.contains(variable)) ? evidence.getNumericalValue(variable) : Double.NaN;
            value = (preResolutionEvidence.contains(variable)) ?
                    preResolutionEvidence.getNumericalValue(variable) :
                    value;
            NumericVariableBox innerBox = (NumericVariableBox) visualNode.getInnerBox();
            innerBox.getVisualState().setStateValue(caseNumber, value);
        }
        
    }
    
    /**
     * This method updates the "finding state" of each node
     *
     * @param evidenceCase the evidence case with which the update must be done.
     */
    public void updateNodesFindingState(EvidenceCase evidenceCase) {
        for (VisualNode visualNode : visualNetwork.getAllNodes()) {
            visualNode.setPreResolutionFinding(false);
            visualNode.setPostResolutionFinding(false);
        }
        for (Finding finding : evidenceCase.getFindings()) {
            Variable variable = finding.getVariable();
            for (VisualNode visualNode : visualNetwork.getAllNodes()) {
                if (variable.getName().equals(visualNode.getNode().getName())) {
                    visualNode.setPostResolutionFinding(true);
                }
            }
        }
        for (Finding finding : preResolutionEvidence.getFindings()) {
            Variable variable = finding.getVariable();
            for (VisualNode visualNode : visualNetwork.getAllNodes()) {
                if (variable.getName().equals(visualNode.getNode().getName())) {
                    visualNode.setPreResolutionFinding(true);
                }
            }
        }
        repaint();
    }
    
    public void temporalEvolution() {
        VisualNode node;
        List<VisualNode> selectedNode = visualNetwork.getSelectedNodes();
        if (selectedNode.size() == 1) {
            node = selectedNode.get(0);
            new TemporalEvolutionDialog(Utilities.getOwner(this), node.getNode(), preResolutionEvidence);
            setSelectedAllNodes(false);
            repaint();
            // TODO - Change code
        }/*
        else if(selectedNode == null){
        	new CostEffectivenessDialog(Utilities.getOwner (this), probNet, true, true).setVisible(true);
        }*/ else if (selectedNode.isEmpty()) {
            new TemporalEvolutionDialog(Utilities.getOwner(this), getNetworkPanel().getProbNet(), preResolutionEvidence);
        }
        
    }
    
    /**
     * This method creates a new evidence case
     */
    public void createNewEvidenceCase() throws NotEvaluableNetworkException, NonProjectablePotentialException, NotEnoughtMemoryException, IncompatibleEvidenceException, CannotNormalizePotentialException, ConstraintViolatedException {
        EvidenceCase newEvidenceCase = new EvidenceCase();
        EvidenceCase currentEvidenceCase = getCurrentEvidenceCase();
        List<Finding> currentFindings = currentEvidenceCase.getFindings();
        for (int i = 0; i < currentFindings.size(); i++) {
            newEvidenceCase.addFinding(currentFindings.get(i));
        }
        addNewEvidenceCase(newEvidenceCase);
    }
    
    /**
     * This method adds a new evidence case
     */
    public void addNewEvidenceCase(EvidenceCase newEvidenceCase) throws NotEvaluableNetworkException, NonProjectablePotentialException, NotEnoughtMemoryException, IncompatibleEvidenceException, CannotNormalizePotentialException, ConstraintViolatedException {
        setPropagationActive(isAutomaticPropagation());
        postResolutionEvidence.add(newEvidenceCase);
        currentCase = (postResolutionEvidence.size() - 1);
        evidenceCasesCompilationState.add(currentCase, false);
        updateAllVisualStates("new", currentCase);
        networkPanel.getMainPanel().getInferenceToolBar().setCurrentEvidenceCaseName(currentCase);
        setSelectedAllNodes(false);
        if (isPropagationActive() && networkPanel.getWorkingMode() == NetworkPanel.WorkingMode.INFERENCE) {
            try {
                doPropagation(postResolutionEvidence.get(currentCase), currentCase);
            } catch (NonProjectablePotentialException | NotEnoughtMemoryException | NotEvaluableNetworkException |
                     CannotNormalizePotentialException | IncompatibleEvidenceException e) {
                setPropagationActive(false);
                throw e;
            }
        }
    }
    
    /**
     * This method makes the first evidence case to be the current
     */
    public void goToFirstEvidenceCase() throws NotEvaluableNetworkException, NonProjectablePotentialException, NotEnoughtMemoryException, IncompatibleEvidenceException, CannotNormalizePotentialException, ConstraintViolatedException {
        currentCase = 0;
        updateAllVisualStates("", currentCase);
        networkPanel.getMainPanel().getInferenceToolBar().setCurrentEvidenceCaseName(currentCase);
        setSelectedAllNodes(false);
        if ((propagationActive) && (evidenceCasesCompilationState.get(currentCase) == false) && (
                networkPanel.getWorkingMode() == NetworkPanel.WorkingMode.INFERENCE
        )) {
            try {
                doPropagation(postResolutionEvidence.get(currentCase), currentCase);
            } catch (CannotNormalizePotentialException | IncompatibleEvidenceException |
                     NonProjectablePotentialException | NotEvaluableNetworkException | NotEnoughtMemoryException e) {
                setPropagationActive(false);
                throw e;
            }
        } else {
            updateNodesFindingState(postResolutionEvidence.get(currentCase));
        }
    }
    
    /**
     * This method makes the previous evidence case to be the current
     */
    public void goToPreviousEvidenceCase() throws NotEvaluableNetworkException, NonProjectablePotentialException, NotEnoughtMemoryException, IncompatibleEvidenceException, CannotNormalizePotentialException, ThereIsNoPreviousEvidenceCaseException, ConstraintViolatedException {
        if (!(currentCase > 0)) {
            throw new ThereIsNoPreviousEvidenceCaseException();
        }
        currentCase--;
        updateAllVisualStates("", currentCase);
        networkPanel.getMainPanel().getInferenceToolBar().setCurrentEvidenceCaseName(currentCase);
        setSelectedAllNodes(false);
        if ((propagationActive) && (evidenceCasesCompilationState.get(currentCase) == false) && (
                networkPanel.getWorkingMode() == NetworkPanel.WorkingMode.INFERENCE
        )) {
            try {
                doPropagation(postResolutionEvidence.get(currentCase), currentCase);
            } catch (NonProjectablePotentialException | NotEnoughtMemoryException | NotEvaluableNetworkException |
                     CannotNormalizePotentialException | IncompatibleEvidenceException e) {
                setPropagationActive(false);
                throw e;
            }
        } else {
            updateNodesFindingState(postResolutionEvidence.get(currentCase));
        }
    }
    
    /**
     * This method makes the next evidence case to be the current
     */
    public void goToNextEvidenceCase() throws NotEvaluableNetworkException, NonProjectablePotentialException, NotEnoughtMemoryException, IncompatibleEvidenceException, CannotNormalizePotentialException, ThereIsNoNextEvidenceCaseException, ConstraintViolatedException {
        if (!(currentCase < (postResolutionEvidence.size() - 1))) {
            throw new ThereIsNoNextEvidenceCaseException();
        }
        currentCase++;
        updateAllVisualStates("", currentCase);
        networkPanel.getMainPanel().getInferenceToolBar().setCurrentEvidenceCaseName(currentCase);
        setSelectedAllNodes(false);
        if ((propagationActive) && (evidenceCasesCompilationState.get(currentCase) == false) && (
                networkPanel.getWorkingMode() == NetworkPanel.WorkingMode.INFERENCE
        )) {
            try {
                doPropagation(postResolutionEvidence.get(currentCase), currentCase);
            } catch (NonProjectablePotentialException | NotEnoughtMemoryException | NotEvaluableNetworkException |
                     CannotNormalizePotentialException | IncompatibleEvidenceException e) {
                setPropagationActive(false);
                throw e;
            }
        } else {
            updateNodesFindingState(postResolutionEvidence.get(currentCase));
        }
    }
    
    /**
     * This method makes the last evidence case to be the current
     */
    public void goToLastEvidenceCase() throws NotEvaluableNetworkException, NonProjectablePotentialException, NotEnoughtMemoryException, IncompatibleEvidenceException, CannotNormalizePotentialException, ConstraintViolatedException {
        currentCase = (postResolutionEvidence.size() - 1);
        updateAllVisualStates("", currentCase);
        networkPanel.getMainPanel().getInferenceToolBar().setCurrentEvidenceCaseName(currentCase);
        setSelectedAllNodes(false);
        if ((propagationActive) && (evidenceCasesCompilationState.get(currentCase) == false) && (
                networkPanel.getWorkingMode() == NetworkPanel.WorkingMode.INFERENCE
        )) {
            try {
                doPropagation(postResolutionEvidence.get(currentCase), currentCase);
            } catch (NonProjectablePotentialException | NotEnoughtMemoryException | NotEvaluableNetworkException |
                     CannotNormalizePotentialException | IncompatibleEvidenceException e) {
                setPropagationActive(false);
                throw e;
            }
        } else {
            updateNodesFindingState(postResolutionEvidence.get(currentCase));
        }
    }
    
    /**
     * This method clears out all the evidence cases. It returns to an 'initial
     * state' in which there is only an initial evidence case with no findings
     * (corresponding to prior probabilities)
     */
    public void clearOutAllEvidenceCases() throws NotEvaluableNetworkException, NonProjectablePotentialException, NotEnoughtMemoryException, IncompatibleEvidenceException, CannotNormalizePotentialException, ConstraintViolatedException {
        setPropagationActive(isAutomaticPropagation());
        postResolutionEvidence.clear();
        evidenceCasesCompilationState.clear();
        EvidenceCase newEvidenceCase = new EvidenceCase();
        postResolutionEvidence.add(newEvidenceCase);
        currentCase = 0;
        evidenceCasesCompilationState.add(currentCase, false);
        updateAllVisualStates("clear", currentCase);
        networkPanel.getMainPanel().getInferenceToolBar().setCurrentEvidenceCaseName(currentCase);
        setSelectedAllNodes(false);
        try {
            doPropagation(postResolutionEvidence.get(currentCase), currentCase);
        } catch (NonProjectablePotentialException | NotEnoughtMemoryException | NotEvaluableNetworkException |
                 CannotNormalizePotentialException | IncompatibleEvidenceException e) {
            setPropagationActive(false);
            throw e;
        }
    }
    
    /**
     * This method updates all visual states of all visual nodes when it is
     * needed for a navigation operation among the existing evidence cases, a
     * creation of a new case or when all cases are cleared out.
     *
     * @param option the specific operation to be done over the visual states.
     */
    public void updateAllVisualStates(String option, int caseNumber) {
        List<VisualNode> allVisualNodes = visualNetwork.getAllNodes();
        for (VisualNode visualNode : allVisualNodes) {
            InnerBox innerBox = visualNode.getInnerBox();
            VisualState visualState = null;
            for (int i = 0; i < innerBox.getNumStates(); i++) {
                if (innerBox instanceof FSVariableBox) {
                    visualState = ((FSVariableBox) innerBox).getVisualState(i);
                } else if (innerBox instanceof NumericVariableBox) {
                    visualState = ((NumericVariableBox) innerBox).getVisualState();
                }
                if (option.equals("new")) {
                    visualState.createNewStateValue();
                } else if (option.equals("clear")) {
                    visualState.clearAllStateValues();
                }
                visualState.setCurrentStateValue(caseNumber);
            }
        }
        repaint();
    }
    
    /**
     * This method does the propagation of the evidence for all the evidence
     * cases in memory.
     *
     * @param mainPanelMenuAssistant the menu assistant associated to the main
     *                               panel.
     */
    public void propagateEvidence(MainPanelMenuAssistant mainPanelMenuAssistant) throws NotEvaluableNetworkException, NonProjectablePotentialException, NotEnoughtMemoryException, IncompatibleEvidenceException, CannotNormalizePotentialException, ConstraintViolatedException {
        setPropagationActive(true);
        if (networkPanel.getWorkingMode() == NetworkPanel.WorkingMode.INFERENCE) {
            for (int i = 0; i < getNumberOfCases(); i++) {
                if (evidenceCasesCompilationState.get(i) == false) {
                    try {
                        doPropagation(postResolutionEvidence.get(currentCase), currentCase);
                    } catch (NonProjectablePotentialException | NotEnoughtMemoryException |
                             NotEvaluableNetworkException |
                             CannotNormalizePotentialException | IncompatibleEvidenceException e) {
                        setPropagationActive(false);
                        throw e;
                    }
                }
            }
            setSelectedAllNodes(false);
            updateAllVisualStates("", currentCase);
            networkPanel.getMainPanel().getInferenceToolBar().setCurrentEvidenceCaseName(currentCase);
            updateNodesFindingState(postResolutionEvidence.get(currentCase));
        }
        mainPanelMenuAssistant.updateOptionsEvidenceCasesNavigation(networkPanel);
        mainPanelMenuAssistant.updateOptionsPropagationTypeDependent(networkPanel);
        mainPanelMenuAssistant.updateOptionsFindingsDependent(networkPanel);
    }
    
    /**
     * This method sets the inference options for this panel.
     */
    public void setInferenceOptions() {
        PropagationOptionsDialog inferenceOptionsDialog = new PropagationOptionsDialog(Utilities.getOwner(this), this,
                                                                                       networkPanel.getMainPanel()
                                                                                                   .getInferenceToolBar());
        inferenceOptionsDialog.setVisible(true);
    }
    
    /**
     * Removes selected objects
     */
    public void removeSelectedObjects() {
        RemoveSelectedEdit cutEdit = new RemoveSelectedEdit(visualNetwork);
        visualNetwork.setSelectedAllObjects(false);
        try {
            @ToCheck(reasonKind = {ToCheck.ReasonKind.USER_EXPERIENCE, ToCheck.ReasonKind.CODE_QUALITY},
                    reasonDescription = "Can a RemoveSelectedEdit actually trigger a do edit exception?")
            var check = false;
            cutEdit.executeEdit();
        } catch (DoEditException e) {
            throw new UnrecoverableException(e);
        }
        propagationActive = isAutomaticPropagation();
        networkChanged = true;
        repaint();
    }
    

    
    
    /**
     * This method inverts the selected link arc-reversal style
     */
    public void invertLinkAndUpdatePotentials() throws DoEditException {
        List<VisualLink> links = visualNetwork.getSelectedLinks();
        if (links.isEmpty()) {
            return;
        }
        Link<Node> link = links.get(0).getLink();
        Node node1 = link.getFrom();
        Node node2 = link.getTo();
        InvertLinkAndUpdatePotentialsEdit invertLink =
                new InvertLinkAndUpdatePotentialsEdit(probNet, node1.getVariable(), node2.getVariable());
        invertLink.executeEdit();
        repaint();
    }
    
    /***
     * Initializes the link restriction potential of a link
     */
    public void enableLinkRestriction() {
        List<VisualLink> links = visualNetwork.getSelectedLinks();
        if (!links.isEmpty()) {
            Link<Node> link = links.get(0).getLink();
            if (!link.hasRestrictions()) {
                link.initializesRestrictionsPotential();
            }
            if (!requestLinkRestrictionValues(Utilities.getOwner(this), link)) {
                probNet.getPNESupport().undoAndDelete();
            }
            link.resetRestrictionsPotential();
            repaint();
        }
    }
    
    /***
     * Resets the link restriction potential of a link
     */
    public void disableLinkRestriction() throws DoEditException {
        RemoveLinkRestrictionEdit removeLinkRestrictionEdit = new RemoveLinkRestrictionEdit(visualNetwork);
        removeLinkRestrictionEdit.executeEdit();
        repaint();
    }
    
    /***
     * Initializes the revelation arc properties of a link
     */
    public void enableRevelationArc() {
        List<VisualLink> links = visualNetwork.getSelectedLinks();
        if (!links.isEmpty()) {
            Link<Node> link = links.get(0).getLink();
            if (!requestRevelationArcValues(Utilities.getOwner(this), link)) {
                probNet.getPNESupport().undoAndDelete();
            }
            repaint();
        }
    }
    
    /**
     * Returns the visualNetwork.
     *
     * @return the visualNetwork.
     */
    public VisualNetwork getVisualNetwork() {
        return visualNetwork;
    }
    
    /**
     * Sets a new visualNetwork.
     *
     * @param visualNetwork
     */
    public void setVisualNetwork(VisualNetwork visualNetwork) {
        this.visualNetwork = visualNetwork;
    }
    
    public void setProbNet(ProbNet probNet) {
        networkChanged = true;
        this.probNet = probNet;
        visualNetwork.setProbNet(probNet);
    }
    
    /**
     * Sets workingMode
     *
     * @param newWorkingMode
     */
    public void setWorkingMode(NetworkPanel.WorkingMode newWorkingMode) {
        visualNetwork.setWorkingMode(newWorkingMode);
        if (newWorkingMode == NetworkPanel.WorkingMode.INFERENCE) {
            editionMode = editionModeManager.getDefaultEditionMode();
            setCursor(editionModeManager.getDefaultCursor());
        }
    }
    
    // TODO OOPN end
    protected double[] getBounds(Graphics2D graphics) {
        return visualNetwork.getNetworkBounds(graphics);
    }
    
    /**
     * If the dimensions of the network are greater than the dimensions of the
     * panel, changes the dimensions of the panel in order to accommodate the
     * whole network.
     */
    public void adjustPanelDimension() {
        double[] bounds = getBounds((Graphics2D) getGraphics());
        this.currentWidth = Math.min(MAX_WIDTH, bounds[1]);
        this.currentHeight = Math.min(MAX_HEIGHT, bounds[3]);
        Dimension newDimension = new Dimension((int) Math.round(getNewWidth()), (int) Math.round(getNewHeight()));
        setPreferredSize(newDimension);
        setSize(newDimension);
    }
    
    /**
     * Sets the zoom so the displayed network fits in the panel.
     */
    public void setZoomToFitNetwork() {
        double[] networkBounds = getBounds((Graphics2D) getGraphics());
        Dimension panelBounds = networkPanel.getMainPanel().getNetworksTabPanel().getSize();
        double zoom = 1;
        
        while (((networkBounds[1] * zoom) > panelBounds.getWidth())
                || ((networkBounds[3] * zoom) > panelBounds.getHeight()) && zoom > 0.1) {
            zoom -= 0.1;
        }
        setZoom(zoom);
    }
    
    public void createNextSliceNode() throws DoEditException {
        Node selectedNode = visualNetwork.getSelectedNodes().getFirst().getNode();
        Variable selectedVariable = selectedNode.getVariable();
        Variable newVariable = new Variable(selectedVariable);
        newVariable.setTimeSlice(selectedVariable.getTimeSlice() + 1);
        Point2D.Double position = new Point2D.Double(selectedNode.getCoordinateX() + 200,
                                                     selectedNode.getCoordinateY());
        AddNodeEdit addNodeEdit = new AddNodeEdit(probNet, newVariable, selectedNode.getNodeType(), position);
        try {
            addNodeEdit.executeEdit();
        } finally {
            adjustPanelDimension();
            repaint();
        }
    }
    
    /**
     * This method sets the network as changed but also modifies the menu so the save button is enabled
     * This is usually done through edit, but some actions in this class do not use edits
     * TODO: check whether some actions like removePolicyFromNode should be refactored as edits or not
     */
    private void setNetworkChangedWithOutEdit(boolean networkChanged) {
        this.networkChanged = networkChanged;
        networkPanel.setModified(networkChanged);
        if (networkChanged) {
            networkPanel.getMainPanel().getMainPanelMenuAssistant().updateOptionsNetworkModified(false, false);
        } else {
            networkPanel.getMainPanel().getMainPanelMenuAssistant().updateOptionsNetworkSaved();
        }
    }
    
    // The key listener needs a focusable object to listen
    @Override
    public boolean isFocusable() {
        return true;
    }
    
    // Moves the selected nodes when pressing the arrows
    @Override
    public void keyPressed(KeyEvent keyEvent) {
        int key = keyEvent.getKeyCode();
        switch (key) {
            case KeyEvent.VK_UP:
                visualNetwork.moveSelectedElements(0, -2);
                break;
            case KeyEvent.VK_RIGHT:
                visualNetwork.moveSelectedElements(+2, 0);
                break;
            case KeyEvent.VK_DOWN:
                visualNetwork.moveSelectedElements(0, +2);
                break;
            case KeyEvent.VK_LEFT:
                visualNetwork.moveSelectedElements(-2, 0);
                break;
        }
        repaint();
    }
    
    @Override
    public void keyReleased(KeyEvent keyEvent) {
    
    }
    
    
    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }
    
    public void cancelAction() {
        boolean alreadyModifiedNetwork = networkPanel.getModified();
        probNet.getPNESupport().undoAndDelete();
        // We restore the network state to not modified, if it was not already modified
        if (!alreadyModifiedNetwork) {
            setNetworkChangedWithOutEdit(false);
            setSelectedAllNodes(false);
            repaint();
        }
    }
    
    @Override
    public void afterUndoingEdit(PNUndoableEditEvent event) {
        List<Finding> findings = preResolutionEvidence.getFindings();
        Set<Variable> findingVariables = findings.stream()
                                                 .map(Finding::getVariable)
                                                 .collect(Collectors.toSet());
        
        List<VisualNode> allVisualNodes = visualNetwork.getAllNodes();
        
        for (VisualNode visualNode : allVisualNodes) {
            Variable nodeVariable = visualNode.getNode().getVariable();
            boolean isPreResolution = findingVariables.contains(nodeVariable);
            visualNode.setPreResolutionFinding(isPreResolution);
        }
        adjustPanelDimension();
        repaint();
    }
    
    @Override
    public void afterEditHappens(PNUndoableEditEvent e) {
        for (Finding finding : preResolutionEvidence.getFindings()) {
            Variable variable = finding.getVariable();
            for (VisualNode visualNode : visualNetwork.getAllNodes()) {
                if (variable.getName().equals(visualNode.getNode().getName())) {
                    visualNode.setPreResolutionFinding(true);
                }
            }
        }
        adjustPanelDimension();
        repaint();
    }
    
    @Override public void onEditFailed(PNUndoableEditEvent event, DoEditException exception) {
        adjustPanelDimension();
        repaint();
    }
    
    @Override
    public void onEditViolatesConstraints(PNUndoableEditEvent pnUndoableEditEvent, ConstraintViolatedException ex) {
        adjustPanelDimension();
        repaint();
    }
}
