package org.openmarkov.gui.configuration;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public enum Theme {
    LIGHT,
    DARK,
    SYSTEM;
    
    public String toUIString() {
        return switch (this) {
            case LIGHT -> "Light";
            case DARK -> "Dark (Beta)";
            case SYSTEM -> "System (Might be unsupported)";
        };
    }
    
    public void setlookAndFeel() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        switch (this) {
            case SYSTEM -> UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            case DARK -> UIManager.setLookAndFeel(new FlatDarculaLaf());
            case LIGHT -> UIManager.setLookAndFeel(new FlatLightLaf());
        }
    }
}
