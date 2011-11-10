package org.openmarkov.core.gui.clipboard;


import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.util.HashSet;

import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;




/**
 * This class is the only one that exports and imports information to and from
 * the clipboard of the application.
 * 
 * @author jmendoza
 * @author jlgozalo
 * @version 1.0 jmendoza
 * @version 1.1 jlgozalo Correction to make notifyDataStored() and
 *          notifyDataInvalid() as static methods(). Including
 *          Suppresswarning("unchecked") as required.
 */
public class ClipboardManager implements ClipboardOwner {

	/**
	 * Shared clipboard of the application.
	 */
	private static Clipboard clipboard = new Clipboard("OpenMarkov");

	/**
	 * Objects that are advised when data is stored in the clipboard.
	 */
	private static HashSet<ClipboardListener> clipboardListeners =
		new HashSet<ClipboardListener>();

	/**
	 * Unique instance of the clipboard of the application.
	 */
	private static ClipboardManager clipboardManager = null;

	/**
	 * String resource.
	 */
	private static StringResource stringResource =
		StringResourceLoader.getUniqueInstance().getBundleMessages();

	/**
	 * This constructor prevents another clipboard is created.
	 */
	private ClipboardManager() {

	}

	/**
	 * Returns the unique instance of the clipboard of the application.
	 * 
	 * @return the unique instance of the clipboard of the application.
	 */
	public static ClipboardManager getUniqueInstance() {

		if (clipboardManager == null) {
			clipboardManager = new ClipboardManager();
		}

		return clipboardManager;

	}

	/**
	 * Export the information to the clipboard.
	 * 
	 * @param info
	 *            information to export to the clipboard.
	 * @throws IllegalArgumentException
	 *             if the parameter is null or if the list is empty.
	 */
	@SuppressWarnings("unchecked")
	public void exportToClipboard(ClipboardContent info)
					throws IllegalArgumentException {

		try {
			clipboard.setContents(info.clone(), this);
			notifyDataStored();
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException(ex.toString());
		}

	}

	/**
	 * Import the information from the clipboard or null if the information is
	 * not valid.
	 * 
	 * @return information that is saved into the clipboard.
	 */
	public ClipboardContent importFromClipboard() {

		if (!isThereDataStored()) {
			System.err.println(stringResource
				.getString("WrongClipboardData.Text.Label"));
			notifyInvalidData();

			return null;
		}

		return ((ClipboardContent) clipboard.getContents(this)).clone();

	}

	/**
	 * Notifies this object that it is no longer the clipboard owner.
	 * 
	 * @param aClipboard
	 *            the clipboard that is no longer owned.
	 * @param contents
	 *            the contents which this owner had placed on the clipboard.
	 */
	@SuppressWarnings("unused")
	public void lostOwnership(Clipboard aClipboard, Transferable contents) {

	}

	/**
	 * Sets a new clipboard listener.
	 * 
	 * @param newClipboardListener
	 *            object that is advised when data is stored in the clipboard.
	 */
	public static void addClipboardListener(
											ClipboardListener newClipboardListener) {

		clipboardListeners.add(newClipboardListener);

	}

	/**
	 * Notifies to the registered clipboard listener that some data is stored in
	 * the clipboard.
	 */
	private static void notifyDataStored() {

		for (ClipboardListener listener : clipboardListeners) {
			listener.dataStoredClipboard();
		}

	}

	/**
	 * Notifies to the registered clipboard listener that there isn't valid data
	 * in the clipboard.
	 */
	private static void notifyInvalidData() {

		for (ClipboardListener listener : clipboardListeners) {
			listener.invalidDataClipboard();
		}

	}

	/**
	 * This method says if there is data stored in the clipboard.
	 * 
	 * @return true if there is data stored in the clipboard; otherwise, false.
	 */
	public boolean isThereDataStored() {

		return clipboard.isDataFlavorAvailable(new ContentDataFlavor());

	}
}
