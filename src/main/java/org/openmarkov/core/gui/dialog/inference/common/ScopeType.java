package org.openmarkov.core.gui.dialog.inference.common;

/**
 * Created by Jorge on 01/07/2015.
 */
public enum ScopeType {
    // Analysis type options
    GLOBAL	        ("Options.SC_GLOBAL"),
    DECISION        ("Options.SC_DECISION");

    private final String display;

    ScopeType(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }
}
