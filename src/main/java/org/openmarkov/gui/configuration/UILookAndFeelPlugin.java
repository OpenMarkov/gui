package org.openmarkov.gui.configuration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openmarkov.gui.componentBuilder.JMenuItemBuilder;
import org.openmarkov.gui.toolplugin.ToolPlugin;
import org.openmarkov.gui.window.MainGUI;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class UILookAndFeelPlugin implements ToolPlugin {
    
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
    
    @Override public @NotNull ToolPluginGroup pluginGroup() {
        return ToolPluginGroup.USER_EXPERIENCE;
    }
    
    @Override public int priorityInGroup() {
        return 0;
    }
    
    @Override public JMenuItem toMenuItem() {
        return new JMenuItemBuilder("Change UI Look and Feel")
                .withItems(Arrays.stream(Theme.values()).map(UILookAndFeelPlugin::themeToButton))
                .build();
    }
    
    private static JMenuItem themeToButton(Theme theme) {
        boolean selected = LocalPreferences.PREFERRED_THEME.get() == theme;
        return new JMenuItemBuilder(theme.toUIString())
                .asRadio()
                .selected(selected)
                .onClick(() -> {
                    LocalPreferences.PREFERRED_THEME.set(theme);
                    UILookAndFeelPlugin.updateInterfaceToLook(MainGUI.INSTANCE.mainPanel.getMainFrame());
                    MainGUI.INSTANCE.mainPanel.getMainMenu().reInitialize();
                })
                .build();
    }
    
    public static void updateInterfaceToLook(@Nullable Container parent) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        LocalPreferences.PREFERRED_THEME.get().setlookAndFeel();
        if (parent != null) {
            while (true) {
                if (parent.getParent() == null) break;
                parent = parent.getParent();
            }
            SwingUtilities.updateComponentTreeUI(parent);
        }
    }
    
}
