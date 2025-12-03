package org.openmarkov.gui.action;

import org.openmarkov.core.action.base.PNEdit;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.gui.graphic.VisualDecisionNode;

public class ImposePolicyEdit extends PNEdit {

    private VisualDecisionNode visualDecisionNode;
    private Potential newPolicy;
    private Potential lastPolicy;
    /**
     * @param visualDecisionNode
     */
    public ImposePolicyEdit(VisualDecisionNode visualDecisionNode,Potential lastPolicy, Potential newPolicy) {
        super(visualDecisionNode.getNode().getProbNet());
        this.visualDecisionNode = visualDecisionNode;
        this.lastPolicy = lastPolicy;
        this.newPolicy = newPolicy;
    }
    public ImposePolicyEdit(VisualDecisionNode visualDecisionNode, Potential newPolicy) {
        super(visualDecisionNode.getNode().getProbNet());
        this.visualDecisionNode = visualDecisionNode;
        this.lastPolicy = visualDecisionNode.getNode().getPotential();
        this.newPolicy = newPolicy;
    }

    @Override
    protected void doEdit() throws DoEditException {
        visualDecisionNode.setPolicy(newPolicy);
    }


    @Override
    public void undo() {
        if(lastPolicy != null) {
            visualDecisionNode.setPolicy(lastPolicy);
        }else{
            visualDecisionNode.removePolicy();
        }
    }

    @Override
    public void redo() {
        visualDecisionNode.setPolicy(newPolicy);
    }
}
