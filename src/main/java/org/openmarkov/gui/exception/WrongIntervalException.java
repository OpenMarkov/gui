package org.openmarkov.gui.exception;

import org.openmarkov.core.exception.IBundledOpenMarkovException;

public sealed abstract class WrongIntervalException extends Exception implements IBundledOpenMarkovException {
    
    static final public class InfinityInIntervalNotAllowed extends WrongIntervalException {
    }
    
    static final public class LimitsValuesAreWrong extends WrongIntervalException {
    }
    
    @Override public String toString() {
        return IBundledOpenMarkovException.toString(this);
    }
    
}
