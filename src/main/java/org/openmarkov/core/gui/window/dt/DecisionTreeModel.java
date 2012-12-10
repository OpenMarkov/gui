package org.openmarkov.core.gui.window.dt;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.openmarkov.core.model.network.Variable;

public class DecisionTreeModel implements TreeModel
{
    private Set<TreeModelListener> listeners;
    private List<Variable> variables;
    
    /**
     * Constructor for DecisionTreeModel.
     */
    public DecisionTreeModel (List<Variable> variables)
    {
        super ();
        this.variables = variables;
        this.listeners = new HashSet<> ();
    }

    @Override
    public void addTreeModelListener (TreeModelListener listener)
    {
        listeners.add (listener);        
    }

    @Override
    public Object getChild (Object parent, int index)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getChildCount (Object parent)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getIndexOfChild (Object parent, Object child)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Object getRoot ()
    {
        return variables.get (0);
    }

    @Override
    public boolean isLeaf (Object node)
    {
        // TODO Auto-generated method stub
        return false;
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
