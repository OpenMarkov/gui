/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog.node;


import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
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
import org.openmarkov.core.gui.dialog.common.CommentHTMLScrollPane;
import org.openmarkov.core.gui.dialog.common.PotentialsTablePanel;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.PolicyType;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.potential.PotentialType;



/**
 * Panel for the relation/probabilities tables (if chance node), utility values
 * (if utility node) or policy values (if decision node)
 * 
 * @author jlgozalo
 * @version 1.0 jlgozalo
 */
public class TablePotentialPanel extends JPanel implements 
	PNUndoableEditListener{

	/**
	 * serial uid
	 */
	private static final long serialVersionUID = 1047978130482205148L;

	/**
	 * remember the last model selected (probabilistic, deterministic or optimal
	 */
	private int prevModelPolicySelected = -1;
	private static int PROBABILISTIC_SELECTED = 0;
	private static int DETERMINISTIC_SELECTED = 1;
	private static int OPTIMAL_SELECTED = 2;

	/**
	 * Dialog string resource.
	 */
	private StringResource dialogStringResource;
	

	/**
	 * Object where all information will be saved.
	 */
	private ProbNode nodeProperties = null;

	/**
	 * Specifies if the node whose adittionalProperties are edited is new.
	 */
	private boolean newNode = false;

	
	private JComboBox jComboBoxRelationType;

	/** buttongroups of the options * */
	private ButtonGroup buttonGroupNetOrCompound = null;
	private ButtonGroup buttonGroupAllOrIndependent = null;
	private ButtonGroup buttonGroupProbabilisticOrDeterministicOrOptimal = null;
	private ButtonGroup buttonGroupTpcOrCanonical = null;
	private ButtonGroup buttonGroupProbabilityOrValue = null;

	/** radio buttons for the different options of the panel * */
	//private JRadioButton jRadioButtonOptimal;
	//private JRadioButton jRadioButtonProbabilisticType;
	//private JRadioButton jRadioButtonDeterministicType;
	private JRadioButton jRadioButtonNeto;
	private JRadioButton jRadioButtonCompound;
	private JRadioButton jRadioButtonTPC;
	private JRadioButton jRadioButtonCanonical;
	private JRadioButton jRadioButtonProbabilities;
	private JRadioButton jRadioButtonValues;
	private JRadioButton jRadioButtonIndependant;
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

	private ProbNode probNode;

	private JPopupMenu uncertaintyPopup;

	/**
	 * constructor without construction parameters
	 */
	public TablePotentialPanel() {

		this( true); //new ElementObservable() );

	}

	/**
	 * constructor without construction parameters
	 */
	public TablePotentialPanel( ProbNode probNode) {

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
	public TablePotentialPanel(final boolean newNode){
		dialogStringResource =
			StringResourceLoader.getUniqueInstance().getBundleDialogs();
		
		this.newNode = newNode;
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
								.addComponent(
									getNodePotentialsTablePanel(),GroupLayout.DEFAULT_SIZE, 184,
									Short.MAX_VALUE )
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
				.addPreferredGap( LayoutStyle.ComponentPlacement.RELATED )
				.addComponent(
					getNodePotentialsTablePanel(), GroupLayout.DEFAULT_SIZE,
					184, Short.MAX_VALUE ).addPreferredGap(
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
			jPanelTpcOrCanonical.setEnabled( false );
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
	protected JRadioButton getJRadioButtonTPC() {

		if (jRadioButtonTPC == null) {
			jRadioButtonTPC = new JRadioButton();
			jRadioButtonTPC.setBounds( 1, 1, 189, 24 );
			jRadioButtonTPC.setName( "jRadioButtonTPC" );
			jRadioButtonTPC.setText( "New JRadioButton" );
			jRadioButtonTPC.setText( dialogStringResource
				.getString( "NodeProbsValuesTablePanel.jRadioButtonTPC.Text" ) );
		//	jRadioButtonTPC.addItemListener( this.listener );
			jRadioButtonTPC.setEnabled( false );
		}
		return jRadioButtonTPC;
	}

	/**
	 * @return the button for the Canonical option to be displayed
	 */
	protected JRadioButton getJRadioButtonCanonical() {

		if (jRadioButtonCanonical == null) {
			jRadioButtonCanonical = new JRadioButton();
			jRadioButtonCanonical.setBounds( 1, 25, 189, 24 );
			jRadioButtonCanonical.setName( "jRadioButtonCanonical" );
			jRadioButtonCanonical.setText( "New JRadioButton" );
			jRadioButtonCanonical
				.setText( dialogStringResource.getString( 
						"NodeProbsValuesTablePanel.jRadioButtonCanonical.Text" ) );
	//		jRadioButtonCanonical.addItemListener( this.listener );
			jRadioButtonCanonical.setEnabled( false );
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
			jPanelAllOrIndependant.add( getJRadioButtonIndependant() );
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
		buttonGroupAllOrIndependent.add( getJRadioButtonIndependant() );
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
			jRadioButtonAll.setSelected( true );
		}
		return jRadioButtonAll;
	}

	/**
	 * @return the button for the Independant parameters to be selected
	 */
	protected JRadioButton getJRadioButtonIndependant() {

		if (jRadioButtonIndependant == null) {
			jRadioButtonIndependant = new JRadioButton();
			jRadioButtonIndependant.setName( "jRadioButtonIndependant" );
			jRadioButtonIndependant.setText( "New JRadioButton" );
			jRadioButtonIndependant.setBounds( 1, 25, 170, 24 );
			jRadioButtonIndependant
				.setText( dialogStringResource.getString( 
						"NodeProbsValuesTablePanel.jRadioButtonIndependant.Text" ) );
			//jRadioButtonIndependant.addItemListener( this.listener );
			jRadioButtonIndependant.setEnabled( true );
		}
		return jRadioButtonIndependant;
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
		// MIGUEL: this.jComboBoxRelationType.setEnabled( false );
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
		this.jComboBoxRelationType.setEnabled( false );
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
		this.jRadioButtonIndependant.setEnabled( false );
		this.jRadioButtonIndependant.setSelected( false );

	}

	/**
	 * this methods shows all required graphical components when Optimal is
	 * deselected
	 */
	protected void showElementsWhenOptimalDeselected() {

		//this.jLabelRelationType.setEnabled( true );
		//this.jComboBoxRelationType.setEnabled( true );
		this.nodePotentialsTablePanel.showValuesTable( true );
		this.nodePotentialsTablePanel.setEnabled( true );
	}

	/**
	 * Get the node Properties in this panel
	 * 
	 * @return the nodeProperties
	 */
	public ProbNode getNodeProperties() {

		return nodeProperties;
	}

	/**
	 * Set the node adittionalProperties in this panel with the provided ones
	 * 
	 * @param nodeProperties
	 *            the nodeProperties to set
	 */
	public void setNodeProperties(final ProbNode nodeProperties) {

		this.nodeProperties = nodeProperties;
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
	 * This method fills the content of the fields from a NodeProperties object.
	 * 
	 * @param adittionalProperties
	 *            object from where load the information.
	 */
	public void setFieldsFromProperties( ProbNode properties ) {

		if ( (properties.getNodeType() == NodeType.DECISION) && 
			(properties.getPolicyType() == PolicyType.OPTIMAL)){
			hideElementsWhenIsDecisionNodeOrUniformPotential();
			//this.getJRadioButtonOptimal().setEnabled( true );
			//this.getJRadioButtonOptimal().setSelected( true );
			this.setPrevModelPolicySelected( OPTIMAL_SELECTED );
		}else {
			
			if ( probNode.getPotentials().get(0).getPotentialType() == 
				PotentialType.TABLE ) {
				getNodePotentialsTablePanel().setData(properties );
			}else {
					hideElementsWhenIsDecisionNodeOrUniformPotential();
			}
				
			if (properties.getNodeType() == NodeType.UTILITY) {
				hideElementsWhenIsUtilityNode();
				if ( probNode.getPotentials().get(0).getPotentialType() == 
					PotentialType.PRODUCT ){
					hideElementsWhenIsDecisionNodeOrUniformPotential();
				}
			} else if (properties.getNodeType() == NodeType.CHANCE) {
				//TODO activar la opción correspondiente
				//this.jRadioButtonProbabilisticType.setSelected( true );
				this.prevModelPolicySelected = PROBABILISTIC_SELECTED;
			}

		}

	}

	
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

