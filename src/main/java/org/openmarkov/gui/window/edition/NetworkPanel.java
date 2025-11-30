/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.window.edition;

import org.openmarkov.core.action.base.*;
import org.openmarkov.core.exception.*;
import org.openmarkov.core.inference.InferenceAlgorithm;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.gui.exception.NotEnoughtMemoryException;
import org.openmarkov.gui.exception.PreResolutionNodeInInferenceException;
import org.openmarkov.gui.exception.ThereIsNoNextEvidenceCaseException;
import org.openmarkov.gui.exception.ThereIsNoPreviousEvidenceCaseException;
import org.openmarkov.gui.graphic.SelectionListener;
import org.openmarkov.gui.graphic.VisualLink;
import org.openmarkov.gui.graphic.VisualNetwork;
import org.openmarkov.gui.graphic.VisualNode;
import org.openmarkov.core.localize.StringDatabase;
import org.openmarkov.gui.menutoolbar.menu.ContextualMenuFactory;
import org.openmarkov.gui.window.MainPanel;
import org.openmarkov.gui.window.MainPanelMenuAssistant;
import org.openmarkov.gui.window.mdi.FrameContentPanel;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.awt.*;
import java.util.List;

// ESCA-JAVA0136: allows more than 30 methods in the class

/**
 * This class implements a panel where is added a scroll panel and into this one
 * a network panel.
 *
 * @author jmendoza
 * @version 1.3 - asaez - Functionality added: - Explanation capabilities, -
 * Management of working modes (edition/inference), - Expansion and
 * contraction of nodes, - Introduction and elimination of evidence -
 * Management of multiple evidence cases.
 */
public class NetworkPanel extends FrameContentPanel implements PNUndoableEditListener {
    /**
     * Static field for serializable class.
     */
    private static final long serialVersionUID = 6804988702698858496L;
    /**
     * Network that is edited.
     */
    public ProbNet probNet;
    /**
     * Panel where the network is painted.
     */
    private EditorPanel editorPanel = null;
    /**
     * Application main
     */
    private MainPanel mainPanel;
    /**
     * Name of the file where the network is saved (updated or not).
     */
    private String networkFile = null;
    
    /**
     * Format of the file where the network is saved (updated or not).
     */
    private String networkFileFormat = null;
    /**
     * Indicates if the network has been modified.
     */
    private boolean modified = false;
    /**
     * This variable indicates in which mode is the network currently working It
     * is initially set to Edition Mode
     */
    private WorkingMode workingMode = WorkingMode.EDITION;
    
    public enum WorkingMode {
        EDITION, INFERENCE;
    }
    
    /**
     * Constructor that creates the instance.
     *
     * @param probNet   network that will be edited.
     * @param mainPanel application main panel.
     */
    public NetworkPanel(ProbNet probNet, MainPanel mainPanel) {
        this.probNet = probNet;
        this.mainPanel = mainPanel;
        probNet.getPNESupport().addListener(this);
        initialize();
    }
    
    /**
     * Constructor that creates the instance.
     *
     * @param mainPanel application main panel.
     */
    public NetworkPanel(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
        initialize();
    }
    
    /**
     * This method requests to the user the additionalProperties of a network.
     *
     * @param owner      window that owns the dialog box.
     * @param probNet    Netwoek
     * @param newNetwork specifies if the network whose additionalProperties are
     *                   going to be edited is new.
     *
     * @return true, if the user has made changes on the additionalProperties;
     * otherwise, false.
     */
    public static boolean requestNetworkProperties(ProbNet probNet, Window owner, boolean newNetwork) {
        return EditorPanel.requestNetworkProperties(owner, probNet);
    }
    
    /**
     * This method initialises this instance.
     */
    private void initialize() {
        setLayout(new BorderLayout());
        // JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        // getNetworkScrollPanel().setSize(new Dimension (300,300));
        // splitPane.setTopComponent(getNetworkScrollPanel());
        // splitPane.setBottomComponent(getPropertiesScrollPanel());
        add(new ScrollableEditorPanel(getEditorPanel()));
    }
    
