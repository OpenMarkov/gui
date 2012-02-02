/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog.node;


import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.LayoutStyle;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.UndoableEditEvent;


import org.openmarkov.core.action.PNUndoableEditEvent;
import org.openmarkov.core.action.PNUndoableEditListener;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.gui.dialog.common.CPTablePanel;
import org.openmarkov.core.gui.dialog.common.CommentHTMLScrollPane;
import org.openmarkov.core.gui.dialog.common.ICIPotentialsTablePanel;
import org.openmarkov.core.gui.dialog.common.PotentialsTablePanel;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.PolicyType;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialType;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.canonical.ICIPotential;



/**
 * Panel for the relation/probabilities tables (if chance node), utility values
 * (if utility node) or policy values (if decision node)
 * 
 * @author jlgozalo
 * @author maryebra
 * @version 1.0 jlgozalo
 */
public class ICIOptionsPanel extends JPanel implements 
	PNUndoableEditListener{

	
	/**
	 * serial uid
	 */
	private static final long serialVersionUID = 1047978130482205148L;
	/**
	 * to identify what is the panel container it could be CPTTablePanel or ICIPotentialsTablePanel
	 */
	Container parentPanel;
	/**
	 * remember the last model selected (probabilistic, deterministic or optimal
	 */
	/**
	 * 
	 */
	private CPTablePanel cpTablePanel;
	
	private int prevModelPolicySelected = -1;
	private static int PROBABILISTIC_SELECTED = 0;
	private static int DETERMINISTIC_SELECTED = 1;
	private static int OPTIMAL_SELECTED = 2;
	/**
	 * remember the last model selected canonical or TPC
	 */
	private int previousModel = -1;
	private static int CANONICAL = 0;
	private static int TPC = 1;
	
	/**
	 * Dialog string resource.
	 */
	private StringResource dialogStringResource;
	
	/**
	 * Specifies if the node whose adittionalProperties are edited is new.
	 */
	private boolean newNode = false;

	
	/** buttongroups of the options * */
	private ButtonGroup buttonGroupNetOrCompound = null;
	private ButtonGroup buttonGroupAllOrIndependent = null;
	private ButtonGroup buttonGroupProbabilisticOrDeterministicOrOptimal = null;
	private ButtonGroup buttonGroupTpcOrCanonical = null;
	private ButtonGroup buttonGroupProbabilityOrValue = null;

	/** radio buttons for the different options of the panel * */
	
	private JRadioButton jRadioButtonNeto;
	private JRadioButton jRadioButtonCompound;
	private JRadioButton jRadioButtonTPC;
	private JRadioButton jRadioButtonCanonical;
	private JRadioButton jRadioButtonProbabilities;
	private JRadioButton jRadioButtonValues;
	private JRadioButton jRadioButtonIndependent;
	private JRadioButton jRadioButtonAll;

	/** the different options panel * */
	private JPanel jPanelNetoOrCompound;
	private JPanel jPanelTpcOrCanonical;
	private JPanel jPanelProbabilityOrValue;
	private JPanel jPanelAllOrIndependant;
	private JPanel jPanelRelationTableType;

	/**
	 * Comment Scroll Panel box and its label
	 */
	private JLabel jLabelNodeRelationComment;
	private CommentHTMLScrollPane commentHTMLScrollPaneNodeProbsComment = null;

	/**
	 * the probability table panel
	 */
	private PotentialsTablePanel nodePotentialsTablePanel;

	/**
	 * object to manage the ItemChange events of the panel
	 */
	//private TablePotentialPanelListenerAssistant listener = null;
	private ICIOptionListenerAssistant listener;

	private ProbNode probNode;

	private JPopupMenu uncertaintyPopup;

	/**
	 * constructor without construction parameters
	 */
	public ICIOptionsPanel() {

		this( true); //new ElementObservable() );

	}

	/**
	 * constructor without construction parameters
	 */
	public ICIOptionsPanel( ProbNode probNode) {

		this( true);//, notifier );
		this.probNode = probNode;
		probNode.getProbNet().getPNESupport().addUndoableEditListener(this);
		
		try {
			initialize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		
	}

	/**
	 * This method initialises this instance.
	 * 
	 * @param newNode
	 *            true if the node is a new node; otherwise false
	 */
	public ICIOptionsPanel(final boolean newNode){
		dialogStringResource =
			StringResourceLoader.getUniqueInstance().getBundleDialogs();
		
		this.newNode = newNode;
		this.listener = new ICIOptionListenerAssistant(this, probNode);
		
		//this.listener =
		//	new TablePotentialPanelListenerAssistant( this );
	

	}

	/**
	 * <p>
	 * <code>Initialize</code>
	 * <p>
	 * initialize the layout for this panel
	 */
	private void initialize() throws Exception {

		//setPreferredSize( new Dimension( 700, 375 ) );
		final GroupLayout groupLayout = new GroupLayout( (JComponent) this );
		groupLayout
			.setHorizontalGroup( groupLayout
				.createParallelGroup( GroupLayout.Alignment.LEADING )
				.addGroup(
					groupLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
							groupLayout
								.createParallelGroup(
									GroupLayout.Alignment.LEADING )
								.addGroup(
									groupLayout
										.createSequentialGroup()
										.addComponent(
											getJLabelNodeRelationComment() )
										.addPreferredGap(
											LayoutStyle.ComponentPlacement.RELATED )
										.addComponent(
											getCommentHTMLScrollPaneNodeDefinitionComment(),
											GroupLayout.DEFAULT_SIZE, 623,
											Short.MAX_VALUE ) )
								/*.addComponent(
									getNodePotentialsTablePanel(),GroupLayout.DEFAULT_SIZE, 184,
									Short.MAX_VALUE )*/
								.addGroup(
									groupLayout
										.createSequentialGroup()
										.addComponent(
											getJPanelTpcOrCanonical(),
											GroupLayout.PREFERRED_SIZE, 196,
											GroupLayout.PREFERRED_SIZE )
										.addPreferredGap(
											LayoutStyle.ComponentPlacement.RELATED )
										.addComponent(
											getJPanelNetoOrCompound(),
											GroupLayout.PREFERRED_SIZE, 137,
											GroupLayout.PREFERRED_SIZE )
										.addPreferredGap(
											LayoutStyle.ComponentPlacement.RELATED )
										.addComponent(
											getJPanelProbabilityOrValue(),
											GroupLayout.PREFERRED_SIZE, 151,
											GroupLayout.PREFERRED_SIZE )
										.addPreferredGap(
											LayoutStyle.ComponentPlacement.RELATED )
										.addComponent(
											getJPanelAllOrIndependant(),
											GroupLayout.DEFAULT_SIZE, 174,
											Short.MAX_VALUE ) ) 						
						).addContainerGap() ) );
		groupLayout.setVerticalGroup( groupLayout.createParallelGroup(
			GroupLayout.Alignment.LEADING ).addGroup(
			groupLayout.createSequentialGroup().addContainerGap().addGroup(
				groupLayout.createParallelGroup( GroupLayout.Alignment.LEADING )
				.addGroup(
					groupLayout.createParallelGroup(
						GroupLayout.Alignment.LEADING ).addComponent(
						getJPanelAllOrIndependant(), GroupLayout.DEFAULT_SIZE,
						58, Short.MAX_VALUE ).addComponent(
						getJPanelProbabilityOrValue(),
						GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE )
						.addComponent(
							getJPanelNetoOrCompound(),
							GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE )
						.addComponent(
							getJPanelTpcOrCanonical(),
							GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE ) ))
				/*.addPreferredGap( LayoutStyle.ComponentPlacement.RELATED )
				.addComponent(
					getNodePotentialsTablePanel(), GroupLayout.DEFAULT_SIZE,
					184, Short.MAX_VALUE )*/.addPreferredGap(
					LayoutStyle.ComponentPlacement.RELATED ).addGroup(
					groupLayout.createParallelGroup(
						GroupLayout.Alignment.LEADING ).addComponent(
						getJLabelNodeRelationComment() ).addComponent(
						getCommentHTMLScrollPaneNodeDefinitionComment(),
						GroupLayout.PREFERRED_SIZE, 60,
						GroupLayout.PREFERRED_SIZE ) 
						).addContainerGap() ));
		setLayout( groupLayout );
	}

	
	/**
	 * @return the panel with the two buttons
	 */
	protected JPanel getJPanelTpcOrCanonical() {

		if (jPanelTpcOrCanonical == null) {
			jPanelTpcOrCanonical = new JPanel();
			jPanelTpcOrCanonical.setLayout( null );
			//jPanelTpcOrCanonical.setSize( 152, 58 );
			jPanelTpcOrCanonical.setBorder( new LineBorder( UIManager
				.getColor( "List.dropLineColor" ), 1, false ) );
			jPanelTpcOrCanonical.setName( "jPanelTpcOrCanonical" );
			initButtonGroupTpcOrCanonical();
			jPanelTpcOrCanonical.add( getJRadioButtonCanonical() );
			jPanelTpcOrCanonical.add( getJRadioButtonTPC() );
			jPanelTpcOrCanonical.setEnabled( true);
		}
		return jPanelTpcOrCanonical;
	}

	/**
	 * iniatilize the button group TPC or Canonical
	 */
	private void initButtonGroupTpcOrCanonical() {

		buttonGroupTpcOrCanonical = new ButtonGroup();
		buttonGroupTpcOrCanonical.add( getJRadioButtonTPC() );
		buttonGroupTpcOrCanonical.add( getJRadioButtonCanonical() );
	}

	/**
	 * @return the button for the TPC option to be displayed
	 */
	public JRadioButton getJRadioButtonTPC() {

		if (jRadioButtonTPC == null) {
			jRadioButtonTPC = new JRadioButton();
			jRadioButtonTPC.setBounds( 1, 1, 189, 24 );
			jRadioButtonTPC.setName( "jRadioButtonTPC" );
			jRadioButtonTPC.setText( "New JRadioButton" );
			jRadioButtonTPC.setText( dialogStringResource
				.getString( "NodeProbsValuesTablePanel.jRadioButtonTPC.Text" ) );
			jRadioButtonTPC.addItemListener( this.listener );
			jRadioButtonTPC.setEnabled( true );
		}
		return jRadioButtonTPC;
	}

	/**
	 * @return the button for the Canonical option to be displayed
	 */
	public JRadioButton getJRadioButtonCanonical() {

		if (jRadioButtonCanonical == null) {
			jRadioButtonCanonical = new JRadioButton();
			jRadioButtonCanonical.setBounds( 1, 25, 189, 24 );
			jRadioButtonCanonical.setName( "jRadioButtonCanonical" );
			jRadioButtonCanonical.setText( "New JRadioButton" );
			jRadioButtonCanonical
				.setText( dialogStringResource.getString( 
						"NodeProbsValuesTablePanel.jRadioButtonCanonical.Text" ) );
			jRadioButtonCanonical.addItemListener( this.listener );
			jRadioButtonCanonical.setEnabled( true );
			//jRadioButtonCanonical.setSelected( true );
		}
		return jRadioButtonCanonical;
	}

	/**
	 * @return the panel for net or compound values to be displayed
	 */
	protected JPanel getJPanelNetoOrCompound() {

		if (jPanelNetoOrCompound == null) {
			jPanelNetoOrCompound = new JPanel();
			jPanelNetoOrCompound.setLayout( null );
			//jPanelNetoOrCompound.setSize( 172, 58 );
			jPanelNetoOrCompound.setBorder( new LineBorder( UIManager
				.getColor( "List.dropLineColor" ), 1, false ) );
			jPanelNetoOrCompound.setName( "jPanelNetoOrCompound" );
			initButtonGroupNetOrCompound();
			jPanelNetoOrCompound.add( getJRadioButtonCompound() );
			jPanelNetoOrCompound.add( getJRadioButtonNeto() );
			jPanelNetoOrCompound.setEnabled( false );
		}
		return jPanelNetoOrCompound;
	}

	/**
	 * iniatilize the button group Net or Compound
	 */
	private void initButtonGroupNetOrCompound() {

		buttonGroupNetOrCompound = new ButtonGroup();
		buttonGroupNetOrCompound.add( getJRadioButtonNeto() );
		buttonGroupNetOrCompound.add( getJRadioButtonCompound() );
	}

	/**
	 * @return the button for the Net values to be displayed
	 */
	protected JRadioButton getJRadioButtonNeto() {

		if (jRadioButtonNeto == null) {
			jRadioButtonNeto = new JRadioButton();
			jRadioButtonNeto.setBounds( 1, 1, 134, 24 );
			jRadioButtonNeto.setName( "jRadioButtonNeto" );
			jRadioButtonNeto.setText( "New JRadioButton" );
			jRadioButtonNeto
				.setText( dialogStringResource.getString( 
						"NodeProbsValuesTablePanel.jRadioButtonNeto.Text" ) );
			//jRadioButtonNeto.addItemListener( this.listener );
			jRadioButtonNeto.setEnabled( false );
		}
		return jRadioButtonNeto;
	}

	/**
	 * @return the button for the Compound values to be displayed
	 */
	protected JRadioButton getJRadioButtonCompound() {

		if (jRadioButtonCompound == null) {
			jRadioButtonCompound = new JRadioButton();
			jRadioButtonCompound.setBounds( 1, 25, 134, 24 );
			jRadioButtonCompound.setName( "jRadioButtonCompound" );
			jRadioButtonCompound.setText( "New JRadioButton" );
			jRadioButtonCompound
				.setText( dialogStringResource.getString( 
						"NodeProbsValuesTablePanel.jRadioButtonCompound.Text" ) );
			//jRadioButtonCompound.addItemListener( this.listener );
			jRadioButtonCompound.setEnabled( false );
		}
		return jRadioButtonCompound;
	}

	/**
	 * @return the panel with the two buttons Probability or Value to be shown
	 */
	protected JPanel getJPanelProbabilityOrValue() {

		if (jPanelProbabilityOrValue == null) {
			jPanelProbabilityOrValue = new JPanel();
			jPanelProbabilityOrValue.setLayout( null );
			//jPanelProbabilityOrValue.setSize( 143, 58 );
			jPanelProbabilityOrValue.setBorder( new LineBorder( UIManager
				.getColor( "List.dropLineColor" ), 1, false ) );
			jPanelProbabilityOrValue.setName( "jPanelProbabilityOrValue" );
			initButtonGroupProbabilityOrValue();
			jPanelProbabilityOrValue.add( getJRadioButtonValues() );
			jPanelProbabilityOrValue.add( getJRadioButtonProbabilities() );
			jPanelProbabilityOrValue.setEnabled( false );// default
		}
		return jPanelProbabilityOrValue;
	}

	/**
	 * iniatilize the button group Probability Or Value
	 */
	private void initButtonGroupProbabilityOrValue() {

		buttonGroupProbabilityOrValue = new ButtonGroup();
		buttonGroupProbabilityOrValue.add( getJRadioButtonProbabilities() );
		buttonGroupProbabilityOrValue.add( getJRadioButtonValues() );
	}

	/**
	 * @return the button for the Probabilities values to be displayed
	 */
	protected JRadioButton getJRadioButtonProbabilities() {

		if (jRadioButtonProbabilities == null) {
			jRadioButtonProbabilities = new JRadioButton();
			jRadioButtonProbabilities.setBounds( 1, 1, 149, 24 );
			jRadioButtonProbabilities.setName( "jRadioButtonProbabilities" );
			jRadioButtonProbabilities.setText( "New JRadioButton" );
			jRadioButtonProbabilities
				.setText( dialogStringResource.getString( 
						"NodeProbsValuesTablePanel.jRadioButtonProbabilities." +
						"Text" ) );
			//jRadioButtonProbabilities.addItemListener( this.listener );
			jRadioButtonProbabilities.setEnabled( false );
		}
		return jRadioButtonProbabilities;
	}

	/**
	 * @return the button for the Non-Numerical values to be displayed
	 */
	protected JRadioButton getJRadioButtonValues() {

		if (jRadioButtonValues == null) {
			jRadioButtonValues = new JRadioButton();
			jRadioButtonValues.setBounds( 1, 25, 149, 24 );
			jRadioButtonValues.setName( "jRadioButtonValues" );
			jRadioButtonValues.setText( "New JRadioButton" );
			jRadioButtonValues
				.setText( dialogStringResource.getString( 
						"NodeProbsValuesTablePanel.jRadioButtonValues.Text" ) );
			//jRadioButtonValues.addItemListener( this.listener );
			jRadioButtonValues.setEnabled( false );
		}
		return jRadioButtonValues;
	}

	/**
	 * @return a panel for all or independant buttons to be selected
	 */
	protected JPanel getJPanelAllOrIndependant() {

		if (jPanelAllOrIndependant == null) {
			jPanelAllOrIndependant = new JPanel();
			jPanelAllOrIndependant.setName( "jPanelAllOrIndependant" );
			jPanelAllOrIndependant.setLayout( null );
			//jPanelAllOrIndependant.setSize( 110, 58 );
			jPanelAllOrIndependant.setBorder( new LineBorder( UIManager
				.getColor( "List.dropLineColor" ), 1, false ) );
			jPanelAllOrIndependant.setName( "jPanelAllOrIndependant" );
			initButtonGroupAllOrIndependent();
			jPanelAllOrIndependant.add( getJRadioButtonAll() );
			jPanelAllOrIndependant.add( getJRadioButtonIndependent() );
			jPanelAllOrIndependant.setEnabled( false );
		}
		return jPanelAllOrIndependant;
	}

	/**
	 * iniatilize the button group All or Independent
	 */
	private void initButtonGroupAllOrIndependent() {

		buttonGroupAllOrIndependent = new ButtonGroup();
		buttonGroupAllOrIndependent.add( getJRadioButtonAll() );
		buttonGroupAllOrIndependent.add( getJRadioButtonIndependent() );
	}

	/**
	 * @return the button for the All parameters to be selected
	 */
	protected JRadioButton getJRadioButtonAll() {

		if (jRadioButtonAll == null) {
			jRadioButtonAll = new JRadioButton();
			jRadioButtonAll.setName( "jRadioButtonAll" );
			jRadioButtonAll.setText( "New JRadioButton" );
			jRadioButtonAll.setBounds( 1, 1, 170, 24 );
			jRadioButtonAll.setText( dialogStringResource
				.getString( "NodeProbsValuesTablePanel.jRadioButtonAll.Text" ) );
		//	jRadioButtonAll.addItemListener( this.listener );
			jRadioButtonAll.setEnabled( false );
			jRadioButtonAll.setSelected( false );
		}
		return jRadioButtonAll;
	}

	/**
	 * @return the button for the Independent parameters to be selected
	 */
	protected JRadioButton getJRadioButtonIndependent() {

		if (jRadioButtonIndependent == null) {
			jRadioButtonIndependent = new JRadioButton();
			jRadioButtonIndependent.setName( "jRadioButtonIndependent" );
			jRadioButtonIndependent.setText( "New JRadioButton" );
			jRadioButtonIndependent.setBounds( 1, 25, 170, 24 );
			jRadioButtonIndependent
				.setText( dialogStringResource.getString( 
						"NodeProbsValuesTablePanel.jRadioButtonIndependant.Text" ) );
			//jRadioButtonIndependent.addItemListener( this.listener );
			jRadioButtonIndependent.setEnabled( false );
		}
		return jRadioButtonIndependent;
	}

	/**
	 * @return panel with the table of probabilistic values
	 */
	public PotentialsTablePanel getNodePotentialsTablePanel() {

		if (nodePotentialsTablePanel == null) {
			nodePotentialsTablePanel = new PotentialsTablePanel(probNode);// default
			nodePotentialsTablePanel.setName( "nodePotentialsTablePanel" );
		}
		return nodePotentialsTablePanel;
	}

	/**
	 * @return label for the node relation comment
	 */
	protected JLabel getJLabelNodeRelationComment() {

		if (jLabelNodeRelationComment == null) {
			jLabelNodeRelationComment = new JLabel();
			jLabelNodeRelationComment.setName( "jLabelNodeRelationComment" );
			jLabelNodeRelationComment.setText( "a Label" );
			jLabelNodeRelationComment
				.setText( dialogStringResource.getString( 
						"NodeProbsValuesTablePanel.jLabelNodeRelationComment.Text" ) );
		}
		return jLabelNodeRelationComment;
	}

	/**
	 * This method initialises commentHTMLScrollPaneNodeDefinitionComment
	 * 
	 * @return a new comment HTML scroll pane.
	 */
	protected CommentHTMLScrollPane getCommentHTMLScrollPaneNodeDefinitionComment() {

		if (commentHTMLScrollPaneNodeProbsComment == null) {
			commentHTMLScrollPaneNodeProbsComment = new CommentHTMLScrollPane();
			commentHTMLScrollPaneNodeProbsComment.setName( 
					"commentHTMLScrollPaneNodeProbsComment" );
		}
		return commentHTMLScrollPaneNodeProbsComment;
	}

	/**
	 * @return the dialogStringResource
	 */
	protected StringResource getDialogStringResource() {

		return dialogStringResource;
	}

	/**
	 * @param dialogStringResource
	 *            the dialogStringResource to set
	 */
	protected void setDialogStringResource(StringResource dialogStringResource) {

		this.dialogStringResource = dialogStringResource;
	}

	/**
	 * @return the prevModelPolicySelected
	 */
	protected int getPrevModelPolicySelected() {

		return prevModelPolicySelected;
	}

	/**
	 * @param prevModelPolicySelected
	 *            the prevModelPolicySelected to set
	 */
	protected void setPrevModelPolicySelected(int prevModelPolicySelected) {

		this.prevModelPolicySelected = prevModelPolicySelected;
	}

	/**
	 * this method hides all non required graphical components when a decision
	 * node is selected
	 */
	protected void hideElementsWhenIsDecisionNodeOrUniformPotential() {

		this.nodePotentialsTablePanel.showValuesTable( false );
	}
	

	/**
	 * this method hides all non required graphical components when a utility
	 * node is selected
	 */
	protected void hideElementsWhenIsUtilityNode() {

		//this.jLabelRelationType.setEnabled( false );
		//this.jRadioButtonOptimal.setEnabled( false );
		//this.jRadioButtonProbabilisticType.setEnabled( false );
		//this.jRadioButtonDeterministicType.setEnabled( false );
		hideAllOptionsPanels();
	}

	/**
	 * this method hides all non required graphical components when Optimal is
	 * selected
	 */
	protected void hideElementsWhenOptimalSelected() {

		//this.jLabelRelationType.setEnabled( false );
		//this.jRadioButtonProbabilisticType.setEnabled( false );
		//this.jRadioButtonDeterministicType.setEnabled( false );
		this.nodePotentialsTablePanel.showValuesTable( false );
		this.nodePotentialsTablePanel.setEnabled( true );
		hideAllOptionsPanels();
	}

	protected void hideAllOptionsPanels() {

		this.jPanelTpcOrCanonical.setEnabled( false );
		this.jRadioButtonTPC.setEnabled( false );
		this.jRadioButtonTPC.setSelected( false );
		this.jRadioButtonCanonical.setEnabled( false );
		this.jRadioButtonCanonical.setSelected( false );
		this.jPanelNetoOrCompound.setEnabled( false );
		this.jRadioButtonNeto.setEnabled( false );
		this.jRadioButtonNeto.setSelected( false );
		this.jRadioButtonCompound.setEnabled( false );
		this.jRadioButtonCompound.setSelected( false );
		this.jPanelProbabilityOrValue.setEnabled( false );
		this.jRadioButtonProbabilities.setEnabled( false );
		this.jRadioButtonProbabilities.setSelected( false );
		this.jRadioButtonValues.setEnabled( false );
		this.jRadioButtonValues.setSelected( false );
		this.jPanelAllOrIndependant.setEnabled( false );
		this.jRadioButtonAll.setEnabled( false );
		this.jRadioButtonAll.setSelected( false );
		this.jRadioButtonIndependent.setEnabled( false );
		this.jRadioButtonIndependent.setSelected( false );

	}

	/**
	 * this methods shows all required graphical components when Optimal is
	 * deselected
	 */
	protected void showElementsWhenOptimalDeselected() {

		//this.jLabelRelationType.setEnabled( true );
		this.nodePotentialsTablePanel.showValuesTable( true );
		this.nodePotentialsTablePanel.setEnabled( true );
	}

	/**
	 * @return the newNode
	 */
	public boolean isNewNode() {

		return newNode;
	}

	/**
	 * @param newNode
	 *            the newNode to set
	 */
	public void setNewNode(boolean newNode) {

		this.newNode = newNode;
	}

	/**
	 * This method fills the content of the fields from a ProbNode object.
	 * 
	 * @param node object from where load the information.
	 */
	public void setFieldsFromNode( ProbNode node ) {

		if ( (node.getNodeType() == NodeType.DECISION) && 
			(node.getPolicyType() == PolicyType.OPTIMAL)){
			hideElementsWhenIsDecisionNodeOrUniformPotential();
			//this.getJRadioButtonOptimal().setEnabled( true );
			//this.getJRadioButtonOptimal().setSelected( true );
			this.setPrevModelPolicySelected( OPTIMAL_SELECTED );
		}else {
			
			if ( probNode.getPotentials().get(0).getPotentialType() == 
				PotentialType.TABLE ) {
				getNodePotentialsTablePanel().setData(node );
			}else {
					hideElementsWhenIsDecisionNodeOrUniformPotential();
			}
				
			if (node.getNodeType() == NodeType.UTILITY) {
				hideElementsWhenIsUtilityNode();
				if ( probNode.getPotentials().get(0).getPotentialType() == 
					PotentialType.PRODUCT ){
					hideElementsWhenIsDecisionNodeOrUniformPotential();
				}
			} else if (node.getNodeType() == NodeType.CHANCE) {
				//TODO activar la opción correspondiente
				//this.jRadioButtonProbabilisticType.setSelected( true );
				this.prevModelPolicySelected = PROBABILISTIC_SELECTED;
			}

		}

	}
	public class ICIOptionListenerAssistant implements ItemListener{
	/**
	 * Identifies the radio button affected by the event.
	 * @param e
	 */
		private ICIOptionsPanel iciOptionPanel;
		private Container parentPanel;
		private ProbNode probNode;
		private ProbNode probNodeCPT;
		private CPTablePanel cpTablePanel;
		
		
			
		public ICIOptionListenerAssistant(ICIOptionsPanel iciOptionPanel, ProbNode probNode){
			this.iciOptionPanel = iciOptionPanel;
			this.probNode = probNode;
		}
	
	public void itemStateChanged(ItemEvent e) {
		//to identify what is the panel container it could be CPTTablePanel or ICIPotentialsTablePanel
		this.parentPanel = iciOptionPanel.getParent();
		if (e.getItem().equals(getJRadioButtonTPC())) {
			itemStateChangedTPC(e);
		}
		if (e.getItem().equals(getJRadioButtonCanonical())) {
			itemStateChangedCanonical(e);
		}
	}
	
		
	private void itemStateChangedCanonical(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.DESELECTED){
			//has been deselected canonical
			previousModel = CANONICAL;
		}else if (e.getStateChange() == ItemEvent.SELECTED ){}
		if ( previousModel == CANONICAL) {
			//do nothing
		} else if (previousModel == TPC) { //tpc --> Canonical
			//show ICIPotentialsTablePanel
			
			
		} else {//first selection -1 it could be also from ICIPotentialsTablePanel
			//show ICIPotentialsTablePanel and allow edition
			if (parentPanel instanceof ICIPotentialsTablePanel) {
				//no changes
			}
		}
	}

	private void itemStateChangedTPC(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.DESELECTED){
			//has been deselected tpc
			previousModel = TPC;			
		}else if (e.getStateChange() == ItemEvent.SELECTED ){}
			if ( previousModel == CANONICAL) { //Canonical --> tpc
				//show TPC do not allow edit
			} else if (previousModel == TPC) {
				//do nothing
			} else {//first selection -1 it could be also from ICIPotentialsTablePanel
				//show TPCTablePanel 
				//this is the first time it is created a CPTablePanel
				previousModel = TPC;
				if (parentPanel instanceof ICIPotentialsTablePanel) {
					ICIPotential iciPotential = (ICIPotential)probNode.getPotentials().get(0);
					try {
						TablePotential tablePotential = (TablePotential)iciPotential.getCPT();
						//New table potential from iciPotential 
						ArrayList<Potential> potentials = new ArrayList<Potential>();
						potentials.add(tablePotential);
						probNodeCPT =  probNode;
						probNodeCPT.setPotentials(potentials);
						
					} catch (NotEnoughMemoryException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					this.cpTablePanel = new CPTablePanel(probNodeCPT, iciOptionPanel);
					cpTablePanel.repaint();
				}
			}
	}
	
	}
	
	/*private CPTablePanel getCPTablePanel() {
		if (cpTablePanel == null) {
			cpTablePanel = new CPTablePanel(probNode);
			boolean newNode = true;
			cpTablePanel.setNewNode(newNode);
			//tablePotentialPanel.setNodeProperties(probNode);
		}
		
		return cpTablePanel;
	}*/

	public void undoableEditHappened(UndoableEditEvent arg0) {
		//TODO Actualiza la tabla cuando se agrega/elimna un padre/ estado
		//Pero si la tabla vas estar en otro cuadro de dialogo, ésto ya no 
		//es necesario
		//setFieldsFromProperties(probNode);
	}

	
	public void undoableEditWillHappen(PNUndoableEditEvent event)
			throws ConstraintViolationException, CanNotDoEditException {
		
		
	}

	
	public void undoEditHappened(PNUndoableEditEvent event) {
		// TODO Auto-generated method stub
		
	}

	


}

