/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.common;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;

import org.apache.log4j.Logger;
import org.openmarkov.core.action.UncertainValuesEdit;
import org.openmarkov.core.action.UncertainValuesRemoveEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.component.PotentialsTablePanelOperations;
import org.openmarkov.core.gui.component.ValuesTable;
import org.openmarkov.core.gui.component.ValuesTableCellRenderer;
import org.openmarkov.core.gui.component.ValuesTableModel;
import org.openmarkov.core.gui.component.ValuesTableOptimalPolicyCellRenderer;
import org.openmarkov.core.gui.component.ValuesTableWithLinkRestrictionCellRenderer;
import org.openmarkov.core.gui.dialog.node.UncertainValuesDialog;
import org.openmarkov.core.gui.menutoolbar.common.ActionCommands;
import org.openmarkov.core.gui.menutoolbar.menu.UncertaintyContextualMenu;
import org.openmarkov.core.gui.util.Utilities;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.PolicyType;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Util;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.ExactDistrPotential;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.operation.LinkRestrictionPotentialOperations;

/**
 * This class implements a Table potential table with the following features:
 * <li>Its elements, except the first column, are modifiable.</li> <li>New
 * elements can be added, creating a new key row with empty data.</li> <li>The
 * key data (first column) consist of a key string following of the index of the
 * row and it is used for internal purposes only.</li> <li>The key data is
 * hidden.</li> <li>The information of a row (except the first column) can not
 * be taken up or down.</li> <li>The rows can not be removed.</li> <li>The first
 * editable row is the one that has the values of the potentials.</li> <li>The
 * rows between 0 and the first editable row are ocuppied by the values of the
 * states of the parents of the variable.</li> <li>The header of columns is
 * hidden.</li>
 * 
 * carmenyago: Changes: 1. adaptation to the new definition of utility node, 2. removing deterministic features 
 * 3. when the potential doesn't exit an exception is raised 
 * 
 * @author jlgozalo
 * @author myebra
 * @author carmenyago 19/06/2916
 */
@SuppressWarnings("serial")
@PotentialPanelPlugin(potentialType = "Table")
public class TablePotentialPanel extends ProbabilityTablePanel {
	protected Logger logger;
	/**
	 * JTable where show the values.
	 */
	protected ValuesTable valuesTable = null;
	/**
	 * Indicates if the data of the table is modifiable.
	 */
	private boolean modifiable;
	/**
	 * Panel to scroll the table.
	 */
	protected JScrollPane valuesTableScrollPane = null;
	protected Node node;
	
	/**
	 * First potential of node;  its class  should be  org.openmarkov.core.model.network.potential.TablePotential or
	 * org.openmarkov.core.model.network.potential.TableDeltaPotential
	 * @author carmenyago
	 */
	protected Potential potential = null;
	
	/**
	 * When potential is an instance of TablePotential, tablePotential is potential casted as TablePotential
	 * When potential is an instance of TableDeltaPotential, tablePotential=(TablePotential)potential.getTablePotential()
	 * @author carmenyago
	 */
	
	protected TablePotential tablePotential=null;
	
	
	/**
	 * True if class of zeroPotential is org.openmarkov.core.model.network.potential.TableDeltaPotential
	 * @author carmenyago
	 */
	protected boolean isTableDeltaPotential=false; 
	
	/**
	 *  
	 * True if some parent has a link restriction to the node 
	 * @author carmenyago
	 */
	protected boolean hasLinkRestriction;
	
	/**
	 * UNCLEAR-->We calculate the uncertainty. This is calculated several times; i have to check if calculations are repeated unnecessarily 
	 *  
	 */
	protected boolean[] uncertaintyInColumns; 
	

	/**
	 * Pseudo-util class with common operations used in potential tables
	 */
	private PotentialsTablePanelOperations tablePotentialsPanelOperations;

	/**
	 * ContextualMenu to assign/remove uncertainty. 
	 * 
	 * This method creates the evidenceCase object when the user do right click on the table.
	 */

	private UncertaintyContextualMenu uncertaintyContextualMenu;
	
	
//	/**
//	 * Constructor use by CPTablePanel
//	 * 
//	 * @param node
//	 */
//	public TablePotentialPanel(Node node) {
//		super();
//		this.node = node;
//		this.tablePotentialsPanelOperations = new PotentialsTablePanelOperations();
//		modifiable = true;
//		showValuesTable(true);
//		setTableSpecificListeners();
//		setData(node);
//		setLayout(new BorderLayout());
//
//		// If the ScrollPane is not created, initialize it and set the Viewport.
//		// Then add the element to the Layout.
//		add(getValuesTableScrollPane(), BorderLayout.CENTER);
//
//		repaint();
//		// add(getCommentHTMLScrollPaneNodeDefinitionComment(),BorderLayout.SOUTH);
//	}
//	
	

	
/**
 * Constructor used by CPTablePanel
 * This method creates, initialises, and displays a ValuesTable object for the first potential of the node
 * 
 * When there is no potential NullListPotentialException is showed-->UNCLEAR stop??? 
 * 
 * 
 * 
 * If it is not TableDeltaPotential or TablePotential it cast to TablePotential
 * @param node : node whose first potential is a TablePotential or a TableDeltaPotential
 * @author carmenyago : adaptation to TableDeltaPotential
 */
public TablePotentialPanel(Node node){
	super();
	
	this.tablePotentialsPanelOperations = new PotentialsTablePanelOperations();
			
	// If there is no potential
	try{
		tablePotentialsPanelOperations.checkIfNoPotential(node.getPotentials()); 
	} catch (Exception e){
		e.printStackTrace();
		JOptionPane.showMessageDialog(this,
				stringDatabase.getString(e.getMessage()),
				stringDatabase.getString(e.getMessage()),
				JOptionPane.ERROR_MESSAGE);
		return;
	}
	this.node = node;
	// This panel displays the first potential of the node
	potential = node.getPotentials().get(0);
	if (potential instanceof ExactDistrPotential){	
		isTableDeltaPotential=true;
		tablePotential=((ExactDistrPotential)potential).getTablePotential();
	} else tablePotential= (TablePotential)potential; 

	// The list of variables of potential
	variables = potential.getVariables();
			
	// Creating the table; class ValuesTable
	valuesTable = new ValuesTable(node, getTableModel(), modifiable);
	valuesTable.setName("PotentialsTablePanel.valuesTable");
	valuesTable.setVisible(true);
	
	modifiable = true;
	
	// Previous-->Ok
	setTableSpecificListeners();
	
	setData();
		
	setLayout(new BorderLayout());

	// If the ScrollPane is not created, initialise it and set the Viewport.
	// Then add the element to the Layout.
	add(getValuesTableScrollPane(), BorderLayout.CENTER);

	repaint();
}


	

//	/**
//	 * Sets a new table model with new data.
//	 * 
//	 * @param newData
//	 *            new data for the table.
//	 */
//	public void setData(Object[][] newData) {
//		setData(newData, columns, 0, 0, NodeType.CHANCE);
//	}

	/**
	 * Sets a new table model with new data.
	 * UNCLEAR--> The previous method setData(Object[][] newData) is never called
	 * Note--> SetData(node) performs a different function, 
	 * so I change the name of this method to setDataInValuesTable(Object[][])
	 * 
	 * @param newData
	 *            new data for the table.
	 * @author carmenyago           
	 */
//	public void setDataInValuesTable(Object[][] newData) {
//		
//		//setData(newData, columns, 0, 0, NodeType.CHANCE);
//		this.firstEditableRow = 0;
//		this.lastEditableRow = 0;
//		setDataInValuesTable(newData, columns);
//		
//	}

	
		
