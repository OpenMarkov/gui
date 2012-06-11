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

import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
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
import org.openmarkov.core.gui.util.NetworkType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.PropertyNames;
import org.openmarkov.core.model.network.type.BayesianNetworkType;
import org.openmarkov.core.model.network.type.DECPOMDPType;
import org.openmarkov.core.model.network.type.DecisionAnalysisNetworkType;
import org.openmarkov.core.model.network.type.InfluenceDiagramType;
import org.openmarkov.core.model.network.type.MDPType;
import org.openmarkov.core.model.network.type.OOBNType;
import org.openmarkov.core.model.network.type.POMDPType;
import org.openmarkov.core.model.network.type.SimpleMarkovModelType;

/**
 * Panel to set the definition of a network. It will have no title field, a
 * TypeNetwork group (with two radio buttons) and a HTML comment text field
 * 
 * @author jlgozalo
 * @version 1.0 jlgozalo
 */
public class NetworkDefinitionPanel extends JPanel implements 
		PropertyNames, CommentListener, ActionListener {

	/*
	 * /** default constructor without construction parameters to allow GUI
	 * builders to do visual representation
	 * 
	 * public NetworkDefinitionPanel() {
	 * 
	 * dialogStringResource =
	 * StringResourceLoader.getUniqueInstance().getBundleDialogs();
	 * networkProperties = new NetworkProperties();
	 * setName("NetworkDefinitionPanel"); initialize(); }
	 */

	private ProbNet probNet;

	private String comment = null;

	
	private StringResource messageStringResource;
	/**
	 * This method initialises this instance.
	 * 
	 * @param newNetwork
	 *            to indicate if the panel is for new networks
	 * @wbp.parser.constructor
	 */
	public NetworkDefinitionPanel(final boolean newNetwork) {

		dialogStringResource = StringResourceLoader.getUniqueInstance()
				.getBundleDialogs();

		messageStringResource =	
				StringResourceLoader.getUniqueInstance().getBundleMessages();

		this.newNetwork = newNetwork;
		setName("NetworkDefinitionPanel");
		initialize();

	}

	/**
	 * This method initialises this instance.
	 * 
	 * @param newNetwork
	 *            to indicate if the panel is for new networks
	 * @param probNet2
	 *            manage the network access
	 */
	public NetworkDefinitionPanel(final boolean newNetwork, ProbNet probNet) {

		this.probNet = probNet;
		dialogStringResource = StringResourceLoader.getUniqueInstance()
				.getBundleDialogs();
		this.newNetwork = newNetwork;
		setName("NetworkDefinitionPanel");
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
																										.addContainerGap()))))));
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
	private JComboBox getJComboBoxNetworkTypes() {

		if (jComboBoxNetworkTypes == null) {
			
			String []networkTypes = {
					
					dialogStringResource.
						getString("NetworkDefinitionPanel.NetworkTypes.Items."
							+ NetworkType.BAYESIAN_NET.toString()),
					dialogStringResource
					.getString("NetworkDefinitionPanel.NetworkTypes.Items."
							+ NetworkType.SIMPLE_MARKOV_MODEL.toString()),
					dialogStringResource
							.getString("NetworkDefinitionPanel.NetworkTypes.Items."
									+ NetworkType.INFLUENCE_DIAGRAM.toString()),
					dialogStringResource
									.getString("NetworkDefinitionPanel.NetworkTypes.Items."
											+ NetworkType.MARKOV_DECISION_PROCESS.toString()),
					dialogStringResource
								.getString("NetworkDefinitionPanel.NetworkTypes.Items."
													+ NetworkType.POMDP.toString()),
					dialogStringResource
					.getString("NetworkDefinitionPanel.NetworkTypes.Items."
															+ NetworkType.DAN.toString()),
					dialogStringResource
					.getString("NetworkDefinitionPanel.NetworkTypes.Items."
															+ NetworkType.DEC_POMDP.toString()),
															
					dialogStringResource
					.getString("NetworkDefinitionPanel.NetworkTypes.Items."
															+ NetworkType.OOBN.toString())
															
			
			};
			jComboBoxNetworkTypes = new JComboBox(networkTypes);
			jComboBoxNetworkTypes.setName("jComboBoxNetworkTypes");
			jComboBoxNetworkTypes.setEditable(false);
			//
			if (newNetwork) {
				//jComboBoxNetworkTypes.setSelectedItem(networkTypes[0]);
				jComboBoxNetworkTypes.addActionListener((ActionListener) this);	
			} else if (!newNetwork) {
				jComboBoxNetworkTypes.setSelectedItem(dialogStringResource
						.getString("NetworkDefinitionPanel.NetworkTypes.Items."
								+ probNet.getNetworkType().toString().toString()));
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

	/**
	 * This method fills the content of the fields from a NetworkProperties
	 * object.
	 * 
	 * @param propNet
	 *            network from where load the information.
	 */
	public void setFieldsFromProperties(ProbNet network) {

		getJComboBoxNetworkTypes().removeActionListener(this);

		if (network.hasConstraint(BayesianNetworkType.class)) {

			getJComboBoxNetworkTypes()
					.setSelectedItem(
							dialogStringResource
									.getString("NetworkDefinitionPanel.NetworkTypes.Items."
											+ NetworkType.BAYESIAN_NET
													.toString()));

		} else if (network.hasConstraint(InfluenceDiagramType.class)) {
			getJComboBoxNetworkTypes()
					.setSelectedItem(
							dialogStringResource
									.getString("NetworkDefinitionPanel.NetworkTypes.Items."
											+ NetworkType.INFLUENCE_DIAGRAM
													.toString()));

		} else if (network.hasConstraint(InfluenceDiagramType.class)) {
			getJComboBoxNetworkTypes()
					.setSelectedItem(
							dialogStringResource
									.getString("NetworkDefinitionPanel.NetworkTypes.Items."
											+ NetworkType.DAN.toString()));

		} else if (network.hasConstraint(SimpleMarkovModelType.class)) {
			getJComboBoxNetworkTypes()
					.setSelectedItem(
							dialogStringResource
									.getString("NetworkDefinitionPanel.NetworkTypes.Items."
											+ NetworkType.SIMPLE_MARKOV_MODEL
													.toString()));
		} else if (network.hasConstraint(MDPType.class)) {
			getJComboBoxNetworkTypes()
					.setSelectedItem(
							dialogStringResource
									.getString("NetworkDefinitionPanel.NetworkTypes.Items."
											+ NetworkType.MARKOV_DECISION_PROCESS
													.toString()));
		} else if (network.hasConstraint(POMDPType.class)) {
			getJComboBoxNetworkTypes()
					.setSelectedItem(
							dialogStringResource
									.getString("NetworkDefinitionPanel.NetworkTypes.Items."
											+ NetworkType.POMDP.toString()));
		}

		getJComboBoxNetworkTypes().addActionListener(this);

		/*
		 * case CHAIN_GRAPH: { getJComboBoxNetworkTypes().setSelectedItem(
		 * dialogStringResource
		 * .getString("NetworkDefinitionPanel.NetworkTypes.Items." +
		 * NetworkType.CHAIN_GRAPH.toString())); break; } }
		 */

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
	private JComboBox jComboBoxNetworkTypes = null;
	/**
	 * The Network Definition Comment Label
	 */
	private JTextArea jTextAreaLabelNetworkDefinitionComment;
	/**
	 * The Network Comment Scroll Panel box
	 */
	private CommentHTMLScrollPane commentHTMLScrollPaneNetworkDefinition = null;

	/**
	 * Dialog string resource.
	 */
	private StringResource dialogStringResource;

	/**
	 * Specifies if the network whose adittionalProperties are edited is new.
	 */
	private boolean newNetwork = false;

/*	public void itemStateChanged(ItemEvent iE) {
		ItemSelectable itemSelectable = iE.getItemSelectable();

		Object selected[] = itemSelectable.getSelectedObjects();
		String itemSelected = selected.length == 0 ? "null"
				: (String) selected[0];
		if (!(itemSelected == null)) {
			ChangeNetworkTypeEdit changeNetworkType = null;
			if (itemSelected.equals(dialogStringResource
					.getString("NetworkDefinitionPanel.NetworkTypes.Items."
							+ NetworkType.BAYESIAN_NET.toString()))) {
				changeNetworkType = new ChangeNetworkTypeEdit(probNet,
						BayesianNetworkType.getUniqueInstance());

			} else if (itemSelected.equals(dialogStringResource
					.getString("NetworkDefinitionPanel.NetworkTypes.Items."
							+ NetworkType.INFLUENCE_DIAGRAM.toString()))) {
				changeNetworkType = new ChangeNetworkTypeEdit(probNet,
						InfluenceDiagramType.getUniqueInstance());
			} else if (itemSelected.equals(dialogStringResource
					.getString("NetworkDefinitionPanel.NetworkTypes.Items."
							+ NetworkType.DAN.toString()))) {
				changeNetworkType = new ChangeNetworkTypeEdit(probNet,
						DecisionAnalysisNetworkType.getUniqueInstance());
			} else if (itemSelected.equals(dialogStringResource
					.getString("NetworkDefinitionPanel.NetworkTypes.Items."
							+ NetworkType.SIMPLE_MARKOV_MODEL.toString()))) {
				changeNetworkType = new ChangeNetworkTypeEdit(probNet,
						SimpleMarkovModelType.getUniqueInstance());
			} else if (itemSelected.equals(dialogStringResource
					.getString("NetworkDefinitionPanel.NetworkTypes.Items."
							+ NetworkType.MARKOV_DECISION_PROCESS.toString()))) {
				changeNetworkType = new ChangeNetworkTypeEdit(probNet,
						MDPType.getUniqueInstance());
			} else if (itemSelected.equals(dialogStringResource
					.getString("NetworkDefinitionPanel.NetworkTypes.Items."
							+ NetworkType.POMDP.toString()))) {
				changeNetworkType = new ChangeNetworkTypeEdit(probNet,
						POMDPType.getUniqueInstance());
			}
			if (changeNetworkType != null)
				try {
					probNet.getPNESupport().announceEdit(changeNetworkType);
					probNet.getPNESupport().doEdit(changeNetworkType);
				} catch (NotEnoughMemoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );

				} catch (ConstraintViolationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (CanNotDoEditException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (DoEditException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (NonProjectablePotentialException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (WrongCriterionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				}
			;
		}

	}*/

	public void commentHasChanged() {

		NetworkCommentEdit networkCommentEdit = new NetworkCommentEdit(probNet,
				getCommentHTMLScrollPaneNetworkDefinition().getCommentText());
		try {
			probNet.getPNESupport().announceEdit(networkCommentEdit);
			probNet.getPNESupport().doEdit(networkCommentEdit);
		} catch (ConstraintViolationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		} catch (CanNotDoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		} catch (DoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		} catch (NotEnoughMemoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		} catch (NonProjectablePotentialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		} catch (WrongCriterionException e) {
			// TODO Auto-generated catch block
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
		if (!(itemSelected == null) /*&& !itemSelected.equals(dialogStringResource
				.getString("NetworkDefinitionPanel.NetworkTypes.Items."
						+ probNet.getNetworkType().toString()))*/ ) {
			ChangeNetworkTypeEdit changeNetworkType = null;
			if (itemSelected.equals(dialogStringResource
					.getString("NetworkDefinitionPanel.NetworkTypes.Items."
							+ NetworkType.BAYESIAN_NET.toString()))) {
				changeNetworkType = new ChangeNetworkTypeEdit(probNet,
						BayesianNetworkType.getUniqueInstance());

			} else if (itemSelected.equals(dialogStringResource
					.getString("NetworkDefinitionPanel.NetworkTypes.Items."
							+ NetworkType.INFLUENCE_DIAGRAM.toString()))) {
				changeNetworkType = new ChangeNetworkTypeEdit(probNet,
						InfluenceDiagramType.getUniqueInstance());
			} else if (itemSelected.equals(dialogStringResource
					.getString("NetworkDefinitionPanel.NetworkTypes.Items."
							+ NetworkType.DAN.toString()))) {
				changeNetworkType = new ChangeNetworkTypeEdit(probNet,
						DecisionAnalysisNetworkType.getUniqueInstance());
			} else if (itemSelected.equals(dialogStringResource
					.getString("NetworkDefinitionPanel.NetworkTypes.Items."
							+ NetworkType.SIMPLE_MARKOV_MODEL.toString()))) {
				changeNetworkType = new ChangeNetworkTypeEdit(probNet,
						SimpleMarkovModelType.getUniqueInstance());
			} else if (itemSelected.equals(dialogStringResource
					.getString("NetworkDefinitionPanel.NetworkTypes.Items."
							+ NetworkType.MARKOV_DECISION_PROCESS.toString()))) {
				changeNetworkType = new ChangeNetworkTypeEdit(probNet,
						MDPType.getUniqueInstance());
			} else if (itemSelected.equals(dialogStringResource
					.getString("NetworkDefinitionPanel.NetworkTypes.Items."
							+ NetworkType.POMDP.toString()))) {
				changeNetworkType = new ChangeNetworkTypeEdit(probNet,
						POMDPType.getUniqueInstance());
			} else if (itemSelected.equals(dialogStringResource
					.getString("NetworkDefinitionPanel.NetworkTypes.Items."
							+ NetworkType.DEC_POMDP.toString()))) {
				changeNetworkType = new ChangeNetworkTypeEdit(probNet,
						DECPOMDPType.getUniqueInstance());
			} else if (itemSelected.equals(dialogStringResource
					.getString("NetworkDefinitionPanel.NetworkTypes.Items."
							+ NetworkType.OOBN.toString()))) {
				changeNetworkType = new ChangeNetworkTypeEdit(probNet,
						OOBNType.getUniqueInstance());
			}
			if (changeNetworkType != null /*&& !itemSelected.equals(dialogStringResource
					.getString("NetworkDefinitionPanel.NetworkTypes.Items."
							+ probNet.getNetworkType().toString()))*/) {
				try {
					probNet.getPNESupport().announceEdit(changeNetworkType);
					probNet.getPNESupport().doEdit(changeNetworkType);
				} catch (NotEnoughMemoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );

				} catch (ConstraintViolationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (CanNotDoEditException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (DoEditException e) {
					// TODO maintain comboBox with the current probNet
					
					e.printStackTrace();
					String message = e.getMessage();
					if (!newNetwork){
						JOptionPane.showMessageDialog(this,  e.getMessage() ,
							e.getMessage() ,
							JOptionPane.ERROR_MESSAGE );
						//It cannot be  done the change selected so combobox selection must be same
						jComboBoxNetworkTypes.setSelectedItem(dialogStringResource
								.getString("NetworkDefinitionPanel.NetworkTypes.Items."
										+ probNet.getNetworkType().toString().toString()));
					}
					
				} catch (NonProjectablePotentialException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				} catch (WrongCriterionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, messageStringResource
							.getString( e.getMessage() ),
						messageStringResource.getString( e.getMessage() ),
						JOptionPane.ERROR_MESSAGE );
				}
			
			}
			
		}
	}
}