    /**
     * This method initializes editorPanel.
     *
     * @return a new editor panel.
     */
    public EditorPanel getEditorPanel() {
        if (editorPanel == null) {
            editorPanel = new EditorPanel(this, new VisualNetwork(probNet));
        }
        return editorPanel;
    }
    
    /**
     * Returns the network which is edited.
     *
     * @return network which is edited.
     */
    public ProbNet getProbNet() {
        return probNet;
    }
    
    /**
     * Returns the application main panel.
     *
     * @return the application main panel.
     */
    public MainPanel getMainPanel() {
        return mainPanel;
    }
    
    /**
     * Returns the modification state of the network.
     *
     * @return true if the network has been modified; otherwise, false.
     */
    public boolean getModified() {
        return modified;
    }
    
    /**
     * Sets the modification state of the network to a new value.
     *
     * @param value new value of the modification state of the network.
     */
    public void setModified(boolean value) {
        modified = value;
        refreshContainerTitle();
    }
    
    /**
     * This method sets the title of the container of the network panel to the
     * name of the file where the network is saved. If the network has been
     * modified, an asterisk appears before the name of the file. If the name of
     * the file is null, then the title of the container is used.
     */
    private void refreshContainerTitle() {
        String newTitle = "";
        if (modified) {
            newTitle = "* ";
        }
        newTitle += (getProbNet().getName() == null) ?
                StringDatabase.getUniqueInstance().getString("InternalFrame.Title.Label") :
                getProbNet().getName();
        container.setTitle(newTitle);
    }
    
    /**
     * Returns the title of the content panel.
     *
     * @return the title of the content panel.
     */
    @Override public String getTitle() {
        return (
                (getProbNet().getName() == null) ?
                        StringDatabase.getUniqueInstance().getString("InternalFrame.Title.Label") :
                        getProbNet().getName()
        );
        // return (String) getProbNet().getName();
    }
    
    /**
     * Returns the name of the file where the network is saved.
     *
     * @return a string that contains the name of the file.
     */
    public String getNetworkFile() {
        return networkFile;
    }
    
    
    /**
     * Sets the name of the file where the network is saved.
     *
     * @param name name of the file.
     */
    public void setNetworkFile(String name) {
        networkFile = name;
    }
    
    /**
     * Returns the format of the file where the network is saved.
     *
     * @return a string that contains the format of the file.
     */
    public String getNetworkFileFormat() {
        return networkFileFormat;
    }
    
    /**
     * Sets the format of the file where the network is saved.
     *
     * @param networkFileFormat format of the file.
     */
    public void setNetworkFileFormat(String networkFileFormat) {
        this.networkFileFormat = networkFileFormat;
    }
    
    /**
     * Changes the state of the edition and carries out the necessary actions in
     * each case.
     *
     * @param newEditionMode new edition state.
     */
    public void setEditionMode(String newEditionMode) {
        editorPanel.setEditionMode(newEditionMode);
    }
    
    /**
     * Returns the current working mode.
     *
     * @return the value of the current working mode (Edition or Inference).
     */
    public WorkingMode getWorkingMode() {
        return workingMode;
    }
    
    /**
     * Changes the current working mode.
     *
     * @param workingMode new value of the working mode.
     */
    public void setWorkingMode(WorkingMode workingMode) {
        this.workingMode = workingMode;
        editorPanel.setWorkingMode(workingMode);
    }
    
    /**
     * Returns the current expansion threshold.
     *
     * @return the value of the current expansion threshold.
     */
    public double getExpansionThreshold() {
        return editorPanel.getExpansionThreshold();
    }
    
    /**
     * Changes the current expansion threshold.
     *
     * @param expansionThreshold new value of the expansion threshold.
     */
    public void setExpansionThreshold(double expansionThreshold) {
        editorPanel.setExpansionThreshold(expansionThreshold);
    }
    
    /**
     * This method shows a dialog box with the additionalProperties of the
     * network. If some property has changed, insert a new undo point into the
     * network undo manager.
     */
    public void changeNetworkProperties() {
        editorPanel.changeNetworkProperties();
    }
    
