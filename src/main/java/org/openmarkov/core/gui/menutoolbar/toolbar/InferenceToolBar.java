package org.openmarkov.core.gui.menutoolbar.toolbar;


import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.openmarkov.core.gui.graphic.VisualState;
import org.openmarkov.core.gui.loader.element.IconLoader;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.gui.menutoolbar.common.ActionCommands;




/**
 * This class implements the inference toolbar of the application.
 * This toolbar appears when entering Inference Working Mode
 * 
 * @author asaez
 * @version 1.0
 */
public class InferenceToolBar extends ToolBarBasic {

	/**
	 * Static field for serializable class.
	 */
	private static final long serialVersionUID = 2660820001862866000L;
	
	/**
	 * Label for expansion threshold.
	 */
	private JLabel expansionThreshold = null;
	
	/**
	 * ComboBox to select the expansion threshold.
	 */
	private ExpansionThresholdComboBox expansionThresholdComboBox = null;
	
	/**
	 * Button to create a new evidence case.
	 */
	private JButton createNewEvidenceCaseButton = null;
	
	/**
	 * Button to go the first evidence case.
	 */
	private JButton goToFirstEvidenceCaseButton = null;

	/**
	 * Button to go the previous evidence case.
	 */
	private JButton goToPreviousEvidenceCaseButton = null;
	
	/**
	 * Text field for the name of the current evidence case.
	 */
	private JTextField currentEvidenceCaseName = null;

	/**
	 * Button to go the next evidence case.
	 */
	private JButton goToNextEvidenceCaseButton = null;

	/**
	 * Button to go the last evidence case.
	 */
	private JButton goToLastEvidenceCaseButton = null;
	
	/**
	 * Button to clear out all evidence cases.
	 */
	private JButton clearOutAllEvidenceCasesButton = null;
	
	/**
	 * Button to propagateEvidence.
	 */
	private JButton propagateEvidenceButton = null;
	
	/**
	 * String resource.
	 */
	private StringResource stringResource = null;

	/**
	 * Icon loader.
	 */
	private IconLoader iconLoader = null;

	/**
	 * This method initializes this instance.
	 * 
	 * @param newListener
	 *            object that listens to the buttons events.
	 */
	public InferenceToolBar(ActionListener newListener) {

		super(newListener);
		initialize();
	}

	/**
	 * This method configures the toolbar.
	 */
	private void initialize() {

		stringResource = StringResourceLoader.getUniqueInstance().getBundleToolBars();
		iconLoader = new IconLoader();

		add(getExpansionThresholdLabel());
		add(getExpansionThresholdComboBox());
		addSeparator();
		add(getCreateNewEvidenceCaseButton());
		addSeparator();
		add(getGoToFirstEvidenceCaseButton());
		add(getGoToPreviousEvidenceCaseButton());
		add(getCurrentEvidenceCaseName());
		add(getGoToNextEvidenceCaseButton());
		add(getGoToLastEvidenceCaseButton());
		addSeparator();
		add(getClearOutAllEvidenceCasesButton());	
		addSeparator();
		add(getPropagateEvidenceButton());
		add(Box.createHorizontalGlue());
	}

	/**
	 * This method inserts the Expansion Threshold Label.
	 * 
	 * @return a Expansion Threshold label.
	 */
	private JLabel getExpansionThresholdLabel() {

		if (expansionThreshold == null) {
			expansionThreshold = new JLabel();
			expansionThreshold.setText("  " + stringResource.getString("ExpansionThreshold.Label") + ": ");
		}
		return expansionThreshold;		
	}
	
	/**
	 * This method initializes expansionThresholdComboBox.
	 * 
	 * @return a new expansion threshold comboBox.
	 */
	private ExpansionThresholdComboBox getExpansionThresholdComboBox() {

		if (expansionThresholdComboBox == null) {
			expansionThresholdComboBox = new ExpansionThresholdComboBox(listener);
		}
		return expansionThresholdComboBox;
	}

