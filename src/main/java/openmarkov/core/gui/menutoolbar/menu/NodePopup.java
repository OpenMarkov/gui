package openmarkov.core.gui.menutoolbar.menu;


import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JMenuItem;

import openmarkov.core.gui.localize.StringResource;
import openmarkov.core.gui.localize.StringResourceLoader;
import openmarkov.core.gui.menutoolbar.common.ActionCommands;



/**
 * This class implements a popup menu that is displayes when the user clicks on
 * a node.
 * 
 * @author jmendoza
 * @author jlgozalo 
 * @version 1.1 jlgozalo - Add change locale management setting the item names.
 */
class NodePopup extends PopupMenuBasic {

	/**
	 * Static field for serializable class.
	 */
	private static final long serialVersionUID = 8556550568033250304L;

	/**
	 * Object that represents the item 'Cut'.
	 */
	private JMenuItem cutMenuItem = null;

	/**
	 * Object that represents the item 'Copy'.
	 */
	private JMenuItem copyMenuItem = null;

	/**
	 * Object that represents the item 'Remove'.
	 */
	private JMenuItem removeMenuItem = null;

	/**
	 * Object that represents the item 'Properties'.
	 */
	private JMenuItem propertiesMenuItem = null;

	/**
	 * String resource.
	 */
	private StringResource stringResource = null;

	private JMenuItem relationMenuItem;

	/**
	 * Object that represents the item 'Expand'.
	 */
	private JMenuItem expandMenuItem = null;
	
	/**
	 * Object that represents the item 'Contract'.
	 */
	private JMenuItem contractMenuItem = null;
	
	/**
	 * Object that represents the item 'addFinding'.
	 */
	private JMenuItem addFindingMenuItem = null;
	
	/**
	 * Object that represents the item 'removeFinding'.
	 */
	private JMenuItem removeFindingMenuItem = null;

	private JMenuItem logMenuItem;
	
	/**
	 * This constructor creates a new instance.
	 * 
	 * @param newListener
	 *            object that listens to the menu events.
	 */
	public NodePopup(ActionListener newListener) {

		super(newListener);

		initialize();

	}

	/**
	 * This method initialises this instance.
	 */
	private void initialize() {

		stringResource =
			StringResourceLoader.getUniqueInstance().getBundleMenus();
		add(getCutMenuItem());
		add(getCopyMenuItem());
		addSeparator();
		add(getRemoveMenuItem());
		addSeparator();
		add(getPropertiesMenuItem());
		add(getRelationMenuItem());
		addSeparator();
		add(getExpandMenuItem());
		add(getContractMenuItem());
		addSeparator();
		add(getAddFindingMenuItem());
		add(getRemoveFindingMenuItem());
		addSeparator();
		add(getLogMenuItem());
		
	}

