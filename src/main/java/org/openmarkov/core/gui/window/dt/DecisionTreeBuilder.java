/*
 * Copyright 2013 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.core.gui.window.dt;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.openmarkov.core.exception.WrongGraphStructureException;
import org.openmarkov.core.inference.PartialOrder;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.SumPotential;

public class DecisionTreeBuilder
{
    public static DecisionTreeElement buildDecisionTree(ProbNet probNet)
    {
        ProbNet dtProbNet = probNet.copy ();
        Variable svVariable = getSuperValueVariable(dtProbNet);
        List<Variable> variables = getPartiallySortedVariables (dtProbNet);
        variables.add (svVariable);
        
        DecisionTreeElement root = new DecisionTreeBranch (probNet);
        
        Stack<DecisionTreeElement> treeStack = new Stack<> ();
        
        treeStack.push (root);
        
        while(!treeStack.isEmpty ())
        {
            DecisionTreeElement treeElement = treeStack.pop ();
            if(treeElement instanceof DecisionTreeNode)
            {
                ProbNode probNode = ((DecisionTreeNode)treeElement).getProbNode ();
                if (probNode.getNodeType () != NodeType.UTILITY)
                {
                    // Get next variable in the list
                    Variable variable = probNode.getVariable ();                
                    for (State state : variable.getStates ())
                    {
                        DecisionTreeBranch treeBranch = new DecisionTreeBranch ((DecisionTreeNode)treeElement, probNet, variable, state);
                        ((DecisionTreeNode)treeElement).addChild (treeBranch);
                        treeStack.push (treeBranch);
                    }
                }
                else // Build Utility node tree
                {
                    for (Node parentNode : probNode.getNode ().getParents ())
                    {
                        ProbNode parentProbNode = (ProbNode)parentNode.getObject ();
                        if(parentProbNode.getNodeType () == NodeType.UTILITY)
                        {                        
                            DecisionTreeNode treeNode =  new DecisionTreeNode ((DecisionTreeNode)treeElement, parentProbNode);
                            ((DecisionTreeNode)treeElement).addChild (treeNode);
                            treeStack.push (treeNode);
                        }
                    }            
                }                
            }else if (treeElement instanceof DecisionTreeBranch)
            {
                Variable branchVariable =  ((DecisionTreeBranch)treeElement).getBranchVariable ();
                Variable variable = null;
                if(branchVariable == null)
                {
                    variable = variables.get (0);
                }else{
                    variable = variables.get (variables.indexOf (branchVariable) + 1);
                }
                    
                DecisionTreeNode child = new DecisionTreeNode (treeElement, probNet.getProbNode (variable));
                ((DecisionTreeBranch)treeElement).setChild (child);
                treeStack.push (child);
            }
        }
        
        return root;
    }
    
    private static Variable getSuperValueVariable (ProbNet probNet)
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
    
    private static List<ProbNode> getUtilityLeaves(ProbNet probNet)
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
    
    private static List<Variable> getPartiallySortedVariables(ProbNet probNet)
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
