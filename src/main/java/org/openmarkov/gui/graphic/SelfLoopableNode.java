package org.openmarkov.gui.graphic;

import org.openmarkov.core.model.network.Point2D;

import java.awt.Graphics2D;

/**
 * This interface is implemented when a node may have a self-loop
 * TODO - Chenck if this is necessary when extracting a superclass fo VisualChanceNode and VisualDecisionNode
 * @autor cyago
 * @version 1.0 - 05/40/2020 - For DESNets
 */
public interface SelfLoopableNode {
    /**
     * Returns the point which will be the center for a circular arrow
     * 05/04/2020 - At this time only Event and Chance Nodes may have circular arrows (self-loops)
     *
     * @param g graphics object where to paint the element.
     *
     * @return the point which will be the center for a circular arrow
     */
    public Point2D.Double getCentreArcPoint   (Graphics2D g);

}
