/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog.common;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.ListIterator;

import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import org.apache.log4j.Logger;
import org.openmarkov.core.action.UncertainValuesEdit;
import org.openmarkov.core.action.UncertainValuesRemoveEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.NullListPotentialsException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.component.ICIValuesTable;
import org.openmarkov.core.gui.component.PotentialsTablePanelOperations;
import org.openmarkov.core.gui.component.ValuesTable;
import org.openmarkov.core.gui.component.ValuesTableCellRenderer;
import org.openmarkov.core.gui.component.ValuesTableModel;
import org.openmarkov.core.gui.dialog.node.UncertainValuesDialog;
import org.openmarkov.core.gui.loader.element.IconLoader;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.gui.menutoolbar.common.ActionCommands;
import org.openmarkov.core.gui.menutoolbar.menu.PopupMenuFactory;
import org.openmarkov.core.gui.menutoolbar.menu.UncertaintyPopup;
import org.openmarkov.core.gui.util.Utilities;
import org.openmarkov.core.model.network.UtilStrings;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.canonical.ICIPotential;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;




/**
 * This class implements a potentials table with the following features:
 * <ul>
 * <li>Its elements, except the first column, are modifiable.</li>
 * <li>New elements can be added, creating a new key row with empty data.</li>
 * <li>The key data (first column) consist of a key string following of the
 * index of the row and it is used for internal purposes only.</li>
 * <li>The key data is hidden.</li>
 * <li>The information of a row (except the first column) can not be taken up
 * or down.</li>
 * <li>The rows can not be removed.</li>
 * <li>The first editable row is the one that has the values of the potentials.</li>
 * <li>The rows between 0 and the first editable row are ocuppied by the values
 * of the states of the parents of the variable.</li>
 * <li>The header of columns is hidden.</li>
 * </ul>
 * This class is based upon KeyTablePanel without buttons
 * 
 * @author jlgozalo
 * @version 1.0 jlgozalo
 */
public class PotentialsTablePanel extends JPanel implements ActionListener {

	/**
	 * Static field for serializable class.
	 */
	private static final long serialVersionUID = 6257314234781632512L;

	/**
	 * Panel to scroll the table.
	 */
	protected JScrollPane valuesTableScrollPane = null;

	/**
	 * Table where show the values.
	 */
	protected ValuesTable valuesTable = null;

	/**
	 * Name of the columns of the table.
	 */
	protected String[] columns = null;

	/**
	 * Data of the cells.
	 */
	protected Object[][] data = null;
	/**
	 * number of positions in this table
	 */
	protected int position = -1;
	/**
	 * list of variables that are shown in this table
	 */
	protected ArrayList<Variable> variables = null;
	
	/**
	 * list of potentials for the variable
	 */
	protected ArrayList<Potential> listPotentials = null;
	
	/**
	 * first editable row (only for temporal storage)
	 */
	private int firstEditableRow = -1;
	/**
	 * last editable row (only for temporal storage)
	 */
	private int lastEditableRow = -1;
	
	/**
	 * base index for coordinates in the table
	 */
	private int baseIndexForCoordinates = -1;

	/**
	 * Indicates if the data of the table is modifiable.
	 */
	private boolean modifiable;

	/**
	 * String resource.
	 */
	protected StringResource stringResource = null;
	
	private StringResource messageStringResource;

	/**
	 * Icon loader.
	 */
	protected IconLoader iconLoader = null;
	/**
	 * EvidenceCase used when uncertainty is added
	 */
	private EvidenceCase evidence = null;

	/**
	 * Properties for options to display in the table
	 */
	protected boolean showAllParameters = true;
	protected boolean showProbabilitiesValues = true;
	protected boolean showTPCvalues = true;
	protected boolean showNetValues = true;
	
	

	private ProbNode probNode;
	/**
	 * The popuMenu that appears when there is a click on the valuesTable Object 
	 */
	private PopupMenuFactory popupMenuFactory;

	protected EvidenceCase evidenceCase ;
	/**
	 * index of the column selected in valuesTable
	 */
	private int selectedColumn = -1;

	private UncertaintyPopup uncertaintyPopup;
	
	private Logger logger;

	/**
	 * this is a default constructor with no construction parameters
	 * @wbp.parser.constructor
	 */
	public PotentialsTablePanel(ProbNode probNode) {

		this( probNode, new String[] { "id", "states", "values" },
			new Object[][] { new Object[] { 0, null, 0 } } ); // default init
		
		this.modifiable=false;
		showValuesTable( false );
	}

	/**
	 * This is the default constructor
	 * 
	 * @param newColumns
	 *            array of texts that appear in the header of the columns.
	 * @param newData
	 *            content of the cells.
	 */
	public PotentialsTablePanel(ProbNode probNode, String[] newColumns, 
			Object[][] newData) {

		stringResource =
			StringResourceLoader.getUniqueInstance().getBundleButtons();
		messageStringResource =	
				StringResourceLoader.getUniqueInstance().getBundleMessages();
		iconLoader = new IconLoader();
		columns = newColumns.clone();
		data = newData.clone();
		this.logger = Logger.getLogger(PotentialsTablePanel.class);
		this.probNode = probNode;
		modifiable = true; // table is modifiable
		initialize();
		showValuesTable( true );
		
	}

