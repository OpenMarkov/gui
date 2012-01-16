/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.graphic;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import org.openmarkov.core.model.graph.Link;
import org.apache.log4j.Logger;


/**
 * This class is the visual representation of a link.
 * 
 * @author jmendoza
 * @version 1.0
 */
public class VisualLink extends VisualElement {
	
	/**
	 * Width of the top of arrow.
	 */
	private static final double WIDTH_TOP_ARROW = 8;

	/**
	 * Height of the top of arrow.
	 */
	private static final double HEIGHT_TOP_ARROW = 12;

	/**
	 * This constant contains the value of the units that the line must
	 * increment its width to be selected with the mouse.
	 */
	private static final double WIDTH_LINE_TO_SELECT = 2;

	/**
	 * Color of lines.
	 */
	private static final Color FOREGROUND_COLOR = Color.DARK_GRAY;

	/**
	 * Start point.
	 */
	private Point2D.Double startPoint = null;

	/**
	 * End point.
	 */
	private Point2D.Double endPoint = null;
	
	/**
	 * Object that has the information (included visual information) of the
	 * destination node.
	 */
	private VisualNode destination = null;

	/**
	 * Object that has the information (included visual information) of the
	 * source node.
	 */
	private VisualNode source = null;

	/**
	 * Object that has the link information.
	 */
	private Link link = null;
	
	private Logger logger;

	/**
	 * Creates a new visual link from a link.
	 * 
	 * @param newLink
	 *            object that has the information of the link.
	 * @param newSource
	 *            source node.
	 * @param newDestination
	 *            destination node.
	 */
	public VisualLink(Link newLink, VisualNode newSource, VisualNode newDestination) {

		link = newLink;
		source = newSource;
		destination = newDestination;
		startPoint = newSource.getPosition();
		endPoint = newDestination.getPosition();
		this.logger = Logger.getLogger(VisualLink.class);

	}

	/**
	 * Creates a new visual link from the two points that define the start and the end of the arrow.
	 * 
	 * @param newStartPoint
	 *            the starting point of the arrow.
	 * @param newEndPoint
	 *            the ending point of the arrow.
	 */
	public VisualLink(Point2D.Double newStartPoint, Point2D.Double newEndPoint) {

		startPoint = newStartPoint;
		endPoint = newEndPoint;
		this.logger = Logger.getLogger(VisualLink.class);

	}
	
	
	/**
	 * Returns the source node of the link.
	 * 
	 * @return the source node of the link.
	 */
	public VisualNode getSourceNode() {

		return source;

	}

	/**
	 * Returns the destination node of the link.
	 * 
	 * @return the destination node of the link.
	 */
	public VisualNode getDestinationNode() {

		return destination;

	}

	/**
	 * Sets the destination node of the link.
	 * 
	 * @param node
	 *            the destination node of the link.
	 */
	public void setDestinationNode(VisualNode node) {
		destination = node;
	}
	
	/**
	 * Returns the link associated with the visual link.
	 * 
	 * @return information of the link.
	 */
	public Link getLink() {

		return link;

	}

	/**
	 * Sets the starting point of the arrow.
	 * 
	 * @param point
	 *            new starting point.
	 */
	public void setStartPoint(Point2D.Double point) {

		startPoint = point;

	}

	/**
	 * Sets the ending point of the arrow.
	 * 
	 * @param point
	 *            new ending point.
	 */
	public void setEndPoint(Point2D.Double point) {

		endPoint = point;

	}

