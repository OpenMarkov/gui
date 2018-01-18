/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.dialog.common;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.table.TableCellRenderer;

import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.gui.component.AugmentedValuesTable;
import org.openmarkov.gui.component.AugmentedValuesTableModel;
import org.openmarkov.gui.component.PotentialsTablePanelOperations;
import org.openmarkov.gui.component.ValuesTable;
import org.openmarkov.gui.component.ValuesTableCellRenderer;
import org.openmarkov.gui.component.ValuesTableModel;
import org.openmarkov.gui.menutoolbar.common.ActionCommands;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.AugmentedTablePotential;
import org.openmarkov.core.model.network.potential.AugmentedTable;
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
 * @author carmenyago Apr/2017
 */
@SuppressWarnings("serial")
@PotentialPanelPlugin(potentialType = "AugmentedTable")
public class AugmentedTablePotentialPanel extends TablePotentialPanel {
/**
 * Variables of the AugmentedTablePotential. 
 * The attribute <code>variables</code> contains the variables of the AugmentedTable
 */
protected List<Variable> potentialVariables;	
		
public AugmentedTablePotentialPanel(){
		super();
}	

/**
 * This method creates, initialises, and displays an AugmentedValuesTable object for the first potential of the node
 * 
 * @param node : node whose first potential is a AugmentedTablePotential 
 */
public AugmentedTablePotentialPanel(Node node){
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
	//The table associated to tablePotential
	tablePotential = ((AugmentedTablePotential)potential).getAugmentedTable();

	//The list of variables of the AugmentedTablePotential
	potentialVariables = potential.getVariables();
	
	//The list of variables of the AugmentedTable of TablePotential
	variables = tablePotential.getVariables();
			
	// Creating the table; class AugmentedValuesTable
	valuesTable = new AugmentedValuesTable(node, getTableModel(), modifiable);
	valuesTable.setName("PotentialsTablePanel.augmentedValuesTable");
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
	
/**
 * This method returns the tableModel of valuesTable. If valuesTable has not a tableModel, this method creates one.
 * 
 * @return the tableModel of valuesTable.  
 * @see valuesTable
 * revised-->minor changes
 * 
 */
@Override
protected ValuesTableModel getTableModel() {
    AugmentedValuesTableModel tableModel = null;
    if ((valuesTable == null) || (valuesTable.getTableModel() == null)) 
        tableModel = new AugmentedValuesTableModel(data, columns, firstEditableRow);
    else 
        tableModel = (AugmentedValuesTableModel) valuesTable.getModel();
    
    return tableModel;
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
 * UNCLEAR--> It is necessary to Override??
 * @author carmenyago
 */
@Override	
public void setData()  {
		
	// true 
	hasLinkRestriction = LinkRestrictionPotentialOperations.hasLinkRestriction(node);
	// Sets the probNet in the table
	
	valuesTable.setData(node);
	
	Object[][] tableData = null;
	uncertaintyInColumns = null;
	String[] newColumns = null;
	
	// tableData contains the table to be displayed in ValuesTable
	// No override. Overriding some internal methos
	tableData = convertListPotentialsToTableFormat();
	
	// Sets the column names in Excel style: A, B, C,....AA,AB...
	// These column names aren't displayed
	newColumns = ValuesTable.getColumnsIdsSpreadSheetStyle(tableData[0].length);
	
	
	//Sets the table model in valuesTable
	//No override
	setDataInValuesTable(tableData, newColumns);
		
	// set the Cell Renders according to NodeType (a different renderer for some DECISON nodes) and the uncertainty
	setCellRenderers(uncertaintyInColumns);
	
	//this.getTableModel().setNotEditablePositions(getNotEditablePositions());
	
	// Establish the column width
	valuesTable.fitColumnsWidthToContent();
}
	
/**
 * Sets a new table model with new data and new columns in valuesTable
 * Only to put and AugmentedValuesTableModel-->Change in values table
 * @param newData
 *            new data for the table
 * @param newColumns
 *            new columns for the table
 */
@Override
public void setDataInValuesTable(Object[][] newData, String[] newColumns) {
    
    // Table data
    data = newData.clone();
    // Table columns
    columns = newColumns.clone();
    
    // resets the tableModel
    valuesTable.resetModel();
 
    // Sets the valuesTable tableModel with columns, data
    valuesTable.setModel(new AugmentedValuesTableModel(data, columns, firstEditableRow));
    
    // Initialises a false an array which tells which data are modified
    valuesTable.initializeDataModified(false);
    
    
    valuesTable.setLastEditableRow(lastEditableRow);
    valuesTable.setFirstEditableRow(firstEditableRow);
    
     //show/hide rows based on the showingAllParameters attribute using a RowFilter mechanism.
    valuesTable.setShowingAllParameters(true);
    
    valuesTable.setNodeType(node.getNodeType());
}





/**
 * Creates and empty array of empty objects with the [number_of_rows][number_of_columns] of the valuesTable
 * Considers the potential is not null
 * Minor override only necessary for calculateFirstEditableRow,also removed tableDeltaPotential lines
 * 
 * @author carmenyago
 *            
 * Continuous variables have only one state
 *  tableSize is always >0       
 */
@Override
protected Object[][] createEmptyTable() { 
 
    int numRows = 0;
    int numColumns = 1; // Variables column
    
    // First editable row coincides with the number of parents
    //CHANGE (minor node by tablePotential
    firstEditableRow = tablePotentialsPanelOperations.calculateFirstEditableRow(tablePotential);
    
    // The baseIndexForCoordinates is the first editable row-->What for-->UNCLEAR
    // The property baseIndexForCoordinates is not Visible. baseIndexForCoordinates= row
    setBaseIndexForCoordinates(firstEditableRow);   
        

    // Number of data elements of tablePotential
    int tableSize =tablePotential.getTableSize();//-->UNCLEAR What happens when there is no parent (f.e. when Tree/ADD )
    
    // Number of states of the variable of the node; if isTableDeltaPotential numDimensions=1
    int numDimensions = tablePotential.getDimensions()[0];
    // Parent variables + states of node variable
    numRows = firstEditableRow + numDimensions;
    lastEditableRow= numRows-1;    
    
    /*if (!isTableDeltaPotential) numRows++;*/ //--> UNCLEAR Last row with the name of the variable and the state with '1' is REMOVED
    numColumns = numColumns + tableSize /numDimensions; 
    
    
    // create the array of arrays
    return new Object[numRows][numColumns];
}

	
/**
 * Sets the data table from potential in oldValues
 * 
 * @param oldValues
 * 
 * @return an array filled with the date table from tablePotential or tableDeltaPotential filled with the data values 
 * from tablePotential or tableDeltaPotential in the correct positions to be displayed by ValuesTable
 * 
 */
@Override
protected Object[][] setPotentialDataInCentreArea(Object[][] oldValues) {
	Object[][] values = oldValues;

	int numColumns = values[0].length;
	
	// rounding initial values
	String[] initialValues = ((AugmentedTable)tablePotential).getFunctionValues();
	for (int j = 1; j <= numColumns - 1; j++) {

		// put the values on the table
		for (int i = getLastEditableRow(); i >= getFirstEditableRow(); i--) {
			int potentialIndex=tablePotentialsPanelOperations.getPotentialIndex(i, j, tablePotential);
			String value = initialValues[potentialIndex];
			values[i][j] = value;
		}
	}
	return values;
}





/**
 * This method calculates the number of data cells and stores it in the attribute positions.
 * The number of data cell is the product of the number of states of all variables
 * 
 * Override because we need the variables in tablePotential no in potential
 */
@Override
protected int setNumberOfPostions() {
	int numPositions = 1;
	try {
		for (Variable variable : tablePotential.getVariables()) {
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
	 * Show/Hide all the parameters
	 * 
	 * @param showAllParameters
	 *            the showAllParameters to set
	 */
	public void setShowAllParameters(boolean showAllParameters) {
		this.showAllParameters = showAllParameters;
		valuesTable.setShowingAllParameters(showAllParameters);
	}


//	/**
//	 * Handles an action performed
//	 * revised-->not changed
//	 */
//	public void actionPerformed(ActionEvent e) {
//		String actionCommand = e.getActionCommand();
//		if (actionCommand.equals(ActionCommands.UNCERTAINTY_ASSIGN)
//				|| actionCommand.equals(ActionCommands.UNCERTAINTY_EDIT)) {
//			try {
//				showUncertaintyDialog();
//			} catch (WrongCriterionException e1) {
//				e1.printStackTrace();
//				JOptionPane.showMessageDialog(this,
//						stringDatabase.getString(e1.getMessage()),
//						stringDatabase.getString(e1.getMessage()),
//						JOptionPane.ERROR_MESSAGE);
//			}
//		} else if (actionCommand.equals(ActionCommands.UNCERTAINTY_REMOVE)) {
//			try {
//				removeUncertainty();
//			} catch (WrongCriterionException e1) {
//				e1.printStackTrace();
//				JOptionPane.showMessageDialog(this,
//						stringDatabase.getString(e1.getMessage()),
//						stringDatabase.getString(e1.getMessage()),
//						JOptionPane.ERROR_MESSAGE);
//			}
//		}
//	}

	/**
     * Method to define the specific listeners in this table (not defined in the
     * common KeyTable hierarchy. This method creates the evidenceCase object
     * when the user do right click on the table.
     * 
     */
    @Override
	protected void setTableSpecificListeners() {        
        valuesTable.addMouseListener(new MouseClickedListener());
    }


/**
 * This class overrides the double click listener calling the
 * 
 * @see doubleClickEvent
 */	
 private class MouseClickedListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() ==1){
                
                String function=null;
                List<Variable> parameterVariables=((AugmentedTablePotential)potential).getParameterVariables();
                ArithmeticExpressionDialog expressionDialog = new ArithmeticExpressionDialog(null, parameterVariables, function);
                expressionDialog.setVisible(true);
                if (expressionDialog.getSelectedButton() == OkCancelHorizontalDialog.OK_BUTTON) {
                    function=expressionDialog.getExpression();
                    int row = valuesTable.rowAtPoint(e.getPoint());
                    int column = valuesTable.columnAtPoint(e.getPoint());
                    valuesTable.setValueAt( function, row, column );                    
                }
            }    
            if (e.getClickCount() == 2) {
                //TODO; now it does nothing
            }
        }
 }	


}




