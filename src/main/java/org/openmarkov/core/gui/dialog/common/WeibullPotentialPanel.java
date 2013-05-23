/*
 * Copyright 2013 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.core.gui.dialog.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.action.WeibullPotentialEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.CycleLengthShift;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.WeibullPotential;

@SuppressWarnings("serial")
@PotentialPanelPlugin(potentialType = "WeibullDistribution")
public class WeibullPotentialPanel extends PotentialPanel implements ItemListener {

    private ProbNode          probNode  = null;
    private WeibullPotential  potential = null;
    private JTextField        constantText;
    private JTextField        shapeText;
    private JTable            coefficientTable;
    private JTable            covarianceTable;
    private JComboBox<String> timeVariableComboBox;
    private JCheckBox         uncertaintyCheckBox;
    private JPanel            covariancePanel;

    public WeibullPotentialPanel(ProbNode probNode) {
        super();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JPanel deterministicPanel = new JPanel();
        deterministicPanel.setLayout(new GridLayout(1, 2));
        JLabel timeVariableLabel = new JLabel();
        timeVariableLabel.setText("Time variable:");
        timeVariableComboBox = new JComboBox<String>();
        timeVariableComboBox.addItemListener(this);
        timeVariableComboBox.setMinimumSize(new Dimension(100, 20));
        JLabel constantLabel = new JLabel();
        constantLabel.setText("Constant:");
        constantText = new JTextField();
        constantText.setPreferredSize(new Dimension(75, 20));
        JPanel westPanel = new JPanel();
        westPanel.setLayout(new GridLayout(5, 2, 5, 5));
        westPanel.add(timeVariableLabel);
        westPanel.add(timeVariableComboBox);
        westPanel.add(constantLabel);
        westPanel.add(constantText);
        JLabel shapeLabel = new JLabel();
        shapeLabel.setText("Shape:");
        shapeText = new JTextField();
        shapeText.setPreferredSize(new Dimension(75, 20));
        westPanel.add(shapeLabel);
        westPanel.add(shapeText);
        JLabel relativeRiskLabel = new JLabel();
        relativeRiskLabel.setText("Relative Risk:");
        westPanel.add(relativeRiskLabel);
        uncertaintyCheckBox = new JCheckBox("Uncertainty");
        uncertaintyCheckBox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                covariancePanel.setVisible(uncertaintyCheckBox.isSelected());
            }
        });
        westPanel.add(uncertaintyCheckBox);
        westPanel.setBorder(new EmptyBorder(10, 25, 10, 25));
        deterministicPanel.add(westPanel);
        JPanel eastPanel = new JPanel();
        eastPanel.setBorder(new TitledBorder("Coefficients"));
        coefficientTable = new JTable();
        JScrollPane coefficientPanel = new JScrollPane(coefficientTable);
        coefficientPanel.setPreferredSize(new Dimension(250, 100));
        coefficientTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        coefficientTable.setDefaultRenderer(String.class, new CoefficientTableCellRenderer());
        eastPanel.add(coefficientPanel);
        deterministicPanel.add(eastPanel);
        covarianceTable = new JTable();
        JScrollPane covarianceTablePanel = new JScrollPane(covarianceTable);
        covarianceTablePanel.setPreferredSize(new Dimension(500, 100));
        covariancePanel = new JPanel();
        covarianceTable.setTableHeader(null);
        TableCellRenderer cellRenderer = new CovarianceTableCellRenderer();
        covarianceTable.setDefaultRenderer(String.class, cellRenderer);
        covarianceTable.setDefaultRenderer(Double.class, cellRenderer);
        covarianceTablePanel.setColumnHeaderView(null);
        covariancePanel.setBorder(new TitledBorder("Covariance matrix"));
        covariancePanel.add(covarianceTablePanel);
        add(deterministicPanel, BorderLayout.NORTH);
        add(covariancePanel, BorderLayout.CENTER);
    }

    @Override
    public void setData(ProbNode probNode) {
        this.probNode = probNode;
        this.potential = (WeibullPotential) this.probNode.getPotentials().get(0);
        List<Variable> variables = potential.getVariables();
        constantText.setText(potential.getConstant() + "");
        shapeText.setText(potential.getShape() + "");
        Variable timeVariable = potential.getTimeVariable();
        Object[] headers = new Object[] { "Variable", "Coefficients" };
        DefaultTableModel dtm = new CoefficientTableModel(headers, 0);
        double[] coefficients = potential.getCoefficients();
        for (int i = 1; i < variables.size(); ++i) {
            Variable potentialVariable = potential.getVariable(i);
            if (!potentialVariable.equals(timeVariable)) {
                double coefficient = 0.0;
                if (coefficients.length > i + 1) {
                    coefficient = coefficients[i + 1];
                }
                dtm.addRow(new Object[] { potentialVariable, coefficient });
            }
        }
        coefficientTable.setModel(dtm);

        timeVariableComboBox.removeAllItems();
        timeVariableComboBox.addItem("-- No time variable");
        for (int i = 1; i < variables.size(); ++i) {
            if (isValidTimeVariable(potential.getVariable(i))) {
                timeVariableComboBox.addItem(potential.getVariable(i).getName());
            }
        }
        if (timeVariable != null) {
            timeVariableComboBox.setSelectedItem(timeVariable.getName());
        }

        DefaultTableModel covarianceTableModel = new CovarianceTableModel();
        int columnCount = coefficients.length + 1;
        int rowCount = coefficients.length + 1;
        covarianceTableModel.setColumnCount(columnCount);
        covarianceTableModel.setRowCount(rowCount);
        covarianceTableModel.setValueAt("log(shape)", 1, 0);
        covarianceTableModel.setValueAt("Constant", 2, 0);
        covarianceTableModel.setValueAt("log(shape)", 0, 1);
        covarianceTableModel.setValueAt("Constant", 0, 2);

        for (int i = 3; i < rowCount; ++i) {
            covarianceTableModel.setValueAt(variables.get(i - 2).getName(), i, 0);
            covarianceTableModel.setValueAt(variables.get(i - 2).getName(), 0, i);
        }

        int index = 0;
        double[] covarianceMatrix = potential.getCovarianceMatrix();
        for (int rowIndex = 1; rowIndex < rowCount; ++rowIndex) {
            for (int columnIndex = 1; columnIndex <= rowIndex; ++columnIndex) {
                double value = (covarianceMatrix != null) ? covarianceMatrix[index] : 0.0;
                covarianceTableModel.setValueAt(value, rowIndex, columnIndex);
                ++index;
            }
        }
        covarianceTable.setModel(covarianceTableModel);
        uncertaintyCheckBox.setSelected(covarianceMatrix != null);
        covariancePanel.setVisible(uncertaintyCheckBox.isSelected());
    }

    private boolean isValidTimeVariable(Variable variable) {
        boolean isValid = false;
        if (variable.isTemporal()) {
            ProbNet probNet = probNode.getProbNet();
            Potential potential = probNet.getProbNode(variable).getPotentials().get(0);
            isValid = potential instanceof CycleLengthShift;
            if (!isValid && probNet.containsShiftedVariable(variable, 1)) {
                try {
                    Variable shiftedVariable = probNet.getShiftedVariable(variable, 1);
                    Potential shiftedPotential = probNet.getProbNode(shiftedVariable).getPotentials().get(0);
                    isValid = shiftedPotential instanceof CycleLengthShift;
                } catch (ProbNodeNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return isValid;
    }

    public void saveChanges() {
        WeibullPotential potential = (WeibullPotential) this.probNode.getPotentials().get(0);
        int coeffRowCount = coefficientTable.getModel().getRowCount();
        double[] coefficients = new double[coeffRowCount+2];
        coefficients[0] = Double.parseDouble(shapeText.getText()); 
        coefficients[1] = Double.parseDouble(constantText.getText()); 
        List<Variable> variables = new ArrayList<>();
        variables.add(potential.getConditionedVariable());
        for (int i = 0; i < coeffRowCount; ++i) {
            double coefficient = Double.parseDouble(coefficientTable.getModel().getValueAt(i, 1).toString());
            coefficients[i+2] = coefficient;
            String variableName = coefficientTable.getModel().getValueAt(i, 0).toString();
            try {
                variables.add(probNode.getProbNet().getVariable(variableName));
            } catch (ProbNodeNotFoundException e) {
                e.printStackTrace();
            }
        }
        Variable timeVariable = null;
        String selectedTimeVariable = timeVariableComboBox.getSelectedItem().toString();
        try {
            timeVariable = probNode.getProbNet().getVariable(selectedTimeVariable);
        } catch (ProbNodeNotFoundException e1) {
            // Ignore
        }
        if (timeVariable != null) {
            variables.add(timeVariable);
        }

        double[] covarianceMatrix = null;
        if (uncertaintyCheckBox.isSelected()) {
            CovarianceTableModel tableModel = (CovarianceTableModel) covarianceTable.getModel();
            int rowCount = tableModel.getRowCount();
            int n = rowCount-1;
            int index = 0;
            covarianceMatrix = new double[(n+1)*n/2];
            for (int rowIndex = 1; rowIndex < rowCount; ++rowIndex) {
                for (int columnIndex = 1; columnIndex <= rowIndex; ++columnIndex) {
                    double value = (Double) tableModel.getValueAt(rowIndex, columnIndex);
                    covarianceMatrix[index] = value;
                    ++index;
                }
            }
        }

        PNEdit edit = new WeibullPotentialEdit(probNode.getProbNet(),
                potential,
                variables,
                coefficients,
                timeVariable,
                covarianceMatrix);

        try {
            probNode.getProbNet().doEdit(edit);
        } catch (ConstraintViolationException
                | CanNotDoEditException
                | NonProjectablePotentialException
                | WrongCriterionException
                | DoEditException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {

    }

    private class CoefficientTableModel extends DefaultTableModel {
        public CoefficientTableModel(Object[] columnNames, int rowCount) {
            super(columnNames, rowCount);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column > 0;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return (columnIndex == 0) ? String.class : Double.class;
        }

    }

    private class CoefficientTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {
            Color backgroundColor = Color.WHITE;
            if (column == 0) {
                backgroundColor = new Color(207, 227, 253);
            }
            setBackground(backgroundColor);

            return super.getTableCellRendererComponent(table,
                    value,
                    isSelected,
                    hasFocus,
                    row,
                    column);
        }
    }

    private class CovarianceTableModel extends DefaultTableModel {

        @Override
        public boolean isCellEditable(int row, int column) {
            return row > 0 && column > 0 && row >= column;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return (columnIndex == 0) ? String.class : Double.class;
        }
    }

    private class CovarianceTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {
            Color backgroundColor = Color.WHITE;
            if (row == 0 || column == 0) {
                backgroundColor = new Color(207, 227, 253);
            } else if (column > row) {
                backgroundColor = new Color(220, 220, 220);
            }
            setBackground(backgroundColor);

            return super.getTableCellRendererComponent(table,
                    value,
                    isSelected,
                    hasFocus,
                    row,
                    column);
        }

    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource().equals(timeVariableComboBox)) {
            String selected = timeVariableComboBox.getSelectedItem().toString();
            Map<String, Double> coefficients = new LinkedHashMap<>();
            DefaultTableModel dtm = (DefaultTableModel) coefficientTable.getModel();

            for (int i = 0; i < dtm.getRowCount(); ++i) {
                String variable = coefficientTable.getModel().getValueAt(i, 0).toString();
                Double coefficient = Double.parseDouble(coefficientTable.getModel().getValueAt(i, 1).toString());
                coefficients.put(variable, coefficient);
            }

            // Clear table
            dtm.setRowCount(0);
            for (String variableName : coefficients.keySet()) {
                if (!selected.equals(variableName)) {
                    dtm.addRow(new Object[] { variableName, coefficients.get(variableName) });
                }
            }

            List<Variable> potentialVariables = potential.getVariables();
            for (Variable potentialVariable : potentialVariables) {
                String variableName = potentialVariable.getName();
                if (!potential.getConditionedVariable().equals(potentialVariable)
                        && !coefficients.containsKey(variableName)
                        && !selected.equals(variableName)) {
                    dtm.addRow(new Object[] { variableName, 0.0 });
                }
            }
        }
    }

}