	//	/**
//	 * Sets a new table model with new data and new columns
//	 * 
//	 * @param newData
//	 *            new data for the table
//	 * @param newColumns
//	 *            new columns for the table
//	 */
//	public void setData(Object[][] newData, String[] newColumns,
//			int firstEditableRow, int lastEditableRow, NodeType nodeType) {
//		showValuesTable(true);
//		data = newData.clone();
//		columns = newColumns.clone();
//		this.firstEditableRow = firstEditableRow;
//		this.lastEditableRow = lastEditableRow;
//		valuesTable.resetModel();
//		// valuesTable.setVariable(node.getPotentials().get( 0
//		// ).getVariable( 0 ));
//		valuesTable.setModel(getTableModel());
//		valuesTable.initializeDataModified(false);
//		((ValuesTableModel) valuesTable.getModel())
//				.setFirstEditableRow(firstEditableRow);
//		valuesTable.setLastEditableRow(lastEditableRow);
//		valuesTable.setShowingAllParameters(true);
//		valuesTable.setNodeType(nodeType);
//	}

	
	/**
	 * Sets a new table model with new data and new columns in valuesTable
	 * UNCLEAR--> do we need showValuesTable?? It is not already done?
	 * UNCLEAR --> lastEditableRow, firstEditableRow??
	 * @param newData
	 *            new data for the table
	 * @param newColumns
	 *            new columns for the table
	 * @author carmenyago
	 * revised--> minor changes           
	 * Previously named setData; I find this name confusing because coincides with setData()
	 */
	public void setDataInValuesTable(Object[][] newData, String[] newColumns) {
		// UNCLEAR--> I think is already done
		//showValuesTable(true);
        
		// Table data
		data = newData.clone();
		// Table columns
		columns = newColumns.clone();
		
		// resets the tableModel
		valuesTable.resetModel();
	 
		// Sets the valuesTable tableModel with columns, data
		valuesTable.setModel(new ValuesTableModel(data, columns, firstEditableRow));
		
		// Initialises a false an array which tells which data are modified
		valuesTable.initializeDataModified(false);
		
		
		valuesTable.setLastEditableRow(lastEditableRow);
		
		 //show/hide rows based on the showingAllParameters attribute using a RowFilter mechanism.
		valuesTable.setShowingAllParameters(true);
		
		valuesTable.setNodeType(node.getNodeType());
	}
	
	
	
	
//	/**
//	 * Sets a new table model with new data and new columns based on three
//	 * items: <li>list of Potentials of the variable</li> <li>states of the
//	 * variable</li> <li>parents of the variable</li>
//	 * 
//	 * @param listPotentials
//	 *            - the list of potentials of the variable
//	 * @param variableName
//	 *            - name of the variable
//	 * @param variableStates
//	 *            - states of the variable
//	 * @param parents
//	 *            - parents of the variable
//	 */
//	public void setData(Node node) {
//		this.node = node;
//		hasLinkRestriction = LinkRestrictionPotentialOperations
//				.hasLinkRestriction(node);
//		valuesTable.setData(node);
//		Object[][] tableData = null;
//		boolean[] uncertaintyInColumns = null;
//		String[] newColumns = null;
//		if (node.getPotentials() != null) {
//			tableData = convertListPotentialsToTableFormat(node);
//			newColumns = ValuesTable
//					.getColumnsIdsSpreadSheetStyle(tableData[0].length);
//			setFirstEditableRow(tablePotentialsPanelOperations
//					.calculateFirstEditableRow(node));
//			setLastEditableRow(tablePotentialsPanelOperations
//					.calculateLastEditableRow(node));
//			setData(tableData, newColumns, firstEditableRow, lastEditableRow,
//					node.getNodeType());
//			uncertaintyInColumns = getUncertaintyInColumns(node);
//			setCellRenderers(uncertaintyInColumns);
//			this.getTableModel().setNotEditablePositions(
//					getNotEditablePositions(node));
//			valuesTable.fitColumnsWidthToContent();
//		} else {
//			tableData = new Object[0][0];
//			setFirstEditableRow(0);
//			setData(tableData);
//			setCellRenderers(uncertaintyInColumns);
//		}
//	}

	/**
	 * It is necessary to implement setData(Node node)
	 * Here I deal with potential = null or potential =0;
	 * 
	 * UNCLEAR--> Called in PotentialEditDialog.showFields(Node)
	 * @author carmenyago
	 */
	public void	setData(Node node) {
		this.node=node;
		
		try{
			tablePotentialsPanelOperations.checkIfNoPotential(node.getPotentials()); 

		} catch(Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,
					stringDatabase.getString(e.getMessage()),
					stringDatabase.getString(e.getMessage()),
					JOptionPane.ERROR_MESSAGE);
			return;				
		}
		setData();
	}
	
	/**
	 * Sets a new table model with new data and new columns based on three
	 * items: <li>list of Potentials of the variable</li> <li>states of the
	 * variable</li> <li>parents of the variable</li>
	 * This method obtains if the node has link restrictions and store it in hasLinkrestriction,
	 * stores the probNet in ValuesTable
	 * fills the tableData (tableData consists of headers + data),
	 * sets the columns name in a Excel mode (A,B,C,...AA,AB...),
	 * sets the tableModel in valuesTable( tableData + column names),
	 * sets uncertaintyInColumns with the columns with uncertainty,
	 * sets the cell renders according to the type of node, and
	 * in the tableMoel, sets the not editable cells due to links restrictions and uncertainty in columns.
	 * Finally, this method adjust the size of the cells in valuesTable
	 * @author carmenyago
	 */
	// Using node sets in variable node
	// What to do with the exception
	public void setData()  {
		
		// true 
		hasLinkRestriction = LinkRestrictionPotentialOperations.hasLinkRestriction(node);
		// Sets the probNet in the table
		
		valuesTable.setData(node);
		
		Object[][] tableData = null;
		uncertaintyInColumns = null;
		String[] newColumns = null;
		
		// tableData contains the table to be displayed in ValuesTable
		tableData = convertListPotentialsToTableFormat();
		
		// Sets the column names in Excel style: A, B, C,....AA,AB...
		// These column names aren't displayed
		newColumns = ValuesTable.getColumnsIdsSpreadSheetStyle(tableData[0].length);
		
		//Calculated in convertListPotentialsToTableFormat-->createEmptyTable()  
		//setFirstEditableRow(tablePotentialsPanelOperations.calculateFirstEditableRow(node));
		//setLastEditableRow(tablePotentialsPanelOperations.calculateLastEditableRow(node));
		
		//Sets the table model in valuesTable
		setDataInValuesTable(tableData, newColumns);
		
		//uncertaintyInColums indicates the data columns which have uncertainty
		uncertaintyInColumns = getUncertaintyInColumns();
		
		// set the Cell Renders according to NodeType (a different renderer for some DECISON nodes) and the uncertainty
		setCellRenderers(uncertaintyInColumns);
		
		// getNotEditablePositions checked if there is any link restriction 
		// which make the correspondent cells no editable and returns an array with the size of the table
		// with the not editable cells set to 1 
		this.getTableModel().setNotEditablePositions(getNotEditablePositions());
		
		// Establish the column width
		valuesTable.fitColumnsWidthToContent();
	}

	
	
//	/**
//	 * 	 
//	 * Sets the columns that have uncertainty a true in a boolean array
//	 * 
//	 * @param node
//	 * @return Boolean array that represents the columns (true = the column has
//	 *         an uncertainty, false = the column has not an uncertainty)
//	 */
//	private boolean[] getUncertaintyInColumns(Node node) {
//		
//		int size = valuesTable.getColumnCount();
//		boolean[] uncertaintyInColumns = new boolean[size - 1];
//
//		if (node.getPotentials().size() > 0) {
//
//			TablePotential tablePotential = (TablePotential) node
//					.getPotentials().get(0);
//			for (int i = 1; i < size; i++) {
//				boolean hasUncertainty = false;
//				try {
//					EvidenceCase configuration = getConfiguration(
//							tablePotential, i);
//					hasUncertainty = tablePotential
//							.hasUncertainty(configuration);
//				} catch (InvalidStateException | IncompatibleEvidenceException e) {
//					e.printStackTrace();
//					JOptionPane.showMessageDialog(this,
//							stringDatabase.getString(e.getMessage()),
//							stringDatabase.getString(e.getMessage()),
//							JOptionPane.ERROR_MESSAGE);
//				}
//				uncertaintyInColumns[i - 1] = hasUncertainty;
//			}
//		}
//		return uncertaintyInColumns;
//	}


	/**
	 * Sets the columns that have uncertainty a true in a boolean array
	 * To do that, this method extracts the uncertainty for every column configuration (parents state set)
	 * 
	 * UNCLEAR-->When we reach this method potential!=null
	 * 
	 * @return Boolean array that represents the columns (true = the column has
	 *         an uncertainty, false = the column has not an uncertainty). This array only contains the data columns
	 * @author carmenyago        
	 */
	private boolean[] getUncertaintyInColumns() {

		int size = valuesTable.getColumnCount();
		
		// Column 0 contains the name of the states
		boolean[] newUncertaintyInColumns = new boolean[size - 1];
		
		for (int i = 1; i < size; i++) {
			boolean hasUncertainty = false;
			try {
				// Returns an evidence case with one finding for every parent variable and its state in the column
				EvidenceCase configuration = getConfiguration(i);
				// If the column configuration has uncertainty hasUncertainty= true
				hasUncertainty = tablePotential.hasUncertainty(configuration);
			} catch (InvalidStateException | IncompatibleEvidenceException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(this,
							stringDatabase.getString(e.getMessage()),
							stringDatabase.getString(e.getMessage()),
							JOptionPane.ERROR_MESSAGE);
			}
				// Indicates whether this column has uncertainty or not 
			newUncertaintyInColumns[i - 1] = hasUncertainty;
		}
		return newUncertaintyInColumns;
	}

	
	
	
