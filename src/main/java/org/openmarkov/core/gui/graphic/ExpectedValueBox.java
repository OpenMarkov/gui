/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.graphic;


import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * This class is the visual representation of the inner box associated
 * to a VisualNode that represents an Expected Value variable
 * 
 * @author asaez 1.0
 */
public class ExpectedValueBox extends InnerBox {

	/**
	 * Font type Helvetica, plain, size 9.
	 */
	protected static final Font SCALE_FONT = new Font("Helvetica", Font.PLAIN, 9);
	
	/**
	 * Vertical separation between the line for expected value and
	 * the line for the scale.
	 */
	private static final double SCALE_VERTICAL_SEPARATION = 12;	
	
	/**
	 * Horizontal Offset for the position of the range values
	 * of the scale.
	 */
	private static final int SCALE_RANGE_HORIZONTAL_OFFSET = 8;

	/**
	 * Vertical Offset for the position of the range values
	 * of the scale.
	 */
	private static final int SCALE_RANGE_VERTICAL_OFFSET = 4;
	

	//Variable que almacena los parámetros necesarios para representar el valor esperado ....... Eliminar..............
	double[] expectedValue;
	


	//private double expectedUtility = 0.0;
	
	//... incluir comentarios javadoc
	private double minUtilityRange = 0.0;
	
	private double maxUtilityRange = 100.0;	
	
	/**
	 * This variable contains the visual state that is part
	 * of this inner box.
	 */
	VisualState visualState = null;
	


	/**
	 * Creates a new Expected Value Variable innerBox.
	 * 
	 * @param vNode
	 *            visualNode to which this Expected Value Variable innerBox is associated.
	 */
	public ExpectedValueBox(VisualNode vNode) {
		visualNode = vNode;
		visualState = new VisualState(visualNode, 0, "  EU");
		expectedValue = createExpectedValue();
	}
	
	
/*	public double getExpectedUtility() {
		return expectedUtility;
	}


	public void setExpectedUtility(double expectedUtility) {
		this.expectedUtility = expectedUtility;
	}
*/	

	public double getMinUtilityRange() {
		return minUtilityRange;
	}
	

	public void setMinUtilityRange(double minUtilityRange) {
		this.minUtilityRange = minUtilityRange;
	}
	

	public double getMaxUtilityRange() {
		return maxUtilityRange;
	}
	

	public void setMaxUtilityRange(double maxUtilityRange) {
		this.maxUtilityRange = maxUtilityRange;
	}
		

	
	

	public VisualState getVisualState() {
		return visualState;
	}


	public void setVisualState(VisualState visualState) {
		this.visualState = visualState;
	}

	
	/**
	 * Returns the number of visual states of this inner box.
	 * 
	 * @return the number of visual states of this inner box.
	 */
	public int getNumStates() {
		return 1;
	}
	
	/**
	 * Returns the shape of the innerBox.
	 * 
	 * @return shape of the innerBox.
	 */
	public Shape getShape(Graphics2D g) {
		double innerNodeHeight = getInnerBoxHeight(g);
		return new Rectangle2D.Double(
				visualNode.getUpperLeftCornerX(g), // + INTERNAL_MARGIN, //...asaez...Antes...BOX_HORIZONTAL_OFFSET = 2
				visualNode.getUpperLeftCornerY(g) + 
						visualNode.getTextHeight(g) + INTERNAL_MARGIN, 
				BOX_WIDTH, 
				innerNodeHeight
				);		
	}
		
