/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog.node;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import org.apache.log4j.Logger;
import org.openmarkov.core.action.NodeReplaceStatesEdit;
import org.openmarkov.core.action.PrecisionEdit;
import org.openmarkov.core.action.VariableTypeEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.action.PartitionedIntervalEdit;
import org.openmarkov.core.gui.component.DiscretizeTablePanel;
import org.openmarkov.core.gui.dialog.common.CommentHTMLScrollPane;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.gui.util.GUIDefaultStates;
import org.openmarkov.core.gui.util.Utilities;
import org.openmarkov.core.model.network.DefaultStates;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.UtilStrings;
import org.openmarkov.core.model.network.VariableType;


/**
 * Panel to set the values of a node with discretized values
 * 
 * @author jlgozalo
 * @author mkpalacio
 * @author myebra
 * @version 1.0 jlgozalo
 * @version 1.1 mkpalacio
 * @version 1.2 myebra
 */
public class NodeDomainValuesTablePanel extends JPanel implements ItemListener, ActionListener{
	
	/**
	 * serial uid
	 */
	private static final long serialVersionUID = 1047978130482205148L;

	/**
	 * Dialog string resource.
	 */
	private StringResource dialogStringResource;

	/**
	 * Object where all information will be saved.
	 */
	private ProbNode probNode = null;

	/**
	 * Specifies if the node whose adittionalProperties are edited is new.
	 */
	private boolean newNode = false;

	/**
	 * label for the values comboBox for the states of the node
	 */
	private JLabel jLabelStatesValues;

	/**
	 * combo box to select the values for the states of the node
	 */
	private JComboBox jComboBoxStatesValues;

	/**
	 * panel, buttonGroup and radioButtons to define monotony in the panel
	 */
	private JPanel jPanelNodeType;
	
	/**
	 * Logger
	 * 
	 */
	
	private Logger logger;
	
	private JLabel jLabelNodeVariableType;
	
	//private PrefixedKeyTablePanel nodeDiscreteStatesTablePanel;
	
	private ButtonGroup buttonGroup = new ButtonGroup();
	
	private JRadioButton jRadioButtonMonotonyDown;
	
	private JRadioButton jRadioButtonMonotonyUp;
	
	private JPanel jPanelMonotonyUpDown;

	/**
	 * label for the table to show the values of the node
	 */
	private JLabel jLabelValuesPanel = null;
	/**
	 * table to show the states of the node
	 */
	private DiscretizeTablePanel discretizedNodeStatesTablePanel = null;

	/**
	 * The Node Values Comment Label
	 */
	private JTextArea jTextAreaLabelNodeValuesComment;
	/**
	 * The Node Values Comment Scroll Panel box
	 */
	private CommentHTMLScrollPane commentHTMLScrollPaneNodeValuesComment = null;
	/**
	 * partitioned interval
	 */
	private PartitionedInterval partitionedInterval = null;
	/**
	 * unit field
	 */
	private JTextField jFieldUnit;
	
	/**
	 * precision combobox
	 */
	private JComboBox jComboBoxPrecision;
	/**
	 * precision field
	 */
	private JFormattedTextField jFormattedTextFieldPrecision;
	/**
	 * label for the precision field
	 */
	private JLabel jLabelPrecision;
	/**
	 * label for the unit field
	 */
	private JLabel jLabelUnit;
	/**
	 * Observable notifier
	 */
	//private ElementObservable notifier  = null;
	/**
	 * TODO listener for actions - 
	 */
	public NodeDiscretizeValuesTablePanelListener listener = null;
	
	private JComboBox jComboBoxNodeVariableType;
	
	private StringResource messageStringResource;
	
	private boolean uploadingData = false;
	
	
	
	/**
	 * constructor without construction parameters
	 */
	public NodeDomainValuesTablePanel() {
		this(true);//, new ElementObservable());
	}

	/**
	 * constructor without construction parameters
	 */
	public NodeDomainValuesTablePanel(ProbNode probNode) {
		this(true);//, notifier);
		this.probNode = probNode;
		try {
			initialize();
		} catch (Throwable e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		}
	}
	

	/**
	 * This method initialises this instance.
	 * 
	 * @param newNode
	 *            true if the node is a new node; otherwise false
	 */
	public NodeDomainValuesTablePanel(final boolean newNode){ 
	                                      //ElementObservable notifier) {

		dialogStringResource = StringResourceLoader.getUniqueInstance()
						.getBundleDialogs();
		messageStringResource =
			StringResourceLoader.getUniqueInstance().getBundleMessages();
		
		setName("NodeDomainValuesTablePanel");
		this.newNode = newNode;
		this.listener = new NodeDiscretizeValuesTablePanelListener(this);
		this.logger = Logger.getLogger(NodeDomainValuesTablePanel.class);
		

	}
	
	/**
	 * <p>
	 * <code>Initialize</code>
	 * <p>
	 * initialize the layout for this panel
	 */
	
