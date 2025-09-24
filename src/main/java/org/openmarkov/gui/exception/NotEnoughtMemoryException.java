package org.openmarkov.gui.exception;

import org.openmarkov.core.exception.IBundledOpenMarkovException;

public class NotEnoughtMemoryException extends Exception implements IBundledOpenMarkovException {
    
    public NotEnoughtMemoryException() {
    }
    
    @Override public String toString() {
        return IBundledOpenMarkovException.toString(this);
    }
}
