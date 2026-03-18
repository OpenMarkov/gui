package org.openmarkov.gui.exception;

import org.openmarkov.core.exception.IBundledOpenMarkovException;

public class NotEnoughMemoryException extends Exception implements IBundledOpenMarkovException {
    
    public NotEnoughMemoryException(OutOfMemoryError error) {
        this.error = error;
    }
    
    @Override public String toString() {
        return IBundledOpenMarkovException.toString(this);
    }
    
    public final OutOfMemoryError error;
}
