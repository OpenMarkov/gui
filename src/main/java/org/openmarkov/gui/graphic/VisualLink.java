/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.graphic;

import org.openmarkov.core.localize.ClassLocalizable;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.Point2D;
import org.openmarkov.gui.configuration.GUIColors;

import java.awt.*;

/**
 * This class is the visual representation of a link.
 *
 * @author jmendoza
 * @version 1.0
 */
public non-sealed class VisualLink extends VisualArrow implements ClassLocalizable {
    
    /**
     * Object that has the information (included visual information) of the
     * destination node.
     */
    private VisualNode destination;
    
    /**
     * Object that has the information (included visual information) of the
     * source node.
     */
    private final VisualNode source;
    
    /**
     * Object that has the link information.
     */
    private final Link<Node> link;
    
    /**
     * Creates a new visual link from a link.
     *
     * @param newLink        object that has the information of the link.
     * @param newSource      source node.
     * @param newDestination destination node.
     */
    public VisualLink(Link<Node> newLink, VisualNode newSource, VisualNode newDestination) {
        super(newSource.getPosition(), newDestination.getPosition(), newLink.isDirected());
        
        link = newLink;
        source = newSource;
        destination = newDestination;
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
     * @param node the destination node of the link.
     */
    public void setDestinationNode(VisualNode node) {
        destination = node;
    }
    
    /**
     * Returns the link associated with the visual link.
     *
     * @return information of the link.
     */
    public Link<Node> getLink() {
        
        return link;
        
    }
    
    /**
     * Returns the shape of the arrow so that it can be selected with the mouse.
     *
     * @return shape of the arrow.
     */
    @Override public Shape getCenteredShape(Graphics2D g) {
        Segment line = new Segment(
                new Point2D.Double(source.getTemporalPosition().getX(), source.getTemporalPosition().getY()),
                new Point2D.Double(destination.getTemporalPosition().getX(),
                                   destination.getTemporalPosition().getY()));
        setStartPoint(source.getCutPoint(line, g));
        setEndPoint(destination.getCutPoint(line, g));
        return super.getCenteredShape(g);
    }
    
    @Override public Shape getShape(Graphics2D g) {
        Shape sourceShape = source.getShape(g);
        Shape destinationShape = destination.getShape(g);
        
        Segment line = new Segment(
                new Point2D.Double(sourceShape.getBounds2D().getCenterX(), sourceShape.getBounds2D().getCenterY()),
                new Point2D.Double(destinationShape.getBounds2D().getCenterX(), destinationShape.getBounds2D().getCenterY()));
        setStartPoint(source.getCutPoint(line, g));
        setEndPoint(destination.getCutPoint(line, g));
        return super.getShape(g);
    }
    
    /**
     * Paints the visual link into the graphics object.
     *
     * @param g graphics object where paint the link.
     */
    @Override public void paint(Graphics2D g) {
        Shape sourceShape = source.getShape(g);
        Shape destinationShape = destination.getShape(g);
        
        // Paint the final arrow when the user releases the button of the
        // mouse
        Segment line;
        try {
            line = new Segment(
                    new Point2D.Double(sourceShape.getBounds2D().getCenterX(), sourceShape.getBounds2D().getCenterY()),
                    new Point2D.Double(destinationShape.getBounds2D().getCenterX(), destinationShape.getBounds2D().getCenterY()));

        } catch (IllegalArgumentException e) {
            return;
        }
        if (link.hasRevealingConditions()) {
            setLinkColor(GUIColors.Network.REVELATION_ARC_VARIABLE);
        } else {
            setLinkColor(GUIColors.Network.LINK);
        }
        
        boolean hasAbsoluteLinkRestriction = link.hasTotalRestriction();
        setDoubleStriped(hasAbsoluteLinkRestriction);
        setSingleStriped(link.hasRestrictions() && !hasAbsoluteLinkRestriction);
        Point2D.Double sourceCutPoint = source.getCutPoint(line, g);
        Point2D.Double cutPoint = destination.getCutPoint(line, g);
        setStartPoint(sourceCutPoint);
        setEndPoint(cutPoint);
        
        super.paint(g);
    }
    
    @Override public String toString() {
        return this.localize();
    }
    
}
