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

	// Default increment between discretized intervals
	private final int increment = 2;

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

		switch (stateAction) {
		case ADD:

			PartitionedInterval newPartitionedInterval = getNewPartitionedInterval();
			link.addRevealingInterval(newPartitionedInterval);

			break;
		case REMOVE: {
			link.getRevealingIntervals().remove(rowSelected);
		}
			break;
		case MODIFYVALUEINTERVAL:
		
		

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
		return new PartitionedInterval(false,
				Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false);
//		Variable var = new Variable("NUM");
//		PartitionedInterval interval = var.getPartitionedInterval();
//		double limits[] = interval.getLimits();
//		double newLimits[] = new double[limits.length + 1];
//		boolean belongsToLeftSide[] = interval.getBelongsToLeftSide();
//		boolean newBelongsToLeftSide[] = new boolean[limits.length + 1];
//		for (int i = 0; i < limits.length; i++) {
//			newLimits[i] = limits[i];
//			newBelongsToLeftSide[i] = belongsToLeftSide[i];
//		}
//		newLimits[limits.length] = interval.getMax() + increment;
//		newBelongsToLeftSide[limits.length] = false;
//		return new PartitionedInterval(newLimits, newBelongsToLeftSide);
	}

}
