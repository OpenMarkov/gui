/*
 * Copyright 2012 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.window.dt;

import java.util.HashSet;
import java.util.Set;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class DecisionTreeModel implements TreeModel
{
    private Set<TreeModelListener> listeners;
    private DecisionTreeElement root;
    
    /**
     * Constructor for DecisionTreeModel.
     */
    public DecisionTreeModel (DecisionTreeElement root)
    {
        super ();
        this.listeners = new HashSet<> ();
        this.root = root;
    }

    @Override
    public void addTreeModelListener (TreeModelListener listener)
    {
        listeners.add (listener);        
    }

    @Override
    public Object getChild (Object parent, int index)
    {
        return ((DecisionTreeElement)parent).getChildren ().get (index);
    }

    @Override
    public int getChildCount (Object parent)
    {
        return ((DecisionTreeElement)parent).getChildren ().size ();
    }

    @Override
    public int getIndexOfChild (Object parent, Object child)
    {
        return ((DecisionTreeElement)parent).getChildren ().indexOf (child);
    }

    @Override
    public Object getRoot ()
    {
        return root;
    }

    @Override
    public boolean isLeaf (Object node)
    {
        return getChildCount (node) == 0;
    }

    @Override
    public void removeTreeModelListener (TreeModelListener listener)
    {
        listeners.remove (listener);
    }

    @Override
    public void valueForPathChanged (TreePath path, Object newValue)
    {
        // TODO Auto-generated method stub
        
    }
}
