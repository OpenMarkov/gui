package org.openmarkov.core.gui.dialog.link;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.JPanel;

import org.openmarkov.core.gui.dialog.common.OkCancelApplyUndoRedoHorizontalDialog;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.model.graph.Link;

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
		String title = dialogStringResource
				.getString("NodePotentialDialog.Title.Label");

		setTitle(dialogStringResource
				.getString("NodePotentialDialog.Title.Label")
				+ ": "
				+ (link == null ? "" : "link between"));

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

	private JPanel getLinkRestrictionPanel() {

		this.linkRestrictionPanel = new LinkRestrictionPanel(link);
		return this.linkRestrictionPanel;
	}

}
