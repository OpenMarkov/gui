/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.configuration;

import org.openmarkov.core.localize.StringDatabase;
import org.openmarkov.gui.dialog.ExceptionDialog;
import org.openmarkov.gui.exception.CannotSavePreferenceException;
import org.openmarkov.gui.exception.MissingPreferenceException;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Convenience class to encapsulate the Preferences for OpenMarkov project
 *
 * @author jlgozalo
 * @version 1.1 30 Oct 2009 - adding kernel and languages subsets. - adding
 * getInt/setInt methods - adding interface implementation (constants)
 */
public class OpenMarkovPreferences {
    /**
     * the package nodes in the Preferences
     */
    public static final String OPENMARKOV_NODE_PREFERENCES = "OPENMARKOV";
    
    /**
     * the preferences
     */
    /*
     * public static Preferences OPENMARKOV_KERNEL_PREFERENCES =
     * Preferences.systemRoot().node( OPENMARKOV_SYSTEM_PREFERENCES );
     */
    public static final Preferences OPENMARKOV_PREFERENCES = Preferences.userRoot().node(OPENMARKOV_NODE_PREFERENCES);
    public static final Preferences OPENMARKOV_DIRECTORIES = OPENMARKOV_PREFERENCES.node("directories");
    public static final Preferences OPENMARKOV_POSITIONS = OPENMARKOV_PREFERENCES.node("positions");
    public static final Preferences OPENMARKOV_COLORS = OPENMARKOV_PREFERENCES.node("colors");
    public static final Preferences OPENMARKOV_LANGUAGES = OPENMARKOV_PREFERENCES.node("languages");
    public static final Preferences OPENMARKOV_FORMATS = OPENMARKOV_PREFERENCES.node("formats");
    
    /**
     * constructor.
     */
    private OpenMarkovPreferences() {
    
    }
    
    static {
        OpenMarkovPreferences.ensurePreferenceAreInitialized();
    }
    
    public static void ensurePreferenceAreInitialized() {
        final boolean initialised = OpenMarkovPreferences
                .getBoolean(OpenMarkovPreferencesKeys.INITIALIZED, OpenMarkovPreferences.OPENMARKOV_PREFERENCES, false);
        if (!initialised) {
            OpenMarkovPreferences.setDefaultPreferences();
        }
    }
    
    /**
     * get a string {@code Preference} with a specific key
     *
     * @param key          - the key to get the preference
     * @param defaultValue - a default value to set if no key is found
     *
     * @return the string value of the preference
     */
    public static String get(String key, Preferences preferences, String defaultValue) {
        try {
            return preferences.get(key, defaultValue);
        } catch (NullPointerException | IllegalStateException | IllegalArgumentException ex) {
            return defaultValue;
        }
    }
    
    /**
     * get a boolean {@code Preference} with a specific key
     *
     * @param key            - the key to get the preference
     * @param preferences    - the preferences node to look for
     * @param defaultBoolean - a default value to set if no key is found
     *
     * @return the boolean value of the preference
     */
    public static boolean getBoolean(String key, Preferences preferences, boolean defaultBoolean) {
        try {
            return preferences.getBoolean(key, defaultBoolean);
        } catch (NullPointerException | IllegalStateException | IllegalArgumentException ex) {
            return defaultBoolean;
        }
    }
    
    /**
     * get an integer {@code Preference} with a specific key
     *
     * @param key            - the key to get the preference
     * @param preferences    - the preferences node to look for
     * @param defaultInteger - a default value to set if no key is found
     *
     * @return the integer value of the preference
     */
    public static int getInteger(String key, Preferences preferences, int defaultInteger) {
        try {
            return preferences.getInt(key, defaultInteger);
        } catch (NullPointerException | IllegalStateException | IllegalArgumentException ex) {
            return defaultInteger;
        }
    }
    
    /**
     * get a Color {@code Preference} with a specific key
     *
     * @param key          - the key to get the preference
     * @param preferences  - the preferences node to look for
     * @param defaultColor - a default value to set if no key is found
     *
     * @return the Color value of the preference
     */
    public static Color getColor(String key, Preferences preferences, Color defaultColor) {
        try {
            Preferences child = preferences.node(key);
            int redParam = child.getInt("RED", defaultColor.getRed());
            int greenParam = child.getInt("GREEN", defaultColor.getGreen());
            int blueParam = child.getInt("BLUE", defaultColor.getBlue());
            return new Color(redParam, greenParam, blueParam);
        } catch (NullPointerException | IllegalStateException | IllegalArgumentException ex) {
            return defaultColor;
        }
    }
    
