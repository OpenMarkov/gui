/**
 * OpenMarkovDevEnvFrame.java
 */
package openmarkov.core.gui.development.environment;


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle;

import openmarkov.core.gui.development.i18n.DevStringResourceLoader;
import openmarkov.core.gui.localize.StringResource;
import openmarkov.core.gui.menutoolbar.sax.SaxDataLoader;

import org.openmarkov.core.exception.LoadingMenusException;



// ESCA-JAVA0234:
/**
 * This frame contains a tree that displays the contents of an XML document.
 * 
 * @author jlgozalo
 * @version 1.0 30/03/2008
 */
@SuppressWarnings("serial")
public class OpenMarkovDevEnvFrame extends JFrame {

	/**
	 * OPENMARKOV Development Environment Frame
	 */
	public OpenMarkovDevEnvFrame() {

		OpenMarkovDevEnv.getOpenMarkovLogger().entering("OpenMarkovDevEnvFrame", "<init>");
		setTitle("OpenMarkov Development Environment");
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setName("OpenMarkovDevelopmentEnvironmentFrame");
		setResourceBundle();
		loadMenus("src/openmarkov/gui/development/OpenMarkovDevEnv.menu.xml");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		initLayout();
		this.addWindowListener(myWindowListener);
		OpenMarkovDevEnv.getOpenMarkovLogger().exiting("OpenMarkovDevEnvFrame", "<init>");
	}

	/**
	 * @return the leftPanel
	 */
	public JScrollPane getLeftPanel() {

		return leftPanel;
	}

	/**
	 * @param leftPanel
	 *            the leftPanel to set
	 */
	public void setLeftPanel(JScrollPane leftPanel) {

		this.leftPanel = leftPanel;
	}

	/**
	 * @return the rightPanel
	 */
	public JPanel getRightPanel() {

		return rightPanel;
	}

	/**
	 * @param rightPanel
	 *            the rightPanel to set
	 */
	public void setRightPanel(JPanel rightPanel) {

		this.rightPanel = rightPanel;
	}

	/**
	 * @return the alabel
	 */
	public JLabel getAlabel() {

		return alabel;
	}

	/**
	 * @param alabel
	 *            the alabel to set
	 */
	public void setAlabel(JLabel alabel) {

		this.alabel = alabel;
	}

	/**
	 * Load the menu for the OPENMARKOV Development Environment
	 * 
	 * @param fileNameMenuXMLFile
	 *            the name of the file that contains the definition for the Menu
	 *            in XML format
	 */
	private void loadMenus(String fileNameMenuXMLFile) {

		SaxDataLoader sml = SaxDataLoader.getUniqueInstance();
		// Parse the file
		try {
			// sml.setResourceBundleLocale(aCARMENDevEnvApplicationResources);
			sml.loadData(fileNameMenuXMLFile);
			// If menu load succeeded, show the menu in the frame
			JMenuBar menubarTop = sml.menubarFind("DevTopMenu");
			if (menubarTop != null) {
				setJMenuBar(menubarTop);
				setVisible(true);
			} else {
				OpenMarkovDevEnv.getOpenMarkovLogger().exiting(
					"OpenMarkovDevEnvFrame",
					"Loading error sml.menubarFind-DevTopMenu");
				this.dispose();
			}
		} catch (LoadingMenusException ex) {
			OpenMarkovDevEnv.getOpenMarkovLogger()
				.exiting(
					"OpenMarkovDevEnvFrame",
					"Loading error sml.menubarFind-DevTopMenu");
			this.dispose();
		}

	}

	private void setResourceBundle() {

		aOPENMARKOVDevEnvApplicationResources =
			DevStringResourceLoader.getUniqueInstance()
				.getFullApplicationBundle();

	}

	/**
	 * initialize the layout of the DevEnvFrame
	 */
	public void initLayout() {

		layout = new GroupLayout(this.getContentPane());
		setLayout(layout);
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addContainerGap().addComponent(alabel).addComponent(leftPanel)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			.addComponent(rightPanel));
		layout.setVerticalGroup(layout.createParallelGroup(
			GroupLayout.Alignment.BASELINE).addComponent(alabel).addComponent(
			leftPanel).addComponent(rightPanel));
		// TODO add the listener between left and right panels
	}

	/**
	 * Group Layout to display the two panels in the frame
	 */
	private GroupLayout layout = null;
	/**
	 * right panel to show the XML definition of windows
	 */
	private JScrollPane leftPanel = new JScrollPane(new JPanel());
	/**
	 * left panel to show the layout of the windows
	 */
	private JPanel rightPanel = new JPanel();

	private JLabel alabel = new JLabel();
	/**
	 * width to display the environment
	 */
	private static final int DEFAULT_WIDTH = 1080;
	/**
	 * height to display the environment
	 */
	private static final int DEFAULT_HEIGHT = 800;

	/**
	 * Messages string resource.
	 */
	private StringResource aOPENMARKOVDevEnvApplicationResources = null;

	/**
	 * Anonymous WindowAdapter class
	 */
	WindowListener myWindowListener = new WindowAdapter() {

		public void windowClosing(WindowEvent w) {

			OpenMarkovDevEnv.getOpenMarkovLogger().exiting(
				"OpenMarkovDevEnvFrame", "Closing OpenMarkov Dev Environment ");
			OpenMarkovDevEnvFrame.this.setVisible(false);
			OpenMarkovDevEnvFrame.this.dispose();
		}
	};

}
