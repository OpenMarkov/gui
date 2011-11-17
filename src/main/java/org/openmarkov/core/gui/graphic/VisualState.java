package org.openmarkov.core.gui.graphic;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;


/**
 * This class implements the graphic representation of each state
 * that a node has.
 * 
 * @author asaez 
 * @version 1.0
 */
public class VisualState extends VisualElement {

	/**
	 * Font type Helvetica, plain, size 11.
	 */
	protected static final Font STATES_FONT = new Font("Helvetica", Font.PLAIN, 11);
	
	/**
	 * Color for the text of the state's name.
	 */	
	private static final Color TEXT_COLOR = Color.BLACK;
	
	/**
	 * Color associated to the Evidence Case number N+0
	 * (where N = [0, 5, 10 ,...]).
	 */	
	public static final Color EVIDENCE_CASE_0_COLOR = Color.RED;
	
	/**
	 * Color associated to the Evidence Case number N+1
	 * (where N = [0, 5, 10 ,...]).
	 */	
	public static final Color EVIDENCE_CASE_1_COLOR = Color.BLUE;
	
	/**
	 * Color associated to the Evidence Case number N+2
	 * (where N = [0, 5, 10 ,...]).
	 */	
	public static final Color EVIDENCE_CASE_2_COLOR = Color.ORANGE;
	
	/**
	 * Color associated to the Evidence Case number N+3
	 * (where N = [0, 5, 10 ,...]).
	 */	
	public static final Color EVIDENCE_CASE_3_COLOR = Color.MAGENTA;
	
	/**
	 * Color associated to the Evidence Case number N+4
	 * (where N = [0, 5, 10 ,...]).
	 */	
	public static final Color EVIDENCE_CASE_4_COLOR = Color.YELLOW;
	
	/**
	 * The VisualNode this State is associated to.
	 */
	private VisualNode visualNode;
	
	/**
	 * The order number assigned to this State. Determines in which 
	 * position will be painted this state.
	 */
	private int stateNumber;
	
	/**
	 * The name assigned to this State.
	 */
	private String stateName;
	
	/**
	 * Array of values (probabilities) assigned to the state.
	 * There is one value for each evidence case in memory.
	 * The values are in the range [0, 1].
	 */
	private ArrayList<Double> stateValues;
	
	/**
	 * This variable indicates which is the position of the 
	 * arrayList currently selected (corresponding with the 
	 * current evidence case).
	 */
	private int currentStateValue;		
	
	/**
	 * String resource.
	 */
	private StringResource stringResource = null;

	/**
	 * Creates a new State.
	 * 
	 * @param visualNode
	 *            visualNode to which this State is associated.
	 * @param number
	 *            total number of states that has the inner box.
	 * @param name
	 *            name of this state.
	 */
	public VisualState(VisualNode visualNode, int number, String name) {
		this.visualNode = visualNode;
		this.stateNumber = number;
		this.stateName = name;
		stateValues = new ArrayList<Double>(1);
		stateValues.add(0, 0.0);
		currentStateValue = 0;
		stringResource = StringResourceLoader.getUniqueInstance().getBundleMessages();
	}
	
	/**
	 * Creates a new State.
	 * 
	 * @param visualNode
	 *            visualNode to which this State is associated.
	 * @param number
	 *            total number of states that has the inner box.
	 * @param name
	 *            name of this state.
	 * @param numValues
	 *            Number of values that has to be each visual state.
	 */
	public VisualState(VisualNode visualNode, int number, String name, int numValues) {
		this.visualNode = visualNode;
		this.stateNumber = number;
		this.stateName = name;
		stateValues = new ArrayList<Double>(1);
		for (int i=0; i<numValues; i++) {
			stateValues.add(i, 0.0);
		}
		currentStateValue = 0;
		stringResource = StringResourceLoader.getUniqueInstance().getBundleMessages();
	}
	
