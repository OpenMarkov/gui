package org.openmarkov.core.gui.dialog.node;

import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.JPanel;

import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.gui.dialog.common.OkCancelHorizontalDialog;
import org.openmarkov.core.model.network.ProbNode;

@SuppressWarnings("serial")
public class ReorderVariablesDialog extends OkCancelHorizontalDialog{

	private JPanel variablesCombinationPanel;
	
	private ProbNode probNode;
	
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
