package openmarkov.core.gui.development.i18n;


import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import openmarkov.core.gui.localize.StringResource;



/**
 * This class creates new string resources with the recorded language.
 * 
 * @author jlgozalo
 * @version 1.0
 */
public class DevStringResourceLoader {

	/**
	 * Path of the resource files.
	 */
	private static final String STRING_RESOURCE_PATH =
		"openmarkov/gui/development/i18n/adittionalProperties/";

	/**
	 * Name of the full application resource bundle file
	 */
	private static final String STRING_RESOURCE_NAME =
		"OpenMarkovDevEnvApplicationResources";
	/**
	 * Default language.
	 */
	private static final String DEFAULT_LANGUAGE = "en";

	/**
	 * Language to use.
	 */
	private static String language = null;

	/**
	 * Unique instance of this class.
	 */
	private static DevStringResourceLoader instance = null;

	/**
	 * This constructor initialises the object with the language of the class.
	 * Then creates all the resource bundles to check if the language is
	 * available for all of them. If this language is not available for all, the
	 * default one is used.
	 */
	private DevStringResourceLoader() {

		getFullApplicationBundle();
		instance = this;

	}

	/**
	 * Returns the unique instance of this class. If the instance doesn't exist,
	 * then a new instance is initialized.
	 * 
	 * @return the unique instance.
	 */
	public static DevStringResourceLoader getUniqueInstance() {

		if (instance == null) {
			if ((language == null) || language.equals("")) {
				language = System.getProperty("user.language");
			}
			instance = new DevStringResourceLoader();
		}

		return instance;

	}

	/**
	 * Sets the language to a new one.
	 * 
	 * @param newLanguage
	 *            new language.
	 */
	public static void setLanguage(String newLanguage) {

		language = newLanguage;

	}

	/**
	 * Returns a string resource linked to the file given as parameter. The
	 * value of the 'language' variable is used. If it is null or empty, the
	 * language of the system is taken into account. If the system's language
	 * isn't available, the default language is English.
	 * 
	 * @param resourceFile
	 *            file that contains the resource strings.
	 * @return a resource bundle linked to the file.
	 */
	private static StringResource getBundle(String resourceFile) {

		StringResource stringResource = null;
		ResourceBundle bundle = null;
		Locale locale = null;
		String file = STRING_RESOURCE_PATH + resourceFile;
		String bundleLanguage = "";

		if ((language == null) || (language.equals(""))) {
			setLanguage(System.getProperty("user.language"));
		}
		locale = new Locale(language);
		try {
			bundle = ResourceBundle.getBundle(file, locale);
		} catch (MissingResourceException e) {
			throw new MissingResourceException("Any of the "
				+ resourceFile.toLowerCase()
				+ " resource string files is missing",
				DevStringResourceLoader.class.getName(), locale.getLanguage());
		}
		bundleLanguage = bundle.getLocale().getLanguage();
		setLanguage(bundleLanguage.equals("") ? DEFAULT_LANGUAGE
			: bundleLanguage);
		stringResource = new StringResource(bundle);

		return stringResource;

	}

	/**
	 * Returns a full application string resource.
	 * 
	 * @return a full application resource bundle.
	 */
	public StringResource getFullApplicationBundle() {

		return getBundle(STRING_RESOURCE_NAME);

	}

	/**
	 * Returns a full application string resource.
	 * 
	 * @param fullApplicationBundleFile
	 *            name of the file where the resource bundle is
	 * @return a full application resource bundle.
	 */
	public StringResource getFullApplicationBundle(
													String fullApplicationBundleFile) {

		return getBundle(fullApplicationBundleFile);

	}
}
