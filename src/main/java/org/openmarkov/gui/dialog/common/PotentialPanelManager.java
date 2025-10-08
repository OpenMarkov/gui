/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.dialog.common;

import org.jetbrains.annotations.NotNull;
import org.openmarkov.core.exception.UnreacheableException;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.plugin.PluginSearch;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Stream;

public class PotentialPanelManager {
    /**
     * Singleton instance
     */
    private static final PotentialPanelManager INSTANCE = new PotentialPanelManager();
    
    private HashMap<String, Class<? extends PotentialPanel>> potentialPanelClasses;
    
    /**
     * Constructor for PotentialPanelManager.
     */
    private PotentialPanelManager() {
        this.potentialPanelClasses = new HashMap<>();
        PotentialPanelManager.findAllPotentials().forEach(plugin -> {
            PotentialPanelPlugin lAnnotation = plugin.getAnnotation(PotentialPanelPlugin.class);
            this.potentialPanelClasses.put(lAnnotation.potentialType(), plugin);
        });
    }
    
    public static PotentialPanelManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * Returns a potential panel by name.
     *
     * @param potentialType the potential's name.
     *
     * @return a new Potential instance given the parameters.
     */
    public final PotentialPanel getPotentialPanel(String potentialType, Node node) {
        if (potentialPanelClasses.get(potentialType) == null) {
            return new EmptyPotentialPanel(node);
        }
        try {
            Constructor<? extends PotentialPanel> constructor
                    = potentialPanelClasses.get(potentialType).getConstructor(Node.class);
            return constructor.newInstance(node);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new UnreacheableException(e);
        }
    }
    
    /**
     * Returns a potential panel by name or family.
     *
     * @param potentialType   the potential's name.
     * @param potentialFamily the potential's family.
     *
     * @return a new Potential instance given the parameters.
     */
    public final PotentialPanel getPotentialPanel(String potentialType, String potentialFamily, Node node) {
        if (potentialPanelClasses.get(potentialType) != null) {
            try {
                Constructor<? extends PotentialPanel> constructor = potentialPanelClasses.get(potentialType)
                                                                                         .getConstructor(Node.class);
                return constructor.newInstance(node);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException ignored) {
            }
        }
        if (potentialPanelClasses.get(potentialFamily) != null) {
            try {
                Constructor<? extends PotentialPanel> constructor = potentialPanelClasses.get(potentialFamily)
                                                                                         .getConstructor(Node.class);
                return constructor.newInstance(node);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException ignored) {
            }
        }
        return new EmptyPotentialPanel(node);
    }
    
    /**
     * Returns all potential panels' names.
     *
     * @return a list of potential panels' names.
     */
    public final Set<String> getAllPotentialsNames() {
        return potentialPanelClasses.keySet();
    }
    
    /**
     * Finds all learning algorithms.
     *
     * @return a list of learning algorithms.
     */
    private static @NotNull Stream<Class<PotentialPanel>> findAllPotentials() {
        return PluginSearch.init()
                           .annotatedWith(PotentialPanelPlugin.class)
                           .childrenOf(PotentialPanel.class)
                           .stream();
    }
}