	/**
	 * This method initialises this instance.
	 */
	protected void initialize() {

		setBorder( new LineBorder( UIManager.getColor( "Table.dropLineColor" ),
			1, false ) );
		final GroupLayout groupLayout = new GroupLayout( (JComponent) this );
		groupLayout.setHorizontalGroup( groupLayout.createParallelGroup(
			GroupLayout.Alignment.LEADING ).addGroup(
			groupLayout.createSequentialGroup().addComponent(
				getValuesTableScrollPane(), GroupLayout.DEFAULT_SIZE, 474,
				Short.MAX_VALUE ) ) );
		groupLayout.setVerticalGroup( groupLayout.createParallelGroup(
			GroupLayout.Alignment.LEADING ).addGroup(
			GroupLayout.Alignment.TRAILING,
			groupLayout.createSequentialGroup().addComponent(
				getValuesTableScrollPane(), GroupLayout.DEFAULT_SIZE, 349,
				Short.MAX_VALUE ) ) );
		setLayout( groupLayout );
		setAutoscrolls( true );
		//setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
		//setCellRenderers();
		setTableSpecificListeners();

	}

	/**
	 * This method initializes valuesTableScrollPane.
	 * 
	 * @return a new values table scroll pane.
	 */
	protected JScrollPane getValuesTableScrollPane() {

		if (valuesTableScrollPane == null) {
			valuesTableScrollPane = new JScrollPane();
			valuesTableScrollPane
				.setName( "PotentialsTablePanel.valuesTableScrollPane" );
			valuesTableScrollPane.setViewportView( getValuesTable() );
		}
		return valuesTableScrollPane;
	}

	/**
	 * This method initialises valuesTable and defines that first two columns
	 * are not selectable
	 * 
	 * @return a new values table.
	 */
	public ValuesTable getValuesTable() {

		if (valuesTable == null) {
			valuesTable = new ValuesTable( probNode, getTableModel(), modifiable );
			valuesTable.setName( "PotentialsTablePanel.valuesTable" );
		}
		return valuesTable;
	}

	/**
	 * special method to show/hide the values table
	 */
	public void showValuesTable(final boolean visible) {

		getValuesTable().setVisible( visible );
	}

	/**
	 * This method initialises tableModel.
	 * 
	 * @return a new tableModel.
	 */
	protected ValuesTableModel getTableModel() {

		ValuesTableModel tableModel = null;
		if (valuesTable == null) {
			tableModel =
				new ValuesTableModel( data, columns, firstEditableRow );
		} else if (valuesTable.getTableModel() == null) {
			tableModel =
				new ValuesTableModel( data, columns, firstEditableRow );
		} else {
			tableModel = (ValuesTableModel) valuesTable.getModel();
		}
		return tableModel;
	}

	/**
	 * This method handles the type of potential to be used for the model to be
	 * deterministic
	 */
	public void setDeterministicModel() {

		valuesTable.setDeterministic( true );
		setShowAllParameters(true);
	}

	/**
	 * This method handles the type of potential to be used for the model to be
	 * probabilistic
	 */
	public void setProbabilisticModel() {

		valuesTable.setDeterministic( false );
		setShowAllParameters(true);
	}

	/**
	 * This method handles the type of potential to be used for the model to be
	 * optimal (decision node)
	 */
	public void setOptimalModel() {

		valuesTable.setShowingOptimal( true );
	}

	/**
	 * This method handles the type of potential to be used for the model to be
	 * general (TablePotential)
	 */
	public void setGeneralModel(int familyIndex) {

		valuesTable.setUsingGeneralPotential( familyIndex );

	}

	/**
	 * This method handles the type of potential to be used for the model to be
	 * canonical (ICIPotential)
	 */
	public void setCanonicalModel(int familyIndex) {

		valuesTable.setUsingGeneralPotential( familyIndex );
	}

	/**
	 * @return the showAllParameters
	 */
	public boolean isShowAllParameters() {

		return showAllParameters;
	}

	/**
	 * @param showAllParameters
	 *            the showAllParameters to set
	 */
	public void setShowAllParameters(boolean showAllParameters) {

		this.showAllParameters = showAllParameters;
		valuesTable.setShowingAllParameters( showAllParameters );
	}

	/**
	 * @return the showProbabilitiesValues
	 */
	public boolean isShowProbabilitiesValues() {

		return showProbabilitiesValues;
	}

	/**
	 * @param showProbabilitiesValues
	 *            the showProbabilitiesValues to set
	 */
	public void setShowProbabilitiesValues(boolean showProbabilitiesValues) {

		this.showProbabilitiesValues = showProbabilitiesValues;
		valuesTable.setShowingProbabilitiesValues( showProbabilitiesValues );
	}

	/**
	 * @return the showTPCvalues
	 */
	public boolean isShowTPCvalues() {

		return showTPCvalues;
	}

	/**
	 * @param showTPCvalues
	 *            the showTPCvalues to set
	 */
	public void setShowTPCvalues(boolean showTPCvalues) {

		this.showTPCvalues = showTPCvalues;
		valuesTable.setShowingTPCvalues( showTPCvalues );
	}

