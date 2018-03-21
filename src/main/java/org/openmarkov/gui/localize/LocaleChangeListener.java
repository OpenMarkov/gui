/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.localize;

import java.util.EventListener;

/**
 * Interface for LocaleChangeEvent Listeners
 *
 * @author jlgozalo
 * @version 1.0 25 Jun 2009
 */
public interface LocaleChangeListener extends EventListener {
	public abstract void processLocaleChange(LocaleChangeEvent event);
}