	private void initialize() throws Exception {


		if (probNode.getNodeType() == NodeType.UTILITY) {
			getJComboBoxNodeVariableType().setSelectedItem(dialogStringResource.getString(
					"NodeDomainValuesTablePanel.jComboBoxNodeVariableType." +
							"Items.Continuous"));
			getNodeDiscretizedStatesTablePanel().setEnabled(false);
			getNodeDiscretizedStatesTablePanel().setVisible(false);
			getJPanelMonotonyUpDown().setEnabled(false);
			getJPanelMonotonyUpDown().setVisible(false);
			getJLabelValuesPanel().setEnabled(false);
			getJLabelValuesPanel().setVisible(false);
			getJFormattedTextFieldPrecision().setValue( Double.valueOf( probNode.
					getVariable().getPrecision() ) );
			//getJFormattedTextFieldUnit().setValue(value);
		
		}
		
		
		setPreferredSize(new Dimension(600, 375));
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(getJLabelNodeVariableType())
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(getJComboBoxNodeVariableType(), GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE)
									.addGap(18)
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(getJPanelMonotonyUpDown(),GroupLayout.PREFERRED_SIZE, 212, GroupLayout.PREFERRED_SIZE)
									)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(getJLabelPrecision())
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(/*getJFormattedTextFieldPrecision()*/getJComboBoxPrecision(), GroupLayout.PREFERRED_SIZE,  49, GroupLayout.PREFERRED_SIZE)
									.addGap(18) 
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(getJLabelUnit())
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(getJTextFieldUnit(), GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
									)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(getJLabelValuesPanel(), GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
									.addComponent(getNodeDiscretizedStatesTablePanel(), GroupLayout.DEFAULT_SIZE, 627, Short.MAX_VALUE))
									)
						
						.addContainerGap())
		)));
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getJLabelNodeVariableType())
						.addComponent(getJComboBoxNodeVariableType(), GroupLayout.PREFERRED_SIZE, /*20*/GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(getJPanelMonotonyUpDown(), GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
						)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getJLabelPrecision(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(/*getJFormattedTextFieldPrecision()*/getJComboBoxPrecision())
						.addComponent(getJLabelUnit(),GroupLayout.PREFERRED_SIZE, /*25*/GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(getJTextFieldUnit())
						)
					.addGap(21)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getJLabelValuesPanel(), 
								GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
						.addComponent(getNodeDiscretizedStatesTablePanel(),GroupLayout.PREFERRED_SIZE,/* 24*/GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
					.addContainerGap(77, Short.MAX_VALUE))
		);
		
		Component[] components = new Component [2];
		components[0] = getJTextFieldUnit();
		components[1] = getJComboBoxPrecision() /*getJFormattedTextFieldPrecision()*/;
		groupLayout.linkSize(components);
		
		Component[] labelComponents = new Component [2];
		labelComponents[0] = getJLabelNodeVariableType();
		labelComponents[1] = getJLabelPrecision();
		groupLayout.linkSize(labelComponents);
		setLayout(groupLayout);
			
	}

	/**
	 * @param newNode
	 *            the newNode to set
	 */
	public void setNewNode(boolean newNode) {

		this.newNode = newNode;
	}
	
	ProbNode getProbNode(){
		return probNode;
	}

	
	/**
	 * This method fills the content of the fields from a NodeProperties object.
	 * 
	 * @param adittionalProperties
	 *            object from where load the information.
	 */
	public void setFieldsFromProperties(ProbNode properties) {
		
		setUploadingData(true);
		//jComboBoxStatesValues.removeItemListener(this);
		(((DiscretizeTablePanel)getNodeDiscretizedStatesTablePanel()).getStandarDomainButton()).removeActionListener( this );
		
	//	jFormattedTextFieldPrecision.setValue( Double.valueOf( properties.getVariable().getPrecision() ) );
		getJComboBoxPrecision().setSelectedItem(String.valueOf(properties.getVariable().getPrecision()));
		//getJComboBoxPrecision().setSelectedIndex(4);// initialized precision to 0.01
		jFieldUnit.setText	(properties.getVariable().getUnit().getString());
		
		if (properties != null) {
			if (properties.getVariable().getVariableType() == 
					VariableType.DISCRETIZED || properties.getVariable().getVariableType() == 
					VariableType.NUMERIC){
				
				State [] states = properties.getVariable().getStates();
				Object[][] tableData = null;
				/*	jComboBoxStatesValues.setSelectedIndex(DefaultStates
							.getIndex(states));
				if (jComboBoxStatesValues.getSelectedIndex() == 
						(jComboBoxStatesValues.getItemCount() - 1) 
						&& Utilities.hasLimitBracketSymbols(states)) { 
							// if the values are others and there are partitioned intervals 
							// described
							tableData = convertStringsToTableFormat(states);
							discretizedNodeStatesTablePanel.setData(tableData);
					} else {*/
							//discretizedNodeStatesTablePanel.setPartitionedInterval();
				getNodeDiscretizedStatesTablePanel().setDataFromPartitionedInterval(probNode.getVariable().getPartitionedInterval());
							tableData = getNodeDiscretizedStatesTablePanel().getData();

							for (int i = 0; i < tableData.length; i++) {
								for (int j = 3; j < tableData[0].length; j++) {
									if (j==3 || j==5) {
										String value = (String) tableData [i][j];
										if (value != "\u221E" && value != "-"+"\u221E" ) { // Infinity values
											/*String roundedValue = Utilities.roundedString(value, 
													Double.toString((Double) getJFormattedTextFieldPrecision().getValue()));
											getNodeStatesTablePanel().getValuesTable().setValueAt(roundedValue, i, j);*/
											
											
												/*String roundedValue = Utilities.roundWithPrecisionToString(Double.parseDouble(value), 
															Double.toString((Double) getJFormattedTextFieldPrecision().getValue()));*/
											//getNodeStatesTablePanel().getValuesTable().setValueAt(roundedValue, i, j);
											getNodeDiscretizedStatesTablePanel().getValuesTable().setValueAt(value, i, j);
										}
									}
								}
							//}
					}
			}
			
			/*jFormattedTextFieldPrecision.addPropertyChangeListener("value", 
					listener);*/
		
			switch (properties.getVariable().getVariableType()) {
			case FINITE_STATES: {
				getJComboBoxNodeVariableType().setSelectedItem(dialogStringResource
						.getString("NodeDomainValuesTablePanel." +
								"jComboBoxNodeVariableType.Items.Discrete"));
				getJLabelPrecision().setEnabled(false);
				getJFormattedTextFieldPrecision().setEnabled(false);
				getJLabelValuesPanel().setVisible(true);
				getJLabelDomainValues().setVisible(true);
				getJComboBoxStatesValues().setVisible(true);
				
				getJTextFieldUnit().setEnabled(false);
				getJTextFieldUnit().setVisible(false);
				getJLabelUnit().setEnabled(false);
				getJLabelUnit().setVisible(false);
				
				getJComboBoxPrecision().setVisible(false);
				getJComboBoxPrecision().setEnabled(false);
				getJLabelPrecision().setVisible(false);
				//getJFormattedTextFieldPrecision().setVisible(false);
				getJPanelMonotonyUpDown().setVisible(false);
    			jRadioButtonMonotonyUp.setEnabled(false);
    			jRadioButtonMonotonyDown.setEnabled(false);
    			jRadioButtonMonotonyUp.setSelected(false);
    			jRadioButtonMonotonyDown.setSelected(true);
    			((DiscretizeTablePanel)getNodeDiscretizedStatesTablePanel()).setEnablePanelButton(true);
				((DiscretizeTablePanel)getNodeDiscretizedStatesTablePanel()).setVisibleButtonPanel(true);
				getJLabelDomainValues().setVisible(false);
				getJComboBoxStatesValues().setVisible(false);
				((DiscretizeTablePanel)getNodeDiscretizedStatesTablePanel()).getStandarDomainButton().setVisible(true);
				((DiscretizeTablePanel)getNodeDiscretizedStatesTablePanel()).getStandarDomainButton().setEnabled(true);
    			/*discretizedNodeStatesTablePanel.getInfiniteNegativeDoubleButton().setVisible(false);
    			discretizedNodeStatesTablePanel.getInfinitePositiveDoubleButton().setVisible(false);*/
				// node comment title
				MessageFormat messageForm =
					new MessageFormat(dialogStringResource.getString(
							"DiscreteValuesTablePanel." +
							"commentHTMLScrollPaneNodeValuesComment.Text"));
				String shortNodeName = properties.getName();
				Object[] labelArgs = new Object[] { shortNodeName };
				// states of the node
				State[] states = properties.getVariable().getStates();
				State[] reorderedStates = states.clone();
				Collections.reverse(Arrays.asList(reorderedStates));
			/*	jComboBoxStatesValues.setSelectedIndex(DefaultStates.getIndex(
						states));
				
				if (jComboBoxStatesValues.getSelectedIndex() == 
					(jComboBoxStatesValues.getItemCount() - 1)) { 
					
					Object[][] tableData = getDataFromStates(reorderedStates);
					discretizedNodeStatesTablePanel.setData(tableData);
					
				} else {*/
					
					Object[][] tableData = getDataFromStates(reorderedStates);
					getNodeDiscretizedStatesTablePanel().setData(tableData);
					
									
			//	}
				
				break;
			}
			case NUMERIC: {
				getJComboBoxNodeVariableType().setSelectedItem(dialogStringResource
						.getString("NodeDomainValuesTablePanel." +
								"jComboBoxNodeVariableType.Items.Continuous"));
				getJLabelPrecision().setEnabled(true);
				getJComboBoxPrecision().setVisible(true);
				getJComboBoxPrecision().setEnabled(true);
				//getJFormattedTextFieldPrecision().setEnabled(true);
				getJLabelValuesPanel().setVisible(false);
				getJLabelDomainValues().setVisible(false);
				getJComboBoxStatesValues().setVisible(false);
				getJLabelPrecision().setVisible(true);
				getJFormattedTextFieldPrecision().setVisible(true);
				getJPanelMonotonyUpDown().setVisible(false);
					jRadioButtonMonotonyUp.setEnabled(false);
					jRadioButtonMonotonyDown.setEnabled(false);
					jRadioButtonMonotonyUp.setSelected(false);
					jRadioButtonMonotonyDown.setSelected(false);
					((DiscretizeTablePanel)getNodeDiscretizedStatesTablePanel()).setEnablePanelButton(false);
					((DiscretizeTablePanel)getNodeDiscretizedStatesTablePanel()).setVisibleButtonPanel(false);
					((DiscretizeTablePanel)getNodeDiscretizedStatesTablePanel()).setEnabledAddValue(false);
					((DiscretizeTablePanel)getNodeDiscretizedStatesTablePanel()).setEnabledRemoveValue(false);
					((DiscretizeTablePanel)getNodeDiscretizedStatesTablePanel()).setEnabledUpValue(false);
					((DiscretizeTablePanel)getNodeDiscretizedStatesTablePanel()).setEnabledDownValue(false);
					((DiscretizeTablePanel)getNodeDiscretizedStatesTablePanel()).getStandarDomainButton().setVisible(false);
					((DiscretizeTablePanel)getNodeDiscretizedStatesTablePanel()).getStandarDomainButton().setEnabled(false);
					getJTextFieldUnit().setEnabled(true);
					getJTextFieldUnit().setVisible(true);
					getJLabelUnit().setEnabled(true);
					getJLabelUnit().setVisible(true);
					/*discretizedNodeStatesTablePanel.getInfiniteNegativeDoubleButton().setVisible(true);
	    			discretizedNodeStatesTablePanel.getInfinitePositiveDoubleButton().setVisible(true);*/
				
				break;
			}
			case DISCRETIZED: {
				getJComboBoxNodeVariableType().setSelectedItem(dialogStringResource
						.getString("NodeDomainValuesTablePanel." +
								"jComboBoxNodeVariableType.Items.Discretized"));
				getJLabelPrecision().setEnabled(true);
				getJComboBoxPrecision().setVisible(true);
				getJComboBoxPrecision().setEnabled(true);
				//getJFormattedTextFieldPrecision().setEnabled(true);
				getJLabelValuesPanel().setVisible(true);
				getJLabelDomainValues().setVisible(true);
				getJComboBoxStatesValues().setVisible(true);
				
				getJLabelPrecision().setVisible(true);
			//	getJFormattedTextFieldPrecision().setVisible(true);
				getJPanelMonotonyUpDown().setVisible(true);
	    			jRadioButtonMonotonyUp.setEnabled(true);
	    			jRadioButtonMonotonyDown.setEnabled(true);
	    			jRadioButtonMonotonyUp.setSelected(false);
	    			jRadioButtonMonotonyDown.setSelected(true);
	    			((DiscretizeTablePanel)getNodeDiscretizedStatesTablePanel()).setUpMonotony(false);
	    			((DiscretizeTablePanel)getNodeDiscretizedStatesTablePanel()).setEnablePanelButton(true);
					((DiscretizeTablePanel)getNodeDiscretizedStatesTablePanel()).setVisibleButtonPanel(true);
					getJLabelDomainValues().setVisible(false);
					getJComboBoxStatesValues().setVisible(false);
					((DiscretizeTablePanel)getNodeDiscretizedStatesTablePanel()).getStandarDomainButton().setVisible(true);
					((DiscretizeTablePanel)getNodeDiscretizedStatesTablePanel()).getStandarDomainButton().setEnabled(true);
					getJTextFieldUnit().setEnabled(true);
					getJTextFieldUnit().setVisible(true);
					getJLabelUnit().setEnabled(true);
					getJLabelUnit().setVisible(true);
	    			/*((DiscretizeTablePanel)getNodeStatesTablePanel()).
	    				setEnablePanelButton(true);
	    			((DiscretizeTablePanel)getNodeStatesTablePanel()).
    				setVisibleButtonPanel(true);*/
	    			/*discretizedNodeStatesTablePanel.getInfiniteNegativeDoubleButton().setVisible(true);
	    			discretizedNodeStatesTablePanel.getInfinitePositiveDoubleButton().setVisible(true);*/
				break;
			}
			}
			
			
		}
	//	jComboBoxStatesValues.addItemListener(this);
		(((DiscretizeTablePanel)getNodeDiscretizedStatesTablePanel()).getStandarDomainButton()).addActionListener( this );
		setUploadingData(false);
	}


	
		/**
	 * This method initializes NodeValuesTable.
	 * 
	 * @return the DiscretizeTablePanel for the Node Values
	 */
	protected DiscretizeTablePanel getNodeDiscretizedStatesTablePanel() {

		if (discretizedNodeStatesTablePanel == null) {
			String[] columnNames = {
							dialogStringResource.getString(
									"DiscretizeTableModel.Columns." +
									"IntervalId.Text"),
							dialogStringResource.getString(
									"DiscretizeTableModel.Columns." +
									"IntervalName.Text"),
							dialogStringResource.getString(
									"DiscretizeTableModel.Columns." +
									"LowLimitSymbol.Text"),
							dialogStringResource.getString(
									"DiscretizeTableModel.Columns." +
									"LowLimitValue.Text"),
							dialogStringResource.getString(
									"DiscretizeTableModel.Columns." +
									"ValuesSeparator.Text"),
							dialogStringResource.getString(
									"DiscretizeTableModel.Columns." +
									"UpperLimitValue.Text"),
							dialogStringResource.getString(
									"DiscretizeTableModel.Columns." +
									"UpperLimitSymbol.Text") };
		

			discretizedNodeStatesTablePanel = new DiscretizeTablePanel(
							columnNames, probNode);
			discretizedNodeStatesTablePanel.setBorder(new EmptyBorder(0,
							0, 0, 0));
			

		}
		return discretizedNodeStatesTablePanel;
	}
	
	
	/**
	 * This method initializes NodeValuesTable.
	 * 
	 * @return the PrefixedKeyTablePanel for the Node Values
	 */
	/*protected PrefixedKeyTablePanel getNodeDiscreteStatesTablePanel() {

		if (nodeDiscreteStatesTablePanel == null) {
			String[] columnNames =
				{
					dialogStringResource.getString(
							"DiscreteValuesTablePanel.ValuesTable." +
							"Columns.Name.Text"),
					dialogStringResource.getString(
							"DiscreteValuesTablePanel.ValuesTable." +
							"Columns.Value.Text") 
					};

			nodeDiscreteStatesTablePanel =
				new PrefixedKeyTablePanel(columnNames, new Object[][] {},
					dialogStringResource.getString(
							"DiscreteValuesTablePanel.ValuesTable." +
							"Columns.Id.Prefix"),true, probNode);
		}
		nodeDiscreteStatesTablePanel.setBorder(new EmptyBorder(0,0,0,0));
		return nodeDiscreteStatesTablePanel;
	}*/

	/**
	 * @return
	 */
	protected JLabel getJLabelDomainValues() {

		if (jLabelStatesValues == null) {
			jLabelStatesValues = new JLabel();
			jLabelStatesValues.setName("jLabelStatesValues");
			jLabelStatesValues.setText("a Label");
			jLabelStatesValues.setText(dialogStringResource.getString(
					"NodeDomainValuesTablePanel.jLabelStatesValues.Text"));
			jLabelStatesValues.setVisible(false);
			jLabelStatesValues.setEnabled(false);
		}
		return jLabelStatesValues;
	}
	/**
	 * @return
	 */
	protected JComboBox getJComboBoxPrecision() {

		if (jComboBoxPrecision == null) {
			
			String [] precisions = {"1",/*"0.25","0.5",*/"0.1", "0.01", "0.001", "0.0001"};
			jComboBoxPrecision = new JComboBox(precisions);
			jComboBoxPrecision.setName("jComboBoxPrecision");
			//jComboBoxPrecision.setMinimumSize(minimumSize);
			//jComboBoxPrecision.setPreferredSize(getMinimumSize());
			jComboBoxPrecision.addItemListener(this);
		}
		return jComboBoxPrecision;
	}
	
	/**
	 * @return
	 */
	protected JComboBox getJComboBoxStatesValues() {

		if (jComboBoxStatesValues == null) {
			jComboBoxStatesValues = new JComboBox(GUIDefaultStates
					.getListStrings());
			jComboBoxStatesValues.setName("jComboBoxStatesValues");
			//jComboBoxStatesValues.addItemListener(listener);
			jComboBoxStatesValues.setEnabled(false);
			jComboBoxStatesValues.setVisible(false);
			
		}
		return jComboBoxStatesValues;
	}

	/**
	 * @return
	 */
	protected JPanel getJPanelMonotonyUpDown() {

		if (jPanelMonotonyUpDown == null) {
			jPanelMonotonyUpDown = new JPanel();
			jPanelMonotonyUpDown.setBorder(new LineBorder(UIManager
							.getColor("List.dropLineColor"), 1, false));
			jPanelMonotonyUpDown.setSize(329, 24);
			jPanelMonotonyUpDown.setName("jPanelMonotonyUpDown");
			jPanelMonotonyUpDown.setLayout(new GridLayout(0, 2, 0, 0));
			
			
			getJRadioButtonMonotonyUp().setEnabled(false);
			getJRadioButtonMonotonyDown().setEnabled(false);
			jPanelMonotonyUpDown.add(getJRadioButtonMonotonyUp());
			jPanelMonotonyUpDown.add(getJRadioButtonMonotonyDown());
			
			initButtonGroupMonotonyUpDown();
			
		}
		
		return jPanelMonotonyUpDown;
	}

	/**
	 * @return
	 */
	protected JRadioButton getJRadioButtonMonotonyUp() {

		if (jRadioButtonMonotonyUp == null) {
			jRadioButtonMonotonyUp = new JRadioButton();
			jRadioButtonMonotonyUp.setName("jRadioButtonMonotonyUp");
			jRadioButtonMonotonyUp.setText("New JRadioButton");
			jRadioButtonMonotonyUp.setText(dialogStringResource.getString(
									"NodeDomainValuesTablePanel." +
									"jRadioButtonMonotonyUp.Text"));
			jRadioButtonMonotonyUp.addItemListener(listener);
		}
		return jRadioButtonMonotonyUp;
	}

	/**
	 * @return
	 */
	protected JRadioButton getJRadioButtonMonotonyDown() {

		if (jRadioButtonMonotonyDown == null) {
			jRadioButtonMonotonyDown = new JRadioButton();
			jRadioButtonMonotonyDown.setName("jRadioButtonMonotonyDown");
			jRadioButtonMonotonyDown.setText("New JRadioButton");
			jRadioButtonMonotonyDown.setText(dialogStringResource.getString(
					"NodeDomainValuesTablePanel.jRadioButtonMonotonyDown.Text"));
			jRadioButtonMonotonyDown.addItemListener(listener);
		}
		return jRadioButtonMonotonyDown;
	}

	/**
	 * 
	 */
	protected void initButtonGroupMonotonyUpDown() {

		buttonGroup.add(jRadioButtonMonotonyUp);
		buttonGroup.add(jRadioButtonMonotonyDown);
	    

	}
	/**
	 * 
	 * @return
	 */
	protected JLabel getJLabelUnit() {
		if (jLabelUnit == null) {
			jLabelUnit = new JLabel();
			jLabelUnit.setHorizontalAlignment(SwingConstants.LEADING);
			jLabelUnit.setHorizontalTextPosition(SwingConstants.RIGHT);
			jLabelUnit.setName("jLabelUnit");
			jLabelUnit.setText("New JLabel");
			jLabelUnit.setText(dialogStringResource.getString(
					"NodeDomainValuesTablePanel.jLabelUnit.Text"));
		}
		return jLabelUnit;
	}
	/**
	 * 
	 */
	protected JTextField getJTextFieldUnit() {
		if (jFieldUnit == null) {
			jFieldUnit = new JTextField();
			jFieldUnit.setText(probNode.getVariable().getUnit().getString());
			jFieldUnit.setName("jFormattedTextFieldPrecision");
			jFieldUnit.addFocusListener(listener);
		}
		return jFieldUnit;
	}
	
	

	/**
	 * get the label for the precision field
	 * @return the JLabelPrecision
	 */
	protected JLabel getJLabelPrecision() {
		if (jLabelPrecision == null) {
			jLabelPrecision = new JLabel();
			jLabelPrecision.setHorizontalAlignment(SwingConstants.LEADING);
			jLabelPrecision.setHorizontalTextPosition(SwingConstants.RIGHT);
			jLabelPrecision.setName("jLabelPrecision");
			jLabelPrecision.setText("New JLabel");
			jLabelPrecision.setText(dialogStringResource.getString(
					"NodeDomainValuesTablePanel.jLabelPrecision.Text"));
		}
		return jLabelPrecision;
	}
	
	/**
	 * get the TextField Precision field
	 * @return the precision field
	 */
	//protected JFormattedTextField getJFormattedTextFieldPrecision() {
	protected JFormattedTextField getJFormattedTextFieldPrecision() {
		if (jFormattedTextFieldPrecision == null) {
			
			NumberFormatter dnFormat = new NumberFormatter ( NumberFormat. 	
					getNumberInstance(Locale.ENGLISH) );
			
			
			/*DecimalFormat decimalFormat = new DecimalFormat();
			 NumberFormatter textFormatter = new NumberFormatter(decimalFormat);
			 
			 textFormatter.setOverwriteMode(true);
			 textFormatter.setAllowsInvalid(true);*/
			 
			
			DefaultFormatterFactory currFactory = new DefaultFormatterFactory(
					dnFormat, dnFormat, dnFormat );
			jFormattedTextFieldPrecision = new JFormattedTextField(currFactory);
			//NumberFormatter nf =  (NumberFormatter)jFormattedTextFieldPrecision.getFormatter();
			
		//	jFormattedTextFieldPrecision = new JTextField();
			
			//nf.setCommitsOnValidEdit(true);
		
			jFormattedTextFieldPrecision.setName("jFormattedTextFieldPrecision");
			//jFormattedTextFieldPrecision.addActionListener( listener );
			//jFormattedTextFieldPrecision.addFocusListener( listener );
			
			jFormattedTextFieldPrecision.addPropertyChangeListener("value", listener);
			//jFormattedTextFieldPrecision.setDocument( new ValidDoubleDocument() );
		}
		return jFormattedTextFieldPrecision;
	}
	
	/**
	 * @return
	 */
	protected JLabel getJLabelValuesPanel() {

		if (jLabelValuesPanel == null) {
			jLabelValuesPanel = new JLabel();
			jLabelValuesPanel.setName("jLabelValuesPanel");
			jLabelValuesPanel.setText("a Label");
			jLabelValuesPanel.setText(dialogStringResource.getString(
					"NodeDomainValuesTablePanel.jLabelValuesPanel.Text"));
		}
		return jLabelValuesPanel;
	}

	/**
	 * This method initialises jLabelNodeValuesComment
	 * 
	 * @return a new label for the comment
	 */
	protected JTextArea getJTextAreaLabelNodeValuesComment() {

		if (jTextAreaLabelNodeValuesComment == null) {
			jTextAreaLabelNodeValuesComment = new JTextArea();
			jTextAreaLabelNodeValuesComment.setLineWrap(true);
			jTextAreaLabelNodeValuesComment.setOpaque(false);
			jTextAreaLabelNodeValuesComment
							.setName("jTextAreaLabelNetworkValuesComment");
			jTextAreaLabelNodeValuesComment.setFocusable(false);
			jTextAreaLabelNodeValuesComment.setEditable(false);
			jTextAreaLabelNodeValuesComment.setFont(getJLabelDomainValues()
							.getFont());
			jTextAreaLabelNodeValuesComment.setText("an Extended Label");
			jTextAreaLabelNodeValuesComment.setText(
					dialogStringResource.getString( 
							"NodeDomainValuesTablePanel." +
							"jTextAreaLabelNodeValuesComment.Text"));
		}
		return jTextAreaLabelNodeValuesComment;
	}

	/**
	 * This method initializes commentHTMLScrollPaneNodeValuesComment
	 * 
	 * @return a new comment HTML scroll pane.
	 */
	protected CommentHTMLScrollPane getCommentHTMLScrollPaneNodeValuesComment() {

		if (commentHTMLScrollPaneNodeValuesComment == null) {
			commentHTMLScrollPaneNodeValuesComment = new CommentHTMLScrollPane();
			commentHTMLScrollPaneNodeValuesComment.setName(
					"NodeDomainValuesTablePanel." +
					"commentHTMLScrollPaneNodeValuesComment");
		}
		return commentHTMLScrollPaneNodeValuesComment;
	}
	
	/**
	 * Initialize the data structure for finite states variables 
	 * @param states
	 * @return
	 */

	protected Object[][] getDataFromStates(State[] states) {
		int numColumns =6; //key column is assigned in setData
		int rows = states.length;
		Object[][] data = new Object [rows][numColumns];
		for (int i=0; i < rows ; i++) {
			//data [i][0] = GUIDefaultStates.getString(states[i].getName());
			data [i][0] = states[i].getName();
		}
		
		return data;
	}
	/**
	 * Convert an array of strings in an array of arrays of objects with the
	 * same elements. As the Elvira parser is still unable to process the
	 * discretize values this method will separate the elements of each interval
	 * in the proper values for the columns
	 * 
	 * @param states
	 *            array of strings.
	 * @return an array of arrays of objects that has the same elements.
	 */
	// TODO this method must be changed when the Elvira parser will retrieve
	// data in an proper separated format
	protected Object[][] convertStringsToTableFormat(State[] states) {

		Object[][] data;
		int i = 0;
		int numIntervals = 0;
		int numColumns = 6; // name-symbol-value-separator-value-symbol
		String aString = "";
		String lowSymbol = "";
		String upperSymbol = "";
		double lowValue;
		double upperValue;
		int index = 0;
		String name = "";

		numIntervals = states.length;
		data = new Object[numIntervals][numColumns];
		int position=0;
		try {
		for (i = 0; i < numIntervals; i++) {
			//for (i = numIntervals-1; i >= 0; i--) {
			position=0;
			//aString = GUIDefaultStates.getString(states[i].getName());
			aString = states[i].getName();
			// find name & lowSymbol
			index = aString.indexOf("[");
			if (index < 0)
				index = aString.indexOf("(");
			name = aString.substring(0, index);
			data[i][position++] = name; // position 0
 			aString = aString.substring(index, aString.length());
			lowSymbol = aString.substring(0, 1);
			data[i][position++] = lowSymbol; // position 1
			// find lowValue
			aString = aString.substring(1, aString.length());
			index = aString.indexOf(",");
			lowValue = Double.valueOf(aString.substring(0, index));
			data[i][position++] = lowValue; // position 2
			// find separator
			aString = aString.substring(index, aString.length());
			data[i][position++] = aString.substring(0, 1); //position 3
			// find upperValue
			aString = aString.substring(1, aString.length());
			index = aString.indexOf("]");
			if (index < 0)
				index = aString.indexOf(")");
			upperValue = Double.valueOf(aString.substring(0, index));
			data[i][position++] = upperValue; // position 4
			// find upperSymbol
			aString = aString.substring(index, aString.length());
			upperSymbol = aString.substring(0, 1);
			data[i][position++] = upperSymbol; // position 5
		}
		} catch (StringIndexOutOfBoundsException ex) {
			//ExceptionsHandler.handleException(ex,
				//	"Error accessing position in Intervals " + i + position--,
					//false );
			logger.info("Error accessing position in Intervals " + i + position--);
			
		}
		return data;
	}
	/**
	 * Convert an array of strings in an array of arrays of objects with the
	 * same elements.
	 * 
	 * @param values
	 *            array of strings.
	 * @return an array of arrays of objects that has the same elements.
	 */
	protected Object[][] convertStringsToTableDiscreteFormat(State[] values) {

		Object[][] data;
		int i, l;

		l = values.length;
		data = new Object[l][1];
		i=l-1;
		for (State value:values) {
			data[i--][0] =GUIDefaultStates.getString( value.getName());
		}
		return data;
	}


	/**
	 * Convert an array of arrays of objects in an array of strings with the
	 * same elements.
	 * 
	 * @param values
	 *            array of arrays of objects.
	 * @return array of strings that has the same elements.
	 */
	protected String[] convertTableFormatToStrings(Object[][] values) {

		String[] data;
		int i, l;
		String oneValue = "";
		l = values.length;
		data = new String[l];
		// name-symbol-value-separator-value-symbol
		for (i = 0; i < l; i++) {
			// do nothing with values[i][0] = internal id
			oneValue = (String) values[i][1]; // name
			oneValue += (String) values[i][2]; // low limit symbol
			oneValue += values[i][3]; // low limit value
			oneValue += (String) values[i][4]; // comma separator
			oneValue += values[i][5]; // high limit value
			oneValue += (String) values[i][6]; // high limit symbol
			data[i] = oneValue;
		}
		return data;
	}

	/**
	 * This method checks the states table, ensuring that there aren't
	 * duplicated states and empty states.
	 * 
	 * @return true if all the states are defined and appears only once.
	 */
	public boolean checkStates() {

		return true;
	}
	
	
	/**
	 * This method initialises jPanelNodeType
	 * 
	 * @return a panel for the node types
	 */
	private JPanel getJPanelNodeType() {

		if (jPanelNodeType == null) {
			jPanelNodeType = new JPanel();
			jPanelNodeType.setName( "jPanelNodeType" );
			jPanelNodeType.setBorder( new LineBorder( Color.BLUE, 1, false ) );
			jPanelNodeType.setLayout( new GridLayout( 3, 1 ) );
			

		}
		return jPanelNodeType;
	}

	/**
	 * This method initialises jLabelNodeType
	 * 
	 * @return a new name label.
	 */
	private JLabel getJLabelNodeVariableType() {

		if (jLabelNodeVariableType == null) {
			jLabelNodeVariableType = new JLabel();
			jLabelNodeVariableType.setName( "jLabelNodeVariableType" );
			jLabelNodeVariableType
				.setHorizontalAlignment( SwingConstants.LEFT );
			jLabelNodeVariableType
				.setHorizontalTextPosition( SwingConstants.LEFT );
			jLabelNodeVariableType.setText( "a Label" );
			jLabelNodeVariableType.setText( dialogStringResource.getString( 
						"NodeDomainValuesTablePanel." +
						"jLabelNodeVariableType.Text" ) );
			jLabelNodeVariableType.setDisplayedMnemonic(
					dialogStringResource.getString(
					"NodeDomainValuesTablePanel.jLabelNodeVariableType.Mnemonic" )
				.charAt( 0 ) );
			//jLabelNodeVariableType.setLabelFor( getJPanelNodeType() );
		}
		return jLabelNodeVariableType;
	}
	
	private JComboBox getJComboBoxNodeVariableType() {

		if (jComboBoxNodeVariableType == null) {
			
			jComboBoxNodeVariableType = new JComboBox();
			jComboBoxNodeVariableType.setName("jComboBoxNodeVariableType");
			
			if (probNode.getNodeType() == NodeType.UTILITY) {
				jComboBoxNodeVariableType.addItem(dialogStringResource.getString(
						"NodeDomainValuesTablePanel.jComboBoxNodeVariableType." +
								"Items.Continuous"));
			} else {
			jComboBoxNodeVariableType.addItem(dialogStringResource.getString(
					"NodeDomainValuesTablePanel.jComboBoxNodeVariableType." +
					"Items.Discrete"));
			jComboBoxNodeVariableType.addItem(dialogStringResource.getString(
					"NodeDomainValuesTablePanel.jComboBoxNodeVariableType." +
							"Items.Discretized"));
			jComboBoxNodeVariableType.addItem(dialogStringResource.getString(
					"NodeDomainValuesTablePanel.jComboBoxNodeVariableType." +
							"Items.Continuous"));
			}
			//jComboBoxNodeVariableType.setSize(181, 80);
			//jComboBoxNodeVariableType.addItemListener(listener);
			jComboBoxNodeVariableType.addItemListener(this);
		}
		return jComboBoxNodeVariableType;
	}

	
	public void itemStateChanged(ItemEvent arg0) {
		int optionDeselected = 0;
		ItemSelectable itemSelectable = arg0.getItemSelectable();
		Object selected[] = itemSelectable.getSelectedObjects();
		String itemSelected = selected.length == 0 ? "null" :
			(String)selected[0];
		JComboBox comboBox= (JComboBox)arg0.getSource();
		if (arg0.getStateChange() == ItemEvent.DESELECTED){
			optionDeselected = comboBox.getSelectedIndex();
		}
			
		if (comboBox.getName().equals("jComboBoxNodeVariableType")){
			if (!(itemSelected == null) && arg0.getStateChange() == ItemEvent.
				SELECTED && !isUploadingData()){
				VariableTypeEdit variableTypeEdit =null;
				if (itemSelected.equals(dialogStringResource
					.getString("NodeDomainValuesTablePanel." +
					"jComboBoxNodeVariableType.Items.Discrete"))) {
				
					variableTypeEdit = new VariableTypeEdit(probNode,
						VariableType.FINITE_STATES);	
					
				}else if (itemSelected.equals(dialogStringResource
						.getString("NodeDomainValuesTablePanel." +
						"jComboBoxNodeVariableType.Items.Discretized"))) {
					
						variableTypeEdit = new VariableTypeEdit(probNode,
							VariableType.DISCRETIZED);	
					
				}else {
					variableTypeEdit = new VariableTypeEdit(probNode,
						VariableType.NUMERIC);	
					
				}
			
				try {
					probNode.getProbNet().getPNESupport().announceEdit(
							variableTypeEdit );
					probNode.getProbNet().getPNESupport().doEdit(
							variableTypeEdit );
					this.removeAll();
					try {
						initialize();
						setFieldsFromProperties(probNode);
					
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						JOptionPane.showMessageDialog(null, messageStringResource
								.getString( e.getMessage() ),
							messageStringResource.getString( e.getMessage() ),
							JOptionPane.ERROR_MESSAGE );
					}
				} catch (ConstraintViolationException e1) {
					JOptionPane.showMessageDialog( this, 
							messageStringResource.getString( e1.getMessage() ),
						messageStringResource.getString( 
								"ConstraintViolationException" ),
						JOptionPane.ERROR_MESSAGE );
				
					comboBox.setSelectedIndex(optionDeselected);
					comboBox.requestFocus();
							
				} catch (CanNotDoEditException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, messageStringResource
							.getString( e1.getMessage() ),
						messageStringResource.getString( e1.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (DoEditException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (NotEnoughMemoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (NonProjectablePotentialException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (WrongCriterionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				}
			}
		
		}else if (comboBox.getName().equals("jComboBoxStatesValues")){
			//warning mpalacios relative function to options position. 
			//Review "otros" option		
				
			if (!(itemSelected==null) && arg0.getStateChange() == ItemEvent.
					SELECTED){
				int i= 0;
				State [] newStates = new State[DefaultStates.getByIndex(
						comboBox.getSelectedIndex()).length];
				for (String str : DefaultStates.getByIndex(
						comboBox.getSelectedIndex())){
					newStates[i] = new State(str);
					i++;
				}
				
				NodeReplaceStatesEdit nodeReplaceStatesEdit = 
					new NodeReplaceStatesEdit(probNode,newStates);
				try {
					probNode.getProbNet().getPNESupport().announceEdit(
							nodeReplaceStatesEdit);
					probNode.getProbNet().getPNESupport().doEdit(
							nodeReplaceStatesEdit);
					this.removeAll();
					try {
						initialize();
						setFieldsFromProperties(probNode);
					
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						JOptionPane.showMessageDialog(null, messageStringResource
								.getString( e.getMessage() ),
							messageStringResource.getString( e.getMessage() ),
							JOptionPane.ERROR_MESSAGE );
					}
				} catch (ConstraintViolationException e) {
					// TODO Auto-generated catch block
					comboBox.setSelectedIndex(optionDeselected);
					comboBox.requestFocus();
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (CanNotDoEditException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (DoEditException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (NotEnoughMemoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (NonProjectablePotentialException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (WrongCriterionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				}
			}
			
		//PRECISION
		} else if (comboBox.getName().equals("jComboBoxPrecision")){
			if (!(itemSelected == null) && arg0.getStateChange() == ItemEvent.
					SELECTED ) {
				PrecisionEdit precisionEdit = new PrecisionEdit (probNode, Double.parseDouble(itemSelected));
				try {
					probNode.getProbNet().getPNESupport().announceEdit(precisionEdit);
					probNode.getProbNet().getPNESupport().doEdit(precisionEdit);
				} catch (ConstraintViolationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, messageStringResource
							.getString( e1.getMessage() ),
						messageStringResource.getString( e1.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (CanNotDoEditException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, messageStringResource
							.getString( e1.getMessage() ),
						messageStringResource.getString( e1.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (DoEditException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
					JOptionPane.showMessageDialog(null, messageStringResource
							.getString( e2.getMessage() ),
						messageStringResource.getString( e2.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (NotEnoughMemoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (NonProjectablePotentialException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (WrongCriterionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				}
			}
		
		if (probNode.getVariable().getVariableType() == VariableType.DISCRETIZED ||
				probNode.getVariable().getVariableType() == VariableType.NUMERIC ) {
			//double precision = (Double) getPanel().getJFormattedTextFieldPrecision().getValue();
			double precision = Double.parseDouble(itemSelected);
			double [] limits = probNode.getVariable().getPartitionedInterval().getLimits();
			boolean [] belongs =  probNode.getVariable().getPartitionedInterval().getBelongsToLeftSide();
			
			for (int i = 0 ; i < limits.length; i++) {
				if (limits[i] != Double.POSITIVE_INFINITY && limits[i] != Double.NEGATIVE_INFINITY) {
					double newLimit = UtilStrings.roundWithPrecision(limits[i], itemSelected);
					if (limits[i] != newLimit) {
						limits[i] = newLimit;
						int j = i;
						while (j+1 <= limits.length-1 && limits[j] >= limits[j+1]) {
							
							if (belongs[j] == false && belongs[j+1] == true) {
								limits[j+1] = limits[j];
							} else {
								if (j+1 == limits.length-1){
									limits[j+1] = Double.POSITIVE_INFINITY;
									break;
								} else 
									limits[j+1] = limits[j] + precision;
							}
								
								j++;
							}
					
						
					//previous limits
						int k = i;
						while (k-1 >=0 && limits[k] <= limits[k-1]) {
							if (belongs[k] == true && belongs[k-1] == false) {
								limits[k-1] = limits[k];
							}  else {
								if (k-1 == 0){
									limits[k-1] = Double.NEGATIVE_INFINITY;
									break;
								} else 
									limits[k-1] = limits[k] - precision;
							}
							k--;
						}
					} else {
						limits[i] = newLimit;
					}
				}
			}
			
			for (int m = 0 ; m < limits.length; m++) {
				if (limits[m] != Double.POSITIVE_INFINITY && limits[m] != Double.NEGATIVE_INFINITY) {
					limits[m] = UtilStrings.roundWithPrecision(limits[m], itemSelected);
				}
			}
			PartitionedInterval newPartitionedInterval = new PartitionedInterval(limits, belongs);
			
			PartitionedIntervalEdit partitionedIntervalEdit = new PartitionedIntervalEdit(probNode, newPartitionedInterval);
			try {
				probNode.getProbNet().getPNESupport().announceEdit(partitionedIntervalEdit);
				probNode.getProbNet().getPNESupport().doEdit(partitionedIntervalEdit);
			} catch (DoEditException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			} catch (NotEnoughMemoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ConstraintViolationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CanNotDoEditException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NonProjectablePotentialException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WrongCriterionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			PartitionedInterval newPartitionInterval = probNode.getVariable().getPartitionedInterval();
			((DiscretizeTablePanel)getNodeDiscretizedStatesTablePanel()).setDataFromPartitionedInterval(newPartitionInterval);	
		}
		}
	}

	public void setUploadingData(boolean uploadingData) {
		this.uploadingData = uploadingData;
	}

	public boolean isUploadingData() {
		return uploadingData;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String actionComand= arg0.getActionCommand();	
		/*if (arg0.getSource().equals(((DiscretizeTablePanel)getNodeStatesTablePanel()).getStandarDomainButton())) {
			actionPerformedStandarDomain(arg0);
		}*/
		if (actionComand.equals ("StandarDomain")) {
			actionPerformedStandarDomain(arg0);
		}	
		
	}

	 void actionPerformedStandarDomain(ActionEvent arg0) {
		StandarDomainsDialog standarDomainDialog = new StandarDomainsDialog(Utilities.getOwner(this));
		if (standarDomainDialog.requestValues() == StandarDomainsDialog.OK_BUTTON) {
			
			ArrayList<JRadioButton> radioButtons = ((StandarDomainPanel)(standarDomainDialog.getJPanelStandarDomains())).getRadioButtons();
			 int index = 0;
			 String states;
			 
			 for (int j = 0; j < radioButtons.size(); j++) {
				 if (radioButtons.get(j).isSelected()) {
					 index = j;
					 states = radioButtons.get(j).getName();
				 }
			 }
			int i= 0;
			State [] newStates = new State[DefaultStates.getByIndex(index).length];
			for (String str : DefaultStates.getByIndex(index)){
				newStates[i] = new State( GUIDefaultStates.getString(str));
				i++;
			}
			NodeReplaceStatesEdit nodeReplaceStatesEdit = 
					new NodeReplaceStatesEdit(probNode,newStates);
				try {
					probNode.getProbNet().getPNESupport().announceEdit(
							nodeReplaceStatesEdit);
					probNode.getProbNet().getPNESupport().doEdit(
							nodeReplaceStatesEdit);
					this.removeAll();
					try {
						initialize();
						setFieldsFromProperties(probNode);
					
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						JOptionPane.showMessageDialog(null, messageStringResource
								.getString( e.getMessage() ),
							messageStringResource.getString( e.getMessage() ),
							JOptionPane.ERROR_MESSAGE );
					}
				} catch (ConstraintViolationException e) {
					// TODO Auto-generated catch block
					
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (CanNotDoEditException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (DoEditException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (NotEnoughMemoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (NonProjectablePotentialException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (WrongCriterionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				}
		}
	}
	
}
