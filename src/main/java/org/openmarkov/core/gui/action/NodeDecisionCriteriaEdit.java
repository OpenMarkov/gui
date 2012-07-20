package org.openmarkov.core.gui.action;

import org.openmarkov.core.action.SimplePNEdit;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.StringWithProperties;

public class NodeDecisionCriteriaEdit extends SimplePNEdit{
	
	private StringWithProperties  currentDecisionCriteria;
	private StringWithProperties newDecisionCriteria;
	private ProbNode probNode;

	public NodeDecisionCriteriaEdit (ProbNode probNode, StringWithProperties decisionCriteria) {
		super(probNode.getProbNet());
		this.probNode = probNode;
		this.currentDecisionCriteria = probNode.getVariable().getDecisionCriteria();
		this.newDecisionCriteria = decisionCriteria;
	}
	@Override
	public void doEdit() throws DoEditException, NotEnoughMemoryException {
		probNode.getVariable().setDecisionCriteria(newDecisionCriteria);
	}
	
	@Override
	public void undo() {
		super.undo();
		probNode.getVariable().setDecisionCriteria(currentDecisionCriteria);
	}

}

