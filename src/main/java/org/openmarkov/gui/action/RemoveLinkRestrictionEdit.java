package org.openmarkov.gui.action;

import org.openmarkov.core.action.base.PNEdit;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.gui.graphic.VisualLink;
import org.openmarkov.gui.graphic.VisualNetwork;

import javax.swing.undo.CannotUndoException;
import java.util.List;

public class RemoveLinkRestrictionEdit extends PNEdit {

    private VisualNetwork visualNetwork;
    List<VisualLink> links;
    Potential restriction;
    Potential lastRestriction;

    /**
     * @param probNet {@code ProbNet}
     */
    public RemoveLinkRestrictionEdit(VisualNetwork visualNetwork) {
        super(visualNetwork.getNetwork());
        this.visualNetwork = visualNetwork;
        this.links = visualNetwork.getSelectedLinks();
        this.restriction = links.get(0).getLink().getRestrictionsPotential();
    }

    @Override
    public void doEdit() {
        if (!links.isEmpty()) {
            Link<Node> link = links.get(0).getLink();
            link.setRestrictionsPotential(null);
        }
    }
    
    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        if (!links.isEmpty()) {
            Link<Node> link = links.get(0).getLink();
            lastRestriction = link.getRestrictionsPotential();
            link.setRestrictionsPotential(restriction);
        }
    }

    @Override
    public void redo() {
        super.redo();
        if (!links.isEmpty()) {
            Link<Node> link = links.get(0).getLink();
            link.setRestrictionsPotential(lastRestriction);
        }
    }
}
