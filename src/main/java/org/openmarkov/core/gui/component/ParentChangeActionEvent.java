/**
 * OpenMarkov - ParentChangeActionEvent.java
 */
package org.openmarkov.core.gui.component;

import org.openmarkov.core.model.network.Variable;


/**
 * ParentChangeActionEvent 
 * encapsulate in a single object the type of action to be execute (ADD or REMOVE)
 * and the Variable that is selected for this action
 * The object is used by the Observable/Observer components to update parents in the GUI
 * 
 * @author jlgozalo
 * @version 1.0 jlgozalo - initial version
 * 12 Feb 2010
 *
 */
public class ParentChangeActionEvent {

	/**
	 * two actions are allowed for the object : ADD or REMOVE
	 * 
	 */
	public static int ADD = 0;
	public static int REMOVE = 1;
	
	/**
	 * action to be executed
	 */
	private int action = -1;
	
	/**
	 * Variable that is selected for the action
	 */
	private Variable parent = null;
	
	/**
	 * default constructor
	 */
	public ParentChangeActionEvent () {
		
	}
	
	/**
	 * ParentChangeActionEvent constructor
	 * @param action - action to be executed (ADD or REMOVE)
	 * @param parent - Variable to be selected for the action
	 */
	public ParentChangeActionEvent (int action, Variable parent) {
		if (action == ParentChangeActionEvent.ADD || action == ParentChangeActionEvent.REMOVE) {
			this.action = action;
			this.parent = parent;
		} else {
			System.out.println("Not allowed parent change action. Only ADD or REMOVE are allowed");
		}
	}

	
	/**
	 * @return the action
	 */
	public int getAction() {
	
		return action;
	}

	
	/**
	 * @param action the action to set
	 */
	public void setAction(int action) {
	
		this.action = action;
	}

	
	/**
	 * @return the parent
	 */
	public Variable getParent() {
	
		return parent;
	}

	
	/**
	 * @param parent the parent to set
	 */
	public void setParent(Variable parent) {
	
		this.parent = parent;
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ParentChangeActionEvent other = (ParentChangeActionEvent) obj;
		if (action != other.action)
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals( other.parent ))
			return false;
		return true;
	}

	/**
	 * toString method
	 */
	public String toString() {
		String buffer = null;
		buffer = "[ParentChangeActionEvent: ";
		if (action == ParentChangeActionEvent.ADD) {
			buffer += "ADD - "; 
		} else if (action == ParentChangeActionEvent.REMOVE) {
			buffer += "REMOVE - "; 
		} else {
			buffer += "action=" + Integer.toString( action) + " - ";
		}
		buffer += "Variable: ";
		buffer += parent.getName() + "]\n";
		return buffer;
	}
	
	
}
