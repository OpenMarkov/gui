package openmarkov.core.gui.network;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import openmarkov.core.gui.utils.Util;

import org.openmarkov.core.exception.NodeWrapperNumberFormatException;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.Potential;
import openmarkov.core.gui.network.PropertyNames.nodePropertyNames;


/**
 * This class is a wrapper of the class openmarkov.networks.ProbNode. It is used to
 * retrieve the adittionalProperties trough methods instead of making it directly in the
 * adittionalProperties object.
 * 
 * @author jmendoza
 * @version 1.0 jmendoza
 * @version 1.1 jlgozalo toSring() added, getRelevance() modified to handle
 *          String->Double getStates() added getXXXComment() added (for Node
 *          definition, values and probsTable)
 * @version 1.2 jlgozalo Potentials, Precision and OtherProperties added
 */
public class NodeWrapper {

	/**
	 * Network that the node belongs to.
	 */
	private ProbNet network = null;

	/**
	 * Node whose wrapper is this object.
	 */
	private ProbNode probNode = null;

	/**
	 * Potentials associated with this node
	 */
	private ArrayList<Potential> listPotentials = null;

	/**
	 * Properties to be managed explicitly
	 */
	private final String[] otherPropertiesKeys =
		new String[] { "CoordinateYD", "CoordinateXD", "CoordinateY",
			"CoordinateX", "DefinitionComment", "DiscreteValuesComment",
			"DiscretizeValuesComment", "ProbsTableComment", "UseDefaultStates",
			"Purpose", "Relevance" };

	/**
	 * Constructor. Only saves the node.
	 * 
	 * @param newNetwork
	 *            network that the node belongs to.
	 * @param newProbNode
	 *            node whose wrapper is going to be created.
	 */
	public NodeWrapper(ProbNet newNetwork, ProbNode newProbNode) {

		network = newNetwork;
		probNode = newProbNode;
		listPotentials = probNode.getPotentials();
		if (getNodeType() == NodeType.UTILITY) {
			setStates( GUIDefaultStates.getStatesNodeType( NodeType.UTILITY, null ) );
		}

	}

	/**
	 * Returns the node associated with this wrapper.
	 * 
	 * @return the original node.
	 */
	public ProbNode getProbNode() {

		return probNode;
	}

	/**
	 * Indicates whether some other object is "equal to" this one. Two instances
	 * are equal if the variables 'probNode' are equals.
	 * 
	 * @param obj
	 *            object to compare to. It must be a NodeWrapper instance.
	 */
	@Override
	public boolean equals(Object obj) throws IllegalArgumentException {

		if (obj instanceof NodeWrapper) {
			return getName().equals( ((NodeWrapper) obj).getName() );
		}
		return false;
	}

	/**
	 * Sets all the adittionalProperties of the node except the type of node and the type
	 * of variable.
	 * 
	 * @param additionalProperties
	 *            new adittionalProperties of the node.
	 * @throws Exception
	 *             if an error occurred.
	 */
	public void setProperties(NodeProperties properties) throws Exception {

		setName( properties.getName() );
		setTitle( properties.getTitle() );
		setX( properties.getX() );
		setY( properties.getY() );
		setRelevance( properties.getRelevance() );
		setPurpose( properties.getPurpose() );
		// setComment(adittionalProperties.getComment());//jlgozalo. add 19/10/09
		setDefinitionComment( properties.getDefinitionComment() ); // jlgozalo.
		// add
		// 19/10/09
		setDiscreteValuesComment( properties.getDiscreteValuesComment() );// jlgozalo.
		// add
		// 19/10/09
		setDiscretizeValuesComment( properties.getDiscretizeValuesComment() );// jlgozalo.
		// add
		// 19/10/09
		setProbsTableComment( properties.getProbsTableComment() );// jlgozalo.
		// add
		// 19/10/09
		setStates( properties.getStates() );
		//setParents( adittionalProperties.getParents() );
		setVariableType( properties.getVariableType() );
		setPartitionedInterval( properties.getPartitionedInterval() ); // jlgozalo.
		// add
		// 12/10/09
		setPrecision( properties.getPrecision() ); // jlgozalo. add 04/02/2010
		setListPotentials( properties.getListPotentials() ); // jlgozalo. add
		// 02/02/2010
		setOtherProperties( properties.getOtherProperties() ); // jlgozalo. add
		// 13/02/2010
	}

