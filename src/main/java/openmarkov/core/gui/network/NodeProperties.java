package openmarkov.core.gui.network;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.Potential;



/**
 * This class is used to save temporally the adittionalProperties of a node.
 * 
 * @author jmendoza
 * @version 1.1 jlgozalo - toString() included & PartitionedInterval included
 * @version 1.2 jlgozalo - precision & potential attribute; getter/setter
 *          included and get/set OtherProperties
 */
public class NodeProperties implements Cloneable, Serializable {

	/**
	 * Static field for serializable class.
	 */
	private static final long serialVersionUID = 6077271692321367040L;

	/**
	 * Name of the node.
	 */
	private String name = null;

	/**
	 * Title of the node.
	 */
	private String title = null;

	/**
	 * X coordinate of the node (only for clipboard purpose).
	 */
	private Double coordinateX;

	/**
	 * Y coordinate of the node (only for clipboard purpose).
	 */
	private Double coordinateY;

	/**
	 * Relevance of the node.
	 */
	private Double relevance = null;

	/**
	 * Comment of the definition of the node.
	 */
	private String definitionComment = null;
	/**
	 * Comment of the discrete values of the node.
	 */
	private String discreteValuesComment = null;
	/**
	 * Comment of the discretize values of the node.
	 */
	private String discretizeValuesComment = null;
	/**
	 * Comment of the probs table of the node.
	 */
	private String probsTableComment = null;

	/**
	 * Purpose of the node.
	 */
	private String purpose = null;

	/**
	 * Type of the node.
	 */
	private NodeType nodeType = null;

	/**
	 * Type of the variables of the node.
	 */
	private VariableType variableType = null;

	/**
	 * Names of the states of the node.
	 */
	private State[] states = null;

	/**
	 * Parents of the node.
	 */
	private ArrayList<NodeWrapper> parents = null;

	/**
	 * Possible parents of the node.
	 */
	private ArrayList<NodeWrapper> possibleParents =
		new ArrayList<NodeWrapper>();

	/**
	 * Partitioned Interval of the node (if Discretized)
	 */
	private PartitionedInterval partitionedInterval = null; // jlgozalo. add
	// 12/10/2009

	/**
	 * precision for the PartitionedInterval (discretized variables)
	 */
	private double precision = 0.1; // jlgozalo. add 02/02/2010

	/**
	 * Table Potential of the variable of the node
	 */
	private ArrayList<Potential> listPotentials = null; // jlgozalo. add
	// 02/02/2010

	/**
	 * Network which the node belongs to.
	 */
	private ProbNet network = null;
	/**
	 * other adittionalProperties in the node not directly managed by OPENMARKOV
	 */
	private Object[][] otherProperties = null;

	/**
	 * Construct a new object setting the fields to their default values.
	 */
	public NodeProperties() {

		name = "";
		title = "";
		coordinateX = 0.0;
		coordinateY = 0.0;
		relevance = 7.0;
		purpose = "";
		definitionComment = "";
		discreteValuesComment = "";
		discretizeValuesComment = "";
		probsTableComment = "";
		nodeType = NodeType.CHANCE;
		variableType = VariableType.FINITE_STATES;
		int i = 0;
		State[] states = new State [GUIDefaultStates.getByIndex(0).length];
		for (String str:GUIDefaultStates.getByIndex(0)){
			states[i] = new State(str);
			i++;
		}
		
		parents = new ArrayList<NodeWrapper>();
		possibleParents = new ArrayList<NodeWrapper>();
		network = null;
		partitionedInterval =
			new PartitionedInterval( true, Double.NEGATIVE_INFINITY,
				Double.POSITIVE_INFINITY, true ); // jlgozalo. add 12/10
		precision = 0.0;
		listPotentials = new ArrayList<Potential>();
		otherProperties = new Object[][] {};
	}

	/**
	 * Returns the name of the node.
	 * 
	 * @return name of the node.
	 */
	public String getName() {

		return name;
	}

	/**
	 * Sets the name of the node.
	 * 
	 * @param value
	 *            new name.
	 */
	public void setName(String value) {

		String newValue = value;

		if (value == null) {
			newValue = "";
		}
		name = newValue;
	}

