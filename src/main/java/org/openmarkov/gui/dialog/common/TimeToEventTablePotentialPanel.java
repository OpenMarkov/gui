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
 * This class implements the Panel for TimeToEventTablePotential. This panel has two comboboxed for the distribution and parametrization,
 * and the correspondent TableWithEvents.
 *
 * @version 1.0 - cyago - 24/03/2019
 * @version 1.1 - cyago - 22/04/2021 - Added parametrization for distributions and javadoc
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

	/**
	 * Creates a TimeToEventTablePotentialPanel for eventNode. If eventNode stores a TimeToEventTablePotential, this is displayed.
	 * Otherwise an Exact distribution is displayed.
	 * @param eventNode - event node which contains the TimeToEventTablePotential
	 */
	public TimeToEventTablePotentialPanel(Node eventNode)
	{
		super();
		this.node = eventNode;


        this.tablePotentialsPanelOperations = new PotentialsTablePanelOperations();

        tteTablePotential = (TimeToEventTablePotential) eventNode.getPotentials().get(0);
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

	/**
	 * Returns the label for the distribution combobox
	 * @return the label for the distribution combobox
	 */
	protected JLabel getJlDistribution() {
		if (jlDistribution==null){
			jlDistribution = new JLabel("Distribution: ");
		}
		return jlDistribution;
	}

	/**
	 * Returns the combobox with the list of possible distributions
	 * @return the combobox with the list of possible distributions
	 */
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

	/**
	 * Populates jcParametrization with the parametrizations of selectedDistribution
	 * @param selectedDistribution distribution whose parametrizations are shown
	 */
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

	/**
	 * Returns the label for the jcParametrization combobox
	 * @return the label for the jcParametrization combobox
	 */
	protected JLabel getJlParametrization() {
		if (jlParametrization==null){
			jlParametrization = new JLabel("Parametrization: ");
		}
		return jlParametrization;
	}

	/**
	 * Returns the combobox with the parametrizations for the distibution selected in jcDistribution
	 * @return the combobox with the parametrizations for the distibution selected in jcDistribution
	 */
	private JComboBox<String> getJcParametrization() {
		if (jcParametrization == null){
			jcParametrization = new JComboBox<String>();
			populateJcParametrization(jcDistribution.getSelectedItem().toString());
			jcDistribution.setSelectedItem(tteTablePotential.getParametrizationName());
			jcParametrization.addItemListener(this);
		}

		return jcParametrization;
	}

	/**
	 * Returns the panel with the comboboxes for distribution and parametrization
	 * @return the panel with the comboboxes for distribution and parametrization
	 */
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




	protected TableWithEventsPanel getTableWithEventsPanel() {
		if (tableWithEventsPanel ==null) {
			tableWithEventsPanel =new TableWithEventsPanel(node, tableWithEvents,tteTablePotential.getNumericVariables() );
		}
		return tableWithEventsPanel;
	}


	/**
	 * Changes the timeToEventPotential and TimeToEventTablePotentialPanel according to the new selected distribution
	 * or the new selected parametrization
	 * @param e jcDistribution with the new selected distribution or jcParametrization with the new selected parametrization
	 */
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
				sParametrization = jcParametrization.getSelectedItem().toString();

			}
		}
		//Change table
		this.remove(tableWithEventsPanel);
		tableWithEventsPanel = null;
		tteTablePotential.changeDistribution(sDistribution,sParametrization);
		tableWithEvents = tteTablePotential.getTableWithEvents();
		tableWithEventsPanel = getTableWithEventsPanel();
		this.add(tableWithEventsPanel, BorderLayout.CENTER);
		this.revalidate();
		this.repaint();
	}


}




