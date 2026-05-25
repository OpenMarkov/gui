/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.graphic;

import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.PurposeType;
import org.openmarkov.gui.configuration.GUIColors;

import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import org.openmarkov.core.model.network.Point2D;

import javax.swing.JPanel;
import java.awt.geom.RoundRectangle2D;

/**
 * This class is the visual representation of an Event Node.
 * It is a version of VisualChangeNode. To be polished if version 1.2
 *
 * @author cyago
 * @version 1 28/12/2019- 25/01/2020 added initialEvent
 */
public final class VisualEventNode extends VisualNode {

	private static final BasicStroke OBSERVED_WIDE_STROKE = new BasicStroke(6.0f);
	private static final BasicStroke OBSERVED_NORMAL_STROKE = new BasicStroke(3.0f);
	
	/**
	 * Width of a the arc of the rounded rectangle.
	 */
	private static final double ARC_WIDTH = 20;

	/**
	 * Height of a the arc of the rounded rectangle.
	 */
	private static final double ARC_HEIGHT = 20;

	/**
	 * Creates a new visual node from a node.
	 *
	 * @param node          object that has the information of the node.
	 * @param visualNetwork editor panel to which this visual node is associated.
	 */
	public VisualEventNode(Node node, VisualNetwork visualNetwork) {
		super(node, visualNetwork);
		expanded = false;
		preResolutionFinding = false;
		postResolutionFinding = false;
		setTemporalPosition(new Point2D.Double(node.getCoordinateX(), node.getCoordinateY()));
	}

	/**
	 * Returns the visual measurements of the node.
	 *
	 * @param g graphics object where to paint the element.
	 * @return an array of six elements that contains the center of the node
	 * (elements 0 and 1), the width and height of the node (elements 2
	 * and 3) and the width and height of the rounded corner (elements 4
	 * and 5).
	 */
	private double[] getNodeDimensions(Graphics2D g) {

		double[] dimensions = new double[6];
		String text = getNodeString();
		double textHeight = getHeight(text, g);
		double textWidth = getWidth(text, g);
		double width;
		double height;
		if (isExpanded()) {
			height = innerBox.getInnerBoxHeight(g) + textHeight + 2 * VERTICAL_SPACE_TO_TEXT
					+ NODE_EXPANDED_HEIGHT_MARGIN * 2;
			width = NODE_EXPANDED_WIDTH;
		} else {
			height = textHeight + 2 * VERTICAL_SPACE_TO_TEXT;
			if (textWidth < textHeight) {
				width = DEFAULT_NODE_CONTRACTED_WIDTH;
			} else {
				width = textWidth + 2 * HORIZONTAL_SPACE_TO_TEXT;
			}
		}

		// for visualization purposes the position is temporal
		dimensions[0] = getTemporalPosition().getX() - width / 2;
		dimensions[1] = getTemporalPosition().getY() - height / 2;

		dimensions[2] = width;
		dimensions[3] = height;
		dimensions[4] = ARC_WIDTH;
		dimensions[5] = ARC_HEIGHT;

		return dimensions;

	}



	/**
	 * New method; not copied
	 * Returns the point which will be the center for a circular arrow
	 * 28/12/2019 - At this time only Event Nodes may have circular arrows (self-loops) . 05/04/2020 -Chance nodes may have self-loops
	 *
	 * @param g graphics object where to paint the element.
	 *
	 * @return
	 */
	@Override
	public org.openmarkov.core.model.network.Point2D.Double getSelfLoopPosition(Graphics2D g) {
		Point2D.Double centreNodePoint = getTemporalPosition();
		double[] dims = getNodeDimensions(g);
		//dims[2] = width and dims[3] = height
		Point2D.Double centreArcPoint = new Point2D.Double();
		centreArcPoint.setLocation(centreNodePoint.getX()+ dims[2],centreNodePoint.getY()+ dims[3]);
		return centreArcPoint;
	}


	/**
	 * Returns the X-coordinate of the upper-left corner of the visual node.
	 *
	 * @return the X-coordinate of the upper-left corner of the visual node.
	 */
	public double getUpperLeftCornerX(Graphics2D g) {
		double[] dims = getNodeDimensions(g);
		return dims[0];
	}

