/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.window.edition;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openmarkov.core.action.AddLinkEdit;
import org.openmarkov.core.action.AddProbNodeEdit;
import org.openmarkov.core.action.UndoManagerSupport;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.exception.NoFindingException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.action.MoveNodeEdit;
import org.openmarkov.core.gui.action.PasteEdit;
import org.openmarkov.core.gui.action.RemoveSelectedEdit;
import org.openmarkov.core.gui.constraint.LinkRestrictionValidator;
import org.openmarkov.core.gui.constraint.RevelationArcValidator;
import org.openmarkov.core.gui.dialog.CostEffectivenessDialog;
import org.openmarkov.core.gui.dialog.OptionsInferenceDialog;
import org.openmarkov.core.gui.dialog.SelectZoomDialog;
import org.openmarkov.core.gui.dialog.link.LinkRestrictionEditDialog;
import org.openmarkov.core.gui.dialog.network.NetworkPropertiesDialog;
import org.openmarkov.core.gui.dialog.node.CommonNodePropertiesDialog;
import org.openmarkov.core.gui.dialog.node.NodeAddFindingDialog;
import org.openmarkov.core.gui.dialog.node.NodePropertiesDialog;
import org.openmarkov.core.gui.dialog.node.PotentialEditDialog;
import org.openmarkov.core.gui.graphic.ExpectedValueBox;
import org.openmarkov.core.gui.graphic.FSVariableBox;
import org.openmarkov.core.gui.graphic.InnerBox;
import org.openmarkov.core.gui.graphic.SelectionListener;
import org.openmarkov.core.gui.graphic.SelectionRectangle;
import org.openmarkov.core.gui.graphic.VisualDecisionNode;
import org.openmarkov.core.gui.graphic.VisualLink;
import org.openmarkov.core.gui.graphic.VisualNetwork;
import org.openmarkov.core.gui.graphic.VisualNode;
import org.openmarkov.core.gui.graphic.VisualState;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.gui.menutoolbar.common.ActionCommands;
import org.openmarkov.core.gui.menutoolbar.menu.NodePopup;
import org.openmarkov.core.gui.menutoolbar.menu.PopupMenuBasic;
import org.openmarkov.core.gui.menutoolbar.menu.PopupMenuFactory;
import org.openmarkov.core.gui.util.Utilities;
import org.openmarkov.core.gui.window.MainPanelMenuAssistant;
import org.openmarkov.core.inference.InferenceAlgorithm;
import org.openmarkov.core.inference.annotation.InferenceManager;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.DefaultStates;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialType;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.type.BayesianNetworkType;
import org.openmarkov.core.model.network.type.InfluenceDiagramType;
import org.openmarkov.core.model.network.type.NetworkType;

/**
 * This class implements the behaviour of a panel where a network will be
 * edited.
 * 
 * 
 * @author jmendoza
 * @author jlgozalo
 * @version 1.1 - jlgozalo - all variables initialized and eliminate the call to
 *          NodePropertiesDialogFactory (to use directly call to
 *          CommonNodePropertiesDialog method.
 * @version 1.3 - asaez - Functionality added: - Explanation capabilities, -
 *          Management of working modes (edition/inference), - Expansion and
 *          contraction of nodes, - Introduction and elimination of evidence -
 *          Management of multiple evidence cases.
 */
