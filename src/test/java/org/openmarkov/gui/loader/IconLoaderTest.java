/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.loader;

import static org.junit.Assert.assertNotNull;

import java.util.MissingResourceException;

import javax.swing.Icon;

import org.junit.Before;
import org.junit.Test;
import org.openmarkov.gui.loader.element.IconLoader;



/**
 * This class tests the class {@link IconLoader}.
 * 
 * @author jmendoza
 * @author jlgozalo
 * @version 1.1 jlgozalo add test for Infinite Positive and negative icons
 */
public class IconLoaderTest {
	/**
	 * Common object for all tests.
	 */
	private IconLoader loader = null;


	/**
	 * Creates a new icon loader for all tests.
	 */
	@Before
	public void setUp() {
		this.loader = new IconLoader();
	}


	/**
	 * This method tests the method 'load' when tries to load an icon.
	 * 
	 * @throws MissingResourceException if any icon doesn't exist.
	 */
	@Test
	public final void testLoad() throws MissingResourceException {
		Icon icon;

		icon = this.loader.load(IconLoader.ICON_NEW_ENABLED);
		assertNotNull(icon);
		icon = this.loader.load(IconLoader.ICON_OPEN_ENABLED);
		assertNotNull(icon);
		icon = this.loader.load(IconLoader.ICON_SAVE_ENABLED);
		assertNotNull(icon);
		icon = this.loader.load(IconLoader.ICON_CLOSE_ENABLED);
		assertNotNull(icon);
		icon = this.loader.load(IconLoader.ICON_UNDO_ENABLED);
		assertNotNull(icon);
		icon = this.loader.load(IconLoader.ICON_REDO_ENABLED);
		assertNotNull(icon);
		icon = this.loader.load(IconLoader.ICON_SELECTION_ENABLED);
		assertNotNull(icon);
		icon = this.loader.load(IconLoader.ICON_CHANCE_ENABLED);
		assertNotNull(icon);
		icon = this.loader.load(IconLoader.ICON_DECISION_ENABLED);
		assertNotNull(icon);
		icon = this.loader.load(IconLoader.ICON_UTILITY_ENABLED);
		assertNotNull(icon);
		icon = this.loader.load(IconLoader.ICON_LINK_ENABLED);
		assertNotNull(icon);
		icon = this.loader.load(IconLoader.ICON_ZOOM_IN_ENABLED);
		assertNotNull(icon);
		icon = this.loader.load(IconLoader.ICON_ZOOM_OUT_ENABLED);
		assertNotNull(icon);
		icon = this.loader.load(IconLoader.ICON_CUT_ENABLED);
		assertNotNull(icon);
		icon = this.loader.load(IconLoader.ICON_COPY_ENABLED);
		assertNotNull(icon);
		icon = this.loader.load(IconLoader.ICON_PASTE_ENABLED);
		assertNotNull(icon);
		icon = this.loader.load(IconLoader.ICON_REMOVE_ENABLED);
		assertNotNull(icon);
		icon = this.loader.load(IconLoader.ICON_ARROW_UP_ENABLED);
		assertNotNull(icon);
		icon = this.loader.load(IconLoader.ICON_ARROW_DOWN_ENABLED);
		assertNotNull(icon);
		icon = this.loader.load(IconLoader.ICON_PLUS_ENABLED);
		assertNotNull(icon);
		icon = this.loader.load(IconLoader.ICON_MINUS_ENABLED);
		assertNotNull(icon);
		icon = this.loader.load(IconLoader.ICON_PLUS_ENABLED);
		assertNotNull(icon);
		icon = this.loader.load(IconLoader.ICON_MINUS_ENABLED);
		assertNotNull(icon);
		icon = this.loader.load(IconLoader.ICON_INFINITE_POSITIVE_ENABLED);
		assertNotNull(icon);
		icon = this.loader.load(IconLoader.ICON_INFINITE_NEGATIVE_ENABLED);
		assertNotNull(icon);
		icon = this.loader.load(IconLoader.OPENMARKOV_LOGO_ICON_16);
		assertNotNull(icon);
		//icon = this.loader.load(IconLoader.OPENMARKOV_LOGO_ICON_32);
		//assertNotNull(icon);
		//icon = this.loader.load(IconLoader.OPENMARKOV_LOGO_ICON_64);
		//assertNotNull(icon);
	}


	/**
	 * This method tests the method 'load' when tries to load an incorrect icon.
	 */
	@Test(expected = MissingResourceException.class)
	public final void testWrongLoad() {
		this.loader.load("incorrect.gif");
	}
}