	/**
	 * This method initializes createNewEvidenceCaseButton.
	 * 
	 * @return a Create New Evidence Case button.
	 */
	private JButton getCreateNewEvidenceCaseButton() {

		if (createNewEvidenceCaseButton == null) {
			createNewEvidenceCaseButton = new JButton();
			createNewEvidenceCaseButton.setIcon(iconLoader.load(IconLoader.ICON_CREATE_NEW_EVIDENCE_CASE_ENABLED));
			createNewEvidenceCaseButton.setFocusable(false);
			createNewEvidenceCaseButton.setActionCommand(ActionCommands.CREATE_NEW_EVIDENCE_CASE);
			createNewEvidenceCaseButton.setToolTipText(stringResource
					.getString(ActionCommands.CREATE_NEW_EVIDENCE_CASE + STRING_TOOLTIP_SUFFIX));
			createNewEvidenceCaseButton.addActionListener(listener);
		}
		return createNewEvidenceCaseButton;		
	}
	
	/**
	 * This method initializes goToFirstEvidenceCaseButton.
	 * 
	 * @return a Go To First Evidence Case button.
	 */
	private JButton getGoToFirstEvidenceCaseButton() {

		if (goToFirstEvidenceCaseButton == null) {
			goToFirstEvidenceCaseButton = new JButton();
			goToFirstEvidenceCaseButton.setIcon(iconLoader.load(IconLoader.ICON_GO_TO_FIRST_EVIDENCE_CASE_ENABLED));
			goToFirstEvidenceCaseButton.setFocusable(false);
			goToFirstEvidenceCaseButton.setActionCommand(ActionCommands.GO_TO_FIRST_EVIDENCE_CASE);
			goToFirstEvidenceCaseButton.setToolTipText(stringResource
					.getString(ActionCommands.GO_TO_FIRST_EVIDENCE_CASE + STRING_TOOLTIP_SUFFIX));
			goToFirstEvidenceCaseButton.addActionListener(listener);
		}
		return goToFirstEvidenceCaseButton;		
	}
	
	/**
	 * This method initializes goToPreviousEvidenceCaseButton.
	 * 
	 * @return a Go To Previous Evidence Case button.
	 */
	private JButton getGoToPreviousEvidenceCaseButton() {

		if (goToPreviousEvidenceCaseButton == null) {
			goToPreviousEvidenceCaseButton = new JButton();
			goToPreviousEvidenceCaseButton.setIcon(iconLoader.load(IconLoader.ICON_GO_TO_PREVIOUS_EVIDENCE_CASE_ENABLED));
			goToPreviousEvidenceCaseButton.setFocusable(false);
			goToPreviousEvidenceCaseButton.setActionCommand(ActionCommands.GO_TO_PREVIOUS_EVIDENCE_CASE);
			goToPreviousEvidenceCaseButton.setToolTipText(stringResource
					.getString(ActionCommands.GO_TO_PREVIOUS_EVIDENCE_CASE + STRING_TOOLTIP_SUFFIX));
			goToPreviousEvidenceCaseButton.addActionListener(listener);
		}
		return goToPreviousEvidenceCaseButton;		
	}
	
	/**
	 * This method initializes the text field of the Current Evidence Case.
	 * 
	 * @return a Text Field with the name of the evidence case.
	 */
	private JTextField getCurrentEvidenceCaseName() {

		if (currentEvidenceCaseName == null) {
			currentEvidenceCaseName = new JTextField();
			Font font = new Font(currentEvidenceCaseName.getFont().getName(), Font.PLAIN, 
					currentEvidenceCaseName.getFont().getSize()+2);
			currentEvidenceCaseName.setFont(font);
			currentEvidenceCaseName.setBackground(VisualState.EVIDENCE_CASE_0_COLOR);
			currentEvidenceCaseName.setForeground(Color.WHITE);
			currentEvidenceCaseName.setText("   " + stringResource.getString("CaseNumber.Label") + " 0   ");
			currentEvidenceCaseName.setMaximumSize(currentEvidenceCaseName.getPreferredSize());
			currentEvidenceCaseName.setHorizontalAlignment(JTextField.CENTER);
			currentEvidenceCaseName.setEditable(false);
		}
		return currentEvidenceCaseName;		
	}
	
