package org.openmarkov.core.gui.dialog.node;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;

import javax.swing.JPanel;

import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.gui.dialog.common.OkCancelApplyUndoRedoHorizontalDialog;
import org.openmarkov.core.gui.dialog.treeadd.AddStatesCheckBoxPanel;

/**
 * 
 * @author maryebra
 *
 */
public class StandarDomainsDialog extends OkCancelApplyUndoRedoHorizontalDialog {

	private JPanel standarDomainsPanel;
	
	public StandarDomainsDialog(Window owner) {
		super(owner);
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
	getComponentsPanel().add( getJPanelStandarDomains(), BorderLayout.CENTER );
	
}

public JPanel getJPanelStandarDomains() {
	
	if (standarDomainsPanel == null) {
		standarDomainsPanel = new StandarDomainPanel();
		//statesCheckBoxPanel.setLayout( new FlowLayout());
		standarDomainsPanel.setName( "jPanelStandarDomains" );
	}
	return standarDomainsPanel;

	
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
