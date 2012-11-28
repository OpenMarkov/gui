/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import org.apache.log4j.Logger;
import org.openmarkov.core.gui.loader.element.IconLoader;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.gui.menutoolbar.menu.ContextualMenuFactory;
import org.openmarkov.core.model.network.EvidenceCase;
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
	 * @author maryebra
	 * @version 1.0 jlgozalo
	 */
	public abstract class ProbabilityTablePanel  extends PotentialPanel implements ActionListener {

		/**
		 * Static field for serializable class.
		 */
		private static final long serialVersionUID = 6257314234781632512L;
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
		protected List<Variable> variables = null;
		
		/**
		 * list of potentials for the variable
		 */
		protected List<Potential> listPotentials = null;
		
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
		
		

		//private ProbNode probNode;
		/**
		 * The contextualMenu that appears when there is a click on the valuesTable Object 
		 */
		protected ContextualMenuFactory contextualMenuFactory;

		protected EvidenceCase evidenceCase ;
		/**
		 * index of the column selected in valuesTable
		 */
		protected int selectedColumn = -1;

		private JLabel jLabelNodeRelationComment;
		private CommentHTMLScrollPane commentHTMLScrollPaneNodeProbsComment = null;
		/**
		 * Dialog string resource.
		 */
		private StringResource dialogStringResource;
		
		
		private Logger logger;

		/**
		 * this is a default constructor with no construction parameters
		 * @wbp.parser.constructor
		 */
		public ProbabilityTablePanel() {

        this ( new String[] {"id", "states", "values"}, new Object[][] {new Object[] {0,
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
		public ProbabilityTablePanel( String[] newColumns, 
				Object[][] newData) {

			stringResource =
				StringResourceLoader.getUniqueInstance().getBundleButtons();
			iconLoader = new IconLoader();
			columns = newColumns.clone();
			data = newData.clone();
			this.logger = Logger.getLogger(PotentialsTablePanel.class);
			//this.probNode = probNode;
			 // table is modifiable
			//setLayout(new BorderLayout());
			//add(getCommentHTMLScrollPaneNodeDefinitionComment(),BorderLayout.SOUTH);
			//initialize();
			//showValuesTable( true );
			repaint();
		}

		/**
		 * This method initialises this instance.
		 */
	/*	private void initialize() {
			/*setBorder( new LineBorder( UIManager.getColor( "Table.dropLineColor" ),1, false ) );
			
				final GroupLayout groupLayout = new GroupLayout( (JComponent) this );
				
				groupLayout.setHorizontalGroup( 
					groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING )
					.addGroup(
							groupLayout.createSequentialGroup().addComponent(
						getValuesTableScrollPane(), GroupLayout.DEFAULT_SIZE, 474,
						Short.MAX_VALUE ) ) );
				
				groupLayout.setVerticalGroup( 
					groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING )
					.addGroup(GroupLayout.Alignment.TRAILING,
							groupLayout.createSequentialGroup().addComponent(
						getValuesTableScrollPane(), GroupLayout.DEFAULT_SIZE, 349,
						Short.MAX_VALUE ) ) );
				
				setLayout( groupLayout );
				setAutoscrolls( true );
				//setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
				//setCellRenderers();
		
			setLayout(new BorderLayout());
			
		}*/
		
		/**
		 * @return label for the node relation comment
		 */
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
		/**
		 * This method initializes commentHTMLScrollPaneNodeDefinitionComment
		 * 
		 * @return a new comment HTML scroll pane.
		 */
	/*	protected CommentHTMLScrollPane getCommentHTMLScrollPaneNodeDefinitionComment() {

			if (commentHTMLScrollPaneNodeProbsComment == null) {
				commentHTMLScrollPaneNodeProbsComment = new CommentHTMLScrollPane();
				commentHTMLScrollPaneNodeProbsComment.setName( 
						"commentHTMLScrollPaneNodeProbsComment" );
			}
			return commentHTMLScrollPaneNodeProbsComment;
		}*/

		/**
		 * @return the showAllParameters
		 */
		public boolean isShowAllParameters() {

			return showAllParameters;
		}

		/**
		 * @return the showProbabilitiesValues
		 */
		public boolean isShowProbabilitiesValues() {

			return showProbabilitiesValues;
		}

	

		/**
		 * @return the showTPCvalues
		 */
		public boolean isShowTPCvalues() {

			return showTPCvalues;
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
		protected List<Variable> getVariables() {
		
			return variables;
		}

		
		/**
		 * @param variables the variables to set
		 */
		
		/*protected void setVariables(ArrayList<Variable> variables) {
			//TODO update this statement, when constructor of this class with 
			//potential as parameter is implemented
			if (probNode != null && probNode.getNodeType() == NodeType.UTILITY){
				this.variables = new ArrayList<Variable>();
				this.variables.add(probNode.getVariable());
				for (Variable variable: variables)
					this.variables.add(variable);
			}else
			
			this.variables = variables;
			
		}*/
		
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

		//@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		public Object[][] getData() {
			return this.data;
		}
	
	}



