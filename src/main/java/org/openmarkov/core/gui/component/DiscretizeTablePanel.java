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
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
import org.openmarkov.core.gui.action.PartitionedIntervalEdit;
import org.openmarkov.core.gui.dialog.common.KeyTablePanel;
import org.openmarkov.core.gui.loader.element.IconLoader;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.gui.util.GUIDefaultStates;
import org.openmarkov.core.gui.util.Utilities;
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
	TableModelListener,MouseListener {

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

	private final String infinity = "\u221E";
	private final String minusInfinity = "-"+"\u221E";
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
	 * Button to select variable states.
	 */
	protected JButton standarDomainButton = null;

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
										/*131*/200,
										Short.MAX_VALUE)
								.addComponent(
										getButtonPanel(),
										GroupLayout.PREFERRED_SIZE,
										/*131*/200,
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
		
		DefaultTableCellRenderer statesRender = new DefaultTableCellRenderer();
		statesRender.setHorizontalAlignment(SwingConstants.LEFT);
		
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
		if (probNode.getVariable().getVariableType() == VariableType.FINITE_STATES 
				|| probNode.getVariable().getVariableType() == VariableType.DISCRETIZED) {
			TableColumn aColumn = valuesTable.getColumnModel().getColumn(1);
			aColumn.setCellRenderer(statesRender);
			if (probNode.getVariable().getVariableType() == VariableType.FINITE_STATES) {
				for (int i = 2; i < maxColumn; i++) {
					TableColumn columni = valuesTable.getColumnModel().getColumn(i);
					columni.setCellRenderer(tcr);
					columni.setPreferredWidth(0);
					columni.setMaxWidth(0);
					columni.setMinWidth(0);
					valuesTable.getTableHeader().getColumnModel().getColumn(i)
									.setCellRenderer(tcr);
				}
			}
		}

		// set Columns = Up and Low limits
		if (probNode.getVariable().getVariableType() == VariableType.NUMERIC || probNode.getVariable().getVariableType() == VariableType.DISCRETIZED ) {
			jComboBoxLowerSymbol = getLowerSymbolComboBox ();
			jComboBoxUpperSymbol = getUpperSymbolComboBox ();
	
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

	}
	
	private JComboBox getLowerSymbolComboBox () {
		if (jComboBoxLowerSymbol == null) {
			jComboBoxLowerSymbol = new JComboBox(intervalLowerSymbols);
		}
		return jComboBoxLowerSymbol;
	}

	private JComboBox getUpperSymbolComboBox () {
		if (jComboBoxUpperSymbol == null) {
			jComboBoxUpperSymbol = new JComboBox(intervalUpperSymbols);
		}
		return jComboBoxUpperSymbol;
	}
	/**
	 * Method to define the specific listeners in this table (not defined in the
	 * common KeyTable hierarchy
	 */
	protected void defineTableSpecificListeners() {

		valuesTable.addMouseListener(this); 
	}

	/**
	 * This method is used to change the interval's type in a discretize Table 
	 * To closed from opened 
	 * To opened from closed
	 */
	private void changeLimitIntervalDiscretize(int row, int column) {
		
		if (column == lowerLimitSymbolColumnNum
						|| column == upperLimitSymbolColumnNum) {
			boolean lower = false;
			String aux = (String) valuesTable.getValueAt(row, column);
			if (column == lowerLimitSymbolColumnNum) {
				lower = true;
				if (aux.equals("(")) {
					//double lowerLimit = (Double)valuesTable.getValueAt(row, column +1);
					//if ( lowerLimit == Double.NEGATIVE_INFINITY || lowerLimit == Double.POSITIVE_INFINITY) {
					if ( valuesTable.getValueAt(row, lowLimitValueColumnNum) == infinity || valuesTable.getValueAt(row, lowLimitValueColumnNum) == minusInfinity) {
						JOptionPane.showMessageDialog(this, "Infinity can not belong to the interval");
					} else if (row-1>=0 && valuesTable.getValueAt(row-1, lowerLimitSymbolColumnNum) == "[" 
							&&  valuesTable.getValueAt(row-1, lowLimitValueColumnNum) == valuesTable.getValueAt(row-1, upperLimitValueColumnNum)) {
						JOptionPane.showMessageDialog(this, "Not permitted action. You must change limit values first");
					} else {
						valuesTable.setValueAt("[", row, column);
						if (row > 0){
							valuesTable.setValueAt(")", row-1, 
									upperLimitSymbolColumnNum);
						} 
						NodePartitionedIntervalEdit nodePartitionedIntervalEdit = 
								new NodePartitionedIntervalEdit(probNode, StateAction.
										MODIFYDELIMITERINTERVAL, row, lower);
							try {
								probNode.getProbNet().getPNESupport().announceEdit(
										nodePartitionedIntervalEdit);
								probNode.getProbNet().getPNESupport().doEdit(
										nodePartitionedIntervalEdit);
							} catch (ConstraintViolationException e) {
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
					//checkIntervalDiscretize("[", fila, columna,upMonotony);
				} else if (aux.equals("[")) {
					if (valuesTable.getValueAt(row, lowLimitValueColumnNum) == valuesTable.getValueAt(row, upperLimitValueColumnNum) 
							&& valuesTable.getValueAt(row, upperLimitSymbolColumnNum) == "]") {
						JOptionPane.showMessageDialog(this, "Not permitted action. You must change limit values first");
					} else {
						valuesTable.setValueAt("(", row, column);
						if (row > 0){
							valuesTable.setValueAt("]", row-1, 
									upperLimitSymbolColumnNum);
						}
						NodePartitionedIntervalEdit nodePartitionedIntervalEdit = 
								new NodePartitionedIntervalEdit(probNode, StateAction.
										MODIFYDELIMITERINTERVAL, row, lower);
							try {
								probNode.getProbNet().getPNESupport().announceEdit(
										nodePartitionedIntervalEdit);
								probNode.getProbNet().getPNESupport().doEdit(
										nodePartitionedIntervalEdit);
							} catch (ConstraintViolationException e) {
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
					//checkIntervalDiscretize("(", fila, columna,upMonotony);
				}
			}
			if (column == upperLimitSymbolColumnNum) {
				if (aux.equals(")")) {
					//double upperLimit = (Double)valuesTable.getValueAt(row, column -1);
					//if ( upperLimit == Double.NEGATIVE_INFINITY || upperLimit == Double.POSITIVE_INFINITY) {
					if ( valuesTable.getValueAt(row, upperLimitValueColumnNum) == infinity || valuesTable.getValueAt(row, upperLimitValueColumnNum) == minusInfinity) {
						JOptionPane.showMessageDialog(this, "Infinity can not belong to the interval");
					} else if (row+1 <= probNode.getVariable().getStates().length-1 && valuesTable.getValueAt(row+1, upperLimitSymbolColumnNum) == "]" 
							&&  valuesTable.getValueAt(row+1, lowLimitValueColumnNum) == valuesTable.getValueAt(row+1, upperLimitValueColumnNum)) {
						JOptionPane.showMessageDialog(this, "Not permitted action. You must change limit values first");
					} else {
						valuesTable.setValueAt("]", row, column);
						if (row < valuesTable.getRowCount()-1){
							valuesTable.setValueAt("(", row + 1, 
									lowerLimitSymbolColumnNum);
						}
						NodePartitionedIntervalEdit nodePartitionedIntervalEdit = 
								new NodePartitionedIntervalEdit(probNode, StateAction.
										MODIFYDELIMITERINTERVAL, row, lower);
							try {
								probNode.getProbNet().getPNESupport().announceEdit(
										nodePartitionedIntervalEdit);
								probNode.getProbNet().getPNESupport().doEdit(
										nodePartitionedIntervalEdit);
							} catch (ConstraintViolationException e) {
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
					//checkIntervalDiscretize("]", fila, columna,upMonotony);
				} else if(aux.equals("]")) {
					if (valuesTable.getValueAt(row, lowLimitValueColumnNum) == valuesTable.getValueAt(row, upperLimitValueColumnNum) 
							&& valuesTable.getValueAt(row, lowerLimitSymbolColumnNum) == "[") {
						JOptionPane.showMessageDialog(this, "Not permitted action. You must change limit values first");
					} else {
						valuesTable.setValueAt(")", row, column);
						if (row < valuesTable.getRowCount()-1){
							valuesTable.setValueAt("[", row + 1, 
									lowerLimitSymbolColumnNum);
						}
						NodePartitionedIntervalEdit nodePartitionedIntervalEdit = 
								new NodePartitionedIntervalEdit(probNode, StateAction.
										MODIFYDELIMITERINTERVAL, row, lower);
							try {
								probNode.getProbNet().getPNESupport().announceEdit(
										nodePartitionedIntervalEdit);
								probNode.getProbNet().getPNESupport().doEdit(
										nodePartitionedIntervalEdit);
								
							} catch (ConstraintViolationException e) {
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
					//checkIntervalDiscretize(")", fila, columna,upMonotony);
				}
			}
			
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
									.addComponent( getStandarDomainButton(),
												GroupLayout.DEFAULT_SIZE,
												55,
												Short.MAX_VALUE)
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
					    .addComponent(getStandarDomainButton())
						.addGap(5, 5, 5)
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
	 * This method initializes upValueButton.
	 * 
	 * @return a new up value button.
	 */
	public JButton getStandarDomainButton() {

		if (standarDomainButton == null) {
			standarDomainButton = new JButton();
			standarDomainButton.setName( "KeyTablePanel.standarDomainButton" );
			standarDomainButton.setText( stringResource.getString( "StandarDomain.Text.Label") );
			/*standarDomainButton.setMnemonic( stringResource.getString(
				"StandarDomain.Text.Mnemonic" ).charAt( 0 ) );
			.setIcon( iconLoader
				.load( IconLoader.ICON_ARROW_UP_ENABLED ) );*/
			standarDomainButton.setVisible( true );
			standarDomainButton.setEnabled( true );
			standarDomainButton.setActionCommand("StandarDomain");
			//standarDomainButton.addActionListener( this );
		}
		return standarDomainButton;
	}

	/**
	 * This method initializes infinitePositiveDoubleButton.
	 * 
	 * @return a new positive infinite value button.
	 */
	public JButton getInfinitePositiveDoubleButton() {

		if (jButtonInfinitePositiveDouble == null) {
			jButtonInfinitePositiveDouble = new JButton();
			jButtonInfinitePositiveDouble
							.setName("DiscretizeTablePanel.jButtonInfinitePositiveDouble");
			jButtonInfinitePositiveDouble.setText(stringResource
							.getString("InfinitePositive.Text.Label"));
			jButtonInfinitePositiveDouble.setIcon(iconLoader
							.load(IconLoader.ICON_INFINITE_POSITIVE_ENABLED));
			//jButtonInfinitePositiveDouble.setVisible(reorderEnabled);
			jButtonInfinitePositiveDouble.setEnabled(false);
			jButtonInfinitePositiveDouble.addActionListener(this);
			jButtonInfinitePositiveDouble.setVisible(false);
			
		}
		return jButtonInfinitePositiveDouble;
	}

	/**
	 * This method initializes infiniteNegativeDoubleButton.
	 * 
	 * @return a new negative infinite value button.
	 */
	public JButton getInfiniteNegativeDoubleButton() {

		if (jButtonInfiniteNegativeDouble == null) {
			jButtonInfiniteNegativeDouble = new JButton();
			jButtonInfiniteNegativeDouble
							.setName("DiscretizeTablePanel.jButtonInfiniteNegativeDouble");
			jButtonInfiniteNegativeDouble.setText(stringResource
							.getString("InfiniteNegative.Text.Label"));
			jButtonInfiniteNegativeDouble.setIcon(iconLoader
							.load(IconLoader.ICON_INFINITE_NEGATIVE_ENABLED));
			//jButtonInfiniteNegativeDouble.setVisible(reorderEnabled);
			jButtonInfiniteNegativeDouble.setEnabled(false);
			jButtonInfiniteNegativeDouble.addActionListener(this);
			jButtonInfiniteNegativeDouble.setVisible(false);
		}
		return jButtonInfiniteNegativeDouble;
	}
	/**
	 * 
	 */
	public int getLowerLimitSymbolColumnNum() {
		return lowerLimitSymbolColumnNum;
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
	
	/*public void setNewData(Object[][] newData) {
		if (newData != null) {
			
			discretizeTableModel = new DiscretizeTableModel(data, columns);
			valuesTable.setModel(discretizeTableModel);
			valuesTable.getModel().addTableModelListener(this);
			this.defineTableLookAndFeel();
		}
	}*/

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
		for (int i = 0; i < rows; i++ ){
			intervalTable[i][0] = GUIDefaultStates.getString(reorderedStates[i].getName());
		}
		
		setData(intervalTable);
	}
	
	public void setDataFromPartitionedInterval( PartitionedInterval partitionInterval) {
			
		Object[][] data;
		int i = 0;
		int numIntervals = 0;
		int numColumns = 6; // name-symbol-value-separator-value-symbol
		String[] limits;
		boolean[] belongsToLeftSide;

		numIntervals = partitionInterval.getNumSubintervals();
		double values[] =  partitionInterval.getLimits();
		limits = convertToStringLimitValues(values, Double.toString(probNode.getVariable().getPrecision()));
		belongsToLeftSide = partitionInterval.getBelongsToLeftSide();
		data = new Object[numIntervals][numColumns];
		State states [] = probNode.getVariable().getStates();
		State reorderedStates [] = states.clone();
		Collections.reverse(Arrays.asList(reorderedStates));
		
		for (i = 0; i < numIntervals; i++) {
			//for (i = numIntervals-1; i <=0; i--) {
			data[i][0] = GUIDefaultStates.getString(reorderedStates[i].getName()); // name
			data[i][1] = (belongsToLeftSide[i] ? "(" : "["); // low interval
																// symbol
			data[i][2] = limits[i]; // low interval value
			data[i][3] = ","; // separator ","
			data[i][4] = limits[i + 1]; // high interval value
			data[i][5] = (belongsToLeftSide[i + 1] ? "]" : ")"); // high
																	// interval
																	// symbol
		}
	
			setData(data);
		}

	public String[] convertToStringLimitValues(double []limits, String precision) {
		String []tableLimits = new String[limits.length];
		String rounded = "";
	
		int numDecimals;
		
		int indexE = precision.indexOf('E');
		 if (indexE != -1) {
       	 numDecimals = Integer.parseInt(precision.substring(indexE +2, indexE +3));
		 } else {
			 int decimalPoint = precision.indexOf('.');
	         int one = precision.indexOf('1');
		         if (decimalPoint != -1 && one != -1) {
		        	 numDecimals = one - decimalPoint ;
		         } else {
		        	 numDecimals = 0;
		         }
		 }
		 
		
		for (int i = 0; i < limits.length; i++) {
			if (limits[i] == Double.POSITIVE_INFINITY) {
				tableLimits[i] = infinity;
			} else if (limits[i] == Double.NEGATIVE_INFINITY) {
				tableLimits[i] =  minusInfinity;
			} else {
				rounded = Double.toString(limits[i]);
				//adding final zeros
				int roundedStringDecimalPlace = rounded.indexOf('.');
				 if (roundedStringDecimalPlace == -1) {
					 rounded += ".0";
				 }
				 roundedStringDecimalPlace = rounded.indexOf('.');
		         int finalLength = roundedStringDecimalPlace + numDecimals + 1;
		         if (finalLength <= rounded.length()) {
		        	 rounded = rounded.substring(0, finalLength);
		         } else {
		                 while (finalLength > rounded.length()) {
		                	 rounded += "0";
		                 }
		         }
				// rounded = rounded.replace(',', '.');
		         tableLimits[i] = rounded;
			}
			
		}
		
		return tableLimits;
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
		}/* else if (e.getSource().equals(this.standarDomainButton)) {
			actionPerformedStandarDomain();
		}*/
	}

	/*protected void actionPerformedStandarDomain() {
		StandarDomainsDialog standarDomainDialog = new StandarDomainsDialog(Utilities.getOwner(this));
		if (standarDomainDialog.requestValues() == NodePropertiesDialog.OK_BUTTON) {
		
			 
			 ArrayList<JRadioButton> radioButtons = ((StandarDomainPanel)(standarDomainDialog.getJPanelStandarDomains())).getRadioButtons();
			 int index = 0;
			 String states;
			 
			 for (int j = 0; j < radioButtons.size(); j++) {
				 if (radioButtons.get(j).isSelected()) {
					 index = j;
					 states = radioButtons.get(j).getName();
				 }
			 }
			int i= 0;
			State [] newStates = new State[DefaultStates.getByIndex(index).length];
			for (String str : DefaultStates.getByIndex(index)){
				newStates[i] = new State(str);
				i++;
			}
			NodeReplaceStatesEdit nodeReplaceStatesEdit = 
					new NodeReplaceStatesEdit(probNode,newStates);
				try {
					probNode.getProbNet().getPNESupport().announceEdit(
							nodeReplaceStatesEdit);
					probNode.getProbNet().getPNESupport().doEdit(
							nodeReplaceStatesEdit);
					this.removeAll();
					try {
						initialize();
						setFieldsFromProperties(probNode);
					
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						JOptionPane.showMessageDialog(null, messageStringResource
								.getString( e.getMessage() ),
							messageStringResource.getString( e.getMessage() ),
							JOptionPane.ERROR_MESSAGE );
					}
				} catch (ConstraintViolationException e) {
					// TODO Auto-generated catch block
					
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (CanNotDoEditException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (DoEditException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (NotEnoughMemoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (NonProjectablePotentialException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (WrongCriterionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				}
		}
	}*/
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
				if (probNode.getVariable().getVariableType() == VariableType.DISCRETIZED) {
					PartitionedInterval newPartitionedInterval = probNode.getVariable().getPartitionedInterval();
					setDataFromPartitionedInterval(newPartitionedInterval);
					valuesTable.getSelectionModel().setSelectionInterval(newIndex, newIndex);
				} else {
				//Object newRow [] = nodeStateEdit.getNewRowOfData();
				//newRow [0] = getKeyString(newIndex);
				//getTableModel().insertRow(newIndex, newRow); 
				getTableModel().insertRow(newIndex, new Object[] {getKeyString(newIndex), option });
				valuesTable.getSelectionModel().setSelectionInterval(newIndex, newIndex);
				}
				
				
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
		removeState(selectedRow);
		
		
		
	}
	/**
	 * 
	 * @param selectedRow
	 */
	protected void removeState(int selectedRow) {
		int rowCount = 0;
		String lowerSymbol = "(";
		//if (selectedRow < probNode.getVariable().getStates().length -2) {
			if (selectedRow > 0) {
				lowerSymbol = (valuesTable.getValueAt(selectedRow -1, upperLimitSymbolColumnNum ) == ")") ? "[":"(";
			}
			//} 
			
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
					//Object newRow [] = nodeStateEdit.getNewRowOfData();
					/*Object newRow [] = {"",  valuesTable.getValueAt(selectedRow +1, 1) , lowerSymbol,
							valuesTable.getValueAt(selectedRow +1, 3), "," , valuesTable.getValueAt(selectedRow +1, 5), 
							valuesTable.getValueAt(selectedRow +1, 6)};*/
					//update key column
					int auxSelectedRow = selectedRow;
					while (auxSelectedRow < rowCount) {
						getTableModel().setValueAt(
							getKeyString(auxSelectedRow), auxSelectedRow, 0);
						auxSelectedRow++;
					}
					//update values of the table 
					/*for (int i=1; i<getTableModel().getColumnCount(); i++){
						getTableModel().setValueAt(newRow[i], selectedRow, i);
					}*/
				} else {
					valuesTable.getSelectionModel().setSelectionInterval(
						selectedRow - 1, selectedRow - 1);
				}
				//after eliminate row check the lower limit
				if (selectedRow > 0) {
					if (valuesTable.getValueAt(selectedRow-1, upperLimitSymbolColumnNum) == "]" ) {
						valuesTable.setValueAt("(", selectedRow, lowerLimitSymbolColumnNum);
					} else {
						valuesTable.setValueAt("[", selectedRow, lowerLimitSymbolColumnNum);
					}
					valuesTable.setValueAt(valuesTable.getValueAt(selectedRow-1, upperLimitValueColumnNum), selectedRow, lowLimitValueColumnNum);
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
	//if variable is numeric or discretized it is necessary also to  change partitioned interval values
		//TODO
		
	
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
			cancelCellEditing();
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
			cancelCellEditing();
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
		/*if (selectedColumn == Integer.valueOf(dialogStringResource.getString(
				"DiscretizeTableModel.Columns.LowLimitValue.Order"))) {
			valuesTable.setValueAt(Double.POSITIVE_INFINITY, selectedRow,
									selectedColumn);
		}
		if (selectedColumn == Integer.valueOf( dialogStringResource.getString( 
				"DiscretizeTableModel.Columns.UpperLimitValue.Order" ))) {
			valuesTable.setValueAt(Double.POSITIVE_INFINITY, selectedRow,
									selectedColumn);
		}*/
		double []limits = probNode.getVariable().getPartitionedInterval().getLimits();
		boolean []belongs = probNode.getVariable().getPartitionedInterval().getBelongsToLeftSide();
		
		limits [limits.length -1] = Double.POSITIVE_INFINITY;
		belongs [limits.length -1] = false;
		
		PartitionedInterval newPartitionedInterval = new PartitionedInterval(limits, belongs);
		
		PartitionedIntervalEdit partitionedIntervalEdit = new PartitionedIntervalEdit(probNode, newPartitionedInterval);
		try {
			probNode.getProbNet().getPNESupport().announceEdit(
					partitionedIntervalEdit);
		
			probNode.getProbNet().getPNESupport().doEdit(
						partitionedIntervalEdit);
		} catch (DoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (NotEnoughMemoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConstraintViolationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CanNotDoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NonProjectablePotentialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WrongCriterionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		valuesTable.setValueAt(infinity, selectedRow,
				selectedColumn);
	}

	/**
	 * Invoked when the button 'InfiniteNegative' is pressed.
	 */
	protected void actionPerformedInfiniteNegativeValue() {

		int selectedRow = valuesTable.getSelectedRow();
		int selectedColumn = valuesTable.getSelectedColumn();

		cancelCellEditing();
		/*if (selectedColumn == Integer.valueOf(dialogStringResource.getString( 
				"DiscretizeTableModel.Columns.LowLimitValue.Order"))) {
			valuesTable.setValueAt( Double.NEGATIVE_INFINITY, selectedRow,
									selectedColumn);
		}
		if (selectedColumn == Integer.valueOf( dialogStringResource.getString(
				"DiscretizeTableModel.Columns.UpperLimitValue.Order"))) {
			valuesTable.setValueAt(Double.NEGATIVE_INFINITY, selectedRow,
									selectedColumn);
		}*/
		double []limits = probNode.getVariable().getPartitionedInterval().getLimits();
		boolean []belongs = probNode.getVariable().getPartitionedInterval().getBelongsToLeftSide();
		
		limits [0] = Double.NEGATIVE_INFINITY;
		belongs [0] = true;
		
		PartitionedInterval newPartitionedInterval = new PartitionedInterval(limits, belongs);
		
		PartitionedIntervalEdit partitionedIntervalEdit = new PartitionedIntervalEdit(probNode, newPartitionedInterval);
		try {
			probNode.getProbNet().getPNESupport().announceEdit(
					partitionedIntervalEdit);
		
			probNode.getProbNet().getPNESupport().doEdit(
						partitionedIntervalEdit);
		} catch (DoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (NotEnoughMemoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConstraintViolationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CanNotDoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NonProjectablePotentialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WrongCriterionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		valuesTable.setValueAt(minusInfinity, selectedRow,
				selectedColumn);
	}

	/**
	 * Invoked when the row selection changes.
	 * 
	 * @param e
	 *            selection event information.
	 */
	/*@Override
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
	}*/

	/**
	 * Returns a key represented by an index.
	 * 
	 * @param index
	 *            index of the key which will be returned
	 * @return the string that content the key.
	 */
	private String getKeyString(int index) {

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

	
	public void tableChanged(TableModelEvent tableEvent) {
		int column = tableEvent.getColumn();
		int row = tableEvent.getLastRow();
		//Object aux = tableEvent.getSource();
		
		//if (probNode.getVariable().getVariableType() == VariableType.DISCRETIZED) {
		boolean lower = (column - 1 == lowerLimitSymbolColumnNum ? true: false);
		if (tableEvent.getType()== TableModelEvent.UPDATE && 
				((DiscretizeTableModel)tableEvent.getSource()).getValueAt(row, column)
				instanceof String && column == 1) {
			String newName = (String) ((DiscretizeTableModel)tableEvent.getSource()).
					getValueAt(row, column);
			NodeStateEdit nodeStateEdit = new NodeStateEdit(probNode, StateAction.RENAME, row, newName);
			
			try {
				probNode.getProbNet().getPNESupport().doEdit(
						nodeStateEdit);
				probNode.getProbNet().getPNESupport().announceEdit(
						nodeStateEdit);
			
			} catch (NotEnoughMemoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DoEditException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NonProjectablePotentialException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WrongCriterionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ConstraintViolationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CanNotDoEditException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		if (tableEvent.getType()== TableModelEvent.UPDATE && 
				((DiscretizeTableModel)tableEvent.getSource()).getValueAt(row, column) == infinity && column == upperLimitValueColumnNum) {
			//valuesTable.setValueAt(Double.POSITIVE_INFINITY, row, column);
			valuesTable.setValueAt(infinity, row, column);
		} else if (tableEvent.getType()== TableModelEvent.UPDATE && 
				((DiscretizeTableModel)tableEvent.getSource()).getValueAt(row, column) == minusInfinity && column == lowLimitValueColumnNum) {
			valuesTable.setValueAt(minusInfinity, row, column);
		} else if (tableEvent.getType()== TableModelEvent.UPDATE && 
				((DiscretizeTableModel)tableEvent.getSource()).getValueAt(row, column)
						instanceof Double){
			
			double newValue = (Double)((DiscretizeTableModel)tableEvent.getSource()).
				getValueAt(row, column);	
			//setting precision to the new value according with the precision value introduced by the user
			double precision = probNode.getVariable().getPrecision();
			double roundedValue = Utilities.roundWithPrecision(newValue, Double.toString(precision));
			
			double [] currentLimits = probNode.getVariable().getPartitionedInterval().getLimits();
			boolean []currentBelongs = probNode.getVariable().getPartitionedInterval().getBelongsToLeftSide();
			
			
			int limitsIndex;
			if ( lower ){
				limitsIndex=row;
			}else{
				limitsIndex = row + 1;
			}
			
			 // posterious limits
			int i = limitsIndex;
			currentLimits[i] = roundedValue;
				
			while (i+1 <= currentLimits.length-1 && currentLimits[i] >= currentLimits[i+1]) {
					
				if (currentBelongs[i] == false && currentBelongs[i+1] == true) {
					currentLimits[i+1] = currentLimits[i];
				} else {
					if (i+1 == currentLimits.length-1){
						currentLimits[i+1] = Double.POSITIVE_INFINITY;
						break;
					} else 
						currentLimits[i+1] = currentLimits[i] + precision;
				}
					
					i++;
				}
		
			
		//previous limits
			int k = limitsIndex;
			while (k-1 >=0 && currentLimits[k] <= currentLimits[k-1]) {
				if (currentBelongs[k] == true && currentBelongs[k-1] == false) {
					currentLimits[k-1] = currentLimits[k];
				}  else {
					if (k-1 == 0){
						currentLimits[k-1] = Double.NEGATIVE_INFINITY;
						break;
					} else 
						currentLimits[k-1] = currentLimits[k] - precision;
				}
				k--;
			}
			
			for (int m = 0 ; m < currentLimits.length; m++) {
				if (currentLimits[m] != Double.POSITIVE_INFINITY && currentLimits[m] != Double.NEGATIVE_INFINITY) {
					currentLimits[m] = Utilities.roundWithPrecision(currentLimits[m], Double.toString(precision));
				}
			}
			
		
		PartitionedInterval newPartitionedInterval = new PartitionedInterval(currentLimits, currentBelongs);
		
		PartitionedIntervalEdit partitionedIntervalEdit = new PartitionedIntervalEdit(probNode, newPartitionedInterval);
		try {
			probNode.getProbNet().getPNESupport().announceEdit(
					partitionedIntervalEdit);
		
			probNode.getProbNet().getPNESupport().doEdit(
						partitionedIntervalEdit);
		} catch (DoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (NotEnoughMemoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConstraintViolationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CanNotDoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NonProjectablePotentialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WrongCriterionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		PartitionedInterval newPartitionInterval = probNode.getVariable().
				getPartitionedInterval();
		setDataFromPartitionedInterval(newPartitionInterval);	
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



	public void mouseClicked(MouseEvent e) {
		int row = valuesTable.rowAtPoint(e.getPoint());
		int column = valuesTable.columnAtPoint(e.getPoint());
		//if ((row > -1) && (column > -1)) {
		if (probNode.getVariable().getVariableType() == VariableType.NUMERIC ||probNode.getVariable().getVariableType() == VariableType.DISCRETIZED){
		if (column == lowerLimitSymbolColumnNum
				|| column == upperLimitSymbolColumnNum) {
				changeLimitIntervalDiscretize(row, column);
		} else if (column == lowLimitValueColumnNum	|| column == upperLimitValueColumnNum) {
		//infinity buttons management
		
		PartitionedInterval interval = probNode.getVariable().getPartitionedInterval();
		int numIntervals = interval.getNumSubintervals();
		if (!isUpMonotony()) {
			if (row == 0 && column == 3 
					/*&& valuesTable.getValueAt(0, 3) !=  minusInfinity*/) {
				getInfiniteNegativeDoubleButton().setVisible(true);
				getInfiniteNegativeDoubleButton().setEnabled(true);
				getInfinitePositiveDoubleButton().setVisible(false);
				getInfinitePositiveDoubleButton().setEnabled(false);
			} else if (row == numIntervals-1 && column == 5
					/*&& valuesTable.getValueAt(numIntervals-1, 5) !=  infinity*/) {
				getInfinitePositiveDoubleButton().setVisible(true);
				getInfinitePositiveDoubleButton().setEnabled(true);
				getInfiniteNegativeDoubleButton().setVisible(false);
				getInfiniteNegativeDoubleButton().setEnabled(false);
			
			}else  {
				getInfiniteNegativeDoubleButton().setVisible(false);
				getInfinitePositiveDoubleButton().setVisible(false);
				getInfiniteNegativeDoubleButton().setEnabled(false);
				getInfinitePositiveDoubleButton().setEnabled(false);
			}
			
		} else if (isUpMonotony()) {
			if (row == 0 && column == 5
					/* && valuesTable.getValueAt(0, 5) !=  infinity*/) {
				getInfinitePositiveDoubleButton().setVisible(true);
				getInfinitePositiveDoubleButton().setEnabled(true);
				getInfiniteNegativeDoubleButton().setVisible(false);
				getInfiniteNegativeDoubleButton().setEnabled(false);
			} else if (row == numIntervals-1 && column == 3
					/*&& valuesTable.getValueAt(numIntervals-1, 3) != minusInfinity*/) {
				getInfiniteNegativeDoubleButton().setVisible(true);
				getInfiniteNegativeDoubleButton().setEnabled(true);
				getInfinitePositiveDoubleButton().setVisible(false);
				getInfinitePositiveDoubleButton().setEnabled(false);
			}
			else  {
				getInfiniteNegativeDoubleButton().setVisible(false);
				getInfinitePositiveDoubleButton().setVisible(false);
				getInfiniteNegativeDoubleButton().setEnabled(false);
				getInfinitePositiveDoubleButton().setEnabled(false);
			}
		}
		}
		}
	}

	


	public void mouseEntered(MouseEvent e) {
		
		
	}



	public void mouseExited(MouseEvent e) {
	
		
	}



	public void mousePressed(MouseEvent e) {
	
	}



	public void mouseReleased(MouseEvent e) {
		
		
	}

	
}
