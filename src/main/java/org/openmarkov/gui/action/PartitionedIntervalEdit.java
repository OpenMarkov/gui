/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.action;

import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.action.SimplePNEdit;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNet;

@SuppressWarnings("serial") public class PartitionedIntervalEdit extends SimplePNEdit {

	private PartitionedInterval currentPartitionedInterval;

	private PartitionedInterval newPartitionedInterval;

	private Node node = null;

	public PartitionedIntervalEdit(Node node, PartitionedInterval newPartitionedInterval) {
		super(node.getProbNet());
		this.node = node;
		this.newPartitionedInterval = newPartitionedInterval;
		this.currentPartitionedInterval = node.getVariable().getPartitionedInterval();
	}

	@Override public void doEdit() {
		node.getVariable().setPartitionedInterval(newPartitionedInterval);

	}
	
	@Override public void doEdit(ProbNet probNet) throws DoEditException.ConstraintViolated {
		PNEdit.startEdit(this, probNet);
		this.doEdit();
		PNEdit.endEdit(this);
	}
	
	@Override public void undo() {
		super.undo();
		node.getVariable().setPartitionedInterval(currentPartitionedInterval);
	}

}