	/**
	 * Paints the inner part of the visual node into the graphics object.
	 * 
	 * @param g
	 *            graphics object where paint the node.
	 */
	public void paint(Graphics2D g) {
		Shape shape = getShape(g);
		g.setPaint(BACKGROUND_COLOR);
		g.fill(shape);
		g.setStroke(NORMAL_STROKE);
		g.setPaint(FOREGROUND_COLOR);
		g.draw(shape);	
		g.setFont(INNERBOX_FONT);
		
		visualState.paint(g);
		
		/*
		Double stateXposition = visualNode.getUpperLeftCornerX(g) + 
				INDENT;
		Double stateYposition = visualNode.getUpperLeftCornerY(g) + 
				visualNode.getTextHeight(g) + INTERNAL_MARGIN + 
				BAR_VERTICAL_POSITION;
	
		//draw the line of the expected value
		g.drawString("  EU", stateXposition.intValue(), stateYposition.intValue());
		if (visualNode.getEditorPanel().isPropagationActive()) {
			g.setPaint(BAR_COLOR);
			g.fill(new Rectangle2D.Double(
					stateXposition + BAR_HORIZONTAL_POSITION, 
					stateYposition - BAR_HEIGHT - 1,
					(expectedValue[2]*100)/(expectedValue[1]-expectedValue[0]),
					BAR_HEIGHT)
					);
			//g.drawString("" + (Math.floor((expectedValue[2]+expectedValue[0]) * 10))/10, //...asaez................................
			g.drawString("" + (Math.floor((expectedUtility) * 100))/100,  //...asaez................................
					(stateXposition.intValue() + 
							new Double(VALUE_HORIZONTAL_POSITION).intValue()),
					stateYposition.intValue());
			g.setPaint(FOREGROUND_COLOR);
		}
		*/
		
		//draw the scale in the bottom part
		Double scaleXPostion = visualNode.getUpperLeftCornerX(g) + 
				INTERNAL_MARGIN + STATES_INDENT + BAR_HORIZONTAL_POSITION_UTILITY - 1;
		Double scaleYPostion = visualNode.getUpperLeftCornerY(g) +
				visualNode.getTextHeight(g) + INTERNAL_MARGIN + 
				STATES_VERTICAL_SEPARATION + SCALE_VERTICAL_SEPARATION +
				(BAR_HEIGHT*(visualState.getNumberOfValues()-1));
		
		
		g.draw(new Line2D.Double(scaleXPostion,
				scaleYPostion, 
				scaleXPostion + BAR_FULL_LENGTH, 
				scaleYPostion)
				);
		g.draw(new Line2D.Double(scaleXPostion,
				scaleYPostion - (BAR_HEIGHT/2), 
				scaleXPostion, 
				scaleYPostion + (BAR_HEIGHT/2))
				);
		g.draw(new Line2D.Double(scaleXPostion + (BAR_FULL_LENGTH/4), 
				scaleYPostion - (BAR_HEIGHT/2), 
				scaleXPostion + (BAR_FULL_LENGTH/4), 
				scaleYPostion + (BAR_HEIGHT/2))
				);
		g.draw(new Line2D.Double(scaleXPostion + (BAR_FULL_LENGTH/2), 
				scaleYPostion - (BAR_HEIGHT/2), 
				scaleXPostion + (BAR_FULL_LENGTH/2), 
				scaleYPostion + (BAR_HEIGHT/2))
				);
		g.draw(new Line2D.Double(scaleXPostion + (BAR_FULL_LENGTH*3/4), 
				scaleYPostion - (BAR_HEIGHT/2), 
				scaleXPostion + (BAR_FULL_LENGTH*3/4), 
				scaleYPostion + (BAR_HEIGHT/2))
				);
		g.draw(new Line2D.Double(scaleXPostion + BAR_FULL_LENGTH, 
				scaleYPostion - (BAR_HEIGHT/2), 
				scaleXPostion + BAR_FULL_LENGTH, 
				scaleYPostion + (BAR_HEIGHT/2))
				);

		g.setFont(SCALE_FONT);
		//g.drawString(""+expectedValue[0], //...asaez................................
		g.drawString("" + minUtilityRange, //...asaez................................
				scaleXPostion.intValue() - SCALE_RANGE_HORIZONTAL_OFFSET,
				scaleYPostion.intValue() + g.getFont().getSize() + 
						SCALE_RANGE_VERTICAL_OFFSET
				);
		//g.drawString(""+expectedValue[1],//...asaez................................
		g.drawString("" + maxUtilityRange,//...asaez................................				
				(int) (scaleXPostion.intValue()+ BAR_FULL_LENGTH) - 
						SCALE_RANGE_HORIZONTAL_OFFSET, 
				scaleYPostion.intValue() + g.getFont().getSize() + 
						SCALE_RANGE_VERTICAL_OFFSET
				);
		g.setFont(INNERBOX_FONT);
	}
	

	/**
	 * Returns the height of the innerBox.
	 * 
	 * @param g
	 *            graphics object.
	 * 
	 * @return the height of the innerBox.
	 */
	public double getInnerBoxHeight(Graphics2D g) {
		double innerBoxHeight = 0.0;
		if (visualNode.getEditorPanel().isPropagationActive()) {	
			innerBoxHeight = INTERNAL_MARGIN*2 + 
				STATES_VERTICAL_SEPARATION + 
				SCALE_VERTICAL_SEPARATION +
				(visualState.getNumberOfValues()-1)*BAR_HEIGHT+
				BAR_HEIGHT/2 +
				SCALE_FONT.getSize();
		} else {			
			innerBoxHeight = INTERNAL_MARGIN*2 + 
				STATES_VERTICAL_SEPARATION + 
				SCALE_VERTICAL_SEPARATION +
				BAR_HEIGHT/2 +
				SCALE_FONT.getSize();
		}
		return innerBoxHeight;
	}
	
	
	
	//Método auxiliar para generar el valor esperado....... Eliminar..............
	private double[] createExpectedValue () {
		double[] expVal = new double[3];
		double aux1 = (Math.floor(Math.random()* 1000))/10;
		double aux2 = (Math.floor(Math.random()* 1000))/10;
		if (aux1 > aux2) {
			double aux3 = aux1;
			aux1 = aux2;
			aux2 = aux3;
		}
		expVal[0] = aux1; //límite inferior del valor esperado
		expVal[1] = aux2; //límite superior del valor esperado
		
		//...asaez................................
		
		//................................asaez...
		
		
		double value = ((Math.floor(Math.random()* 1000))/10);
		while (value > (aux2-aux1)) {
			value = value/1.4;
		}
		expVal[2] = (Math.floor((value) * 10))/10; //valor esperado
		return expVal;		
	}

}