    /**
     * This method absorbs a node into the rest of the net arc-reversal style. This means updating the only utility
     * child it might have and removing it next.
     */
    public void absorbNode() throws DoEditException {
        editorPanel.absorbNode();
    }
    
    /**
     * This method absorbs intermediate utility nodes.
     */
    public void absorbParents() throws DoEditException {
        editorPanel.absorbParents();
    }
    
    /**
     * This method shows a dialog box with the additionalProperties of a node.
     * If some property has changed, insert a new undo point into the network
     * undo manager.
     */
    public void changeNodeProperties() throws NotEvaluableNetworkException, NonProjectablePotentialException, NotEnoughtMemoryException, IncompatibleEvidenceException, CannotNormalizePotentialException, ConstraintViolatedException {
        editorPanel.changeNodeProperties();
    }
    
    /**
     * This method has been created for testing.
     */
    public void changePotential() throws IncompatibleEvidenceException, ThereIsNoPotentialsInNodeException, NotEvaluableNetworkException, NonProjectablePotentialException, NotEnoughtMemoryException, CannotNormalizePotentialException, ConstraintViolatedException {
        editorPanel.showPotentialDialog(getWorkingMode() != WorkingMode.EDITION);
    }
    
    /**
     * This method manage the temporal evolution of a variable.
     */
    public void temporalEvolution() {
        editorPanel.temporalEvolution();
    }
    
    /**
     * This method imposes a policy in a decision node.
     */
    public void imposePolicyInNode() throws IncompatibleEvidenceException.EvidenceIsIncompatibleWithOther, ThereIsNoPotentialsInNodeException, NotEnoughtMemoryException {
        editorPanel.imposePolicyInNode();
    }
    
    /**
     * This method edits an imposed policy of a decision node.
     */
    public void editNodePolicy() throws IncompatibleEvidenceException.EvidenceIsIncompatibleWithOther, ThereIsNoPotentialsInNodeException, NotEnoughtMemoryException {
        editorPanel.editNodePolicy();
    }
    
    /**
     * This method removes an imposed policy from a decision node.
     */
    public void removePolicyFromNode() throws DoEditException {
        editorPanel.removePolicyFromNode();
    }
    
    /**
     * This method shows the expected utility of a decision node.
     */
    public void showExpectedUtilityOfNode()
            throws IncompatibleEvidenceException.EvidenceIsIncompatibleWithOther,
            NonProjectablePotentialException,
            NotEvaluableNetworkException.NotApplicableNetwork,
            NotEvaluableNetworkException.UnsatisfiedContraints, ThereIsNoPotentialsInNodeException, NotEnoughtMemoryException, ConstraintViolatedException {
        editorPanel.showExpectedUtilityOfNode();
    }
    
    /**
     * This method shows the optimal policy for a decision node.
     */
    public void showOptimalPolicyOfNode()
            throws IncompatibleEvidenceException.EvidenceIsIncompatibleWithOther,
            NonProjectablePotentialException,
            NotEvaluableNetworkException.NotApplicableNetwork,
            NotEvaluableNetworkException.UnsatisfiedContraints,
            ThereIsNoPotentialsInNodeException, NotEnoughtMemoryException, ConstraintViolatedException {
        editorPanel.showOptimalPolicyOfNode();
    }
    
    /**
     * This method expands a node.
     */
    public void expandNode() {
        editorPanel.expandNode();
    }
    
    /**
     * This method contracts a node.
     */
    public void contractNode() {
        editorPanel.contractNode();
    }
    
    /**
     * This method adds a finding in a node.
     */
    public void addFinding() {
        editorPanel.addFinding();
    }
    
    /**
     * This method removes findings from selected nodes.
     */
    public void removeFinding() throws PreResolutionNodeInInferenceException, DoEditException {
        editorPanel.removeFinding();
    }
    
