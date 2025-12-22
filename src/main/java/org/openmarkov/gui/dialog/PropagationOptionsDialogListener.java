/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.dialog;

import org.openmarkov.core.exception.*;
import org.openmarkov.core.localize.StringDatabase;
import org.openmarkov.gui.exception.NotEnoughtMemoryException;
import org.openmarkov.gui.menutoolbar.toolbar.InferenceToolBar;
import org.openmarkov.gui.window.edition.EditorPanel;
import org.openmarkov.gui.window.edition.NetworkPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listener associated to OptionsInferenceDialog.
 *
 * @author asaez
 * @version 1.0
 */
public class PropagationOptionsDialogListener implements ActionListener {
	/**
	 * The Dialog to which this listener is associated
	 */
    PropagationOptionsDialog automaticPropagationOptionsDialog;
	/**
	 * The editor panel that called the associated dialog.
	 */
    EditorPanel editorPanel;
	/**
	 * The inference tool bar associated to the panel.
	 */
    InferenceToolBar inferenceToolBar;

	/**
	 * constructor
	 */
	public PropagationOptionsDialogListener(PropagationOptionsDialog optionsInferenceDialog, EditorPanel editorPanel,
			InferenceToolBar inferenceToolBar) {
		this.automaticPropagationOptionsDialog = optionsInferenceDialog;
		this.editorPanel = editorPanel;
		this.inferenceToolBar = inferenceToolBar;
	}

	/**
	 * Invoked when an action occurs.
	 *
	 * @param actionEvent event information.
	 */
	@Override public void actionPerformed(ActionEvent actionEvent) {
		String command = actionEvent.getActionCommand();
		String inferenceType = automaticPropagationOptionsDialog.getButtonGroup().getSelection().getActionCommand();
		StringDatabase stringDatabase = StringDatabase.getUniqueInstance();
        if (command.equals(stringDatabase.getString("OptionsInferenceDialog.jButtonOK"))) {
            if (inferenceType.equals(stringDatabase.getString("OptionsInferenceDialog.optionAuto"))) {
				editorPanel.setAutomaticPropagation(true);
				editorPanel.setPropagationActive(true);
                if (editorPanel.getNetworkPanel().getWorkingMode() == NetworkPanel.WorkingMode.INFERENCE) {
					for (int caseIndex = 0; caseIndex < editorPanel.getNumberOfCases(); caseIndex++) {
                        if (!editorPanel.getEvidenceCasesCompilationState(caseIndex)) {
                            try {
                                editorPanel.doPropagation(editorPanel.getEvidenceCase(caseIndex), caseIndex);
                            } catch (NotEvaluableNetworkException | NonProjectablePotentialException |
                                     CannotNormalizePotentialException | NotEnoughtMemoryException |
                                     IncompatibleEvidenceException | ConstraintViolatedException e) {
                                throw new UnrecoverableException(e);
                            }
                            editorPanel.updateAllVisualStates("", caseIndex);
						}
					}
					editorPanel.setSelectedAllNodes(false);
					inferenceToolBar.setCurrentEvidenceCaseName(editorPanel.getCurrentCase());
					editorPanel.updateNodesFindingState(editorPanel.getCurrentEvidenceCase());
				}
            } else if (inferenceType.equals(stringDatabase.getString("OptionsInferenceDialog.optionManual"))) {
				editorPanel.setAutomaticPropagation(false);
                if (editorPanel.getNetworkPanel().getWorkingMode() == NetworkPanel.WorkingMode.INFERENCE) {
					inferenceToolBar.setCurrentEvidenceCaseName(editorPanel.getCurrentCase());
				}
			}
        } else if (command.equals(stringDatabase.getString("OptionsInferenceDialog.jButtonCancel"))) {
			// do nothing
		}
		automaticPropagationOptionsDialog.setVisible(false);
	}
}