	/**
	 * This method returns a fixed set of adittionalProperties of the node.
	 * 
	 * @return various adittionalProperties of the node.
	 */
	public NodeProperties getProperties() {

		NodeProperties properties = new NodeProperties();

		properties.setName( getName() );
		properties.setTitle( getTitle() );
		properties.setX( getX() );
		properties.setY( getY() );
		properties.setRelevance( getRelevance() );
		properties.setPurpose( getPurpose() );
		// adittionalProperties.setComment(getComment());//jlgozalo. add 19/10/09
		properties.setDefinitionComment( getDefinitionComment() );// jlgozalo.
		// add
		// 19/10/09
		//adittionalProperties.setDiscreteValuesComment( getDiscreteValuesComment() );// jlgozalo.
		// add
		// 19/10/09
		//adittionalProperties.setDiscretizeValuesComment( getDiscretizeValuesComment() );// jlgozalo.
		// add
		// 19/10/09
		//adittionalProperties.setProbsTableComment( getProbsTableComment() );// jlgozalo.
		// add
		// 19/10/09
		properties.setNodeType( getNodeType() );
		properties.setVariableType( getVariableType() );
		properties.setParents( getParents() );
		properties.setPossibleParents( getPossibleParents() );
		properties.setNetwork( getNetwork() );
		properties.setStates( getStates() ); // jlgozalo. add 15/08/09
		//properties.setPartitionedInterval( getPartitionedInterval() ); // jlgozalo.
		// add
		// 12/10/09
		properties.setPrecision( getPrecision() ); // jlgozalo. add 04/02/2010
		properties.setListPotentials( getListPotentials() ); // jlgozalo. add
		// 02/02/2010
		properties.setOtherProperties( getOtherProperties() ); // jlgozalo. add
		// 13/02/2010
		return properties;
	}

	/**
	 * Returns the X position of the node.
	 * 
	 * @return X position of the node.
	 */
	public Double getX() {

		/*
		 * In order to avoid the lost of precision on the screen using integer
		 * coordinates, the coordinates are trated as double values internally
		 * and the adittionalProperties that are read from Writer class are assigned as
		 * integers.
		 */
		Double doubleInfo = 
			Double.parseDouble(probNode.additionalProperties.get("CoordinateXD"));
		Integer integerInfo = 
			Integer.parseInt(probNode.additionalProperties.get("CoordinateX"));

		if (integerInfo == null) {
			doubleInfo = 0.0;
			setX( doubleInfo );
		} else if (doubleInfo == null) {
			doubleInfo = integerInfo.doubleValue();
			setX( doubleInfo );
		} else if (Math.abs( integerInfo.doubleValue()
			- doubleInfo.doubleValue() ) > 1) {
			doubleInfo = integerInfo.doubleValue();
			setX( doubleInfo );
		}
		return doubleInfo;
	}

	/**
	 * Sets the X position of the node.
	 * 
	 * @param info
	 *            new X position.
	 */
	@SuppressWarnings("unchecked")
	public void setX(Double info) {

		probNode.additionalProperties.put("CoordinateXD", info.toString());
		probNode.additionalProperties.put("CoordinateX", 
				Integer.toString(info.intValue()));
	}

