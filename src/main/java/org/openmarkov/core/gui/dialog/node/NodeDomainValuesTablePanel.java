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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;


import org.apache.log4j.Logger;
import org.openmarkov.core.action.NodeReplaceStatesEdit;
import org.openmarkov.core.action.VariableTypeEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.component.DiscretizeTablePanel;
import org.openmarkov.core.gui.dialog.common.CommentHTMLScrollPane;
import org.openmarkov.core.gui.dialog.common.KeyTablePanel;
import org.openmarkov.core.gui.dialog.common.PrefixedKeyTablePanel;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.gui.util.GUIDefaultStates;
import org.openmarkov.core.gui.util.Utilities;
import org.openmarkov.core.model.network.DefaultStates;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.VariableType;


/**
 * Panel to set the values of a node with discretized values
 * 
 * @author jlgozalo
 * @author mkpalacio
 * @version 1.0 jlgozalo
 * @version 1.1 mkpalacio
 */
public class NodeDomainValuesTablePanel extends JPanel implements ItemListener{
	
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
	
	private PrefixedKeyTablePanel nodeDiscreteStatesTablePanel;
	
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
	 * precision field
	 */
	private JFormattedTextField jFormattedTextFieldPrecision;
	/**
	 * label for the precision field
	 */
	private JLabel jLabelPrecision;
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
	public NodeDomainValuesTablePanel(
			 ProbNode probNode) {
		this(true);//, notifier);
		this.probNode = probNode;
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

		setPreferredSize(new Dimension(600, 375));
		
		final GroupLayout groupLayout = new GroupLayout((JComponent) this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(getJLabelNodeVariableType(), 
									GroupLayout.DEFAULT_SIZE, 49, 
									Short.MAX_VALUE ))
							.addGroup(groupLayout.createSequentialGroup().
									addContainerGap().addComponent(
											getJLabelPrecision(),
											GroupLayout.DEFAULT_SIZE, 49, 
											Short.MAX_VALUE ))	
					)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(getJFormattedTextFieldPrecision(), 
									GroupLayout.PREFERRED_SIZE, 87, 
									GroupLayout.PREFERRED_SIZE)
							.addGap(44)
							.addComponent(getJPanelMonotonyUpDown(), 
									GroupLayout.PREFERRED_SIZE, 212, 
									GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(getJComboBoxNodeVariableType(), 
									GroupLayout.PREFERRED_SIZE, 181, 
									GroupLayout.PREFERRED_SIZE)
							.addGap(112)
							.addComponent(getJLabelStatesValues())
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(getJComboBoxStatesValues(), 
									GroupLayout.PREFERRED_SIZE, 
									GroupLayout.DEFAULT_SIZE, 
									GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(266, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(getJLabelValuesPanel(), 
							GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(getNodeStatesTablePanel(), 
							GroupLayout.DEFAULT_SIZE, 627, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING,
							false)
						.addComponent(getJLabelNodeVariableType(), 
								GroupLayout.DEFAULT_SIZE, 
								GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(groupLayout.createParallelGroup(
								Alignment.BASELINE)
							.addComponent(getJComboBoxNodeVariableType(), 
									GroupLayout.PREFERRED_SIZE, 17, 
									Short.MAX_VALUE)
							.addComponent(getJComboBoxStatesValues(), 
									GroupLayout.PREFERRED_SIZE, 
									GroupLayout.DEFAULT_SIZE, 
									GroupLayout.PREFERRED_SIZE)
							.addComponent(getJLabelStatesValues())))
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(18)
							.addGroup(groupLayout.createParallelGroup(
									Alignment.BASELINE)
								.addComponent(getJFormattedTextFieldPrecision(),
										GroupLayout.PREFERRED_SIZE, 
										GroupLayout.DEFAULT_SIZE, 
										GroupLayout.PREFERRED_SIZE)
								.addComponent(getJLabelPrecision(), 
										GroupLayout.DEFAULT_SIZE, 
										GroupLayout.DEFAULT_SIZE, 
										Short.MAX_VALUE)))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(18)
							.addComponent(getJPanelMonotonyUpDown(), 
									GroupLayout.PREFERRED_SIZE, 22, 
									GroupLayout.PREFERRED_SIZE)
							.addGap(13)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(getNodeStatesTablePanel(), 
								GroupLayout.PREFERRED_SIZE, 207, 
								GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(9)
							.addComponent(getJLabelValuesPanel(), 
									GroupLayout.PREFERRED_SIZE, 24, 
									GroupLayout.PREFERRED_SIZE)))
					.addGap(87))
		);
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
		jComboBoxStatesValues.removeItemListener(this);
		if (properties != null) {
			//jComboBoxNodeVariableType.removeItemListener(this);
			
			
			if (getNodeStatesTablePanel() instanceof DiscretizeTablePanel){
				// TODO CONSIDER next line if it is required values type
				// jComboBoxStatesValues.setSelectedIndex(adittionalProperties.getStatesSelected());
				
				State [] states = properties.getVariable().getStates();
				Object[][] tableData = null;
				jComboBoxStatesValues.setSelectedIndex(DefaultStates
							.getIndex(states));
				// intervals of the node
				if (properties.getVariable().getVariableType() == 
					VariableType.NUMERIC){ 
		    			jRadioButtonMonotonyUp.setEnabled(true);
		    			jRadioButtonMonotonyDown.setEnabled(true);
		    			jRadioButtonMonotonyUp.setSelected(true);
		    			jRadioButtonMonotonyDown.setSelected(false);
		    			((DiscretizeTablePanel)getNodeStatesTablePanel()).
		    				setEnablePanelButton(false);
		    			
				}else{
						jRadioButtonMonotonyUp.setEnabled(false);
						jRadioButtonMonotonyDown.setEnabled(false);
						jRadioButtonMonotonyUp.setSelected(false);
						jRadioButtonMonotonyDown.setSelected(false);
						((DiscretizeTablePanel)getNodeStatesTablePanel()).
	    				setEnablePanelButton(true);
				}
			    //TODO Review this if structure
				//we assume that in this point elvira data was 
				// translated to openMarkov/xml format. So, states 
				// with limit brackets no exists.
				if (jComboBoxStatesValues.getSelectedIndex() == 
					(jComboBoxStatesValues.getItemCount() - 1) 
					&& Utilities.hasLimitBracketSymbols(states)) { 
						// if the values are others and there are partitioned intervals 
						// described
						tableData = convertStringsToTableFormat(states);
						discretizedNodeStatesTablePanel.setData(tableData);
				} else {
						discretizedNodeStatesTablePanel.
							setPartitionedInterval();
						tableData = discretizedNodeStatesTablePanel.getData();
				}

				/*partitionedInterval = adittionalProperties.getVariable().
				getPartitionedInterval();
				if (partitionedInterval != null) {
					Object[][] auxTableData = partitionedInterval.
					convertToTableFormat();
					for (int i = 0; i < states.length; i++) {
						//if (i < tableData.length) { // set the name of intervals
						//auxTableData[i][0] = tableData[i][0];
						//we assume that in this point elvira data was 
						// translated to probNet/xml format. So, states with
						// limit brackets no exists.
						auxTableData[i][0] = states[i];
						//}
					}
					discretizedNodeStatesTablePanel.setData(auxTableData);
				} else {
					System.out.println("NodeDomainValuesTablePanel."
							+ "setFieldsfromProperties >> "
							+  "No partitionedInterval defined Yet");
				}*/
				//TODO Activar la siguiente línea, la precisión sólo es válida 
				//para variables discretizadas y continuas
				//jFormattedTextFieldPrecision.setValue( properties.getVariable().
					//	getPrecision() );
				
			} else {
				// node comment title
				MessageFormat messageForm =
					new MessageFormat(dialogStringResource.getString(
							"DiscreteValuesTablePanel." +
							"commentHTMLScrollPaneNodeValuesComment.Text"));
				String shortNodeName = properties.getName();
				Object[] labelArgs = new Object[] { shortNodeName };
				// states of the node
				State[] states = properties.getVariable().getStates();
				jComboBoxStatesValues.setSelectedIndex(DefaultStates.getIndex(
						states));
				if (jComboBoxStatesValues.getSelectedIndex() == 
					(jComboBoxStatesValues.getItemCount() - 1)) { 
					// if the values are others
					nodeDiscreteStatesTablePanel.setData(
							convertStringsToTableDiscreteFormat( states ) );
				} else {
					nodeDiscreteStatesTablePanel.setData( 
							convertStringsToTableDiscreteFormat( states ) );
									//DefaultStates.getStrings(states)));
				}
			}
			//jFormattedTextFieldPrecision.removeActionListener( listener );
			//jFormattedTextFieldPrecision.removeFocusListener( listener );
			jFormattedTextFieldPrecision.removePropertyChangeListener("value", 
					listener);
			
			jFormattedTextFieldPrecision.setValue( Double.valueOf( properties.
					getVariable().getPrecision() ) );
			/*try {
				jFormattedTextFieldPrecision.commitEdit();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			//jFormattedTextFieldPrecision.addActionListener( listener );
			//jFormattedTextFieldPrecision.addFocusListener( listener );
			jFormattedTextFieldPrecision.addPropertyChangeListener("value", 
					listener);
		
			switch (properties.getVariable().getVariableType()) {
			case FINITE_STATES: {
				getJComboBoxNodeVariableType().setSelectedItem("Discreta");
				//this.setNodeVariable( VariableType.DISCRETE );
				break;
			}
			case NUMERIC: {
				getJComboBoxNodeVariableType().setSelectedItem("Continua");
				//this.setNodeVariable( VariableType.CONTINUOUS );
				break;
			}
			case DISCRETIZED: {
				getJComboBoxNodeVariableType().setSelectedItem("Discretizada");
				//this.setNodeVariable( VariableType.DISCRETIZED );
				break;
			}
			}
			
			
		}
		jComboBoxStatesValues.addItemListener(this);
		setUploadingData(false);
	}

	protected KeyTablePanel getNodeStatesTablePanel() {

		if (probNode != null){
			if (( probNode.getNodeType()== NodeType.CHANCE || 
					probNode.getNodeType()== NodeType.DECISION) && 
				probNode.getVariable().getVariableType()==
					VariableType.FINITE_STATES){
				return getNodeDiscreteStatesTablePanel();
			}else
				return getNodeDiscretizedStatesTablePanel();
		}else
			return getNodeDiscreteStatesTablePanel();
			
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
	protected PrefixedKeyTablePanel getNodeDiscreteStatesTablePanel() {

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
	}

	/**
	 * @return
	 */
	protected JLabel getJLabelStatesValues() {

		if (jLabelStatesValues == null) {
			jLabelStatesValues = new JLabel();
			jLabelStatesValues.setName("jLabelStatesValues");
			jLabelStatesValues.setText("a Label");
			jLabelStatesValues.setText(dialogStringResource.getString(
					"NodeDomainValuesTablePanel.jLabelStatesValues.Text"));
		}
		return jLabelStatesValues;
	}

	/**
	 * @return
	 */
	protected JComboBox getJComboBoxStatesValues() {

		if (jComboBoxStatesValues == null) {
			jComboBoxStatesValues = new JComboBox(GUIDefaultStates
					.getListStrings());
			jComboBoxStatesValues.setName("jComboBoxStatesValues");
			jComboBoxStatesValues.addItemListener(listener);
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
	protected JFormattedTextField getJFormattedTextFieldPrecision() {
		if (jFormattedTextFieldPrecision == null) {
			
			NumberFormatter dnFormat = new NumberFormatter ( NumberFormat.
					getNumberInstance(Locale.ENGLISH) );
			
			DefaultFormatterFactory currFactory = new DefaultFormatterFactory(
					dnFormat, dnFormat, dnFormat );
			jFormattedTextFieldPrecision = new JFormattedTextField(currFactory);
			//NumberFormatter nf =  (NumberFormatter)jFormattedTextFieldPrecision.getFormatter();
			
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
			jTextAreaLabelNodeValuesComment.setFont(getJLabelStatesValues()
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
			position=0;
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
			data[i--][0] = value.getName();
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
				.setHorizontalAlignment( SwingConstants.RIGHT );
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

		if (jComboBoxStatesValues == null) {
			jComboBoxNodeVariableType = new JComboBox();
			jComboBoxNodeVariableType.setName("jComboBoxNodeVariableType");
			jComboBoxNodeVariableType.addItem(dialogStringResource.getString(
					"NodeDomainValuesTablePanel.jComboBoxNodeVariableType." +
					"Items.Discrete"));
			jComboBoxNodeVariableType.addItem(dialogStringResource.getString(
					"NodeDomainValuesTablePanel.jComboBoxNodeVariableType." +
							"Items.Discretized"));
			jComboBoxNodeVariableType.addItem(dialogStringResource.getString(
					"NodeDomainValuesTablePanel.jComboBoxNodeVariableType." +
							"Items.Continuous"));
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
				} catch (DoEditException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotEnoughMemoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NonProjectablePotentialException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WrongCriterionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
					}
				} catch (ConstraintViolationException e) {
					// TODO Auto-generated catch block
					comboBox.setSelectedIndex(optionDeselected);
					comboBox.requestFocus();
					e.printStackTrace();
				} catch (CanNotDoEditException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DoEditException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotEnoughMemoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NonProjectablePotentialException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WrongCriterionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void setUploadingData(boolean uploadingData) {
		this.uploadingData = uploadingData;
	}

	public boolean isUploadingData() {
		return uploadingData;
	}
	
}
