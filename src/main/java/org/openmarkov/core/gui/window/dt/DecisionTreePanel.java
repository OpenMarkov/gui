package org.openmarkov.core.gui.window.dt;

import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;

import org.openmarkov.core.exception.WrongGraphStructureException;
import org.openmarkov.core.gui.window.edition.ZoomablePanel;
import org.openmarkov.core.inference.PartialOrder;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.SumPotential;

@SuppressWarnings("serial")
public class DecisionTreePanel  extends ZoomablePanel
{
    protected JTree jTree;    
      
    public DecisionTreePanel(ProbNet probNet)
    {
        ProbNet dtProbNet = probNet.copy ();
        Variable svVariable = getSuperValueVariable(dtProbNet);
        List<Variable> variables = getPartiallySortedVariables (dtProbNet);
        variables.add (svVariable);
        
        DecisionTreeBranch root = new DecisionTreeBranch (dtProbNet, variables);
        DecisionTreeModel model = new DecisionTreeModel (root);
        jTree = new DecisionTree (model, this);
        for (int i = 0; i < jTree.getRowCount (); i++)
        {
            jTree.expandRow (i);
        }        
        setViewportView (jTree);
        MouseListener ml = new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		int newX = (int)(e.getX() * zoom.getZoom());
        		int newY = (int)(e.getY() * zoom.getZoom());
        		e.translatePoint(newX - e.getX(), newY - e.getY()); 
        		super.mouseClicked(e);
        	}


        	@Override
        	public void mousePressed(MouseEvent e) {
        		int newX = (int)(e.getX() * zoom.getZoom());
        		int newY = (int)(e.getY() * zoom.getZoom());
        		e.translatePoint(newX - e.getX(), newY - e.getY()); 	
        		super.mouseClicked(e);
        	}


        	@Override
        	public void mouseReleased(MouseEvent e) {
        		int newX = (int)(e.getX() * zoom.getZoom());
        		int newY = (int)(e.getY() * zoom.getZoom());
        		e.translatePoint(newX - e.getX(), newY - e.getY()); 
        		super.mouseClicked(e);
        	}        	
		};
		jTree.addMouseListener(ml);         
    }
    
    
    private Variable getSuperValueVariable (ProbNet probNet)
    {
        Variable svVariable = null;
        // Look for leaves
        List<ProbNode> leaves = getUtilityLeaves (probNet);
        // if there is more than one leave, create a new super value node
        if(leaves.size () > 1)
        {
            svVariable = new Variable ("Global Utility");
            ProbNode svNode = probNet.addVariable (svVariable, NodeType.UTILITY);
            List<Variable> leafVariables = new ArrayList<> (leaves.size ());
            for(ProbNode leafNode : leaves)
            {
                leafVariables.add(leafNode.getVariable ());
            }
            svNode.addPotential (new SumPotential (leafVariables, PotentialRole.UTILITY));
            for(ProbNode leaf : leaves)
            {
                probNet.addLink (leaf, svNode, true);
            }
        }else if (leaves.size () == 1)
        {
            svVariable = leaves.get (0).getVariable ();
        }

        return svVariable;
    }
    
    private List<ProbNode> getUtilityLeaves(ProbNet probNet)
    {
        List<ProbNode> leaves = new ArrayList<> ();
        for(ProbNode node : probNet.getProbNodes ())
        {
            if (node.getNodeType () == NodeType.UTILITY
                && node.getNode ().getChildren ().isEmpty ())
            {
                leaves.add (node);
            }
        }
        return leaves;
    } 
    
    private List<Variable> getPartiallySortedVariables(ProbNet probNet)
    {
        List<Variable> variables = null;
        PartialOrder partialOrder = null;
        try
        {
            partialOrder = new PartialOrder (probNet);
        }
        catch (WrongGraphStructureException e)
        {
            e.printStackTrace ();
        }
        variables = new ArrayList<Variable> (partialOrder.getNumVariables ());
        for (List<Variable> variableSubList : partialOrder.getOrder ())
        {
            variables.addAll (variableSubList);
        }
        return variables;
    }
    
 
    
    @Override
    protected double[] getBounds (Graphics2D graphics)
    {
        return new double[4];
    }



 
}
