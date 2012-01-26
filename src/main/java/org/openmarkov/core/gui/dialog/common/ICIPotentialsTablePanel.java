
/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/
package org.openmarkov.core.gui.dialog.common;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.LayoutStyle;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import org.apache.log4j.Logger;

import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.NullListPotentialsException;
import org.openmarkov.core.gui.component.PotentialsTablePanelOperations;
import org.openmarkov.core.gui.component.ValuesTable;
import org.openmarkov.core.gui.component.ValuesTableCellRenderer;
import org.openmarkov.core.gui.component.ValuesTableModel;
import org.openmarkov.core.gui.dialog.node.ICIOptionsPanel;
import org.openmarkov.core.gui.localize.StringResource;



import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;



import org.openmarkov.core.model.network.potential.canonical.ICIPotential;


//TODO review setData methods
@PotentialPanelPlugin(potentialType="Max")
public class ICIPotentialsTablePanel extends ProbabilityTablePanel {
	/*
	private CPTablePanel cpTablePanel;
	
	
	private ButtonGroup buttonGroupNetOrCompound = null;
	private ButtonGroup buttonGroupAllOrIndependent = null;
	private ButtonGroup buttonGroupProbabilisticOrDeterministicOrOptimal = null;
	private ButtonGroup buttonGroupTpcOrCanonical = null;
	private ButtonGroup buttonGroupProbabilityOrValue = null;


	
	private JRadioButton jRadioButtonNeto;
	private JRadioButton jRadioButtonCompound;
	private JRadioButton jRadioButtonTPC;
	private JRadioButton jRadioButtonCanonical;
	private JRadioButton jRadioButtonProbabilities;
	private JRadioButton jRadioButtonValues;
	private JRadioButton jRadioButtonIndependent;
	private JRadioButton jRadioButtonAll;

	
	private JPanel jPanelNetoOrCompound;
	private JPanel jPanelTpcOrCanonical;
	private JPanel jPanelProbabilityOrValue;
	private JPanel jPanelAllOrIndependant;
	private JPanel jPanelRelationTableType;
	
	private StringResource dialogStringResource;

	
	private JLabel jLabelNodeRelationComment;
	private CommentHTMLScrollPane commentHTMLScrollPaneNodeProbsComment = null;
	*/
	private ICIOptionsPanel iciOptionPanel;
	protected Logger logger;
	public ICIPotentialsTablePanel(ProbNode probNode) {
		super(probNode);
		
		add(getICIOptionPanel(),BorderLayout.NORTH);
		
	
		// TODO Auto-generated constructor stub
		
		add(getValuesTableScrollPane(), BorderLayout.CENTER);
		//jContentPane.add(getBottomPanel(), BorderLayout.SOUTH);
		showValuesTable( true );
		setData(probNode);
	}
	
