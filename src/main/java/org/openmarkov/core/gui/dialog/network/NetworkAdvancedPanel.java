package org.openmarkov.core.gui.dialog.network;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import org.openmarkov.core.gui.dialog.node.NodePropertiesDialog;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.gui.util.Utilities;
import org.openmarkov.core.model.network.ProbNet;
import javax.swing.JButton;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class NetworkAdvancedPanel extends JPanel implements ActionListener{

	
	private ProbNet probNet;
	private StringResource dialogStringResource;
	private boolean newNetwork;
	private JButton agentsButton;
	private JButton decisionCriteriaButton;
	private boolean newElement;

	/**
	 * This method initialises this instance.
	 * 
	 * @param newNetwork
	 *            to indicate if the panel is for new networks
	 * @param probNet2
	 *            manage the network access
	 */
	public NetworkAdvancedPanel(final boolean newNetwork, ProbNet probNet) {

		this.probNet = probNet;
		dialogStringResource = StringResourceLoader.getUniqueInstance()
				.getBundleDialogs();
		this.newNetwork = newNetwork;
		setName("NetworkAdvancedPanel");
		initialize();
		
		if (probNet.getAgents() == null) {
			getAgentsButton().setEnabled(false);
		} else if (probNet.getAgents() != null) {
			getAgentsButton().setEnabled(true);
		}
		
		if (!probNet.onlyChanceNodes()) {
			getDecisionCriteriaButton().setEnabled(true);
		} else if (probNet.onlyChanceNodes()) {
			getDecisionCriteriaButton().setEnabled(false);
		}
		

	}
	private void initialize() {
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(173)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(getAgentsButton(), Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(getDecisionCriteriaButton(), Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(131)
					.addComponent(getDecisionCriteriaButton())
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(getAgentsButton())
					.addContainerGap(219, Short.MAX_VALUE))
		);
			setLayout(groupLayout);
	}
	
	JButton getAgentsButton () {
		if (agentsButton == null) {
			agentsButton = new JButton(dialogStringResource
					.getString("NetworkAdvancedPanel.Agents.Text"));
			//agentsButton.setMinimumSize();
			agentsButton.addActionListener(this);
		}
		return agentsButton;
	}
	
	JButton getDecisionCriteriaButton () {
		if (decisionCriteriaButton == null) {
			decisionCriteriaButton = new JButton(dialogStringResource
					.getString("NetworkAdvancedPanel.DecisionCriteria.Text"));
			//decisionCriteriaButton.setMinimumSize(60);
			decisionCriteriaButton.addActionListener(this);
		}
		return decisionCriteriaButton;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals( agentsButton )) {
			actionPerformedAgents();
		} else if (e.getSource().equals( decisionCriteriaButton )) {
			actionPerformedDecisionCriteria();
		} 
	}
	
	protected void actionPerformedAgents() {
		NetworkAgentsDialog networkAgentsDialog = new NetworkAgentsDialog(Utilities.getOwner(this), probNet, newNetwork);
		if (networkAgentsDialog.requestValues() == NodePropertiesDialog.OK_BUTTON) {
		}
	}
	
	protected void actionPerformedDecisionCriteria() {
		DecisionCriteriaDialog decisionCriteriaDialog = new DecisionCriteriaDialog(Utilities.getOwner(this), probNet, newNetwork);
		if (decisionCriteriaDialog.requestValues() == NodePropertiesDialog.OK_BUTTON) {
		}
	}
}
