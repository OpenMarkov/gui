/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.loader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import org.openmarkov.gui.loader.element.CursorLoader;

import java.awt.*;


/**
 * This class tests the CursorLoader class.
 *
 * @author jlgozalo
 * @version 1.0
 */
public class CursorLoaderTest {

	/**
	 * test to verify that exception is captured when the cursor GIF file is not
	 * present.
	 */
	//TODO Fix compilation error when migrating to JUnit 5
	//@Test
	//public void testNonExistantCursorForCursorNameProvidedClassicStyle() {

		//Cursor cursor = CursorLoader.TEST_CURSOR_FOR_NON_EXISTANT;
		//assertEquals("The cursor will be null", cursor, null);
	//}

	/**
	 * test to verify OK
	 */
	@Test public void testLoadOK() {

		Cursor cursor = CursorLoader.CURSOR_DEFAULT;
		assertEquals("The cursor Names must be the same ", cursor.getName(),
				new Cursor(Cursor.DEFAULT_CURSOR).getName());
	}

	/**
	 * This method tests if all the cursor hanged by this class are not null.
	 */
	@Test public void testAllCursors() {

		assertNotNull(CursorLoader.CURSOR_DEFAULT);
		assertNotNull(CursorLoader.CURSOR_NODES_MOVEMENT);
		assertNotNull(CursorLoader.CURSOR_MULTIPLE_SELECTION);
	}
}