	/**
	 * Returns the Y-coordinate of the upper-left corner of the visual node.
	 *
	 * @return the Y-coordinate of the upper-left corner of the visual node.
	 */
	public double getUpperLeftCornerY(Graphics2D g) {
		double[] dims = getNodeDimensions(g);
		return dims[1];
	}

	/**
	 * Returns the shape of the node.
	 *
	 * @return shape of the node.
	 */
	@Override public Shape getCenteredShape(Graphics2D g) {
		double dimensions[] = getNodeDimensions(g);
		return new RoundRectangle2D.Double(dimensions[0], dimensions[1], dimensions[2], dimensions[3], dimensions[4],
				dimensions[5]);

	}
	
	@Override public RoundRectangle2D.Double getShape(Graphics2D g) {
		double[] dimensions = getNodeDimensions(g);
		dimensions[0] = getTemporalPosition().getX();
		dimensions[1] = getTemporalPosition().getY();
		return new RoundRectangle2D.Double(dimensions[0], dimensions[1], dimensions[2], dimensions[3], dimensions[4],
		                                   dimensions[5]);
	}
	
	/**
	 * Returns the point where the segment cuts with the border of the node.
	 * Copied
	 *
	 * @param segment segment that cuts the border of the node.
	 * @return the point where the segments cuts the border or null if it
	 * doesn't.
	 */
	@Override public Point2D.Double getCutPoint(Segment segment, Graphics2D g) {
		RoundRectangle2D.Double dimensions = getShape(g);
		double radius = dimensions.getArcWidth() / 2;
		double rectangleWidth = dimensions.getWidth() - dimensions.getArcWidth();
		double rectangleHeight = dimensions.getHeight() - dimensions.getArcHeight();
		Point2D.Double point1 = new Point2D.Double(dimensions.getX() + radius, dimensions.getY());
		Point2D.Double point2 = new Point2D.Double(point1.getX() + rectangleWidth, point1.getY());
		Point2D.Double point3 = new Point2D.Double(point2.getX() + radius, point2.getY() + radius);
		Point2D.Double point4 = new Point2D.Double(point3.getX(), point3.getY() + rectangleHeight);
		Point2D.Double point5 = new Point2D.Double(point2.getX(), point4.getY() + radius);
		Point2D.Double point6 = new Point2D.Double(point1.getX(), point5.getY());
		Point2D.Double point7 = new Point2D.Double(dimensions.getX(), point4.getY());
		Point2D.Double point8 = new Point2D.Double(dimensions.getX(), point3.getY());
		Point2D.Double circleULCenter = new Point2D.Double(point1.getX(), point8.getY());
		Point2D.Double circleURCenter = new Point2D.Double(point2.getX(), point3.getY());
		Point2D.Double circleDLCenter = new Point2D.Double(point6.getX(), point7.getY());
		Point2D.Double circleDRCenter = new Point2D.Double(point5.getX(), point4.getY());
		
		// try to find the cut point in the upper horizontal segment of the
		// round rectangle
		Point2D.Double point = segment.cutPoint(new Segment(point1, point2));
		if (point != null) {
			return point;
		}
		// try to find the cut point in the right vertical segment of the round
		// rectangle
		point = segment.cutPoint(new Segment(point3, point4));
		if (point != null) {
			return point;
		}
		// try to find the cut point in the lower horizontal segment of the
		// round rectangle
		point = segment.cutPoint(new Segment(point5, point6));
		if (point != null) {
			return point;
		}
		// try to find the cut point in the left vertical segment of the round
		// rectangle
		point = segment.cutPoint(new Segment(point7, point8));
		if (point != null) {
			return point;
		}
		// try to find the cut point in the upper left corner of the round
		// rectangle
		Point2D.Double[] points = segment.cutPoint(circleULCenter, radius);
		if (points != null) {
			for (int i = 0; i < points.length; i++) {
				if ((points[i].getX() < circleULCenter.getX()) && (points[i].getY() < circleULCenter.getY())) {
					return points[i];
				}
			}
		}
		// try to find the cut point in the upper right corner of the round
		// rectangle
		points = segment.cutPoint(circleURCenter, radius);
		if (points != null) {
			for (int i = 0; i < points.length; i++) {
				if ((points[i].getX() > circleURCenter.getX()) && (points[i].getY() < circleURCenter.getY())) {
					return points[i];
				}
			}
		}
		// try to find the cut point in the lower right corner of the round
		// rectangle
		points = segment.cutPoint(circleDRCenter, radius);
		if (points != null) {
			for (int i = 0; i < points.length; i++) {
				if ((points[i].getX() > circleDRCenter.getX()) && (points[i].getY() > circleDRCenter.getY())) {
					return points[i];
				}
			}
		}
		// try to find the cut point in the lower left corner of the round
		// rectangle
		points = segment.cutPoint(circleDLCenter, radius);
		if (points != null) {
			for (int i = 0; i < points.length; i++) {
				if ((points[i].getX() < circleDLCenter.getX()) && (points[i].getY() > circleDLCenter.getY())) {
					return points[i];
				}
			}
		}
		
		return point;
	}

