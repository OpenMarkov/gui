package org.openmarkov.core.gui.window.dt;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

public class DecisionTreeCellRenderer implements
TreeCellRenderer
{

    @Override
    public Component getTreeCellRendererComponent (JTree tree,
                                                   Object object,
                                                   boolean selected,
                                                   boolean expanded,
                                                   boolean leaf,
                                                   int row,
                                                   boolean hasFocus)
    {
        return (Component)object;
    }
}