    /**
     * set a string {@code Preference} with a specific key
     *
     * @param key         - the key to access the preference
     * @param value       - the string value to set the preference
     * @param preferences - the preference node to look for
     */
    public static void set(String key, String value, Preferences preferences) {
        try {
            preferences.put(key, value);
            preferences.sync();
        } catch (BackingStoreException ignored) {
        }
    }
    
    /**
     * set a boolean {@code Preference} with a specific key
     *
     * @param key         - the key to access the preference
     * @param value       - the boolean value to set the preference
     * @param preferences - the preference node to look for
     */
    public static void setBoolean(String key, boolean value, Preferences preferences) {
        try {
            preferences.putBoolean(key, value);
            preferences.sync();
        } catch (BackingStoreException ignored) {
        }
    }
    
    /**
     * set a integer {@code Preference} with a specific key
     *
     * @param key         - the key to access the preference
     * @param value       - the int value to set the preference
     * @param preferences - the preference node to look for
     */
    public static void setInteger(String key, int value, Preferences preferences) {
        try {
            preferences.putInt(key, value);
            preferences.sync();
        } catch (BackingStoreException ignored) {
        }
    }
    
    /**
     * set a Color {@code Preference} with a specific key
     *
     * @param key         - the key to access the preference
     * @param value       - the int value to set the preference
     * @param preferences - the preference node to look for
     */
    public static void setColor(String key, Color value, Preferences preferences) {
        try {
            Preferences child = preferences.node(key);
            child.putInt("RED", value.getRed());
            child.putInt("GREEN", value.getGreen());
            child.putInt("BLUE", value.getBlue());
            child.sync();
            preferences.sync();
        } catch (BackingStoreException ignored) {
        }
    }
    
    /**
     * set the default preferences for OpenMarkov
     */
    public static void setDefaultPreferences() {
        System.out.println("Initializing OpenMarkov Default Preferences");
        // OPENMARKOV KERNEL PREFERENCES
        // OPENMARKOV DIRECTORIES PREFERENCES
        setDefaultDirectories();
        // OPENMARKOV POSITIONS (for dialogs and windows) PREFERENCES
        setDefaultDimensions();
        // OPENMARKOV COLORS PREFERENCES
        setDefaultColors();
        // OPENMARKOV OTHER PREFERENCES
        set(OpenMarkovPreferencesKeys.PREFERENCE_LANGUAGE, System.getProperty("user.language"), OPENMARKOV_LANGUAGES);
        // set the default initial flag
        setBoolean(OpenMarkovPreferencesKeys.INITIALIZED, true, OPENMARKOV_PREFERENCES);
    }
    
    /**
     * set default directories
     */
    public static void setDefaultDirectories() {
        set(OpenMarkovPreferencesKeys.LATEST_OPEN_DIRECTORY, "", OPENMARKOV_DIRECTORIES);
        set(OpenMarkovPreferencesKeys.LATEST_OPEN_FILE_1, "", OPENMARKOV_DIRECTORIES);
        set(OpenMarkovPreferencesKeys.LATEST_OPEN_FILE_2, "", OPENMARKOV_DIRECTORIES);
        set(OpenMarkovPreferencesKeys.LATEST_OPEN_FILE_3, "", OPENMARKOV_DIRECTORIES);
        set(OpenMarkovPreferencesKeys.LATEST_OPEN_FILE_4, "", OPENMARKOV_DIRECTORIES);
        set(OpenMarkovPreferencesKeys.LATEST_OPEN_FILE_5, "", OPENMARKOV_DIRECTORIES);
        set(OpenMarkovPreferencesKeys.LATEST_OPEN_FILE_6, "", OPENMARKOV_DIRECTORIES);
        set(OpenMarkovPreferencesKeys.LATEST_OPEN_FILE_7, "", OPENMARKOV_DIRECTORIES);
        set(OpenMarkovPreferencesKeys.LATEST_OPEN_FILE_8, "", OPENMARKOV_DIRECTORIES);
        set(OpenMarkovPreferencesKeys.LATEST_OPEN_FILE_9, "", OPENMARKOV_DIRECTORIES);
        // set( STRING_RESOURCES_PATH, "openmarkov/gui/localize/",
        // OPENMARKOV_LANGUAGES );
        set(OpenMarkovPreferencesKeys.STRING_LANGUAGES_PATH, "localize", OPENMARKOV_LANGUAGES);
    }
    
