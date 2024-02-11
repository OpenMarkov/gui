package org.openmarkov.gui.localize.spi;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.spi.ResourceBundleProvider;

import org.apache.commons.io.FilenameUtils;
import org.openmarkov.gui.localize.StringBundle;
import org.openmarkov.gui.localize.XMLResourceBundle;

public interface LocalizeResourcesProvider extends ResourceBundleProvider {
	
	public default Map<String,StringBundle> getBundlesMap(Locale locale){
		Map<String,StringBundle> bundles = new LinkedHashMap<>();
		String localeSuffix = "_" + locale.getLanguage();
		Class<? extends LocalizeResourcesProvider> thisClass = this.getClass();
		String infixWithLocalize = getInfixForPathGetBundles() + File.separator + "localize";
		//URL dirElement = thisClass.getResource(infixWithLocalize);
		URL dirElement = auxGetResource(infixWithLocalize);
		File classpathElement = null;
		try {
			classpathElement = new File(dirElement.toURI());
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		if (classpathElement.isDirectory()) {
			File localizeFolder = new File(classpathElement.getAbsolutePath());
			File[] listFiles = localizeFolder.listFiles();
			if (listFiles != null) {
				for (final File fileEntry : listFiles) {
					if (fileEntry.isFile()) {
						String name = fileEntry.getName();
						if (name.endsWith(".xml")) {
							String baseName = FilenameUtils.getBaseName(name);
							if (baseName.endsWith(localeSuffix)) {
								String file = infixWithLocalize + File.separator + name;
								Module m = thisClass.getModule();
								InputStream bis = null;
								try {
									//bis = m.getResourceAsStream(file);
									bis = auxGetResourceAsStream(file);
								} catch (IOException e1) {
									e1.printStackTrace();
								}								
								XMLResourceBundle xmlResourceBundle = null;
								try {
									xmlResourceBundle = new XMLResourceBundle(bis);
								} catch (IOException e) {
									e.printStackTrace();
								}								
								StringBundle strBundle = new StringBundle(xmlResourceBundle);
								baseName = baseName.substring(0, baseName.length() - localeSuffix.length());
								bundles.put(baseName, strBundle);
							}
						}
					}
				}
			}
		}
		return bundles;		
	}
	
	
	InputStream auxGetResourceAsStream(String name) throws IOException;
	
	URL auxGetResource(String infix);
	
	
	
	public String getInfixForPathGetBundles();
	
	public default String getInfixForPathGetBundles(String nameWithDots) {
		return File.separatorChar+nameWithDots.replace('.', File.separatorChar);		
	}
	
	/*
	private static ResourceBundle createXMLResourceBundle(String file, Locale locale) {
		ResourceBundle bundle;
		bundle = ResourceBundle.getBundle(file, locale);
		return bundle;			
	}
	*/

	/*
	private static StringBundle getBundle(String resourceFile) {
			StringBundle stringBundle = null;
			XMLResourceBundle bundle = null;
			// TODO: Manolo
			//String file = "localize/" + resourceFile;
			String file = "/localize/" + resourceFile;
			try {
				bundle = (XMLResourceBundle) createXMLResourceBundle(file, locale);
			} catch (MissingResourceException e) {
				System.out.println("WARNING: Resource bundle " + resourceFile + " could not be found for locale '" + locale
						+ "'. English will be used instead");
				setLanguage("en");
				try {
					bundle = (XMLResourceBundle) createXMLResourceBundle(file, locale);
				} catch (MissingResourceException e1) {
					throw new MissingResourceException(
							"Any of the " + resourceFile.toLowerCase() + " resource string files is missing",
							StringDatabase.class.getName(), getLocale().getLanguage());
				}
			}
			stringBundle = new StringBundle(bundle);
			return stringBundle;
		}
		*/
	

}
