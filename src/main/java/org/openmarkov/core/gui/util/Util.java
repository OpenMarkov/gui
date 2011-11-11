package org.openmarkov.core.gui.util;


import java.awt.Container;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.util.HashSet;

import javax.swing.JComponent;

import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
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
	
	/**
	 * This method checks if exists the specified node.
	 * 
	 * @param name
	 *            name of the node to search.
	 * @return true if the node exists; otherwise, false.
	 */
	
	//called by nodeDefinitionPanel and nodePropertiesDialogListenerAssitent
	//for static test (unused)
	
	public static boolean existNode(ProbNet probNet, String name) {
		
		try {
			probNet.getProbNode(name);
			return true;
		} catch (ProbNodeNotFoundException e) {
			return false;
		}
	}
	/** Traslates a <code>String</code> from windows style to UNIX (change \\ 
	 * for /)
	 * @param windowsString <code>String</code>
	 * @return String */
	public static String windows2unixPath(String windowsString) {
		int longStr = windowsString.length();
		String unixString = new String();
		String barra ="\\";
		char barraChar = barra.charAt(0);
		for (int i = 0; i < longStr; i++) {
			char c = windowsString.charAt(i);
			if (c == barraChar) {
				unixString = unixString + "/";
			} else {
				unixString = unixString + c; 
			}
		}
		return unixString;
	}
	/**
	 * This method returns the name of a new node with a specified type. The
	 * name of the nodes starts with a letter that depends on its type: - Chance
	 * nodes name starts with any capital letter except 'D' and 'U'. - Decision
	 * nodes name starts with 'D'. - Utility nodes name starts with 'U'. Then
	 * the name continues with an index. If there are already a node that starts
	 * with a desired letter, an index is added to the letter to form the new
	 * name of the node.
	 * 
	 * @param type
	 *            type of the new node.
	 * @param existingNames
	 *            array that contains all the existing names of nodes.
	 * @return the name of the next node that is going to be created.
	 */
	public static String getNextNodeName(NodeType type,
											HashSet<String> existingNames) {

		String name = null;

		switch (type) {
		case CHANCE: {
			name = getNextChanceNodeName( existingNames );
			break;
		}
		case DECISION: {
			name = getNextDecisionNodeName( existingNames );
			break;
		}
		case UTILITY: {
			name = getNextUtilityNodeName( existingNames );
			break;
		}
		}
		return name;
	}
	/**
	 * This method returns the name of the next chance node. If exists the node
	 * 'A', then checks if exists the node 'B'. If this node already exists 'B',
	 * then checks the node 'C', and so on until 'Z'. If exists the node 'Z',
	 * then checks 'A1', 'B1', etc. If exists 'Z1' then checks 'A2'. The only
	 * letters that this method never returns are 'D' and 'U'.
	 * 
	 * @param existingNames
	 *            names of the nodes that exist.
	 * @return the name of the next chance node.
	 */
	private static String getNextChanceNodeName(HashSet<String> existingNames) {

		char letter;
		int index;
		boolean found = false;
		String name = "";

		index = 0;
		while (!found) {
			letter = 'A';
			while (!found && (letter <= 'Z')) {
				name = letter + ((index > 0) ? Integer.toString( index ) : "");
				if (!existingNames.contains( name )) {
					found = true;
				} else {
					letter++;
					if ((letter == 'D') || (letter == 'U')) {
						letter++;
					}
				}
			}
			index++;
		}
		return name;
	}

	/**
	 * This method returns the name of the next decision node. If exists the
	 * node 'D', then checks if exists the node 'D1'. If this node exists, the
	 * checks the node 'D2', and so on.
	 * 
	 * @param existingNames
	 *            names of the nodes that exist.
	 * @return the name of the next decision node.
	 */
	private static String getNextDecisionNodeName(HashSet<String> existingNames) {

		return getNextNodeWithLetter( existingNames, 'D' );
	}

	/**
	 * This method returns the name of the next utility node. If exists the node
	 * 'U', then checks if exists the node 'U1'. If this node exists, the checks
	 * the node 'U2', and so on.
	 * 
	 * @param existingNames
	 *            names of the nodes that exist.
	 * @return the name of the next utility node.
	 */
	private static String getNextUtilityNodeName(HashSet<String> existingNames) {

		return getNextNodeWithLetter( existingNames, 'U' );
	}

	/**
	 * This method returns the name of the next node whose name starts with a
	 * specified letter. For example the letter is 'X'. If exists the node 'X',
	 * then checks if exists the node 'X1'. If this node exists, the checks the
	 * node 'X2', and so on.
	 * 
	 * @param existingNames
	 *            names of the nodes that exist.
	 * @param letter
	 *            letter that the next node starts with.
	 * @return the name of the next node that starts with a specified letter.
	 */
	private static String getNextNodeWithLetter(HashSet<String> existingNames,
												char letter) {

		String name = "";
		int index;
		boolean found = false;

		index = 0;
		while (!found) {
			name = letter + ((index > 0) ? Integer.toString( index ) : "");
			if (!existingNames.contains( name )) {
				found = true;
			} else {
				index++;
			}
		}
		return name;
	}
	public static String getPath(){
		 return System.getProperty("user.dir");
	}
	public static String getResourcesPath(){
		 return getPath() + "\\src\\main\\resources";
	}



	
}
