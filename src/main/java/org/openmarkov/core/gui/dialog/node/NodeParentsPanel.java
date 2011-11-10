package org.openmarkov.core.gui.dialog.node;


import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;


import org.openmarkov.core.gui.dialog.common.PrefixedDataTablePanel;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.ProbNode;



/**
 * Panel to set the parents of a node.
 * 
 * @author jlgozalo
 * @version 1.0 jlgozalo
 */
public class NodeParentsPanel extends JPanel implements ItemListener {

	/**
	 * constructor without construction parameters
	 */
	public NodeParentsPanel() {

		this(false);//, new ElementObservable());
	}

	/**
	 * constructor
	 */ 
	public NodeParentsPanel(ProbNode probNode){//, ElementObservable notifier) {
		this(false);//, notifier);
		
		this.probNode = probNode;
		try {
			initialize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}	

	/**
	 * This method initialises this instance.
	 * 
	 * @param newNode - true if the node is a new node; otherwise false
	 * @param notifier - Observable notifier 
	 */
	public NodeParentsPanel(final boolean newNode){//, ElementObservable notifier) {

		dialogStringResource =
			StringResourceLoader.getUniqueInstance().getBundleDialogs();
		this.newNode = newNode;
		//this.notifier = notifier;
		setName("NodeParentsPanel");
		
	}

	/**
	 * <p>
	 * <code>Initialize</code>
	 * <p>
	 * initialize the layout for this panel
	 */
	private void initialize() throws Exception {

		setPreferredSize(new Dimension(700, 300));
		final GroupLayout groupLayout = new GroupLayout((JComponent) this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(getJLabelNodeParentsTable(), GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(getPrefixedDataTablePanelParentsTable(), GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(getPrefixedDataTablePanelParentsTable(), GroupLayout.PREFERRED_SIZE, 229, GroupLayout.PREFERRED_SIZE)
						.addComponent(getJLabelNodeParentsTable()))
					.addContainerGap(14, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}

	/**
	 * @return
	 */
	protected JLabel getJLabelNodeParentsTable() {

		if (jLabelNodeParentsTable == null) {
			jLabelNodeParentsTable = new JLabel();
			jLabelNodeParentsTable.setName("jLabelNodeParentsTable");
			jLabelNodeParentsTable.setText("a Label");
			jLabelNodeParentsTable.setText(dialogStringResource
				.getString("NodeParentsPanel.jLabelNodeParentsTable.Text"));
		}
		return jLabelNodeParentsTable;
	}

	/**
	 * This method initializes prefixedDataTablePanelParentsTable
	 * 
	 * @return a new parents table.
	 */
	protected PrefixedDataTablePanel getPrefixedDataTablePanelParentsTable() {

		if (prefixedDataTablePanelParentsTable == null) {
			prefixedDataTablePanelParentsTable =
				new PrefixedDataTablePanel( probNode,
					new String[] { "",dialogStringResource
						.getString("NodeParentsPanel.prefixedDataTablePanelParentsTable.Columns.Name.Text") },
					new Object[][] {},
					new Object[][] {},
					dialogStringResource
						.getString("NodeParentsPanel.prefixedDataTablePanelParentsTable.Title"),
					true);//, notifier);
			prefixedDataTablePanelParentsTable
				.setName("prefixedDataTablePanelParentsTable");
		}
		return prefixedDataTablePanelParentsTable;
	}

	/**
	 * Get the node adittionalProperties in this panel
	 * 
	 * @return the nodeProperties
	 */
	public ProbNode getNetworkProperties() {

		return probNode;
	}

	/**
	 * Set the node adittionalProperties in this panel with the provided ones
	 * 
	 * @param nodeProperties
	 *            the nodeProperties to set
	 */
	public void setNodeProperties(final ProbNode nodeProperties) {

		this.probNode = nodeProperties;
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
	 * Invoked when an item has been selected.
	 * 
	 * @param e
	 *            event information.
	 */
	public void itemStateChanged(ItemEvent e) {

		subItemStateChanged(e);
	}

	/**
	 * Invoked when an item has been selected. This method must be overridden in
	 * subclasses to listen to their combobox components.
	 * 
	 * @param e
	 *            event information.
	 */
	protected void subItemStateChanged(ItemEvent e) {

	};

	/**
	 * This method fills a NodeProperties object from the content of the fields
	 * of the dialog box.
	 * 
	 * @param adittionalProperties
	 *            object where save the information.
	 */
	/*public void setPropertiesFromFields(NodeProperties adittionalProperties) {

		adittionalProperties
			.setParents(fillNodeWrapperWithArray(prefixedDataTablePanelParentsTable
				.getData()));

	}*/

	/**
	 * This method fills the content of the fields from a NodeProperties object.
	 * 
	 * @param adittionalProperties
	 *            object from where load the information.
	 */
	public void setFieldsFromProperties(ProbNode node) {

		getPrefixedDataTablePanelParentsTable().setData(
			fillArrayWithNodes(node.getNode().getParents()));
		//getPrefixedDataTablePanelParentsTable().setPrefixedData(
			//fillArrayWithNodeWrapper(node.getPossibleParents()));

	}

	/**
	 * Returns an array of arrays of objects that contains in each row the name
	 * and the title of each node of the arraylist.
	 * 
	 * @param nodes
	 *            arraylist of nodes.
	 * @return an array of arrays of objects that contains the name and the
	 *         title of the nodes.
	 */

	private static Object[][] fillArrayWithNodes(
														ArrayList<Node> nodes) {

		int i, l;
		Object[][] result;
		l = nodes.size();
		result = new Object[l][2];
		for (i = 0; i < l; i++) {
            result[i][0] = "p_"+i; //internal name for the parent
			result[i][1] = ((ProbNode)nodes.get(i).getObject()).getName();
		}
		
		return result;
	}


	/**
	 * serial uid
	 */
	private static final long serialVersionUID = 1047978130482205148L;

	/**
	 * Table where the parents are shown.
	 */
	private PrefixedDataTablePanel prefixedDataTablePanelParentsTable;

	/**
	 * Label for the table
	 */
	private JLabel jLabelNodeParentsTable;

	/**
	 * Dialog string resource.
	 */
	private StringResource dialogStringResource;

	/**
	 * Object where all information will be saved.
	 */
	private ProbNode probNode = null;

	/**
	 * Specifies if the node whose adittionalProperties are edited is new.
	 */
	private boolean newNode = false;


}
