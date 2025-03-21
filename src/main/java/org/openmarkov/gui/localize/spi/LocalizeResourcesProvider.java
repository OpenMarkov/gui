package org.openmarkov.gui.localize.spi;

import org.apache.commons.io.FilenameUtils;
import org.openmarkov.gui.localize.StringBundle;
import org.openmarkov.gui.localize.XMLResourceBundle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.spi.ResourceBundleProvider;

public interface LocalizeResourcesProvider extends ResourceBundleProvider {
    
    public default Map<String, StringBundle> getBundlesMap(Locale locale) {
        Map<String, StringBundle> bundles = new LinkedHashMap<>();
        String localeSuffix = "_" + locale.getLanguage();
        Class<? extends LocalizeResourcesProvider> thisClass = this.getClass();
        String infix = getInfixForPathGetBundles()
                .replaceFirst("org.openmarkov.", "");
        String infixWithLocalize = infix + File.separator + "localize";
        while (infixWithLocalize.startsWith(File.separator)){
            infixWithLocalize=infixWithLocalize.substring(1);
        }
        //URL dirElement = thisClass.getResource(infixWithLocalize);
        URL dirElement = getResourceFromResourcesRoot(infixWithLocalize);
        File classpathElement = null;
        try {
            classpathElement = new File(dirElement.toURI());
        } catch (URISyntaxException | NullPointerException e1) {
            e1.printStackTrace();
        }
        if (classpathElement != null && classpathElement.isDirectory()) {
            File localizeFolder = new File(classpathElement.getAbsolutePath());
            File[] listFiles = localizeFolder.listFiles();
            /*
            Arrays.stream(Objects.requireNonNull(listFiles))
                    .filter(File::isFile)
                    .filter(file->file.getName().endsWith(".xml"))
                    .filter(file -> FilenameUtils.getBaseName(file.getName()).endsWith(localeSuffix))
                    .forEach(fileEntry->{
                        String baseName = FilenameUtils.getBaseName(fileEntry.getName());
                        InputStream bis = null;
                        try {
                            bis = new FileInputStream(fileEntry);
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
                    });
            */
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
                                    bis = new FileInputStream(fileEntry);
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
    
    Class<? extends LocalizeResourcesProvider> auxClass();
    
    public default InputStream getResourceAsStreamFromResourcesRoot(String infix){
        var resource = getResourceFromResourcesRoot(infix);
        try{
            return resource.openStream();
        }catch (NullPointerException | IOException ex){
            return null;
        }
    }
    
    public default URL getResourceFromResourcesRoot(String infix) {
        var resourcesDirUrl = auxClass().getProtectionDomain().getCodeSource().getLocation();
        String resourcesDir = resourcesDirUrl.getFile().substring(1);
        var resourceLocation = Path.of(resourcesDir).resolve(infix);
        if (!resourceLocation.toFile().exists()) {
            return null;
        }
        try {
            return resourceLocation.toUri().toURL();
        } catch (MalformedURLException e) {
            return null;
        }
    }
    
    
    public String getInfixForPathGetBundles();
    
    public default String getInfixForPathGetBundles(String nameWithDots) {
        return File.separatorChar + nameWithDots.replace('.', File.separatorChar);
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
