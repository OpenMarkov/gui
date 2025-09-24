package org.openmarkov.gui.exception;

import org.openmarkov.core.exception.IBundledOpenMarkovException;
import org.openmarkov.gui.graphic.VisualNode;

public class PreResolutionNodeInInferenceException extends Exception implements IBundledOpenMarkovException {
    
    public PreResolutionNodeInInferenceException(VisualNode node) {
        this.node = node;
    }
    
    @Override public String toString() {
        return IBundledOpenMarkovException.toString(this);
    }
    
    public final VisualNode node;
}
