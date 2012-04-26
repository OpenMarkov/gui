/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

/**
 * 
 */
package org.openmarkov.core.gui.component;


import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;


import org.openmarkov.core.action.StateAction;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.action.NodePartitionedIntervalEdit;
import org.openmarkov.core.gui.action.NodeStateEdit;
import org.openmarkov.core.gui.dialog.common.KeyTablePanel;
import org.openmarkov.core.gui.loader.element.IconLoader;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.gui.util.GUIDefaultStates;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.VariableType;



/**
 * This class implements a panel to encapsulate a Discretize Table with the
 * following features:
 * <ul>
 * <li>Its elements, except the first column that will be hidden, are
 * modifiable.</li>
 * <li>New elements can be added, creating a new key row with empty data.</li>
 * <li>The key data (first column) consist of a key string following of the
 * index of the row. This row is hidden</li>
 * <li>The information of a row (except the first column) can be taken up or
 * down.</li>
 * <li>The rows can be removed.</li>
 * <li>The initial lower value of a row is the upper value of the previous row
 * plus a delta, if the model monotony is DOWN</li>
 * <li>The initial upper value of a row is the lower value of the previous row
 * plus a delta, if the model monotony is UP</li>
 * <li>Infinite positive and negative are allowed as values through specific
 * buttons</li>
 * </ul>
 * 
 * @author jlgozalo
 * @author myebra
 * @version 1.0 29 Jun 2009
 */