	private JMenuItem getLogMenuItem() {
		if (logMenuItem == null) {
		logMenuItem = new JMenuItem();
		logMenuItem.setName("Edit.Log");
		logMenuItem.setText(stringResource
			.getString(MainMenu.EDIT_LOG_MENUITEM + LABEL_SUFFIX));
		logMenuItem.setMnemonic(stringResource.getString(
			MainMenu.EDIT_LOG_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
		logMenuItem.setActionCommand(ActionCommands.LOG);
		logMenuItem.addActionListener(listener);
	}

	return logMenuItem;
	}

	/**
	 * This method initialises cutMenuItem.
	 * 
	 * @return a new 'Cut' menu item.
	 */
	private JMenuItem getCutMenuItem() {

		if (cutMenuItem == null) {
			cutMenuItem = new JMenuItem();
			cutMenuItem.setName("Edit.Cut");
			cutMenuItem.setText(stringResource
				.getString(MainMenu.EDIT_CUT_MENUITEM + LABEL_SUFFIX));
			cutMenuItem.setMnemonic(stringResource.getString(
				MainMenu.EDIT_CUT_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			cutMenuItem.setActionCommand(ActionCommands.CLIPBOARD_CUT);
			cutMenuItem.addActionListener(listener);
		}

		return cutMenuItem;

	}

	/**
	 * This method initialises copyMenuItem.
	 * 
	 * @return a new 'Copy' menu item.
	 */
	private JMenuItem getCopyMenuItem() {

		if (copyMenuItem == null) {
			copyMenuItem = new JMenuItem();
			copyMenuItem.setName("Edit.Copy");
			copyMenuItem.setText(stringResource
				.getString(MainMenu.EDIT_COPY_MENUITEM + LABEL_SUFFIX));
			copyMenuItem.setMnemonic(stringResource.getString(
				MainMenu.EDIT_COPY_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			copyMenuItem.setActionCommand(ActionCommands.CLIPBOARD_COPY);
			copyMenuItem.addActionListener(listener);
		}

		return copyMenuItem;

	}

	/**
	 * This method initialises removeMenuItem.
	 * 
	 * @return a new 'Remove' menu item.
	 */
	private JMenuItem getRemoveMenuItem() {

		if (removeMenuItem == null) {
			removeMenuItem = new JMenuItem();
			removeMenuItem.setName("Edit.Remove");
			removeMenuItem.setText(stringResource
				.getString(MainMenu.EDIT_REMOVE_MENUITEM + LABEL_SUFFIX));
			removeMenuItem.setMnemonic(stringResource.getString(
				MainMenu.EDIT_REMOVE_MENUITEM + MNEMONIC_SUFFIX).charAt(0));
			removeMenuItem.setActionCommand(ActionCommands.OBJECT_REMOVAL);
			removeMenuItem.addActionListener(listener);
		}

		return removeMenuItem;

	}

	/**
	 * This method initialises propertiesMenuItem.
	 * 
	 * @return a new 'Properties' menu item.
	 */
	private JMenuItem getPropertiesMenuItem() {

		if (propertiesMenuItem == null) {
			propertiesMenuItem = new JMenuItem();
			propertiesMenuItem.setName("Edit.NodeProperties");
			propertiesMenuItem
				.setText(stringResource
					.getString(MainMenu.EDIT_NODEPROPERTIES_MENUITEM
						+ LABEL_SUFFIX));
			propertiesMenuItem.setMnemonic(stringResource.getString(
				MainMenu.EDIT_NODEPROPERTIES_MENUITEM + MNEMONIC_SUFFIX)
				.charAt(0));
			propertiesMenuItem.setActionCommand(ActionCommands.NODE_PROPERTIES);
			propertiesMenuItem.addActionListener(listener);
		}

		return propertiesMenuItem;

	}
	
	/**
	 * This method initialises tableMenuItem.
	 * 
	 * @return a new 'Table' menu item.
	 */
	private JMenuItem getRelationMenuItem() {

		if (relationMenuItem == null) {
			relationMenuItem = new JMenuItem();
			relationMenuItem.setName("Edit.Table");
			relationMenuItem
				.setText(stringResource
					.getString(MainMenu.EDIT_NODERELATION_MENUITEM
						+ LABEL_SUFFIX));
			relationMenuItem.setMnemonic(stringResource.getString(
				MainMenu.EDIT_NODERELATION_MENUITEM + MNEMONIC_SUFFIX)
				.charAt(0));
		relationMenuItem.setActionCommand(ActionCommands.CHANGE_POTENTIAL);
			relationMenuItem.addActionListener(listener);
		}

		return relationMenuItem;

	}

	/**
	 * This method initialises expandMenuItem.
	 * 
	 * @return a new 'Expand' menu item.
	 */
	private JMenuItem getExpandMenuItem() {

		if (expandMenuItem == null) {
			expandMenuItem = new JMenuItem();
			expandMenuItem.setName("Inference.Expansion");
			expandMenuItem
				.setText(stringResource
					.getString(MainMenu.INFERENCE_EXPAND_NODE_MENUITEM
						+ LABEL_SUFFIX));
			expandMenuItem.setMnemonic(stringResource.getString(
				MainMenu.INFERENCE_EXPAND_NODE_MENUITEM + MNEMONIC_SUFFIX)
				.charAt(0));
			expandMenuItem.setActionCommand(ActionCommands.NODE_EXPANSION);
			expandMenuItem.addActionListener(listener);
		}

		return expandMenuItem;

	}

	/**
	 * This method initialises contractMenuItem.
	 * 
	 * @return a new 'Contract' menu item.
	 */
	private JMenuItem getContractMenuItem() {

		if (contractMenuItem == null) {
			contractMenuItem = new JMenuItem();
			contractMenuItem.setName("Inference.Contraction");
			contractMenuItem
				.setText(stringResource
					.getString(MainMenu.INFERENCE_CONTRACT_NODE_MENUITEM
						+ LABEL_SUFFIX));
			contractMenuItem.setMnemonic(stringResource.getString(
				MainMenu.INFERENCE_CONTRACT_NODE_MENUITEM + MNEMONIC_SUFFIX)
				.charAt(0));
			contractMenuItem.setActionCommand(ActionCommands.NODE_CONTRACTION);
			contractMenuItem.addActionListener(listener);
		}

		return contractMenuItem;

	}
	
	/**
	 * This method initialises addFindingMenuItem.
	 * 
	 * @return a new 'addFinding' menu item.
	 */
	private JMenuItem getAddFindingMenuItem() {

		if (addFindingMenuItem == null) {
			addFindingMenuItem = new JMenuItem();
			addFindingMenuItem.setName("Inference.AddFinding");
			addFindingMenuItem
				.setText(stringResource
					.getString(MainMenu.INFERENCE_ADD_FINDING_MENUITEM
						+ LABEL_SUFFIX));
			addFindingMenuItem.setMnemonic(stringResource.getString(
				MainMenu.INFERENCE_ADD_FINDING_MENUITEM + MNEMONIC_SUFFIX)
				.charAt(0));
			addFindingMenuItem.setActionCommand(ActionCommands.NODE_ADD_FINDING);
			addFindingMenuItem.addActionListener(listener);
		}

		return addFindingMenuItem;

	}
	
	/**
	 * This method initialises removeFindingMenuItem.
	 * 
	 * @return a new 'removeFinding' menu item.
	 */
	private JMenuItem getRemoveFindingMenuItem() {

		if (removeFindingMenuItem == null) {
			removeFindingMenuItem = new JMenuItem();
			removeFindingMenuItem.setName("Inference.RemoveFinding");
			removeFindingMenuItem
				.setText(stringResource
					.getString(MainMenu.INFERENCE_REMOVE_FINDING_MENUITEM
						+ LABEL_SUFFIX));
			removeFindingMenuItem.setMnemonic(stringResource.getString(
				MainMenu.INFERENCE_REMOVE_FINDING_MENUITEM + MNEMONIC_SUFFIX)
				.charAt(0));
			removeFindingMenuItem.setActionCommand(ActionCommands.NODE_REMOVE_FINDING);
			removeFindingMenuItem.addActionListener(listener);
		}

		return removeFindingMenuItem;

	}
	
	/**
	 * Returns the component that corresponds to an action command.
	 * 
	 * @param actionCommand
	 *            action command that identifies the component.
	 * @return a components identified by the action command.
	 */
	@Override
	protected JComponent getJComponentActionCommand(String actionCommand) {

		JComponent component = null;

		if (actionCommand.equals(ActionCommands.CLIPBOARD_CUT)) {
			component = cutMenuItem;
		} else if (actionCommand.equals(ActionCommands.CLIPBOARD_COPY)) {
			component = copyMenuItem;
		} else if (actionCommand.equals(ActionCommands.OBJECT_REMOVAL)) {
			component = removeMenuItem;
		} else if (actionCommand.equals(ActionCommands.NODE_PROPERTIES)) {
			component = propertiesMenuItem;
		} else if (actionCommand.equals(ActionCommands.CHANGE_POTENTIAL)) {
			component = relationMenuItem;
		} else if (actionCommand.equals(ActionCommands.NODE_EXPANSION)) {
			component = expandMenuItem;
		} else if (actionCommand.equals(ActionCommands.NODE_CONTRACTION)) {
			component = contractMenuItem;
		} else if (actionCommand.equals(ActionCommands.NODE_ADD_FINDING)) {
			component = addFindingMenuItem;
		} else if (actionCommand.equals(ActionCommands.NODE_REMOVE_FINDING)) {
			component = removeFindingMenuItem;
		} else if (actionCommand.equals(ActionCommands.LOG)) {
			component = logMenuItem;
		}

		return component;

	}
}
