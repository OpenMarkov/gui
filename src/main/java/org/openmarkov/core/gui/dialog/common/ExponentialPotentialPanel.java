package org.openmarkov.core.gui.dialog.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
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
import org.openmarkov.core.action.PotentialChangeEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.ExponentialHazardPotential;

@SuppressWarnings("serial")
@PotentialPanelPlugin(potentialType = "Hazard (Exponential)")
public class ExponentialPotentialPanel extends PotentialPanel {
    private ProbNode               probNode  = null;
    private ExponentialHazardPotential potential = null;
    private JTextField             constantText;
    private JTable                 coefficientTable;
    private JTable                 covarianceTable;
    private JCheckBox              uncertaintyCheckBox;
    private JPanel                 covariancePanel;

    public ExponentialPotentialPanel(ProbNode probNode) {
        super();
        initComponents();
        setData(probNode);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JPanel deterministicPanel = new JPanel();
        deterministicPanel.setLayout(new BorderLayout());
        JLabel constantLabel = new JLabel();
        constantLabel.setText("Constant:");
        constantText = new JTextField();
        constantText.setPreferredSize(new Dimension(75, 20));
        constantLabel.setLabelFor(constantText);
        JPanel northPanel = new JPanel();
        northPanel.add(constantLabel);
        northPanel.add(constantText);
        uncertaintyCheckBox = new JCheckBox("Uncertainty");
        uncertaintyCheckBox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                covariancePanel.setVisible(uncertaintyCheckBox.isSelected());
            }
        });
        northPanel.setBorder(new EmptyBorder(10, 25, 10, 25));
        deterministicPanel.add(northPanel, BorderLayout.NORTH);
        JPanel centerPanel = new JPanel();
        centerPanel.setBorder(new TitledBorder("Coefficients"));
        coefficientTable = new JTable();
        JScrollPane coefficientPanel = new JScrollPane(coefficientTable);
        coefficientPanel.setPreferredSize(new Dimension(250, 100));
        coefficientTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        coefficientTable.setDefaultRenderer(String.class, new CoefficientTableCellRenderer());
        centerPanel.add(coefficientPanel);
        deterministicPanel.add(centerPanel, BorderLayout.CENTER);
        deterministicPanel.add(uncertaintyCheckBox, BorderLayout.SOUTH);
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
        this.potential = (ExponentialHazardPotential) this.probNode.getPotentials().get(0);
        List<Variable> variables = potential.getVariables();
        constantText.setText(potential.getConstant() + "");
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

        DefaultTableModel covarianceTableModel = new CovarianceTableModel();
        int columnCount = coefficients.length + 1;
        int rowCount = coefficients.length + 1;
        covarianceTableModel.setColumnCount(columnCount);
        covarianceTableModel.setRowCount(rowCount);
        covarianceTableModel.setValueAt("Constant", 1, 0);
        covarianceTableModel.setValueAt("Constant", 0, 1);

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

    public boolean saveChanges() {
        ExponentialHazardPotential oldPotential = (ExponentialHazardPotential) this.probNode.getPotentials().get(0);
        ProbNet probNet = probNode.getProbNet();
        int coeffRowCount = coefficientTable.getModel().getRowCount();
        double[] coefficients = new double[coeffRowCount + 1];
        coefficients[0] = Double.parseDouble(constantText.getText());
        List<Variable> variables = new ArrayList<>();
        variables.add(oldPotential.getConditionedVariable());
        for (int i = 0; i < coeffRowCount; ++i) {
            double coefficient = Double.parseDouble(coefficientTable.getModel().getValueAt(i, 1).toString());
            coefficients[i + 1] = coefficient;
            String variableName = coefficientTable.getModel().getValueAt(i, 0).toString();
            try {
                variables.add(probNet.getVariable(variableName));
            } catch (ProbNodeNotFoundException e) {
                e.printStackTrace();
            }
        }

        double[] covarianceMatrix = null;
        if (uncertaintyCheckBox.isSelected()) {
            CovarianceTableModel tableModel = (CovarianceTableModel) covarianceTable.getModel();
            int rowCount = tableModel.getRowCount();
            int n = rowCount - 1;
            int index = 0;
            covarianceMatrix = new double[(n + 1) * n / 2];
            for (int rowIndex = 1; rowIndex < rowCount; ++rowIndex) {
                for (int columnIndex = 1; columnIndex <= rowIndex; ++columnIndex) {
                    double value = (Double) tableModel.getValueAt(rowIndex, columnIndex);
                    covarianceMatrix[index] = value;
                    ++index;
                }
            }
        }

        ExponentialHazardPotential newPotential = new ExponentialHazardPotential(oldPotential.getVariables(),
                oldPotential.getPotentialRole(),
                coefficients,
                covarianceMatrix);
        PNEdit edit = new PotentialChangeEdit(probNode.getProbNet(), oldPotential, newPotential);

        try {
            probNet.doEdit(edit);
        } catch (ConstraintViolationException
                | CanNotDoEditException
                | NonProjectablePotentialException
                | WrongCriterionException
                | DoEditException e) {
            e.printStackTrace();
        }
        return true;
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

}
