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
    private static StringResource stringResource = StringResourceLoader.getUniqueInstance().getBundleMenus();

    /**
     * Suffix that has label string resources.
     */
    private final static String LABEL_SUFFIX = ".Label";

    /**
     * Suffix that has mnemonic string resources.
     */
    private final static String MNEMONIC_SUFFIX = ".Mnemonic";

    /**
     * Suffix to retrieve tooltip strings from a string resource.
     */
    private final static String STRING_TOOLTIP_SUFFIX = ".ToolTip.Label";
    
    public static String getString(String stringId)
    {
        return stringResource.getString(stringId);
    }
    
    public static String getLabel(String stringId)
    {
        return stringResource.getString(stringId + LABEL_SUFFIX);
    }
    
    public static String getMnemonic(String stringId)
    {
        return stringResource.getString(stringId + MNEMONIC_SUFFIX);
    }
    
    
    
}
