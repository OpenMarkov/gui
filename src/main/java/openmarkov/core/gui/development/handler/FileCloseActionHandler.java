package openmarkov.core.gui.development.handler;


import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;

import openmarkov.core.gui.development.environment.OpenMarkovDevEnv;
import openmarkov.core.gui.menutoolbar.sax.MenuItemDevAdapter;



/**
 * Handles the dynamic activation inside the File New Action in the menu
 * 
 * @author jlgozalo
 * @version 1.0 05/04/2009 jlgozalo
 */
public class FileCloseActionHandler extends MenuItemDevAdapter {

	/**
	 * This method is called when the File New Action in the Menu is activated.
	 */
	public void itemActivated(JMenuItem item, ActionEvent event, String sCommand) {

		setAParentFrame(OpenMarkovDevEnv.getOpenMarkovDevEnvFrame());
		setAOpenMarkovDevEnvProperties(OpenMarkovDevEnv.getOpenMarkovDevEnvProperties());
		closeFile();
		getAOpenMarkovDevEnvProperties().getProperties().remove("WorkingFileName");
	}

	private void closeFile() {

		JFileChooser chooser = new JFileChooser();
		File file =
			(File) getAOpenMarkovDevEnvProperties().getProperties().get(
				"WorkingFileName");
		chooser.showSaveDialog(getAParentFrame());
	}

	/**
	 * Saves a OpenMarkov Development Definition in a file.
	 * 
	 * @param definition
	 *            Definition to save in the file.
	 * @param fileName
	 *            file where the definition is going to be saved.
	 * @throws Exception
	 *             if an I/O error has occurred.
	 */
	public static void saveDefinitionFile(String definition, String fileName)
					throws Exception {

		// CarmenDefinitionWriter.getUniqueInstance().writeProbNet(fileName,
		// network);
	}

}