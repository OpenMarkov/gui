/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.dialog.common;

import org.openmarkov.core.model.network.*;
import org.openmarkov.core.model.network.modelUncertainty.ProbDensFunction;
import org.openmarkov.core.model.network.modelUncertainty.ParametrizedFunction.ParametrizedFunctionManager;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.TableWithEvents;
import org.openmarkov.core.model.network.potential.TimeToEventTablePotential;
import org.openmarkov.gui.component.PotentialsTablePanelOperations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * This class implements a Table eventTablePotential table.
 * Transition class to be merged with the new structure of tables
 * @version 1.0 - cyago - 24/03/2019
 */
@SuppressWarnings("serial") @PotentialPanelPlugin( potentialType = "TimeToEventTable")
public class TimeToEventTablePotentialPanel
		extends ProbabilityTablePanel implements ItemListener{

	private JPanel jDistributionAndParametrization;
	private JLabel jlDistribution;
	private JComboBox<String> jcDistribution;
	private JLabel jlParametrization;
	private JComboBox<String> jcParametrization;
	private TableWithEventsPanel tableWithEventsPanel;

	protected Node node;
	private PotentialsTablePanelOperations tablePotentialsPanelOperations;
	protected TimeToEventTablePotential tteTablePotential;
	protected TablePotential tablePotential;
	protected TableWithEvents tableWithEvents;
	protected ParametrizedFunctionManager parametrizedFunctionManager;

//	protected ProbDensFunctionManager distributionManager;
	protected ProbDensFunction distribution;



	public TimeToEventTablePotentialPanel(Node node)
	{
		super();
		this.node = node;


        this.tablePotentialsPanelOperations = new PotentialsTablePanelOperations();

        tteTablePotential = (TimeToEventTablePotential) node.getPotentials().get(0);
        tableWithEvents =  tteTablePotential.getTableWithEvents();



        setLayout(new BorderLayout());
		this.add(getjDistributionAndParametrization(),BorderLayout.PAGE_START);


		this.add( getTableWithEventsPanel(),BorderLayout.CENTER);

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

	protected JComboBox<String> getJcDistribution() {

		parametrizedFunctionManager = ParametrizedFunctionManager.getUniqueInstance();
		Map<String, List<String>> distributionsMap = parametrizedFunctionManager.getDistributionsMap();
		Set<String> keySet =distributionsMap.keySet();
		String[] distributionNames = keySet.toArray(new String[keySet.size()]);

		if (jcDistribution == null){
			jcDistribution = new JComboBox<String>(distributionNames);
			jcDistribution.setSelectedItem(tteTablePotential.getDistributionName());
			jcDistribution.addItemListener(this);
		}

		return jcDistribution;
	}

	private void populateJcParametrization(String selectedDistribution){
		try {
			parametrizedFunctionManager = ParametrizedFunctionManager.getUniqueInstance();
			Map<String, List<String>> distributionsMap = parametrizedFunctionManager.getDistributionsMap();
			List<String> parametrizations = distributionsMap.get(selectedDistribution);
			jcParametrization.setModel( new DefaultComboBoxModel(new Vector<String>( parametrizations )) );
			jcParametrization.setSelectedItem(tteTablePotential.getParametrizationName());
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	protected JLabel getJlParametrization() {
		if (jlParametrization==null){
			jlParametrization = new JLabel("Parametrization: ");
		}
		return jlParametrization;
	}

	private JComboBox<String> getJcParametrization() {
		if (jcParametrization == null){
			jcParametrization = new JComboBox<String>();
			populateJcParametrization(jcDistribution.getSelectedItem().toString());
			jcDistribution.setSelectedItem(tteTablePotential.getParametrizationName());
			jcParametrization.addItemListener(this);
		}

		return jcParametrization;
	}

	protected JPanel getjDistributionAndParametrization() {
		if (jDistributionAndParametrization ==null){
			jDistributionAndParametrization = new JPanel(new FlowLayout(FlowLayout.CENTER, 10,10));
			//Distribution label + combobox
			JPanel jDistribution = new JPanel(new FlowLayout(FlowLayout.LEFT, 5,10));
			jDistribution.add(getJlDistribution());
			jDistribution.add(getJcDistribution());

			//Parametrization label + combobox
			JPanel jParametrization = new JPanel(new FlowLayout(FlowLayout.LEFT, 5,10));
			jParametrization.add(getJlParametrization());
			jParametrization.add(getJcParametrization());

			//Ditribution + Parametrizatoin
			jDistributionAndParametrization.add(jDistribution);
			jDistributionAndParametrization.add(jParametrization);
		}

		return jDistributionAndParametrization;
	}



	public void setjDistributionAndParametrization(JPanel jDistributionAndParametrization) {
		this.jDistributionAndParametrization = jDistributionAndParametrization;
	}




	protected void setJcDistribution(JComboBox<String> jcDistribution) {
		this.jcDistribution = jcDistribution;
	}


//	protected JComboBox<String> getJcParametrization() {
//
//		tteTablePotential.getDistributionName();
//
//		distributionManager.getParemetersOfTTEProbDensFunction("String functionName");
//		return jcParametrization;
//	}



	protected TableWithEventsPanel getTableWithEventsPanel() {
		if (tableWithEventsPanel ==null) {
			tableWithEventsPanel =new TableWithEventsPanel(node, tableWithEvents,tteTablePotential.getNumericVariables() );
		}
		return tableWithEventsPanel;
	}

	protected void setTableWithEventsPanel(TableWithEventsPanel tableWithEventsPanel) {
		this.tableWithEventsPanel = tableWithEventsPanel;

	}



	protected void setJlDistribution(JLabel jlDistribution) {
		this.jlDistribution = jlDistribution;
	}




	@Override
	public void itemStateChanged(ItemEvent e)
	{
		String sDistribution = jcDistribution.getItemAt(getJcDistribution().getSelectedIndex());
		String sParametrization = jcParametrization.getSelectedItem().toString();
		// if the state combobox is changed
		if (e.getSource() == jcDistribution) {

			if (sDistribution.compareTo(tteTablePotential.getDistributionName()) != 0) {
				//Change parametrizations
				populateJcParametrization(sDistribution);
				this.remove(tableWithEventsPanel);
				sParametrization = jcParametrization.getSelectedItem().toString();
				tteTablePotential.changeDistribution(sDistribution,sParametrization);
				tableWithEvents = tteTablePotential.getTableWithEvents();
				tableWithEventsPanel = new TableWithEventsPanel(node, tteTablePotential.getTableWithEvents());
				this.add(tableWithEventsPanel, BorderLayout.CENTER);
				this.revalidate();
				this.repaint();
			}
		}


		//Change table
		this.remove(tableWithEventsPanel);
		tteTablePotential.changeDistribution(sDistribution,sParametrization);
		tableWithEvents = tteTablePotential.getTableWithEvents();
		tableWithEventsPanel = new TableWithEventsPanel(node, tteTablePotential.getTableWithEvents());
		this.add(tableWithEventsPanel, BorderLayout.CENTER);
		this.revalidate();
		this.repaint();



	}


}




