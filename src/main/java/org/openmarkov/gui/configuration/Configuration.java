/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.configuration;

public interface Configuration extends DefaultConfiguration {

	/**
	 * @return Component name. <code>String</code>
	 */
	public String getComponentName();

	/**
	 * @param name <code>String</code>. Property name.
	 * @return An <code>Object</code> whose name = <code>name</code>.
	 */
	public Object getProperty(String name);

	/**
	 * Creates or modifies a property.
	 *
	 * @param name  <code>String</code>. Property name.
	 * @param value <code>Object</code>.
	 */
	public void setProperty(String name, Object value);

}
