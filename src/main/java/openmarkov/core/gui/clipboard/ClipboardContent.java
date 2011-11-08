package openmarkov.core.gui.clipboard;


import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.ArrayList;

import openmarkov.core.gui.localize.StringResourceLoader;



/**
 * This class defines the data that can be put in the clipboard.
 * 
 * @author jmendoza
 * @author jlgozalo
 * @version 1.0 jmendoza
 * @version 1.1 jlgozalo Singleton incorporated, CloneNodesLink as private,
 *          Assignment of nodes/links using addAll() and toString() method
 *          incorporated
 */
public class ClipboardContent implements Transferable, Cloneable {

	/**
	 * Nodes adittionalProperties.
	 */
	private ArrayList<NodeProperties> nodes = null;

	/**
	 * Links adittionalProperties.
	 */
	private ArrayList<LinkProperties> links = null;

	// ESCA-JAVA0126:
	/**
	 * Constructor that saves the information to export/import.
	 * 
	 * @param newNodes
	 *            information of the nodes to export to/import from the
	 *            clipboard.
	 * @param newLinks
	 *            information of the links to export to/import from the
	 *            clipboard.
	 * @throws IllegalArgumentException
	 *             if the parameter is null or if the list is empty.
	 */
	@SuppressWarnings("unchecked")
	public ClipboardContent(ArrayList<NodeProperties> newNodes,
							ArrayList<LinkProperties> newLinks)
					throws IllegalArgumentException {

		if ((newNodes == null) || (newNodes.size() == 0)) {
			throw new IllegalArgumentException(StringResourceLoader
				.getUniqueInstance().getBundleMessages().getString(
					"ClipboardContentEmpty.Text.Label"));
		}
		nodes = new ArrayList<NodeProperties>();
		links = new ArrayList<LinkProperties>();
		nodes.addAll(newNodes);
		links.addAll(newLinks);

	}

	/**
	 * Returns the adittionalProperties of the nodes.
	 * 
	 * @return the list of adittionalProperties of nodes.
	 */
	public ArrayList<NodeProperties> getNodes() {

		return (ArrayList<NodeProperties>) nodes;

	}

	/**
	 * Returns the adittionalProperties of the links.
	 * 
	 * @return the list of adittionalProperties of links.
	 */
	public ArrayList<LinkProperties> getLinks() {

		return (ArrayList<LinkProperties>) links;

	}

	/**
	 * This method clones the nodes and the links. All the cloned links will
	 * have references to the cloned nodes. Cloned lists must be initialized and
	 * empty. If not, the clonation isn't performed.
	 * 
	 * @param sourceNodes
	 *            list of nodes to clone.
	 * @param sourceLinks
	 *            list of links to clone.
	 * @param clonedNodes
	 *            cloned list of nodes.
	 * @param clonedLinks
	 *            cloned list of links.
	 */
	private static void cloneNodesLinks(ArrayList<NodeProperties> sourceNodes,
										ArrayList<LinkProperties> sourceLinks,
										ArrayList<NodeProperties> clonedNodes,
										ArrayList<LinkProperties> clonedLinks) {

		LinkProperties newLink = null;
		int index = 0;

		if ((clonedNodes != null) && (clonedLinks != null)) {
			for (NodeProperties node : sourceNodes) {
				clonedNodes.add(node.clone());
			}
			for (LinkProperties link : sourceLinks) {
				newLink = link.clone();
				index = clonedNodes.indexOf(newLink.getSourceNodeProperties());
				if (index >= 0) {
					newLink.setSourceNodeProperties(clonedNodes.get(index));
				}
				index =
					clonedNodes.indexOf(newLink.getDestinationNodeProperties());
				if (index >= 0) {
					newLink
						.setDestinationNodeProperties(clonedNodes.get(index));
				}
				clonedLinks.add(newLink);
			}
		}

	}

	/**
	 * Creates and returns a copy of this object.
	 * 
	 * @return A clone of the current clipboard Content
	 */
	@Override
	public ClipboardContent clone() {

		ClipboardContent obj = null;

		try {
			obj = (ClipboardContent) super.clone();
			obj.nodes = new ArrayList<NodeProperties>();
			obj.links = new ArrayList<LinkProperties>();
			cloneNodesLinks(nodes, links, obj.nodes, obj.links);
		} catch (CloneNotSupportedException e) {

			return null;
		}

		return obj;

	}

	/**
	 * Returns an array of DataFlavor objects indicating the flavors the data
	 * can be provided in.
	 * 
	 * @return an array of data flavors in which this data can be transferred.
	 */
	public DataFlavor[] getTransferDataFlavors() {

		return new DataFlavor[] { new ContentDataFlavor() };

	}

	/**
	 * Returns whether or not the specified data flavor is supported for this
	 * object.
	 * 
	 * @param flavor
	 *            the requested flavor for the data.
	 * @return boolean indicating whether or not the data flavor is supported.
	 */
	public boolean isDataFlavorSupported(DataFlavor flavor) {

		return flavor.equals(new ContentDataFlavor());

	}

	/**
	 * Returns an object which represents the data to be transferred.
	 * 
	 * @param flavor
	 *            the requested flavor for the data
	 * @return an object which represents the data to be transferred.
	 * @throws UnsupportedFlavorException
	 *             if the requested data flavor is not supported.
	 */
	public Object getTransferData(DataFlavor flavor)
					throws UnsupportedFlavorException {

		if (!isDataFlavorSupported(flavor)) {
			throw new UnsupportedFlavorException(flavor);
		}

		return this;

	}

	/**
	 * Returns a String with the information in the object about nodes and links
	 * 
	 * @return a String with information in the object
	 */
	public String toString() {

		StringBuffer buf = new StringBuffer();
		buf.append("[ClipBoardContent.class: " + "\n");
		buf.append("Number of nodes: " + nodes.size() + "\n");
		for (NodeProperties node : nodes) {
			buf.append(node.toString() + "\n");
		}
		buf.append("Number of links: " + links.size() + "\n");
		for (LinkProperties link : links) {
			buf.append(link.toString() + "\n");
		}
		buf.append("]" + "\n");
		return buf.toString();
	}
}
