package org.openmarkov.core.gui.window.edition;

import org.openmarkov.core.model.network.ProbNet;


/**
 * This class assists to a network panel in operations with the clipboard.
 * 
 * @author jmendoza
 * @version 1.0
 */
public class EditorPanelClipboardAssistant {


	/**
	 * Network panel that this object assists.
	 */
	private ProbNet network = null;

	/**
	 * Constructor that saves the references to the network panel and the
	 * clipboard manager.
	 * 
	 * @param newNetwork
	 *            network to manage.
	 */
	public EditorPanelClipboardAssistant(ProbNet newNetwork) {
//TODO review the clipboard function
	
		network = newNetwork;

	}

	/**
	 * This method extracts the adittionalProperties of the nodes and the adittionalProperties of
	 * their links and copies them into the clipboard.
	 * 
	 * @param nodes
	 *            nodes to copy to the clipboard.
	 */
	/**public void exportToClipboard(ArrayList<NodeWrapper> nodes) {

		ArrayList<NodeProperties> nodesToCopy = new ArrayList<NodeProperties>();
		HashSet<LinkProperties> linksToCopy = new HashSet<LinkProperties>();
		NodeProperties nodeProperties1 = null;
		NodeProperties nodeProperties2 = null;
		LinkProperties linkProperties = null;

		for (NodeWrapper node : nodes) {
			nodeProperties1 = node.getProperties();
			nodeProperties1.adaptToClipboard();
			nodesToCopy.add(nodeProperties1);
			for (LinkWrapper link : node.getLinks()) {
			
				//changed by mpalacios
				//linkwrapper return probnode instead nodewrapper
			//	nodeProperties1 = link.getNode1().getProperties();
				nodeProperties1.adaptToClipboard();
			//	nodeProperties2 = link.getNode2().getProperties();
				nodeProperties2.adaptToClipboard();
				linkProperties =
					new LinkProperties(nodeProperties1, nodeProperties2);
				linksToCopy.add(linkProperties);
			}
		}
		clipboardManager.exportToClipboard(new ClipboardContent(nodesToCopy,
			new ArrayList<LinkProperties>(linksToCopy)));

	}*/

	/**
	 * This method imports various nodes from the clipboard and creates them in
	 * the network.
	 * 
	 * @param pastedElements
	 *            an array of two elements: the first element will contain the
	 *            list of pasted nodes and the second one will contain the list
	 *            of created links. This array must be initialized.
	 * @return true if all the nodes of the clipboard were pasted; if any node
	 *         wasn't pasted, return false.
	 */
	@SuppressWarnings( { "cast", "unchecked" })
	/*public boolean pasteFromClipboard(ArrayList[] pastedElements) {

		ArrayList<NodeProperties> nodes = null;
		ArrayList<LinkProperties> links = null;
		ClipboardContent content = null;
		ArrayList[] createdElements = null;

		content = clipboardManager.importFromClipboard();
		if (content != null) {
			nodes = content.getNodes();
			links = content.getLinks();
			//change CRITICAL for CLIPBOARD
			//createdElements = network.createAndAdaptNodesAndLinks(nodes, links);
			pastedElements[0] = (ArrayList<NodeWrapper>) createdElements[0];
			pastedElements[1] = (ArrayList<LinkWrapper>) createdElements[1];

			return (pastedElements[0].size() == nodes.size());
		}

		return true;

	}*/

	/**
	 * This method says if there is data stored in the clipboard.
	 * 
	 * @return true if there is data stored in the clipboard; otherwise, false.
	 */
	public boolean isThereDataStored() {

		return false ;

	}
}
