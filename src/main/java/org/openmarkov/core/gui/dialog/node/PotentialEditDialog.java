/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog.node;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import org.openmarkov.core.action.SetPotentialEdit;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.gui.dialog.common.ICIPotentialsTablePanel;
import org.openmarkov.core.gui.dialog.common.OkCancelApplyUndoRedoHorizontalDialog;
import org.openmarkov.core.gui.dialog.common.PolicyTypePanel;
import org.openmarkov.core.gui.dialog.common.PotentialPanel;
import org.openmarkov.core.gui.dialog.common.PotentialPanelManager;
import org.openmarkov.core.gui.dialog.common.TablePotentialPanel;
import org.openmarkov.core.gui.dialog.treeadd.TreeADDPanel;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.gui.util.Utilities;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.PolicyType;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.plugin.RelationType;
import org.openmarkov.core.model.network.potential.plugin.RelationTypeManager;

/**
 * Dialog box to edit all type of potentials ( TablePotential and TreeADDs ). 
 * If the potential is a utility role or uniform type, then no Values panel is displayed. 
 * If potential is TreeADDpotential, then graphic edition panel is showed. 
 * 
 * @author mpalacios
 * @author jmendoza
 * @author ibermejo
 * @version 1.0
 * @version 1.2 jlgozalo - set class to use independent panels;
 */
public class PotentialEditDialog extends OkCancelApplyUndoRedoHorizontalDialog {


    /**
     * 
     */
    private static final long serialVersionUID = -7344555059488539825L;
    
    /**
     * Dialog string resource.
     */
    private StringResource dialogStringResource;

    /**
     * The JComboBox object that shows all the potentials types 
     */
    private JComboBox potentialTypeComboBox;

    /**
     * The node edited
     */
    private ProbNode probNode;

    /**
     * Message string resource for i18n
     */
    private StringResource messageStringResource;

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
     * Relation Type Manager
     */
    RelationTypeManager relationTypeManager;
    
    /**
     * Panel of the graphic editor
     */
    private PotentialPanel potentialPanel;    
    
    /**
     * The builder object that contains UncertaintyPopup
     */
    //unused private PopupMenuFactory popupMenuFactory;

    /**
     * Option deselected in the jComboboxRelationType
     */
    private int optionPreviouslySelected = 0;

    private String previouslySelectedPotentialType = "";

    /**
     * Creates the dialog.
     */
    
    public PotentialEditDialog(Window owner, ProbNode probNode, boolean newElement) {
        super(owner);
        this.probNode = probNode;
        //TODO create PNESupport
        probNode.getProbNet().getPNESupport().openParenthesis();
        initialize();
        setLocationRelativeTo(owner);
        setMinimumSize(new Dimension( 750, 450 ));
        setResizable(true);

    }
    
    /**
     * This method configures the dialog box.
     */
    private void initialize() {

        relationTypeManager = new RelationTypeManager ();
        dialogStringResource =
            StringResourceLoader.getUniqueInstance().getBundleDialogs();
        messageStringResource =
            StringResourceLoader.getUniqueInstance().getBundleMessages();
        String title = dialogStringResource
                .getString("NodePotentialDialog.Title.Label");
        
        setTitle(dialogStringResource
            .getString("NodePotentialDialog.Title.Label")
            + ": " + (probNode == null? "":probNode.getName()));
       
        configureComponentsPanel();
        pack();
    }
    
    /**
     * Sets up the panel where all components, except the buttons of the buttons
     * panel, will be appear.
     */
    private void configureComponentsPanel() {
        getComponentsPanel().setLayout(new BorderLayout(5, 5));
        getComponentsPanel().add(getPotentialTypePanel(), BorderLayout.NORTH );
        getComponentsPanel().add(getPotentialPanel (), BorderLayout.CENTER);
    }
    
