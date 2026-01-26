package org.openmarkov.gui.dialog.node;

import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.UnrecoverableException;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.PolicyType;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.gui.action.ImposePolicyEdit;
import org.openmarkov.gui.dialog.common.CommentHTMLScrollPane;
import org.openmarkov.gui.dialog.common.ICIPotentialsTablePanel;
import org.openmarkov.gui.dialog.common.TablePotentialPanel;
import org.openmarkov.gui.exception.BinomialPotentialWrongValueException;
import org.openmarkov.gui.graphic.VisualDecisionNode;
import org.openmarkov.gui.graphic.VisualNode;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ImposePolicyDialog extends PotentialEditDialog{

    private Node node;
    private VisualDecisionNode visualDecisionNode;
    private CommentHTMLScrollPane commentPane;
    private Class<? extends Potential> previouslySelectedPotentialType;
    private int optionPreviouslySelected;
    private Potential lastPolicy;
    /**
     * Constructor. initialises the instance.
     *
     * @param owner window that owns the dialog.
     */
    public ImposePolicyDialog(Window owner, VisualNode visualNode) {
        super(owner,visualNode);
        this.node = visualNode.getNode();
        this.commentPane = getCommentPane();
        this.lastPolicy = node.getPotential();
        this.previouslySelectedPotentialType = getPreviouslySelectedPotentialType();
        this.optionPreviouslySelected = getOptionPreviouslySelected();
        this.visualDecisionNode = (VisualDecisionNode) visualNode;
        initialize();
    }

    @Override
    protected void potentialTypeChanged() {
        Class<? extends Potential> potentialType = (Class<? extends Potential>) getPotentialTypeJCombobox().getSelectedItem();
        if (!potentialType.equals(previouslySelectedPotentialType)) {
            Potential newPotential = instanciatePotential(potentialType);
            visualDecisionNode.setPolicy(newPotential);
            updatePotentialPanel();
            previouslySelectedPotentialType = potentialType;
            optionPreviouslySelected = getPotentialTypeJCombobox().getSelectedIndex();
            getComponentsPanel().add(getPotentialPanel(), BorderLayout.CENTER);
            getComponentsPanel().updateUI();
            getComponentsPanel().repaint();
            this.repaint();
            this.pack();
        }

    }



    @Override
    protected boolean doOkClickBeforeHide() throws BinomialPotentialWrongValueException.ThetaValueIsWrong, BinomialPotentialWrongValueException.NValuesIsWrong, DoEditException {
        if (getPotentialPanel() instanceof TablePotentialPanel) {
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
        Potential newPolicy = visualDecisionNode.getNode().getPotential();

        ImposePolicyEdit imposePolicyEdit = new ImposePolicyEdit(visualDecisionNode,lastPolicy,newPolicy);
        node.getProbNet().getPNESupport().closeParenthesis();
        node.getProbNet().getPNESupport().undo();
        imposePolicyEdit.executeEdit();
        return true;
    }

    @Override
    protected void doCancelClickBeforeHide() {
        if(lastPolicy != null) {
            visualDecisionNode.setPolicy(lastPolicy);
        }else{
            visualDecisionNode.removePolicy();
        }
        getPotentialPanel().close();
        node.getProbNet().getPNESupport().closeParenthesis();
    }

    private void initialize(){
        if(!visualDecisionNode.isHasPolicy()) {
            node.setPolicyType(PolicyType.OPTIMAL);
            List<Variable> variables = new ArrayList<Variable>();
            // it is added first conditioned variable
            variables.add(node.getVariable());
            for (Node parent : node.getParents()) {
                variables.add(parent.getVariable());
            }
            TablePotential policy = new TablePotential(variables, PotentialRole.POLICY);

            ImposePolicyEdit imposePolicyEdit = new ImposePolicyEdit(visualDecisionNode, policy);
            try {
                imposePolicyEdit.executeEdit();
            } catch (DoEditException e) {
                throw new UnrecoverableException(e);
            }
        }
        // Set default title
        setTitle("NodePotentialDialog.Title");
        configureComponentsPanel();
        pack();

    }
}
