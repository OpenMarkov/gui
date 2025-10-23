/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.dialog.common;

import org.openmarkov.core.action.base.PNUndoableEditEvent;
import org.openmarkov.core.action.base.PNUndoableEditListener;
import org.openmarkov.core.action.core.PotentialChangeEdit;
import org.openmarkov.core.exception.*;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.potential.ConditionalGaussianPotential;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.gui.dialog.node.PotentialEditDialog;
import org.openmarkov.gui.exception.BinomialPotentialWrongValueException;
import org.openmarkov.gui.exception.NotEnoughtMemoryException;
import org.openmarkov.gui.util.Utilities;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import java.awt.*;

@SuppressWarnings("serial") @PotentialPanelPlugin(potentialType = "Conditional Gaussian")
public class ConditionalGaussianPotentialPanel
        extends PotentialPanel implements PNUndoableEditListener {
    
    private JButton editMeanButton;
    private JButton editVarianceButton;
    private ProbNet probNet;
    private Node meanDummyNode = null;
    private Node varianceDummyNode = null;
    private Potential oldPotential;
    private ConditionalGaussianPotential newPotential;
    
    public ConditionalGaussianPotentialPanel(Node node) {
        super();
        initComponents();
        this.probNet = node.getProbNet();
        this.oldPotential = node.getPotentials().get(0);
        this.newPotential = (ConditionalGaussianPotential) oldPotential.copy();
        setData(node);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        editMeanButton = new JButton("Edit mean potential");
        editMeanButton.addActionListener(e -> {
            try {
                editMeanPotential();
            } catch (IncompatibleEvidenceException.EvidenceIsIncompatibleWithOther |
                     ThereIsNoPotentialsInNodeException | NotEnoughtMemoryException ex) {
                throw new UnrecoverableException(ex);
            }
        });
        editVarianceButton = new JButton("Edit variance potential");
        editVarianceButton.addActionListener(e -> {
            try {
                editVariancePotential();
            } catch (IncompatibleEvidenceException.EvidenceIsIncompatibleWithOther |
                     ThereIsNoPotentialsInNodeException | NotEnoughtMemoryException ex) {
                throw new UnrecoverableException(ex);
            }
        });
        buttonPanel.add(editMeanButton);
        buttonPanel.add(editVarianceButton);
        add(buttonPanel, BorderLayout.PAGE_START);
    }
    
    private void editMeanPotential() throws IncompatibleEvidenceException.EvidenceIsIncompatibleWithOther, ThereIsNoPotentialsInNodeException, NotEnoughtMemoryException {
        PotentialEditDialog potentialEditDialog = new PotentialEditDialog(Utilities.getOwner(this), meanDummyNode,
                                                                          false, isReadOnly());
        if (potentialEditDialog.requestValues() == OkCancelHorizontalDialog.OK_BUTTON) {
            // TODO: Do nothing?
        } else {
            meanDummyNode.getProbNet().getPNESupport().undoAndDelete();
        }
    }
    
    private void editVariancePotential() throws IncompatibleEvidenceException.EvidenceIsIncompatibleWithOther, ThereIsNoPotentialsInNodeException, NotEnoughtMemoryException {
        PotentialEditDialog potentialEditDialog = new PotentialEditDialog(Utilities.getOwner(this), varianceDummyNode,
                                                                          false, isReadOnly());
        if (potentialEditDialog.requestValues() == OkCancelHorizontalDialog.OK_BUTTON) {
            // TODO: Do nothing?
        } else {
            varianceDummyNode.getProbNet().getPNESupport().undoAndDelete();
        }
    }
    
    @Override public void setData(Node node) {
        ProbNet meanDummyNet = new ProbNet(probNet.getNetworkType());
        meanDummyNode = meanDummyNet.addPotential(newPotential.getMean());
        meanDummyNet.getPNESupport().addListener(this);
        
        ProbNet varianceDummyNet = new ProbNet(probNet.getNetworkType());
        varianceDummyNode = varianceDummyNet.addPotential(newPotential.getVariance());
        varianceDummyNet.getPNESupport().addListener(this);
    }
    
    @Override public void close() {
        meanDummyNode.getProbNet().getPNESupport().removeListener(this);
        varianceDummyNode.getProbNet().getPNESupport().removeListener(this);
    }
    
    @Override
    public boolean saveChanges() throws BinomialPotentialWrongValueException.ThetaValueIsWrong, BinomialPotentialWrongValueException.NValuesIsWrong, DoEditException {
        boolean result = super.saveChanges();
        newPotential.setComment(oldPotential.getComment());
        PotentialChangeEdit edit = new PotentialChangeEdit(probNet, oldPotential, newPotential);
        edit.executeEdit();
        return result;
    }
    
    private void update() throws NonProjectablePotentialException {
        TablePotential projectedPotential = newPotential.tableProject(new EvidenceCase(), null).get(0);
        // TODO update table with projected potential
    }
    
    @Override public void afterEditHappens(PNUndoableEditEvent event) {
        // Update new potential and potential panel
        if (event.getEdit() instanceof PotentialChangeEdit) {
            newPotential.setMean(meanDummyNode.getPotentials().get(0));
            newPotential.setVariance(varianceDummyNode.getPotentials().get(0));
            
            //update();
        }
    }
}
