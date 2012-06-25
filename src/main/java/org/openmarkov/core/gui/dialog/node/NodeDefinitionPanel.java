/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.node;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

import org.openmarkov.core.action.NodeAlwaysObservedEdit;
import org.openmarkov.core.action.NodeCommentEdit;
import org.openmarkov.core.action.NodeNameEdit;
import org.openmarkov.core.action.PurposeEdit;
import org.openmarkov.core.action.RelevanceEdit;
import org.openmarkov.core.action.TimeSliceEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.action.NodeAgentEdit;
import org.openmarkov.core.gui.constraint.AlwaysObservedPropertyValidator;
import org.openmarkov.core.gui.dialog.CommentListener;
import org.openmarkov.core.gui.dialog.common.CommentHTMLScrollPane;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.gui.util.Purpose;
import org.openmarkov.core.gui.util.Utilities;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.StringWithProperties;
import org.openmarkov.core.model.network.StringsWithProperties;
import org.openmarkov.core.model.network.VariableType;
/**
 * Panel to set the definition of a node.
 * 
 * @author jlgozalo
 * @version 1.0 jlgozalo
 * @versión 1.5 mpalacios
 */
public class NodeDefinitionPanel extends JPanel implements FocusListener,
		ItemListener, CommentListener, ActionListener {

	private JComboBox jComboBoxNetworkAgents;
	private JLabel jLabelTimeSlice;
	private JComboBox jComboBoxTimeSlice;
	private JLabel jLabelDecisionCriteria;
	private JComboBox jComboBoxDecisionCriteria;

	/**
	 * constructor without construction parameters
	 */
	public NodeDefinitionPanel() {

		this(true);// , new ElementObservable() );
	}

	/**
	 * constructor
	 * 
	 * @param notifier
	 *            - the element that will sent events to this class
	 */
	public NodeDefinitionPanel(ProbNode probNode) {
		
		this(true);// , notifier );
		this.probNode = probNode;
		
		try {
			initialize();
		} catch (Throwable e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );

		}
		if (probNode.getProbNet().getAgents()!= null) {
			getJComboBoxNetworkAgents().setEnabled(true);
			getJComboBoxNetworkAgents().setVisible(true);
			getJLabelNetworkAgents().setVisible(true);
		} else if(probNode.getProbNet().getAgents() == null /*&& probNode.getVariable().getAgent() == null*/) {
			getJComboBoxNetworkAgents().setEnabled(false);
			getJComboBoxNetworkAgents().setVisible(false);
			getJLabelNetworkAgents().setVisible(false);
		}  /*else if (probNode.getProbNet().getAgents() == null && probNode.getVariable().getAgent() != null) {
			// Dec-POMDP --> POMDP an agent has been already assigned to current variable
			getJComboBoxNetworkAgents().setEnabled(true);
			getJComboBoxNetworkAgents().setVisible(true);
			getJLabelNetworkAgents().setVisible(true);
		}*/
		
		//Check if the network has associated Only AtemporalVariablesConstranint
		if (probNode.getProbNet().variablesCouldBeTemporal()) {
		//String type = probNode.getProbNet().getNetworkType().toString();
		//if ((probNode.getProbNet().getNetworkType().toString()).equals("DEC_POMDP")) {
			getJComboBoxTimeSlice().setEnabled(true);
			getJComboBoxTimeSlice().setVisible(true);
			getJLabelTimeSlice().setVisible(true);
		} else {
			getJComboBoxTimeSlice().setEnabled(false);
			getJComboBoxTimeSlice().setVisible(false);
			getJLabelTimeSlice().setVisible(false);
		}
		if (probNode.getNodeType() == NodeType.UTILITY) {
			getJComboBoxDecisionCriteria().setEnabled(true);
			getJComboBoxDecisionCriteria().setVisible(true);
			getJLabelDecisionCriteria().setVisible(true);
		} else {
			getJComboBoxDecisionCriteria().setEnabled(false);
			getJComboBoxDecisionCriteria().setVisible(false);
			getJLabelDecisionCriteria().setVisible(false);
		}
		
		getJComboBoxNodePurpose().setEnabled(true);
		getJComboBoxNodeRelevance().setEnabled(true);
		if (!AlwaysObservedPropertyValidator.validate(probNode)) {
			getJLabelAlwaysObserved().setVisible(false);
			getJCheckBoxAlwaysObserved().setVisible(false);
		}
		
	}

	/**
	 * This method initialises this instance.
	 * 
	 * @param newNode
	 *            - true if the node is a new node; otherwise false
	 * @param notifier
	 *            - the element that will sent events to this class
	 */
	public NodeDefinitionPanel(final boolean newNode) {// , ElementObservable
														// notifier) {

		dialogStringResource = StringResourceLoader.getUniqueInstance()
				.getBundleDialogs();
		messageStringResource = StringResourceLoader.getUniqueInstance()
				.getBundleMessages();
		this.newNode = newNode;

		// this.notifier = notifier;
		/*try {
			initialize();
		} catch (Throwable e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );

		}*/

	}

	/**
	 * Get the node Properties in this panel
	 * 
	 * @return the nodeProperties
	 */
	public ProbNode getNodeProperties() {

		return probNode;
	}

	/**
	 * Set the node adittionalProperties in this panel with the provided ones
	 * 
	 * @param nodeProperties
	 *            the nodeProperties to set
	 */
	public void setNodeProperties(final ProbNode nodeProperties) {

		this.probNode = nodeProperties;
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
	 * <p>
	 * <code>Initialize</code>
	 * <p>
	 * initialize the layout for this panel
	 */
	private void initialize() throws Exception {

		this.getCommentHTMLScrollPaneNodeDefinitionComment();
		setName("NodeDefinitionPanel");
		setFocusable(false);
		setDoubleBuffered(false);
		setMinimumSize(new Dimension(500, 245));
		setMaximumSize(new Dimension(500, 245));
		setPreferredSize(new Dimension(500, 245));
		setFocusCycleRoot(true);
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(getJLabelNodeName())
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(getJTextFieldNodeName(), GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE)
									.addGap(18)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(getJLabelTimeSlice())
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(getJComboBoxTimeSlice(),GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
									)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(getJLabelNodePurpose())
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(getJComboBoxNodePurpose(), GroupLayout.PREFERRED_SIZE,  203, GroupLayout.PREFERRED_SIZE/* 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE*/)
									.addGap(18) /***/
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(getJLabelNodeRelevance())
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(getJComboBoxNodeRelevance(), GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
									)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(getAgentsOrDecisionCriteriaOrObservedLabel()) 
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(getAgentsOrDecisionCriteriaOrObserved(), GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE)
									/*.addGap(18)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(getJLabelAlwaysObserved())
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(getJCheckBoxAlwaysObserved(), GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)*/
								
									)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(getJTextAreaLabelNodeDefinitionComment())
									.addComponent(getCommentHTMLScrollPaneNodeDefinitionComment(), 30, 560,	Short.MAX_VALUE))
									)
						
						.addContainerGap())
		)));
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getJLabelNodeName())
						.addComponent(getJTextFieldNodeName(), GroupLayout.PREFERRED_SIZE, /*20*/GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(getJLabelTimeSlice())
						.addComponent(getJComboBoxTimeSlice())
						)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getJComboBoxNodePurpose(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(getJLabelNodePurpose())
						.addComponent(getJLabelNodeRelevance(),GroupLayout.PREFERRED_SIZE, /*25*/GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(getJComboBoxNodeRelevance())
						)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getAgentsOrDecisionCriteriaOrObservedLabel())
						.addComponent(getAgentsOrDecisionCriteriaOrObserved(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						/*.addComponent(getJLabelAlwaysObserved())
						.addComponent(getJCheckBoxAlwaysObserved(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)*/
						)
					.addGap(21)
					//.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getJTextAreaLabelNodeDefinitionComment())
						.addComponent(getCommentHTMLScrollPaneNodeDefinitionComment(),GroupLayout.DEFAULT_SIZE,	62,	150))
					.addContainerGap(77, Short.MAX_VALUE))
		);
		Component[] components = new Component [3];
		components[0] = getJComboBoxNodePurpose();
		components[1] = getJTextFieldNodeName();
		components[2] = getAgentsOrDecisionCriteriaOrObserved();
		groupLayout.linkSize(components);
		
		Component[] components2 = new Component [5];
		components2[0] = getAgentsOrDecisionCriteriaOrObservedLabel();
		components2[1] = getJLabelNodeName();
		components2[2] = getJLabelNodePurpose();
		components2[3] = getJLabelNodeRelevance();
		components2[4] = getJLabelTimeSlice();
		groupLayout.linkSize(components2);
		
		Component[] components3 = new Component [2];
		components3[0] = getJComboBoxNodeRelevance();
		components3[1] = getJComboBoxTimeSlice();
		groupLayout.linkSize(components3);
		
		setLayout(groupLayout);
		
	}

	/**
	 * This method initialises jLabelTimeSlice
	 * 
	 * @return a new name label.
	 */
	private JLabel getJLabelTimeSlice() {

		if (jLabelTimeSlice == null) {
			jLabelTimeSlice = new JLabel();
			jLabelTimeSlice.setHorizontalAlignment(SwingConstants.LEFT);
			jLabelTimeSlice.setHorizontalTextPosition(SwingConstants.LEFT);
			jLabelTimeSlice.setName("jLabelTimeSlice");
			jLabelTimeSlice.setText("a Label");
			jLabelTimeSlice.setText(dialogStringResource
					.getString("NodeDefinitionPanel.jLabelTimeSlice.Text"));
			
			jLabelTimeSlice.setLabelFor(getJComboBoxTimeSlice());
		}
		return jLabelTimeSlice;
	}

	/**
	 * initialize the content of the Combo box for the tme slice for temporal variables
	 * 
	 * @return the JComboBoxNodeRelevance
	 */
	private JComboBox getJComboBoxTimeSlice() {

		if (jComboBoxTimeSlice == null) {
			jComboBoxTimeSlice = new JComboBox();
			jComboBoxTimeSlice.setName("jComboBoxTimeSlice");
			jComboBoxTimeSlice.setEditable(true);
			jComboBoxTimeSlice.setSize(60, 40);
			if (!probNode.getProbNet().onlyTemporal()) {
				//It corresponds with no time slice, atemporal selection timeSlice = Integer.MIN 
				jComboBoxTimeSlice.addItem(dialogStringResource
					.getString("NodeDefinitionPanel.Atemporal.Text"));
			}
			jComboBoxTimeSlice.addItem("0");
			jComboBoxTimeSlice.addItem("1");
			
			String timeSlice = String.valueOf(probNode.getVariable().getTimeSlice());
				if (timeSlice.equals("0")) {
					if (probNode.getProbNet().onlyTemporal()) {
						jComboBoxTimeSlice.setSelectedIndex(0);
					} else {
						jComboBoxTimeSlice.setSelectedIndex(1);
					}
				} else if (timeSlice.equals("1")) {
					if (probNode.getProbNet().onlyTemporal()) {
						jComboBoxTimeSlice.setSelectedIndex(1);
					} else {
						jComboBoxTimeSlice.setSelectedIndex(2);
					}
				}else {
					jComboBoxTimeSlice.setSelectedIndex(0);
				}
			
			jComboBoxTimeSlice.addItemListener(this);
			
			//jComboBoxTimeSlice.setEnabled(false);
		}
		return jComboBoxTimeSlice;
	}
	
	/**
	 * This method initialises jLabelNodeName
	 * 
	 * @return a new name label.
	 */
	private JLabel getJLabelNodeName() {

		if (jLabelNodeName == null) {
			jLabelNodeName = new JLabel();
			jLabelNodeName.setHorizontalAlignment(SwingConstants.LEFT);
			jLabelNodeName.setHorizontalTextPosition(SwingConstants.LEFT);
			jLabelNodeName.setName("jLabelNodeName");
			jLabelNodeName.setText("a Label");
			jLabelNodeName.setText(dialogStringResource
					.getString("NodeDefinitionPanel.jLabelNodeName.Text"));
			jLabelNodeName.setDisplayedMnemonic(dialogStringResource.getString(
					"NodeDefinitionPanel.jLabelNodeName.Mnemonic").charAt(0));
			jLabelNodeName.setLabelFor(getJTextFieldNodeName());
		}
		return jLabelNodeName;
	}

	/**
	 * This method initialises jTextFieldNodeName
	 * 
	 * @return a new name field.
	 */
	public JTextField getJTextFieldNodeName() {

		int dis = 15;
		if (jTextFieldNodeName == null) {
			jTextFieldNodeName = new JTextField();
			jTextFieldNodeName.setName("jTextFieldNodeName");
			jTextFieldNodeName.setPreferredSize(new Dimension(50, dis));
			// jTextFieldNodeName.addActionListener( this );
			jTextFieldNodeName.addFocusListener(this);
		}
		return jTextFieldNodeName;
	}

	/**
	 * This method initialises jLabelAlwaysObserved
	 * 
	 * @return a new label for the always observed property.
	 */
	private JLabel getJLabelAlwaysObserved() {

		if (jLabelAlwaysObserved == null) {
			jLabelAlwaysObserved = new JLabel();
			jLabelAlwaysObserved.setHorizontalAlignment(SwingConstants.LEFT);
			jLabelAlwaysObserved.setHorizontalTextPosition(SwingConstants.LEFT);
			jLabelAlwaysObserved.setVerticalAlignment(SwingConstants.CENTER);
			jLabelAlwaysObserved.setVerticalTextPosition(SwingConstants.CENTER);
			jLabelAlwaysObserved.setName("jLabelAlwaysObserved");
			jLabelAlwaysObserved.setText("a Label");
			jLabelAlwaysObserved
					.setText(dialogStringResource
							.getString("NodeDefinitionPanel.jLabelAlwaysObserved.Text"));
			jLabelAlwaysObserved
					.setDisplayedMnemonic(dialogStringResource
							.getString(
									"NodeDefinitionPanel.jLabelAlwaysObserved.Mnemonic")
							.charAt(0));
			jLabelAlwaysObserved.setLabelFor(getJTextFieldNodeName());
		}
		
		
		return jLabelAlwaysObserved;
	}

	/**
	 * This method initialises jCheckBoxAlwaysObserved
	 * 
	 * @return a new checkbox
	 */
	public JCheckBox getJCheckBoxAlwaysObserved() {

		if (jCheckboxAlwaysObserved == null) {
			jCheckboxAlwaysObserved = new JCheckBox();
			jCheckboxAlwaysObserved.setName("jCheckboxAlwaysObserved");
			jCheckboxAlwaysObserved.setVerticalAlignment(SwingConstants.CENTER);
			
			jCheckboxAlwaysObserved.addActionListener(this);
			jCheckboxAlwaysObserved.addFocusListener(this);
		}
	
		return jCheckboxAlwaysObserved;
	}
	


	/**
	 * This method initialises jRadioButtonChanceNodeType.
	 * 
	 * @return a new chance type radio button.
	 */
	/*
	 * private JRadioButton getJRadioButtonChanceNodeType() {
	 * 
	 * if (jRadioButtonChanceNodeType == null) {
	 * 
	 * jRadioButtonChanceNodeType = new JRadioButton();
	 * jRadioButtonChanceNodeType .setHorizontalTextPosition(
	 * SwingConstants.RIGHT ); jRadioButtonChanceNodeType
	 * .setHorizontalAlignment( SwingConstants.LEFT );
	 * jRadioButtonChanceNodeType.setName( "jRadioButtonChanceNodeType" );
	 * jRadioButtonChanceNodeType.setText( "an option" );
	 * jRadioButtonChanceNodeType .setText( dialogStringResource .getString(
	 * "NodeDefinitionPanel.jRadioButtonChanceNodeType.Text" ) );
	 * jRadioButtonChanceNodeType.setMnemonic( dialogStringResource .getString(
	 * "NodeDefinitionPanel.jRadioButtonChanceNodeType.Mnemonic" ) .charAt( 0 )
	 * ); jRadioButtonChanceNodeType.setSelected( true ); //
	 * jRadioButtonChanceNodeType.setEnabled(newNode);
	 * 
	 * } return jRadioButtonChanceNodeType; }
	 */

	/**
	 * This method initialises jRadioButtonDecisionNodeType.
	 * 
	 * @return a new decision type radio button.
	 */
	/*
	 * private JRadioButton getJRadioButtonDecisionNodeType() {
	 * 
	 * if (jRadioButtonDecisionNodeType == null) { jRadioButtonDecisionNodeType
	 * = new JRadioButton(); jRadioButtonDecisionNodeType
	 * .setHorizontalTextPosition( SwingConstants.RIGHT );
	 * jRadioButtonDecisionNodeType .setHorizontalAlignment( SwingConstants.LEFT
	 * ); jRadioButtonDecisionNodeType .setName( "jRadioButtonDecisionNodeType"
	 * ); jRadioButtonDecisionNodeType.setText( "an option" );
	 * jRadioButtonDecisionNodeType .setText( dialogStringResource .getString(
	 * "NodeDefinitionPanel.jRadioButtonDecisionNodeType.Text" ) );
	 * jRadioButtonDecisionNodeType .setMnemonic( dialogStringResource
	 * .getString( "NodeDefinitionPanel.jRadioButtonDecisionNodeType.Mnemonic" )
	 * .charAt( 0 ) ); // jRadioButtonDecisionNodeType.setEnabled(newNode); }
	 * return jRadioButtonDecisionNodeType; }
	 */

	/**
	 * This method initialises jRadioButtonUtilityNodeType.
	 * 
	 * @return a new utility type radio button.
	 */
	/*
	 * private JRadioButton getJRadioButtonUtilityNodeType() {
	 * 
	 * if (jRadioButtonUtilityNodeType == null) { jRadioButtonUtilityNodeType =
	 * new JRadioButton(); jRadioButtonUtilityNodeType
	 * .setHorizontalTextPosition( SwingConstants.RIGHT );
	 * jRadioButtonUtilityNodeType .setHorizontalAlignment( SwingConstants.LEFT
	 * ); jRadioButtonUtilityNodeType.setName( "jRadioButtonUtilityNodeType" );
	 * jRadioButtonUtilityNodeType.setText( "an option" );
	 * jRadioButtonUtilityNodeType .setText( dialogStringResource .getString(
	 * "NodeDefinitionPanel.jRadioButtonUtilityNodeType.Text" ) );
	 * jRadioButtonUtilityNodeType.setMnemonic( dialogStringResource .getString(
	 * "NodeDefinitionPanel.jRadioButtonUtilityNodeType.Mnemonic" ) .charAt( 0 )
	 * ); // jRadioButtonUtilityNodeType.setEnabled(newNode);
	 * 
	 * } return jRadioButtonUtilityNodeType; }
	 */

	/**
	 * This method initialises jLabelNodeType
	 * 
	 * @return a new name label.
	 */
	/*
	 * private JLabel getJLabelNodeVariableType() {
	 * 
	 * if (jLabelNodeVariableType == null) { jLabelNodeVariableType = new
	 * JLabel(); jLabelNodeVariableType.setName( "jLabelNodeVariableType" );
	 * jLabelNodeVariableType .setHorizontalAlignment( SwingConstants.RIGHT );
	 * jLabelNodeVariableType .setHorizontalTextPosition( SwingConstants.LEFT );
	 * jLabelNodeVariableType.setText( "a Label" ); jLabelNodeVariableType
	 * .setText( dialogStringResource .getString(
	 * "NodeDefinitionPanel.jLabelNodeVariableType.Text" ) );
	 * jLabelNodeVariableType.setDisplayedMnemonic( dialogStringResource
	 * .getString( "NodeDefinitionPanel.jLabelNodeVariableType.Mnemonic" )
	 * .charAt( 0 ) ); jLabelNodeVariableType.setLabelFor( getJPanelNodeType()
	 * ); } return jLabelNodeVariableType; }
	 */

	/**
	 * This method initialises jPanelNodeVariableType
	 * 
	 * @return a panel for the variable types of the node
	 */
	/*
	 * private JPanel getJPanelNodeType() {
	 * 
	 * if (jPanelNodeVariableType == null) { jPanelNodeVariableType = new
	 * JPanel(); jPanelNodeVariableType.setName( "jPanelNodeVariableType" );
	 * jPanelNodeVariableType.setBorder( new LineBorder( Color.BLUE, 1, false )
	 * ); jPanelNodeVariableType.setLayout( new GridLayout( 3, 1 ) );
	 * jPanelNodeVariableType .add( getJRadioButtonDiscreteNodeVariableType() );
	 * jPanelNodeVariableType .add( getJRadioButtonDiscretizedNodeVariableType()
	 * ); jPanelNodeVariableType .add(
	 * getJRadioButtonContinuousNodeVariableType() );
	 * initButtonGroupNodeVariableType(); } return jPanelNodeVariableType; }
	 */

	/**
	 * iniatilize the button group Node Variable Type with the three buttons
	 */
	/*
	 * private void initButtonGroupNodeVariableType() {
	 * 
	 * jButtonGroupNodeVariableType = new ButtonGroup();
	 * jButtonGroupNodeVariableType .add(
	 * getJRadioButtonDiscreteNodeVariableType() ); jButtonGroupNodeVariableType
	 * .add( getJRadioButtonDiscretizedNodeVariableType() );
	 * jButtonGroupNodeVariableType .add(
	 * getJRadioButtonContinuousNodeVariableType() ); }
	 */

	/**
	 * This method initialises jRadioButtonDiscreteNodeVariableType.
	 * 
	 * @return a new discrete variables type radio button.
	 */
	/*
	 * private JRadioButton getJRadioButtonDiscreteNodeVariableType() {
	 * 
	 * if (jRadioButtonDiscreteNodeVariableType == null) {
	 * jRadioButtonDiscreteNodeVariableType = new JRadioButton();
	 * jRadioButtonDiscreteNodeVariableType .setName(
	 * "jRadioButtonDiscreteNodeVariableType" );
	 * jRadioButtonDiscreteNodeVariableType.setText( "an option" );
	 * jRadioButtonDiscreteNodeVariableType .setText( dialogStringResource
	 * .getString(
	 * "NodeDefinitionPanel.jRadioButtonDiscreteNodeVariableType.Text" ) );
	 * jRadioButtonDiscreteNodeVariableType .setMnemonic( dialogStringResource
	 * .getString(
	 * "NodeDefinitionPanel.jRadioButtonDiscreteNodeVariableType.Mnemonic" )
	 * .charAt( 0 ) ); jRadioButtonDiscreteNodeVariableType.setSelected( true );
	 * // jRadioButtonDiscreteNodeVariableType.setEnabled(newNode);
	 * jRadioButtonDiscreteNodeVariableType.addItemListener( this );
	 * 
	 * } return jRadioButtonDiscreteNodeVariableType; }
	 */

	/**
	 * This method initialises jRadioButtonContinuousNodeVariableType.
	 * 
	 * @return a new continuous variables type radio button.
	 */
	/*
	 * private JRadioButton getJRadioButtonContinuousNodeVariableType() {
	 * 
	 * if (jRadioButtonContinuousNodeVariableType == null) {
	 * jRadioButtonContinuousNodeVariableType = new JRadioButton();
	 * jRadioButtonContinuousNodeVariableType .setName(
	 * "jRadioButtonContinuousNodeVariableType" );
	 * jRadioButtonContinuousNodeVariableType.setText( "an option" );
	 * jRadioButtonContinuousNodeVariableType .setText( dialogStringResource
	 * .getString(
	 * "NodeDefinitionPanel.jRadioButtonContinuousNodeVariableType.Text" ) );
	 * jRadioButtonContinuousNodeVariableType .setMnemonic( dialogStringResource
	 * .getString(
	 * "NodeDefinitionPanel.jRadioButtonContinuousNodeVariableType.Mnemonic" )
	 * .charAt( 0 ) ); // this button is disabled until the management of the
	 * continuous // variable is set in the source code
	 * jRadioButtonContinuousNodeVariableType.setEnabled( false );
	 * jRadioButtonContinuousNodeVariableType.addItemListener( this );
	 * 
	 * } return jRadioButtonContinuousNodeVariableType; }
	 */
	/**
	 * This method initialises jRadioButtonDiscretizedNodeVariableType.
	 * 
	 * @return a new discretized variables type radio button.
	 */
	/*
	 * private JRadioButton getJRadioButtonDiscretizedNodeVariableType() {
	 * 
	 * if (jRadioButtonDiscretizedNodeVariableType == null) {
	 * jRadioButtonDiscretizedNodeVariableType = new JRadioButton();
	 * jRadioButtonDiscretizedNodeVariableType .setName(
	 * "jRadioButtonDiscretizedNodeVariableType" );
	 * jRadioButtonDiscretizedNodeVariableType.setText( "an option" );
	 * jRadioButtonDiscretizedNodeVariableType .setText( dialogStringResource
	 * .getString(
	 * "NodeDefinitionPanel.jRadioButtonDiscretizedNodeVariableType.Text" ) );
	 * jRadioButtonDiscretizedNodeVariableType .setMnemonic(
	 * dialogStringResource .getString(
	 * "NodeDefinitionPanel.jRadioButtonDiscretizedNodeVariableType.Mnemonic" )
	 * .charAt( 0 ) ); jRadioButtonDiscretizedNodeVariableType.addItemListener(
	 * this );
	 * 
	 * } return jRadioButtonDiscretizedNodeVariableType; }
	 */

	/**
	 * This method initialises jLabelNodeName
	 * 
	 * @return a new name label.
	 */
	private JLabel getJLabelNodeRelevance() {

		if (jLabelNodeRelevance == null) {
			jLabelNodeRelevance = new JLabel();
			jLabelNodeRelevance.setHorizontalTextPosition(SwingConstants.LEFT);
			jLabelNodeRelevance.setHorizontalAlignment(SwingConstants.LEFT);
			jLabelNodeRelevance.setName("jLabelNodeRelevance");
			jLabelNodeRelevance.setText("a Label");
			jLabelNodeRelevance.setText(dialogStringResource
					.getString("NodeDefinitionPanel.jLabelNodeRelevance.Text"));
			jLabelNodeRelevance.setDisplayedMnemonic(dialogStringResource
					.getString(
							"NodeDefinitionPanel.jLabelNodeRelevance.Mnemonic")
					.charAt(0));
			jLabelNodeRelevance.setLabelFor(getJComboBoxNodeRelevance());
		}
		return jLabelNodeRelevance;
	}
	
	/**
	 * This method initialises jLabelNodeName
	 * 
	 * @return a new name label.
	 */
	private JLabel getJLabelNetworkAgents() {

		if (jLabelNetworkAgents == null) {
			jLabelNetworkAgents = new JLabel();
			jLabelNetworkAgents.setHorizontalTextPosition(SwingConstants.LEFT);
			jLabelNetworkAgents.setHorizontalAlignment(SwingConstants.LEFT);
			jLabelNetworkAgents.setName("jLabelNetworkAgent");
			jLabelNetworkAgents.setText("a Label");
			jLabelNetworkAgents.setText(dialogStringResource
					.getString("NodeDefinitionPanel.jLabelNetworkAgents.Text"));
			/*jLabelNetworkAgents.setDisplayedMnemonic(dialogStringResource
					.getString(
							"NodeDefinitionPanel.jLabelNodeRelevance.Mnemonic")
					.charAt(0));*/
			jLabelNetworkAgents.setLabelFor(getJComboBoxNetworkAgents());
		}
		return jLabelNetworkAgents;
	}

	/**
	 * initialize the content of the Combo box for the Node Relevance
	 * 
	 * @return the JComboBoxNodeRelevance
	 */
	private JComboBox getJComboBoxNodeRelevance() {

		if (jComboBoxNodeRelevance == null) {
			jComboBoxNodeRelevance = new JComboBox();
			jComboBoxNodeRelevance.setName("jComboBoxNodeRelevance");
			jComboBoxNodeRelevance.setEditable(true);
			jComboBoxNodeRelevance.setSize(60, 40);
			fillJComboBoxNodeRelevanceWithoutDecimals();
			jComboBoxNodeRelevance.setEnabled(false);
		}
		return jComboBoxNodeRelevance;
	}

	/**
	 * fill the jComboBoxNodeRelevance with the appropriate values with an
	 * increment of 0.1. If not used, mathematical addition to avoid the
	 * Precision problems with the proccesors Therefore, it is using a "string"
	 * concatenation with integers and then a conversion to doubles
	 */
	private void fillJComboBoxNodeRelevance() {

		String number = "0.0";
		if (jComboBoxNodeRelevance != null) {
			for (int realPart = 0; realPart < 10; realPart++) {
				for (int decimalPart = 0; decimalPart < 10; decimalPart++) {
					number = Integer.toString(realPart) + "."
							+ Integer.toString(decimalPart);
					jComboBoxNodeRelevance.addItem(Double.valueOf(number));
				}
			}
		}
	}

	/**
	 * fill the jComboBoxNodeRelevance with the appropriate values with an
	 * increment of 1.0. The values appear in reverse order.
	 */
	private void fillJComboBoxNodeRelevanceWithoutDecimals() {
		if (jComboBoxNodeRelevance != null) {
			for (int value = 10; value >= 0; value--) {
				jComboBoxNodeRelevance.addItem(Double.valueOf(value));
			}
		}
	}

	/**
	 * This method initialises jLabelNodeName
	 * 
	 * @return a new name label.
	 */
	private JLabel getJLabelNodePurpose() {

		if (jLabelNodePurpose == null) {
			jLabelNodePurpose = new JLabel();
			jLabelNodePurpose.setName("jLabelNodePurpose");
			jLabelNodePurpose.setHorizontalTextPosition(SwingConstants.LEFT);
			jLabelNodePurpose.setHorizontalAlignment(SwingConstants.LEFT);
			jLabelNodePurpose.setText("a Label");
			jLabelNodePurpose.setText(dialogStringResource
					.getString("NodeDefinitionPanel.jLabelNodePurpose.Text"));
			jLabelNodePurpose
					.setDisplayedMnemonic(dialogStringResource.getString(
							"NodeDefinitionPanel.jLabelNodePurpose.Mnemonic")
							.charAt(0));
			jLabelNodePurpose.setLabelFor(getJTextFieldNodeName());
		}
		return jLabelNodePurpose;
	}

	/**
	 * initialize the content of the Combo box for the Node Purpose
	 * 
	 * @return the JComboBoxNodePurpose
	 */
	private JComboBox getJComboBoxNodePurpose() {

		if (jComboBoxNodePurpose == null) {
			jComboBoxNodePurpose = new JComboBox(Purpose.getListStrings(false));
			jComboBoxNodePurpose.setName("jComboBoxNodePurpose");
			jComboBoxNodePurpose.setSelectedIndex(0);
			jComboBoxNodePurpose.setMaximumRowCount(9);
			// jComboBoxNodePurpose.addItemListener( this );
			jComboBoxNodePurpose.setEditable(true);

		}
		return jComboBoxNodePurpose;
	}
	/**
	 * initialize the content of the Combo box for the Node Purpose
	 * 
	 * @return the JComboBoxNodePurpose
	 */
	private JComboBox getJComboBoxNetworkAgents() {

		if (jComboBoxNetworkAgents == null) {
			//StringsWithProperties agents = probNode.getProbNet().getAgents();
			ArrayList<StringWithProperties> agents = probNode.getProbNet().getAgents();
			String [] agentNames = null;
			if (agents != null) {
				//Set<String> names = agents.getNames();
				//agentNames = names.toArray(new String[names.size()]);
				// String []auxAgentNames = names.toArray(new String[names.size()]);
				// agentNames  = new String [names.size()+1];
				 agentNames  = new String [agents.size()+1];
				 agentNames [0]= "";
				 for (int i = 1; i < agents.size()+1; i++) {
					// agentNames[i] = auxAgentNames[i-1];
					 agentNames[i] = agents.get(i-1).getString();
				 }
				
			} else if (agents == null /*&& probNode.getVariable().getAgent() == null*/) {
				agentNames = new String[1]; 
				agentNames[0] = "";
			}/* else if (agents == null && probNode.getVariable().getAgent() != null) { 
				// Dec-POMDP --> POMDP an agent has been already assigned to current variable
				agentNames = new String[2]; 
				agentNames[0] = "";
				agentNames[1] = probNode.getVariable().getAgent().getString();
			}*/
			jComboBoxNetworkAgents = new JComboBox(agentNames);
			jComboBoxNetworkAgents.setName("jComboBoxAgents");
			jComboBoxNetworkAgents.setPreferredSize(new Dimension(50, 15));
			if (probNode.getVariable().getAgent() != null && agents != null) {
				String name = probNode.getVariable().getAgent().getString();
				int i ;
				for (i = 0; i < agentNames.length; i++) {
					if (name == agentNames[i]) {
						break;
					}
				}
				jComboBoxNetworkAgents.setSelectedIndex(i);
			} else {
				jComboBoxNetworkAgents.setSelectedIndex(0);
			}
			jComboBoxNetworkAgents.setEditable(true);
			jComboBoxNetworkAgents.addItemListener(this);

		}
		return jComboBoxNetworkAgents;
	}
	//TODO decision criteria comboBox getter
	private JComponent getAgentsOrDecisionCriteriaOrObserved() {
		if (probNode.getNodeType() == NodeType.DECISION) {
			return getJComboBoxNetworkAgents();
		} else if (probNode.getNodeType() == NodeType.UTILITY) {
			return getJComboBoxDecisionCriteria();
		} else if (probNode.getNodeType() == NodeType.CHANCE) {
			return getJCheckBoxAlwaysObserved();
		}
		//default
		return getJComboBoxNetworkAgents();
	}
	
	private JLabel getAgentsOrDecisionCriteriaOrObservedLabel() {
		if (probNode.getNodeType() == NodeType.DECISION) {
			return getJLabelNetworkAgents();
		} else if (probNode.getNodeType() == NodeType.UTILITY) {
			return getJLabelDecisionCriteria();
		} else if (probNode.getNodeType() == NodeType.CHANCE) {
			return getJLabelAlwaysObserved();
		}
		//default
		return getJLabelNetworkAgents();
	}

	private JLabel getJLabelDecisionCriteria () {
		if (jLabelDecisionCriteria == null) {
			jLabelDecisionCriteria = new JLabel();
			jLabelDecisionCriteria.setName("jLabelDecisionDriteria");
			jLabelDecisionCriteria.setHorizontalTextPosition(SwingConstants.LEFT);
			jLabelDecisionCriteria.setHorizontalAlignment(SwingConstants.LEFT);
			jLabelDecisionCriteria.setText("a Label");
			jLabelDecisionCriteria.setText(dialogStringResource
					.getString("NodeDefinitionPanel.jLabelDecisionDriteria.Text"));
			/*jLabelDecisionCriteria
					.setDisplayedMnemonic(dialogStringResource.getString(
							"NodeDefinitionPanel.jLabelNodePurpose.Mnemonic")
							.charAt(0));*/
			jLabelDecisionCriteria.setLabelFor(getJComboBoxDecisionCriteria());
		}
		return jLabelDecisionCriteria;
	}
	
	
	private JComboBox getJComboBoxDecisionCriteria() {
		if (jComboBoxDecisionCriteria == null) {
			StringsWithProperties decisionCriteria = probNode.getProbNet().getDecisionCriteria();
			String [] criteriaNames = null;
			if (decisionCriteria != null) {
				Set<String> names = decisionCriteria.getNames();
				criteriaNames = names.toArray(new String[names.size()]);
				 String []auxAgentNames = names.toArray(new String[names.size()]);
				 criteriaNames  = new String [names.size()+1];
				 criteriaNames [0]= "";
				 for (int i = 1; i < names.size()+1; i++) {
					 criteriaNames[i] = auxAgentNames[i-1];
				 }
				
			} else {
				criteriaNames = new String[1]; 
				criteriaNames[0] = "";
			}
			jComboBoxDecisionCriteria = new JComboBox(criteriaNames);
			jComboBoxDecisionCriteria.setName("jComboBoxDecisionCriteria");
			jComboBoxDecisionCriteria.setSelectedIndex(0);
			//jComboBoxNetworkAgents.setMaximumRowCount(9);
			// jComboBoxNodePurpose.addItemListener( this );
			jComboBoxDecisionCriteria.setEditable(true);
			
		}
		return jComboBoxDecisionCriteria;

	}
	/**
	 * This method initialises jLabelNodeDefinitionComment
	 * 
	 * @return a new label for the comment
	 */
	protected JTextArea getJTextAreaLabelNodeDefinitionComment() {

		if (jTextAreaLabelNodeDefinitionComment == null) {
			jTextAreaLabelNodeDefinitionComment = new JTextArea();
			jTextAreaLabelNodeDefinitionComment.setLineWrap(true);
			jTextAreaLabelNodeDefinitionComment.setOpaque(false);
			jTextAreaLabelNodeDefinitionComment
					.setName("jTextAreaLabelNodeDefinitionComment");
			jTextAreaLabelNodeDefinitionComment.setFocusable(false);
			jTextAreaLabelNodeDefinitionComment.setEditable(false);
			jTextAreaLabelNodeDefinitionComment.setFont(getJLabelNodeName()
					.getFont());
			jTextAreaLabelNodeDefinitionComment.setText("an Extended Label");
			MessageFormat messageForm = new MessageFormat(
					dialogStringResource
							.getString("NodeDefinitionPanel.jTextAreaLabelNodeDefinitionComment.Text"));
			Object[] labelArgs = new Object[] { getJTextFieldNodeName()
					.getText() };
			jTextAreaLabelNodeDefinitionComment.setText(messageForm
					.format(labelArgs));
		}
		return jTextAreaLabelNodeDefinitionComment;
	}

	/**
	 * This method initialises commentHTMLScrollPaneNodeDefinitionComment
	 * 
	 * @return a new comment HTML scroll pane.
	 */
	private CommentHTMLScrollPane getCommentHTMLScrollPaneNodeDefinitionComment() {

		if (commentHTMLScrollPaneNodeDefinitionComment == null) {
			commentHTMLScrollPaneNodeDefinitionComment = new CommentHTMLScrollPane();
			commentHTMLScrollPaneNodeDefinitionComment
					.setName("commentHTMLScrollPaneNodeDefinitionComment");
			commentHTMLScrollPaneNodeDefinitionComment.addCommentListener(this);
		}
		return commentHTMLScrollPaneNodeDefinitionComment;
	}

	/**
	 * @return the variableType
	 */
	public VariableType getVariableType() {

		return variableType;
	}

	/**
	 * @param variableType
	 *            the variableType to set
	 */
	private void setNodeVariable(VariableType variableType) {

		this.variableType = variableType;
	}

	/**
	 * Invoked when an item has been selected.
	 * 
	 * @param e
	 *            event information.
	 */
	public void itemStateChanged(ItemEvent e) {
	
		int optionDeselected = 0;
		int optionSelected = 0;
		ItemSelectable itemSelectable = e.getItemSelectable();
		Object selected[] = itemSelectable.getSelectedObjects();
		String itemSelected = selected.length == 0 ? "null" : selected[0]
				.toString();
		JComboBox comboBox = (JComboBox) e.getSource();
		if (e.getStateChange() == ItemEvent.DESELECTED) {
			optionDeselected = comboBox.getSelectedIndex();
		}
		if (e.getStateChange() == ItemEvent.SELECTED) {
			optionSelected = comboBox.getSelectedIndex();
		}
		//optionSelected = comboBox.getSelectedIndex();
		
		if (comboBox.getName().equals("jComboBoxNodePurpose")) {

			if (!(itemSelected == null)
					&& e.getStateChange() == ItemEvent.SELECTED) {
				PurposeEdit purposeEdit = null;
				for (String purposeString : Purpose.getListStrings(true)) {
					if (itemSelected.equals(Purpose.getString(purposeString))) {
						purposeEdit = new PurposeEdit(probNode, purposeString);
						break;
					}

				}
				try {
					probNode.getProbNet().getPNESupport()
							.announceEdit(purposeEdit);
					probNode.getProbNet().getPNESupport().doEdit(purposeEdit);
				} catch (ConstraintViolationException e1) {
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString(e1.getMessage()), messageStringResource
							.getString("ConstraintViolationException"),
							JOptionPane.ERROR_MESSAGE);
					comboBox.setSelectedIndex(optionDeselected);
					comboBox.requestFocus();

				} catch (CanNotDoEditException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e1.getMessage() ),
						messageStringResource.getString( e1.getMessage() ),
						JOptionPane.ERROR_MESSAGE );

				} catch (DoEditException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e1.getMessage() ),
						messageStringResource.getString( e1.getMessage() ),
						JOptionPane.ERROR_MESSAGE );

				} catch (NotEnoughMemoryException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e1.getMessage() ),
						messageStringResource.getString( e1.getMessage() ),
						JOptionPane.ERROR_MESSAGE );

				} catch (NonProjectablePotentialException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e2.getMessage() ),
						messageStringResource.getString( e2.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (WrongCriterionException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e3.getMessage() ),
						messageStringResource.getString( e3.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				}
			}
		} else if (comboBox.getName().equals("jComboBoxNodeRelevance")) {
			if (!(itemSelected == null)
					&& e.getStateChange() == ItemEvent.SELECTED) {
				RelevanceEdit relevanceEdit = null;

				relevanceEdit = new RelevanceEdit(probNode,
						Double.valueOf(itemSelected));

				try {
					probNode.getProbNet().getPNESupport()
							.announceEdit(relevanceEdit);
					probNode.getProbNet().getPNESupport().doEdit(relevanceEdit);
				} catch (ConstraintViolationException e1) {
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString(e1.getMessage()), messageStringResource
							.getString("ConstraintViolationException"),
							JOptionPane.ERROR_MESSAGE);
					comboBox.setSelectedIndex(optionDeselected);
					comboBox.requestFocus();

				} catch (CanNotDoEditException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e1.getMessage() ),
						messageStringResource.getString( e1.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (DoEditException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e1.getMessage() ),
						messageStringResource.getString( e1.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (NotEnoughMemoryException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e1.getMessage() ),
						messageStringResource.getString( e1.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (NonProjectablePotentialException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e2.getMessage() ),
						messageStringResource.getString( e2.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (WrongCriterionException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e3.getMessage() ),
						messageStringResource.getString( e3.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				}
			}
		} else if (comboBox.getName().equals("jComboBoxTimeSlice")) {
			if (!(itemSelected == null)
					&& e.getStateChange() == ItemEvent.SELECTED) {
				TimeSliceEdit timeSliceEdit = null;
				if (itemSelected.equals(dialogStringResource
						.getString("NodeDefinitionPanel.Atemporal.Text"))) {
					timeSliceEdit  = new TimeSliceEdit(probNode,
							Integer.MIN_VALUE);
				} else {
					timeSliceEdit  = new TimeSliceEdit(probNode,
						Integer.valueOf(itemSelected));
				}
				
				try {
					probNode.getProbNet().getPNESupport().announceEdit(timeSliceEdit);
					probNode.getProbNet().getPNESupport().doEdit(timeSliceEdit);
					//comboBox.setSelectedIndex(optionSelected);
				} catch (DoEditException e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(this, messageStringResource
								.getString( e1.getMessage() ),
							messageStringResource.getString( e1.getMessage() ),
							JOptionPane.ERROR_MESSAGE );
				} catch (NotEnoughMemoryException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e1.getMessage() ),
						messageStringResource.getString( e1.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (ConstraintViolationException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e1.getMessage() ),
						messageStringResource.getString( e1.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (CanNotDoEditException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e1.getMessage() ),
						messageStringResource.getString( e1.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (NonProjectablePotentialException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e1.getMessage() ),
						messageStringResource.getString( e1.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (WrongCriterionException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e1.getMessage() ),
						messageStringResource.getString( e1.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				}
				
			}
		} else if (comboBox.getName().equals("jComboBoxAgents")) {
			if (!(itemSelected == null)
					/*&& e.getStateChange() == ItemEvent.SELECTED*/) {
				StringWithProperties agent = new StringWithProperties(itemSelected);
				NodeAgentEdit nodeAgentEdit = new NodeAgentEdit(probNode, agent);
				
				try {
					
					probNode.getProbNet().getPNESupport().announceEdit(nodeAgentEdit);
					probNode.getProbNet().getPNESupport().doEdit(nodeAgentEdit);
				//	comboBox.setSelectedIndex(optionSelected);
				} catch (DoEditException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (NotEnoughMemoryException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ConstraintViolationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (CanNotDoEditException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
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

	/**
	 * Invoked when a focus lost action occurs.
	 * 
	 * @param e
	 *            - event information
	 */
	public void focusLost(FocusEvent e) {
		if (e.getSource().equals(this.jTextFieldNodeName)) {
			// actionPerformedNodeNameChangeValue();
			if (!probNode.getName().equals(this.jTextFieldNodeName.getText())) {
				NodeNameEdit nodeNameEdit = new NodeNameEdit(probNode,
						this.jTextFieldNodeName.getText());
				try {
					probNode.getProbNet().getPNESupport()
							.announceEdit(nodeNameEdit);
					probNode.getProbNet().getPNESupport().doEdit(nodeNameEdit);
				} catch (ConstraintViolationException e1) {
					// TODO Auto-generated catch block
					// e1.printStackTrace();
					JOptionPane.showMessageDialog(this, e1.getMessage(),
							messageStringResource
									.getString("ConstraintViolationException"),
							JOptionPane.ERROR_MESSAGE);
					jTextFieldNodeName.setText(probNode.getName());
					jTextFieldNodeName.requestFocus();
				} catch (CanNotDoEditException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e1.getMessage() ),
						messageStringResource.getString( e1.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (DoEditException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e1.getMessage() ),
						messageStringResource.getString( e1.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (NotEnoughMemoryException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e1.getMessage() ),
						messageStringResource.getString( e1.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (NonProjectablePotentialException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e2.getMessage() ),
						messageStringResource.getString( e2.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (WrongCriterionException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e3.getMessage() ),
						messageStringResource.getString( e3.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				}
			}
		}
	}

	/**
	 * Invoked when a focus gained action occurs.
	 * 
	 * @param e
	 *            - event information
	 */

	public void focusGained(FocusEvent e) {

		if (e.getSource().equals(this.jTextFieldNodeName)) {
			this.getJTextFieldNodeName().selectAll();
		}
	}

	/**
	 * This method fills the content of the fields from a NodeProperties object.
	 * 
	 * @param adittionalProperties
	 *            object from where load the information.
	 */
	public void setFieldsFromProperties(ProbNode properties) {

		jTextFieldNodeName.setText(properties.getName());
		// node type elements in the panel depending network type
		/*
		 * NetworkType netType =
		 * adittionalProperties.getNetwork().getNetworkType(); if
		 * (NetworkType.BAYESIAN_NET == netType) {
		 * jRadioButtonChanceNodeType.setEnabled( true );
		 * jRadioButtonDecisionNodeType.setEnabled( false );
		 * jRadioButtonUtilityNodeType.setEnabled( false ); } else if
		 * (NetworkType.INFLUENCE_DIAGRAM == netType) {
		 * jRadioButtonChanceNodeType.setEnabled( false );
		 * jRadioButtonDecisionNodeType.setEnabled( false );
		 * jRadioButtonUtilityNodeType.setEnabled( false ); } else if
		 * (NetworkType.CHAIN_GRAPH == netType) { // future extension } else if
		 * (NetworkType.MARKOV_NET == netType) { // future extension }
		 */
		// node type
		/*
		 * switch (adittionalProperties.getNodeType()) { case CHANCE: {
		 * jRadioButtonChanceNodeType.setEnabled( true );
		 * jRadioButtonChanceNodeType.setSelected( true ); break; } case
		 * DECISION: { jRadioButtonDecisionNodeType.setEnabled( true );
		 * jRadioButtonDecisionNodeType.setSelected( true ); break; } case
		 * UTILITY: { jRadioButtonUtilityNodeType.setEnabled( true );
		 * jRadioButtonUtilityNodeType.setSelected( true ); break; } default:
		 * break; } if (adittionalProperties.getNodeType() == NodeType.UTILITY)
		 * { jRadioButtonDiscreteNodeVariableType.setEnabled( false );
		 * jRadioButtonDiscretizedNodeVariableType.setEnabled( false );
		 * jRadioButtonContinuousNodeVariableType.setEnabled( false ); }
		 */
		// node variable type
		// relevance

		// if (properties.getVariable().getVariableType() ==
		// VariableType.FINITE_STATES){

		jComboBoxNodeRelevance.removeItemListener(this);
		jComboBoxNodePurpose.removeItemListener(this);

		jComboBoxNodeRelevance.setSelectedItem(properties.getRelevance());
		jComboBoxNodeRelevance.setEnabled(true);
		// purpose
		jComboBoxNodePurpose.setSelectedIndex(Purpose.getIndex(properties
				.getPurpose()));
		jComboBoxNodePurpose.setEnabled(true);

		jComboBoxNodeRelevance.addItemListener(this);
		jComboBoxNodePurpose.addItemListener(this);
		// }
		// node comment title
		MessageFormat messageForm = new MessageFormat(
				dialogStringResource
						.getString("NodeDefinitionPanel.commentHTMLScrollPaneNodeDefinitionComment.Text"));
		String shortNodeName = getJTextFieldNodeName().getText();
		Object[] labelArgs = new Object[] { shortNodeName };
		commentHTMLScrollPaneNodeDefinitionComment.setTitle(messageForm
				.format(labelArgs));
		// node def comment
		commentHTMLScrollPaneNodeDefinitionComment
				.setCommentHTMLTextPaneText(properties.getComment());

		jCheckboxAlwaysObserved.setSelected(properties.getVariable()
				.isAlwaysObserved());
	}

	/**
	 * This method checks that the name field is filled and there isn't any node
	 * with the same name.
	 * 
	 * @return true, if the name field isn't empty and there isn't any node with
	 *         this name; otherwise, false.
	 */
	public boolean checkName() {

		String name = jTextFieldNodeName.getText();
		boolean result = true;

		if ((name == null) || name.equals("")) {
			result = false;
		} else if (!probNode.getName().equals(name)
				&& Utilities.existNode(probNode.getProbNet(), name)) {
			result = false;
		}
		if (!result) {
			jTextFieldNodeName.requestFocus();
			return false;
		}
		return true;
	}

	/**
	 * This method checks that the purpose field is filled if this field is
	 * enabled.
	 * 
	 * @return true, if the purpose field isn't empty; otherwise, false.
	 */
	public boolean checkPurpose() {

		return true;
	}

	/**
	 * serial uid
	 */
	private static final long serialVersionUID = 1047978130482205148L;
	/**
	 * The Node Name Label
	 */
	private JLabel jLabelNodeName = null;
	/**
	 * The Node Name Text Field
	 */
	private JTextField jTextFieldNodeName = null;
	/**
	 * The always observed property label
	 */
	private JLabel jLabelAlwaysObserved = null;
	/**
	 * The always observed checkbox
	 */
	private JCheckBox jCheckboxAlwaysObserved = null;
	/**
	 * The Node Type Label
	 */
	private JLabel jLabelNodeType = null;
	/**
	 * Panel that contains the node type options group.
	 */
	private JPanel jPanelNodeType = null;
	/**
	 * The Node Type Button Group
	 */
	private ButtonGroup jButtonGroupNodeType = null;
	/**
	 * The Node Type Chance Radio Button
	 */
	private JRadioButton jRadioButtonChanceNodeType = null;
	/**
	 * The Node Type Decision Radio Button
	 */
	private JRadioButton jRadioButtonDecisionNodeType = null;
	/**
	 * The Node Type Utility Radio Button
	 */
	private JRadioButton jRadioButtonUtilityNodeType = null;
	/**
	 * The Node Variable Type Label
	 */
	private JLabel jLabelNodeVariableType = null;
	/**
	 * Network agents label
	 */
	private JLabel jLabelNetworkAgents = null;
	/**
	 * Panel that contains the variables type options group.
	 */
	private JPanel jPanelNodeVariableType = null;

	/**
	 * internal node type item for convenience purpose
	 */
	private VariableType variableType = null;
	/**
	 * the Node Relevance Label
	 */
	private JLabel jLabelNodeRelevance = null;
	/**
	 * The Node Relevance Combo Box
	 */
	private JComboBox jComboBoxNodeRelevance = null;
	/**
	 * The Node Purpose Label
	 */
	private JLabel jLabelNodePurpose = null;
	/**
	 * The Node Purpose Combo Box
	 */
	private JComboBox jComboBoxNodePurpose = null;

	/**
	 * The Node Definition Comment Label
	 */
	private JTextArea jTextAreaLabelNodeDefinitionComment;
	/**
	 * The Node Comment Scroll Panel box
	 */
	private CommentHTMLScrollPane commentHTMLScrollPaneNodeDefinitionComment = null;

	/**
	 * Dialog string resource.
	 */
	private StringResource dialogStringResource;
	/**
	 * Messages string resource.
	 */
	private StringResource messageStringResource;

	/**
	 * Object where all information will be saved.
	 */
	private ProbNode probNode = null;

	/**
	 * Specifies if the node whose adittionalProperties are edited is new.
	 */
	private boolean newNode = false;

	public void commentHasChanged() {
		NodeCommentEdit nodeCommentEdit = new NodeCommentEdit(probNode,
				getCommentHTMLScrollPaneNodeDefinitionComment()
						.getCommentText(), "DefinitionComment");
		try {
			probNode.getProbNet().getPNESupport().announceEdit(nodeCommentEdit);
			probNode.getProbNet().getPNESupport().doEdit(nodeCommentEdit);
		} catch (ConstraintViolationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		} catch (CanNotDoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		} catch (DoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		} catch (NotEnoughMemoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		} catch (NonProjectablePotentialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		} catch (WrongCriterionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		}

	}

	/****
	 * Starts the edit event to change the alwaysObserved property
	 */
	public void alwaysObservedPropertyHasChanged() {
		NodeAlwaysObservedEdit edit = new NodeAlwaysObservedEdit(this.probNode,
				this.jCheckboxAlwaysObserved.isSelected());
		try {
			probNode.getProbNet().getPNESupport().announceEdit(edit);
			probNode.getProbNet().getPNESupport().doEdit(edit);
		} catch (NotEnoughMemoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		} catch (ConstraintViolationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		} catch (CanNotDoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		} catch (NonProjectablePotentialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		} catch (WrongCriterionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		} catch (DoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		}

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.jCheckboxAlwaysObserved)) {
			alwaysObservedPropertyHasChanged();
		}

	}
}