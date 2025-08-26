package org.openmarkov.gui.exception;

import org.openmarkov.core.exception.BundledOpenMarkovException;
import org.openmarkov.gui.graphic.VisualNetwork;

public class NoSelectedNodeException extends BundledOpenMarkovException {
    public NoSelectedNodeException(VisualNetwork visualNetwork) {
        this.visualNetwork = visualNetwork;
    }
    
    public final VisualNetwork visualNetwork;
}
