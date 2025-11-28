package org.openmarkov.gui.dialog.node;

import org.openmarkov.core.action.core.SetPotentialEdit;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.ThereIsNoPotentialsInNodeException;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.PolicyType;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.plugin.PotentialManager;
import org.openmarkov.core.model.network.potential.plugin.PotentialType;
import org.openmarkov.gui.dialog.common.*;
import org.openmarkov.gui.exception.BinomialPotentialWrongValueException;
import org.openmarkov.gui.exception.NotEnoughtMemoryException;
import org.openmarkov.gui.graphic.VisualDecisionNode;
import org.openmarkov.gui.graphic.VisualNode;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

public class ImposePolicyDialog extends PotentialEditDialog{

    /**
     * Constructor. initialises the instance.
     *
     * @param owner window that owns the dialog.
     */
    public ImposePolicyDialog(Window owner, VisualNode visualNode) {
        super(owner,visualNode);
    }

    @Override
    protected JComboBox<String> getPotentialTypeJCombobox() {
        JComboBox<String> potentialTypeComboBox = getPotentialTypeComboBox();
        PotentialManager potentialManager = getPotentialManager();
        Node node = getNode();
        if (potentialTypeComboBox == null) {
            List<String> filteredPotentialNames = potentialManager.getFilteredPotentials(node);
            Collections.sort(filteredPotentialNames);
            potentialTypeComboBox = new JComboBox<>(filteredPotentialNames.toArray(new String[0]));
            String currentPotentialType = node.getPotentials()
                    .get(0)
                    .getClass()
                    .getAnnotation(PotentialType.class)
                    .name();
            // Compute the number of columns of the conditional probability table
            int tableColumns = 1;
            for (Node parent : node.getParents()) {
                tableColumns *= parent.getVariable().getNumStates();
            }
            System.out.println(tableColumns);
            // Show small uniform potentials as table potentials. Saves clicks
            if (currentPotentialType.equals("Uniform") && tableColumns <= 128) {

                Potential newPotential = stringToPotential("Table");
                node.setPotentialConsistently(newPotential);

            }
            // Show small uniform potentials as 'Exact' potentials. Saves clicks
            if (node.getNodeType() == NodeType.UTILITY && currentPotentialType.equals("Uniform") && tableColumns <= 128) {

                Potential newPotential = stringToPotential("Exact");
                node.setPotentialConsistently(newPotential);

            }


            potentialTypeComboBox.setSelectedItem(currentPotentialType);
            potentialTypeComboBox.setBorder(new LineBorder(UIManager.getColor("List.dropLineColor"), 1, false));
            potentialTypeComboBox.setName("jComboBoxRelationType");
            potentialTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
                @Override public void actionPerformed(java.awt.event.ActionEvent evt) {

                    potentialTypeChanged();
                }
            });

            potentialTypeComboBox.setEnabled(!isReadOnly());
        }
        return potentialTypeComboBox;
    }

    @Override
    protected void potentialTypeChanged() {
        /*String potentialType = (String) potentialTypeComboBox.getSelectedItem();
        lastPotential = node.getPotentials().get(0);
        if (node.getNodeType() == NodeType.DECISION)
            hasPolicy = ((VisualDecisionNode) visualNode).isHasPolicy();
        if (!previouslySelectedPotentialType.equals(potentialType)) {
            SetPotentialEdit setPotentialEdit = new SetPotentialEdit(node, potentialType, lastPotential, hasPolicy, (VisualDecisionNode) visualNode);
            setPotentialEdit.setPotential();

            updatePotentialPanel();
            previouslySelectedPotentialType = potentialType;
            optionPreviouslySelected = potentialTypeComboBox.getSelectedIndex();
            getComponentsPanel().add(getPotentialPanel(), BorderLayout.CENTER);
            getComponentsPanel().updateUI();
            getComponentsPanel().repaint();
            this.repaint();
            this.pack();

        }

         */
    }



    @Override
    protected boolean doOkClickBeforeHide() throws BinomialPotentialWrongValueException.ThetaValueIsWrong, BinomialPotentialWrongValueException.NValuesIsWrong, DoEditException {
        /*if (getPotentialPanel() instanceof TablePotentialPanel) {
            ((TablePotentialPanel) getPotentialPanel()).getValuesTable().stopCellEditing();
        }
        if (getPotentialPanel() instanceof ICIPotentialsTablePanel) {
            ((ICIPotentialsTablePanel) getPotentialPanel()).getICIValuesTable().stopCellEditing();
        }
        getPotentialPanel().saveChanges();
        if (commentPane.isChanged()) {
            // check if the comment is empty
            String comment = commentPane.isEmpty() ? "" : commentPane.getCommentText();
            node.getPotentials().get(0).setComment(comment);
        }
        SetPotentialEdit setPotentialEdit;
        if (node.getNodeType() == NodeType.DECISION) {
            setPotentialEdit = new SetPotentialEdit(node, lastPotential, node.getPotential(), hasPolicy, (VisualDecisionNode) visualNode);
        } else {
            setPotentialEdit = new SetPotentialEdit(node, lastPotential, node.getPotential());
        }
        setPotentialEdit.executeEdit();
        node.finalizePotentialEdition();
        node.getProbNet().getPNESupport().closeParenthesis();

         */
        return true;
    }



}
