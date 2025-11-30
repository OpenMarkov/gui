package org.openmarkov.gui.action;

import org.openmarkov.core.action.base.PNEdit;
import org.openmarkov.core.exception.*;
import org.openmarkov.core.model.network.*;
import org.openmarkov.gui.graphic.VisualNode;

public class RemoveFindingEdit extends PNEdit {
    
    private EvidenceCase evidenceCase;
    private Variable variable;
    private Finding finding;
    private VisualNode visualNode;
    
    
    /**
     * @param node {@code ProbNet}
     */
    public RemoveFindingEdit(VisualNode visualNode, EvidenceCase evidenceCase, Variable variable) {
        super(visualNode.getNode().getProbNet());
        this.visualNode = visualNode;
        this.evidenceCase = evidenceCase;
        this.variable = variable;
    }
    
    @Override protected void doEdit() {
        finding = evidenceCase.getFinding(variable);
        evidenceCase.removeFinding(variable);
        visualNode.setPreResolutionFinding(false);
    }
    
    @Override public void undo() {
        super.undo();
        try {
            evidenceCase.addFinding(finding);
            visualNode.setPreResolutionFinding(true);
        } catch (IncompatibleEvidenceException.EvidenceIsIncompatibleWithOther e) {
            throw new UnreacheableException(e);
        }
    }
    
    @Override
    public void redo() {
        super.redo();
        
    }
    
}