	/**
	 * Returns the Y position of the node in the screen.
	 * 
	 * @return Y position of the node in the screen.
	 */
	public Double getY() {

		/*
		 * In order to avoid the lost of precision on the screen using integer
		 * coordinates, the coordinates are trated as double values internally
		 * and the adittionalProperties that are read from Writer class are assigned as
		 * integers.
		 */
		Double doubleInfo = 
			Double.parseDouble(probNode.additionalProperties.get("CoordinateYD"));
		Integer integerInfo = 
			Integer.parseInt(probNode.additionalProperties.get("CoordinateY"));

		if (integerInfo == null) {
			doubleInfo = 0.0;
			setY( doubleInfo );
		} else if (doubleInfo == null) {
			doubleInfo = integerInfo.doubleValue();
			setY( doubleInfo );
		} else if (Math.abs( integerInfo.doubleValue()
			- doubleInfo.doubleValue() ) > 1) {
			doubleInfo = integerInfo.doubleValue();
			setY( doubleInfo );
		}
		return doubleInfo;
	}

	/**
	 * Sets the Y position of the node.
	 * 
	 * @param info
	 *            new Y position.
	 */
	@SuppressWarnings("unchecked")
	public void setY(Double info) {

		probNode.additionalProperties.put("CoordinateYD", info.toString() );
		probNode.additionalProperties.put("CoordinateY", 
				Integer.toString(info.intValue()));
	}

	/**
	 * Returns the name of the node.
	 * 
	 * @return name of the node.
	 */
	public String getName() {

		return probNode.getName();
	}

	/**
	 * Sets the name of the variable.
	 * 
	 * @param info
	 *            new name.
	 */
	@SuppressWarnings("unchecked")
	public void setName(String info) {

		String newInfo = info;

		if (newInfo == null) {
			newInfo = "";
		}
		probNode.getVariable().setName( newInfo );

		// probNode.getVariable().setName(newInfo);
		// probNode.getInfoNode().setProperty("Name", newInfo);
	}

	/**
	 * Returns the title of the node.
	 * 
	 * @return title of the node.
	 */
	public String getTitle() {

		String info = (String) probNode.additionalProperties.get( "Title" );

		if (info == null) {
			info = "";
			setTitle( info );
		}
		return info;
	}

	/**
	 * Sets the title of the node.
	 * 
	 * @param info
	 *            new title.
	 */
	@SuppressWarnings("unchecked")
	public void setTitle(String info) {

		probNode.additionalProperties.put( "Title", info );
	}

	/**
	 * Returns the relevance of the node read from the network file or set a
	 * default value
	 * 
	 * @return relevance of the node.
	 */
	public Double getRelevance() {

		Double value = Double.parseDouble(probNode.additionalProperties.get( 
				nodePropertyNames.RELEVANCE));
		if (value == null) {
			value = 5.0;
		}
		setRelevance( value );
		return value;
	}

	/**
	 * Sets the relevance of the node.
	 * 
	 * @param info
	 *            new relevance.
	 */
	@SuppressWarnings("unchecked")
	public void setRelevance(Double info) {

		probNode.additionalProperties.put(
				nodePropertyNames.RELEVANCE.toString(), info.toString());
	}

	/**
	 * Returns the purpose of the node.
	 * 
	 * @return purpose of the node.
	 */
	public String getPurpose() {

		String info = (String) probNode.additionalProperties.get(
				nodePropertyNames.PURPOSE.toString());

		if (info == null) {
			info = "";
			setPurpose( info );
		}
		return info;
	}

	/**
	 * Sets the purpose of the node.
	 * 
	 * @param info
	 *            new purpose.
	 */
	@SuppressWarnings("unchecked")
	public void setPurpose(String info) {

		probNode.additionalProperties.put( "Purpose", info );
	}

	/**
	 * Returns the comment of the node This method is maintained just for
	 * compatibility mode with initial OpenMarkov files
	 * 
	 * @return comment of the node
	 */
	public String getComment() {

		String info = (String) probNode.additionalProperties.get( "Comment" );

		if (info == null) {
			info = "";
			setComment( info );
		}
		return info;
	}

	/**
	 * Sets the comment of the node This method is maintained just for
	 * compatibility mode with initial OpenMarkov files
	 * 
	 * @param info
	 *            new comment
	 */
	@SuppressWarnings("unchecked")
	public void setComment(String info) {

		probNode.additionalProperties.put( "Comment", info );
	}

