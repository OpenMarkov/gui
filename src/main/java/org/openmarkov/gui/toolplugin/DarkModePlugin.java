package org.openmarkov.gui.toolplugin;

import com.formdev.flatlaf.FlatDarculaLaf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openmarkov.gui.window.MainPanel;

import javax.swing.*;

public class DarkModePlugin implements ToolPlugin {
    
    private static boolean isLightMode = true; //!com.jthemedetecor.OsThemeDetector.getDetector().isDark();
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
        DarkModePlugin.isLightMode = !DarkModePlugin.isLightMode;
        updateInterfaceToLook(parent);
    }
    
    public static void updateInterfaceToLook(@Nullable JFrame parent) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        if (DarkModePlugin.isLightMode) {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } else {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        }
        if (parent != null) {
            SwingUtilities.updateComponentTreeUI(parent);
        }
    }
}
