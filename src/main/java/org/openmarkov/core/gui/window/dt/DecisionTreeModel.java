package org.openmarkov.core.gui.window.dt;

import java.util.HashSet;
import java.util.Set;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class DecisionTreeModel implements TreeModel
{
    private Set<TreeModelListener> listeners;
    private DecisionTreeBranch root;
    
    /**
     * Constructor for DecisionTreeModel.
     */
    public DecisionTreeModel (DecisionTreeBranch root)
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
        Object child = null;
        if(parent instanceof DecisionTreeNode)
        {
            child = ((DecisionTreeNode)parent).getChildren ().get (index);
        }else if(parent instanceof DecisionTreeBranch)
        {
            child = ((DecisionTreeBranch)parent).getChild ();
        }
        return child;
    }

    @Override
    public int getChildCount (Object parent)
    {
        int count = 0;
        if(parent instanceof DecisionTreeNode)
        {
            count = ((DecisionTreeNode)parent).getChildren ().size ();
        }else if(parent instanceof DecisionTreeBranch)
        {
            count = (((DecisionTreeBranch) parent).getChild () == null)? 0 : 1;
        }        
        return count;
    }

    @Override
    public int getIndexOfChild (Object parent, Object child)
    {
        int index = 0;
        if(parent instanceof DecisionTreeNode)
        {
            index = ((DecisionTreeNode)parent).getChildren ().indexOf (child);
        }
        else if ((parent instanceof DecisionTreeBranch)
                 && ((DecisionTreeBranch) parent).getChild ().equals (child))
        {
            index = 0;
        }        
        return index;
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
