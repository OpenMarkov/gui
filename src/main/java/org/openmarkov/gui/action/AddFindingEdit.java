package org.openmarkov.gui.action;

import org.openmarkov.core.action.base.PNEdit;
import org.openmarkov.core.exception.*;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.gui.graphic.VisualNode;


public class AddFindingEdit extends PNEdit {
    
    VisualNode visualNode;
    EvidenceCase evidenceCase;
    Finding finding;
    Finding previousFinding;
    
    public AddFindingEdit(VisualNode visualNode, EvidenceCase evidenceCase, Finding previousFinding, Finding finding) {
        super(visualNode.getNode().getProbNet());
        this.visualNode = visualNode;
        this.evidenceCase = evidenceCase;
        this.finding = finding;
        this.previousFinding = previousFinding;
    }
    
    @Override
    protected void doEdit() throws DoEditException.CannotDoEditException {
        try {
            evidenceCase.addFinding(finding);
            visualNode.setPreResolutionFinding(true);
        } catch (IncompatibleEvidenceException.EvidenceIsIncompatibleWithOther e) {
            throw new DoEditException.CannotDoEditException(e);
        }
    }
    
    @Override
    public void undo() {
        super.undo();
        if (previousFinding == null) {
            evidenceCase.removeFinding(finding.getVariable());
            visualNode.setPreResolutionFinding(false);
        } else {
            try {
                evidenceCase.removeFinding(finding.getVariable());
                evidenceCase.addFinding(previousFinding);
            } catch (IncompatibleEvidenceException.EvidenceIsIncompatibleWithOther e) {
                throw new UnreachableException(e);
            }
        }
        
    }
    
    
    @Override
    public void redo() {
        super.redo();
    }
    
    
}
