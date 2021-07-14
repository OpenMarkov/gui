/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.dialog.common;

import java.util.LinkedHashMap;

import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.openmarkov.gui.action.OtherPropertyEdit;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.ProbNet;


/**
 * This class implements a key table with the following features:
 * <ul>
 * <li>Its elements, except the first column, are modifiable.</li>
 * <li>New elements can be added, creating a new key row with empty data.</li>
 * <li>The key data (first column) consist of a key string following of the
 * index of the row.</li>
 * <li>The information of a row (except the first column) can be taken up or
 * down.</li>
 * <li>The rows can be removed.</li>
 * </ul>
 *
 * @author jmendoza
 * @version 1.0 jmendoza
 */
public class PrefixedOtherPropertiesTablePanel extends KeyTablePanel implements TableModelListener {
	/**
	 * Static field for serializable class.
	 */
	private static final long serialVersionUID = 8550762264755243008L;
	/**
	 * Key prefix.
	 */
	private String keyPrefix = null;
	
	private Node node = null;
	
	private ProbNet probNet = null;
		
	
	/**
	 * this a default constructor with no construction parameters
	 */
	public PrefixedOtherPropertiesTablePanel() {
		keyPrefix = "";
		initialize();
	}

	/**
	 * This is the default constructor
	 *
	 * @param newColumns   array of texts that appear in the header of the columns.
	 * @param noKeyData    content of the cells except the first column.
	 * @param newKeyPrefix prefix of the keys of each row that appear in the first
	 *                     column.
	 */
	public PrefixedOtherPropertiesTablePanel(String[] newColumns, Object[][] noKeyData, String newKeyPrefix,
			boolean firstColumnHidden) {// , ElementObservable notifier) {
		super(newColumns, new Object[0][0], true, true);// , notifier);
		keyPrefix = newKeyPrefix;
		initialize();
		getValuesTable().setFirstColumnHidden(firstColumnHidden);
		setData(noKeyData);
		tableModel.addTableModelListener(this);
		//valuesTable.getModel().addTableModelListener(this);
		//getValuesTable().getModel().addTableModelListener(this);
		//getTableModel().addTableModelListener(this);
	}
	
	/**
	 * This is the constructor when called from a Node Properties Dialog
	 *
	 * @param newColumns   array of texts that appear in the header of the columns.
	 * @param noKeyData    content of the cells except the first column.
	 * @param newKeyPrefix prefix of the keys of each row that appear in the first
	 *                     column.
	 */
	public PrefixedOtherPropertiesTablePanel(String[] newColumns, Object[][] noKeyData, String newKeyPrefix,
			boolean firstColumnHidden, Node node) {// , ElementObservable notifier) {
		super(newColumns, new Object[0][0], true, true);// , notifier);
		this.node = node;
		keyPrefix = newKeyPrefix;
		initialize();
		getValuesTable().setFirstColumnHidden(firstColumnHidden);
		setData(noKeyData);
		tableModel.addTableModelListener(this);
		//valuesTable.getModel().addTableModelListener(this);
		//getValuesTable().getModel().addTableModelListener(this);
		//getTableModel().addTableModelListener(this);
	}
	
	/**
	 * This is the constructor when called from a Network Properties Dialog
	 *
	 * @param newColumns   array of texts that appear in the header of the columns.
	 * @param noKeyData    content of the cells except the first column.
	 * @param newKeyPrefix prefix of the keys of each row that appear in the first
	 *                     column.
	 */
	public PrefixedOtherPropertiesTablePanel(String[] newColumns, Object[][] noKeyData, String newKeyPrefix,
			boolean firstColumnHidden, ProbNet probNet) {// , ElementObservable notifier) {
		super(newColumns, new Object[0][0], true, true);// , notifier);
		this.probNet = probNet;
		keyPrefix = newKeyPrefix;
		initialize();
		getValuesTable().setFirstColumnHidden(firstColumnHidden);
		setData(noKeyData);
		tableModel.addTableModelListener(this);
		//valuesTable.getModel().addTableModelListener(this);
		//getValuesTable().getModel().addTableModelListener(this);
		//getTableModel().addTableModelListener(this);
	}
	
