/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.menutoolbar.toolbar;


import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToggleButton;

import org.openmarkov.core.gui.loader.element.IconLoader;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.gui.menutoolbar.common.ActionCommands;




/**
 * This class implements the edition toolbar of the application.
 * 
 * @author jmendoza
 */
public class EditionToolBar extends ToolBarBasic {

	/**
	 * Static field for serializable class.
	 */
	private static final long serialVersionUID = 2660826021862866432L;

	/**
	 * Button to invoke cut.
	 */
	private JButton cutButton = null;

	/**
	 * Button to invoke copy.
	 */
	private JButton copyButton = null;

	/**
	 * Button to invoke paste.
	 */
	private JButton pasteButton = null;

	/**
	 * Button to invoke remove.
	 */
	private JButton removeButton = null;

	/**
	 * Button to invoke undo.
	 */
	private JButton undoButton = null;

	/**
	 * Button to invoke redo.
	 */
	private JButton redoButton = null;

	/**
	 * Button to activate object selection.
	 */
	private JToggleButton objectSelectionButton = null;

	/**
	 * Button to activate chance creation.
	 */
	private JToggleButton chanceCreationButton = null;

	/**
	 * Button to activate decision creation.
	 */
	private JToggleButton decisionCreationButton = null;

	/**
	 * Button to activate utility creation.
	 */
	private JToggleButton utilityCreationButton = null;

	/**
	 * Button to activate link creation.
	 */
	private JToggleButton linkCreationButton = null;
	
	/**
	 * Button to activate instance creation.
	 */
	private JToggleButton instanceCreationButton = null;	
	
	/**
	 * Combobox to select class to instantiate.
	 */
	private ClassComboBox classComboBox = null;
	

	/**
	 * Button group to make autoexclusive the edition options.
	 */
	private ButtonGroup groupEditionOptions = new ButtonGroup();

	/**
	 * String resource.
	 */
	private StringResource stringResource = null;

	/**
	 * Icon loader.
	 */
	private IconLoader iconLoader = null;

	/**
	 * This method initialises this instance.
	 * 
	 * @param newListener
	 *            object that listens to the buttons events.
	 */
	public EditionToolBar(ActionListener newListener) {

		super(newListener);
		initialize();
	}

	/**
	 * This method configures the toolbar.
	 */
	private void initialize() {

		stringResource =
			StringResourceLoader.getUniqueInstance().getBundleToolBars();
		iconLoader = new IconLoader();
		add(getCutButton());
		add(getCopyButton());
		add(getPasteButton());
		add(getRemoveButton());
		addSeparator();
		add(getUndoButton());
		add(getRedoButton());
		addSeparator();
		add(getObjectSelectionButton());
		add(getChanceCreationButton());
		add(getDecisionCreationButton());
		add(getUtilityCreationButton());
		add(getLinkCreationButton());	
		add(getInstanceCreationButton());
		add(getClassComboBox());
		add(Box.createHorizontalGlue());
	}

	/**
	 * This method initialises cutButton.
	 * 
	 * @return a cut button.
	 */
	private JButton getCutButton() {

		if (cutButton == null) {
			cutButton = new JButton();
			cutButton.setIcon(iconLoader.load(IconLoader.ICON_CUT_ENABLED));
			cutButton.setFocusable(false);
			cutButton.setActionCommand(ActionCommands.CLIPBOARD_CUT);
			cutButton
				.setToolTipText(stringResource
					.getString(ActionCommands.CLIPBOARD_CUT
						+ STRING_TOOLTIP_SUFFIX));
			cutButton.addActionListener(listener);
		}
		return cutButton;
	}

	/**
	 * This method initialises copyButton.
	 * 
	 * @return a copy button.
	 */
	private JButton getCopyButton() {

		if (copyButton == null) {
			copyButton = new JButton();
			copyButton.setIcon(iconLoader.load(IconLoader.ICON_COPY_ENABLED));
			copyButton.setFocusable(false);
			copyButton.setActionCommand(ActionCommands.CLIPBOARD_COPY);
			copyButton.setToolTipText(stringResource
				.getString(ActionCommands.CLIPBOARD_COPY
					+ STRING_TOOLTIP_SUFFIX));
			copyButton.addActionListener(listener);
		}
		return copyButton;
	}

