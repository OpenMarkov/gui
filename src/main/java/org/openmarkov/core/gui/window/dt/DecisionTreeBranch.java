
package org.openmarkov.core.gui.window.dt;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;

@SuppressWarnings("serial")
public class DecisionTreeBranch extends DecisionTreeElement
{

    private Variable         branchVariable;
    private State            branchState;
    private DecisionTreeNode parent;
    private DecisionTreeNode child;
    private ProbNet			 probNet;

    public DecisionTreeBranch (DecisionTreeNode parent,
                               ProbNet probNet,
                               Variable branchVariable,
                               State branchState,
                               List<Variable> variables)
    {
        this.parent = parent;
        this.probNet = probNet;
        this.branchState = branchState;
        this.branchVariable = branchVariable;
        if (!variables.isEmpty ())
        {
            this.child = new DecisionTreeNode (this, probNet, variables);
        }
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
        DecimalFormat df = new DecimalFormat("0.00");        
        StringBuilder txtLeft = new StringBuilder("<html><table border=1>");
        if(parent != null && ((DecisionTreeNode)parent).getProbNode ().getNodeType () == NodeType.DECISION)
        {
            if( ((DecisionTreeNode)parent).isBestDecision(this)) {
                txtLeft.append ("<td width=10px bgcolor=red border=0></td>");
            }
            else {
                txtLeft.append ("<td width=10px border=0></td>");                   
            }
        }
        txtLeft.append ("<td align=center border=0>");
        if (branchVariable != null)
        {
            txtLeft.append (branchVariable.getName () + "=");
            txtLeft.append (branchState.getName ());
        }
        if(parent != null && parent.getProbNode ().getNodeType () == NodeType.CHANCE)
        {
            txtLeft.append (" P=" + df.format (getBranchProbability()));
        }
        txtLeft.append (" U=" + df.format (child.getUtility ()));
        txtLeft.append ("</td>");
        txtLeft.append ("</table></html>");
        return txtLeft.toString ();
    }

    public List<DecisionTreeElement> getChildren ()
    {
        List<DecisionTreeElement> children = new LinkedList<> ();
        children.add (child);
        return children;
    }
    
    public double getUtility ()
    {
        double utility = (child != null)? child.getUtility () : 0;
        if(parent != null && ((DecisionTreeNode)parent).getProbNode ().getNodeType () == NodeType.CHANCE)
        {
            utility *= getBranchProbability ();
        }
        return utility;
    } 
    
    public double getBranchProbability ()
    {
    	double parentScenarioProb = parent.getScenarioProbability();
    	return (parentScenarioProb!=0)?getScenarioProbability()/parentScenarioProb:0;
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
    
    public double getScenarioProbability()
    {
    	double scenarioProbability = 1;    	
    	if(child.getProbNode().getNodeType() == NodeType.UTILITY)
    	{
        	EvidenceCase evidenceCase = getBranchStates();
	    	for(Finding finding : evidenceCase.getFindings())
	    	{
	    		ProbNode probNode = probNet.getProbNode(finding.getVariable());
	    		if(probNode.getNodeType() == NodeType.CHANCE)
	    		{
	    			Potential potential = probNode.getPotentials().get(0);
	    			scenarioProbability *= potential.getProbability(evidenceCase);
	    		}
	    	}
    	}else
    	{
    		scenarioProbability = child.getScenarioProbability();
    	}
        return scenarioProbability;
    }       

    @Override
    public void update (boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        leftLabel.setText (getBranchDescriptiontHTML ());
    }
}
