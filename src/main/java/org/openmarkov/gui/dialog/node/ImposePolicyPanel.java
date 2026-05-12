package org.openmarkov.gui.dialog.node;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openmarkov.core.action.base.PNEdit;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.UnrecoverableException;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.PolicyType;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.gui.action.ImposePolicyEdit;
import org.openmarkov.gui.graphic.VisualDecisionNode;

import java.util.ArrayList;

/**
 * Dialog for imposing a policy on a decision node. If the node does not already
 * have a policy, a new {@link TablePotential} with role POLICY is created.
 */
public class ImposePolicyPanel extends PotentialEditPanel {
    
    private final @NotNull VisualDecisionNode visualNode;
    
    public ImposePolicyPanel(VisualDecisionNode visualNode, boolean readOnly, boolean potentialInitializesOnEditHistory) {
        this.visualNode = visualNode;
        super(visualNode.getNode(), readOnly, potentialInitializesOnEditHistory);
    }
    
    @Override protected void setPotentialInNode(@NotNull Potential newPotential) {
        this.visualNode.setPolicy(newPotential);
    }
    
    @Override protected void initializePotential() {
        if (!visualNode.isHasPolicy()) {
            Node node = visualNode.getNode();
            node.setPolicyType(PolicyType.OPTIMAL);
            var variables = new ArrayList<>(node.getParents().stream().map(Node::getVariable).toList());
            variables.addFirst(node.getVariable());
            try {
                new ImposePolicyEdit(visualNode, new TablePotential(variables, PotentialRole.POLICY)).executeEdit();
            } catch (DoEditException e) {
                throw new UnrecoverableException(e);
            }
        }
    }
    
    @Override
    protected @NotNull PNEdit generateSetPotentialEdit(@Nullable Potential originalPotential, @Nullable Potential newPotential) {
        return new ImposePolicyEdit(this.visualNode, originalPotential, newPotential);
    }
    
    @Override protected void removePotentialOnClose(@Nullable Potential originalPotential) {
        if (originalPotential != null) {
            this.visualNode.setPolicy(originalPotential);
        } else {
            this.visualNode.removePolicy();
        }
    }
    
}
