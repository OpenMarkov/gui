package org.openmarkov.core.gui.dialog.treeadd;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;



import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.gui.dialog.common.OkCancelApplyUndoRedoHorizontalDialog;
/**
 * 
 * @author myebra
 *
 */

@SuppressWarnings("serial")
public class SplitIntervalDialog extends OkCancelApplyUndoRedoHorizontalDialog {

	private SplitIntervalPanel splitIntervalPanel;

	
	public SplitIntervalDialog(Window owner) {
		super(owner);
		initialize();
		setLocationRelativeTo(owner);
		setMinimumSize(new Dimension( 200, 200));
		//setResizable(true);
		pack();
		
	}
	private void initialize() {
		setTitle("Split Interval");
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
		getComponentsPanel().add( getJPanelSplitInterval(), BorderLayout.CENTER );
		
	}
	
	protected SplitIntervalPanel getJPanelSplitInterval() {
	
		if (splitIntervalPanel == null) {
			splitIntervalPanel = new SplitIntervalPanel();
			//splitIntervalPanel.setLayout( new FlowLayout() );
			splitIntervalPanel.setName( "jPanelSplitInterval" );
			
		}
		return splitIntervalPanel;

		
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
		if (((SplitIntervalPanel)getJPanelSplitInterval()).getLimit().getText() == null) {
			
			return false;
		}
		
		return true;
	}

	/**
	 * This method carries out the actions when the user press the Cancel button
	 * before hide the dialog.
	 */
	protected void doCancelClickBeforeHide() {
		
	}

}
