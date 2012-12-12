
package org.openmarkov.core.gui.window.dt;

import java.awt.BorderLayout;
import java.awt.Color;

import org.openmarkov.core.gui.window.mdi.FrameContentPanel;
import org.openmarkov.core.model.network.ProbNet;

@SuppressWarnings("serial")
public class DecisionTreeWindow extends FrameContentPanel
{
    private String  title   = null;

    public DecisionTreeWindow (ProbNet probNet)
    {
        setLayout(new BorderLayout());
        title  = probNet.getName () + " DT";
        DecisionTreePanel decisionTreePanel = new DecisionTreePanel (probNet);
        add (decisionTreePanel, BorderLayout.CENTER);
        setBackground (Color.blue);
    }

    @Override
    public String getTitle ()
    {
        return title;
    }

    @Override
    public void close ()
    {
        // TODO Auto-generated method stub
    }

}
