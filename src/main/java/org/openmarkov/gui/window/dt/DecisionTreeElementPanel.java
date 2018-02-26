/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.window.dt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class DecisionTreeElementPanel  extends JPanel
{
    /**
     * Container of SummaryBox' text or the variable icon
     */
    protected JLabel           leftLabel  = new JLabel ();
    /**
     * Container of leaf data: Potential description or value
     */
    protected JLabel           rightLabel = new JLabel ();
    
    protected List<DecisionTreeElementPanel> children;
    
    public DecisionTreeElementPanel()
    {
        super (new BorderLayout ());        
        this.add (leftLabel, BorderLayout.WEST);
        this.add (rightLabel, BorderLayout.CENTER);
        setBackground (Color.white);
        
        children = new ArrayList<> ();
    }  
    
    public abstract void update (boolean selected,
                                 boolean expanded,
                                 boolean leaf,
                                 int row,
                                 boolean hasFocus);

    /**
     * Returns the children.
     * @return the children.
     */
    public List<DecisionTreeElementPanel> getChildren ()
    {
        return children;
    }
    
    public void addChild(DecisionTreeElementPanel child)
    {
        children.add (child);
    }
    
    
    
}
