package org.openmarkov.gui.exception;

import org.openmarkov.core.exception.IBundledOpenMarkovException;

public class ThereIsNoPreviousEvidenceCaseException extends Exception implements IBundledOpenMarkovException {
    @Override public String toString() {
        return IBundledOpenMarkovException.toString(this);
    }
}
