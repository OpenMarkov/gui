/**
 * OpenMarkov - NodeProbsValuesTablePanelItemListenerAssistant.java
 */
package org.openmarkov.core.gui.dialog.node;


import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JOptionPane;

import org.openmarkov.core.gui.component.AlgorithmRelationTypes;




/**
 * This class assist to the NodeProbsValuesTablePanel with the ItemListener
 * events
 * 
 * @author jlgozalo
 * @version 1.0 23/01/2010
 */

public class NodeProbsValuesTablePanelItemListenerAssistant implements
				ItemListener {

	/**
	 * panel for handling events
	 */
	private NodeProbsValuesTablePanel panel = null;

	/**
	 * model selected (probabilistic, deterministic or optimal
	 */
	private static int PROBABILISTIC_SELECTED = 0;
	private static int DETERMINISTIC_SELECTED = 1;
	private static int OPTIMAL_SELECTED = 2;

	/**
	 * constructor without construction parameters
	 */
	public NodeProbsValuesTablePanelItemListenerAssistant() {

	}

	/**
	 * constructor
	 * 
	 * @param panel
	 */
	public NodeProbsValuesTablePanelItemListenerAssistant(
								NodeProbsValuesTablePanel panel) {

		setPanel(panel);
	}

	/**
	 * @return the panel
	 */
	public NodeProbsValuesTablePanel getPanel() {

		return panel;
	}

	/**
	 * @param panel
	 *            the panel to set
	 */
	public void setPanel(NodeProbsValuesTablePanel panel) {

		this.panel = panel;
	}

	/**
	 * Invoked when an item has been selected.
	 * 
	 * @param e
	 *            event information.
	 */
	public void itemStateChanged(ItemEvent e) {

		if (e.getItemSelectable().equals(getPanel().getJComboBoxRelationType())) {
			itemStateChangedComboBoxRelationType(e.getStateChange());
		}
		if (e.getItem().equals(getPanel().getJRadioButtonProbabilisticType())) {
			itemStateChangedProbabilisticType(e.getStateChange());
		}
		if (e.getItem().equals(getPanel().getJRadioButtonDeterministicType())) {
			itemStateChangedDeterministicType(e.getStateChange());
		}
		if (e.getItem().equals(getPanel().getJRadioButtonOptimal())) {
			itemStateChangedOptimal(e.getStateChange());
		}
		if (e.getItem().equals(getPanel().getJRadioButtonTPC())) {
			itemStateChangedTPC(e.getStateChange());
		}
		if (e.getItem().equals(getPanel().getJRadioButtonCanonical())) {
			itemStateChangedCanonical(e.getStateChange());
		}
		if (e.getItem().equals(getPanel().getJRadioButtonNeto())) {
			itemStateChangedNeto(e.getStateChange());
		}
		if (e.getItem().equals(getPanel().getJRadioButtonCompound())) {
			itemStateChangedCompound(e.getStateChange());
		}
		if (e.getItem().equals(getPanel().getJRadioButtonProbabilities())) {
			itemStateChangedShowProbabilities(e.getStateChange());
		}
		if (e.getItem().equals(getPanel().getJRadioButtonValues())) {
			itemStateChangedShowValues(e.getStateChange());
		}
		if (e.getItem().equals(getPanel().getJRadioButtonAll())) {
			itemStateChangedShowAll(e.getStateChange());
		}
		if (e.getItem().equals(getPanel().getJRadioButtonIndependant())) {
			itemStateChangedShowIndependant(e.getStateChange());
		}

	}

	/**
	 * Invoked when the RelationType ComboBox changes
	 */
	private void itemStateChangedComboBoxRelationType(int itemEvent) {

		if (itemEvent == ItemEvent.SELECTED) {
			int index =
				getPanel().getJComboBoxRelationType().getSelectedIndex();
			String familia = AlgorithmRelationTypes.getByIndex(index);
			if (familia.equals(AlgorithmRelationTypes.RELATION_TYPE_UNIFORM)) {
				// is a general relation
				getPanel().getJPanelTpcOrCanonical().setEnabled(false);
				getPanel().getJRadioButtonCanonical().setSelected(false);
				getPanel().getJRadioButtonCanonical().setEnabled(false);
				getPanel().getJRadioButtonTPC().setSelected(false);
				getPanel().getJRadioButtonTPC().setEnabled(false);
				getPanel().getJPanelNetoOrCompound().setEnabled(false);
				getPanel().getJRadioButtonCompound().setSelected(false);
				getPanel().getJRadioButtonCompound().setEnabled(false);
				getPanel().getJRadioButtonNeto().setSelected(false);
				getPanel().getJRadioButtonNeto().setEnabled(false);
				getPanel().getNodePotentialsTablePanel().setGeneralModel(index);
			} else {
				// is a canonical relation
				getPanel().getJPanelTpcOrCanonical().setEnabled(true);
				getPanel().getJRadioButtonCanonical().setEnabled(true);
				getPanel().getJRadioButtonTPC().setEnabled(true);
				getPanel().getNodePotentialsTablePanel().setCanonicalModel(
					index);
			}
		}
	}
	

	/**
	 * Invoked when the Optimal Check Button changes
	 */
	private void itemStateChangedOptimal(int itemEvent) {

		int result;
		if (itemEvent == ItemEvent.SELECTED) {
			if (getPanel().getPrevModelPolicySelected() == -1) {
				// if it is the first time, no ask
				result = JOptionPane.YES_OPTION;
			} else {
				result =
					JOptionPane
						.showConfirmDialog(
							getPanel(),
							getPanel()
								.getDialogStringResource()
								.getString(
									"NodeProbsValuesTablePanel.Msg.ChangeToOptimal.Text"),
							getPanel()
								.getDialogStringResource()
								.getString(
									"NodeProbsValuesTablePanel.Msg.ChangeToOptimal.Title"),
							JOptionPane.YES_NO_OPTION);
			}
			if (result == JOptionPane.YES_OPTION) {
				getPanel().hideElementsWhenOptimalSelected();
				getPanel().setPrevModelPolicySelected(OPTIMAL_SELECTED);
				getPanel().getNodePotentialsTablePanel().setOptimalModel();
			} else {
				// restore previous selection
				if (getPanel().getPrevModelPolicySelected() == PROBABILISTIC_SELECTED) {
					getPanel().getJRadioButtonProbabilisticType().setSelected(
						true);
				} else if (getPanel().getPrevModelPolicySelected() == DETERMINISTIC_SELECTED) {
					getPanel().getJRadioButtonDeterministicType().setSelected(
						true);
				}
			}
		}
	}

	/**
	 * Invoked when the Probabilistic Type Check Button changes
	 */
	private void itemStateChangedProbabilisticType(int itemEvent) {

		int result;
		if (itemEvent == ItemEvent.SELECTED) {
			if (getPanel().getPrevModelPolicySelected() == -1) { // no
				// change.
				// First
				// time.
				result = JOptionPane.YES_OPTION;
			} else {
				result =
					JOptionPane
						.showConfirmDialog(
							getPanel(),
							getPanel()
								.getDialogStringResource()
								.getString(
									"NodeProbsValuesTablePanel.Msg.ChangeToProbabilistic.Text"),
							getPanel()
								.getDialogStringResource()
								.getString(
									"NodeProbsValuesTablePanel.Msg.ChangeToProbabilistic.Title"),
							JOptionPane.YES_NO_OPTION);
			}
			if (result == JOptionPane.YES_OPTION) {
				if (getPanel().getPrevModelPolicySelected() == OPTIMAL_SELECTED) {
					getPanel().showElementsWhenOptimalDeselected();
				}
				getPanel().getJRadioButtonAll().setEnabled(true);
				getPanel().getJRadioButtonAll().setSelected( true );
				getPanel().getJRadioButtonIndependant().setEnabled(true);
				getPanel().getJPanelProbabilityOrValue().setEnabled(false);
				getPanel().getJRadioButtonProbabilities().setSelected(false);
				getPanel().getJRadioButtonProbabilities().setEnabled(false);
				getPanel().getJRadioButtonValues().setSelected(false);
				getPanel().getJRadioButtonValues().setEnabled(false);
				getPanel().getNodePotentialsTablePanel()
					.setProbabilisticModel();
			} else {
				if (getPanel().getPrevModelPolicySelected() == OPTIMAL_SELECTED) {
					getPanel().getJRadioButtonOptimal().setSelected(true);
				} else if (getPanel().getPrevModelPolicySelected() == DETERMINISTIC_SELECTED) {
					getPanel().getJRadioButtonDeterministicType().setSelected(
						true);
				}
			}

		}
	}

	/**
	 * Invoked when the Deterministic Type Check Button changes
	 */
	private void itemStateChangedDeterministicType(int itemEvent) {

		int result;
		if (itemEvent == ItemEvent.SELECTED) {
			if (getPanel().getPrevModelPolicySelected() == -1) { // no
				// change.
				// First
				// time.
				result = JOptionPane.YES_OPTION;
			} else {
				result =
					JOptionPane
						.showConfirmDialog(
							getPanel(),
							getPanel()
								.getDialogStringResource()
								.getString(
									"NodeProbsValuesTablePanel.Msg.ChangeToDeterministic.Text"),
							getPanel()
								.getDialogStringResource()
								.getString(
									"NodeProbsValuesTablePanel.Msg.ChangeToDeterministic.Title"),
							JOptionPane.YES_NO_OPTION);
			}
			if (result == JOptionPane.YES_OPTION) {
				if (getPanel().getPrevModelPolicySelected() == OPTIMAL_SELECTED) {
					getPanel().showElementsWhenOptimalDeselected();
				}
				getPanel().getJPanelProbabilityOrValue().setEnabled(true);
				getPanel().getJRadioButtonValues().setEnabled(true);
				getPanel().getJRadioButtonProbabilities().setEnabled(true);
				getPanel().getJRadioButtonProbabilities().setSelected(true);
				getPanel().getJRadioButtonAll().setSelected( true );
				getPanel().getNodePotentialsTablePanel()
					.setDeterministicModel();
			} else {
				if (getPanel().getPrevModelPolicySelected() == OPTIMAL_SELECTED) {
					getPanel().getJRadioButtonOptimal().setSelected(true);
				} else if (getPanel().getPrevModelPolicySelected() == PROBABILISTIC_SELECTED) {
					getPanel().getJRadioButtonProbabilisticType().setSelected(
						true);
				}
			}
		}
	}

	/**
	 * Invoked when the RelationType ComboBox changes
	 */
	private void itemStateChangedTPC(int itemEvent) {

		if (itemEvent == ItemEvent.SELECTED) {
			getPanel().getJPanelNetoOrCompound().setEnabled(false);
			getPanel().getJRadioButtonNeto().setSelected(false);
			getPanel().getJRadioButtonNeto().setEnabled(false);
			getPanel().getJRadioButtonCompound().setSelected(false);
			getPanel().getJRadioButtonCompound().setEnabled(false);
			getPanel().getNodePotentialsTablePanel().setShowTPCvalues(true);
		}
	}

	/**
	 * Invoked when the RelationType ComboBox changes
	 */
	private void itemStateChangedCanonical(int itemEvent) {

		if (itemEvent == ItemEvent.SELECTED) {
			getPanel().getJPanelNetoOrCompound().setEnabled(true);
			getPanel().getJRadioButtonNeto().setEnabled(true);
			getPanel().getJRadioButtonCompound().setEnabled(true);
			getPanel().getNodePotentialsTablePanel().setShowTPCvalues(false);
			}
	}

	/**
	 * Invoked when the Show Net values option is selected
	 */
	private void itemStateChangedNeto(int itemEvent) {

		if (itemEvent == ItemEvent.SELECTED) {
			getPanel().getNodePotentialsTablePanel().setShowNetValues(true);
		}
	}

	/**
	 * Invoked when the Show Compound values option is selected
	 */
	private void itemStateChangedCompound(int itemEvent) {

		if (itemEvent == ItemEvent.SELECTED) {
			getPanel().getNodePotentialsTablePanel().setShowNetValues(false);
			
		}
	}

	/**
	 * Invoked when the Show probabilities in the table
	 */
	private void itemStateChangedShowProbabilities(int itemEvent) {

		if (itemEvent == ItemEvent.SELECTED) {
			getPanel().getJRadioButtonAll().setEnabled(true);
			getPanel().getJRadioButtonIndependant().setEnabled(true);
			getPanel().getNodePotentialsTablePanel().setShowProbabilitiesValues(true);
		}
	}

	/**
	 * Invoked when the Show Values for Deterministic type Button changes
	 */
	private void itemStateChangedShowValues(int itemEvent) {

		if (itemEvent == ItemEvent.SELECTED) {
			getPanel().getJRadioButtonAll().setEnabled(false);
			getPanel().getJRadioButtonIndependant().setEnabled(false);
			getPanel().getNodePotentialsTablePanel().setShowProbabilitiesValues(false);
		}
	}

	/**
	 * Invoked when the Show All parameters is selected
	 */
	private void itemStateChangedShowAll(int itemEvent) {

		if (itemEvent == ItemEvent.SELECTED) {
			getPanel().getNodePotentialsTablePanel().setShowAllParameters(true);
		}
	}

	/**
	 * Invoked when the Show Values for Deterministic type Button changes
	 */
	private void itemStateChangedShowIndependant(int itemEvent) {

		if (itemEvent == ItemEvent.SELECTED) {
			getPanel().getNodePotentialsTablePanel()
				.setShowAllParameters(false);

		}
	}

}
