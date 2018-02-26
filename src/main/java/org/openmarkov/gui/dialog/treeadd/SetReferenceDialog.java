/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.gui.dialog.treeadd;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.util.Map;

import javax.swing.JComboBox;

import org.openmarkov.gui.dialog.common.OkCancelHorizontalDialog;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDBranch;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDPotential;

@SuppressWarnings("serial")
public class SetReferenceDialog  extends OkCancelHorizontalDialog{

    private TreeADDBranch branch;
    private JComboBox<String> labels;
    private Map<String,TreeADDBranch> labeledBranches;
    
    public SetReferenceDialog(Window owner, TreeADDBranch branch, TreeADDPotential rootTreeADDPotential) {
        super(owner);
        this.branch = branch;
        labels = new JComboBox<>();
        labels.setSize(new Dimension(150, 25));
        setMinimumSize(new Dimension( 200, 100 ));
        setLocationRelativeTo(owner);
        getComponentsPanel().add(labels, BorderLayout.NORTH);
        
        labeledBranches = rootTreeADDPotential.getLabeledBranches();
        for(String label : labeledBranches.keySet())
        {
            Potential labeledPotential = labeledBranches.get(label).getPotential();
            if(branch.getParentVariables().containsAll(labeledPotential.getVariables()))
            {
                labels.addItem(label);
            }
        }
    }

    @Override
    protected boolean doOkClickBeforeHide() {
        String selectedLabel = labels.getSelectedItem().toString();
        branch.setReferencedBranch(labeledBranches.get(selectedLabel));
        return true;
    }

    
    
}
