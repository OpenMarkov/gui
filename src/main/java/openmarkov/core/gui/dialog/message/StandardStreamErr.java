package openmarkov.core.gui.dialog.message;


/**
 * This class forwards the character stream to the text area as error messages.
 * 
 * @author jmendoza
 * @version 1.0 jmendoza
 */
public class StandardStreamErr extends StandardStream {

	/**
	 * Default constructor.
	 * 
	 * @param newMessageArea
	 *            area where the messages are written.
	 */
	public StandardStreamErr(MessageArea newMessageArea) {

		super(newMessageArea);
	}

	/**
	 * Prints a string.
	 * 
	 * @param x
	 *            the string to be printed.
	 */
	@Override
	public void print(String x) {

		messageArea.writeErrorMessage(x);
	}

	/**
	 * Terminate the current line by writing the line separator string. The line
	 * separator string is defined by the system property line.separator.
	 */
	@Override
	public void println() {

		messageArea.writeErrorMessage(System.getProperty("line.separator"));
	}
}
