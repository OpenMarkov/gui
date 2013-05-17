/*
 * Copyright 2013 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.core.gui.dialog.common;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

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
public class WeibullPotentialPanel extends PotentialPanel implements ItemListener{

    private ProbNode probNode = null;
    private WeibullPotential potential = null;
    private JTextField constantText;
    private JPanel northPanel;
    private JTextField shapeText;
    private JTextField relativeRiskText;
    private JTable coefficientTable;
    private JComboBox<String> timeVariableComboBox;
    
    public WeibullPotentialPanel(ProbNode probNode) {
        super();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JLabel timeVariableLabel = new JLabel();
        timeVariableLabel.setText("Time variable: ");
        timeVariableComboBox = new JComboBox<String>();
        timeVariableComboBox.addItemListener(this);
        JLabel constantLabel = new JLabel();
        constantLabel.setText("Constant: ");
        constantText = new JTextField();
        constantText.setPreferredSize(new Dimension(50, 20));
        northPanel = new JPanel();
        northPanel.add(timeVariableLabel);
        northPanel.add(timeVariableComboBox);
        northPanel.add(constantLabel);
        northPanel.add(constantText);
        JLabel shapeLabel = new JLabel();
        shapeLabel.setText("Shape: ");
        shapeText = new JTextField();
        shapeText.setPreferredSize(new Dimension(50, 20));
        northPanel.add(shapeLabel);
        northPanel.add(shapeText);
        JLabel relativeRiskLabel = new JLabel();
        relativeRiskLabel.setText("Relative Risk: ");
        relativeRiskText = new JTextField();
        relativeRiskText.setPreferredSize(new Dimension(50, 20));
        northPanel.add(relativeRiskLabel);
        northPanel.add(relativeRiskText);
        add(northPanel, BorderLayout.NORTH);
        coefficientTable = new JTable();
        JScrollPane tablePanel = new JScrollPane(coefficientTable);
        coefficientTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        add(tablePanel, BorderLayout.CENTER);
    }

    @Override
    public void setData(ProbNode probNode) {
        this.probNode = probNode;
        this.potential = (WeibullPotential)this.probNode.getPotentials().get(0);
        List<Variable> variables = potential.getVariables();
        constantText.setText(potential.getConstant()+"");
        shapeText.setText(potential.getShape()+"");
        relativeRiskText.setText(potential.getRelativeRisk()+"");
        Variable timeVariable = potential.getTimeVariable();
        DefaultTableModel dtm = new CoefficientTableModel(new Object[] {"Variable", "Coefficients"}, 0);
        for(int i=1; i<variables.size(); ++i)
        {
            Variable potentialVariable = potential.getVariable(i); 
            if(!potentialVariable.equals(timeVariable))
            {
                Double coefficient = 0.0;
                if(potential.getCoefficients().size() > i-1)
                {
                    coefficient = potential.getCoefficients().get(i - 1);
                }
                dtm.addRow(new Object[]{potentialVariable, coefficient});
            }
        }
        coefficientTable.setModel(dtm);
        
        timeVariableComboBox.removeAllItems();
        timeVariableComboBox.addItem("-- No time variable");
        for(int i=1; i<variables.size(); ++i)
        {
            if(isValidTimeVariable(potential.getVariable(i)))
            {
                timeVariableComboBox.addItem(potential.getVariable(i).getName());
            }
        }
        if(timeVariable!=null)
        {
            timeVariableComboBox.setSelectedItem(timeVariable.getName());
        }
    }
    
    private boolean isValidTimeVariable(Variable variable) {
        boolean isValid = false;
        if(variable.isTemporal())
        {
            ProbNet probNet = probNode.getProbNet();
            Potential potential = probNet.getProbNode(variable).getPotentials().get(0);
            isValid = potential instanceof CycleLengthShift;
            if(!isValid && probNet.containsShiftedVariable(variable, 1))
            {
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

    public void saveChanges()
    {
        WeibullPotential potential = (WeibullPotential)this.probNode.getPotentials().get(0);
        double constant = Double.parseDouble(constantText.getText());
        double shape = Double.parseDouble(shapeText.getText());
        double relativeRisk = Double.parseDouble(relativeRiskText.getText());
        List<Double> coefficients = new ArrayList<>();
        List<Variable> variables = new ArrayList<>();
        variables.add(potential.getConditionedVariable());
        for(int i=0; i < coefficientTable.getModel().getRowCount(); ++i)
        {
            double coefficient = Double.parseDouble(coefficientTable.getModel().getValueAt(i, 1).toString());
            coefficients.add(coefficient);
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
        if(timeVariable != null)
        {
            variables.add(timeVariable);
        }
        
        PNEdit edit = new WeibullPotentialEdit(probNode.getProbNet(), potential, variables, constant, shape,
                coefficients, relativeRisk, timeVariable);
        
        try {
            probNode.getProbNet().doEdit(edit);
        } catch (ConstraintViolationException | CanNotDoEditException
                | NonProjectablePotentialException | WrongCriterionException | DoEditException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        
    }
    
    private class CoefficientTableModel extends DefaultTableModel
    {
        public CoefficientTableModel(Object[] columnNames, int rowCount) {
            super(columnNames, rowCount);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column > 0;
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if(e.getSource().equals(timeVariableComboBox))
        {
            String selected = timeVariableComboBox.getSelectedItem().toString();
            Map<String, Double> coefficients = new LinkedHashMap<>();
            DefaultTableModel dtm = (DefaultTableModel)coefficientTable.getModel();
            
            for(int i=0; i < dtm.getRowCount(); ++i)
            {
                String variable = coefficientTable.getModel().getValueAt(i, 0).toString();
                Double coefficient = (Double)coefficientTable.getModel().getValueAt(i, 1);
                coefficients.put(variable, coefficient);
            }

            // Clear table
            dtm.setRowCount(0);
            for(String variableName : coefficients.keySet())
            {
                if(!selected.equals(variableName))
                {
                    dtm.addRow(new Object[] {variableName, coefficients.get(variableName)});
                }
            }

            List<Variable> potentialVariables = potential.getVariables();
            for(Variable potentialVariable : potentialVariables)
            {
                String variableName = potentialVariable.getName();
                if(!potential.getConditionedVariable().equals(potentialVariable) && 
                        !coefficients.containsKey(variableName) &&
                        !selected.equals(variableName))
                {
                    dtm.addRow(new Object[] {variableName, 0.0});                    
                }
            }
        }
    }
    

}
