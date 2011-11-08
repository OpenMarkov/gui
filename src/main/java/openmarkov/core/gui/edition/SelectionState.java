package openmarkov.core.gui.edition;


import java.awt.Cursor;

import openmarkov.core.gui.loader.element.CursorLoader;



/**
 * Types of states when selecting nodes: - DEFAULT: the mouse cursor is set to
 * default and nothing is happening. - MOVING_NODES: the mouse cursor is set to
 * moveCursor and the nodes can be moved. - SELECTING_NODES: the mouse cursor is
 * set to selectionCursor and various visual elements can be selected
 * 
 * @author jmendoza
 * @version 1.0
 */
enum SelectionState {
	/**
	 * State actived when nothing is happening.
	 */
	DEFAULT(0),

	/**
	 * Various nodes are been moved.
	 */
	MOVING_NODES(1),

	/**
	 * Various nodes can be selected using the selection rectangle.
	 */
	SELECTING_NODES(2);

	/**
	 * Cursor associated to the state.
	 */
	private Cursor currentCursor = null;

	// ESCA-JAVA0126: allows throws unchecked Exception
	/**
	 * Constructor that saves the information about the cursor associated to the
	 * state.
	 * 
	 * @param state
	 *            new state.
	 * @throws IllegalArgumentException
	 *             if the state is not valid.
	 */
	private SelectionState(int state) throws IllegalArgumentException {

		switch (state) {
		case 0: {
			currentCursor = CursorLoader.CURSOR_DEFAULT;
			break;
		}
		case 1: {
			currentCursor = CursorLoader.CURSOR_NODES_MOVEMENT;
			break;
		}
		case 2: {
			currentCursor = CursorLoader.CURSOR_MULTIPLE_SELECTION;
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