	/**
	 * This method initializes goToNextEvidenceCaseButton.
	 * 
	 * @return a Go To Next Evidence Case button.
	 */
	private JButton getGoToNextEvidenceCaseButton() {

		if (goToNextEvidenceCaseButton == null) {
			goToNextEvidenceCaseButton = new JButton();
			goToNextEvidenceCaseButton.setIcon(iconLoader.load(IconLoader.ICON_GO_TO_NEXT_EVIDENCE_CASE_ENABLED));
			goToNextEvidenceCaseButton.setFocusable(false);
			goToNextEvidenceCaseButton.setActionCommand(ActionCommands.GO_TO_NEXT_EVIDENCE_CASE);
			goToNextEvidenceCaseButton.setToolTipText(stringResource
					.getString(ActionCommands.GO_TO_NEXT_EVIDENCE_CASE + STRING_TOOLTIP_SUFFIX));
			goToNextEvidenceCaseButton.addActionListener(listener);
		}
		return goToNextEvidenceCaseButton;		
	}
	
	/**
	 * This method initializes goToLastEvidenceCaseButton.
	 * 
	 * @return a Go To Last Evidence Case button.
	 */
	private JButton getGoToLastEvidenceCaseButton() {

		if (goToLastEvidenceCaseButton == null) {
			goToLastEvidenceCaseButton = new JButton();
			goToLastEvidenceCaseButton.setIcon(iconLoader.load(IconLoader.ICON_GO_TO_LAST_EVIDENCE_CASE_ENABLED));
			goToLastEvidenceCaseButton.setFocusable(false);
			goToLastEvidenceCaseButton.setActionCommand(ActionCommands.GO_TO_LAST_EVIDENCE_CASE);
			goToLastEvidenceCaseButton.setToolTipText(stringResource
					.getString(ActionCommands.GO_TO_LAST_EVIDENCE_CASE + STRING_TOOLTIP_SUFFIX));
			goToLastEvidenceCaseButton.addActionListener(listener);
		}
		return goToLastEvidenceCaseButton;		
	}

	/**
	 * This method initializes clearOutAllEvidenceCasesButton.
	 * 
	 * @return a Clear Out All Evidence Cases button.
	 */
	private JButton getClearOutAllEvidenceCasesButton() {

		if (clearOutAllEvidenceCasesButton == null) {
			clearOutAllEvidenceCasesButton = new JButton();
			clearOutAllEvidenceCasesButton.setIcon(iconLoader.load(IconLoader.ICON_CLEAR_OUT_ALL_EVIDENCE_CASES_ENABLED));
			clearOutAllEvidenceCasesButton.setFocusable(false);
			clearOutAllEvidenceCasesButton.setActionCommand(ActionCommands.CLEAR_OUT_ALL_EVIDENCE_CASES);
			clearOutAllEvidenceCasesButton.setToolTipText(stringResource
					.getString(ActionCommands.CLEAR_OUT_ALL_EVIDENCE_CASES + STRING_TOOLTIP_SUFFIX));
			clearOutAllEvidenceCasesButton.addActionListener(listener);
		}
		return clearOutAllEvidenceCasesButton;		
	}
	
