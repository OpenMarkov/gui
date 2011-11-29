package org.openmarkov.core.gui.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target (ElementType.TYPE)
public @interface ToolPlugin {
    
    String name ();
    String command ();
}
