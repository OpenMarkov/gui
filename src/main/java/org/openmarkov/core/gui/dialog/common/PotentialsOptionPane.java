package org.openmarkov.core.gui.dialog.common;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;


import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.model.network.potential.PotentialType;

/**
 * This class implements a dialog box where the user can select various elements
 * from a table.
 * 
 * @author jmendoza
 * @version 1.0 jmendoza
 * @version 1.1 jlgozalo not showing the id column 
 */
public class PotentialsOptionPane extends OkCancelHorizontalDialog implements 
	ItemListener{
	

	ButtonGroup buttonGroup = new ButtonGroup ();
	
	JRadioButton uniformRadioButton = new JRadioButton("Uniform");
	
	JRadioButton tableRadioButton = new JRadioButton("Table");
	
	JRadioButton treeADDRadioButton = new JRadioButton("Tree - ADD");
	
	/*JRadioButton uniformRadioButton = new JRadioButton("Uniform");
	
	JRadioButton uniformRadioButton = new JRadioButton("Uniform");
	
	JRadioButton uniformRadioButton = new JRadioButton("Uniform");
	
	JRadioButton uniformRadioButton = new JRadioButton("Uniform");*/
	
	/**
	 * 
	 */
	private PotentialType potentialType = PotentialType.UNIFORM;
	
	/**
	 * Elements of the table.
	 */
	private Object[][] data = null;

	/**
	 * Columns of the table.
	 */
	private String[] columns = null;

	/**
	 * Selected rows of the table.
	 */
	private Object[][] selectedRows = null;

	/**
	 * Panel that contains the scroll pane.
	 */
	private JPanel valuesTableScrollPane = null;

	/**
	 * Panel to scroll the table.
	 */
	private JScrollPane subValuesTableScrollPane = null;

	/**
	 * Table where show the values.
	 */
	private KeyTable valuesTable = null;

	/**
	 * Model table.
	 */
	private DefaultTableModel tableModel = null;

	/**
	 * String resource.
	 */
	private StringResource stringResource = null;

	private JPanel buttonPanel = null;

	private boolean selectedOption = false;

	/**
	 * Constructor that calls the superclass constructor and saves the objects.
	 * 
	 * @param owner
	 *            window that owns this dialog box.
	 * @param title
	 *            title of the dialog box.
	 * @param newData
	 *            content of the rows of the table.
	 * @param newColumns
	 *            titles of the columns of the table.
	 */
	public PotentialsOptionPane( Window owner, String title ) {

		super(owner);
		initialize();
		setLocationRelativeTo(owner);

	}

	/**
	 * This method configures the dialog box.
	 * 
	 * 
	 */
	private void initialize() {

		stringResource =
			StringResourceLoader.getUniqueInstance().getBundleMessages();
		String title = stringResource.getString("NodeRelation.Title.Label");
		setTitle(title);
		this.setPreferredSize(new Dimension(250,300));
		configureComponentsPanel();
		pack();

	}

	/**
	 * Sets up the panel where all components, except the buttons of the buttons
	 * panel, will be appear.
	 */
	private void configureComponentsPanel() {

		getComponentsPanel().add(getOpions());

	}

	/**
	 * This method initialises valuesTableScrollPane.
	 * 
	 * @return a new panel that contains the scroll pane.
	 */
	private JPanel getOpions() {
		getRadioOptions();
		BoxLayout layout ;
		if (buttonPanel == null) {
			buttonPanel = new JPanel ();
			layout = new BoxLayout(buttonPanel, BoxLayout.Y_AXIS);
			buttonPanel.setLayout(layout);
			buttonPanel.add( uniformRadioButton );
			buttonPanel.add( tableRadioButton );
			buttonPanel.add( treeADDRadioButton );
			/*SpringUtilities.makeGrid(buttonPanel,
                    3, 3, //rows, cols
                    5, 5, //initialX, initialY
                    5, 5);//xPad, yPad
			layout.putConstraint(SpringLayout.WEST, buttonPanel,
                    10, SpringLayout.WEST, uniformRadioButton);
			layout.putConstraint(SpringLayout.NORTH, uniformRadioButton,
                    10,SpringLayout.NORTH, buttonPanel);
			
			layout.putConstraint(SpringLayout.SOUTH, buttonPanel,
                    10,SpringLayout.SOUTH, tableRadioButton);
			layout.putConstraint(SpringLayout.SOUTH, tableRadioButton,
                    10,SpringLayout.SOUTH, buttonPanel);
						
			layout.putConstraint(SpringLayout.SOUTH, buttonPanel,
                    10, SpringLayout.SOUTH, treeADDRadioButton );
			layout.putConstraint(SpringLayout.SOUTH, buttonPanel,
                    50, SpringLayout.SOUTH, treeADDRadioButton);
			
			*/
			getButtonGroup();
			
			pack();
			
			
		}

		return buttonPanel;

	}
	/**
	 * This method initialises valuesTableScrollPane.
	 * 
	 * @return a new panel that contains the scroll pane.
	 */
	private void getRadioOptions() {

		uniformRadioButton.addItemListener(this);
		tableRadioButton.addItemListener(this);
		treeADDRadioButton.addItemListener(this);
			
	}
	

	private void getButtonGroup() {
		ButtonGroup buttonGro = new ButtonGroup();
		buttonGro.add( uniformRadioButton );
		buttonGro.add( tableRadioButton );
		buttonGro.add( treeADDRadioButton );
		
	}

	
	

	

	// ESCA-JAVA0025:
	/**
	 * Cancel the operation.
	 */
	@Override
	protected void doCancelClickBeforeHide() {

	}

	/**
	 * Checks if the user has selected any element.
	 * 
	 * @return true if the dialog box must be closed; otherwise, false.
	 */
	@Override
	protected boolean doOkClickBeforeHide() {
		
		if (selectedOption == false) {
			JOptionPane.showMessageDialog(
				this, stringResource.getString("NoRowsSelected.Text.Label"),
				stringResource.getString("ErrorWindow.Title.Label"),
				JOptionPane.ERROR_MESSAGE);

			return false;
		}

		return true;

	}

	/**
	 * Returns the selected elements of the table.
	 * 
	 * @return an array that contains the selected elements of the table or null
	 *         if nothing is selected.
	 */
	public  PotentialType getSelectedOption() {

		return potentialType;

	}



	/**
	 * This method shows the dialog and requests the user to select at least one
	 * row.
	 * 
	 * @return OK_BUTTON if the user has pressed the 'Ok' button or
	 *         CANCEL_BUTTON if the user has pressed the 'Cancel' button.
	 */
	public int requestSelectPotential() {

		setVisible(true);

		return selectedButton;

	}

	
	public void itemStateChanged(ItemEvent e) {
		if ( e.getStateChange() == ItemEvent.SELECTED ){
			selectedOption = true;
			if (((JRadioButton) e.getSource()).getText() == PotentialType.TABLE.toString())
			 potentialType = PotentialType.TABLE;
		}
		
	}
}
