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
            case SYSTEM -> "System";
            case DARK -> "Dark (Beta)";
            case LIGHT -> "Light (Beta)";
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
