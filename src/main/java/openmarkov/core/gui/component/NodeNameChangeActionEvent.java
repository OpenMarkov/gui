/**
 * OpenMarkov - StatesChangeActionEvent.java
 */
package openmarkov.core.gui.component;


/**
 * NodeNameChangeActionEvent encapsulate in a single object the data for the
 * action to execute 
 * The object is used by the Observable/Observer components to
 * update name of node in the GUI
 * 
 * @author jlgozalo
 * @version 1.0 jlgozalo - initial version 12 Feb 2010
 */
public class NodeNameChangeActionEvent {

	/**
	 * old name of the variable that is selected for the action
	 */
	private String oldName = null;

	/**
	 * new name of the variable that is selected for the action
	 */
	private String newName = null;

	/**
	 * default constructor
	 */
	public NodeNameChangeActionEvent() {

	}

	/**
	 * NodeNameChangeActionEvent constructor
	 * 
	 * @param oldName -
	 *            String with the old name to be changed
	 * @param newName -
	 *            String for the new name to be set
	 */
	public NodeNameChangeActionEvent(String oldName, String newName) {

		this.oldName = oldName;
		this.newName = newName;
	}

	/**
	 * @return the oldName
	 */
	public String getOldName() {

		return oldName;
	}

	/**
	 * @param oldName -
	 *            the oldName to set
	 */
	public void setOldName(String oldName) {

		this.oldName = oldName;
	}

	/**
	 * @return the newName
	 */
	public String getNewName() {

		return newName;
	}

	/**
	 * @param newName - 
	 *            the newName to set
	 */
	public void setNewName(String newName) {

		this.newName = newName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (this == obj){
			return true;}
		if (obj == null){
			return false;}
		if (getClass() != obj.getClass()){
			return false;}
		final NodeNameChangeActionEvent other = (NodeNameChangeActionEvent) obj;
		if (oldName == null) {
			if (other.oldName != null){
				return false;}
		} else if (!oldName.equals( other.oldName )){
			return false;}
		if (newName == null) {
			if (other.newName != null){
				return false;}
		} else if (!newName.equals( other.newName )){
			return false;}
		return true;
	}

	/**
	 * toString method
	 */
	public String toString() {

		String buffer = null;
		buffer = "[NodeNameChangeActionEvent: ";
		buffer += "OldName=";
		buffer += oldName;
		buffer += "; NewName=";
		buffer += newName + " ]\n";
		return buffer;
	}

}
