package openmarkov.core.gui.network;


import java.io.Serializable;


/**
 * This class saves the adittionalProperties of the nodes that links a link. It is used
 * only for clipboard purposes.
 * 
 * @author jlgozalo
 * @author jmendoza
 * @version 1.1 toString() method
 */
public class LinkProperties implements Cloneable, Serializable {

	/**
	 * Static field for serializable class.
	 */
	private static final long serialVersionUID = 9137727548723158016L;

	/**
	 * Properties of the source node.
	 */
	private NodeProperties sourceNodeProperties;

	/**
	 * Properties of the destination node.
	 */
	private NodeProperties destinationNodeProperties;

	/**
	 * Default constructor that sets all fields to null.
	 */
	public LinkProperties() {

		sourceNodeProperties = null;
		destinationNodeProperties = null;
	}

	/**
	 * Constructor that accepts the values of the fields.
	 * 
	 * @param newSourceNodeProperties
	 *            adittionalProperties of the source node.
	 * @param newDestinationNodeProperties
	 *            adittionalProperties of the destination node.
	 */
	public LinkProperties(NodeProperties newSourceNodeProperties,
							NodeProperties newDestinationNodeProperties) {

		setSourceNodeProperties(newSourceNodeProperties);
		setDestinationNodeProperties(newDestinationNodeProperties);
	}

	/**
	 * Returns the adittionalProperties of the source node.
	 * 
	 * @return the adittionalProperties of the source node.
	 */
	public NodeProperties getSourceNodeProperties() {

		return sourceNodeProperties;
	}

	/**
	 * Sets the adittionalProperties of the source node.
	 * 
	 * @param newSourceNodeProperties
	 *            new source node adittionalProperties.
	 */
	public void setSourceNodeProperties(NodeProperties newSourceNodeProperties) {

		sourceNodeProperties = newSourceNodeProperties;
	}

	/**
	 * Returns the adittionalProperties of the destination node.
	 * 
	 * @return the adittionalProperties of the destination node.
	 */
	public NodeProperties getDestinationNodeProperties() {

		return destinationNodeProperties;
	}

	/**
	 * Sets the adittionalProperties of the destination node.
	 * 
	 * @param newDestinationNodeProperties
	 *            new destination node adittionalProperties.
	 */
	public void setDestinationNodeProperties(
												NodeProperties newDestinationNodeProperties) {

		destinationNodeProperties = newDestinationNodeProperties;
	}

	/**
	 * Creates and returns a copy of this object.
	 * 
	 * @return a LinkProperties object as a copy of the original
	 */
	@Override
	public LinkProperties clone() {

		LinkProperties obj = null;

		try {
			obj = (LinkProperties) super.clone();
			obj.sourceNodeProperties = obj.sourceNodeProperties.clone();
			obj.destinationNodeProperties =
				obj.destinationNodeProperties.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
		return obj;
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 * 
	 * @param obj
	 *            object to compare with this one. It must be a LinkProperties
	 * @return true if the object is equal; false otherwise instance.
	 */
	@Override
	public boolean equals(Object obj) {

		LinkProperties compared = null;

		if (obj instanceof LinkProperties) {
			compared = (LinkProperties) obj;
			return compared.getSourceNodeProperties().equals(
				sourceNodeProperties)
				&& compared.getDestinationNodeProperties().equals(
					destinationNodeProperties);
		}
		return false;
	}

	/**
	 * This method removes any information about networks and nodes that belong
	 * to a network.
	 */
	public void adaptToClipboard() {

		sourceNodeProperties.adaptToClipboard();
		destinationNodeProperties.adaptToClipboard();
	}

	/**
	 * Print the information of the object
	 * 
	 * @return a String with the information of the object
	 */
	@Override
	public String toString() {

		StringBuffer buf = new StringBuffer();
		buf.append("[LinkProperties.class: " + "\n");

		buf.append("]");
		return buf.toString();
	}
}
