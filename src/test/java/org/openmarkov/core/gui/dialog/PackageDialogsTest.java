package org.openmarkov.core.gui.dialog;


import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.textui.TestRunner;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.openmarkov.core.gui.dialog.node.NodeDomainValuesTablePanelTest;
import org.openmarkov.core.gui.dialog.node.TablePotentialPanelTest;




/**
 * This class tests the classes that belong to the package
 * {@link openmarkov.gui.dialogs.nodes}.
 * 
 * @author jlgozalo
 * @version 1.0 15/08/09
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { NodeDomainValuesTablePanelTest.class,
						TablePotentialPanelTest.class})

public class PackageDialogsTest {

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

		return new JUnit4TestAdapter(PackageDialogsTest.class);
	}
}
