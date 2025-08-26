package org.openmarkov.gui.exception;

import org.openmarkov.core.exception.BundledOpenMarkovException;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDBranch;

public class WrongClassException extends BundledOpenMarkovException {
    public WrongClassException(Class<?> expectedClass, Class<?> foundClass) {
        this.expectedClass = expectedClass;
        this.foundClass = foundClass;
    }
    
    public final Class<?> expectedClass;
    public final Class<?> foundClass;
}
