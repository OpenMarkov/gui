/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.localize;


import java.awt.Component;
import java.awt.Container;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.openmarkov.core.gui.component.LastRecentFilesMenuItem;
import org.openmarkov.core.gui.configuration.OpenMarkovPreferences;
import org.openmarkov.core.gui.menutoolbar.toolbar.ZoomComboBox;
import org.openmarkov.core.gui.window.mdi.MDIMenu;
import org.openmarkov.core.gui.window.message.NonEditableTextArea;




/**
 * This class creates new string resources with the recorded language.
 * 
 * @author jmendoza
 * @version 1.0 jmendoza
 * @version 1.1 jlgozalo adding the listeners for i18n support adding the
 *          get/set for the locale and setting internal StringResource variables
 *          for improving performance
 * @version 1.2. jlgozalo including ZoomComboBox component
 */
public class StringResourceLoader implements LocaleChangeListener {

	/**
	 * Path of the resource files.
	 */
	private static final String STRING_LANGUAGE_PATH  = 
		OpenMarkovPreferences.get(OpenMarkovPreferences.STRING_LANGUAGES_PATH, 
			OpenMarkovPreferences.OPENMARKOV_LANGUAGES,
			"localize/");
	/**
	 * Default language.
	 */
	private static final String DEFAULT_LANGUAGE = System.getProperty("user.language");

	/**
	 * Language to use.
	 */
	private static String language = null;

	/**
	 * Locale to use
	 */
	private static Locale locale = null;

	/**
	 * Unique instance of this class.
	 */
	private static StringResourceLoader instance = null;

	/**
	 * Set the different StringResources to improve performance
	 */
	private static StringResource menusStringResource = null;
	private static StringResource messagesStringResource = null;
	private static StringResource dialogsStringResource = null;
	private static StringResource toolBarsStringResource = null;
	private static StringResource selectablesStringResource = null;
	private static StringResource languagesStringResource = null;
	private static StringResource buttonsStringResource = null;
	private static StringResource fullAppStringResource = null;
	private static String fullApplicationName = "";

	/**
	 * This constructor initializes the object with the language of the class.
	 * Then creates all the resource bundles to check if the language is
	 * available for all of them. If this language is not available for all, the
	 * default one is used.
	 */
	private StringResourceLoader() {

		getBundleMenus();
		getBundleMessages();
		getBundleDialogs();
		getBundleToolBars();
		getBundleSelectables();
		getBundleLanguages();
		getBundleButtons();
		setLocale(new Locale(language));
		instance = this;

	}