	private ICIOptionsPanel getICIOptionPanel() {

		if (iciOptionPanel == null) {
			iciOptionPanel = new ICIOptionsPanel(probNode);
			boolean newNode = true;
			iciOptionPanel.setNewNode(newNode);
			//tablePotentialPanel.setNodeProperties(probNode);
		}
		
		return iciOptionPanel;
	}
	/*
	private void initialize() throws Exception {

		//setPreferredSize( new Dimension( 700, 375 ) );
		final GroupLayout groupLayout = new GroupLayout( (JComponent) this );
		
		groupLayout.setHorizontalGroup( groupLayout
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
									getCPTablePanel(),GroupLayout.DEFAULT_SIZE, 184,
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
					getCPTablePanel(), GroupLayout.DEFAULT_SIZE,
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
	
	/*
	
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


	private void initButtonGroupTpcOrCanonical() {

		buttonGroupTpcOrCanonical = new ButtonGroup();
		buttonGroupTpcOrCanonical.add( getJRadioButtonTPC() );
		buttonGroupTpcOrCanonical.add( getJRadioButtonCanonical() );
	}


	protected JRadioButton getJRadioButtonTPC() {

		if (jRadioButtonTPC == null) {
			jRadioButtonTPC = new JRadioButton();
			jRadioButtonTPC.setBounds( 1, 1, 189, 24 );
			jRadioButtonTPC.setName( "jRadioButtonTPC" );
			jRadioButtonTPC.setText( "New JRadioButton" );
			jRadioButtonTPC.setText( dialogStringResource
				.getString( "NodeProbsValuesTablePanel.jRadioButtonTPC.Text" ) );
		//	jRadioButtonTPC.addItemListener( this.listener );
			jRadioButtonTPC.setEnabled( true );
		}
		return jRadioButtonTPC;
	}

	
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
			jRadioButtonCanonical.setEnabled( true );
		}
		return jRadioButtonCanonical;
	}


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

	private void initButtonGroupNetOrCompound() {

		buttonGroupNetOrCompound = new ButtonGroup();
		buttonGroupNetOrCompound.add( getJRadioButtonNeto() );
		buttonGroupNetOrCompound.add( getJRadioButtonCompound() );
	}


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


	private void initButtonGroupProbabilityOrValue() {

		buttonGroupProbabilityOrValue = new ButtonGroup();
		buttonGroupProbabilityOrValue.add( getJRadioButtonProbabilities() );
		buttonGroupProbabilityOrValue.add( getJRadioButtonValues() );
	}

	
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

	
	private void initButtonGroupAllOrIndependent() {

		buttonGroupAllOrIndependent = new ButtonGroup();
		buttonGroupAllOrIndependent.add( getJRadioButtonAll() );
		buttonGroupAllOrIndependent.add( getJRadioButtonIndependent() );
	}

	
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

	
	protected CommentHTMLScrollPane getCommentHTMLScrollPaneNodeDefinitionComment() {

		if (commentHTMLScrollPaneNodeProbsComment == null) {
			commentHTMLScrollPaneNodeProbsComment = new CommentHTMLScrollPane();
			commentHTMLScrollPaneNodeProbsComment.setName( 
					"commentHTMLScrollPaneNodeProbsComment" );
		}
		return commentHTMLScrollPaneNodeProbsComment;
	}

	
	
	
	
	
	
	private CPTablePanel getCPTablePanel() {

		if (cpTablePanel == null) {
			cpTablePanel = new CPTablePanel(probNode);
			//boolean newNode = true;
			//cpTablePanel.setNewNode(newNode);
			//tablePotentialPanel.setNodeProperties(probNode);
		}
		
		return cpTablePanel;
	}
	
	*/
	/**
	 * @return the panel with the two buttons
	 */
	
	/**
	 * Sets a new table model with new data.
	 * 
	 * @param newData
	 *            new data for the table.
	 */
	public void setData(Object[][] newData) {

		setData( newData, columns, 0, 0 , NodeType.CHANCE);
	}

