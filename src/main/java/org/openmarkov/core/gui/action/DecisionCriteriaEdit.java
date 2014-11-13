
package org.openmarkov.core.gui.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.jmx.Agent;
import org.openmarkov.core.action.SimplePNEdit;
import org.openmarkov.core.action.StateAction;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.model.network.Criterion;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.StringWithProperties;

@SuppressWarnings("serial")
public class DecisionCriteriaEdit extends SimplePNEdit
{
    private String                     criterionName;
    private StateAction                stateAction;
    // private StringsWithProperties lastAgents;
    private List<Criterion> lastCriteria;
    private Object[][]                 dataTable;
    private String 	                   newName;

    public DecisionCriteriaEdit (ProbNet probnet,
                                 StateAction stateAction,
                                 String newName,
                                 String agentName,
                                 Object[][] dataTable)
    {
        super (probnet);
        // probNet.getPNESupport().setWithUndo(true);
        this.criterionName = agentName;
        this.stateAction = stateAction;
        this.newName = newName;
        if (probnet.getAgents () != null)
        {
            this.lastCriteria = new ArrayList<Criterion> (probnet.getDecisionCriteria ());
        }
        else
        {
            this.lastCriteria = probnet.getDecisionCriteria ();
        }
        this.dataTable = dataTable;
    }

    @Override
    public void doEdit ()
        throws DoEditException
    {
        // StringsWithProperties agents = probNet.getAgents();
        List<Criterion> criteria = probNet.getDecisionCriteria ();
        Criterion criterion = null;
        switch (stateAction)
        {
            case ADD :
                if (criteria == null)
                {
                    // agents = new StringsWithProperties();
                    criteria = new ArrayList<Criterion> ();
                }
                criterion = new Criterion (criterionName);
                // agents.put(agentName);
                criteria.add (criterion);
                probNet.setDecisionCriteria (criteria);
                break;
            case REMOVE :
                for (Criterion criterio : criteria)
                {
                    if (criterio.getCriterionName().equals (criterionName))
                    {
                        criterion = criterio;
                    }
                }
                criteria.remove (criterion);
                // TODO assign criteria to node
                // it is also necessary to delete this criteria from the node it
                // was assigned to
                /*
                 * if (criteria != null) { for (Node node :
                 * probNet.getNodes()) { if
                 * (node.getVariable().getDecisionCriteria
                 * ().getString().equals(criteriaName)) {
                 * node.getVariable().setDecisionCriteria(null); } } }
                 */
                if (criteria.size () == 0)
                {
                    criteria = null;
                }
                probNet.setDecisionCriteria (criteria);
                break;
            case DOWN :
                // StringsWithProperties newAgentsDown = new
                // StringsWithProperties();
                ArrayList<Criterion> newCriteriasDown = new ArrayList<Criterion> ();
                for (int i = 0; i < dataTable.length; i++)
                {
                    // newAgentsDown.put((String)dataTable[i][0]);
                    newCriteriasDown.add (new Criterion ((String) dataTable[i][0]));
                }
                probNet.setDecisionCriteria (newCriteriasDown);
                break;
            case UP :
                // StringsWithProperties newAgentsUp = new
                // StringsWithProperties();
                ArrayList<Criterion> newCriteriasUp = new ArrayList<Criterion> ();
                for (int i = 0; i < dataTable.length; i++)
                {
                    // newAgentsUp.put((String)dataTable[i][0]);
                    newCriteriasUp.add (new Criterion ((String) dataTable[i][0]));
                }
                probNet.setDecisionCriteria (newCriteriasUp);
                break;
            case RENAME :
                // agents.rename(agentName, newName);
                // StringsWithProperties newAgentsRename = new
                // StringsWithProperties();
                ArrayList<Criterion> newCriteriasRename = new ArrayList<Criterion> ();
                for (int i = 0; i < dataTable.length; i++)
                {
                    // newAgentsRename.put((String)dataTable[i][0]);
                    newCriteriasRename.add (new Criterion ((String) dataTable[i][0]));

                }
                // We substitute the new name in the nodes they had that criterion
                for(Node node : probNet.getNodes()){
                	// Only Utility nodes have criterion
                	if(node.getNodeType() == NodeType.UTILITY &&
                			// we get the utility nodes with no empty criterion
                			node.getVariable().getDecisionCriterion()!= null && 
                			// we get the nodes with the same criterion as criterionName
                			node.getVariable().getDecisionCriterion().getCriterionName().equals(criterionName)){
                			// We change the name of the criterion in those nodes
                			node.getVariable().getDecisionCriterion().setCriterionName(newName);
                		
                	}
                }

                
                probNet.setDecisionCriteria (newCriteriasRename);
                break;
        }
    }

    @Override
    public void undo ()
    {
        super.undo ();
        probNet.setDecisionCriteria (lastCriteria);
        // TODO restore criteria in nodes
    }
}
