/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.dialog.common;

import org.openmarkov.core.exception.UnrecoverableException;
import org.openmarkov.gui.loader.element.IconBind;
import org.openmarkov.core.localize.StringDatabase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class implements a dialog box with a horizontal buttons panel placed in
 * the bottom of the window. This panel has two buttons: a 'OK' button that is
 * activated pressing the ENTER key and a 'CANCEL' button activated pressing the
 * ESC key.
 *
 * @author jmendoza
 * @version 1.2 jlgozalo - 30/05/2010 set OK_BUTTON to default
 */
public class OkCancelHorizontalDialog extends BottomPanelButtonDialog {

	/**
	 *
	 */
	private static final long serialVersionUID = 1176820837760605949L;

	// ESCA-JAVA0007:
	/**
	 * Constant that indicates that the user has pressed 'Ok' button.
	 */
	public static int OK_BUTTON = JOptionPane.OK_OPTION;

	// ESCA-JAVA0007:
	/**
	 * Constant that indicates that the user has pressed 'Cancel' button.
	 */
	public static int CANCEL_BUTTON = JOptionPane.CANCEL_OPTION;

	/**
	 * Button selected by the user.
	 */
	protected int selectedButton = 0;
	/**
	 * String database
	 */
	protected StringDatabase stringDatabase = StringDatabase.getUniqueInstance();
	/**
	 * Ok button.
	 */
	private JButton jButtonOK = null;
	/**
	 * Cancel button.
	 */
	private JButton jButtonCancel = null;

	/**
	 * Constructor. initialises the instance.
	 *
	 * @param owner window that owns the dialog.
	 */
	public OkCancelHorizontalDialog(Window owner) {
		super(owner);
		initialize();
		pack();
	}

	/**
	 * This method initialises this instance.
	 */
	private void initialize() {
		// setSize(550, 310);
		setName("OKCancelHorizontalDialog");
		configureButtonsPanel();
		setDefaultButton(getJButtonOK());
	}

	/**
	 * Sets up the panel where the buttons of the buttons panel will be appear.
	 */
	private void configureButtonsPanel() {
		addButtonToButtonsPanel(getJButtonOK());
		addButtonToButtonsPanel(getJButtonCancel());
	}

	/**
	 * This method initialises jButtonApply.
	 *
	 * @return a new Ok button.
	 */
    protected JButton getJButtonOK() {
        
        if (this.jButtonOK == null) {
            this.jButtonOK = new JButton();
            this.jButtonOK.setName("jButtonApply");
            this.jButtonOK.setIcon(IconBind.ACCEPT_ENABLED.icon());
            this.jButtonOK.setText(this.stringDatabase.getString("OKCancelHorizontalDialog.jButtonOK.Text"));
            this.jButtonOK.setMnemonic(this.stringDatabase.getString("OKCancelHorizontalDialog.jButtonOK.Mnemonic")
                                                          .charAt(0));
            this.jButtonOK.addActionListener(getOnOkClickListener());
        }
        return this.jButtonOK;
    }
    
    protected ActionListener getOnOkClickListener() {
        if (this.onOkClickListener == null) {
            this.onOkClickListener = e -> {
                try {
                    if (doOkClickBeforeHide()) {
                        selectedButton = OK_BUTTON;
                        dispose();
                    }
                } catch (Exception ex) {
                    throw new UnrecoverableException(ex);
                }
            };
        }
        return this.onOkClickListener;
    }
    
    private ActionListener onOkClickListener;

	/**
	 * This method initialises jButtonCancel.
	 *
	 * @return a new Cancel button.
	 */
	protected JButton getJButtonCancel() {
        
        if (this.jButtonCancel == null) {
            this.jButtonCancel = new JButton();
            this.jButtonCancel.setName("jButtonCancel");
            this.jButtonCancel.setIcon(IconBind.REMOVE_ENABLED.icon());
            this.jButtonCancel.setText(this.stringDatabase.getString("OKCancelHorizontalDialog.jButtonCancel.Text"));
            this.jButtonCancel
                    .setMnemonic(this.stringDatabase.getString("OKCancelHorizontalDialog.jButtonCancel.Mnemonic")
                                                    .charAt(0));
            setCancelButton(this.jButtonCancel);
            this.jButtonCancel.addActionListener(new ActionListener() {

				@Override public void actionPerformed(ActionEvent e) {
					doCancelClickBeforeHide();
                    OkCancelHorizontalDialog.this.selectedButton = CANCEL_BUTTON;
					setVisible(false);
					dispose();
				}
			});
		}
        return this.jButtonCancel;
	}

	/**
	 * Shows or hides this Dialog depending on the value of parameter b. If b is
	 * true, the selected button is set to OK_BUTTON.
	 *
	 * @param b if true, makes the dialog visible, otherwise hides the dialog.
	 */
	@Override public void setVisible(boolean b) {

		if (b) {
            this.selectedButton = OK_BUTTON;
		}
		super.setVisible(b);
	}

	/**
	 * This method carries out the actions when the user presses the Ok button
	 * before hiding the dialog.
	 *
	 * @return true if the dialog box can be closed.
	 */
    protected boolean doOkClickBeforeHide() throws Exception {
		return true;
	}

	/**
	 * This method carries out the actions when the user press the Cancel button
	 * before hide the dialog.
	 */
	protected void doCancelClickBeforeHide() {

	}

	public int getSelectedButton() {
        return this.selectedButton;
	}
 
 
}
