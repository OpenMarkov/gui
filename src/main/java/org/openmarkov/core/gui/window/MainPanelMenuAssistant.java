/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.window;


import java.util.ArrayList;

import javax.swing.event.UndoableEditEvent;

import org.openmarkov.core.action.PNESupport;
import org.openmarkov.core.action.PNUndoableEditEvent;
import org.openmarkov.core.action.PNUndoableEditListener;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.gui.graphic.SelectionListener;
import org.openmarkov.core.gui.graphic.VisualNode;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.gui.menutoolbar.common.ActionCommands;
import org.openmarkov.core.gui.menutoolbar.common.MenuAssistant;
import org.openmarkov.core.gui.menutoolbar.common.MenuToolBarBasic;
import org.openmarkov.core.gui.menutoolbar.common.ZoomMenuToolBar;
import org.openmarkov.core.gui.window.edition.EditionState;
import org.openmarkov.core.gui.window.edition.NetworkPanel;
import org.openmarkov.core.gui.window.edition.Zoom;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.type.InfluenceDiagramType;
import org.openmarkov.core.model.network.type.MDPType;
import org.openmarkov.core.model.network.type.POMDPType;
import org.openmarkov.core.model.network.type.SimpleMarkovModelType;



/**
 * This class assists to the class MainPanel to manage the menus and toolbars.
 * 
 * @author jmendoza
 * @version 1.0 jmendoza
 * @version 1.1 jlgozalo modify setZoom method to use floating point comparison
 *          instead != method and add default statement for case sentences
 * @version 1.2 - asaez - Functionality added: Treatment of options related to 
 * 			- Explanation capabilities,
 * 			- Management of working modes (edition/inference), 
 * 			- Expansion and contraction of nodes, 
 * 			- Introduction and elimination of evidence 
 * 			- Management of multiple evidence cases.
 */
