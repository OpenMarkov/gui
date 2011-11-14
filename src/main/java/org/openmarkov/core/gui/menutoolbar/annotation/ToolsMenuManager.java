package org.openmarkov.core.gui.menutoolbar.annotation;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.openmarkov.core.gui.localize.LocalizedMenuItem;
import org.openmarkov.plugin.PluginLoader;
import org.openmarkov.plugin.service.FilterIF;
import org.openmarkov.plugin.service.PluginLoaderIF;

public class ToolsMenuManager
{
    private PluginLoaderIF pluginsLoader; 
    
    /**
     * Constant that defines the menu 'Tools'.
     */
    public static final String TOOLS_MENU = "Tools";

    /**
     * Constructor for ToolsMenuManager.
     */
    public ToolsMenuManager ()
    {
        super ();
        this.pluginsLoader = new PluginLoader ();
    }    
    
    /**
     * Finds a tools menu item by name. 
     * @param name of the tools menu item.
     * @return a tools menu item.
     */
    public final Class<?> findToolsMenuItemsByName (String name)
    {
        try
        {
            List<Class<?>> plugins = findAllToolsMenuItems ();
            for (Class<?> plugin : plugins) {
                ToolsMenuItem lAnnotation = plugin.getAnnotation (ToolsMenuItem.class);
                if (lAnnotation.name ().equals (name))
                    return plugin;
            }
        }
        catch (Exception e) {}
        return null;
    }
    
  
    /**
     * Finds all learning tools menu items. 
     * @return a list of tools menu items.
     */
    public final  List<Class<?>> findAllToolsMenuItems ()
    {
        try
        {
            FilterIF filter = org.openmarkov.plugin.Filter.filter().toBeAnnotatedBy (ToolsMenuItem.class);
            return pluginsLoader.loadAllPlugins (filter);          
        }
        catch (Exception e) {}
        return null;
    }

    public ArrayList<JMenuItem> getMenuItems ()
    {
        ArrayList<JMenuItem> menuItems = new ArrayList<JMenuItem> ();
        try
        {
            List<Class<?>> plugins = findAllToolsMenuItems ();
            for (Class<?> plugin : plugins) {
                ToolsMenuItem lAnnotation = plugin.getAnnotation (ToolsMenuItem.class);
                JMenuItem menuItem = new LocalizedMenuItem (TOOLS_MENU + "." + lAnnotation.name (),
                                                            TOOLS_MENU + "." + lAnnotation.name ());
                menuItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_N,
                                                                 InputEvent.CTRL_DOWN_MASK));
            }
        }
        catch (Exception e) {}
        
        return menuItems;
    }    
}
