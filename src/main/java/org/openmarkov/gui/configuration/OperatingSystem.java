/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.configuration;

import org.openmarkov.java.enumUtils.EnumUtils;

public enum OperatingSystem {
    WINDOWS,
    LINUX,
    OTHER;
    
    public String toString() {
        return EnumUtils.toTitleCase(this);
    }
}
