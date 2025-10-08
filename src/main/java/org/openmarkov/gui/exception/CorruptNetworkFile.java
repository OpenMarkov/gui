package org.openmarkov.gui.exception;

import org.openmarkov.core.exception.IBundledOpenMarkovException;

import java.net.URL;

public class CorruptNetworkFile extends Exception implements IBundledOpenMarkovException {
    
    public CorruptNetworkFile(URL fileToRead, Exception ex) {
        super(ex);
        this.fileToRead = fileToRead;
    }
    
    private final URL fileToRead;
    
    @Override public String toString() {
        return IBundledOpenMarkovException.toString(this);
    }
}
