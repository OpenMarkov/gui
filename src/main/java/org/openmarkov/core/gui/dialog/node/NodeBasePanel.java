/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog.node;


import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;





/**
 * Base Panel for node panels
 * 
 * @author jlgozalo
 * @version 1.0 jlgozalo
 */
public class NodeBasePanel extends JPanel implements ItemListener {

	private StringResource messageStringResource;

	/**
	 * constructor without construction parameters
	 */
	public NodeBasePanel() {

		dialogStringResource =
			StringResourceLoader.getUniqueInstance().getBundleDialogs();
		init();

	}

	/**
	 * This method initialises this instance.
	 * 
	 * @param newNode
	 *            true if the node is a new node; otherwise false
	 */
	public NodeBasePanel(final boolean newNode) {

		dialogStringResource =
			StringResourceLoader.getUniqueInstance().getBundleDialogs();

		messageStringResource =	
				StringResourceLoader.getUniqueInstance().getBundleMessages();
		this.newNode = newNode;
		setName("NodeBasePanel");
		init();

	}

	/**
	 * set the visual aspect of the panel
	 */
	private void init() {

		try {
			initialize();
		} catch (Throwable e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
			
		}
	}


	/**
	 * @return the newNode
	 */
	public boolean isNewNode() {

		return newNode;
	}

	/**
	 * @param newNode
	 *            the newNode to set
	 */
	public void setNewNode(boolean newNode) {

		this.newNode = newNode;
	}

	/**
	 * <p>
	 * <code>Initialize</code>
	 * <p>
	 * initialize the layout for this panel
	 */
	private void initialize() throws Exception {

		final GroupLayout groupLayout = new GroupLayout((JComponent) this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(
			GroupLayout.Alignment.LEADING).addGap(0, 500, Short.MAX_VALUE));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(
			GroupLayout.Alignment.LEADING).addGap(0, 375, Short.MAX_VALUE));
		setLayout(groupLayout);

	}

	/**
	 * Invoked when an item has been selected.
	 * 
	 * @param e
	 *            event information.
	 */
	public void itemStateChanged(ItemEvent e) {

	}


	/**
	 * serial uid
	 */
	private static final long serialVersionUID = 1047978130482205148L;

	/**
	 * Dialog string resource.
	 */
	private StringResource dialogStringResource = null;

	/**
	 * Network to which the node belongs.
	 */

	/**
	 * Specifies if the node whose additionalProperties are edited is new.
	 */
	private boolean newNode = false;

}