//	/**
//	 * calculate the number of rows of the table based on the type of the node,
//	 * the number of parents and the number of states of the variable
//	 * 
//	 * @param additionalProperties
//	 *            - node additionalProperties
//	 * @return the number of rows of this Potentials Table
//	 */
//	protected int howManyRows(Node properties) {
//		int numRows = 0;
//		if (properties.getParents() != null) {
//			numRows = properties.getParents().size();
//		}
//		if (properties.getNodeType() == NodeType.UTILITY) {
//			numRows += 1;
//		} else {
//			if (properties.getVariable().getStates() != null) {
//				numRows = numRows + properties.getVariable().getStates().length;
//			}
//		}
//		return numRows;
//	}
//	
	
	/**
	 * calculate the number of rows of the table based on the parents and  states of the node variable 
	 * Last row with the name of the variable when TablePotential REMOVED
	 * @author carmenyago
	 */
	protected int howManyRows(Node n) {
		return n.getParents().size() + n.getVariable().getStates().length;		
	}

	/**
	 * Set a blank data table
	 * 
	 * @param additionalProperties
	 *            - to obtain the required number of rows and columns
	 * @return the blank data table
	 */
	/*
	 * UNUSED METHOD private Object[][] setBlankTable(Node properties) {
	 * Object[][] blankTable = null; int numRows = howManyRows(properties); int
	 * numColumns = ValuesTable.howManyColumns(properties); blankTable = new
	 * Object[numRows][numColumns]; for (int i = 0; i <
	 * properties.getVariable().getStates().length; i++) { } return blankTable;
	 * }/*
	 * 
	 * /** to retrieve the ListPotentials corresponding to the data in the table
	 * 
	 * @return
	 */
	/*
	 * Unused method public ArrayList<Potential> getListPotentialsFromData() {
	 * ArrayList<Potential> result = null; result =
	 * convertTableFormatToListPotentials(valuesTable); //
	 * setListPotentials(result); return result; }
	 */
	/*
	 * Unused method
	 * 
	 * private TablePotential getThisPotential(List<Potential> listPotentials) {
	 * TablePotential aPotential = null;
	 * 
	 * try { aPotential = (TablePotential)listPotentials.get(0); } catch
	 * (Exception ex) { // ExceptionsHandler.handleException( // ex,
	 * "no Potential.get(0) !!!", false );
	 * logger.error("no Potential.get(0) !!!"); } return aPotential; }
	 */
	
	

//	/**
//	 * Prepare the table data from the <code>Potential</code>s and States.
//	 * <p>
//	 * If the Potential is null, then the information is taken from the
//	 * <code>NodeProperties</code>
//	 * 
//	 * @param listPotentials
//	 *            - potentials of the table
//	 * @param states
//	 *            - states of the variable of this node
//	 * @param parents
//	 *            - <code>NodeWrapper</code> list of the parents
//	 * @return the table data to be set
//	 */
//	protected Object[][] convertListPotentialsToTableFormat(Node node) {
//		Object[][] values = null;
//		try {
//			// mpal
//			tablePotentialsPanelOperations.checkIfNoPotential(node
//					.getPotentials());
//			values = createEmptyTable(node);
//			values = setParentsNameInUpperLeftCornerArea(values, node);
//			values = setParentsStatesInTopArea(values, node);
//			values = setNodeStatesInLeftArea(values, node);
//			values = setPotentialDataInCentreArea(values, node);
//			if (node.getNodeType() != NodeType.UTILITY) {
//				values = setVariableNameInLowerLeftCornerArea(values, node);
//				values = setVariableStatesInBottomArea(values, node);
//			}
//			setPosition(setNumberOfPostions(node.getPotentials()));
//		} catch (NullListPotentialsException ex) {
//			// If the conversion fails, we decided to clear the table values
//			// with null Objects
//			values = new Object[howManyRows(node)][ValuesTable
//					.howManyColumns(node)];
//
//		}
//		return values;
//	}

	
	/**
	 * Creates an array[number_of_rows][number_of_columns] with the objects displayed in the cells of valueTable
	 * Considers the potential is not null
	 * @return the table data to be set
	 * 
	 * @author carmenyago 
	 */
	protected Object[][] convertListPotentialsToTableFormat(){
		Object[][] values = null;
			
	
		// Empty array values[number_of_rows][number_of_colums]
		values = createEmptyTable();
		
		// Sets the number of the parent variables
		values = setParentsNameInUpperLeftCornerArea(values);
		
		// Set the states of the parents on  the top of the table
		// UNCLEAR--> what happens when the parent variable is continuous????
		values = setParentsStatesInTopArea(values);
		// Set the states of the node variable on the left column  
		values = setNodeStatesInLeftArea(values);
		
		// Set the TablePotential/TableDeltaPotential Data on values
		values = setPotentialDataInCentreArea(values);
		
		// UNCLEAR--> REMOVED
		// When the potential is a TablePotential, last row is filled with the name of the node variable in values[lastRow, 0 ] 
		// and when column>0, values[lastRow, column] is the number of the state that has the maximum value in the column 
		/*
		if (!isTableDeltaPotential) {
			values = setVariableNameInLowerLeftCornerArea(values,node);
			values = setVariableStatesInBottomArea(values);
		}
		*/
		
		// The variable position stores the number o data cells
		setNumberOfPostions();
		return values;
	}

	
	
	
	
