package openmarkov.core.gui.edition.visual;


import java.awt.Color;
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
	 * Color of the Box.
	 */
	private static final Color BACKGROUND_COLOR = Color.WHITE;
	
	/**
	 * Color of lines and letters.
	 */	
	private static final Color FOREGROUND_COLOR = Color.BLACK;
	
	/**
	 * Color of bars proportional to the value of the state.
	 */	
	private static final Color BAR_COLOR = Color.RED;
	
	/**
	 * Font type Helvetica, plain, size 9.
	 */
	protected static final Font SCALE_FONT = new Font("Helvetica", Font.PLAIN, 9);
	
	/**
	 * Internal margin around the Box.
	 */
	private static final double INTERNAL_MARGIN = 7;
	
	/**
	 * Horizontal Offset of the Box .
	 */
	private static final double BOX_HORIZONTAL_OFFSET = 2;
	
	/**
	 * Width of the Box.
	 */
	private static final double BOX_WIDTH = 
		VisualNode.NODE_EXPANDED_WIDTH - (2 * INTERNAL_MARGIN);	

	/**
	 * Indentation.
	 */
	private static final double INDENT = 5;
	
	/**
	 * Relative vertical position of the bar.
	 */
	private static final double BAR_VERTICAL_POSITION = 12;
	
	/**
	 * Horizontal starting position of the bar.
	 */
	private static final double BAR_HORIZONTAL_POSITION = 42;
	
	/**
	 * Maximum length of the bar.
	 */
	private static final double BAR_FULL_LENGTH = 100;
	
	/**
	 * Height of the bar.
	 */
	private static final double BAR_HEIGHT = 5;
	
	/**
	 * Horizontal position for the value to be shown on the right of the bar.
	 */
	private static final double VALUE_HORIZONTAL_POSITION = 
			BAR_HORIZONTAL_POSITION + BAR_FULL_LENGTH + INDENT*2;
	
	/**
	 * Vertical separation between the line for expected value and
	 * the line for the scale.
	 */
	private static final double VERTICAL_SEPARATION = 7;
	
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
	
	/**
	 * Creates a new Finite States Variable innerBox.
	 * 
	 * @param vNode
	 *            visualNode to which this Finite States Variable innerBox is associated.
	 */
	public ExpectedValueBox(VisualNode vNode) {
		visualNode = vNode;
		expectedValue = createExpectedValue();
	}
	
	/**
	 * Returns the shape of the innerBox.
	 * 
	 * @return shape of the innerBox.
	 */
	public Shape getShape(Graphics2D g) {
		double innerNodeHeight = getInnerBoxHeight(g);
		return new Rectangle2D.Double(
				visualNode.getUpperLeftCornerX(g) + BOX_HORIZONTAL_OFFSET,
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
			g.drawString("" + (Math.floor((expectedValue[2]+expectedValue[0]) * 10))/10, 
					(stateXposition.intValue() + 
							new Double(VALUE_HORIZONTAL_POSITION).intValue()),
					stateYposition.intValue());
			g.setPaint(FOREGROUND_COLOR);
		}
		
		//draw the scale in the bottom part
		Double scaleXPostion = visualNode.getUpperLeftCornerX(g) + 
				INDENT + BAR_HORIZONTAL_POSITION;
		Double scaleYPostion = visualNode.getUpperLeftCornerY(g) +
				visualNode.getTextHeight(g) + INTERNAL_MARGIN + 
				BAR_VERTICAL_POSITION + VERTICAL_SEPARATION;
		
		
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
		g.drawString(""+expectedValue[0], 
				scaleXPostion.intValue() - SCALE_RANGE_HORIZONTAL_OFFSET,
				scaleYPostion.intValue() + g.getFont().getSize() + 
						SCALE_RANGE_VERTICAL_OFFSET
				);
		g.drawString(""+expectedValue[1],
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
	 * @return the height of the innerBox.
	 */
	public double getInnerBoxHeight(Graphics2D g) {
		double innerBoxHeight;
		innerBoxHeight = (INTERNAL_MARGIN*2) + 
				BAR_VERTICAL_POSITION + 
				VERTICAL_SEPARATION +
				BAR_HEIGHT/2;
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
		double value = ((Math.floor(Math.random()* 1000))/10);
		while (value > (aux2-aux1)) {
			value = value/1.4;
		}
		expVal[2] = (Math.floor((value) * 10))/10; //valor esperado
		return expVal;		
	}
	
}
