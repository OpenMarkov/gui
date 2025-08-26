package org.openmarkov.gui.exception;

import org.openmarkov.core.exception.BundledOpenMarkovException;

public class UnexpectedMenuActionException extends BundledOpenMarkovException {
    public UnexpectedMenuActionException(String actionCommand) {
        this.actionCommand = actionCommand;
    }
    
    public final String actionCommand;
}
