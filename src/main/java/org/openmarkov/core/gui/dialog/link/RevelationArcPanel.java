package org.openmarkov.core.gui.dialog.link;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.openmarkov.core.gui.dialog.common.PrefixedKeyTablePanel;
import org.openmarkov.core.gui.dialog.common.SelectableKeyTablePanel;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;

/*****
 * Panel to set the revealing states for a link
 * 
 * @author ckonig
 * 
 */
@SuppressWarnings("serial")
public class RevelationArcPanel extends JPanel implements ItemListener {

	/**
	 * Dialog string resource.
	 */
	private StringResource dialogStringResource;
	

	 private StringResource messageStringResource;
	/***
	 * Object where all the information will be saved
	 */
	private Link link;

	/**
	 * label for the table to show the values of the node
	 */
	private JLabel jLabelValuesPanel = null;

	/**
	 * table to show the states of the node
	 */

	private SelectableKeyTablePanel nodeDiscreteStatesTablePanel;

	/**
	 * constructor without construction parameters
	 */
	public RevelationArcPanel(Link link) {

		this.link = link;
		dialogStringResource = StringResourceLoader.getUniqueInstance()
				.getBundleDialogs();
		messageStringResource =	
				StringResourceLoader.getUniqueInstance().getBundleMessages();
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

	public void initialize() {
		setPreferredSize(new Dimension(600, 375));
		this.add(this.getJLabelValuesPanel());
		this.add(this.getNodeDiscreteStatesTablePanel());
	}

	/**
	 * @return
	 */
	protected JLabel getJLabelValuesPanel() {

		if (jLabelValuesPanel == null) {
			jLabelValuesPanel = new JLabel();
			jLabelValuesPanel.setName("jLabelValuesPanel");
			jLabelValuesPanel.setText("a Label");
			jLabelValuesPanel.setText(dialogStringResource
					.getString("RevelationArcPanel.jLabelValuesPanel.Text"));
		}
		return jLabelValuesPanel;
	}

	/**
	 * This method initializes NodeValuesTable.
	 * 
	 * @return the PrefixedKeyTablePanel for the Node Values
	 */
	protected PrefixedKeyTablePanel getNodeDiscreteStatesTablePanel() {

		if (nodeDiscreteStatesTablePanel == null) {
			String[] columnNames = {
					dialogStringResource
							.getString("DiscreteValuesTablePanel.ValuesTable."
									+ "Columns.Name.Text"),
					"",
					dialogStringResource
							.getString("DiscreteValuesTablePanel.ValuesTable."
									+ "Columns.Value.Text") };

			nodeDiscreteStatesTablePanel = new SelectableKeyTablePanel(
					columnNames, new Object[][] {},
					dialogStringResource
							.getString("DiscreteValuesTablePanel.ValuesTable."
									+ "Columns.Id.Prefix"), true, link);
		}
		nodeDiscreteStatesTablePanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		return nodeDiscreteStatesTablePanel;
	}

	public void setFieldsFromProperties(Link link) {

		if (link != null) {

			nodeDiscreteStatesTablePanel
					.setData(convertStringsToTableDiscreteFormat(link));
		}

	}

	/**
	 * Convert an array of strings in an array of arrays of objects with the
	 * same elements.
	 * 
	 * @param values
	 *            array of strings.
	 * @return an array of arrays of objects that has the same elements.
	 */
	protected Object[][] convertStringsToTableDiscreteFormat(Link link) {
		ProbNode node = (ProbNode) link.getNode1().getObject();
		State[] values = node.getVariable().getStates();
		ArrayList<State> revealingStates = link.getRevealingStates();
		Object[][] data;
		int i, l;

		l = values.length;
		data = new Object[l][2];
		i = l - 1;
		for (State value : values) {
			data[i][0] = revealingStates.contains(value) ? true : false;
			data[i--][1] = value.getName();
		}
		return data;
	}

	public void itemStateChanged(ItemEvent e) {

	}

	public void saveChanges() {

	}

}
