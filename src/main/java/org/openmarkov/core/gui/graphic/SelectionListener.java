/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.graphic;


import java.util.ArrayList;

/**
 * This interface is used in order to a network panel advise to the listener
 * that some objects are selected, to copy, cut or remove them.
 * 
 * @author jmendoza
 * @author asaez
 * @version 1.1 	Added arrayOfNodes as a new parameter
 * 					Needed for knowing which nodes are currently selected
 */
public interface SelectionListener {

	/**
	 * This method indicates how many nodes and links are selected.
	 * 
	 * @param nodes
	 *            number of selected nodes.
	 * @param links
	 *            number of selected links.
	 * @param arrayOfNodes
	 * 			  array of nodes that are currently selected
	 */
	void objectsSelected(int nodes, int links, ArrayList<VisualNode> arrayOfNodes);
	
}
