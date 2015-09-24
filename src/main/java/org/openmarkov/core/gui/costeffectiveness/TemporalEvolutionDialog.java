/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.costeffectiveness;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Window;
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

import org.apache.commons.io.FilenameUtils;
import org.openmarkov.core.gui.dialog.common.OkCancelHorizontalDialog;
import org.openmarkov.core.gui.dialog.inference.common.ScopeSelectorPanel;
import org.openmarkov.core.gui.dialog.inference.common.ScopeType;
import org.openmarkov.core.inference.TransitionTime;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.Variable;

/**
 * Input dialog for cost effectiveness purposes used to introduce relevant
 * information such as cycle length, number of cycles, introduce findings to
 * numerical variables within the network, cost and effectiveness discount
 * rate...
 *
 * @author myebra
 */
public class TemporalEvolutionDialog extends OkCancelHorizontalDialog {
    private static final long       serialVersionUID       = 1L;
    private JLabel                  numSlicesLabel;
    private JTextField              numSlicesTextField;
    private Integer                 numSlices;
    private ProbNet                 probNet;
    private ScopeSelectorPanel      scopeSelectorPanel;

    /**
     * Creates a CostEffectivenessDialog for temporal evolution
     *
     * @param owner
     *            The parent of the dialog
     */
    public TemporalEvolutionDialog(Window owner, ProbNet probNet) {
        super(owner);
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
        setMinimumSize(new Dimension(300, 300));
        this.setResizable(true);
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

        if(scopeSelectorPanel == null){
            scopeSelectorPanel = new ScopeSelectorPanel(probNet);
        }
        otherPanel.add(scopeSelectorPanel);
        panel.add(otherPanel, BorderLayout.NORTH);
        getComponentsPanel().setLayout(new BorderLayout(20, 0));
        getComponentsPanel().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        getComponentsPanel().add(panel, BorderLayout.NORTH);
        getComponentsPanel().add(new JPanel());
        pack();
        repaint();
    }

    private JLabel getJLabelNumSlices() {
        if (numSlicesLabel == null) {
            numSlicesLabel = new JLabel(stringDatabase.getString("CostEffectiveness.NumberOfCycles"));
        }
        return numSlicesLabel;
    }

    private JTextField getNumSlicesTextField() {
        if (numSlicesTextField == null) {
            numSlices = Integer.parseInt(numSlicesTextField.getText());
            numSlicesTextField = new JTextField();
            numSlicesTextField.setText("" + numSlices);
            numSlicesTextField.setColumns(10);
            numSlicesTextField.setName("numSlicesTextField");
        }
        return numSlicesTextField;
    }

    @Override
    protected boolean doOkClickBeforeHide() {
        numSlices = Integer.valueOf(getNumSlicesTextField().getText());
        return true;
    }


    public void setTitle(String netName) {
        String title = stringDatabase.getString("CostEffectiveness.TemporalEvolution"
                + ".Label");
        super.setTitle(title + " - " + FilenameUtils.getBaseName(netName));
    }

    public int requestData() {
        return getSelectedButton();
    }

    public int getNumSlices() {
        return numSlices;
    }
}
