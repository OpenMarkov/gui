package openmarkov.core.gui.utils;


import java.awt.Container;
import java.awt.Window;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import org.openmarkov.core.model.network.State;



/**
 * This class implements various methods that are used by the rest of classes of
 * the application.
 * 
 * @author jmendoza
 * @version 1.0
 * @version 1.1 jlgozalo 25/06/09 function to set Text in components in a
 *          container
 * @version 1.2 jlgozalo - 10/05/10 - set private constructor, remove functions 
 * and fix warnings
 */
public class Util {
	
	/**
	 * private constructor for a class with only static methods
	 */
	private Util () {
		
	}
	/**
	 * Returns the window that owns the component.
	 * 
	 * @param component
	 *            component whose top level window will be returned.
	 * @return the top level ancestor of the component, if it exists and it is a
	 *         Window instance, of null if it isn't a window instance.
	 */
	public static Window getOwner(JComponent component) {

		Container ancestor = component.getTopLevelAncestor();

		if (ancestor == null) {

			return null;
		} else if (ancestor instanceof Window) {
			return (Window) ancestor;
		} else {
			return null;
		}

	}

	/**
	 * Checks if the mouse event hasn't key modifiers.
	 * 
	 * @param e
	 *            mouse event information.
	 * @return true if the mouse event hasn't modifiers; otherwise, false.
	 */
	public static boolean noMouseModifiers(MouseEvent e) {

		return ((e.getModifiers() & 0xF) == 0);

	}
	/**
	 * find the symbols for left-closed, left-open, right-closed or right-open
	 * characters 
	 * 
	 * @param states - array of intervals of the states
	 * @return true if the states has bracket symbols; false otherwise
	 */
	public static boolean hasLimitBracketSymbols(State[] states) {

		boolean result = false;
		for (State stt : states) {
			result =
				(stt.getName().indexOf('[') != -1) || (stt.getName().indexOf(
						'(') != -1)	|| (stt.getName().indexOf(')') != -1) || 
						(stt.getName().indexOf(']') != -1);
			if (result) {
				break;
			}
		}
		return result;
	}
	public static String toScapeString(String string){
		String[][] escape_symbolos = {
			 	        {"\"", "&quot;"}, // " - double-quote
			 	        {"&", "&amp;"},   // & - ampersand
			 	        {"<", "&lt;"},    // < - less-than
			 	        {">", "&gt;"},    // > - greater-than
			 	    };
		
		//for (char c:(CharSequence)string){
			
		//}
		return null;
	}
	
	/**
	 * Gets the corresponding position on potential edited.
	 * 
	 * @param row
	 *           row of JTable
	 * @param col
	 *           column of JTable
	 * @param numStates
	 *           Number of states of the node.
	 * @param numParents
	 *           Number of parents of the node.
	 *                    
	 * @return the value position on potential. -1 if the the position is 
	 * undetermined.
	 */
	public static int toPositionOnPotentialReordered(int row, int col, int numStates, 
			int numOfParents){
		//ValuePosOnTable is the Relative position of the value on the table. 
		//First value row position is 0. The first value row position is that 
		//position next to the last parent state.
		//The firsts values of each configuration of the potential are in the last row 
		int valuePosOnTable = row - numOfParents;
		if (valuePosOnTable > -1){
			return numStates - (valuePosOnTable  + 1 )+ ( 
					numStates * (col -1) );
		}else{
			return -1;
		}
		
	}
	/**
	 * Gets the corresponding position on JTable.
	 * 
	 * @param index
	 *           index of the value in the potential
	 * @param col
	 *           column of JTable
	 * @param numStates
	 *           Number of states of the node.
	 * @param numParents
	 *           Number of parents of the node.
	 *                    
	 * @return the value position on potential.
	 */
	public static int toPositionOnJtable(int index, int col, int numOfStates, 
			int numOfParents){
		
		return numOfParents -1 + numOfStates + (numOfStates * ( col - 1 ) ) - 
			index;
		
	}

	
}
