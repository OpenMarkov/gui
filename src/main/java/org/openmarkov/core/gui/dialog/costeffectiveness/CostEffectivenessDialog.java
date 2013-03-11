/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.costeffectiveness;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.FilenameUtils;
import org.openmarkov.core.gui.dialog.common.OkCancelHorizontalDialog;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;

/**
 * Input dialog for cost effectiveness purposes used to introduce relevant
 * information such as cycle length, number of cycles, introduce findings to
 * numerical variables within the network, cost and effectiveness discount
 * rate...
 * @author myebra
 */
public class CostEffectivenessDialog extends OkCancelHorizontalDialog
    implements
        ItemListener,
        FocusListener
{
    private static final long           serialVersionUID          = 1L;
    private final JPanel                contentPanel              = new JPanel ();
    private JLabel                      initialAgeLabel;
    private JLabel                      cycleLengthLabel;
    private JTextField                  cycleLengthTextField;
    private JComboBox<String>           unitsCombo;
    private JCheckBox                   checkZeroCycle;
    private JLabel                      finalAgeLabel;
    private JTextField                  finalAgeTextField;
    private JTextField                  initialAgeTextField;
    private JLabel                      costDiscountLabel;
    private JLabel                      effectivenessDiscountLabel;
    private JTextField                  costDiscountTextField;
    private JTextField                  effectivenessDiscountTextField;
    private Integer                     initialAge;
    private Integer                     finalAge;
    private Double                      costDiscount;
    private Double                      cycleLength;
    private String                      units;
    private Double                      effectivenessDiscount;
    private JTextField                  txtSimulationNumber;
    private JLabel                      lblSimulationsNumber;
    private Integer                     simulationsNumber;
    private boolean                     containsAgeNode            = false;
    private JLabel                      numSlicesLabell;
    private JTextField                  numSlicesTextField;
    private Integer                     numSlices;
    private JRadioButton                instantButton;
    private JRadioButton                accumulativeButton;
    private ButtonGroup                 buttonGroup;
    private JPanel                      instantOrAccumulativePanel;
    private boolean                     isAccumulative            = false;
    private JPanel                      numSlicesPanel;
    private Map<Variable, Double>       numericTemporalVariables;
    private Map<String, JTextField> numericTemporalComponents = new HashMap<> ();
    

    /**
     * Creates a CostEffectivenessDialog for expansion only
     * @param owner The parent of the dialog
     */
    public CostEffectivenessDialog (Window owner)
    {
        super (owner);
        setLocationRelativeTo (owner);
        // setMinimumSize(new Dimension(250 , 150));
        BorderLayout layout = new BorderLayout (5, 5);
        getComponentsPanel ().setLayout (layout);
        getComponentsPanel ().add (getNumSlicesPanel (), BorderLayout.NORTH);
        setResizable (true);
        pack ();
        repaint ();
    }

    /**
     * Creates a CostEffectivenessDialog for temporal evolution
     * @param owner The parent of the dialog
     */
    public CostEffectivenessDialog (Window owner,
                                    ProbNet probNet,
                                    boolean isTemporalEvolution)
    {
        super (owner);
        setLocationRelativeTo (owner);
        this.containsAgeNode = probNet.checkIfThereIsAgeNode ();
        
        this.numericTemporalVariables = new HashMap<>(); 
        for(ProbNode numericalTemporalNode : probNet.getSpecialTimeDependentNodes ())
        {
            numericTemporalVariables.put (numericalTemporalNode.getVariable (), 0.0);
        }
        initialize (isTemporalEvolution);
        setResizable (false);
        setTitle(probNet.getName (), isTemporalEvolution);
        pack ();
        repaint ();
    }

    private void initialize (boolean isTemporalEvolution)
    {
        setMinimumSize (new Dimension (250, 150));
        contentPanel.setBorder (new EmptyBorder (5, 5, 5, 5));
        int rows = numericTemporalVariables.size () + 2;
        rows = (!containsAgeNode) ? (rows + 1) : rows;
        JPanel panel = new JPanel ();
        panel.setLayout (new GridLayout (rows, 4, 10, 10));
        panel.add (getCycleLengthLabel ());
        panel.add (getCycleLengthTextField ());
        panel.add (getUnitsJComboBox ());
        if (isTemporalEvolution)
        {
            panel.add (new JLabel (""));
        }
        else
        {
            panel.add (getCheckCycleCero ());
        }
        if (containsAgeNode)
        {
            panel.add (getInitialAgeLabel ());
            panel.add (getInitialAgeTextField ());
            panel.add (getFinalAgeLabel ());
            panel.add (getFinalAgeTextField ());
        }
        else
        {
            panel.add (getJLabelNumSlices ());
            panel.add (getNumSlicesTextField ());
            panel.add (new JLabel (""));
            panel.add (new JLabel (""));
        }
        for (Variable numericTemporalVariable : numericTemporalVariables.keySet ())
        {
            if (!numericTemporalVariable.getBaseName ().equalsIgnoreCase ("Age"))
            {
                JLabel label = new JLabel (numericTemporalVariable.getName ());
                JTextField textField = new JTextField (10);
                textField.setName (numericTemporalVariable.getName ());
                textField.setText (""+numericTemporalVariables.get (numericTemporalVariable));
                textField.addFocusListener (this);
                panel.add (label);
                panel.add (textField);
                numericTemporalComponents.put (numericTemporalVariable.getName (), textField);
                panel.add (new JLabel (stringDatabase.getString ("CostEffectiveness.Cycles")));
                panel.add (new JLabel (""));
            }
        }
        panel.add (getCostDiscountLabel ());
        panel.add (getCostDiscountTextField ());
        panel.add (getEffectivenessDiscountLabel ());
        panel.add (getEffectivenessDiscountTextField ());
        getComponentsPanel ().setLayout (new BorderLayout (20, 0));
        getComponentsPanel ().setBorder (BorderFactory.createEmptyBorder (10, 10, 10, 10));
        getComponentsPanel ().add (panel, BorderLayout.NORTH);
        if (isTemporalEvolution)
        {
            getComponentsPanel ().add (new JPanel ());
            getComponentsPanel ().add (getJPanelInstantOrAccumulative (), BorderLayout.CENTER);
        }
        pack ();
        repaint ();
    }

    private JPanel getNumSlicesPanel ()
    {
        if (numSlicesPanel == null)
        {
            numSlicesPanel = new JPanel ();
            GroupLayout groupLayout = new GroupLayout (numSlicesPanel);
            groupLayout.setHorizontalGroup (groupLayout.createParallelGroup (Alignment.LEADING).addGroup (groupLayout.createSequentialGroup ().addContainerGap ().addGroup (groupLayout.createParallelGroup (Alignment.LEADING).addGroup (groupLayout.createSequentialGroup ().addGroup (groupLayout.createParallelGroup (Alignment.LEADING,
                                                                                                                                                                                                                                                                                                                          false).addGroup (groupLayout.createSequentialGroup ().addComponent (getJLabelNumSlices ()).addPreferredGap (LayoutStyle.ComponentPlacement.RELATED).addComponent (getNumSlicesTextField (),
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            75,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            GroupLayout.PREFERRED_SIZE))).addContainerGap ()))));
            groupLayout.setVerticalGroup (groupLayout.createParallelGroup (Alignment.LEADING).addGroup (groupLayout.createSequentialGroup ().addContainerGap ().addGroup (groupLayout.createParallelGroup (Alignment.LEADING).addComponent (getJLabelNumSlices ()).addComponent (getNumSlicesTextField (),
                                                                                                                                                                                                                                                                                 GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                                                                                                 GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                                                                                                 GroupLayout.PREFERRED_SIZE)).addContainerGap ()));
            numSlicesPanel.setLayout (groupLayout);
        }
        return numSlicesPanel;
    }    

    private JLabel getJLabelNumSlices ()
    {
        if (numSlicesLabell == null)
        {
            numSlicesLabell = new JLabel (stringDatabase.getString ("CostEffectiveness.NumberOfCycles"));
        }
        return numSlicesLabell;
    }

    private JTextField getNumSlicesTextField ()
    {
        if (numSlicesTextField == null)
        {
            numSlices = 1;
            numSlicesTextField = new JTextField ();
            numSlicesTextField.setText (""+numSlices);
            numSlicesTextField.setColumns (10);
            numSlicesTextField.setName ("numSlicesTextField");
            numSlicesTextField.addFocusListener (this);
        }
        return numSlicesTextField;
    }

    private JTextField getTxtSimulationsNumber ()
    {
        if (txtSimulationNumber == null)
        {
            txtSimulationNumber = new JTextField ("20");
            txtSimulationNumber.setColumns (10);
            txtSimulationNumber.setVisible (false);
        }
        return txtSimulationNumber;
    }

    private JLabel getLblSimulationsNumber ()
    {
        if (lblSimulationsNumber == null)
        {
            lblSimulationsNumber = new JLabel (stringDatabase.getString ("CostEffectiveness.NumberOfSimulations"));
            lblSimulationsNumber.setVisible (false);
        }
        return lblSimulationsNumber;
    }

    private JLabel getInitialAgeLabel ()
    {
        if (initialAgeLabel == null)
        {
            initialAgeLabel = new JLabel (stringDatabase.getString ("CostEffectiveness.InitialAge"));
        }
        return initialAgeLabel;
    }

    private JComboBox<String> getUnitsJComboBox ()
    {
        if (unitsCombo == null)
        {
            String units[] = {stringDatabase.getString ("CostEffectiveness.Months"), stringDatabase.getString ("CostEffectiveness.Years")};
            unitsCombo = new JComboBox<> (units);
            unitsCombo.addItemListener (this);
        }
        return unitsCombo;
    }

    private JCheckBox getCheckCycleCero ()
    {
        if (checkZeroCycle == null)
        {
            checkZeroCycle = new JCheckBox (stringDatabase.getString ("CostEffectiveness.ZeroCycle"));
        }
        return checkZeroCycle;
    }

    private JLabel getCycleLengthLabel ()
    {
        if (cycleLengthLabel == null)
        {
            cycleLengthLabel = new JLabel (stringDatabase.getString ("CostEffectiveness.CycleLength"));
        }
        return cycleLengthLabel;
    }

    private JTextField getCycleLengthTextField ()
    {
        if (cycleLengthTextField == null)
        {
            cycleLengthTextField = new JTextField ("1");
        }
        return cycleLengthTextField;
    }

    private JLabel getFinalAgeLabel ()
    {
        if (finalAgeLabel == null)
        {
            finalAgeLabel = new JLabel (stringDatabase.getString ("CostEffectiveness.FinalAge"));
        }
        return finalAgeLabel;
    }

    private JLabel getCostDiscountLabel ()
    {
        if (costDiscountLabel == null)
        {
            costDiscountLabel = new JLabel (stringDatabase.getString ("CostEffectiveness.CostDiscount"));
        }
        return costDiscountLabel;
    }

    private JLabel getEffectivenessDiscountLabel ()
    {
        if (effectivenessDiscountLabel == null)
        {
            effectivenessDiscountLabel = new JLabel (stringDatabase.getString ("CostEffectiveness.EffectivenessDiscount"));
        }
        return effectivenessDiscountLabel;
    }

    private JTextField getFinalAgeTextField ()
    {
        if (finalAgeTextField == null)
        {
            finalAgeTextField = new JTextField ("100");
            finalAgeTextField.setName ("finalAgeText");
            // finalAgeTextField.addActionListener(this);
        }
        return finalAgeTextField;
    }

    private JTextField getInitialAgeTextField ()
    {
        if (initialAgeTextField == null)
        {
            initialAgeTextField = new JTextField ("12");
            initialAgeTextField.setName ("initialAgeText");
            // initialAgeTextField.addActionListener(this);
        }
        return initialAgeTextField;
    }

    private JTextField getCostDiscountTextField ()
    {
        if (costDiscountTextField == null)
        {
            costDiscountTextField = new JTextField ("3.0");
            costDiscountTextField.setColumns (10);
        }
        return costDiscountTextField;
    }

    private JTextField getEffectivenessDiscountTextField ()
    {
        if (effectivenessDiscountTextField == null)
        {
            effectivenessDiscountTextField = new JTextField ("3.0");
            effectivenessDiscountTextField.setColumns (10);
        }
        return effectivenessDiscountTextField;
    }

    private JRadioButton getInstantValuesButton ()
    {
        if (instantButton == null)
        {
            instantButton = new JRadioButton (stringDatabase.getString ("CostEffectiveness.InstantValues"), true);
            instantButton.addItemListener (this);
        }
        return instantButton;
    }

    private JRadioButton getAccumulativeValuesButton ()
    {
        if (accumulativeButton == null)
        {
            accumulativeButton = new JRadioButton (stringDatabase.getString ("CostEffectiveness.CumulativeValues"), false);
            accumulativeButton.addItemListener (this);
        }
        return accumulativeButton;
    }

    public Map<Variable, Double> getNumericTemporalValues ()
    {
        return numericTemporalVariables;
    }

    private void initButtonGroup ()
    {
        buttonGroup = new ButtonGroup ();
        buttonGroup.add (getInstantValuesButton ());
        buttonGroup.add (getAccumulativeValuesButton ());
    }

    /**
     * @return the panel with the two buttons
     */
    private JPanel getJPanelInstantOrAccumulative ()
    {
        if (instantOrAccumulativePanel == null)
        {
            instantOrAccumulativePanel = new JPanel ();
            instantOrAccumulativePanel.setLayout (new GridLayout (2, 1));
            instantOrAccumulativePanel.setBorder (BorderFactory.createTitledBorder (BorderFactory.createEtchedBorder (),
                                                                                    stringDatabase.getString ("CostEffectiveness.TemporalDisplay")));
            instantOrAccumulativePanel.setName ("instantOrAccumulativePanel");
            initButtonGroup ();
            instantOrAccumulativePanel.add (getInstantValuesButton ());
            instantOrAccumulativePanel.add (getAccumulativeValuesButton ());
        }
        return instantOrAccumulativePanel;
    }

    public int requestData ()
    {
        setVisible (true);
        return selectedButton;
    }

    @Override
    protected boolean doOkClickBeforeHide ()
    {
        boolean allValid = checkTextFieldsValidity();
        if(allValid)
        {
            if (containsAgeNode)
            {
                initialAge = Integer.valueOf (getInitialAgeTextField ().getText ());
                finalAge = Integer.valueOf (getFinalAgeTextField ().getText ());
            }
            else
            {
                numSlices = Integer.valueOf (getNumSlicesTextField ().getText ());
            }
            costDiscount = Double.valueOf (getCostDiscountTextField ().getText ());
            effectivenessDiscount = Double.valueOf (getEffectivenessDiscountTextField ().getText ());
            cycleLength = Double.valueOf (getCycleLengthTextField ().getText ());
            simulationsNumber = Integer.valueOf (getTxtSimulationsNumber ().getText ());
        }
        return allValid;
    }

    public Integer getInitialAge ()
    {
        return initialAge;
    }

    public Integer getFinalAge ()
    {
        return finalAge;
    }

    public double getCostDiscount ()
    {
        return costDiscount;
    }

    public double getCycleLength ()
    {
        return cycleLength;
    }

    public double getEffectivenessDiscount ()
    {
        return effectivenessDiscount;
    }

    public int getNumSlices ()
    {
        return (numSlices != null)? numSlices : finalAge - initialAge;
    }

    public String getUnits ()
    {
        return units;
    }

    public boolean getZeroCycle ()
    {
        return getCheckCycleCero ().isSelected ();
    }

    public void showSimulationsNumberElements (boolean isProbabilistic)
    {
        getLblSimulationsNumber ().setVisible (isProbabilistic);
        getTxtSimulationsNumber ().setVisible (isProbabilistic);
    }

    public int getSimulationsNumber ()
    {
        return simulationsNumber;
    }

    public boolean isThereNodeAge ()
    {
        return containsAgeNode;
    }

    public boolean isAccumulative ()
    {
        return this.isAccumulative;
    }

    @Override
    public void itemStateChanged (ItemEvent e)
    {
        if (e.getItem ().equals (getInstantValuesButton ()))
        {
            this.isAccumulative = false;
        }
        if (e.getItem ().equals (getAccumulativeValuesButton ()))
        {
            this.isAccumulative = true;
        }
        if (e.getItem ().equals (getUnitsJComboBox ()))
        {
            units = (String) getUnitsJComboBox ().getSelectedItem ();
        }
    }

    private void setTitle (String netName, boolean isTemporalEvolution)
    {
        String title = stringDatabase.getString (((isTemporalEvolution)? "CostEffectiveness.TemporalEvolution" : "CostEffectiveness.Analysis") + ".Label");
        super.setTitle (title + " - " +  FilenameUtils.getBaseName (netName));
    }

    private boolean checkTextFieldsValidity()
    {
        boolean allValid = true;
        if(initialAgeTextField != null)
        {
            allValid &= checkTextFieldValidity(initialAgeTextField);
        }
        if(finalAgeTextField != null)
        {
            allValid &= checkTextFieldValidity(finalAgeTextField);
        }
        
        for(JTextField numericTemporalField : numericTemporalComponents.values ())
        {
            allValid &= checkTextFieldValidity(numericTemporalField);
        }
        
        return allValid;
    }
    private boolean checkTextFieldValidity (JTextField sourceTextField)
    {
        boolean valid = true;
        if (sourceTextField.getName ().equals ("initialAgeText"))
        {
            for (Variable numericTemporalVariable : numericTemporalVariables.keySet ())
            {
                if (numericTemporalVariable.isTemporal ()
                    && numericTemporalVariable.getBaseName ().equalsIgnoreCase ("age")
                    && numericTemporalVariable.getTimeSlice () == 0)
                {
                    double initialAge = Double.parseDouble (sourceTextField.getText ());
                    PartitionedInterval interval = numericTemporalVariable.getPartitionedInterval ();
                    // check whether introduced values are correct or not
                    if ((!interval.isLeftClosed () && initialAge <= interval.getMin ()) || initialAge < interval.getMin ())
                    {
                        valid = false;
                        JOptionPane.showMessageDialog (this.getParent (),
                                                       stringDatabase.getString ("CostEffectiveness.InitialAge")+ " "+
                                                               stringDatabase.getString ("CostEffectiveness.VariableTooLow"));
                    }else
                    {
                        this.initialAge =  (int) initialAge; 
                    }
                    sourceTextField.setText (""+this.initialAge);
                }
            }
        }
        if (sourceTextField.getName ().equals ("finalAgeText"))
        {
            for (Variable numericTemporalVariable : numericTemporalVariables.keySet ())
            {
                if (numericTemporalVariable.isTemporal ()
                    && numericTemporalVariable.getBaseName ().equalsIgnoreCase ("age")
                    && numericTemporalVariable.getTimeSlice () == 0)
                {
                    double finalAge = Double.parseDouble (sourceTextField.getText ());
                    PartitionedInterval interval = numericTemporalVariable.getPartitionedInterval ();
                    // check whether introduced values are correct or nor
                    if ((!interval.isLeftClosed () && finalAge <= interval.getMin ())
                        || finalAge < interval.getMin ())
                    {
                        JOptionPane.showMessageDialog (this.getParent (),
                                                       stringDatabase.getString ("CostEffectiveness.FinalAge")+ " "+
                                                       stringDatabase.getString ("CostEffectiveness.VariableTooLow"));
                        valid = false;
                        
                    }
                    else if ((!interval.isRightClosed () && finalAge >= interval.getMax ())
                             || finalAge > interval.getMax ())
                    {
                        JOptionPane.showMessageDialog (this.getParent (),
                                                       stringDatabase.getString ("CostEffectiveness.FinalAge")+ " "+
                                                               stringDatabase.getString ("CostEffectiveness.VariableTooHigh"));
                        valid = false;
                    }else
                    {
                        this.finalAge =  (int) finalAge; 
                    }
                    sourceTextField.setText (""+this.finalAge);
                }
            }
        }
        if (numericTemporalComponents.containsKey (sourceTextField.getName ()) || 
                sourceTextField.equals (getNumSlicesTextField ()))
        {
            int cycleLength = Integer.valueOf (getCycleLengthTextField ().getText ());
            boolean numSlicesDefined = getNumSlicesTextField ().getText () != null;
            int numSlices = (numSlicesDefined) ? Integer.valueOf (getNumSlicesTextField ().getText ()) : -1;
            for (Variable numericTemporalVariable  : numericTemporalVariables.keySet ())
            {
                PartitionedInterval interval = numericTemporalVariable.getPartitionedInterval ();
                double numericValue = Double.parseDouble (numericTemporalComponents.get (numericTemporalVariable.getName ()) .getText ());
                double timeHorizon = numericValue + numSlices * cycleLength;
                if (numSlicesDefined)
                {
                    if ((!interval.isRightClosed () && timeHorizon >= interval.getMax ())
                        || timeHorizon > interval.getMax ())
                    {
                        JOptionPane.showMessageDialog (this.getParent (),
                                                       numericTemporalVariable.getBaseName ()
                                                               + " " + stringDatabase.getString ("CostEffectiveness.ExceedsTimeHorizon"));
                        valid = false;
                    }
                }
                if ((!interval.isLeftClosed () && numericValue <= interval.getMin ())
                    || numericValue < interval.getMin ())
                {
                    JOptionPane.showMessageDialog (this.getParent (),
                                                   numericTemporalVariable.getBaseName () +  " "
                                                           + stringDatabase.getString ("CostEffectiveness.VariableTooLow"));
                    valid = false;
                }
                if(valid)
                {
                    numericTemporalVariables.put (numericTemporalVariable, numericValue); 
                }
                numericTemporalComponents.get (numericTemporalVariable.getName ()).setText (""+numericValue);
           }
            
            if(valid)
            {
                this.numSlices = numSlices;
            }
            getNumSlicesTextField ().setText (""+this.numSlices);
        }
        return valid;
    }

    @Override
    public void focusGained (FocusEvent e)
    {
        // Ignore
    }

    @Override
    public void focusLost (FocusEvent e)
    {
        if (e.getSource () instanceof JTextField && ((JTextField) e.getSource ()).getName () != null)
        {
            JTextField sourceTextField = (JTextField) e.getSource ();
            checkTextFieldValidity(sourceTextField);
        }  
    }    
}
