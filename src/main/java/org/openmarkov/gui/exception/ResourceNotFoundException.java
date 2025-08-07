package org.openmarkov.gui.exception;

import org.openmarkov.core.exception.BundledOpenMarkovException;

public class ResourceNotFoundException extends BundledOpenMarkovException {
    public ResourceNotFoundException(String resource) {
        this.resource = resource;
    }
    
    public final String resource;
}
