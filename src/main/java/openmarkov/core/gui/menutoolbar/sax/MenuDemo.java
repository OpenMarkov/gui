package openmarkov.core.gui.menutoolbar.sax;


/*
 * Demo to test the use of the XML SAX Parser to read menus from external files
 * @author jlgozalo
 * 
 * @version 1.0
 */

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.openmarkov.core.exception.LoadingMenusException;



/**
 * Demo class that shows load Menu XML.
 * 
 * @author jlgozalo
 * @version 1.0
 */
public class MenuDemo implements MenuItemHandler, ToolBarItemHandler {

	JLabel jLabel = null;
	MenuHandlersTable menuHandlersTable = null;

	/**
	 * Main constructor
	 */
	public MenuDemo() {

		super();
	}

	/**
	 * Takes appropriate action when a menu item is activated.
	 */
	public void itemActivated(JMenuItem menuItem, ActionEvent e, String sCommand) {

		jLabel.setText("Activated: " + sCommand);
		if (sCommand.equals("Exit")) {
			System.exit(0);
		}
	}

	/**
	 * Reports that a ChecnkBoxJMenuItem has been deactivated.
	 * 
	 * @param e
	 *            java.awt.event.ActionEvent
	 */
	public void itemDeselected(JMenuItem menuItem, ItemEvent e, String sCommand) {

		jLabel.setText("Deselected: " + sCommand);
		if (sCommand.equals("Disable Button 1")) {

		}
	}

	/**
	 * Reports that a CheckboxJMenuItem has been deactivated.
	 * 
	 * @param e
	 *            java.awt.event.ActionEvent
	 */
	public void itemSelected(JMenuItem menuItem, ItemEvent e, String sCommand) {

		jLabel.setText("Selected: " + sCommand);
		if (sCommand.equals("Disable Button 1")) {
		}
	}

	/**
	 * Takes appropriate action when a toolbar component is activated.
	 */
	public void itemActivated(JComponent item, ActionEvent e, String sCommand) {

		jLabel.setText("Activated: " + sCommand);
		if (sCommand.equals("Exit")) {
			System.exit(0);
		}
	}

	/**
	 * Reports that a toolbar component has been deactivated.
	 * 
	 * @param e
	 *            java.awt.event.ActionEvent
	 */
	public void itemDeselected(JComponent item, ItemEvent e, String sCommand) {

		jLabel.setText("Deselected: " + sCommand);
		if (sCommand.equals("Disable Button 1")) {

		}
	}

	/**
	 * Reports that a toolbar component has been deactivated.
	 * 
	 * @param e
	 *            java.awt.event.ActionEvent
	 */
	public void itemSelected(JComponent item, ItemEvent e, String sCommand) {

		jLabel.setText("Selected: " + sCommand);
		if (sCommand.equals("Disable Button 1")) {
		}
	}

	/**
	 * Demonstrates how to use "Menu XML"
	 * 
	 * @param args[]
	 *            command line parameters args[0]=TopMenu-name args[1-ss]=Name
	 *            of menu files to be loaded
	 */
	public static void main(String args[]) {

		MenuDemo md = new MenuDemo();
		md.runDemo(args);
	}

	/**
	 * main() calls this method to run the demo.
	 * 
	 * @param args
	 *            args[0]=TopMenu-name args[1-ss]=Name of menu files to be
	 *            loaded (future:directories under home)
	 */
	public void runDemo(String[] args) {

		MenuHandlersTable mhTable = MenuHandlersTable.getUniqueInstance();

		// Default asigment of handlers just in case the configuration files has
		// not a proper one
		// Bind names of handlers to the MenuItemHandlers they represent
		mhTable.registerMenuItemHandler("FileHandler", this);
		mhTable.registerMenuItemHandler("EditHandler", this);
		mhTable.registerMenuItemHandler("ViewHandler", this);
		mhTable.registerMenuItemHandler("ViewNodesHandler", this);
		mhTable.registerMenuItemHandler("ViewZoomHandler", this);
		mhTable.registerMenuItemHandler("WindowHandler", this);
		mhTable.registerMenuItemHandler("ToolsHandler", this);
		mhTable.registerMenuItemHandler("HelpHandler", this);

		SaxDataLoader sml = SaxDataLoader.getUniqueInstance();
		// Parse the files
		// >>>> this must be replaced by a Directory searching for files with
		// extension .menu
		for (int i = 1; i < args.length; i++) {
			try {
				sml.loadData(args[i]);
			} catch (LoadingMenusException ex) {
				// do something
			}
		}

		// If menu load succeeded, show the menu in a frame
		JMenuBar menubarTop = sml.menubarFind(args[0]);
		if (menubarTop != null) {
			JFrame frame = new JFrame("JLG Menu Demo Test");
			frame.addWindowListener(new WindowAdapter() {

				public void windowClosing(WindowEvent e) {

					System.exit(0);
				}
			});
			frame.setJMenuBar(menubarTop);
			jLabel = new JLabel("Waiting...");
			frame.add(jLabel);
			frame.pack();
			frame.setVisible(true);
		} else {
			menubarTop = sml.menubarFind();
			if (menubarTop != null) {
				JFrame frame = new JFrame("JLG Menu Demo Test");
				frame.addWindowListener(new WindowAdapter() {

					public void windowClosing(WindowEvent e) {

						System.exit(0);
					}
				});
				frame.setJMenuBar(menubarTop);
				jLabel = new JLabel("Waiting...");
				frame.add(jLabel);
				frame.pack();
				frame.setVisible(true);
			} else {
				System.out.println(args[0] + ": no such menu");
			}
		}
	}
}