	/**
	 * Returns the shape of the arrow so that it can be selected with the mouse.
	 * 
	 * @return shape of the arrow.
	 */
	@Override
	public Shape getShape(Graphics2D g) {

		GeneralPath polygon = null;
		int index = 0;
		int length = 0;
		Point2D.Double[] allPoints = null;
		Point2D.Double[] points = null;		

		Point2D.Double pStart = null;
		Point2D.Double pEnd = null;

		if ((source != null) && (destination != null)) {	
			/*pStart = new Point2D.Double(source.getProbNode().getNode().getCoordinateX(),
										source.getProbNode().getNode().getCoordinateY());
			pEnd = new Point2D.Double(destination.getProbNode().getNode().getCoordinateX(), 
										destination.getProbNode().getNode().getCoordinateY());
										*/
			pStart = new Point2D.Double(source.getTemporalPosition().getX(),
					source.getTemporalPosition().getY());
			pEnd = new Point2D.Double(destination.getTemporalPosition().getX(), 
					destination.getTemporalPosition().getY());
		}

		if ((pStart != null) && (pEnd != null)) {
			allPoints = calculatePointsOfArrow(pStart, pEnd);
			points = new Point2D.Double[8];
			points[0] = allPoints[0];
			points[1] = allPoints[1];
			points[2] = allPoints[2];
			points[3] = allPoints[6];
			points[4] = allPoints[8];
			points[5] = allPoints[4];
			points[6] = allPoints[5];
			points[7] = allPoints[0];
			polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, points.length);
			polygon.moveTo((float) points[0].getX(), (float) points[0].getY());
			length = points.length;
			for (index = 1; index < length; index++) {
				polygon.lineTo(
					(float) points[index].getX(), (float) points[index].getY());
			}
			polygon.closePath();
		} else {
			polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 0);
		}

		return polygon;

	}
	
	/**
	 * Returns the shape of the arrow as it must be painted.
	 * 
	 * @return shape of the arrow.
	 */
	private Shape getShapeToPaint(Point2D.Double start, Point2D.Double end) {

		GeneralPath polygon = null;
		int index = 0;
		int length = 0;
		Point2D.Double[] allPoints =
			calculatePointsOfArrow(start, end);
		Point2D.Double[] points = new Point2D.Double[7];

		points[0] = allPoints[0];
		points[1] = allPoints[1];
		points[2] = allPoints[3];
		points[3] = allPoints[7];
		points[4] = allPoints[3];
		points[5] = allPoints[5];
		points[6] = allPoints[0];
		polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, points.length);
		polygon.moveTo((float) points[0].getX(), (float) points[0].getY());
		length = points.length;
		for (index = 1; index < length; index++) {
			polygon.lineTo((float) points[index].getX(), (float) points[index]
				.getY());
		}
		polygon.closePath();

		return polygon;

	}
	
	/**
	 * Returns the line to be painted for undirected links.
	 * 
	 * @return shape of the line.
	 */
	private Shape getLineToPaint(Point2D.Double start, Point2D.Double end) {

		GeneralPath polygon = null;
		int index = 0;
		int length = 0;

		Point2D.Double[] points = new Point2D.Double[2];

		points[0] = start;
		points[1] = end;

		polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, points.length);
		polygon.moveTo((float) points[0].getX(), (float) points[0].getY());
		length = points.length;
		for (index = 1; index < length; index++) {
			polygon.lineTo((float) points[index].getX(), (float) points[index]
				.getY());
		}
		polygon.closePath();

		return polygon;
	}

	
	/**
	 * Calculates the nine points of the line with top of arrow. /** Calculates
	 * the nine points of the line with top of arrow.
	 * 
	 * <pre>
	 * 0</pre>
	 * <pre>
	 *         * *
	 * </pre>
	 * <pre>
	 *        *   *
	 * </pre>
	 * <pre>
	 *       *     *
	 * </pre>
	 * <pre>
	 *      *       *
	 * </pre>
	 * <pre>
	 *     *         *
	 * </pre>
	 * <pre>
	 *    *           *
	 * </pre>
	 * <pre>
	 *   *             *
	 * </pre>
	 * <pre>
	 *  1****2**3**4****5
	 * </pre>
	 * <pre>
	 *       *     *
	 * </pre>
	 * <pre>
	 *       *     *
	 * </pre>
	 * <pre>
	 *       *     *
	 * </pre>
	 * <pre>
	 *       *     *
	 * </pre>
	 * <pre>
	 *       *     *
	 * </pre>
	 * <pre>
	 *       *     *
	 * </pre>
	 * <pre>
	 *       *     *
	 * </pre>
	 * <pre>
	 *       *     *
	 * </pre>
	 * <pre>
	 *       6**7**8
	 * </pre>
	 * 
	 * @param start
	 *            start of the line.
	 * @param end
	 *            end of the line.
	 * @return return an array that contains the coordinates of the nine points.
	 */
	private static Point2D.Double[] calculatePointsOfArrow(Point2D.Double start, Point2D.Double end) {

		double tx = start.getX();
		double ty = start.getY();
		double angle = Math.atan((end.getY() - ty) / (end.getX() - tx));
		Point2D.Double[] points = new Point2D.Double[9];
		double incrHeight = 0;
		double halfWidth = WIDTH_TOP_ARROW / 2;
		int index = 0;
		int length = 0;
		AffineTransform transformation2D = new AffineTransform();

		transformation2D.rotate(-angle);
		transformation2D.translate(-tx, -ty);
		points[0] = new Point2D.Double();
		transformation2D.transform(end, points[0]);
		incrHeight =
			(points[0].getX() >= 0) ? HEIGHT_TOP_ARROW : -HEIGHT_TOP_ARROW;
		points[1] =
			new Point2D.Double(points[0].getX() - incrHeight, points[0].getY()
				- halfWidth);
		points[3] = new Point2D.Double(points[1].getX(), points[0].getY());
		points[2] =
			new Point2D.Double(points[3].getX(), points[3].getY()
				- WIDTH_LINE_TO_SELECT);
		points[4] =
			new Point2D.Double(points[3].getX(), points[3].getY()
				+ WIDTH_LINE_TO_SELECT);
		points[5] =
			new Point2D.Double(points[1].getX(), points[0].getY() + halfWidth);
		points[7] = new Point2D.Double(0, 0);
		points[6] =
			new Point2D.Double(points[7].getX(), points[7].getY()
				- WIDTH_LINE_TO_SELECT);
		points[8] =
			new Point2D.Double(points[7].getX(), points[7].getY()
				+ WIDTH_LINE_TO_SELECT);
		try {
			transformation2D = transformation2D.createInverse();
		} catch (NoninvertibleTransformException e) {
			//ExceptionsHandler.handleException(e, null, true);
			Logger.getLogger(VisualLink.class).info(e);
		}
		length = points.length;
		for (index = 0; index < length; index++) {
			transformation2D.transform(points[index], points[index]);
		}

		return points;

	}

	
	/**
	 * Paints the arrow into the graphics object.
	 * 
	 * @param g
	 *            graphics object where paint the link.
	 */

	public void paintArrow(Graphics2D g, Point2D.Double start, Point2D.Double end) {
		Shape shape = null;
		if ((start != null) && (end != null)) {
			if ((Math.abs(start.getX() - end.getX()) > 0.01) || (Math.abs(start.getY() - end.getY()) > 0.01)) {
				if (isSelected()) {
					g.setStroke(WIDE_STROKE);
				} else {
					g.setStroke(NORMAL_STROKE);
				}
				g.setPaint(FOREGROUND_COLOR);
				shape = getShapeToPaint(start, end);
				g.fill(shape);
				g.draw(shape);
			}
		}
	}
	
	/**
	 * Paints the line into the graphics object.
	 * 
	 * @param g
	 *            graphics object where paint the link.
	 */

	public void paintLine(Graphics2D g, Point2D.Double start, Point2D.Double end) {
		Shape shape = null;
		if ((start != null) && (end != null)) {
			if ((Math.abs(start.getX() - end.getX()) > 0.01) || (Math.abs(start.getY() - end.getY()) > 0.01)) {
				if (isSelected()) {
					g.setStroke(WIDE_STROKE);
				} else {
					g.setStroke(NORMAL_STROKE);
				}
				g.setPaint(FOREGROUND_COLOR);
				shape = getLineToPaint(start, end);
				g.draw(shape);
			}
		}
	}
	
	/**
	 * Paints the visual link into the graphics object.
	 * 
	 * @param g
	 *            graphics object where paint the link.
	 */
	@Override
	public void paint(Graphics2D g) {

		if ((source != null) && (destination != null)) {
			//Paint the final arrow when the user releases the button of the mouse 
			Segment line;
			Point2D.Double sPoint;
			Point2D.Double ePoint;
	
			try {
				/*line =
					new Segment(new Point2D.Double(
							source.getProbNode().getNode().getCoordinateX(),
							source.getProbNode().getNode().getCoordinateY()), new Point2D.Double(
							destination.getProbNode().getNode().getCoordinateX(), 
							destination.getProbNode().getNode().getCoordinateY()));
							*/
				line =
					new Segment(new Point2D.Double(
							source.getTemporalPosition().getX(),
							source.getTemporalPosition().getY()), new Point2D.Double(
							destination.getTemporalPosition().getX(), 
							destination.getTemporalPosition().getY()));
			} catch (IllegalArgumentException e) {
	
				return;
			}
			sPoint = source.cutPoint(line, g);
			ePoint = destination.cutPoint(line, g);
			if ((sPoint != null) && (ePoint != null) && (link.isDirected())) {
				paintArrow(g, sPoint, ePoint);
			}
			else if ((sPoint != null) && (ePoint != null) && 
					(!link.isDirected())) {
				paintLine(g, sPoint, ePoint);
			}
		} else if (link.isDirected()){
			//Paint the arrow while the user has not released the button of the mouse
			paintArrow(g, startPoint, endPoint);			
		}
		else{
			paintLine(g, startPoint, endPoint);
		}
	}

}