	/**
	 * Returns the unique instance of this class. If the instance doesn't exist,
	 * then a new instance is initialized.
	 * 
	 * @return the unique instance.
	 */
	public static StringResourceLoader getUniqueInstance() {

		if (instance == null) {
			if ((language == null) || language.equals("")) {
				language =  DEFAULT_LANGUAGE;
			}
			instance = new StringResourceLoader();
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

        if (newLanguage.equals("es")) { 
            language = newLanguage;
        } else {
            language = "en";
        }
    }
	
	/**
	 * @return the language
	 */
	public String getLanguage() {

		return language;
	}

	/**
	 * @return the locale
	 */
	public Locale getLocale() {

		return locale;
	}

	/**
	 * @param locale
	 *            the locale to set
	 */
	public static void setLocale(Locale newLocale) {

		locale = newLocale;
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
	public StringResource getBundle(String resourceFile) {
		StringResource stringResource = null;
		ResourceBundle bundle = null;
		String file =  "localize/" + resourceFile;
		//String bundleLanguage = "";
		Locale tempLocale = null;
		String tempLanguage = "";

		/*if ((language == null) || (language.equals(""))) {
			setLanguage(System.getProperty("user.language"));
		}
		*/
		tempLanguage = getLanguage();
		tempLocale = new Locale(tempLanguage);
		setLocale(tempLocale);
		try {
			 bundle = ResourceBundle.getBundle(file, tempLocale);
    	   		
		} catch (MissingResourceException e) {
			System.out.println("WARNING: Resource bundle " + resourceFile
					+ " could not be found for locale '" + tempLocale
					+ "'. English will be used instead");
			setLanguage("en");
			tempLanguage = getLanguage();
			tempLocale = new Locale(tempLanguage);
			setLocale(tempLocale);		
			try {
				 bundle = ResourceBundle.getBundle(file, tempLocale);
	   	   		
			} catch (MissingResourceException e1) {
				throw new MissingResourceException("Any of the "
					+ resourceFile.toLowerCase()
					+ " resource string files is missing",
					StringResourceLoader.class.getName(), getLocale().getLanguage());
			
			}			
		}
		
		stringResource = new StringResource(bundle);
		//bundleLanguage = bundle.getLocale().getLanguage();
		return stringResource;

	}

	/**
	 * Returns a menus string resource.
	 * 
	 * @return a menus resource bundle.
	 */
	public StringResource getBundleMenus() {

		if (menusStringResource == null) {
			menusStringResource = getBundle("Menus");
		}
		return menusStringResource;

	}

	/**
	 * Returns a messages string resource.
	 * 
	 * @return a messages resource bundle.
	 */
	public StringResource getBundleMessages() {

		if (messagesStringResource == null) {
			messagesStringResource = getBundle("Messages");
		}
		return messagesStringResource;
	}

	/**
	 * Returns a dialogs string resource.
	 * 
	 * @return a dialogs resource bundle.
	 */
	public StringResource getBundleDialogs() {

		if (dialogsStringResource == null) {
			dialogsStringResource = getBundle("Dialogs");
		}
		return dialogsStringResource;
	}

	/**
	 * Returns a toolbars string resource.
	 * 
	 * @return a toolbars resource bundle.
	 */
	public StringResource getBundleToolBars() {

		if (toolBarsStringResource == null) {
			toolBarsStringResource = getBundle("ToolBars");
		}
		return toolBarsStringResource;

	}

	/**
	 * Returns a states string resource.
	 * 
	 * @return a states resource bundle.
	 */
	public StringResource getBundleSelectables() {

		if (selectablesStringResource == null) {
			selectablesStringResource = getBundle("Selectables");
		}
		return selectablesStringResource;

	}

	/**
	 * Returns a languages string resource.
	 * 
	 * @return a languages resource bundle.
	 */
	public StringResource getBundleLanguages() {

		if (languagesStringResource == null) {
			languagesStringResource = getBundle("Languages");
		}
		return languagesStringResource;
	}

	/**
	 * Returns a buttons string resource.
	 * 
	 * @return a buttons resource bundle.
	 */
	public StringResource getBundleButtons() {

		if (buttonsStringResource == null) {
			buttonsStringResource = getBundle("Buttons");
		}
		return buttonsStringResource;

	}

	/**
	 * Returns a full application string resource.
	 * 
	 * @param fullApplicationBundleFile
	 *            name of the bundle file
	 * @return a full application resource bundle.
	 */
	public StringResource getFullApplicationBundle(
													String fullApplicationBundleFile) {

		if (fullAppStringResource == null) {
			fullAppStringResource = getBundle(fullApplicationBundleFile);
			fullApplicationName = fullApplicationBundleFile;
		}
		return fullAppStringResource;

	}

	// Create the listener list
	protected javax.swing.event.EventListenerList listenerList =
		new javax.swing.event.EventListenerList();

	// This methods allows classes to register for LocaleChangeEvent
	public void addStringResourceLocaleChangeListener(
														StringResourceLocaleChangeListener listener) {

		listenerList.add(StringResourceLocaleChangeListener.class, listener);
	}

	// This methods allows classes to unregister for LocaleChangeEvent
	public void removeStringResourceLocaleChangeListener(
															StringResourceLocaleChangeListener listener) {

		listenerList.remove(StringResourceLocaleChangeListener.class, listener);
	}

	/**
	 * This private class is used to fire LocaleChangeEvent
	 * @param evt - event to manage for locale change
	 */
	protected static void fireLocaleChangeEvent(LocaleChangeEvent evt) {

		Object[] listeners =
			StringResourceLoader.getUniqueInstance().listenerList
				.getListenerList();
		// Each listener occupies two elements - the first is the listener class
		// and the second is the listener instance
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == StringResourceLocaleChangeListener.class) {
				((StringResourceLocaleChangeListener) listeners[i + 1])
					.processStringResourceLocaleChange(evt);
			}
		}
	}

	public void processLocaleChange(LocaleChangeEvent event) {

		setLanguage(event.getLanguage());
		setLocale(event.getLocale());
		resetStringResourceBundles();
		fireLocaleChangeEvent(event);

	}

	/**
	 * reset the StringResource to null
	 */
	private void resetStringResourceBundles() {

		menusStringResource = null;
		messagesStringResource = null;
		dialogsStringResource = null;
		toolBarsStringResource = null;
		selectablesStringResource = null;
		languagesStringResource = null;
		buttonsStringResource = null;
		getBundleMenus();
		getBundleMessages();
		getBundleDialogs();
		getBundleToolBars();
		getBundleSelectables();
		getBundleLanguages();
		getBundleButtons();
		if (!fullApplicationName.equals("")) {
			getFullApplicationBundle(fullApplicationName);
		}

	}

