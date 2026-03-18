/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.dialog.common;

import org.jetbrains.annotations.NotNull;
import org.openmarkov.core.exception.ThereIsNoPotentialsInNodeException;
import org.openmarkov.core.exception.UnreachableException;
import org.openmarkov.core.exception.UnrecoverableException;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.java.classUtils.ClassUtils;
import org.openmarkov.plugin.ExtensionTree;
import org.openmarkov.plugin.PluginSearch;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.stream.Stream;

public class PotentialPanelManager {
    /**
     * Singleton instance
     */
    private static final PotentialPanelManager INSTANCE = new PotentialPanelManager();
    
    private final HashMap<Class<? extends Potential>, Class<? extends PotentialPanel>> potentialPanelClassesByClass;
    
    /**
     * Constructor for PotentialPanelManager.
     */
    private PotentialPanelManager() {
        this.potentialPanelClassesByClass = new HashMap<>();
        PotentialPanelManager.findAllPotentialPanels().forEach(plugin -> {
            PotentialPanelPlugin lAnnotation = plugin.getAnnotation(PotentialPanelPlugin.class);
            for (var potentialClass : lAnnotation.potentialClasses()) {
                potentialPanelClassesByClass.put(potentialClass, plugin);
            }
        });
        
        PluginSearch.init()
                    .childrenOf(Potential.class)
                    .filter(ClassUtils::isConcrete)
                    .filter(potentialClass -> !this.potentialPanelClassesByClass.containsKey(potentialClass))
                    .extensionTree()
                    .breathFirstLevelOrderQueue()
                    .reversed()
                    .stream().map(ExtensionTree::getCurrentClass)
                    .forEach(potentialClass -> {
                        for (Class<?> superClass : ClassUtils.superClassesOf(potentialClass).reversed()) {
                            var potentialPanelClass = this.potentialPanelClassesByClass.get(superClass);
                            if (potentialPanelClass != null) {
                                this.potentialPanelClassesByClass.put(potentialClass, potentialPanelClass);
                                return;
                            }
                        }
                    });
        
    }
    
    public Class<? extends PotentialPanel> getPotentialPanelClassOf(Class<? extends Potential> potentialClass) {
        return this.potentialPanelClassesByClass.get(potentialClass);
    }
    
    public static PotentialPanelManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * Returns a potential panel by name or family.
     *
     * @return a new Potential instance given the parameters.
     */
    public final PotentialPanel createPotentialPanel(Node node) {
        try {
            try {
                Constructor<? extends PotentialPanel> constructor =
                        potentialPanelClassesByClass.get(node.getFirstPotential().getClass())
                                                    .getConstructor(Node.class);
                return constructor.newInstance(node);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new UnreachableException(e);
            }
        } catch (ThereIsNoPotentialsInNodeException e) {
            throw new UnrecoverableException(e);
        }
    }
    
    /**
     * Finds all learning algorithms.
     *
     * @return a list of learning algorithms.
     */
    private static @NotNull Stream<Class<? extends PotentialPanel>> findAllPotentialPanels() {
        return PluginSearch.init()
                           .annotatedWith(PotentialPanelPlugin.class)
                           .childrenOf(PotentialPanel.class)
                           .stream();
    }
    
    
}
