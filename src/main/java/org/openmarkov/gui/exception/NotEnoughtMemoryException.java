package org.openmarkov.gui.exception;

import org.openmarkov.core.exception.IBundledOpenMarkovException;

public class NotEnoughtMemoryException extends Exception implements IBundledOpenMarkovException {
    
    public NotEnoughtMemoryException(OutOfMemoryError error) {
        this.error = error;
    }
    
    @Override public String toString() {
        return IBundledOpenMarkovException.toString(this);
    }
    
    public final OutOfMemoryError error;
}
