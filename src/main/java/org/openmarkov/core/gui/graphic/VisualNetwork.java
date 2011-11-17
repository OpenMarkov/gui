package org.openmarkov.core.gui.graphic;


import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoableEdit;


import org.openmarkov.core.action.AddLinkEdit;
import org.openmarkov.core.action.AddProbNodeEdit;
import org.openmarkov.core.action.CRemoveLinkEdit;
import org.openmarkov.core.action.PNESupport;
import org.openmarkov.core.action.PNUndoableEditEvent;
import org.openmarkov.core.action.PNUndoableEditListener;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.gui.edition.EditorPanel;
import org.openmarkov.core.gui.network.NetworkChangeListener;
import org.openmarkov.core.gui.undo.MovedNodeInfo;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;




/**
 * This class implements the visual representation of a network.
 * 
 * @author jmendoza 1.0
 * @author jlgozalo 1.1
 * @version 1.1 Javadoc tags corrected, variables initialized * 
 * @version 1.2 asaez - modified the constructor, the creation of
 *              visual nodes and the order of painting nodes
 */
public class VisualNetwork implements NetworkChangeListener, 
PNUndoableEditListener {

	
	/**
	 * Network whose visual representation is managed by this object.
	 */
	private ProbNet probNet = null;
	
	/**
	 * Editor panel associated to this network.
	 */
	private EditorPanel editorPanel = null;
	
	/**
	 * This variable indicates if nodes must be drawn by title.
	 */
	// TODO este valor debe asignarse usando las preferencias de usuario de
	// visualización de redes
	private boolean byTitle = false;
	
	/**
	 * List of visual nodes.
	 */
	private ArrayList<VisualNode> visualNodes = new ArrayList<VisualNode>();

	/**
	 * List of visual links.
	 */
	private ArrayList<VisualLink> visualLinks = new ArrayList<VisualLink>();

	/**
	 * List of selected nodes.
	 */
	private ArrayList<VisualNode> selectedNodes = new ArrayList<VisualNode>();

	/**
	 * List of selected links.
	 */
	private ArrayList<VisualLink> selectedLinks = new ArrayList<VisualLink>();

	/**
	 * This variable indicates if any node has change its selection state.
	 */
	private boolean changedSelectionStateNode = false;

	/**
	 * Listener to the selection.
	 */
	private HashSet<SelectionListener> selectionListeners =
		new HashSet<SelectionListener>();

	private Graphics2D g2;

	//private LinkWrapper linkWrapper;
	/**
	 * Position of the mouse cursor when it is pressed.
	 */

	/** 
	 * Creates a new visual network.
	 * 
	 * @param probNet
	 *            object that has the information of the network.
	 * @param editorPanel
	 *            editor panel associated to this network.
	 */ 
	public VisualNetwork(ProbNet probNet, EditorPanel editorPanel) {
        
		//this.pNESupport.addUndoableEditListener(this);
		this.probNet = probNet;
		this.editorPanel = editorPanel;
		
		//network.addNetworkChangeListener(this);
		//changed by mpalacios
		constructVisualInfo();

	}

	/**
	 * This method notifies that a network has been changed.
	 * 
	 * @param newNetwork
	 *            changed network.
	 */
	@SuppressWarnings("unused")
	public void networkChanged(ProbNet newNetwork) {

		//constructVisualInfo();
		//changed by mpalacios
	}

	/**
	 * Calculates the width and height of the panel according to the position of
	 * the left-most and bottom-most nodes.
	 * 
	 * @param g
	 *            graphics where the network is painted.
	 * @return an array that contains the lowest and highest X coordinate and
	 *         the lowest and highest Y coordinate.
	 */
	public double[] getNetworkBounds(Graphics2D g) {

		double[] networkBounds =
			{ Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE,
				Double.MIN_VALUE };
		Rectangle2D nodeBounds = null;
	

		for (VisualNode node : visualNodes) {
			nodeBounds = node.getShape(g).getBounds2D();
			networkBounds[0] = Math.min(nodeBounds.getMinX(), networkBounds[0]);
			networkBounds[1] = Math.max(nodeBounds.getMaxX(), networkBounds[1]);
			networkBounds[2] = Math.min(nodeBounds.getMinY(), networkBounds[2]);
			networkBounds[3] = Math.max(nodeBounds.getMaxY(), networkBounds[3]);
		}
		networkBounds[0] -= 2;
		networkBounds[1] += 2;
		networkBounds[2] -= 2;
		networkBounds[3] += 2;

		return networkBounds;

	}

	/**
	 * This method constructs the lists of the visual nodes and visual links. It
	 * only creates visual information for the new nodes and links and delete
	 * the visual representation of the nodes and links that don't exist.
	 */
	private void constructVisualInfo() {

		ArrayList<ProbNode> nodes = null;
		ArrayList<VisualNode> vNodesToDelete = new ArrayList<VisualNode>();
		ArrayList<VisualLink> vLinksToDelete = new ArrayList<VisualLink>();
		ArrayList<Link> links = null;
		ProbNode nodeToCheck = null;
		Link linkToCheck = null;
		VisualNode vNode1 = null;
		VisualNode vNode2 = null;
		int i = -1;
		int l = -1;

		nodes = probNet.getProbNodes();
		for (VisualNode vNode : visualNodes) {
			nodeToCheck = vNode.getProbNode();
			int index = nodes.indexOf(nodeToCheck);
			if ( index >=0 && vNode.getTemporalPosition().getX() == nodes.get(
					index).getNode().getCoordinateX() && vNode.
					getTemporalPosition().getX() == nodes.get( index ).getNode().
					getCoordinateX() ){
			//if (nodes.contains(nodeToCheck)  ) {
				//nodes.indexOf(o)
				nodes.remove(nodeToCheck);
			
			} else {
				vNodesToDelete.add(vNode);
			}
		}
		
		
		
		visualNodes.removeAll(vNodesToDelete);
		for (ProbNode node : nodes) {
			vNode1 = createVisualNode(node);
			visualNodes.add(vNode1);
			vNode1.setByTitle(byTitle);
			
		}
		
		//links = probNet.backupProbNet.getLinks();
		links = probNet.getGraph().getLinks();
		
		for (VisualLink vLink : visualLinks) {
			linkToCheck = vLink.getLink();
			if (links.contains(linkToCheck) && !containsNodeToDelete(linkToCheck, vNodesToDelete)) {
				links.remove(linkToCheck);
			} else {
				vLinksToDelete.add(vLink);
			}
		}
		visualLinks.removeAll(vLinksToDelete);
		l = visualNodes.size();
		for (Link link : links) {
			i = 0;
			vNode1 = null;
			vNode2 = null;
			while ((i < l) && ((vNode1 == null) || (vNode2 == null))) {
				if (vNode1 == null) {
					if (link.getNode1().equals(
						visualNodes.get(i).getProbNode().getNode())) {
						vNode1 = visualNodes.get(i);
					}
				}
				if (vNode2 == null) {
					if (link.getNode2().equals(
						visualNodes.get(i).getProbNode().getNode())) {
						vNode2 = visualNodes.get(i);
					}
				}
				i++;
			}
			if ((vNode1 != null) && (vNode2 != null)) {
				visualLinks.add(new VisualLink(link, vNode1, vNode2));
			}
		}
	}

	private boolean containsNodeToDelete(Link linkToCheck, ArrayList<VisualNode> vNodesToDelete) {
		
		for (VisualNode vNode: vNodesToDelete)
		if (linkToCheck.contains(vNode.getProbNode().getNode()))
			return true;
		
			return false;
	}

	/**
	 * Changes the presentation mode of the text of the nodes.
	 * 
	 * @param value
	 *            new value of the presentation mode of the text of the nodes.
	 */
	public void setByTitle(boolean value) {

		if (byTitle != value) {
			byTitle = value;
		}
		for (VisualNode node : visualNodes) {
			node.setByTitle(value);
		}

	}

	/**
	 * Returns the presentation mode of the text of the nodes.
	 * 
	 * @return true if the title of the nodes is the name or false if it is the
	 *         name.
	 */
	public boolean getByTitle() {

		return byTitle;

	}

	/**
	 * Creates a new list of visual nodes reordering them following
	 * this criteria: 
	 * - first criteria: selection state -> the selected nodes are in
	 *   the first places of the array.
	 * - second criteria: relevance -> the higher the relevance 
	 *   the nearer to the start of the array.
	 * 
	 * @return a new ordered array (first, selected nodes, and last, 
	 * 			non selected nodes; each group is ordered in 
	 * 			descending relevance criteria).
	 */
	private ArrayList<VisualNode> reorderVisualNodes() {
		int selPos = 0;
		ArrayList<VisualNode> newList = new ArrayList<VisualNode>();
		ArrayList<VisualNode> nodesSelected = new ArrayList<VisualNode>();
		ArrayList<VisualNode> nodesUnselected = new ArrayList<VisualNode>();

		for (VisualNode node : visualNodes) {
			if (node.isSelected()) {
				nodesSelected.add(node);
			} else {
				nodesUnselected.add(node);
			}
		}
		
		int selected = nodesSelected.size();
		int counter1 = 0;
		while (counter1 < selected) {
			VisualNode candidate = null;
			double highestRelevance = -1;
			for (int i=0; i<nodesSelected.size(); i++) {
				double relevance = nodesSelected.get(i).getProbNode().getRelevance();
				if (relevance > highestRelevance) {
					highestRelevance = relevance;
					candidate = nodesSelected.get(i);
				}
			}
			newList.add(selPos, candidate);
			selPos++;
			nodesSelected.remove(candidate);
			counter1++;
		}
		
		int unselected = nodesUnselected.size();
		int counter2 = 0;
		while (counter2 < unselected) {
			VisualNode candidate = null;
			double highestRelevance = -1;
			for (int i=0; i<nodesUnselected.size(); i++) {
				double relevance = nodesUnselected.get(i).getProbNode().getRelevance();
				if (relevance > highestRelevance) {
					highestRelevance = relevance;
					candidate = nodesUnselected.get(i);
				}
			}
			newList.add(selPos, candidate);
			selPos++;
			nodesUnselected.remove(candidate);
			counter2++;
		}
		
		return newList;
	}

	/**
	 * Paints the nodes. The nodes are painted in reverse order of its
	 * position in the array. It means that the selected nodes are
	 * always shown at first plane; and the nodes with higher relevance
	 * are shown ahead of those with lower if they have the same selection
	 * state
	 * 
	 * @param g
	 *            the graphics context in which to paint.
	 */
	private void paintNodes(Graphics2D g) {
		VisualNode visualNode = null;
		visualNodes = reorderVisualNodes();
		for (int i = (visualNodes.size() - 1); i >= 0; i--) {
			visualNode = visualNodes.get(i);
			visualNode.paint(g);
		}
	}

	/**
	 * Paints the links.
	 * 
	 * @param g
	 *            the graphics context in which to paint.
	 */
	private void paintLinks(Graphics2D g) {

		for (VisualLink visualLink : visualLinks) {
			visualLink.paint(g);
		}

	}

	/**
	 * Overwrited 'paint' method to avoid to call it explicitly.
	 * 
	 * @param g
	 *            the graphics context in which to paint.
	 */
	public void paint(Graphics2D g) {
        this.g2 = g ;
		paintLinks(g);
		paintNodes(g);

	}

	/**
	 * Checks if is there a node in a position. You must specify if the node
	 * must be selected or not.
	 * 
	 * @param position
	 *            position to be checked.
	 * @param g
	 *            graphics where the network is painted.
	 * @return if there is a (selected or not) node in the position, returns it,
	 *         else, returns null.
	 */
	public VisualNode whatNodeInPosition(Point2D.Double position, Graphics2D g) {

		VisualNode node = null;
		VisualNode nodeFound = null;
		int index = 0, length = visualNodes.size();

		while ((nodeFound == null) && (index < length)) {
			node = visualNodes.get(index++);
			if (node.pointInsideShape(position, g)) {
				nodeFound = node;
			}
		}

		return nodeFound;

	}
	
	/**
	 * Checks if is there a visual state in a position.
	 * 
	 * @param position
	 *            position to be checked.
	 * @param g
	 *            graphics where the network is painted.
	 * @return if there is a visual state in the position, returns it,
	 *         else, returns null.
	 */
	public VisualState whatStateInPosition(Point2D.Double position, Graphics2D g) {

		VisualState state = null;
		VisualState stateFound = null;
		VisualNode node = null;
		int index = 0;
		int nodesLength = visualNodes.size();
		while ((stateFound == null) && (index < nodesLength)) {
			node = visualNodes.get(index++);
			if (node.pointInsideShape(position, g)) {
				if (node.getInnerBox() instanceof FSVariableBox) {
					int numStates = ((FSVariableBox)node.getInnerBox()).getNumStates();
					for (int i=0; i<numStates; i++) {
						state = ((FSVariableBox)node.getInnerBox()).getVisualState(i);
						if (state.pointInsideShape(position, g)) {
							stateFound = state;
						}
					}
				}
			}
		}
		return stateFound;
	}

	/**
	 * Checks if is there a link in a position. You must specify if the link
	 * must be selected or not.
	 * 
	 * @param position
	 *            position to be checked.
	 * @param g
	 *            graphics where the network is painted.
	 * @return if there is a (selected or not) link in the position, returns it,
	 *         else, returns null.
	 */
	public VisualLink whatLinkInPosition(Point2D.Double position, Graphics2D g) {

		VisualLink link = null;
		VisualLink linkFound = null;
		int index = 0;
		int length = visualLinks.size();

		while ((linkFound == null) && (index < length)) {
			link = visualLinks.get(index++);
			if (link.pointInsideShape(position, g)) {
				linkFound = link;
			}
		}

		return linkFound;

	}

	/**
	 * Checks if is there any selected element in a position.
	 * 
	 * @param position
	 *            position to be checked.
	 * @param g
	 *            graphics where the network is painted.
	 * @return if a selected element is there in the position, returns the
	 *         element, else returns null.
	 */
	public VisualElement whatElementInPosition(Point2D.Double position,
												Graphics2D g) {

		VisualElement elementSelected = null;

		if ((elementSelected = whatNodeInPosition(position, g)) == null) {
			elementSelected = whatLinkInPosition(position, g);
		}

		return elementSelected;

	}

	/**
	 * Sets the selection state of an element.
	 * 
	 * @param element
	 *            element to be selected/deselected.
	 * @param selected
	 *            new selection state.
	 */
	private void setSelectedElement(VisualElement element, boolean selected) {

		if (selected != element.isSelected()) {
			if (element instanceof VisualNode) {
				if (selected) {
					selectedNodes.add((VisualNode) element);
				} else {
					selectedNodes.remove(element);
				}
				changedSelectionStateNode = true;
			} else {
				if (selected) {
					selectedLinks.add((VisualLink) element);
				} else {
					selectedLinks.remove(element);
				}
			}
			notifyObjectsSelected();
			element.setSelected(selected);
		}

	}

	/**
	 * Sets the selection state of a node.
	 * 
	 * @param node
	 *            node to be selected/deselected.
	 * @param selected
	 *            new selection state.
	 */
	public void setSelectedNode(VisualNode node, boolean selected) {

		setSelectedElement(node, selected);

	}

	/**
	 * Sets the selection state of a link.
	 * 
	 * @param link
	 *            link to be selected/deselected.
	 * @param selected
	 *            new selection state.
	 */
	public void setSelectedLink(VisualLink link, boolean selected) {

		setSelectedElement(link, selected);

	}

	/**
	 * Sets the selection state of a node identified by its name.
	 * 
	 * @param name
	 *            name of the node to be selected/deselected.
	 * @param selected
	 *            new selection state.
	 */
	public void setSelectedNode(String name, boolean selected) {

		boolean found = false;
		int i = 0, l = visualNodes.size();

		while (!found && (i < l)) {
			if (visualNodes.get(i).getProbNode().getName().equals(name)) {
				setSelectedElement(visualNodes.get(i), selected);
				found = true;
			} else {
				i++;
			}
		}

	}

	/**
	 * Selects all nodes.
	 * 
	 * @param selected
	 *            new selection state.
	 */
	public void setSelectedAllNodes(boolean selected) {

		for (VisualNode node : visualNodes) {
			setSelectedElement(node, selected);
		}

	}

	/**
	 * Selects all links.
	 * 
	 * @param selected
	 *            new selection state.
	 */
	public void setSelectedAllLinks(boolean selected) {

		for (VisualLink link : visualLinks) {
			setSelectedElement(link, selected);
		}

	}

	/**
	 * Selects all nodes and links.
	 * 
	 * @param selected
	 *            new selection state.
	 */
	public void setSelectedAllObjects(boolean selected) {

		setSelectedAllNodes(selected);
		setSelectedAllLinks(selected);

	}

	/**
	 * Move some nodes an amount in both axis. The parameter 'selected'
	 * indicates if the nodes must be selected or it doesn't mind.
	 * 
	 * @param diffX
	 *            X-axis movement.
	 * @param diffY
	 *            Y-axis movement.
	 * @param selected
	 *            if true, only the selected nodes are moved; if false, all
	 *            nodes are moved.
	 */
	private void moveNodes(double diffX, double diffY, boolean selected) {

		//ProbNode nodeWrapper = null;

		for (VisualNode node : visualNodes) {
			//nodeWrapper = node.getProbNode();
			if (!selected || (node.isSelected())) {
				
				
				/*MoveNodeEdit moveNodeEdit = new MoveNodeEdit(node.getProbNode(), 
						node.getProbNode().getNode().getCoordinateX() + 
						diffX, node.getProbNode().getNode().getCoordinateY() + diffY);
				
				try {
					probNet.getPNESupport().announceEdit(moveNodeEdit);
					probNet.getPNESupport().doEdit(moveNodeEdit);
				} catch (ConstraintViolationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (CanNotDoEditException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (DoEditException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}*/
				
				//nodeWrapper.getNode().coordinateX =+ diffX;
				//nodeWrapper.getNode().coordinateY =+ diffY;
				//node.setTemporalPosition(diffX, diffY);
				node.setTemporalPosition(new Point2D.Double(node.
						getTemporalPosition().getX() + diffX,
						node.getTemporalPosition().getY() + diffY ));
				
				node.paint((Graphics2D) g2);
				
				//constructVisualInfo();
			}
		}

	}

	/**
	 * Move the selected nodes an amount in both axis.
	 * 
	 * @param diffX
	 *            X-axis movement.
	 * @param diffY
	 *            Y-axis movement.
	 */
	public void moveSelectedNodes(double diffX, double diffY) {

		moveNodes(diffX, diffY, true);

	}

	/**
	 * Move all the nodes an amount in both axis.
	 * 
	 * @param diffX
	 *            X-axis movement.
	 * @param diffY
	 *            Y-axis movement.
	 */
	public void moveAllNodes(double diffX, double diffY) {

		moveNodes(diffX, diffY, false);

	}

	/**
	 * Selects the nodes that are inside the selection rectangle and deslects
	 * the ones that are outside.
	 * 
	 * @param selection
	 *            object that manages the selection.
	 */
	public void selectNodesInsideSelection(SelectionRectangle selection) {

		for (VisualNode node : visualNodes) {
			setSelectedElement(node, selection.containsNode(node));
		}

	}

	/**
	 * Fills the array of information of the selected nodes and their actual
	 * state.
	 * 
	 * @return list where are the moved nodes information.
	 */
	public ArrayList<MovedNodeInfo> fillActualNodesMovedInfo() {

		ArrayList<MovedNodeInfo> movedNodes = new ArrayList<MovedNodeInfo>();

		for (VisualNode node : visualNodes) {
			if (node.isSelected()) {
				movedNodes.add(new MovedNodeInfo(node.getProbNode(), node
					.getPosition()));
			}
		}

		return movedNodes;

	}

	/**
	 * Fills the array of information of the selected nodes and the differences
	 * of their state.
	 * 
	 * @param movedNodes
	 *            list where is saved the moved nodes information.
	 */
	public void fillDifferencesNodesMovedInfo(
												ArrayList<MovedNodeInfo> movedNodes) {

		ProbNode probNodeAux = null;

		for (MovedNodeInfo movedNode : movedNodes) {
			probNodeAux = movedNode.getProbNode();
			movedNode.setDiffPosition(new Point2D.Double(
					probNodeAux.getNode().getCoordinateX()
				- movedNode.getDiffPosition().getX(), 
				probNodeAux.getNode().getCoordinateY() 
				- movedNode.getDiffPosition().getY()));
		}

	}
	
	/**
	 * Fills the array of information of the selected nodes and their actual
	 * state.
	 * 
	 * @return list where are the moved nodes information.
	 */
	public ArrayList<VisualNode> fillVisualNodesSelected() {

		ArrayList<VisualNode> movedNodes = new ArrayList<VisualNode>();

		for (VisualNode node : visualNodes) {
			if (node.isSelected()) {
				movedNodes.add(node);
			}
		}

		return movedNodes;

	}
	
	/**
	 * This method returns a list containing all the nodes in the network.
	 * 
	 * @return a list containing all the nodes in the network.
	 */
	public ArrayList<VisualNode> getAllNodes() {

		return visualNodes;

	}

	/**
	 * This method returns a list that contains all the links that leave of or
	 * arrive in one node of the list of nodes passed as parameter.
	 * 
	 * @param nodes
	 *            list of nodes whose links are returned.
	 * @return a list of links related to the nodes.
	 */
	public ArrayList<VisualLink> getLinksOfNodes(ArrayList<VisualNode> nodes) {

		ArrayList<VisualLink> links = new ArrayList<VisualLink>();
		int i, l = nodes.size();
		boolean found = false;

		for (VisualLink visualLink : visualLinks) {
			found = false;
			i = 0;
			while (!found && (i < l)) {
				if (visualLink.getSourceNode().equals(nodes.get(i))
					|| visualLink.getDestinationNode().equals(nodes.get(i))) {
					links.add(visualLink);
					found = true;
				} else {
					i++;
				}
			}
		}

		return links;

	}

	/**
	 * Sets a new selection listener.
	 * 
	 * @param listener
	 *            listener to be set.
	 */
	public void addSelectionListener(SelectionListener listener) {

		selectionListeners.add(listener);

	}

	/**
	 * This method returns a list containing the selected nodes.
	 * 
	 * @return a list containing the selected nodes.
	 */
	public ArrayList<VisualNode> getSelectedNodes() {

		return new ArrayList<VisualNode>(selectedNodes);

	}

	/**
	 * This method returns a list containing the selected links.
	 * 
	 * @return a list containing the selected links.
	 */
	public ArrayList<VisualLink> getSelectedLinks() {

		return new ArrayList<VisualLink>(selectedLinks);

	}

	/**
	 * Returns the number of selected nodes.
	 * 
	 * @return number of selected nodes.
	 */
	public int getSelectedNodesNumber() {

		return selectedNodes.size();

	}

	/**
	 * Returns the number of selected links.
	 * 
	 * @return number of selected links.
	 */
	public int getSelectedLinksNumber() {

		return selectedLinks.size();

	}

	/**
	 * Notifies to the registered selection listener how many nodes and links
	 * are selected, and which are the especific selected nodes. 
	 * Also notifies this situation to the menu assistant.
	 */
	private void notifyObjectsSelected() {

		for (SelectionListener listener : selectionListeners) {
			listener.objectsSelected(
				getSelectedNodesNumber(), getSelectedLinksNumber(), getSelectedNodes());
		}
	}

	/**
	 * Returns the network that is painted by this object.
	 * 
	 * @return network which is painted.
	 */
	public ProbNet getNetwork() {

		return probNet;

	}
	public PNESupport getpNESupport() {
//review method
		return probNet.getPNESupport();

	}

	
	public void undoableEditHappened(UndoableEditEvent e) {
		
		
		
		UndoableEdit edit=e.getEdit();
		Object p=e.getSource();
		ProbNet p2=(ProbNet)p;
		//if (edit instanceof AddVariableEdit){
		/*if (edit instanceof AddProbNodeEdit){
			
			
			String name=((AddVariableEdit)edit).getVariable().getName();
			ProbNode newProbNode;
			try {
				newProbNode = pNESupport.getProbNet().getProbNode(name);
				nodeWrapper =
				createNewNonamedNode(newProbNode, cursorPosition);
			} catch (ProbNodeNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				//JOptionPane.showMessageDialog(
					//	Util.getOwner(this), e2.getMessage(), stringResource
						//	.getString("ErrorWindow.Title.Label"),
						//JOptionPane.ERROR_MESSAGE);
					}
			
			}else if (edit instanceof AddLinkEdit){
			
				}*/
	
			constructVisualInfo();
		
	}

	private void constructVisualInfo(UndoableEdit edit) {
		
		
		//UndoableEdit edit=e.getEdit();
		//Object p=e.getSource();
		//ProbNet p2=(ProbNet)p;
		//if (edit instanceof AddVariableEdit){
				
		if (edit instanceof AddProbNodeEdit ){
			VisualNode node1 = createVisualNode(
					((AddProbNodeEdit)edit).getProbNode());
			visualNodes.add(node1);
			node1.setByTitle(byTitle);
		}else if (edit instanceof AddLinkEdit){
			//ProbNet probNet =((AddLinkEdit) edit).getProbNet();
			
			ProbNode probNodeSource =probNet.getProbNode(
					((AddLinkEdit) edit).getVariable1());
			ProbNode probNodeDestination =probNet.getProbNode(
					((AddLinkEdit) edit).getVariable2());
			
			VisualNode node1 = null;
			VisualNode node2 = null;
			int i = 0;
			int l = -1;
			l = visualNodes.size();
			while ((i < l) && ((node1 == null) || (node2 == null))) {
				if (node1 == null) {
					/*if (linkWrapper.getNode1().equals(
						visualNodes.get(i).getNodeWrapper())) {
						node1 = visualNodes.get(i);
					}*/
					if (probNodeSource.equals(
							visualNodes.get(i).getProbNode())) {
							node1 = visualNodes.get(i);
					}
				}
				if (node2 == null) {
					if (probNodeDestination.equals(
						visualNodes.get(i).getProbNode())) {
						node2 = visualNodes.get(i);
					}
				}
				i++;
			}
							
			if ((node1 != null) && (node2 != null)) {
				Link lk= probNet.getGraph().getLink(probNodeSource.getNode(), probNodeDestination.getNode(), true);
				visualLinks.add(new VisualLink(lk, node1, node2));
			}
					
		}else if (edit instanceof CRemoveLinkEdit){
			ArrayList<Link> links = null;
			links = probNet.getGraph().getLinks();
			Link linkToCheck = null;
			ArrayList<VisualLink> linksToDelete = new ArrayList<VisualLink>();
			
			for (VisualLink vLink : visualLinks) {
				linkToCheck = vLink.getLink();
				if (links.contains(linkToCheck)) {
					links.remove(linkToCheck);
				} else {
					linksToDelete.add(vLink);
				}
			}
			visualLinks.removeAll(linksToDelete);
		}
		
		
		/*ArrayList<NodeWrapper> nodes = getNodewrappers(network.getProbNodes());
		ArrayList<VisualNode> nodesToDelete = new ArrayList<VisualNode>();
		ArrayList<VisualLink> linksToDelete = new ArrayList<VisualLink>();
		ArrayList<LinkWrapper> links = null;
		NodeWrapper nodeToCheck = null;
		LinkWrapper linkToCheck = null;
		VisualNode node1 = null;
		VisualNode node2 = null;
		int i = -1;
		int l = -1;

		//nodes = network.getNodes();
		for (VisualNode vNode : visualNodes) {
			nodeToCheck = vNode.getNodeWrapper();
			if (nodes.contains(nodeToCheck)) {
				nodes.remove(nodeToCheck);
			} else {
				nodesToDelete.add(vNode);
			}
		}
		visualNodes.removeAll(nodesToDelete);
		for (NodeWrapper node : nodes) {
			node1 = VisualNodeFactory.createVisualNode(node);
			visualNodes.add(node1);
			node1.setByTitle(byTitle);
			
		}
		links = network.getLinks();
		for (VisualLink vLink : visualLinks) {
			linkToCheck = vLink.getLink();
			if (links.contains(linkToCheck)) {
				links.remove(linkToCheck);
			} else {
				linksToDelete.add(vLink);
			}
		}
		visualLinks.removeAll(linksToDelete);
		l = visualNodes.size();
		for (LinkWrapper link : links) {
			i = 0;
			node1 = null;
			node2 = null;
			while ((i < l) && ((node1 == null) || (node2 == null))) {
				if (node1 == null) {
					if (link.getNode1().equals(
						visualNodes.get(i).getNodeWrapper())) {
						node1 = visualNodes.get(i);
					}
				}
				if (node2 == null) {
					if (link.getNode2().equals(
						visualNodes.get(i).getNodeWrapper())) {
						node2 = visualNodes.get(i);
					}
				}
				i++;
			}
			if ((node1 != null) && (node2 != null)) {
				visualLinks.add(new VisualLink(link, node1, node2));
			}
		}*/

	}

	

	
	public void undoableEditWillHappen(PNUndoableEditEvent event)
			throws ConstraintViolationException, CanNotDoEditException {
		// TODO Auto-generated method stub
		
	}

	/*public void setLinkWrapper(ProbNode sourceNode,
		ProbNode destinationNode) {
		//LinkWrapper link = null;
		NodeType sourceNodeType = null;
		NodeType destinationNodeType = null;
		ProbNode sourceProbNode = null;
		ProbNode destinationProbNode = null;
		ArrayList<ProbNode> probNodes = probNet.getProbNodes();

		sourceNodeType = sourceNode.getNodeType();
		destinationNodeType = destinationNode.getNodeType();
		sourceProbNode = sourceNode;
		destinationProbNode = destinationNode;
		//nodes exists
		if (!probNodes.contains(sourceProbNode)
			|| !probNodes.contains(destinationProbNode)) {
			//throw new Exception(stringResource
				//.getString("LinkedNodesNotExist.Text.Label"));
			//nodes different
		} else if (sourceProbNode.equals(destinationProbNode)) {
			//throw new Exception(stringResource
				//.getString("LinkNotAllowed.Text.Label"));
			//node utility partner
		} else if ((sourceNodeType == NodeType.UTILITY)
			&& (destinationNodeType != NodeType.UTILITY)) {
			//throw new Exception(stringResource
				//.getString("LinkNotAllowed.Text.Label"));
		}
		//link don´t exists
		if (probNet.getGraph().getLink(
			sourceProbNode.getNode(), destinationProbNode.getNode(), true) != null) {
			//throw new Exception(stringResource.getString(
				//"LinkExists.Text.Label", sourceNode.getName(), destinationNode
					//.getName()));
		}
		//inverse link exists
		if (probNet.getGraph().getLink(
			destinationProbNode.getNode(), sourceProbNode.getNode(), true) != null) {
			//throw new Exception(stringResource.getString(
				//"InverseLinkExists.Text.Label", sourceNode.getName(),
				//destinationNode.getName()));
		}
		
		/*try {
			this.getGraph().addLink(
				sourceProbNode.getNode(), destinationProbNode.getNode(), true);
		} catch (Exception e) {
			throw new Exception(stringResource.getString(
				"LinkMakesCycle.Text.Label", sourceNode.getName(),
				destinationNode.getName()));
		}*/
		/*linkWrapper =
			new LinkWrapper(probNet.getGraph().getLink(
				sourceProbNode.getNode(), destinationProbNode.getNode(), true),
				sourceNode, destinationNode);
	
	}*/
	
		
	/**
	 * Returns different types of visual nodes according to the supplied node.
	 * 
	 * @param node
	 *            node whose visual representation is going to be returned.
	 * @return the visual representation of the node.
	 */
	private VisualNode createVisualNode(ProbNode node) {

		switch (node.getNodeType()) {
			case CHANCE: {
				return new VisualChanceNode(node, editorPanel);
			}
			case DECISION: {
				VisualDecisionNode vdn= new VisualDecisionNode(node, editorPanel);
			/*	if ( node.getPolicyType() == PolicyType.PROBABILISTIC )
					vdn.setSelected(true);*/
				return  vdn;
			}
			case UTILITY: {
				return new VisualUtilityNode(node, editorPanel);
			}
			default: {
				return null;
			}
		}

	}

	
	public void undoEditHappened(PNUndoableEditEvent event) {
		UndoableEdit edit=event.getEdit();
		//Object p=event.getSource();
		//ProbNet p2=(ProbNet)p;
		//if (edit instanceof AddVariableEdit){
		/*if (edit instanceof AddProbNodeEdit){
			
			
			String name=((AddVariableEdit)edit).getVariable().getName();
			ProbNode newProbNode;
			try {
				newProbNode = pNESupport.getProbNet().getProbNode(name);
				nodeWrapper =
				createNewNonamedNode(newProbNode, cursorPosition);
			} catch (ProbNodeNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				//JOptionPane.showMessageDialog(
					//	Util.getOwner(this), e2.getMessage(), stringResource
						//	.getString("ErrorWindow.Title.Label"),
						//JOptionPane.ERROR_MESSAGE);
					}
			
			}else if (edit instanceof AddLinkEdit){
			
				}*/
	
			constructVisualInfo();
		
	}
	
}
