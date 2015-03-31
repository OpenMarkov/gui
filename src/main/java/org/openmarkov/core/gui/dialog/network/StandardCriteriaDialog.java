package org.openmarkov.core.gui.dialog.network;

import java.awt.BorderLayout;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.openmarkov.core.action.DecisionCriteriaEdit;
import org.openmarkov.core.action.StateAction;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.dialog.common.OkCancelApplyUndoRedoHorizontalDialog;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.model.network.Criterion;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Criterion.CECriterion;

public class StandardCriteriaDialog extends OkCancelApplyUndoRedoHorizontalDialog{

	/**
	 * Generated serial version UID
	 */
	private static final long serialVersionUID = 7540156073158685187L;

	/**
	 * Standard criteria panel
	 */
	private StandardCriteriaPanel standardCriteriaPanel;
	
    /**
     * String database
     */
    protected StringDatabase        stringDatabase = StringDatabase.getUniqueInstance ();
    private ProbNet probNet;

	
	public StandardCriteriaDialog(Window owner, ProbNet probNet) {
		super(owner);
		this.probNet = probNet;
		initialize();
		setLocationRelativeTo(owner);
		setResizable(true);
		pack();
	}
	
	private void initialize() {	
		configureComponentsPanel();
		pack();
	}
	
	private void configureComponentsPanel() {
		getComponentsPanel().setLayout(new BorderLayout(5, 5));
		getComponentsPanel().add(getJPanelStandardCriteria(), BorderLayout.CENTER );
		
	}
	
	public JPanel getJPanelStandardCriteria() {
		
		if (standardCriteriaPanel == null) {
			standardCriteriaPanel = new StandardCriteriaPanel();
			//statesCheckBoxPanel.setLayout( new FlowLayout());
			standardCriteriaPanel.setName( "jPanelStandardCriteria" );
		}
		return standardCriteriaPanel;
	}
	
