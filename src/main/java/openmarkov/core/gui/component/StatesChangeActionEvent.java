/**
 * OpenMarkov - StatesChangeActionEvent.java
 */
package openmarkov.core.gui.component;



/**
 * StatesChangeActionEvent 
 * encapsulate in a single object the type of action to be execute (ADD, REMOVE, UP or DOWN)
 * and the State that is selected for this action
 * The object is used by the Observable/Observer components to update states in the GUI
 * 
 * @author jlgozalo
 * @version 1.0 jlgozalo - initial version
 * 12 Feb 2010
 *
 */
public class StatesChangeActionEvent {

	/**
	 * four actions are allowed for the object : ADD, REMOVE, UP, DOWN
	 * 
	 */
	public static int ADD = 0;
	public static int REMOVE = 1;
	public static int UP = 2;
	public static int DOWN = 3;
	
	
	/**
	 * action to be executed
	 */
	private int action = -1;
	
	/**
	 * State of the variable that is selected for the action
	 */
	private String state = null;
	
	/**
	 * default constructor
	 */
	public StatesChangeActionEvent () {
		
	}
	
	/**
	 * ParentChangeActionEvent constructor
	 * @param action - action to be executed (ADD or REMOVE)
	 * @param state - Variable to be selected for the action
	 */
	public StatesChangeActionEvent (int action, String state) {
		if (action == StatesChangeActionEvent.ADD 
  		 || action == StatesChangeActionEvent.REMOVE
  		 || action == StatesChangeActionEvent.UP
  		 || action == StatesChangeActionEvent.DOWN 	) {
			this.action = action;
			this.state = state;
		} else {
			System.out.println("Not allowed state change action. " +
					           "Only ADD, REMOVE, UP or DOWN are allowed");
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
	 * @return the state
	 */
	public String getState() {
	
		return state;
	}

	
	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
	
		this.state = state;
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
		final StatesChangeActionEvent other = (StatesChangeActionEvent) obj;
		if (action != other.action)
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals( other.state ))
			return false;
		return true;
	}

	/**
	 * toString method
	 */
	public String toString() {
		String buffer = null;
		buffer = "[StatesChangeActionEvent: ";
		if (action == StatesChangeActionEvent.ADD) {
			buffer += "ADD - "; 
		} else if (action == StatesChangeActionEvent.REMOVE) {
			buffer += "REMOVE - "; 
		} else if (action == StatesChangeActionEvent.UP) {
			buffer += "UP - "; 
		} else if (action == StatesChangeActionEvent.DOWN) {
			buffer += "DOWN - "; 
		} else {
			buffer += "action=" + Integer.toString( action) + " - ";
		}
		buffer += "State: ";
		buffer += state + "]\n";
		return buffer;
	}
	
	
}
