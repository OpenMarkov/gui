package org.openmarkov.core.gui.action;



import java.awt.geom.Point2D;
import java.util.ArrayList;


import org.openmarkov.core.action.SimplePNEdit;
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.gui.edition.visual.VisualNode;
import org.openmarkov.core.model.network.ProbNode;

/**
 * <code>MoveNodeEdi</code> is a simple edit that allow to modify the position of
 * a group of nodes
 *  
 * @version 1.0 21/12/10
 * @author Miguel Palacios
 * 
 */
public class MoveNodeEdit extends SimplePNEdit {
	
	/**
	 * 	The node
	 */
	//protected ArrayList<VisualNode> probNodes;
	/**
	 * Current X position of node
	 */
	private double currentX;
	/**
	 * Current Y position of node
	 */
	private double currentY;
	/**
	 * New X position of node
	 */
	private double newX;
	/**
	 * New Y position of node
	 */
	private double newY;
	private double diffX;
	private double diffY;
	
	private ArrayList<Point2D.Double> lastPositions = 
		new ArrayList<Point2D.Double>();
	private ArrayList<Point2D.Double> newPositions = 
		new ArrayList<Point2D.Double>();

	private ArrayList<String> namesNode = 
		new ArrayList<String>();


	/**
	 * Creates a new <code>MoveNodeEdit</code> with the nodes, and new 
	 * X, Y coordinates.
	 * @param movedNodes
	 *            the nodes that will be edited, with their new positions.
	 * 
	 */
	public MoveNodeEdit(ArrayList<VisualNode> movedNodes){
		
		super(movedNodes.get(0).getProbNode().getProbNet());
		   
		for (VisualNode visualNode:movedNodes){
			 lastPositions.add((Point2D.Double) visualNode.getPosition().clone());
			 newPositions.add( (Point2D.Double) visualNode.getTemporalPosition().
					 clone() );
			 namesNode.add(visualNode.getProbNode().getName());
		}
	       
	}

	@Override
	public void doEdit() {
		ProbNode probNode = null;
		int i = 0;
		for (String name:namesNode){
			try {
				probNode = probNet.getProbNode(name);
				probNode.getNode().setCoordinateX(newPositions.get(i).getX());
				probNode.getNode().setCoordinateY(newPositions.get(i).getY());
			} catch (ProbNodeNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
				
		
		}
	}

	public void undo() {
		super.undo();
		int i = 0;
		ProbNode probNode = null;
		for (String name:namesNode){
			try {
				probNode = probNet.getProbNode(name);
				probNode.getNode().setCoordinateX(lastPositions.get(i).getX());
				probNode.getNode().setCoordinateY(lastPositions.get(i).getY());
			} catch (ProbNodeNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
				
		
		}
	}
	/**
	 * Gets the node 
	 * 
	 *  @return probNode  
	 *  */
	
	public ProbNode getProbNode() {
		return null;
		//return probNode;
	}
	
	
}