	/**
	 * This method initialises pasteButton.
	 * 
	 * @return a paste button.
	 */
	private JButton getPasteButton() {

		if (pasteButton == null) {
			pasteButton = new JButton();
			pasteButton.setIcon(iconLoader.load(IconLoader.ICON_PASTE_ENABLED));
			pasteButton.setFocusable(false);
			pasteButton.setActionCommand(ActionCommands.CLIPBOARD_PASTE);
			pasteButton.setToolTipText(stringResource
				.getString(ActionCommands.CLIPBOARD_PASTE
					+ STRING_TOOLTIP_SUFFIX));
			pasteButton.addActionListener(listener);
		}
		return pasteButton;
	}

	/**
	 * This method initialises removeButton.
	 * 
	 * @return a remove button.
	 */
	private JButton getRemoveButton() {

		if (removeButton == null) {
			removeButton = new JButton();
			removeButton.setIcon(iconLoader
				.load(IconLoader.ICON_REMOVE_ENABLED));
			removeButton.setFocusable(false);
			removeButton.setActionCommand(ActionCommands.OBJECT_REMOVAL);
			removeButton.setToolTipText(stringResource
				.getString(ActionCommands.OBJECT_REMOVAL
					+ STRING_TOOLTIP_SUFFIX));
			removeButton.addActionListener(listener);
		}
		return removeButton;
	}

	/**
	 * This method initialises undoButton.
	 * 
	 * @return a undo button.
	 */
	private JButton getUndoButton() {

		if (undoButton == null) {
			undoButton = new JButton();
			undoButton.setIcon(iconLoader.load(IconLoader.ICON_UNDO_ENABLED));
			undoButton.setFocusable(false);
			undoButton.setActionCommand(ActionCommands.UNDO);
			undoButton.setToolTipText(stringResource
				.getString(ActionCommands.UNDO + STRING_TOOLTIP_SUFFIX));
			undoButton.addActionListener(listener);
		}
		return undoButton;
	}

	/**
	 * This method initialises redoButton.
	 * 
	 * @return a redo button.
	 */
	private JButton getRedoButton() {

		if (redoButton == null) {
			redoButton = new JButton();
			redoButton.setIcon(iconLoader.load(IconLoader.ICON_REDO_ENABLED));
			redoButton.setFocusable(false);
			redoButton.setActionCommand(ActionCommands.REDO);
			redoButton.setToolTipText(stringResource
				.getString(ActionCommands.REDO + STRING_TOOLTIP_SUFFIX));
			redoButton.addActionListener(listener);
		}
		return redoButton;
	}

	/**
	 * This method initialises objectSelectionButton.
	 * 
	 * @return a object selection button.
	 */
	private JToggleButton getObjectSelectionButton() {

		if (objectSelectionButton == null) {
			objectSelectionButton = new JToggleButton();
			objectSelectionButton.setIcon(iconLoader
				.load(IconLoader.ICON_SELECTION_ENABLED));
			objectSelectionButton
				.setActionCommand(ActionCommands.OBJECT_SELECTION);
			objectSelectionButton.setFocusable(false);
			objectSelectionButton.setToolTipText(stringResource
				.getString(ActionCommands.OBJECT_SELECTION
					+ STRING_TOOLTIP_SUFFIX));
			objectSelectionButton.addActionListener(listener);
			groupEditionOptions.add(objectSelectionButton);
		}
		return objectSelectionButton;
	}

	/**
	 * This method initialises chanceCreationButton.
	 * 
	 * @return a chance creation button.
	 */
	private JToggleButton getChanceCreationButton() {

		if (chanceCreationButton == null) {
			chanceCreationButton = new JToggleButton();
			chanceCreationButton.setIcon(iconLoader
				.load(IconLoader.ICON_CHANCE_ENABLED));
			chanceCreationButton
				.setActionCommand(ActionCommands.CHANCE_CREATION);
			chanceCreationButton.setFocusable(false);
			chanceCreationButton.setToolTipText(stringResource
				.getString(ActionCommands.CHANCE_CREATION
					+ STRING_TOOLTIP_SUFFIX));
			chanceCreationButton.addActionListener(listener);
			groupEditionOptions.add(chanceCreationButton);
		}
		return chanceCreationButton;
	}

	/**
	 * This method initialises decisionCreationButton.
	 * 
	 * @return a decision creation button.
	 */
	private JToggleButton getDecisionCreationButton() {

		if (decisionCreationButton == null) {
			decisionCreationButton = new JToggleButton();
			decisionCreationButton.setIcon(iconLoader
				.load(IconLoader.ICON_DECISION_ENABLED));
			decisionCreationButton
				.setActionCommand(ActionCommands.DECISION_CREATION);
			decisionCreationButton.setFocusable(false);
			decisionCreationButton.setToolTipText(stringResource
				.getString(ActionCommands.DECISION_CREATION
					+ STRING_TOOLTIP_SUFFIX));
			decisionCreationButton.addActionListener(listener);
			groupEditionOptions.add(decisionCreationButton);
		}
		return decisionCreationButton;
	}

