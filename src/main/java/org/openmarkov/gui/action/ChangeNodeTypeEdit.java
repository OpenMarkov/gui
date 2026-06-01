package org.openmarkov.gui.action;

import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.openmarkov.core.action.base.PNEdit;
import org.openmarkov.core.exception.ConstraintViolatedException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.NodeType;

public class ChangeNodeTypeEdit extends PNEdit {
    
    private final @NotNull Node node;
    private final @NotNull NodeType newNodeType;
    private final @NotNull NodeType oldNodeType;
    
    public ChangeNodeTypeEdit(Node node, NodeType newNodeType) {
        super(node.getProbNet());
        this.node = node;
        this.newNodeType = newNodeType;
        this.oldNodeType = node.getNodeType();
    }
    
    @Override
    protected void doEdit() throws DoEditException.CannotDoEditException {
        node.setNodeType(newNodeType);
        try {
            probNet.checkConstraints();
        } catch (ConstraintViolatedException e) {
            throw new DoEditException.CannotDoEditException(e, this);
        }
    }
    
    @Override
    public void undo() {
        throw new NotImplementedException("Undo not implemented yet");
    }
    
    
    @Override
    public void redo() {
        throw new NotImplementedException("Redo not implemented yet");
    }
    
    
}
