package org.openmarkov.core.gui.localize;

import javax.swing.JCheckBoxMenuItem;

import org.openmarkov.core.gui.loader.element.IconLoader;

@SuppressWarnings("serial")
public class LocalizedCheckBoxMenuItem extends JCheckBoxMenuItem
{
    
    /**
     * Icon loader.
     */
    private static IconLoader iconLoader = new IconLoader();
    
    
    public LocalizedCheckBoxMenuItem(String name, String actionCommand, boolean useMnemonic)
    {
        this.setName(name);
        this.setText(MenuLocalizer.getString(name));
        if(useMnemonic)
        {
            this.setMnemonic(MenuLocalizer.getMnemonic(name).charAt(0));
        }
        this.setActionCommand (actionCommand);
    }    

    public LocalizedCheckBoxMenuItem(String name, String actionCommand)
    {
        this(name, actionCommand, false);
    }
    
    public LocalizedCheckBoxMenuItem(String name, String actionCommand, String iconName,  boolean useMnemonic)
    {
        this(name, actionCommand, useMnemonic);
        this.setIcon (iconLoader.load(iconName));
    }    

    public LocalizedCheckBoxMenuItem(String name, String actionCommand, String iconName)
    {
        this(name, actionCommand, iconName, true);
    }    
    
}
