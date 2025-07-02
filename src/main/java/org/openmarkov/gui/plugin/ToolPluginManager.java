/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.plugin;

import org.openmarkov.gui.localize.LocalizedMenuItem;
import org.openmarkov.core.localize.StringDatabase;
import org.openmarkov.plugin.PluginLoader;
import org.openmarkov.plugin.service.FilterIF;
import org.openmarkov.plugin.service.PluginLoaderIF;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager class for tool plugins in OpenMarkov.
 * <p>
 * This class is responsible for locating, loading, and managing all tool plugins
 * defined with the {@link ToolPlugin} annotation. It dynamically generates
 * corresponding menu items and executes the selected plugins at runtime.
 * </p>
 * 
 * <p>This class implements the Singleton pattern.</p>
 */
public class ToolPluginManager {
	
    /** Singleton instance of the plugin manager. */
	private static ToolPluginManager instance = null;
	
    /** Plugin loader interface used to load plugins. */
	private PluginLoaderIF pluginsLoader;
	
    /** Map of loaded plugins, indexed by their command identifiers. */
	private Map<String, Class<?>> plugins;

    /**
     * Private constructor.
     * Initializes the plugin loader and populates the plugin map
     * with all available tool plugins.
     */
	private ToolPluginManager() {
		super();
		this.pluginsLoader = new PluginLoader();
		this.plugins = new HashMap<String, Class<?>>();
		for (Class<?> plugin : findAllToolPlugins()) {
			this.plugins.put(plugin.getAnnotation(ToolPlugin.class).command(), plugin);
		}
	}

    /**
     * Returns the singleton instance of the plugin manager.
     * 
     * @return The unique instance of {@code ToolPluginManager}.
     */
	public static ToolPluginManager getInstance() {
		if (instance == null) {
			instance = new ToolPluginManager();
		}
		return instance;
	}

	/**
	 * Finds all learning tools menu items.
	 *
	 * @return a list of tools menu items.
	 */
	private final List<Class<?>> findAllToolPlugins() {
		try {
			FilterIF filter = org.openmarkov.plugin.Filter.filter().toBeAnnotatedBy(ToolPlugin.class);
			return pluginsLoader.loadAllPlugins(filter);
		} catch (Exception e) {
		}
		return null;
	}

    /**
     * Finds all classes in the project annotated with {@link ToolPlugin}.
     * 
     * @return A list of classes representing tool plugins,
     *         or {@code null} if an error occurs.
     */
	public List<JMenuItem> getMenuItems() {
		List<JMenuItem> menuItems = new ArrayList<>();
		try {
			for (Class<?> plugin : plugins.values()) {
				ToolPlugin lAnnotation = plugin.getAnnotation(ToolPlugin.class);
				JMenuItem menuItem = new LocalizedMenuItem(lAnnotation.name(), lAnnotation.command());
				menuItems.add(menuItem);
			}
		} catch (Exception e) {
		}

		return menuItems;
	}

    /**
     * Generates a list of menu items for all available tool plugins.
     * 
     * @return A list of {@link JMenuItem} objects representing the plugins.
     */
	public void processCommand(String command, JFrame parent) {
	    Class<?> plugin = plugins.get(command);
	    if (plugin == null) {
	        showError("Tools.Plugin.NotAvailable", command);
	        return;
	    }

	    try {
	        try {
	            plugin.getConstructor().newInstance();
	        } catch (NoSuchMethodException e) {
	            plugin.getConstructor(JFrame.class).newInstance(parent);
	        }
	    } catch (ReflectiveOperationException e) {
	        showError("Tools.Plugin.Error", command);
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Displays an error message dialog with the specified message key and command.
	 * 
	 * @param messageKey The key for the localized error message.
	 * @param command    The command that caused the error.
	 */
	private void showError(String messageKey, String command) {
	    JOptionPane.showMessageDialog(null,
	            StringDatabase.getUniqueInstance().getString(messageKey) + " " + command,
	            StringDatabase.getUniqueInstance().getString("ErrorWindow.Title.Label"),
	            JOptionPane.ERROR_MESSAGE);
	}
}
