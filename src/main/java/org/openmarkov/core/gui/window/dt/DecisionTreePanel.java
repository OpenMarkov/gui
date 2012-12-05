
package org.openmarkov.core.gui.window.dt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.openmarkov.core.exception.WrongGraphStructureException;
import org.openmarkov.core.gui.dialog.treeadd.TreeADDController;
import org.openmarkov.core.gui.window.mdi.FrameContentPanel;
import org.openmarkov.core.inference.PartialOrder;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDBranch;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDPotential;

@SuppressWarnings("serial")
public class DecisionTreePanel extends FrameContentPanel
{
    private ProbNet           probNet = null;
    /**
     * The builder object of Tree - ADDs
     */
    private TreeADDController treeADDController;

    public DecisionTreePanel (ProbNet probNet)
    {
        this.probNet = probNet;
        PartialOrder partialOrder = null;
        try
        {
            partialOrder = new PartialOrder (probNet);
        }
        catch (WrongGraphStructureException e)
        {
            e.printStackTrace ();
        }
        List<Variable> variables = new ArrayList<Variable> (partialOrder.getNumVariables ());
        for (List<Variable> variableSubList : partialOrder.getOrder ())
        {
            variables.addAll (variableSubList);
        }
        setLayout (new BorderLayout ());
        Variable svVariable = findSuperValueNode (probNet).getVariable ();
        TreeADDPotential treeADDPotential = new TreeADDPotential (variables, PotentialRole.UTILITY,
                                                                  svVariable);
        // Remove first as the constructor of TreeADDPotential already creates
        // branches for the first variable
        variables.remove (0);
        List<TreeADDBranch> lastStepBranches = new ArrayList<> ();
        lastStepBranches.addAll (treeADDPotential.getBranches ());
        List<Variable> remainingVariables = new ArrayList<>(variables);
        for (Variable variable : variables)
        {
            List<TreeADDBranch> currentStepBranches = new ArrayList<> ();
                for (TreeADDBranch branch : lastStepBranches)
                {
                    TreeADDPotential potential = new TreeADDPotential (remainingVariables,
                                                                       variable,
                                                                       PotentialRole.UTILITY,
                                                                       svVariable);
                    branch.setPotential (potential);
                    currentStepBranches.addAll (potential.getBranches ());
                }
            lastStepBranches = currentStepBranches;
        }
        treeADDController = new TreeADDController (probNet, treeADDPotential);
        add (treeADDController, BorderLayout.CENTER);
        setBackground (Color.blue);
    }

    @Override
    public String getTitle ()
    {
        return probNet.getName () + " DT";
    }

    @Override
    public void close ()
    {
        // TODO Auto-generated method stub
    }

    public ProbNode findSuperValueNode (ProbNet probNet)
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
}
