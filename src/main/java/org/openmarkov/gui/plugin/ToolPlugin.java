/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define a tool plugin for OpenMarkov.
 * <p>
 * This annotation should be applied to classes that represent tools
 * intended to appear as menu items within the graphical user interface.
 * </p>
 * 
 * <p>The metadata provided by this annotation is used to:</p>
 * <ul>
 *   <li>Define the visible name of the plugin in the Tools menu.</li>
 *   <li>Associate an internal command string used to trigger the plugin.</li>
 * </ul>
 * 
 * <p>This annotation is retained at runtime to allow dynamic discovery.</p>
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.TYPE) 
public @interface ToolPlugin {

    /**
     * The display name of the plugin shown in the Tools menu.
     * 
     * @return The plugin's name.
     */
	String name();

    /**
     * The internal command string associated with the plugin.
     * 
     * @return The plugin's command identifier.
     */
	String command();
}
