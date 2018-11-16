package org.openmarkov.gui.menutoolbar.menu;

import org.openmarkov.core.model.network.Node;
import org.openmarkov.gui.graphic.VisualNode;
import org.openmarkov.gui.localize.LocalizedMenuItem;
import org.openmarkov.gui.menutoolbar.common.ActionCommands;
import org.openmarkov.gui.menutoolbar.common.MenuItemNames;
import org.openmarkov.gui.window.edition.EditorPanel;

import javax.swing.*;
import java.awt.event.ActionListener;

public class TreeContextualMenu extends ContextualMenu {
    /**
     * Static field for serializable class.
     */
    private static final long serialVersionUID = 8556550111033250304L;
    /**
     * Object that represents the item 'Expand N'.
     */
    private JMenuItem expandNMenuItem = null;
    /**
     * Object that represents the item 'Expand all'.
     */
    private JMenuItem expandAllMenuItem = null;

    public TreeContextualMenu(ActionListener newListener) {
        super(newListener);
        initialize();

    }

    /**
     * Construct the menu from the items
     */
    private void initialize() {
        add(getExpandNMenuItem());
        add(getExpandAllMenuItem());
    }

    /**
     * This method initializes cutMenuItem.
     *
     * @return a new 'Cut' menu item.
     */
    private JMenuItem getExpandNMenuItem() {
        if (expandNMenuItem == null) {
            expandNMenuItem = new LocalizedMenuItem(MenuItemNames.TREE_EXPAND_N_MENUITEM, ActionCommands.TREE_EXPAND_N);
            expandNMenuItem.addActionListener(listener);
        }
        return expandNMenuItem;
    }

    /**
     * This method initialises copyMenuItem.
     *
     * @return a new 'Copy' menu item.
     */
    private JMenuItem getExpandAllMenuItem() {
        if (expandAllMenuItem == null) {
            expandAllMenuItem = new LocalizedMenuItem(MenuItemNames.TREE_EXPAND_ALL_MENUITEM, ActionCommands.TREE_EXPAND_ALL);
            expandAllMenuItem.addActionListener(listener);
        }
        return expandAllMenuItem;
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
            case ActionCommands.TREE_EXPAND_N:
                component = expandNMenuItem;
                break;
            case ActionCommands.TREE_EXPAND_ALL:
                component = expandAllMenuItem;
                break;
        }
        return component;
    }
}
