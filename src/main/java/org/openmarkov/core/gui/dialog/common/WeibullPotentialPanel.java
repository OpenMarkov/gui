/*
 * Copyright 2013 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.core.gui.dialog.common;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

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
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.potential.WeibullPotential;

@SuppressWarnings("serial")
@PotentialPanelPlugin(potentialType = "Weibull")
public class WeibullPotentialPanel extends PotentialPanel{

    private ProbNode probNode = null;
    private JLabel constantLabel;
    private JTextField constantText;
    private JPanel northPanel;
    private JLabel gammaLabel;
    private JTextField gammaText;
    private JLabel relativeRiskLabel;
    private JTextField relativeRiskText;
    private JTable coefficientTable;
    
    public WeibullPotentialPanel(ProbNode probNode) {
        super();
        initComponents();
        setData(probNode);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        constantLabel = new JLabel();
        constantLabel.setText("Constant: ");
        constantText = new JTextField();
        constantText.setPreferredSize(new Dimension(50, 20));
        northPanel = new JPanel();
        northPanel.add(constantLabel);
        northPanel.add(constantText);
        gammaLabel = new JLabel();
        gammaLabel.setText("Gamma: ");
        gammaText = new JTextField();
        gammaText.setPreferredSize(new Dimension(50, 20));
        northPanel.add(gammaLabel);
        northPanel.add(gammaText);
        relativeRiskLabel = new JLabel();
        relativeRiskLabel.setText("Relative Risk: ");
        relativeRiskText = new JTextField();
        relativeRiskText.setPreferredSize(new Dimension(50, 20));
        northPanel.add(relativeRiskLabel);
        northPanel.add(relativeRiskText);
        add(northPanel, BorderLayout.NORTH);
        coefficientTable = new JTable();
        JScrollPane tablePanel = new JScrollPane(coefficientTable);
        add(tablePanel, BorderLayout.CENTER);
    }

    @Override
    public void setData(ProbNode probNode) {
        this.probNode = probNode;
        WeibullPotential potential = (WeibullPotential)this.probNode.getPotentials().get(0);
        constantText.setText(potential.getConstant()+"");
        gammaText.setText(potential.getGamma()+"");
        relativeRiskText.setText(potential.getRelativeRisk()+"");
        DefaultTableModel dtm = new CoefficientTableModel(new Object[] {"Variable", "Coefficients"}, potential.getVariables().size()-1);
        for(int i=1; i<potential.getVariables().size(); ++i)
        {
            dtm.setValueAt(potential.getVariable(i).getName(), i-1, 0);
            if(potential.getCoefficients().size() > i-1)
            {
                dtm.setValueAt(potential.getCoefficients().get(i - 1), i-1, 1);
            }else
            {
                dtm.setValueAt(0.0,i-1,1);
            }
        }
        coefficientTable.setModel(dtm);
    }
    
    public void saveChanges()
    {
        WeibullPotential potential = (WeibullPotential)this.probNode.getPotentials().get(0);
        double constant = Double.parseDouble(constantText.getText());
        double gamma = Double.parseDouble(gammaText.getText());
        double relativeRisk = Double.parseDouble(relativeRiskText.getText());
        List<Double> coefficients = new ArrayList<>();
        for(int i=0; i < coefficientTable.getModel().getRowCount(); ++i)
        {
            double coefficient = Double.parseDouble(coefficientTable.getModel().getValueAt(i, 1).toString());
            coefficients.add(coefficient);
        }
        PNEdit edit = new WeibullPotentialEdit(probNode.getProbNet(), potential, constant, gamma,
                coefficients, relativeRisk);
        
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
    

}
