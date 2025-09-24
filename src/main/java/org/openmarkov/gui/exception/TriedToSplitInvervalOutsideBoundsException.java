package org.openmarkov.gui.exception;

import org.openmarkov.core.exception.IBundledOpenMarkovException;

public class TriedToSplitInvervalOutsideBoundsException extends Exception implements IBundledOpenMarkovException {
    
    public TriedToSplitInvervalOutsideBoundsException() {
    }
    
    @Override public String toString() {
        return IBundledOpenMarkovException.toString(this);
    }
}
