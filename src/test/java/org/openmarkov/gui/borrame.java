package org.openmarkov.gui;

import org.openmarkov.gui.configuration.LocalPreference;

import java.awt.*;
import java.util.prefs.Preferences;

public class borrame {
    
    private static final Preferences OPENMARKOV_PREFERENCES = Preferences.userRoot().node("org").node("openmarkov");
    private static final LocalPreference<Color> FAVOURITE_COLOR = new LocalPreference<>
            (OPENMARKOV_PREFERENCES, "favourite_color", Color.class, () -> Color.RED);
    
    void use() {
        System.out.println("Your favourite color is " + FAVOURITE_COLOR.get());
        FAVOURITE_COLOR.set(Color.BLACK);
        System.out.println("Your favourite color now is " + FAVOURITE_COLOR.get());
        FAVOURITE_COLOR.clear();
        System.out.println("Your favourite color is the old one " + FAVOURITE_COLOR.get());
    }
    
}