public class DiscretizeTablePanel extends KeyTablePanel implements 
	TableModelListener {

	/**
	 * default serial id
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * ComboBox for intervals symbols
	 */
	final String[] intervalLowerSymbols = new String[] { "[", "(" };
	final String[] intervalUpperSymbols = new String[] { "]", ")" };
	final String commaSeparator = ",";
	JComboBox jComboBoxLowerSymbol = null;
	JComboBox jComboBoxUpperSymbol = null;
	/**
	 * number of the columns in this type of table
	 */
	int idColumnNum;
	int intervalNameColumnNum;
	int lowerLimitSymbolColumnNum;
	int lowLimitValueColumnNum;
	int valuesSeparatorColumnNum;
	int upperLimitValueColumnNum;
	int upperLimitSymbolColumnNum;

	/**
	 * monotony of the items in the table - true = up; false=down;
	 */
	private boolean upMonotony = true; // default=UP
	/**
	 * Key prefix (required to maintain the index of the table even if it is not
	 * shown to the user)
	 */
	private String keyPrefix = null;
	/**
	 * Infinite Positive Button
	 */
	private JButton jButtonInfinitePositiveDouble = null;

	/**
	 * Infinite Positive Button
	 */
	private JButton jButtonInfiniteNegativeDouble = null;

	/**
	 * resource bundle
	 */
	private StringResource dialogStringResource = null;

	/**
	 * discretize table model
	 */
	private DiscretizeTableModel discretizeTableModel = null;

	protected ProbNode probNode;

	protected StringResource messageStringResource;
	
	

	/**
	 * default constructor
	 * @wbp.parser.constructor
	 */
	public DiscretizeTablePanel(String[] newColumns,ProbNode probNode) {

		this(newColumns, new Object[0][0], "s", probNode);
		
		// s = keyPrefix for id column; not shown to user
	}
	
	 

	/**
	 * constructor with parameters
	 */
	public DiscretizeTablePanel(String[] newColumns, Object[][] noKeyData,
								String newKeyPrefix, ProbNode probNode){

		super(newColumns, new Object[0][0], true, true);//, notifier);
		messageStringResource =
			StringResourceLoader.getUniqueInstance().getBundleMessages();
		this.probNode = probNode;
		keyPrefix = newKeyPrefix;
		dialogStringResource = StringResourceLoader.getUniqueInstance()
						.getBundleDialogs();
		initialize();
		setData(noKeyData); // also it is setting the model for the table
		// define the look and feel for the table element
		defineTableLookAndFeel();
		// define specific listeners
		defineTableSpecificListeners();
		getTableModel().addTableModelListener(this);
	}

	/**
	 * This method initializes this instance.
	 */
	@Override
	protected void initialize() {

		// get the number of each column from the external property file
		idColumnNum = Integer.valueOf(dialogStringResource.getString(
				"DiscretizeTableModel.Columns.IntervalId.Order"));
		intervalNameColumnNum = Integer.valueOf(dialogStringResource.getString(
				"DiscretizeTableModel.Columns.IntervalName.Order"));
		lowerLimitSymbolColumnNum = Integer.valueOf(
				dialogStringResource.getString(
						"DiscretizeTableModel.Columns.LowLimitSymbol.Order"));
		lowLimitValueColumnNum = Integer.valueOf(dialogStringResource.getString(
				"DiscretizeTableModel.Columns.LowLimitValue.Order"));
		valuesSeparatorColumnNum = Integer.valueOf(
				dialogStringResource.getString(
						"DiscretizeTableModel.Columns.ValuesSeparator.Order"));
		upperLimitValueColumnNum = Integer.valueOf(
				dialogStringResource.getString(
						"DiscretizeTableModel.Columns.UpperLimitValue.Order"));
		upperLimitSymbolColumnNum = Integer.valueOf(
				dialogStringResource.getString(
						"DiscretizeTableModel.Columns.UpperLimitSymbol.Order"));

		// define the border and layout for the panel
		setBorder(new EmptyBorder(0, 0, 0, 0));
		final GroupLayout groupLayout = new GroupLayout((JComponent) this);
		groupLayout.setHorizontalGroup(groupLayout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(groupLayout
							.createSequentialGroup()
							.addContainerGap()
							.addComponent(
										getValuesTableScrollPane(),
										GroupLayout.PREFERRED_SIZE,
									    406,
										GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(
										getButtonPanel(),
										GroupLayout.DEFAULT_SIZE,
										67,
										Short.MAX_VALUE)));
		groupLayout.setVerticalGroup(groupLayout
						.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addGroup(groupLayout
							.createSequentialGroup()
							.addContainerGap()
							.addGroup(groupLayout
								.createParallelGroup(GroupLayout.Alignment.TRAILING)
								.addComponent(
										getValuesTableScrollPane(),
										GroupLayout.Alignment.LEADING,
										GroupLayout.DEFAULT_SIZE,
										131,
										Short.MAX_VALUE)
								.addComponent(
										getButtonPanel(),
										GroupLayout.PREFERRED_SIZE,
										131,
										Short.MAX_VALUE))
								.addGap(24, 24,	24)));
		setLayout(groupLayout);

		
	}

	/**
	 * This method initializes tableModel.
	 * 
	 * @return a new tableModel.
	 */
	@Override
	protected DiscretizeTableModel getTableModel() {

		if (discretizeTableModel == null) {
			discretizeTableModel = new DiscretizeTableModel(data, columns);
		}
		return discretizeTableModel;
	}

	/**
	 * defines the look and feel of the table (column width, etc...)
	 */
	protected void defineTableLookAndFeel() {

		// center the data in all columns
		DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
		tcr.setHorizontalAlignment(SwingConstants.CENTER);

		int maxColumn = valuesTable.getColumnModel().getColumnCount();
		for (int i = 1; i < maxColumn; i++) {
			TableColumn aColumn = valuesTable.getColumnModel().getColumn(i);
			aColumn.setCellRenderer(tcr);
			aColumn.setPreferredWidth(110);
			aColumn.setMaxWidth(110);
			aColumn.setMinWidth(110);
			valuesTable.getTableHeader().getColumnModel().getColumn(i)
							.setCellRenderer(tcr);
		}
		// set special columns
		if ( probNode.getVariable().getVariableType() == VariableType.NUMERIC ){
			TableColumn aColumn = valuesTable.getColumnModel().getColumn(1);
			aColumn.setCellRenderer(tcr);
			aColumn.setPreferredWidth( 0 );
			aColumn.setMaxWidth( 0 );
			aColumn.setMinWidth( 0 );
			valuesTable.getTableHeader().getColumnModel().getColumn(1).setPreferredWidth(0);
			valuesTable.getTableHeader().getColumnModel().getColumn(1).setMinWidth(0);
			valuesTable.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(0);
		}

		// set Columns = Up and Low limits
		jComboBoxLowerSymbol = new JComboBox(intervalLowerSymbols);
		jComboBoxUpperSymbol = new JComboBox(intervalUpperSymbols);

		TableColumn lowLimitSymbolColumn = valuesTable.getColumnModel()
						.getColumn(lowerLimitSymbolColumnNum);
		lowLimitSymbolColumn.setCellEditor(new DefaultCellEditor(
						jComboBoxLowerSymbol));
		lowLimitSymbolColumn.setCellRenderer(new MyComboBoxRenderer(
						intervalLowerSymbols));
		lowLimitSymbolColumn.setMinWidth(32);
		lowLimitSymbolColumn.setPreferredWidth(32);
		lowLimitSymbolColumn.setMaxWidth(32);
		
		TableColumn upperLimitSymbolColumn = valuesTable.getColumnModel()
						.getColumn(upperLimitSymbolColumnNum);
		upperLimitSymbolColumn.setCellEditor(new DefaultCellEditor(
						jComboBoxUpperSymbol));
		upperLimitSymbolColumn.setCellRenderer(new MyComboBoxRenderer(
						intervalUpperSymbols));
		upperLimitSymbolColumn.setMinWidth(32);
		upperLimitSymbolColumn.setPreferredWidth(32);
		upperLimitSymbolColumn.setMaxWidth(32);

		// set Column = valuesSeparator = ","
		TableColumn valuesSeparatorColumn = valuesTable.getColumnModel()
						.getColumn(valuesSeparatorColumnNum);
		valuesSeparatorColumn.setMinWidth(10);
		valuesSeparatorColumn.setPreferredWidth(10);
		valuesSeparatorColumn.setMaxWidth(10);

	}

	/**
	 * Method to define the specific listeners in this table (not defined in the
	 * common KeyTable hierarchy
	 */
	protected void defineTableSpecificListeners() {

		valuesTable.addMouseListener(new java.awt.event.MouseAdapter() {

			public void mouseClicked(java.awt.event.MouseEvent e) {

				int fila = valuesTable.rowAtPoint(e.getPoint());
				int columna = valuesTable.columnAtPoint(e.getPoint());
				if ((fila > -1) && (columna > -1)) {
						changeIntervalDiscretize(fila, columna);
				}
			}
		});

	}

	/**
	 * This method is used to change the interval's type in a discretize Table 
	 * To closed from opened 
	 * To opened from closed
	 */
	private void changeIntervalDiscretize(int fila, int columna) {
		
		if (columna == lowerLimitSymbolColumnNum
						|| columna == upperLimitSymbolColumnNum) {
			boolean lower = false;
			String aux = (String) valuesTable.getValueAt(fila, columna);
			if (columna == lowerLimitSymbolColumnNum) {
				lower = true;
				if (aux.equals("(")) {
					valuesTable.setValueAt("[", fila, columna);
					if (fila > 0){
						valuesTable.setValueAt(")", fila-1, 
								upperLimitSymbolColumnNum);
					} 
					//checkIntervalDiscretize("[", fila, columna,upMonotony);
				} else {
					valuesTable.setValueAt("(", fila, columna);
					if (fila > 0){
						valuesTable.setValueAt("]", fila-1, 
								upperLimitSymbolColumnNum);
					}
					//checkIntervalDiscretize("(", fila, columna,upMonotony);
				}
			}
			if (columna == upperLimitSymbolColumnNum) {
				if (aux.equals(")")) {
					valuesTable.setValueAt("]", fila, columna);
					if (fila < valuesTable.getRowCount()-1){
						valuesTable.setValueAt("(", fila + 1, 
								lowerLimitSymbolColumnNum);
					}
					//checkIntervalDiscretize("]", fila, columna,upMonotony);
				} else {
					valuesTable.setValueAt(")", fila, columna);
					if (fila < valuesTable.getRowCount()-1){
						valuesTable.setValueAt("[", fila + 1, 
								lowerLimitSymbolColumnNum);
					}
					//checkIntervalDiscretize(")", fila, columna,upMonotony);
				}
			}
			//mpalacios
					
			NodePartitionedIntervalEdit nodePartitionedIntervalEdit = 
				new NodePartitionedIntervalEdit(probNode, StateAction.
						MODIFYDELIMITERINTERVAL, fila, lower);
			try {
				probNode.getProbNet().getPNESupport().announceEdit(
						nodePartitionedIntervalEdit);
				probNode.getProbNet().getPNESupport().doEdit(
						nodePartitionedIntervalEdit);
				
				/*Object newRow [] = nodePartitionedIntervalEdit.getNewRowOfData();
				newRow [0] = getKeyString(newIndex);
				getTableModel().insertRow(newIndex, newRow); 
				valuesTable.getSelectionModel().setSelectionInterval(newIndex,
						newIndex);*/
				
			} catch (ConstraintViolationException e) {
				JOptionPane.showMessageDialog(this, messageStringResource
						.getString( e.getMessage() ),
					messageStringResource.getString( e.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
				
				//jTextFieldNodeName.setText( this.nodeProperties.getName() );
				//jTextFieldNodeName.requestFocus();
				
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
			} catch (NotEnoughMemoryException e) {
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
			} catch (WrongCriterionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, messageStringResource
						.getString( e.getMessage() ),
					messageStringResource.getString( e.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			}
		} else if ( columna == lowLimitValueColumnNum
						|| columna == upperLimitValueColumnNum) {
			 double j = (Double) valuesTable.getValueAt(fila,columna);
			 System.out.println(j);
			System.out.println("DiscretizeTablePanel.changeIntervalDiscretize");
			System.out.println(">> check here the values of the interval with the other intervals");
		}
	}


	/**
	 * method to control and change the values and symbols when user changes the
	 * values and limits in the table, depending upon the type of monotony
	 */

	protected void checkIntervalDiscretize(String statecurrent, int fila,
											int columna, boolean upMonotony) {

		//double aux, aux2;
		if (upMonotony) { // monotony UP
			if (fila != valuesTable.getRowCount() - 1
				&& columna == upperLimitSymbolColumnNum) {
				if (statecurrent.equals(")")) {
					valuesTable.setValueAt("[", fila + 1,
											lowerLimitSymbolColumnNum);
					// Change Value
					//aux = (Double) valuesTable
					//				.getValueAt(fila, upperLimitValueColumnNum);
					//valuesTable.setValueAt(aux, fila + 1,
					//						lowLimitValueColumnNum);
				} else {
					valuesTable.setValueAt("(", fila + 1,
											lowerLimitSymbolColumnNum);
					// Change value
					/*
					 * 
					aux = (Double) valuesTable
									.getValueAt(fila - 1,
												lowLimitValueColumnNum);
					aux2 = (Double) valuesTable
									.getValueAt(fila, upperLimitValueColumnNum);

					if (aux == aux2) {
						valuesTable.setValueAt(
												aux , // required??
												fila - 1,
												lowLimitValueColumnNum);
					}
					*/

				}
			}
			if (fila != 0
				&& columna == lowerLimitSymbolColumnNum) {
				if (statecurrent.equals("(")) {
					valuesTable.setValueAt("]", fila - 1,
											upperLimitSymbolColumnNum);
				} else {
					valuesTable.setValueAt(")", fila - 1,
											upperLimitSymbolColumnNum);
					// Change Value
					/*aux = (Double) valuesTable
									.getValueAt(fila + 1,
												upperLimitValueColumnNum);
					valuesTable.setValueAt(aux, fila, lowLimitValueColumnNum);
					*/
				}
			}
			
		} else { // Down monotony
			if (fila != 0 && columna == upperLimitSymbolColumnNum) {
				if (statecurrent.equals(")")) {
					valuesTable.setValueAt("[", fila - 1,
											lowerLimitSymbolColumnNum);
					// Change Value
					/*
					 * aux = (Double) valuesTable
									.getValueAt(fila, upperLimitValueColumnNum);
					valuesTable.setValueAt(aux, fila - 1,
											lowLimitValueColumnNum);
					*/
				} else {
					valuesTable.setValueAt("(", fila - 1,
											lowerLimitSymbolColumnNum);
					// Change value
					/*
					 * aux = (Double) valuesTable
									.getValueAt(fila - 1,
												lowLimitValueColumnNum);
					aux2 = (Double) valuesTable
									.getValueAt(fila, upperLimitValueColumnNum);

					if (aux == aux2) {
						valuesTable.setValueAt(
												aux , // required??
												fila - 1,
												lowLimitValueColumnNum);
					}
					 */
				}
			}
			if (fila != valuesTable.getRowCount() - 1
							&& columna == lowerLimitSymbolColumnNum) {
				if (statecurrent.equals("(")) {
					valuesTable.setValueAt("]", fila + 1,
											upperLimitSymbolColumnNum);
				} else {
					valuesTable.setValueAt(")", fila + 1,
											upperLimitSymbolColumnNum);
					// Change Value
					/*
					 * aux = (Double) valuesTable
									.getValueAt(fila + 1,
												upperLimitValueColumnNum);
					valuesTable.setValueAt(aux, fila, lowLimitValueColumnNum);
					*/
				}
			}
		}
	}

	/**
	 * This method initializes buttonPanel.
	 * 
	 * @return a new button panel.
	 */
	@Override
	protected JPanel getButtonPanel() {

		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setName("DiscretizeTablePanel.buttonPanel");
			final GroupLayout groupLayout = new GroupLayout(
							(JComponent) buttonPanel);
			groupLayout.setHorizontalGroup(
			    groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addGroup(
						groupLayout.createSequentialGroup()
							.addGroup(
								groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
									.addComponent(getAddValueButton(),
												GroupLayout.DEFAULT_SIZE,
												55,
												Short.MAX_VALUE)
									.addComponent(getDownValueButton(),
												GroupLayout.Alignment.LEADING,
												GroupLayout.DEFAULT_SIZE,
												55,
												Short.MAX_VALUE)
									.addComponent(getUpValueButton(),
												GroupLayout.Alignment.LEADING,
												GroupLayout.DEFAULT_SIZE,
												55,
												Short.MAX_VALUE)
									.addComponent(getRemoveValueButton(),
												GroupLayout.Alignment.LEADING,
												GroupLayout.DEFAULT_SIZE,
												55,
												Short.MAX_VALUE)
									.addComponent(getInfinitePositiveDoubleButton(),
												GroupLayout.Alignment.LEADING,
												GroupLayout.DEFAULT_SIZE,
												55,
												Short.MAX_VALUE)
									.addComponent(getInfiniteNegativeDoubleButton(),
												GroupLayout.Alignment.LEADING,
												GroupLayout.DEFAULT_SIZE,
												55,
												Short.MAX_VALUE))
									.addContainerGap()));
			groupLayout.setVerticalGroup(
			    groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addGroup(
					    groupLayout.createSequentialGroup()
						.addComponent(getAddValueButton())
						.addGap(5, 5, 5)
						.addComponent(getRemoveValueButton())
						.addGap(5, 5, 5)
						.addComponent(getUpValueButton())
						.addGap(5, 5, 5)
						.addComponent(getDownValueButton())
						.addGap(5, 5, 5)
						.addComponent(getInfinitePositiveDoubleButton())
						.addGap(5, 5, 5)
						.addComponent(getInfiniteNegativeDoubleButton())
						.addGap(48, 48, 48)));
			buttonPanel.setLayout(groupLayout);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes infinitePositiveDoubleButton.
	 * 
	 * @return a new positive infinite value button.
	 */
	protected JButton getInfinitePositiveDoubleButton() {

		if (jButtonInfinitePositiveDouble == null) {
			jButtonInfinitePositiveDouble = new JButton();
			jButtonInfinitePositiveDouble
							.setName("DiscretizeTablePanel.jButtonInfinitePositiveDouble");
			jButtonInfinitePositiveDouble.setText(stringResource
							.getString("InfinitePositive.Text.Label"));
			jButtonInfinitePositiveDouble.setIcon(iconLoader
							.load(IconLoader.ICON_INFINITE_POSITIVE_ENABLED));
			jButtonInfinitePositiveDouble.setVisible(reorderEnabled);
			jButtonInfinitePositiveDouble.setEnabled(true);
			jButtonInfinitePositiveDouble.addActionListener(this);
			jButtonInfinitePositiveDouble.setVisible(true);
			
		}
		return jButtonInfinitePositiveDouble;
	}

	/**
	 * This method initializes infiniteNegativeDoubleButton.
	 * 
	 * @return a new negative infinite value button.
	 */
	protected JButton getInfiniteNegativeDoubleButton() {

		if (jButtonInfiniteNegativeDouble == null) {
			jButtonInfiniteNegativeDouble = new JButton();
			jButtonInfiniteNegativeDouble
							.setName("DiscretizeTablePanel.jButtonInfiniteNegativeDouble");
			jButtonInfiniteNegativeDouble.setText(stringResource
							.getString("InfiniteNegative.Text.Label"));
			jButtonInfiniteNegativeDouble.setIcon(iconLoader
							.load(IconLoader.ICON_INFINITE_NEGATIVE_ENABLED));
			jButtonInfiniteNegativeDouble.setVisible(reorderEnabled);
			jButtonInfiniteNegativeDouble.setEnabled(true);
			jButtonInfiniteNegativeDouble.addActionListener(this);
			jButtonInfiniteNegativeDouble.setVisible(true);
		}
		return jButtonInfiniteNegativeDouble;
	}

	/**
	 * Sets a new table model with new data.
	 * 
	 * @param newData
	 *            new data for the table without the key column.
	 */
	@Override
	public void setData(Object[][] newData) {

		if (newData != null) {
			data = fillDataKeys(newData);
			discretizeTableModel = new DiscretizeTableModel(data, columns);
			valuesTable.setModel(discretizeTableModel);
			valuesTable.getModel().addTableModelListener(this);
			this.defineTableLookAndFeel();
		}
	}

	/**
	 * @return the upMonotony
	 */
	public boolean isUpMonotony() {

		return upMonotony;
	}

	/**
	 * @param upMonotony
	 *            the upMonotony to set
	 */
	public void setUpMonotony(boolean upMonotony) {

		this.upMonotony = upMonotony;
	}

	/**
	 * execute the change in the table when a set of default states have been
	 * selected in the combo box
	 */
	public void setNewDataInTable(int selectedIndex) {

		Object[][] newData = null;
		switch (selectedIndex) {

		case 0: // present-absent
			if (upMonotony) {
				Object[][] auxData = {
								{ GUIDefaultStates.getString("absent"), "[", 0.0,
												",", 2.0, "]" },
								{ GUIDefaultStates.getString("present"), "(", 2.0,
												",", 4.0, "]" } };
				newData = auxData;
			} else {
				Object[][] auxData = {
								{ GUIDefaultStates.getString("present"), "[", 2.0,
												",", 4.0, ")" },
								{ GUIDefaultStates.getString("absent"), "[", 0.0,
												",", 2.0, ")" } };
				newData = auxData;
			}
			break;
		case 1: // yes-no
			if (upMonotony) {
				Object[][] auxData = {
								{ GUIDefaultStates.getString("yes"), "[", 0.0,
												",", 2.0, "]" },
								{ GUIDefaultStates.getString("no"), "(", 2.0,
												",", 4.0, "]" } };
				newData = auxData;
			} else {
				Object[][] auxData = {
								{ GUIDefaultStates.getString("yes"), "[", 2.0,
												",", 4.0, ")" },
								{ GUIDefaultStates.getString("no"), "[", 0.0,
												",", 2.0, ")" } };
				newData = auxData;
			}
			break;
		case 2: // positive-negative
			if (upMonotony) {
				Object[][] auxData = {
								{ GUIDefaultStates.getString("positive"), "[", 0.0,
												",", 2.0, "]" },
								{ GUIDefaultStates.getString("negative"), "(", 2.0,
												",", 4.0, "]" } };
				newData = auxData;
			} else {
				Object[][] auxData = {
								{ GUIDefaultStates.getString("positive"), "[", 2.0,
												",", 4.0, ")" },
								{ GUIDefaultStates.getString("negative"), "[", 0.0,
												",", 2.0, ")" } };
				newData = auxData;
			}
			break;
		case 3: //severe-moderate-mild-absent
			if (upMonotony) {
				Object[][] auxData = {
								{ GUIDefaultStates.getString("severe"), "[", 0.0,
									",", 2.0, "]" },
								{ GUIDefaultStates.getString("moderate"), "(", 2.0,
									",", 4.0, "]" },
								{ GUIDefaultStates.getString("mild"), "(", 4.0,
												",", 6.0, "]" },
								{ GUIDefaultStates.getString("absent"), "(", 6.0,
												",", 8.0, "]" } };
				newData = auxData;
			} else {
				Object[][] auxData = {
								{ GUIDefaultStates.getString("severe"), "[", 6.0,
									",", 8.0, ")" },
								{ GUIDefaultStates.getString("moderate"), "[", 4.0,
										",", 6.0, ")" },
								{ GUIDefaultStates.getString("mild"), "[", 2.0,
												",", 4.0, ")" },
								{ GUIDefaultStates.getString("absent"), "[", 0.0,
												",", 2.0, ")" } };
				newData = auxData;
			}
			break;
		case 4: //high-medium-low
			if (upMonotony) {
				Object[][] auxData = {
								{ GUIDefaultStates.getString("high"), "[", 0.0,
									",", 2.0, "]" },
								{ GUIDefaultStates.getString("medium"), "(", 2.0,
												",", 4.0, "]" },
								{ GUIDefaultStates.getString("low"), "(", 4.0,
												",", 6.0, "]" } };
				newData = auxData;
			} else {
				Object[][] auxData = {
								{ GUIDefaultStates.getString("high"), "[", 4.0,
									",", 6.0, ")" },
								{ GUIDefaultStates.getString("medium"), "[", 2.0,
										",", 4.0, ")" },
								{ GUIDefaultStates.getString("low"), "[", 0.0,
												",", 2.0, ")" } };
				newData = auxData;
			}
			break;
		default: //nonamed
			Object[][] auxData = {
						{ GUIDefaultStates.getString("nonamed"), 
							"(", Double.NEGATIVE_INFINITY,
							",", Double.POSITIVE_INFINITY, ")" } };
				newData = auxData;
			break;
		}
		setData(newData);
	}

	/**
	 * execute the change in the table when a set of default states have been
	 * selected in the combo box
	 */
	public void setPartitionedInterval() {
		
		PartitionedInterval partitionInterval = probNode.getVariable().
			getPartitionedInterval();
		Object [][] intervalTable = partitionInterval.convertToTableFormat();
		State states [] = probNode.getVariable().getStates();
		int rows = intervalTable.length;
		//TODO six is the number of columns of this particular table
		//int col = 6;
		//invert states to display in the correct order
		State reorderedStates [] = states.clone();
		Collections.reverse(Arrays.asList(reorderedStates));
		//Object [][] newData = new Object[rows][col];
		for (int i = 0; i < rows; i++ ){
			//for (int i = rows-1; i <=0; i-- ){
			intervalTable[i][0] = GUIDefaultStates.getString(reorderedStates[i].getName());
		}
		
		setData(intervalTable);
	}

	/**
	 * Returns the content of the table.
	 * 
	 * @return the content of the table.
	 */
	@Override
	public Object[][] getData() {

		DiscretizeTableModel model = (DiscretizeTableModel) valuesTable.getModel();
		int columnCount = model.getColumnCount();
		int rowCount = model.getRowCount();
		int i = 0;
		int j = 0;
		Object[][] datatmp = new Object[rowCount][columnCount];
		Vector vectorData = model.getDataVector();
		Vector vectorRow = null;

		for (i = 0; i < rowCount; i++) {
			vectorRow = (Vector) vectorData.get(i);
			for (j = 0; j < columnCount; j++) {
				datatmp[i][j] = vectorRow.get(j);
			}
		}
		return datatmp;
	}

	/**
	 * This method takes a data object and creates a new column that content a
	 * row key. This key begins with the key prefix following a number that
	 * starts at 0.
	 * 
	 * @param oldData
	 *            data to add a key column.
	 * @return a data object with one more column that contains the keys.
	 */
	private Object[][] fillDataKeys(Object[][] oldData) {

		Object[][] newData = null;
		int i1 = 0; // aux int
		int i2 = 0; // aux int
		int l1 = 0; // num of rows
		int l2 = 0; // num of columns

		l1 = oldData.length;
		if (l1 > 0) {
			l2 = oldData[0].length + 1;
			newData = new Object[l1][l2];
			for (i1 = 0; i1 < l1; i1++) {
				newData[i1][0] = getKeyString(i1);
				for (i2 = 1; i2 < l2; i2++) {
					newData[i1][i2] = oldData[i1][i2 - 1];
				}
			}
			return newData;
		}
		return new Object[0][0];
	}

	/**
	 * Invoked when an action occurs.
	 * 
	 * @param e
	 *            event information.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		super.actionPerformed(e);
		if (e.getSource().equals(this.jButtonInfinitePositiveDouble)) {
			actionPerformedInfinitePositiveValue();
		} else if (e.getSource().equals(this.jButtonInfiniteNegativeDouble)) {
			actionPerformedInfiniteNegativeValue();
		}
	}

	/**
	 * Invoked when the button 'add' is pressed.
	 */
	@Override
	protected void actionPerformedAddValue() {

		int rowCount = 0;
		rowCount = valuesTable.getRowCount();
		
		
		String option= JOptionPane.showInputDialog(this, 
				"Proporcione el nuevo estado", "Agregar estado", 
				JOptionPane.QUESTION_MESSAGE);
				
		if (option != null){
			int newIndex = 0;
			newIndex = valuesTable.getRowCount();
			
			NodeStateEdit nodeStateEdit = new NodeStateEdit(probNode, 
					StateAction.ADD, newIndex, option);
			try {
				probNode.getProbNet().getPNESupport().announceEdit(nodeStateEdit);
				probNode.getProbNet().getPNESupport().doEdit(nodeStateEdit);
				
				Object newRow [] = nodeStateEdit.getNewRowOfData();
				newRow [0] = getKeyString(newIndex);
				getTableModel().insertRow(newIndex, newRow); 
				valuesTable.getSelectionModel().setSelectionInterval(newIndex,
						newIndex);
				
			} catch (ConstraintViolationException e) {
				JOptionPane.showMessageDialog(this, messageStringResource
						.getString( e.getMessage() ),
					messageStringResource.getString( e.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
				
				//jTextFieldNodeName.setText( this.nodeProperties.getName() );
				//jTextFieldNodeName.requestFocus();
				
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
			} catch (NotEnoughMemoryException e) {
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
			} catch (WrongCriterionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, messageStringResource
						.getString( e.getMessage() ),
					messageStringResource.getString( e.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			}
		}
		/*if (rowCount > 0) { // adding a new row with calculated initial values
			double auxDownMonotony = (Double) valuesTable
							.getValueAt(rowCount - 1, lowLimitValueColumnNum);
			double auxUpMonotony = (Double) valuesTable
							.getValueAt(rowCount - 1, upperLimitValueColumnNum);
		    if (upMonotony) {
		    	discretizeTableModel.insertRow(rowCount, new Object[] {
							getKeyString(rowCount),
							DefaultStates.getString("nonamed"), "[",
							auxUpMonotony , ",", 
							Double.POSITIVE_INFINITY, "]" });
		    } else { //downMonotony
		    	discretizeTableModel.insertRow(rowCount, new Object[] {
							getKeyString(rowCount),
							DefaultStates.getString("nonamed"), "[",
							Double.NEGATIVE_INFINITY, ",", 
							auxDownMonotony , "]" });
		    	
		    }
		} else { // adding a new row with maximum interval initial value
			discretizeTableModel.insertRow(rowCount, new Object[] {
							getKeyString(rowCount),
							DefaultStates.getString("nonamed"), "[",
							Double.NEGATIVE_INFINITY, ",",
							Double.POSITIVE_INFINITY, "]" });
		}*/
		//checkIntervalDiscretize("[", rowCount, lowerLimitSymbolColumnNum, 
			//	upMonotony);
		//checkIntervalDiscretize("]", rowCount, upperLimitSymbolColumnNum, 
//				upMonotony);
		valuesTable.getSelectionModel().setSelectionInterval(rowCount, rowCount);
	}

	/**
	 * Invoked when the button 'remove' is pressed.
	 */
	@Override
	protected void actionPerformedRemoveValue() {

		int selectedRow = valuesTable.getSelectedRow();
		int rowCount = 0;
		
		
		NodeStateEdit nodeStateEdit = new NodeStateEdit(probNode, 
				StateAction.REMOVE, selectedRow, "");
		try {
			probNode.getProbNet().getPNESupport().announceEdit(nodeStateEdit);
			probNode.getProbNet().getPNESupport().doEdit(nodeStateEdit);
			cancelCellEditing();
			getTableModel().removeRow(selectedRow);
			rowCount = valuesTable.getRowCount();
			if (rowCount > 0) {
				if (selectedRow < rowCount) {
					valuesTable.getSelectionModel().setSelectionInterval(
						selectedRow, selectedRow);
					Object newRow [] = nodeStateEdit.getNewRowOfData();
					int auxSelectedRow = selectedRow;
					while (auxSelectedRow < rowCount) {
						getTableModel().setValueAt(
							getKeyString(auxSelectedRow), auxSelectedRow, 0);
						auxSelectedRow++;
					}
					for (int i=1; i<getTableModel().getColumnCount(); i++){
						getTableModel().setValueAt(newRow[i], selectedRow, i);
					}
				} else {
					valuesTable.getSelectionModel().setSelectionInterval(
						selectedRow - 1, selectedRow - 1);
				}
			}
		} catch (ConstraintViolationException e) {
			JOptionPane.showMessageDialog(this, messageStringResource.getString(
					e.getMessage() ),messageStringResource.getString( 
							e.getMessage() ), JOptionPane.ERROR_MESSAGE );
			
			//jTextFieldNodeName.setText( this.nodeProperties.getName() );
			//jTextFieldNodeName.requestFocus();
			//e.printStackTrace();
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
		} catch (NotEnoughMemoryException e) {
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
		} catch (WrongCriterionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		}
	}

	/**
	 * Invoked when the button 'up' is pressed.
	 */
	@Override
	protected void actionPerformedUpValue() {

		int selectedRow = valuesTable.getSelectedRow();
		Object swap = null;
		
		NodeStateEdit nodeStateEdit = new NodeStateEdit(probNode, 
				StateAction.UP, selectedRow, "");
		
		try {
			probNode.getProbNet().getPNESupport().announceEdit(nodeStateEdit);
			probNode.getProbNet().getPNESupport().doEdit(nodeStateEdit);
			
			stopCellEditing();
			swap = valuesTable.getValueAt(selectedRow, 1);
			valuesTable.setValueAt(
				valuesTable.getValueAt(selectedRow - 1, 1), selectedRow, 1);
			valuesTable.setValueAt(swap, selectedRow - 1, 1);
			valuesTable.getSelectionModel().setSelectionInterval(
				selectedRow - 1, selectedRow - 1);
			
		} catch (ConstraintViolationException e) {
			JOptionPane
			.showMessageDialog(
				this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource
					.getString( e.getMessage() ),
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
		} catch (NotEnoughMemoryException e) {
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
		} catch (WrongCriterionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		}
		
		/*int selectedRow = valuesTable.getSelectedRow();
		Object swap = null;

		stopCellEditing();
		swap = valuesTable.getValueAt(selectedRow, intervalNameColumnNum);
		valuesTable.setValueAt(valuesTable.getValueAt(selectedRow - 1,
														intervalNameColumnNum),
								selectedRow, intervalNameColumnNum);
		valuesTable.setValueAt(swap, selectedRow - 1, intervalNameColumnNum);
		valuesTable.getSelectionModel()
						.setSelectionInterval(selectedRow - 1, selectedRow - 1);*/
	}

	/**
	 * Invoked when the button 'down' is pressed.
	 */
	@Override
	protected void actionPerformedDownValue() {
		int selectedRow = valuesTable.getSelectedRow();
		Object swap = null;
		
		NodeStateEdit nodeStateEdit = new NodeStateEdit(probNode, 
				StateAction.DOWN, selectedRow, "");
		
		try {
			probNode.getProbNet().getPNESupport().announceEdit(nodeStateEdit);
			probNode.getProbNet().getPNESupport().doEdit(nodeStateEdit);
			
			stopCellEditing();
			swap = valuesTable.getValueAt(selectedRow, 1);
			valuesTable.setValueAt(
				valuesTable.getValueAt(selectedRow + 1, 1), selectedRow, 1);
			valuesTable.setValueAt(swap, selectedRow + 1, 1);
			valuesTable.getSelectionModel().setSelectionInterval(
				selectedRow + 1, selectedRow + 1);
								
		} catch (ConstraintViolationException e) {
			JOptionPane
			.showMessageDialog(
				this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource
					.getString( e.getMessage() ),
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
		} catch (NotEnoughMemoryException e) {
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
		} catch (WrongCriterionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		}
		
		
		/*int selectedRow = valuesTable.getSelectedRow();
		Object swap = null;

		stopCellEditing();
		swap = valuesTable.getValueAt(selectedRow, intervalNameColumnNum);
		valuesTable.setValueAt(valuesTable.getValueAt(selectedRow + 1,
														intervalNameColumnNum),
								selectedRow, intervalNameColumnNum);
		valuesTable.setValueAt(swap, selectedRow + 1, intervalNameColumnNum);
		valuesTable.getSelectionModel()
						.setSelectionInterval(selectedRow + 1, selectedRow + 1);*/
	}

	/**
	 * Invoked when the button 'InfinitePositive' is pressed.
	 */
	protected void actionPerformedInfinitePositiveValue() {

		int selectedRow = valuesTable.getSelectedRow();
		int selectedColumn = valuesTable.getSelectedColumn();

		cancelCellEditing();
		if (selectedColumn == Integer.valueOf(dialogStringResource.getString(
				"DiscretizeTableModel.Columns.LowLimitValue.Order"))) {
			valuesTable.setValueAt(Double.POSITIVE_INFINITY, selectedRow,
									selectedColumn);
		}
		if (selectedColumn == Integer.valueOf( dialogStringResource.getString( 
				"DiscretizeTableModel.Columns.UpperLimitValue.Order" ))) {
			valuesTable.setValueAt(Double.POSITIVE_INFINITY, selectedRow,
									selectedColumn);
		}

	}

	/**
	 * Invoked when the button 'InfiniteNegative' is pressed.
	 */
	protected void actionPerformedInfiniteNegativeValue() {

		int selectedRow = valuesTable.getSelectedRow();
		int selectedColumn = valuesTable.getSelectedColumn();

		cancelCellEditing();
		if (selectedColumn == Integer.valueOf(dialogStringResource.getString( 
				"DiscretizeTableModel.Columns.LowLimitValue.Order"))) {
			valuesTable.setValueAt( Double.NEGATIVE_INFINITY, selectedRow,
									selectedColumn);
		}
		if (selectedColumn == Integer.valueOf( dialogStringResource.getString(
				"DiscretizeTableModel.Columns.UpperLimitValue.Order"))) {
			valuesTable.setValueAt(Double.NEGATIVE_INFINITY, selectedRow,
									selectedColumn);
		}
	}

	/**
	 * Invoked when the row selection changes.
	 * 
	 * @param e
	 *            selection event information.
	 */
	@Override
	@SuppressWarnings("unused")
	public void valueChanged(ListSelectionEvent e) {

		int index = valuesTable.getSelectedRow();
		int rowCount = valuesTable.getRowCount();

		super.valueChanged(e);
		if ((valuesTable.getRowCount() == 0)
						|| (valuesTable.getSelectedRow() == -1)) {
			jButtonInfiniteNegativeDouble.setEnabled(false);
			jButtonInfinitePositiveDouble.setEnabled(false);
		} else {
			jButtonInfiniteNegativeDouble.setEnabled(true);
			jButtonInfinitePositiveDouble.setEnabled(true);
		}
	}

	/**
	 * Returns a key represented by an index.
	 * 
	 * @param index
	 *            index of the key which will be returned
	 * @return the string that content the key.
	 */
	protected String getKeyString(int index) {

		return keyPrefix + index;

	}

	/**
	 * Class to manage Discretize Render Table in columns "()" and "[]"
	 * 
	 * @author Alberto Ruiz
	 */
	public class MyComboBoxRenderer extends JComboBox implements
					TableCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public MyComboBoxRenderer(String[] items) {

			super(items);

		}

		public Component getTableCellRendererComponent(JTable table,
														Object value,
														boolean isSelected,
														boolean hasFocus,
														int row, int column) {

			if (isSelected) {
				setForeground(table.getSelectionForeground());
				super.setBackground(table.getSelectionBackground());
			} else {
				setForeground(table.getForeground());
				setBackground(table.getBackground());
			}

			// Select the current value
			setSelectedItem(value);
			return this;
		}
	}

	
	public void tableChanged(TableModelEvent arg0) {
		int column = arg0.getColumn();
		int row = arg0.getLastRow();
		Object aux = arg0.getSource();
		
		
		boolean lower = (column - 1 == lowerLimitSymbolColumnNum ? true: false);
		if (arg0.getType()== TableModelEvent.UPDATE && 
				((DiscretizeTableModel)arg0.getSource()).getValueAt(row, column)
						instanceof Double){
			double newValue = (Double)((DiscretizeTableModel)arg0.getSource()).
				getValueAt(row, column);		
			NodePartitionedIntervalEdit nodePartitionedIntervalEdit = 
				new NodePartitionedIntervalEdit(probNode, StateAction.
						MODIFYVALUEINTERVAL, row, newValue, lower);
			try {
				probNode.getProbNet().getPNESupport().announceEdit(
						nodePartitionedIntervalEdit);
				probNode.getProbNet().getPNESupport().doEdit(
						nodePartitionedIntervalEdit);
				
				if (row > 0 && nodePartitionedIntervalEdit.getLower()){
					valuesTable.setValueAt(probNode.getVariable().
							getPartitionedInterval().getLimit(row), row-1, 
							upperLimitValueColumnNum);
				}else if (row < probNode.getVariable().getStates().length-1)				
					valuesTable.setValueAt(probNode.getVariable().
							getPartitionedInterval().getLimit(row + 1 ), row + 1, 
							lowLimitValueColumnNum);
						
			} catch (ConstraintViolationException e) {
				JOptionPane.showMessageDialog(this, messageStringResource
						.getString( e.getMessage() ),
					messageStringResource.getString( e.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			
				if (nodePartitionedIntervalEdit.getLower()){
					valuesTable.setValueAt(probNode.getVariable().
							getPartitionedInterval().getLimit(row), row, 
							lowLimitValueColumnNum);
				}else				
					valuesTable.setValueAt(probNode.getVariable().
							getPartitionedInterval().getLimit(row + 1 ), row, 
							upperLimitValueColumnNum);
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
			} catch (NotEnoughMemoryException e) {
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
			} catch (WrongCriterionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, messageStringResource
						.getString( e.getMessage() ),
					messageStringResource.getString( e.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			}
		}
		
	}
	public void setEnablePanelButton(boolean b){
		
		if (b){
			addValueButton.setEnabled(b);
		}else {
			addValueButton.setEnabled(b);
			upValueButton.setEnabled(b);
			downValueButton.setEnabled(b);
			removeValueButton.setEnabled(b);
		}
	}

	public void setVisibleButtonPanel(boolean b){
		getButtonPanel().setVisible(b);
	}

	
}
