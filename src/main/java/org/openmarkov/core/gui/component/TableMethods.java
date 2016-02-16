package org.openmarkov.core.gui.component;

import org.openmarkov.core.model.network.Node;

public interface TableMethods {
//  TODO - Remove the comment when the method in PotentialsTablePanelOperation
//  be implemented	
//	public int[] getRowAndColumn(int potentialPosition, Node node);

	public int getPotentialIndex(int row, int column, Node node);

	public int calculateFirstEditableRow(Node properties);

	public int calculateLastEditableRow(Node properties);

}
