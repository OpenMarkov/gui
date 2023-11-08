/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.gui.dialog.common;

import org.openmarkov.core.action.PotentialChangeEdit;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.potential.FunctionPotential;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

@SuppressWarnings("serial") @PotentialPanelPlugin(potentialType = "Function") public class FunctionPotentialPanel
		extends PotentialPanel {

	/**
	 * Panel with the function
	 */


	protected FunctionPanel functionPanel;

	private Node node = null;
	private FunctionPotential potential = null;

	public FunctionPotentialPanel(Node node) {
		super();
		setData(node);
		initComponents();
	}

	private void initComponents() {

		setLayout(new BorderLayout());
		JPanel northPanel = new JPanel();
		northPanel.setBorder(new TitledBorder("Function"));
		northPanel.setPreferredSize(new Dimension(800, 100));
		// String function= (potential.getCovariates()==null)?null:potential.getCovariates()[0];
		List variables = node.getPotentials().get(0).getVariables();
		functionPanel = new FunctionPanel(variables.subList(1,variables.size()), ((FunctionPotential) potential).getFunction());
		northPanel.add(functionPanel, BorderLayout.NORTH);
		add(northPanel, BorderLayout.NORTH);
	}


	@Override
	public void setData(Node node) {
		this.node = node;
		this.potential = (FunctionPotential) node.getPotentials().get(0);
	}

	public boolean saveChanges() {
		FunctionPotential newPotential = (FunctionPotential) this.potential.copy();

		newPotential.setFunction(functionPanel.getFunction());

		PotentialChangeEdit potentialChangeEdit = new PotentialChangeEdit(node.getProbNet(), this.potential,
				newPotential);
		try {
			node.getProbNet().doEdit(potentialChangeEdit);
		} catch (ConstraintViolationException | NonProjectablePotentialException | WrongCriterionException | DoEditException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override public void close() {

	}

}
