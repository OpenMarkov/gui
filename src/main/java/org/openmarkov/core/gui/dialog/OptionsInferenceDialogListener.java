/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.gui.menutoolbar.toolbar.InferenceToolBar;
import org.openmarkov.core.gui.window.edition.EditorPanel;
import org.openmarkov.core.gui.window.edition.NetworkPanel;


/**
 * Listener associated to OptionsInferenceDialog.
 * 
 * @author asaez
 * @version 1.0
 */
public class OptionsInferenceDialogListener implements ActionListener {
	
	/**
	 * String resource.
	 */
	private StringResource stringResource = null;
	
	/**
	 * The Dialog to which this listener is associated 
	 */
	OptionsInferenceDialog optionsInferenceDialog = null;
	
	/**
	 * The editor panel that called the associated dialog.
	 */
	EditorPanel editorPanel = null;
	
	/**
	 * The inference tool bar associated to the panel.
	 */
	InferenceToolBar inferenceToolBar = null;
	
	/**
	 * constructor 
	 */
	public OptionsInferenceDialogListener(OptionsInferenceDialog optionsInferenceDialog, 
			EditorPanel editorPanel, InferenceToolBar inferenceToolBar) {
		this.optionsInferenceDialog = optionsInferenceDialog;
		this.editorPanel = editorPanel;
		this.inferenceToolBar = inferenceToolBar;
		stringResource = StringResourceLoader.getUniqueInstance().getBundleDialogs();
	}
	
	/**
	 * Invoked when an action occurs.
	 * 
	 * @param actionEvent
	 *            event information.
	 */
	public void actionPerformed(ActionEvent actionEvent) {
		
		String command = actionEvent.getActionCommand();
		
		String inferenceType = optionsInferenceDialog.getButtonGroup().getSelection().getActionCommand();
		
		if (command.equals(stringResource.getString("OptionsInferenceDialog.jButtonOK.Label"))) {
			if (inferenceType.equals(stringResource.
					getString("OptionsInferenceDialog.optionAuto.Label"))) {
				editorPanel.setAutomaticPropagation(true);
				editorPanel.setPropagationActive(true);
				if (editorPanel.getNetworkPanel().getWorkingMode() == NetworkPanel.INFERENCE_WORKING_MODE) {
					for (int i=0; i<editorPanel.getNumberOfCases(); i++) { 
						if (editorPanel.getEvidenceCasesCompilationState(i) == false) {
							editorPanel.doPropagation(editorPanel.getEvidenceCase(i), i);
							editorPanel.updateAllVisualStates("", i);							
						}
					}
					editorPanel.setSelectedAllNodes(false);
					inferenceToolBar.setCurrentEvidenceCaseName(editorPanel.getCurrentCase());
					editorPanel.updateNodesFindingState(editorPanel.getCurrentEvidenceCase());
				}
			} else if (inferenceType.equals(stringResource.
					getString("OptionsInferenceDialog.optionManual.Label"))) {
				editorPanel.setAutomaticPropagation(false);
				if (editorPanel.getNetworkPanel().getWorkingMode() == NetworkPanel.INFERENCE_WORKING_MODE) {
					inferenceToolBar.setCurrentEvidenceCaseName(editorPanel.getCurrentCase()); 
				}
			}
		} else if (command.equals(stringResource.getString("OptionsInferenceDialog.jButtonCancel.Label"))) {
			//do nothing
		}
		optionsInferenceDialog.setVisible(false);
		
	}

}