	/**
	 * Returns the visualNode to which this sate is associated.
	 * 
	 * @return visualNode to which this sate is associated.
	 */	
	public VisualNode getVisualNode() {
		return visualNode;
	}

	/**
	 * Sets the visualNode to which this sate is associated.
	 * 
	 * @param visualNode
	 *            the visualNode to which this sate is associated.
	 */
	public void setVisualNode(VisualNode visualNode) {
		this.visualNode = visualNode;
	}

	/**
	 * Returns the order number assigned to this state.
	 * 
	 * @return order number assigned to this state.
	 */	
	public int getStateNumber() {
		return stateNumber;
	}

	/**
	 * Sets the order number of this state.
	 * 
	 * @param stateNumber
	 *            the order number of this state.
	 */
	public void setStateNumber(int stateNumber) {
		this.stateNumber = stateNumber;
	}

	/**
	 * Returns the name assigned to this state.
	 * 
	 * @return name assigned to this state.
	 */	
	public String getStateName() {
		return stateName;
	}

	/**
	 * Sets the name of this state.
	 * 
	 * @param stateName
	 *            the name of this state.
	 */
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	/**
	 * Sets which is the position of the array of values that is selected.
	 * 
	 * @param currentStateValue
	 *            the position of the array of values to be set.
	 */
	public void setCurrentStateValue(int currentStateValue) {
		this.currentStateValue = currentStateValue;
	}
	
	/**
	 * Creates a new position in the array of values of the visual state 
	 * It initially assigned 0.0 to this new position 
	 */
	public void createNewStateValue() {
		stateValues.add(0.0);
	}	
	
	/**
	 * Clears all the positions in the array of values of the visual state 
	 * and creates again the initial position assigning 0.0 to it 
	 */
	public void clearAllStateValues() {
		stateValues.clear();
		stateValues.add(0, 0.0);
	}	
	
	/**
	 * Sets the value (probability) of this state for the given position 
	 * of the array (this position matches the evidence case number).
	 * The value is truncated so it only has two decimals
	 * 
	 * @param caseNumber
	 *            the position in the array to be established
	 * @param value
	 *            the value (probability) to be set
	 */
	public void setStateValue(int caseNumber, double value) {
		try {
			double truncatedValue = (Math.rint(value*100))/100;
			stateValues.set(caseNumber, truncatedValue);
		} catch (Exception exc) {
			JOptionPane.showMessageDialog(null, "ERROR" +
					"\n\n" + exc.getMessage(), 
					stringResource.getString("ExceptionGeneric.Title.Label"),
					JOptionPane.ERROR_MESSAGE);
		}		
	}
	
	/**
	 * Returns the number of positions in the array. This number is the
	 * same that the number of evidence cases in memory and the same that
	 * the number of bars that should be painted
	 * 
	 * @return the number of bars to be painted for that state.
	 */	
	public int getNumberOfValues() {
		return stateValues.size();
	}

	/**
	 * Calculates the position that this state occupies inside the inner box.
	 * This position is reserve
	 * 
	 * @return the position that this state occupies inside the inner box
	 */	
	private int getStatePosition() {
		FSVariableBox innerBox = (FSVariableBox)visualNode.getInnerBox();
		return (innerBox.getNumStates() - stateNumber);
	}

