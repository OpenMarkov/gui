package org.openmarkov.core.gui.loader.element;


import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.net.URL;

import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;




/**
 * This class is used to load cursors from a folder.
 * 
 * @author jmendoza
 * @version 1.0
 * @version 1.1 jlgozalo - Handle Exceptions and Change inefficient Integer
 *          conversion to String in load(number)
 */
public class CursorLoader {

	/**
	 * Folder where cursors are saved.
	 */
	private static final String CURSORS_PATH = "/openmarkov/gui/resources/cursors/";

	/**
	 * Name of the cursor that represents the default one.
	 */
	public static final Cursor CURSOR_DEFAULT = load(Cursor.DEFAULT_CURSOR);

	/**
	 * Name of the cursor that represents the movement of nodes.
	 */
	public static final Cursor CURSOR_NODES_MOVEMENT = load(Cursor.MOVE_CURSOR);

	/**
	 * Name of the cursor that represents the selection of varios nodes.
	 */
	public static final Cursor CURSOR_MULTIPLE_SELECTION =
		load(Cursor.CROSSHAIR_CURSOR);

	/**
	 * Name of the cursor that represents the creation of chance nodes.
	 */
	public static final Cursor CURSOR_CHANCE_CREATION = load("chance.gif");

	/**
	 * Name of the cursor that represents the creation of decision nodes.
	 */
	public static final Cursor CURSOR_DECISION_CREATION = load("decision.gif");

	/**
	 * Name of the cursor that represents the creation of utility nodes.
	 */
	public static final Cursor CURSOR_UTILITY_CREATION = load("utility.gif");

	/**
	 * Name of the cursor that represents the creation of links.
	 */
	public static final Cursor CURSOR_LINK_CREATION = load("link.gif");
	/**
	 * Name of a inexistent cursor just for automated testing of the class
	 * juanluisgf - Nov 2008
	 */
	public static final Cursor TEST_CURSOR_FOR_NON_EXISTANT =
		load("nonexistant.gif");

	/**
	 * String resource.
	 */
	private static StringResource stringResource =
		StringResourceLoader.getUniqueInstance().getBundleMessages();

	/**
	 * This method returns the standard cursor identified by its number.
	 * 
	 * @param cursorNumber
	 *            number of the standard cursor.
	 * @return a cursor corresponding to the number.
	 */
	private static Cursor load(int cursorNumber) {

		try {

			return new Cursor(cursorNumber);
		} catch (IllegalArgumentException e) {
			String number = Integer.toString(cursorNumber);
			System.err.println(stringResource.getString(
				"CursorStandardNotExists.Text.Label", number));

			return null;
		}

	}

	/**
	 * This method loads a cursor resource and handles the exception if not
	 * exist.
	 * 
	 * @param cursorName
	 *            name of the cursor to load.
	 * @return a reference to the cursor resource.
	 */
	private static Cursor load(String cursorName) {

		try {
			Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
			URL resource =
				CursorLoader.class.getResource(CURSORS_PATH + cursorName);
			Image image = null;
			if (resource == null) {
				return null;
			} else {
				image = tk.getImage(resource);
				if (image == null) {
					System.err.println(stringResource.getString(
						"CursorResourceNotExists.Text.Label", CURSORS_PATH
							+ cursorName));
					return null;
				}
				return tk.createCustomCursor(image, new Point(0, 0), "");
			}
		} catch (Exception ex) {
			System.err.println(stringResource
				.getString("CursorResourceNotExists.Text.Label", CURSORS_PATH
					+ cursorName));

			return null;
		}
	}
}
