/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.gui.window.edition.mode;

import org.openmarkov.core.annotation.Limits;
import org.openmarkov.core.annotation.RequiredConstructors;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.gui.window.edition.EditorPanel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Limits(classesThatCanBeAnnotated = EditionMode.class, requiredConstructors = @RequiredConstructors({EditorPanel.class, ProbNet.class}))
@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.TYPE) public @interface EditionState {
    String name();
    
    String icon();
    
    String cursor() default "[default]";
}
