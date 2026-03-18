package org.openmarkov.gui.exception;

import org.openmarkov.core.exception.IBundledOpenMarkovException;

public class TriedToSplitIntervalOutsideBoundsException extends Exception implements IBundledOpenMarkovException {
    
    public TriedToSplitIntervalOutsideBoundsException() {
    }
    
    @Override public String toString() {
        return IBundledOpenMarkovException.toString(this);
    }
}