	/**
	 * Returns the title of the node.
	 * 
	 * @return title of the node.
	 */
	public String getTitle() {

		return title;
	}

	/**
	 * Sets the title of the node.
	 * 
	 * @param value
	 *            new title.
	 */
	public void setTitle(String value) {

		String newValue = value;

		if (value == null) {
			newValue = "";
		}
		title = newValue;
	}

	/**
	 * Returns the X position of the node.
	 * 
	 * @return X position of the node.
	 */
	public Double getX() {

		return coordinateX;
	}

	/**
	 * Sets the X position of the node.
	 * 
	 * @param value
	 *            new X position.
	 */
	public void setX(Double value) {

		Double newValue = value;

		if (value == null) {
			newValue = new Double( 0.0 );
		}
		coordinateX = newValue;
	}

	/**
	 * Returns the Y position of the node.
	 * 
	 * @return Y position of the node.
	 */
	public Double getY() {

		return coordinateY;
	}

	/**
	 * Sets the Y position of the node.
	 * 
	 * @param value
	 *            new Y position.
	 */
	public void setY(Double value) {

		Double newValue = value;

		if (value == null) {
			newValue = new Double( 0.0 );
		}
		coordinateY = newValue;
	}

	/**
	 * Returns the relevance of the node.
	 * 
	 * @return relevance of the node.
	 */
	public Double getRelevance() {

		return relevance;
	}

	/**
	 * Sets the relevance of the node.
	 * 
	 * @param value
	 *            new relevance.
	 */
	public void setRelevance(Double value) {

		Double newValue = value;

		if (value == null) {
			newValue = new Double( 0.0 );
		}
		relevance = newValue;
	}

	/**
	 * Returns the purpose of the node.
	 * 
	 * @return purpose of the node.
	 */
	public String getPurpose() {

		return purpose;
	}

	/**
	 * Sets the purpose of the node.
	 * 
	 * @param value
	 *            new purpose.
	 */
	public void setPurpose(String value) {

		String newValue = value;

		if (value == null) {
			newValue = "";
		}
		purpose = newValue;
	}

	/**
	 * Returns the comment of the node.
	 * 
	 * @return comment of the node.
	 */
	public String getDefinitionComment() {

		return definitionComment;
	}

	/**
	 * Sets the comment of the node.
	 * 
	 * @param value
	 *            new comment.
	 */
	public void setDefinitionComment(String value) {

		String newValue = value;

		if (value == null) {
			newValue = "";
		}
		definitionComment = newValue;
	}

	/**
	 * Returns the discreteValuesComment of the node.
	 * 
	 * @return discreteValuesComment of the node.
	 */
	public String getDiscreteValuesComment() {

		return discreteValuesComment;
	}

	/**
	 * Sets the discreteValuesComment of the node.
	 * 
	 * @param value
	 *            new discreteValuesComment.
	 */
	public void setDiscreteValuesComment(String value) {

		String newValue = value;

		if (value == null) {
			newValue = "";
		}
		discreteValuesComment = newValue;
	}

	/**
	 * Returns the discretizeValuesComment of the node.
	 * 
	 * @return discretizeValuesComment of the node.
	 */
	public String getDiscretizeValuesComment() {

		return discretizeValuesComment;
	}

	/**
	 * Sets the discretizeValuesComment of the node.
	 * 
	 * @param value
	 *            new comment.
	 */
	public void setDiscretizeValuesComment(String value) {

		String newValue = value;

		if (value == null) {
			newValue = "";
		}
		discretizeValuesComment = newValue;
	}

	/**
	 * Returns the probsTableComment of the node.
	 * 
	 * @return probsTableComment of the node.
	 */
	public String getProbsTableComment() {

		return probsTableComment;

	}

	/**
	 * Sets the probsTableComment of the node.
	 * 
	 * @param value
	 *            new probsTableComment.
	 */
	public void setProbsTableComment(String value) {

		String newValue = value;

		if (value == null) {
			newValue = "";
		}
		probsTableComment = newValue;
	}

