/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.window.edition;


import java.awt.Cursor;

import org.openmarkov.core.gui.loader.element.CursorLoader;




/**
 * This enum defines the possible states of the edition.
 * 
 * @author jmendoza
 * @version 1.0
 */
public enum EditionState {
	/**
	 * State used to select objects.
	 */
	SELECTION(0),

	/**
	 * State used to insert new chance nodes.
	 */
	CHANCE(1),

	/**
	 * State used to insert new decision nodes.
	 */
	DECISION(2),

	/**
	 * State used to insert new utility nodes.
	 */
	UTILITY(3),

	/**
	 * State used to insert new links.
	 */
	LINK(4);

	/**
	 * Cursor associated to the state.
	 */
	private Cursor currentCursor = null;

	/**
	 * Constructor that saves the information about the cursor associated to the
	 * state.
	 * 
	 * @param state
	 *            new state.
	 * @throws IllegalArgumentException
	 *             if the state is not valid.
	 */
	private EditionState(int state) throws IllegalArgumentException {

		switch (state) {
		case 0: {
			currentCursor = CursorLoader.CURSOR_DEFAULT;
			break;
		}
		case 1: {
			currentCursor = CursorLoader.CURSOR_CHANCE_CREATION;
			break;
		}
		case 2: {
			currentCursor = CursorLoader.CURSOR_DECISION_CREATION;
			break;
		}
		case 3: {
			currentCursor = CursorLoader.CURSOR_UTILITY_CREATION;
			break;
		}
		case 4: {
			currentCursor = CursorLoader.CURSOR_LINK_CREATION;
			break;
		}
		default: {
			throw new IllegalArgumentException();
		}
		}

	}

	/**
	 * Returns the cursor associated to the state.
	 * 
	 * @return the cursor associated to the state.
	 */
	public Cursor getCursor() {

		return currentCursor;

	}
}
