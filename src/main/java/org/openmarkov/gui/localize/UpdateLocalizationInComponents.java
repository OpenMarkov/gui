package org.openmarkov.gui.localize;

import org.openmarkov.core.localize.StringDatabase;
import org.openmarkov.gui.component.LastRecentFilesMenuItem;
import org.openmarkov.gui.menutoolbar.toolbar.ZoomComboBox;
import org.openmarkov.gui.window.mdi.MDIMenu;
import org.openmarkov.gui.window.message.NonEditableTextArea;

import javax.swing.*;
import java.awt.*;

public class UpdateLocalizationInComponents {
    
    /**
     * Method to change behaviours in a container using Java Reflection API. All
     * the different objects must comply with a strictly naming convention to
     * prevent string not to be updated properly. When the objects will be
     * created by programmers, the "name" property of the object must be set as
     * "ContainerOwner.ComponentVariableName" where the ContainerOwner is the
     * name of the container where the component belongs to. All other objects
     * must implement there own listeners.
     *
     * @param c the container to be updated
     */
    public static void allComponentsUpdateSetText(Container c) {
        StringDatabase stringDatabase = StringDatabase.getUniqueInstance();
        String temp = "";
        Component[] listComponents = c.getComponents();
        for (Component item : listComponents) {
            if (item instanceof JButton) {
                if (!((JButton) item).getText().equals("")) {
                    temp = ((JButton) item).getName() + ".Text.Label";
                    ((JButton) item).setText(stringDatabase.getString(temp));
                }
            } else if (item instanceof JDialog) {
                temp = ((JDialog) item).getName() + ".Title.Text";
                ((JDialog) item).setTitle(stringDatabase.getString(temp));
                allComponentsUpdateSetText((Container) item);
            } else if (item instanceof JFrame) {
                temp = ((JFrame) item).getName() + ".Title.Text";
                ((JFrame) item).setTitle(stringDatabase.getString(temp));
                allComponentsUpdateSetText((Container) item);
            } else if (item instanceof JLabel) {
                temp = ((JLabel) item).getName() + ".Text";
                ((JLabel) item).setText(stringDatabase.getString(temp));
            } else if (item instanceof MDIMenu) {
                // doNothing
            } else if (item instanceof JMenu) {
                temp = ((JMenu) item).getName() + ".Label";
                ((JMenu) item).setText(stringDatabase.getString(temp));
                temp = ((JMenu) item).getName() + ".Mnemonic";
                ((JMenu) item).setMnemonic(stringDatabase.getString(temp).charAt(0));
                allComponentsUpdateSetText((Container) item);
            } else if (item instanceof JMenuItem) {
                temp = ((JMenuItem) item).getName() + ".Label";
                ((JMenuItem) item).setText(stringDatabase.getString(temp));
                temp = ((JMenuItem) item).getName() + ".Mnemonic";
                ((JMenuItem) item).setMnemonic(stringDatabase.getString(temp).charAt(0));
            } else if (item instanceof NonEditableTextArea) {
                // doNothing
            } else if (item instanceof JPanel) {
                allComponentsUpdateSetText((Container) item);
            } else if (item instanceof JTextArea) {
                temp = ((JTextArea) item).getName() + ".Text";
                ((JTextArea) item).setText(stringDatabase.getString(temp));
            } else if (item instanceof JTextField) {
                temp = ((JTextField) item).getName() + ".Text";
                ((JTextField) item).setText(stringDatabase.getString(temp));
            } else if (item instanceof ZoomComboBox) {
                temp = (String) ((ZoomComboBox) item).getSelectedItem();
                ((ZoomComboBox) item).setSelectedItem(temp);
            } else if (item instanceof Container) {
                allComponentsUpdateSetText((Container) item);
            } else {
                // do nothing for non registered objects as
                // those objects must implement the listener.
                // if required this method can be expanded
            }
        } // end-for
        if (c instanceof JMenu) {
            temp = ((JMenu) c).getName() + ".Label";
            ((JMenu) c).setText(stringDatabase.getString(temp));
            // extract JMenuItems
            int itemCount = ((JMenu) c).getItemCount();
            for (int i = 0; i < itemCount; i++) {
                Component item = ((JMenu) c).getItem(i);
                if (item instanceof JMenu) {
                    temp = ((JMenu) item).getName() + ".Label";
                    ((JMenu) item).setText(stringDatabase.getString(temp));
                    temp = ((JMenu) item).getName() + ".Mnemonic";
                    ((JMenu) item).setMnemonic(stringDatabase.getString(temp).charAt(0));
                    allComponentsUpdateSetText((Container) item);
                } else if (item instanceof LastRecentFilesMenuItem) {
                    // do not change
                } else if (item instanceof JMenuItem) {
                    temp = ((JMenuItem) item).getName() + ".Label";
                    ((JMenuItem) item).setText(stringDatabase.getString(temp));
                    temp = ((JMenuItem) item).getName() + ".Mnemonic";
                    ((JMenuItem) item).setMnemonic(stringDatabase.getString(temp).charAt(0));
                } else {
                    // only JSeparators are entering here!!!!
                }
            }
        }
    }
    
}
