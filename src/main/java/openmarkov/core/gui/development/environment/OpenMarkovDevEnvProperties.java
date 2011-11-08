/**
 * 
 */
package openmarkov.core.gui.development.environment;


import java.util.HashMap;


/**
 * The <code>OpenMarkovDevEnvProperties</code> stores the different adittionalProperties
 * that the environment requires to work like WorkingFileName, etc...
 * 
 * @author jlgozalo
 * @version 1.0 jlgozalo 05/04/2009
 */
public class OpenMarkovDevEnvProperties {

	/**
	 * public constructor
	 */
	public OpenMarkovDevEnvProperties() {

		properties = new HashMap<String, Object>();
	}

	/**
	 * Return the adittionalProperties for the Development Environment
	 * 
	 * @return adittionalProperties
	 */
	public HashMap<String, Object> getProperties() {

		return properties;
	}

	/**
	 * @param adittionalProperties
	 *            the adittionalProperties to set
	 */
	public void setProperties(HashMap<String, Object> properties) {

		this.properties = properties;
	}

	/**
	 * This object contains all the information that the development environment
	 * needs for working properly
	 */
	private HashMap<String, Object> properties = null;

	/**
	 * To string method
	 * 
	 * @return string with the content for this instance
	 */
	@Override
	public String toString() {

		StringBuffer buf = new StringBuffer();
		buf.append("[OpenMarkovDevEnvProperties : \n");
		buf.append(properties.toString());
		buf.append("\n]");
		return buf.toString();
	}
}
