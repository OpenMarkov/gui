package org.openmarkov.gui.toolplugin;

import com.formdev.flatlaf.FlatDarculaLaf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openmarkov.gui.configuration.LocalPreferences;

import javax.swing.*;
import java.awt.*;

public class DarkModePlugin implements ToolPlugin {
    
    //!com.jthemedetecor.OsThemeDetector.getDetector().isDark();
    /*
        The user's theme can be detected using com.jthemedetecor.OsThemeDetector.getDetector().isDark(). But this
        requires the dependency:
        <dependency>
            <groupId>org.openani.jsystemthemedetector</groupId>
            <artifactId>jSystemThemeDetector</artifactId>
            <version>3.8</version>
        </dependency>
     */
    
    @Override public @NotNull String menuOptionText() {
        return "Toggle dark mode";
    }
    
    @Override public @NotNull ToolPluginGroup pluginGroup() {
        return ToolPluginGroup.USER_EXPERIENCE;
    }
    
    @Override public int priorityInGroup() {
        return 0;
    }
    
    @Override public void showDialog(@Nullable JFrame parent) throws Exception {
        LocalPreferences.PREFERS_DARK_THEME.set(!LocalPreferences.PREFERS_DARK_THEME.get());
        updateInterfaceToLook(parent);
    }
    
    public static void updateInterfaceToLook(@Nullable Container parent) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        if (LocalPreferences.PREFERS_DARK_THEME.get()) {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } else {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        if (parent != null) {
            while (true) {
                if (parent.getParent() == null) break;
                parent = parent.getParent();
            }
            SwingUtilities.updateComponentTreeUI(parent);
        }
    }
}
