/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.dialog.node;

import org.openmarkov.core.action.core.SetPotentialEdit;
import org.openmarkov.core.action.core.SetPotentialVariablesEdit;
import org.openmarkov.core.exception.*;
import org.openmarkov.core.model.network.*;
import org.openmarkov.core.model.network.modelUncertainty.ProbDensFunctionManager;
import org.openmarkov.core.model.network.potential.*;
import org.openmarkov.core.model.network.potential.plugin.PotentialUtils;
import org.openmarkov.gui.action.AugmentedPotentialValueEdit;
import org.openmarkov.gui.commonComponents.JComboBoxFunctionRender;
import org.openmarkov.gui.dialog.common.*;
import org.openmarkov.gui.exception.BinomialPotentialWrongValueException;
import org.openmarkov.gui.exception.NotEnoughtMemoryException;
import org.openmarkov.gui.graphic.VisualNode;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Dialog box to edit all type of potentials ( TablePotential and TreeADDs ). If
 * the potential is a utility role or uniform type, then no Values panel is
 * displayed. If potential is TreeADDpotential, then graphic edition panel is
 * showed.
 *
 * @author mpalacios
 * @author jmendoza
 * @author ibermejo
 * @version 1.3 cmyago 19/06/2016 - adapted the class to the new utility treatment; minor changes
 */
