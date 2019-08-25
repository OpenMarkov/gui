/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.dialog.node;

import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.modelUncertainty.*;
import org.openmarkov.core.model.network.potential.TimeToEventTablePotential;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.gui.dialog.common.OkCancelHorizontalDialog;
import org.openmarkov.gui.loader.element.IconLoader;
import org.openmarkov.gui.localize.StringDatabase;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class AssignUncertainTteDialog extends OkCancelHorizontalDialog {

	private static final int STATE_COLUMN_INDEX = 0;
	private static final int DISTRIBUTION_COLUMN_INDEX = 1;
	private static final int PARAMETERS_COLUMN_INDEX = 2;
	private static final int NAME_COLUMN_INDEX = 3;
	private static final long serialVersionUID = 1L;
	protected StringDatabase stringDatabase = StringDatabase.getUniqueInstance();
	// Components related to the distributions box
	private DistributionTableModel distributionTableModel;
	private JTable distributionTable;
	private JPanel distributionsPanel;
	private Variable variable;
	private List<String> distributionTypes;
	private boolean isChanceVariable;
	// List of uncertain values
	private List<UncertainValue> uncertainColumn;
	// List of doubles calculated from uncertainColum by taking the mean value
	private List<Double> valuesColumn;
	// Base position for storing the array of uncertain values in the table
	// potential
	private int posBase;


	/**
	 * Creates and displays the UncertainValuesDialog for a ExactDistrPotential
	 *
	 * @param owner
	 * @param potential     - exactDistrPotential for which we will set uncertainty
	 * @throws WrongCriterionException
	 * @wbp.parser.constructor
	 * @author carmenyago -minor changes to the TablePotential method
	 */
	public AssignUncertainTteDialog(Window owner, TimeToEventTablePotential potential, int position)
			throws WrongCriterionException {
		super(owner);
		TablePotential tablePotential = potential.getTablePotential();

		distributionTypes = new ArrayList<>();
		variable = tablePotential.getVariable(0);
		setTitle("Set Probabilistic Time To Event");
		posBase = position;
		setResizable(true);
		JPanel componentsPanel = getComponentsPanel();
		// Panel of distributions
		distributionsPanel = new JPanel();
		fillDistributionsTableModel(variable, position, tablePotential);
		distributionTable.getModel().addTableModelListener(new DistributionsTableListener());
		distributionTable.addMouseListener(new DistributionsTableMouseListener());
		distributionsPanel.setBorder(new TitledBorder("Distributions"));
		JScrollPane distributionsTablePane = new JScrollPane(distributionTable);
		distributionsPanel.add(distributionsTablePane);
		distributionsTablePane.setPreferredSize(new Dimension(300, 100));
		distributionsPanel.setPreferredSize(new Dimension(350, 150));
		componentsPanel.add(distributionsPanel);
		try {
			initialize();
		} catch (Throwable e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, stringDatabase.getString(e.getMessage()),
					stringDatabase.getString(e.getMessage()), JOptionPane.ERROR_MESSAGE);
		}

		Point parentLocation = owner.getLocation();
		Dimension parentSize = owner.getSize();
		int x = (int) (parentLocation.getX() + parentSize.getWidth() / 2 - getSize().getWidth() / 2);
		int y = (int) (parentLocation.getY() + parentSize.getHeight() / 2 - getSize().getHeight() / 2);
		setLocation(new Point(x, y));
	}

	public static boolean hasUncertainValues(UncertainValue[] auxUncertainTable) {
		boolean hasUncertainValues;
		if ((auxUncertainTable == null) || (auxUncertainTable.length == 0)) {
			hasUncertainValues = false;
		} else {
			hasUncertainValues = false;
			for (int i = 0; (i < auxUncertainTable.length) && !hasUncertainValues; i++) {
				hasUncertainValues = (auxUncertainTable[i] != null);
			}
		}
		return hasUncertainValues;
	}

	public static List<Double> calculateReferenceValues(List<UncertainValue> uncertainColumn) {
		List<Integer> complementIndexes = new ArrayList<Integer>();
		List<Integer> dirichletIndexes = new ArrayList<Integer>();
		List<Integer> otherIndexes = new ArrayList<Integer>();
		double[] refValues = new double[uncertainColumn.size()];
		ComplementFamily comp = new ComplementFamily(uncertainColumn);
		DirichletFamily dir = new DirichletFamily(uncertainColumn);
		List<UncertainValue> otherUncertain = new ArrayList<UncertainValue>();
		for (int i = 0; i < uncertainColumn.size(); i++) {
			UncertainValue uncertainValue = uncertainColumn.get(i);
			if (uncertainValue.getProbDensFunction() instanceof ComplementFunction) {
				complementIndexes.add(i);
			} else if (uncertainValue.getProbDensFunction() instanceof DirichletFunction) {
				dirichletIndexes.add(i);
			} else {
				otherIndexes.add(i);
			}
		}
		otherUncertain = getElementsFromIndexes(uncertainColumn, otherIndexes);
		// Process other
		FamilyDistribution other = new FamilyDistribution(otherUncertain);
		double[] meanOther = other.getMean();
		placeInArray(refValues, otherIndexes, meanOther);
		// Process Dirichlet
		double[] meanDir = dir.getMean();
		placeInArray(refValues, dirichletIndexes, meanDir);
		// Process complements
		double massForComp = 1.0 - (Tools.sum(meanOther) + Tools.sum(meanDir));
		comp.setProbMass(massForComp);
		double[] meanComp = comp.getMean();
		placeInArray(refValues, complementIndexes, meanComp);
		List<Double> ref = new ArrayList<Double>();
		for (int i = 0; i < refValues.length; i++) {
			ref.add(refValues[i]);
		}
		return ref;
	}

	private static void placeInArray(double[] refValue, List<Integer> indexes, double[] x) {
		for (int i = 0; i < indexes.size(); i++) {
			refValue[indexes.get(i)] = x[i];
		}
	}

	private static List<UncertainValue> getElementsFromIndexes(List<UncertainValue> column, List<Integer> index) {
		List<UncertainValue> list = new ArrayList<UncertainValue>();
		for (Integer aux : index) {
			list.add(column.get(aux));
		}
		return list;
	}

	private static List<UncertainValue> getUncertainValuesOfClasses(List<UncertainValue> uncertainValues,
			List<Class<? extends ProbDensFunction>> classes) {
		List<UncertainValue> filtered = new ArrayList<UncertainValue>();
		for (UncertainValue aux : uncertainValues) {
			boolean isInClasses = false;
			for (int i = 0; (i < classes.size()) && !isInClasses; i++) {
				isInClasses = classes.get(i).isAssignableFrom(aux.getProbDensFunction().getClass());
			}
			if (isInClasses) {
				filtered.add(aux);
			}
		}
		return filtered;
	}

	private static boolean thereAreExactValuesGreaterThanZero(List<UncertainValue> arrayUncertain) {
		boolean thereAre = false;
		for (int i = 0; (i < arrayUncertain.size()) && !thereAre; i++) {
			UncertainValue aux = arrayUncertain.get(i);
			ProbDensFunction probDensityFunction = aux.getProbDensFunction();
			thereAre = (probDensityFunction instanceof ExactFunction) && probDensityFunction.getMean() > 0;
		}
		return thereAre;
	}

	/**
	 * @param uncertainValues
	 * @param types
	 * @return
	 */
	private static int[] getIndexesUncertainValuesOfClasses(List<UncertainValue> uncertainValues,
			List<Class<? extends ProbDensFunction>> types) {
		List<Integer> indexes = new ArrayList<Integer>();
		for (int i = 0; i < uncertainValues.size(); i++) {
			UncertainValue uncertainValue = uncertainValues.get(i);
			ProbDensFunction probDensFunction = uncertainValue.getProbDensFunction();
			boolean isInTypes = false;
			for (int j = 0; (j < types.size()) && !isInTypes; j++) {
				isInTypes = types.get(j).isAssignableFrom(probDensFunction.getClass());
			}
			if (isInTypes) {
				indexes.add(i);
			}
		}
		int numIndexesOfTypes = indexes.size();
		int[] intIndexes = new int[numIndexesOfTypes];
		for (int i = 0; i < numIndexesOfTypes; i++) {
			intIndexes[i] = indexes.get(i);
		}
		return intIndexes;
	}

	public static int[] getIndexesUncertainValuesOfClass(List<UncertainValue> uncertainValues,
			Class<? extends ProbDensFunction> functionClass) {
		List<Class<? extends ProbDensFunction>> classes = new ArrayList<>();
		classes.add(functionClass);
		return getIndexesUncertainValuesOfClasses(uncertainValues, classes);
	}

	private static List<UncertainValue> getUncertainValuesOfClass(List<UncertainValue> arrayUncertain,
			Class<? extends ProbDensFunction> type) {
		List<Class<? extends ProbDensFunction>> types = new ArrayList<>();
		types.add(type);
		return getUncertainValuesOfClasses(arrayUncertain, types);
	}

	public int requestUncertainValues() {
		setVisible(true);
		return this.selectedButton;
	}

	public List<Double> getValuesColumn() {
		return valuesColumn;
	}

	public List<UncertainValue> getUncertainColumn() {
		return uncertainColumn;
	}

	public int getPosBase() {
		return posBase;
	}