	/**
	 * Returns the comment of the definition of the node.
	 * 
	 * @return comment of the definition of the node.
	 */
	public String getDefinitionComment() {

		String info = (String) probNode.additionalProperties.get(
				nodePropertyNames.COMMENT.toString() );
		if (info == null) {
			info = "";
			setDefinitionComment( info );
		}
		return info;
	}

	/**
	 * Sets the comment of definition of the node
	 * 
	 * @param info
	 *            new comment for definition of the node
	 */
	@SuppressWarnings("unchecked")
	public void setDefinitionComment(String info) {

		probNode.additionalProperties.put( "DefinitionComment", info );
	}

	/**
	 * Returns the comment of the discrete values of the node.
	 * 
	 * @return comment of the discrete values of the node.
	 */
	public String getDiscreteValuesComment() {

		String info =
			(String) probNode.additionalProperties.get( "DiscreteValuesComment" );
		if (info == null) {
			info = "";
			setDiscreteValuesComment( info );
		}
		return info;
	}

	/**
	 * Sets the comment of discrete values of the node
	 * 
	 * @param info
	 *            new comment for discrete values of the node
	 */
	@SuppressWarnings("unchecked")
	public void setDiscreteValuesComment(String info) {

		probNode.additionalProperties.put( "DiscreteValuesComment", info );
	}

	/**
	 * Returns the comment of the discretize values of the node.
	 * 
	 * @return comment of the discretize values of the node.
	 */
	public String getDiscretizeValuesComment() {

		String info =
			(String) probNode.additionalProperties.get( "DiscretizeValuesComment" );
		if (info == null) {
			info = "";
			setDiscretizeValuesComment( info );
		}
		return info;
	}

	/**
	 * Sets the comment of discretize values of the node
	 * 
	 * @param info
	 *            new comment for discretize values of the node
	 */
	@SuppressWarnings("unchecked")
	public void setDiscretizeValuesComment(String info) {

		probNode.additionalProperties.put( "DiscretizeValuesComment", info );
	}

	/**
	 * Returns the comment of the probability tables of the node.
	 * 
	 * @return comment of the probability tables of the node.
	 */
	public String getProbsTableComment() {

		String info = (String) probNode.additionalProperties.get( "ProbsTableComment" );
		if (info == null) {
			info = "";
			setProbsTableComment( info );
		}
		return info;
	}

	/**
	 * Sets the comment of probability tables of the node
	 * 
	 * @param info
	 *            new comment for the probability tables of the node
	 */
	@SuppressWarnings("unchecked")
	public void setProbsTableComment(String info) {

		probNode.additionalProperties.put( "ProbsTableComment", info );
	}

	/**
	 * Returns the type of the node.
	 * 
	 * @return type of the node.
	 */
	public NodeType getNodeType() {

		return probNode.getNodeType();
	}

	/**
	 * Sets the type of variable which the node represents.
	 * 
	 * @param variableType
	 *            the type of the variable (DISCRETE, DISCRETIZED or (in future)
	 *            CONTINUOUS
	 */
	public void setVariableType(VariableType variableType) {

		probNode.getVariable().setVariableType( variableType );
	}

	/**
	 * Returns the type of the variable which the node represents.
	 * 
	 * @return type of the variable.
	 */
	public VariableType getVariableType() {

		Variable variable = probNode.getVariable();

		return variable.getVariableType();
	}

	/**
	 * Sets the states of the node.
	 * 
	 * @param info
	 *            new states.
	 */
	@SuppressWarnings("unchecked")
	public void setStates(State[] info) {

		State[] newInfo = info;

		if (newInfo == null) {
			newInfo = new State[ 0 ];
		}
		probNode.getVariable().setStates( newInfo );
		probNode.additionalProperties.put("UseDefaultStates",Boolean.FALSE.toString());
	}

	/**
	 * Returns the states of this node
	 * 
	 * @return the array of states
	 */
	public State[] getStates() {

		State[] states = probNode.getVariable().getStates();
		if (states == null) {
			setStates( states );
		}

		return states;
	}

