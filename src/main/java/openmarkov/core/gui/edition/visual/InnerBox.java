package openmarkov.core.gui.edition.visual;


import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

/**
 * This abstract class specifies the methods that all inner boxes of the 
 * visual nodes have to implement.
 * 
 * @author asaez 
 * @version 1.0
 */
public abstract class InnerBox extends VisualElement {
	
	/**
	 * Font type Helvetica, plain, size 11.
	 */
	protected static final Font INNERBOX_FONT = new Font("Helvetica", Font.PLAIN, 11);

	/**
	 * Object used to measure text in a specific font.
	 */
	private static FontMetrics fontMeter =
		new JPanel().getFontMetrics(INNERBOX_FONT);
	
	/**
	 * Returns the height of the text used in the innerBox.
	 * 
	 * @param text
	 *            text that appears in the innerBox.
	 * @param g
	 *            graphics object where to paint the element.
	 * @return the height of the text used in the innerBox.
	 */
	protected static double getInnerBoxTextHeight(String text, Graphics2D g) {

		return fontMeter.getStringBounds(text, g).getHeight();
	}
	
	/**
	 * Returns the width of the text used in the innerBox.
	 * 
	 * @param text
	 *            text that appears in the innerBox.
	 * @param g
	 *            graphics object where to paint the element.
	 * @return the width of the text used in the innerBox.
	 */
	protected static double getInnerBoxTextWidth(String text, Graphics2D g) {

		return fontMeter.getStringBounds(text, g).getWidth();
	}
	
	/**
	 * The height of this InnerBox.
	 */
	protected double height;
	
	/**
	 * The VisualNode this InnerBox is associated to.
	 */
	protected VisualNode visualNode;
	
	/**
	 * Returns the visualNode associated with the innerBox.
	 * 
	 * @return visualNode associated with the innerBox.
	 */
	public VisualNode getVisualNode() {
		return visualNode;
	}

	/**
	 * Returns the height of the innerBox. It's calculated depending on the
	 * font, the number of states and the cases in memory
	 * 
	 * @return the height of the innerBox.
	 */
	public abstract double  getInnerBoxHeight(Graphics2D g);


}
