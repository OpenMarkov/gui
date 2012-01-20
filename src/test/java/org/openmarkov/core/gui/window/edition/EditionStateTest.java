/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.window.edition;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openmarkov.core.gui.loader.element.CursorLoader;



/**
 * This class is used to test the class
 * {@link openmarkov.gui.edition.main.EditionState}.
 * 
 * @author jmendoza
 */
public class EditionStateTest {
	/**
	 * This method creates all the states and checks if the cursor that they
	 * return is correct.
	 */
	@Test
	public final void testGetCursor() {
		assertEquals(EditionState.SELECTION.getCursor(),
				CursorLoader.CURSOR_DEFAULT);
		assertEquals(EditionState.CHANCE.getCursor(),
				CursorLoader.CURSOR_CHANCE_CREATION);
		assertEquals(EditionState.DECISION.getCursor(),
				CursorLoader.CURSOR_DECISION_CREATION);
		assertEquals(EditionState.UTILITY.getCursor(),
				CursorLoader.CURSOR_UTILITY_CREATION);
		assertEquals(EditionState.LINK.getCursor(),
				CursorLoader.CURSOR_LINK_CREATION);
	}
}
