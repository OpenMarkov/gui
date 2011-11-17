package org.openmarkov.core.gui.component;


import java.util.ArrayList;
import java.util.MissingResourceException;

import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;




/**
 * This class is used to encapsulate the different relation types of the nodes
 * and their dependent-language strings. It is used in the NodePotentialPanel to
 * display the set of algorithms in the combo box
 * 
 * @author jlgozalo
 * @version 1.0 initial version
 * @version 1.1 jlgozalo - 28/05/2010 - enhance use of For loop 
 */
public class AlgorithmRelationTypes {

	/**
	 * Internal names of the different relation types.
	 */
	private static ArrayList<String> list = null;
	

	/**
	 * Uniform item
	 */
	public static String RELATION_TYPE_UNIFORM = "Uniform";
	
	/**
	 * Uniform item
	 */
	public static String RELATION_TYPE_TABLE = "Table";
	
	/**
	 * For tree- ADD potentials
	 */
	public static String RELATION_TYPE_TREE_ADD = "Tree_ADD";
	
	/**
	 * For Time Shift potentials
	 */
	public static String RELATION_TYPE_CYCLELENGTHSHIFT = "CycleLengthShift";
	
	/**
	 * For Time potentials with same potential that previous temporal variable.
	 */
	public static String RELATION_TYPE_SAMEASPREVIOUS = "SameAsPrevious";
	
	/**
	 * For product potentials on supervalue nodes
	 */
	public static String RELATION_TYPE_PRODUCT = "Product";
	
	/**
	 * For sum potentials on supervalue nodes
	 */
	public static String RELATION_TYPE_SUM = "Sum";
	
	/**
	 * String resource.
	 */
	private static StringResource stringResource = null;

	/**
	 * This method adds all the default relation types.
	 */
	private static void fillList() {

		if (list == null) {
			list = new ArrayList<String>();
			list.add(RELATION_TYPE_UNIFORM);
			list.add(RELATION_TYPE_TABLE);
			list.add(RELATION_TYPE_TREE_ADD);
			list.add(RELATION_TYPE_CYCLELENGTHSHIFT);
			list.add(RELATION_TYPE_SAMEASPREVIOUS);
			list.add(RELATION_TYPE_SUM);
			list.add(RELATION_TYPE_PRODUCT);
			
			/*list.add("OR");
			list.add("MAXCausal");
			list.add("MAXGeneral");
			list.add("AND");
			list.add("MINCausal");
			list.add("MINGeneral");*/
		}
	}

	/**
	 * It retrieves the dependent-language string of the desired element. If the
	 * item hasn't a dependent-language string (because this item is not a
	 * default one), the returned string is the item itself.
	 * 
	 * @param element
	 *            name of the item
	 * @return a string that represents the item in the actual language.
	 */
	public static String getString(String element) {

		if (stringResource == null) {
			stringResource =
				StringResourceLoader.getUniqueInstance().getBundleSelectables();
		}
		try {
			return stringResource.getString("algorithmRelationType." + element
				+ ".Text");
		} catch (MissingResourceException e) {
			return element;
		}
	}

	/**
	 * This method returns an array of strings, each one has the default items
	 * contained in one element of the whole list separated by dashes.
	 * 
	 * @return an array that contains a list of string that contains the
	 *         different items separated by dashes.
	 */
	public static String[] getListStrings() {

		String[] strings = null;
		int i = 0;
		int l = 0;

		if (list == null) {
			fillList();
		}
		l = list.size();
		strings = new String[l];
		for (i = 0; i < l; i++) {
			strings[i] = getString(list.get(i));
		}
		return strings;
	}


	/**
	 * This method returns an array containing the default items of an element
	 * of the list. If the index is out of range (index < 0 || index > list
	 * size) the null is returned.
	 * 
	 * @param index
	 *            element of the list of default states.
	 * @return an array that contains the default states of an element of the
	 *         list of default states.
	 */
	public static String getByIndex(int index) {

		if (list == null) {
			fillList();
		}
		try {
			return list.get(index);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	/**
	 * toString()
	 * @return the string with the details of the instance
	 */
	public static String listToString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("[AlgorithmRelationTypes :(" + list.size() );
		for (String item: list) {
			strBuf.append(item);
        }
		strBuf.append("]");
		return strBuf.toString();
	}
}