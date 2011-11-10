package org.openmarkov.core.gui.dialog.node;


import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;


import org.openmarkov.core.gui.component.ElementObservable;
import org.openmarkov.core.gui.dialog.common.PrefixedOtherPropertiesTablePanel;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.model.network.ProbNode;




/**
 * Panel to set other adittionalProperties in the node not managed by OpenMarkov directly. It
 * will have a scroll table with two visible columns and a third column to store
 * the object type. Also, a "+" and "-" buttons to manage the insert and delete
 * adittionalProperties in the right side and the "Accept" and "Cancel" buttons on the
 * bottom, and a HTML comment text field
 * 
 * @author jlgozalo
 * @version 1.0 jlgozalo initial
 */
public class NodeOtherPropsTablePanel extends JPanel implements ItemListener {

	/**
	 * constructor without construction parameters
	 */
	public NodeOtherPropsTablePanel() {

		this( true, new ElementObservable() );
	}

	/**
	 * This method initialises this instance.
	 * 
	 * @param newNode
	 *            true if the node is a new node; otherwise false
	 */
	public NodeOtherPropsTablePanel(final boolean newNode,
									ElementObservable notifier) {

		dialogStringResource =
			StringResourceLoader.getUniqueInstance().getBundleDialogs();

		this.newNode = newNode;
		this.notifier = notifier;
		setName( "NodeOtherPropsTablePanel" );
		try {
			initialize();
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	/**
	 * Get the node Properties in this panel
	 * 
	 * @return the nodeProperties
	 */
	public ProbNode getNodeProperties() {

		return nodeProperties;
	}

	/**
	 * Set the node adittionalProperties in this panel with the provided ones
	 * 
	 * @param nodeProperties
	 *            the nodeProperties to set
	 */
	public void setNodeProperties(final ProbNode nodeProperties) {

		this.nodeProperties = nodeProperties;
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
	 * init the layout for this panel. Firstly, the horizontal layout will be
	 * set with one row (parallel group) in the first row the table with
	 * adittionalProperties and the Add/Delete buttons and to force the buttons to be
	 * aligned, we will do: - sequential group (gap, table, gap,
	 * buttonsParalellGroup) Then, the vertical layout will be set with the two
	 * columns (parallel group) in the first column, the table in the second
	 * column, the Add/Delete buttons
	 */
	private void initialize() {

		setPreferredSize( new Dimension( 700, 375 ) );
		final GroupLayout groupLayout = new GroupLayout( (JComponent) this );
		groupLayout.setHorizontalGroup( groupLayout.createParallelGroup(
			GroupLayout.Alignment.LEADING ).addGroup(
			groupLayout.createSequentialGroup().addContainerGap().addComponent(
				getJLabelOtherPropertiesTable(), GroupLayout.DEFAULT_SIZE,
				GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ).addPreferredGap(
				LayoutStyle.ComponentPlacement.RELATED ).addComponent(
				getOtherPropertiesTablePanel(), GroupLayout.PREFERRED_SIZE,
				GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE ).addGap(
				61, 61, 61 ) ) );
		groupLayout.setVerticalGroup( groupLayout.createParallelGroup(
			GroupLayout.Alignment.LEADING ).addGroup(
			groupLayout.createSequentialGroup().addContainerGap().addGroup(
				groupLayout
					.createParallelGroup( GroupLayout.Alignment.TRAILING )
					.addComponent(
						getOtherPropertiesTablePanel(),
						GroupLayout.Alignment.LEADING,
						GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE )
					.addComponent(
						getJLabelOtherPropertiesTable(),
						GroupLayout.Alignment.LEADING ) ).addContainerGap(
				89, Short.MAX_VALUE ) ) );
		setLayout( groupLayout );

	}

	/**
	 * This method initialises jLabelOtherPropertiesTable
	 * 
	 * @return the JLabel for the Other Properties Table
	 */
	private JLabel getJLabelOtherPropertiesTable() {

		if (jLabelOtherPropertiesTable == null) {
			jLabelOtherPropertiesTable = new JLabel();
			jLabelOtherPropertiesTable.setName( "jLabelOtherPropertiesTable" );
			jLabelOtherPropertiesTable
				.setVerticalTextPosition( SwingConstants.TOP );
			jLabelOtherPropertiesTable
				.setVerticalAlignment( SwingConstants.TOP );
			jLabelOtherPropertiesTable.setText( "a Label :" );
			jLabelOtherPropertiesTable
				.setText( dialogStringResource
					.getString( "NodeOtherPropertiesPanel.jLabelOtherPropertiesTable.Text" ) );
		}
		return jLabelOtherPropertiesTable;
	}

	/**
	 * This method initialises otherPropertiesTable.
	 * 
	 * @return the PrefixedKeyTablePanel for the Other adittionalProperties of the network
	 */
	private PrefixedOtherPropertiesTablePanel getOtherPropertiesTablePanel() {

		if (otherPropertiesTablePanel == null) {
			String columnNames[] = new String[] {
				dialogStringResource
				.getString( "NodeOtherPropertiesPanel.OtherPropertiesTablePanel.PropertyIdColumn.Label" ),
				dialogStringResource
				.getString( "NodeOtherPropertiesPanel.OtherPropertiesTablePanel.PropertyNameColumn.Label" ),
				dialogStringResource
				.getString( "NodeOtherPropertiesPanel.OtherPropertiesTablePanel.PropertyValueColumn.Label" ),
				// dialogStringResource
				// .getString("NodeOtherPropertiesPanel.OtherPropertiesTablePanel.PropertyTypeColumn.Label")
				};
			otherPropertiesTablePanel =
				new PrefixedOtherPropertiesTablePanel(
					columnNames,
					new Object[][] {},
					dialogStringResource
						.getString( "NodeOtherPropertiesPanel.OtherPropertiesTablePanel.PropertyIdColumn.Prefix" ),
					true);//, notifier ); //true => not show the id column
			}
		return otherPropertiesTablePanel;
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
	 * This method fills the content of the fields from a NodeProperties object.
	 * 
	 * @param adittionalProperties
	 *            object from where load the information.
	 */
	public void setFieldsFromProperties(ProbNode properties) {

		//getOtherPropertiesTablePanel()
			//.setData( adittionalProperties.getOtherProperties() );
	}

	/**
	 * serial uid
	 */
	private static final long serialVersionUID = 1047978130482205148L;

	/**
	 * Observable pattern - notifier
	 */
	private ElementObservable notifier = null;

	/**
	 * Dialog string resource.
	 */
	private StringResource dialogStringResource;

	/**
	 * label for the table to show the other adittionalProperties
	 */
	private JLabel jLabelOtherPropertiesTable = null;
	/**
	 * table to show the other adittionalProperties
	 */
	private PrefixedOtherPropertiesTablePanel otherPropertiesTablePanel = null;

	/**
	 * Object where all information will be saved.
	 */
	private ProbNode nodeProperties = null;

	
	/**
	 * Specifies if the node whose adittionalProperties are edited is new.
	 */
	private boolean newNode = false;

}