    /**
     * set default dimensions
     */
    public static void setDefaultDimensions() {
        setInteger(OpenMarkovPreferencesKeys.X_OPENMARKOV_MAIN_FRAME, 0, OPENMARKOV_POSITIONS);
        setInteger(OpenMarkovPreferencesKeys.Y_OPENMARKOV_MAIN_FRAME, 0, OPENMARKOV_POSITIONS);
        setInteger(OpenMarkovPreferencesKeys.X_OPEMARKOV_HELP_DIMENSION, 640, OPENMARKOV_POSITIONS);
        setInteger(OpenMarkovPreferencesKeys.Y_OPENMARKOV_HELP_DIMENSION, 480, OPENMARKOV_POSITIONS);
    }
    
    /**
     * set default color preferences
     */
    public static void setDefaultColors() {
        // OPENMARKOV COLORS PREFERENCEs
        setColor(OpenMarkovPreferencesKeys.NODECHANCE_BACKGROUND_COLOR, // def
                 new Color(251, 249, 153), // cream color before it was --&gt;
                 // //new Color( 235, 245, 35 ),
                 // //one type of yellow
                 OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor(OpenMarkovPreferencesKeys.NODECHANCE_FOREGROUND_COLOR, // def
                 Color.BLACK, // black color
                 OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor(OpenMarkovPreferencesKeys.NODECHANCE_TEXT_COLOR, // def
                 Color.BLACK, // black color
                 OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor(OpenMarkovPreferencesKeys.NODEDECISION_BACKGROUND_COLOR, // def
                 new Color(207, 227, 253), // light blue color before it was
                 // --&gt; //new Color( 25, 255, 255 ),
                 // // gray color
                 OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor(OpenMarkovPreferencesKeys.NODEDECISION_FOREGROUND_COLOR, // def
                 Color.BLACK, // black color
                 OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor(OpenMarkovPreferencesKeys.NODEDECISION_TEXT_COLOR, // def
                 Color.BLACK, // black color
                 OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor(OpenMarkovPreferencesKeys.NODEUTILITY_BACKGROUND_COLOR, // def
                 new Color(208, 230, 178), // light green color before it was
                 // --&gt; //new Color( 0, 125, 0 ),
                 // //green color
                 OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor(OpenMarkovPreferencesKeys.NODEUTILITY_FOREGROUND_COLOR, // def
                 Color.BLACK, // black color
                 OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor(OpenMarkovPreferencesKeys.NODEUTILITY_TEXT_COLOR, // def
                 new Color(0, 0, 0), // black color before it was --&gt; //new
                 // Color( 230, 230, 230 ), //one dark
                 // green color
                 OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor(OpenMarkovPreferencesKeys.TABLE_HEADER_TEXT_COLOR_1, // def
                 new Color(0, 0, 0), // black color
                 OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor(OpenMarkovPreferencesKeys.TABLE_HEADER_TEXT_COLOR_2, // def
                 new Color(0, 0, 0), // black color
                 OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor(OpenMarkovPreferencesKeys.TABLE_HEADER_TEXT_COLOR_3, // def
                 new Color(0, 0, 0), // black color
                 OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor(OpenMarkovPreferencesKeys.TABLE_HEADER_TEXT_BACKGROUND_COLOR_1, // def
                 new Color(150, 150, 150), // another light light gray color
                 OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor(OpenMarkovPreferencesKeys.TABLE_HEADER_TEXT_BACKGROUND_COLOR_2, // def
                 new Color(170, 170, 170), // another light gray color
                 OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor(OpenMarkovPreferencesKeys.TABLE_FIRST_COLUMN_FOREGROUND_COLOR, // def
                 new Color(0, 0, 0), // black color
                 OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor(OpenMarkovPreferencesKeys.TABLE_FIRST_COLUMN_BACKGROUND_COLOR, // def
                 new Color(192, 192, 192), // light gray color
                 OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor(OpenMarkovPreferencesKeys.TABLE_CELLS_FOREGROUND_COLOR, // def
                 new Color(0, 0, 0), // black color
                 OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor(OpenMarkovPreferencesKeys.TABLE_CELLS_BACKGROUND_COLOR, // def
                 new Color(255, 255, 255), // white color
                 OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor(OpenMarkovPreferencesKeys.REVELATION_ARC_VARIABLE, // def
                 new Color(128, 0, 0), // dark red
                 OpenMarkovPreferences.OPENMARKOV_COLORS);
        setColor(OpenMarkovPreferencesKeys.ALWAYS_OBSERVED_VARIABLE, // def
                 new Color(128, 0, 0), // dark red
                 OpenMarkovPreferences.OPENMARKOV_COLORS);
    }
}
