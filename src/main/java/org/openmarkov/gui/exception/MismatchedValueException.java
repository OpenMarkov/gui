package org.openmarkov.gui.exception;

import org.openmarkov.core.exception.IBundledOpenMarkovException;

public class MismatchedValueException extends Exception implements IBundledOpenMarkovException {
    
    private final String expected;
    private final Object found;
    private final String foundAsString;
    
    public MismatchedValueException(String expected, Object found) {
        this.expected = expected;
        this.found = found;
        this.foundAsString = this.found.toString();
    }
    
    @Override public String toString() {
        return IBundledOpenMarkovException.toString(this);
    }
}
