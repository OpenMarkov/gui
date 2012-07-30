/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.network;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;

import org.openmarkov.core.action.ChangeNetworkTypeEdit;
import org.openmarkov.core.action.NetworkCommentEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.dialog.CommentListener;
import org.openmarkov.core.gui.dialog.common.CommentHTMLScrollPane;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.PropertyNames;
import org.openmarkov.core.model.network.type.plugin.NetworkTypeManager;

/**
 * Panel to set the definition of a network. 
 * 
 * @author jlgozalo
 * @version 1.1 ibermejo
 */
public class NetworkDefinitionPanel extends JPanel implements 
		PropertyNames, CommentListener, ActionListener {

	private ProbNet probNet;
	private StringResource messageStringResource;
	private JDialog parent;
	private NetworkTypeManager networkTypeManager;
	
	/**
	 * This method initialises this instance.
	 * 
	 * @param newNetwork
	 *            to indicate if the panel is for new networks
	 * @param probNet2
	 *            manage the network access
	 */
	public NetworkDefinitionPanel(final boolean newNetwork, ProbNet probNet, JDialog parent) {

		this.probNet = probNet;
		dialogStringResource = StringResourceLoader.getUniqueInstance()
				.getBundleDialogs();
		this.newNetwork = newNetwork;
		this.parent = parent;
		setName("NetworkDefinitionPanel");
        networkTypeManager = new NetworkTypeManager (); 
		initialize();
	}

	/**
	 * initialises the panel
	 */
	private void initialize() {

		final GroupLayout groupLayout = new GroupLayout((JComponent) this);
		groupLayout
				.setHorizontalGroup(groupLayout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								groupLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												groupLayout
														.createParallelGroup(
																GroupLayout.Alignment.LEADING)
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addComponent(
																				getJTextAreaLabelNetworkDefinitionComment(),
																				GroupLayout.PREFERRED_SIZE,
																				GroupLayout.DEFAULT_SIZE,
																				GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				getCommentHTMLScrollPaneNetworkDefinition(),
																				GroupLayout.DEFAULT_SIZE,
																				370,
																				Short.MAX_VALUE)
																		.addContainerGap())
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addGroup(
																				groupLayout
																						.createParallelGroup(
																								GroupLayout.Alignment.LEADING,
																								false)
																						.addComponent(
																								getJLabelNetworkTypes(),
																								GroupLayout.DEFAULT_SIZE,
																								GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE))
																		.addPreferredGap(
																				LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				groupLayout
																						.createParallelGroup(
																								GroupLayout.Alignment.LEADING)
																						.addGroup(
																								groupLayout
																										.createSequentialGroup()
																										.addComponent(
																												getJComboBoxNetworkTypes(),
																												GroupLayout.PREFERRED_SIZE,
																												182,
																												GroupLayout.PREFERRED_SIZE)
																										.addContainerGap())))
                                                        .addGroup(
                                                                groupLayout
                                                                        .createSequentialGroup()
                                                                        .addComponent(
                                                                                      getCheckBoxIsObjectOriented (),
                                                                                      GroupLayout.PREFERRED_SIZE,
                                                                                      180,
                                                                                      GroupLayout.PREFERRED_SIZE)
                                                                                     .addContainerGap())																										
										        
										        )));
		groupLayout
				.setVerticalGroup(groupLayout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								groupLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												groupLayout
														.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
														.addComponent(
																getJLabelNetworkTypes(),
																GroupLayout.DEFAULT_SIZE,
																25,
																Short.MAX_VALUE)
														.addComponent(
																getJComboBoxNetworkTypes(),
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												groupLayout
														.createParallelGroup(
																GroupLayout.Alignment.LEADING,
																false)
														.addComponent(
																getJTextAreaLabelNetworkDefinitionComment())
														.addComponent(
																getCommentHTMLScrollPaneNetworkDefinition(),
																GroupLayout.DEFAULT_SIZE,
																117,
																Short.MAX_VALUE))
                                        .addGroup(
                                                groupLayout
                                                        .createParallelGroup(
                                                                GroupLayout.Alignment.LEADING,
                                                                false)
                                                        .addComponent(
                                                                getCheckBoxIsObjectOriented ()))
																
										.addContainerGap(189, Short.MAX_VALUE)));
		setLayout(groupLayout);
	}

	/**
	 * initializes the getJLabelNetworkTypes
	 * 
	 * @return jLabelNetworkTypes the label for the NetworkTypes field
	 */
	private JLabel getJLabelNetworkTypes() {

		if (jLabelNetworkTypes == null) {
			jLabelNetworkTypes = new JLabel();
			jLabelNetworkTypes.setText("a Label :");
			jLabelNetworkTypes.setText(dialogStringResource
					.getString("NetworkDefinitionPanel.NetworkTypes.Text"));
			jLabelNetworkTypes.setMinimumSize(new Dimension(25, 0));
			jLabelNetworkTypes.setName("jLabelNetworkTypes");
			jLabelNetworkTypes.setDisplayedMnemonic(dialogStringResource
					.getString("NetworkDefinitionPanel.NetworkTypes.Mnemonic")
					.charAt(0));
			jLabelNetworkTypes.setLabelFor(getJComboBoxNetworkTypes());
		}
		return jLabelNetworkTypes;
	}

	/**
	 * initialises the jComboBoxNetworkTypes
	 * 
	 * @return jComboBoxNetworkTypes the comboBox of the Network Types field
	 */
	private JComboBox<String> getJComboBoxNetworkTypes() {

		if (jComboBoxNetworkTypes == null) {
		    Set<String> networkTypeNames = networkTypeManager.getNetworkTypeNames ();
			List<String> networkTypes = new ArrayList<>(networkTypeNames.size ());
			
			for(String networkType : networkTypeNames)
			{
			    networkTypes.add (dialogStringResource.getString("NetworkDefinitionPanel.NetworkTypes.Items." + networkType));
			}
			Collections.sort (networkTypes);
			String[] networkTypeArray = new String[networkTypes.size ()];
			networkTypes.toArray(networkTypeArray);
			jComboBoxNetworkTypes = new JComboBox<String>(networkTypeArray);
			jComboBoxNetworkTypes.setName("jComboBoxNetworkTypes");
			jComboBoxNetworkTypes.setEditable(false);
			//
			if (newNetwork) {
				//jComboBoxNetworkTypes.setSelectedItem(networkTypes[0]);
				jComboBoxNetworkTypes.addActionListener((ActionListener) this);
				// Set Bayesian Network as default
				jComboBoxNetworkTypes.setSelectedItem (dialogStringResource.getString("NetworkDefinitionPanel.NetworkTypes.Items.BayesianNetwork"));
			} else if (!newNetwork) {
				jComboBoxNetworkTypes.setSelectedItem(dialogStringResource
						.getString("NetworkDefinitionPanel.NetworkTypes.Items."
								+ networkTypeManager.getName (probNet.getNetworkType())));
				jComboBoxNetworkTypes.addActionListener((ActionListener) this);		
			}
		}
		return jComboBoxNetworkTypes;
	}

	/**
	 * initialises the getJTextAreaLabelNetworkDefinitionComment
	 * 
	 * @return jTextAreaLabelNetworkDefinitionComment the extended label for the
	 *         comment field of Network Definition
	 */
	protected JTextArea getJTextAreaLabelNetworkDefinitionComment() {

		if (jTextAreaLabelNetworkDefinitionComment == null) {
			jTextAreaLabelNetworkDefinitionComment = new JTextArea();
			jTextAreaLabelNetworkDefinitionComment.setLineWrap(true);
			jTextAreaLabelNetworkDefinitionComment.setOpaque(false);
			jTextAreaLabelNetworkDefinitionComment
					.setName("jTextAreaLabelNetworkDefinitionComment");
			jTextAreaLabelNetworkDefinitionComment.setFocusable(false);
			jTextAreaLabelNetworkDefinitionComment.setEditable(false);
			jTextAreaLabelNetworkDefinitionComment
					.setFont(getJLabelNetworkTypes().getFont());
			jTextAreaLabelNetworkDefinitionComment.setText("an Extended Label");
			jTextAreaLabelNetworkDefinitionComment
					.setText(dialogStringResource
							.getString("NetworkDefinitionPanel.NetworkDefinitionComment.Text"));
		}
		return jTextAreaLabelNetworkDefinitionComment;
	}

	/**
	 * initialises the getCommentHTMLScrollPaneForNetworkDefinition
	 * 
	 * @return commentHTMLScrollPaneNetworkDefinition the comment for the Node
	 *         definition
	 */
	private CommentHTMLScrollPane getCommentHTMLScrollPaneNetworkDefinition() {

		if (commentHTMLScrollPaneNetworkDefinition == null) {
			commentHTMLScrollPaneNetworkDefinition = new CommentHTMLScrollPane();
			commentHTMLScrollPaneNetworkDefinition
					.setName("commentHTMLScrollPaneNetworkDefinition");

			commentHTMLScrollPaneNetworkDefinition.addCommentListener(this);

		}
		return commentHTMLScrollPaneNetworkDefinition;
	}
	
	private JCheckBox getCheckBoxIsObjectOriented ()
	{
	    if(jcheckBoxIsObjectOriented == null)
	    {
	        jcheckBoxIsObjectOriented = new JCheckBox ("Is Object Oriented", false);
	    }
	    return jcheckBoxIsObjectOriented;
	}

	/**
	 * This method fills the content of the fields from a NetworkProperties
	 * object.
	 * 
	 * @param propNet
	 *            network from where load the information.
	 */
	public void setFieldsFromProperties(ProbNet network) {

		getJComboBoxNetworkTypes().removeActionListener(this);
		getJComboBoxNetworkTypes()
				.setSelectedItem(
						dialogStringResource
								.getString("NetworkDefinitionPanel.NetworkTypes.Items."
										+ networkTypeManager.getName (probNet.getNetworkType())));

		getJComboBoxNetworkTypes().addActionListener(this);

		// set the title for comment
		MessageFormat messageForm = new MessageFormat(
				dialogStringResource.getString("NetworkDefinitionPanel."
						+ "CommentHTMLScrollPaneNetworkDefinition.Text"));

		// String shortNetworkName = (String)network.properties.
		// get(netPropertyNames.NAME.toString());
		String shortNetworkName = network.getName();
		int lastIndexOfSlashPath = shortNetworkName.lastIndexOf("\\");
		shortNetworkName = shortNetworkName.substring(lastIndexOfSlashPath + 1);
		Object[] labelArgs = new Object[] { shortNetworkName };
		getCommentHTMLScrollPaneNetworkDefinition().setTitle(
				messageForm.format(labelArgs));

		// String comment = (String)network.properties.get(
		// netPropertyNames.COMMENT.toString());

		getCommentHTMLScrollPaneNetworkDefinition().setCommentHTMLTextPaneText(
				network.getComment());
	}

	/**
	 * This method checks the name field.
	 * 
	 * @return true, if the name field isn't empty; otherwise, false.
	 */
	protected boolean checkName() {

		// String name = getJTextFieldNetworkName().getText();
		return true;

	}

	/**
	 * internal serial id
	 */
	private static final long serialVersionUID = 1047978130482205148L;

	/**
	 * The Network Type Label
	 */
	private JLabel jLabelNetworkTypes = null;
	/**
	 * The Network Types Combo Box Drop Down List
	 */
	private JComboBox<String> jComboBoxNetworkTypes = null;
	/**
	 * The Network Definition Comment Label
	 */
	private JTextArea jTextAreaLabelNetworkDefinitionComment;
	/**
	 * The Network Comment Scroll Panel box
	 */
	private CommentHTMLScrollPane commentHTMLScrollPaneNetworkDefinition = null;

    /**
     * Checkbox to define Object Orientedness of Network
     */
    private JCheckBox jcheckBoxIsObjectOriented = null;

    /**
	 * Dialog string resource.
	 */
	private StringResource dialogStringResource;

	/**
	 * Specifies if the network whose adittionalProperties are edited is new.
	 */
	private boolean newNetwork = false;

	public void commentHasChanged() {

		NetworkCommentEdit networkCommentEdit = new NetworkCommentEdit(probNet,
				getCommentHTMLScrollPaneNetworkDefinition().getCommentText());
		try {
			probNet.doEdit(networkCommentEdit);
        }
        catch (ConstraintViolationException | CanNotDoEditException | DoEditException
                | NotEnoughMemoryException | NonProjectablePotentialException
                | WrongCriterionException e)
        {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		}

	}
	@Override
	public void actionPerformed(ActionEvent ae) {
		String itemSelected  = (String) jComboBoxNetworkTypes.getSelectedItem(); 
		if (!(itemSelected == null)) {
			org.openmarkov.core.model.network.type.NetworkType selectedNetworkType = null;
			for(String networkTypeName : networkTypeManager.getNetworkTypeNames ())
			{
			    if(itemSelected.equals(dialogStringResource
			                           .getString("NetworkDefinitionPanel.NetworkTypes.Items."
			                                   + networkTypeName)))
			    {
			        selectedNetworkType = networkTypeManager.getNetworkType (networkTypeName);
			    }
			}
			

			if (selectedNetworkType != null) {
			    ChangeNetworkTypeEdit changeNetworkType = new ChangeNetworkTypeEdit(probNet, selectedNetworkType);
				try {
					probNet.doEdit(changeNetworkType);

					((NetworkPropertiesDialog)parent).getNetworkAdvancedPanel().getAgentsButton().setEnabled((probNet.getAgents() != null));
					
					((NetworkPropertiesDialog)parent).getNetworkAdvancedPanel().getDecisionCriteriaButton().setEnabled((probNet.onlyChanceNodes()));
					
                }
                catch (NotEnoughMemoryException | ConstraintViolationException
                        | CanNotDoEditException | NonProjectablePotentialException
                        | WrongCriterionException e)
                {
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );

				} catch (DoEditException e) {
					// TODO maintain comboBox with the current probNet
					
					e.printStackTrace();
					//if (!newNetwork){
						JOptionPane.showMessageDialog(this,  e.getMessage() ,
							e.getMessage() ,
							JOptionPane.ERROR_MESSAGE );
						//It cannot be  done the change selected so combobox selection must be same
						jComboBoxNetworkTypes.setSelectedItem(dialogStringResource
								.getString("NetworkDefinitionPanel.NetworkTypes.Items."
										+ probNet.getNetworkType().toString().toString()));
					//}
					
				} 
			}
			
		}
	}
}