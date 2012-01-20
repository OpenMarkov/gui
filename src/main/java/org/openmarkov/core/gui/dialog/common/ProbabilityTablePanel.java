/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import org.apache.log4j.Logger;
import org.openmarkov.core.gui.component.ValuesTable;
import org.openmarkov.core.gui.component.ValuesTableModel;
import org.openmarkov.core.gui.loader.element.IconLoader;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.gui.menutoolbar.menu.PopupMenuFactory;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;

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
	public class ProbabilityTablePanel  extends PotentialPanel implements ActionListener {

		/**
		 * Static field for serializable class.
		 */
		private static final long serialVersionUID = 6257314234781632512L;

		/**
		 * Panel to scroll the table.
		 */
		protected JScrollPane valuesTableScrollPane = null;

		/**
		 * JTable where show the values.
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
		protected int firstEditableRow = -1;
		/**
		 * last editable row (only for temporal storage)
		 */
		protected int lastEditableRow = -1;
		
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
		
		

		protected ProbNode probNode;
		/**
		 * The popupMenu that appears when there is a click on the valuesTable Object 
		 */
		protected PopupMenuFactory popupMenuFactory;

		protected EvidenceCase evidenceCase ;
		/**
		 * index of the column selected in valuesTable
		 */
		protected int selectedColumn = -1;

		
		
		private Logger logger;

		/**
		 * this is a default constructor with no construction parameters
		 * @wbp.parser.constructor
		 */
		public ProbabilityTablePanel(ProbNode probNode) {

        this (probNode, new String[] {"id", "states", "values"}, new Object[][] {new Object[] {0,
                null, 0}}); // default init
		}

		/**
		 * This is the default constructor
		 * 
		 * @param newColumns
		 *            array of texts that appear in the header of the columns.
		 * @param newData
		 *            content of the cells.
		 */
		public ProbabilityTablePanel(ProbNode probNode, String[] newColumns, 
				Object[][] newData) {

			stringResource =
				StringResourceLoader.getUniqueInstance().getBundleButtons();
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
					.setName( "ProbabilityTablePanel.valuesTableScrollPane" );
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
		 * This method initializes tableModel.
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
		 * @param listPotentials the listPotentials to set
		 */
		public void setListPotentials(ArrayList<Potential> listPotentials) {
		
			this.listPotentials = listPotentials;
		}

	
			
		/**
		 * Set the Base index for the coordinates in the table related to the 
		 * Potential of the variable of this node
		 * 
		 * @param value - the new base index for coordinates in the table
		 */
		protected void setBaseIndexForCoordinates (int value) {
			this.baseIndexForCoordinates = value;
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

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	
	}