	/**
	 * Returns a fictitious rectangular shape around the state. This shape
	 * has a height equivalent to the sum of the height of all the bars of 
	 * the state (a narrow margin is added) and its width includes the text 
	 * of the name and the numerical value (a margin is also added).
	 * 
	 * @return shape of the State.
	 */
	public Shape getShape(Graphics2D g) {
		Double x = visualNode.getUpperLeftCornerX(g) + 
				FSVariableBox.INTERNAL_MARGIN + FSVariableBox.STATES_INDENT - 1;
		Double w = FSVariableBox.BOX_WIDTH - 
				(FSVariableBox.STATES_INDENT *2) + 1;
		Double y = 0.0;
		Double h = 0.0;
		if (visualNode.getEditorPanel().isPropagationActive()) {
			y = visualNode.getUpperLeftCornerY(g) + 
				visualNode.getTextHeight(g) + FSVariableBox.INTERNAL_MARGIN + 
				(FSVariableBox.STATES_VERTICAL_SEPARATION*getStatePosition()) +
				((stateValues.size()-1)*FSVariableBox.BAR_HEIGHT*(getStatePosition()-1)) - 
				FSVariableBox.BAR_HEIGHT - 4;
			h = (FSVariableBox.BAR_HEIGHT*stateValues.size()) + 4;
		} else {
			y = visualNode.getUpperLeftCornerY(g) + 
				visualNode.getTextHeight(g) + FSVariableBox.INTERNAL_MARGIN + 
				(FSVariableBox.STATES_VERTICAL_SEPARATION*getStatePosition()) - 
				FSVariableBox.BAR_HEIGHT - 4;
			h = FSVariableBox.BAR_HEIGHT + 4;
		}		
		return new Rectangle2D.Double(x, y, w, h);
	}
	
