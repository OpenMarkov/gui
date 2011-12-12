package org.openmarkov.core.gui.dialog.common;


import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;


import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.gui.loader.element.IconLoader;



/**
 * This class adds the Apply, Undo and Redo buttons to the OKCancelHorizontalDialog. <p>
 * A dialog box is implemented with a horizontal buttons panel placed in
 * the bottom of the window. <br>
 * This panel has three buttons: <br>
 * <ul><li>a 'Ok' button that is
 * activated pressing the ENTER key, </li>
 * <li>a 'Cancel' button activated pressing the
 * ESC key, </li>
 * <li>an 'Apply' button activated pressing also the ENTER key</li>
 * <li>an 'Undo' button activated pressing also the CTRL-Z key to undo the last modification</li>
 * <li>a 'Redo' button activated pressing also the CTRL-Y key to redo the last modification</li>
 *  
 * @author jlgozalo
 * @version 1.0
 * @version 1.1 - 04/02/2010 - adding Undo/Redo buttons
 */
public class OkCancelApplyUndoRedoHorizontalDialog extends OkCancelHorizontalDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1176820837760605949L;

	// ESCA-JAVA0007:
	/**
	 * Constant that indicates that the user has pressed 'Apply' button.
	 */
	public static int APPLY_BUTTON = JOptionPane.OK_OPTION;

	/**
	 * Constant that indicates that the user has pressed 'CTRL-Z' button.
	 */
	public static int UNDO_BUTTON = 10;

	/**
	 * Constant that indicates that the user has pressed 'CTRL-Y' button.
	 */
	public static int REDO_BUTTON = 20;

	/**
	 * Apply button.
	 */
	private JButton jButtonApply = null;

	/**
	 * Undo button.
	 */
	private JButton jButtonUndo = null;

	/**
	 * Redo button.
	 */
	private JButton jButtonRedo = null;


	/**
	 * Constructor. initialises the instance.
	 * 
	 * @param owner
	 *            window that owns the dialog.
	 */
	public OkCancelApplyUndoRedoHorizontalDialog(Window owner) {

		super(owner);
		initialize();
		pack();
	}

	/**
	 * This method initialises this instance.
	 */
	private void initialize() {

		setName("OKCancelApplyUndoRedoHorizontalDialog");
		configureButtonsPanel();
	}

	/**
	 * Sets up the panel where the buttons of the buttons panel will be appear.
	 */
	private void configureButtonsPanel() {

		//addButtonToButtonsPanel(getJButtonApply());
		//addButtonToButtonsPanel(getJButtonUndo());
		//addButtonToButtonsPanel(getJButtonRedo());
		
	}

	/**
	 * This method initialises jButtonApply.
	 * 
	 * @return a new Apply button.
	 */
	private JButton getJButtonApply() {

		if (jButtonApply == null) {
			jButtonApply = new JButton();
			jButtonApply.setName("jButtonApply");
			jButtonApply.setIcon(iconLoader
				.load(IconLoader.ICON_APPLY_ENABLED));
			jButtonApply.setText(getStringResource()
				.getString("OKCancelApplyHorizontalDialog.jButtonApply.Text"));
			jButtonApply.setMnemonic(getStringResource().getString(
				"OKCancelApplyHorizontalDialog.jButtonApply.Mnemonic").charAt(0));
			jButtonApply.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {

					try {
						if (doOkClickBeforeHide()) {
							selectedButton = APPLY_BUTTON;
						}
					} catch (NotEnoughMemoryException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
		}
		return jButtonApply;
	}
	/**
	 * This method initialises jButtonUndo
	 * 
	 * @return a new Undo button
	 */
	private JButton getJButtonUndo() {

		if (jButtonUndo == null) {
			jButtonUndo = new JButton();
			jButtonUndo.setName("jButtonUndo");
			jButtonUndo.setIcon(iconLoader
				.load(IconLoader.ICON_UNDO_ENABLED));
			jButtonUndo.setText(getStringResource()
				.getString("OKCancelApplyHorizontalDialog.jButtonUndo.Text"));
			jButtonUndo.setMnemonic(getStringResource().getString(
				"OKCancelApplyHorizontalDialog.jButtonUndo.Mnemonic").charAt(0));
			jButtonUndo.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
                   //TODO set actions
					System.out.println("Doing something to Undo operation...");
				}
			});
		}
		return jButtonUndo;
	}
	/**
	 * This method initialises jButtonREdo
	 * 
	 * @return a new Redo button.
	 */
	private JButton getJButtonRedo() {

		if (jButtonRedo == null) {
			jButtonRedo = new JButton();
			jButtonRedo.setName("jButtonRedo");
			jButtonRedo.setIcon(iconLoader
				.load(IconLoader.ICON_REDO_ENABLED));
			jButtonRedo.setText(getStringResource()
				.getString("OKCancelApplyHorizontalDialog.jButtonRedo.Text"));
			jButtonRedo.setMnemonic(getStringResource().getString(
				"OKCancelApplyHorizontalDialog.jButtonRedo.Mnemonic").charAt(0));
			jButtonRedo.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
	                  //TODO set actions
					System.out.println("Doing something to Redo operation...");

				}
			});
		}
		return jButtonRedo;
	}
}