public class MainPanelMenuAssistant extends MenuAssistant implements
				SelectionListener, PNUndoableEditListener {

	/**
	 * Composed action command that contains all the save and close actions
	 * (except save).
	 */
	public static final String[] FILING_ACTION_COMMANDS =
		{ActionCommands.SAVE_OPEN_NETWORK, ActionCommands.SAVEAS_NETWORK, ActionCommands.CLOSE_NETWORK,
			ActionCommands.NETWORK_PROPERTIES };

	/**
	 * Composed action command that contains all the edition actions (except
	 * undo and redo).
	 */
	public static final String[] EDITING_ACTION_COMMANDS =
		{ ActionCommands.OBJECT_SELECTION, ActionCommands.CHANCE_CREATION, 
			ActionCommands.DECISION_CREATION, ActionCommands.UTILITY_CREATION,
			ActionCommands.LINK_CREATION }; 
	
	/**
	 * Composed action command that contains inference actions.
	 */
	public static final String[] INFERENCE_ACTION_COMMANDS =
		{ ActionCommands.CREATE_NEW_EVIDENCE_CASE, ActionCommands.GO_TO_FIRST_EVIDENCE_CASE, 
			ActionCommands.GO_TO_PREVIOUS_EVIDENCE_CASE, ActionCommands.GO_TO_NEXT_EVIDENCE_CASE,
			ActionCommands.GO_TO_LAST_EVIDENCE_CASE, ActionCommands.CLEAR_OUT_ALL_EVIDENCE_CASES,
			ActionCommands.PROPAGATE_EVIDENCE}; 
	
	/**
	 * Composed action command that contains all the viewing actions (except
	 * view message window).
	 */
	public static final String[] VIEWING_ACTION_COMMANDS =
		{ ActionCommands.ZOOM, ActionCommands.ZOOM_IN, ActionCommands.ZOOM_OUT,
			ActionCommands.ZOOM_OTHER, ActionCommands.NODES };

	/**
	 * Menus and toolbar that manage zoom.
	 */
	private ZoomMenuToolBar[] zoomMenus = null;

	/**
	 * MainPanel from which this object depends.
	 */
	private MainPanel mainPanel = null;	

	/**
	 * networkPanel that is currently selected.
	 */
	private NetworkPanel currentNetworkPanel = null;
	
	private StringResource stringResource;
	
	/**
	 * Constructor that registers the arrays of menus.
	 * 
	 * @param newBasicMenus
	 *            array of basic menus and toolbars.
	 * @param newZoomMenus
	 *            array of zoom menus and toolbars.
	 * @param mainPanel
	 *            MainPanel that creates this MainPanelMenuAssistant.
	 */
	public MainPanelMenuAssistant(MenuToolBarBasic[] newBasicMenus,
									ZoomMenuToolBar[] newZoomMenus, MainPanel mainPanel) {

		super(newBasicMenus);
		stringResource =
			StringResourceLoader.getUniqueInstance().getBundleMenus();
			
		ZoomMenuToolBar[] menus = newZoomMenus;

		if (menus == null) {
			menus = new ZoomMenuToolBar[0];
		}
		zoomMenus = menus;
		this.mainPanel = mainPanel;

	}

	/**
	 * Sets the zoom value on the menus and toolbars.
	 * 
	 * @param value
	 *            new zoom value.
	 */
	public void setZoom(double value) {

		for (ZoomMenuToolBar menu : zoomMenus) {
			menu.setZoom(value);
		}
		Double dd = new Double(value);
		
		if (dd.equals(Zoom.MIN_VALUE)) {
			setOptionEnabled(ActionCommands.ZOOM_OUT, false);
		} else {
			setOptionEnabled(ActionCommands.ZOOM_OUT, true);
		}
		if (dd.equals(Zoom.MAX_VALUE)) {
			setOptionEnabled(ActionCommands.ZOOM_IN, false);
		} else {
			setOptionEnabled(ActionCommands.ZOOM_IN, true);
		}
		

	}

	/**
	 * Enables the menu items and toolbar buttons when all networks are closed.
	 */
	public void updateOptionsAllNetworkClosed() {

		setOptionEnabled(FILING_ACTION_COMMANDS, false);
		setOptionEnabled(ActionCommands.SAVE_NETWORK, false);
		setOptionEnabled(EDITING_ACTION_COMMANDS, false);
		setOptionEnabled(INFERENCE_ACTION_COMMANDS, false);
		setOptionEnabled(ActionCommands.SELECT_ALL, false);
		setOptionEnabled(ActionCommands.CHANGE_WORKING_MODE, false);
		setOptionEnabled(ActionCommands.CHANGE_TO_INFERENCE_MODE, false);
		setOptionEnabled(ActionCommands.CHANGE_TO_EDITION_MODE, false);
		setOptionEnabled(ActionCommands.NODE_EXPANSION, false);
		setOptionEnabled(ActionCommands.NODE_CONTRACTION, false);
		setOptionEnabled(ActionCommands.NODE_ADD_FINDING, false);
		setOptionEnabled(ActionCommands.NODE_REMOVE_FINDING, false);
		setOptionEnabled(ActionCommands.NODE_REMOVE_ALL_FINDINGS, false);
		addOptionText(ActionCommands.UNDO, null);
		addOptionText(ActionCommands.REDO, null);
		setOptionEnabled(ActionCommands.UNDO, false);
		setOptionEnabled(ActionCommands.REDO, false);
		setOptionEnabled(ActionCommands.CLIPBOARD_CUT, false);
		setOptionEnabled(ActionCommands.CLIPBOARD_COPY, false);
		setOptionEnabled(ActionCommands.CLIPBOARD_PASTE, false);
		setOptionEnabled(ActionCommands.OBJECT_REMOVAL, false);
		setOptionEnabled(ActionCommands.NODE_PROPERTIES, false);
		setOptionEnabled(ActionCommands.EDIT_POTENTIAL, false);
		setOptionEnabled(ActionCommands.COST_EFFECTIVENESS_DETERMINISTIC, false);
		setOptionEnabled(ActionCommands.SENSITIVITY_ANALYSIS, false);
		setOptionEnabled(ActionCommands.LINK_PROPERTIES, false);
		setOptionEnabled(VIEWING_ACTION_COMMANDS, false);
		setOptionEnabled(ActionCommands.INFERENCE_OPTIONS, false);
	}

	/**
	 * Disables the menu items and toolbar buttons when any network is opened.
	 */
	public void updateOptionsNewNetworkOpen() {
		int workingMode = NetworkPanel.EDITION_WORKING_MODE;
		if (!(currentNetworkPanel == null)) {
			workingMode = currentNetworkPanel.getWorkingMode();
			boolean enable = currentNetworkPanel.getProbNet().getNetworkType() instanceof
					InfluenceDiagramType || currentNetworkPanel.getProbNet().getNetworkType() instanceof
					SimpleMarkovModelType;
			setOptionEnabled(ActionCommands.COST_EFFECTIVENESS_DETERMINISTIC, enable);
			setOptionEnabled(ActionCommands.SENSITIVITY_ANALYSIS, enable);
			
		}
		setOptionEnabled(FILING_ACTION_COMMANDS, true);
		if (workingMode == NetworkPanel.EDITION_WORKING_MODE) {
			setOptionEnabled(EDITING_ACTION_COMMANDS, true);
			setOptionEnabled(INFERENCE_ACTION_COMMANDS, false);
		}
		setOptionEnabled(VIEWING_ACTION_COMMANDS, true);
		setOptionEnabled(ActionCommands.CHANGE_WORKING_MODE, true);	
		setOptionEnabled(ActionCommands.INFERENCE_OPTIONS, true);
	}

	/**
	 * Activates the corresponding options when a network has been modified.
	 * 
	 * @param undoManager
	 *            network panel undo manager.
	 */
	/*public void updateOptionsNetworkModified(UndoManagerInfo undoManager) {

		updateUndoRedo(undoManager);
		//changed by mpalacios
		updateUndoRedo(true, true);
		setOptionEnabled(ActionCommands.SAVE_NETWORK, true);

	}*/
	/**
	 * Activates the corresponding options when a network has been modified.
	 * 
	 * @param undoManager
	 *            network panel undo manager.
	 */
	public void updateOptionsNetworkModified(boolean canUndo, boolean canRedo) {

		//updateUndoRedo(undoManager);
		//changed by mpalacios
		updateUndoRedo(canUndo, canRedo);
		setOptionEnabled(ActionCommands.SAVE_NETWORK, true);

	}

	/**
	 * Activates the corresponding options when a network has been saved.
	 */
	public void updateOptionsNetworkSaved() {

		setOptionEnabled(ActionCommands.SAVE_NETWORK, false);

	}

	/**
	 * Activates the options byTitle or byName.
	 * 
	 * @param byTitleActive
	 *            if true, the option 'byTitle' will be activated; if false, the
	 *            option 'byName' will be activated.
	 */
	public void setByTitle(boolean byTitleActive) {

		if (byTitleActive) {
			setOptionSelected(ActionCommands.BYTITLE_NODES, true);
		} else {
			setOptionSelected(ActionCommands.BYNAME_NODES, true);
		}

	}

	/**
	 * Activates the options on the menus and toolbars that depend on the
	 * network.
	 * 
	 * @param networkPanel
	 *            information of the network panel.
	 */
	public void updateOptionsNetworkDependent(NetworkPanel networkPanel) {

		currentNetworkPanel = networkPanel;
		int workingMode = NetworkPanel.EDITION_WORKING_MODE;
		if (!(currentNetworkPanel == null)) {
			workingMode = currentNetworkPanel.getWorkingMode();
		}			
		if (networkPanel.getByTitle()) {
			setOptionSelected(ActionCommands.BYTITLE_NODES, true);
		} else {
			setOptionSelected(ActionCommands.BYNAME_NODES, true);
		}
		setOptionEnabled(ActionCommands.OBJECT_SELECTION, false);
		setOptionEnabled(ActionCommands.CHANCE_CREATION, false);
		setOptionEnabled(ActionCommands.DECISION_CREATION, false);
		setOptionEnabled(ActionCommands.UTILITY_CREATION, false);
		setOptionEnabled(ActionCommands.LINK_CREATION, false);
		setOptionEnabled(ActionCommands.COST_EFFECTIVENESS_DETERMINISTIC, false);
		setOptionEnabled(ActionCommands.SENSITIVITY_ANALYSIS, false);
		setOptionEnabled(ActionCommands.CHANGE_TO_INFERENCE_MODE, false);
		setOptionEnabled(ActionCommands.CHANGE_TO_EDITION_MODE, false);
		
		if (workingMode == NetworkPanel.EDITION_WORKING_MODE) {
			setOptionEnabled(ActionCommands.OBJECT_SELECTION, true);
			setOptionEnabled(ActionCommands.CHANCE_CREATION, true);
			setOptionEnabled(ActionCommands.LINK_CREATION, true);
			setOptionEnabled(ActionCommands.CHANGE_TO_INFERENCE_MODE, true);
			setOptionEnabled(INFERENCE_ACTION_COMMANDS, false);
            if (networkPanel.getProbNet ().getNetworkType () instanceof InfluenceDiagramType
                || networkPanel.getProbNet ().getNetworkType () instanceof SimpleMarkovModelType
                || networkPanel.getProbNet ().getNetworkType () instanceof MDPType
                || networkPanel.getProbNet ().getNetworkType () instanceof POMDPType)
            {
				setOptionEnabled(ActionCommands.DECISION_CREATION, true);
				setOptionEnabled(ActionCommands.UTILITY_CREATION, true);
				setOptionEnabled(ActionCommands.COST_EFFECTIVENESS_DETERMINISTIC, true);
				setOptionEnabled(ActionCommands.SENSITIVITY_ANALYSIS, true);
				setOptionEnabled(ActionCommands.PROPAGATE_EVIDENCE, false);
			}
		} else {
			setOptionEnabled(ActionCommands.CHANGE_TO_EDITION_MODE, true);
			setOptionEnabled(ActionCommands.CREATE_NEW_EVIDENCE_CASE, true);
			updateOptionsEvidenceCasesNavigation(networkPanel);
			if (networkPanel.isAutomaticPropagation()) {
				setOptionEnabled(ActionCommands.PROPAGATE_EVIDENCE, false);
			} else {
				setOptionEnabled(ActionCommands.PROPAGATE_EVIDENCE, true);
			}
		}
		updateOptionsFindingsDependent(networkPanel);
		mainPanel.changeWorkingModeButton(workingMode);
		
		/*for (NodeType type : networkPanel.getNetwork().getNetworkType()
			.getNodeTypes()) {
			switch (type) {
			case CHANCE: {
				setOptionEnabled(ActionCommands.CHANCE_CREATION, true);
				break;
			}
			case DECISION: {
				setOptionEnabled(ActionCommands.DECISION_CREATION, true);
				break;
			}
			case UTILITY: {
				setOptionEnabled(ActionCommands.UTILITY_CREATION, true);
				break;
			}
			default: {
				setOptionEnabled(ActionCommands.CHANCE_CREATION, true);
				break;
			}
			}
		}*/
		setOptionEnabled(ActionCommands.SAVE_NETWORK, networkPanel
			.getModified());
		objectsSelected(networkPanel.getSelectedNodesNumber(), networkPanel
			.getSelectedLinksNumber(), networkPanel.getSelectedNodes());
		setZoom(networkPanel.getZoom());
		
		/*updateUndoRedo(networkPanel.getUndoManager().canUndo(),
				networkPanel.getUndoManager().canUndo());*/
		
		updateUndoRedo(networkPanel.getProbNet().getPNESupport().getCanUndo(),
				networkPanel.getProbNet().getPNESupport().getCanRedo());
		
		
		
		//updateUndoRedo(networkPanel.getUndoManager());
		setEditionOption(networkPanel.getEditionState(), networkPanel
			.isThereDataStored());

		mainPanel.setToolBarPanel(networkPanel.getWorkingMode());

	}

	/**
	 * Enables or disables the undo and redo operations in the menubar and in
	 * the toolbar, according to the state of undo and redo of the network.
	 * 
	 * @param undoManager
	 *            undo manager.
	 */
	/*private void updateUndoRedo(UndoManagerInfo undoManager) {

		if (undoManager.canUndo()) {
			setOptionEnabled(ActionCommands.UNDO, true);
			addOptionText(ActionCommands.UNDO, undoManager
				.getUndoPresentationName());
		} else {
			setOptionEnabled(ActionCommands.UNDO, false);
			addOptionText(ActionCommands.UNDO, null);
		}
		if (undoManager.canRedo()) {
			setOptionEnabled(ActionCommands.REDO, true);
			addOptionText(ActionCommands.REDO, undoManager
				.getRedoPresentationName());
		} else {
			setOptionEnabled(ActionCommands.REDO, false);
			addOptionText(ActionCommands.REDO, null);
		}

	}*/
	
	/**
	 * Enables or disables the undo and redo operations in the menubar and in
	 * the toolbar, according to the state of undo and redo of the network.
	 * 
	 * @param undoManager
	 *            undo manager.
	 */
	private void updateUndoRedo(boolean canUndo, boolean canRedo) {
		
		if (canUndo) {
			setOptionEnabled(ActionCommands.UNDO, true);
			addOptionText(ActionCommands.UNDO, "Deshacer");
		} else {
			setOptionEnabled(ActionCommands.UNDO, false);
			addOptionText(ActionCommands.UNDO, null);
		}
		if (canRedo) {
			setOptionEnabled(ActionCommands.REDO, true);
			addOptionText(ActionCommands.REDO, "Rehacer");
		} else {
			setOptionEnabled(ActionCommands.REDO, false);
			addOptionText(ActionCommands.REDO, null);
		}

	}
	
	/**

	 * Activates the options on the menus and toolbars that depend on the 
	 * working mode established on the network (edition or inference)
	 * 
	 * @param workingMode
	 *            the working mode (edition or inference). 
	 * @param networkPanel
	 *            information of the network panel.
	 */
	public void updateOptionsNewWorkingMode(int workingMode, NetworkPanel networkPanel) {
		if (workingMode == NetworkPanel.INFERENCE_WORKING_MODE) {
			setOptionEnabled(EDITING_ACTION_COMMANDS, false);
			setOptionEnabled(ActionCommands.UNDO, false);
			setOptionEnabled(ActionCommands.REDO, false);
			setOptionEnabled(ActionCommands.CLIPBOARD_CUT, false);
			setOptionEnabled(ActionCommands.CLIPBOARD_COPY, false);
			setOptionEnabled(ActionCommands.CLIPBOARD_PASTE, false);
			setOptionEnabled(ActionCommands.OBJECT_REMOVAL, false);
			setOptionEnabled(ActionCommands.NODE_PROPERTIES, false);
			setOptionEnabled(ActionCommands.EDIT_POTENTIAL, false);
			setOptionEnabled(ActionCommands.LINK_PROPERTIES, false);
			setOptionEnabled(ActionCommands.CHANGE_TO_INFERENCE_MODE, false);
			setOptionEnabled(ActionCommands.CHANGE_TO_EDITION_MODE, true);
			setOptionEnabled(ActionCommands.CREATE_NEW_EVIDENCE_CASE, true);
			updateOptionsEvidenceCasesNavigation(networkPanel);
			if (networkPanel.isAutomaticPropagation()) {
				setOptionEnabled(ActionCommands.PROPAGATE_EVIDENCE, false);
			} else {
				setOptionEnabled(ActionCommands.PROPAGATE_EVIDENCE, true);
			}
		} else if (workingMode == NetworkPanel.EDITION_WORKING_MODE) {
			setOptionEnabled(EDITING_ACTION_COMMANDS, true);
			setOptionEnabled(INFERENCE_ACTION_COMMANDS, false);
			setOptionEnabled(ActionCommands.CHANGE_TO_INFERENCE_MODE, true);
			setOptionEnabled(ActionCommands.CHANGE_TO_EDITION_MODE, false);
			setOptionEnabled(ActionCommands.PROPAGATE_EVIDENCE, false);
		}
		objectsSelected(networkPanel.getSelectedNodesNumber(), networkPanel.getSelectedLinksNumber(), 
				networkPanel.getSelectedNodes());
	}

	/**
	 * Activates the menu items and toolbar buttons for navigate among
	 * the set of evidence cases.
	 * 
	 * @param networkPanel
	 *            information of the network panel.
	 */
	public void updateOptionsEvidenceCasesNavigation(NetworkPanel networkPanel) {
		if (networkPanel.getNumberOfCases() > 1) {
			setOptionEnabled(ActionCommands.CLEAR_OUT_ALL_EVIDENCE_CASES, true);
			if (networkPanel.getCurrentCase() > 0) {
				setOptionEnabled(ActionCommands.GO_TO_FIRST_EVIDENCE_CASE, true);
				setOptionEnabled(ActionCommands.GO_TO_PREVIOUS_EVIDENCE_CASE, true);
			} else {
				setOptionEnabled(ActionCommands.GO_TO_FIRST_EVIDENCE_CASE, false);
				setOptionEnabled(ActionCommands.GO_TO_PREVIOUS_EVIDENCE_CASE, false);				
			}
			if (networkPanel.getCurrentCase() < (networkPanel.getNumberOfCases()-1)) {
				setOptionEnabled(ActionCommands.GO_TO_NEXT_EVIDENCE_CASE, true);
				setOptionEnabled(ActionCommands.GO_TO_LAST_EVIDENCE_CASE, true);
			} else {
				setOptionEnabled(ActionCommands.GO_TO_NEXT_EVIDENCE_CASE, false);
				setOptionEnabled(ActionCommands.GO_TO_LAST_EVIDENCE_CASE, false);				
			}
		} else {
			setOptionEnabled(ActionCommands.GO_TO_FIRST_EVIDENCE_CASE, false);
			setOptionEnabled(ActionCommands.GO_TO_PREVIOUS_EVIDENCE_CASE, false);
			setOptionEnabled(ActionCommands.GO_TO_NEXT_EVIDENCE_CASE, false);
			setOptionEnabled(ActionCommands.GO_TO_LAST_EVIDENCE_CASE, false);
			setOptionEnabled(ActionCommands.CLEAR_OUT_ALL_EVIDENCE_CASES, false);
		}
		updateOptionsFindingsDependent(networkPanel);
	}
	
	/**
	 * Activates the options on the menus and toolbars that depend on the
	 * propagation type established on the network (automatic or manual).
	 * 
	 * @param networkPanel
	 *            information of the network panel.
	 */
	public void updateOptionsPropagationTypeDependent(NetworkPanel networkPanel) {
		if (networkPanel.isAutomaticPropagation()) {
			setOptionEnabled(ActionCommands.PROPAGATE_EVIDENCE, false);
		} else {
			if (networkPanel.getWorkingMode() == NetworkPanel.INFERENCE_WORKING_MODE) {
				setOptionEnabled(ActionCommands.PROPAGATE_EVIDENCE, true);
			}
		}
	}	
	
	/**
	 * Activates the options on the menus and toolbars that depend on the
	 * existence of findings in the current evidence case.
	 * 
	 * @param networkPanel
	 *            information of the network panel.
	 */
	public void updateOptionsFindingsDependent(NetworkPanel networkPanel) {
		setOptionEnabled(ActionCommands.NODE_REMOVE_ALL_FINDINGS,
				networkPanel.areThereFindingsInCase()); 	
	}

	/**
	 * Activates an edition option on the menus and toolbars according to the
	 * edition state.
	 * 
	 * @param value
	 *            actual edition state.
	 * @param canPaste
	 *            if the state is SELECTION, this parameter says if there is
	 *            data in the clipboard.
	 */
	public void setEditionOption(EditionState value, boolean canPaste) {

		boolean optionPaste = false;
		boolean optionSelectAll = false;

		switch (value) {
		case SELECTION: {
			setOptionSelected(ActionCommands.OBJECT_SELECTION, true);
			optionSelectAll = true;
			optionPaste = canPaste;
			break;
		}
		case CHANCE: {
			setOptionSelected(ActionCommands.CHANCE_CREATION, true);
			break;
		}
		case DECISION: {
			setOptionSelected(ActionCommands.DECISION_CREATION, true);
			break;
		}
		case UTILITY: {
			setOptionSelected(ActionCommands.UTILITY_CREATION, true);
			break;
		}
		case LINK: {
			setOptionSelected(ActionCommands.LINK_CREATION, true);
			break;
		}
		default:
			setOptionSelected(ActionCommands.OBJECT_SELECTION, true);
			optionSelectAll = true;
			optionPaste = canPaste;
			break;
		}
		setOptionEnabled(ActionCommands.SELECT_ALL, optionSelectAll);
		setOptionEnabled(ActionCommands.CLIPBOARD_PASTE, optionPaste);

	}


	/**
	 * This method activates o desactivates some options depending on the
	 * numbers of nodes or links selected or the expanded state of the specific nodes selected
	 * 
	 * @param nodes
	 *            number of selected nodes.
	 * @param links
	 *            number of selected links.
	 * @param arrayOfNodes
	 *            an array with the selected nodes.
	 */
	public void objectsSelected(int nodes, int links, ArrayList<VisualNode> arrayOfNodes) {
		boolean canCut = false;
		boolean canCopy = false;
		boolean canRemove = false;
		boolean canNodeProperties = false;
		boolean canNodeTable = false;		
		boolean canLinkProperties = false;
		boolean canExpand = false;
		boolean canContract = false;
		boolean canAddFinding = false;
		boolean canRemoveFinding = false;
		boolean canLog = false;

		int workingMode = NetworkPanel.EDITION_WORKING_MODE;
		if (!(currentNetworkPanel == null)) {
			workingMode = currentNetworkPanel.getWorkingMode();
		}	
		if (nodes > 0) {
            canCopy = true;
			if (workingMode == NetworkPanel.EDITION_WORKING_MODE) {
				canRemove = true;
                canCut = true;
			}
			if (links <= 0) {
				//if we are in Inference Mode, options about expansion and contraction can be activated
				if (workingMode == NetworkPanel.INFERENCE_WORKING_MODE) {
					if (arrayOfNodes.size() > 0) {
						VisualNode visualNode = null;
						for (int i=0; i < arrayOfNodes.size(); i++) {
							visualNode = arrayOfNodes.get(i);
							//if at least one node is expanded, 'contract node(s)' option must be active
							if (visualNode.isExpanded()) {
								canContract = true;
							}
							//if at least one node is contracted, 'expand node(s)' option must be active
							if (!(visualNode.isExpanded())) {
								canExpand = true;
							}
						}			
					}
				}
				if (nodes == 1) {
					if (workingMode == NetworkPanel.EDITION_WORKING_MODE) {
						canNodeProperties = true;
						canNodeTable = true;
						 if (arrayOfNodes.get(0).getProbNode().getVariable().isTemporal()){
							 canLog = true;
						 }
						 String label = null;
						 switch (arrayOfNodes.get(0).getProbNode().getNodeType()){
						 case CHANCE:
							 label = stringResource.getString("Edit.NodePotential.Label");
							 break;
						 case UTILITY:
							 label = stringResource.getString("Edit.Utility.Label");
							 break;
						 case DECISION:	 
							 label = stringResource.getString("Edit.Policy.Label");
							 break;
						 }
						 setText(ActionCommands.EDIT_POTENTIAL, label);
					}
					VisualNode visualNode = arrayOfNodes.get(0);
					if (visualNode.getFindingInNode()) {
						canRemoveFinding = true;
					} else {
						canAddFinding = true;
					}
				}
			}
		} else {
			if (links > 0) {
				if (workingMode == NetworkPanel.EDITION_WORKING_MODE) {
					canRemove = true;
				}
				if (links == 1) {
					if (workingMode == NetworkPanel.EDITION_WORKING_MODE) {
						canLinkProperties = true;
					}
				}
			}
		}
		setOptionEnabled(ActionCommands.CLIPBOARD_CUT, canCut);
		setOptionEnabled(ActionCommands.CLIPBOARD_COPY, canCopy);
		setOptionEnabled(ActionCommands.OBJECT_REMOVAL, canRemove);
		setOptionEnabled(ActionCommands.NODE_PROPERTIES, canNodeProperties);
		setOptionEnabled(ActionCommands.EDIT_POTENTIAL, canNodeTable);
		setOptionEnabled(ActionCommands.LINK_PROPERTIES, canLinkProperties);
		setOptionEnabled(ActionCommands.NODE_EXPANSION, canExpand);
		setOptionEnabled(ActionCommands.NODE_CONTRACTION, canContract);
		setOptionEnabled(ActionCommands.NODE_ADD_FINDING, canAddFinding);
		setOptionEnabled(ActionCommands.NODE_REMOVE_FINDING, canRemoveFinding);
		setOptionEnabled(ActionCommands.LOG, canLog);
	}
	
	/**
	 * This method indicates that some information has been put into the
	 * clipboard.
	 */
	public void dataStoredClipboard() {

		setOptionEnabled(ActionCommands.CLIPBOARD_PASTE, true);

	}

	/**
	 * This method indicates that there isn't valid information in the
	 * clipboard.
	 */
	public void invalidDataClipboard() {

		setOptionEnabled(ActionCommands.CLIPBOARD_PASTE, false);

	}

	/**
	 * This method notifies to the listener that an edition action has occurred
	 * on a network panel.
	 * 
	 * @param undoManager
	 *            undo manager object limited in functionality.
	 */
	/*public void editionPerformed(UndoManagerInfo undoManager) {

		updateOptionsNetworkModified(undoManager);

	}*/

	
	public void undoableEditHappened(UndoableEditEvent e) {
		 
		ProbNet probNet = currentNetworkPanel.getProbNet ();
		updateOptionsNetworkModified(probNet.getPNESupport().getCanUndo(),
				probNet.getPNESupport().getCanRedo());
		/*updateOptionsNetworkModified(((ProbNet)e.getSource()).getPNESupport().getCanUndo(),
				((ProbNet)e.getSource()).getPNESupport().getCanRedo());*/
		

	}

	
	public void undoableEditWillHappen(UndoableEditEvent event)
			throws ConstraintViolationException, CanNotDoEditException {
		// TODO Auto-generated method stub
		
	}

	
	public void undoEditHappened(UndoableEditEvent event) {
        ProbNet probNet = currentNetworkPanel.getProbNet ();
        updateOptionsNetworkModified(((PNESupport)event.getSource()).getCanUndo(),
                ((PNESupport)event.getSource()).getCanRedo());
        
        /*updateOptionsNetworkModified(((PNESupport)event.getSource()).getCanUndo(),
                ((PNESupport)event.getSource()).getCanRedo());*/
        
		
	}
}
