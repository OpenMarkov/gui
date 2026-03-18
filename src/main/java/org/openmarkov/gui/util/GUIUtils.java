/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.util;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * This class implements various methods that are used by the rest of classes of
 * the application.
 *
 * @author jmendoza
 * @version 1.2 jlgozalo - 10/05/10 - set private constructor, remove functions.
 * @version 1.3 jrico - Added showDialog.
 * and fix warnings
 */
public final class GUIUtils {
    
    /**
     * private constructor for a class with only static methods
     */
    private GUIUtils() {
    }
    
    /**
     * Returns the window that owns the component.
     *
     * @param component component whose top level window will be returned.
     *
     * @return the top level ancestor of the component, if it exists and it is a
     * Window instance, of null if it isn't a window instance.
     */
    public static Window getOwner(JComponent component) {
        Container ancestor = component.getTopLevelAncestor();
        if (ancestor instanceof Window window) {
            return window;
        }
        return null;
    }
    
    /**
     * Checks if the mouse event hasn't key modifiers.
     *
     * @param e mouse event information.
     *
     * @return true if the mouse event hasn't modifiers; otherwise, false.
     */
    public static boolean noMouseModifiers(MouseEvent e) {
        return ((e.getModifiersEx() & 0xF) == 0);
    }
    
    public static void showDialog(@NotNull JDialog dialog) {
        var parent = dialog.getParent();
        if (parent != null) {
            dialog.setLocationRelativeTo(dialog.getParent());
        }
        dialog.setVisible(true);
    }
    
    public static void addHoverComponentLogger(Component target) {
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (!(event instanceof MouseEvent me)) {
                return;
            }
            if (me.getID() != MouseEvent.MOUSE_MOVED) {
                return;
            }
            Component source = (Component) me.getSource();
            if (!isChildOf(target, source)) {
                return;
            }
            Point eventPos = me.getPoint();
            Point screenPos = source.getLocationOnScreen();
            Point windowPos = target.getLocationOnScreen();
            Point posInTarget = new Point(screenPos.x + eventPos.x - windowPos.x, screenPos.y + eventPos.y - windowPos.y);

            Component hoveredComponent = SwingUtilities.getDeepestComponentAt(target, posInTarget.x, posInTarget.y);
            if (hoveredComponent == null) {
                return;
            }
            System.out.println("Hovering: " + hoveredComponent.getClass() + " with name " + hoveredComponent.getName() + " at " + posInTarget);
        }, AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }
    
    // Helper to check if a component belongs to our parent dialog
    private static boolean isChildOf(Component parent, Component child) {
        while (child != null) {
            if (child == parent) return true;
            child = child.getParent();
        }
        return false;
    }
}
