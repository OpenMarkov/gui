/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.menutoolbar.common;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.textui.TestRunner;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


/**
 * This class tests the classes that belong to the package
 * {@link openmarkov.gui.menutoolbar.common}.
 * 
 * @author jmendoza
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ActionCommandsTest.class})
public class PackageCommonTest {
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
		return new JUnit4TestAdapter(PackageCommonTest.class);
	}
}
