/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.graphic.prm;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

import org.openmarkov.core.gui.graphic.VisualElement;
import org.openmarkov.core.gui.graphic.VisualNode;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.prm.Instance;

/**
 * This class is the visual representation of a chance node.
 * 
 * @author ibermejo
 * @version 1.0
 */
public class VisualInstance extends VisualElement {

	protected static final BasicStroke OBSERVED_WIDE_STROKE = new BasicStroke(
			6.0f);

	/**
	 * Default internal color of the visual instance
	 * 
	 */
	private static final Color BACKGROUND_COLOR = Color.WHITE;
	
	/**
	 * Internal color of the visual instance when it is marked as input.
	 */
	private static final Color BACKGROUND_COLOR_INPUT = new Color(0.8f, 0.8f, 0.8f);	

	/**
	 * Color of the letters
	 */
	private static final Color FOREGROUND_COLOR = Color.BLACK;

	/**
	 * Width of a the arc of the rounded rectangle.
	 */
	private static final double ARC_WIDTH = 20;

	/**
	 * Height of a the arc of the rounded rectangle.
	 */
	private static final double ARC_HEIGHT = 20;

	/**
	 * Horizontal margin for the bounding box
	 */
	protected static final double HORIZONTAL_MARGIN = 120;
	
	/**
	 * Vertical margin for the bounding box
	 */
	protected static final double VERTICAL_MARGIN = 50;	
	
	private  static final Font FONT_HELVETICA_BOLD = new Font("Helvetica", Font.BOLD, 15);	

	private  static final Font FONT_HELVETICA_PLAIN = new Font("Helvetica", Font.PLAIN, 15);	
	
	/**
	 * Embedded instance
	 */
	private Instance instance;
	
	/**
	 * Dimensions of the instance
	 */
	private double[] dimensions = new double[6];

	private ArrayList<VisualNode> visualNodes = new ArrayList<VisualNode>();

	private HashMap<String, VisualInstance> visualSubInstances = new HashMap<String, VisualInstance>();

	public VisualInstance(Instance instance, ArrayList<VisualNode> allVisualNodes)
	{
		this.instance = instance;
		
		// Calculate bounding box
		double topCorner = Double.POSITIVE_INFINITY; 
		double bottomCorner = 0.0;
		double leftCorner = Double.POSITIVE_INFINITY;
		double rightCorner = 0;
		for(ProbNode probNode: instance.getNodes())
		{

			if(probNode.getNode().getCoordinateX() < leftCorner)
			{
				leftCorner = probNode.getNode().getCoordinateX();
			}
			if(probNode.getNode().getCoordinateX() > rightCorner)
			{
				rightCorner = probNode.getNode().getCoordinateX();
			}
			if(probNode.getNode().getCoordinateY() < topCorner)
			{
				topCorner = probNode.getNode().getCoordinateY();
			}
			if(probNode.getNode().getCoordinateY() > bottomCorner)
			{
				bottomCorner = probNode.getNode().getCoordinateY();
			}					
		}
		
		for(ProbNode probNode: instance.getNodes())
		{
			for(VisualNode visualNode: allVisualNodes)
			{
				if(probNode.equals(visualNode.getProbNode()))
				{
					visualNodes.add(visualNode);
				}
			}
		}		
		
		leftCorner -= HORIZONTAL_MARGIN;
		rightCorner += HORIZONTAL_MARGIN;
		topCorner -= VERTICAL_MARGIN;
		bottomCorner += VERTICAL_MARGIN;
		
		dimensions[0] = leftCorner;
		dimensions[1] = topCorner;

		dimensions[2] = rightCorner - leftCorner;
		dimensions[3] = bottomCorner - topCorner;
		dimensions[4] = ARC_WIDTH;
		dimensions[5] = ARC_HEIGHT;		
		
		for(Instance subInstance : instance.getSubInstances().values())
		{
			visualSubInstances.put(subInstance.getName(), new VisualInstance(subInstance, allVisualNodes));
		}
	}

	@Override
	public Shape getShape(Graphics2D g) {
		return new RoundRectangle2D.Double(dimensions[0], dimensions[1],
				dimensions[2], dimensions[3], dimensions[4], dimensions[5]);
	}

	@Override
	public void paint(Graphics2D g) {
		Shape shape = getShape(g);
		Color backgroundColor= (instance.isInput())? BACKGROUND_COLOR_INPUT : BACKGROUND_COLOR;
		g.setPaint(backgroundColor);
		g.fill(shape);
		g.setPaint(FOREGROUND_COLOR);
		
		String text = adjustText(instance.getClassNet().getName(), dimensions[2], 3, FONT_HELVETICA_BOLD, g);
		g.drawString(text, (float) dimensions[0] + 10.0f, (float) dimensions[1] + 15.0f);
		g.setStroke((isSelected())? WIDE_STROKE : NORMAL_STROKE);
		g.draw(shape);
		
		for(VisualInstance subInstance : visualSubInstances.values())
		{
			subInstance.paint(g);
		}
	}

	/**
	 * Get instance name
	 * @return
	 */
	public String getName() {
		return instance.getName();
	}
	
	/**
	 * Returns the real position of the instance.
	 * 
	 * @return position of the node in the screen.
	 */
	public Point2D.Double getPosition() {

		return new Point2D.Double(dimensions[0], dimensions[1]);
	}	
	
	public void move(double diffX, double diffY) {
		move(diffX, diffY, true);
	}

	private void move(double diffX, double diffY, boolean moveNodes) {
		dimensions[0] += diffX;
		dimensions[1] += diffY;
		for(VisualInstance subInstance : visualSubInstances.values())
		{
			subInstance.move(diffX, diffY, false);
		}
		if(moveNodes)
		{
			for(VisualNode visualNode : visualNodes)
			{
				visualNode.setTemporalPosition(new Point2D.Double(visualNode
						.getTemporalPosition().getX() + diffX, visualNode
						.getTemporalPosition().getY() + diffY));
			}		
		}
	}
	
	public double getCoordinateX()
	{
		return dimensions[0];
	}

	public double getCoordinateY()
	{
		return dimensions[1];
	}
	
	public double getWidth()
	{
		return dimensions[2];
	}
	
	public double getHeight()
	{
		return dimensions[3];
	}

	public boolean isInput() {
		return instance.isInput();
	}
	
	public void setInput(boolean b) {
		instance.setInput(b);
	}

	public Instance getInstance() {
		return instance;
	}

	public boolean acceptsAsInput(VisualInstance inputInstance) {
		return instance.acceptsAsInput(inputInstance.getInstance());
	}

	public VisualInstance getSubInstance(String name) {
		return visualSubInstances.get(name);
	}
}