    /**
     * This method updates the expansion state (expanded/contracted) of the
     * nodes. It is used in transitions from edition to inference mode and vice
     * versa, and also when the user modifies the current expansion threshold in
     * the Inference tool bar
     */
    public void updateNodesExpansionState(NetworkPanel.WorkingMode newWorkingMode) {
        editorPanel.updateNodesExpansionState(newWorkingMode);
    }
    
    /**
     * This method updates the value of each state for each node in the network
     * with the current individual probabilities.
     */
    public void updateIndividualProbabilitiesAndUtilities() throws NotEvaluableNetworkException, NonProjectablePotentialException, NotEnoughtMemoryException, IncompatibleEvidenceException, CannotNormalizePotentialException, ConstraintViolatedException {
        editorPanel.updateIndividualProbabilitiesAndUtilities();
    }
    
    /**
     * This method removes all the findings established in the current evidence
     * case.
     */
    public void removeAllFindings() throws NotEvaluableNetworkException, NonProjectablePotentialException, NotEnoughtMemoryException, IncompatibleEvidenceException, CannotNormalizePotentialException, ConstraintViolatedException {
        editorPanel.removeAllFindings();
    }
    
    /**
     * This method returns true if there are any finding in the current evidence
     * case.
     *
     * @return true if the current evidence case has at least one finding.
     */
    public boolean areThereFindingsInCase() {
        return editorPanel.areThereFindingsInCase();
    }
    
    /**
     * This method copies the selected nodes to the clipboard.
     *
     * @param cut if true, the nodes copied to the clipboard are also removed.
     */
    public void exportToClipboard(boolean cut) {
        editorPanel.exportToClipboard(cut);
    }
    
    /**
     * This method imports various nodes from the clipboard and creates them in
     * the network.
     */
    public void pasteFromClipboard() throws DoEditException {
        editorPanel.pasteFromClipboard();
    }
    
    /**
     * This method says if there is data stored in the clipboard.
     *
     * @return true if there is data stored in the clipboard; otherwise, false.
     */
    public boolean isThereDataStored() {
        return editorPanel.isThereDataStored();
    }
    
    /**
     * This method removes the selected objects. First removes the selected
     * links and then removes the selected nodes. Also notifies that there
     * aren't selected elements and creates a new undo point.
     */
    public void removeSelectedObjects() {
        editorPanel.removeSelectedObjects();
    }
    
    /**
     * This methods reverts the selected link.
     */
    public void invertLinkAndUpdatePotentials() throws DoEditException {
        editorPanel.invertLinkAndUpdatePotentials();
    }
    
    /****
     * This methods enables the link restriction of the selected link.
     */
    public void enableLinkRestriction() {
        editorPanel.enableLinkRestriction();
    }
    
    /****
     * This method enables the revelation arc properties of the selected link.
     */
    public void enableRevelationArc() {
        editorPanel.enableRevelationArc();
    }
    
    /***
     * This method resets the link restriction of the selected link.
     */
    public void disableLinkRestriction() throws DoEditException {
        editorPanel.disableLinkRestriction();
    }
    
    /**
     * Sets a new contextual menu factory.
     *
     * @param newContextualMenuFactory contextual menu factory to be set.
     */
    public void setContextualMenuFactory(ContextualMenuFactory newContextualMenuFactory) {
        editorPanel.setContextualMenuFactory(newContextualMenuFactory);
    }
    
    /**
     * Sets a new selection listener.
     *
     * @param listener listener to be set.
     */
    public void addSelectionListener(SelectionListener listener) {
        editorPanel.addSelectionListener(listener);
    }
    
    /**
     * This method performs a undo operation.
     *
     * @throws CannotUndoException if undo can't be performed.
     */
    public void undo() throws CannotUndoException {
        editorPanel.undo();
    }
    
    /**
     * This method performs a redo operation.
     *
     * @throws CannotRedoException if redo can't be performed.
     */
    public void redo() throws CannotRedoException {
        editorPanel.redo();
    }
    
    /**
     * Selects all nodes and links.
     */
    public void selectAllObjects() {
        editorPanel.selectAllObjects();
    }
    