//	/**
//	 * set values table size for the potential
//	 * 
//	 * @param values
//	 *            - the table that is being modified
//	 * @param listPotentials
//	 *            - the list of potentials of the node
//	 * @param additionalProperties
//	 *            - the additionalProperties of the node
//	 */
//	private Object[][] createEmptyTable(Node node) {
//
//		int numRows = 0;
//		int numColumns = 1; // at least, there is one column for the node names
//		int row = tablePotentialsPanelOperations.calculateFirstEditableRow(node);
//		setBaseIndexForCoordinates(row);
//		setFirstEditableRow(row);
//		TablePotential tablePotential = (TablePotential) node.getPotentials()
//				.get(0);
//		List<Variable> variablesBeforeReorder = tablePotential.getVariables();
//		setVariables(variablesBeforeReorder);
//		if (node.getNodeType() == NodeType.UTILITY) {
//			setBaseIndexForCoordinates(row - 1);
//			numRows = getVariables().size();
//			setLastEditableRow(numRows - 1);
//			// numRows++;
//			if (tablePotential.getTableSize() == 0)
//				numColumns++;
//			else
//				numColumns += tablePotential.getTableSize();
//		} else {
//			// number of states of the conditioned variable
//			int numDimensions = tablePotential.getDimensions()[0];
//			// parents + variableStates
//			numRows = getVariables().size() - 1 + numDimensions;
//			setLastEditableRow(numRows - 1);
//			numRows = numRows + 1; // + 1 for variableValues (when used in show
//									// as Values
//			if (numDimensions == 0) {
//				// do nothing??
//			} else { // all table div by variable states
//				numColumns = numColumns
//						+ (tablePotential.getTableSize() / numDimensions);
//			}
//		}
//		// create the array of arrays
//		return new Object[numRows][numColumns];
//	}

	/**
	 * Creates and empty array of empty objects with the [number_of_rows][number_of_columns] of the valuesTable
	 * Considers the potential is not null
	 * UNCLEAR --> setBaseIndexForCoordinates
	 * @author carmenyago
	 *            
	 * Continuous variables have only one state
	 *  tableSize is always >0       
	 */
	private Object[][] createEmptyTable() { 
     
		// If there is no potential
		try{
			tablePotentialsPanelOperations.checkIfNoPotential(node.getPotentials()); 
		} catch (Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,
					stringDatabase.getString(e.getMessage()),
					stringDatabase.getString(e.getMessage()),
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		int numRows = 0;
		int numColumns = 1; // Variables column
		
		// First editable row coincides with the number of parents
		firstEditableRow = tablePotentialsPanelOperations.calculateFirstEditableRow(node);
		
		// The baseIndexForCoordinates is the first editable row-->What for-->UNCLEAR
		// The property baseIndexForCoordinates is not Visible. baseIndexForCoordinates= row
		setBaseIndexForCoordinates(firstEditableRow);	
			
		if (isTableDeltaPotential) setBaseIndexForCoordinates(firstEditableRow - 1); //UNCLEAR

		// Number of data elements of tablePotential
		int tableSize =tablePotential.getTableSize();//-->UNCLEAR What happens when there is no parent (f.e. when Tree/ADD )
		
		// Number of states of the variable of the node; if isTableDeltaPotential numDimensions=1
		int numDimensions=1;
		if (!isTableDeltaPotential) 
			numDimensions = tablePotential.getDimensions()[0];
		// Parent variables + states of node variable
		numRows = firstEditableRow + numDimensions;
		lastEditableRow= numRows-1;    
		
		/*if (!isTableDeltaPotential) numRows++;*/ //--> UNCLEAR Last row with the name of the variable and the state with '1' is REMOVED
		numColumns = numColumns + tableSize /numDimensions;	
		
		// create the array of arrays
		return new Object[numRows][numColumns];
	}

//	/**
//	 * This methods fills the Upper Left corner of the table with the name of
//	 * the parents of the node
//	 * 
//	 * @param values
//	 *            - the table that is being modified
//	 * @param additionalProperties
//	 *            - the additionalProperties of the node
//	 */
//	private Object[][] setParentsNameInUpperLeftCornerArea(
//			Object[][] oldValues, Node node) {
//		Object[][] values = oldValues;
//		List<Variable> parents = new ArrayList<Variable>();
//		for (Variable variable : getVariables()) {
//			if (!variable.getName().equals(node.getName())) {
//				parents.add(variable);
//			}
//		}
//		if ((parents != null) && (parents.size() > 0)) {
//			for (int i = 0; i < parents.size(); i++) {
//				values[i][0] = parents.get(i);
//			}
//		}
//		return values;
//	}
	
	
	/**
	 * This methods fills the Upper Left corner of the table with the name of
	 * the parents of the node
	 * 
	 * @param oldValues
	 *            - the table that is being modified
	 * @author carmenyago
	 *        
	 * 
	 */
	private Object[][] setParentsNameInUpperLeftCornerArea(Object[][] oldValues) {
		Object[][] values = oldValues;
		// Adding the parent
		// The first variable is always the node variable
		for (int i = 1; i< variables.size(); i++){
			values[i-1][0] = variables.get(i);
		}
		return values;
	}
	

//	/**
//	 * @param values
//	 *            - the table that is being modified
//	 * @param listPotentials
//	 *            - the list of potentials of the node
//	 * @param additionalProperties
//	 *            - the additionalProperties of the node
//	 */
//	private Object[][] setParentsStatesInTopArea(Object[][] oldValues, Node node) {
//		Object[][] values = oldValues;
//		int numColumns = (values.length == 0 ? 0 : values[0].length);
//		TablePotential tablePotential = (TablePotential) node.getPotentials()
//				.get(0);
//		List<Variable> variables = tablePotential.getVariables();
//
//		// Initialize the variable with the number of data columns
//		int numRepetitions = numColumns - 1;
//
//		// When clicking "show expected utility", the window was not appearing.
//		// In this case, the decision node has a utility variable, but the node
//		// is not utility, and thus
//		// the value of numParentVariables should not be decreased in one unit.
//		int numParentVariables = (tablePotential.getUtilityVariable() != null)
//				&& (node.getNodeType() == NodeType.UTILITY) ? tablePotential
//				.getNumVariables() : tablePotential.getNumVariables() - 1;
//
//		for (int row = 0; row < numParentVariables; row++) {
//			State[] states;
//			if ((tablePotential.getUtilityVariable() != null)
//					&& (node.getNodeType() == NodeType.UTILITY)) {
//				states = variables.get(row).getStates();
//			} else {
//				// Row + 1 jumps above the node variable
//				states = variables.get(row + 1).getStates();
//
//			}
//			// Number of repetitions is equals to the ratio of the
//			// last variable number of states and the number of states
//			// of the actual variable.
//			numRepetitions = numRepetitions / states.length;
//
//			for (int column = 1; column < numColumns; column++) {
//				// Find the index of the state. We start in zero position of the
//				// array of states, and thus we need to substract a unit to
//				// column
//				// The ratio divides the table in sections and the module
//				// get the position relative to the section.
//				int stateIndex = ((column - 1) / numRepetitions)
//						% states.length;
//				State state = states[stateIndex];
//				values[row][column] = state.getName();
//			}
//
//		}
//		return values;
//	}


	/**
	 * Sets the states of the parents in the top of the table
	 * Potential is not null
	 * @param oldValues
	 *            - the table that is being modified. oldValues !=null and oldValues.lenght is always > 0
	 *             
	 * @author carmenyago
	 */
	private Object[][] setParentsStatesInTopArea(Object[][] oldValues) {
		Object[][] values = oldValues;
		
		int numColumns = values[0].length;
		
		// Initialise the variable with the number of data columns
		int numRepetitions = numColumns - 1; 
		int numParentVariables = variables.size() -1;
		State[] states;
		
		for (int row = 0; row < numParentVariables; row++) {
		    
			states = variables.get(row+1).getStates();
			numRepetitions = numRepetitions / states.length;

			for (int column = 1; column < numColumns; column++) {
				// Find the index of the state. We start in zero position of the
				// array of states, and thus we need to substract a unit to
				// column
				// The ratio divides the table in sections and the module
				// get the position relative to the section.
				int stateIndex = ((column - 1) / numRepetitions)
						% states.length;
				State state = states[stateIndex];
				values[row][column] = state.getName();
			}

		}
		return values;
	}

	
	
//	/**
//	 * this method sets the first row with the values of the states of the node
//	 * (if it is a node chance) or the name of the variable of the node (if it
//	 * is a utility node)
//	 * 
//	 * @param values
//	 *            - the table that is being modified
//	 * @param listPotentials
//	 *            - the list of potentials of the node
//	 * @param additionalProperties
//	 *            - the additionalProperties of the node
//	 */
//	private Object[][] setNodeStatesInLeftArea(Object[][] oldValues,
//			Node properties) {
//		Object[][] values = oldValues;
//		TablePotential tablePotential = (TablePotential) node.getPotentials()
//				.get(0);
//		int row = getFirstEditableRow();
//
//		if (properties.getNodeType() == NodeType.UTILITY) {
//			values[row][0] = properties.getName();
//		} else
//		/* if (properties.getNodeType() == NodeType.CHANCE) */{
//			// set first column values with the state names
//			if (0 < tablePotential.getDimensions()[0]) {
//				// int numOfTheState =
//				// tablePotential.getVariable( 0 ).getNumStates() - 1;
//				int length = values.length - 2;
//				for (State state : tablePotential.getVariable(0).getStates()) {
//					values[length--][0] = state.getName();
//					// row++;
//					// numOfTheState--;
//				}
//			}
//		}
//		return values;
//	}


	/**
	 * this method sets the first row with the values of the states of the node
	 * (if it is a node chance) or the name of the variable of the node (if it
	 * is a utility node)
	 * 
	 * @param oldValues
	 *            - the table that is being modified
	 * @author carmenyago
	 */
	private Object[][] setNodeStatesInLeftArea(Object[][] oldValues) {
		Object[][] values = oldValues;
		if (isTableDeltaPotential) values[firstEditableRow][0] = node.getName();
		else{
			
			// Why not trying lastEditableRow?
			//int length = values.length - 2;
			int length = lastEditableRow; 
			for (State state : variables.get(0).getStates()) {
				values[length--][0] = state.getName();
			}
		}
		return values;
	}

	
	
		
//	/**
//	 * @param values
//	 *            - the table that is being modified
//	 * @param listPotentials
//	 *            - the list of potentials of the node
//	 * @param additionalProperties
//	 *            - the additionalProperties of the node
//	 */
//	private Object[][] setPotentialDataInCentreArea(Object[][] oldValues,
//			Node node) {
//		Object[][] values = oldValues;
//
//		int numColumns = (values.length == 0 ? 0 : values[0].length);
//		TablePotential tablePotential = (TablePotential) node.getPotentials()
//				.get(0);
//
//		// rounding initial values
//		double[] initialValues = tablePotential.getValues();
//		double[] roundedValues = new double[initialValues.length];
//		int maxDecimals = 10;
//		double epsilon;
//		epsilon = Math.pow(10, -(maxDecimals + 2));
//		for (int i = 0; i < initialValues.length; i++) {
//			roundedValues[i] = Util.roundAndReduce(initialValues[i], epsilon,
//					maxDecimals);
//		}
//		tablePotential.setValues(roundedValues);
//
//		for (int j = 1; j <= numColumns - 1; j++) {
//
//			// put the values on the table
//			for (int i = getLastEditableRow(); i >= getFirstEditableRow(); i--) {
//				// gets the potential index of the row i and column j.
//				double value = tablePotential.getValues()[tablePotentialsPanelOperations
//						.getPotentialIndex(i, j, node)];
//				values[i][j] = value;
//			}
//		}
//		return values;
//	}
	
	/**
	 * Sets the data table from potential in oldValues
	 * 
	 * @param oldValues
	 * 
	 * @return an array filled with the date table from tablePotential or tableDeltaPotential filled with the data values 
	 * from tablePotential or tableDeltaPotential in the correct positions to be displayed by ValuesTable
	 * 
	 */
	private Object[][] setPotentialDataInCentreArea(Object[][] oldValues) {
		Object[][] values = oldValues;

		int numColumns = values[0].length;
		
		
		// rounding initial values
		double[] initialValues = tablePotential.getValues();
		double[] roundedValues = new double[initialValues.length];
		int maxDecimals = 10;
		double epsilon;
		epsilon = Math.pow(10, -(maxDecimals + 2));
		for (int i = 0; i < initialValues.length; i++) {
			roundedValues[i] = Util.roundAndReduce(initialValues[i], epsilon,
					maxDecimals);
		}
		// UNCLEAR-->What for??
		//tablePotential.setValues(roundedValues);

		for (int j = 1; j <= numColumns - 1; j++) {

			// put the values on the table
			for (int i = getLastEditableRow(); i >= getFirstEditableRow(); i--) {
				int potentialIndex=tablePotentialsPanelOperations.getPotentialIndex(i, j, node);
				double value = roundedValues[potentialIndex];
				values[i][j] = value;
			}
		}
		return values;
	}

//	/**
//	 * In the lower left corner area, the last row is reserved in the model for
//	 * displaying the name of the variable-->Removed from ConvertListPotentialToTableFormat; now it is NOT USED
//	 * 
//	 * @param olDvalues
//	 *            - the table that is being modified
//	 * @param properties
//	 *            - the node which 'owns' the table
//	 *            
//	 * revised-->not changed
//	 *            
//	 */
//	private Object[][] setVariableNameInLowerLeftCornerArea(
//			Object[][] oldValues, Node properties) {
//		Object[][] values = oldValues;
//		values[getLastEditableRow() + 1][0] = properties.getName();
//		return values;
//	}

//	/**
//	 * In a discretize table model that shows only values (not probabilities),
//	 * this area will store the name of the state that is required to display
//	 * -->Removed from ConvertListPotentialToTableFormat; now it is NOT USED
//	 * 
//	 * @param values
//	 *            - the table that is being modified
//	 * @param additionalProperties
//	 *            - the additionalProperties of the node
//	 */
//	private Object[][] setVariableStatesInBottomArea(Object[][] oldValues,
//			Node node) {
//		Object[][] values = oldValues;
//		int numColumns = (values.length == 0 ? 0 : values[0].length);
//		TablePotential tablePotential = (TablePotential) node.getPotentials()
//				.get(0);
//		State[] states = tablePotential.getVariable(0).getStates();
//		double max;
//		for (int j = numColumns - 1; j >= 1; j--) {
//			max = (Double) values[getFirstEditableRow()][j];
//			values[getLastEditableRow() + 1][j] = states[0].getName();
//			for (int i = getFirstEditableRow() + 1; i <= getLastEditableRow(); i++) {
//				if (((Double) values[i][j]) > max) {
//					max = (Double) values[i][j];
//					values[getLastEditableRow() + 1][j] = states[i
//							- getFirstEditableRow()].getName();
//				}
//			}
//		}
//		return values;
//	}

	
// This method is commented because it is not used	
//	/**
//	 * UNCLEAR-->In a discrete table model that shows only values (not probabilities),
//	 * this area will store the name of the state that is required to display
//	 * 
//	 * The for every column displays the state whose value is the max in the column
//	 * //UNCLEAR--> values.lenght can be 0?
//	 * 
//	 * @param oldValues
//	 *            - the table that is being modified
//	 * @author carmenyago
//	 * minor changes
//	 */
//	private Object[][] setVariableStatesInBottomArea(Object[][] oldValues) {
//		Object[][] values = oldValues;
//		int numColumns = values[0].length; //UNCLEAR--> values.lenght can be 0?
//		State[] states = node.getVariable().getStates();
//		double max;
//		
//		for (int j = numColumns - 1; j >= 1; j--) {
//			
//			max = (Double) values[firstEditableRow][j];
//			values[lastEditableRow + 1][j] = states[0].getName();
//			for (int i = firstEditableRow + 1; i <= lastEditableRow; i++) {
//				if (((Double) values[i][j]) > max) {
//					max = (Double) values[i][j];
//					values[lastEditableRow+ 1][j] = states[i- firstEditableRow].getName();
//				}
//			}
//		}
//		return values;
//	}

// This method is commented because it is not used	
//	/**
//	 * @param values
//	 *            - the table that is being modified
//	 * @param listPotentials
//	 *            - the list of potentials of the node
//	 * @param additionalProperties
//	 *            - the additionalProperties of the node
//	 */
//	private int setNumberOfPostions(List<Potential> listPotentials) {
//		int numPositions = 1;
//		try {
//			for (Variable variable : listPotentials.get(0).getVariables()) {
//				numPositions = numPositions * variable.getNumStates();
//			}
//		} catch (NullPointerException exception) {
//			numPositions = 0;
//			logger.error("not enough memory");
//		}
//		setPosition(numPositions);
//		return numPositions;
//	}

	/**
	 * This method calculates the number of data cells and stores it in the attribute positions.
	 * The number of data cell is the product of the number of states of all variables
	 * 
	 * @author carmenyago
	 * minor changes
	 */
	private int setNumberOfPostions() {
		int numPositions = 1;
		try {
			for (Variable variable : potential.getVariables()) {
				numPositions = numPositions * variable.getNumStates();
			}
		} catch (NullPointerException exception) {
			numPositions = 0;
			logger.error("not enough memory");
		}
		setPosition(numPositions);
		return numPositions;
	}
	
	
	
	/**
	 * Set the variables of the global attribute
	 * 
	 * @param variables
	 */
//	private void setVariables(List<Variable> variables) {
//		// TODO update this statement, when constructor of this class with
//		// potential as parameter is implemented
//		if (node != null && node.getNodeType() == NodeType.UTILITY) {
//			this.variables = new ArrayList<Variable>();
//			this.variables.add(node.getVariable());
//			for (Variable variable : variables)
//				this.variables.add(variable);
//		} else
//			this.variables = variables;
//	}
	
	/**
	 * Calculates the position on valuesTable for a state combination
	 * 
	 * @param stateIndices
	 *            - indexes of the states
	 * @return an array containing the row at the first position and the column
	 *         at the second position.
	 * revised--> only changed the code between CMI, CMF        
	 */
	private int[] getRowAndColumnForStateCombination(int[] stateIndices,
			TablePotential potential) {
		int numStates = node.getVariable().getNumStates();
		int position = potential.getPosition(stateIndices);
		//CMI
		// tempMultiplier = number of columns
		// int tempMultiplier = ValuesTable.howManyColumns(node) - 1;
		int tempMultiplier = tablePotential.getTableSize()/numStates;
		// CMF
		int tempColumnPosition = 0;

		// We start at index 1 because the state of the node is irrelevant for
		// obtain the column (only is relevant for the row)
		// We multiply the number of columns above each state and the index of
		// this variable (in wich state is)
		for (int i = 1; i < potential.getVariables().size(); i++) {
			Variable var = potential.getVariables().get(i);
			tempMultiplier = tempMultiplier / var.getNumStates();
			tempColumnPosition += stateIndices[i] * tempMultiplier;
		}

		// The column will be the column in the data structure plus one row at
		// the beginning
		int column = tempColumnPosition + 1;
		// The row will be the last row in the table minus the relative position
		// in the node state
		int row = getLastEditableRow() - (position % numStates);
		return new int[] { row, column };
	}

//	/****
//	 * Calculates the positions of the table which are not editable due to a
//	 * link restriction. If the position is not editable it contains the value
//	 * 1, otherwise it contains a null value.
//	 * 
//	 * @param node
//	 * 
//	 * @return a two dimensional array with the size of the table containing the
//	 *         information about the editable positions.
//	 */
//	private Object[][] getNotEditablePositions(Node node) {
//		Object[][] notEditablePositions = createEmptyTable(node);
//		if (node.getNodeType() == NodeType.CHANCE && hasLinkRestriction) {
//			List<int[]> statesWithRestriction = LinkRestrictionPotentialOperations
//					.getStateCombinationsWithLinkRestriction(node);
//			TablePotential potential = (TablePotential) node.getPotentials()
//					.get(0);
//			for (int[] state : statesWithRestriction) {
//				int[] position = getRowAndColumnForStateCombination(state,
//						potential);
//				int row = position[0];
//				int column = position[1];
//				notEditablePositions[row][column] = 1;
//			}
//		}
//		boolean[] uncertaintyInColumns = getUncertaintyInColumns();
//			for (int row = firstEditableRow; row < notEditablePositions.length; ++row) {
//				for (int column = 1; column < notEditablePositions[0].length; ++column) {
//					if (uncertaintyInColumns[column - 1]) {
//						notEditablePositions[row][column] = 1;
//					}
//				}
//			}
//			return notEditablePositions;
//	}

		
		
	/****
	 * Calculates the positions of the table which are not editable due to a
	 * link restriction or uncertainty in the columns. 
	 * If the position is not editable the position in the return array is set to 1, otherwise it contains a null value.
	 * 
	 * @return a two dimensional array with the size of the table containing the
	 *         information about the editable positions.
	 *         
	 * UNCLEAR--> Can a utility Node have nodes with restriction and what to do?        
	 * @author carmenyago        
	 * 
	 */
	private Object[][] getNotEditablePositions() {
		Object[][] notEditablePositions = createEmptyTable();
		if (!isTableDeltaPotential && hasLinkRestriction){
			
			List<int[]> statesWithRestriction = LinkRestrictionPotentialOperations
						.getStateCombinationsWithLinkRestriction(node);
				
			for (int[] state : statesWithRestriction) {
				int[] position = getRowAndColumnForStateCombination(state, tablePotential);
				int row = position[0];
				int column = position[1];
				notEditablePositions[row][column] = 1;
			}
		}
		// I suppose it is calculated previously
		//	uncertaintyInColumns = getUncertaintyInColumns();
		for (int row = firstEditableRow; row < notEditablePositions.length; ++row) {
			for (int column = 1; column < notEditablePositions[0].length; ++column) {
				if (uncertaintyInColumns[column - 1]) {
						notEditablePositions[row][column] = 1;
				}
			}
		}
		return notEditablePositions;
	}
	
			

	/**
	 * Convert the table with the data in a List of Potentials to be saved
	 * 
	 * @param valuesTable
	 *            - the table with the data
	 * @return a list of Potentials
	 */
	/*
	 * Unused method private ArrayList<Potential>
	 * convertTableFormatToListPotentials(ValuesTable valuesTable) {
	 * ArrayList<Potential> listPotentials = new ArrayList<Potential>(); if
	 * (getPosition() >= 0) { // it is not a Decision node double[] table = new
	 * double[getPosition()]; TablePotential tablePotential = null; int position
	 * = 0; for (int j = valuesTable.getColumnCount() - 1; j > 0; j--) { for
	 * (int i = valuesTable.getLastEditableRow() - 1; i >=
	 * getFirstEditableRow(); i--, position++) { table[position] = (Double)
	 * valuesTable.getModel().getValueAt(i, j); } } tablePotential = new
	 * TablePotential(getVariables(), PotentialRole.CONDITIONAL_PROBABILITY,
	 * table); listPotentials.add(tablePotential); } return listPotentials; }
	 */

//	/**
//	 * This method generates the evidenceCase based on the column selected on
//	 * the <code>valuesTable</code> object.
//	 * 
//	 * @param tablePotential
//	 *            The TablePotential object edited
//	 * @param col
//	 *            The column selected. Never is 0 , because the column 0 is the
//	 *            states column
//	 * @return An evidence case object
//	 * @throws InvalidStateException
//	 * @throws IncompatibleEvidenceException
//	 */
//	private EvidenceCase getConfiguration(TablePotential tablePotential, int col)
//			throws InvalidStateException, IncompatibleEvidenceException {
//		Variable variable = null;
//		EvidenceCase evidence = new EvidenceCase();
//		// configuration of all variables
//		if (tablePotential.getPotentialRole() == PotentialRole.UTILITY
//				&& tablePotential.getUtilityVariable() != null) {
//			variable = tablePotential.getUtilityVariable();
//			variables = tablePotential.getVariables();
//		} else {
//			variable = tablePotential.getVariable(0);
//			variables = tablePotential.getVariables();
//			variables.remove(0);
//		}
//		int[] parentsConfiguration = new int[variables.size()];
//		// Gets the start position of a reordered potential
//		int startPosition = tablePotentialsPanelOperations
//				.getPotentialStartIndexOfColumn(col, node);
//
//		// gets the configuration selected
//		int[] configuration = tablePotential.getConfiguration(startPosition);
//
//		// Gets the parents configuration
//		int end = 0;
//		if (variable == tablePotential.getUtilityVariable()) {
//			end = -1;
//		}
//		// The nodes with utility values have one less variable
//		int parentsDifferenceChanceNodes = 1;
//		if (tablePotential.getPotentialRole() == PotentialRole.UTILITY
//				&& tablePotential.getUtilityVariable() != null) {
//			parentsDifferenceChanceNodes = 0;
//		}
//		for (int i = configuration.length - 1; i > end; i--) {
//			parentsConfiguration[i - parentsDifferenceChanceNodes] = configuration[i];
//		}
//		// Gets the evidence
//		int j = 0;
//
//		Finding finding;
//		for (Variable var : variables) {
//			finding = new Finding(var, parentsConfiguration[j]);
//			evidence.addFinding(finding);
//			j++;
//		}
//		return evidence;
//	}

	
	/**
	 * This method generates the evidenceCase based on the column selected on
	 * the <code>valuesTable</code> object.
	 * The evidence case has a finding for every parent of the node and its state in column
	 * 
	 * UNCLEAR When is the parents list reordered??? 
	 * 
	 * @param col
	 *            The column selected. Never is 0 , because the column 0 is the
	 *            states column
	 * 
	 * @return An evidence case object
	 * 
	 * @throws InvalidStateException
	 * @throws IncompatibleEvidenceException
	 * 
	 * @author carmenyago
	 * 
	 */
	private EvidenceCase getConfiguration(int col)
			throws InvalidStateException, IncompatibleEvidenceException {

		
		List<Variable>  parents = variables.subList(1, potential.getNumVariables());
		
		EvidenceCase evidence = new EvidenceCase();
		
		
		int[] parentsConfiguration = new int[parents.size()];
		
		/*
		 * If there is no potential, an exception is shown (caught) and startPosition=0 
		 */
		int startPosition = tablePotentialsPanelOperations
				.getPotentialStartIndexOfColumn(col, node);
		
		// gets the configuration of startPosition--> the data position in tablePotential corresponding to 
		// the beginning of the column
		// I suppose configuration=[Node Variable, parent_1,----,parent_n]
		 int[] configuration = tablePotential.getConfiguration(startPosition);
		
		// Extracts the configuration of the parents from configuration
		// It is the same for every cell of the selected column 
		
		for (int i = configuration.length - 1; i > 0; i--) {
			parentsConfiguration[i - 1] = configuration[i];
		}
		
		// Gets the evidence
		int j = 0;
        // Adds to evidence a finding containing the parent and its configuration    
		Finding finding;
		for (Variable var : parents) {
			finding = new Finding(var, parentsConfiguration[j]);
			evidence.addFinding(finding);
			j++;
		}
		return evidence;
	}

	
	
	
	
	
	/**
	 * This method gets the Evidence Case from the selected column
	 * 
	 * @return Evidence case
	 */
	public EvidenceCase getEvidenceCaseFromSelectedColumn() {
		EvidenceCase evi = null;
		try {
			evi = getConfiguration(selectedColumn);
		} catch (InvalidStateException | IncompatibleEvidenceException e) {
			e.printStackTrace();
		}
		return evi;
	}

//	/**
//	 * Creates and shows the UncertainValuesDialog object
//	 * 
//	 * @throws WrongCriterionException
//	 * revised-->minor changes
//	 */
//	public void showUncertaintyDialog() throws WrongCriterionException {
//		// Generates the evidenceCase based on the column
//		// selected on the JTable object
//		evidenceCase = getEvidenceCaseFromSelectedColumn();
//		UncertainValuesDialog uncertDialog = new UncertainValuesDialog(
//				Utilities.getOwner(this), evidenceCase, (TablePotential) node
//						.getPotentials().get(0));
//		int button = uncertDialog.requestUncertainValues();
//		if (button == UncertainValuesDialog.OK_BUTTON) {
//			UncertainValuesEdit uncertEdit = new UncertainValuesEdit(node,
//					uncertDialog.getUncertainColumn(),
//					uncertDialog.getValuesColumn(), uncertDialog.getPosBase(),
//					selectedColumn, uncertDialog.isChanceVariable());
//			try {
//				node.getProbNet().doEdit(uncertEdit);
//				if (selectedColumn > 0) {
//					((ValuesTableCellRenderer) getValuesTable()
//							.getDefaultRenderer(Double.class))
//							.setMark(selectedColumn - 1);
//					getValuesTable().repaint();
//					this.getTableModel().setNotEditablePositions(
//							getNotEditablePositions());
//				}
//			} catch (ConstraintViolationException | CanNotDoEditException
//					| NonProjectablePotentialException | DoEditException e) {
//				e.printStackTrace();
//				JOptionPane.showMessageDialog(this,
//						stringDatabase.getString(e.getMessage()),
//						stringDatabase.getString(e.getMessage()),
//						JOptionPane.ERROR_MESSAGE);
//			}
//		}
//	}

	/**
	 * Creates and shows the UncertainValuesDialog object
	 * 
	 * @throws WrongCriterionException
	 * revised-->minor changes
	 */
	public void showUncertaintyDialog() throws WrongCriterionException {
		// Generates the evidenceCase based on the column
		// selected on the JTable object
		evidenceCase = getEvidenceCaseFromSelectedColumn();
		UncertainValuesDialog uncertDialog;
		if (isTableDeltaPotential){
			uncertDialog = new UncertainValuesDialog(
					Utilities.getOwner(this), evidenceCase, (ExactDistrPotential)potential);
		} 
		else {
			uncertDialog = new UncertainValuesDialog(
				Utilities.getOwner(this), evidenceCase, tablePotential);
		}
		int button = uncertDialog.requestUncertainValues();
		if (button == UncertainValuesDialog.OK_BUTTON) {
			UncertainValuesEdit uncertEdit=null;
			try{
					uncertEdit = new UncertainValuesEdit(node,
					uncertDialog.getUncertainColumn(),
					uncertDialog.getValuesColumn(), uncertDialog.getPosBase(),
					selectedColumn, uncertDialog.isChanceVariable());
			} catch (Exception e){
				e.printStackTrace();
			}
			try {
				node.getProbNet().doEdit(uncertEdit);
				if (selectedColumn > 0) {
					((ValuesTableCellRenderer) getValuesTable()
							.getDefaultRenderer(Double.class))
							.setMark(selectedColumn - 1);
					getValuesTable().repaint();
					this.getTableModel().setNotEditablePositions(
							getNotEditablePositions());
				}
			} catch (ConstraintViolationException | CanNotDoEditException
					| NonProjectablePotentialException | DoEditException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this,
						stringDatabase.getString(e.getMessage()),
						stringDatabase.getString(e.getMessage()),
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * This method initialises valuesTable and defines that first two columns cannot be selected
	 * 
	 * @return a new values table.
	 * revised-->not changed
	 */
	public ValuesTable getValuesTable() {
		if (valuesTable == null) {
			valuesTable = new ValuesTable(node, getTableModel(), modifiable);
			valuesTable.setName("PotentialsTablePanel.valuesTable");
		}
		return valuesTable;
	}

	/**
	 * This method initialises valuesTableScrollPane.
	 * 
	 * @return a new values table scroll pane.
	 * revised-->not changed
	 */
	protected JScrollPane getValuesTableScrollPane() {
		if (valuesTableScrollPane == null) {
			valuesTableScrollPane = new JScrollPane();
			valuesTableScrollPane
					.setName("TablePotentialPanel.valuesTableScrollPane");
			valuesTableScrollPane.setViewportView(getValuesTable());
		}
		return valuesTableScrollPane;
	}

	/**
	 * special method to show/hide the values table
	 * revised-->not changed
	 */
	public void showValuesTable(final boolean visible) {
		getValuesTable().setVisible(visible);
	}

//	/**
//	 * This method initialises tableModel.
//	 * 
//	 * @return a new tableModel.
//	 */
//	protected ValuesTableModel getTableModel() {
//		ValuesTableModel tableModel = null;
//		if (valuesTable == null) {
//			tableModel = new ValuesTableModel(data, columns, firstEditableRow);
//		} else if (valuesTable.getTableModel() == null) {
//			tableModel = new ValuesTableModel(data, columns, firstEditableRow);
//		} else {
//			tableModel = (ValuesTableModel) valuesTable.getModel();
//		}
//		return tableModel;
//	}

	/**
	 * This method returns the tableModel of valuesTable. If valuesTable has not a tableModel, this method creates one.
	 * 
	 * @return the tableModel of valuesTable.  
	 * @see valuesTable
	 * revised-->minor changes
	 * 
	 */
	protected ValuesTableModel getTableModel() {
		ValuesTableModel tableModel = null;
		if ((valuesTable == null) || (valuesTable.getTableModel() == null)) 
			tableModel = new ValuesTableModel(data, columns, firstEditableRow);
		else 
			tableModel = (ValuesTableModel) valuesTable.getModel();
		
		return tableModel;
	}
	
	
// This method is commented because it is not used	
//	/**
//	 * This method handles the type of potential to be used for the model to be
//	 * deterministic
//	 */
//	public void setDeterministicModel() {
//		valuesTable.setDeterministic(true);
//		setShowAllParameters(true);
//	}

// This method is commented because it is not used	
//	/**
//	 * This method handles the type of potential to be used for the model to be
//	 * probabilistic
//	 */
//	public void setProbabilisticModel() {
//		valuesTable.setDeterministic(false);
//		setShowAllParameters(true);
//	}

// This method is commented because it is not used	
//	/**
//	 * This method handles the type of potential to be used for the model to be
//	 * optimal (decision node)
//	 */
//	public void setOptimalModel() {
//		valuesTable.setShowingOptimal(true);
//	}

// This method is commented because it is not used	
//	/**
//	 * This method handles the type of potential to be used for the model to be
//	 * general (TablePotential)
//	 */
//	public void setGeneralModel(int familyIndex) {
//		valuesTable.setUsingGeneralPotential(familyIndex);
//	}

// This method is commented because it is not used	
//	/**
//	 * This method handles the type of potential to be used for the model to be
//	 * canonical (ICIPotential)
//	 */
//	public void setCanonicalModel(int familyIndex) {
//		valuesTable.setUsingGeneralPotential(familyIndex);
//	}

	/**
	 * Show/Hide all the parameters
	 * 
	 * @param showAllParameters
	 *            the showAllParameters to set
	 */
	public void setShowAllParameters(boolean showAllParameters) {
		this.showAllParameters = showAllParameters;
		valuesTable.setShowingAllParameters(showAllParameters);
	}

// Commented because it is not used
//	/**
//	 * Show/Hide the probabilities values
//	 * 
//	 * @param showProbabilitiesValues
//	 *            the showProbabilitiesValues to set
//	 */
//	public void setShowProbabilitiesValues(boolean showProbabilitiesValues) {
//		this.showProbabilitiesValues = showProbabilitiesValues;
//		valuesTable.setShowingProbabilitiesValues(showProbabilitiesValues);
//	}


// This method is commented because it is not used		
//	/**
//	 * Show/Hide the TPC values
//	 * 
//	 * @param showTPCvalues
//	 *            the showTPCvalues to set
//	 */
//	public void setShowTPCvalues(boolean showTPCvalues) {
//		this.showTPCvalues = showTPCvalues;
//		valuesTable.setShowingTPCvalues(showTPCvalues);
//	}

	/**
	 * Handles an action performed
	 * revised-->not changed
	 */
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if (actionCommand.equals(ActionCommands.UNCERTAINTY_ASSIGN)
				|| actionCommand.equals(ActionCommands.UNCERTAINTY_EDIT)) {
			try {
				showUncertaintyDialog();
			} catch (WrongCriterionException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this,
						stringDatabase.getString(e1.getMessage()),
						stringDatabase.getString(e1.getMessage()),
						JOptionPane.ERROR_MESSAGE);
			}
		} else if (actionCommand.equals(ActionCommands.UNCERTAINTY_REMOVE)) {
			try {
				removeUncertainty();
			} catch (WrongCriterionException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this,
						stringDatabase.getString(e1.getMessage()),
						stringDatabase.getString(e1.getMessage()),
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

//	/**
//	 * Method for removing the uncertain values for a certain configuration
//	 * 
//	 * @throws WrongCriterionException
//	 */
//	public void removeUncertainty() throws WrongCriterionException {
//		evidenceCase = getEvidenceCaseFromSelectedColumn();
//		UncertainValuesRemoveEdit uncertEdit = new UncertainValuesRemoveEdit(
//				node, evidenceCase);
//		try {
//			node.getProbNet().doEdit(uncertEdit);
//			if (selectedColumn > 0) {
//				((ValuesTableCellRenderer) getValuesTable().getDefaultRenderer(
//						Double.class)).unMark(selectedColumn - 1);
//				getValuesTable().repaint();
//				this.getTableModel().setNotEditablePositions(
//						getNotEditablePositions(node));
//			}
//		} catch (ConstraintViolationException | CanNotDoEditException
//				| NonProjectablePotentialException | DoEditException e) {
//			e.printStackTrace();
//			JOptionPane.showMessageDialog(this,
//					stringDatabase.getString(e.getMessage()),
//					stringDatabase.getString(e.getMessage()),
//					JOptionPane.ERROR_MESSAGE);
//		}
//	}

	/**
	 * Method for removing the uncertain values for a certain configuration
	 * 
	 * @throws WrongCriterionException
	 * revised-->minor changes; only changed the call to getNotEditablePositions
	 */
	public void removeUncertainty() throws WrongCriterionException {
		evidenceCase = getEvidenceCaseFromSelectedColumn();
		UncertainValuesRemoveEdit uncertEdit = new UncertainValuesRemoveEdit(
				node, evidenceCase);
		try {
			node.getProbNet().doEdit(uncertEdit);
			if (selectedColumn > 0) {
				((ValuesTableCellRenderer) getValuesTable().getDefaultRenderer(
						Double.class)).unMark(selectedColumn - 1);
				getValuesTable().repaint();
				this.getTableModel().setNotEditablePositions(
						getNotEditablePositions());
			}
		} catch (ConstraintViolationException | CanNotDoEditException
				| NonProjectablePotentialException | DoEditException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,
					stringDatabase.getString(e.getMessage()),
					stringDatabase.getString(e.getMessage()),
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
	
	/**
	 * Method for update the options showed in the contextual menu
	 * revised-->not changed
	 */
	private void updateContextualMenuOptions() {
		if (node.getPotentials().size() > 0
				&& node.getPotentials().get(0) instanceof TablePotential) {
			TablePotential tablePotential = (TablePotential) node
					.getPotentials().get(0);
			boolean hasUncertainty = tablePotential
					.hasUncertainty(getEvidenceCaseFromSelectedColumn());
			if (hasUncertainty) {
				getUncertaintyContextualMenu().getJComponentActionCommand(
						ActionCommands.UNCERTAINTY_ASSIGN.toString())
						.setEnabled(false);
				getUncertaintyContextualMenu().getJComponentActionCommand(
						ActionCommands.UNCERTAINTY_EDIT.toString()).setEnabled(
						true);
				getUncertaintyContextualMenu().getJComponentActionCommand(
						ActionCommands.UNCERTAINTY_REMOVE.toString())
						.setEnabled(true);
			} else {
				getUncertaintyContextualMenu().getJComponentActionCommand(
						ActionCommands.UNCERTAINTY_ASSIGN.toString())
						.setEnabled(true);
				getUncertaintyContextualMenu().getJComponentActionCommand(
						ActionCommands.UNCERTAINTY_EDIT.toString()).setEnabled(
						false);
				getUncertaintyContextualMenu().getJComponentActionCommand(
						ActionCommands.UNCERTAINTY_REMOVE.toString())
						.setEnabled(false);
			}
		}
	}

//	/**
//	 * Handles the double click in a cell
//	 * 
//	 * @param evt
//	 */
//	private void doubleClickEvent(MouseEvent evt) {
//		if (node.getPotentials().size() > 0
//				&& node.getPotentials().get(0) instanceof TablePotential) {
//			TablePotential tablePotential = (TablePotential) node
//					.getPotentials().get(0);
//
//			EvidenceCase configuration = null;
//			int selectedColumn = valuesTable.columnAtPoint(evt.getPoint());
//			try {
//				configuration = getConfiguration((TablePotential) node
//						.getPotentials().get(0), selectedColumn);
//			} catch (InvalidStateException | IncompatibleEvidenceException e) {
//				e.printStackTrace();
//			}
//			boolean hasUncertainty = tablePotential
//					.hasUncertainty(configuration);
//			if (hasUncertainty) {
//				try {
//					showUncertaintyDialog();
//				} catch (WrongCriterionException e1) {
//					e1.printStackTrace();
//					JOptionPane.showMessageDialog(this,
//							stringDatabase.getString(e1.getMessage()),
//							stringDatabase.getString(e1.getMessage()),
//							JOptionPane.ERROR_MESSAGE);
//				}
//			}
//		}
//	}

	/**
	 * Handles the double click in a cell
	 * 
	 * @param evt
	 * revised-->minor changes; only changed the call to getConfiguration
	 */
	private void doubleClickEvent(MouseEvent evt) {
		if (node.getPotentials().size() > 0
				&& node.getPotentials().get(0) instanceof TablePotential) {
			TablePotential tablePotential = (TablePotential) node
					.getPotentials().get(0);

			EvidenceCase configuration = null;
			int selectedColumn = valuesTable.columnAtPoint(evt.getPoint());
			try {
				configuration = getConfiguration(selectedColumn);
			} catch (InvalidStateException | IncompatibleEvidenceException e) {
				e.printStackTrace();
			}
			boolean hasUncertainty = tablePotential
					.hasUncertainty(configuration);
			if (hasUncertainty) {
				try {
					showUncertaintyDialog();
				} catch (WrongCriterionException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this,
							stringDatabase.getString(e1.getMessage()),
							stringDatabase.getString(e1.getMessage()),
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	
	
	
	/**
	 * This method initialises uncertaintyContextualMenu.
	 * 
	 * @return the node contextual menu.
	 * revised-->not changed
	 */
	private UncertaintyContextualMenu getUncertaintyContextualMenu() {
		if (uncertaintyContextualMenu == null) {
			uncertaintyContextualMenu = new UncertaintyContextualMenu(this);
			uncertaintyContextualMenu.setName("uncertaintyContextualMenu");
		}
		return uncertaintyContextualMenu;
	}

//	/**
//	 * set renders for the cells in the table. Only has to be called when set
//	 * data.
//	 * 
//	 * @param uncertaintyInColumns2
//	 */
//	protected void setCellRenderers(boolean[] uncertaintyInColumns) {
//		int firstEditableRow = getFirstEditableRow();
//		TableCellRenderer cellRenderer = null;
//		if (node.getPotentials().size() > 0) {
//			if (node.getNodeType() != NodeType.DECISION) {
//				if (!hasLinkRestriction) {
//					cellRenderer = new ValuesTableCellRenderer(
//							firstEditableRow, uncertaintyInColumns);
//				} else {
//					cellRenderer = new ValuesTableWithLinkRestrictionCellRenderer(
//							firstEditableRow, uncertaintyInColumns);
//				}
//			} else { // node.getNodeType() == NodeType.DECISION)
//				if (node.getPolicyType() == PolicyType.OPTIMAL
//						&& (node.getPotentials().isEmpty() || !node
//								.getPotentials().get(0).isUtility())) {
//					boolean imposingPolicyByUser = node.hasPolicy() && !isReadOnly();
//					cellRenderer = new ValuesTableOptimalPolicyCellRenderer(
//							firstEditableRow, uncertaintyInColumns, imposingPolicyByUser);
//				} else {
//					boolean showingOptimalPolicy = node.getPotentials().get(0).isUtility() && isReadOnly();
//					if (!showingOptimalPolicy) {
//						cellRenderer = new ValuesTableCellRenderer(
//								firstEditableRow, uncertaintyInColumns);
//					} else {
//						// When showing the expected utility we want the color of the cells to be green
//						cellRenderer = new ValuesTableOptimalPolicyCellRenderer(
//								firstEditableRow, uncertaintyInColumns, true);
//					}
//				}
//			}
//			valuesTable.setDefaultRenderer(Double.class, cellRenderer);
//			valuesTable.setDefaultRenderer(String.class, cellRenderer);
//		}
//
//	}


	/**
	 * This method sets renders for the cells in the table. Only has to be called when it sets
	 * data.
	 * It is always used when potential!=null
	 * 
	 * In a DECISION node a change is colored in green
	 * 
	 * UNCLEAR--> When ReadOnly is se?
	 *  
	 * NodeType.DECISION + policyType.OPTIMAL +!potential.isUtility()
	 * @param uncertaintyInColumns
	 * @author carmenyago
	 */
	protected void setCellRenderers(boolean[] uncertaintyInColumns) {
		
		TableCellRenderer cellRenderer = null;
		
		if (node.getNodeType() != NodeType.DECISION) {
			// Creates the TableCellRenderer distinguishing if the node has or not link restrictions
			if (!hasLinkRestriction) {
				cellRenderer = new ValuesTableCellRenderer(
							firstEditableRow, uncertaintyInColumns);
			} else {
				cellRenderer = new ValuesTableWithLinkRestrictionCellRenderer(
							firstEditableRow, uncertaintyInColumns);
			}
			
			
		} else { // node.getNodeType() == NodeType.DECISION)
			if ( (node.getPolicyType() == PolicyType.OPTIMAL) && 
					(node.getPotentials().isEmpty() || (!node.getPotentials().get(0).hasCriterion())))
					
			{
				// UNCLEAR--> When ReadOnly is se?
				// A node has policy if is a decision node with a non uniform potential
				boolean imposingPolicyByUser = node.hasPolicy() && !isReadOnly();
				cellRenderer = new ValuesTableOptimalPolicyCellRenderer(
							firstEditableRow, uncertaintyInColumns, imposingPolicyByUser);
			} else {
				boolean showingOptimalPolicy = node.getPotentials().get(0).hasCriterion() && isReadOnly();
				if (!showingOptimalPolicy) {
					cellRenderer = new ValuesTableCellRenderer(
								firstEditableRow, uncertaintyInColumns);
				} else {
						// When showing the expected utility we want the color of the cells to be green
					cellRenderer = new ValuesTableOptimalPolicyCellRenderer(
								firstEditableRow, uncertaintyInColumns, true);
				}
			}
		}
		valuesTable.setDefaultRenderer(Double.class, cellRenderer);
		valuesTable.setDefaultRenderer(String.class, cellRenderer);
	}

	
	/**
	 * Method to define the specific listeners in this table (not defined in the
	 * common KeyTable hierarchy. This method creates the evidenceCase object
	 * when the user do right click on the table.
	 * 
	 * revised--> not changed
	 * UNCLEAR-->What happens with read only and uncertainty?
	 */
	protected void setTableSpecificListeners() {
		valuesTable.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				int row = valuesTable.rowAtPoint(e.getPoint());
				int col = valuesTable.columnAtPoint(e.getPoint());
				selectedColumn = col;
				if (SwingUtilities.isLeftMouseButton(e)) {
					valuesTable.editCellAt(
							valuesTable.rowAtPoint(e.getPoint()),
							valuesTable.columnAtPoint(e.getPoint()), e);
				}
				if (SwingUtilities.isRightMouseButton(e)) {
					if ((row > -1) && (col > 0) && !isReadOnly()) {
						if (getUncertaintyContextualMenu() != null) {
							updateContextualMenuOptions();
							getUncertaintyContextualMenu().show(valuesTable,
									e.getX(), e.getY());
						}
					}
				}
			}

		});
		valuesTable.addMouseListener(new DoubleClickListener());
	}

	/**
	 * This class overrides the double click listener calling the
	 * 
	 * @see doubleClickEvent
	 * revised-->not changed
	 */
	public class DoubleClickListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				doubleClickEvent(e);
			}
		}
	}

	/**
	 * Close the table
	 * revised-->not changed
	 */
	@Override
	public void close() {
		getValuesTable().close();
	}

//	/**
//	 * This method sets the attributes this.readOnly= readOnly and modifiable = !readOnly to indicate 
//	 * if the table is read only (readOnly=true) or editable (readOnly = false).
//	 * It also changes the cell renderer according to readOnly
//	 * @param readOnly
//	 * 			- if true, all the table cells become not editable, if false the data cells become editable
//	 * 
//	 */
//	@Override
//	public void setReadOnly(boolean readOnly) {
//		boolean wasReadOnly = super.isReadOnly();
//		super.setReadOnly(readOnly);
//		/*
//		The read only attribute is set after the constructor is invoked and then,
//		after the setData(node) method is called. Thus, the cell renderer may need to be changed.
//		This is the case if the new read only value is different from the previous one.
//		 */
//		if (wasReadOnly != readOnly) {
//			boolean[] uncertaintyInColumns = null;
//			if (node.getPotentials() != null) {
//				uncertaintyInColumns = getUncertaintyInColumns(node);
//				setCellRenderers(uncertaintyInColumns);
//			} else {
//				setCellRenderers(uncertaintyInColumns);
//			}
//		}
//		getValuesTable().setModifiable(!readOnly);
//	}

	/**
	 * This method sets the attributes this.readOnly= readOnly and modifiable = !readOnly to indicate 
	 * if the table is read only (readOnly=true) or editable (readOnly = false).
	 * It also changes the cell renderer according to readOnly
	 * @param readOnly
	 * 			- if true, all the table cells become not editable, if false the data cells become editable
	 * revised-->minor changes; only changed the call to getUncertaintyInColumns
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		boolean wasReadOnly = super.isReadOnly();
		super.setReadOnly(readOnly);
		/*
		The read only attribute is set after the constructor is invoked and then,
		after the setData(node) method is called. Thus, the cell renderer may need to be changed.
		This is the case if the new read only value is different from the previous one.
		 */
		if (wasReadOnly != readOnly) {
			boolean[] uncertaintyInColumns = null;
			if (node.getPotentials() != null) {
				uncertaintyInColumns = getUncertaintyInColumns();
				setCellRenderers(uncertaintyInColumns);
			} else {
				setCellRenderers(uncertaintyInColumns);
			}
		}
		getValuesTable().setModifiable(!readOnly);
	}

}




