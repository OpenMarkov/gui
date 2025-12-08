package org.openmarkov.gui.configuration;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.prefs.Preferences;

enum LocalPreferenceResolveStrategy {
    BACKING_STORE,
    FILE_SYSTEM,
    RESORT_TO_DEFAULT;
    
    public static @Nullable String get(List<String> path) {
        for (var strategy : LocalPreferenceResolveStrategy.values()) {
            var valueString = strategy.getString(path);
            if (valueString != null) {
                return valueString;
            }
        }
        return null;
    }
    
    public static boolean isSet(List<String> path) {
        for (var strategy : LocalPreferenceResolveStrategy.values()) {
            var isSet = strategy.valueIsSet(path);
            if (isSet) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean put(List<String> path, String value) {
        for (var strategy : LocalPreferenceResolveStrategy.values()) {
            if (strategy.putString(path, value)) {
                return true;
            }
        }
        return false;
    }
    
    public static void clear(List<String> path) {
        for (var strategy : LocalPreferenceResolveStrategy.values()) {
            strategy.clearString(path);
        }
    }
    
    private @Nullable String getString(List<String> path) {
        try {
            return switch (this) {
                case BACKING_STORE -> {
                    var node = Preferences.userRoot().node("OPENMARKOV");
                    for (int i = 0; i < path.size() - 1; i++) {
                        node = node.node(path.get(i));
                    }
                    yield node.get(path.getLast(), null);
                }
                case FILE_SYSTEM -> {
                    var preferenceFile = new File("openmarkov_preferences");
                    for (String pathElement : path) {
                        preferenceFile = new File(preferenceFile, pathElement);
                    }
                    yield Files.readString(preferenceFile.toPath());
                }
                case RESORT_TO_DEFAULT -> null;
            };
        } catch (RuntimeException | IOException e) {
            return null;
        }
    }
    
    private @Nullable boolean valueIsSet(List<String> path) {
        try {
            return switch (this) {
                case BACKING_STORE -> {
                    var node = Preferences.userRoot().node("OPENMARKOV");
                    for (int i = 0; i < path.size() - 1; i++) {
                        node = node.node(path.get(i));
                    }
                    yield node.get(path.getLast(), null) != null;
                }
                case FILE_SYSTEM -> {
                    var preferenceFile = new File("openmarkov_preferences");
                    for (String pathElement : path) {
                        preferenceFile = new File(preferenceFile, pathElement);
                    }
                    try {
                        Files.readString(preferenceFile.toPath());
                        yield true;
                    } catch (IOException e) {
                        yield false;
                    }
                }
                case RESORT_TO_DEFAULT -> false;
            };
        } catch (RuntimeException e) {
            return false;
        }
    }
    
    private boolean putString(List<String> path, String value) {
        try {
            switch (this) {
                case BACKING_STORE -> {
                    var node = Preferences.userRoot().node("OPENMARKOV");
                    for (int i = 0; i < path.size() - 1; i++) {
                        node = node.node(path.get(i));
                    }
                    node.put(path.getLast(), value);
                }
                case FILE_SYSTEM -> {
                    var preferenceFile = new File("openmarkov_preferences");
                    for (String pathElement : path) {
                        preferenceFile = new File(preferenceFile, pathElement);
                    }
                    Files.writeString(preferenceFile.toPath(), value);
                }
                case RESORT_TO_DEFAULT -> {
                }
            }
            return true;
        } catch (RuntimeException | IOException e) {
            return false;
        }
    }
    
    private boolean clearString(List<String> path) {
        try {
            switch (this) {
                case BACKING_STORE -> {
                    var node = Preferences.userRoot().node("org").node("openmarkov");
                    for (int i = 0; i < path.size() - 1; i++) {
                        node = node.node(path.get(i));
                    }
                    node.remove(path.getLast());
                }
                case FILE_SYSTEM -> {
                    var preferenceFile = new File("openmarkov_preferences");
                    for (String pathElement : path) {
                        preferenceFile = new File(preferenceFile, pathElement);
                    }
                    Files.delete(preferenceFile.toPath());
                }
                case RESORT_TO_DEFAULT -> {
                }
            }
            return true;
        } catch (RuntimeException | IOException e) {
            return false;
        }
    }
    
    
}
