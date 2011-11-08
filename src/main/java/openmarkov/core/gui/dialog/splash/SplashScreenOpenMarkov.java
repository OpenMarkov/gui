package openmarkov.core.gui.dialog.splash;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;

import openmarkov.core.gui.OpenMarkov;
import openmarkov.core.gui.utils.Util;



/**
 * SplashScreenOpenMarkov Splash Screen Loader in OpenMarkov to prevent impatient user
 * and to show the progress of loading elements in the Main Program
 * 
 * @author jlgozalo
 * @version 1.0 16/11/2008
 */
public class SplashScreenOpenMarkov {

	private SplashScreen splash;

	/**
	 * the logo file
	 */
	private final String logoFile = Util.getResourcesPath() + 
			"\\images\\OpenMarkovSplash2.jpg" ;
	
	/**
	 * start the splash screen, do work and destroy
	 * @wbp.parser.entryPoint
	 */
	public SplashScreenOpenMarkov() {

		/*
		 * splashScreenInit(); simulateDoingWork(); splashScreenDestroy();
		 */
	}

	/**
	 * This method draws on the splash screen.
	 * @wbp.parser.entryPoint
	 */
	public void splashScreenInit() {

		// TODO externalize to OpenMarkov Properties the string for the icon
		
		//URL url = getClass().getResource(logoFile);
		URL url;
		try {
			url = new File(logoFile).toURI().toURL();
			ImageIcon myImage =
					new ImageIcon(url);
				splash = new SplashScreen(myImage);
				splash.setLocationRelativeTo(null);
				splash.setProgressMax(100);
				splash.setScreenVisible(true);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
	}	

	/**
	 * simulate the main program is being loaded
	 */
	public void doingWork() {

		// do something here to simulate the program doing something that
		// is time consuming
		/*String poop = "";
		for (int i = 0; i <= 1000; i++) {
			for (long j = 0; j < 2000; ++j) {
				poop = " " + (j + i);
			}
			
		}
		*/

	}

	/**
	 * destroy the splash Screen turning not visible
	 */
	public void splashScreenDestroy() {

		splash.setScreenVisible(false);
	}

	/**
	 * get splash
	 * 
	 * @return aSplash The real splash screen
	 * @wbp.parser.entryPoint
	 */
	public SplashScreen getSplash() {

		return splash;
	}

}