	/**
	 * Paints the three parts of the visual representation of a state:
	 * - The state's name.
	 * - Horizontal bars which lengths are proportional to the values 
	 *      assigned to the state for each of the evidence cases in memory.
	 * - The value (probability) assigned to the state for the current
	 *      evidence case. This value has the range [0, 1].
	 * 
	 * @param g
	 *            graphics object where paint the node.
	 */
	public void paint(Graphics2D g) {
		Double x = visualNode.getUpperLeftCornerX(g) + 
				FSVariableBox.INTERNAL_MARGIN + FSVariableBox.STATES_INDENT;
		Double yText = 0.0;
		Double yFirstBar =  0.0;
		if (visualNode.getEditorPanel().isPropagationActive()) {
			yText =  visualNode.getUpperLeftCornerY(g) + 
					visualNode.getTextHeight(g) + FSVariableBox.INTERNAL_MARGIN + 
					(FSVariableBox.STATES_VERTICAL_SEPARATION*getStatePosition()) +
					((stateValues.size()-1)*FSVariableBox.BAR_HEIGHT*(getStatePosition()-1)) +
					(((stateValues.size()-1)*FSVariableBox.BAR_HEIGHT) / 2);
			yFirstBar =  visualNode.getUpperLeftCornerY(g) +
					visualNode.getTextHeight(g) + FSVariableBox.INTERNAL_MARGIN + 
					(FSVariableBox.STATES_VERTICAL_SEPARATION*getStatePosition()) +
					((stateValues.size()-1)*FSVariableBox.BAR_HEIGHT*(getStatePosition()-1)) -
					FSVariableBox.BAR_HEIGHT - 1;
		} else {
			yText =  visualNode.getUpperLeftCornerY(g) + 
					visualNode.getTextHeight(g) + FSVariableBox.INTERNAL_MARGIN + 
					(FSVariableBox.STATES_VERTICAL_SEPARATION*getStatePosition());
			yFirstBar =  visualNode.getUpperLeftCornerY(g) +
					visualNode.getTextHeight(g) + FSVariableBox.INTERNAL_MARGIN + 
					(FSVariableBox.STATES_VERTICAL_SEPARATION*getStatePosition())-
					FSVariableBox.BAR_HEIGHT - 1;
		}
		g.setPaint(TEXT_COLOR);
		g.setFont(STATES_FONT);
		stateName = adjustText(stateName, FSVariableBox.BAR_HORIZONTAL_POSITION,
				2, STATES_FONT, g);
		g.drawString(stateName, x.intValue(), yText.intValue());
		
		if (getVisualNode().getEditorPanel().isPropagationActive()) {				
			for (int i=0; i<stateValues.size(); i++) {
				g.setPaint(Color.BLACK);
				g.drawLine(new Double(x + FSVariableBox.BAR_HORIZONTAL_POSITION - 1).intValue(), 
						new Double(yFirstBar + (i * FSVariableBox.BAR_HEIGHT) - 1).intValue(),
						new Double(x + FSVariableBox.BAR_HORIZONTAL_POSITION - 1).intValue(),
						new Double(yFirstBar + (i * FSVariableBox.BAR_HEIGHT) + 
								FSVariableBox.BAR_HEIGHT).intValue()
						);
				g.drawLine(new Double(x + FSVariableBox.BAR_HORIZONTAL_POSITION + 
								FSVariableBox.BAR_FULL_LENGTH).intValue(), 
						new Double(yFirstBar + (i * FSVariableBox.BAR_HEIGHT) - 1).intValue(),
						new Double(x + FSVariableBox.BAR_HORIZONTAL_POSITION + 
								FSVariableBox.BAR_FULL_LENGTH).intValue(), 
						new Double(yFirstBar + (i * FSVariableBox.BAR_HEIGHT) + 
								FSVariableBox.BAR_HEIGHT).intValue()
						);
				if (i%5 == 0) {
					g.setPaint(EVIDENCE_CASE_0_COLOR);
				} else if (i%5 == 1) {
					g.setPaint(EVIDENCE_CASE_1_COLOR);
				} else if (i%5 == 2) {
					g.setPaint(EVIDENCE_CASE_2_COLOR);
				} else if (i%5 == 3) {
					g.setPaint(EVIDENCE_CASE_3_COLOR);
				} else if (i%5 == 4) {
					g.setPaint(EVIDENCE_CASE_4_COLOR);
				}
				g.fill(new Rectangle2D.Double(x + FSVariableBox.BAR_HORIZONTAL_POSITION, 
						yFirstBar + (i * FSVariableBox.BAR_HEIGHT),
						(stateValues.get(i)*10000)/FSVariableBox.BAR_FULL_LENGTH,
						FSVariableBox.BAR_HEIGHT)
						);
				if (currentStateValue%5 == 0) {
					g.setPaint(EVIDENCE_CASE_0_COLOR);
				} else if (currentStateValue%5 == 1) {
					g.setPaint(EVIDENCE_CASE_1_COLOR);
				} else if (currentStateValue%5 == 2) {
					g.setPaint(EVIDENCE_CASE_2_COLOR);
				} else if (currentStateValue%5 == 3) {
					g.setPaint(EVIDENCE_CASE_3_COLOR);
				} else if (currentStateValue%5 == 4) {
					g.setPaint(EVIDENCE_CASE_4_COLOR);
				}
				g.drawString(stateValues.get(currentStateValue).toString(), 
						(x.intValue() + new Double(FSVariableBox.VALUE_HORIZONTAL_POSITION).intValue()),
						yText.intValue());			
			}
		} else {
			g.setPaint(Color.BLACK);
			g.drawLine(new Double(x + FSVariableBox.BAR_HORIZONTAL_POSITION - 1).intValue(), 
					new Double(yFirstBar - 1).intValue(),
					new Double(x + FSVariableBox.BAR_HORIZONTAL_POSITION - 1).intValue(),
					new Double(yFirstBar + FSVariableBox.BAR_HEIGHT).intValue()
					);
			g.drawLine(new Double(x + FSVariableBox.BAR_HORIZONTAL_POSITION + 
							FSVariableBox.BAR_FULL_LENGTH).intValue(), 
					new Double(yFirstBar - 1).intValue(),
					new Double(x + FSVariableBox.BAR_HORIZONTAL_POSITION + 
							FSVariableBox.BAR_FULL_LENGTH).intValue(), 
					new Double(yFirstBar + FSVariableBox.BAR_HEIGHT).intValue()
					);
		}
		g.setPaint(TEXT_COLOR);
	}
}
