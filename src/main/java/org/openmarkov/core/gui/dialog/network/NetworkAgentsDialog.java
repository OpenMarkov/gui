package org.openmarkov.core.gui.dialog.network;

import java.awt.Window;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.openmarkov.core.gui.component.DiscretizeTablePanel;
import org.openmarkov.core.gui.dialog.common.OkCancelHorizontalDialog;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.model.network.AdditionalProperties;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.PolicyType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.StringsWithProperties;
/**
 * 
 * @author myebra
 *
 */
@SuppressWarnings("serial")
public class NetworkAgentsDialog extends OkCancelHorizontalDialog{

	private StringResource dialogStringResource;

	private NetworkAgentsTablePanel newtworkAgentsPanel;

	private JPanel componentsPanel;
	
	private ProbNet probNet;
	
	public NetworkAgentsDialog(Window owner, ProbNet probNet) {
		super(owner);
		this.probNet = probNet;
		initialize();
		setName("NetworkAgentsDialog");
		setLocationRelativeTo(owner);
		pack();
	}

	/**
	 * This method configures the dialog box.
	 */
	private void initialize() {

		dialogStringResource =
			StringResourceLoader.getUniqueInstance().getBundleDialogs();
		setTitle(dialogStringResource
			.getString("NetworkAgentsDialog.Title.Label"));
		configureComponentsPanel();
		pack();
	}

	private void configureComponentsPanel() {

		getComponentsPanel().add(getNetworkAgentsPanel());
		setFieldFromProperties(probNet);
	}
	/**
	 * This method initialises componentsPanel.
	 * 
	 * @return a new components panel.
	 */
	protected JPanel getComponentsPanel() {

		if (componentsPanel == null) {
			componentsPanel = new JPanel();
		}

		return componentsPanel;

	}

	private NetworkAgentsTablePanel getNetworkAgentsPanel() {
		if (newtworkAgentsPanel == null) {
			String[] columnNames = {"Agents"};
			newtworkAgentsPanel = new NetworkAgentsTablePanel(columnNames, probNet);
			newtworkAgentsPanel.setName("networkAgentsPanel");
			newtworkAgentsPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		}

		return newtworkAgentsPanel;

	}
	
	 public void setFieldFromProperties (ProbNet probNet) {
		 Object [][] data = null;
		 StringsWithProperties agents = probNet.getAgents();
		 //if (!agents.isEmpty()) {
		 if (agents != null) {
			 Set<String> agentsNames = agents.getNames();
			 Iterator<String> iterator = agentsNames.iterator();
		
			 int i = 0;
			 while (iterator.hasNext()) {
				 String name = (String) iterator.next();
				// AdditionalProperties additionalPorperties =  agents.getProperties(name);
				 if (name != null) {
					 data [i][0] = name;
					 i++;
				 }
			 }
			 
			 //agents.getProperties(string)
			 getNetworkAgentsPanel().setData(data);
		 }
	}
	 
	 public int requestValues() {
	       setVisible(true);
	       return selectedButton;
	    }
}