	/**
	 * This method initialises utilityCreationButton.
	 * 
	 * @return a utility creation button.
	 */
	private JToggleButton getUtilityCreationButton() {

		if (utilityCreationButton == null) {
			utilityCreationButton = new JToggleButton();
			utilityCreationButton.setIcon(iconLoader
				.load(IconLoader.ICON_UTILITY_ENABLED));
			utilityCreationButton
				.setActionCommand(ActionCommands.UTILITY_CREATION);
			utilityCreationButton.setFocusable(false);
			utilityCreationButton.setToolTipText(stringResource
				.getString(ActionCommands.UTILITY_CREATION
					+ STRING_TOOLTIP_SUFFIX));
			utilityCreationButton.addActionListener(listener);
			groupEditionOptions.add(utilityCreationButton);
		}
		return utilityCreationButton;
	}

	/**
	 * This method initialises linkCreationButton.
	 * 
	 * @return a link creation button.
	 */
	private JToggleButton getLinkCreationButton() {

		if (linkCreationButton == null) {
			linkCreationButton = new JToggleButton();
			linkCreationButton.setIcon(iconLoader
				.load(IconLoader.ICON_LINK_ENABLED));
			linkCreationButton.setActionCommand(ActionCommands.LINK_CREATION);
			linkCreationButton.setFocusable(false);
			linkCreationButton
				.setToolTipText(stringResource
					.getString(ActionCommands.LINK_CREATION
						+ STRING_TOOLTIP_SUFFIX));
			linkCreationButton.addActionListener(listener);
			groupEditionOptions.add(linkCreationButton);
		}
		return linkCreationButton;
	}
	
	/**
	 * This method initialises instanceCreationButton.
	 * 
	 * @return a link creation button.
	 */
	private JToggleButton getInstanceCreationButton() {

		if (instanceCreationButton == null) {
			instanceCreationButton = new JToggleButton();
			instanceCreationButton.setIcon(iconLoader
				.load(IconLoader.ICON_INSTANCE_ENABLED));
			instanceCreationButton.setActionCommand(ActionCommands.INSTANCE_CREATION);
			instanceCreationButton.setFocusable(false);
			instanceCreationButton
				.setToolTipText(stringResource
					.getString(ActionCommands.INSTANCE_CREATION
						+ STRING_TOOLTIP_SUFFIX));
			instanceCreationButton.addActionListener(listener);
			groupEditionOptions.add(instanceCreationButton);
		}
		return instanceCreationButton;
	}	
	
	/**  This method initialises classComboBox.
	 * 
	 * @return a class combo box.
	 */
	public ClassComboBox getClassComboBox() {

		if (classComboBox == null) {
			classComboBox = new ClassComboBox(listener);
			classComboBox.setEnabled(false);
		}
		return classComboBox;
	}	

	/**
	 * Returns the component that correspond to an action command.
	 * 
	 * @param actionCommand
	 *            action command that identifies the component.
	 * @return a components identified by the action command.
	 */
	@Override
	protected JComponent getJComponentActionCommand(String actionCommand) {

		JComponent component = null;

		if (actionCommand.equals(ActionCommands.CLIPBOARD_CUT)) {
			component = cutButton;
		} else if (actionCommand.equals(ActionCommands.CLIPBOARD_COPY)) {
			component = copyButton;
		} else if (actionCommand.equals(ActionCommands.CLIPBOARD_PASTE)) {
			component = pasteButton;
		} else if (actionCommand.equals(ActionCommands.OBJECT_REMOVAL)) {
			component = removeButton;
		} else if (actionCommand.equals(ActionCommands.UNDO)) {
			component = undoButton;
		} else if (actionCommand.equals(ActionCommands.REDO)) {
			component = redoButton;
		} else if (actionCommand.equals(ActionCommands.OBJECT_SELECTION)) {
			component = objectSelectionButton;
		} else if (actionCommand.equals(ActionCommands.CHANCE_CREATION)) {
			component = chanceCreationButton;
		} else if (actionCommand.equals(ActionCommands.DECISION_CREATION)) {
			component = decisionCreationButton;
		} else if (actionCommand.equals(ActionCommands.UTILITY_CREATION)) {
			component = utilityCreationButton;
		} else if (actionCommand.equals(ActionCommands.LINK_CREATION)) {
			component = linkCreationButton;
		}else if (actionCommand.equals(ActionCommands.INSTANCE_CREATION)) {
			component = instanceCreationButton;
		}
		return component;
	}
}
