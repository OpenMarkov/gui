/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.dialog;

import org.openmarkov.core.exception.*;
import org.openmarkov.core.localize.StringDatabase;
import org.openmarkov.gui.exception.NotEnoughMemoryException;
import org.openmarkov.gui.menutoolbar.toolbar.InferenceToolBar;
import org.openmarkov.gui.window.edition.editorPanel.EditorPanel;
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
					for (int caseIndex = 0; caseIndex < editorPanel.getEvidenceManager().getNumberOfCases(); caseIndex++) {
                        if (!editorPanel.getEvidenceManager().getEvidenceCasesCompilationState(caseIndex)) {
                            try {
                                editorPanel.getEvidenceManager().doPropagation(editorPanel.getEvidenceManager().getEvidenceCase(caseIndex), caseIndex);
                            } catch (NotEvaluableNetworkException | NonProjectablePotentialException |
                                     CannotNormalizePotentialException | NotEnoughMemoryException |
                                     IncompatibleEvidenceException | ConstraintViolatedException e) {
                                throw new UnrecoverableException(e);
                            }
                            editorPanel.updateAllVisualStates("", caseIndex);
						}
					}
					editorPanel.getVisualNetwork().setSelectedAllNodes(false);
					inferenceToolBar.setCurrentEvidenceCaseName(editorPanel.getEvidenceManager().getCurrentCase());
					editorPanel.getEvidenceManager().updateNodesFindingState(editorPanel.getEvidenceManager().getCurrentEvidenceCase());
				}
            } else if (inferenceType.equals(stringDatabase.getString("OptionsInferenceDialog.optionManual"))) {
				editorPanel.setAutomaticPropagation(false);
                if (editorPanel.getNetworkPanel().getWorkingMode() == NetworkPanel.WorkingMode.INFERENCE) {
					inferenceToolBar.setCurrentEvidenceCaseName(editorPanel.getEvidenceManager().getCurrentCase());
				}
			}
        } else if (command.equals(stringDatabase.getString("OptionsInferenceDialog.jButtonCancel"))) {
			// do nothing
		}
		automaticPropagationOptionsDialog.setVisible(false);
	}
}
