package org.openmarkov.gui.exception;

import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.model.network.Node;

public class OtherPropertyAlreadyExistsException extends DoEditException {
    
    public final String propertyName;
    
    public OtherPropertyAlreadyExistsException(String propertyName) {
        this.propertyName = propertyName;
    }
}
