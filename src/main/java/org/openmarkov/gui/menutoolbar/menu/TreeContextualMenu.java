package org.openmarkov.gui.menutoolbar.menu;

import org.openmarkov.gui.localize.LocalizedMenuItem;
import org.openmarkov.gui.menutoolbar.common.ActionCommands;
import org.openmarkov.gui.menutoolbar.common.MenuItemNames;

import javax.swing.*;
import java.awt.event.ActionListener;

public class TreeContextualMenu extends ContextualMenu {
    /**
     * Static field for serializable class.
     */
    private static final long serialVersionUID = 8556550111033250304L;
    /**
     * Object that represents the item 'Expand next'.
     */
    private JMenuItem expandNextMenuItem = null;
    /**
     * Object that represents the item 'Expand all'.
     */
    private JMenuItem expandAllMenuItem = null;
    /**
     * Object that represents the item 'Open network'.
     */
    private JMenuItem openNetworkMenuItem = null;
    /**
     * Object that represents the item 'Show CEP'.
     */
    private JMenuItem showCEPMenuItem = null;

    public TreeContextualMenu(ActionListener newListener) {
        super(newListener);
        initialize();

    }

    /**
     * Construct the menu from the items
     */
    private void initialize() {
        add(getExpandNextMenuItem());
        add(getExpandAllMenuItem());
        add(getOpenNetworkMenuItem());
        add(getShowCEPMenuItem());
    }

    /**
     * This method initializes expandNextMenuItem.
     *
     * @return a new 'Expand next' menu item.
     */
    private JMenuItem getExpandNextMenuItem() {
        if (expandNextMenuItem == null) {
            expandNextMenuItem = new LocalizedMenuItem(MenuItemNames.TREE_EXPAND_NEXT_MENUITEM, ActionCommands.TREE_EXPAND_NEXT);
            expandNextMenuItem.addActionListener(listener);
        }
        return expandNextMenuItem;
    }

    /**
     * This method initialises expandAllMenuItem.
     *
     * @return a new 'Expand all' menu item.
     */
    private JMenuItem getExpandAllMenuItem() {
        if (expandAllMenuItem == null) {
            expandAllMenuItem = new LocalizedMenuItem(MenuItemNames.TREE_EXPAND_ALL_MENUITEM, ActionCommands.TREE_EXPAND_ALL);
            expandAllMenuItem.addActionListener(listener);
        }
        return expandAllMenuItem;
    }

    /**
     * This method initialises openNetworkMenuItem.
     *
     * @return a new 'Open network' menu item.
     */
    private JMenuItem getOpenNetworkMenuItem() {
        if (openNetworkMenuItem == null) {
            openNetworkMenuItem = new LocalizedMenuItem(MenuItemNames.TREE_OPEN_NETWORK_MENUITEM, ActionCommands.TREE_OPEN_NETWORK);
            openNetworkMenuItem.addActionListener(listener);
        }
        return openNetworkMenuItem;
    }

    /**
     * This method initialises showCEPMenuItem.
     *
     * @return a new 'Show CEP' menu item.
     */
    private JMenuItem getShowCEPMenuItem() {
        if (showCEPMenuItem == null) {
            showCEPMenuItem = new LocalizedMenuItem(MenuItemNames.TREE_SHOW_CEP_MENUITEM, ActionCommands.TREE_SHOW_CEP);
            showCEPMenuItem.addActionListener(listener);
        }
        return showCEPMenuItem;
    }

    /**
     * Returns the component that corresponds to an action command.
     *
     * @param actionCommand action command that identifies the component.
     * @return a components identified by the action command.
     */
    @Override
    protected JComponent getJComponentActionCommand(String actionCommand) {
        JComponent component = null;
        switch (actionCommand) {
            case ActionCommands.TREE_EXPAND_NEXT:
                component = expandNextMenuItem;
                break;
            case ActionCommands.TREE_EXPAND_ALL:
                component = expandAllMenuItem;
                break;
            case ActionCommands.TREE_OPEN_NETWORK:
                component = openNetworkMenuItem;
                break;
            case ActionCommands.TREE_SHOW_CEP:
                component = showCEPMenuItem;
                break;
        }
        return component;
    }
}
