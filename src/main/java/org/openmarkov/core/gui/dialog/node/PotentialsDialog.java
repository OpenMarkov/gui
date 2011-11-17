package org.openmarkov.core.gui.dialog.node;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.ItemSelectable;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoableEdit;


import org.openmarkov.core.action.PNUndoableEditEvent;
import org.openmarkov.core.action.PNUndoableEditListener;
import org.openmarkov.core.action.RemovePolicyEdit;
import org.openmarkov.core.action.SetPotentialEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.component.AlgorithmRelationTypes;
import org.openmarkov.core.gui.dialog.common.OkCancelApplyUndoRedoHorizontalDialog;
import org.openmarkov.core.gui.dialog.treeadd.TreeADDBuilder;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.gui.menutoolbar.menu.PopupMenuFactory;
import org.openmarkov.core.gui.network.Util;
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
import org.openmarkov.core.model.network.potential.treeadd.TreeADDPotential;

/**
 * Dialog box to edit all type of potentials ( TablePotential and TreeADDs ). 
 * If the potential is a utility role or uniform type, then no Values panel is displayed. 
 * If potential is TreeADDpotential, then graphic edition panel is showed. 
 * 
 * @author mpalacios
 * @author jmendoza
 * @version 1.0
 * @version 1.2 jlgozalo - set class to use independent panels;
 */
