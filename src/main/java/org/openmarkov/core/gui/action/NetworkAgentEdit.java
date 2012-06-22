package org.openmarkov.core.gui.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.openmarkov.core.action.SimplePNEdit;
import org.openmarkov.core.action.StateAction;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.AdditionalProperties;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.StringWithProperties;
import org.openmarkov.core.model.network.StringsWithProperties;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.operation.PotentialOperations;
/**
 * <code>NetworkAgentEdit</code> is a simple edit that allow modify
 * the agents of a network
 * @author myebra
 *
 */
@SuppressWarnings("serial")
public class NetworkAgentEdit extends SimplePNEdit {
	
	private String agentName;
	private String newName;
	private int agentIndex;
	private StateAction stateAction;
	//private StringsWithProperties lastAgents;
	private ArrayList<StringWithProperties> lastAgents;
	private Object [][]dataTable;
	private ArrayList<ProbNode> oldNodes;

	public NetworkAgentEdit(ProbNet probnet, StateAction stateAction, String newName, String agentName, Object [][]dataTable) {
		super(probnet);
		//probNet.getPNESupport().setWithUndo(true);
		this.agentName = agentName;
		this.stateAction = stateAction;
		this.newName = newName;
		if(probnet.getAgents() != null){
			//StringsWithProperties agents =  probnet.getAgents();
			ArrayList<StringWithProperties> agents =  probnet.getAgents();
			//this.lastAgents = probnet.getAgents().copy();
			this.lastAgents = (ArrayList<StringWithProperties>) probnet.getAgents().clone();
		}else {
			this.lastAgents = probnet.getAgents();
		}
		this.dataTable = dataTable;
		this.oldNodes = (ArrayList<ProbNode>) probNet.getProbNodes().clone();
	}

	@Override
	public void doEdit() throws DoEditException, NotEnoughMemoryException {
		//StringsWithProperties agents = probNet.getAgents();
		ArrayList<StringWithProperties> agents = probNet.getAgents();
		StringWithProperties agent = null;
		switch (stateAction){
		case ADD:
			if (agents == null) {
				//agents = new StringsWithProperties();
				agents = new ArrayList<StringWithProperties>();
			}
			agent = new StringWithProperties(agentName);
			//agents.put(agentName);
			agents.add(agent);
			probNet.setAgents(agents);
			break;
		case REMOVE:
			for (StringWithProperties agente : agents) {
				if (agente.getString().equals(agentName)) {
					agent = agente;
				}
			}
			agents.remove(agent);
			//it is also necessary to delete this agent from the node it was assigned to
			if (agent != null) {
				for (ProbNode node : probNet.getProbNodes()) {
					if (node.getVariable().getAgent().getString().equals(agentName)) {
						node.getVariable().setAgent(null);
					} 
				}
			}
			
			if (agents.size() == 0) {
				agents = null;
			}
			probNet.setAgents(agents);
			break;
		case DOWN:
			//StringsWithProperties newAgentsDown = new StringsWithProperties();
			ArrayList<StringWithProperties> newAgentsDown = new ArrayList<StringWithProperties>();
			for (int i = 0; i < dataTable.length; i++) {
				//newAgentsDown.put((String)dataTable[i][0]);
				newAgentsDown.add(new StringWithProperties((String)dataTable[i][0]));
			}
			probNet.setAgents(newAgentsDown);
			break;
		case UP:
			//StringsWithProperties newAgentsUp = new StringsWithProperties();
			ArrayList<StringWithProperties> newAgentsUp = new ArrayList<StringWithProperties>();
			for (int i = 0; i < dataTable.length; i++) {
				//newAgentsUp.put((String)dataTable[i][0]);
				newAgentsUp.add(new StringWithProperties((String)dataTable[i][0]));
			}
			probNet.setAgents(newAgentsUp);
			break;
		case RENAME:
			//agents.rename(agentName, newName);
			//StringsWithProperties newAgentsRename = new StringsWithProperties();
			ArrayList<StringWithProperties> newAgentsRename = new ArrayList<StringWithProperties>();
			for (int i = 0; i < dataTable.length; i++) {
				//newAgentsRename.put((String)dataTable[i][0]);
				newAgentsRename.add(new StringWithProperties((String)dataTable[i][0]));
			}
			probNet.setAgents(newAgentsRename);
			break;
			
		}
		
		
	}
	@Override
	public void undo() {
		super.undo();
		probNet.setAgents(lastAgents);
		//TODO restaurate agents in nodes
	}

}