	/**
	 * @return the showNetValues
	 */
	public boolean isShowNetValues() {

		return showNetValues;
	}

	/**
	 * @param showNetValues
	 *            the showNetValues to set
	 */
	public void setShowNetValues(boolean showNetValues) {

		this.showNetValues = showNetValues;
		if (isShowNetValues()) {
			// show Net values
		} else {
			// show compound values
		}
	}


	/**
	 * set renders for the cells in the table. Only has to be called when set data.
	 */
	protected void setCellRenderers() {
		int size = valuesTable.getColumnCount();
		boolean [] aux = new boolean [size-1];
		boolean hasUncertainty;
		if ( probNode.getPotentials().size() > 0 && probNode.getNodeType() != NodeType.DECISION ){
			TablePotential tablePotential = (TablePotential)probNode.getPotentials().get(0);
			
			for (int i=1; i<size;i++){
				hasUncertainty = false;
				try {
					hasUncertainty = tablePotential.hasUncertainty(getConfiguration(tablePotential,i));
				} catch (InvalidStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (IncompatibleEvidenceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				}
				aux[i-1]= hasUncertainty;
			}
			
		}
		
		
		valuesTable.setDefaultRenderer(
			Double.class, new ValuesTableCellRenderer(
				getFirstEditableRow(), aux ) );
		valuesTable.setDefaultRenderer(
			String.class, new ValuesTableCellRenderer(
				getFirstEditableRow(), aux ) );
	}

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
		valuesTable.setShowingAllParameters( true );
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
				convertListPotentialsToTableFormat(properties );
			newColumns =
				ValuesTable
					.getColumnsIdsSpreedSheetStyle( ValuesTable
						.howManyColumns( properties ) );
			setFirstEditableRow( PotentialsTablePanelOperations.calculateFirstEditableRow(
				probNode.getPotentials(), properties ) );
			setLastEditableRow( PotentialsTablePanelOperations.calculateLastEditableRow(
				probNode.getPotentials(), properties ) );
			setData( tableData, newColumns, firstEditableRow, lastEditableRow , properties.getNodeType() );
			setCellRenderers();
		} else {
			tableData = new Object[ 0 ][ 0 ];
			setFirstEditableRow( 0 );
			setData( tableData );
			setCellRenderers();
		}
	}

	
	
	/**
	 * to retrieve the ListPotentials corresponding to the data in the table
	 * 
	 * @return
	 */
	public ArrayList<Potential> getListPotentialsFromData() {

		ArrayList<Potential> result = null;
		result = convertTableFormatToListPotentials( valuesTable );
        //setListPotentials(result);
		return result;
	}

	/**
	 * sets the first row for edition
	 * 
	 * @param firstRow -
	 *            the first row that is available for edition
	 */
	protected void setFirstEditableRow(int firstEditableRow) {

		this.firstEditableRow = firstEditableRow;

	}

	/**
	 * gets the first row on edition
	 * 
	 * @return first row for edition
	 */
	protected int getFirstEditableRow() {

		return this.firstEditableRow;
	}

	/**
	 * @return the lastEditableRow
	 */
	protected int getLastEditableRow() {

		return lastEditableRow;
	}

	/**
	 * @param lastEditableRow
	 *            the lastEditableRow to set
	 */
	protected void setLastEditableRow(int lastEditableRow) {

		this.lastEditableRow = lastEditableRow;
	}

	
	/**
	 * @return the position
	 */
	protected int getPosition() {
	
		return position;
	}

	
	/**
	 * @param position the position to set
	 */
	protected void setPosition(int position) {
	
		this.position = position;
	}

	
	/**
	 * @return the variables
	 */
	protected ArrayList<Variable> getVariables() {
	
		return variables;
	}

	
	/**
	 * @param variables the variables to set
	 */
	protected void setVariables(ArrayList<Variable> variables) {
		//TODO update this statement, when constructor of this class with 
		//potential as parameter is implemented
		if (probNode != null && probNode.getNodeType() == NodeType.UTILITY){
			this.variables = new ArrayList<Variable>();
			this.variables.add(probNode.getVariable());
			for (Variable variable: variables)
				this.variables.add(variable);
		}else
		
		this.variables = variables;
		
	}

	/**
	 * calculate the number of rows of the table based on the type of the node,
	 * the number of parents and the number of states of the variable
	 * 
	 * @param adittionalProperties -
	 *            node adittionalProperties
	 * @return the number of rows of this Potentials Table
	 */
	protected int howManyRows(ProbNode properties) {

		int numRows = 0;
		if (properties.getNode().getParents() != null) {
			numRows = properties.getNode().getParents().size();
		}
		if (properties.getNodeType() == NodeType.UTILITY) {
			numRows += 1;
		} else {
			if (properties.getVariable().getStates() != null) {
				numRows = numRows + properties.getVariable().getStates().length;
			}
		}
		return numRows;
	}
	
	/**
	 * calculate the number of rows of the table based on the type of the node,
	 * the number of parents and the number of states of the variable for
	 * canonical models
	 * 
	 * @param adittionalProperties -
	 *            node adittionalProperties
	 * @return the number of rows of this Potentials Table
	 */
	protected int howManyCanonicalRows(ProbNode properties) {

		int numRows =2;//The first two rows are first for parent´s name and second one for parent´s states 
	
			if (properties.getVariable().getStates() != null) {
				numRows = numRows + properties.getVariable().getStates().length;
			}
		
		return numRows;
	}

	
	/**
	 * @param listPotentials the listPotentials to set
	 */
	public void setListPotentials(ArrayList<Potential> listPotentials) {
	
		this.listPotentials = listPotentials;
	}

	/**
	 * Set a blank data table
	 * 
	 * @param adittionalProperties -
	 *            to obtain the required number of rows and columns
	 * @return the blank data table
	 */
	private Object[][] setBlankTable(ProbNode properties) {

		Object[][] blankTable = null;
		int numRows = howManyRows( properties );
		int numColumns = ValuesTable.howManyColumns( properties );
		blankTable = new Object[ numRows ][ numColumns ];
	    // TODO seria mas practico hacer un potential y luego ejecutar
		// el resto del metodo pero esto funciona
		for (int i = 0; i < properties.getVariable().getStates().length; i++) {

		}

		return blankTable;
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
		int numColumns = ICIValuesTable.howManyCanonicalColumns( properties );
		blankTable = new Object[ numRows ][ numColumns ];
	    // TODO seria mas practico hacer un potential y luego ejecutar
		// el resto del metodo pero esto funciona
		for (int i = 0; i < properties.getVariable().getStates().length; i++) {

		}

		return blankTable;
	}


	private Potential getThisPotential(ArrayList<Potential> listPotentials) {

		Potential aPotential = null;
		try {
			aPotential = ((TablePotential) listPotentials.get( 0 ));
		} catch (Exception ex) {
			//ExceptionsHandler.handleException(
				//ex, "no Potential.get(0) !!!", false );
			logger.error("no Potential.get(0) !!!");
			
		}

		return aPotential;
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
	protected Object[][] convertListPotentialsToTableFormat(ProbNode properties) {
		Object[][] values = null;
		try {
			//mpal
			PotentialsTablePanelOperations.checkIfNoPotential( 
					properties.getPotentials());
			values = setValuesTableSize(values, properties );
			values = setParentsNameInUpperLeftCornerArea(
				values, properties );
			values = setParentsStatesInTopArea( values, properties );
			values = setNodeStatesInLeftArea( values, properties );
			values = setPotentialDataInCentreArea( values, properties );
			if (probNode.getNodeType() !=  NodeType.UTILITY){
				values = setVariableNameInLowerLeftCornerArea(
						values, properties );
				values = setVariableStatesInBottomArea( values, properties );
			}
			setPosition(setNumberOfPostions(properties.getPotentials()));
		} catch (NullListPotentialsException ex) {
			values = setBlankTable( properties );
		}
		return values;
	}
	

	
	/**
	 * Set the Base index for the coordinates in the table related to the 
	 * Potential of the variable of this node
	 * 
	 * @param value - the new base index for coordinates in the table
	 */
	private void setBaseIndexForCoordinates (int value) {
		this.baseIndexForCoordinates = value;
	}
	
	/**
	 * set values table size for the potential
	 * 
	 * @param values -
	 *            the table that is being modified
	 * @param listPotentials -
	 *            the list of potentials of the node
	 * @param adittionalProperties -
	 *            the adittionalProperties of the node
	 */

	private Object[][] setValuesTableSize (Object [][]oldValues,
                                     ProbNode properties) {
		Object [][] values = oldValues;
		int numRows = 0;
		int numColumns = 1; //at least, there is one column for the node names
		int row =
			PotentialsTablePanelOperations.calculateFirstEditableRow(
				properties.getPotentials(), properties );
		setBaseIndexForCoordinates( row );
		setFirstEditableRow( row );
		TablePotential tablePotential =
			(TablePotential) getThisPotential( properties.getPotentials() );
		ArrayList<Variable> variablesBeforeReorder =
			tablePotential.getVariables();
		
		setVariables( variablesBeforeReorder );
		if (properties.getNodeType() == NodeType.UTILITY) {
			setBaseIndexForCoordinates ( row - 1 );
			numRows = getVariables().size() ;
			setLastEditableRow(numRows-1);
			//numRows++;
			if (tablePotential.getTableSize() == 0)
				numColumns ++;
			else
				numColumns += tablePotential.getTableSize();
		} else {
			int numDimensions = tablePotential.getDimensions()[0];//number of states of the conditioned variable
			numRows = getVariables().size() - 1 + numDimensions ; // parents + variableStates 
			setLastEditableRow(numRows-1);
			numRows = numRows + 1 ; // + 1 for variableValues (when used in show as Values
			if (numDimensions == 0) {
				// do nothing??
			} else { // all table div by variable states
				numColumns =
					numColumns
						+ (tablePotential.getTableSize() / numDimensions );
			}
		}
       // create the array of arrays
		values = new Object[ numRows ][ numColumns ];
		return values;
	}
	
	

	


	/**
	 * This methods fills the Upper Left corner of the table with the name of
	 * the parents of the node
	 * 
	 * @param values -
	 *            the table that is being modified
	 * @param adittionalProperties -
	 *            the adittionalProperties of the node
	 */
	private Object[][] setParentsNameInUpperLeftCornerArea(
														Object[][] oldValues,
														ProbNode properties) {

		Object[][] values = oldValues;
		ArrayList<Variable> listParents = new ArrayList<Variable>();
		for (Variable variable : getVariables()) {
			if (!variable.getName().equals( properties.getName() )) {
				listParents.add( variable );
			}
		}

		if ((listParents != null) && (listParents.size() > 0)) {
			for (int i = 0; i < listParents.size(); i++) {
				values[i][0] = listParents.get( i );
			}
		}
		return values;
	}	
	
	private Object[][] setFirstCanonicalColumn(	Object[][] oldValues, ProbNode properties) {
		
		Object[][] values = oldValues;
		ICIPotential iciPotential = (ICIPotential) getThisICIPotential(properties.getPotentials());
		Variable conditioned = iciPotential.getVariables().get(0);
		values [0][0] = ""; //First cell is empty
		values [1][0] = conditioned.getBaseName();// name of the conditioned variable
		State[] states = conditioned.getStates();
		for (int i = 0; i < states.length; i++) {
			values[i+2][0] = states[i].getName();
			}
		return values;
	}
	
	

	/**
	 * @param values -
	 *            the table that is being modified
	 * @param listPotentials -
	 *            the list of potentials of the node
	 * @param adittionalProperties -
	 *            the adittionalProperties of the node
	 */
	private Object[][] setParentsStatesInTopArea(Object[][] oldValues,
										ProbNode properties) {

		Object[][] values = oldValues;
		TablePotential tablePotential =
			(TablePotential) getThisPotential( properties.getPotentials());
		ArrayList<Variable> variablesReordered = new ArrayList<Variable>();
		ListIterator<Variable> it =
			getVariables().listIterator( getVariables().size() );
		while (it.hasPrevious()) {
			variablesReordered.add( (Variable) it.previous() );
		}
		/*try {
			tablePotential =
				DiscretePotentialOperations.reorder(
					tablePotential, variablesReordered );
		} catch (NotEnoughMemoryException exception) {
			ExceptionsHandler.handleException(
				exception, "not enougth memory", true );
		}*/
		
		int numColumns = (values.length == 0 ? 0 : values[0].length);
		State[] states;
	
		//07/07/2010 mpalacios
		int accumulateStates = 1;
		int numStates;
		int numberOfVariables=variablesReordered.size();
		for (int row = 0; row < numberOfVariables-1;row++ ){
			numStates = variablesReordered.get(row).getNumStates();
			states = variablesReordered.get(row).getStates();
			//states = tablePotential.getVariable(row).getStates();
			int col=1;
			while (col<numColumns){
				for (State state : states)
				{
					for (int i = 1; i<=accumulateStates; i++){
						values[numberOfVariables-row-2][col] = state.getName();
				        col++;
					}
					
				}     
			}
			accumulateStates *= numStates;
		}

		return values;
	}		
		
	/**
	 * @param values -
	 *            the table that is being modified
	 * @param listPotentials -
	 *            the list of potentials of the node
	 * @param additionalProperties -
	 *            the adittionalProperties of the node
	 */
	private int setNumberOfPostions( ArrayList<Potential> listPotentials) {
		
		int numPositions = 1;
		try {
			for (Variable variable : listPotentials.get( 0 ).getVariables()) {
				numPositions = numPositions * variable.getNumStates();
			}	
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
	 * this method sets the first row with the values of the states of the node
	 * (if it is a node chance) or the name of the variable of the node (if it
	 * is a utility node)
	 * 
	 * 
	 * @param values -
	 *            the table that is being modified
	 * @param listPotentials -
	 *            the list of potentials of the node
	 * @param adittionalProperties -
	 *            the adittionalProperties of the node
	 */
	private Object[][] setNodeStatesInLeftArea(
											Object[][] oldValues,
											ProbNode properties) {

		Object[][] values = oldValues;
	    TablePotential tablePotential =
			(TablePotential) getThisPotential( properties.getPotentials());
		int row = getFirstEditableRow();
		if (properties.getNodeType() == NodeType.UTILITY) {
			values[row][0] = properties.getName();
		} else /*if (properties.getNodeType() == NodeType.CHANCE)*/ {
			// set first column values with the state names
			if (0 < tablePotential.getDimensions()[0]) {
				//int numOfTheState =
				//	tablePotential.getVariable( 0 ).getNumStates() - 1;
				int length = values.length - 2;
				for (State state : tablePotential.getVariable( 0 ).getStates()) {
					values[length--][0] = state.getName();
					//row++;
					//numOfTheState--;
				}
			}
		}
		return values;

	}
	/**
	 * @param values -
	 *            the table that is being modified
	 * @param listPotentials -
	 *            the list of potentials of the node
	 * @param adittionalProperties -
	 *            the adittionalProperties of the node
	 */
	private Object[][] setPotentialDataInCentreArea(Object[][] oldValues,
													ProbNode properties) {

		Object[][] values = oldValues;
		int position = 0;
		int numColumns = (values.length == 0 ? 0 : values[0].length);
		TablePotential tablePotential =
			(TablePotential) getThisPotential( properties.getPotentials());
		
		
		ArrayList<Variable> newOrderVariables = new ArrayList<Variable>();
		ArrayList<Variable> variables = probNode.getPotentials().get(0).getVariables();
		//Collections.reverse(variables); // reorder the variables
		int end=-1;
		if ( variables.size() > 0 ){
			if (!(probNode.getNodeType() == NodeType.UTILITY )){
				newOrderVariables.add(variables.get(0));
				end = 0;
			}
			for (int i = variables.size()-1; i>end; i--){
				newOrderVariables.add(variables.get(i));
				
			}
			
		}
		try {
			tablePotential =
				DiscretePotentialOperations.reorder(
					tablePotential, newOrderVariables );
		} catch (NotEnoughMemoryException exception) {
			//ExceptionsHandler.handleException(
				//exception, "not enougth memory", true );
			logger.fatal("not enougth memory");
		}
		
		/*for (int i = getLastEditableRow(); i >= getFirstEditableRow(); i--) {
			for (int j = numColumns - 1; j >= 1; j--, position++) {
				double value = tablePotential.getTable()[position];
				values[i][j] = value;
			}
		}*/
		int cont = getLastEditableRow();
		/*if (probNode.getNodeType() == NodeType.UTILITY )
			cont = getLastEditableRow()-1;
		else
			cont = getLastEditableRow();*/
		
		for (int j = 1; j <= numColumns - 1; j++) {
			for (int i = cont; i >= getFirstEditableRow(); 
				i--,position++) {
				double value = tablePotential.getValues()[position];
				
				values[i][j] = value;
				
			}
		}
		return values;
	}
	
	

	/**
	 * In the lower left corner area, the last row is reserved in the model for
	 * displaying the name of the variable
	 * 
	 * 
	 * @param values -
	 *            the table that is being modified
	 * @param listPotentials -
	 *            the list of potentials of the node
	 * @param adittionalProperties -
	 *            the adittionalProperties of the node
	 */
	private Object[][] setVariableNameInLowerLeftCornerArea (Object [][]oldValues,
	                                                  ProbNode properties) {
		Object[][] values = oldValues;
		values[getLastEditableRow()+1][0] = properties.getName();
		return values;

	}
      
	/**
	 * In a discretize table model that shows only values (not probabilities),
	 * this area will store the name of the state that is required to display
	 * 
	 * @param values -
	 *            the table that is being modified
	 * @param adittionalProperties -
	 *            the adittionalProperties of the node
	 */
	private Object[][] setVariableStatesInBottomArea (Object [][]oldValues,
	                                                  ProbNode properties) {
		
		Object[][] values = oldValues;
		int position = 0;
		int numColumns = (values.length==0?0:values[0].length);
		TablePotential tablePotential =
			(TablePotential) getThisPotential( properties.getPotentials());
		State[] states = tablePotential.getVariable( 0 ).getStates();
		double max;
	    for (int j = numColumns - 1; j >= 1; j--, position++) {
	    	max = (Double)values[getFirstEditableRow()][j];
	    	values[getLastEditableRow()+1][j] = states[0].getName();
     		for (int i = getFirstEditableRow()+1; i <= getLastEditableRow(); i++) {
			   if (((Double)values[i][j]) > max ) {
			         max = (Double) values[i][j];
			         values[getLastEditableRow()+1][j] = 
			        	     states[i-getFirstEditableRow()].getName();
			   }
     		}
	    }
	    return values;
	}
	
	
	/**
	 * Convert the table with the data in a List of Potentials to be saved
	 * 
	 * @param valuesTable -
	 *            the table with the data
	 * @return a list of Potentials
	 */
	private ArrayList<Potential> convertTableFormatToListPotentials(
											ValuesTable valuesTable) {

		ArrayList<Potential> listPotentials = new ArrayList<Potential>();
		if (getPosition() >= 0) { // it is not a Decision node
			double[] table = new double[ getPosition() ];
			TablePotential tablePotential = null;
			int position = 0;
			for (int j = valuesTable.getColumnCount() - 1; j > 0; j--) {
				for (int i = valuesTable.getLastEditableRow() - 1; 
				         i >= getFirstEditableRow(); i--, position++) {
					table[position] =
						(Double) valuesTable.getModel().getValueAt( i, j );
				}
			}
			tablePotential = new TablePotential(
				getVariables(), PotentialRole.CONDITIONAL_PROBABILITY, table);
			listPotentials.add( tablePotential );
		}
		return listPotentials;
	}

	
	public void addParent(Variable parent) {
		
	}
	
	public void deleteParent(Variable parent) {
	
	}

	public void addState(String state) {
		
	}
	
	public void deleteState(String state) {
	
	}

	public void doUpdateVariableName(String oldName, String newName) {
		if (oldName.equals( this.getVariables().get( 0 ).getName())) {
		     //replace variable name in ArrayListVariables
		     this.getVariables().get( 0 ).setName( newName ); 
		}
		if (oldName.equals(probNode.getPotentials().get(0).getVariables().get(0).getName())) {
		//replace variable name in the TablePotential
		probNode.getPotentials().get( 0 ).getVariables().get( 0 ).setName( newName );
		}
		//replace variable name in the NodePotentialTable 
		if (this.getValuesTable().getVariable() != null ) {
			if (oldName.equals( this.getValuesTable().getVariable().getName()) ) {
				this.getValuesTable().getVariable().setName( newName );
			}
		}
	}
	
	/**
	 * Translates an integer position to the binary equivalent
	 * @param numSignificantPositions
	 * @param position
	 * @return
	 */
	private String[] pos2Bin(int numSignificantPositions, int position) {
		String[] result = new String[numSignificantPositions];
		String result1 = "";
		result1 = Integer.toBinaryString( position );
		int index = result1.length()-1;
		for (int i=numSignificantPositions-1; (i>=0 & index >=0) ; i--,index--) {
			result[i]=result1.substring( index,index+1);
		}
		result = new String().split( result1 );
		System.out.println("result1 ="+result1);
		System.out.print("result=");
		for (int i=0;i<result.length;i++) {
		System.out.print(result[i]);
		}
		return result;
	}
	
	/**
	 * Method to define the specific listeners in this table (not defined in the
	 * common KeyTable hierarchy. This method creates the evidenceCase object when
	 * the user do right click on the table.
	 */
	protected void setTableSpecificListeners() {

		valuesTable.addMouseListener(new java.awt.event.MouseAdapter() {
			
			

			public void mouseClicked(java.awt.event.MouseEvent e) {
				if ( SwingUtilities.isRightMouseButton(e) ){
					int row = valuesTable.rowAtPoint(e.getPoint());
					int col = valuesTable.columnAtPoint(e.getPoint());
					
					if ((row > -1) && (col > 0)) {
						
						if ( getUncertaintyPopup() != null ){
							selectedColumn = col;
							updatePopupMenuOptions();
							getUncertaintyPopup().show( valuesTable, e.getX(),
									e.getY() );
							
						}
					}
				}
			}

			
		});

	}
	
	private void updatePopupMenuOptions(){
		if ( probNode.getPotentials().size() >0 && probNode.getPotentials().get(0)
				instanceof TablePotential ){
			TablePotential tablePotential = (TablePotential)probNode.getPotentials().get(0);
			boolean hasUncertainty = tablePotential.hasUncertainty(getEvidenceCaseFromSelectedColumn());
			if ( hasUncertainty ){
				getUncertaintyPopup().getJComponentActionCommand(ActionCommands.UNCERTAINTY_ASSIGN.toString()).setEnabled(false);
				getUncertaintyPopup().getJComponentActionCommand(ActionCommands.UNCERTAINTY_EDIT.toString()).setEnabled(true);
				getUncertaintyPopup().getJComponentActionCommand(ActionCommands.UNCERTAINTY_REMOVE.toString()).setEnabled(true);
			}else{
				getUncertaintyPopup().getJComponentActionCommand(ActionCommands.UNCERTAINTY_ASSIGN.toString()).setEnabled(true);
				getUncertaintyPopup().getJComponentActionCommand(ActionCommands.UNCERTAINTY_EDIT.toString()).setEnabled(false);
				getUncertaintyPopup().getJComponentActionCommand(ActionCommands.UNCERTAINTY_REMOVE.toString()).setEnabled(false);
			}
		}
	}
	
	/**
	 * This method generates the evidenceCase based on the column selected on the 
	 * <code>valuesTable</code> object.
	 * @param tablePotential
	 * 		The TablePotential object edited
	 * @param col
	 * 		The column selected. Never is 0 , because the column 0 is the states column
	 * @return
	 * 		An evidence case object
	 * @throws InvalidStateException
	 * @throws IncompatibleEvidenceException
	 */
	
	private EvidenceCase getConfiguration(TablePotential tablePotential, int col)
		throws InvalidStateException, IncompatibleEvidenceException {
		Variable variable = null;
		ArrayList<Variable> variables = null;
		EvidenceCase evidence = new EvidenceCase();
		//configuration of all variables
		
		if ( tablePotential.getPotentialRole() == PotentialRole.UTILITY ){
			variable = tablePotential.getUtilityVariable();
			variables = tablePotential.getVariables();
			
		}else if ( tablePotential.getPotentialRole() == PotentialRole.
				CONDITIONAL_PROBABILITY ){
			variable = tablePotential.getVariable(0);
			variables = tablePotential.getVariables();
			variables.remove(0);
		}
		
		int [] parentsConfiguration = new int [variables.size()];
		//Gets the start position of a reordered potential
		int startPosition = UtilStrings.toPositionOnPotentialReordered(variable.
				getNumStates()+ variables.size()-1, col, variable.getNumStates(), 
				variables.size());
		int finalPosition = startPosition + variable.getNumStates() - 1;
		
		//the source variables are reordered
		ArrayList <Variable> reorderedVariables = new ArrayList <Variable>(); 
		if (!(tablePotential.getPotentialRole() == PotentialRole.UTILITY)){
			reorderedVariables.add(variable);
		}
			
		for (int i=variables.size()-1;i >= 0 ; i-- ){
			reorderedVariables.add(variables.get(i));
		}
		//gets the potential with variables and values table reordered
		TablePotential reorderedTablePotential = null;
		try {
			reorderedTablePotential = DiscretePotentialOperations.reorder( 
					tablePotential,reorderedVariables);
		} catch (NotEnoughMemoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		}
		//gets the configuration selected
		int [] configuration = reorderedTablePotential.getConfiguration(
				startPosition);
		 
		//back to the original order of variables configuration
		//first value of configuration matches the value of the first variable
		//in inverse order because the potential visualization is in inverse order
		int j=0;
		int end=0;
		if (tablePotential.getPotentialRole() == PotentialRole.UTILITY){
			end=-1;
		}
		for (int i = configuration.length-1; i>end; i--){
			parentsConfiguration [j++] = configuration [ i ];  
		}
		//Gets the evidence
		j = 0;
		Finding finding;
		for (Variable var:variables){
			finding = new Finding(var, parentsConfiguration[j] );
			evidence.addFinding(finding);
			j++;
		}
	 	
		return evidence;
	}
	
	public EvidenceCase getEvidenceCaseFromSelectedColumn(){
		
		EvidenceCase evi=null;
		try {
			evi = getConfiguration((TablePotential)
				probNode.getPotentials().get( 0 ),
				selectedColumn);
		} catch (InvalidStateException e) {
			// TODO Auto-generated catch block
			System.err.println( e.getMessage() );
		} catch (IncompatibleEvidenceException e) {
			// TODO Auto-generated catch block
			System.err.println( e.getMessage() );
		}
		return evi;
	}
	
	
	/**
	 * Creates and shows the UncertainValuesDialog object 
	 * @throws WrongCriterionException 
	 */
	public void showUncertaintyDialog() throws WrongCriterionException {
		//Generates the evidenceCase based on the column
		//selected on the JTable object
		evidenceCase = getEvidenceCaseFromSelectedColumn();
		
		try {
			UncertainValuesDialog uncertDialog = new UncertainValuesDialog(
					Utilities.getOwner(this), evidenceCase,  
					(TablePotential)probNode.getPotentials().get( 0 ));
			int button = uncertDialog.requestUncertainValues();
			 if (button == UncertainValuesDialog.OK_BUTTON){
				 UncertainValuesEdit uncertEdit = new UncertainValuesEdit(
						 probNode,uncertDialog.getUncertainColumn(),uncertDialog.getValuesColumn(),uncertDialog.getPosBase(),selectedColumn,uncertDialog.isChanceVariable());  
				 
				 try{
				 	probNode.getProbNet().getPNESupport().announceEdit(uncertEdit);
					probNode.getProbNet().getPNESupport().doEdit(uncertEdit);
					
					if ( selectedColumn > 0 ){ 
						( (ValuesTableCellRenderer)	getValuesTable().getDefaultRenderer(
							 Double.class)).setMark(selectedColumn-1) ;
					 		getValuesTable().repaint();
					}
				 }catch (ConstraintViolationException e) {
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
					} catch (NonProjectablePotentialException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						JOptionPane.showMessageDialog(this, messageStringResource
								.getString( e.getMessage() ),
							messageStringResource.getString( e.getMessage() ),
							JOptionPane.ERROR_MESSAGE );
					}
			 
			 
			 }
			
			
			
		} catch (NotEnoughMemoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		} 
		
	}
	
	
	/**
	 * Method for removing the uncertain values for a certain configuration
	 * @throws WrongCriterionException 
	 * @throws NotEnoughMemoryException 
	 */
	public void removeUncertainty() throws NotEnoughMemoryException, WrongCriterionException{
		
		evidenceCase = getEvidenceCaseFromSelectedColumn();
		UncertainValuesRemoveEdit uncertEdit = new UncertainValuesRemoveEdit(probNode,evidenceCase);
		
		 try{
			 	probNode.getProbNet().getPNESupport().announceEdit(uncertEdit);
				probNode.getProbNet().getPNESupport().doEdit(uncertEdit);
				
				if ( selectedColumn > 0 ){ 
					( (ValuesTableCellRenderer)	getValuesTable().getDefaultRenderer(
						 Double.class)).unMark(selectedColumn-1) ;
				 		getValuesTable().repaint();
				}
				
			 }catch (ConstraintViolationException e) {
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
				} catch (NonProjectablePotentialException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				}
				
			
	}
	
	
	/**
	 * This method initializes uncertaintyPopup.
	 * 
	 * @return the node popup menu.
	 */
	private UncertaintyPopup getUncertaintyPopup() {

		if (uncertaintyPopup == null) {
			uncertaintyPopup = new UncertaintyPopup(this);
			uncertaintyPopup.setName("uncertaintyPopup");
		}
		return uncertaintyPopup;
	}
	

	
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if (actionCommand.equals( ActionCommands.UNCERTAINTY_ASSIGN )) {
			try {
				showUncertaintyDialog();
			} catch (WrongCriterionException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this, messageStringResource
						.getString( e1.getMessage() ),
					messageStringResource.getString( e1.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			}
		}
		else if (actionCommand.equals( ActionCommands.UNCERTAINTY_EDIT )) {
			try {
				showUncertaintyDialog();
			} catch (WrongCriterionException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this, messageStringResource
						.getString( e1.getMessage() ),
					messageStringResource.getString( e1.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			}
		}
		else if (actionCommand.equals( ActionCommands.UNCERTAINTY_REMOVE )) {
			try {
				removeUncertainty();
			} catch (NotEnoughMemoryException e1) {
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
				
		
	}

	

}
