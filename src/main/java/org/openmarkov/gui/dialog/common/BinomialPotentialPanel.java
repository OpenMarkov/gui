/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.dialog.common;

import org.openmarkov.core.action.core.PotentialChangeEdit;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.BinomialPotential;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.gui.exception.BinomialPotentialWrongValueException;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

@SuppressWarnings("serial") @PotentialPanelPlugin(potentialType = "Binomial") public class BinomialPotentialPanel
        extends PotentialPanel {
    
    private JSpinner NSpinner;
    private JTextField thetaTextField;
    
    private Node node;
    private int defaultSpinnerValue = 1;
    // Allowed range for the delta potential
    private int minValue = 1;
    private int maxValue = Integer.MAX_VALUE;
    
    public BinomialPotentialPanel(Node node) {
        super();
        this.node = node;
        initComponents();
        
        setData(node);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        JPanel namelessPanel = new JPanel();
        namelessPanel.setBorder(new EtchedBorder());
        if (node.getVariable().getVariableType() == VariableType.NUMERIC) {
            
            //Create the model with the defaultValue, the min and max values and the precision
            // Only positive integers
            SpinnerNumberModel model = new SpinnerNumberModel(defaultSpinnerValue, minValue, maxValue, 1);
            
            NSpinner = new JSpinner(model);
            NSpinner.setPreferredSize(new Dimension(100, 20));
            JLabel NLabel = new JLabel("Number of cases:");
            NLabel.setLabelFor(NSpinner);
            thetaTextField = new JTextField(10);
            thetaTextField.setText("0");
            JLabel thetaLabel = new JLabel("Probability of success");
            thetaLabel.setLabelFor(thetaTextField);
            namelessPanel.add(NLabel);
            namelessPanel.add(NSpinner);
            namelessPanel.add(thetaLabel);
            namelessPanel.add(thetaTextField);
            
        } else {
            //UNCLEAR
            System.out.println("Not numeric");
        }
        namelessPanel.setPreferredSize(new Dimension(200, 50));
        add(namelessPanel);
    }
    
    @Override public void setData(Node node) {
        this.node = node;
        BinomialPotential oldPotential = null;
        if (!node.getPotentials().isEmpty() && node.getPotentials().get(0) instanceof BinomialPotential) {
            oldPotential = (BinomialPotential) node.getPotentials().get(0);
        }
        // The model inits the valid range and the mean value
        if (node.getVariable().getVariableType() == VariableType.NUMERIC) {
            if (oldPotential != null) {
                
                int NValue = oldPotential.getN();
                // UNCLEAR--&gt;Where to check, when loading or when saving
                // We put these value into the spinner if the value is into the bounds
                if ((NValue > 0) && (NValue <= Integer.MAX_VALUE)) {
                    NSpinner.setValue(NValue);
                }
                
                double thetaValue = oldPotential.gettheta();
                if ((thetaValue >= 0) && (thetaValue <= 1.00)) {
                    thetaTextField.setText(String.valueOf(thetaValue));
                }
                
            }
            
        } else {
            //UNCLEAR--&gt;Where to check
        }
    }
    
    @Override public boolean saveChanges() throws BinomialPotentialWrongValueException.ThetaValueIsWrong,
            BinomialPotentialWrongValueException.NValuesIsWrong, DoEditException {
        boolean result = super.saveChanges();
        ProbNet probNet = node.getProbNet();
        Potential oldPotential = node.getPotentials().get(0);
        Potential newPotential = null;
        if (node.getVariable().getVariableType() == VariableType.NUMERIC) {
            int NValue = Integer.parseInt(NSpinner.getValue().toString());
            double thetaValue = Double.parseDouble(thetaTextField.getText());
            if (NValue <= 0) {
                throw new BinomialPotentialWrongValueException.NValuesIsWrong(NValue);
            }
            if (thetaValue < 0 || thetaValue > 1) {
                throw new BinomialPotentialWrongValueException.ThetaValueIsWrong(thetaValue);
            }
            newPotential = new BinomialPotential(oldPotential.getVariables(), oldPotential.getPotentialRole(),
                                                 NValue, thetaValue);
        }
        newPotential.setComment(oldPotential.getComment());
        PotentialChangeEdit edit = new PotentialChangeEdit(probNet, oldPotential, newPotential);
        edit.doEdit(probNet);
        return result;
    }
    
    @Override public void close() {
        // Do nothing
    }
    
}
