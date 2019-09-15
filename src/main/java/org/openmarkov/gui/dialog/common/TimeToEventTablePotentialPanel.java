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
import org.openmarkov.core.model.network.*;
import org.openmarkov.core.model.network.modelUncertainty.ProbDensFunction;
import org.openmarkov.core.model.network.modelUncertainty.ProbDensFunctionManager;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.TableWithEventsPotential;
import org.openmarkov.core.model.network.potential.TimeToEventTablePotential;
import org.openmarkov.core.model.network.potential.TransitionTablePotential;
import org.openmarkov.gui.component.PotentialsTablePanelOperations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * This class implements a Table eventTablePotential table.
 * Transition class to be merged with the new structure of tables
 * @version 1.0 - cyago - 24/03/2019
 */
@SuppressWarnings("serial") @PotentialPanelPlugin( potentialType = "TimeToEventTable")
public class TimeToEventTablePotentialPanel
		extends ProbabilityTablePanel implements ItemListener{

	private JPanel jDistribution;
	private JLabel jlDistribution;
	private JComboBox<String> jcDistribution;
	private TableWithEventsPotentialPanel tableWithEventsPotentialPanel;

	protected Node node;
	private PotentialsTablePanelOperations tablePotentialsPanelOperations;
	protected TimeToEventTablePotential tteTablePotential;
	protected TablePotential tablePotential;
	protected TableWithEventsPotential tableWithEvents;
	protected ProbDensFunctionManager distributionManager;
	protected ProbDensFunction distribution;


	public TimeToEventTablePotentialPanel(Node node)
	{
		super();
		this.node = node;


        this.tablePotentialsPanelOperations = new PotentialsTablePanelOperations();

        // If there is no eventTablePotential
        try {
            tablePotentialsPanelOperations.checkIfNoPotential(node.getPotentials());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, stringDatabase.getString(e.getMessage()),
                    stringDatabase.getString(e.getMessage()), JOptionPane.ERROR_MESSAGE);
            return;
        }
        this.node = node;

        tteTablePotential = (TimeToEventTablePotential) node.getPotentials().get(0);
        tableWithEvents =  tteTablePotential.getTableWithEvents();


        setLayout(new BorderLayout());
		this.add(getjDistribution(),BorderLayout.PAGE_START);


		this.add( getTableWithEventsPotentialPanel(),BorderLayout.CENTER);

//		jsP = new JScrollPane();
//		jsP.setName("TimeToEventTablePotentialPanel.jsP");

		repaint();

	}



	@Override
	public void setData(Node node) {

	}

	@Override
	public void close() {

	}

	protected JLabel getJlDistribution() {
		if (jlDistribution==null){
			jlDistribution = new JLabel("Distribution: ");
		}
		return jlDistribution;
	}
	protected JPanel getjDistribution() {


		if (jDistribution==null){
			jDistribution = new JPanel(new FlowLayout());
			jDistribution.add(getJlDistribution());
			jDistribution.add(getJcDistribution());
		}

		return jDistribution;
	}



	public void setjDistribution(JPanel jDistribution) {
		this.jDistribution = jDistribution;
	}

	protected JComboBox<String> getJcDistribution() {

        distributionManager =  ProbDensFunctionManager.getUniqueInstance();
        String[] arrayDistributions = distributionManager.getDistributions().stream()
                .toArray(String[]::new);

		if (jcDistribution == null){
			jcDistribution = new JComboBox<String>(arrayDistributions);
			jcDistribution.setSelectedItem(tteTablePotential.getDistributionName());
			jcDistribution.addItemListener(this);
		}

		return jcDistribution;
	}




	protected void setJcDistribution(JComboBox<String> jcDistribution) {
		this.jcDistribution = jcDistribution;
	}

	protected TableWithEventsPotentialPanel getTableWithEventsPotentialPanel() {
		if (tableWithEventsPotentialPanel ==null) {
			tableWithEventsPotentialPanel =new TableWithEventsPotentialPanel(node, tableWithEvents);
		}
		return tableWithEventsPotentialPanel;
	}

	protected void setTableWithEventsPotentialPanel(TableWithEventsPotentialPanel tableWithEventsPotentialPanel) {
		this.tableWithEventsPotentialPanel = tableWithEventsPotentialPanel;

	}



	protected void setJlDistribution(JLabel jlDistribution) {
		this.jlDistribution = jlDistribution;
	}




	@Override
	public void itemStateChanged(ItemEvent e)
	{
		// if the state combobox is changed
		if (e.getSource() == jcDistribution) {

			String sDistribution = jcDistribution.getItemAt(getJcDistribution().getSelectedIndex());
			if (sDistribution.compareTo(tteTablePotential.getDistributionName()) != 0) {
				try {
					distribution = distributionManager.getProbDensFunctionClass(sDistribution).newInstance();
				} catch (InstantiationException ex) {
					ex.printStackTrace();
				} catch (IllegalAccessException ex) {
					ex.printStackTrace();
				}
				this.remove(tableWithEventsPotentialPanel);
				tteTablePotential.changeDistribution(sDistribution);
				tableWithEvents = tteTablePotential.getTableWithEvents();
				tableWithEventsPotentialPanel = new TableWithEventsPotentialPanel(node, tteTablePotential.getTableWithEvents());
				this.add(tableWithEventsPotentialPanel, BorderLayout.CENTER);
				this.revalidate();
				this.repaint();


			}
		}
	}


}