	/**
	 * Sets a new table model with new data and new columns
	 * 
	 * @param newData
	 *            new data for the table
	 * @param newColumns
	 *            new columns for the table
	 */
	public void setData(Object[][] newData, String[] newColumns,
						int firstEditableRow, int lastEditableRow, NodeType nodeType) {

		showValuesTable( true );
		data = newData.clone();
		columns = newColumns.clone();
		this.firstEditableRow = firstEditableRow;
		this.lastEditableRow = lastEditableRow;
		valuesTable.resetModel();
		
		//valuesTable.setVariable(probNode.getPotentials().get( 0 ).getVariable( 0 ));
		
		valuesTable.setModel( getTableModel() );
		valuesTable.initializeDataModified( false );
		((ValuesTableModel) valuesTable.getModel())
			.setFirstEditableRow( firstEditableRow );
		valuesTable.setLastEditableRow( lastEditableRow );
		//valuesTable.setShowingAllParameters( true );
		valuesTable.setNodeType(nodeType);

	}
	/**
	 * Sets a new table model with new data and new columns based on three
	 * items:
	 * <li>list of Potentials of the variable</li>
	 * <li>states of the variable</li>
	 * <li>parents of the variable</li>
	 * 
	 * @param listPotentials -
	 *            the list of potentials of the variable
	 * @param variableName -
	 *            name of the variable
	 * @param variableStates -
	 *            states of the variable
	 * @param parents -
	 *            parents of the variable
	 */
	public void setData(ProbNode properties) {
		Object[][] tableData = null;
		String[] newColumns = null;
		if (properties.getPotentials() != null) {
			//listPotentials = PotentialsTablePanelOperations.checkIfPotentialsMustBeChanged(listPotentials, adittionalProperties);
			//setListPotentials(probNode.getPotentials());
			//tableData =
				//convertListPotentialsToTableFormat( listPotentials, adittionalProperties );
			tableData =
				convertListPotentialsToCanonicalTableFormat(properties );
			newColumns =
				ValuesTable
					.getColumnsIdsSpreedSheetStyle( ValuesTable
						.howManyCanonicalColumns( properties ) );
			setFirstEditableRow(calculateFirstEditableRow(probNode.getPotentials()));
			setLastEditableRow(calculateLastEditableRow(
				probNode.getPotentials()));
			setData( tableData, newColumns, firstEditableRow, lastEditableRow , properties.getNodeType() );
			//TODO setCellRenderes
			setCellRenderers();
		} else {
			tableData = new Object[ 0 ][ 0 ];
			setFirstEditableRow( 0 );
			setData( tableData );
			//TODO setCellRenderes
			setCellRenderers();
		}
	}
	/**
	 * calculate the last editable Row of the table, based upon:
	 * <p>
	 * <ul>
	 * <li>number of parents for the node</li>
	 * <li>type of the node (utility or other)</li>
	 * </ul>
	 * 
	 * @param listPotentials -
	 *            potentials for the variable
	 * @param adittionalProperties -
	 *            adittionalProperties for this variable
	 */
	public static int calculateLastEditableRow(ArrayList<Potential> listPotentials) {
		int row = 0;
		if (listPotentials != null) {
			row = listPotentials.get(0).getVariables().get(0).getNumStates()+1; 
			//numStates of the child variable plus one empty cell plus a cell for the variable´s name
		} else {
			row = 0;
		}

		return row+1;
	}
	public static int calculateFirstEditableRow(ArrayList<Potential> listPotentials) {
		int row = 0;
		if (listPotentials != null) {
		
				row = 2; //In a canonical table there are always two rows: one for parent´s names
							//and another for parent´s states
			
		} else {
			row = 0;
		}

		return row;
	}
	/**
	 * calculate the number of rows of the canonical table based on the type of the node,
	 * the number of parents and the number of states of the variable for
	 * canonical models
	 * 
	 * @param adittionalProperties -
	 *            node adittionalProperties
	 * @return the number of rows of this Potentials Table
	 */
	protected int howManyCanonicalRows(ProbNode properties) {

		int numRows =2;//The first two rows are first for parent´s name and second one for parent´s states 
	
			if (properties.getVariable().getStates() != null) {//there is a row for each child state
				numRows = numRows + properties.getVariable().getStates().length;
			}
		
		return numRows;
	}
	/**
	 * Set a blank data table for canonical models
	 * 
	 * @param adittionalProperties -
	 *            to obtain the required number of rows and columns
	 * @return the blank data table
	 */
	private Object[][] setBlankCanonicalTable(ProbNode properties) {

		Object[][] blankTable = null;
		int numRows = howManyCanonicalRows( properties );
		int numColumns = ValuesTable.howManyCanonicalColumns( properties );
		blankTable = new Object[ numRows ][ numColumns ];
	    for (int i = 0; i < properties.getVariable().getStates().length; i++) {}

		return blankTable;
	}
	
	private Potential getThisICIPotential(ArrayList<Potential> listPotentials) {

		Potential aPotential = null;
		try {
			aPotential = ((ICIPotential) listPotentials.get( 0 ));
		} catch (Exception ex) {
			//ExceptionsHandler.handleException(
				//ex, "no Potential.get(0) !!!", false );
			logger.error("no Potential.get(0) !!!");
			
		}

		return aPotential;
	}
	
	/**
	 * Calculates number of positions in a canonical table 
	 * Number of canonical table positions: sum of the product of each parent variable states by the child variable states (conditioned)
	 * 
	 * @param values -
	 *            the table that is being modified
	 * @param listPotentials -
	 *            the list of potentials of the node
	 * @param additionalProperties -
	 *            the adittionalProperties of the node
	 */
	private int getNumberOfPostions( ArrayList<Potential> listPotentials) {
		
		int numPositions = 0;
		int numParentStates;
				try {
			ArrayList<Variable> variables = listPotentials.get( 0 ).getVariables();
			int numChildStates= variables.get(0).getNumStates();
			for (int i = 1; i < variables.size() ; i++) {
				numParentStates = variables.get(i).getNumStates();
				numPositions += numParentStates * numChildStates;
			}
		
			numPositions +=  numChildStates; //for the leak column
		} catch (NullPointerException exception) {
			numPositions = 0;
			//ExceptionsHandler.handleException(
				//exception, "not enougth memory", false );
			logger.error("not enougth memory");
		}
		setPosition( numPositions);
		return numPositions;

	}
	/**
	 * Prepare the table data from the <code>Potential</code>s and States.
	 * <p>
	 * If the Potential is null, then the information is taken from the
	 * <code>NodeProperties</code>
	 * 
	 * @param listPotentials -
	 *            potentials of the table
	 * @param states -
	 *            states of the variable of this node
	 * @param parents -
	 *            <code>NodeWrapper</code> list of the parents
	 * @return the table data to be set
	 */
	protected Object[][] convertListPotentialsToCanonicalTableFormat(ProbNode properties) {
		Object[][] values = null;
		try {
			
			PotentialsTablePanelOperations.checkIfNoPotential( 
					properties.getPotentials());
			values = setCanonicalTableSize(values, properties);
			values = setCanonicalTable(values, properties);
			
			
			setPosition(getNumberOfPostions(properties.getPotentials()));
			
		} catch (NullListPotentialsException ex) {
			values = setBlankCanonicalTable( properties );
		}
		return values;
	}
	/**
	 * set values table size for the potential of the canonical model
	 * 
	 * @param values -
	 *            the table that is being modified
	 *
	 * @param adittionalProperties -
	 *            the adittionalProperties of the node
	 */

