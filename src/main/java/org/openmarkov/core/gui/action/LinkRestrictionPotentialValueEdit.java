package org.openmarkov.core.gui.action;

import java.util.ArrayList;

import org.openmarkov.core.action.SimplePNEdit;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;

@SuppressWarnings("serial")
public class LinkRestrictionPotentialValueEdit extends SimplePNEdit {

	/**
	 * The column of the table where is the potential
	 */
	private int col;
	/**
	 * The row of the table where is the potential
	 */
	private int row;
	/**
	 * The new value of the potential
	 */
	private Integer newValue;

	/***
	 * The link with the link restriction potential.
	 */
	private Link link;
	/****
	 * The parent node of the link.
	 */
	private ProbNode node1;
	/****
	 * The child node of the link.
	 */
	private ProbNode node2;

	/**
	 * the table potential before the edit
	 */
	private double[] lastTable;

	/***
	 * the table potential after the edit
	 */
	private double[] newTable;

	/**
	 * The potential of the link restriction
	 */
	private TablePotential tablePotential;

	public LinkRestrictionPotentialValueEdit(Link link, Integer newValue,
			int row, int col) {
		super(((ProbNode) link.getNode1().getObject()).getProbNet());
		this.link = link;
		this.node1 = (ProbNode) link.getNode1().getObject();
		this.node2 = (ProbNode) link.getNode2().getObject();
		this.col = col;
		this.row = row;
		this.tablePotential = (TablePotential) link.getRestrictionsPotential();
		this.newValue = newValue;
		this.lastTable = ((TablePotential) link.getRestrictionsPotential())
				.getValues().clone();
	}

	@Override
	public void doEdit() throws DoEditException, NotEnoughMemoryException {
		int numStates2=node2.getVariable().getNumStates();
		State state1 = node1.getVariable().getStates()[col - 1];
		State state2 = node2.getVariable().getStates()[numStates2-row];
		link.setCompatibilityValue(state1, state2,
				this.newValue.intValue());
		newTable = ((TablePotential) link.getRestrictionsPotential()).values
				.clone();
	}

	public void redo() {
		this.setTypicalRedo(false);
		super.redo();
		tablePotential.setValues(newTable);

	}

	public void undo() {
		super.undo();
		tablePotential.setValues(lastTable);
	}

}