	/**
	 * Method to change behaviours in a container using Java Reflection API. 
	 * All the different objects must comply with a strictly naming convention
	 * to prevent string not to be updated properly. When the objects will be
	 * created by programmers, the "name" property of the object must be set as
	 * "ContainerOwner.ComponentVariableName" where the ContainerOwner is the
	 * name of the container where the component belongs to. All other objects
	 * must implement there own listeners.
	 * 
	 * @param c
	 *            the container to be updated
	 */
	public void allComponentsUpdateSetText(Container c) {

		StringResource stringResource =
			StringResourceLoader.getUniqueInstance().getBundleDialogs();
		StringResource stringMenuResource =
			StringResourceLoader.getUniqueInstance().getBundleMenus();
		String temp = "";
		Component[] listComponents = c.getComponents();
		for (Component item : listComponents) {
			if (item instanceof JButton) {
				if (!((JButton) item).getText().equals("")) {
					temp = ((JButton) item).getName() + ".Text.Label";
					((JButton) item).setText(buttonsStringResource.getString(temp));
				}
			} else if (item instanceof JDialog) {
				temp = ((JDialog) item).getName() + ".Title.Text";
				((JDialog) item).setTitle(stringResource.getString(temp));
				allComponentsUpdateSetText((Container) item);
			} else if (item instanceof JFrame) {
				temp = ((JFrame) item).getName() + ".Title.Text";
				((JFrame) item).setTitle(stringResource.getString(temp));
				allComponentsUpdateSetText((Container) item);
			} else if (item instanceof JLabel) {
				temp = ((JLabel) item).getName() + ".Text";
				((JLabel) item).setText(stringResource.getString(temp));
			} else if (item instanceof MDIMenu) {
				// doNothing
			} else if (item instanceof JMenu) {
				temp = ((JMenu) item).getName() + ".Label";
				((JMenu) item).setText(stringMenuResource.getString(temp));
				temp = ((JMenu) item).getName() + ".Mnemonic";
				((JMenu) item).setMnemonic(stringMenuResource.getString(temp)
					.charAt(0));
				allComponentsUpdateSetText((Container) item);
			} else if (item instanceof JMenuItem) {
				temp = ((JMenuItem) item).getName() + ".Label";
				((JMenuItem) item).setText(stringMenuResource.getString(temp));
				temp = ((JMenuItem) item).getName() + ".Mnemonic";
				((JMenuItem) item).setMnemonic(stringMenuResource.getString(
					temp).charAt(0));
			} else if (item instanceof NonEditableTextArea) {
				// doNothing
			} else if (item instanceof JPanel) {
				allComponentsUpdateSetText((Container) item);
			} else if (item instanceof JTextArea) {
				temp = ((JTextArea) item).getName() + ".Text";
				((JTextArea) item).setText(stringResource.getString(temp));
			} else if (item instanceof JTextField) {
				temp = ((JTextField) item).getName() + ".Text";
				((JTextField) item).setText(stringResource.getString(temp));
			} else if (item instanceof ZoomComboBox) {
				temp = (String) ((ZoomComboBox) item).getSelectedItem();
				((ZoomComboBox) item).setSelectedItem( temp );
			} else if (item instanceof Container) {
				allComponentsUpdateSetText((Container) item);
			} else {
				// do nothing for non registered objects as
				// those objects must implement the listener.
				// if required this method can be expanded
			}

		} // end-for
		if (c instanceof JMenu ) {
			temp = ((JMenu) c).getName() + ".Label";
			((JMenu) c).setText(stringMenuResource.getString(temp));
			// extract JMenuItems
			int itemCount = ((JMenu) c).getItemCount();
			for (int i = 0; i < itemCount; i++) {
				Component item = ((JMenu) c).getItem(i);
				if (item instanceof JMenu) {
					temp = ((JMenu) item).getName() + ".Label";
					((JMenu) item).setText(stringMenuResource.getString(temp));
					temp = ((JMenu) item).getName() + ".Mnemonic";
					((JMenu) item).setMnemonic(stringMenuResource.getString(
						temp).charAt(0));
					allComponentsUpdateSetText((Container) item);
				} else if (item instanceof LastRecentFilesMenuItem) {
					// do not change
				} else if (item instanceof JMenuItem) {
					temp = ((JMenuItem) item).getName() + ".Label";
					((JMenuItem) item).setText(stringMenuResource
						.getString(temp));
					temp = ((JMenuItem) item).getName() + ".Mnemonic";
					((JMenuItem) item).setMnemonic(stringMenuResource
						.getString(temp).charAt(0));
				} else {
					// only JSeparators are entering here!!!!
				}
			}

		}

	}

}
