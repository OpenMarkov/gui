
package org.openmarkov.core.gui.dialog.network;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.openmarkov.core.gui.dialog.node.NodePropertiesDialog;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.gui.util.Utilities;
import org.openmarkov.core.model.network.ProbNet;

@SuppressWarnings("serial")
public class NetworkAdvancedPanel extends JPanel
    implements
        ActionListener
{
    private ProbNet probNet;
    private boolean newNetwork;
    private JButton agentsButton;
    private JButton decisionCriteriaButton;

    /**
     * This method initialises this instance.
     * @param newNetwork to indicate if the panel is for new networks
     * @param probNet2 manage the network access
     */
    public NetworkAdvancedPanel (final boolean newNetwork, ProbNet probNet)
    {
        this.probNet = probNet;
        this.newNetwork = newNetwork;
        setName ("NetworkAdvancedPanel");
        initialize ();
        if (probNet.getAgents () == null)
        {
            getAgentsButton ().setEnabled (false);
        }
        else if (probNet.getAgents () != null)
        {
            getAgentsButton ().setEnabled (true);
        }
        if (!probNet.onlyChanceNodes ())
        {
            getDecisionCriteriaButton ().setEnabled (true);
        }
        else if (probNet.onlyChanceNodes ())
        {
            getDecisionCriteriaButton ().setEnabled (false);
        }
    }

    private void initialize ()
    {
        GroupLayout groupLayout = new GroupLayout (this);
        groupLayout.setHorizontalGroup (groupLayout.createParallelGroup (Alignment.LEADING).addGroup (groupLayout.createSequentialGroup ().addGap (173).addGroup (groupLayout.createParallelGroup (Alignment.TRAILING,
                                                                                                                                                                                                   false).addComponent (getAgentsButton (),
                                                                                                                                                                                                                        Alignment.LEADING,
                                                                                                                                                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                                        Short.MAX_VALUE).addComponent (getDecisionCriteriaButton (),
                                                                                                                                                                                                                                                       Alignment.LEADING,
                                                                                                                                                                                                                                                       GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                                                                       268,
                                                                                                                                                                                                                                                       Short.MAX_VALUE)).addContainerGap ()));
        groupLayout.setVerticalGroup (groupLayout.createParallelGroup (Alignment.LEADING).addGroup (groupLayout.createSequentialGroup ().addGap (131).addComponent (getDecisionCriteriaButton ()).addPreferredGap (ComponentPlacement.UNRELATED).addComponent (getAgentsButton ()).addContainerGap (219,
                                                                                                                                                                                                                                                                                                    Short.MAX_VALUE)));
        setLayout (groupLayout);
    }

    JButton getAgentsButton ()
    {
        if (agentsButton == null)
        {
            agentsButton = new JButton (
                                        StringDatabase.getUniqueInstance ().getString ("NetworkAdvancedPanel.Agents.Text"));
            // agentsButton.setMinimumSize();
            agentsButton.addActionListener (this);
        }
        return agentsButton;
    }

    JButton getDecisionCriteriaButton ()
    {
        if (decisionCriteriaButton == null)
        {
            decisionCriteriaButton = new JButton (
                                                  StringDatabase.getUniqueInstance ().getString ("NetworkAdvancedPanel.DecisionCriteria.Text"));
            // decisionCriteriaButton.setMinimumSize(60);
            decisionCriteriaButton.addActionListener (this);
        }
        return decisionCriteriaButton;
    }

    @Override
    public void actionPerformed (ActionEvent e)
    {
        if (e.getSource ().equals (agentsButton))
        {
            actionPerformedAgents ();
        }
        else if (e.getSource ().equals (decisionCriteriaButton))
        {
            actionPerformedDecisionCriteria ();
        }
    }

    protected void actionPerformedAgents ()
    {
        NetworkAgentsDialog networkAgentsDialog = new NetworkAgentsDialog (
                                                                           Utilities.getOwner (this),
                                                                           probNet, newNetwork);
        if (networkAgentsDialog.requestValues () == NodePropertiesDialog.OK_BUTTON)
        {
        }
    }

    protected void actionPerformedDecisionCriteria ()
    {
        DecisionCriteriaDialog decisionCriteriaDialog = new DecisionCriteriaDialog (
                                                                                    Utilities.getOwner (this),
                                                                                    probNet,
                                                                                    newNetwork);
        if (decisionCriteriaDialog.requestValues () == NodePropertiesDialog.OK_BUTTON)
        {
        }
    }
}
