package org.openmarkov.core.gui.dialog.link;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.text.MessageFormat;

import javax.swing.JPanel;

import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.gui.dialog.common.OkCancelApplyUndoRedoHorizontalDialog;
import org.openmarkov.core.gui.dialog.common.ProbabilityTablePanel;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.PolicyType;
import org.openmarkov.core.model.network.ProbNode;

@SuppressWarnings("serial")
public class LinkRestrictionEditDialog extends
		OkCancelApplyUndoRedoHorizontalDialog {

	private Link link;

	/**
	 * Message string resource for i18n
	 */
	private StringResource messageStringResource;

	/**
	 * Dialog string resource.
	 */
	private StringResource dialogStringResource;

	/**
	 * Panel of the graphic editor
	 */
	private LinkRestrictionPanel linkRestrictionPanel;

	public LinkRestrictionEditDialog(Window owner, Link link) {
		super(owner);
		this.link = link;
		((ProbNode) link.getNode1().getObject()).getProbNet().getPNESupport()
				.openParenthesis();
		initialize();
		setLocationRelativeTo(owner);
		setMinimumSize(new Dimension(750, 450));
		setResizable(true);
	}

	/**
	 * This method configures the dialog box.
	 */
	private void initialize() {

		dialogStringResource = StringResourceLoader.getUniqueInstance()
				.getBundleDialogs();
		messageStringResource = StringResourceLoader.getUniqueInstance()
				.getBundleMessages();

		ProbNode node1 = (ProbNode) link.getNode1().getObject();
		ProbNode node2 = (ProbNode) link.getNode2().getObject();
		String title = "";
		if (link != null) {
			MessageFormat messageForm = new MessageFormat(
					dialogStringResource
							.getString("LinkRestrictionDialog.Title.Label"));
			Object[] labelArgs = new Object[] { node1.getName(),
					node2.getName() };
			title = messageForm.format(labelArgs);
		}
		setTitle(title);
		configureComponentsPanel();
		pack();
	}

	/**
	 * Sets up the panel where all components, except the buttons of the buttons
	 * panel, will be appear.
	 */
	private void configureComponentsPanel() {
		getComponentsPanel().setLayout(new BorderLayout(5, 5));

		getComponentsPanel()
				.add(getLinkRestrictionPanel(), BorderLayout.CENTER);
	}

	private ProbabilityTablePanel getLinkRestrictionPanel() {

		if (this.linkRestrictionPanel == null) {
			this.linkRestrictionPanel = new LinkRestrictionPanel(link);
		} 
		return linkRestrictionPanel;
	}

	/**
	 * @return An integer indicating the button clicked by the user when closing
	 *         this dialog
	 */
	public int requestValues() {
		setVisible(true);
		return selectedButton;
	}

	/**
	 * This method carries out the actions when the user presses the OK button
	 * before hiding the dialog.
	 * 
	 * @return true if all the fields are correct.
	 * @throws NotEnoughMemoryException
	 */
	@Override
	protected boolean doOkClickBeforeHide() throws NotEnoughMemoryException {
		getLinkRestrictionPanel().saveChanges();
		((ProbNode) link.getNode1().getObject()).getProbNet().getPNESupport()
				.closeParenthesis();
		return true;
	}

	@Override
	protected void doCancelClickBeforeHide() {
		((ProbNode) link.getNode1().getObject()).getProbNet().getPNESupport()
				.closeParenthesis();

	}

}
