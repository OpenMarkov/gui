package org.openmarkov.core.gui.loader;


import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.textui.TestRunner;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;



/**
 * This class tests the classes that belong to the package
 * {@link openmarkov.gui.resources.cursors}.
 * 
 * @author jmendoza
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { CursorLoaderTest.class })
public class PackageCursorsTest {

	/**
	 * This method initiates the tests as an application.
	 * 
	 * @param args
	 *            parameters passed to the command line.
	 */
	public static void main(String[] args) {

		TestRunner.run(suite());
	}

	/**
	 * This method initiates the tests.
	 * 
	 * @return the results of the test.
	 */
	public static Test suite() {

		return new JUnit4TestAdapter(PackageCursorsTest.class);
	}
}
