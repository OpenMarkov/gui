package org.openmarkov.gui.exception;


//TODO: This should probably be a UnrecheableException instead of being wrapped on it when used.
public class WrongClassException extends RuntimeException {
    public WrongClassException(Class<?> expectedClass, Class<?> foundClass) {
        this.expectedClass = expectedClass;
        this.foundClass = foundClass;
    }
    
    public final Class<?> expectedClass;
    public final Class<?> foundClass;
    
}
