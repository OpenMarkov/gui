package org.openmarkov.core.gui.action;

import org.openmarkov.core.action.SimplePNEdit;
import org.openmarkov.core.action.StateAction;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;

@SuppressWarnings("serial")
public class RevelationConditionEdit extends SimplePNEdit {

	/***
	 * Object which stores the revelation conditions
	 */
	private Link link;

	/**
	 * The action to carry out
	 */
	private StateAction stateAction;

	private double newValue;

	/**
	 * index of the row selected
	 */
	private int rowSelected;
	private boolean isLower;



	public RevelationConditionEdit(Link link, StateAction stateAction, int row,
			double newValue, boolean isLower) {
		super(((ProbNode) link.getNode1().getObject()).getProbNet());
		this.link = link;
		this.stateAction = stateAction;
		this.rowSelected = row;
		this.newValue = newValue;
		this.isLower = isLower;
	}

	@Override
	public void doEdit() throws DoEditException, NotEnoughMemoryException {
		System.out.println(rowSelected);
		switch (stateAction) {
		case ADD:

			PartitionedInterval newPartitionedInterval = getNewPartitionedInterval();
			link.addRevealingInterval(newPartitionedInterval);

			break;
		case REMOVE: {
			link.getRevealingIntervals().remove(rowSelected);
		}
			break;
		case MODIFYVALUEINTERVAL: {
			PartitionedInterval currentPartitionedInterval = link
					.getRevealingIntervals().get(rowSelected);
			int intervalIndex = isLower ? 0 : 1;
			currentPartitionedInterval.getLimits()[intervalIndex] = newValue;
		}
			break;

		case MODIFYDELIMITERINTERVAL: {
			PartitionedInterval currentPartitionedInterval = link
					.getRevealingIntervals().get(rowSelected);
			int intervalIndex = isLower ? 0 : 1;
			currentPartitionedInterval.getBelongsToLeftSide()[intervalIndex] = !currentPartitionedInterval
					.getBelongsToLeftSide(intervalIndex);
		}
			break;

		}

	}

	/**
	 * This method add a new default subInterval, in the current
	 * PartitionedInterval object
	 * 
	 * @return The PartitionedInterval object with a new default subInterval
	 */

	private PartitionedInterval getNewPartitionedInterval() {
		if (link.getRevealingIntervals().isEmpty()) {
			return new PartitionedInterval(false, Double.NEGATIVE_INFINITY,
					Double.POSITIVE_INFINITY, false);
		} else {
			PartitionedInterval interval = link.getRevealingIntervals().get(
					link.getRevealingIntervals().size() - 1);
			return new PartitionedInterval(false, interval.getLimit(1),
					Double.POSITIVE_INFINITY, false);
		}
	}

}