public class EditorPanel extends JPanel implements MouseListener,
		MouseMotionListener {

	private ProbNet probNet;

	/**
	 * Static field for serializable class.
	 */
	private static final long serialVersionUID = 2789011585460326400L;

	/**
	 * Constant that indicates the value of the Expansion Threshold by default.
	 */
	// This should be in a future a configuration option that should be read on
	// start
	private static final int DEFAULT_THRESHOLD_VALUE = 5;

	/**
	 * Current state of the edition.
	 */
	private EditionState editionState = EditionState.SELECTION;

	/**
	 * Current selection state.
	 */
	private SelectionState selectionState = SelectionState.DEFAULT;

	/**
	 * This variable indicates which is the expansion threshold of the network
	 */
	private double currentExpansionThreshold = DEFAULT_THRESHOLD_VALUE;

	/**
	 * Network panel associated to this editor panel
	 */
	private NetworkPanel networkPanel = null;

	/**
	 * Array of Evidence cases treated for this editor panel
	 */
	private ArrayList<EvidenceCase> evidenceCases;
	
	/**
	 * Each position of this array indicates if the corresponding evidence
	 * case is currently compiled (if true) or not (if false)
	 */
	private ArrayList<Boolean> evidenceCasesCompilationState;

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
	 * This variable indicates if it has been a change in the properties or
	 * in the potential values in some node.
	 */
	private boolean networkChanged = false;

	/**
	 * Visual representation of the network
	 */
	private VisualNetwork visualNetwork = null;

	/**
	 * Indicates if a node has been moved.
	 */
	private boolean nodeMoved = false;

	/**
	 * Rectangle used to select various nodes.
	 */
	private SelectionRectangle selection = null;

	/**
	 * Position of the mouse cursor when it is pressed.
	 */
	private Point2D.Double cursorPosition = new Point2D.Double();

	/**
	 * Object to convert coordinates of the screen to the panel and vice versa.
	 */
	private Zoom zoom = new Zoom();

	/**
	 * Maximum width of the panel.
	 */
	private double maxWidth = Toolkit.getDefaultToolkit().getScreenSize()
			.getWidth() * 20;

	/**
	 * Maximum height of the panel.
	 */
	private double maxHeight = Toolkit.getDefaultToolkit().getScreenSize()
			.getHeight() * 20;

	/**
	 * Listener that listen to the changes of size.
	 */
	private HashSet<EditorPanelSizeListener> sizeListeners = new HashSet<EditorPanelSizeListener>();

	/**
	 * Information of the movement of the nodes.
	 */
	private ArrayList<VisualNode> movedNodes = null;

	/**
	 * This object represents the arrow that is painted when a new link is being
	 * created.
	 */
	private VisualLink newLink = null;

	/**
	 * This object represents the source node of a new link.
	 */
	private VisualNode newLinkSource = null;

	/**
	 * Object that creates the popup menus.
	 */
	private PopupMenuFactory popupMenuFactory = null;

	/**
	 * Object that assists this panel in the operations with the clipboard.
	 */
	private EditorPanelClipboardAssistant clipboardAssistant = null;

	/**
	 * String resource.
	 */
	private StringResource stringResource = null;

	/**
	 * Object Dialog for potentials edition
	 */
	PotentialEditDialog potentialsDialog = null;

	/****
	 * Dialog for link restriction edition
	 */
	LinkRestrictionEditDialog linkRestrictionDialog = null;

	private CostEffectivenessDialog costEffectivenessDialog;

    private boolean approximateInferenceWarningGiven = false;

	/**
	 * Constructor that creates the instance.
	 * 
	 * @param networkPanel
	 *            network that will be edited.
	 */
	public EditorPanel(NetworkPanel networkPanel) {

		// super();
		this.networkPanel = networkPanel;
		this.probNet = networkPanel.getProbNet();
		visualNetwork = new VisualNetwork(probNet, this);
		automaticPropagation = true;
		propagationActive = true;
		evidenceCases = new ArrayList<EvidenceCase>(1);
		currentCase = 0;
		EvidenceCase evidenceCase = new EvidenceCase();
		evidenceCases.add(currentCase, evidenceCase);
		evidenceCasesCompilationState = new ArrayList<Boolean>(1);
		evidenceCasesCompilationState.add(currentCase, false);

		this.probNet.getPNESupport().addUndoableEditListener(visualNetwork);
		initialize();
		
        inferenceManager = new InferenceManager ();
	}

	/**
	 * This method initializes this instance.
	 */
	private void initialize() {

		addMouseListener(this);
		addMouseMotionListener(this);
		selection = new SelectionRectangle();
		this.setBackground(SystemColor.WHITE);
		adjustPanelDimension();
		stringResource = StringResourceLoader.getUniqueInstance()
				.getBundleMessages();
		// undoManager = new UndoManagerWrapper();

		clipboardAssistant = new EditorPanelClipboardAssistant();
	}

	/**
	 * Return the maximum height of the panel till now.
	 * 
	 * @return maximum height of the panel till now.
	 */
	double getMaxHeight() {

		return zoom.panelToScreen(maxHeight);

	}

	/**
	 * Return the maximum width of the panel till now.
	 * 
	 * @return maximum width of the panel till now.
	 */
	double getMaxWidth() {

		return zoom.panelToScreen(maxWidth);

	}

	/**
	 * Changes the presentation mode of the text of the nodes.
	 * 
	 * @param value
	 *            new value of the presentation mode of the text of the nodes.
	 */
	public void setByTitle(boolean value) {

		visualNetwork.setByTitle(value);
		adjustPanelDimension();
		repaint();

	}

	/**
	 * Returns the presentation mode of the text of the nodes.
	 * 
	 * @return true if the title of the nodes is the name or false if it is the
	 *         name.
	 */
	public boolean getByTitle() {

		return visualNetwork.getByTitle();

	}

	/**
	 * Overwrite 'paint' method to avoid to call it explicitly.
	 * 
	 * @param g
	 *            the graphics context in which to paint.
	 */
	@Override
	public void paint(Graphics g) {

		Graphics2D g2D = (Graphics2D) g;

		super.paint(g);
		g2D.scale(zoom.getZoom(), zoom.getZoom());
		visualNetwork.paint(g2D);
		if (newLink != null) {
			newLink.paint(g2D);
		}
		selection.paint(g2D);

	}

	/**
	 * Returns the state of edition.
	 * 
	 * @return state of edition.
	 */
	public EditionState getEditionState() {

		return editionState;

	}

	/**
	 * Changes the state of the edition and carries out the necessary actions in
	 * each case.
	 * 
	 * @param newState
	 *            new edition state.
	 */
	public void setEditionState(EditionState newState) {

		if (editionState != newState) {
			setCursor(newState.getCursor());
			visualNetwork.setSelectedAllObjects(false);
			editionState = newState;
			repaint();
		}

	}

	/**
	 * Changes the state of the selection and carries out the necessary actions
	 * in each case.
	 * 
	 * @param newState
	 *            new mouse state.
	 */
	private void setSelectionState(SelectionState newState) {

		setCursor(newState.getCursor());
		selectionState = newState;

	}

	/**
	 * Selects all nodes and links.
	 */
	public void selectAllObjects() {

		visualNetwork.setSelectedAllObjects(true);
		repaint();

	}

	/**
	 * If the dimensions of the network are greater than the dimensions of the
	 * panel, changes the dimensions of the panel in order to accomodate the
	 * whole network.
	 */
	private void adjustPanelDimension() {

		double[] networkBounds = visualNetwork
				.getNetworkBounds((Graphics2D) getGraphics());
		Dimension newDimension = null;
		double incrLeft = 0, incrTop = 0;
		double incrRight = networkBounds[1];
		double incrBottom = networkBounds[3];

		if (networkBounds[0] < 0) {
			incrLeft = -networkBounds[0];
		}
		if (networkBounds[2] < 0) {
			incrTop = -networkBounds[2];
		}
		if ((incrLeft > 0) || (incrTop > 0)) {
			visualNetwork.moveAllNodes(incrLeft, incrTop);
			networkBounds[0] = 0;
			networkBounds[1] += incrLeft;
			networkBounds[2] = 0;
			networkBounds[3] += incrTop;
		}
		maxWidth = Math.max(maxWidth, networkBounds[1]);
		maxHeight = Math.max(maxHeight, networkBounds[3]);
		newDimension = new Dimension((int) Math.round(getMaxWidth()),
				(int) Math.round(getMaxHeight()));
		setPreferredSize(newDimension);
		setSize(newDimension);
		if ((incrLeft > 0) || (incrTop > 0)) {
			notifySizeChanged(zoom.panelToScreen(incrLeft),
					zoom.panelToScreen(incrTop), zoom.panelToScreen(incrRight),
					zoom.panelToScreen(incrBottom));
		}

	}

	/**
	 * Notifies to the registered size listener (if any) that the panel's size
	 * has changed.
	 * 
	 * @param incrLeft
	 *            increase for the left side.
	 * @param incrTop
	 *            increase overhead.
	 * @param incrRight
	 *            increase for the right side.
	 * @param incrBottom
	 *            increase for below.
	 */
	private void notifySizeChanged(double incrLeft, double incrTop,
			double incrRight, double incrBottom) {

		for (EditorPanelSizeListener listener : sizeListeners) {
			listener.sizeChanged(incrLeft, incrTop, incrRight, incrBottom);
		}

	}

	/**
	 * Invoked when a mouse button has been clicked (pressed and released) on
	 * the component.
	 * 
	 * @param e
	 *            mouse event information.
	 */

	public void mouseClicked(MouseEvent e) {

	}

	/**
	 * Invoked when a mouse button has been pressed on the component.
	 * 
	 * @param e
	 *            mouse event information.
	 */
	public void mousePressed(MouseEvent e) {

		// Specific functionality depending on the edition state
		switch (editionState) {
		case SELECTION: {
			mousePressedSelection(e);
			break;
		}
		case CHANCE: {
			mousePressedNodeCreation(e, NodeType.CHANCE);
			break;
		}
		case DECISION: {
			mousePressedNodeCreation(e, NodeType.DECISION);
			break;
		}
		case UTILITY: {
			mousePressedNodeCreation(e, NodeType.UTILITY);
			break;
		}
		case LINK: {
			mousePressedLinkCreation(e);
			break;
		}
		default: {
			break;
		}
		}

		// Generic functionality regardless of the edition state
		VisualNode node = null;
		VisualLink link = null;

		Graphics2D g = (Graphics2D) getGraphics();
		if (SwingUtilities.isLeftMouseButton(e)) {
			if (e.getClickCount() == 2) {
				if (Utilities.noMouseModifiers(e)) {
					if (networkPanel.getWorkingMode() == NetworkPanel.EDITION_WORKING_MODE) {
						// If we are in Edition Mode a double click must open
						// the corresponding properties dialog (for node, link or network)
						if ((node = visualNetwork.whatNodeInPosition(
								cursorPosition, g)) != null) {
							changeNodeProperties(node);
						} else if ((link = visualNetwork.whatLinkInPosition(
								cursorPosition, g)) != null) {
							changeLinkProperties(link);
						} else {
							changeNetworkProperties();
						}
					} else {
						// If we are in Inference Mode a double click inside a
						// visual state must introduce evidence in the corresponding node.
						// If the double click is inside a node but outside its inner box 
						// (in its 'expanded external shape'), its properties dialog  
						// should be open
						if (visualNetwork
								.whatStateInPosition(cursorPosition, g) != null) {
							VisualState visualState = visualNetwork
									.whatStateInPosition(cursorPosition, g);
							setNewFinding(visualState);
						} else {
							if ((visualNetwork.whatNodeInPosition(cursorPosition, g) != null) &&
									(visualNetwork.whatInnerBoxInPosition(cursorPosition, g) == null)){
									changeNodeProperties();								
							}
						}
					}
				}
			} else if (e.isAltDown()) {
				if ((node = visualNetwork.whatNodeInPosition(cursorPosition, g)) != null) {
					if (!node.isSelected()) {
						visualNetwork.setSelectedAllObjects(false);
						visualNetwork.setSelectedNode(node, true);
					}
					changePotential();
				}
			}
		} else if (SwingUtilities.isRightMouseButton(e)) {
			showContextualMenu(e, g);
		}
		repaint();

	}

	/**
	 * Invoked when a mouse button has been pressed on the component in the
	 * SELECTION state.
	 * 
	 * @param e
	 *            mouse event information.
	 */
	private void mousePressedSelection(MouseEvent e) {

		VisualNode node = null;
		VisualLink link = null;
		Graphics2D g = (Graphics2D) getGraphics();

		cursorPosition.setLocation(zoom.screenToPanel(e.getX()),
				zoom.screenToPanel(e.getY()));
		if (SwingUtilities.isLeftMouseButton(e)) {
			if (e.isControlDown() || e.isShiftDown()) {
				if ((node = visualNetwork.whatNodeInPosition(cursorPosition, g)) != null) {
					visualNetwork.setSelectedNode(node, !node.isSelected());
				} else if ((link = visualNetwork.whatLinkInPosition(
						cursorPosition, g)) != null) {
					visualNetwork.setSelectedLink(link, !link.isSelected());
				}
			} else {
				if ((node = visualNetwork.whatNodeInPosition(cursorPosition, g)) != null) {
					if (!node.isSelected()) {
						visualNetwork.setSelectedAllObjects(false);
						visualNetwork.setSelectedNode(node, true);
					}

					setSelectionState(SelectionState.MOVING_NODES);
					// TODO revisar si es necesario agregar parentesis
					// probNet.getPNESupport().openParenthesis();

				} else if ((link = visualNetwork.whatLinkInPosition(
						cursorPosition, g)) != null) {
					if (!link.isSelected()) {
						visualNetwork.setSelectedAllObjects(false);
						visualNetwork.setSelectedLink(link, true);
					}
				} else {
					visualNetwork.setSelectedAllObjects(false);
					selection.initSelection(cursorPosition, 0, 0);
					setSelectionState(SelectionState.SELECTING_NODES);
				}
			}
		}
	}

	/**
	 * Shows contextual menu
	 * 
	 * @param e
	 *            MouseEvent
	 * @param g
	 *            Graphics2D
	 */
	private void showContextualMenu(MouseEvent e, Graphics2D g) {
		VisualNode node = null;
		VisualLink link = null;

		if ((node = visualNetwork.whatNodeInPosition(cursorPosition, g)) != null) {
			if (!node.isSelected()) {
				visualNetwork.setSelectedAllObjects(false);
				visualNetwork.setSelectedNode(node, true);
			}
			getPopupMenu(PopupMenuFactory.NODE).show(this, e.getX(), e.getY());
			if (node.getProbNode().getNodeType().equals(NodeType.DECISION)){
				if (networkPanel.getWorkingMode() == NetworkPanel.EDITION_WORKING_MODE) {
					((NodePopup)getPopupMenu(PopupMenuFactory.NODE)).
							setPopupDecisionNodeInEditionMode();
				} else {
					if (evidenceCasesCompilationState.get(currentCase)) {
						((NodePopup)getPopupMenu(PopupMenuFactory.NODE)).
								setPopupDecisionNodeInCompiledInferenceMode();
					} else {
						((NodePopup)getPopupMenu(PopupMenuFactory.NODE)).
								setPopupDecisionNodeInNotCompiledInferenceMode();
					}
				}
			} else {
				((NodePopup)getPopupMenu(PopupMenuFactory.NODE)).
						setDefaultPopupNode(); 
			}
			networkPanel.getMainPanel().getMainPanelMenuAssistant().
					objectsSelected(1, 0, visualNetwork.getSelectedNodes());
		} else if ((link = visualNetwork.whatLinkInPosition(cursorPosition, g)) != null) {
			if (!link.isSelected()) {
				visualNetwork.setSelectedAllObjects(false);
				visualNetwork.setSelectedLink(link, true);
			}
			if (visualNetwork.getSelectedLinksNumber() == 1) {
				boolean linkRestrictionEnabled = false;
				if (LinkRestrictionValidator.isValid(link.getLink())) {
					linkRestrictionEnabled = true;
				}

				((PopupMenuBasic) getPopupMenu(PopupMenuFactory.LINK))
						.setOptionEnabled(
								ActionCommands.LINK_RESTRICTION_ENABLE_PROPERTIES,
								(linkRestrictionEnabled));
				((PopupMenuBasic) getPopupMenu(PopupMenuFactory.LINK))
						.setOptionEnabled(
								ActionCommands.LINK_RESTRICTION_DISABLE_PROPERTIES,
								(linkRestrictionEnabled && link.getLink()
										.hasRestrictions()));

				boolean revelationArcEnabled = false;
				if (RevelationArcValidator.isValid(link.getLink())) {
					revelationArcEnabled = true;
				}
				((PopupMenuBasic) getPopupMenu(PopupMenuFactory.LINK))
						.setOptionEnabled(
								ActionCommands.LINK_REVELATIONARC_PROPERTIES,
								revelationArcEnabled);
			} else {
				((PopupMenuBasic) getPopupMenu(PopupMenuFactory.LINK))
						.setOptionEnabled(
								ActionCommands.LINK_RESTRICTION_ENABLE_PROPERTIES,
								false);
				((PopupMenuBasic) getPopupMenu(PopupMenuFactory.LINK))
						.setOptionEnabled(
								ActionCommands.LINK_RESTRICTION_DISABLE_PROPERTIES,
								false);

				((PopupMenuBasic) getPopupMenu(PopupMenuFactory.LINK))
						.setOptionEnabled(
								ActionCommands.LINK_REVELATIONARC_PROPERTIES,
								false);
			}
			getPopupMenu(PopupMenuFactory.LINK).show(this, e.getX(), e.getY());
		} else {
			visualNetwork.setSelectedAllObjects(false);
			getPopupMenu(PopupMenuFactory.NETWORK).show(this, e.getX(),
					e.getY());
		}
	}

	/**
	 * Invoked when a mouse button has been pressed on the component in the
	 * CHANCE, DECISION or UTILITY states.
	 * 
	 * @param e
	 *            mouse event information.
	 * @param nodeType
	 *            type of the node that will be created.
	 */
	private void mousePressedNodeCreation(MouseEvent e, NodeType nodeType) {
		Graphics2D g = (Graphics2D) getGraphics();
		cursorPosition.setLocation(zoom.screenToPanel(e.getX()),
				zoom.screenToPanel(e.getY()));
		if (SwingUtilities.isLeftMouseButton(e)) {
			if (Utilities.noMouseModifiers(e)) {
				if (visualNetwork.whatElementInPosition(cursorPosition, g) == null) {

					probNet.getPNESupport().setWithUndo(true);

					HashSet<String> existingNames = new HashSet<String>();
					for (ProbNode node : probNet.getProbNodes()) {
						existingNames.add(node.getName());
					}
					String nodeName = Utilities.getNextNodeName(nodeType,
							existingNames);
					AddProbNodeEdit addProbNodeEdit = new AddProbNodeEdit(
							probNet, new Variable(nodeName,
									DefaultStates.getStatesNodeType(nodeType,
											probNet.getDefaultStates())),
							nodeType, cursorPosition);
					try {
						probNet.getPNESupport().announceEdit(addProbNodeEdit);

						// visualNetwork.setCursorPosition(cursorPosition);
						probNet.getPNESupport().doEdit(addProbNodeEdit);

					} catch (ConstraintViolationException e1) {
						System.err.println(e1.toString() + " 1");
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (CanNotDoEditException e1) {
						// TODO Auto-generated catch block
						System.err.println(e1.toString() + " 2");
						e1.printStackTrace();
					} catch (DoEditException e1) {
						System.err.println(e1.toString() + " 3");
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (NotEnoughMemoryException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					} catch (NonProjectablePotentialException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (WrongCriterionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					// undoManager.addEditAddNode(
					// visualNetwork.getNetwork(), nodeWrapper);
					// notifyEditionPerformed();
					adjustPanelDimension();
					repaint();
				}
			}
		}
	}

	/**
	 * Invoked when a mouse button is pressed on the component in the LINK
	 * state.
	 * 
	 * @param e
	 *            mouse event information.
	 */
	private void mousePressedLinkCreation(MouseEvent e) {

		Graphics2D g = (Graphics2D) getGraphics();
		VisualNode node = null;

		cursorPosition.setLocation(zoom.screenToPanel(e.getX()),
				zoom.screenToPanel(e.getY()));
		if (SwingUtilities.isLeftMouseButton(e)) {
			if (Utilities.noMouseModifiers(e)) {
				if ((node = visualNetwork.whatNodeInPosition(cursorPosition, g)) != null) {
					newLink = new VisualLink(node.getPosition(), cursorPosition);
					newLinkSource = node;
				}
			}
		}

	}

	/**
	 * Invoked when a mouse button is pressed on a component and then dragged.
	 * 
	 * @param e
	 *            mouse event information.
	 */
	public void mouseDragged(MouseEvent e) {

		switch (editionState) {
		case SELECTION: {
			mouseDraggedSelection(e);
			break;
		}
		case LINK: {
			mouseDraggedLinkCreation(e);
			break;
		}
		default: {
			break;
		}
		}

	}

	/**
	 * Invoked when a mouse button is pressed on a component and then dragged in
	 * the SELECTION state.
	 * 
	 * @param e
	 *            mouse event information.
	 */
	public void mouseDraggedSelection(MouseEvent e) {

		Point2D.Double point = new Point2D.Double(zoom.screenToPanel(e.getX()),
				zoom.screenToPanel(e.getY()));
		double diffX = point.getX() - cursorPosition.getX();
		double diffY = point.getY() - cursorPosition.getY();

		cursorPosition.setLocation(point);
		if (SwingUtilities.isLeftMouseButton(e)) {
			if (selectionState == SelectionState.MOVING_NODES) {
				visualNetwork.moveSelectedNodes(diffX, diffY);

				nodeMoved = true;
			} else if (selectionState == SelectionState.SELECTING_NODES) {
				selection.setSize(selection.getWidth() + diffX,
						selection.getHeight() + diffY);
				visualNetwork.selectElementsInsideSelection(selection);
			}
			repaint();
		}

	}

	/**
	 * Invoked when a mouse button is pressed on a component and then dragged in
	 * the LINK state.
	 * 
	 * @param e
	 *            mouse event information.
	 */
	public void mouseDraggedLinkCreation(MouseEvent e) {

		if (newLink != null) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				newLink.setEndPoint(new Point2D.Double(zoom.screenToPanel(e
						.getX()), zoom.screenToPanel(e.getY())));
				repaint();
			}
		}

	}

	/**
	 * Invoked when a mouse button has been released on the component.
	 * 
	 * @param e
	 *            mouse event information.
	 */
	public void mouseReleased(MouseEvent e) {

		switch (editionState) {
		case SELECTION: {
			mouseReleasedSelection(e);
			break;
		}
		case LINK: {
			mouseReleasedLinkCreation(e);
			break;
		}
		default: {
			break;
		}
		}

	}

	// ESCA-JAVA0173: suppress warning unused for Enerjy validation
	/**
	 * Invoked when a mouse button has been released on the component in the
	 * SELECTION state.
	 * 
	 * @param e
	 *            mouse event information.
	 */

	public void mouseReleasedSelection(MouseEvent e) {

		selection.clearSelectionSquare();
		if (selectionState == SelectionState.MOVING_NODES) {
			if (nodeMoved) {
				movedNodes = visualNetwork.fillVisualNodesSelected();

				// visualNetwork.fillDifferencesNodesMovedInfo(movedNodes);

				cursorPosition.setLocation(zoom.screenToPanel(e.getX()),
						zoom.screenToPanel(e.getY()));

				MoveNodeEdit moveNodeEdit = new MoveNodeEdit(movedNodes);

				try {
					probNet.getPNESupport().announceEdit(moveNodeEdit);
					probNet.getPNESupport().doEdit(moveNodeEdit);
				} catch (ConstraintViolationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (CanNotDoEditException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (DoEditException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// probNet.getPNESupport().closeParenthesis();
				catch (NotEnoughMemoryException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (NonProjectablePotentialException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (WrongCriterionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				nodeMoved = false;
				adjustPanelDimension();
			}
		} else if (selectionState == SelectionState.DEFAULT) {

		}
		setSelectionState(SelectionState.DEFAULT);
		repaint();

	}

	/**
	 * Invoked when a mouse button has been released on the component in the
	 * LINK state.
	 * 
	 * @param e
	 *            mouse event information.
	 */
	public void mouseReleasedLinkCreation(MouseEvent e) {

		Graphics2D g = (Graphics2D) getGraphics();
		VisualNode newLinkDestination = null;
		Point2D.Double point = new Point2D.Double(zoom.screenToPanel(e.getX()),
				zoom.screenToPanel(e.getY()));

		if (newLink != null) {
			newLink = null;
			if (SwingUtilities.isLeftMouseButton(e)) {
				if ((newLinkDestination = visualNetwork.whatNodeInPosition(
						point, g)) != null) {
					if (!newLinkSource.equals(newLinkDestination)) {
						try {

							AddLinkEdit linkEdit = new AddLinkEdit(probNet,
									probNet.getVariable(newLinkSource
											.getProbNode().getName()),
									probNet.getVariable(newLinkDestination
											.getProbNode().getName()), true);

							probNet.getPNESupport().announceEdit(linkEdit);
							probNet.getPNESupport().doEdit(linkEdit);

						} catch (Exception ex) {
							JOptionPane
									.showMessageDialog(
											Utilities.getOwner(this),
											ex.getMessage(),
											stringResource
													.getString("ErrorWindow.Title.Label"),
											JOptionPane.ERROR_MESSAGE);

						}
						/*
						 * if (link != null) {
						 * undoManager.addEditAddLink(visualNetwork
						 * .getNetwork(), link); }
						 */
					}
				}
			}
		}
		repaint();

	}

	/**
	 * Invoked when the mouse button enters the component.
	 * 
	 * @param e
	 *            mouse event information.
	 */

	public void mouseEntered(MouseEvent e) {

	}

	/**
	 * Invoked when the mouse button exits the component.
	 * 
	 * @param e
	 *            mouse event information.
	 */

	public void mouseExited(MouseEvent e) {

	}

	/**
	 * Invoked when the mouse cursor has been moved onto a component but no
	 * buttons have been pushed.
	 * 
	 * @param e
	 *            mouse event information.
	 */

	public void mouseMoved(MouseEvent e) {

	}

	/**
	 * This method allows to an object to be registered as size listener.
	 * 
	 * @param l
	 *            size listener.
	 */
	public void addEditorPanelSizeListener(EditorPanelSizeListener l) {

		sizeListeners.add(l);

	}

	/**
	 * Changes the value of the zoom.
	 * 
	 * @param value
	 *            new zoom.
	 */
	public void setZoom(double value) {

		Dimension newDimension = null;
		Double dd = new Double(zoom.getZoom());
		Double dd1 = new Double(value);
		if (dd.compareTo(dd1) != 0) { // jlgozalo. 24/08 fix condition to !=
			zoom.setZoom(value);
			newDimension = new Dimension((int) Math.round(getMaxWidth()),
					(int) Math.round(getMaxHeight()));
			setPreferredSize(newDimension);
			setSize(newDimension);
			adjustPanelDimension();
			repaint();
		}

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
	 * Returns a limited in functionality UndoManager object. The original
	 * UndoManager object is not returned in order to avoid its methods aren't
	 * invoked by another object.
	 * 
	 * @return a limited in functionality UndoManager object.
	 */
	public UndoManagerSupport getUndoManager() {

		return probNet.getPNESupport().getUndoManager();
		// undoManager.getUndoManagerInfo();

	}

	/**
	 * This method performs a undo or redo operation.
	 * 
	 * @param undoOperation
	 *            if true, an undo must be performed; if false, a redo will be
	 *            performed.
	 * @throws CannotUndoException
	 *             if undo can't be performed.
	 * @throws CannotRedoException
	 *             if redo can't be performed.
	 */
	private void undoRedo(boolean undoOperation) throws CannotUndoException,
			CannotRedoException {

		visualNetwork.setSelectedAllObjects(false);
		if (undoOperation) {
			probNet.getPNESupport().undo();
			// undoManager.undo();
		} else {
			// undoManager.redo();
			probNet.getPNESupport().redo();
		}

		adjustPanelDimension();
		repaint();

	}

	/**
	 * This method performs a undo operation.
	 * 
	 * @throws CannotUndoException
	 *             if undo can't be performed.
	 */
	public void undo() throws CannotUndoException {

		undoRedo(true);

	}

	/**
	 * This method performs a redo operation.
	 * 
	 * @throws CannotRedoException
	 *             if redo can't be performed.
	 */
	public void redo() throws CannotRedoException {

		undoRedo(false);

	}

	/**
	 * Sets a new selection listener.
	 * 
	 * @param listener
	 *            listener to be set.
	 */
	public void addSelectionListener(SelectionListener listener) {

		visualNetwork.addSelectionListener(listener);

	}

	/**
	 * Sets a new popup menu factory.
	 * 
	 * @param newPopupMenuFactory
	 *            popup menu factory to be set.
	 */
	public void setPopupMenuFactory(PopupMenuFactory newPopupMenuFactory) {

		popupMenuFactory = newPopupMenuFactory;

	}

	/**
	 * Retrieves the popup menu that corresponds to the parameter.
	 * 
	 * @param popup
	 *            popup menu to be returned.
	 * @return the popup menu corresponding the the parameter.
	 */
	private JPopupMenu getPopupMenu(int popup) {

		return (popupMenuFactory != null) ? popupMenuFactory
				.getPopupMenu(popup) : null;

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
	public ArrayList<VisualNode> getSelectedNodes() {
		return visualNetwork.getSelectedNodes();
	}

	/**
	 * Selects or deselects all nodes of the network.
	 * 
	 * @param selected
	 *            new selection state.
	 */
	public void setSelectedAllNodes(boolean selected) {
		visualNetwork.setSelectedAllNodes(selected);
	}

	/**
	 * Selects or deselects all objects of the network.
	 * 
	 * @param selected
	 *            new selection state.
	 */
	public void setSelectedAllObjects(boolean selected) {
		visualNetwork.setSelectedAllObjects(selected);
	}

	/**
	 * This method shows a dialog box with the additionalProperties of a node.
	 * If some property has changed, insert a new undo point into the network
	 * undo manager.
	 * 
	 * @param selectedNode
	 */
	public void changeNodeProperties(VisualNode selectedNode) {
		if (requestNodePropertiesToUser2(Utilities.getOwner(this),
				selectedNode.getProbNode(), false)) {
			adjustPanelDimension();
			repaint();
			if (selectedNode.getInnerBox() instanceof FSVariableBox) {
				((FSVariableBox) selectedNode.getInnerBox())
						.recreateVisualStates(evidenceCases.size());
			}
			networkChanged = true;
			removeNodeEvidenceInAllCases(selectedNode.getProbNode());
		} else
			probNet.getPNESupport().undoAndDelete();
	}

	public void changeNodeProperties() {
		ArrayList<VisualNode> selectedNodes = visualNetwork.getSelectedNodes();
		if (selectedNodes.size() == 1) {
			changeNodeProperties(selectedNodes.get(0));
		}
	}

	/**
     * 
     */
	public void changePotential() {

		ArrayList<VisualNode> selectedNodes = visualNetwork.getSelectedNodes();

		ProbNode probNode = selectedNodes.get(0).getProbNode();
		/*
		 * Potential oldPotential = probNode.getPotentials().get(0);
		 * PotentialEditDialog dialog = new PotentialEditDialog(owner,
		 * oldPotential, newElement); Potential newPotential =
		 * dialog.getNewPotential(); if ( newPotential != null ) { new edit =
		 * new ChangeNodePotentialEdit(newPotential);//sets the potential in the
		 * probNode pNESupport.doedit //probnet PNESuport, inside panels
		 * PNESupports will be owned by the edit dialog adjustPanelDimension();
		 * repaint(); networkChanged = true; }
		 */

		if (requestPotentialValues(Utilities.getOwner(this), probNode, false)) {
			// if the user has selected the ok button when closing the dialog
			adjustPanelDimension();
			repaint();
			networkChanged = true;
			removeNodeEvidenceInAllCases(probNode);
		} else {
			probNet.getPNESupport().undoAndDelete();
		}
	}

	/**
	 * This method requests to the user the adittionalProperties of a node.
	 * 
	 * @param owner
	 *            owner window that shows the dialog box.
	 * @param probNode
	 *            object that contains the adittionalProperties of the node and
	 *            where changes will be saved.
	 * @param newNode
	 *            specifies if the node whose adittionalProperties are going to
	 *            be edited is new.
	 * @return true, if the user save the changes on probNode; otherwise, false.
	 */
	private boolean requestNodePropertiesToUser2(Window owner,
			ProbNode probNode, boolean newNode) {

		NodePropertiesDialog nodePropertiesDialog = new CommonNodePropertiesDialog(
				owner, probNode, newNode);
		return (nodePropertiesDialog.requestProperties() == NodePropertiesDialog.OK_BUTTON);
	}

	private boolean requestPotentialValues(Window owner, ProbNode probNode,
			boolean newNode) {
		potentialsDialog = new PotentialEditDialog(owner, probNode, newNode);
		return (potentialsDialog.requestValues()// to know if the user has
												// selected the ok button when
												// closing the dialog
		== NodePropertiesDialog.OK_BUTTON);
	}

	private boolean requestLinkRestrictionValues(Window owner, Link link) {

		linkRestrictionDialog = new LinkRestrictionEditDialog(owner, link);
		return (linkRestrictionDialog.requestValues() == NodePropertiesDialog.OK_BUTTON);

	}

	private boolean requestCostEffectiveness(Window owner,
			String suffixTypeAnalysis, boolean isProbabilistic) {
		costEffectivenessDialog = new CostEffectivenessDialog(owner);
		costEffectivenessDialog.showSimulationsNumberElements(isProbabilistic);
		return (costEffectivenessDialog.requestData(probNet.getName(),
				suffixTypeAnalysis) == CostEffectivenessDialog.OK_BUTTON);
	}

	/**
	 * This method shows a dialog box with the adittionalProperties of a link.
	 * If some property has changed, insert a new undo point into the network
	 * undo manager.
	 * 
	 * @param link
	 */
	public void changeLinkProperties(VisualLink link) {

		/*
		 * This method must be implemented to activate the possibility of
		 * editing the adittionalProperties of a link in future versions.
		 */

	}

	/**
	 * This method shows a dialog box with the adittionalProperties of the
	 * network. If some property has changed, insert a new undo point into the
	 * network undo manager.
	 */
	public void changeNetworkProperties() {
		// TODO be careful with local pNESupport and extern pNESupport
		if (!requestNetworkProperties(Utilities.getOwner(this), probNet, false)) {
			probNet.getPNESupport().undoAndDelete();
		}

	}

	/**
	 * This method requests to the user the adittionalProperties of a network.
	 * 
	 * @param owner
	 *            window that owns the dialog box.
	 * @param adittionalProperties
	 *            object that contains the adittionalProperties of the network
	 *            and where changes will be saved, if the user accepts the
	 *            changes.
	 * @param newNetwork
	 *            specifies if the network whose adittionalProperties are going
	 *            to be edited is new.
	 * @return true, if the user has made changes on the adittionalProperties;
	 *         otherwise, false.
	 */
	public static boolean requestNetworkProperties(Window owner,
			ProbNet probNet, boolean newNetwork) {

		NetworkPropertiesDialog dialogProperties = new NetworkPropertiesDialog(
				owner, probNet, newNetwork);
		return (dialogProperties.requestProperties() == NetworkPropertiesDialog.OK_BUTTON);

	}

	/**
	 * This method requests to the user a new value of zoom for the actual
	 * network.
	 * 
	 * @param owner
	 *            window that owns the dialog box.
	 */
	public void requestZoomToUser(Window owner) {

		SelectZoomDialog dialogZoom = new SelectZoomDialog(owner);

		if (dialogZoom.requestZoom(getZoom()) == SelectZoomDialog.OK_BUTTON) {
			setZoom(dialogZoom.getZoom());
		}

	}

	/**
	 * This method copies the selected nodes to the clipboard.
	 * 
	 * @param cut
	 *            if true, the nodes copied to the clipboard are also removed.
	 */
	public void exportToClipboard(boolean cut) {
		if (clipboardAssistant == null) {
			JOptionPane.showMessageDialog(Utilities.getOwner(this),
					stringResource.getString("ClipboardNotSet.Text.Label"),
					stringResource.getString("ErrorWindow.Title.Label"),
					JOptionPane.ERROR_MESSAGE);
		} else {
			ArrayList<ProbNode> selectedNodes = new ArrayList<ProbNode>();
			for (VisualNode visualNode : visualNetwork.getSelectedNodes()) {
				selectedNodes.add(visualNode.getProbNode());
			}

			ArrayList<Link> selectedLinks = new ArrayList<Link>();
			for (VisualLink visualLink : visualNetwork.getSelectedLinks()) {
				selectedLinks.add(visualLink.getLink());
			}
			SelectedContent copiedContent = new SelectedContent(selectedNodes,
					selectedLinks);

			if (!copiedContent.isEmpty()) {
				clipboardAssistant.copyToClipboard(copiedContent);
				if (cut) {
					removeSelectedObjects();
				}
			}
		}
	}

	/**
	 * This method imports the content from the clipboard and creates it in the
	 * network.
	 */
	public void pasteFromClipboard() {
		if (clipboardAssistant == null) {
			JOptionPane.showMessageDialog(Utilities.getOwner(this),
					stringResource.getString("ClipboardNotSet.Text.Label"),
					stringResource.getString("ErrorWindow.Title.Label"),
					JOptionPane.ERROR_MESSAGE);
		} else {
			if (clipboardAssistant.isThereDataStored()) {
				visualNetwork.setSelectedAllObjects(false);
				SelectedContent clipboardContent = clipboardAssistant.paste();

				PasteEdit pasteEdit = new PasteEdit(visualNetwork,
						clipboardContent);

				try {
					probNet.doEdit(pasteEdit);
					// Set the nodes and links we just pasted as selected
					SelectedContent pastedContent = pasteEdit
							.getPastedContent();
					for (ProbNode node : pastedContent.getNodes()) {
						visualNetwork.setSelectedNode(node.getName(), true);
					}
					for (Link link : pastedContent.getLinks()) {
						visualNetwork.setSelectedLink(link, true);
					}
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane
							.showMessageDialog(
									Utilities.getOwner(this),
									stringResource
											.getString("CannotPasteAllNodes.Text.Label"),
									stringResource
											.getString("ErrorWindow.Title.Label"),
									JOptionPane.WARNING_MESSAGE);
				}

				adjustPanelDimension();
				repaint();
			}
		}
	}

	/**
	 * This method says if there is data stored in the clipboard.
	 * 
	 * @return true if there is data stored in the clipboard; otherwise, false.
	 */
	public boolean isThereDataStored() {
		return (clipboardAssistant != null) ? clipboardAssistant
				.isThereDataStored() : false;
	}
	
	/**
	 * This method imposes a policy in a decision node.
	 */
	public void imposePolicyInNode() {		
		System.out.println("Pulsada la opción 'Imponer Política'");	//...Borrar
		VisualNode node = null;		
		ArrayList<VisualNode> selectedNode = visualNetwork.getSelectedNodes();
		if (selectedNode.size() == 1) {
			node = selectedNode.get(0);
			if (node.getProbNode().getNodeType() == NodeType.DECISION) {
				//TODO...Here must be the code for imposing the policy to the node...
				((VisualDecisionNode)node).setHasPolicy(true);
			}
		}
		setSelectedAllNodes(false);
		repaint();		
	}
	
	/**
	 * This method edits an imposed policy of a decision node.
	 */
	public void editNodePolicy() {		
		System.out.println("Pulsada la opción 'Editar Política'"); //...Borrar 
		setSelectedAllNodes(false);
		repaint();			
	}
	
	/**
	 * This method removes an imposed policy from a decision node.
	 */
	public void removePolicyFromNode() {		
		System.out.println("Pulsada la opción 'Eliminar Política'"); //...Borrar
		VisualNode node = null;		
		ArrayList<VisualNode> selectedNode = visualNetwork.getSelectedNodes();
		if (selectedNode.size() == 1) {
			node = selectedNode.get(0);
			if (node.getProbNode().getNodeType() == NodeType.DECISION) {
				//TODO...Here must be the code for removing the imposed policy from the node...
				((VisualDecisionNode)node).setHasPolicy(false);
			}
		}
		setSelectedAllNodes(false);
		repaint();				
	}
	
	/**
	 * This method shows the expected utility of a decision node.
	 */
	public void showExpectedUtilityOfNode() {		
		System.out.println("Pulsada la opción 'Ver Utilidad Esperada'"); //...Borrar
		setSelectedAllNodes(false);
		repaint();			
	}
	
	/**
	 * This method shows the optimal policy for a decision node.
	 */
	public void showOptimalPolicyOfNode() {		
		System.out.println("Pulsada la opción 'Ver Política Óptima'");	//...Borrar
		setSelectedAllNodes(false);
		repaint();			
	} 

	/**
	 * This method expands a node.
	 */
	public void expandNode() {
		VisualNode visualNode = null;
		ArrayList<VisualNode> selectedNodes = visualNetwork.getSelectedNodes();
		if (selectedNodes.size() > 0) {
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
		VisualNode visualNode = null;
		ArrayList<VisualNode> selectedNodes = visualNetwork.getSelectedNodes();
		if (selectedNodes.size() > 0) {
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
		propagationActive = isAutomaticPropagation();
		Graphics2D g = (Graphics2D) getGraphics();
		VisualNode node = null;
		ArrayList<VisualNode> selectedNode = visualNetwork.getSelectedNodes();
		if (selectedNode.size() == 1) {
			node = selectedNode.get(0);
			NodeAddFindingDialog nodeAddFinding = new NodeAddFindingDialog(
					Utilities.getOwner(this), node, g, this);
		}
		repaint();
		setSelectedAllNodes(false);
		networkPanel.getMainPanel().getExistingInferenceToolBar()
				.setCurrentEvidenceCaseName(currentCase);
		networkPanel.getMainPanel().getMainPanelMenuAssistant()
				.updateOptionsFindingsDependent(networkPanel);
	}

	/**
	 * This method removes findings from selected nodes.
	 */
	public void removeFinding() {
		propagationActive = isAutomaticPropagation();
		VisualNode node = null;		
		ArrayList<VisualNode> selectedNodes = visualNetwork.getSelectedNodes();
		for (int i=0; i < selectedNodes.size(); i++) {
			node = selectedNodes.get(i);
			Variable variable = node.getProbNode().getVariable();
			if (evidenceCases.get(currentCase).getFinding(variable) != null) {
				try {
					evidenceCases.get(currentCase).removeFinding(variable);
					node.setFindingInNode(false);
				} catch (NoFindingException exc) {
					JOptionPane.showMessageDialog(Utilities.getOwner(this), "ERROR\n" +
							stringResource.getString("ExceptionNoFinding.Text.Label") +
							"\n\n" + exc.getMessage(), 
							stringResource.getString("ExceptionNoFinding.Title.Label"),
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		if ((propagationActive) &&
				(networkPanel.getWorkingMode() == NetworkPanel.INFERENCE_WORKING_MODE)) {
			doPropagation(evidenceCases.get(currentCase), currentCase);
		}
		networkPanel.getMainPanel().getExistingInferenceToolBar(). 
				setCurrentEvidenceCaseName(currentCase);
		networkPanel.getMainPanel().getMainPanelMenuAssistant().
				updateOptionsFindingsDependent(networkPanel); 
		setSelectedAllNodes(false);
		repaint();
	}

	/**
	 * This method returns the current Evidence Case.
	 * 
	 * @return the current Evidence Case.
	 */
	public EvidenceCase getCurrentEvidenceCase() {
		return evidenceCases.get(currentCase);
	}

	/**
	 * This method returns the Evidence Case.
	 * 
	 * @param caseNumber
	 *            the number of the case to be returned.
	 * 
	 * @return the selected Evidence Case.
	 */
	public EvidenceCase getEvidenceCase(int caseNumber) {
		return evidenceCases.get(caseNumber);
	}
	
    /**
     * This method returns list of evidence cases
     * 
     * @return the list of Evidence Cases.
     */
    public ArrayList<EvidenceCase> getEvidence ()
    {
        return evidenceCases;
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
	 * @param currentCase
	 *            new value for the current evidence case.
	 */
	public void setCurrentCase(int currentCase) {
		this.currentCase = currentCase;
	}

	/**
	 * This method returns the number of Evidence Cases that the ArrayList is
	 * currently holding .
	 * 
	 * @return the number of Evidence Cases in the ArrayList.
	 */
	public int getNumberOfCases() {
		return evidenceCases.size();
	}
		
	/**
	 * This method returns a boolean indicating if the case number passed as
	 * parameter is currently compiled.
	 *            
	 * @param caseNumber
	 *            number of the evidence case.
	 *            
	 * @return the compilation state of the case.
	 */	
	public boolean getEvidenceCasesCompilationState(int caseNumber) {
		return evidenceCasesCompilationState.get(caseNumber);
	}

	/**
	 * This method sets which is the compilation state of the case.
	 * 
	 * @param caseNumber
	 *            number of the evidence case to be set.
	 * @param value
	 *            true if compiled; false otherwise.
	 */
	public void setEvidenceCasesCompilationState(int caseNumber, boolean value) {
		this.evidenceCasesCompilationState.set(caseNumber, value);
	}
	
    /**
     * This method sets the list of evidence cases
     * 
     */
    public void setEvidence (ArrayList<EvidenceCase> evidence)
    {
        this.evidenceCases = evidence;
        
        if(this.evidenceCases.isEmpty())
        {
        	this.evidenceCases.add(new EvidenceCase());
        }
        
        currentCase = 0;
        
        //Update visual info on evidence
        for(VisualNode node : visualNetwork.getAllNodes ()) 
        {
            node.setFindingInNode (false);
        }
        for(EvidenceCase evidenceCase: evidence)
        {
            for(Finding finding: evidenceCase.getFindings ()) 
            {
                for(VisualNode node : visualNetwork.getAllNodes ()) 
                {
                    if(node.getProbNode ().getVariable ().equals (finding.getVariable ()))
                    {
                        node.setFindingInNode (true);
                    }
                }
            }
        }
        
        //Update evidenceCasesCompilationState
        evidenceCasesCompilationState.clear ();
        for(int i=0; i < evidence.size (); ++i)
        {
            evidenceCasesCompilationState.add (false);
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
	 * @param automaticPropagation
	 *            new value of the propagation type.
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
	 * @param propagationActive
	 *            new value of the propagation status.
	 */
	public void setPropagationActive(boolean propagationActive) {
		this.propagationActive = propagationActive;
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
	 * This method changes the current expansion threshold.
	 * 
	 * @param expansionThreshold
	 *            new value of the expansion threshold.
	 */
	public void setExpansionThreshold(double expansionThreshold) {
		this.currentExpansionThreshold = expansionThreshold;
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
	 * This method updates the expansion state (expanded/contracted) of the
	 * nodes. It is used in transitions from edition to inference mode and vice
	 * versa, and also when the user modifies the current expansion threshold in
	 * the Inference tool bar
	 * 
	 * @param newWorkingMode
	 *            new value of the working mode.
	 */
	public void updateNodesExpansionState(int newWorkingMode) {
		if (newWorkingMode == NetworkPanel.EDITION_WORKING_MODE) {
			VisualNode visualNode = null;
			ArrayList<VisualNode> allNodes = visualNetwork.getAllNodes();
			if (allNodes.size() > 0) {
				for (int i = 0; i < allNodes.size(); i++) {
					visualNode = allNodes.get(i);
					if (visualNode.isExpanded()) {
						visualNode.setExpanded(false);
					}
					repaint();
				}
			}
		} else if (newWorkingMode == NetworkPanel.INFERENCE_WORKING_MODE) {
			VisualNode visualNode = null;
			ArrayList<VisualNode> allNodes = visualNetwork.getAllNodes();
			if (allNodes.size() > 0) {
				for (int i = 0; i < allNodes.size(); i++) {
					visualNode = allNodes.get(i);
					if (visualNode.getProbNode().getRelevance() >= getExpansionThreshold()) {
						visualNode.setExpanded(true);
					} else {
						visualNode.setExpanded(false);
					}
					repaint();
				}
			}
		}
	}

	/**
	 * This method updates the value of each state for each node in the network
	 * with the current individual probabilities.
	 */
	public void updateIndividualProbabilities() {
		// if some visualNode has a number of values different from the
		// number of evidence cases in memory, we need to recreate its 
		// visual states and consider that the network has been changed.
		ArrayList<VisualNode> allVisualNodes = visualNetwork.getAllNodes();			
		Iterator<VisualNode> iterator4 = allVisualNodes.iterator();
		while (iterator4.hasNext()) {
			VisualNode visualNode = iterator4.next();				
			InnerBox innerBox = visualNode.getInnerBox();
			VisualState visualState = null;
			if (innerBox instanceof FSVariableBox) {
				visualState = ((FSVariableBox)innerBox).getVisualState(0);
			} else if (innerBox instanceof ExpectedValueBox)  {
				visualState = ((ExpectedValueBox)innerBox).getVisualState();
			}
			if (visualState.getNumberOfValues() != evidenceCases.size()) {
				if (innerBox instanceof FSVariableBox) {
					((FSVariableBox)innerBox).recreateVisualStates(evidenceCases.size());
				} else if (innerBox instanceof ExpectedValueBox)  {
					((ExpectedValueBox)innerBox).recreateVisualState(evidenceCases.size());
				}
				networkChanged = true;
				for (int i=0; i<evidenceCases.size(); i++) {
					evidenceCasesCompilationState.set(i, false);
				}
			}
		}	
		if ((propagationActive) &&
				(networkPanel.getWorkingMode() == NetworkPanel.INFERENCE_WORKING_MODE)) {
			// if the network has been changed, propagation must be done in
			// each evidence case in memory. Otherwise, only propagation in
			// current case is needed.
			if (networkChanged) { 
				for (int i=0; i<evidenceCases.size(); i++) {
					doPropagation(getEvidenceCase(i), i);
				}
				updateNodesFindingState(evidenceCases.get(currentCase)); 
				networkChanged = false;
			} else {
				if (evidenceCasesCompilationState.get(currentCase) == false) {
					doPropagation(evidenceCases.get(currentCase), currentCase);
				}
			}
		} else if (evidenceCasesCompilationState.get(currentCase) == false) {
			//Even if propagation mode is manual, a propagation should be
			//done the first time that inference mode is selected
			doPropagation(evidenceCases.get(currentCase), currentCase);
		}
		repaint();
	}

	/**
	 * This method removes all the findings established in the current evidence
	 * case.
	 */
	public void removeAllFindings() {
		propagationActive = isAutomaticPropagation();
		ArrayList<VisualNode> visualNodes = visualNetwork.getAllNodes();
		for (int i = 0; i < visualNodes.size(); i++) {
			visualNodes.get(i).setFindingInNode(false);
		}
		ArrayList<Finding> findings = evidenceCases.get(currentCase)
				.getFindings();
		for (int i = 0; i < findings.size(); i++) {
			try {
				evidenceCases.get(currentCase).removeFinding(
						findings.get(i).getVariable());
				doPropagation(evidenceCases.get(currentCase), currentCase);
			} catch (NoFindingException exc) {
				JOptionPane
						.showMessageDialog(
								Utilities.getOwner(this),
								"ERROR\n"
										+ stringResource
												.getString("ExceptionNoFinding.Text.Label")
										+ "\n\n" + exc.getMessage(),
								stringResource
										.getString("ExceptionNoFinding.Title.Label"),
								JOptionPane.ERROR_MESSAGE);
			}
		}
		networkPanel.getMainPanel().getExistingInferenceToolBar()
				.setCurrentEvidenceCaseName(currentCase);
		setSelectedAllNodes(false);
		networkPanel.getMainPanel().getMainPanelMenuAssistant()
				.updateOptionsFindingsDependent(networkPanel);
	}
	
	/**
	 * This method removes the findings that a node could have in all
	 * the evidence cases in memory. It is invoked when a change takes place
	 * in properties or probabilities of a the node
	 * 
	 * @param node
	 *            the node in which to remove the findings.
	 */
	public void removeNodeEvidenceInAllCases(ProbNode node) {
		for (int i=0; i<evidenceCases.size(); i++) {
			ArrayList<Finding> findings = evidenceCases.get(i).getFindings();
			for (int j=0; j<findings.size(); j++) {
				try {
					if (node.getVariable() == (findings.get(j).getVariable())) {
						evidenceCases.get(i).removeFinding(findings.get(j).getVariable());
						if (isAutomaticPropagation() && (inferenceAlgorithm != null)) {
							doPropagation(evidenceCases.get(i), i);
						}
						if (i == currentCase) {
							ArrayList<VisualNode> visualNodes = visualNetwork.getAllNodes();
							for (int k=0; k<visualNodes.size(); k++) {
								if (visualNodes.get(k).getProbNode() == node) {
									visualNodes.get(k).setFindingInNode(false);	
								}
							}		
						}
					}
				} catch (NoFindingException exc) {
					JOptionPane.showMessageDialog(Utilities.getOwner(this), "ERROR\n" +  
							stringResource.getString("ExceptionNoFinding.Text.Label") +
							"\n\n" + exc.getMessage(), 
							stringResource.getString("ExceptionNoFinding.Title.Label"),
							JOptionPane.ERROR_MESSAGE);
				}
			}
			
		}
		setSelectedAllNodes(false);
		networkPanel.getMainPanel().getMainPanelMenuAssistant(). 
				updateOptionsFindingsDependent(networkPanel);
		repaint();
	}

	/**
	 * This method returns true if there are any finding in the current evidence
	 * case.
	 * 
	 * @return true if the current evidence case has at least one finding.
	 */
	public boolean areThereFindingsInCase() {
		boolean areFindings = false;
		ArrayList<Finding> findings = evidenceCases.get(currentCase)
				.getFindings();
		if (findings != null) {
			if (findings.size() > 0) {
				areFindings = true;
			}
		}
		return areFindings;
	}

	/**
	 * This method adds a new finding in the current evidence case
	 * 
	 * @param visualState
	 *            the visual state in which the finding is going to be set.
	 */
	public void setNewFinding(VisualState visualState) {
		propagationActive = isAutomaticPropagation();
		Variable variable = visualState.getVisualNode().getProbNode()
				.getVariable();
		if (evidenceCases.get(currentCase).getFinding(variable) != null) {
			if (evidenceCases.get(currentCase).getState(variable) == visualState
					.getStateNumber()) {
				try {
					evidenceCases.get(currentCase).removeFinding(variable);
					visualState.getVisualNode().setFindingInNode(false);
					evidenceCasesCompilationState.set(currentCase, false);
				} catch (NoFindingException exc) {
					JOptionPane
							.showMessageDialog(
									Utilities.getOwner(this),
									"ERROR\n"
											+ stringResource
													.getString("ExceptionNoFinding.Text.Label")
											+ "\n\n" + exc.getMessage(),
									stringResource
											.getString("ExceptionNoFinding.Title.Label"),
									JOptionPane.ERROR_MESSAGE);
				}
			} else {
				try {
					evidenceCases.get(currentCase).removeFinding(variable);
					Finding finding = new Finding(variable,
							visualState.getStateNumber());
					evidenceCases.get(currentCase).addFinding(finding);
					visualState.getVisualNode().setFindingInNode(true);
					evidenceCasesCompilationState.set(currentCase, false);
				} catch (NoFindingException exc) {
					JOptionPane
							.showMessageDialog(
									Utilities.getOwner(this),
									"ERROR\n"
											+ stringResource
													.getString("ExceptionNoFinding.Text.Label")
											+ "\n\n" + exc.getMessage(),
									stringResource
											.getString("ExceptionNoFinding.Title.Label"),
									JOptionPane.ERROR_MESSAGE);
				} catch (InvalidStateException exc) {
					JOptionPane
							.showMessageDialog(
									Utilities.getOwner(this),
									"ERROR\n"
											+ stringResource
													.getString("ExceptionInvalidState.Text.Label")
											+ "\n\n" + exc.getMessage(),
									stringResource
											.getString("ExceptionInvalidState.Title.Label"),
									JOptionPane.ERROR_MESSAGE);
				} catch (IncompatibleEvidenceException exc) {
					JOptionPane
							.showMessageDialog(
									Utilities.getOwner(this),
									"ERROR\n"
											+ stringResource
													.getString("ExceptionIncompatibleEvidence.Text.Label")
											+ "\n\n" + exc.getMessage(),
									stringResource
											.getString("ExceptionIncompatibleEvidence.Title.Label"),
									JOptionPane.ERROR_MESSAGE);
				} catch (Exception exc) {
					JOptionPane.showMessageDialog(Utilities.getOwner(this),
							"ERROR" + "\n\n" + exc.getMessage(), stringResource
									.getString("ExceptionGeneric.Title.Label"),
							JOptionPane.ERROR_MESSAGE);
				}
			}
		} else {
			Finding finding = new Finding(variable,
					visualState.getStateNumber());
			try {
				evidenceCases.get(currentCase).addFinding(finding);
				visualState.getVisualNode().setFindingInNode(true);
				evidenceCasesCompilationState.set(currentCase, false);
			} catch (InvalidStateException exc) {
				JOptionPane
						.showMessageDialog(
								Utilities.getOwner(this),
								"ERROR\n"
										+ stringResource
												.getString("ExceptionInvalidState.Text.Label")
										+ "\n\n" + exc.getMessage(),
								stringResource
										.getString("ExceptionInvalidState.Title.Label"),
								JOptionPane.ERROR_MESSAGE);
			} catch (IncompatibleEvidenceException exc) {
				JOptionPane
						.showMessageDialog(
								Utilities.getOwner(this),
								"ERROR\n"
										+ stringResource
												.getString("ExceptionIncompatibleEvidence.Text.Label")
										+ "\n\n" + exc.getMessage(),
								stringResource
										.getString("ExceptionIncompatibleEvidence.Title.Label"),
								JOptionPane.ERROR_MESSAGE);
			} catch (Exception exc) {
				JOptionPane.showMessageDialog(Utilities.getOwner(this), "ERROR"
						+ "\n\n" + exc.getMessage(), stringResource
						.getString("ExceptionGeneric.Title.Label"),
						JOptionPane.ERROR_MESSAGE);
			}
		}
		setSelectedAllNodes(false);
		networkPanel.getMainPanel().getExistingInferenceToolBar()
				.setCurrentEvidenceCaseName(currentCase);
		if ((propagationActive)
				&& (evidenceCasesCompilationState.get(currentCase) == false)
				&& (networkPanel.getWorkingMode() == NetworkPanel.INFERENCE_WORKING_MODE)) {
			doPropagation(evidenceCases.get(currentCase), currentCase);
		}
		networkPanel.getMainPanel().getMainPanelMenuAssistant()
				.updateOptionsFindingsDependent(networkPanel);
		repaint();

	}

	/**
	 * Returns the inference algorithm assigned to the panel.
	 * 
	 * @return the inference algorithm assigned to the panel.
	 */
	public InferenceAlgorithm getInferenceAlgorithm() {
		return inferenceAlgorithm;
	}

	/**
	 * Sets the inference algorithm assigned to the panel.
	 * 
	 * @param inferenceAlgorithm
	 *            the inference Algorithm to be assigned to the panel.
	 */
	public void setInferenceAlgorithm(InferenceAlgorithm inferenceAlgorithm) {
		this.inferenceAlgorithm = inferenceAlgorithm;
	}

	/**
	 * This method does the propagation of the evidence in the network
	 * 
	 * @param evidenceCase
	 *            the evidence case with which the propagation must be done.
	 * @param caseNumber
	 *            number of this evidence case.
	 */
	public void doPropagation(EvidenceCase evidenceCase, int caseNumber) {
		// ...PROVISIONAL...THIS SHOULD BE CHANGED WHEN EVALUATION OF INFLUENCE
		// ...DIAGRAMS IS COMPLETE
		// ...We obtain the type of the network. If it is a Bayesian Network, we
		// ...do the real propagation; if it is an Influence Diagram, we do a 
		// ...'fictitious propagation' for painting the nodes with dummy
		// ...information; otherwise, no propagation is done and a message
		// ...is shown.
		NetworkType networkType = probNet.getNetworkType();
		HashMap<Variable, Potential> individualProbabilities = null;
		boolean propagationSucceded = false;
		if (networkType instanceof BayesianNetworkType) {
			try {
		        // This will return null for InfluenceDiagrams until a suitable
		        // inference algorithm is implemented for them
		        inferenceAlgorithm = inferenceManager.getDefaultInferenceAlgorithm (probNet);
			    
				inferenceAlgorithm.setEvidence(evidenceCase);
				long start = System.currentTimeMillis();
				try
				{
				    individualProbabilities = inferenceAlgorithm.getIndividualProbabilities();
				}catch(NotEnoughMemoryException e)
				{
				    if(!approximateInferenceWarningGiven)
				    {
                        JOptionPane.showMessageDialog (Utilities.getOwner (this),
                                                       stringResource.getString ("NotEnoughMemoryForExactInference.Text"),
                                                       stringResource.getString ("NotEnoughMemoryForExactInference.Title"),
                                                       JOptionPane.WARNING_MESSAGE);
                        approximateInferenceWarningGiven = true;
				    }
				    
				    inferenceAlgorithm = inferenceManager.getDefaultApproximateAlgorithm(probNet);
				    inferenceAlgorithm.setEvidence(evidenceCase);
				    individualProbabilities = inferenceAlgorithm.getIndividualProbabilities();
				}
				long elapsedTimeMillis = System.currentTimeMillis() - start;
				System.out.println("Inference took " + elapsedTimeMillis
						+ " milliseconds.");

				if (individualProbabilities != null) {
					for (Variable variable : individualProbabilities.keySet()) {
						ArrayList<VisualNode> allVisualNodes = visualNetwork
								.getAllNodes();
						Potential potential = individualProbabilities
								.get(variable);
						if (potential.getPotentialType() == PotentialType.TABLE) {
							TablePotential tablePotential = (TablePotential) potential;
							if (tablePotential.getNumVariables() == 1) {
								double[] values = tablePotential.getValues();
								Iterator<VisualNode> iterator2 = allVisualNodes
										.iterator();
								while (iterator2.hasNext()) {
									VisualNode visualNode = iterator2.next();
									if (variable.getName().equals(
											visualNode.getProbNode().getName())) {
										if ((visualNode.getInnerBox()) instanceof FSVariableBox) {
											FSVariableBox innerBox = (FSVariableBox) visualNode
													.getInnerBox();
											for (int i = 0; i < innerBox
													.getNumStates(); i++) {
												VisualState visualState = innerBox
														.getVisualState(i);
												visualState.setStateValue(
														caseNumber, values[i]);
											}
										}
										visualNode.setFindingInNode(false);
									}
								}
								// PROVISIONAL2: Currently the propagation
								// algorithm is returning a TablePotential
								// with 0 variables when the node has a Uniform
								// relation
							} else if (tablePotential.getNumVariables() == 0) {
								Iterator<VisualNode> iterator2 = allVisualNodes
										.iterator();
								while (iterator2.hasNext()) {
									VisualNode visualNode = iterator2.next();
									if (variable.getName().equals(
											visualNode.getProbNode().getName())) {
										if ((visualNode.getInnerBox()) instanceof FSVariableBox) {
											FSVariableBox innerBox = (FSVariableBox) visualNode
													.getInnerBox();
											for (int i = 0; i < innerBox
													.getNumStates(); i++) {
												VisualState visualState = innerBox
														.getVisualState(i);
												visualState
														.setStateValue(
																caseNumber,
																(1.0 / innerBox
																		.getNumStates()));
											}
										}
										visualNode.setFindingInNode(false);
									}
								}
								// END OF
								// PROVISIONAL2............................
							} else {
								JOptionPane
										.showMessageDialog(
												Utilities.getOwner(this),
												"ERROR\n"
														+ "Table Potential of "
														+ variable.getName()
														+ " has "
														+ tablePotential
																.getNumVariables()
														+ " variables.\n It cannot be treated by now",
												"Error",
												JOptionPane.ERROR_MESSAGE);
							}
						}
					}
					updateNodesFindingState(evidenceCase);
					propagationSucceded = true;
				}
				repaint();
            } catch (Exception e) {
				e.printStackTrace();
			}

		} else if (networkType instanceof InfluenceDiagramType) {
			// Set the values of the visualStates of nodes without finding
			ArrayList<VisualNode> allVisualNodes2 = visualNetwork.getAllNodes();
			Iterator<VisualNode> iterator1 = allVisualNodes2.iterator();
			while (iterator1.hasNext()) {
				VisualNode visualNode = iterator1.next();
				InnerBox innerBox = visualNode.getInnerBox();
				if (innerBox instanceof FSVariableBox) {// si es un nodo
														// aleatorio o de
														// decisión
					Double aux1 = 0.0;
					Double aux2 = 0.3;
					Double value = 0.0;
					for (int i = 0; i < innerBox.getNumStates() - 1; i++) {
						if ((aux1 + aux2) <= 1.0) {
							value = aux2;
						} else {
							value = (1.0 - aux1);
						}
						aux1 += value;
						VisualState visualState = ((FSVariableBox) innerBox)
								.getVisualState(i);
						visualState.setStateValue(caseNumber, value);
					}
					VisualState visualState = ((FSVariableBox) innerBox)
							.getVisualState(innerBox.getNumStates() - 1);
					visualState.setStateValue(caseNumber, (1.0 - aux1));
				} else { // si es un nodo de utilidad
					VisualState visualState = ((ExpectedValueBox) innerBox)
							.getVisualState();
					visualState.setStateValue(caseNumber, 111.55);
					((ExpectedValueBox) innerBox).setMinUtilityRange(100.0);
					((ExpectedValueBox) innerBox).setMaxUtilityRange(112.0);
				}
				visualNode.setFindingInNode(false);
			}
			// Set the values of the visualStates of nodes with finding
			ArrayList<Finding> findingsInEvidenceCase = evidenceCase
					.getFindings();
			Iterator<Finding> iterator3 = findingsInEvidenceCase.iterator();
			while (iterator3.hasNext()) {
				Finding finding = iterator3.next();
				Variable variable = finding.getVariable();
				ArrayList<VisualNode> allVisualNodes = visualNetwork
						.getAllNodes();
				Iterator<VisualNode> iterator4 = allVisualNodes.iterator();
				while (iterator4.hasNext()) {
					VisualNode visualNode = iterator4.next();
					if (variable.getName().equals(
							visualNode.getProbNode().getName())) { // si el nodo
																	// tiene
																	// hallazgo
						if ((visualNode.getInnerBox()) instanceof FSVariableBox) {
							FSVariableBox innerBox = (FSVariableBox) visualNode
									.getInnerBox();
							for (int i = 0; i < innerBox.getNumStates(); i++) {
								VisualState visualState = innerBox
										.getVisualState(i);
								if (visualState.getStateNumber() == finding
										.getStateIndex()) {
									visualState.setStateValue(caseNumber, 1.0);
								} else {
									visualState.setStateValue(caseNumber, 0.0);
								}
							}
						}
						visualNode.setFindingInNode(true);
					}
				}
			}
			updateNodesFindingState(evidenceCase);
			propagationSucceded = true;
			repaint();
		} else {
			try {
		        // This will return null for InfluenceDiagrams until a suitable
		        // inference algorithm is implemented for them
		        inferenceAlgorithm = inferenceManager.getDefaultInferenceAlgorithm (probNet);
				inferenceAlgorithm.setEvidence(evidenceCase);
				individualProbabilities = inferenceAlgorithm.getIndividualProbabilities();
			} catch (Exception e) {
				JOptionPane
						.showMessageDialog(
								null,
								"ERROR\n"
										+ stringResource
												.getString("NoPropagationCanBeDoneMessage1.Text.Label")
										+ "\n"
										+ stringResource
												.getString("NoPropagationCanBeDoneMessage2.Text.Label")
										+ "\n\n" + networkType,
								stringResource
										.getString("NoPropagationCanBeDoneMessage.Title.Label"),
								JOptionPane.ERROR_MESSAGE);
			}
		}
		evidenceCasesCompilationState.set(caseNumber, propagationSucceded);
		// ...END OF PROVISIONAL...THIS SHOULD BE CHANGED WHEN EVALUATION OF
		// ...INFLUENCE DIAGRAMS IS COMPLETE
	}

	/**
	 * This method updates the "finding state" of each node
	 * 
	 * @param evidenceCase
	 *            the evidence case with which the update must be done.
	 */
	public void updateNodesFindingState(EvidenceCase evidenceCase) {
		Iterator<VisualNode> iterator1 = visualNetwork.getAllNodes().iterator();
		while (iterator1.hasNext()) {
			VisualNode visualNode = iterator1.next();
			visualNode.setFindingInNode(false);
		}
		ArrayList<Finding> findingsInEvidenceCase = evidenceCase.getFindings();
		Iterator<Finding> iterator2 = findingsInEvidenceCase.iterator();
		while (iterator2.hasNext()) {
			Finding finding = iterator2.next();
			Variable variable = finding.getVariable();
			Iterator<VisualNode> iterator3 = visualNetwork.getAllNodes()
					.iterator();
			while (iterator3.hasNext()) {
				VisualNode visualNode = iterator3.next();
				if (variable.getName().equals(
						visualNode.getProbNode().getName())) {
					visualNode.setFindingInNode(true);
				}
			}
		}
		repaint();
	}

	public void showCostEffectivenessDeterministicDialog() {

		/**
		 * if (requestCostEffectiveness(Utilities.getOwner(this),"cea", false))
		 * { ArrayList<Intervention> interventions = new
		 * ArrayList<Intervention>(); final String RESOURCE_EXCEL_TEMPLATE =
		 * "/openmarkov/gui/resources/" +
		 * "template/cost-effectiveness-plot-empty.xls";
		 * 
		 * try { int numSimulations = 0;
		 * 
		 * CostEffectivenessAnalysis costEffectivenessAnalysis = new
		 * CostEffectivenessAnalysis(probNet,
		 * costEffectivenessDialog.getInitialAge(),
		 * costEffectivenessDialog.getFinalAge(),
		 * costEffectivenessDialog.getDiscount(), numSimulations);
		 * 
		 * interventions =
		 * costEffectivenessAnalysis.getAllInterventions(numSimulations)[0];
		 * Intervention[] frontier = costEffectivenessAnalysis.
		 * getFrontierIntervention( interventions.toArray( new
		 * Intervention[interventions.size()]));
		 * 
		 * try {
		 * 
		 * ExcelIO excelTarget = ExcelIO.getUniqueInstance();
		 * excelTarget.setInitialData( costEffectivenessDialog.getInitialAge(),
		 * costEffectivenessDialog.getFinalAge(),
		 * costEffectivenessDialog.getDiscount());
		 * excelTarget.useTemplate(RESOURCE_EXCEL_TEMPLATE);
		 * excelTarget.writeExcelReportOptimalInterventions
		 * (interventions,frontier,
		 * costEffectivenessDialog.getOutputFileName());
		 * JOptionPane.showMessageDialog( Utilities.getOwner(this),
		 * "Report has been created", //stringResource
		 * //.getString("ErrorWindow.Title.Label"),
		 * "Cost effectiveness Analysis", JOptionPane.INFORMATION_MESSAGE);
		 * 
		 * Runtime.getRuntime().exec( "rundll32 SHELL32.DLL, ShellExec_RunDLL "+
		 * excelTarget.getPathFile());
		 * 
		 * 
		 * } catch (IOException e) { JOptionPane.showMessageDialog(
		 * Utilities.getOwner(this), e.getMessage(), stringResource
		 * .getString("ErrorWindow.Title.Label"), JOptionPane.ERROR_MESSAGE); }
		 * 
		 * 
		 * } catch (NotEnoughMemoryException e1) {
		 * JOptionPane.showMessageDialog( Utilities.getOwner(this),
		 * e1.getMessage(), stringResource
		 * .getString("ErrorWindow.Title.Label"), JOptionPane.ERROR_MESSAGE); }
		 * catch (NormalizeNullVectorException e1) { // TODO Auto-generated
		 * catch block e1.printStackTrace(); } catch (DoEditException e1) { //
		 * TODO Auto-generated catch block e1.printStackTrace(); } catch
		 * (ConstraintViolationException e1) { // TODO Auto-generated catch
		 * block e1.printStackTrace(); } catch (CanNotDoEditException e1) { //
		 * TODO Auto-generated catch block e1.printStackTrace(); } catch
		 * (NotEvaluableNetworkException e1) { // TODO Auto-generated catch
		 * block e1.printStackTrace(); } catch (NonProjectablePotentialException
		 * e1) { // TODO Auto-generated catch block e1.printStackTrace(); }
		 * catch (WrongCriterionException e1) { // TODO Auto-generated catch
		 * block e1.printStackTrace(); } catch (IncompatibleEvidenceException
		 * exc) { JOptionPane.showMessageDialog(Utilities.getOwner(this),
		 * "ERROR\n" +
		 * stringResource.getString("ExceptionIncompatibleEvidence.Text.Label")
		 * + "\n\n" + exc.getMessage(), stringResource.getString(
		 * "ExceptionIncompatibleEvidence.Title.Label"),
		 * JOptionPane.ERROR_MESSAGE); } catch (InvalidStateException exc) {
		 * JOptionPane.showMessageDialog(Utilities.getOwner(this), "ERROR\n" +
		 * stringResource.getString("ExceptionInvalidState.Text.Label") + "\n\n"
		 * + exc.getMessage(),
		 * stringResource.getString("ExceptionInvalidState.Title.Label"),
		 * JOptionPane.ERROR_MESSAGE); }
		 * 
		 * }
		 */
	}

	public void showSensitivityAnalysisCostEffectivenessDialog() {
		/*
		 * ArrayList<Variable> decisionsWithoutPolicy = null; try {
		 * decisionsWithoutPolicy =
		 * VarEliminationSMM.getDecisionVariablesWithoutPolicies(this.probNet);
		 * } catch (NotEvaluableNetworkException e1) { // TODO Auto-generated
		 * catch block e1.printStackTrace(); } if
		 * (decisionsWithoutPolicy.size()!=1){ JOptionPane.showMessageDialog(
		 * Utilities.getOwner(this),
		 * "Sensitivity analysis requires all the decisions except one have a policy assigned by the user. Please, check the decisions in the model."
		 * , stringResource .getString("ErrorWindow.Title.Label"),
		 * JOptionPane.ERROR_MESSAGE); }else{ if
		 * (requestCostEffectiveness(Utilities.getOwner(this),"sa", true)) {
		 * 
		 * //Perform a simulation with the reference values
		 * ArrayList<Intervention> interventionsDeterministic = null;
		 * 
		 * CostEffectivenessAnalysis costEffectivenessAnalysisDeterministic; try
		 * { costEffectivenessAnalysisDeterministic = new
		 * CostEffectivenessAnalysis(probNet,
		 * costEffectivenessDialog.getInitialAge(),
		 * costEffectivenessDialog.getFinalAge(),
		 * costEffectivenessDialog.getDiscount(), 0); interventionsDeterministic
		 * = costEffectivenessAnalysisDeterministic.getAllInterventions(0)[0]; }
		 * catch (NotEnoughMemoryException e2) { JOptionPane.showMessageDialog(
		 * Utilities.getOwner(this), e2.getMessage(), stringResource
		 * .getString("ErrorWindow.Title.Label"), JOptionPane.ERROR_MESSAGE); }
		 * catch (NormalizeNullVectorException e2) { e2.printStackTrace(); }
		 * catch (DoEditException e2) { e2.printStackTrace(); } catch
		 * (ConstraintViolationException e2) { e2.printStackTrace(); } catch
		 * (CanNotDoEditException e2) { e2.printStackTrace(); } catch
		 * (NotEvaluableNetworkException e2) { e2.printStackTrace(); } catch
		 * (NonProjectablePotentialException e2) { e2.printStackTrace(); } catch
		 * (WrongCriterionException e2) { e2.printStackTrace(); } catch
		 * (IncompatibleEvidenceException exc) {
		 * JOptionPane.showMessageDialog(Utilities.getOwner(this), "ERROR\n" +
		 * stringResource.getString("ExceptionIncompatibleEvidence.Text.Label")
		 * + "\n\n" + exc.getMessage(),
		 * stringResource.getString("ExceptionIncompatibleEvidence.Title.Label"
		 * ), JOptionPane.ERROR_MESSAGE); } catch (InvalidStateException exc) {
		 * JOptionPane.showMessageDialog(Utilities.getOwner(this), "ERROR\n" +
		 * stringResource.getString("ExceptionInvalidState.Text.Label") + "\n\n"
		 * + exc.getMessage(),
		 * stringResource.getString("ExceptionInvalidState.Title.Label"),
		 * JOptionPane.ERROR_MESSAGE); }
		 * 
		 * ArrayList<Intervention>[] interventionsProbabilistic = null; final
		 * String RESOURCE_EXCEL_TEMPLATE_2_STATES =
		 * "/openmarkov/gui/resources/" + "template/sa-plot-2-states-empty.xls";
		 * final String RESOURCE_EXCEL_TEMPLATE_3_STATES =
		 * "/openmarkov/gui/resources/" + "template/sa-plot-3-states-empty.xls";
		 * 
		 * CostEffectivenessAnalysis costEffectivenessAnalysis = null; try {
		 * costEffectivenessAnalysis = new CostEffectivenessAnalysis(probNet,
		 * costEffectivenessDialog.getInitialAge(),
		 * costEffectivenessDialog.getFinalAge(),
		 * costEffectivenessDialog.getDiscount(),
		 * costEffectivenessDialog.getSimulationsNumber()); int
		 * numSimulationsInEachThread = 20; interventionsProbabilistic =
		 * costEffectivenessAnalysis
		 * .getAllInterventionsWithThreads(costEffectivenessDialog
		 * .getSimulationsNumber(),numSimulationsInEachThread);
		 * //interventionsProbabilistic =
		 * costEffectivenessAnalysis.getAllInterventions
		 * (costEffectivenessDialog.getSimulationsNumber()); } catch
		 * (NotEnoughMemoryException e1) { JOptionPane.showMessageDialog(
		 * Utilities.getOwner(this), e1.getMessage(), stringResource
		 * .getString("ErrorWindow.Title.Label"), JOptionPane.ERROR_MESSAGE); }
		 * catch (NormalizeNullVectorException e1) { e1.printStackTrace(); }
		 * catch (DoEditException e1) { e1.printStackTrace(); } catch
		 * (ConstraintViolationException e1) { e1.printStackTrace(); } catch
		 * (CanNotDoEditException e1) { e1.printStackTrace(); } catch
		 * (NotEvaluableNetworkException e1) { e1.printStackTrace(); } catch
		 * (NonProjectablePotentialException e1) { e1.printStackTrace(); } catch
		 * (WrongCriterionException e1) { e1.printStackTrace(); }
		 * 
		 * Variable decision = decisionsWithoutPolicy.get(0); int
		 * numStatesDecisionToAnalyze = decision.getNumStates();
		 * ExcelSensitivityAnalysis excelTarget =
		 * ExcelSensitivityAnalysis.getUniqueInstance();
		 * excelTarget.setInitialData
		 * (costEffectivenessDialog.getSimulationsNumber(),
		 * decisionsWithoutPolicy.get(0)); try {
		 * excelTarget.useTemplate(numStatesDecisionToAnalyze
		 * ,RESOURCE_EXCEL_TEMPLATE_2_STATES,RESOURCE_EXCEL_TEMPLATE_3_STATES);
		 * } catch (IOException e) { JOptionPane.showMessageDialog(
		 * Utilities.getOwner(this), e.getMessage(), stringResource
		 * .getString("ErrorWindow.Title.Label"), JOptionPane.ERROR_MESSAGE); }
		 * try { excelTarget.writeExcelReportSensitivityAnalysis(
		 * interventionsDeterministic,interventionsProbabilistic,
		 * costEffectivenessDialog.getOutputFileName()); } catch (IOException e)
		 * { JOptionPane.showMessageDialog( Utilities.getOwner(this),
		 * e.getMessage(), stringResource .getString("ErrorWindow.Title.Label"),
		 * JOptionPane.ERROR_MESSAGE); } JOptionPane.showMessageDialog(
		 * Utilities.getOwner(this), "Report has been created",
		 * "Cost effectiveness Analysis", JOptionPane.INFORMATION_MESSAGE); }
		 * 
		 * }
		 */
	}

	/**
	 * This method creates a new evidence case
	 */
	public void createNewEvidenceCase() {
		try {
			propagationActive = isAutomaticPropagation();
			EvidenceCase newEvidenceCase = new EvidenceCase();
			EvidenceCase currentEvidenceCase = getCurrentEvidenceCase();
			ArrayList<Finding> currentFindings = currentEvidenceCase
					.getFindings();
			for (int i = 0; i < currentFindings.size(); i++) {
				newEvidenceCase.addFinding(currentFindings.get(i));
			}
			evidenceCases.add(newEvidenceCase);
			currentCase = (evidenceCases.size() - 1);
			evidenceCasesCompilationState.add(currentCase, false);
			updateAllVisualStates("new", currentCase);
			networkPanel.getMainPanel().getExistingInferenceToolBar().
					setCurrentEvidenceCaseName(currentCase); 
			setSelectedAllNodes(false);
			doPropagation(evidenceCases.get(currentCase), currentCase); 
		} catch (InvalidStateException exc) {
			JOptionPane
					.showMessageDialog(
							Utilities.getOwner(this),
							"ERROR\n"
									+ stringResource
											.getString("ExceptionInvalidState.Text.Label")
									+ "\n\n" + exc.getMessage(),
							stringResource
									.getString("ExceptionInvalidState.Title.Label"),
							JOptionPane.ERROR_MESSAGE);
		} catch (IncompatibleEvidenceException exc) {
			JOptionPane
					.showMessageDialog(
							Utilities.getOwner(this),
							"ERROR\n"
									+ stringResource
											.getString("ExceptionIncompatibleEvidence.Text.Label")
									+ "\n\n" + exc.getMessage(),
							stringResource
									.getString("ExceptionIncompatibleEvidence.Title.Label"),
							JOptionPane.ERROR_MESSAGE);
		} catch (Exception exc) {
			JOptionPane.showMessageDialog(Utilities.getOwner(this), "ERROR"
					+ "\n\n" + exc.getMessage(),
					stringResource.getString("ExceptionGeneric.Title.Label"),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * This method makes the first evidence case to be the current
	 */
	public void goToFirstEvidenceCase() {
		currentCase = 0;
		updateAllVisualStates("", currentCase);
		networkPanel.getMainPanel().getExistingInferenceToolBar().
				setCurrentEvidenceCaseName(currentCase);
		setSelectedAllNodes(false);
		if ((propagationActive) &&
				(evidenceCasesCompilationState.get(currentCase) == false) &&
				(networkPanel.getWorkingMode() == NetworkPanel.INFERENCE_WORKING_MODE)) {
			doPropagation(evidenceCases.get(currentCase), currentCase);
		} else {
			updateNodesFindingState(evidenceCases.get(currentCase));
		}
	}

	/**
	 * This method makes the previous evidence case to be the current
	 */
	public void goToPreviousEvidenceCase() {
		if (currentCase > 0) {
			currentCase--;
			updateAllVisualStates("", currentCase);
			networkPanel.getMainPanel().getExistingInferenceToolBar().
					setCurrentEvidenceCaseName(currentCase);
			setSelectedAllNodes(false);
			if ((propagationActive) &&
					(evidenceCasesCompilationState.get(currentCase) == false) &&
					(networkPanel.getWorkingMode() == NetworkPanel.INFERENCE_WORKING_MODE)) {
				doPropagation(evidenceCases.get(currentCase), currentCase);
			} else {
				updateNodesFindingState(evidenceCases.get(currentCase));
			}
		} else {
			JOptionPane.showMessageDialog(Utilities.getOwner(this), "ERROR\n" +
					stringResource.getString("NoPreviousEvidenceCaseMessage.Text.Label"), 
					stringResource.getString("NoPreviousEvidenceCaseMessage.Title.Label"),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * This method makes the next evidence case to be the current
	 */
	public void goToNextEvidenceCase() {
		if (currentCase < (evidenceCases.size() - 1)) {
			currentCase++;
			updateAllVisualStates("", currentCase);
			networkPanel.getMainPanel().getExistingInferenceToolBar().
					setCurrentEvidenceCaseName(currentCase); 
			setSelectedAllNodes(false);
			if ((propagationActive) &&
					(evidenceCasesCompilationState.get(currentCase) == false) &&
					(networkPanel.getWorkingMode() == NetworkPanel.INFERENCE_WORKING_MODE)) {
				doPropagation(evidenceCases.get(currentCase), currentCase);
			} else {
				updateNodesFindingState(evidenceCases.get(currentCase));
			}
		} else {
			JOptionPane.showMessageDialog(Utilities.getOwner(this), "ERROR\n" + 
					stringResource.getString("NoNextEvidenceCaseMessage.Text.Label"), 
					stringResource.getString("NoNextEvidenceCaseMessage.Title.Label"),
					JOptionPane.ERROR_MESSAGE);
		}
	}	

	/**
	 * This method makes the last evidence case to be the current
	 */
	public void goToLastEvidenceCase() {
		currentCase = (evidenceCases.size() - 1);
		updateAllVisualStates("", currentCase);
		networkPanel.getMainPanel().getExistingInferenceToolBar().
				setCurrentEvidenceCaseName(currentCase);
		setSelectedAllNodes(false);
		if ((propagationActive) &&
				(evidenceCasesCompilationState.get(currentCase) == false) &&
				(networkPanel.getWorkingMode() == NetworkPanel.INFERENCE_WORKING_MODE)) {
			doPropagation(evidenceCases.get(currentCase), currentCase);
		} else {
			updateNodesFindingState(evidenceCases.get(currentCase));
		}
	}

	/**
	 * This method clears out all the evidence cases. It returns to an
	 * 'initial state' in which there is only an initial evidence case
	 * with no findings (corresponding to prior probabilities)
	 */
	public void clearOutAllEvidenceCases() {
		propagationActive = isAutomaticPropagation(); 
		evidenceCases.clear();
		evidenceCasesCompilationState.clear();
		EvidenceCase newEvidenceCase = new EvidenceCase();
		evidenceCases.add(newEvidenceCase);
		currentCase = 0;
		evidenceCasesCompilationState.add(currentCase, false);
		updateAllVisualStates("clear", currentCase);
		networkPanel.getMainPanel().getExistingInferenceToolBar().
				setCurrentEvidenceCaseName(currentCase); 
		setSelectedAllNodes(false);
		doPropagation(evidenceCases.get(currentCase), currentCase);
	}
	
	/**
	 * This method updates all visual states of all visual nodes when it is
	 * needed for a navigation operation among the existing evidence cases, a
	 * creation of a new case or when all cases are cleared out.
	 * 
	 * @param option
	 *            the specific operation to be done over the visual states.
	 */
	public void updateAllVisualStates(String option, int caseNumber) {
		ArrayList<VisualNode> allVisualNodes = visualNetwork.getAllNodes();
		Iterator<VisualNode> iterator = allVisualNodes.iterator();
		while (iterator.hasNext()) {
			VisualNode visualNode = iterator.next();
			InnerBox innerBox = visualNode.getInnerBox();
			VisualState visualState = null;
			for (int i = 0; i < innerBox.getNumStates(); i++) {
				if (innerBox instanceof FSVariableBox) {
					visualState = ((FSVariableBox) innerBox).getVisualState(i);
				} else if (innerBox instanceof ExpectedValueBox) {
					visualState = ((ExpectedValueBox) innerBox)
							.getVisualState();
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
	 * @param mainPanelMenuAssistant
	 *            the menu assistant associated to the main panel.
	 */
	public void propagateEvidence(MainPanelMenuAssistant mainPanelMenuAssistant) {
		setPropagationActive(true);
		if (networkPanel.getWorkingMode() == NetworkPanel.INFERENCE_WORKING_MODE) {
			for (int i=0; i<getNumberOfCases(); i++) { 
				if (evidenceCasesCompilationState.get(i) == false) {
					doPropagation(getEvidenceCase(i), i);
				}
			}
			setSelectedAllNodes(false);
			updateAllVisualStates("", currentCase); 
			networkPanel.getMainPanel().getExistingInferenceToolBar().
					setCurrentEvidenceCaseName(currentCase); 
			updateNodesFindingState(evidenceCases.get(currentCase)); 
		}
		mainPanelMenuAssistant
				.updateOptionsEvidenceCasesNavigation(networkPanel);
		mainPanelMenuAssistant
				.updateOptionsPropagationTypeDependent(networkPanel);
		mainPanelMenuAssistant.updateOptionsFindingsDependent(networkPanel);
	}

	/**
	 * This method sets the inference options for this panel.
	 */
	public void setInferenceOptions() {
		OptionsInferenceDialog optionsInferenceDialog = new OptionsInferenceDialog(
				Utilities.getOwner(this), this, networkPanel.getMainPanel()
						.getExistingInferenceToolBar());
	}

	/**
	 * Removes selected objects
	 */
	public void removeSelectedObjects() {
		RemoveSelectedEdit cutEdit = new RemoveSelectedEdit(visualNetwork);
		visualNetwork.setSelectedAllObjects(false);

		try {
			probNet.doEdit(cutEdit);
			propagationActive = isAutomaticPropagation();
			networkChanged = true;
			repaint();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(Utilities.getOwner(this),
					e.getMessage(),
					stringResource.getString("ErrorWindow.Title.Label"),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/***
	 * Initializes the link restriction potential of a link
	 */
	public void enableLinkRestriction() {

		ArrayList<VisualLink> links = visualNetwork.getSelectedLinks();
		if (!links.isEmpty()) {
			Link link = links.get(0).getLink();
			try {
				if (!link.hasRestrictions()) {
					link.initializesRestrictionsPotential();
				}
				if (!requestLinkRestrictionValues(Utilities.getOwner(this),
						link)) {
					probNet.getPNESupport().undoAndDelete();
				}

			} catch (NotEnoughMemoryException e) {
				JOptionPane.showMessageDialog(Utilities.getOwner(this),
						e.getMessage(),
						stringResource.getString("ErrorWindow.Title.Label"),
						JOptionPane.ERROR_MESSAGE);
			}
			repaint();
		}
	}

	/***
	 * Resets the link restriction potential of a link
	 */
	public void disableLinkRestriction() {

		ArrayList<VisualLink> links = visualNetwork.getSelectedLinks();
		if (!links.isEmpty()) {
			Link link = links.get(0).getLink();
			try {
				link.resetRestrictionsPotential();
			} catch (NotEnoughMemoryException e) {
				JOptionPane.showMessageDialog(Utilities.getOwner(this),
						e.getMessage(),
						stringResource.getString("ErrorWindow.Title.Label"),
						JOptionPane.ERROR_MESSAGE);
			}
			repaint();
		}
	}
	
	/**
	 * Sets a new visualNetwork.
	 * @param visualNetwork
	 */
	public void setVisualNetwork(VisualNetwork visualNetwork)
	{
	    this.visualNetwork = visualNetwork;
	}

}