    /**
     * @return label for the type of relations or policy
     */
    protected JLabel getPotentialTypeJLabel() {

        if (lblPotentialType == null) {
            lblPotentialType = new JLabel();
            lblPotentialType.setName( "jLabelRelationType" );
            lblPotentialType.setText( "a Label" );
            lblPotentialType.setText( dialogStringResource.getString( 
                    "NodeProbsValuesTablePanel.jLabelRelationType.Text" ) );
        }
        return lblPotentialType;
    }
    /**
     * @return ComboBox with the types of families of relation to be used
     */
    protected JComboBox getPotentialTypeJCombobox() {

        if (potentialTypeComboBox == null) {
            List<String> filteredPotentialNames = relationTypeManager.getFilteredPotentials (probNode); 
            potentialTypeComboBox = new JComboBox (filteredPotentialNames.toArray ());
//            potentialTypeComboBox = new JComboBox( relationTypeManager.getAllPotentialsNames ().toArray () );
            
            potentialTypeComboBox.setBorder( new LineBorder( UIManager.getColor(
                    "List.dropLineColor" ), 1, false ) );
            potentialTypeComboBox.setName( "jComboBoxRelationType" );
            potentialTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    potentialTypeComboBoxActionPerformed(evt);
                }
            });
        }
        return potentialTypeComboBox;
    }
    
    /**
     * Enables or disables the potential type combo box
     * @param enable
     */
    public void setEnabledPotentialTypeCombobox(boolean enable)
    {
        getPotentialTypeJCombobox() .setEnabled (enable);
    }

    /**
     * Gets the panel that matches the type of potential to be edited 
     * @return the potential panel matching the potential edited.
     */
    private PotentialPanel getPotentialPanel () {

        if(potentialPanel == null)
        {
            String potentialName = (String) potentialTypeComboBox.getSelectedItem ();
            String potentialFamily = relationTypeManager.getPotentialsFamily (potentialName);
            potentialPanel = PotentialPanelManager.getInstance ().getPotentialPanel(potentialName, potentialFamily, probNode);
        }
        return potentialPanel;
    }
    
    /**
     * @return An integer indicating the button clicked by the user when closing this dialog
     */
    public int requestValues() {
        // Shows the potentials' options table
        if (!(probNode.getNodeType() == NodeType.DECISION && 
                probNode.getPolicyType() == PolicyType.OPTIMAL)){
            showFields(probNode);
        }else{
            setEnabledDecisionOptions(true);
        }
        setVisible(true);
        return selectedButton;
    }
    
    /**
     * This method fills the content of the fields from a ProbNode object.
     * In this method, when Elvira will be discontinued, the code for
     * discriminating discrete and discretized variables must be eliminated
     * 
     * @param probNode
     *            object from where load the information.
     */
    // TODO Remove all this
    private void showFields(ProbNode probNode) {

        //The element order in PotentialType object are same that 
        //JComboBoxRelationType
        previouslySelectedPotentialType =  probNode.getPotentials ().get (0).getClass ().getAnnotation (RelationType.class).name ();        
        getPotentialTypeJCombobox ().setSelectedItem (previouslySelectedPotentialType);     
        updatePotentialPanel (); 
        
        // Elvira do not distinguish between DISCRETE and DISCRETIZED
        // so here we will see if there are intervals in the states
        if (Utilities.hasLimitBracketSymbols(probNode.getVariable().getStates())
                        && (probNode.getVariable().getVariableType() == 
                            VariableType.FINITE_STATES)) {
            // really DISCRETIZED, so change the value of the VariableType
            probNode.getVariable().setVariableType(VariableType.DISCRETIZED);
        }

        // set the nodeProperties variable in this dialog and panels
        this.probNode = probNode;
        //*******
        setTitle(dialogStringResource.getString(
                "NodePotentialDialog.Title.Label")+ ": " + probNode.getName());

        getPotentialPanel().setData( probNode );
        //updatePotentialPanel();
        if ( probNode.getNodeType() == NodeType.DECISION ){
            setEnabledDecisionOptions(true);
        }
    }
    /**
     * @return The panel that indicates the type of the table 
     * (and perhaps the type of policy (optimal or imposed))
     */
    protected JPanel getPotentialTypePanel() {

        if (potentialTypePanel == null) {
            potentialTypePanel = new JPanel();
            //jPanelRelationTableType.setBorder( new LineBorder( UIManager
                //.getColor( "List.dropLineColor" ), 1, false ) );
            potentialTypePanel.setLayout( new FlowLayout() );
            potentialTypePanel.setSize( 294, 29 );
            potentialTypePanel.setName( "potentialTypePanel" );
            potentialTypePanel.add(getPotentialTypeJLabel());
            potentialTypePanel.add(getPotentialTypeJCombobox());
            potentialTypePanel.add( getPoliticyTypePanel() );
        }
        return potentialTypePanel;
    }
    /**
     * @return PolicyTypePanel with three radio buttons with the types of policy:
     * optimal, deterministic, or probabilistic
     */
    protected PolicyTypePanel getPoliticyTypePanel() {

        if (pnlPolicyType == null) {
            pnlPolicyType = new PolicyTypePanel(this, probNode);
        }
        return pnlPolicyType;
    }
    
    
    protected void potentialTypeComboBoxActionPerformed (ActionEvent evt)
    {
        String potentialType =  (String) potentialTypeComboBox.getSelectedItem();
        if(!previouslySelectedPotentialType.equals (potentialType))
        {
            SetPotentialEdit setPotentialEdit = new SetPotentialEdit (probNode, potentialType);
            try
            {
                probNode.getProbNet ().doEdit (setPotentialEdit);
            }
            catch (ConstraintViolationException e1)
            {
                JOptionPane.showMessageDialog (this,
                                               messageStringResource.getString (e1.getMessage ()),
                                               messageStringResource.getString ("ConstraintViolationException"),
                                               JOptionPane.ERROR_MESSAGE);
                revertPotentialTypeChange ();
                potentialTypeComboBox.requestFocus ();
            }
            catch (Exception e1)
            {
                e1.printStackTrace ();
            }
            updatePotentialPanel ();        
            optionPreviouslySelected = potentialTypeComboBox.getSelectedIndex ();
        }

    }    
    
    /**
     * This method carries out the actions when the user presses the OK button
     * before hiding the dialog.
     * 
     * @return true if all the fields are correct.
     * @throws NotEnoughMemoryException 
     */
    @Override
    protected boolean doOkClickBeforeHide() throws NotEnoughMemoryException {
        getPotentialPanel ().saveChanges ();
        probNode.getProbNet().getPNESupport().closeParenthesis();
        return true;
    }
    
    @Override
    protected void doCancelClickBeforeHide() {
        probNode.getProbNet().getPNESupport().closeParenthesis();
    }
    
    /**
     * Update potential panel
     */
    public void updatePotentialPanel() {
        getComponentsPanel ().remove (getPotentialPanel ());
        potentialPanel = null;
        getComponentsPanel ().add (getPotentialPanel (), BorderLayout.CENTER);
        getComponentsPanel ().updateUI ();
        getComponentsPanel ().repaint ();
        this.repaint ();
        this.pack ();
    }
    
    /**
     * Shows and activated the options related to decision policy
     * @param show
     */
    private void setEnabledDecisionOptions (boolean show)
    {
        if (show)
        {
            switch (probNode.getPolicyType ())
            {
                case OPTIMAL :
                    getPotentialTypeJCombobox ().setEnabled (false);
                    break;
                case DETERMINISTIC :
                    getPotentialTypeJCombobox ().setEnabled (false);
                    break;
                case PROBABILISTIC :
                    Potential potential = probNode.getPotentials ().get (0);
                    switch (potential.getPotentialType ())
                    {
                        case UNIFORM :
                        case TABLE :
                            getPotentialTypeJCombobox ().setSelectedIndex (potential.getPotentialType ().getType ());
                            // getJComboBoxRelationType().setEnabled(false);
                            break;
                    // TODO definir el comportamiento para los demás tipos de
                    // potenciales
                    }
                    break;
            }
        }
        getPoliticyTypePanel ().setEnabledDecisionOptions (show);
    }
    
    public void revertPotentialTypeChange()
    {
        getPotentialTypeJCombobox().setSelectedIndex(optionPreviouslySelected);
    }

    
  /*  public Potential getNewPotential(){
    	PotentialPanel potentialPanel = getPotentialPanel();
    	
    	if (potentialPanel instanceof ICIPotentialsTablePanel) {
    		return ((ICIPotentialsTablePanel)potentialPanel).getThisICIPotential();
    	}else if (potentialPanel instanceof TablePotentialPanel) {
    		
    	}else if (potentialPanel instanceof TreeADDPanel) {}
    		
    }*/
}
