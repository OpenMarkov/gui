/*
 * Copyright 2013 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
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
import org.openmarkov.core.model.network.type.DecisionAnalysisNetworkType;
import org.openmarkov.core.model.network.type.InfluenceDiagramType;

public class DecisionTreeBuilder
{
    public static DecisionTreeElement buildDecisionTree (ProbNet probNet)
    {
        DecisionTreeElement root = null;
        if (probNet.getNetworkType () instanceof InfluenceDiagramType)
        {
            root = buildDecisionTreeFromID (probNet);
        }
        else if (probNet.getNetworkType () instanceof DecisionAnalysisNetworkType)
        {
            root = buildDecisionTreeFromDAN (probNet);
        }
        return root;
    }

    /**
     * Builds a decision tree from a decision analysis network
     * @param probNet
     * @return
     */    
    private static DecisionTreeElement buildDecisionTreeFromDAN (ProbNet probNet)
    {
        ProbNet dtProbNet = probNet.copy ();
        ProbNode svNode = getSuperValueNode (dtProbNet);
        List<Variable> alwaysObservedVariables = getAlwaysObservedVariables (dtProbNet);
        DecisionTreeElement root = null;
        if(!alwaysObservedVariables.isEmpty ())
        {
            Variable firstAlwaysObservedVariable = alwaysObservedVariables.get (0);
            ProbNode firstAlwaysObservedNode = probNet.getProbNode (firstAlwaysObservedVariable);
            DecisionTreeNode treeNode = new DecisionTreeNode (null, firstAlwaysObservedNode);
            dtProbNet.removeProbNode (firstAlwaysObservedNode);
            for (State state : firstAlwaysObservedVariable.getStates ())
            {
                DecisionTreeBranch treeBranch = new DecisionTreeBranch (
                                                                        treeNode,
                                                                        probNet,
                                                                        firstAlwaysObservedVariable, 
                                                                        state);
                treeNode.addChild (treeBranch);
                treeBranch.setChild ((DecisionTreeNode)buildDecisionTreeFromDAN (dtProbNet));
            }            
        }else
        {
            // find decisions without parents
            List<ProbNode> parentlessDecisions = getParentlessDecisions (dtProbNet);
            Stack<DecisionTreeElement> treeStack = new Stack<> ();
            treeStack.push (root);
            while (!treeStack.isEmpty ())
            {
                DecisionTreeElement treeElement = treeStack.pop ();
                if (treeElement instanceof DecisionTreeNode)
                {
                    ProbNode probNode = ((DecisionTreeNode) treeElement).getProbNode ();
                    if (probNode.getNodeType () != NodeType.UTILITY)
                    {
                        // Get next variable in the list
                        Variable variable = probNode.getVariable ();
                        for (State state : variable.getStates ())
                        {
                            DecisionTreeBranch treeBranch = new DecisionTreeBranch (
                                                                                    (DecisionTreeNode) treeElement,
                                                                                    dtProbNet,
                                                                                    variable, state);
                            ((DecisionTreeNode) treeElement).addChild (treeBranch);
                            treeStack.push (treeBranch);
                        }
                    }
                    else
                    // Build Utility node tree
                    {
                        for (Node parentNode : probNode.getNode ().getParents ())
                        {
                            ProbNode parentProbNode = (ProbNode) parentNode.getObject ();
                            if (parentProbNode.getNodeType () == NodeType.UTILITY)
                            {
                                DecisionTreeNode treeNode = new DecisionTreeNode (
                                                                                  (DecisionTreeNode) treeElement,
                                                                                  parentProbNode);
                                ((DecisionTreeNode) treeElement).addChild (treeNode);
                                treeStack.push (treeNode);
                            }
                        }
                    }
                }
                else if (treeElement instanceof DecisionTreeBranch)
                {
                    Variable branchVariable = ((DecisionTreeBranch) treeElement).getBranchVariable ();
                    Variable variable = null;
                    if (branchVariable == null)
                    {
                        variable = alwaysObservedVariables.get (0);
                    }
                    else
                    {
                        variable = alwaysObservedVariables.get (alwaysObservedVariables.indexOf (branchVariable) + 1);
                    }
                    DecisionTreeNode child = new DecisionTreeNode (treeElement,
                                                                   dtProbNet.getProbNode (variable));
                    ((DecisionTreeBranch) treeElement).setChild (child);
                    treeStack.push (child);
                }
            }
        }
        return root;
    }

    /**
     * Generates a list of decision nodes that don't have parent decisions
     * @param probNet
     * @return
     */
    private static List<ProbNode> getParentlessDecisions (ProbNet probNet)
    {
        List<ProbNode> parentlessDecisions = new ArrayList<> ();
        for (ProbNode probNode : probNet.getProbNodes (NodeType.DECISION))
        {
            boolean hasParentDecisions = false;
            Stack<ProbNode> parentNodes = new Stack<> ();
            parentNodes.push (probNode);
            while (!hasParentDecisions && !parentNodes.isEmpty ())
            {
                ProbNode node = parentNodes.pop ();
                for (Node parent : node.getNode ().getParents ())
                {
                    ProbNode parentNode = (ProbNode) parent.getObject ();
                    hasParentDecisions |= parentNode.getNodeType () == NodeType.DECISION;
                    parentNodes.push (parentNode);
                }
            }
            if (!hasParentDecisions)
            {
                parentlessDecisions.add (probNode);
            }
        }
        return parentlessDecisions;
    }

    /**
     * Gets the list of always-observed-variables in the DAN 
     * @param dtProbNet
     * @return
     */
    private static List<Variable> getAlwaysObservedVariables (ProbNet dtProbNet)
    {
        List<Variable> alwaysObservedVariables = new ArrayList<> ();
        for (Variable variable : dtProbNet.getVariables ())
        {
            if (variable.isAlwaysObserved ())
            {
                alwaysObservedVariables.add (variable);
            }
        }
        return alwaysObservedVariables;
    }

    /**
     * Builds a decision tree from an influence diagram
     * @param probNet
     * @return
     */
    private static DecisionTreeElement buildDecisionTreeFromID (ProbNet probNet)
    {
        ProbNet dtProbNet = probNet.copy ();
        ProbNode svNode = getSuperValueNode (dtProbNet);
        List<Variable> variables = getPartiallySortedVariables (dtProbNet);
        DecisionTreeElement root = new DecisionTreeBranch (dtProbNet);
        Stack<DecisionTreeElement> treeStack = new Stack<> ();
        treeStack.push (root);
        List<DecisionTreeBranch> leaves = new ArrayList<> ();
        // Build tree with decision & utility nodes
        while (!treeStack.isEmpty ())
        {
            DecisionTreeElement treeElement = treeStack.pop ();
            // If a node
            if (treeElement instanceof DecisionTreeNode)
            {
                ProbNode probNode = ((DecisionTreeNode) treeElement).getProbNode ();
                // Get next variable in the list
                Variable variable = probNode.getVariable ();
                for (State state : variable.getStates ())
                {
                    DecisionTreeBranch treeBranch = new DecisionTreeBranch (
                                                                            (DecisionTreeNode) treeElement,
                                                                            dtProbNet, variable,
                                                                            state);
                    ((DecisionTreeNode) treeElement).addChild (treeBranch);
                    treeStack.push (treeBranch);
                }
            }
            // If a branch
            else if (treeElement instanceof DecisionTreeBranch)
            {
                Variable branchVariable = ((DecisionTreeBranch) treeElement).getBranchVariable ();
                Variable childVariable = null;
                // If this is the root 
                if (branchVariable == null)
                {
                    childVariable = variables.get (0);
                }
                // If this neither the root nor a leaf                
                else if (variables.indexOf (branchVariable) + 1 < variables.size ())
                {
                    childVariable = variables.get (variables.indexOf (branchVariable) + 1);
                }
                // If this is a leaf
                else
                {
                    leaves.add ((DecisionTreeBranch) treeElement);
                }
                if (childVariable != null)
                {
                    DecisionTreeNode child = new DecisionTreeNode (
                                                                   treeElement,
                                                                   dtProbNet.getProbNode (childVariable));
                    ((DecisionTreeBranch) treeElement).setChild (child);
                    treeStack.push (child);
                }
            }
        }

        // Add utility trees at the tip of each leaf
        addUtilityNodes (leaves, svNode);
        return root;
    }

    /**
     * Looks for the super value node. If there is none, it creates it.
     * @param probNet
     * @return
     */
    private static ProbNode getSuperValueNode (ProbNet probNet)
    {
        ProbNode svNode = null;
        // Look for leaves
        List<ProbNode> leaves = getUtilityLeaves (probNet);
        // if there is more than one leave, create a new super value node
        if (leaves.size () > 1)
        {
            Variable svVariable = new Variable ("Global Utility");
            svNode = probNet.addVariable (svVariable, NodeType.UTILITY);
            List<Variable> leafVariables = new ArrayList<> (leaves.size ());
            for (ProbNode leafNode : leaves)
            {
                leafVariables.add (leafNode.getVariable ());
            }
            svNode.addPotential (new SumPotential (leafVariables, PotentialRole.UTILITY));
            for (ProbNode leaf : leaves)
            {
                probNet.addLink (leaf, svNode, true);
            }
        }
        else if (leaves.size () == 1)
        {
            svNode = leaves.get (0);
        }
        return svNode;
    }

    private static List<ProbNode> getUtilityLeaves (ProbNet probNet)
    {
        List<ProbNode> leaves = new ArrayList<> ();
        for (ProbNode node : probNet.getProbNodes ())
        {
            if (node.getNodeType () == NodeType.UTILITY
                && node.getNode ().getChildren ().isEmpty ())
            {
                leaves.add (node);
            }
        }
        return leaves;
    }

    /**
     * Using PartialOrder generates a sorted plain list of decision and chance variables
     * @param probNet
     * @return
     */
    private static List<Variable> getPartiallySortedVariables (ProbNet probNet)
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
    
    /**
     * Adds a utility tree at the tip of each leaf
     * @param leaves
     * @param svNode
     */
    private static void addUtilityNodes (List<DecisionTreeBranch> leaves, ProbNode svNode)
    {
        // Add utility nodes
        for (DecisionTreeBranch leaf : leaves)
        {
            DecisionTreeNode child = new DecisionTreeNode (leaf, svNode);
            leaf.setChild (child);
            Stack<DecisionTreeNode> utilityTreeStack = new Stack<> ();
            utilityTreeStack.push (child);
            while (!utilityTreeStack.isEmpty ())
            {
                DecisionTreeNode utilityTreeNode = utilityTreeStack.pop ();
                ProbNode utilityNode = utilityTreeNode.getProbNode ();
                for (Node parentNode : utilityNode.getNode ().getParents ())
                {
                    ProbNode parentProbNode = (ProbNode) parentNode.getObject ();
                    if (parentProbNode.getNodeType () == NodeType.UTILITY)
                    {
                        DecisionTreeNode treeNode = new DecisionTreeNode (utilityTreeNode,
                                                                          parentProbNode);
                        utilityTreeNode.addChild (treeNode);
                        utilityTreeStack.push (treeNode);
                    }
                }
            }
        }
    }    
}
