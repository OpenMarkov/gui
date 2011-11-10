package org.openmarkov.core.gui.clipboard;


/**
 * This interface is used in order to a network panel advise to the listener
 * that some information has been put into the clipboard to paste it.
 * 
 * @author jmendoza
 * @version 1.0 jmendoza
 */
public interface ClipboardListener {

	/**
	 * This method indicates that some information has been put into the
	 * clipboard.
	 */
	void dataStoredClipboard();

	/**
	 * This method indicates that there isn't valid information in the
	 * clipboard.
	 */
	void invalidDataClipboard();
}
