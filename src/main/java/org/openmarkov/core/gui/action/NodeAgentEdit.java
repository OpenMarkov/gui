package org.openmarkov.core.gui.action;

import org.openmarkov.core.action.SimplePNEdit;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.StringWithProperties;

@SuppressWarnings("serial")
public class NodeAgentEdit extends SimplePNEdit{
	
	private StringWithProperties  currentAgent;
	private StringWithProperties newAgent;
	private ProbNode probNode;

	public NodeAgentEdit (ProbNode probNode, StringWithProperties agent) {
		super(probNode.getProbNet());
		this.probNode = probNode;
		this.currentAgent = probNode.getAgent();
		this.newAgent = agent;
	}
	@Override
	public void doEdit() throws DoEditException, NotEnoughMemoryException {
		// TODO Auto-generated method stub
		probNode.setAgent(newAgent);
	}
	
	@Override
	public void undo() {
		super.undo();
		probNode.setAgent(currentAgent);
	}

}
