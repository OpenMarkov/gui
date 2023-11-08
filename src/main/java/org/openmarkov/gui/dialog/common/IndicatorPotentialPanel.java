/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.dialog.common;

import org.openmarkov.core.action.PotentialChangeEdit;
import org.openmarkov.core.exception.*;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.potential.IndicatorPotential;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel for indicator potential in events.
 * @author cmyago
 * @version 1.0 FIXME resize panel elements
 */

@SuppressWarnings("serial") @PotentialPanelPlugin(potentialType = "Indicator") public class IndicatorPotentialPanel
		extends PotentialPanel {

	private JTextField tteTextField;
	private JTextField pOcurrenceTextField;

	private JComboBox<String> stateComboBox;
	private JSpinner valueSpinner;
	private Node node;

	public IndicatorPotentialPanel(Node node) {
		super();
		this.node = node;
		initComponents();
		setData(node);
	}

	private void initComponents() {
		setLayout(new BorderLayout());
		JPanel namelessPanel = new JPanel();
		namelessPanel.setLayout(new GridLayout(2,2,10, 20));
		namelessPanel.setBorder(new EmptyBorder(15, 400, 15, 400));
//		namelessPanel.setBorder(new EtchedBorder());

		tteTextField = new JTextField(15);

		JLabel tteLabel = new JLabel("Time-to-event:");
		tteLabel.setLabelFor(tteTextField);
		namelessPanel.add(tteLabel);
		namelessPanel.add(tteTextField);
		pOcurrenceTextField = new JTextField(15);
		JLabel probabilityTextLabel = new JLabel("Probability of ocurrence:");
		probabilityTextLabel.setLabelFor(pOcurrenceTextField);
		namelessPanel.add(probabilityTextLabel);
		namelessPanel.add(pOcurrenceTextField);

		add(namelessPanel);

	}

	@Override public void setData(Node node) {
		this.node = node;
		tteTextField.setText(String.valueOf(((IndicatorPotential)(node.getPotentials().get(0))).getTte()));
		pOcurrenceTextField.setText(String.valueOf(((IndicatorPotential)(node.getPotentials().get(0))).getpOccurrence()));
	}

	@Override public boolean saveChanges() {
		boolean result = super.saveChanges();
		ProbNet probNet = node.getProbNet();
		IndicatorPotential oldPotential = (IndicatorPotential) node.getPotentials().get(0);
		IndicatorPotential newPotential =  new IndicatorPotential(oldPotential);
		//FIXME control values
		try {
			newPotential.setTte(Double.parseDouble(tteTextField.getText()));
			newPotential.setpOccurrence(Double.parseDouble(pOcurrenceTextField.getText()));
		}catch(OutOfRangeException e){
			JOptionPane.showMessageDialog(null, "Values out of range",
					"Invalid value", JOptionPane.ERROR_MESSAGE);
		}

		PotentialChangeEdit edit = new PotentialChangeEdit(probNet, oldPotential, newPotential);
		try {
			probNet.doEdit(edit);
		} catch (ConstraintViolationException | NonProjectablePotentialException | WrongCriterionException | DoEditException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override public void close() {
		// Do nothing
	}

}
