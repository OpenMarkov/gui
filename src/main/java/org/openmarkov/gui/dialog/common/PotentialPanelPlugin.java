/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.dialog.common;

import org.openmarkov.core.annotation.Limits;
import org.openmarkov.core.annotation.RequiredConstructors;
import org.openmarkov.core.model.network.Node;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Limits(classesThatCanBeAnnotated = PotentialPanel.class, requiredConstructors = @RequiredConstructors({Node.class}))
@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.TYPE) public @interface PotentialPanelPlugin {
	String potentialType();
}
