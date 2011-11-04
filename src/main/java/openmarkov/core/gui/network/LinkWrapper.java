package openmarkov.core.gui.network;


import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.ProbNode;


/**
 * This class is used only to contain the references to the nodes that it links
 * as {@link openmarkov.gui.networks.NodeWrapper} instances.
 * 
 * @author jmendoza
 */
public class LinkWrapper {

	/**
	 * Underlying link.
	 */
	private Link link = null;

	/**
	 * Source node of the link.
	 */
	private ProbNode node1 = null;

	/**
	 * Destination node of the link.
	 */
	private ProbNode node2 = null;

	/**
	 * Constructor that saves the underlying link and the nodes that it links.
	 * 
	 * @param newLink
	 *            link to save.
	 * @param newNode1
	 *            source node.
	 * @param newNode2
	 *            destination node.
	 */
	public LinkWrapper(Link newLink, ProbNode newNode1, ProbNode newNode2) {

		link = newLink;
		node1 = newNode1;
		node2 = newNode2;
	}

	/**
	 * Returns the underlying link.
	 * 
	 * @return the underlying link.
	 */
	Link getLink() {

		return this.link;
	}
	/**
	 * Sets the underlying link.
	 * 
	 * 
	 */
	public void setLink(Link newLink) {

		this.link=newLink;
	}

	/**
	 * Returns the source node.
	 * 
	 * @return the source node.
	 */
	public ProbNode getNode1() {

		return this.node1;
	}

	/**
	 * Returns the destination node.
	 * 
	 * @return the destination node.
	 */
	public ProbNode getNode2() {

		return this.node2;
	}

	/**
	 * Indicates whether some other object is "equal to" this one. Two instances
	 * are equal if the underlying links are equal.
	 * 
	 * @param obj
	 *            object to compare to. It must be a LinkWrapper instance.
	 */
	@Override
	public boolean equals(Object obj) {

		LinkWrapper compared;

		if (obj instanceof LinkWrapper) {
			compared = (LinkWrapper) obj;
			return node1.equals(compared.getNode1())
				&& node2.equals(compared.getNode2());
		}
		return false;
	}
}
