package org.openmarkov.gui.dialog.common;

import org.openmarkov.core.action.base.PNUndoableEditEvent;
import org.openmarkov.core.action.base.PNUndoableEditListener;
import org.openmarkov.core.action.core.PotentialChangeEdit;
import org.openmarkov.core.exception.*;
import org.openmarkov.gui.dialog.node.PotentialEditDialog;
import org.openmarkov.gui.exception.BinomialPotentialWrongValueException;
import org.openmarkov.gui.exception.NotEnoughtMemoryException;
import org.openmarkov.gui.util.Utilities;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.potential.DiscretizedCauchyPotential;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.TablePotential;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
@PotentialPanelPlugin(potentialClasses = DiscretizedCauchyPotential.class)
public class DiscretizedCauchyPotentialPanel extends PotentialPanel implements PNUndoableEditListener {
    
    private JButton editMedianButton;
    private JButton editScaleButton;
    private ProbNet probNet;
    private Node medianDummyNode = null;
    private Node scaleDummyNode = null;
    private Potential oldPotential;
    private DiscretizedCauchyPotential newPotential;
    
    public DiscretizedCauchyPotentialPanel(Node node) {
        super();
        initComponents();
        this.probNet = node.getProbNet();
        this.oldPotential = node.getPotentials().get(0);
        this.newPotential = (DiscretizedCauchyPotential) oldPotential.copy();
        setData(node);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        editMedianButton = new JButton("Edit median potential");
        editMedianButton.addActionListener(e -> {
            try {
                editMedianPotential();
            } catch (IncompatibleEvidenceException.EvidenceIsIncompatibleWithOther |
                     ThereIsNoPotentialsInNodeException | NotEnoughtMemoryException ex) {
                throw new UnrecoverableException(ex);
            }
        });
        editScaleButton = new JButton("Edit scale potential");
        editScaleButton.addActionListener(e -> {
            try {
                editScalePotential();
            } catch (IncompatibleEvidenceException.EvidenceIsIncompatibleWithOther |
                     ThereIsNoPotentialsInNodeException | NotEnoughtMemoryException ex) {
                throw new UnrecoverableException(ex);
            }
        });
        buttonPanel.add(editMedianButton);
        buttonPanel.add(editScaleButton);
        add(buttonPanel, BorderLayout.PAGE_START);
    }
    
    private void editMedianPotential() throws IncompatibleEvidenceException.EvidenceIsIncompatibleWithOther, ThereIsNoPotentialsInNodeException, NotEnoughtMemoryException {
        PotentialEditDialog potentialEditDialog = new PotentialEditDialog(Utilities.getOwner(this), medianDummyNode, false, isReadOnly());
        if (potentialEditDialog.requestValues() == OkCancelHorizontalDialog.OK_BUTTON) {
            // TODO: Do nothing?
        } else {
            medianDummyNode.getProbNet().getPNESupport().undoAndDelete();
        }
    }
    
    private void editScalePotential() throws IncompatibleEvidenceException.EvidenceIsIncompatibleWithOther, ThereIsNoPotentialsInNodeException, NotEnoughtMemoryException {
        PotentialEditDialog potentialEditDialog = new PotentialEditDialog(Utilities.getOwner(this), scaleDummyNode, false, isReadOnly());
        if (potentialEditDialog.requestValues() == OkCancelHorizontalDialog.OK_BUTTON) {
            // TODO: Do nothing?
        } else {
            scaleDummyNode.getProbNet().getPNESupport().undoAndDelete();
        }
    }
    
    @Override
    public void setData(Node node) {
        ProbNet medianDummyNet = new ProbNet(probNet.getNetworkType());
        medianDummyNode = medianDummyNet.addPotential(newPotential.getMedian());
        medianDummyNet.getPNESupport().addListener(this);
        
        ProbNet scaleDummyNet = new ProbNet(probNet.getNetworkType());
        scaleDummyNode = scaleDummyNet.addPotential(newPotential.getScale());
        scaleDummyNet.getPNESupport().addListener(this);
    }
    
    @Override
    public void close() {
        medianDummyNode.getProbNet().getPNESupport().removeListener(this);
        scaleDummyNode.getProbNet().getPNESupport().removeListener(this);
    }
    
    @Override
    public boolean saveChanges() throws BinomialPotentialWrongValueException.ThetaValueIsWrong, BinomialPotentialWrongValueException.NValuesIsWrong, DoEditException {
        boolean result = super.saveChanges();
        newPotential.setComment(oldPotential.getComment());
        Node node = probNet.getNode(newPotential.getVariable(0));
        PotentialChangeEdit edit = new PotentialChangeEdit(node, oldPotential, newPotential);
        edit.executeEdit();
        return result;
    }
    
    private void update() throws NonProjectablePotentialException {
        TablePotential projectedPotential = newPotential.tableProject(new EvidenceCase(), null).get(0);
        // TODO update table with projected potential
    }
    
    @Override
    public void afterEditHappens(PNUndoableEditEvent event) {
        // Update new potential and potential panel
        if (event.getEdit() instanceof PotentialChangeEdit) {
            newPotential.setMedian(medianDummyNode.getPotentials().get(0));
            newPotential.setScale(scaleDummyNode.getPotentials().get(0));
            
            //update();
        }
    }
}