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
	private StringsWithProperties lastAgents;
	private Object [][]dataTable;

	public NetworkAgentEdit(ProbNet probnet, StateAction stateAction, String newName, String agentName, Object [][]dataTable) {
		super(probnet);
		probNet.getPNESupport().setWithUndo(true);
		this.agentName = agentName;
		this.stateAction = stateAction;
		this.newName = newName;
		this.lastAgents = probnet.getAgents();
		this.dataTable = dataTable;
	}

	@Override
	public void doEdit() throws DoEditException, NotEnoughMemoryException {
		StringsWithProperties agents = probNet.getAgents();
		switch (stateAction){
		case ADD:
			if (agents == null) {
				agents = new StringsWithProperties();
			}
			agents.put(agentName);
			probNet.setAgents(agents);
			break;
		case REMOVE:
			 agents.remove(agentName);
			 probNet.setAgents(agents);
			break;
		case DOWN:
			StringsWithProperties newAgentsDown = new StringsWithProperties();
			for (int i = 0; i < dataTable.length; i++) {
				newAgentsDown.put((String)dataTable[i][0]);
			}
			probNet.setAgents(newAgentsDown);
			break;
		case UP:
			StringsWithProperties newAgentsUp = new StringsWithProperties();
			for (int i = 0; i < dataTable.length; i++) {
				newAgentsUp.put((String)dataTable[i][0]);
			}
			probNet.setAgents(newAgentsUp);
			break;
		case RENAME:
			//agents.rename(agentName, newName);
			StringsWithProperties newAgentsRename = new StringsWithProperties();
			for (int i = 0; i < dataTable.length; i++) {
				newAgentsRename.put((String)dataTable[i][0]);
			}
			probNet.setAgents(newAgentsRename);
			break;
			
		}
		
		
	}
	@Override
	public void undo() {
		super.undo();
		probNet.setAgents(lastAgents);
	}

}
