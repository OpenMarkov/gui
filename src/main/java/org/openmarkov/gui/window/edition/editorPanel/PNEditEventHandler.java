package org.openmarkov.gui.window.edition.editorPanel;

import org.openmarkov.core.action.base.PNEdit;
import org.openmarkov.core.action.base.PNEditListener;
import org.openmarkov.core.exception.ConstraintViolatedException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.gui.action.PasteEdit;
import org.openmarkov.gui.graphic.VisualNode;
import org.openmarkov.gui.window.edition.SelectedContent;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class PNEditEventHandler implements PNEditListener {
    private final EditorPanel editorPanel;
    
    PNEditEventHandler(EditorPanel editorPanel) {
        this.editorPanel = editorPanel;
    }
    
    @Override
    public void afterUndoingEdit(PNEdit edit) {
        List<Finding> findings = this.editorPanel.getEvidenceManager().getPreResolutionEvidence().getFindings();
        Set<Variable> findingVariables = findings.stream()
                                                 .map(Finding::getVariable)
                                                 .collect(Collectors.toSet());
        List<VisualNode> allVisualNodes = this.editorPanel.getVisualNetwork().getAllNodes();
        for (VisualNode visualNode : allVisualNodes) {
            Variable nodeVariable = visualNode.getNode().getVariable();
            boolean isPreResolution = findingVariables.contains(nodeVariable);
            visualNode.setPreResolutionFinding(isPreResolution);
        }
        this.editorPanel.readjustAndRepaint();
    }
    
    @Override
    public void afterEditExecutes(PNEdit edit) {
        for (Finding finding : this.editorPanel.getEvidenceManager().getPreResolutionEvidence().getFindings()) {
            Variable variable = finding.getVariable();
            for (VisualNode visualNode : this.editorPanel.getVisualNetwork().getAllNodes()) {
                if (variable.getName().equals(visualNode.getNode().getName())) {
                    visualNode.setPreResolutionFinding(true);
                }
            }
        }
        if (edit instanceof PasteEdit pasteEdit) {
            this.editorPanel.getVisualNetwork().setSelectedAllObjects(false);
            SelectedContent pastedContent = pasteEdit.getPastedContent();
            for (Node node : pastedContent.nodes()) {
                this.editorPanel.getVisualNetwork().setSelectedNode(node.getName(), true);
            }
            for (Link<Node> link : pastedContent.links()) {
                this.editorPanel.getVisualNetwork().setSelectedLink(link, true);
            }
        }
        this.editorPanel.readjustAndRepaint();
    }
    
    @Override public void onEditFailed(PNEdit edit, DoEditException exception) {
        this.editorPanel.readjustAndRepaint();
    }
    
    @Override
    public void onEditViolatesConstraints(PNEdit edit, ConstraintViolatedException ex) {
        this.editorPanel.readjustAndRepaint();
    }
    
    @Override public void afterRedoingEdit(PNEdit edit) {
        this.editorPanel.readjustAndRepaint();
    }
}
