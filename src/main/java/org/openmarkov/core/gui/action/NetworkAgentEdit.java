package org.openmarkov.core.gui.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.openmarkov.core.action.SimplePNEdit;
import org.openmarkov.core.action.StateAction;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.operation.PotentialOperations;
/**
 * <code>NetworkAgentEdit</code> is a simple edit that allow modify
 * the agents of a network
 * @author myebra
 *
 */
public class NetworkAgentEdit extends SimplePNEdit {
	
	private String newAgent;
	private int agentIndex;
	private StateAction stateAction;

	public NetworkAgentEdit(ProbNet probnet, StateAction stateAction, int agentIndex, String newAgent) {
		super(probnet);
		this.newAgent = newAgent;
		this.stateAction = stateAction;
	}

	@Override
	public void doEdit() throws DoEditException, NotEnoughMemoryException {
		switch (stateAction){
		case ADD:
			//probNet.
			break;
		case REMOVE:
			break;
		case DOWN:
			
			break;
		case UP:
			break;
		case RENAME:
			break;
			
		}
		
		
	}

}
