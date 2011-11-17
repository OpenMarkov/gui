package org.openmarkov.core.gui;

import org.openmarkov.core.gui.configuration.ComponentConfiguration;
import org.openmarkov.core.gui.configuration.OpenMarkovConfiguration;
import org.openmarkov.core.gui.window.MainGUI;

/** This class stores a set of adittionalProperties and the <code>main</code> method.<p>
 * If there is some other main method in other class is only for 
 * test.<p>
 * @author manuel
 * @author fjdiez
 * @author jmendoza
 * @version 1.0
  * @version 1.1 jlgozalo - Suppress public modifier in the configuration
 *          attributes (not required); add explicit initial value and fix bug in
 *          the getUniqueInstance with the mainGui starting inside the singleton
 *          (not outside) to prevent double GUI initialization
 * @since OpenMarkov 1.0 
 */
public class OpenMarkov {

	// Attributes
	/** Stores variables such as initialPath, netsDirectory ... */
	ComponentConfiguration openMarkovKernelConfiguration = null;

	/** Stores the configuration of each component. */
	OpenMarkovConfiguration openMarkovConfiguration = null;

	/** OpenMarkov unique instance. Used in singleton pattern. */
	private static OpenMarkov openMarkov = null;

	/** OpenMarkov private variable to get access to GUI */
	private static MainGUI openMarkovGUI = null;

	// Constructor
	private OpenMarkov() {
		
	}

	// Methods
	/** @return OpenMarkov unique instance (singleton pattern). */
	public static OpenMarkov getUniqueInstance() {
		if (openMarkov == null) {
			openMarkov = new OpenMarkov();
			openMarkovGUI = new MainGUI();
		}

		return openMarkov;
	}

	/**
	 * OpenMarkov main class 
	 * @param args
	 */
	public static void main(String[] args) {
		OpenMarkov.getUniqueInstance();
	}
	
}