    /**
     * Returns the presentation mode of the text of the nodes.
     *
     * @return true if the title of the nodes is the name or false if it is the
     * name.
     */
    public boolean getByTitle() {
        return editorPanel.getByTitle();
    }
    
    /**
     * Changes the presentation mode of the text of the nodes.
     *
     * @param value new value of the presentation mode of the text of the nodes.
     */
    public void setByTitle(boolean value) {
        editorPanel.setByTitle(value);
    }
    
    /**
     * Returns the value of the zoom.
     *
     * @return actual value of zoom.
     */
    @Override public double getZoom() {
        return editorPanel.getZoom();
    }
    
    /**
     * Changes the value of the zoom.
     *
     * @param value new zoom.
     */
    @Override public void setZoom(double value) {
        editorPanel.setZoom(value);
    }
    
    /**
     * Returns the number of selected nodes.
     *
     * @return number of selected nodes.
     */
    public int getSelectedNodesNumber() {
        return editorPanel.getSelectedNodesNumber();
    }
    
    /**
     * Returns the number of selected links.
     *
     * @return number of selected links.
     */
    public int getSelectedLinksNumber() {
        return editorPanel.getSelectedLinksNumber();
    }
    
    /**
     * Returns a list containing the currently selected nodes.
     *
     * @return a list containing the currently selected nodes.
     */
    public List<VisualNode> getSelectedNodes() {
        return editorPanel.getSelectedNodes();
    }
    
    /**
     * Returns a list containing the currently selected links.
     *
     * @return a list containing the currently selected links.
     */
    public List<VisualLink> getSelectedLinks() {
        return editorPanel.getSelectedLinks();
    }
    
    /**
     * Selects or deselects all nodes of the network.
     *
     * @param selected new selection state.
     */
    public void setSelectedAllNodes(boolean selected) {
        editorPanel.setSelectedAllNodes(selected);
    }
    
    /**
     * Selects or deselects all objects of the network.
     *
     * @param selected new selection state.
     */
    public void setSelectedAllObjects(boolean selected) {
        editorPanel.setSelectedAllObjects(selected);
    }
    
    @Override public void afterEditHappens(PNUndoableEditEvent arg0) {
        if (arg0.getEdit().getClass() != OpenParenthesisEdit.class &&
                arg0.getEdit().getClass() != CloseParenthesisEdit.class) {
            setModified(true);
        }
    }
    
    @Override public void beforeEditHappens(PNUndoableEditEvent event) {
        repaint();
    }
    
    @Override public void afterUndoingEdit(PNUndoableEditEvent event) {
        setModified(event.getEdit().getProbNet().getPNESupport().getCanUndo());
        repaint();
    }
    
    /**
     * This method returns the number of the current Evidence Case.
     *
     * @return the number of the current Evidence Case.
     */
    public int getCurrentCase() {
        return editorPanel.getCurrentCase();
    }
    
    /**
     * This method returns the number of Evidence Cases that the ArrayList is
     * currently holding .
     *
     * @return the number of Evidence Cases in the ArrayList.
     */
    public int getNumberOfCases() {
        return editorPanel.getNumberOfCases();
    }
    
    /**
     * This method creates a new evidence case
     */
    public void createNewEvidenceCase() throws NotEvaluableNetworkException, NonProjectablePotentialException, NotEnoughtMemoryException, IncompatibleEvidenceException, CannotNormalizePotentialException, ConstraintViolatedException {
        editorPanel.createNewEvidenceCase();
    }
    
    /**
     * This method makes the first evidence case to be the current
     */
    public void goToFirstEvidenceCase() throws NotEvaluableNetworkException, NonProjectablePotentialException, NotEnoughtMemoryException, IncompatibleEvidenceException, CannotNormalizePotentialException, ConstraintViolatedException {
        editorPanel.goToFirstEvidenceCase();
    }
    
    /**
     * This method makes the previous evidence case to be the current
     */
    public void goToPreviousEvidenceCase() throws NotEvaluableNetworkException, NonProjectablePotentialException, NotEnoughtMemoryException, IncompatibleEvidenceException, CannotNormalizePotentialException, ThereIsNoPreviousEvidenceCaseException, ConstraintViolatedException {
        editorPanel.goToPreviousEvidenceCase();
    }
    
