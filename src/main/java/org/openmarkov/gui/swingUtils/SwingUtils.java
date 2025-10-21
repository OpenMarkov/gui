package org.openmarkov.gui.swingUtils;


public class SwingUtils {
    
    public static org.openmarkov.core.model.network.Point2D.Double swing2DPointToOM2DPoint(java.awt.geom.Point2D.Double point) {
        return new org.openmarkov.core.model.network.Point2D.Double(point.x, point.y);
    }
    
    public static java.awt.geom.Point2D.Double om2DPointToSwing2DPoint(org.openmarkov.core.model.network.Point2D.Double point) {
        return new java.awt.geom.Point2D.Double(point.x, point.y);
    }
    
}
