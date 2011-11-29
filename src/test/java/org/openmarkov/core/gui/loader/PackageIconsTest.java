package org.openmarkov.core.gui.loader;

import junit.framework.*;
import junit.textui.*;
import org.junit.runner.*;
import org.junit.runners.*;


/**
 * This class tests the classes that belong to the package
 * {@link openmarkov.gui.resources.icons}.
 * 
 * @author jmendoza
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({IconLoaderTest.class})
public class PackageIconsTest {
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
		return new JUnit4TestAdapter(PackageIconsTest.class);
	}
}