    /**
     * This method makes the next evidence case to be the current
     */
    public void goToNextEvidenceCase() throws NotEvaluableNetworkException, NonProjectablePotentialException, NotEnoughtMemoryException, IncompatibleEvidenceException, CannotNormalizePotentialException, ThereIsNoNextEvidenceCaseException, ConstraintViolatedException {
        editorPanel.goToNextEvidenceCase();
    }
    
    /**
     * This method makes the last evidence case to be the current
     */
    public void goToLastEvidenceCase() throws NotEvaluableNetworkException, NonProjectablePotentialException, NotEnoughtMemoryException, IncompatibleEvidenceException, CannotNormalizePotentialException, ConstraintViolatedException {
        editorPanel.goToLastEvidenceCase();
    }
    
    /**
     * This method clears out all the evidence cases. It returns to an 'initial
     * state' in which there is only an initial evidence case with no findings
     * (corresponding to prior probabilities)
     */
    public void clearOutAllEvidenceCases() throws NotEvaluableNetworkException, NonProjectablePotentialException, NotEnoughtMemoryException, IncompatibleEvidenceException, CannotNormalizePotentialException, ConstraintViolatedException {
        editorPanel.clearOutAllEvidenceCases();
    }
    
    /**
     * This method does the propagation of the evidence for all the evidence
     * cases in memory.
     *
     * @param mainPanelMenuAssistant the menu assistant associated to the main
     *                               panel.
     */
    public void propagateEvidence(MainPanelMenuAssistant mainPanelMenuAssistant) throws NotEvaluableNetworkException, NonProjectablePotentialException, NotEnoughtMemoryException, IncompatibleEvidenceException, CannotNormalizePotentialException, ConstraintViolatedException {
        editorPanel.propagateEvidence(mainPanelMenuAssistant);
    }
    
    /**
     * This method sets the inference options for this network.
     */
    public void setInferenceOptions() {
        editorPanel.setInferenceOptions();
    }
    
    /**
     * This method returns true if propagation type currently set in the panel
     * is automatic; false if manual.
     *
     * @return true if the current propagation type is automatic.
     */
    public boolean isAutomaticPropagation() {
        return editorPanel.isAutomaticPropagation();
    }
    
    /**
     * This method sets the current propagation type in the panel.
     *
     * @param automaticPropagation new value of the propagation type.
     */
    public void setAutomaticPropagation(boolean automaticPropagation) {
        editorPanel.setAutomaticPropagation(automaticPropagation);
    }
    
    /**
     * This method returns the propagation status: true if propagation should be
     * done right now; false otherwise.
     *
     * @return true if propagation should be done right now.
     */
    public boolean isPropagationActive() {
        return editorPanel.isPropagationActive();
    }
    
    /**
     * This method sets the propagation status.
     *
     * @param propagationActive new value of the propagation status.
     */
    public void setPropagationActive(boolean propagationActive) {
        editorPanel.setPropagationActive(propagationActive);
    }
    
    /**
     * Returns the inference algorithm assigned to the panel.
     *
     * @return the inference algorithm assigned to the panel.
     */
    public InferenceAlgorithm getInferenceAlgorithm() throws NotEvaluableNetworkException, NotSupportedOperationException {
        return editorPanel.getInferenceAlgorithm();
    }
    
    /**
     * Sets the inference algorithm assigned to the panel.
     *
     * @param inferenceAlgorithm the inference Algorithm to be assigned to the
     *                           panel.
     */
    public void setInferenceAlgorithm(InferenceAlgorithm inferenceAlgorithm) {
        editorPanel.setInferenceAlgorithm(inferenceAlgorithm);
    }
    
    @Override public void close() {
        // TODO Auto-generated method stub
    }
    
    // TODO OOPN end
    
    public void createNextSliceNode() throws DoEditException {
        editorPanel.createNextSliceNode();
    }
}
