package org.openmarkov.core.gui.dialog.common;


import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.textui.TestRunner;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;



/**
 * This class tests the classes that belong to the package
 * {@link openmarkov.gui.commons}.
 * 
 * @author jlgozalo
 * @version 1.0 08/12/09
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { PotentialsTablePanelTest.class})

public class PackageCommonsTest {

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

		return new JUnit4TestAdapter(PackageCommonsTest.class);
	}
}
