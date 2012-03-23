/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.io.format.annotation;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import org.junit.Test;
import org.openmarkov.core.gui.dialog.io.NetsIO;
import org.openmarkov.core.model.network.ProbNet;



/**
 * This class tests the class {@link openmarkov.gui.io.NetsIO}.
 * 
 * @author jmendoza
 * @author mkpalacio
 */
public class NetsIOTest {
	/**
	 * This method opens the a net from a file and saves it into another file.
	 * It makes the asserts to verify if the tests are good.
	 * 
	 * @param fileToOpen the file from which open the network.
	 * @param fileToSave the file into which save the network.
	 * @throws Exception if an error has occurred.
	 */
	private void openSaveNetwork(String fileToOpen, String fileToSave)
			throws Exception {
		ProbNet net = null;
		File file;
		String fileNameOpen = null;
		String fileNameSave = null;
		String path = null;
		file = new File(getClass().getClassLoader().getResource(fileToOpen).toURI());
		fileNameOpen = file.getAbsolutePath();
		if (fileNameOpen == null) {
			fail("The test file " + fileNameOpen + " can't be found");
		} else {
			net = NetsIO.openNetworkFile(fileNameOpen).getProbNet ();
			assertNotNull(net);
		}
		path = file.getParent();
		fileNameSave = path + File.separator + fileToSave;
		NetsIO.saveNetworkFile(net, fileNameSave);
		net = null;
		net = NetsIO.openNetworkFile(fileNameSave).getProbNet ();
		assertNotNull(net);
		new File(fileNameSave).delete();
	}


	/**
	 * This method tests the methods 'openNetworkFile' and 'saveNetworkFile',
	 * opening and saving various files that contain bayes nets and influence
	 * diagrams.
	 * 
	 * @throws Exception if an error occurred while the networks are opened and
	 * saved.
	 */
	@Test
	public final void testOpenSaveNetworkFile() throws Exception {
		openSaveNetwork("Net1.elv", "Net1Saved.elv");
		openSaveNetwork("Net2.elv", "Net2Saved.elv");
		
	}
}