	/**
	 * This method takes a data object and creates a new column that content a
	 * row key. This key begins with the key prefix following a number that
	 * starts at 0.
	 *
	 * @param oldData data to add a key column.
	 * @return a data object with one more column that contains the keys.
	 */
	private Object[][] fillDataKeys(Object[][] oldData) {

		Object[][] newData = null;
		int i1 = 0;
		int i2 = 0;
		int l1 = 0;
		int l2 = 0;

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
	 * Returns a key represented by an index.
	 *
	 * @param index index of the key which will be returned
	 * @return the string that content the key.
	 */
	public String getKeyString(int index) {
		return keyPrefix + index;
	}
	
	/**
	 * Sets a new table model with new data.
	 *
	 * @param noKeyData new data for the table without the key column.
	 */
	@Override public void setData(Object[][] noKeyData) {
	///@Override public void setData(Object[][] newData) {
		data = fillDataKeys(noKeyData);
		tableModel = null;
		//tableModel.setDataVector(data, columns);
		valuesTable.setModel(getTableModel());
		// valuesTable.getModel().addTableModelListener(this);
	}
	
	/**
	 * Set the node additionalProperties in this panel with the provided ones
	 *
	 * @param nodeProperties the Node to get the properties from
	 */
	public void setProperties (Node node) {
		this.node = node;
		LinkedHashMap<String,String> otherProperties = node.getOtherProperties();
		setFieldsFromProperties(otherProperties);
	}
	
	/**
	 * Set the network additionalProperties in this panel with the provided ones
	 *
	 * @param probNetProperties the ProbNet to get the properties from
	 */
	public void setProperties (ProbNet probNet) {
		this.probNet = probNet;
		LinkedHashMap<String,String> otherProperties = probNet.getOtherProperties();
		setFieldsFromProperties(otherProperties);
	}
	
	public void setProperties () {
		if (node != null) {
			LinkedHashMap<String,String> otherProperties = node.getOtherProperties();
			setFieldsFromProperties(otherProperties);
		} else if (probNet != null) {
			LinkedHashMap<String,String> otherProperties = probNet.getOtherProperties();
			setFieldsFromProperties(otherProperties);
		}
	}
	
	public void setFieldsFromProperties(LinkedHashMap<String,String> otherProperties) {
		Object[] keys = otherProperties.keySet().toArray();
		Object[] values = otherProperties.values().toArray();
		int s = otherProperties.size();
		Object [][] data = new String [s][2];
		for (int i = 0; i < s; i++) {
			data[i][0] = keys[i].toString();
			data[i][1] = values[i].toString();
		}
		
		setData(data);
		// getOtherPropertiesTablePanel()
		// .setData( additionalProperties.getOtherProperties() );
	}
	
	/**
	 * Invoked when the button 'add' is pressed.
	 */
	@Override protected void actionPerformedAddValue() {
		String propertyName = JOptionPane.showInputDialog(this, stringDatabase.getString("AddOtherProperty.Name.Message"),
				stringDatabase.getString("AddOtherProperty.Name.Title"), JOptionPane.QUESTION_MESSAGE);
		if (propertyName != null) {
			String propertyValue = JOptionPane.showInputDialog(this, stringDatabase.getString("AddOtherProperty.Value.Message"),
					stringDatabase.getString("AddOtherProperty.Value.Title"), JOptionPane.QUESTION_MESSAGE);
			int newIndex = valuesTable.getRowCount();
			String propertyID = getKeyString(newIndex);
			
			try {
				int selectedRowIndex = valuesTable.getSelectedRow();
				int rowCount = valuesTable.getRowCount();
				String[] rowData = { propertyID, propertyName, propertyValue};
				tableModel.addRow(rowData);	// Add row to the end of the model
				//tableModel.moveRow(rowCount, rowCount, selectedRowIndex + 1);
				//valuesTable.setRowSelectionInterval(selectedRowIndex + 1, selectedRowIndex + 1);
				valuesTable.setRowSelectionInterval(rowCount, rowCount);
							
				String[] noIDrowData = { propertyName, propertyValue};
				
				if (node != null) {
					OtherPropertyEdit otherPropertyEdit = new OtherPropertyEdit(node, "ADD", selectedRowIndex, noIDrowData);
					node.getProbNet().doEdit(otherPropertyEdit);
				} else if (probNet != null) {
					OtherPropertyEdit otherPropertyEdit = new OtherPropertyEdit(probNet, "ADD", selectedRowIndex, noIDrowData);
					probNet.doEdit(otherPropertyEdit);
				}
				
			} catch (ConstraintViolationException | NonProjectablePotentialException | WrongCriterionException | DoEditException e){
				JOptionPane.showMessageDialog(this, stringDatabase.getString(e.getMessage()),
						stringDatabase.getString(e.getMessage()), JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	/**
	 * Invoked when the button 'remove' is pressed.
	 */
	@Override protected void actionPerformedRemoveValue() {
		try {
			int selectedRowIndex = valuesTable.getSelectedRow();
			tableModel.removeRow(selectedRowIndex);
						
			//ProbNet probNet = null;
			if (node != null) {
				OtherPropertyEdit otherPropertyEdit = new OtherPropertyEdit(node, "REMOVE", selectedRowIndex, null);
				node.getProbNet().doEdit(otherPropertyEdit);
				//probNet = node.getProbNet();
				//probNet.doEdit(otherPropertyEdit);
			} else if (this.probNet != null) {
				OtherPropertyEdit otherPropertyEdit = new OtherPropertyEdit(probNet, "REMOVE", selectedRowIndex, null);
				//probNet = this.probNet;
				probNet.doEdit(otherPropertyEdit);
			}
			
		} catch (ConstraintViolationException | NonProjectablePotentialException | WrongCriterionException | DoEditException e){
			JOptionPane.showMessageDialog(this, stringDatabase.getString(e.getMessage()),
					stringDatabase.getString(e.getMessage()), JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Invoked when the button 'up' is pressed.
	 */
	@Override protected void actionPerformedUpValue() {
		int selectedRowIndex = valuesTable.getSelectedRow();
		try {
			tableModel.moveRow(selectedRowIndex, selectedRowIndex, selectedRowIndex - 1);
			valuesTable.setRowSelectionInterval(selectedRowIndex - 1, selectedRowIndex - 1);
						
			if (node != null) {
				OtherPropertyEdit otherPropertyEdit = new OtherPropertyEdit(node, "UP", selectedRowIndex, null);
				node.getProbNet().doEdit(otherPropertyEdit);
			} else if (probNet != null) {
				OtherPropertyEdit otherPropertyEdit = new OtherPropertyEdit(probNet, "UP", selectedRowIndex, null);
				probNet.doEdit(otherPropertyEdit);
			}
			
		} catch (ConstraintViolationException | NonProjectablePotentialException | WrongCriterionException | DoEditException e){
			JOptionPane.showMessageDialog(this, stringDatabase.getString(e.getMessage()),
					stringDatabase.getString(e.getMessage()), JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Invoked when the button 'down' is pressed.
	 */
	@Override protected void actionPerformedDownValue() {
		int selectedRowIndex = valuesTable.getSelectedRow();
		try {
			tableModel.moveRow(selectedRowIndex, selectedRowIndex, selectedRowIndex + 1);
			valuesTable.setRowSelectionInterval(selectedRowIndex + 1, selectedRowIndex + 1);
			
			if (node != null) {
				OtherPropertyEdit otherPropertyEdit = new OtherPropertyEdit(node, "DOWN", selectedRowIndex, null);
				node.getProbNet().doEdit(otherPropertyEdit);
			} else if (probNet != null) {
				OtherPropertyEdit otherPropertyEdit = new OtherPropertyEdit(probNet, "DOWN", selectedRowIndex, null);
				probNet.doEdit(otherPropertyEdit);
			}
			
		} catch (ConstraintViolationException | NonProjectablePotentialException | WrongCriterionException | DoEditException e){
			JOptionPane.showMessageDialog(this, stringDatabase.getString(e.getMessage()),
					stringDatabase.getString(e.getMessage()), JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Invoked when the row selection changes.
	 *
	 * @param e selection event information.
	 */
	@Override public void valueChanged(ListSelectionEvent e) {
		super.valueChanged(e);
		int rowCount = valuesTable.getRowCount();
		if (rowCount >= 1) {
			removeValueButton.setEnabled(true);
		} else {
			removeValueButton.setEnabled(false);
		}
	}
	
	public void tableChanged(TableModelEvent e) {
		int row = e.getLastRow();

		if (e.getType() == TableModelEvent.UPDATE) {
			String newName = ((DefaultTableModel) e.getSource()).getValueAt(row, 1).toString();
			String newValue = ((DefaultTableModel) e.getSource()).getValueAt(row, 2).toString();
			String[] rowData = { newName, newValue};

			try {
				if (node != null) {
					OtherPropertyEdit otherPropertyEdit = new OtherPropertyEdit(node, "RENAME", row, rowData);
					node.getProbNet().doEdit(otherPropertyEdit);
				} else if (probNet != null) {
					OtherPropertyEdit otherPropertyEdit = new OtherPropertyEdit(probNet, "RENAME", row, rowData);
					probNet.doEdit(otherPropertyEdit);
				}
				
			} catch (ConstraintViolationException | NonProjectablePotentialException | WrongCriterionException | DoEditException e1) {
				JOptionPane.showMessageDialog(this, stringDatabase.getString(e1.getMessage()),
						stringDatabase.getString(e1.getMessage()), JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
