/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.window.message;

import java.io.PrintStream;

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
	 * @param newMessageArea area where the messages are written.
	 * @param err
	 */
	public StandardStreamErr(MessageArea newMessageArea, PrintStream err) {
		super(newMessageArea, err);
	}

	/**
	 * Prints a string.
	 *
	 * @param x the string to be printed.
	 */
	@Override public void print(String x) {
		this.originalOut.print(x);
		messageArea.writeErrorMessage(x);
	}

	/**
	 * Terminate the current line by writing the line separator string. The line
	 * separator string is defined by the system property line.separator.
	 */
	@Override public void println() {

		messageArea.writeErrorMessage(System.getProperty("line.separator"));
	}
}