public class PotentialEditDialog extends OkCancelHorizontalDialog
        implements ActionListener, PanelResizeEventListener {
    /**
     *
     */
    private static final long serialVersionUID = -7344555059488539825L;
    /**
     * The JComboBox object that shows all the potentials types
     */
    private JComboBox<Class<? extends Potential>> potentialTypeComboBox;
    /**
     * The node edited
     */
    private Node node;
    /**
     * The panel that contains all the common option to potentials
     */
    private JPanel potentialTypePanel;
    private PolicyTypePanel pnlPolicyType;
    /**
     * Label for relation type
     */
    private JLabel lblPotentialType;
    /**
     * Panel of the graphic editor
     */
    private PotentialPanel potentialPanel;
    
    /**
     * Option deselected in the jComboboxRelationType
     */
    private int optionPreviouslySelected = 0;
    private Class<? extends Potential> previouslySelectedPotentialType = null;
    /**
     * If true, values inside the dialog will not be editable
     */
    private boolean readOnly;
    private JButton reorderVariablesButton;
    
    //For Univariate
    /**
     * The JComboBox object that shows all the potentials types
     */
    
    private JComboBox<String> univariateDistrComboBox;
    /**
     * Label for distribution type
     */
    private JLabel lblUnivariateDistrComboBox;
    
    /**
     *
     */
    private JComboBox<String> univariateDistrParametrizationComboBox;
    
    /**
     *
     */
    private JLabel lblParametrizationComboBox;
    /**
     *
     */
    private String previouslySelectedDistributionName = "Exact";
    
    
    private CommentHTMLScrollPane commentPane;
    
    private VisualNode visualNode;
    
    private Potential lastPotential;
    
    
    /**
     * Creates the dialog.
     */
    public PotentialEditDialog(Window owner, Node node, boolean newElement, boolean readOnly) {
        super(owner);
        this.node = node;
        this.lastPotential = node.getPotential();
        this.readOnly = readOnly;
        node.getProbNet().getPNESupport().setWithUndo(true);
        node.getProbNet().getPNESupport().openParenthesis();
        initialize();
        List<Potential> potentials = node.getPotentials();
        if (!potentials.isEmpty() && potentials.get(0).getComment() != null && !potentials.get(0).getComment()
                                                                                          .isEmpty()) {
            commentPane.setCommentHTMLTextPaneText(potentials.get(0).getComment());
        }
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        Rectangle bounds = owner.getBounds();
        int width = screenSize.width / 2;
        int height = screenSize.height / 2;
        // center point of the owner window
        int x = bounds.x / 2 - width / 2;
        int y = bounds.y / 2 - height / 2;
        this.setBounds(x, y, width, height);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(width, height / 2));
        setResizable(true);
        pack();
    }
    
    /**
     * Constructor
     */
    public PotentialEditDialog(Window owner, Node node, boolean newElement) {
        this(owner, node, newElement, false);
    }
    
    public PotentialEditDialog(Window owner, VisualNode visualNode) {
        super(owner);
        this.visualNode = visualNode;
        this.node = visualNode.getNode();
        node.getProbNet().getPNESupport().setWithUndo(true);
        node.getProbNet().getPNESupport().openParenthesis();
        List<Potential> potentials = node.getPotentials();
        if (!potentials.isEmpty() && potentials.get(0).getComment() != null && !potentials.get(0).getComment()
                                                                                          .isEmpty()) {
            commentPane.setCommentHTMLTextPaneText(potentials.get(0).getComment());
        }
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        Rectangle bounds = owner.getBounds();
        int width = screenSize.width / 2;
        int height = screenSize.height / 2;
        // center point of the owner window
        int x = bounds.x / 2 - width / 2;
        int y = bounds.y / 2 - height / 2;
        this.setBounds(x, y, width, height);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(width, height / 2));
        setResizable(true);
        pack();
    }
    
    /**
     * This method configures the dialog box.
     */
    private void initialize() {
        // Set default title
        setTitle("NodePotentialDialog.Title");
        configureComponentsPanel();
        pack();
    }
    
    /**
     * Sets up the panel where all components, except the buttons of the buttons
     * panel, will be appear.
     */
    protected void configureComponentsPanel() {
        getComponentsPanel().setLayout(new BorderLayout(5, 5));
        // getComponentsPanel().setSize(294, 29);
        getComponentsPanel().setMaximumSize(new Dimension(180, 40));
        getComponentsPanel().add(getPotentialTypePanel(), BorderLayout.NORTH);
        getComponentsPanel().add(getPotentialPanel(), BorderLayout.CENTER);
        
        // For univariate
        if (showUnivariateDistrComboBox()) {
            getUnivariateDistrJCombobox().setVisible(true);
            getUnivariateDistrJCombobox().setEnabled(true);
            getUnivariateDistrParametrizationJCombobox().setVisible(true);
            getUnivariateDistrParametrizationJCombobox().setEnabled(true);
        } else {
            getUnivariateDistrJCombobox().setVisible(false);
            getUnivariateDistrJCombobox().setEnabled(false);
            getUnivariateDistrParametrizationJCombobox().setVisible(false);
            getUnivariateDistrParametrizationJCombobox().setEnabled(false);
            
        }
        
        if (enableReorderVariableButton()) {
            getReorderVariablesButton().setVisible(true);
            getReorderVariablesButton().setEnabled(true);
        } else {
            getReorderVariablesButton().setVisible(false);
            getReorderVariablesButton().setEnabled(false);
        }
        getComponentsPanel().add(getCommentPane(), BorderLayout.SOUTH);
        
    }
    
    /**
     * @return label for the type of relations or policy
     */
    protected JLabel getPotentialTypeJLabel() {
        if (lblPotentialType == null) {
            lblPotentialType = new JLabel();
            lblPotentialType.setName("jLabelRelationType");
            lblPotentialType.setText("a Label");
            lblPotentialType.setText(stringDatabase.getString("NodeProbsValuesTablePanel.jLabelRelationType.Text"));
        }
        return lblPotentialType;
    }
    
    /**
     * @return ComboBox with the types of families of relation to be used
     */
    protected JComboBox<Class<? extends Potential>> getPotentialTypeJCombobox() {
        if (potentialTypeComboBox == null) {
            Class<? extends Potential> potentialClass = node.getPotentials().getFirst().getClass();
            List<Class<? extends Potential>> filteredPotentialNames = new ArrayList<>(PotentialUtils.getFilteredPotentialClasses(node));
            if (!filteredPotentialNames.contains(potentialClass)) {
                filteredPotentialNames.add(potentialClass);
            }
            filteredPotentialNames.sort(Comparator.comparing(PotentialUtils::getPotentialName));
            potentialTypeComboBox = new JComboBox<>(filteredPotentialNames.toArray(new Class[0]));
            potentialTypeComboBox.setRenderer(new JComboBoxFunctionRender<Class<? extends Potential>>(PotentialUtils::getPotentialName));
            potentialTypeComboBox.setSelectedItem(potentialClass);
            potentialTypeComboBox.setBorder(new LineBorder(UIManager.getColor("List.dropLineColor"), 1, false));
            potentialTypeComboBox.setName("jComboBoxRelationType");
            potentialTypeComboBox.addActionListener(evt -> potentialTypeChanged());
            potentialTypeComboBox.setEnabled(!readOnly);
        }
        return potentialTypeComboBox;
    }
    
    /**
     * Enables or disables the potential type combo box
     *
     * @param enable To indicate if the Potential Type combobox should be enabled
     */
    public void setEnabledPotentialTypeCombobox(boolean enable) {
        getPotentialTypeJCombobox().setEnabled(enable);
    }
    
    /**
     * Gets the panel that matches the type of potential to be edited
     *
     * @return the potential panel matching the potential edited.
     */
    protected PotentialPanel getPotentialPanel() {
        if (potentialPanel == null) {
            potentialPanel = PotentialPanelManager.getInstance().createPotentialPanel(node);
            potentialPanel.setReadOnly(readOnly);
            potentialPanel.suscribePanelResizeEventListener(this);
        }
        return potentialPanel;
    }
    
    @Override public void setTitle(String title) {
        String nodeName = (node == null) ? "" : node.getName();
        super.setTitle(stringDatabase.getString(title) + ": " + nodeName);
    }
    
    /**
     * @return An integer indicating the button clicked by the user when closing
     * this dialog
     */
    public int requestValues() throws
            IncompatibleEvidenceException.EvidenceIsIncompatibleWithOther,
            ThereIsNoPotentialsInNodeException, NotEnoughtMemoryException {
        // Shows the potentials' options table
        if (node.getNodeType() == NodeType.DECISION && node.getPolicyType() == PolicyType.OPTIMAL && readOnly) {
            setEnabledDecisionOptions(true);
        } else {
            showFields(node);
        }
        setVisible(true);
        return selectedButton;
    }
    
    /**
     * This method fills the content of the fields from a Node object. In
     * this method, when Elvira will be discontinued, the code for
     * discriminating discrete and discretized variables must be eliminated
     *
     * @param node object from where load the information.
     */
    // TODO Remove all this
    private void showFields(Node node) throws ThereIsNoPotentialsInNodeException, IncompatibleEvidenceException.EvidenceIsIncompatibleWithOther {
        // The element order in PotentialType object are same that
        // JComboBoxRelationType
        previouslySelectedPotentialType = node.getFirstPotential().getClass();
        getPotentialTypeJCombobox().setSelectedItem(previouslySelectedPotentialType);
        updatePotentialPanel();
        // Elvira do not distinguish between DISCRETE and DISCRETIZED
        // so here we will see if there are intervals in the states
        if (Util.hasLimitBracketSymbols(node.getVariable().getStates()) && (
                node.getVariable().getVariableType() == VariableType.FINITE_STATES
        )) {
            // really DISCRETIZED, so change the value of the VariableType
            node.getVariable().setVariableType(VariableType.DISCRETIZED);
        }
        // set the nodeProperties variable in this dialog and panels
        this.node = node;
        // *******
        getPotentialPanel().setData(node);
    }
    
    /**
     * @return The panel that indicates the type of the table (and perhaps the
     * type of policy (optimal or imposed))
     */
    protected JPanel getPotentialTypePanel() {
        if (potentialTypePanel == null) {
            potentialTypePanel = new JPanel();
            // jPanelRelationTableType.setBorder( new LineBorder( UIManager
            // .getColor( "List.dropLineColor" ), 1, false ) );
            potentialTypePanel.setLayout(new FlowLayout());
            potentialTypePanel.setSize(294, 29);
            potentialTypePanel.setName("potentialTypePanel");
            potentialTypePanel.add(getPotentialTypeJLabel());
            potentialTypePanel.add(getPotentialTypeJCombobox());
            //For Univariate
            potentialTypePanel.add(getUnivariateDistrTypeJLabel());
            potentialTypePanel.add(getUnivariateDistrJCombobox());
            potentialTypePanel.add(getParametrizationComboBoxJLabel());
            potentialTypePanel.add(getUnivariateDistrParametrizationJCombobox());
            potentialTypePanel.add(getReorderVariablesButton());
            // potentialTypePanel.add( getPoliticyTypePanel() );
            // /getPoliticyTypePanel().setVisible(false);
            // getPotentialPanel().setEnabled(false);
        }
        return potentialTypePanel;
    }
    
    //For Univariate
    
    /**
     * @return label for the type of relations or policy
     */
    protected JLabel getUnivariateDistrTypeJLabel() {
        if (lblUnivariateDistrComboBox == null) {
            lblUnivariateDistrComboBox = new JLabel();
            lblUnivariateDistrComboBox.setName("jLabelDistrType");
            lblUnivariateDistrComboBox.setText("Distribution");
            //TODO
            //lblDistrType.setText (stringDatabase.getValuesInAString ("NodeProbsValuesTablePanel.jLabelRelationType.Text"));
        }
        return lblUnivariateDistrComboBox;
    }
    
    private boolean showUnivariateDistrComboBox() {
        boolean enable = false;
        // We retrieve the necessary data from the node
        
        if (getPotentialPanel() instanceof UnivariateDistrPotentialPanel) {
            ProbDensFunctionManager probDensFunctionManager = ProbDensFunctionManager.getUniqueInstance();
            List<String> distributionUnivariateNames = probDensFunctionManager.getDistributions();
            Collections.sort(distributionUnivariateNames);
            univariateDistrComboBox
                    .setModel(new DefaultComboBoxModel<String>(distributionUnivariateNames.toArray(new String[0])));
            String univariateName = ((UnivariateDistrPotential) (node.getPotentials().get(0)))
                    .getProbDensFunctionUnivariateName();
            univariateDistrComboBox.setSelectedItem(univariateName);
            enable = true;
        }
        // Finally, the value of enable is returned
        return enable;
    }
    
    /**
     * @return The univariate distribution JComboBox
     */
    protected JComboBox<String> getUnivariateDistrJCombobox() {
        
        if (univariateDistrComboBox == null) {
            
            univariateDistrComboBox = new JComboBox<String>();
            univariateDistrComboBox.setBorder(new LineBorder(UIManager.getColor("List.dropLineColor"), 1, false));
            univariateDistrComboBox.setName("jComboBoxDistr");
            univariateDistrComboBox.addActionListener(new java.awt.event.ActionListener() {
                @Override public void actionPerformed(java.awt.event.ActionEvent evt) {
                    String univariateName = (String) univariateDistrComboBox.getSelectedItem();
                    showUnivariateDistrParametrizationComboBox(univariateName);
                }
            });
            univariateDistrComboBox.setEnabled(!readOnly);
        }
        return univariateDistrComboBox;
        
    }
    
    /**
     * @return The univariate distribution parametrization JComboBox
     */
    protected JComboBox<String> getUnivariateDistrParametrizationJCombobox() {
        
        if (univariateDistrParametrizationComboBox == null) {
            
            univariateDistrParametrizationComboBox = new JComboBox<String>();
            univariateDistrParametrizationComboBox
                    .setBorder(new LineBorder(UIManager.getColor("List.dropLineColor"), 1, false));
            univariateDistrParametrizationComboBox.setName("jComboBoxParametrization");
            univariateDistrParametrizationComboBox.addActionListener(evt -> {
                try {
                    distributionChanged();
                } catch (DoEditException e) {
                    throw new UnrecoverableException(e);
                }
            });
            univariateDistrParametrizationComboBox.setEnabled(!readOnly);
        }
        return univariateDistrParametrizationComboBox;
        
    }
    
    protected void distributionChanged() throws DoEditException {
        String distributionUnivariateName = (String) univariateDistrComboBox.getSelectedItem();
        String distributionParameters = (String) univariateDistrParametrizationComboBox.getSelectedItem();
        //When we are changing the distribution the first value should be selected
        
        String distributionName = ProbDensFunctionManager.getUniqueInstance()
                                                         .getDistributionName(distributionUnivariateName, distributionParameters);
        if (!previouslySelectedDistributionName.equals(distributionName)) {
            AugmentedPotentialValueEdit nodePotentialEdit = new AugmentedPotentialValueEdit(node, distributionName);
            nodePotentialEdit.executeEdit();
            updatePotentialPanel();
            previouslySelectedDistributionName = distributionName;
            
        }
    }
    
    /**
     * @return The parametrization ComboBox JLabel
     */
    protected JLabel getParametrizationComboBoxJLabel() {
        if (lblParametrizationComboBox == null) {
            lblParametrizationComboBox = new JLabel();
            lblParametrizationComboBox.setName("jLabelDistrType");
            lblParametrizationComboBox.setText("Parametrization");
            //TODO
            //lblParametrizationComboBox.setText (stringDatabase.getValuesInAString ("NodeProbsValuesTablePanel.jLabelRelationType.Text"));
        }
        return lblParametrizationComboBox;
    }
    
    /**
     * @return True iff it is enabled
     */
    private boolean showUnivariateDistrParametrizationComboBox(String univariateName) {
        // We retrieve the necessary data from the node
        //UNCLEAR this if is Necessary??
        ProbDensFunctionManager probDensFunctionManager = ProbDensFunctionManager.getUniqueInstance();
        List<String[]> parametrizationDataList = probDensFunctionManager.getParametrizations(univariateName);
        List<String> parametrizationNames = new ArrayList<String>();
        for (String[] parametrizationData : parametrizationDataList) {
            parametrizationNames.add(parametrizationData[0]);
        }
        Collections.sort(parametrizationNames);
        univariateDistrParametrizationComboBox
                .setModel(new DefaultComboBoxModel<String>(parametrizationNames.toArray(new String[0])));
        String parametrizationName;
        if (!univariateName
                .equals(((UnivariateDistrPotential) node.getPotentials().get(0)).getProbDensFunctionUnivariateName())) {
            parametrizationName = parametrizationNames.get(0);
        } else {
            parametrizationName = ((UnivariateDistrPotential) node.getPotentials().get(0))
                    .getProbDensFunctionParametrizationName();
        }
        
        univariateDistrParametrizationComboBox.setSelectedItem(parametrizationName);
        return true;
    }
    
    
    /**
     * @return The panel that indicates the type of the table (and perhaps the
     * type of policy (optimal or imposed))
     */
    protected JButton getReorderVariablesButton() {
        if (reorderVariablesButton == null) {
            reorderVariablesButton = new JButton(stringDatabase.getString("PotentialEditDialog.ReorderVariables.Text"));
            reorderVariablesButton.setName("reorderVariablesButton");
            // reorderVariablesButton.setVisible(false);
            reorderVariablesButton.addActionListener(this);
        }
        return reorderVariablesButton;
    }
    
    /**
     * This method initializes getCommentPane
     *
     * @return a new comment HTML scroll pane.
     */
    protected CommentHTMLScrollPane getCommentPane() {
        
        if (commentPane == null) {
            commentPane = new CommentHTMLScrollPane();
            commentPane.setName("commentPane");
            commentPane.setPreferredSize(new Dimension(10, 30));
        }
        return commentPane;
    }
    
    /**
     * @return PolicyTypePanel with three radio buttons with the types of
     * policy: optimal, deterministic, or probabilistic
     */
    protected PolicyTypePanel getPoliticyTypePanel() {
        if (pnlPolicyType == null) {
            pnlPolicyType = new PolicyTypePanel(this, node);
        }
        return pnlPolicyType;
    }
    
    protected void potentialTypeChanged() {
        Class<? extends Potential> potentialType = (Class<? extends Potential>) potentialTypeComboBox.getSelectedItem();
        if (!previouslySelectedPotentialType.equals(potentialType)) {
            
            Potential newPotential = instanciatePotential(potentialType);
            node.setPotentialConsistently(newPotential);
            
            updatePotentialPanel();
            previouslySelectedPotentialType = potentialType;
            optionPreviouslySelected = potentialTypeComboBox.getSelectedIndex();
            getComponentsPanel().add(getPotentialPanel(), BorderLayout.CENTER);
            getComponentsPanel().updateUI();
            getComponentsPanel().repaint();
            this.repaint();
            this.pack();
            
        }
    }
    
    /**
     * This method carries out the actions when the user presses the OK button
     * before hiding the dialog.
     *
     * @return true if all the fields are correct.
     */
    @Override
    protected boolean doOkClickBeforeHide() throws BinomialPotentialWrongValueException.ThetaValueIsWrong, BinomialPotentialWrongValueException.NValuesIsWrong, DoEditException {
        if (getPotentialPanel() instanceof TablePotentialPanel) {
            ((TablePotentialPanel) getPotentialPanel()).getValuesTable().stopCellEditing();
        }
        if (getPotentialPanel() instanceof ICIPotentialsTablePanel) {
            ((ICIPotentialsTablePanel) getPotentialPanel()).getICIValuesTable().stopCellEditing();
        }
        getPotentialPanel().saveChanges();
        if (commentPane.isChanged()) {
            // check if the comment is empty
            String comment = commentPane.isEmpty() ? "" : commentPane.getCommentText();
            node.getPotentials().get(0).setComment(comment);
        }
        SetPotentialEdit setPotentialEdit;
        setPotentialEdit = new SetPotentialEdit(node, lastPotential, node.getPotential());
        node.getProbNet().getPNESupport().closeParenthesis();
        node.getProbNet().getPNESupport().undo();
        setPotentialEdit.executeEdit();
        return true;
    }
    
    @Override protected void doCancelClickBeforeHide() {
        if (lastPotential != null)
            node.setPotentialConsistently(lastPotential);
        getPotentialPanel().close();
        node.getProbNet().getPNESupport().closeParenthesis();
    }
    
    /**
     * Update potential panel
     */
    public void updatePotentialPanel() {
        getComponentsPanel().remove(getPotentialPanel());
        potentialPanel.close();
        potentialPanel = null;
        // For Univariate
        if (showUnivariateDistrComboBox()) {
            getUnivariateDistrTypeJLabel().setVisible(true);
            getUnivariateDistrJCombobox().setVisible(true);
            getUnivariateDistrJCombobox().setEnabled(true);
            getParametrizationComboBoxJLabel().setVisible(true);
            getUnivariateDistrParametrizationJCombobox().setVisible(true);
            getUnivariateDistrParametrizationJCombobox().setEnabled(true);
        } else {
            getUnivariateDistrTypeJLabel().setVisible(false);
            getUnivariateDistrJCombobox().setVisible(false);
            getUnivariateDistrJCombobox().setEnabled(false);
            
            getParametrizationComboBoxJLabel().setVisible(false);
            getUnivariateDistrParametrizationJCombobox().setVisible(false);
            getUnivariateDistrParametrizationJCombobox().setEnabled(false);
            
        }
        
        if (enableReorderVariableButton()) {
            getReorderVariablesButton().setVisible(true);
            getReorderVariablesButton().setEnabled(true);
        } else {
            getReorderVariablesButton().setVisible(false);
            getReorderVariablesButton().setEnabled(false);
        }
        
        getComponentsPanel().add(getPotentialPanel(), BorderLayout.CENTER);
        getComponentsPanel().updateUI();
        getComponentsPanel().repaint();
        this.repaint();
        this.pack();
    }
    
    /**
     * Shows and activates the options related to decision policy
     *
     * @param show To indicate whether the options have to be shown and enabled or not
     */
    private void setEnabledDecisionOptions(boolean show) {
        if (show) {
            switch (node.getPolicyType()) {
                case OPTIMAL, DETERMINISTIC:
                    getPotentialTypeJCombobox().setEnabled(false);
                    break;
                case PROBABILISTIC:
                    Potential potential = node.getPotentials().get(0);
                    // TODO definir el comportamiento para los demás tipos de potenciales
                    if (potential instanceof UniformPotential || potential instanceof TablePotential) {
                        getPotentialTypeJCombobox().setSelectedItem(potential.getClass());
                        // getJComboBoxRelationType().setEnabled(false);
                    }
                    break;
            }
        }
        getPoliticyTypePanel().setEnabledDecisionOptions(show);
    }
    
    public void revertPotentialTypeChange() {
        getPotentialTypeJCombobox().setSelectedIndex(optionPreviouslySelected);
    }
    
    /**
     * @return the readOnly
     */
    public boolean isReadOnly() {
        return readOnly;
    }
    
    @Override public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(reorderVariablesButton)) {
            try {
                actionPerformedReorderVariables();
            } catch (DoEditException ex) {
                throw new UnrecoverableException(ex);
            }
        }
    }
    
    protected void actionPerformedReorderVariables() throws DoEditException {
        ReorderVariablesDialog reorderVariablesDialog = new ReorderVariablesDialog(this, node);
        if (reorderVariablesDialog.requestValues() == OkCancelHorizontalDialog.OK_BUTTON) {
            List<Variable> newVariables = reorderVariablesDialog.getReorderVariablesPanel().getVariables();
            PotentialPanel potentialPanelForAction = getPotentialPanel();
            Potential nodePotential = node.getPotentials().get(0);
            if (potentialPanelForAction instanceof TablePotentialPanel) {
                Potential potential = nodePotential.reorder(newVariables);
                SetPotentialEdit potentialEdit = new SetPotentialEdit(node, potential);
                ProbNet probNet = node.getProbNet();
                potentialEdit.executeEdit();
                updatePotentialPanel();
                
            } else if (potentialPanelForAction instanceof ICIPotentialsTablePanel) {
                SetPotentialVariablesEdit setPotentialVariables = new SetPotentialVariablesEdit(node, newVariables);
                ProbNet probNet = node.getProbNet();
                setPotentialVariables.executeEdit();
                updatePotentialPanel();
            }
        }
    }
    
    @Override public void panelSizeChanged(PanelResizeEvent event) {
        pack();
        repaint();
    }
    
    /**
     * This method computes if reorderVariableButton should be enabled
     *
     * @return true if the ReorderVariableButton should be enabled
     */
    private boolean enableReorderVariableButton() {
        boolean enable = false;
        // We retrieve the necessary data from the node
        Potential potential = node.getPotentials().get(0);
        int numPotentialVariables = potential.getNumVariables();
        
        if ((numPotentialVariables > 2) && getPotentialPanel() instanceof ProbabilityTablePanel) {
            enable = true;
        }
        // Finally, the value of enable is returned
        return enable;
    }
    
    protected Node getNode() {
        return node;
    }
    
    protected Potential instanciatePotential(Class<? extends Potential> potentialType) {
        lastPotential = node.getPotentials().get(0);
        assert potentialType != null;
        if (potentialType == CycleLengthShift.class) {
            return PotentialUtils.instanciateSafely(potentialType, lastPotential.getVariables(),
                                                    lastPotential.getPotentialRole(), node.getProbNet()
                                                                                          .getCycleLength());
        }
        return PotentialUtils.instanciateSafely(potentialType, lastPotential.getVariables(), lastPotential.getPotentialRole());
    }
    
    protected Class<? extends Potential> getPreviouslySelectedPotentialType() {
        return this.previouslySelectedPotentialType;
    }
    
    protected int getOptionPreviouslySelected() {
        return this.optionPreviouslySelected;
    }
    
    
}
