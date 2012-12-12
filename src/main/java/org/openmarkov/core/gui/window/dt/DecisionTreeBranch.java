
package org.openmarkov.core.gui.window.dt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;

@SuppressWarnings("serial")
public class DecisionTreeBranch extends JPanel
{
    /**
     * Container of SummaryBox' text or the variable icon
     */
    private JLabel           leftLabel  = new JLabel ();
    /**
     * Container of leaf data: Potential description or value
     */
    private JLabel           rightLabel = new JLabel ();
    private Variable         branchVariable;
    private State            branchState;
    private DecisionTreeNode parent;
    private DecisionTreeNode child;
    private double           utility    = 0;
    private double           probability = -1;

    public DecisionTreeBranch (DecisionTreeNode parent,
                               ProbNet probNet,
                               Variable branchVariable,
                               State branchState,
                               List<Variable> variables)
    {
        this.parent = parent;
        this.branchState = branchState;
        this.branchVariable = branchVariable;
        this.add (leftLabel, BorderLayout.WEST);
        this.add (rightLabel, BorderLayout.CENTER);
        setBackground (Color.white);
        if (!variables.isEmpty ())
        {
            this.child = new DecisionTreeNode (this, probNet, variables);
        }
        utility = getUtility();
        if(parent != null && parent.getProbNode ().getNodeType () == NodeType.CHANCE)
        {
            probability = getBranchProbability();
        }
        leftLabel.setText (getBranchDescriptiontHTML ());
    }

    public DecisionTreeBranch (ProbNet probNet, List<Variable> variables)
    {
        this (null, probNet, null, null, variables);
    }

    /**
     * Builds the text to be shown in the branch
     */
    public String getBranchDescriptiontHTML ()
    {
        StringBuilder txtLeft = new StringBuilder("<html><table border=1>");
        txtLeft.append ("<td align=center border=0>");
        if (branchVariable != null)
        {
            txtLeft.append (branchVariable.getName () + "=");
            txtLeft.append (branchState.getName ());
        }
        if(probability >= 0)
        {
            DecimalFormat df = new DecimalFormat("#.##");
            txtLeft.append (" P= " + df.format (probability));
        }
        txtLeft.append (" U= " + utility);
        txtLeft.append ("</td>");
        txtLeft.append ("</table></html>");
        return txtLeft.toString ();
    }

    public DecisionTreeNode getChild ()
    {
        return child;
    }
    
    public double getUtility ()
    {
        return (child != null)? child.getUtility () : 0;
    } 
    
    public double getBranchProbability ()
    {
        return parent.getMarginalProbabilities ()[branchVariable.getStateIndex (branchState)];
    }   
    
    public EvidenceCase getBranchStates()
    {
        EvidenceCase evidenceCase = (parent!=null)? new EvidenceCase(parent.getBranchStates()) : new EvidenceCase();
        if(branchVariable != null)
        {
            try
            {
                evidenceCase.addFinding (new Finding(branchVariable, branchState));
            }
            catch (InvalidStateException | IncompatibleEvidenceException e)
            {
                e.printStackTrace();
            }
        }
        return evidenceCase;
    }
}