public class PotentialsDialog extends OkCancelApplyUndoRedoHorizontalDialog 
	implements ItemListener, PNUndoableEditListener {


	private JLabel jLabelNodeRelationComment;
	
	/**
	 * Dialog string resource.
	 */
	private StringResource dialogStringResource;

	/**
	 * The JComboBox object that shows all the potentials types 
	 */
	private JComboBox jComboBoxRelationType;

	/**
	 * Panel that contains the panel where probability table are. It is used to
	 * place the fields at the top of the panel.
	 */
	private TablePotentialPanel tablePotentialPanel;

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
	private JPanel jPanelRelationTableType;

	/**
	 * Option to set as probabilistic the potential
	 */
	private JRadioButton jRadioButtonProbabilisticType;

	/**
	 * Option to set as deterministic the potential
	 */
	private JRadioButton jRadioButtonDeterministicType;

	/**
	 * Option to set as Optimal the potential
	 */
	private JRadioButton jRadioButtonOptimal;

	/**
	 * Label for relation type
	*/
	private JLabel jLabelRelationType;
	
	
	private JPanel jPanelRadioPolicyType;

	private ButtonGroup buttonGroupRelationType;

	/**
	 * Panel of the graphic editor of Tree - ADDs
	 */
	private JPanel nodeADDPotentialPanel;
	
	/**
	 * The builder object that contains UncertaintyPopup
	 */
	private PopupMenuFactory popupMenuFactory;

	/**
	 * The builder object of Tree - ADDs
	 */
	private TreeADDBuilder treeADDBuilder;
	/**
	 * Option deselected in the jComboboxRelationType
	 */
	private int optionDeselected = 0;

	private PolicyType previousPolicy;

	/**
	 * Creates the dialog.
	 */
	
	public PotentialsDialog(Window owner, ProbNode probNode, boolean newElement) {
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
		getComponentsPanel().add( getJPanelRelationType(), BorderLayout.NORTH );
		getComponentsPanel().add(getPotentialPanel (), BorderLayout.CENTER);
	}
	/**
	 * Gets the label object for the RelationComment object. if do not exists
	 * create it
	 * @return
	 * 		the label object 
	 */		 
	private JLabel getJLabelNodeRelationComment() {

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
	 * @return label for the type of relations or policy
	 */
	protected JLabel getJLabelRelationType() {

		if (jLabelRelationType == null) {
			jLabelRelationType = new JLabel();
			jLabelRelationType.setName( "jLabelRelationType" );
			jLabelRelationType.setText( "a Label" );
			jLabelRelationType.setText( dialogStringResource.getString( 
					"NodeProbsValuesTablePanel.jLabelRelationType.Text" ) );
		}
		return jLabelRelationType;
	}
	/**
	 * @return ComboBox with the types of families of relation to be used
	 */
	protected JComboBox getJComboBoxRelationType() {

		if (jComboBoxRelationType == null) {
			jComboBoxRelationType =
				new JComboBox( AlgorithmRelationTypes.getListStrings() );
			jComboBoxRelationType.setBorder( new LineBorder( UIManager.getColor(
					"List.dropLineColor" ), 1, false ) );
			jComboBoxRelationType.setName( "jComboBoxRelationType" );
			//jComboBoxRelationType.addItemListener( this.listener );
			

		}
		return jComboBoxRelationType;
	}
	/**
	 * This method initializes tablePotentialPanel.
	 * 
	 * @return a TablePotentialPanel instance
	 */
	private TablePotentialPanel getTablePotentialPanel() {

		if (tablePotentialPanel == null) {
			tablePotentialPanel = new TablePotentialPanel(probNode);
			boolean newNode = true;
			tablePotentialPanel.setNewNode(newNode);
			//tablePotentialPanel.setNodeProperties(probNode);
		}
		
		return tablePotentialPanel;
	}
	/**
	 * This method initializes nodeADDPotentialPanel and treeADDBuilder.
	 * 
	 * @return a panel that contains a tresADDBuilder object
	 */
	private JPanel getADDPotentialPanel() {

		if (nodeADDPotentialPanel == null) {
			nodeADDPotentialPanel = new JPanel ();
			nodeADDPotentialPanel.setLayout(new BorderLayout());
			treeADDBuilder = new TreeADDBuilder ( 
					(TreeADDPotential)probNode.getPotentials().get( 0 ));
			nodeADDPotentialPanel.add( treeADDBuilder, BorderLayout.CENTER );
			nodeADDPotentialPanel.setName("nodeTreeADDPotentialPanel");
			nodeADDPotentialPanel.setBackground(Color.blue);
			boolean newNode = true;
			//nodeADDPotentialPanel.setNewNode(newNode);
			//nodeADDPotentialPanel.setNodeProperties(probNode);
		}
		
		return nodeADDPotentialPanel;
	}
	/**
	 * Gets the panel that matches the type of potential edited 
	 * @return
	 * 	the potential panel matching the potential edited.
	 */		
	private JPanel getPotentialPanel () {
		//Decision node could no has potential
		if ( probNode.getPotentials().size()>0 && probNode.getPotentials().
				get(0) instanceof TreeADDPotential ){
			return getADDPotentialPanel ();
		}else{
			return getTablePotentialPanel ();
		}
	}
	/**
	 * Show 
	 * @return
	 */
	public int requestValues() {
		//Muestra el cuadro de opciones de potenciales
		/*PotentialsOptionPane options = new PotentialsOptionPane( this, 
				"hola");
		PotentialType potentialType = ( options.requestSelectPotential() == 
			PotentialsOptionPane.OK_BUTTON ) ? options.getSelectedOption() : null;
		*/
		if (!(probNode.getNodeType() == NodeType.DECISION && 
				probNode.getPolicyType() == PolicyType.OPTIMAL)){
			showFields(probNode);
		
		}else{
			setEnabledDecisionOptions(true);
		}
		setVisible(true);
		//TODO revisar el acceso a los componentes en la siguiente línea
		if ( getPotentialPanel() instanceof TablePotentialPanel ){
			probNode.getProbNet().getPNESupport().removeUndoableEditListener(
				((TablePotentialPanel)getPotentialPanel ()).
				getNodePotentialsTablePanel().getValuesTable());
		}
		return selectedButton;
	}
	
	/**
	 * This method fills the content of the fields from a ProbNode object.
	 * In this method, when Elvira will be discontinued, the code for
	 * discriminate discrete and discretized variables must be eliminated
	 * 
	 * @param probNode
	 *            object from where load the information.
	 */
	private void showFields(ProbNode probNode) {

		
		this.getJComboBoxRelationType().removeItemListener(this);
		PotentialType potentialType = probNode.getPotentials().get( 0 ).
			getPotentialType();
		//The element order in PotentialType object are same that 
		//JComboBoxRelationType 
		getJComboBoxRelationType().setSelectedIndex(potentialType.ordinal());
		
		// Elvira do not distinguish between DISCRETE and DISCRETIZED
		// so here we will see if there are intervals in the states
		if (Util.hasLimitBracketSymbols(probNode.getVariable().getStates())
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

		if ( getPotentialPanel() instanceof TablePotentialPanel ){
			tablePotentialPanel.setNodeProperties(probNode);
			// set the NodeProbsValuesTablePanel fields
			tablePotentialPanel.setFieldsFromProperties( probNode );
			if ( probNode.getNodeType() == NodeType.DECISION ){
				setEnabledDecisionOptions(true);
			}
		}
		this.getJComboBoxRelationType().addItemListener(this);
		
	}
	/**
	 * @return jPanel with two buttons with the types of Relation of the table
	 */
	protected JPanel getJPanelRelationType() {

		if (jPanelRelationTableType == null) {
			jPanelRelationTableType = new JPanel();
			//jPanelRelationTableType.setBorder( new LineBorder( UIManager
				//.getColor( "List.dropLineColor" ), 1, false ) );
			jPanelRelationTableType.setLayout( new FlowLayout() );
			jPanelRelationTableType.setSize( 294, 29 );
			jPanelRelationTableType.setName( "jPanelRelationTableType" );
			jPanelRelationTableType.add(getJLabelRelationType());
			jPanelRelationTableType.add(getJComboBoxRelationType());
			jPanelRelationTableType.add( getJPanelRadioPoliticyType() );
		}
		return jPanelRelationTableType;
	}
	/**
	 * @return jPanel with three radio buttons with the types of Relation
	 */
	protected JPanel getJPanelRadioPoliticyType() {

		if (jPanelRadioPolicyType == null) {
			jPanelRadioPolicyType = new JPanel();
			jPanelRadioPolicyType.setBorder( new LineBorder( UIManager
				.getColor( "List.dropLineColor" ), 1, false ) );
			jPanelRadioPolicyType.setLayout( new FlowLayout() );
			//jPanelRelationTableType.setSize( 294, 29 );
			//jPanelRelationTableType.setName( "jPanelRadioRelationTableType" );
			jPanelRadioPolicyType.add( getJRadioButtonOptimalType() );
			jPanelRadioPolicyType.add( getJRadioButtonProbabilisticType() );
			jPanelRadioPolicyType.add( getJRadioButtonDeterministicType() );
			getButtonGroupRelationType();
		}
		return jPanelRadioPolicyType;
	}
	
	/**
	 * @return the button for Probabilistic model
	 */
	protected JRadioButton getJRadioButtonProbabilisticType() {

		if (jRadioButtonProbabilisticType == null) {
			jRadioButtonProbabilisticType = new JRadioButton();
			jRadioButtonProbabilisticType.setMargin( new Insets( 0, 0, 0, 0 ) );
			jRadioButtonProbabilisticType
				.setName( "jRadioButtonProbabilisticType" );
			jRadioButtonProbabilisticType.setText( "New JRadioBut" );
			jRadioButtonProbabilisticType
				.setText( dialogStringResource
					.getString( "NodeProbsValuesTablePanel." +
							"jRadioButtonProbabilisticType.Text" ) );
			jRadioButtonProbabilisticType.addItemListener( this );
			jRadioButtonProbabilisticType.setEnabled(false);
		}
		return jRadioButtonProbabilisticType;
	}

	/**
	 * @return the button for the Deterministic Model
	 */
	protected JRadioButton getJRadioButtonDeterministicType() {

		if (jRadioButtonDeterministicType == null) {
			jRadioButtonDeterministicType = new JRadioButton();
			jRadioButtonDeterministicType.setMargin( new Insets( 0, 0, 0, 0 ) );
			jRadioButtonDeterministicType
				.setName( "jRadioButtonDeterministicType" );
			jRadioButtonDeterministicType.setText( "New JRadioBut" );
			jRadioButtonDeterministicType
				.setText( dialogStringResource
					.getString( "NodeProbsValuesTablePanel." +
							"jRadioButtonDeterministicType.Text" ) );
			jRadioButtonDeterministicType.addItemListener( this );
			jRadioButtonDeterministicType.setEnabled(false);
		}
		return jRadioButtonDeterministicType;
	}

	/**
	 * @return the button for the Optimal model (when decision node)
	 */
	protected JRadioButton getJRadioButtonOptimalType() {

		if (jRadioButtonOptimal == null) {
			jRadioButtonOptimal = new JRadioButton();
			jRadioButtonOptimal.setMargin( new Insets( 0, 0, 0, 0 ) );
			jRadioButtonOptimal.setName( "jRadioButtonOptimal" );
			jRadioButtonOptimal.setText( "New JRadioBut" );
			jRadioButtonOptimal
				.setText( dialogStringResource
					.getString( "NodeProbsValuesTablePanel." +
							"jRadioButtonOptimal.Text" ) );
			jRadioButtonOptimal.setSelected(true);
			jRadioButtonOptimal.setEnabled(false);
			jRadioButtonOptimal.addItemListener( this );
		
			
		}
		return jRadioButtonOptimal;
	}
	/**
	 * initialize the button group Probabilistic Or Deterministic Or Optimal
	 */
	private void getButtonGroupRelationType() {

		buttonGroupRelationType = new ButtonGroup();
		buttonGroupRelationType.add( getJRadioButtonProbabilisticType() );
		buttonGroupRelationType.add( getJRadioButtonDeterministicType() );
		buttonGroupRelationType.add( getJRadioButtonOptimalType() );

	}
	
	public void itemStateChanged(ItemEvent e) {
		
		if (e.getItemSelectable().equals(getJComboBoxRelationType())) {
			itemStateChangedComboBoxRelationType(e);
		}
		if (e.getItem().equals(getJRadioButtonProbabilisticType())) {
			itemStateChangedProbabilisticType(e);
		}
		if (e.getItem().equals(getJRadioButtonDeterministicType())) {
			itemStateChangedDeterministicType(e);
		}
		if (e.getItem().equals(getJRadioButtonOptimalType())) {
			itemStateChangedOptimalType(e);
		}
	}
	
	
	private void itemStateChangedDeterministicType(ItemEvent e) {
		/*if (e.getStateChange() == ItemEvent.DESELECTED){
			//optionDeselected = comboBox.getSelectedIndex();
			previousPolicy = Policy.PROBABILISTIC;
		}else if (e.getStateChange() == ItemEvent.SELECTED ){
			if ( getPotentialPanel() instanceof TablePotentialPanel){
				((TablePotentialPanel)getPotentialPanel()).
				hideElementsWhenIsDecisionNodeOrUniformPotential();
			}
		}*/
		
	}
	private void itemStateChangedOptimalType(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.DESELECTED){
			//optionDeselected = comboBox.getSelectedIndex();
			previousPolicy = PolicyType.OPTIMAL;
		}else if (e.getStateChange() == ItemEvent.SELECTED ){
			getJComboBoxRelationType().setEnabled(true);
			if ( previousPolicy == PolicyType.PROBABILISTIC ){
				RemovePolicyEdit removePolicyEdit = null;
				removePolicyEdit =	new RemovePolicyEdit( probNode );
				try {
					probNode.getProbNet().getPNESupport().announceEdit( 
							removePolicyEdit );
					probNode.getProbNet().getPNESupport().doEdit( 
							removePolicyEdit );
				} catch (ConstraintViolationException e1) {
					JOptionPane.showMessageDialog(this, 
							messageStringResource
							.getString( e1.getMessage() ),
							messageStringResource.getString( 
							"ConstraintViolationException" ),
							JOptionPane.ERROR_MESSAGE );
					//getJComboBoxRelationType().requestFocus();

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
			}
			
			if ( getPotentialPanel() instanceof TablePotentialPanel){
				((TablePotentialPanel)getPotentialPanel()).
				hideElementsWhenIsDecisionNodeOrUniformPotential();
				getJComboBoxRelationType().setEnabled(false);
			}
		}
		
	}
	private void itemStateChangedProbabilisticType(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.DESELECTED){
			//optionDeselected = comboBox.getSelectedIndex();
			previousPolicy = PolicyType.PROBABILISTIC;
		}else if (e.getStateChange() == ItemEvent.SELECTED ){
			getJComboBoxRelationType().setEnabled(true);
			if ( previousPolicy == PolicyType.OPTIMAL ){
				SetPotentialEdit setPotentialEdit = null;
				setPotentialEdit =	new SetPotentialEdit(
						probNode, PotentialType.TABLE );
				try {
					probNode.getProbNet().getPNESupport().announceEdit( 
							setPotentialEdit );
					probNode.getProbNet().getPNESupport().doEdit( 
							setPotentialEdit );
				} catch (ConstraintViolationException e1) {
					JOptionPane.showMessageDialog(this, 
							messageStringResource
							.getString( e1.getMessage() ),
							messageStringResource.getString( 
							"ConstraintViolationException" ),
							JOptionPane.ERROR_MESSAGE );
					getJComboBoxRelationType().removeItemListener(this);
					getJComboBoxRelationType().setSelectedIndex(optionDeselected);
					getJComboBoxRelationType().addItemListener(this);
					//getJComboBoxRelationType().requestFocus();

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
			}
			
		}
		if ( getPotentialPanel() instanceof TablePotentialPanel){
			((TablePotentialPanel)getPotentialPanel()).
			showElementsWhenOptimalDeselected();
		}
		
	}
		
	
	private void itemStateChangedComboBoxRelationType(ItemEvent e) {
		ItemSelectable itemSelectable = e.getItemSelectable();
		Object selected[] = itemSelectable.getSelectedObjects();
		String itemSelected = selected.length == 0 ? "null" :
			selected[0].toString();
		JComboBox comboBox= (JComboBox)e.getSource();
		if (e.getStateChange() == ItemEvent.DESELECTED){
			String h = e.getItem().toString();
			//optionDeselected = comboBox.getSelectedIndex();
			optionDeselected = PotentialType.getEnumMember(h).ordinal();
		}
			
		if (comboBox.getName().equals( "jComboBoxRelationType" )){
			
			if (!(itemSelected == null) && e.getStateChange() == ItemEvent.
				SELECTED){
				
				PotentialType potentialType = PotentialType.getEnumMember(itemSelected);
				
				if ((!(probNode.getVariable().isTemporal()) ||
						probNode.getVariable().getTimeSlice()==0)
						&&  (potentialType == PotentialType.SAME_AS_PREVIOUS || 
							potentialType == PotentialType.CYCLE_LENGTH_SHIFT)){
					comboBox.removeItemListener(this);
					comboBox.setSelectedIndex(optionDeselected);
					comboBox.addItemListener(this);
					
					JOptionPane.showMessageDialog( this, messageStringResource
							.getString( "Potential undefined for no " +
									"temporal variables or time slice 0" ),
						messageStringResource.getString( 
								"Variable potential message" ),
						JOptionPane.INFORMATION_MESSAGE );
					
				}else if (( probNode.getVariable().getVariableType() == VariableType.NUMERIC
						&& probNode.getNodeType() == NodeType.CHANCE)
						&&  !(potentialType == PotentialType.UNIFORM || 
						potentialType == PotentialType.SAME_AS_PREVIOUS || 
						potentialType == PotentialType.CYCLE_LENGTH_SHIFT)){
					
					JOptionPane.showMessageDialog( this, messageStringResource
							.getString( "Potential undefined for numeric " +
									"variables" ),
						messageStringResource.getString( 
								"Variable potential" ),
						JOptionPane.INFORMATION_MESSAGE );
						comboBox.removeItemListener(this);
						comboBox.setSelectedIndex(optionDeselected);
						comboBox.addItemListener(this);
															
								}else if ( !(probNode.getNodeType() == 
									NodeType.UTILITY)  && potentialType == 
										PotentialType.PRODUCT){
									comboBox.removeItemListener(this);
									comboBox.setSelectedIndex(optionDeselected);
									comboBox.addItemListener(this);
									JOptionPane.showMessageDialog( this, messageStringResource
											.getString( "Potential undefined for no " +
													"utility variables" ),
										messageStringResource.getString( 
												"Variable potential message" ),
										JOptionPane.INFORMATION_MESSAGE );
										
										//comboBox.requestFocus(); 
								}else		
								{
									SetPotentialEdit setPotentialEdit = null;
									setPotentialEdit =	new SetPotentialEdit(
											probNode, potentialType );
									try {
										probNode.getProbNet().getPNESupport().announceEdit( 
												setPotentialEdit );
										probNode.getProbNet().getPNESupport().doEdit( 
												setPotentialEdit );
									} catch (ConstraintViolationException e1) {
										JOptionPane.showMessageDialog(this, 
												messageStringResource
												.getString( e1.getMessage() ),
												messageStringResource.getString( 
												"ConstraintViolationException" ),
												JOptionPane.ERROR_MESSAGE );
										comboBox.removeItemListener(this);
										comboBox.setSelectedIndex(optionDeselected);
										comboBox.addItemListener(this);
										comboBox.requestFocus();

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
								}
			}
		}
		
	}
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
	 * This method carries out the actions when the user press the OK button
	 * before hide the dialog.
	 * 
	 * @return true if all the fields are correct.
	 * @throws NotEnoughMemoryException 
	 */
	@Override
	protected boolean doOkClickBeforeHide() throws NotEnoughMemoryException {
		if (! ( getPotentialPanel() instanceof TablePotentialPanel ) ){
			SetPotentialEdit setPotentialEdit = new SetPotentialEdit(probNode, 
					treeADDBuilder.getTreePotential() );
			try {
				probNode.getProbNet().getPNESupport().announceEdit( 
						setPotentialEdit );
				probNode.getProbNet().getPNESupport().doEdit( 
						setPotentialEdit );
			} catch (ConstraintViolationException e1) {
				JOptionPane.showMessageDialog( this, messageStringResource
					.getString( e1.getMessage() ),
				messageStringResource.getString( 
						"ConstraintViolationException" ),
				JOptionPane.ERROR_MESSAGE );
				//comboBox.setSelectedIndex(optionDeselected);
				//comboBox.requestFocus();
			
			} catch (CanNotDoEditException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (DoEditException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NonProjectablePotentialException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WrongCriterionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		getJComboBoxRelationType().removeItemListener(this);
		probNode.getProbNet().getPNESupport().closeParenthesis();
		probNode.getProbNet().getPNESupport().removeUndoableEditListener(this);
		return true;
	}
	@Override
	protected void doCancelClickBeforeHide() {
		getJComboBoxRelationType().removeItemListener(this);
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
			if ( ( ( SetPotentialEdit) edit ).getNewPotential() instanceof 
					TreeADDPotential){
				if ( tablePotentialPanel != null ){
					getComponentsPanel().remove(tablePotentialPanel);
				}
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
					((TablePotentialPanel) getPotentialPanel()).
					setFieldsFromProperties(probNode);
					if (probNode.getNodeType() == NodeType.DECISION){
						setEnabledDecisionOptions(true);
					}else{
						setEnabledDecisionOptions(false);
					}
				}else{ 
					tablePotentialPanel.
						hideElementsWhenIsDecisionNodeOrUniformPotential();
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
	private void setEnabledDecisionOptions(boolean show) {
		getJRadioButtonOptimalType().removeItemListener(this);
		getJRadioButtonDeterministicType().removeItemListener(this);
		getJRadioButtonProbabilisticType().removeItemListener(this);
		if (show){
			/*getJRadioButtonOptimalType().setSelected(false);
		getJRadioButtonDeterministicType().setSelected(false);
		getJRadioButtonProbabilisticType().setSelected(false);*/

			switch (probNode.getPolicyType()){
			case OPTIMAL:
				getJRadioButtonOptimalType().setSelected(true);
				getJComboBoxRelationType().setEnabled(false);
				break;
			case DETERMINISTIC:
				getJRadioButtonDeterministicType().setSelected(true);
				getJComboBoxRelationType().setEnabled(false);
				break;
			case PROBABILISTIC:
				Potential potential = probNode.getPotentials().get(0);
				switch (potential.getPotentialType()){
				case UNIFORM:
				case TABLE:
					getJComboBoxRelationType().removeItemListener(this);
					getJComboBoxRelationType().setSelectedIndex(
							potential.getPotentialType().getType());
					//getJComboBoxRelationType().setEnabled(false);
					getJComboBoxRelationType().addItemListener(this);
					break;
					//TODO definir el comportamiento para los demás tipos de potenciales	
				}

				getJRadioButtonProbabilisticType().setSelected(true);
				break;
			}

			getJRadioButtonOptimalType().addItemListener(this);
			getJRadioButtonDeterministicType().addItemListener(this);
			getJRadioButtonProbabilisticType().addItemListener(this);
			
			getJRadioButtonOptimalType().setEnabled(true);
			getJRadioButtonDeterministicType().setEnabled(false);
			getJRadioButtonProbabilisticType().setEnabled(true);
		}else{
			getJRadioButtonOptimalType().setEnabled(false);
			getJRadioButtonDeterministicType().setEnabled(false);
			getJRadioButtonProbabilisticType().setEnabled(false);
		}
	}
	
	
	public void undoableEditWillHappen(PNUndoableEditEvent event)
			throws ConstraintViolationException, CanNotDoEditException {
		// TODO Auto-generated method stub
		
	}
	
	public void undoEditHappened(PNUndoableEditEvent event) {
		// TODO Auto-generated method stub
		
	}
	public int getPreviousPolicy() {
		// TODO Auto-generated method stub
		return 0;
	}
	public void setShowNetValues(boolean b) {
		// TODO Auto-generated method stub
		
	}

}
