/**
 * OpenMarkovDevEnv class
 */
package openmarkov.core.gui.development.environment;


import java.awt.EventQueue;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class helps to create and validate the XML OpenMarkov Data files that are
 * used to create the OpenMarkov GUI in runtime. This tool gives a set of features
 * to the programmers to create the dialog windows without a GUI IDE (like
 * Netbeans or Visual Editor). It is, in fact, a Zero-code Development
 * Architecture that prevents mistakes and ensure no errors will appear on the
 * loading process. It is using a DTD file to display the XML document as a
 * tree.
 * 
 * @author jlgozalo
 * @version 1.0 30/03/2009
 */
public class OpenMarkovDevEnv {

	/**
	 * @return the aOpenMarkovDevEnvFrame
	 */
	public static OpenMarkovDevEnvFrame getOpenMarkovDevEnvFrame() {

		if (aOpenMarkovDevEnvFrame == null) {
			aOpenMarkovDevEnvFrame = new OpenMarkovDevEnvFrame();
		}
		return aOpenMarkovDevEnvFrame;
	}

	/**
	 * @return the adittionalProperties
	 */
	public static OpenMarkovDevEnvProperties getOpenMarkovDevEnvProperties() {

		if (aOpenMarkovDevEnvProperties == null) {
			aOpenMarkovDevEnvProperties = new OpenMarkovDevEnvProperties();
		}
		return aOpenMarkovDevEnvProperties;

	}

	/**
	 * @return the logger
	 */
	public static Logger getOpenMarkovLogger() {

		if (aOpenMarkovLogger == null) {
			aOpenMarkovLogger = Logger.getLogger(openMarkovLogger);
		}
		return aOpenMarkovLogger;

	}

	/**
	 * @param args
	 *            not required
	 */
	public static void main(String[] args) {

		checkLogger();
		EventQueue.invokeLater(new Runnable() {

			public void run() {

				getOpenMarkovDevEnvProperties();
				getOpenMarkovDevEnvFrame();
				getOpenMarkovLogger().entering("main", "to show frame");
				aOpenMarkovDevEnvFrame.setVisible(true);
			}
		});
	}

	/**
	 * private constructor
	 */
	private OpenMarkovDevEnv() {

	}

	private static void checkLogger() {

		if (System.getProperty("java.util.logging.config.class") == null
			&& System.getProperty("java.util.logging.config.file") == null) {
			try {
				getOpenMarkovLogger().setLevel(Level.WARNING);
				final int LOG_ROTATION_COUNT = 10;
				Handler handler =
					new FileHandler(loggingFileName, 0, LOG_ROTATION_COUNT);
				getOpenMarkovLogger().addHandler(handler);
			} catch (IOException e) {
				getOpenMarkovLogger().log(
					Level.SEVERE, "Can't create log file handler", e);
			}
		}
	}

	/**
	 * private theFrame for the OpenMarkov Development Environment
	 */
	private static OpenMarkovDevEnvFrame aOpenMarkovDevEnvFrame = null;

	/**
	 * the OpenMarkov Dev Environment adittionalProperties
	 */
	private static OpenMarkovDevEnvProperties aOpenMarkovDevEnvProperties = null;

	/**
	 * the OpenMarkov Dev Environment logger
	 */
	private static Logger aOpenMarkovLogger = null;

	/**
	 * the OpenMarkov Logging
	 */
	private static final String openMarkovLogger = "openmarkov.gui";
	/**
	 * the OpenMarkov logging filename
	 */
	private static final String loggingFileName = "OpenMarkovDevEnv.log";
}