	/**
	 * Returns the network that this node belongs to.
	 * 
	 * @return the network that this node belongs to.
	 */
	public ProbNet getNetwork() {

		return network;
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

	/**
	 * This method sets the new parents of the node. The nodes that doesn't
	 * exist in the same network of this node are ignored. First it removes the
	 * links that leave from the actual parents that aren't in the arraylist.
	 * Then it adds a links for each node that isn't parent of this node and
	 * appears in the arraylist.
	 * 
	 * @param newParents
	 *            new parents of the node.
	 * @throws Exception
	 *             if an error occurred.
	 */
	/*changed by mpalacios
	 * public void setParents(ArrayList<NodeWrapper> newParents) throws Exception {

		ArrayList<NodeWrapper> actualParents;

		if (newParents != null) {
			actualParents = getParents();
			for (NodeWrapper node : actualParents) {
				if (!newParents.contains( node )) {
					network.backupProbNet.removeLink( node, this );
				}
			}
			actualParents = getParents();
			for (NodeWrapper node : newParents) {
				if (!actualParents.contains( node )) {
					try {
						network.backupProbNet.addLink( node, this );
					} catch (Exception e) {
						System.err.println( e.getMessage() );
					}
				}
			}
		}
	}*/

	/**
	 * Returns the parents of the node as a list of NodeWrapper objects.
	 * 
	 * @return a more verbose list of the parents of the node.
	 */
	public ArrayList<NodeWrapper> getParents() {

		ArrayList<NodeWrapper> result = new ArrayList<NodeWrapper>();

		for (Node node : probNode.getNode().getParents()) {
			result
				.add( new NodeWrapper( network, (ProbNode) node.getObject() ) );
		}
		return result;
	}

	/**
	 * Returns a list that contains the nodes of the network that can be parent
	 * of the node passed as parameter. This list contains the nodes that
	 * actually are parents of this node. Depending of the type of the node,
	 * some nodes cannot be parents of it: the CHANCE and DECISION nodes can
	 * have parents that has the type CHANCE or DECISION but the UTILITY nodes
	 * can have parents of any type.
	 * 
	 * @return the possible parents of the node including its actual parents.
	 */
	public ArrayList<NodeWrapper> getPossibleParents() {

		ProbNet probNet = probNode.getProbNet();
		ArrayList<Node> allNodes = probNet.getGraph().getNodes();
		ArrayList<NodeWrapper> possibleParents = new ArrayList<NodeWrapper>();
		HashSet<NodeType> parentsType = new HashSet<NodeType>();
		HashSet<ProbNode> descendants;

		descendants = getDescendants();
		allNodes.removeAll( descendants );
		parentsType.add( NodeType.CHANCE );
		parentsType.add( NodeType.DECISION );
		if (probNode.getNodeType() == NodeType.UTILITY) {
			parentsType.add( NodeType.UTILITY );
		}
		for (Node possibleParent : allNodes) {
			if (parentsType.contains( ((ProbNode) possibleParent.getObject())
				.getNodeType() )) {
				possibleParents.add( new NodeWrapper( network,
					(ProbNode) possibleParent.getObject() ) );
			}
		}
		return possibleParents;
	}

	/**
	 * This method returns this node and its descendants.
	 * 
	 * @return this node and all its descendants.
	 */
	private HashSet<ProbNode> getDescendants() {

		HashSet<ProbNode> descendants = new HashSet<ProbNode>();

		fillDescendants( probNode, descendants );
		return descendants;
	}

	/**
	 * This method add to the hashset the node passed as parameter and its
	 * descendants. The hashset must be initialized.
	 * 
	 * @param probNode
	 *            node whose descendant are returned.
	 * @param descendants
	 *            descendants of the node.
	 */
	private void fillDescendants(ProbNode probNode,
									HashSet<ProbNode> descendants) {

		if (descendants != null) {
			descendants.add( probNode );
			for (Node child : probNode.getNode().getChildren()) {
				fillDescendants( (ProbNode) child.getObject(), descendants );
			}
		}
	}

	/**
	 * Returns the links of the node. Each one is specified by a reference to
	 * LinkProperties.
	 * 
	 * @return the links of a node.
	 */
	/*public ArrayList<LinkWrapper> getLinks() {

		ArrayList<LinkWrapper> links = network.backupProbNet.getLinks();
		ArrayList<LinkWrapper> nodeLinks = new ArrayList<LinkWrapper>();

		for (LinkWrapper link : links) {
			if (link.getNode1().equals( this ) || link.getNode2().equals( this )) {
				nodeLinks.add( link );
			}
		}
		return nodeLinks;
	}*/

	/**
	 * Returns the partitioned interval of the variable of the node.
	 * 
	 * @return partitioned interval of Variable in the node.
	 */
	/*public PartitionedInterval getPartitionedInterval() {

		PartitionedInterval partitionedInterval =
			(PartitionedInterval) probNode.getVariable()
				.getPartitionedInterval();

		if (partitionedInterval == null) {
			if (Util.hasLimitBracketSymbols( this.getStates() )) {
				// do a PartitionedInterval from old Elvira States format
				partitionedInterval =
					convertStatesToPartitionedInterval( this.getStates() );
				setPartitionedInterval( partitionedInterval );
			} else {
				partitionedInterval =
					new PartitionedInterval( true, Double.NEGATIVE_INFINITY,
						Double.POSITIVE_INFINITY, true );
				setPartitionedInterval( partitionedInterval );
			}
		}
		return partitionedInterval;
	}
*/
	/*protected PartitionedInterval convertStatesToPartitionedInterval(
			State[] states) {

		PartitionedInterval partitionedInterval = null;

		// convert Strings to TableFormat
		Object[][] data;
		int i = 0;
		int numIntervals = 0;
		int numColumns = 6; // name-symbol-value-separator-value-symbol
		String aString = "";
		String lowSymbol = "";
		String upperSymbol = "";
		double lowValue;
		double upperValue;
		int index = 0;
		String name = "";

		numIntervals = states.length;
		data = new Object[ numIntervals ][ numColumns ];
		for (i = 0; i < numIntervals; i++) {
			aString = states[i].toString();
			// find name & lowSymbol
			index = aString.indexOf( "[" );
			if (index < 0)
				index = aString.indexOf( "(" );
			name = aString.substring( 0, index );
			data[i][0] = name;
			aString = aString.substring( index, aString.length() );
			lowSymbol = aString.substring( 0, 1 );
			data[i][1] = lowSymbol;
			// find lowValue
			aString = aString.substring( 1, aString.length() );
			index = aString.indexOf( "," );
			lowValue = Double.valueOf( aString.substring( 0, index ) );
			data[i][2] = lowValue;
			// find separator
			aString = aString.substring( index, aString.length() );
			data[i][3] = aString.substring( 0, 1 );
			// find upperValue
			aString = aString.substring( 1, aString.length() );
			index = aString.indexOf( "]" );
			if (index < 0)
				index = aString.indexOf( ")" );
			upperValue = Double.valueOf( aString.substring( 0, index ) );
			data[i][4] = upperValue;
			// find upperSymbol
			aString = aString.substring( index, aString.length() );
			upperSymbol = aString.substring( 0, 1 );
			data[i][5] = upperSymbol;
		}
		// Convert data to PartitionedInterval
		int numSubIntervals = 0;
		double limits[] = null;
		boolean belongsToLeftSide[] = null;
		numSubIntervals = data.length;
		limits = new double[ numSubIntervals + 1 ];
		belongsToLeftSide = new boolean[ numSubIntervals + 1 ];
		if (numSubIntervals > 1) {
			try {// id-name-symbol-value-separator-value-symbol
				for (i = 0; i < numSubIntervals; i++) {
					belongsToLeftSide[i] = (data[i][1] == "[" ? true : false);
					limits[i] = ((Double) data[i][2]).doubleValue();
				}
				limits[i] = ((Double) data[i - 1][4]).doubleValue();
				belongsToLeftSide[i] = (data[i - 1][5] == "]" ? true : false);

			} catch (NumberFormatException ex) {
				ExceptionsHandler.handleException( new NodeWrapperNumberFormatException(i),"" , false );
			}
			partitionedInterval =
				new PartitionedInterval( limits, belongsToLeftSide );
		} else {
			boolean leftClosed = (data[0][1] == "[" ? true : false);
			boolean rightClosed = (data[0][5] == "]" ? true : false);
			double min = ((Double) data[0][2]).doubleValue();
			double max = ((Double) data[0][4]).doubleValue();
			partitionedInterval =
				new PartitionedInterval( leftClosed, min, max, rightClosed );
		}

		return partitionedInterval;
	}*/

	/**
	 * Sets the partitioned Interval of Variable in the node.
	 * 
	 * @param partitionedInterval
	 *            the new Partitioned Interval for Variable in ProbNode
	 */
	@SuppressWarnings("unchecked")
	public void setPartitionedInterval(PartitionedInterval partitionedInterval) {

		probNode.getVariable().setPartitionedInterval( partitionedInterval );
	}

	/**
	 * Sets the precision for the partitioned Interval of Variable in the node.
	 * 
	 * @param precision
	 *            the new precision for Variable in ProbNode
	 */
	@SuppressWarnings("unchecked")
	public void setPrecision(Double precision) {

		probNode.getVariable().setPrecision( precision );
	}

	/**
	 * gets the precision of the variable in the node
	 */
	public Double getPrecision() {

		Double value = probNode.getVariable().getPrecision();
		if (value == null) {
			value = 0.1;
		}
		setPrecision( value );
		return value;

	}

	/**
	 * gets the Other adittionalProperties of the variable in the node
	 */
	public Object[][] getOtherProperties() {

		HashMap<String, Object> mapProperties =
			(HashMap<String, Object>) probNode.additionalProperties.clone();
		Object[][] value = null;
		if (mapProperties == null) {
			value = new Object[][] {};
		} else {
			// remove the following adittionalProperties that are managed explicitly
			// coordinateYD, DefinitionComment, CoordinateXD, Title,
			// CoordinateY, CoordinateX, UseDefaultStates,
			// DiscreteValuesComment, Relevance, ProbsTableComment, Purpose,
			// DiscretizeValuesComment
			for (int i = 0; i < otherPropertiesKeys.length; i++) {
				mapProperties.remove( otherPropertiesKeys[i] );
			}
			Set<String> keys = (Set<String>) mapProperties.keySet();
			value = new Object[ mapProperties.size() ][ 2 ];
			int i = 0;
			for (String key : keys) {
				value[i][0] = key;
				value[i++][1] = mapProperties.get( key );
			}
		}
		return value;
	}

	/**
	 * sets the Other adittionalProperties of the variable in the node
	 */
	public void setOtherProperties(Object[][] newProperties) {

		HashMap<String, String> mapProperties =
			(HashMap<String, String>) probNode.additionalProperties.clone();
		if (newProperties != null) {
			for (int i = 0; i < newProperties.length; i++) {
				if (mapProperties.containsKey( (String) newProperties[i][0] )) {
					mapProperties.remove( (String) newProperties[i][0] );
				}
				mapProperties.put(newProperties[i][0].toString(), 
						newProperties[i][1].toString());
			}
		}
		probNode.additionalProperties = mapProperties;
	}

	/**
	 * @return the listPotentials
	 */
	public ArrayList<Potential> getListPotentials() {

		listPotentials = probNode.getPotentials();
		return listPotentials;
	}

	/**
	 * @param listPotentials
	 *            the listPotentials to set
	 */
	public void setListPotentials(ArrayList<Potential> listPotentials) {

		probNode.getPotentials().set( 0, listPotentials.get( 0 ) );
     	this.listPotentials = listPotentials;
	}


	
	/**
	 * Print the information of the object
	 */
	public String toString() {

		StringBuffer buf = new StringBuffer();
		buf.append( "[NodeWrapper.class (only name displayed)" + "\n" );
		buf.append( "probNode =" + probNode.getName() + "\n" );
		buf.append( "]" + "\n" );
		return buf.toString();
	}
}
