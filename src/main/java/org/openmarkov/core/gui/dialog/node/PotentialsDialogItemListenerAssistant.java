/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog.node;


import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JOptionPane;



/**
 * This class assists to the PotentialsDialog with ItemListener events
 * 
 * @author mpalacios
 * @version 1.0 23/06/2011
 */

public class PotentialsDialogItemListenerAssistant implements
				ItemListener {

	/**
	 * panel for handling events
	 */
	private PotentialEditDialog potentialEditDialog = null;

	/**
	 * model selected (probabilistic, deterministic or optimal
	 */
	private static int PROBABILISTIC_SELECTED = 0;
	private static int DETERMINISTIC_SELECTED = 1;
	private static int OPTIMAL_SELECTED = 2;

	/**
	 * constructor without construction parameters
	 */
	public PotentialsDialogItemListenerAssistant() {

	}

	/**
	 * constructor
	 * 
	 * @param panel
	 */
	public PotentialsDialogItemListenerAssistant(
								PotentialEditDialog potentialsDialog) {

		setPotentialsDialog(potentialsDialog);
	}

	/**
	 * @return the panel
	 */
	public PotentialEditDialog getPotentialEditDialog() {

		return potentialEditDialog;
	}

	/**
	 * @param panel
	 *            the panel to set
	 */
	public void setPotentialsDialog(PotentialEditDialog potentialEditDialog) {

		this.potentialEditDialog = potentialEditDialog;
	}

	/**
	 * Invoked when an item has been selected.
	 * 
	 * @param e
	 *            event information.
	 */
	public void itemStateChanged(ItemEvent e) {

		if (e.getItemSelectable().equals(getPotentialEditDialog().getPotentialTypeJCombobox())) {
			itemStateChangedComboBoxRelationType(e.getStateChange());
		}
		/*if (e.getItem().equals(getPotentialEditDialog().getJRadioButtonProbabilisticType())) {
			itemStateChangedProbabilisticType(e.getStateChange());
		}
		if (e.getItem().equals(getPotentialEditDialog().getJRadioButtonDeterministicType())) {
			itemStateChangedDeterministicType(e.getStateChange());
		}
		if (e.getItem().equals(getPotentialEditDialog().getJRadioButtonOptimalType())) {
			itemStateChangedOptimal(e.getStateChange());
		}
		if (e.getItem().equals(getPotentialsDialog().getJRadioButtonTPC())) {
			itemStateChangedTPC(e.getStateChange());
		}
		if (e.getItem().equals(getPotentialsDialog().getJRadioButtonCanonical())) {
			itemStateChangedCanonical(e.getStateChange());
		}
		if (e.getItem().equals(getPotentialsDialog().getJRadioButtonNeto())) {
			itemStateChangedNeto(e.getStateChange());
		}
		if (e.getItem().equals(getPotentialsDialog().getJRadioButtonCompound())) {
			itemStateChangedCompound(e.getStateChange());
		}
		if (e.getItem().equals(getPotentialsDialog().getJRadioButtonProbabilities())) {
			itemStateChangedShowProbabilities(e.getStateChange());
		}
		if (e.getItem().equals(getPotentialsDialog().getJRadioButtonValues())) {
			itemStateChangedShowValues(e.getStateChange());
		}
		if (e.getItem().equals(getPotentialsDialog().getJRadioButtonAll())) {
			itemStateChangedShowAll(e.getStateChange());
		}
		if (e.getItem().equals(getPotentialsDialog().getJRadioButtonIndependant())) {
			itemStateChangedShowIndependant(e.getStateChange());
		}
		 */
	}

	/**
	 * Invoked when the RelationType ComboBox changes
	 */
	private void itemStateChangedComboBoxRelationType(int itemEvent) {

		if (itemEvent == ItemEvent.SELECTED) {
			int index =
				getPotentialEditDialog().getPotentialTypeJCombobox().getSelectedIndex();
			
			//TODO activar las siguientes instrucciones cuando la validación
			//de visualizaciones sea activada
			
			/*String familia = RelationTypes.getByIndex(index);
			if (familia.equals(RelationTypes.RELATION_TYPE_UNIFORM)) {
				// is a general relation
				getPotentialsDialog().getJPanelTpcOrCanonical().setEnabled(false);
				getPotentialsDialog().getJRadioButtonCanonical().setSelected(false);
				getPotentialsDialog().getJRadioButtonCanonical().setEnabled(false);
				getPotentialsDialog().getJRadioButtonTPC().setSelected(false);
				getPotentialsDialog().getJRadioButtonTPC().setEnabled(false);
				getPotentialsDialog().getJPanelNetoOrCompound().setEnabled(false);
				getPotentialsDialog().getJRadioButtonCompound().setSelected(false);
				getPotentialsDialog().getJRadioButtonCompound().setEnabled(false);
				getPotentialsDialog().getJRadioButtonNeto().setSelected(false);
				getPotentialsDialog().getJRadioButtonNeto().setEnabled(false);
				getPotentialsDialog().getNodePotentialsTablePanel().setGeneralModel(index);
			} else {
				// is a canonical relation
				getPotentialsDialog().getJPanelTpcOrCanonical().setEnabled(true);
				getPotentialsDialog().getJRadioButtonCanonical().setEnabled(true);
				getPotentialsDialog().getJRadioButtonTPC().setEnabled(true);
				getPotentialsDialog().getNodePotentialsTablePanel().setCanonicalModel(
					index);
			}*/
		}
	}
	

	/**
	 * Invoked when the Optimal Check Button changes
	 */
	private void itemStateChangedOptimal(int itemEvent) {

		int result;
		if (itemEvent == ItemEvent.SELECTED) {
			if (getPotentialEditDialog().getPreviousPolicy() == -1) {
				// if it is the first time, no ask
				result = JOptionPane.YES_OPTION;
			} else {
				result =
					JOptionPane
						.showConfirmDialog(
							getPotentialEditDialog(),
							getPotentialEditDialog()
								.getStringResource()
								.getString(
									"NodeProbsValuesTablePanel.Msg.ChangeToOptimal.Text"),
							getPotentialEditDialog()
								.getStringResource()
								.getString(
									"NodeProbsValuesTablePanel.Msg.ChangeToOptimal.Title"),
							JOptionPane.YES_NO_OPTION);
			}
			/*if (result == JOptionPane.YES_OPTION) {
				getPotentialsDialog().hideElementsWhenOptimalSelected();
				getPotentialsDialog().setPreviousPolicy(OPTIMAL_SELECTED);
				getPotentialsDialog().getNodePotentialsTablePanel().setOptimalModel();
			} else {
				// restore previous selection
				if (getPotentialsDialog().getPreviousPolicy() == PROBABILISTIC_SELECTED) {
					getPotentialsDialog().getJRadioButtonProbabilisticType().setSelected(
						true);
				} else if (getPotentialsDialog().getPreviousPolicy() == DETERMINISTIC_SELECTED) {
					getPotentialsDialog().getJRadioButtonDeterministicType().setSelected(
						true);
				}
			}*/
		}
	}

	/**
	 * Invoked when the Probabilistic Type Check Button changes
	 */
	private void itemStateChangedProbabilisticType(int itemEvent) {

		int result;
		if (itemEvent == ItemEvent.SELECTED) {
			if (getPotentialEditDialog().getPreviousPolicy() == -1) { // no
				// change.
				// First
				// time.
				result = JOptionPane.YES_OPTION;
			} else {
				result =
					JOptionPane
						.showConfirmDialog(
							getPotentialEditDialog(),
							getPotentialEditDialog()
								.getStringResource()
								.getString(
									"NodeProbsValuesTablePanel.Msg.ChangeToProbabilistic.Text"),
							getPotentialEditDialog()
								.getStringResource()
								.getString(
									"NodeProbsValuesTablePanel.Msg.ChangeToProbabilistic.Title"),
							JOptionPane.YES_NO_OPTION);
			}
			/*if (result == JOptionPane.YES_OPTION) {
				if (getPotentialsDialog().getPreviousPolicy() == OPTIMAL_SELECTED) {
					getPotentialsDialog().showElementsWhenOptimalDeselected();
				}
				getPotentialsDialog().getJRadioButtonAll().setEnabled(true);
				getPotentialsDialog().getJRadioButtonAll().setSelected( true );
				getPotentialsDialog().getJRadioButtonIndependant().setEnabled(true);
				getPotentialsDialog().getJPanelProbabilityOrValue().setEnabled(false);
				getPotentialsDialog().getJRadioButtonProbabilities().setSelected(false);
				getPotentialsDialog().getJRadioButtonProbabilities().setEnabled(false);
				getPotentialsDialog().getJRadioButtonValues().setSelected(false);
				getPotentialsDialog().getJRadioButtonValues().setEnabled(false);
				getPotentialsDialog().getNodePotentialsTablePanel()
					.setProbabilisticModel();
			} else {
				if (getPotentialsDialog().getPrevModelPolicySelected() == OPTIMAL_SELECTED) {
					getPotentialsDialog().getJRadioButtonOptimal().setSelected(true);
				} else if (getPotentialsDialog().getPrevModelPolicySelected() == DETERMINISTIC_SELECTED) {
					getPotentialsDialog().getJRadioButtonDeterministicType().setSelected(
						true);
				}
			}*/

		}
	}

	/**
	 * Invoked when the Deterministic Type Check Button changes
	 */
	private void itemStateChangedDeterministicType(int itemEvent) {

		int result;
		if (itemEvent == ItemEvent.SELECTED) {
			if (getPotentialEditDialog().getPreviousPolicy() == -1) { // no
				// change.
				// First
				// time.
				result = JOptionPane.YES_OPTION;
			} else {
				result =
					JOptionPane
						.showConfirmDialog(
							getPotentialEditDialog(),
							getPotentialEditDialog()
								.getStringResource()
								.getString(
									"NodeProbsValuesTablePanel.Msg.ChangeToDeterministic.Text"),
							getPotentialEditDialog()
								.getStringResource()
								.getString(
									"NodeProbsValuesTablePanel.Msg.ChangeToDeterministic.Title"),
							JOptionPane.YES_NO_OPTION);
			}
			/*if (result == JOptionPane.YES_OPTION) {
				if (getPotentialsDialog().getPrevModelPolicySelected() == OPTIMAL_SELECTED) {
					getPotentialsDialog().showElementsWhenOptimalDeselected();
				}
				getPotentialsDialog().getJPanelProbabilityOrValue().setEnabled(true);
				getPotentialsDialog().getJRadioButtonValues().setEnabled(true);
				getPotentialsDialog().getJRadioButtonProbabilities().setEnabled(true);
				getPotentialsDialog().getJRadioButtonProbabilities().setSelected(true);
				getPotentialsDialog().getJRadioButtonAll().setSelected( true );
				getPotentialsDialog().getNodePotentialsTablePanel()
					.setDeterministicModel();
			} else {
				if (getPotentialsDialog().getPrevModelPolicySelected() == OPTIMAL_SELECTED) {
					getPotentialsDialog().getJRadioButtonOptimal().setSelected(true);
				} else if (getPotentialsDialog().getPrevModelPolicySelected() == PROBABILISTIC_SELECTED) {
					getPotentialsDialog().getJRadioButtonProbabilisticType().setSelected(
						true);
				}
			}*/
		}
	}

	/**
	 * Invoked when the RelationType ComboBox changes
	 */
	private void itemStateChangedTPC(int itemEvent) {

		if (itemEvent == ItemEvent.SELECTED) {
			/*getPotentialsDialog().getJPanelNetoOrCompound().setEnabled(false);
			getPotentialsDialog().getJRadioButtonNeto().setSelected(false);
			getPotentialsDialog().getJRadioButtonNeto().setEnabled(false);
			getPotentialsDialog().getJRadioButtonCompound().setSelected(false);
			getPotentialsDialog().getJRadioButtonCompound().setEnabled(false);
			getPotentialsDialog().getNodePotentialsTablePanel().setShowTPCvalues(true);
			*/
		}
	}

	/**
	 * Invoked when the RelationType ComboBox changes
	 */
	private void itemStateChangedCanonical(int itemEvent) {

		if (itemEvent == ItemEvent.SELECTED) {
			/*getPotentialsDialog().getJPanelNetoOrCompound().setEnabled(true);
			getPotentialsDialog().getJRadioButtonNeto().setEnabled(true);
			getPotentialsDialog().getJRadioButtonCompound().setEnabled(true);
			getPotentialsDialog().getNodePotentialsTablePanel().setShowTPCvalues(false);
			*/
			}
	}

	/**
	 * Invoked when the Show Net values option is selected
	 */
	private void itemStateChangedNeto(int itemEvent) {

		if (itemEvent == ItemEvent.SELECTED) {
			getPotentialEditDialog().setShowNetValues(true);
		}
	}

	/**
	 * Invoked when the Show Compound values option is selected
	 */
	private void itemStateChangedCompound(int itemEvent) {

		if (itemEvent == ItemEvent.SELECTED) {
			getPotentialEditDialog().setShowNetValues(false);
			
		}
	}

	/**
	 * Invoked when the Show probabilities in the table
	 */
	private void itemStateChangedShowProbabilities(int itemEvent) {

		if (itemEvent == ItemEvent.SELECTED) {
			/*getPotentialsDialog().getJRadioButtonAll().setEnabled(true);
			getPotentialsDialog().getJRadioButtonIndependant().setEnabled(true);
			getPotentialsDialog().getNodePotentialsTablePanel().setShowProbabilitiesValues(true);
			*/
		}
	}

	/**
	 * Invoked when the Show Values for Deterministic type Button changes
	 */
	private void itemStateChangedShowValues(int itemEvent) {

		if (itemEvent == ItemEvent.SELECTED) {
			/*getPotentialsDialog().getJRadioButtonAll().setEnabled(false);
			getPotentialsDialog().getJRadioButtonIndependant().setEnabled(false);
			getPotentialsDialog().getNodePotentialsTablePanel().setShowProbabilitiesValues(false);
			*/
		}
	}

	/**
	 * Invoked when the Show All parameters is selected
	 */
	private void itemStateChangedShowAll(int itemEvent) {

		if (itemEvent == ItemEvent.SELECTED) {
			//getPotentialsDialog().getNodePotentialsTablePanel().setShowAllParameters(true);
		}
	}

	/**
	 * Invoked when the Show Values for Deterministic type Button changes
	 */
	private void itemStateChangedShowIndependant(int itemEvent) {

		if (itemEvent == ItemEvent.SELECTED) {
			//getPotentialsDialog().getNodePotentialsTablePanel()
				//.setShowAllParameters(false);

		}
	}

}
