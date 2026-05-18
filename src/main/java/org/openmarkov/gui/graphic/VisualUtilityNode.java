/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.graphic;

import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.Point2D;
import org.openmarkov.gui.configuration.GUIColors;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;

/**
 * This class is the visual representation of a utility node.
 *
 * @author jmendoza
 * @version 1.2 asaez - add expanded representation
 */
public non-sealed class VisualUtilityNode extends VisualNode {
    
    /**
     * Creates a new visual node from a node.
     *
     * @param node          object that has the information of the node.
     * @param visualNetwork visual network to which this visual node is associated.
     */
    public VisualUtilityNode(Node node, VisualNetwork visualNetwork) {
        super(node, visualNetwork);
        expanded = false;
        preResolutionFinding = false;
        postResolutionFinding = false;
        setTemporalPosition(new Point2D.Double(node.getCoordinateX(), node.getCoordinateY()));
        innerBox = new NumericVariableBox(this, "  EU");
    }
    
    /**
     * Returns the X-coordinate of the upper-left corner of the visual node.
     *
     * @return the X-coordinate of the upper-left corner of the visual node.
     */
    @Override public double getUpperLeftCornerX(Graphics2D g) {
        Point2D.Double[] points = getPoints(g, true);
        return points[1].getX();
    }
    
    /**
     * Returns the Y-coordinate of the upper-left corner of the visual node.
     *
     * @return the Y-coordinate of the upper-left corner of the visual node.
     */
    @Override public double getUpperLeftCornerY(Graphics2D g) {
        Point2D.Double[] points = getPoints(g, true);
        return points[1].getY();
    }
    
    /**
     * Returns the six points of the hexagon that limits the node. The order is:
     * first the most left point,
     * second the left top point,
     * third the right top point,
     * fourth the most right point,
     * fifth the right bottom point,
     * sixth the left bottom points.
     *
     * @param g        graphic object where the node can be painted.
     * @param centered
     *
     * @return an array that contains the six (or four) points of the hexagon.
     */
    private Point2D.Double[] getPoints(Graphics2D g, boolean centered) {
        String text = getNodeString();
        double textHeight = getHeight(text, g);
        double textWidth = getWidth(text, g);
        double posX = getTemporalPosition().getX();
        double posY = getTemporalPosition().getY();
        
        double hexagonWidth;
        double hexagonHeight;
        
        if (isExpanded()) {
            hexagonHeight = innerBox.getInnerBoxHeight(g) + textHeight + 2 * VERTICAL_SPACE_TO_TEXT
                    + NODE_EXPANDED_HEIGHT_MARGIN * 2;
            hexagonWidth = NODE_EXPANDED_WIDTH + 8.0;
        } else {
            hexagonHeight = textHeight + 2 * VERTICAL_SPACE_TO_TEXT;
            if (textWidth < textHeight) {
                hexagonWidth = DEFAULT_NODE_CONTRACTED_WIDTH;
            } else {
                hexagonWidth = textWidth + 2 * HORIZONTAL_SPACE_TO_TEXT;
            }
        }
        
        double triangleWidth = 8.0;
        
        Point2D.Double[] points = new Point2D.Double[6];
        
        points[0] = new Point2D.Double(posX - hexagonWidth / 2, posY);
        points[3] = new Point2D.Double(posX + hexagonWidth / 2, posY);
        points[1] = new Point2D.Double(points[0].getX() + triangleWidth, posY - (hexagonHeight / 2));
        points[2] = new Point2D.Double(points[3].getX() - triangleWidth, points[1].getY());
        points[4] = new Point2D.Double(points[2].getX(), posY + (hexagonHeight / 2));
        points[5] = new Point2D.Double(points[1].getX(), points[4].getY());
        if (!centered) {
            for (int i = 0; i < points.length; i++) {
                var centeredPoint = points[i];
                points[i] = new Point2D.Double(
                        centeredPoint.getX() + hexagonWidth / 2,
                        centeredPoint.getY() + hexagonHeight / 2);
            }
        }
        return points;
        
    }
    
    /**
     * Returns the shape of the node.
     *
     * @return shape of the node.
     */
    @Override public Shape getCenteredShape(Graphics2D g) {
        Point2D.Double[] points = getPoints(g, true);
        int length = points.length;
        GeneralPath polygon = new GeneralPath(Path2D.WIND_EVEN_ODD, length);
        polygon.moveTo(points[0].getX(), points[0].getY());
        for (int i = 1; i < length; i++) {
            polygon.lineTo(points[i].getX(), points[i].getY());
        }
        polygon.closePath();
        return polygon;
    }
    
    @Override public Shape getShape(Graphics2D g) {
        Point2D.Double[] points = getPoints(g, false);
        int length = points.length;
        GeneralPath polygon = new GeneralPath(Path2D.WIND_EVEN_ODD, length);
        polygon.moveTo(points[0].getX(), points[0].getY());
        for (int i = 1; i < length; i++) {
            polygon.lineTo(points[i].getX(), points[i].getY());
        }
        polygon.closePath();
        return polygon;
    }
    
    /**
     * Returns the point where the segment cuts with the border of the node.
     *
     * @param segment segment that cuts the border of the node.
     *
     * @return the point where the segments cuts the border or null if it
     * doesn't.
     */
    @Override public Point2D.Double getCutPoint(Segment segment, Graphics2D g) {
        
        Point2D.Double[] points = getPoints(g, false);
        int length = points.length;
        Point2D.Double result = null;
        int index1 = 0;
        int index2 = 1;
        int iteration = 0;
        
        while ((result == null) && (iteration < length)) {
            result = segment.cutPoint(new Segment(points[index1], points[index2]));
            index1 = (index1 + 1) % length;
            index2 = (index2 + 1) % length;
            iteration++;
            
        }
        
        return result;
        
    }
    
    /**
     * Paints the visual node into the graphics object as a hexagon.
     *
     * @param g graphics object where paint the node.
     */
    @Override public void paint(Graphics2D g) {
        
        String text = getNodeString();
        double textHeight = getHeight(text, g);
        double textWidth = getWidth(text, g);
        Shape shape = getShape(g);
        Point2D.Double[] points = getPoints(g, true);
        
        g.setPaint(GUIColors.Network.UtilityNode.BACKGROUND.getColor());
        g.fill(shape);
        g.setPaint(GUIColors.Network.UtilityNode.FOREGROUND.getColor());
        g.setStroke(getContourStroke());
        
        g.draw(shape);
        g.setFont(FONT_HELVETICA);
        g.setPaint(GUIColors.Network.UtilityNode.TEXT.getColor());
        
        if (isExpanded()) {
            double interiorWitdh = points[2].getX() - points[1].getX();
            text = adjustText(text, interiorWitdh, 3, FONT_HELVETICA, g);
            textWidth = getWidth(text, g);
        }
        
        FontMetrics fontMetrics = new JPanel().getFontMetrics(FONT_HELVETICA);
        double textPosX = shape.getBounds2D().getCenterX() - fontMetrics.stringWidth(text) / 2;
        double textPosY = shape.getBounds2D().getY() + (textHeight);
        
        g.drawString(text, (float) textPosX, (float) textPosY);
        if (isExpanded()) {
            var innerBoxGraphics = (Graphics2D) g.create();
            innerBoxGraphics.translate(shape.getBounds2D()
                                            .getX() + VisualUtilityNode.NODE_EXPANDED_WIDTH_MARGIN + InnerBox.INTERNAL_MARGIN,
                                       shape.getBounds2D().getY()
                                               + InnerBox.INTERNAL_MARGIN
                                               + fontMetrics.getHeight());
            innerBox.paint(innerBoxGraphics);
        }
        
    }
    
    private static final double NODE_EXPANDED_WIDTH_MARGIN = 4;
}
