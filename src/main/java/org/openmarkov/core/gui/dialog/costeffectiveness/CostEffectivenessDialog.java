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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;

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
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;

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
        PropertyChangeListener,
        FocusListener
{
    // TODO internationalization
    /**
	 * 
	 */
    private static final long           serialVersionUID          = 1L;
    private final JPanel                contentPanel              = new JPanel ();
    private JLabel                      initialAgeLabel;
    private JLabel                      cycleLengthLabel;
    private JLabel                      unitLabel;
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
    private JLabel                      yearsLabel;
    private Integer                     initialAge;
    private Integer                     finalAge;
    private Double                      costDiscount;
    private Double                      cycleLength;
    private String                      units;
    private Double                      effectivenessDiscount;
    private JLabel                      lblOutputFile;
    private JLabel                      yearsLabel2;
    private JTextField                  txtSimulationNumber;
    private JLabel                      lblSimulationsNumber;
    private Integer                     simulationsNumber;
    private boolean                     thereIsNodeAge            = false;
    private JLabel                      numSlicesLabell;
    private JTextField                  numSlicesJTextField;
    private Integer                     numSlices;
    private JRadioButton                instantButton;
    private JRadioButton                accumulativeButton;
    private ButtonGroup                 buttonGroup;
    private JPanel                      instantOrAccumulativePanel;
    private boolean                     isAccumulative            = false;
    private JPanel                      numSlicesPanel;
    private List<ProbNode>              numericTemporalNodes;
    private HashMap<String, JTextField> numericTemporalComponents = new HashMap<> ();
    

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
        this.thereIsNodeAge = probNet.checkIfThereIsAgeNode ();
        this.numericTemporalNodes = probNet.getSpecialTimeDependentNodes ();
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
        int rows = numericTemporalNodes.size () + 2;
        rows = (!thereIsNodeAge) ? (rows + 1) : rows;
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
        if (thereIsNodeAge)
        {
            panel.add (getInitialAgeLabel ());
            panel.add (getInitialAgeTextField ());
            panel.add (getFinalAgeLabel ());
            panel.add (getFinalAgeTextField ());
        }
        else
        {
            panel.add (getJLabelNumSlices ());
            panel.add (getNumSlicesJTextField ());
            panel.add (new JLabel (""));
            panel.add (new JLabel (""));
        }
        for (int i = 0; i < numericTemporalNodes.size (); i++)
        {
            if (!numericTemporalNodes.get (i).getVariable ().getBaseName ().equalsIgnoreCase ("Age"))
            {
                JLabel label = new JLabel (numericTemporalNodes.get (i).getVariable ().getName ());
                JTextField textField = new JTextField (10);
                textField.setName (numericTemporalNodes.get (i).getVariable ().getName ());
                textField.setText ("0");
                textField.addPropertyChangeListener (this);
                textField.addFocusListener (this);
                panel.add (label);
                panel.add (textField);
                numericTemporalComponents.put (label.getText (), textField);
                panel.add (new JLabel ("Cycles"));
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
                                                                                                                                                                                                                                                                                                                          false).addGroup (groupLayout.createSequentialGroup ().addComponent (getJLabelNumSlices ()).addPreferredGap (LayoutStyle.ComponentPlacement.RELATED).addComponent (getNumSlicesJTextField (),
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            75,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            GroupLayout.PREFERRED_SIZE))).addContainerGap ()))));
            groupLayout.setVerticalGroup (groupLayout.createParallelGroup (Alignment.LEADING).addGroup (groupLayout.createSequentialGroup ().addContainerGap ().addGroup (groupLayout.createParallelGroup (Alignment.LEADING).addComponent (getJLabelNumSlices ()).addComponent (getNumSlicesJTextField (),
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
            numSlicesLabell = new JLabel ("Number of Cycles");
        }
        return numSlicesLabell;
    }

    private JTextField getNumSlicesJTextField ()
    {
        if (numSlicesJTextField == null)
        {
            numSlicesJTextField = new JTextField ();
            numSlicesJTextField.setText ("1");
            numSlicesJTextField.setColumns (10);
        }
        return numSlicesJTextField;
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
            lblSimulationsNumber = new JLabel ("Simulations number");
            lblSimulationsNumber.setVisible (false);
        }
        return lblSimulationsNumber;
    }

    public JLabel getOutputFileLabel ()
    {
        if (lblOutputFile == null)
        {
            lblOutputFile = new JLabel ("Output file name");
        }
        return lblOutputFile;
    }

    private JLabel getYearsLabel2 ()
    {
        if (yearsLabel2 == null)
        {
            yearsLabel2 = new JLabel ("years");
        }
        return yearsLabel2;
    }

    public JLabel getInitialAgeLabel ()
    {
        if (initialAgeLabel == null)
        {
            initialAgeLabel = new JLabel ("Initial age");
        }
        return initialAgeLabel;
    }

    public JLabel unitLabel ()
    {
        if (unitLabel == null)
        {
            unitLabel = new JLabel ("Cycle Length");
        }
        return unitLabel;
    }

    public JComboBox<String> getUnitsJComboBox ()
    {
        if (unitsCombo == null)
        {
            String units[] = {"months", "years"};
            unitsCombo = new JComboBox<> (units);
            unitsCombo.addItemListener (this);
        }
        return unitsCombo;
    }

    public JCheckBox getCheckCycleCero ()
    {
        if (checkZeroCycle == null)
        {
            checkZeroCycle = new JCheckBox ("Zero cycle");
        }
        return checkZeroCycle;
    }

    public JLabel getCycleLengthLabel ()
    {
        if (cycleLengthLabel == null)
        {
            cycleLengthLabel = new JLabel ("Cycle Length");
        }
        return cycleLengthLabel;
    }

    public JTextField getCycleLengthTextField ()
    {
        if (cycleLengthTextField == null)
        {
            cycleLengthTextField = new JTextField ("1");
        }
        return cycleLengthTextField;
    }

    public JLabel getFinalAgeLabel ()
    {
        if (finalAgeLabel == null)
        {
            finalAgeLabel = new JLabel ("Final age");
        }
        return finalAgeLabel;
    }

    public JLabel getCostDiscountLabel ()
    {
        if (costDiscountLabel == null)
        {
            costDiscountLabel = new JLabel ("Cost discount (%)");
        }
        return costDiscountLabel;
    }

    public JLabel getEffectivenessDiscountLabel ()
    {
        if (effectivenessDiscountLabel == null)
        {
            effectivenessDiscountLabel = new JLabel ("Effectiveness discount (%)");
        }
        return effectivenessDiscountLabel;
    }

    private JLabel getYearsLabel ()
    {
        if (yearsLabel == null)
        {
            yearsLabel = new JLabel ("years");
        }
        return yearsLabel;
    }

    public JTextField getFinalAgeTextField ()
    {
        if (finalAgeTextField == null)
        {
            finalAgeTextField = new JTextField ("100");
            finalAgeTextField.setName ("finalAgeText");
            // finalAgeTextField.addActionListener(this);
        }
        return finalAgeTextField;
    }

    public JTextField getInitialAgeTextField ()
    {
        if (initialAgeTextField == null)
        {
            initialAgeTextField = new JTextField ("12");
            initialAgeTextField.setName ("initialAgeText");
            // initialAgeTextField.addActionListener(this);
        }
        return initialAgeTextField;
    }

    public JTextField getCostDiscountTextField ()
    {
        if (costDiscountTextField == null)
        {
            costDiscountTextField = new JTextField ("3.0");
            costDiscountTextField.setColumns (10);
        }
        return costDiscountTextField;
    }

    public JTextField getEffectivenessDiscountTextField ()
    {
        if (effectivenessDiscountTextField == null)
        {
            effectivenessDiscountTextField = new JTextField ("3.0");
            effectivenessDiscountTextField.setColumns (10);
        }
        return effectivenessDiscountTextField;
    }

    public JRadioButton getInstantValuesButton ()
    {
        if (instantButton == null)
        {
            instantButton = new JRadioButton ("Instant values", true);
            instantButton.addItemListener (this);
        }
        return instantButton;
    }

    public JRadioButton getAccumulativeValuesButton ()
    {
        if (accumulativeButton == null)
        {
            accumulativeButton = new JRadioButton ("Cumulative values", false);
            accumulativeButton.addItemListener (this);
        }
        return accumulativeButton;
    }

    public HashMap<String, JTextField> getNumericTemporalValues ()
    {
        return numericTemporalComponents;
    }

    public void initButtonGroup ()
    {
        buttonGroup = new ButtonGroup ();
        buttonGroup.add (getInstantValuesButton ());
        buttonGroup.add (getAccumulativeValuesButton ());
    }

    /**
     * @return the panel with the two buttons
     */
    protected JPanel getJPanelInstantOrAccumulative ()
    {
        if (instantOrAccumulativePanel == null)
        {
            instantOrAccumulativePanel = new JPanel ();
            instantOrAccumulativePanel.setLayout (new GridLayout (2, 1));
            // jPanelTpcOrCanonical.setSize( 152, 58 );
            // instantOrAccumulativePanel.setBorder( new LineBorder( UIManager
            // .getColor( "List.dropLineColor" ), 1, false ) );
            instantOrAccumulativePanel.setBorder (BorderFactory.createTitledBorder (BorderFactory.createEtchedBorder (),
                                                                                    "Temporal display"));
            instantOrAccumulativePanel.setName ("instantOrAccumulativePanel");
            initButtonGroup ();
            instantOrAccumulativePanel.add (getInstantValuesButton ());
            instantOrAccumulativePanel.add (getAccumulativeValuesButton ());
            // instantOrAccumulativePanel.setEnabled( true);
            // instantOrAccumulativePanel.setVisible(true);
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
        if (thereIsNodeAge)
        {
            initialAge = Integer.valueOf (getInitialAgeTextField ().getText ());
            finalAge = Integer.valueOf (getFinalAgeTextField ().getText ());
        }
        else
        {
            numSlices = Integer.valueOf (getNumSlicesJTextField ().getText ());
        }
        costDiscount = Double.valueOf (getCostDiscountTextField ().getText ());
        effectivenessDiscount = Double.valueOf (getEffectivenessDiscountTextField ().getText ());
        cycleLength = Double.valueOf (getCycleLengthTextField ().getText ());
        simulationsNumber = Integer.valueOf (getTxtSimulationsNumber ().getText ());
        return true;
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
        return thereIsNodeAge;
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

    @Override
    public void propertyChange (PropertyChangeEvent e)
    {
        if (((JTextField) e.getSource ()).getName () != null)
        {
            if (e.getSource () instanceof JTextField
                && ((JTextField) e.getSource ()).getName ().equals ("initialAgeText"))
            {
                for (int i = 0; i < numericTemporalNodes.size (); i++)
                {
                    if (numericTemporalNodes.get (i).getVariable ().isTemporal ()
                        && numericTemporalNodes.get (i).getVariable ().getBaseName ().equals ("Age")
                        && numericTemporalNodes.get (i).getVariable ().getTimeSlice () == 0)
                    {
                        // check whether introduced values are correct or nor
                        if (numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().isLeftClosed ())
                        {
                            if (Double.parseDouble (((JTextField) e.getSource ()).getText ()) < numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().getMin ())
                            {
                                JOptionPane.showMessageDialog (this.getParent (),
                                                               "Initial age is less than the minimum of variable domain");
                            }
                        }
                        else
                        {
                            if (Double.parseDouble (((JTextField) e.getSource ()).getText ()) <= numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().getMin ())
                            {
                                JOptionPane.showMessageDialog (this.getParent (),
                                                               "Initial age is less than the minimum of variable domain");
                            }
                        }
                    }
                }
            }
            if (e.getSource () instanceof JTextField
                && ((JTextField) e.getSource ()).getName ().equals ("finalAgeText"))
            {
                for (int i = 0; i < numericTemporalNodes.size (); i++)
                {
                    if (numericTemporalNodes.get (i).getVariable ().isTemporal ()
                        && numericTemporalNodes.get (i).getVariable ().getBaseName ().equals ("Age")
                        && numericTemporalNodes.get (i).getVariable ().getTimeSlice () == 0)
                    {
                        // check whether introduced values are correct or nor
                        if (numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().isLeftClosed ())
                        {
                            if (Double.parseDouble (((JTextField) e.getSource ()).getText ()) < numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().getMin ())
                            {
                                JOptionPane.showMessageDialog (this.getParent (),
                                                               "Final age is less than the minimum of variable domain");
                            }
                        }
                        else
                        {
                            if (Double.parseDouble (((JTextField) e.getSource ()).getText ()) <= numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().getMin ())
                            {
                                JOptionPane.showMessageDialog (this.getParent (),
                                                               "Final age is less than the minimum of variable domain");
                            }
                        }
                        if (numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().isRightClosed ())
                        {
                            if (Double.parseDouble (((JTextField) e.getSource ()).getText ()) > numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().getMax ())
                            {
                                JOptionPane.showMessageDialog (this.getParent (),
                                                               "Final age is more than the maximum of variable domain");
                            }
                        }
                        else
                        {
                            if (Double.parseDouble (((JTextField) e.getSource ()).getText ()) >= numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().getMax ())
                            {
                                JOptionPane.showMessageDialog (this.getParent (),
                                                               "Final age is more than the maximum of variable domain");
                            }
                        }
                    }
                }
            }
            if (e.getSource () instanceof JTextField
                && numericTemporalComponents.get (((JTextField) e.getSource ()).getName ()) != null)
            {
                for (int i = 0; i < numericTemporalNodes.size (); i++)
                {
                    if (((JTextField) e.getSource ()).getName ().equals (numericTemporalNodes.get (i).getVariable ().getName ()))
                    {
                        if (numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().isRightClosed ())
                        {
                            if (Double.parseDouble (((JTextField) e.getSource ()).getText ()) > numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().getMax ())
                            {
                                JOptionPane.showMessageDialog (this.getParent (),
                                                               numericTemporalNodes.get (i).getVariable ().getName ()
                                                                       + " is more than the maximum of variable domain");
                            }
                            if (getNumSlicesJTextField ().getText () != null
                                && Double.parseDouble (((JTextField) e.getSource ()).getText ())
                                   + Integer.valueOf (getNumSlicesJTextField ().getText ())
                                   * Integer.valueOf (getCycleLengthTextField ().getText ()) > numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().getMax ())
                            {
                                JOptionPane.showMessageDialog (this.getParent (),
                                                               numericTemporalNodes.get (i).getVariable ().getName ()
                                                                       + " exceeds the time horizon");
                            }
                        }
                        else
                        {
                            if (Double.parseDouble (((JTextField) e.getSource ()).getText ()) >= numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().getMax ())
                            {
                                JOptionPane.showMessageDialog (this.getParent (),
                                                               numericTemporalNodes.get (i).getVariable ().getName ()
                                                                       + " is more than the maximum of variable domain");
                            }
                            if (getNumSlicesJTextField ().getText () != null
                                && Double.parseDouble (((JTextField) e.getSource ()).getText ())
                                   + Integer.valueOf (getNumSlicesJTextField ().getText ())
                                   * Integer.valueOf (getCycleLengthTextField ().getText ()) >= numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().getMax ())
                            {
                                ((JTextField) e.getSource ()).setText ("");
                                JOptionPane.showMessageDialog (this.getParent (),
                                                               numericTemporalNodes.get (i).getVariable ().getName ()
                                                                       + " exceeds the time horizon");
                            }
                        }
                        if (numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().isLeftClosed ())
                        {
                            if (Double.parseDouble (((JTextField) e.getSource ()).getText ()) < numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().getMin ())
                            {
                                JOptionPane.showMessageDialog (this.getParent (),
                                                               numericTemporalNodes.get (i).getVariable ().getName ()
                                                                       + " is less than the minimum of variable domain");
                            }
                        }
                        else
                        {
                            if (Double.parseDouble (((JTextField) e.getSource ()).getText ()) <= numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().getMin ())
                            {
                                JOptionPane.showMessageDialog (this.getParent (),
                                                               numericTemporalNodes.get (i).getVariable ().getName ()
                                                                       + " is less than the minimum of variable domain");
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void focusGained (FocusEvent arg0)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void focusLost (FocusEvent e)
    {
        if (((JTextField) e.getSource ()).getName () != null)
        {
            if (e.getSource () instanceof JTextField
                && ((JTextField) e.getSource ()).getName ().equals ("initialAgeText"))
            {
                for (int i = 0; i < numericTemporalNodes.size (); i++)
                {
                    if (numericTemporalNodes.get (i).getVariable ().isTemporal ()
                        && numericTemporalNodes.get (i).getVariable ().getBaseName ().equals ("Age")
                        && numericTemporalNodes.get (i).getVariable ().getTimeSlice () == 0)
                    {
                        // check whether introduced values are correct or nor
                        if (numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().isLeftClosed ())
                        {
                            if (Double.parseDouble (((JTextField) e.getSource ()).getText ()) < numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().getMin ())
                            {
                                ((JTextField) e.getSource ()).setText ("");
                                JOptionPane.showMessageDialog (this.getParent (),
                                                               "Initial age is less than the minimum of variable domain");
                            }
                        }
                        else
                        {
                            if (Double.parseDouble (((JTextField) e.getSource ()).getText ()) <= numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().getMin ())
                            {
                                ((JTextField) e.getSource ()).setText ("");
                                JOptionPane.showMessageDialog (this.getParent (),
                                                               "Initial age is less than the minimum of variable domain");
                            }
                        }
                    }
                }
            }
            if (e.getSource () instanceof JTextField
                && ((JTextField) e.getSource ()).getName ().equals ("finalAgeText"))
            {
                for (int i = 0; i < numericTemporalNodes.size (); i++)
                {
                    if (numericTemporalNodes.get (i).getVariable ().isTemporal ()
                        && numericTemporalNodes.get (i).getVariable ().getBaseName ().equals ("Age")
                        && numericTemporalNodes.get (i).getVariable ().getTimeSlice () == 0)
                    {
                        // check whether introduced values are correct or nor
                        if (numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().isLeftClosed ())
                        {
                            if (Double.parseDouble (((JTextField) e.getSource ()).getText ()) < numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().getMin ())
                            {
                                ((JTextField) e.getSource ()).setText ("");
                                JOptionPane.showMessageDialog (this.getParent (),
                                                               "Final age is less than the minimum of variable domain");
                            }
                        }
                        else
                        {
                            if (Double.parseDouble (((JTextField) e.getSource ()).getText ()) <= numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().getMin ())
                            {
                                ((JTextField) e.getSource ()).setText ("");
                                JOptionPane.showMessageDialog (this.getParent (),
                                                               "Final age is less than the minimum of variable domain");
                            }
                        }
                        if (numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().isRightClosed ())
                        {
                            if (Double.parseDouble (((JTextField) e.getSource ()).getText ()) > numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().getMax ())
                            {
                                ((JTextField) e.getSource ()).setText ("");
                                JOptionPane.showMessageDialog (this.getParent (),
                                                               "Final age is more than the maximum of variable domain");
                            }
                        }
                        else
                        {
                            if (Double.parseDouble (((JTextField) e.getSource ()).getText ()) >= numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().getMax ())
                            {
                                ((JTextField) e.getSource ()).setText ("");
                                JOptionPane.showMessageDialog (this.getParent (),
                                                               "Final age is more than the maximum of variable domain");
                            }
                        }
                    }
                }
            }
            if (e.getSource () instanceof JTextField
                && numericTemporalComponents.get (((JTextField) e.getSource ()).getName ()) != null)
            {
                for (int i = 0; i < numericTemporalNodes.size (); i++)
                {
                    if (((JTextField) e.getSource ()).getName ().equals (numericTemporalNodes.get (i).getVariable ().getName ()))
                    {
                        if (numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().isRightClosed ())
                        {
                            if (Double.parseDouble (((JTextField) e.getSource ()).getText ()) > numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().getMax ())
                            {
                                ((JTextField) e.getSource ()).setText ("");
                                JOptionPane.showMessageDialog (this.getParent (),
                                                               numericTemporalNodes.get (i).getVariable ().getName ()
                                                                       + " is more than the maximum of variable domain");
                            }
                            if (getNumSlicesJTextField ().getText () != null
                                && Double.parseDouble (((JTextField) e.getSource ()).getText ())
                                   + Integer.valueOf (getNumSlicesJTextField ().getText ())
                                   * Integer.valueOf (getCycleLengthTextField ().getText ()) > numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().getMax ())
                            {
                                ((JTextField) e.getSource ()).setText ("");
                                JOptionPane.showMessageDialog (this.getParent (),
                                                               numericTemporalNodes.get (i).getVariable ().getName ()
                                                                       + " exceeds the time horizon");
                            }
                        }
                        else
                        {
                            if (Double.parseDouble (((JTextField) e.getSource ()).getText ()) >= numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().getMax ())
                            {
                                ((JTextField) e.getSource ()).setText ("");
                                JOptionPane.showMessageDialog (this.getParent (),
                                                               numericTemporalNodes.get (i).getVariable ().getName ()
                                                                       + " is more than the maximum of variable domain");
                            }
                            if (getNumSlicesJTextField ().getText () != null
                                && Double.parseDouble (((JTextField) e.getSource ()).getText ())
                                   + Integer.valueOf (getNumSlicesJTextField ().getText ())
                                   * Integer.valueOf (getCycleLengthTextField ().getText ()) >= numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().getMax ())
                            {
                                ((JTextField) e.getSource ()).setText ("");
                                JOptionPane.showMessageDialog (this.getParent (),
                                                               numericTemporalNodes.get (i).getVariable ().getName ()
                                                                       + " exceeds the time horizon");
                            }
                        }
                        if (numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().isLeftClosed ())
                        {
                            if (Double.parseDouble (((JTextField) e.getSource ()).getText ()) < numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().getMin ())
                            {
                                ((JTextField) e.getSource ()).setText ("");
                                JOptionPane.showMessageDialog (this.getParent (),
                                                               numericTemporalNodes.get (i).getVariable ().getName ()
                                                                       + " is less than the minimum of variable domain");
                            }
                        }
                        else
                        {
                            if (Double.parseDouble (((JTextField) e.getSource ()).getText ()) <= numericTemporalNodes.get (i).getVariable ().getPartitionedInterval ().getMin ())
                            {
                                ((JTextField) e.getSource ()).setText ("");
                                JOptionPane.showMessageDialog (this.getParent (),
                                                               numericTemporalNodes.get (i).getVariable ().getName ()
                                                                       + " is less than the minimum of variable domain");
                            }
                        }
                    }
                }
            }
        }
    }

    public void setTitle (String netName, boolean isTemporalEvolution)
    {
        String title = stringDatabase.getString (((isTemporalEvolution)? "CostEffectiveness.TemporalEvolution" : "CostEffectiveness.Analysis") + ".Label");
        super.setTitle (title + " - " +  FilenameUtils.getBaseName (netName));
    }
}
