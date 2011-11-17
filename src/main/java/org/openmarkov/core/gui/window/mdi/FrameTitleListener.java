package org.openmarkov.core.gui.window.mdi;


import javax.swing.JInternalFrame;


/**
 * This interface enables the listener to know the when the title of the frame
 * has changed.
 * 
 * @author jmendoza
 * @version 1.0
 * @version 1.1 jlgozalo - remove public identifier to methods (not required in
 *          an interface definition)
 */
interface FrameTitleListener {

	/**
	 * This method executes when the title of a frame has changed.
	 * 
	 * @param frame
	 *            frame whose title has been changed.
	 */
	void titleChanged(JInternalFrame frame);
}
