package org.openmarkov.gui.dialog.node;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openmarkov.core.action.base.PNEdit;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.UnrecoverableException;
import org.openmarkov.core.localize.StringDatabase;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.PolicyType;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.gui.action.ImposePolicyEdit;
import org.openmarkov.gui.graphic.VisualDecisionNode;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Dialog for imposing a policy on a decision node. If the node does not already
 * have a policy, a new {@link TablePotential} with role POLICY is created.
 */
public class ImposePolicyDialog extends PotentialEditDialog{
    
    private final @NotNull VisualDecisionNode visualNode;
    
    public ImposePolicyDialog(Window owner, boolean readOnly, VisualDecisionNode visualNode) {
        this.visualNode = visualNode;
        super(owner, visualNode.getNode(), readOnly);
    }
    
    @Override PotentialEditPanel generatePotentialEditPanel(Node node, boolean readOnly) {
        return new ImposePolicyPanel(this.visualNode, readOnly, true);
    }
}
