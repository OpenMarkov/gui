package org.openmarkov.gui.configuration;

import org.openmarkov.gui.dialog.common.WindowDimensions;
import org.openmarkov.gui.dialog.io.FileChooser;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public final class OpenMarkovLocalPreferences {
    
    public static final LocalPreference<Long> LATEST_CONNECTION = LocalPreference
            .of("system/latest_connection", () -> System.currentTimeMillis(), Long.class);
    
    public static final LocalPreference<String> LATEST_USER_CONNECTED = LocalPreference
            .of("system/latest_user", () -> System.getProperty("user.name"), String.class);
    
    public static final LocalPreference<File> LATEST_OPEN_DIRECTORY = LocalPreference
            .of("directories/latest_open_directory", () -> new File("."), File.class);
    
    public static final LocalPreference<File> LATEST_SAVED_DIRECTORY = LocalPreference
            .of("directories/latest_saved_directory", () -> new File("."), File.class);
    
    public static final LocalPreference<File> LATEST_OPEN_DATASET_DIRECTORY = LocalPreference
            .of("directories/latest_open_dataset_directory", () -> new File("."), File.class);
    
    public static final LocalPreference<File> LATEST_SAVED_DATASET_DIRECTORY = LocalPreference
            .of("directories/latest_saved_dataset_directory", () -> new File("."), File.class);
    
    public static final LocalPreference<ArrayList<String>> LAST_OPEN_NETWORKS_FILES = LocalPreference
            .of("directories/last_open_networks_files", () -> new ArrayList<>(),
                obj -> LocalPreferencesUtils.verifyCollection(obj, String.class));
    
    
    public static final LocalPreference<WindowDimensions> LATEST_MAIN_GUI_DIMENSIONS = LocalPreference
            .of("user_interface/latest_main_gui_dimensions",
                () -> new WindowDimensions(new Point(0, 0), new Dimension(600, 400), 0),
                WindowDimensions.class);
    
    public static final LocalPreference<Boolean> PREFERS_DARK_THEME = LocalPreference
            .of("user_interface/prefers_dark_theme", () -> false, Boolean.class);
    
    public static final LocalPreference<String> PREFERENCE_LANGUAGE = LocalPreference
            .of("languages/user_preferred_language", () -> System.getProperty("user.language"), String.class);
    
    public static final LocalPreference<String> LATEST_NETWORK_FORMAT = LocalPreference
            .of("formats/latest_network_format", () -> FileChooser.DEFAULT_FILE_FORMAT, String.class);
    
    public static final LocalPreference<String> LATEST_SAVED_NETWORK_FORMAT = LocalPreference
            .of("formats/latest_saved_network_format", () -> FileChooser.DEFAULT_FILE_FORMAT, String.class);
    
    public static final LocalPreference<String> LATEST_LOADED_EVIDENCE_FORMAT = LocalPreference
            .of("formats/latest_loaded_evidence_format", () -> "xlsx", String.class);
    
    public static final LocalPreference<String> LATEST_SAVED_DATASET_FORMAT = LocalPreference
            .of("formats/latest_saved_dataset_format", () -> FileChooser.DEFAULT_FILE_FORMAT, String.class);
    
    
    public static final LocalPreference<Color> NODECHANCE_BACKGROUND_COLOR = LocalPreference
            .of("colors/node_chance_background", () -> new Color(251, 249, 153), Color.class);
    
    public static final LocalPreference<Color> NODECHANCE_FOREGROUND_COLOR = LocalPreference
            .of("colors/node_chance_foreground", () -> Color.BLACK, Color.class);
    
    public static final LocalPreference<Color> NODECHANCE_TEXT_COLOR = LocalPreference
            .of("colors/node_chance_text", () -> Color.BLACK, Color.class);
    
    
    public static final LocalPreference<Color> NODEDECISION_BACKGROUND_COLOR = LocalPreference
            .of("colors/node_decision_background", () -> new Color(207, 227, 253), Color.class);
    
    public static final LocalPreference<Color> NODEDECISION_FOREGROUND_COLOR = LocalPreference
            .of("colors/node_decision_foreground", () -> Color.BLACK, Color.class);
    
    public static final LocalPreference<Color> NODEDECISION_TEXT_COLOR = LocalPreference
            .of("colors/node_decision_text", () -> Color.BLACK, Color.class);
    
    public static final LocalPreference<Color> NODEUTILITY_BACKGROUND_COLOR = LocalPreference
            .of("colors/node_utility_background", () -> new Color(208, 230, 178), Color.class);
    
    public static final LocalPreference<Color> NODEUTILITY_FOREGROUND_COLOR = LocalPreference
            .of("colors/node_utility_foreground", () -> Color.BLACK, Color.class);
    
    public static final LocalPreference<Color> NODEUTILITY_TEXT_COLOR = LocalPreference
            .of("colors/node_utility_text", () -> Color.BLACK, Color.class);
    
    public static final LocalPreference<Color> TABLE_HEADER_TEXT_COLOR_1 = LocalPreference
            .of("colors/tableheader_first_row", () -> Color.BLACK, Color.class);
    
    public static final LocalPreference<Color> TABLE_HEADER_TEXT_COLOR_2 = LocalPreference
            .of("colors/tableheader_second_row", () -> Color.BLACK, Color.class);
    
    public static final LocalPreference<Color> TABLE_HEADER_TEXT_COLOR_3 = LocalPreference
            .of("colors/tableheader_third_row", () -> Color.BLACK, Color.class);
    
    
    public static final LocalPreference<Color> ALWAYS_OBSERVED_VARIABLE = LocalPreference
            .of("colors/always_observed_variable_border_color", () -> new Color(128, 0, 0), Color.class);
    
    public static final LocalPreference<Color> REVELATION_ARC_VARIABLE = LocalPreference
            .of("colors/revelation_arc_color", () -> new Color(128, 0, 0), Color.class);
    
    private static final List<LocalPreference<?>> ALL_PREFERENCES;
    
    static {
        List<LocalPreference<?>> allPreferences = new ArrayList<>();
        for (var field : OpenMarkovLocalPreferences.class.getDeclaredFields()) {
            if (!LocalPreference.class.isAssignableFrom(field.getType())) continue;
            try {
                allPreferences.add(LocalPreference.class.cast(field.get(null)));
            } catch (IllegalAccessException ignored) {
            }
        }
        ALL_PREFERENCES = Collections.unmodifiableList(allPreferences);
    }
    
    public static List<LocalPreference<?>> getAllPreferences() {
        return ALL_PREFERENCES;
    }
    
}
