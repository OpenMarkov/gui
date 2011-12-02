package org.openmarkov.core.gui.io;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.textui.TestRunner;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.openmarkov.core.gui.io.format.annotation.NetsIOTest;


/**
 * This class tests the classes that belong to the package
 * {@link openmarkov.gui.io}.
 * 
 * @author jmendoza
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({NetsIOTest.class})
public class PackageIoTest {
	/**
	 * This method initiates the tests as an application.
	 * 
	 * @param args parameters passed to the command line.
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
		return new JUnit4TestAdapter(PackageIoTest.class);
	}
}