	private Object[][] setCanonicalTableSize (Object [][]oldValues,
                                     ProbNode properties) {
		Object [][] values = oldValues;
		int numRows = 0;
		int numColumns = 2; //at least, there is one column for leak potential and the first one with child states and name 
		//first editable row in a canonical table is always the third one
		//first one for the parent´s names and second one for parent´s states
		int row = 2; 
					
		setBaseIndexForCoordinates( row );
		setFirstEditableRow( row );
		
		ICIPotential iciPotential = (ICIPotential) getThisICIPotential(properties.getPotentials());
		ArrayList<Variable> variables = iciPotential.getVariables();
		
		setVariables( variables );
		
		numRows = getVariables().get(0).getNumStates() + row;
		
		setLastEditableRow(numRows-1);
		
		for (int i = 1; i < variables.size(); i++) {
			numColumns += variables.get(i).getNumStates();
		}	
		
       // create the array of arrays
		values = new Object[ numRows ][ numColumns ];
		return values;
	}
	
	/**
	 * 
	 * @param oldValues
	 * @param probNode
	 * @return
	 */
	private Object[][] setCanonicalTable( Object [][]oldValues, ProbNode probNode) {
		
		Object[][] values = oldValues;
		ICIPotential iciPotential = (ICIPotential) getThisICIPotential(probNode.getPotentials());
		ArrayList<Variable> variables = iciPotential.getVariables();
		int lastRow = values.length -1;
		int lastColumn = values[0].length-1;
		
		// First column - conditioned variable
		Variable conditionedVariable = variables.get(0);
		values[0][0] = "";
		values[1][0] = conditionedVariable.getName();
		State [] childStates = conditionedVariable.getStates();
		for(int i=0; i<childStates.length; ++i)
		{
			values[lastRow-i][0] = childStates[i].getName();
		}
		
		int columnOffset = 1;
		for (int i = 1; i < variables.size(); ++i) {
			
			// Header
			Variable variable = variables.get(i);
			State [] states = variable.getStates();
			for(int j = 0; j < states.length; ++j)
			{
				values[0][j+columnOffset] = variable.getName();
				values[1][j+columnOffset] = states[j].getName();
			}
			// Values
			double[] noisyParameters = iciPotential.getNoisyParameters(variable);
			int numStates = conditionedVariable.getNumStates();
			for(int k = 0; k < noisyParameters.length; ++k)
			{
				
				values[lastRow - k % numStates][columnOffset + k / numStates] = noisyParameters[k];
			}
			
			columnOffset+=variable.getNumStates();
		}

		// Leaky parent
		// Header
		
		values[0][lastColumn] = "Leak";
		values[1][lastColumn] = "--";
		double[] leakyParameters = iciPotential.getLeakyParameters();
		for(int i=0; i < leakyParameters.length; ++i)
		{
			values [lastRow-i][lastColumn] = leakyParameters[i];
		}

	return values;
	}
	

	/**
	 * set renders for the cells in the table. Only has to be called when set data.
	 */
	protected void setCellRenderers() {
		int size = valuesTable.getColumnCount();//returns number of columns in the column model
		boolean [] editableColumns = new boolean [size-1];
		
		for (int i=1; i<size;i++){
			editableColumns [i-1] = false;//Uncertainty values false for canonical models
		}
		
		
		valuesTable.setDefaultRenderer(
			Double.class, new ValuesTableCellRenderer(
				getFirstEditableRow(), editableColumns ) );
		valuesTable.setDefaultRenderer(
			String.class, new ValuesTableCellRenderer(
				getFirstEditableRow(), editableColumns ) );

}

    @Override
    public void saveChanges ()
        throws NotEnoughMemoryException
    {
        // TODO Auto-generated method stub
        
    }



}

