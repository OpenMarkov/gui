/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog.node;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.openmarkov.core.exception.ExceptionUncertainValuesDialogEdition;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.dialog.common.OkCancelHorizontalDialog;
import org.openmarkov.core.gui.loader.element.IconLoader;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.modelUncertainty.ComplementFamily;
import org.openmarkov.core.model.network.modelUncertainty.DirichletFamily;
import org.openmarkov.core.model.network.modelUncertainty.FamilyDistribution;
import org.openmarkov.core.model.network.modelUncertainty.ProbDensFunction;
import org.openmarkov.core.model.network.modelUncertainty.Tools;
import org.openmarkov.core.model.network.modelUncertainty.TypeProbDensityFunction;
import org.openmarkov.core.model.network.modelUncertainty.UncertainValue;
import org.openmarkov.core.model.network.potential.TablePotential;

public class UncertainValuesDialog extends OkCancelHorizontalDialog {

	public class DistributionsTableListener implements TableModelListener {

		
		public void tableChanged(TableModelEvent e) {

			
		}

	}


	public class ConfigurationTableModel extends DefaultTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ConfigurationTableModel(String[][] initialData,
				String[] namesColumnsConfigurationTable) {
			super(initialData,namesColumnsConfigurationTable);
		}
		
		public boolean isCellEditable(int arg0, int arg1) {
		
			return false;
		}


	}


	public class DistributionsTableModel extends DefaultTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public DistributionsTableModel(Object[][] initialData,
				String[] namesColumnsDistributionsTable) {
			super(initialData,namesColumnsDistributionsTable);
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return (col>0);
		}

	}
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private StringResource messageStringResource;
	/**
	 * Remove button.
	 */
	//private JButton jButtonRemove = null;
	
	//Components related to the configuration box
	ConfigurationTableModel configurationTableModel;
	
	JTable configurationTable;

	private JScrollPane configurationScrollPane;

	private Container configurationPanel;

	private JLabel labelConfiguration;
	
	//Components related to the distributions box
	DistributionsTableModel distributionsTableModel;
	
	JTable distributionsTable;

	private JScrollPane distributionsScrollPane;

	private JLabel labelDistributions;

	private JPanel distributionsPanel;
	
	Variable variable;
	
	String[] allowedStringsDistributions;
	
	boolean isChanceVariable;	
	/**
	 * Dialogs string resource.
	 */
	protected StringResource dialogStringResource = null;
	
	//Array of uncertain values
	ArrayList<UncertainValue> uncertainColumn;
	//Array of doubles calculated from uncertainColum by taking the mean value
	ArrayList<Double> valuesColumn;

	public ArrayList<Double> getValuesColumn() {
		return valuesColumn;
	}



	public ArrayList<UncertainValue> getUncertainColumn() {
		return uncertainColumn;
	}






	//Base position for storing the array of uncertain values in the table potential
	private int posBase;

	
	

	
		
	
	public int getPosBase() {
		return posBase;
	}



	/**
	 * @param owner
	 * @param variable
	 * @param configuration
	 * @param uncertainValues
	 * @throws NotEnoughMemoryException 
	 * @throws WrongCriterionException 
	 * @wbp.parser.constructor
	 */
	public UncertainValuesDialog(Window owner,EvidenceCase 
			configuration,TablePotential potential) throws NotEnoughMemoryException, WrongCriterionException {
		super(owner);
		
	
		stringResource =
			StringResourceLoader.getUniqueInstance().getBundleDialogs();

			messageStringResource =	
					StringResourceLoader.getUniqueInstance().getBundleMessages();

						
		
		isChanceVariable = !(potential.isUtility());
		variable = isChanceVariable?potential.getVariable(0):potential.getUtilityVariable();
		
	
		posBase = getPositionBaseUncertainValue(potential,configuration);
		
			
		setResizable(true);
		//getComponentsPanel().setLayout(new BorderLayoutxLayout(getComponentsPanel(), BoxLayout.X_AXIS));
		
		JPanel componentsPanel = getComponentsPanel();
		

		//Panel of Configuration
		configurationPanel = new JPanel();
		configurationPanel.setLayout(new BorderLayout());
		
		componentsPanel.add(configurationPanel,BorderLayout.NORTH);
		
		labelConfiguration = new JLabel("Configuration");
		configurationPanel.add(labelConfiguration,BorderLayout.NORTH);
		
		configurationScrollPane = new JScrollPane();
		configurationPanel.add(configurationScrollPane,BorderLayout.CENTER);
		
				
		fillConfigurationTableModel(configuration);
			
		configurationTable = new JTable(configurationTableModel);
		configurationTable
				.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
		configurationScrollPane.getViewport().add(configurationTable);
		int width = 100;
		int height = 50;
		//configurationPanel.setSize(width, 100);
		configurationTable.setSize(width, height);
		//configurationTable.setBounds(0, 0, width, 100);
		configurationTable.setSize(width, height);
		
		//Panel of distributions
		
		distributionsPanel = new JPanel();
		distributionsPanel.setLayout(new BorderLayout());
		componentsPanel.add(distributionsPanel,BorderLayout.CENTER);
		
		labelDistributions = new JLabel("Distributions");
		distributionsPanel.add(labelDistributions,BorderLayout.NORTH);
		
		distributionsScrollPane = new JScrollPane();
		distributionsPanel.add(distributionsScrollPane,BorderLayout.CENTER);
		
	/*	updateUncertainValuesCheckBox = new JCheckBox("Update reference values");
		distributionsAndUpdatePanel.add(updateUncertainValuesCheckBox,BorderLayout.SOUTH);*/
		
		fillDistributionsTableModel(variable,configuration,potential);
	
		distributionsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
		//distributionsTable.setAutoResizeMode(javax.swing.JTable.HEIGHT);
		distributionsScrollPane.getViewport().add(distributionsTable);
		//distributionsPanel.setSize(width, 50);
		distributionsPanel.setSize(width, height);
		//distributionsTable.setBounds(0, 0, width, 150);
		distributionsTable.setSize(width, height);
		//distributionsTable.setSize(width, height);
		
		distributionsTable.getModel().addTableModelListener(
	            new DistributionsTableListener());

		
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
	
	
	public int requestUncertainValues(){
		
		
		setVisible(true);
		
		return this.selectedButton;
		
	}
	
			
	private int getPositionBaseUncertainValue(TablePotential potential,
			EvidenceCase configuration) {

		int[] coordinates;
		int sizeCoordinates;
		int pos;
		
		int sizeEvi = configuration.getFindings().size();
		
		sizeCoordinates = sizeEvi + (isChanceVariable?1:0);
		coordinates = new int[sizeCoordinates];
		
		ArrayList<Variable> varsTable = potential.getVariables();
		
		int startLoop;
		if (isChanceVariable){
			coordinates[0]=0;
			startLoop = 1;
		}
		else{
			startLoop = 0;
		}
		for (int i=startLoop;i<sizeCoordinates;i++){
			coordinates[i]=configuration.getFinding(varsTable.get(i)).getStateIndex();
		}
		
		pos = potential.getPosition(coordinates);
		return pos;
	}
	
	


	private void fillDistributionsTableModel(Variable variable,
			EvidenceCase configuration, TablePotential potential) throws NotEnoughMemoryException, WrongCriterionException {
				
		UncertainValue[] uncertainTable = potential.getUncertainTable();
		
		TablePotential auxProjected = potential.tableProject(configuration, null).get(0);
		
		UncertainValue[] auxUncertainTable = auxProjected.getUncertainTable();
		//GEt the table of uncertain values
		if (!hasUncertainValues(auxUncertainTable)){
			//Case assign
			uncertainTable = createExactUncertainValuesFrom(auxProjected);
		}
		else{
			//Case edit
			uncertainTable = auxProjected.getUncertainTable();
		}
		
		//Fill the table for the dialog
		int numColumnsTable = 4;
		int indexTypePDF=1;
		String[] namesColumnsDistributionsTable = new String[numColumnsTable];
		//String colPrefix = "UncertainValuesDialog.DistributionsTable.Columns.";
		//namesColumnsDistributionsTable[0]=dialogStringResource.getString(colPrefix+"State.Label");
		//namesColumnsDistributionsTable[1]=dialogStringResource.getString(colPrefix+"Distribution.Label");
		//namesColumnsDistributionsTable[2]=dialogStringResource.getString(colPrefix+"Parameters.Label");
		//namesColumnsDistributionsTable[3]=dialogStringResource.getString(colPrefix+"Name.Label");
		namesColumnsDistributionsTable[0]="State";
		namesColumnsDistributionsTable[1]="Distribution";
		namesColumnsDistributionsTable[2]="Parameters";
		namesColumnsDistributionsTable[3]="Name";
		//String[] stringsDistributions = TypeProbDensityFunction.getStringsValues();
		allowedStringsDistributions = TypeProbDensityFunction.getAllowedStringsValues(isChanceVariable);
		
		State[] states = variable.getStates();
		int numStates = states.length;
		Object[][] initialData = new Object[numStates][numColumnsTable];
		
		JComboBox auxCombo = new JComboBox(allowedStringsDistributions);
				
		int lastPosStates = numStates-1;
				
		for (int i=0;i<numStates;i++){
			UncertainValue uncertainValue = uncertainTable[i];
			int iPosInitialData = lastPosStates-i;
			initialData[iPosInitialData][0]=states[i].getName();
			//int ordinal = uncertainValue.getProbDensityFunction().getType().ordinal();
			initialData[iPosInitialData][indexTypePDF]= uncertainValue.getProbDensityFunction().getType().toString();
			initialData[iPosInitialData][2]=uncertainValue.getArguments();
			initialData[iPosInitialData][3]=uncertainValue.getName();
		}
		
		distributionsTableModel = new DistributionsTableModel(initialData,namesColumnsDistributionsTable);
				
		distributionsTable = new JTable(distributionsTableModel);
		
		//Model for the column "Distribution"
		TableColumnModel columnModel = distributionsTable.getColumnModel();
		TableColumn column = columnModel.getColumn(indexTypePDF);
		column.setCellEditor(new DefaultCellEditor(auxCombo));
		columnModel.getColumn(0).setCellEditor(null);
		
			
	}

	/**
	 * @param auxProjected Table potential which has no uncertain values. Its values are used for creating the uncertain values
	 * @return An array of uncertain values
	 */
	private UncertainValue[] createExactUncertainValuesFrom(
			TablePotential auxProjected) {
		UncertainValue[] uncertainTable;
		double[] tableProjected = auxProjected.getValues();
		uncertainTable = new UncertainValue[tableProjected.length];
		for (int i=0;i<tableProjected.length;i++){
			uncertainTable[i]= new UncertainValue(tableProjected[i]);
		}
		return uncertainTable;
	}

	
	public static boolean hasUncertainValues(UncertainValue[] auxUncertainTable) {
		boolean hasUncertainValues;
		if ((auxUncertainTable==null)||(auxUncertainTable.length==0)){
			hasUncertainValues = false;
		}
		else{
			hasUncertainValues = false;
			for (int i=0;(i<auxUncertainTable.length)&&!hasUncertainValues;i++){
				hasUncertainValues = (auxUncertainTable[i]!=null);
			}
		}
		return hasUncertainValues;
	}

	private void fillConfigurationTableModel(EvidenceCase configuration) {
		int numFindings;
		String[] namesColumnsConfigurationTable = new String[2];
		//String colPrefix = "UncertainValuesDialog.ConfigurationTable.Columns.";
		//namesColumnsConfigurationTable[0]=dialogStringResource.getString(colPrefix+"Variable.Label");
		//namesColumnsConfigurationTable[1]=dialogStringResource.getString(colPrefix+"State.Label");
		namesColumnsConfigurationTable[0]="Variable";
		namesColumnsConfigurationTable[1]="State";
		
		numFindings = configuration.getNumberOfFindings();
		String[][] initialData = new String[numFindings][2];
		ArrayList<Finding> findings = configuration.getFindings();
		int lastIndexFindings;
		lastIndexFindings = numFindings-1;
		for (int i=0;i<numFindings;i++){
			int positionInInitialData = lastIndexFindings-i;
			Finding auxFinding = findings.get(i);
			Variable variable = auxFinding.getVariable();
			initialData[positionInInitialData][0]=variable.getName();
			initialData[positionInInitialData][1]=variable.getStateName(auxFinding.getStateIndex());
		}
		
		configurationTableModel = new ConfigurationTableModel(initialData,namesColumnsConfigurationTable);
	}



		/**
		 * This method initializes this instance.
		 */
		private void initialize() {

		
			// setSize(550, 310);
			setName("UncertainValuesDialog");
			//setTitle(dialogStringResource.getString("UncertainValuesDialog.Title.Label")+variable);
			setTitle("Title"+variable);
			iconLoader = new IconLoader();
			configureButtonsPanel();
			setDefaultButton(getJButtonOK());
			quitIconsOfButtons();
			this.getJButtonOK().setText("Accept");
			this.getJButtonCancel().setText("Cancel");
			//this.jButtonRemove.setText("Remove");
			pack();
			
		}
		
		/**
		 * This method carries out the actions when the user press the Ok button
		 * before hide the dialog.
		 * 
		 * @return true if the dialog box can be closed.
		 */
		protected boolean doOkClickBeforeHide() {
			
			ArrayList<UncertainValue> arrayUncertain = readDataFromDistributionModel();

			boolean verify;
			
			verify = doesVerifyLocalConstraintsUncertainty(arrayUncertain);
			
			if (verify){
				
						
				if (isChanceVariable){
					if (!doVerifyGlobalConstraintUncertainty(arrayUncertain)){
					//System.out.println("Distribution "+typeDistrib+" does not verify the constraints associated to its domain.");
						verify = false;
					}
				}
				
			}
			
			if (verify){
				uncertainColumn = reverse(arrayUncertain);
				valuesColumn = calculateReferenceValues();
				
			}
			
			return verify;
			
		};

		
		
		
		 private ArrayList<UncertainValue> reverse(
				ArrayList<UncertainValue> array) {
			ArrayList<UncertainValue> rev;
			rev = new ArrayList<UncertainValue>();
			for (int i=array.size()-1;i>=0;i--){
				rev.add(array.get(i));
			}
			return rev;
		}



		private ArrayList<Double> calculateReferenceValues() {
			double[] refValues;
			ArrayList<UncertainValue> otherUncertain;
			ArrayList<Integer> indexComp,indexDir,indexOther;
			
			indexComp = new ArrayList<Integer>();
			indexDir = new ArrayList<Integer>();
			indexOther = new ArrayList<Integer>();
			
			refValues = new double[uncertainColumn.size()];
			
			ComplementFamily comp = (ComplementFamily) extractFamilyDistribution(uncertainColumn,TypeProbDensityFunction.COMPLEMENT);
			DirichletFamily dir = (DirichletFamily) extractFamilyDistribution(uncertainColumn,TypeProbDensityFunction.DIRICHLET);
			
			int sizeUncertain = uncertainColumn.size();
			otherUncertain = new ArrayList<UncertainValue>();
			for (int i=0;i<sizeUncertain;i++){
				UncertainValue aux = uncertainColumn.get(i);
				TypeProbDensityFunction type = aux.getProbDensityFunction().getType();
				switch(type){
				case COMPLEMENT:
					indexComp.add(i);
					break;
				case DIRICHLET:
					indexDir.add(i);
					break;
				default:
					indexOther.add(i);
				}
			}
			
		
			
			otherUncertain = getElementsFromIndexes(uncertainColumn,indexOther);
			//Process other
			FamilyDistribution other = new FamilyDistribution(otherUncertain);
			
			double[] meanOther = other.getMean();
			
			placeInArray(refValues,indexOther,meanOther);
			
			//Process Dirichlet
			double[] meanDir = dir.getMean();
			
			placeInArray(refValues,indexDir,meanDir);
	
			
			//Process complements
			double massForComp = 1.0-(Tools.sum(meanOther)+Tools.sum(meanDir));
			
			comp.setProbMass(massForComp);
			double[] meanComp = comp.getMean();
		
			placeInArray(refValues,indexComp,meanComp);
				
			ArrayList<Double> ref;
			ref = new ArrayList<Double>();
			for (int i=0;i<refValues.length;i++){
				ref.add(refValues[i]);
			}
			return ref;	
			
		}



		private static void placeInArray(double[] refValue,
				ArrayList<Integer> indexes, double[] x) {
			for (int i=0;i<indexes.size();i++){
				refValue[indexes.get(i)]= x[i];
			}
			
		}



		private static ArrayList<UncertainValue> getElementsFromIndexes(ArrayList<UncertainValue> column,
				ArrayList<Integer> index) {
			ArrayList<UncertainValue> array;
			array = new ArrayList<UncertainValue>();
			for (Integer aux:index){
				array.add(column.get(aux));
			}
			return array;
		}



		private FamilyDistribution extractFamilyDistribution(ArrayList<UncertainValue> uncertainColumn, TypeProbDensityFunction type) {
			ArrayList<UncertainValue> siblings = getUncertainValuesOfType(uncertainColumn,type);
			return FamilyDistribution.constructNewFamilyDistributions(siblings,type);
			
		}

		



		


		private boolean doesVerifyLocalConstraintsUncertainty(ArrayList<UncertainValue> arrayUncertain) {
					 
			 boolean verify = true;
			 
			 //Verify individual constraints for each Uncertain Value
			for (int i=0;i<arrayUncertain.size()&&verify;i++){
				UncertainValue auxUncertain = arrayUncertain.get(i);
				String typeDistrib = auxUncertain.getProbDensityFunction().getType().toString();
				if (!auxUncertain.isCorrectArgumentsInProbDensFunction()){
					try {
						String message = "Incorrect number of parameters in distribution "+typeDistrib.toString();
						throw new ExceptionUncertainValuesDialogEdition(message);
					} catch (ExceptionUncertainValuesDialogEdition e) {
						
					}
					verify = false;
				}
				else{
					if (!auxUncertain.doParametersVerifyDomainConstraint(isChanceVariable)){
						try {
							String message = "Distribution "+typeDistrib+" does not verify the constraints associated to its domain.";
							throw new ExceptionUncertainValuesDialogEdition(message);
						} catch (ExceptionUncertainValuesDialogEdition e) {
						
						}
						verify = false;
					}
				}
			}
			return verify;
			
			
		}
		
		



		private boolean doVerifyGlobalConstraintUncertainty(
				ArrayList<UncertainValue> arrayUncertain) {
			
			FamilyDistribution family = new FamilyDistribution(arrayUncertain);
					return (doVerifyRule1(family)
							&&doVerifyRule2(family)
							&&doVerifyRule3(family));
		}


		
		/* If one of the distributions is Exact, Range, or Triangular, then:
			 • all the others must be either exact, or range, or triangular, or complement;
			 • at least one of the others must be Complement;
			 • the sum of the maxima of all the distributions (different from Complement) cannot
			 be greater than 1.*/

		private boolean doVerifyRule1(FamilyDistribution family) {
			
			int totalSizeFamily;
			boolean verify;
			int sizeExact;
			ArrayList<UncertainValue> exactRangeOrUncertain;
			
			ArrayList<TypeProbDensityFunction> rangeOrTriangTypes;
			
			rangeOrTriangTypes = new ArrayList<TypeProbDensityFunction>();
			rangeOrTriangTypes.add(TypeProbDensityFunction.RANGE);
			rangeOrTriangTypes.add(TypeProbDensityFunction.TRIANGULAR);
			
			ArrayList<UncertainValue> uncertainFamily = family.getFamily();
			
			ArrayList<UncertainValue> exactUncertain = getUncertainValuesOfType(uncertainFamily,TypeProbDensityFunction.EXACT);
			
			
			
			
			
			totalSizeFamily = uncertainFamily.size();
			ArrayList<UncertainValue> rangeOrTriangUncertain = getUncertainValuesOfTypes(uncertainFamily,rangeOrTriangTypes);
			int sizeRangeOrTriang = rangeOrTriangUncertain.size();
			sizeExact = exactUncertain.size();
			if ((sizeRangeOrTriang>0)&&thereAreExactValuesGreaterThanZero(exactUncertain)){
				int numComplement = getUncertainValuesOfType(uncertainFamily,TypeProbDensityFunction.COMPLEMENT).size();
				exactRangeOrUncertain = (ArrayList<UncertainValue>) rangeOrTriangUncertain.clone();
				exactRangeOrUncertain.addAll(exactUncertain);
				verify = ((numComplement>0)
							&&(sizeExact+sizeRangeOrTriang+numComplement==totalSizeFamily))
							&&(Tools.sum(new FamilyDistribution(exactRangeOrUncertain).getMaximum())<=1.0);
				
			}
			else{
				verify = true; 
			}
			if (!verify){
				try {
					String message = "Rule 1 of the specification of sensitivity analysis in ProbModelXML has been violated. Please, check the distributions and its parameteres.";
					throw new ExceptionUncertainValuesDialogEdition(message);
				} catch (ExceptionUncertainValuesDialogEdition e) {
				
				}
				
			}
			return verify;
		}



		private static ArrayList<UncertainValue> getUncertainValuesOfTypes(ArrayList<UncertainValue> arrayUncertain, 
				ArrayList<TypeProbDensityFunction> types) {
			
			ArrayList<UncertainValue> selected = new ArrayList<UncertainValue>();
			
			for (UncertainValue aux:arrayUncertain){
				TypeProbDensityFunction auxType = aux.getProbDensityFunction().getType();
				boolean isInTypes=false;
				for(int i=0;(i<types.size())&&!isInTypes;i++){
					isInTypes = (auxType == types.get(i));
				}
				if (isInTypes){
					selected.add(aux);
				}
			}
			return selected;
		}
		
		private static boolean thereAreExactValuesGreaterThanZero(ArrayList<UncertainValue> arrayUncertain){ 
			boolean thereAre = false;
			for (int i=0;(i<arrayUncertain.size())&&!thereAre;i++){
				UncertainValue aux = arrayUncertain.get(i);
				ProbDensFunction probDensityFunction = aux.getProbDensityFunction();
				thereAre = (probDensityFunction.getType() == TypeProbDensityFunction.EXACT)
					 && probDensityFunction.getMean()>0;
			}
			return thereAre;
		}
		
		
		/**
		 * @param arrayUncertain
		 * @param types
		 * @return
		 */
		private static int[] getIndexesUncertainValuesOfTypes(ArrayList<UncertainValue> arrayUncertain, 
				ArrayList<TypeProbDensityFunction> types) {
			
			ArrayList<Integer> indexes = new ArrayList<Integer>();
			
			for (int i=0;i<arrayUncertain.size();i++){
				UncertainValue aux = arrayUncertain.get(i);
				TypeProbDensityFunction auxType = aux.getProbDensityFunction().getType();
				boolean isInTypes=false;
				for(int j=0;(j<types.size())&&!isInTypes;j++){
					isInTypes = (auxType == types.get(j));
				}
				if (isInTypes){
					indexes.add(i);
				}
			}
			int numIndexesOfTypes = indexes.size();
			int []intIndexes = new int[numIndexesOfTypes];
			
			for (int i=0;i<numIndexesOfTypes;i++){
				intIndexes[i] = indexes.get(i);
			}
			return intIndexes;
		}
		
		public static int[] getIndexesUncertainValuesNotInTypes(ArrayList<UncertainValue> arrayUncertain, 
				ArrayList<TypeProbDensityFunction> types) {
			
			ArrayList<Integer> indexes = new ArrayList<Integer>();
			
			for (int i=0;i<arrayUncertain.size();i++){
				UncertainValue aux = arrayUncertain.get(i);
				TypeProbDensityFunction auxType = aux.getProbDensityFunction().getType();
				boolean notInTypes = true;
				for(int j=0;(j<types.size())&&notInTypes;j++){
					notInTypes = !(auxType == types.get(j));
				}
				if (notInTypes){
					indexes.add(i);
				}
			}
			int numIndexesOfTypes = indexes.size();
			int []intIndexes = new int[numIndexesOfTypes];
			
			for (int i=0;i<numIndexesOfTypes;i++){
				intIndexes[i] = indexes.get(i);
			}
			return intIndexes;
		}
		
		
		public static int[] getIndexesUncertainValuesOfType(ArrayList<UncertainValue> arrayUncertain, 
				TypeProbDensityFunction type){
			ArrayList<TypeProbDensityFunction> aux = new ArrayList<TypeProbDensityFunction>();
			aux.add(type);
			return getIndexesUncertainValuesOfTypes(arrayUncertain,aux);
		}
		
		

		private static ArrayList<UncertainValue> getUncertainValuesOfType(ArrayList<UncertainValue> arrayUncertain, 
				TypeProbDensityFunction type) {
			ArrayList<TypeProbDensityFunction> types;
			types = new ArrayList<TypeProbDensityFunction>();
			types.add(type);
			return getUncertainValuesOfTypes(arrayUncertain,types);
		}


		private boolean doVerifyRule4(FamilyDistribution family) {
			boolean verify;
			
			ArrayList<UncertainValue> uncertainFamily = family.getFamily();
			int totalSizeFamily = uncertainFamily.size();
			
			ArrayList<UncertainValue> compUncertain = getUncertainValuesOfType(uncertainFamily,TypeProbDensityFunction.COMPLEMENT);
			
			verify = (totalSizeFamily != compUncertain.size()); 
			
			if (!verify){
				try {
					String message = "Rule 4 of the specification of sensitivity analysis in ProbModelXML has been violated. Please, check the distributions and its parameters.";
					throw new ExceptionUncertainValuesDialogEdition(message);
				} catch (ExceptionUncertainValuesDialogEdition e) {
					
				}
				
			}
			return verify;
		}



		private boolean doVerifyRule3(FamilyDistribution family) {
			int totalSizeFamily;
			boolean verify;
			
			ArrayList<UncertainValue> uncertainFamily = family.getFamily();
			totalSizeFamily = uncertainFamily.size();
			
			ArrayList<UncertainValue> dirUncertain = getUncertainValuesOfType(uncertainFamily,TypeProbDensityFunction.DIRICHLET);
			int numDirichlet = dirUncertain.size();
			if (numDirichlet>0){
				if (numDirichlet>1){
					ArrayList<UncertainValue> exactUncertain = getUncertainValuesOfType(uncertainFamily,TypeProbDensityFunction.EXACT);
					int numExact = exactUncertain.size();
					verify = ((numExact+numDirichlet==totalSizeFamily)
							&&areAllZero(new FamilyDistribution(exactUncertain).getMean()));
				}
				else{
					verify = false;
				}
			}
			else{
				verify = true;
			}
			if (!verify){
				try {
					String message = "Rule 3 of the specification of sensitivity analysis in ProbModelXML has been violated. Please, check the distributions and its parameters.";
					throw new ExceptionUncertainValuesDialogEdition(message);
				} catch (ExceptionUncertainValuesDialogEdition e) {
					
				}
				
			}
			return verify;
		}


		 /*If one of the distributions is a Beta, then:
			 • all the others must be Exact, with v = 0, or Complement;
			 • at least one of the others must be Complement.*/
		
		private boolean doVerifyRule2(FamilyDistribution family) {
			int totalSizeFamily;
			boolean verify;
			
			ArrayList<UncertainValue> uncertainFamily = family.getFamily();
			totalSizeFamily = uncertainFamily.size();
			
			ArrayList<UncertainValue> betaUncertain = getUncertainValuesOfType(uncertainFamily,TypeProbDensityFunction.BETA);
			int numBeta = betaUncertain.size();
			if (numBeta>0){
				if (numBeta==1){
					ArrayList<UncertainValue> exactUncertain = getUncertainValuesOfType(uncertainFamily,TypeProbDensityFunction.EXACT);
					ArrayList<UncertainValue> compUncertain = getUncertainValuesOfType(uncertainFamily,TypeProbDensityFunction.COMPLEMENT);
					int numExact = exactUncertain.size();
					int numComp = compUncertain.size();
					verify = ((numExact+numComp+1==totalSizeFamily)
							&&areAllZero(new FamilyDistribution(exactUncertain).getMean())
									&&(numComp>=1));
				}
				else{
					verify = false;
				}
			}
			else{
				verify = true;
			}
			if (!verify){
				try {
					String message = "Rule 2 of the specification of sensitivity analysis in ProbModelXML has been violated. Please, check the distributions and its parameters.";
					throw new ExceptionUncertainValuesDialogEdition(message);
				} catch (ExceptionUncertainValuesDialogEdition e) {
					
				}
				
			}
			return verify;
		}



		private boolean areAllZero(double[] x) {
			boolean allZero;
			
			allZero = true;
			for (int i=0;(i<x.length)&&allZero;i++){
				allZero = x[i]==0.0;
			}
			
			return allZero;
		}

		
		public boolean isChanceVariable(){
			return isChanceVariable;
		}


		private ArrayList<UncertainValue> readDataFromDistributionModel() {
			
			Vector<?> data = distributionsTableModel.getDataVector();
			int numRows = data.size();
			
			ArrayList<UncertainValue> dataUncertain = new ArrayList<UncertainValue>();
			
			
			for (int i=0;i<numRows;i++){
				Vector<?> row = (Vector<?>) data.get(i);
				String strType = (String)row.get(1);
				TypeProbDensityFunction auxType = TypeProbDensityFunction.valueEnumOf(strType);
				UncertainValue aux = new UncertainValue(auxType,(String)(row.get(2)),(String)(row.get(3)),false,false);
				dataUncertain.add(aux);
			}
			
			return dataUncertain;
		}

		
		


		private void quitIconsOfButtons() {
			this.getJButtonOK().setIcon(null);
			this.getJButtonCancel().setIcon(null);
		}

		/**
		 * Sets up the panel where the buttons of the buttons panel will be appear.
		 */
		private void configureButtonsPanel() {

			addButtonToButtonsPanel(getJButtonOK());
			//addButtonToButtonsPanel(getJButtonRemove());
			addButtonToButtonsPanel(getJButtonCancel());
		}
	
		/**
		 * This method initializes jButtonRemove.
		 * 
		 * @return a new Cancel button.
		 */
	/*	private JButton getJButtonRemove() {

			if (jButtonRemove == null) {
				jButtonRemove = new JButton();
				jButtonRemove.setName("jButtonCancel");
				jButtonRemove.setText(stringResource
					.getString("OKCancelHorizontalDialog.jButtonCancel.Text"));
				jButtonRemove.setMnemonic(stringResource.getString(
					"OKCancelHorizontalDialog.jButtonCancel.Mnemonic").charAt(0));
				setCancelButton(jButtonRemove);
				jButtonRemove.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {

						doCancelClickBeforeHide();
						selectedButton = CANCEL_BUTTON;
						setVisible(false);
						dispose();
					}
				});
			}
			return jButtonRemove;
		}
	*/
		
	
		
		/* public readDataFromDistributionsTable(){
			  
			  
			  
			  
		 }*/
	
		  
		  /**
		   * This class is used for painting and colouring the table and
		   * the headers
		   */

		  class RendererConfigurationTable extends DefaultTableCellRenderer {
		     		     
		/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {

		      if (row==1) {
		        setBackground(Color.gray);
		      }
		      else {
		    	setBackground(Color.white);
		      }

		      return super.getTableCellRendererComponent(table,
				       value, isSelected, hasFocus,
				       row, column);
		     }
		  }
		  
		  
		
		  
		  
		 
	

}



		