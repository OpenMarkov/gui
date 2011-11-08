package openmarkov.core.gui.loader;


import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;

import openmarkov.core.gui.OpenMarkov;
import openmarkov.core.gui.loader.element.IconLoader;
import openmarkov.core.gui.utils.Util;



/**
 * OpenMarkovLogoIcon encapsulates in a single class the icon to be used in frames
 * and be easy to maintain
 * 
 * @author jlgozalo
 * @version 1.0 14 Jun 2009
 */
public class OpenMarkovLogoIcon {

	/**
	 * Icon for the Main OpenMarkov Frame
	 */
	static final String OPENMARKOV_LOGO_IMAGEICON_16 = Util.getResourcesPath() +
		"\\images\\OM_16p4.png";
	static final String OPENMARKOV_LOGO_IMAGEICON_32 = Util.getResourcesPath() +
			"\\images\\C2_32.jpg";
	static final String OPENMARKOV_LOGO_IMAGEICON_64 = Util.getResourcesPath() +
			"\\images\\C2_64.jpg";

	/** OpenMarkovLogoIcon unique instance. Used in singleton pattern. */
	private static OpenMarkovLogoIcon instance = null;

	/**
	 * default constructor
	 */
	private OpenMarkovLogoIcon() {

	}

	/**
	 * the unique instance for this object
	 * 
	 * @return OpenMarkovLogoIcon single instance (singleton pattern)
	 */
	public static OpenMarkovLogoIcon getUniqueInstance() {

		if (instance == null) {
			instance = new OpenMarkovLogoIcon();
		}
		return instance;
	}

	/**
	 * retrieves the openmarkov logo image for 16 points
	 * 
	 * @return the image for 16 points
	 */
	public Image getOpenMarkovLogoIconImage16() {

		Image icon =
			Toolkit.getDefaultToolkit().getImage(
				OpenMarkov.class.getResource(OPENMARKOV_LOGO_IMAGEICON_16));
		
		return icon;
	}

	/**
	 * retrieves the openmarkov logo image for 32 points
	 * 
	 * @return the image for 32 points
	 */
	public Image getOpenMarkovLogoIconImage32() {

		Image icon =
			Toolkit.getDefaultToolkit().getImage(
				OpenMarkov.class.getResource(OPENMARKOV_LOGO_IMAGEICON_32));
		return icon;
	}

	/**
	 * retrieves the openmarkov logo image for 64 points
	 * 
	 * @return the image for 64 points
	 */
	public Image getOpenMarkovLogoIconImage64() {

		Image icon =
			Toolkit.getDefaultToolkit().getImage(
				OpenMarkov.class.getResource(OPENMARKOV_LOGO_IMAGEICON_64));
		return icon;
	}
	/**
	 * retrieves the openmarkov logo icon for 16 points
	 * 
	 * @return the icon for 16 points
	 */
	public ImageIcon getOpenMarkovLogoIcon16() {

		return new IconLoader().load(IconLoader.OPENMARKOV_LOGO_ICON_16);
	}
	/**
	 * retrieves the openmarkov logo icon for 32 points
	 * 
	 * @return the icon for 32 points
	 */
	public ImageIcon getOpenMarkovLogoIcon32() {

		return new IconLoader().load(IconLoader.OPENMARKOV_LOGO_ICON_32);
	}
	/**
	 * retrieves the openmarkov logo icon for 64 points
	 * 
	 * @return the icon for 64 points
	 */
	public ImageIcon getOpenMarkovLogoIcon64() {

		return new IconLoader().load(IconLoader.OPENMARKOV_LOGO_ICON_64);
	}

}
