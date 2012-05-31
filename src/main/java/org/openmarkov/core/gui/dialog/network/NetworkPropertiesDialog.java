/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog.network;


import java.awt.Window;

import javax.help.BadIDException;
import javax.swing.JTabbedPane;


import org.openmarkov.core.action.ChangeNetworkTypeEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.dialog.HelpViewer;
import org.openmarkov.core.gui.dialog.common.OkCancelHorizontalDialog;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.gui.util.PropertyNames;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.type.BayesianNetworkType;



/**
 * Dialog box to set the options of a network.
 * 
 * @author jmendoza
 * @version 1.0 jmendoza
 * @version 1.2 jlgozalo new Group layout and semantic errors fixed
 */
public class NetworkPropertiesDialog extends OkCancelHorizontalDialog implements
	PropertyNames{
	
	private ProbNet probNet;

	private NetworkAdvancedPanel networkAdvancedPanel;

	/**
	 * This method initializes this instance.
	 * 
	 * @param owner
	 *            window that owns the dialog.
	 * @param newElement
	 *            if true, it indicates that a new network is being created; if
	 *            false, an existing network is being modified.
	 * @wbp.parser.constructor
	 */
	public NetworkPropertiesDialog(Window owner, boolean newElement) {

		super(owner);

		newNetwork = newElement;
		initialize();
		setName("NetworkPropertiesDialog");
		setLocationRelativeTo(owner);
		//setOnlineHelp("Network Properties Dialog");
	}
	/**
	 * This method initializes this instance.
	 * 
	 * @param owner
	 *            window that owns the dialog.
	 * @param newElement
	 *            if true, it indicates that a new network is being created; if
	 *            false, an existing network is being modified.
	 */
	public NetworkPropertiesDialog(Window owner, ProbNet probNet, boolean newElement) {
		
		super(owner);
		if (!newElement) {
			probNet.getPNESupport().setWithUndo(true);
		}
		probNet.getPNESupport().openParenthesis();
	    this.probNet = probNet;
		newNetwork = newElement;
		initialize();
		setName("NetworkPropertiesDialog");
		setLocationRelativeTo(owner);
		//SsetOnlineHelp("Network Properties Dialog");
	}
	
	

	/**
	 * This method configures the dialog box.
	 */
	private void initialize() {

		dialogStringResource =
			StringResourceLoader.getUniqueInstance().getBundleDialogs();
		setTitle(dialogStringResource
			.getString("NetworkPropertiesDialog.Title.Label"));
		configureComponentsPanel();
		pack();
	}

	
	/**
	 * Sets up the panel where all components, except the buttons of the buttons
	 * panel, will be appear.
	 */
	private void configureComponentsPanel() {

		getComponentsPanel().add(getTabbedPane());
	}

	/**
	 * This method initialises tabbedPane.
	 * 
	 * @return a new tabbed pane.
	 */
	private JTabbedPane getTabbedPane() {

		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane();

			tabbedPane.addTab("", null, getNetworkDefinitionPanel(), null);
			tabbedPane.setTitleAt(0, dialogStringResource
				.getString("NetworkPropertiesDialog.DefinitionTab.Label"));

			tabbedPane.addTab(
				dialogStringResource
					.getString("NetworkPropertiesDialog.VariablesTab.Label"),
				null, getNetworkVariablesPanel(), null);
			tabbedPane.addTab(
					dialogStringResource
						.getString("NetworkPropertiesDialog.Advanced.Label"),
					null, getNetworkAdvancedPanel(), null);

			tabbedPane
				.addTab(
					dialogStringResource
						.getString("NetworkPropertiesDialog.OtherPropertiesTab.Label"),
					null, getNetworkOtherPropertiesPanel(), null);
			tabbedPane.setName("tabbedPane");
		}

		return tabbedPane;

	}

	/**
	 * This method initialises networkAdvancedPanel.
	 * 
	 * @return a new definition panel.
	 */
	private NetworkAdvancedPanel getNetworkAdvancedPanel() {

		if (networkAdvancedPanel == null) {
			networkAdvancedPanel = new NetworkAdvancedPanel(newNetwork, 
					probNet);
			networkAdvancedPanel.setName("networkAdvancedPanel");
		}	
		return networkAdvancedPanel;
		}
	/**
	 * This method initialises networkDefinitionPanel.
	 * 
	 * @return a new definition panel.
	 */
	private NetworkDefinitionPanel getNetworkDefinitionPanel() {

		if (networkDefinitionPanel == null) {
			networkDefinitionPanel = new NetworkDefinitionPanel(newNetwork, 
					probNet);
			networkDefinitionPanel.setName("networkDefinitionPanel");
			
		
		}

		return networkDefinitionPanel;

	}

	/**
	 * This method initialises networkVariablesPanel.
	 * 
	 * @return a new variables definition panel.
	 */
	private NetworkVariablesPanel getNetworkVariablesPanel() {

		if (networkVariablesPanel == null) {
			networkVariablesPanel = new NetworkVariablesPanel(probNet);
			networkVariablesPanel.setName("networkVariablesPanel");

		}

		return networkVariablesPanel;

	}

	/**
	 * This method initialises networkOtherPropertiesPanel.
	 * 
	 * @return a new other adittionalProperties panel.
	 */
	private NetworkOtherPropertiesPanel getNetworkOtherPropertiesPanel() {

		if (networkOtherPropertiesPanel == null) {
			networkOtherPropertiesPanel =
				new NetworkOtherPropertiesPanel(newNetwork);
			networkOtherPropertiesPanel.setName("networkOtherPropertiesPanel");

		}

		return networkOtherPropertiesPanel;

	}

	/**
	 * online help convenience method
	 */
	private void setOnlineHelp(String onlineSection) {
		/**
		 * auxiliar help Viewer
		 */
		HelpViewer helpViewer = null;

		helpViewer = HelpViewer.getUniqueInstance();
		try {
		helpViewer.getHb().enableHelpKey(this.getContentPane(), 
		                                      onlineSection,
		                                      helpViewer.getHs());
		} catch (BadIDException ex) {
			System.out.println("WARNING >> "+ ex.getMessage() );
			System.out.println(ex.getStackTrace());
		} catch (Exception ex) {
			System.out.println("WARNING >> "+ ex.getMessage() );
			System.out.println(ex.getStackTrace());
	
		}
     }
	

	

	
	/**
	 * This method fills the content of the fields from a NetworkProperties
	 * object.
	 * 
	 * @param adittionalProperties
	 *            object from where load the information.
	 */
	private void setFieldsFromProperties() {

		setTitle(dialogStringResource
			.getString("NetworkPropertiesDialog.Title.Label") + ": "
			+ probNet.getName());

		getNetworkDefinitionPanel().setFieldsFromProperties(
				probNet);
		getNetworkVariablesPanel().setFieldsFromProperties();
		//getNetworkOtherPropertiesPanel().setFieldsFromProperties(adittionalProperties);*/
		// TODO set the fields in the OtherProperties Table Panel

	}


	/**
	 * This method carries out the actions when the user press the Ok button
	 * before hide the dialog.
	 * 
	 * @return true always
	 */
	@Override
	protected boolean doOkClickBeforeHide() {
		//if networkType is null it is set type Bayessian network
		/*if (probNet.getNetworkType() == null) {
			ChangeNetworkTypeEdit changeNetworkType = new ChangeNetworkTypeEdit(probNet,
					BayesianNetworkType.getUniqueInstance());
			try {
				probNet.getPNESupport().announceEdit(changeNetworkType);
				probNet.getPNESupport().doEdit(changeNetworkType);
				
					
			} catch (DoEditException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			} catch (NotEnoughMemoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ConstraintViolationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CanNotDoEditException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NonProjectablePotentialException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WrongCriterionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}*/
		
		probNet.getPNESupport().closeParenthesis();
		
        if (!getNetworkDefinitionPanel().checkName()) {
			return false;
		}
		//changed by mpalacios: probNet will be updated by Edits
		//setPropertiesFromFields(networkProperties);
		return true;

	}

	// ESCA-JAVA0025: allows an empty method to override another one
	/**
	 * This method carries out the actions when the user press the Cancel button
	 * before hide the dialog.
	 */
	@Override
	protected void doCancelClickBeforeHide() {
		probNet.getPNESupport().closeParenthesis();
	}

	/**
	 * This method shows the dialog and requests the user the network
	 * adittionalProperties.
	 * 
	 * @param adittionalProperties
	 *            adittionalProperties of the network.
	 * @return OK_BUTTON if the user has pressed the 'Ok' button or
	 *         CANCEL_BUTTON if the user has pressed the 'Cancel' button.
	 */
	
	/**
	 * This method shows the dialog and requests the user the network
	 * adittionalProperties.
	 * 
	 * @param adittionalProperties
	 *            adittionalProperties of the network.
	 * @return OK_BUTTON if the user has pressed the 'Ok' button or
	 *         CANCEL_BUTTON if the user has pressed the 'Cancel' button.
	 */
	public int requestProperties() {

		setFieldsFromProperties();
		//setFieldsFromProperties(networkName, pNESupport.getProbNet());
		//networkProperties = adittionalProperties;
		setVisible(true);

		return selectedButton;

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8734100506781534551L;

	/**
	 * Panel to tab the different options.
	 */
	private JTabbedPane tabbedPane = null;

	/**
	 * Panel that contains the panel where definition fields are. It is used to
	 * place the fields at the top of the panel.
	 */
	private NetworkDefinitionPanel networkDefinitionPanel = null;

	/**
	 * Panel that contains the panel where variables definition fields are. It
	 * is used to place the fields at the top of the panel.
	 */
	private NetworkVariablesPanel networkVariablesPanel = null;

	/**
	 * Panel that contains the panel where a set of other adittionalProperties are. It is
	 * used to place the fields at the top of the panel.
	 */
	private NetworkOtherPropertiesPanel networkOtherPropertiesPanel = null;

	/**
	 * Dialog string resource.
	 */
	private StringResource dialogStringResource;


	/**
	 * Specifies if the network whose adittionalProperties are edited is new.
	 */
	private boolean newNetwork = false;
	
	
}