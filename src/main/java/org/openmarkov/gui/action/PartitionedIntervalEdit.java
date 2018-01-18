package org.openmarkov.gui.action;

import org.openmarkov.core.action.SimplePNEdit;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.Node;

@SuppressWarnings("serial")
public class PartitionedIntervalEdit extends SimplePNEdit{

	private PartitionedInterval currentPartitionedInterval;
	
	private PartitionedInterval newPartitionedInterval;
	
	private Node node = null;
		
	public PartitionedIntervalEdit(Node node, PartitionedInterval newPartitionedInterval) {
		super(node.getProbNet());
		this.node = node;
		this.newPartitionedInterval = newPartitionedInterval;
		this.currentPartitionedInterval = node.getVariable().getPartitionedInterval();
	}

	@Override
	public void doEdit() throws DoEditException {
		node.getVariable().setPartitionedInterval(newPartitionedInterval);
		
	}
	@Override
	public void undo(){
		super.undo();
		node.getVariable().setPartitionedInterval(currentPartitionedInterval);
	}

}
