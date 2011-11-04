package openmarkov.core.gui.network;


import org.openmarkov.core.model.network.VariableType;


/**
 * This class is used to save temporally the adittionalProperties of a network.
 * 
 * @author jmendoza
 * @version 1.0
 * @version 1.1 jlgozalo - adding get/set OtherProperties method
 */
public class NetworkProperties implements Cloneable {

	/**
	 * Name of the network.
	 */
	private String name = null;

	/**
	 * Type of the network.
	 */
	private NetworkType networkType = null;

	/**
	 * Type of the variables of the network.
	 */
	private VariableType variableType = null;

	/**
	 * Title of the network.
	 */
	private String title = null;

	/**
	 * Author of the network.
	 */
	private String author = null;

	/**
	 * Comment of the network.
	 */
	private String comment = null;

	/**
	 * Last update of the network.
	 */
	private String lastUpdate = null;

	/**
	 * User who modified the network.
	 */
	private String updatedBy = null;

	/**
	 * Version of the network.
	 */
	private Double version = null;

	/**
	 * Index in the list of default states of the nodes of the network.
	 */
	private int defaultStatesIndex = 0;

	/**
	 * other adittionalProperties in the node not directly managed by OPENMARKOV
	 */
	private Object[][] otherProperties = null;

	/**
	 * Construct a new object setting the fields to their default values.
	 */
	public NetworkProperties() {

		name = "";
		networkType = NetworkType.BAYESIAN_NET;
		variableType = VariableType.FINITE_STATES;
		title = "";
		author = "";
		comment = "";
		lastUpdate = "";
		updatedBy = "";
		version = 1.0;
		defaultStatesIndex = 0;
		otherProperties = new Object[][] {};
	}

	/**
	 * Sets the name of the network.
	 * 
	 * @param value
	 *            new name.
	 */
	public void setName(String value) {

		name = value;
	}

	/**
	 * Sets the type of the network.
	 * 
	 * @param value
	 *            new type.
	 */
	public void setNetworkType(NetworkType value) {

		networkType = value;
	}

	/**
	 * Sets the type of the variables of the network.
	 * 
	 * @param value
	 *            new type.
	 */
	public void setVariableType(VariableType value) {

		variableType = value;
	}

	/**
	 * Sets the title of the network.
	 * 
	 * @param value
	 *            new name.
	 */
	public void setTitle(String value) {

		title = value;
	}

	/**
	 * Sets the author of the network.
	 * 
	 * @param value
	 *            new author.
	 */
	public void setAuthor(String value) {

		author = value;
	}

	/**
	 * Sets the comment of the network.
	 * 
	 * @param value
	 *            new comment.
	 */
	public void setComment(String value) {

		comment = value;
	}

	/**
	 * Sets the update info of the network.
	 * 
	 * @param value
	 *            new last update info.
	 */
	public void setLastUpdate(String value) {

		lastUpdate = value;
	}

	/**
	 * Sets the user who updates the network.
	 * 
	 * @param value
	 *            new user who updated the network.
	 */
	public void setUpdatedBy(String value) {

		updatedBy = value;
	}

	/**
	 * Sets the version of the network.
	 * 
	 * @param value
	 *            new version.
	 */
	public void setVersion(Double value) {

		version = value;
	}

	/**
	 * Sets the default states index of the nodes of the network.
	 * 
	 * @param value
	 *            new default states index.
	 */
	public void setDefaultStatesIndex(int value) {

		defaultStatesIndex = value;
	}

	/**
	 * Returns the name of the network.
	 * 
	 * @return name of the network.
	 */
	public String getName() {

		return name;
	}

	/**
	 * Returns the type of the network.
	 * 
	 * @return type of the network.
	 */
	public NetworkType getNetworkType() {

		return networkType;
	}

	/**
	 * Returns the type of the variables of the network.
	 * 
	 * @return type of the variables of the network.
	 */
	public VariableType getVariableType() {

		return variableType;
	}

	/**
	 * Returns the title of the network.
	 * 
	 * @return title of the network.
	 */
	public String getTitle() {

		return title;
	}

	/**
	 * Returns the author of the network.
	 * 
	 * @return author of the network.
	 */
	public String getAuthor() {

		return author;
	}

	/**
	 * Returns the comment of the network.
	 * 
	 * @return comment of the network.
	 */
	public String getComment() {

		return comment;
	}

	/**
	 * Returns the update info of the network.
	 * 
	 * @return update info of the network.
	 */
	public String getLastUpdate() {

		return lastUpdate;
	}

	/**
	 * Returns the user who updates the network.
	 * 
	 * @return user who updates the network.
	 */
	public String getUpdatedBy() {

		return updatedBy;
	}

	/**
	 * Returns the version of the network.
	 * 
	 * @return version of the network.
	 */
	public Double getVersion() {

		return version;
	}

	/**
	 * Returns the default states index of the nodes of the network.
	 * 
	 * @return default states index of the nodes of the network.
	 */
	public int getDefaultStatesIndex() {

		return defaultStatesIndex;
	}

	/**
	 * Returns the other adittionalProperties of the node.
	 * 
	 * @return other adittionalProperties of the node.
	 */
	public Object[][] getOtherProperties() {

		return otherProperties;
	}

	/**
	 * Sets the other adittionalProperties of the node.
	 * 
	 * @param value -
	 *            new other adittionalProperties
	 */
	public void setOtherProperties(Object[][] value) {

		Object[][] newValue = value;

		if (value == null) {
			newValue = new Object[][] {};
		}
		otherProperties = newValue;
	}


	/**
	 * Indicates whether some other object is "equal to" this one.
	 * 
	 * @param obj
	 *            object to compare with this one. It must be a
	 *            NetworkProperties instance.
	 * @return true if the object is equal to this instance object
	 */
	@Override
	public boolean equals(Object obj) {

		NetworkProperties properties;
		if (obj instanceof NetworkProperties) {
			properties = (NetworkProperties) obj;
			if (name.equals( properties.getName() )
				&& networkType.equals( properties.getNetworkType() )
				&& variableType.equals( properties.getVariableType() )
				&& title.equals( properties.getTitle() )
				&& author.equals( properties.getAuthor() )
				&& comment.equals( properties.getComment() )
				&& lastUpdate.equals( properties.getLastUpdate() )
				&& updatedBy.equals( properties.getUpdatedBy() )
				&& version.equals( properties.getVersion() )
				&& (defaultStatesIndex == properties.getDefaultStatesIndex())
				&& otherProperties.length == properties.getOtherProperties().length) {
				if (otherProperties.length == 0) {
					return true;
				} else {
					boolean result = true;
					for (int i = 0; result & i < otherProperties.length; i++) {
						result =
							(otherProperties[i][0].equals( properties
								.getOtherProperties()[i][0] ) && otherProperties[i][1]
								.equals( properties.getOtherProperties()[i][1] ));
					}
					return result;
				}
			}
		}
		return false;
	}

	/**
	 * Creates and returns a copy of this object.
	 * 
	 * @return the clone of the NetworkProperties instance
	 */
	@Override
	public NetworkProperties clone() {

		NetworkProperties obj = null;

		try {
			obj = (NetworkProperties) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
		return obj;
	}
}
