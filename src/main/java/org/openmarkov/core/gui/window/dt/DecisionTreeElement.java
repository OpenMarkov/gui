package org.openmarkov.core.gui.window.dt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openmarkov.core.model.network.EvidenceCase;

@SuppressWarnings("serial")
public abstract class DecisionTreeElement extends JPanel
{
    /**
     * Container of SummaryBox' text or the variable icon
     */
    protected JLabel           leftLabel  = new JLabel ();
    /**
     * Container of leaf data: Potential description or value
     */
    protected JLabel           rightLabel = new JLabel ();
    
    public DecisionTreeElement()
    {
        super (new BorderLayout ());        
        this.add (leftLabel, BorderLayout.WEST);
        this.add (rightLabel, BorderLayout.CENTER);
        setBackground (Color.white);
    }
    
    public abstract List<DecisionTreeElement> getChildren();
    
    public abstract double getUtility ();
    
    public abstract EvidenceCase getBranchStates ();
    
    public abstract void update (boolean selected,
                           boolean expanded,
                           boolean leaf,
                           int row,
                           boolean hasFocus);
    
    public abstract double getScenarioProbability();
}