	/**
	 * Paints the visual node into the graphics object as a rounded rectangle.
	 *
	 * @param g graphics object where paint the node.
	 */
	@Override public void paint(Graphics2D g) {
//TODO Inference mode? Take a look to Chance Nodes
		String text = getNodeString();
		double textHeight = getHeight(text, g);
		double textWidth = getWidth(text, g);
		Shape shape = getShape(g);
		double[] dimensions = getNodeDimensions(g);


// If it is Non Terminal Node
		if (node.getPurpose().equals(PurposeType.TERMINAL_EVENT.getName())){
			g.setPaint(GUIColors.Network.EventNode.BACKGROUND_TERMINAL.getColor());
		} else if (node.getPurpose().equals(PurposeType.INITIAL_EVENT.getName())) {
			g.setPaint(GUIColors.Network.EventNode.BACKGROUND_INITIAL.getColor());
		}else {
			g.setPaint(GUIColors.Network.EventNode.BACKGROUND.getColor());
		}

		g.fill(shape);
		g.setPaint(GUIColors.Network.EventNode.FOREGROUND.getColor());

		if (node.isAlwaysObserved()) {
			g.setPaint(GUIColors.Network.ALWAYS_OBSERVED.getColor());
			g.setStroke((isSelected()) ? OBSERVED_WIDE_STROKE : OBSERVED_NORMAL_STROKE);
		} else if (node.isInput()) {
			g.setPaint(GUIColors.Network.EventNode.FOREGROUND.getColor());
			g.setStroke((isSelected()) ? WIDE_DASHED_STROKE : NORMAL_DASHED_STROKE);
		} else {
			g.setPaint(GUIColors.Network.EventNode.FOREGROUND.getColor());
			g.setStroke((isSelected()) ? WIDE_STROKE : NORMAL_STROKE);
		}

		g.draw(shape);
		g.setFont(FONT_HELVETICA);
		g.setPaint(GUIColors.Network.EventNode.TEXT.getColor());

		if (isExpanded()) {
			text = adjustText(text, dimensions[2], 3, FONT_HELVETICA, g);
			textWidth = getWidth(text, g);
		}
		FontMetrics fontMetrics = new JPanel().getFontMetrics(FONT_HELVETICA);
		double textPosX = shape.getBounds2D().getCenterX() - fontMetrics.stringWidth(text) / 2;
		double textPosY = shape.getBounds2D().getY() + (textHeight);
		
		g.drawString(text, (float) textPosX, (float) textPosY);
		if (isExpanded()) {
			var innerBoxGraphics = (Graphics2D) g.create();
			innerBoxGraphics.translate(shape.getBounds2D().getX() + InnerBox.INTERNAL_MARGIN,
			                           shape.getBounds2D().getY()
											   + InnerBox.INTERNAL_MARGIN
											   + fontMetrics.getHeight());
			innerBox.paint(innerBoxGraphics);
		}

	}

	@Override public void update(int numCases) {
		//TODO This innerbox is for Chance nodes. Check what do Event nodes need.
		innerBox = new FSVariableBox(this);
		super.update(numCases);
	}
}
