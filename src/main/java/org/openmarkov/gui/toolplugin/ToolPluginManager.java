/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.toolplugin;

import org.jetbrains.annotations.NotNull;
import org.openmarkov.gui.window.MainPanel;
import org.openmarkov.plugin.Filter;
import org.openmarkov.plugin.PluginLoader;
import org.openmarkov.plugin.service.FilterIF;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.List;

/**
 * Manager class for tool plugins in OpenMarkov.
 * <p>
 * This class is responsible for locating, loading, and managing all tool plugins
 * defined with the {@link ToolPlugin} annotation. It dynamically generates
 * corresponding menu items and executes the selected plugins at runtime.
 * </p>
 *
 * <p>This class implements the Singleton pattern.</p>
 *
 * @author unknown
 * @version 1.1 jrico. Removed the old use of {@code actionCommand}. Simplified class without changing behavior.
 * Adapted to version 1.1 of {@link ToolPlugin}.
 */
public final class ToolPluginManager {
    
    /**
     * Singleton instance of the plugin manager.
     */
    @NotNull
    private static final ToolPluginManager INSTANCE = new ToolPluginManager();
    
    /**
     * List of loaded tool plugins.
     */
    private final List<ToolPlugin> plugins;
    
    /**
     * Private constructor.
     * Initializes the plugin loader and populates the plugin map
     * with all available tool plugins.
     */
    private ToolPluginManager() {
        this.plugins = ToolPluginManager
                .findAllToolPlugins()
                .stream()
                .map(toolPluginClass -> {
                    try {
                        return toolPluginClass.getDeclaredConstructor().newInstance();
                    } catch (InstantiationException | NoSuchMethodException | IllegalAccessException |
                             InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                })
                .sorted(Comparator.comparing(ToolPlugin::menuOptionText))
                .toList();
    }
    
    /**
     * Returns the singleton instance of the plugin manager.
     *
     * @return The unique instance of {@code ToolPluginManager}.
     */
    public static ToolPluginManager getInstance() {
        return ToolPluginManager.INSTANCE;
    }
    
    
    /**
     * Finds all Plugin Tools classes.
     *
     * @return a list of Plugin Tools classes.
     */
    private static List<Class<ToolPlugin>> findAllToolPlugins() {
        FilterIF filter = Filter.filter().toImplement(ToolPlugin.class);
        return new PluginLoader().loadAllPlugins(filter).stream()
                                 .map(pluginClass -> (Class<ToolPlugin>) pluginClass)
                                 .filter(toolPluginClass -> !toolPluginClass.isInterface())
                                 .toList();
    }
    
    /**
     * Finds all classes in the project annotated with {@link ToolPlugin}.
     *
     * @return A list of classes representing tool plugins.
     */
    public List<JMenuItem> getMenuItems() {
        return this.plugins.stream().map(ToolPluginManager::toolPluginToMenuItem).toList();
    }
    
    /**
     * Creates a JMenuItem to represent this ToolPlugin.
     * <p>
     * The JMenuItem's text will be equal to {@link ToolPlugin#menuOptionText()}.
     * <p>
     * When clicking on the JMenuItem, {@link ToolPlugin#showDialog(JFrame)} will be triggered.
     *
     * @param toolPlugin the plugin to represent via a {@link JMenuItem}.
     * @return a JMenuItem to represent this ToolPlugin
     */
    private static @NotNull JMenuItem toolPluginToMenuItem(ToolPlugin toolPlugin) {
        JMenuItem menuItem = new JMenuItem();
        menuItem.setAction(new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                toolPlugin.showDialog(MainPanel.getUniqueInstance().getMainFrame());
            }
        });
        menuItem.setName(toolPlugin.getClass().getSimpleName());
        menuItem.setText(toolPlugin.menuOptionText());
        var mnemonic = toolPlugin.mnemonic();
        if (mnemonic != null) {
            menuItem.setMnemonic(mnemonic);
        }
        return menuItem;
    }
    
}
