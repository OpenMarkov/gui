package org.openmarkov.core.gui.dialog.treeadd;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;

import javax.swing.border.EmptyBorder;


import org.openmarkov.core.exception.NotEnoughMemoryException;

import org.openmarkov.core.gui.dialog.common.OkCancelApplyUndoRedoHorizontalDialog;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;

import org.openmarkov.core.model.network.potential.treeadd.TreeADDBranch;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDPotential;
/**
 * 
 * @author myebra
 *
 */
@SuppressWarnings("serial")
public class ChangeIntervalDialog extends OkCancelApplyUndoRedoHorizontalDialog {

	private ChangeIntervalPanel changeIntervalPanel;
	
	/**
	 * Dialog string resource.
	 */
	private StringResource dialogStringResource;
	
	private TreeADDPotential treeADDParent;
	private TreeADDBranch treeBranch;
	@SuppressWarnings("unused")
	private StringResource messageStringResource;
	
	public ChangeIntervalDialog(Window owner, TreeADDBranch treeBranch) {
		super(owner);
		this.treeBranch = treeBranch;
		//add(checkBoxPanel, BorderLayout.NORTH );
		initialize();
		setLocationRelativeTo(owner);
		setMinimumSize(new Dimension( 200, 200 ));
		setResizable(true);
		pack();
		
	}
	private void initialize() {

		configureComponentsPanel();
		pack();
	}
	/**
	 * Sets up the panel where all components, except the buttons of the buttons
	 * panel, will be appear.
	 */
	private void configureComponentsPanel() {
		/*dialogStringResource =
				StringResourceLoader.getUniqueInstance().getBundleDialogs();
		messageStringResource =
				StringResourceLoader.getUniqueInstance().getBundleMessages();
		setTitle(dialogStringResource
				.getString("NodePotentialDialog.Title.Label"));*/
		getComponentsPanel().setLayout(new BorderLayout(5, 5));
		//getComponentsPanel().add( getJPanelChangeInterval(), BorderLayout.CENTER );
		getComponentsPanel().add( getChangeIntervalPanel(), BorderLayout.CENTER );
		
	}
	protected ChangeIntervalPanel getChangeIntervalPanel() {
		
		if (changeIntervalPanel == null) {
			dialogStringResource = StringResourceLoader.getUniqueInstance()
					.getBundleDialogs();
			messageStringResource =
					StringResourceLoader.getUniqueInstance().getBundleMessages();
	
			setName("NodeDomainValuesTablePanel");
			//changeIntervalPanel = new ChangeIntervalPanel(columnNames, treeADDParent);
			changeIntervalPanel = new ChangeIntervalPanel(treeBranch);
			//changeIntervalPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
			changeIntervalPanel.setLayout( new FlowLayout() );
			changeIntervalPanel.setName( "jPanelChangeInterval" );
			
		}
		return changeIntervalPanel;

		
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
