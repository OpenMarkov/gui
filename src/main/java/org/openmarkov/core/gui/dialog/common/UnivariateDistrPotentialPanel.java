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
import org.openmarkov.core.gui.component.UnivariateValuesTable;
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
	import org.openmarkov.core.model.network.modelUncertainty.ProbDensFunction;
	import org.openmarkov.core.model.network.modelUncertainty.ProbDensFunctionManager;
	import org.openmarkov.core.model.network.potential.UnivariateDistrPotential;
	import org.openmarkov.core.model.network.potential.TablePotential;
	import org.openmarkov.core.model.network.potential.operation.LinkRestrictionPotentialOperations;

	/**
	 * 
	 */
	@SuppressWarnings("serial")
	@PotentialPanelPlugin(potentialType = "UnivariateDistr")
	public class UnivariateDistrPotentialPanel extends ProbabilityTablePanel {
		protected Logger logger;
		/**
		 * JTable where show the values.
		 */
		protected UnivariateValuesTable valuesTable = null;
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
		protected UnivariateDistrPotential potential = null;
		
		
		protected List<Variable>  pseudoVariablesDistribution = null;
		
		
		/**
		 * When potential is an instance of TablePotential, tablePotential is potential casted as TablePotential
		 * When potential is an instance of TableDeltaPotential, tablePotential=(TablePotential)potential.getTablePotential()
		 * @author carmenyago
		 */
		
		protected TablePotential tableDistribution=null;
		
		/**
		 * 
		 */
		protected String previouslySelectedDistribution="";
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
		

	/**
	 * Constructor used by CPTablePanel
	 * This method creates, initialises, and displays a ValuesTable object for the first potential of the node
	 * 
	 * When there is no potential NullListPotentialException is showed-->UNCLEAR stop??? 
	 * 
	 * 
	 * 
	 * 
	 * @param node : node whose first potential is a TablePotential or a TableDeltaPotential
	 * @author carmenyago : adaptation to TableDeltaPotential
	 */
	public UnivariateDistrPotentialPanel(Node node){
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
		potential=(UnivariateDistrPotential)node.getPotentials().get(0);
		// This panel displays the first potential of the node

		// The list of variables of potential
		variables = potential.getVariables();
		
		if (!(potential instanceof UnivariateDistrPotential)) { 
			potential=new UnivariateDistrPotential(variables, potential.getPotentialRole());
		}	
		
		tableDistribution=((UnivariateDistrPotential)potential).getDistributionTable();
		pseudoVariablesDistribution = potential.getDistributionTable().getVariables();		
		
		previouslySelectedDistribution = potential.getProbDensFunctionName();
		// Creating the table; class ValuesTable
		valuesTable = new UnivariateValuesTable(node, getTableModel(), modifiable);
		valuesTable.setName("PotentialUnivariatePanel.valuesTable");
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
		 * Sets a new table model with new data and new columns in valuesTable
		 * @param newData
		 *            new data for the table
		 * @param newColumns
		 *            new columns for the table
		 * @author carmenyago
		 * revised--> minor changes           
		 * Previously named setData; I find this name confusing because coincides with setData()
		 */
		public void setDataInValuesTable(Object[][] newData, String[] newColumns) {
	        
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

		

		/**
		 * Sets the columns that have uncertainty a true in a boolean array
		 * To do that, this method extracts the uncertainty for every column configuration (parents state set)
		 * 
		 * 
		 * @return Boolean array that represents the columns (true = the column has
		 *         an uncertainty, false = the column has not an uncertainty). This array only contains the data columns
		 * @author carmenyago        
		 */
		private boolean[] getUncertaintyInColumns() {

			int size = valuesTable.getColumnCount();
			
			// Column 0 contains the name of the states
			boolean[] newUncertaintyInColumns = new boolean[size - 1];
			for (int i = 0; i < size-1; i++) newUncertaintyInColumns[i]=false;
				
						

			return newUncertaintyInColumns;
		}

		
			
		/**
		 * calculate the number of rows of the table based on the parents and  states of the node variable 
		 * Last row with the name of the variable when TablePotential REMOVED
		 * @author carmenyago
		 */
		protected int howManyRows(Node n) {
			return n.getParents().size() + n.getVariable().getStates().length;		
		}

		
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
			
			// The variable position stores the number o data cells
			setNumberOfPostions();
			return values;
		}

		

		/**
		 * Creates and empty array of empty objects with the [number_of_rows][number_of_columns] of the valuesTable
		 * Considers the potential is not null
		 * @author carmenyago
		 *            
		 * Continuous variables have only one state
		 *  tableSize is always >0       
		 */
		private Object[][] createEmptyTable() { 
	     
			
			int numRows = 0;
			int numColumns = 1; // Variables column
			
			// First editable row coincides with the number of parents
			firstEditableRow = tablePotentialsPanelOperations.calculateFirstEditableRow(tableDistribution);
			
			// The baseIndexForCoordinates is the first editable row-->What for-->UNCLEAR
			// The property baseIndexForCoordinates is not Visible. baseIndexForCoordinates= row
			setBaseIndexForCoordinates(firstEditableRow);	
				
			//if (isTableDeltaPotential) setBaseIndexForCoordinates(firstEditableRow - 1); //UNCLEAR

			// Number of data elements of tablePotential
			int tableSize =tableDistribution.getTableSize();//-->UNCLEAR What happens when there is no parent (f.e. when Tree/ADD )
			
			int numDimensions = tableDistribution.getDimensions()[0];
			// Parent variables + states of node variable
			numRows = firstEditableRow + numDimensions;
			lastEditableRow= numRows-1;    
			
			/*if (!isTableDeltaPotential) numRows++;*/ //--> UNCLEAR Last row with the name of the variable and the state with '1' is REMOVED
			numColumns = numColumns + tableSize /numDimensions;	
			
			// create the array of arrays
			return new Object[numRows][numColumns];
		}

		
		
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
			for (int i = 1; i< pseudoVariablesDistribution.size(); i++){
				values[i-1][0] = pseudoVariablesDistribution.get(i);
			}
			return values;
		}
		



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
			int numParentVariables = pseudoVariablesDistribution.size() -1;
			State[] states;
			
			for (int row = 0; row < numParentVariables; row++) {
			    
				states = pseudoVariablesDistribution.get(row+1).getStates();
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
			int length = lastEditableRow; 
			for (State state : pseudoVariablesDistribution.get(0).getStates()) {
				values[length--][0] = state.getName();
			}
			return values;
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
		private Object[][] setPotentialDataInCentreArea(Object[][] oldValues) {
			Object[][] values = oldValues;

			int numColumns = values[0].length;
			
			
			// rounding initial values
			double[] initialValues = tableDistribution.getValues();
			double[] roundedValues = new double[initialValues.length];
			int maxDecimals = 10;
			double epsilon;
			epsilon = Math.pow(10, -(maxDecimals + 2));
			for (int i = 0; i < initialValues.length; i++) {
				roundedValues[i] = Util.roundAndReduce(initialValues[i], epsilon,
						maxDecimals);
			}

			for (int j = 1; j <= numColumns - 1; j++) {

				// put the values on the table
				
				
				for (int i =lastEditableRow ; i >= firstEditableRow; i--) {
					int potentialIndex=tablePotentialsPanelOperations.getPotentialIndex(i, j, tableDistribution);
					double value = roundedValues[potentialIndex];
					values[i][j] = value;
				}
			}
			return values;
		}


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
		
		
		
		
					
		/****
		 * Calculates the positions of the table which are not editable due to a
		 * link restriction or uncertainty in the columns. 
		 * If the position is not editable the position in the return array is set to 1, otherwise it contains a null value.
		 * 
		 * @return a two dimensional array with the size of the table containing the
		 *         information about the editable positions.
		 *                 
		 * @author carmenyago        
		 * 
		 */
		private Object[][] getNotEditablePositions() {
			Object[][] notEditablePositions = createEmptyTable();

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
	     * 
	     * @param evt
	     */
		public void distributionChanged (String newDistributionName, ActionEvent evt)
	    {
	        
	    	   
	        if (!previouslySelectedDistribution.equals(newDistributionName))
	        {
	            
	            Class <? extends ProbDensFunction> newDistributionClass;
	            try { 
	            	newDistributionClass = ProbDensFunctionManager.getUniqueInstance().getProbDensFunctionClass(newDistributionName);
	            } catch (Exception e){
	            	JOptionPane.showMessageDialog (this,
	                         e.toString(), "Can't instantiate this distribution",
	                        JOptionPane.ERROR_MESSAGE);
	            	return;
	            }
	            potential.setProbDensFunctionClass(newDistributionClass); 
	            potential.setDistributionTable(potential.getVariables(),potential.getPotentialRole());
	            tableDistribution = potential.getDistributionTable();
	            pseudoVariablesDistribution = tableDistribution.getVariables();
	            setData();
	            repaint();
	            previouslySelectedDistribution = newDistributionName;
	        }
	    }
		
		
		
		


		
		/**
		 * This method generates the evidenceCase based on the column selected on
		 * the <code>valuesTable</code> object.
		 * The evidence case has a finding for every parent of the node and its state in column
		 *  
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
			 int[] configuration = tableDistribution.getConfiguration(startPosition);
			
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
			uncertDialog = new UncertainValuesDialog(
					Utilities.getOwner(this), evidenceCase, tableDistribution);


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
				valuesTable = new UnivariateValuesTable(node, getTableModel(), modifiable);
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


		/**
		 * This method returns the tableModel of valuesTable. If valuesTable has not a tableModel, this method creates one.
		 * 
		 * @return the tableModel of valuesTable.  
		 * @see valuesTable
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


		/**
		 * Handles the double click in a cell
		 * 
		 * @param evt
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
		 */
		private UncertaintyContextualMenu getUncertaintyContextualMenu() {
			if (uncertaintyContextualMenu == null) {
				uncertaintyContextualMenu = new UncertaintyContextualMenu(this);
				uncertaintyContextualMenu.setName("uncertaintyContextualMenu");
			}
			return uncertaintyContextualMenu;
		}



		/**
		 * This method sets renders for the cells in the table. Only has to be called when it sets
		 * data.
		 * It is always used when potential!=null
		 * 
		 * In a DECISION node a change is colored in green
		 * 
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


		/**
		 * This method sets the attributes this.readOnly= readOnly and modifiable = !readOnly to indicate 
		 * if the table is read only (readOnly=true) or editable (readOnly = false).
		 * It also changes the cell renderer according to readOnly
		 * @param readOnly
		 * 			- if true, all the table cells become not editable, if false the data cells become editable
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
