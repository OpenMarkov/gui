package org.openmarkov.core.gui.dialog.common;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;

import org.openmarkov.core.gui.component.ValuesTable;
import org.openmarkov.core.gui.dialog.node.ICIOptionsPanel;
import org.openmarkov.core.model.network.ProbNode;

@SuppressWarnings("serial")
public class CPTablePanel extends TablePotentialPanel{
	
	
	/**
	 * Indicates if the data of the table is modifiable.
	 */
	private boolean modifiable;
	
	
	public CPTablePanel(ProbNode probNode) {
		super(probNode);
		modifiable = false;
		//add(iciOptionPanel,BorderLayout.NORTH);
		//repaint();
		
	}
	
	/**
	 * This method initializes valuesTable and defines that first two columns
	 * are not selectable
	 * 
	 * @return a new values table.
	 */
	@Override
	protected ValuesTable getValuesTable() {

		if (valuesTable == null) {
			valuesTable = new ValuesTable( probNode, getTableModel(), modifiable );
			valuesTable.setName( "PotentialsTablePanel.valuesTable" );
		}
		return valuesTable;
	}
	/**
	 * This method initializes valuesTableScrollPane.
	 * 
	 * @return a new values table scroll pane.
	 */
	@Override
	public JScrollPane getValuesTableScrollPane() {

		if (valuesTableScrollPane == null) {
			valuesTableScrollPane = new JScrollPane();
			valuesTableScrollPane
				.setName( "ProbabilityTablePanel.valuesTableScrollPane" );
			valuesTableScrollPane.setViewportView( getValuesTable() );
		}
		return valuesTableScrollPane;
	}
	
}
