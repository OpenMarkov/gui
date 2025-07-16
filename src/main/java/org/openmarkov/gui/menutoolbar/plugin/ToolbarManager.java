/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.menutoolbar.plugin;

import org.jetbrains.annotations.NotNull;
import org.openmarkov.core.exception.OpenMarkovException;
import org.openmarkov.core.localize.LocalizedException;
import org.openmarkov.gui.menutoolbar.toolbar.ToolBarBasic;
import org.openmarkov.gui.window.MainPanel;
import org.openmarkov.plugin.PluginSearch;

import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Stream;

public class ToolbarManager {
    private MainPanel mainPanel;
    private Map<String, Class<ToolBarBasic>> toolbarClasses;
    private List<String> activeToolbars = new ArrayList<>();
    
    public ToolbarManager(MainPanel mainPanel) {
        toolbarClasses = new HashMap<>();
        this.mainPanel = mainPanel;
        findAllToolbars().forEach(toolbarClass->{
            Toolbar toolbar = toolbarClass.getAnnotation(Toolbar.class);
            this.toolbarClasses.put(toolbar.name(), toolbarClass);
        });
    }
    
    public Set<String> getToolbarNames() {
        return toolbarClasses.keySet();
    }
    
    public void addToolbar(String name) {
        ToolBarBasic instance = null;
        
        if (!activeToolbars.contains(name)) {
            if (toolbarClasses.containsKey(name)) {
                LocalizedException localizedException = null;
                try {
                    Constructor<?> constructor = toolbarClasses.get(name).getConstructor(ActionListener.class);
                    instance = (ToolBarBasic) constructor.newInstance(mainPanel.getMainPanelListenerAssistant());
                } catch (NoSuchMethodException e) {
                    localizedException = new LocalizedException(new OpenMarkovException("NoSuchMethod", name), null);
                } catch (SecurityException e) {
                    localizedException = new LocalizedException(new OpenMarkovException("Security"), null);
                } catch (InstantiationException e) {
                    localizedException = new LocalizedException(new OpenMarkovException("Instantiation"), null);
                } catch (IllegalAccessException e) {
                    localizedException = new LocalizedException(new OpenMarkovException("IllegalAccess"), null);
                } catch (IllegalArgumentException e) {
                    localizedException = new LocalizedException(new OpenMarkovException("IllegalArgument"), null);
                } catch (InvocationTargetException e) {
                    localizedException = new LocalizedException(new OpenMarkovException("InvocationTarget"), null);
                } finally {
                    if (localizedException != null) localizedException.showException();
                }
            }
            mainPanel.getToolBarPanel().add(instance);
        }
        activeToolbars.add(name);
    }
    
    /**
     * This method gets all the plugins with Toolbar annotations
     *
     * @return a list with the plugins detected with Toolbar annotations.
     */
    private final @NotNull Stream<Class<ToolBarBasic>> findAllToolbars() {
        return PluginSearch.init()
                           .annotatedWith(Toolbar.class)
                           .childrenOf(ToolBarBasic.class)
                           .stream();
    }
}
