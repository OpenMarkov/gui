package org.openmarkov.gui.exception;

import org.openmarkov.core.exception.BundledOpenMarkovException;

public class CannotSavePreferenceException extends BundledOpenMarkovException {
    public CannotSavePreferenceException(String key, Object value) {
        this.key = key;
        this.value = value;
    }
    
    public final String key;
    public final Object value;
}
