/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.localize;

/**
 * Wrapper class for GUI localization
 * @author Iñigo
 *
 */
public class MenuLocalizer
{
    
    /**
     * String resource.
     */    
    private static StringResourceLoader stringResourceLoader = StringResourceLoader.getUniqueInstance();

    /**
     * Suffix that has label string resources.
     */
    private final static String LABEL_SUFFIX = ".Label";

    /**
     * Suffix that has mnemonic string resources.
     */
    private final static String MNEMONIC_SUFFIX = ".Mnemonic";

    public static String getString(String stringId)
    {
        return stringResourceLoader.getBundleMenus ().getString(stringId);
    }
    
    public static String getLabel(String stringId)
    {
        return stringResourceLoader.getBundleMenus ().getString(stringId + LABEL_SUFFIX);
    }
    
    public static String getMnemonic(String stringId)
    {
        return stringResourceLoader.getBundleMenus ().getString(stringId + MNEMONIC_SUFFIX);
    }
    
    
    
}
