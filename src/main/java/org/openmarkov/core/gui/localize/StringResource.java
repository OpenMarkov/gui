/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.localize;


import java.util.MissingResourceException;


/**
 * This class contains a ResourceBundle object and limits the access to this
 * object.
 * 
 * @author jmendoza 1.0
 * @author jlgozalo 1.1
 * @author jlgozalo 1.2
 * @version 1.1 toString() added
 * @version 1.2 always return a String. If the key is not found, return a blank
 *          string to avoid stopping OPENMARKOV
 */
public class StringResource {

	/**
	 * Underlying resource.
	 */
	XMLResourceBundle resourceBundle = null;

	/**
	 * Default constructor. It saves the reference to a resource bundle.
	 * 
	 * @param newResourceBundle
	 *            underlying resource bundle.
	 */
	public StringResource(XMLResourceBundle newResourceBundle) {

		resourceBundle = newResourceBundle;

	}

	/**
	 * This method returns the requested string resource. If the key does not
	 * exist then a "virtual" string resource is returned to avoid the program
	 * to be stopped
	 * 
	 * @param key
	 *            the key of the desired string.
	 * @return the string associated with the key. if the resource doesn't
	 *         exist, then a special string is returned.
	 */
	public String getString(String key) {

		String aString = "";
		try {
			aString = resourceBundle.getString( key );
		} catch (MissingResourceException | NullPointerException e1) {
			aString = ">>> " + key + " <<<";
		}
		return aString;

	}

	/**
	 * This method returns the requested string resource, replacing each '~' by
	 * an element of the array. The number of '~' replaced depends on the number
	 * of elements of the array.
	 * 
	 * @param key
	 *            the key of the desired string.
	 * @param strings
	 *            strings that will replace the '~'.
	 * @return the string associated with the key. if the resource doesn't
	 *         exist, then a special string is returned.
	 */
	public String getString(String key, String... strings) {

		String result = "";
		String parameter = "";
		boolean flag = true;
		int i = 0;
		int l = 0;
		int index = 0;
		final String diacritic = "~";

		try {
			result = getString( key );
			if (strings != null) {
				l = strings.length;
				while (flag && (i < l)) {
					if ((index = result.indexOf( diacritic, index )) >= 0) {
						parameter = strings[i++];
						if (parameter == null) {
							parameter = "";
						}
						result =
							result.substring( 0, index )
								+ result.substring( index ).replaceFirst(
									diacritic, parameter );
						index += parameter.length();
					} else {
						flag = false;
					}
				}
			}
		} catch (MissingResourceException e1) {
			result = ">>> " + key + " <<<";
		}

		return result;

	}

	/**
	 * Print the information of this object
	 * 
	 * @return the information of the object
	 */
	@Override
	public String toString() {

		StringBuffer buf = new StringBuffer();
		buf.append( "[" + this.getClass().getName() + ":" + "\n" );
		buf.append( "Resourcebundle =" + resourceBundle.toString() );
		buf.append( "]" );
		return buf.toString();
	}

}
