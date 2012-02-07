/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.window;


import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.openmarkov.core.gui.configuration.OpenMarkovPreferences;
import org.openmarkov.core.gui.configuration.OpenMarkovPreferencesKeys;
import org.openmarkov.core.gui.dialog.SplashScreenLoader;
import org.openmarkov.core.gui.loader.element.OpenMarkovLogoIcon;



/**
 * This class constructs the main GUI in a frame with a splash screen during the
 * loading and reading configuration from external preferences.
 * 
 * @author mendoza
 * @author jlgozalo
 * @version 0 mendoza - initial version
 * @version 1.0 jlgozalo - including splashscreen
 * @version 1.1 jlgozalo - externalizing thisFrame to handle AboutBox and others
 * @version 1.2 jlgozalo - including external preferences
 * @version 1.3 jlgozalo - replacing System.err with JOptionPane
 */
public class MainGUI {

	/**
	 * Main frame of the GUI
	 */
	JFrame thisFrame = null;

	/**
	 * Main panel of the GUI.
	 */
	MainPanel mainPanel = null;

	/**
	 * isShowed is true if the GUI is displayed correctly
	 */
	private boolean isShowed = false;

	/**
	 * used to set visible after initialization
	 */
	private boolean finished = false;

	/**
	 * Launch the MainGUIInit runnable process
	 * @wbp.parser.entryPoint
	 */
	public MainGUI() {

		mainGUIinit();
	}

	/**
	 * Creates the main frame as a window with a splash Screen
	 * @wbp.parser.entryPoint
	 */
	private void mainGUIinit() {
		
		/**
		 * Splash Screen panel
		 */
		final SplashScreenLoader splash = new SplashScreenLoader();

		configureUI();
		splash.splashScreenInit();
		new SwingWorker<Void, Integer>() {

			/**
			 * @throws Exception
			 * @return Void
			 */
			protected Void doInBackground() throws Exception {

				splash.getSplash().setProgress( "Loading OpenMarkov preferences", 0 );
				doReadPreferences();
				splash.doingWork();
				thisFrame = new JFrame();
				thisFrame
					.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
				thisFrame.setTitle( "OpenMarkov" );
				thisFrame.setName( "MainGUI" );
		        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(thisFrame.getGraphicsConfiguration());  
				thisFrame.setSize( screenPortionSize(screenInsets) );
				thisFrame.setLocation( screenInsets.left, screenInsets.top );
				splash.getSplash().setProgress( "Loading Resources", 25 );
				// TODO here will be the plug-in loaders in future
				splash.doingWork();
				thisFrame.setIconImage( OpenMarkovLogoIcon.getUniqueInstance()
					.getOpenMarkovLogoIconImage16() );
				splash.getSplash().setProgress( "Loading Main Panel", 50 );
				splash.doingWork();
				thisFrame.setContentPane( getMainPanel() );
				splash.getSplash().setProgress( "Completed", 100 );
				splash.doingWork();

				setFinished( true );
				return null;

			}

			protected void process(List<Integer> chunks) {

				try {
					while (!isFinished()) {
						// wait
					}
					wait( 50000 );
				} catch (InterruptedException ex) {
					// do nothing
				}
			}

			protected void done() {

				// loading the application
				splash.splashScreenDestroy();
				thisFrame.setVisible( true );
			}
		}.execute();
		this.isShowed = true;

	}

	/**
	 * This method sets and configures the UI manager.
	 * @wbp.parser.entryPoint
	 */
	private static void configureUI() {
		
		try {
			UIManager
				.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(
				null, "Unable to progress Look & Feel : ClassNotFound problem",
				"Look & Feel error", JOptionPane.ERROR_MESSAGE );
		} catch (InstantiationException e) {
			JOptionPane.showMessageDialog(
				null, "Unable to progress Look & Feel : Instatiation problem",
				"Look & Feel error", JOptionPane.ERROR_MESSAGE );
		} catch (IllegalAccessException e) {
			JOptionPane.showMessageDialog(
				null, "Unable to progress Look & Feel : IllegalAccess problem",
				"Look & Feel error", JOptionPane.ERROR_MESSAGE );
		} catch (UnsupportedLookAndFeelException e) {
			JOptionPane.showMessageDialog(
				null, "Unable to progress Look & Feel : Unsupported problem",
				"Look & Feel error", JOptionPane.ERROR_MESSAGE );
		}
		/*
		 * The next line is used to avoid that disabled menuitems are
		 * highlighted.
		 */

		UIManager.put( "MenuItem.disabledAreNavigable", Boolean.FALSE );

	}

	/**
	 * This method returns a dimension that represents the 3/4 size of the
	 * screen.
	 * 
	 * @return new dimensions of the window.
	 * @wbp.parser.entryPoint
	 */
    private Dimension screenPortionSize (Insets screenInsets)
    {
        Dimension screen = Toolkit.getDefaultToolkit ().getScreenSize ();
        int width = screen.width - screenInsets.right - screenInsets.left;
        int height = screen.height - screenInsets.top - screenInsets.bottom;
        return new Dimension (width, (height * 6) / 7);
    }

	/**
	 * This method initialises mainPanel.
	 * 
	 * @return a new main panel.
	 */
	public MainPanel getMainPanel() {

		if (mainPanel == null) {
			mainPanel = new MainPanel( this.thisFrame );
		}

		return mainPanel;

	}

	/**
	 * isShowing is true if the panel is already loaded
	 * 
	 * @return true if it is loaded and showed
	 */
	public boolean isShowing() {

		return this.isShowed;
	}

	/**
	 * @return the finished
	 */
	public boolean isFinished() {

		return finished;
	}

	/**
	 * @param finished
	 *            the finished to set
	 */
	public void setFinished(boolean finished) {

		this.finished = finished;
	}

	/**
	 * @return the thisFrame
	 * @wbp.parser.entryPoint
	 */
	public JFrame getThisFrame() {

		return this.thisFrame;
	}

	/**
	 * read the <code>OpenMarkovPreferences</code> configuration, and set the
	 * LastConnection preference to current Time
	 */
	private static void doReadPreferences() {

		final boolean initialised =
			OpenMarkovPreferences.getBoolean(
					OpenMarkovPreferencesKeys.INITIALIZED,
				OpenMarkovPreferences.OPENMARKOV_PREFERENCES, false );
		if (!initialised) {
			OpenMarkovPreferences.setDefaultPreferences();
		}
    	OpenMarkovPreferences.set(
    			OpenMarkovPreferencesKeys.LAST_CONNECTION, Double.toString( System
				.currentTimeMillis() ), OpenMarkovPreferences.OPENMARKOV_PREFERENCES );
		OpenMarkovPreferences.set( OpenMarkovPreferencesKeys.LAST_USER_CONNECTED, System
			.getProperty( "user.name" ), OpenMarkovPreferences.OPENMARKOV_PREFERENCES );
	}

}