//	private int getPositionBaseUncertainValue(TablePotential potential) {
//		int pos;
//		coordinates = new int[1];
//		List<Variable> varsTable = potential.getVariables();
//			coordinates[i] = configuration.getFinding(varsTable.get(i)).getStateIndex();
//			coordinates[i] = 1;
//		}
//		pos = potential.getPosition(coordinates);
//		return pos;
//	}

	private String getColumnString(String column) {
		return stringDatabase.getString("UncertainValuesDialog.DistributionsTable.Columns." + column + ".Label");
	}

	private void fillDistributionsTableModel(Variable variable, int position, TablePotential potential)
			throws WrongCriterionException {
		UncertainValue[] uncertainTable = potential.getUncertainValues();

		if (uncertainTable==null) {
			uncertainTable=createExactUncertainValuesFromDouble(potential);
		} else if (uncertainTable[position]== null){
			uncertainTable[position]= new UncertainValue(potential.getValues()[position]);
		}

		String[] englishColumnNames = new String[] { "State", "Distribution", "Parameters", "Name" };
		int numColumns = englishColumnNames.length;
		String[] columnNames = new String[numColumns];
		for (int i = 0; i < numColumns; i++) {
			columnNames[i] = getColumnString(englishColumnNames[i]);
		}

		List<String> allowedDistributionTypes = ProbDensFunctionManager.getUniqueInstance()
				.getValidProbDensFunctionsTime();
		//State[] states = variable.getStates();
		int numStates = 1;
		Object[][] initialData = new Object[numStates][columnNames.length];
		JComboBox<String> distributionTypesCombo = new JComboBox<String>();
		for (String allowedDistributionType : allowedDistributionTypes) {
			distributionTypesCombo.addItem(allowedDistributionType);
		}
		int lastPosStates = numStates - 1;
		UncertainValue uncertainValue = uncertainTable[position];
		ProbDensFunction probDensFunction = uncertainValue.getProbDensFunction();
		String distribution = probDensFunction.getClass().getAnnotation(ProbDensFunctionType.class).name();
		distributionTypes.add(distribution);
		int iPosInitialData = lastPosStates;
		Object[] initialDataIPosInitialData = initialData[iPosInitialData];
		initialDataIPosInitialData[STATE_COLUMN_INDEX] = variable.getName();
		initialDataIPosInitialData[DISTRIBUTION_COLUMN_INDEX] = distribution;
		initialDataIPosInitialData[PARAMETERS_COLUMN_INDEX] = getString(probDensFunction.getParameters());
		initialDataIPosInitialData[NAME_COLUMN_INDEX] = uncertainValue.getName();

		distributionTableModel = new DistributionTableModel(initialData, columnNames);
		distributionTable = new JTable(distributionTableModel);
		// Model for the column "Distribution"
		TableColumnModel columnModel = distributionTable.getColumnModel();
		TableColumn column = columnModel.getColumn(DISTRIBUTION_COLUMN_INDEX);
		column.setCellEditor(new DefaultCellEditor(distributionTypesCombo));
		columnModel.getColumn(0).setCellEditor(null);
	}

	private String getString(double[] parameters) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < parameters.length; ++i) {
			sb.append(parameters[i]);
			sb.append(" ");
		}
		return sb.toString();
	}

	/**
	 * @param projectedPotential Table potential which has no uncertain values. Its values are
	 *                           used for creating the uncertain values
	 * @return An array of uncertain values
	 */
	private UncertainValue[] createExactUncertainValuesFromDouble(TablePotential projectedPotential) {
		double[] tableProjected = projectedPotential.getValues();
		UncertainValue[] uncertainTable = new UncertainValue[tableProjected.length];
		for (int i = 0; i < tableProjected.length; i++) {
			uncertainTable[i] = new UncertainValue(tableProjected[i]);
		}
		return uncertainTable;
	}

	private String getConfigurationDescription(Variable variable, boolean isChanceVariable,
			EvidenceCase configuration) {
		StringBuilder sb = new StringBuilder();
		sb.append((isChanceVariable) ? "P" : "U");
		sb.append("(");
		sb.append(variable.getName());
		sb.append(" | ");
		List<Finding> findings = configuration.getFindings();
		for (Finding finding : findings) {
			sb.append(finding.getVariable().getName());
			sb.append(" = '");
			sb.append(finding.getState());
			sb.append("', ");
		}
		if (sb.charAt(sb.length() - 2) == ',') {
			sb.delete(sb.length() - 2, sb.length());
		}
		sb.append(")");
		return sb.toString();
	}

	/**
	 * This method initializes this instance.
	 */
	private void initialize() {
		setName("UncertainValuesDialog");
		iconLoader = new IconLoader();
		configureButtonsPanel();
		setDefaultButton(getJButtonOK());
		quitIconsOfButtons();
		pack();
	}

	/**
	 * This method carries out the actions when the user press the Ok button
	 * before hide the dialog.
	 *
	 * @return true if the dialog box can be closed.
	 */
	protected boolean doOkClickBeforeHide() {
		TableCellEditor currentEditor = distributionTable.getCellEditor();

		if (currentEditor != null) {
			currentEditor.stopCellEditing();
		}

		List<UncertainValue> uncertainValues = readDataFromTable();
//		boolean verify = verifyLocalConstraintsUncertainty(uncertainValues);
//		if (verify) {
//			if (isChanceVariable) {
//				if (!verifyGlobalConstraintUncertainty(uncertainValues)) {
//					// System.out.println("Distribution "+typeDistrib+" does not verify the constraints associated to its domain.");
//					verify = false;
//				}222
//			}
//		}
	boolean verify = true;
		if (verify) {
			uncertainColumn = reverse(uncertainValues);
			valuesColumn = calculateReferenceValues();
		}
		return verify;
	}

	private List<UncertainValue> reverse(List<UncertainValue> list) {
		List<UncertainValue> rev = new ArrayList<UncertainValue>();
		for (int i = list.size() - 1; i >= 0; i--) {
			rev.add(list.get(i));
		}
		return rev;
	}

	private List<Double> calculateReferenceValues() {
		return calculateReferenceValues(uncertainColumn);
	}



	private boolean areAllZero(double[] x) {
		boolean allZero;
		allZero = true;
		for (int i = 0; (i < x.length) && allZero; i++) {
			allZero = x[i] == 0.0;
		}
		return allZero;
	}



	private List<UncertainValue> readDataFromTable() {
		Vector<?> data = distributionTableModel.getDataVector();
		int numRows = data.size();
		List<UncertainValue> uncertainValues = new ArrayList<UncertainValue>();
		ProbDensFunctionManager distributionManager = ProbDensFunctionManager.getUniqueInstance();
		for (int i = 0; i < numRows; i++) {
			Vector<?> row = (Vector<?>) data.get(i);
			String distributionType = row.get(DISTRIBUTION_COLUMN_INDEX).toString();
			String[] parameters = row.get(PARAMETERS_COLUMN_INDEX).toString().split(" ");
			double[] parameterArray = new double[parameters.length];
			for (int j = 0; j < parameters.length; ++j) {
				parameterArray[j] = Double.parseDouble(parameters[j]);
			}
			String name = (String) row.get(NAME_COLUMN_INDEX);
			ProbDensFunction probDensFunction = distributionManager.newInstance(distributionType, parameterArray);
			UncertainValue uncertainValue = new UncertainValue(probDensFunction, name);
			uncertainValues.add(uncertainValue);
		}
		return uncertainValues;
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
		// addButtonToButtonsPanel(getJButtonRemove());
		addButtonToButtonsPanel(getJButtonCancel());
	}

	public class DistributionsTableListener implements TableModelListener {
		public void tableChanged(TableModelEvent e) {
			if (e.getColumn() == DISTRIBUTION_COLUMN_INDEX) {
				int selectedRow = distributionTable.getSelectedRow();
				String distributionType = distributionTableModel.getValueAt(selectedRow, DISTRIBUTION_COLUMN_INDEX)
						.toString();
				DistributionParameterDialog parameterDialog = new DistributionParameterDialog(getOwner(),
						distributionType);
				if (!distributionTypes.get(selectedRow).equals(distributionType)) {
					parameterDialog.setVisible(true);
					if (parameterDialog.getSelectedButton() == OK_BUTTON) {
						StringBuilder parameterString = new StringBuilder();
						for (double parameter : parameterDialog.getParameters()) {
							parameterString.append(parameter);
							parameterString.append(" ");
						}
						distributionTableModel
								.setValueAt(parameterString.toString(), selectedRow, PARAMETERS_COLUMN_INDEX);
						distributionTypes.set(selectedRow, distributionType);
					} else {
						distributionTableModel
								.setValueAt(distributionTypes.get(selectedRow), selectedRow, DISTRIBUTION_COLUMN_INDEX);
					}
				}
			}
		}
	}

	public class DistributionsTableMouseListener extends MouseAdapter {

		@Override public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2 && distributionTable.getSelectedColumn() == PARAMETERS_COLUMN_INDEX) {
				int selectedRow = distributionTable.getSelectedRow();
				String distributionType = distributionTableModel.getValueAt(selectedRow, DISTRIBUTION_COLUMN_INDEX)
						.toString();
				String currentParameters = distributionTableModel.getValueAt(selectedRow, PARAMETERS_COLUMN_INDEX)
						.toString();
				double[] parameters = null;
				if (!currentParameters.isEmpty()) {
					String[] parameterArray = currentParameters.split(" ");
					parameters = new double[parameterArray.length];
					for (int i = 0; i < parameters.length; ++i) {
						parameters[i] = Double.parseDouble(parameterArray[i]);
					}
				}
				DistributionParameterDialog parameterDialog = new DistributionParameterDialog(getOwner(),
						distributionType, parameters);
				parameterDialog.setVisible(true);
				if (parameterDialog.getSelectedButton() == OK_BUTTON) {
					StringBuilder parameterString = new StringBuilder();
					for (double parameter : parameterDialog.getParameters()) {
						parameterString.append(parameter);
						parameterString.append(" ");
					}
					distributionTableModel.setValueAt(parameterString.toString(), selectedRow, PARAMETERS_COLUMN_INDEX);
				}
			}
		}
	}

	public class DistributionTableModel extends DefaultTableModel {

		private static final long serialVersionUID = 1L;

		public DistributionTableModel(Object[][] initialData, String[] columnNames) {
			super(initialData, columnNames);
		}

		@Override public boolean isCellEditable(int row, int col) {
			return (col == DISTRIBUTION_COLUMN_INDEX) || (col == NAME_COLUMN_INDEX);
		}
	}

	/**
	 * This class is used for painting and coloring the table and the headers
	 */
	@SuppressWarnings("unused") private class RendererConfigurationTable extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			setBackground((row == 1) ? Color.gray : Color.white);
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
	}
}
