package org.openmarkov.core.gui.loader;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Cursor;

import org.junit.Test;
import org.openmarkov.core.gui.loader.element.CursorLoader;



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
	@Test
	public void testNonExistantCursorForCursorNameProvidedClassicStyle() {

		Cursor cursor = CursorLoader.TEST_CURSOR_FOR_NON_EXISTANT;
		assertEquals("The cursor will be null", cursor, null);
	}

	/** test to verify OK */
	@Test
	public void testLoadOK() {

		Cursor cursor = CursorLoader.CURSOR_DEFAULT;
		assertEquals(
			"The cursor Names must be the same ", cursor.getName(), new Cursor(
				Cursor.DEFAULT_CURSOR).getName());
	}

	/**
	 * This method tests if all the cursor hanged by this class are not null.
	 */
	@Test
	public void testAllCursors() {

		assertNotNull(CursorLoader.CURSOR_DEFAULT);
		assertNotNull(CursorLoader.CURSOR_NODES_MOVEMENT);
		assertNotNull(CursorLoader.CURSOR_MULTIPLE_SELECTION);
		//assertNotNull(CursorLoader.CURSOR_CHANCE_CREATION);
		assertNotNull(CursorLoader.CURSOR_DECISION_CREATION);
		assertNotNull(CursorLoader.CURSOR_UTILITY_CREATION);
		assertNotNull(CursorLoader.CURSOR_LINK_CREATION);
	}
}
