package openmarkov.core.gui.dialog.option;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import openmarkov.core.gui.edition.EditorPanel;
import openmarkov.core.gui.localize.StringResource;
import openmarkov.core.gui.localize.StringResourceLoader;
import openmarkov.core.gui.menutoolbar.toolbar.InferenceToolBar;

/**
 * Dialog box to set the inference options
 * 
 * @author asaez
 * @version 1.0
 */
public class OptionsInferenceDialog extends JDialog {
	
	/**
	 * String resource.
	 */
	private StringResource stringResource = null;
	
	/**
	 * Button group that holds the radio buttons that will be shown.
	 * There is a radio button for each state of the node.
	 */
	ButtonGroup buttonGroup = new ButtonGroup();

	
	/**
	 * This method initialises this instance.
	 * 
	 * @param owner
	 *            window that owns this dialog.
	 * @param editorPanel
	 *            the editor panel that called this dialog.
	 */
	public OptionsInferenceDialog(Window owner, EditorPanel editorPanel, 
			InferenceToolBar inferenceToolBar) {
		
		stringResource = StringResourceLoader.getUniqueInstance().getBundleDialogs();
		
		JPanel principalPanel = new JPanel();
		JPanel textPanel = new JPanel();
		JPanel radioButtonsPanel = new JPanel();
		JPanel buttonsPanel = new JPanel();
		JButton okButton = new JButton(stringResource.
				getString("OptionsInferenceDialog.jButtonOK.Label"));	
		JButton cancelButton = new JButton(stringResource.
				getString("OptionsInferenceDialog.jButtonCancel.Label"));

		setTitle(stringResource.getString("OptionsInferenceDialog.Title.Label"));
		this.getContentPane().setLayout(new BorderLayout());
		setLocationRelativeTo(owner);
		this.getContentPane().add(principalPanel, BorderLayout.CENTER);
		principalPanel.setLayout(new BorderLayout());
			
		textPanel.setLayout(new GridLayout(2,1));
		textPanel.add(new JLabel("\n" + stringResource.
					getString("OptionsInferenceDialog.Text.Label"), SwingConstants.CENTER));
		principalPanel.add(textPanel, BorderLayout.NORTH);
		
		radioButtonsPanel.setLayout(new GridLayout(2, 1));
		JRadioButton jRadioButton1 = new JRadioButton(stringResource.
				getString("OptionsInferenceDialog.optionAuto.Label"));
		jRadioButton1.setActionCommand(stringResource.
				getString("OptionsInferenceDialog.optionAuto.Label"));
		JRadioButton jRadioButton2 = new JRadioButton(stringResource.
				getString("OptionsInferenceDialog.optionManual.Label"));
		jRadioButton2.setActionCommand(stringResource.
				getString("OptionsInferenceDialog.optionManual.Label"));
		radioButtonsPanel.add(jRadioButton1);
		radioButtonsPanel.add(jRadioButton2);
		jRadioButton1.setSelected(true);
		buttonGroup.add(jRadioButton1);
		buttonGroup.add(jRadioButton2);
		principalPanel.add(radioButtonsPanel, BorderLayout.CENTER);		
			
		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);
		principalPanel.add(buttonsPanel, BorderLayout.SOUTH);
			
		OptionsInferenceDialogListener optionsInferenceDialogListener = 
				new OptionsInferenceDialogListener(this, editorPanel, inferenceToolBar);
		okButton.addActionListener(optionsInferenceDialogListener);
		cancelButton.addActionListener(optionsInferenceDialogListener);

		pack();
		setMinimumSize(new Dimension(300, getHeight()));
		setModal(true);
		setVisible(true);
	}	
	

	/**
	 * This method returns the button group contained by this dialog.
	 * 
	 * @return the button group contained by this dialog.
	 */	
	public ButtonGroup getButtonGroup() {
		return buttonGroup;
	}


}
