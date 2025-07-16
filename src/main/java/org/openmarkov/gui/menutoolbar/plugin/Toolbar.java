/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.menutoolbar.plugin;

import org.openmarkov.core.annotation.Limits;
import org.openmarkov.core.annotation.RequiredConstructors;
import org.openmarkov.gui.menutoolbar.toolbar.ToolBarBasic;

import java.awt.event.ActionListener;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Limits(classesThatCanBeAnnotated = ToolBarBasic.class, requiredConstructors = @RequiredConstructors({ActionListener.class}))
@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.TYPE) public @interface Toolbar {
    String name();
}
