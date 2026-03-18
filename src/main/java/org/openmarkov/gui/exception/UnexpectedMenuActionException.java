package org.openmarkov.gui.exception;


//TODO: This should probably be a UnrecheableException instead of being wrapped on it when used.
public class UnexpectedMenuActionException extends RuntimeException {
    public UnexpectedMenuActionException(String actionCommand) {
        this.actionCommand = actionCommand;
    }
    
    public final String actionCommand;
    
}
