package org.openmarkov.core.gui.dialog.node;


import java.awt.Window;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;


import org.openmarkov.core.gui.dialog.common.OkCancelApplyUndoRedoHorizontalDialog;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.gui.util.Utilities;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.VariableType;


/**
 * Dialog box to set the adittionalProperties of a node. This is the basic of all the node
 * adittionalProperties dialog. Subclasses adds the fields that corresponds to each type
 * of node.
 * If the node is a utility node, then no Values panel is displayed
 * 
 * @author jmendoza
 * @version 1.0
 * @version 1.1 alberto - changeNameTabRelation()
 * @version 1.2 jlgozalo - set class to use independent panels;
 * @version 1.3 mpalacios
 */
public abstract class NodePropertiesDialog extends
				OkCancelApplyUndoRedoHorizontalDialog implements ChangeListener{

/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

/**
	 * Constant that defines the height of the tables that are shown into the
	 * dialog box.
	 */
	protected static final int TABLE_HEIGHT = 180;

	/**
	 * Panel to tab the different options.
	 */
	private JTabbedPane tabbedPane = null;

	/**
	 * Panel that contains the panel where node fields are. It is used to place
	 * the fields at the top of the panel.
	 */
	private NodeDefinitionPanel nodeDefinitionPanel = null;


	/**
	 * Panel that contains the panel where discretize values fields are. It is
	 * used to place the fields at the top of the panel.
	 */
	private NodeDomainValuesTablePanel nodeDomainValuesTablePanel =
		null;

	private DiscreteValuesTablePanel discreteValuesTablePanel =
		null;
	/**
	 * Panel that contains the panel where parents fields are. It is used to
	 * place the fields at the top of the panel.
	 */
	private NodeParentsPanel nodeParentsPanel = null;

	/**
	 * Panel that contains the panel where probability table are. It is used to
	 * place the fields at the top of the panel.
	 */
	//protected NodeProbsValuesTablePanel nodeProbsValuesTablePanel = null;
	/**
	 * Panel that contains the panel where other property table fields are. It
	 * is used to place the fields at the top of the panel.
	 */
	private NodeOtherPropsTablePanel nodeOtherPropsTablePanel = null;

	/**
	 * Specifies if the network whose adittionalProperties are edited is new.
	 */
	private boolean newNode = false;

	/**
	 * Object where all information will be saved.
	 */
	protected ProbNode probNode = null;

	/**
	 * Dialogs string resource.
	 */
	protected StringResource dialogStringResource = null;

	/**
	 * Messages string resource.
	 */
	protected StringResource messageStringResource = null;
	
	
	/**
	 * This method initialises this instance.
	 * 
	 * @param owner
	 *            window that owns this dialog.
	 * @param newElement
	 *            if true, it indicates that a new node is being created; if
	 *            false, an existing node is being modified.
	 */
	public NodePropertiesDialog(Window owner, ProbNode probNode, boolean newElement) {

		super(owner);
		this.probNode = probNode;
		newNode = newElement;
		initialize();
		
	}

	
	
	/**
	 * This method fills the content of the fields from a ProbNode object.
	 * In this method, when Elvira will be discontinued, the code for
	 * discriminate discrete and discretized variables must be eliminated
	 * 
	 * @param probNode
	 *            object from where load the information.
	 */
	private void setFieldsFromProperties(ProbNode probNode) {

		// Elvira do not distinguish between DISCRETE and DISCRETIZED
		// so here we will see if there are intervals in the states

		if (Utilities.hasLimitBracketSymbols(probNode.getVariable().getStates())
						&& (probNode.getVariable().getVariableType() == 
							VariableType.FINITE_STATES)) {
			// really DISCRETIZED, so change the value of the VariableType
			probNode.getVariable().setVariableType(VariableType.DISCRETIZED);
		}

		// set the nodeProperties variable in this dialog and panels
		this.probNode = probNode;
		//*******
		setTitle(dialogStringResource
					.getString("NodePropertiesDialog.Title.Label")
					+ ": " + probNode.getName());

		nodeDefinitionPanel.setNodeProperties(probNode);

		//*******
		if (probNode.getNodeType() == NodeType.CHANCE
						|| probNode.getNodeType() == NodeType.DECISION) {
			
			nodeDomainValuesTablePanel.setFieldsFromProperties(probNode);
			if (probNode.getVariable().getVariableType() == VariableType.FINITE_STATES) {
				//tabbedPane.setEnabledAt(tabbedPane.indexOfTab(dialogStringResource
				  //      .getString("NodePropertiesDialog.DiscreteValuesTab.Title.Label")),
					//	true); // set enable the DiscreteValuesPanel
				//changed by mpalacios
				tabbedPane.setEnabledAt(tabbedPane.indexOfTab(dialogStringResource
						.getString("NodePropertiesDialog.DiscretizeValuesTab.Title.Label")),
						true); // set disable the DiscreteValuesPanel
			} else if (probNode.getVariable().getVariableType() == VariableType.DISCRETIZED) {
				//tabbedPane.setEnabledAt(tabbedPane.indexOfTab(dialogStringResource
					//	.getString("NodePropertiesDialog.DiscreteValuesTab.Title.Label")),
						//false); // set disable the DiscreteValuesPanel
				tabbedPane.setEnabledAt(tabbedPane.indexOfTab(dialogStringResource
						.getString("NodePropertiesDialog.DiscretizeValuesTab.Title.Label")),
						true); // set enable the DiscreteValuesPanel
			}
			/*
			 * TODO when Continuous variable will be included, remember to include
			 * also in the previous two else if, the appropiate method setVisible()
			 * else if (adittionalProperties.getVariableType()==VariableType.CONTINUOUS) {
			 * nodeContinuousValuesTablePanel.setFieldsFromProperties(adittionalProperties); }
			 */
		} else {
			tabbedPane.setEnabledAt(tabbedPane.indexOfTab(dialogStringResource
			                      						.getString("NodePropertiesDialog.DiscreteValuesTab.Title.Label")),
			                      						false); // set disable the DiscreteValuesPanel
			tabbedPane.setEnabledAt(tabbedPane.indexOfTab(dialogStringResource
			                      						.getString("NodePropertiesDialog.DiscretizeValuesTab.Title.Label")),
			                      						false); // set disable the DiscreteValuesPanel
			                      	
		}
			
		//*******
		nodeParentsPanel.setNodeProperties(probNode);

		//*******
		/*nodeProbsValuesTablePanel.setNodeProperties(probNode);
		String auxTitle = dialogStringResource
							.getString(
									"NodePropertiesDialog.ProbTablesTab.Title.Label");
		int auxTabPosition = tabbedPane.indexOfTab(auxTitle);
		if (probNode.getNodeType() == NodeType.CHANCE ) {
            auxTitle = dialogStringResource
			.getString(
				   "NodePropertiesDialog.ProbTablesTab.Title.Label.NodeChance");
		} else if (probNode.getNodeType() == NodeType.DECISION ) {
            auxTitle = dialogStringResource
			.getString(
				   "NodePropertiesDialog.ProbTablesTab.Title.Label.NodeDecision");
		} else if (probNode.getNodeType() == NodeType.UTILITY ) {
            auxTitle = dialogStringResource
			.getString(
				   "NodePropertiesDialog.ProbTablesTab.Title.Label.NodeUtility");
		}
		tabbedPane.setTitleAt(auxTabPosition,auxTitle);*/

		//*******
		nodeOtherPropsTablePanel.setNodeProperties(probNode);


		// set the NodeDefinitionPanel fields
		nodeDefinitionPanel.setFieldsFromProperties(probNode);
		// set the NodeParentsPanel fields
		nodeParentsPanel.setFieldsFromProperties(probNode);
		// set the NodeProbsValuesTablePanel fields
		//nodeProbsValuesTablePanel.setFieldsFromProperties(probNode);
		// set the NodeOtherPropsTablePanel fields //Disable by mpalacios
		nodeOtherPropsTablePanel.setFieldsFromProperties(probNode);
		// setSpecificFieldsFromProperties(adittionalProperties);
	}

	/**
	 * This method configures the dialog box.
	 */
	protected void initialize() {

		dialogStringResource =
			StringResourceLoader.getUniqueInstance().getBundleDialogs();
		messageStringResource =
			StringResourceLoader.getUniqueInstance().getBundleMessages();
		setTitle(dialogStringResource
			.getString("NodePropertiesDialog.Title.Label")
			+ ": " + (probNode ==null? "":probNode.getName()));
		getComponentsPanel().setName("NodePropertiesDialogComponentPane");
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
	protected JTabbedPane getTabbedPane() {

		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane();
			tabbedPane.setName("NodePropertiesDialogTabbedPane");
			tabbedPane
				.addTab(
					dialogStringResource
						.getString("NodePropertiesDialog.DefinitionTab.Title.Label"),
					null, getNodeDefinitionPanel(), null);
			//changed by mpalacios
			//tabbedPane
				//.addTab(
					//dialogStringResource
						//.getString("NodePropertiesDialog.DiscreteValuesTab.Title.Label"),
					//null, getNodeDiscreteValuesTablePanel(), null);
			
			tabbedPane
				.addTab(
					dialogStringResource
						.getString("NodePropertiesDialog.DiscretizeValuesTab.Title.Label"),
					null, getNodeDomainValuesTablePanel(), null);
			
			tabbedPane.addTab(
				dialogStringResource
					.getString("NodePropertiesDialog.ParentsTab.Title.Label"),
				null, getNodeParentsPanel(), null);
			
			/*tabbedPane
				.addTab(
					dialogStringResource
						.getString("NodePropertiesDialog.ProbTablesTab.Title.Label"),
					null, getNodeProbsTablePanel(), null);*/
			
			tabbedPane
				.addTab(
					dialogStringResource
						.getString("NodePropertiesDialog.OtherPropsTab.Title.Label"),
					null, getNodeOtherPropsTablePanel(), null);

		}
		return tabbedPane;
	}


	
	/**
	 * @return the nodeProperties
	 */
	public ProbNode getNodeProperties() {
	
		return probNode;
	}

	/**
	 * This method initialises nodeDefinitionPanel.
	 * 
	 * @return a new node definition panel.
	 */
	protected JPanel getNodeDefinitionPanel() {

		if (nodeDefinitionPanel == null) {
			nodeDefinitionPanel = new NodeDefinitionPanel(probNode);
			nodeDefinitionPanel.setName("nodeDefinitionPanel");
			nodeDefinitionPanel.setNewNode(newNode);
			nodeDefinitionPanel.setNodeProperties(probNode);

		}
		return nodeDefinitionPanel;
	}

	/**
	 * This method initialises discreteValuesTablePanel.
	 * 
	 * @return a new node discrete values table panel
	 */
	protected JPanel getNodeDiscreteValuesTablePanel() {

		if (discreteValuesTablePanel == null) {
			discreteValuesTablePanel = new DiscreteValuesTablePanel();
			discreteValuesTablePanel
				.setName("discreteValuesTablePanel");
			discreteValuesTablePanel.setNewNode(newNode);
			discreteValuesTablePanel.setNodeProperties(probNode);
			

		}

		return discreteValuesTablePanel;
	}
	
	/**
	 * This method initializes nodeDomainValuesTablePanel.
	 * 
	 * @return a new node discrete values table panel
	 */
	protected JPanel getNodeDomainValuesTablePanel() {

		if (nodeDomainValuesTablePanel == null) {
			nodeDomainValuesTablePanel =
				new NodeDomainValuesTablePanel(probNode);
			nodeDomainValuesTablePanel.getJLabelPrecision().setHorizontalAlignment(SwingConstants.LEFT);
			nodeDomainValuesTablePanel
				.setName("nodeDiscretizeValuesTablePanel");
			nodeDomainValuesTablePanel.setNewNode(newNode);
		}
		return nodeDomainValuesTablePanel;
	}

	/**
	 * This method initialises nodeParentsPanel.
	 * 
	 * @return a new node parents panel
	 */
	protected JPanel getNodeParentsPanel() {

		if (nodeParentsPanel == null) {
			nodeParentsPanel = new NodeParentsPanel(probNode);
			nodeParentsPanel.setName("nodeParentsPanel");
			nodeParentsPanel.setNewNode(newNode);
			//nodeParentsPanel.setNodeProperties(probNode);

		}
		return nodeParentsPanel;
	}

	/**
	 * This method initializes nodeOtherPropsTablePanel.
	 * 
	 * @return a new node other adittionalProperties table panel
	 */
	protected JPanel getNodeOtherPropsTablePanel() {

		if (nodeOtherPropsTablePanel == null) {
			nodeOtherPropsTablePanel = new NodeOtherPropsTablePanel();
			nodeOtherPropsTablePanel.setName("nodeOtherPropsTablePanel");
			nodeOtherPropsTablePanel.setNewNode(newNode);
			nodeOtherPropsTablePanel.setNodeProperties(probNode);

		}
		return nodeOtherPropsTablePanel;
	}

	/**
	 * This method carries out the actions when the user press the Cancel button
	 * before hide the dialog.
	 */
	@Override
	protected void doCancelClickBeforeHide() {
		probNode.getProbNet().getPNESupport().closeParenthesis();
	}

	/**
	 * This method shows the dialog and requests the user the node adittionalProperties.
	 * 
	 * @param newNetwork
	 *            network to which the node belongs.
	 * @param adittionalProperties
	 *            adittionalProperties of the node.
	 * @return OK_BUTTON if the user has pressed the 'Ok' button or
	 *         CANCEL_BUTTON if the user has pressed the 'Cancel' button.
	 */
	public int requestProperties() {

		setFieldsFromProperties(probNode);
		setVisible(true);
		//TODO revisar el acceso a los componentes en la siguiente línea
		/*probNode.getProbNet().getPNESupport().removeUndoableEditListener(
				((NodeProbsValuesTablePanel)getNodeProbsTablePanel()).
				getNodePotentialsTablePanel().getValuesTable());*/
		/*probNode.getProbNet().getPNESupport().removeUndoableEditListener(
				(getNodeDomainValuesTablePanel().get);*/
		
		return selectedButton;
		
	}

	/**
	 * This method carries out the actions when the user press the OK button
	 * before hide the dialog.
	 * 
	 * @return true if all the fields are correct.
	 */
	@Override
	protected boolean doOkClickBeforeHide() {

		if (generalChecks() /*&& specificChecks()*/) {
			probNode.getProbNet().getPNESupport().closeParenthesis();
			return true;
			
		}
		return false;
	}

	/**
	 * This method carries out the checks of the general fields. This fields
	 * appears in all of the subclasses.
	 * 
	 * @return true if all the fields are correct.
	 */
	private boolean generalChecks() {

		if (!nodeDefinitionPanel.checkName()) {
			return false;
		}
		if (!nodeDefinitionPanel.checkPurpose()) {
			return false;
		}
		return true;
	}

	/**
	 * This method carries out the checks of the specific fields. This specific
	 * fields depend on the type of the node.
	 * 
	 * @return true if all the fields are correct.
	 */
	protected abstract boolean specificChecks();


}
