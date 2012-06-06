package org.openmarkov.core.gui.dialog.network;

import java.awt.Window;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.gui.dialog.common.OkCancelHorizontalDialog;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
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
	
	public NetworkAgentsDialog(Window owner, ProbNet probNet, boolean newElement) {
		super(owner);
		this.probNet = probNet;
		/*if (!newElement) {
			probNet.getPNESupport().setWithUndo(true);
		}*/
		probNet.getPNESupport().setWithUndo(true);
		probNet.getPNESupport().openParenthesis();
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
		//setFieldFromProperties(probNet);
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
			String[] columnNames = {"Key", "Agents"};
			newtworkAgentsPanel = new NetworkAgentsTablePanel(columnNames, probNet);
			newtworkAgentsPanel.setName("networkAgentsPanel");
			newtworkAgentsPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		}

		return newtworkAgentsPanel;

	}
	
	 public void setFieldFromProperties (ProbNet probNet) {
		 Object [][] data = null;
		 StringsWithProperties agents = probNet.getAgents();
		 
		 if (agents != null) {
			 data =new Object [agents.getNames().size()][1];
			 Set<String> agentsNames = agents.getNames();
			 Iterator<String> iterator = agentsNames.iterator();
		
			 int i = 0;
			 while (iterator.hasNext()) {
				 String name = (String) iterator.next();
				 if (name != null) {
					 data [i][0] = name;
					 i++;
				 }
			 }
			 //initializing data structure for the table model
			getNetworkAgentsPanel().setData(data);
			// initializing data structure for supervising data order in GUI 
			getNetworkAgentsPanel().setDataTable(data);
		 }
	}
	 
	 public int requestValues() {
		 setFieldFromProperties(probNet);
	       setVisible(true);
	       return selectedButton;
	    }
		/**
		 * This method carries out the actions when the user press the Ok button
		 * before hide the dialog.
		 * 
		 * @return true if the dialog box can be closed.
		 * @throws NotEnoughMemoryException 
		 */
		protected boolean doOkClickBeforeHide() throws NotEnoughMemoryException {
			probNet.getPNESupport().closeParenthesis();
			return true;
		}

		/**
		 * This method carries out the actions when the user press the Cancel button
		 * before hide the dialog.
		 */
		protected void doCancelClickBeforeHide() {
			probNet.getPNESupport().closeParenthesis();
			//TODO PNESupport must support more depth levels parenthesis 
			//As current performance edits from NetworkAgentsPanel only be undone when cancel
			//NodesPropertiesDialog
			for (int i = getNetworkAgentsPanel().getEdits().size()-1; i >=0; i--) {
				getNetworkAgentsPanel().getEdits().get(i).undo();
			}
			
		}

}