	/**
	 * This method initialises propagateEvidenceButton.
	 * 
	 * @return a Propagate evidence button.
	 */
	private JButton getPropagateEvidenceButton() {

		if (propagateEvidenceButton == null) {
			propagateEvidenceButton = new JButton();
			propagateEvidenceButton.setIcon(iconLoader.load(IconLoader.ICON_PROPAGATE_EVIDENCE_ENABLED));
			propagateEvidenceButton.setFocusable(false);
			propagateEvidenceButton.setActionCommand(ActionCommands.PROPAGATE_EVIDENCE);
			propagateEvidenceButton.setToolTipText(stringResource
					.getString(ActionCommands.PROPAGATE_EVIDENCE + STRING_TOOLTIP_SUFFIX));
			propagateEvidenceButton.addActionListener(listener);
		}
		return propagateEvidenceButton;		
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

		if (actionCommand.equals(ActionCommands.PROPAGATE_EVIDENCE)) {
			component = propagateEvidenceButton;
		} else if (actionCommand.equals(ActionCommands.CREATE_NEW_EVIDENCE_CASE)) {
			component = createNewEvidenceCaseButton;
		} else if (actionCommand.equals(ActionCommands.GO_TO_FIRST_EVIDENCE_CASE)) {
			component = goToFirstEvidenceCaseButton;
		} else if (actionCommand.equals(ActionCommands.GO_TO_PREVIOUS_EVIDENCE_CASE)) {
			component = goToPreviousEvidenceCaseButton;
		} else if (actionCommand.equals(ActionCommands.GO_TO_NEXT_EVIDENCE_CASE)) {
			component = goToNextEvidenceCaseButton;
		} else if (actionCommand.equals(ActionCommands.GO_TO_LAST_EVIDENCE_CASE)) {
			component = goToLastEvidenceCaseButton;
		} else if (actionCommand.equals(ActionCommands.CLEAR_OUT_ALL_EVIDENCE_CASES)) {
			component = clearOutAllEvidenceCasesButton;
		}
		return component;
	}
	
	/**
	 * This method establishes the expansion threshold to be shown in the ComboBox.
	 * 
	 * @param expansionThreshold 
	 * 				The expansion threshold to be shown in the ComboBox.
	 */
	public void setExpansionThreshold (double expansionThreshold) {
		expansionThresholdComboBox.setExpansionThreshold(expansionThreshold);
	}	

	/**
	 * This method sets the text and the background color to be shown in the text field
	 * 
	 * @param currentCase 
	 * 				The number of the current case.
	 */
	public void setCurrentEvidenceCaseName (int currentCase, boolean automaticPropagation) {
		if (automaticPropagation) {
			if (currentCase < 10) {
				currentEvidenceCaseName.setText("   " + stringResource.getString("CaseNumber.Label")
						+ " " + currentCase + "   ");
			} else {
				currentEvidenceCaseName.setText(stringResource.getString("CaseNumber.Label")
						+ " " + currentCase);
			}
			if (currentCase%5 == 0) {
				currentEvidenceCaseName.setBackground(VisualState.EVIDENCE_CASE_0_COLOR);
				currentEvidenceCaseName.setForeground(Color.WHITE);
			} else if (currentCase%5 == 1) {
				currentEvidenceCaseName.setBackground(VisualState.EVIDENCE_CASE_1_COLOR);
				currentEvidenceCaseName.setForeground(Color.WHITE);
			} else if (currentCase%5 == 2) {
				currentEvidenceCaseName.setBackground(VisualState.EVIDENCE_CASE_2_COLOR);
				currentEvidenceCaseName.setForeground(Color.WHITE);
			} else if (currentCase%5 == 3) {
				currentEvidenceCaseName.setBackground(VisualState.EVIDENCE_CASE_3_COLOR);
				currentEvidenceCaseName.setForeground(Color.BLACK);
			} else if (currentCase%5 == 4) {
				currentEvidenceCaseName.setBackground(VisualState.EVIDENCE_CASE_4_COLOR);
				currentEvidenceCaseName.setForeground(Color.BLACK);
			}
		} else {
			currentEvidenceCaseName.setBackground(Color.WHITE);
			currentEvidenceCaseName.setForeground(Color.BLACK);
			currentEvidenceCaseName.setText(" " + stringResource.getString("NotCompiledNet.Label")
					+ " (" + currentCase + ") ");			
		}
	}	


	
}
