package org.openmarkov.gui.exception;

import org.openmarkov.core.exception.BundledOpenMarkovException;

public class MissingPreferenceException extends BundledOpenMarkovException {
    
    public final String key;
    
    public MissingPreferenceException(String key) {
        this.key = key;
    }
    
}
