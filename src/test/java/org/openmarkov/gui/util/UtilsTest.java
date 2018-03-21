/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.util;

import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Util;

import static org.junit.Assert.assertTrue;

/**
 * This class tests the CommonNodePropertiesDialog class (not the visual
 * behaviour).
 *
 * @author jlgozalo
 * @version 1.0
 */
public class UtilsTest {

	@Before public void setUp() throws Exception {
	}

	/**
	 * test to verify that hasLimitBracketSymbols return true if a limit is
	 * found
	 */
	@Test public void testHasLimitBracketSymbols() {

		State[] states = new State[4];
		states[0] = new State("(0.7,1]");
		states[1] = new State("(0.4,0.7]");
		states[2] = new State("(0.15,0.4]");
		states[3] = new State("[0,0.15]");

		assertTrue(Util.hasLimitBracketSymbols(states));

	}

	/**
	 * test to verify that hasLimitBracketSymbols return true if a limit is
	 * found
	 */
	@Test public void testHasNoLimitBracketSymbols() {

		State[] states = new State[3];
		states[0] = new State("ausente");
		states[1] = new State("presente");
		states[2] = new State("desconocido");

		assertTrue(!Util.hasLimitBracketSymbols(states));

	}

}