	/**
	 * Returns the type of the node.
	 * 
	 * @return type of the node.
	 */
	public NodeType getNodeType() {

		return nodeType;
	}

	/**
	 * Sets the type of the node.
	 * 
	 * @param value
	 *            new node type.
	 */
	public void setNodeType(NodeType value) {

		NodeType newValue = value;

		if (value == null) {
			newValue = NodeType.CHANCE;
		}
		nodeType = newValue;
	}

	/**
	 * Returns the type of the variable which the node represents.
	 * 
	 * @return type of the variable.
	 */
	public VariableType getVariableType() {

		return variableType;
	}

	/**
	 * Sets the type of the variable which the node represents.
	 * 
	 * @param value
	 *            new variable type.
	 */
	public void setVariableType(VariableType value) {

		VariableType newValue = value;

		if (value == null) {
			newValue = VariableType.FINITE_STATES;
		}
		variableType = newValue;
	}

	/**
	 * Returns the states of the node.
	 * 
	 * @return states of the node.
	 */
	public State[] getStates() {

		return states;
	}

	/**
	 * Sets the states of the node.
	 * 
	 * @param strings
	 *            new states.
	 */
	public void setStates(State[] strings) {

		State[] newValue = strings;

		if (strings == null) {
			newValue = new State[0];
		}
		states = newValue;
	}

	/**
	 * Returns the parents of the node.
	 * 
	 * @return parents of the node.
	 */
	public ArrayList<NodeWrapper> getParents() {

		return parents;
	}

	/**
	 * Sets the parents of the node.
	 * 
	 * @param value
	 *            new parents.
	 */
	public void setParents(ArrayList<NodeWrapper> value) {

		ArrayList<NodeWrapper> newValue = value;

		if (value == null) {
			newValue = new ArrayList<NodeWrapper>();
		}
		parents = newValue;
	}

	/**
	 * Returns the possible parents of the node.
	 * 
	 * @return possible parents of the node.
	 */
	public ArrayList<NodeWrapper> getPossibleParents() {

		return possibleParents;
	}

