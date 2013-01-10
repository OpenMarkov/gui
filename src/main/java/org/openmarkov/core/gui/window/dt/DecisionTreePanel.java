/*
 * Copyright 2012 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.window.dt;

import java.awt.Graphics2D;

import javax.swing.JTree;

import org.openmarkov.core.gui.window.edition.ZoomablePanel;
import org.openmarkov.core.model.network.ProbNet;

@SuppressWarnings("serial")
public class DecisionTreePanel extends ZoomablePanel
{
    protected JTree jTree;    
      
    public DecisionTreePanel(ProbNet probNet)
    {
        DecisionTreeElement root = DecisionTreeBuilder.buildDecisionTree (probNet); 
        DecisionTreeModel model = new DecisionTreeModel (root);
        jTree = new DecisionTree (model, this);
        for (int i = 0; i < jTree.getRowCount (); i++)
        {
            jTree.expandRow (i);
        }        
        setViewportView (jTree);
    }
    
    @Override
    protected double[] getBounds (Graphics2D graphics)
    {
        return new double[4];
    }
 
}
