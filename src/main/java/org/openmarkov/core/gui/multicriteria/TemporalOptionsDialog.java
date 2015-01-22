/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.multicriteria;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.io.FilenameUtils;
import org.openmarkov.core.action.TemporalOptionsEdit;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.dialog.common.OkCancelHorizontalDialog;
import org.openmarkov.core.inference.TemporalOptions;
import org.openmarkov.core.inference.TransitionTime;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.Variable;

/**
 * TODO - Copy of costeffectiveness.TemporalCostEffectivenessDialog class 
 */
public class TemporalOptionsDialog extends OkCancelHorizontalDialog {
    private static final long       serialVersionUID       = 1L;
    private Integer                 simulationsNumber;
    private JLabel                  numSlicesLabel;
    private JTextField              numSlicesTextField;
    private Integer                 numSlices;
    private JRadioButton            beginningOfCycleButton;
    private JRadioButton            endOfCycleButton;
    private JRadioButton            halfCycleButton;
    private ButtonGroup             transitionsButtonGroup;
    private JPanel                  transitionsPanel;
    private JPanel                  numSlicesPanel;
    private Integer                 numSimulations;
    private TemporalOptions		    temporalOptions;
    private ProbNet					probNet;

    /**
     * Creates a CostEffectivenessDialog for expansion only
     * 
     * @param owner
     *            The parent of the dialog
     */
    public TemporalOptionsDialog(Window owner) {
        super(owner);
        // setMinimumSize(new Dimension(250 , 150));
        BorderLayout layout = new BorderLayout(5, 5);
        getComponentsPanel().setLayout(layout);
        getComponentsPanel().add(getNumSlicesPanel(), BorderLayout.NORTH);
        setResizable(false);
        pack();
        Point parentLocation = owner.getLocation();
        Dimension parentSize = owner.getSize();
        int x = (int) (parentLocation.getX() + parentSize.getWidth() / 2 - getSize().getWidth() / 2);
        int y = (int) (parentLocation.getY() + parentSize.getHeight() / 2 - getSize().getHeight() / 2);
        setLocation(new Point(x, y));
        repaint();
    }

    /**
     * Creates a CostEffectivenessDialog for temporal evolution
     * 
     * @param owner
     *            The parent of the dialog
     * @param b
     */
    public TemporalOptionsDialog(ProbNet probNet, Window owner) {
        super(owner);
        this.temporalOptions = probNet.getInferenceOptions().getTemporalOptions().clone();
        this.probNet = probNet;
        initialize();
        setResizable(false);
        setTitle(probNet.getName());
        pack();
        Point parentLocation = owner.getLocation();
        Dimension parentSize = owner.getSize();
        int x = (int) (parentLocation.getX() + parentSize.getWidth() / 2 - getSize().getWidth() / 2);
        int y = (int) (parentLocation.getY() + parentSize.getHeight() / 2 - getSize().getHeight() / 2);
        setLocation(new Point(x, y));
        repaint();
    }

    private void initialize() {
        setMinimumSize(new Dimension(250, 150));
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JPanel otherPanel = new JPanel();
        otherPanel.setLayout(new BorderLayout());
        JPanel slicesPanel = new JPanel();
        slicesPanel.add(getJLabelNumSlices());
        slicesPanel.add(getNumSlicesTextField());
        slicesPanel.setBorder(new TitledBorder("Time horizon"));
        slicesPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        otherPanel.add(slicesPanel, BorderLayout.NORTH);
        otherPanel.add(getTransitionsPanel(), BorderLayout.CENTER);
        panel.add(otherPanel, BorderLayout.NORTH);
        
        getComponentsPanel().setLayout(new BorderLayout(20, 0));
        getComponentsPanel().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        getComponentsPanel().add(panel, BorderLayout.NORTH);
        
        
        pack();
        repaint();
    }

    private JPanel getNumSlicesPanel() {
        if (numSlicesPanel == null) {
            numSlicesPanel = new JPanel();
            numSlicesPanel.setLayout(new GridLayout(1, 2, 10, 10));
            numSlicesPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            numSlicesPanel.add(getJLabelNumSlices());
            numSlicesPanel.add(getNumSlicesTextField());
        }
        return numSlicesPanel;
    }

    private JLabel getJLabelNumSlices() {
        if (numSlicesLabel == null) {
            numSlicesLabel = new JLabel(stringDatabase.getString("CostEffectiveness.NumberOfCycles"));
        }
        return numSlicesLabel;
    }