	/**
	 * Sets the possible parents of the node.
	 * 
	 * @param value
	 *            new possible parents.
	 */
	public void setPossibleParents(ArrayList<NodeWrapper> value) {

		ArrayList<NodeWrapper> newValue = value;

		if (value == null) {
			newValue = new ArrayList<NodeWrapper>();
		}
		possibleParents = newValue;
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
	 * Returns the network which the node belongs to.
	 * 
	 * @return the network which the node belongs to.
	 */
	public ProbNet getNetwork() {

		return network;
	}

	/**
	 * Sets the network which the node belongs to.
	 * 
	 * @param value
	 *            network which the node belongs to.
	 */
	public void setNetwork(ProbNet value) {

		network = value;
	}

	/**
	 * Returns the Partitioned Interval for the node (assuming Discretized)
	 * 
	 * @return the partitionedInterval //jlgozalo. add 12/10
	 */
	public PartitionedInterval getPartitionedInterval() {

		return partitionedInterval;
	}

	/**
	 * Sets the partitioned Interval for the node (assuming Discretized)
	 * 
	 * @param partitionedInterval
	 *            the partitionedInterval to set //jlgozalo. add 12/10
	 */
	public void setPartitionedInterval(PartitionedInterval partitionedInterval) {

		PartitionedInterval newPartInterval = partitionedInterval;
		if (partitionedInterval == null) {
			newPartInterval =
				new PartitionedInterval( true, Double.NEGATIVE_INFINITY,
					Double.POSITIVE_INFINITY, true );
		}
		this.partitionedInterval = newPartInterval;
	}

	/**
	 * @return the precision
	 */
	public Double getPrecision() {

		return precision;
	}

	/**
	 * @param precision
	 *            the precision to set
	 */
	public void setPrecision(Double value) {

		Double newValue;

		if (value == null) {
			newValue = new Double( 0.0 );
		} else {
			newValue = value;
		}
		this.precision = newValue;
	}

	/**
	 * @return the list of potentials
	 */
	public ArrayList<Potential> getListPotentials() {

		return listPotentials;
	}

	/**
	 * @param potential
	 *            the list of potentials to set
	 */
	public void setListPotentials(ArrayList<Potential> listPotentials) {

		this.listPotentials = listPotentials;
	}

	/**
	 * Indicates whether some other object is "equal to" this one. The graph
	 * which the node belongs to is not compared.
	 * 
	 * @param obj
	 *            object to compare with this one. It must be a NodeProperties
	 *            instance.
	 */
	@Override
	public boolean equals(Object obj) {

		NodeProperties properties;

		if (obj instanceof NodeProperties) {
			properties = (NodeProperties) obj;
			if (name.equals( properties.getName() )
				&& title.equals( properties.getTitle() )
				&& relevance.equals( properties.getRelevance() )
				&& purpose.equals( properties.getPurpose() )
				&& definitionComment.equals( properties.getDefinitionComment() )
				&& discreteValuesComment.equals( properties
					.getDiscreteValuesComment() )
				&& discretizeValuesComment.equals( properties
					.getDiscretizeValuesComment() )
				&& probsTableComment.equals( properties.getProbsTableComment() )
				&& nodeType.equals( properties.getNodeType() )
				&& variableType.equals( properties.getVariableType() )
				&& Arrays.equals( states, properties.getStates() )
				&& parents.equals( properties.getParents() )
				&& partitionedInterval.equals( properties
					.getPartitionedInterval() )
				&& listPotentials.equals( properties.getListPotentials() )
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
	 */
	@Override
	public NodeProperties clone() {

		NodeProperties obj = null;

		try {
			obj = (NodeProperties) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
		return obj;
	}

	/**
	 * This method removes any information about networks and nodes that belong
	 * to a network.
	 */
	public void adaptToClipboard() {

		parents = new ArrayList<NodeWrapper>();
		possibleParents = new ArrayList<NodeWrapper>();
		network = null;
	}

	/**
	 * Print the adittionalProperties of the class
	 */
	public String toString() {

		StringBuffer buf = new StringBuffer();
		buf.append( "[NodeProperties.class " + "\n" );
		buf.append( "name = " + name + "\n" );
		buf.append( "title = " + title + "\n" );
		buf.append( "coordinateX = " + coordinateX.toString() + "\n" );
		buf.append( "coordinateY = " + coordinateY.toString() + "\n" );
		buf.append( "relevance = " + relevance.toString() + "\n" );
		buf.append( "purpose = " + purpose + "\n" );
		buf.append( "comment = " + definitionComment + "\n" );
		buf.append( "discreteValuesComment = " + discreteValuesComment + "\n" );
		buf.append( "discretizeValuesComment = " + discretizeValuesComment
			+ "\n" );
		buf.append( "probsTableComment = " + probsTableComment + "\n" );
		buf.append( "nodeType = " + nodeType + "\n" );
		buf.append( "variableType = " + variableType + "\n" );
		buf.append( "states = " + "size:" + states.length + "\n" );
		for (State str : states) {
			buf.append( str.getName() + "\n" );
		}
		buf.append( "partitionedInterval = " + partitionedInterval + "\n" );
		buf.append( "listPotentials = size:" + listPotentials.size() + "\n" );
		for (Potential potential : listPotentials) {
			buf.append( potential + "\n" );
		}
		buf.append( "parents = " + "size:" + parents.size() + "\n" );
		for (NodeWrapper node : parents) {
			buf.append( node.toString() + "\n" );
		}
		buf.append( "possibleParents = " + "size:" + possibleParents.size()
			+ "\n" );
		for (NodeWrapper node : possibleParents) {
			buf.append( node.toString() + "\n" );
		}
		buf.append( "Network = " + network + "\n" );
		buf.append( "otherProperties = " + "size:" + otherProperties.length
			+ "\n" );
		for (int i = 0; i < otherProperties.length; i++) {
			buf.append( otherProperties[i][0] + ":"
				+ otherProperties[i][1].toString() + "\n" );
		}
		buf.append( "]" + "\n" );

		return buf.toString();
	}
}
