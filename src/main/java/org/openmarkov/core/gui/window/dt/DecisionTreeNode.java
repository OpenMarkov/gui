/*
 * Copyright 2012 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.core.gui.window.dt;

import java.awt.Font;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;

import org.openmarkov.core.gui.dialog.treeadd.IconFactory;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.ProductPotential;
import org.openmarkov.core.model.network.potential.SumPotential;
import org.openmarkov.core.model.network.potential.TablePotential;

@SuppressWarnings("serial")
public class DecisionTreeNode extends DecisionTreeElement
{
    private ProbNode                  probNode              = null;
    private List<DecisionTreeElement> children              = null;
    private DecisionTreeElement        parent                = null;

    public DecisionTreeNode (ProbNode probNode)
    {
        this.probNode = probNode;
        children = new LinkedList<> ();
        leftLabel.setIcon (createNodeIcon (probNode));
    }

    /**
     * Returns the probNode.
     * @return the probNode.
     */
    public ProbNode getProbNode ()
    {
        return probNode;
    }

    /**
     * Returns the children.
     * @return the children.
     */
    public List<DecisionTreeElement> getChildren ()
    {
        return children;
    }

    /**
     * Create a new icon for a node of the ADD/Tree
     * @return
     */
    protected Icon createNodeIcon (ProbNode node)
    {
        Font textIconFont = new Font ("Helvetica", Font.BOLD, 15);
        Icon icon = null;
        switch (node.getNodeType ())
        {
            case CHANCE :
            {
                icon = IconFactory.createChanceIcon (node.getName (), textIconFont);
                break;
            }
            case DECISION :
            {
                icon = IconFactory.createDecisionIcon (node.getName (), textIconFont);
                break;
            }
            case UTILITY :
            {
                icon = IconFactory.createUtilityIcon (node.getName (), textIconFont);
                break;
            }
        }
        return icon;
    }

    public double getUtility ()
    {
        double utility = 0;
        if (probNode.getNodeType () == NodeType.DECISION)
        {
            double maxUtility = Double.NEGATIVE_INFINITY;
            for (DecisionTreeElement branch : children)
            {
                double branchUtility = branch.getUtility ();
                if (branchUtility > maxUtility)
                {
                    maxUtility = branchUtility;
                }
            }
            utility = maxUtility;
        }
        else if (probNode.getNodeType () == NodeType.CHANCE)
        {
            double sumUtility = 0;
            for (DecisionTreeElement child : children)
            {
                sumUtility += child.getUtility ();
            }
            utility = sumUtility;
        }else if (probNode.getNodeType () == NodeType.UTILITY)
        {
            Potential potential = probNode.getPotentials ().get (0);
            if(potential instanceof SumPotential)
            {
                double sumUtility = 0;
                for (DecisionTreeElement child : children)
                {
                    sumUtility += child.getUtility ();
                }
                utility = sumUtility;
            }else if(potential instanceof ProductPotential)
            {
                double productUtility = 1;
                for (DecisionTreeElement child : children)
                {
                    productUtility *= child.getUtility ();
                }
                utility = productUtility;
                
            }else if(potential instanceof TablePotential)
            {
                utility = ((TablePotential)potential).getValue (getBranchStates());
            }
        }
        
        return utility;
    }

    public EvidenceCase getBranchStates ()
    {
        return parent.getBranchStates ();
    } 

    public boolean isBestDecision (DecisionTreeElement branch)
    {
        boolean isBestDecision = false;
        if (probNode.getNodeType () == NodeType.DECISION)
        {
            isBestDecision = true;
            double thisUtility = branch.getUtility ();
            for (DecisionTreeElement otherBranch : children)
            {
                isBestDecision &= thisUtility >= otherBranch.getUtility ();
            }
        }        
        return isBestDecision;
    }

    @Override
    public void update (boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        if(probNode.getNodeType () == NodeType.UTILITY)
        {
            rightLabel.setText (" U ="+getUtility ());
        }
    }
    
    public double getScenarioProbability()
    {
    	double scenarioProbability = 0;
    	if(probNode.getNodeType() == NodeType.CHANCE)
    	{
	    	for(DecisionTreeElement child : children)
	    	{
	    		scenarioProbability +=child.getScenarioProbability();
	    	}
    	}else if(probNode.getNodeType() == NodeType.DECISION)
    	{
    		scenarioProbability = children.get(0).getScenarioProbability();
    	}
    	return scenarioProbability;
    }
    
    public void addChild(DecisionTreeElement child)
    {
        child.setParent (this);
        children.add (child);
    }

    @Override
    public String toString ()
    {
        StringBuilder builder = new StringBuilder ();
        builder.append ("DecisionTreeNode [probNode=");
        builder.append (probNode.getName ());
        builder.append (", children=").append (children);
        builder.append ("]");
        return builder.toString ();
    }

    @Override
    public void setParent (DecisionTreeElement parent)
    {
        this.parent = parent;
    }
    
    
}
