package openmarkov.core.gui.development.handler;


import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JMenuItem;

import openmarkov.core.gui.development.environment.OpenMarkovDevEnv;
import openmarkov.core.gui.menutoolbar.sax.MenuItemDevAdapter;



/**
 * Handles the dynamic activation inside the File New Action in the menu
 * 
 * @author jlgozalo
 * @version 1.0 05/04/2009
 */
public class FileExitActionHandler extends MenuItemDevAdapter {

	/**
	 * FileNewActionHandler default constructor
	 */
	public FileExitActionHandler() {

	}

	/**
	 * This method is called when the File New Action in the Menu is activated.
	 */
	public void itemActivated(JMenuItem item, ActionEvent event, String sCommand) {

		setAParentFrame(OpenMarkovDevEnv.getOpenMarkovDevEnvFrame());
		setAOpenMarkovDevEnvProperties(OpenMarkovDevEnv.getOpenMarkovDevEnvProperties());
		doExit();
	}

	/**
	 * execute the action for Exit
	 */
	private void doExit() {

		try {
			if (getAOpenMarkovDevEnvProperties().getProperties().get(
				"WorkingFileName") != null) {
				// debe preguntar si salvar el fichero antes de salir
				System.out.println("salvando antes de cerrar");
			}
		} catch (NullPointerException e) {
			// there is no file open
			System.out.println("no hay fichero que salvar");
		}

		aFrame = getAParentFrame();
		aFrame.dispose();
	}

	private JFrame aFrame = null;
}