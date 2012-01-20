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
import java.awt.ItemSelectable;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoableEdit;

import org.openmarkov.core.action.PNUndoableEditEvent;
import org.openmarkov.core.action.PNUndoableEditListener;
import org.openmarkov.core.action.SetPotentialEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.dialog.common.OkCancelApplyUndoRedoHorizontalDialog;
import org.openmarkov.core.gui.dialog.common.PolicyTypePanel;
import org.openmarkov.core.gui.dialog.common.PotentialPanel;
import org.openmarkov.core.gui.dialog.common.PotentialPanelManager;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.gui.util.Utilities;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.PolicyType;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.CycleLengthShift;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialType;
import org.openmarkov.core.model.network.potential.ProductPotential;
import org.openmarkov.core.model.network.potential.SameAsPrevious;
import org.openmarkov.core.model.network.potential.plugin.RelationType;
import org.openmarkov.core.model.network.potential.plugin.RelationTypeManager;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDPotential;

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
public class PotentialEditDialog extends OkCancelApplyUndoRedoHorizontalDialog 
    implements PNUndoableEditListener {


    /**
     * 
     */
    private static final long serialVersionUID = -7344555059488539825L;

    private JLabel lblNodeRelationComment;
    
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
     * Panel of the graphic editor of Tree - ADDs
     */
    private JPanel nodeADDPotentialPanel;
    
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


    /**
     * Creates the dialog.
     */
    
    public PotentialEditDialog(Window owner, ProbNode probNode, boolean newElement) {
        super(owner);
        this.probNode = probNode;
        probNode.getProbNet().getPNESupport().addUndoableEditListener(this);
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

        dialogStringResource =
            StringResourceLoader.getUniqueInstance().getBundleDialogs();
        messageStringResource =
            StringResourceLoader.getUniqueInstance().getBundleMessages();
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
     * Gets the label object for the Comment object. If it does not exist,
     * creates it
     * @return
     *      the label object 
     */      
    private JLabel getCommentJLabel() {

        if (lblNodeRelationComment == null) {
            lblNodeRelationComment = new JLabel();
            lblNodeRelationComment.setName( "jLabelNodeRelationComment" );
            lblNodeRelationComment.setText( "a Label" );
            lblNodeRelationComment
                .setText( dialogStringResource.getString( 
                        "NodeProbsValuesTablePanel.jLabelNodeRelationComment.Text" ) );
        }
        return lblNodeRelationComment;
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
            RelationTypeManager relationTypeManager = new RelationTypeManager (); 
            potentialTypeComboBox =
                new JComboBox( relationTypeManager.getAllPotentialsNames ().toArray () );
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
    // TODO no debe depender de ningún tipo de potencial concreto; usar anotaciones
    private PotentialPanel getPotentialPanel () {

        if(potentialPanel == null)
        {
            PotentialPanelManager potentialPanelManager = new PotentialPanelManager(); 
            potentialPanel = potentialPanelManager.getPotentialPanel((String) potentialTypeComboBox.getSelectedItem (), probNode);
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
        probNode.getProbNet ().getPNESupport ().removeUndoableEditListener (getPotentialPanel ().getValuesTable ());
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

        PotentialType potentialType = probNode.getPotentials().get( 0 ).
            getPotentialType();
        //The element order in PotentialType object are same that 
        //JComboBoxRelationType 
        getPotentialTypeJCombobox ().setSelectedItem (probNode.getPotentials ().get (0).getClass ().getAnnotation (RelationType.class).name ());        
        
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
                "NodePropertiesDialog.Title.Label")+ ": " + probNode.getName());

        getPotentialPanel().setFieldsFromNode( probNode );
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
    // TODO Create a new class PolicyTypePanel and move all the code relative to it
    protected PolicyTypePanel getPoliticyTypePanel() {

        if (pnlPolicyType == null) {
            pnlPolicyType = new PolicyTypePanel(this, probNode);
        }
        return pnlPolicyType;
    }
    
    
    protected void potentialTypeComboBoxActionPerformed (ActionEvent evt)
    {
        String potentialType =  (String) potentialTypeComboBox.getSelectedItem();
        // TODO Each potential type will have to know which combination of variables it accepts
/*                if ((!(probNode.getVariable().isTemporal()) ||
                probNode.getVariable().getTimeSlice()==0)
                &&  (potentialType == PotentialType.SAME_AS_PREVIOUS || 
                    potentialType == PotentialType.CYCLE_LENGTH_SHIFT)){
            comboBox.removeItemListener(this);
            comboBox.setSelectedIndex(optionDeselected);
            comboBox.addItemListener(this);
            JOptionPane.showMessageDialog (this,
                                           messageStringResource.getString ("Potential undefined for no "
                                                                            + "temporal variables or time slice 0"),
                                           messageStringResource.getString ("Variable potential message"),
                                           JOptionPane.INFORMATION_MESSAGE);
            //TODO agregar método en potencial que compruebe si puede aplicarse a un conjunto de variables
        } else if (( probNode.getVariable().getVariableType() == VariableType.NUMERIC
                && probNode.getNodeType() == NodeType.CHANCE)
                &&  !(potentialType == PotentialType.UNIFORM || 
                potentialType == PotentialType.SAME_AS_PREVIOUS || 
                potentialType == PotentialType.CYCLE_LENGTH_SHIFT)){
            
            JOptionPane.showMessageDialog (this,
                                           messageStringResource.getString ("Potential undefined for numeric "
                                                                            + "variables"),
                                           messageStringResource.getString ("Variable potential"),
                                           JOptionPane.INFORMATION_MESSAGE);
                comboBox.removeItemListener(this);
                comboBox.setSelectedIndex(optionDeselected);
                comboBox.addItemListener(this);
        }
        else if (!(probNode.getNodeType () == NodeType.UTILITY)
                 && potentialType == PotentialType.PRODUCT)
        {
            comboBox.removeItemListener (this);
            comboBox.setSelectedIndex (optionDeselected);
            comboBox.addItemListener (this);
            JOptionPane.showMessageDialog (this,
                                           messageStringResource.getString ("Potential undefined for no "
                                                                            + "utility variables"),
                                           messageStringResource.getString ("Variable potential message"),
                                           JOptionPane.INFORMATION_MESSAGE);
                                
                                //comboBox.requestFocus(); 
        } else {*/
                SetPotentialEdit setPotentialEdit = new SetPotentialEdit(probNode, potentialType);
                try {
                    probNode.getProbNet().doEdit(setPotentialEdit );
                    } catch (ConstraintViolationException e1) {
                        JOptionPane.showMessageDialog(this, messageStringResource.getString( e1.getMessage() ),
                                        messageStringResource.getString( 
                                        "ConstraintViolationException" ),
                                        JOptionPane.ERROR_MESSAGE );
                        revertPotentialTypeChange();
                        potentialTypeComboBox.requestFocus();

                    } catch (CanNotDoEditException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } catch (DoEditException e1) {
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
                getComponentsPanel().remove (getPotentialPanel ());
                potentialPanel = null;
                getComponentsPanel().add(getPotentialPanel (), BorderLayout.CENTER);
//                }
        
        optionPreviouslySelected = potentialTypeComboBox.getSelectedIndex ();

    }    
    
    
    private void itemStateChangedPotentialType(ItemEvent e) {
        ItemSelectable itemSelectable = e.getItemSelectable();
        Object selected[] = itemSelectable.getSelectedObjects();
        String itemSelected = selected.length == 0 ? "null" :
            selected[0].toString();
        JComboBox comboBox= (JComboBox)e.getSource();
        if (e.getStateChange() == ItemEvent.DESELECTED){
            String h = e.getItem().toString();
            optionPreviouslySelected = comboBox.getSelectedIndex();
        }
            
        if (comboBox.getName().equals( "jComboBoxRelationType" )){
            
            if (!(itemSelected == null) && e.getStateChange() == ItemEvent.
                SELECTED){
                
 
            }
        }
        
    }
    
    // TODO Mover
    private boolean isValidPotentialType(PotentialType potentialType, Variable variable){
        if ( ( variable.isTemporal() && variable.getTimeSlice()>0 &&
                !(potentialType == PotentialType.CYCLE_LENGTH_SHIFT ||
                        potentialType == PotentialType.CYCLE_LENGTH_SHIFT ) )){
            return false;
        }
        if ( variable.getVariableType() == VariableType.NUMERIC && 
                potentialType == PotentialType.UNIFORM){
            return true;
        }
        if ( probNode.getNodeType() == NodeType.UTILITY && isProductNode() &&  
                potentialType == PotentialType.PRODUCT){
            return true;
        }
        
        return true;
    }
    
    // TODO Mover
    private boolean isProductNode() {
        //if some one of the parents are not utility node
        ArrayList<Node> parents = probNode.getNode().getParents();
        if ( parents.size() > 0 ){
            for (Node node:parents){
                if ( ((ProbNode)node.getObject()).getNodeType() != NodeType.UTILITY ){
                    return false;
                }
            }
        }else{
            return false;
        }
                
        return true;
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
        if (probNode.getPotentials().get(0) instanceof TreeADDPotential ){

        }
    
        probNode.getProbNet().getPNESupport().closeParenthesis();
        probNode.getProbNet().getPNESupport().removeUndoableEditListener(this);
        return true;
    }
    
    @Override
    protected void doCancelClickBeforeHide() {
        probNode.getProbNet().getPNESupport().closeParenthesis();
        probNode.getProbNet().getPNESupport().removeUndoableEditListener(this);
        
    }
    
    public void undoableEditHappened(UndoableEditEvent e) {
        UndoableEdit edit = e.getEdit();
        //getPotentialPanel().removeAll();
        if ( edit instanceof SetPotentialEdit ){
            /*this.getJComboBoxRelationType().setSelectedIndex((
                    (SetPotentialEdit) edit ).getNewPotentialType ().ordinal());*/
            Potential newPotential = ( ( SetPotentialEdit) edit ).getNewPotential();
            if (newPotential instanceof TreeADDPotential){
                // TODO iñigo
                /*if ( iciOptionsPanel != null ){
                    getComponentsPanel().remove(iciOptionsPanel);
                }*/
                getComponentsPanel().add(getPotentialPanel (), BorderLayout.CENTER);
                //TODO desactivar las opciones de decisión
            }else{
                if ( nodeADDPotentialPanel != null ){
                    getComponentsPanel().remove( nodeADDPotentialPanel );
                }
                
                getComponentsPanel().add(getPotentialPanel (), BorderLayout.CENTER);
                if ( !(newPotential instanceof SameAsPrevious) && !(newPotential 
                        instanceof CycleLengthShift) && !(newPotential instanceof 
                                ProductPotential) ){
                    getPotentialPanel().setFieldsFromNode(probNode);
                    if (probNode.getNodeType() == NodeType.DECISION){
                        setEnabledDecisionOptions(true);
                    }else{
                        setEnabledDecisionOptions(false);
                    }
                }else{ 
                    // TODO iñigo
                    /*iciOptionsPanel.
                        hideElementsWhenIsDecisionNodeOrUniformPotential();*/
                }
            }
            getComponentsPanel().updateUI();
            getComponentsPanel().repaint();
            this.repaint();
            this.pack();
        }
        
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
    
    public void undoableEditWillHappen(PNUndoableEditEvent event)
            throws ConstraintViolationException, CanNotDoEditException {
        // TODO Auto-generated method stub
        
    }
    
    public void undoEditHappened(PNUndoableEditEvent event) {
        // TODO Auto-generated method stub
        
    }
    public int getPreviousPolicy() {
        // TODO Remove
        return 0;
    }
    public void setShowNetValues(boolean b) {
        // TODO Remove ???
        
    }

}
