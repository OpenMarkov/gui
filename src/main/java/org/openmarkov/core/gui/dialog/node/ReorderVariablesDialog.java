package org.openmarkov.core.gui.dialog.node;

import java.awt.BorderLayout;
import java.awt.Window;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.gui.dialog.common.OkCancelHorizontalDialog;
import org.openmarkov.core.gui.dialog.network.NetworkAgentsTablePanel;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.StringWithProperties;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.PotentialRole;

@SuppressWarnings("serial")
public class ReorderVariablesDialog extends OkCancelHorizontalDialog{

	private JPanel variablesCombinationPanel;
	
	private ProbNode probNode;

	private ReorderVariablesPanel reorderVariablesPanel;
	
	public ReorderVariablesDialog(Window owner, ProbNode probNode) {
		super(owner);
		this.probNode = probNode;
		initialize();
		setLocationRelativeTo(owner);
		//setMinimumSize(new Dimension( 100, 100 ));
		setResizable(true);
		pack();
	}

	private void initialize() {

		configureComponentsPanel();
		pack();
	}
	private void configureComponentsPanel() {
		
		getComponentsPanel().setLayout(new BorderLayout(5, 5));
		getComponentsPanel().add( getVariablesCombinationPanel(), BorderLayout.CENTER );
		
	}
	
	protected JPanel getVariablesCombinationPanel() {
		
		if (variablesCombinationPanel == null) {
			variablesCombinationPanel = new VariablesCombinationPanel(probNode);
			//dissociateStatesCheckBoxPanel.setLayout( new FlowLayout() );
			variablesCombinationPanel.setName( "variablesCombinationPanel" );
			
		}
		return variablesCombinationPanel;

		
	}
	
	private ReorderVariablesPanel getReorderVariablesPanel() {
		if (reorderVariablesPanel == null) {
			String[] columnNames = {"Key", "Names"};
			reorderVariablesPanel = new ReorderVariablesPanel(columnNames, probNode);
			reorderVariablesPanel.setName("networkAgentsPanel");
			reorderVariablesPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		}

		return reorderVariablesPanel;

	}
	
	public void setFieldFromProperties (ProbNode probNode) {
		ArrayList<Variable> variables = new ArrayList<Variable>();
		if (probNode.getPotentials().get(0).getPotentialRole() == PotentialRole.CONDITIONAL_PROBABILITY) {
		variables = (ArrayList<Variable>) probNode.getPotentials().get(0).getVariables().clone();
			variables.remove(0);
			
		} else if (probNode.getPotentials().get(0).getPotentialRole() == PotentialRole.UTILITY) {
			variables = (ArrayList<Variable>) probNode.getPotentials().get(0).getVariables().clone();
		}
		
		
		
		if (variables != null) {
			Object [][] data = new Object [variables.size()][1];
			for (int i = 0; i < variables.size(); i++) {
				data[i][0] = variables.get(i).getName();
			}
			 //initializing data structure for the table model
			getReorderVariablesPanel().setData(data);
			// initializing data structure for supervising data order in GUI 
			getReorderVariablesPanel().setDataTable(data);
		 }
	}
	public int requestValues() {
		
		setVisible(true);
		
		return selectedButton;
	}
	/**
	 * This method carries out the actions when the user press the Ok button
	 * before hide the dialog.
	 * 
	 * @return true if the dialog box can be closed.
	 * @throws NotEnoughMemoryException 
	 */
	protected boolean doOkClickBeforeHide() throws NotEnoughMemoryException {
		
		
		return true;
	}

	/**
	 * This method carries out the actions when the user press the Cancel button
	 * before hide the dialog.
	 */
	protected void doCancelClickBeforeHide() {
		
	}

}
