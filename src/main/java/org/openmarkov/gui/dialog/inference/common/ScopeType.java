package org.openmarkov.gui.dialog.inference.common;

/**
 * Created by Jorge on 01/07/2015.
 */
public enum ScopeType {
    // Analysis type options
    GLOBAL	        ("ScopeSelector.Scenario.Global"),
    DECISION        ("ScopeSelector.Scenario.Decision");

    private final String display;

    ScopeType(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }
}