	/**
	 * This method carries out the actions when the user press the Ok button
	 * before hide the dialog.
	 * 
	 * @return true if the dialog box can be closed.
	 */
	protected boolean doOkClickBeforeHide() {
		
		// This must be a single operation
		probNet.getPNESupport().openParenthesis();
		
		// Remove all criterion in the probNet with edits
		while(!probNet.getDecisionCriteria().isEmpty()){
			Criterion criterionToBeDeleted = probNet.getDecisionCriteria().get(0);
			DecisionCriteriaEdit edit = new DecisionCriteriaEdit(probNet, StateAction.REMOVE, criterionToBeDeleted, null);
			try {
				probNet.getPNESupport().doEdit(edit);
			} catch (DoEditException | NonProjectablePotentialException
					| WrongCriterionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//Get the list with the new criteria 
		List<Criterion> defaultCriteria = getDefaultCriteria();

		// Add new criteria to the probNet
		for(Criterion criterion : defaultCriteria){
			DecisionCriteriaEdit edit = new DecisionCriteriaEdit(probNet, StateAction.ADD, criterion, null);
			try {
				probNet.getPNESupport().doEdit(edit);
			} catch (DoEditException | NonProjectablePotentialException
					| WrongCriterionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		probNet.getPNESupport().closeParenthesis();
		this.getParent().repaint();
		return true;
	}

	public int requestValues() {
		
		setVisible(true);
		
		return selectedButton;
	}
	
	/**
	 * Gets a list with the default criteria selected. Gets the string with the selected default criteria (from the standardCriteriaPanel) and parses it.   
	 * @return A list with the new criteria
	 */
	private List<Criterion> getDefaultCriteria() {
		List<Criterion> defaultCriteria = new ArrayList<Criterion>();
		
		String selectedDefaultCriteria = null;
		for(JRadioButton radioButton : standardCriteriaPanel.getRadioButtons()){
			if(radioButton.isSelected()){
				selectedDefaultCriteria = radioButton.getText();
			}
		}
		
		if(selectedDefaultCriteria.equals(stringDatabase.getString ("defaultCriteria.costBeneffitEuros.Text"))){			
			Criterion costCriterion = new Criterion(stringDatabase.getString ("defaultCriteria.cost.Text"), stringDatabase.getString ("defaultCriteria.euros.Text"));
			Criterion beneffitCriterion = new Criterion(stringDatabase.getString ("defaultCriteria.beneffit.Text"), stringDatabase.getString ("defaultCriteria.euros.Text"));
			costCriterion.setUnicriteriaScale(-1);
		
			defaultCriteria.add(costCriterion);
			defaultCriteria.add(beneffitCriterion);
		} else if(selectedDefaultCriteria.equals(stringDatabase.getString ("defaultCriteria.costBeneffitPounds.Text"))){
			Criterion costCriterion = new Criterion(stringDatabase.getString ("defaultCriteria.cost.Text"), stringDatabase.getString ("defaultCriteria.pounds.Text"));
			Criterion beneffitCriterion = new Criterion(stringDatabase.getString ("defaultCriteria.beneffit.Text"), stringDatabase.getString ("defaultCriteria.pounds.Text"));
			costCriterion.setUnicriteriaScale(-1);
		
			defaultCriteria.add(costCriterion);
			defaultCriteria.add(beneffitCriterion);
		} else if(selectedDefaultCriteria.equals(stringDatabase.getString ("defaultCriteria.costBeneffitDollars.Text"))){
			Criterion costCriterion = new Criterion(stringDatabase.getString ("defaultCriteria.cost.Text"), stringDatabase.getString ("defaultCriteria.dollars.Text"));
			Criterion beneffitCriterion = new Criterion(stringDatabase.getString ("defaultCriteria.beneffit.Text"), stringDatabase.getString ("defaultCriteria.dollars.Text"));
			costCriterion.setUnicriteriaScale(-1);
		
			defaultCriteria.add(costCriterion);
			defaultCriteria.add(beneffitCriterion);			
		} else if(selectedDefaultCriteria.equals(stringDatabase.getString ("defaultCriteria.costEffectivenessEurosQALY.Text"))){
			Criterion costCriterion = new Criterion(stringDatabase.getString ("defaultCriteria.cost.Text"), stringDatabase.getString ("defaultCriteria.euros.Text"));
			costCriterion.setCECriterion(CECriterion.Cost);
			
			Criterion effectivenessCriterion = new Criterion(stringDatabase.getString ("defaultCriteria.effectiveness.Text"), stringDatabase.getString ("defaultCriteria.qaly.Text"));
			effectivenessCriterion.setCECriterion(CECriterion.Effectiveness);
		
			defaultCriteria.add(costCriterion);
			defaultCriteria.add(effectivenessCriterion);
		} else if(selectedDefaultCriteria.equals(stringDatabase.getString ("defaultCriteria.costEffectivenessPoundsQALY.Text"))){
			Criterion costCriterion = new Criterion(stringDatabase.getString ("defaultCriteria.cost.Text"), stringDatabase.getString ("defaultCriteria.pounds.Text"));
			costCriterion.setCECriterion(CECriterion.Cost);
			
			Criterion effectivenessCriterion = new Criterion(stringDatabase.getString ("defaultCriteria.effectiveness.Text"), stringDatabase.getString ("defaultCriteria.qaly.Text"));
			effectivenessCriterion.setCECriterion(CECriterion.Effectiveness);
		
			defaultCriteria.add(costCriterion);
			defaultCriteria.add(effectivenessCriterion);
		} else if(selectedDefaultCriteria.equals(stringDatabase.getString ("defaultCriteria.costEffectivenessDollarsQALY.Text"))){
			Criterion costCriterion = new Criterion(stringDatabase.getString ("defaultCriteria.cost.Text"), stringDatabase.getString ("defaultCriteria.dollars.Text"));
			costCriterion.setCECriterion(CECriterion.Cost);
			
			Criterion effectivenessCriterion = new Criterion(stringDatabase.getString ("defaultCriteria.effectiveness.Text"), stringDatabase.getString ("defaultCriteria.qaly.Text"));
			effectivenessCriterion.setCECriterion(CECriterion.Effectiveness);
		
			defaultCriteria.add(costCriterion);
			defaultCriteria.add(effectivenessCriterion);
		} 
		
		return defaultCriteria;
		
	}
	

}
