
package org.openmarkov.core.gui.window.dt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NormalizeNullVectorException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.dialog.treeadd.IconFactory;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;

@SuppressWarnings("serial")
public class DecisionTreeNode extends JPanel
{
    /**
     * Container of SummaryBox' text or the variable icon
     */
    private JLabel                   leftLabel             = new JLabel ();
    /**
     * Container of leaf data: Potential description or value
     */
    private JLabel                   rightLabel            = new JLabel ();
    private ProbNode                 probNode              = null;
    private List<DecisionTreeBranch> branches              = null;
    private DecisionTreeBranch       parent                = null;
    private double[]                 marginalProbabilities = null;

    public DecisionTreeNode (DecisionTreeBranch parent, ProbNet probNet, List<Variable> variables)
    {
        this.parent = parent;
        this.add (leftLabel, BorderLayout.WEST);
        this.add (rightLabel, BorderLayout.CENTER);
        setBackground (Color.white);
        Variable variable = variables.remove (0);
        probNode = probNet.getProbNode (variable);
        branches = new LinkedList<> ();
        if (probNode.getNodeType () != NodeType.UTILITY)
        {
            for (State state : variable.getStates ())
            {
                branches.add (new DecisionTreeBranch (this, probNet, variable, state,
                                                      new ArrayList<> (variables)));
            }
        }
        else
        {
            branches.add (new DecisionTreeBranch (probNet, variables));
        }
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
    public List<DecisionTreeBranch> getChildren ()
    {
        return branches;
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
            for (DecisionTreeBranch branch : branches)
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
            for (DecisionTreeBranch branch : branches)
            {
                sumUtility += branch.getUtility ();
            }
            utility = sumUtility;
        }
        return utility;
    }

    public double[] getMarginalProbabilities ()
    {
        if(marginalProbabilities == null)
        {
            Potential potential = probNode.getPotentials ().get (0);
            EvidenceCase evidenceCase = getBranchStates ();
            try
            {
                List<Variable> variablesOfInterest = new ArrayList<> ();
                variablesOfInterest.add (probNode.getVariable ());
                List<TablePotential> projectedPotentials = potential.tableProject (evidenceCase, null);
                TablePotential marginalizedProbs = DiscretePotentialOperations.marginalize (projectedPotentials.get (0),
                                                                                            variablesOfInterest);
                marginalProbabilities = DiscretePotentialOperations.normalize (marginalizedProbs).values;
            }
            catch (NonProjectablePotentialException | WrongCriterionException | NormalizeNullVectorException e)
            {
                e.printStackTrace ();
            }
        }
        return marginalProbabilities;
    }

    public EvidenceCase getBranchStates ()
    {
        return parent.getBranchStates ();
    }
}
