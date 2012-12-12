package org.openmarkov.core.gui.window.dt;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;

import org.openmarkov.core.exception.WrongGraphStructureException;
import org.openmarkov.core.inference.PartialOrder;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;

@SuppressWarnings("serial")
public class DecisionTreePanel  extends JScrollPane
{
    protected JTree jTree;    

    public DecisionTreePanel(ProbNet probNet)
    {
        List<Variable> variables = getPartiallySortedVariables (probNet);
        List<Variable> utilityVariables = getSortedUtilityVariables(probNet);
        //variables.addAll (utilityVariables);
        
        DecisionTreeBranch root = new DecisionTreeBranch (probNet, variables);
        
        DecisionTreeModel model = new DecisionTreeModel (root);
        jTree = new JTree (model);
        jTree.getSelectionModel ().setSelectionMode (TreeSelectionModel.SINGLE_TREE_SELECTION);
        // Allows JTree nodes to accept CR/LF codes
        jTree.setShowsRootHandles (true);
        jTree.setRowHeight (0);
        jTree.setCellRenderer (new DecisionTreeCellRenderer ());        
        setViewportView (jTree);
    }
    
    private List<Variable> getSortedUtilityVariables (ProbNet probNet)
    {
        ProbNet utilityProbNet = probNet.copy ();
        // Remove all nodes that aren't utility nodes
        for(ProbNode node : utilityProbNet.getProbNodes ())
        {
            if(node.getNodeType () != NodeType.UTILITY)
            {
                utilityProbNet.removeProbNode (node);
            }
        }
        // Look for leaves
        List<ProbNode> leaves = getLeaves (utilityProbNet);
        // if there is more than one leave, create a new super value node
        if(leaves.size () > 1)
        {
            Variable svVariable = new Variable ("Global Utility");
            ProbNode svNode = utilityProbNet.addVariable (svVariable, NodeType.UTILITY);
            for(ProbNode leaf : leaves)
            {
                utilityProbNet.addLink (leaf, svNode, true);
            }
        }
        
        List<Variable> sortedUtilityVariables = new ArrayList<> ();
        while(!utilityProbNet.getProbNodes ().isEmpty ())
        {
            for(ProbNode leaf : getLeaves (utilityProbNet))
            {
                sortedUtilityVariables.add (leaf.getVariable ());
                utilityProbNet.removeProbNode (leaf);
            }
        }
        return sortedUtilityVariables;
    }
    
    private List<ProbNode> getLeaves(ProbNet probNet)
    {
        List<ProbNode> leaves = new ArrayList<> ();
        for(ProbNode node : probNet.getProbNodes ())
        {
            if(node.getNode ().getChildren ().isEmpty ())
            {
                leaves.add (node);
            }
        }
        return leaves;
    }

    private ProbNode findSuperValueNode (ProbNet probNet)
    {
        ProbNode svNode = null;
        for (ProbNode node : probNet.getProbNodes (NodeType.UTILITY))
        {
            if (node.getNode ().getChildren ().isEmpty ())
            {
                svNode = node;
            }
        }
        return svNode;
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
}