    private JTextField getNumSlicesTextField() {
        if (numSlicesTextField == null) {
    		numSlices = this.temporalOptions.getNumberOfSlices();
        	
            numSlicesTextField = new JTextField();
            numSlicesTextField.setText("" + numSlices);
            numSlicesTextField.setColumns(10);
            numSlicesTextField.setName("numSlicesTextField");
        }
        return numSlicesTextField;
    }

    private JRadioButton getBeginningOfCycleButton() {
        if (beginningOfCycleButton == null) {
            beginningOfCycleButton = new JRadioButton(stringDatabase.getString("CostEffectiveness.BeginningOfCycle"),
                    true);
        }
        return beginningOfCycleButton;
    }

    private JRadioButton getEndOfCycleButton() {
        if (endOfCycleButton == null) {
            endOfCycleButton = new JRadioButton(stringDatabase.getString("CostEffectiveness.EndOfCycle"),
                    true);
        }
        return endOfCycleButton;
    }

    private JRadioButton getHalfCycleButton() {
        if (halfCycleButton == null) {
            halfCycleButton = new JRadioButton(stringDatabase.getString("CostEffectiveness.HalfCycle"),
                    true);
        }
        return halfCycleButton;
    }

    private void initTransitionsButtonGroup() {
        transitionsButtonGroup = new ButtonGroup();
        transitionsButtonGroup.add(getBeginningOfCycleButton());
        transitionsButtonGroup.add(getHalfCycleButton());
        transitionsButtonGroup.add(getEndOfCycleButton());
        
        if(this.temporalOptions.getTransition().equals(TransitionTime.BEGINNING)){
        	beginningOfCycleButton.setSelected(true);
        } else if(this.temporalOptions.getTransition().equals(TransitionTime.HALF)){
        	halfCycleButton.setSelected(true);
        } else if(this.temporalOptions.getTransition().equals(TransitionTime.END)){
        	endOfCycleButton.setSelected(true);
        }
    }

    /**
     * @return the panel with the transition buttons
     */
    private JPanel getTransitionsPanel() {
        if (transitionsPanel == null) {
            transitionsPanel = new JPanel();
            transitionsPanel.setLayout(new GridLayout(3, 1));
            transitionsPanel.setBorder(new TitledBorder("Transitions"));
            transitionsPanel.setName("transitionsPanel");
            initTransitionsButtonGroup();
            transitionsPanel.add(getBeginningOfCycleButton());
            transitionsPanel.add(getHalfCycleButton());
            transitionsPanel.add(getEndOfCycleButton());
        }
        return transitionsPanel;
    }

    public int requestData() {
        setVisible(true);
        return selectedButton;
    }

    @Override
    protected boolean doOkClickBeforeHide() {
    	try{
    		int numSlices = Integer.parseInt(numSlicesTextField.getText());
    		setNumSlices(numSlices);
    	} catch (NumberFormatException exception){
			JOptionPane.showMessageDialog(
					null,
					stringDatabase.getString("NumberFormatException.Text.Label"),
					stringDatabase.getString("NumberFormatException.Title.Label"),
					JOptionPane.ERROR_MESSAGE
					);
			
    	}
    	
    	this.temporalOptions.setNumberOfSlices(numSlices);
    	if(beginningOfCycleButton.isSelected()){
    		this.temporalOptions.setTransition(TransitionTime.BEGINNING);
    	}else if(halfCycleButton.isSelected()){
    		this.temporalOptions.setTransition(TransitionTime.HALF);
    	}else if(endOfCycleButton.isSelected()){
    		this.temporalOptions.setTransition(TransitionTime.END);
    	}
    	
    	TemporalOptionsEdit edit = new TemporalOptionsEdit(probNet, temporalOptions);
    	try {
			probNet.getPNESupport().doEdit(edit);
		} catch (DoEditException | NonProjectablePotentialException
				| WrongCriterionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return super.doOkClickBeforeHide();
    }
    
    private void setNumSlices(int slices){
    	this.numSlices = slices;
    }

    public int getNumSlices() {
        return numSlices;
    }
    
    public TransitionTime getTransitionTime() {
        TransitionTime transitionTime = TransitionTime.BEGINNING;
        if (halfCycleButton != null && halfCycleButton.isSelected()) {
            transitionTime = TransitionTime.HALF;
        }
        if (endOfCycleButton != null && endOfCycleButton.isSelected()) {
            transitionTime = TransitionTime.END;
        }
        return transitionTime;
    }

    public int getSimulationsNumber() {
        return simulationsNumber;
    }

    public Integer getNumSimulations() {
        return numSimulations;
    }
}
