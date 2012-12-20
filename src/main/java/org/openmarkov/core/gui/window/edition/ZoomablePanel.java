package org.openmarkov.core.gui.window.edition;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;

import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public abstract class ZoomablePanel extends JScrollPane
{

    /**
     * Object to convert coordinates of the screen to the panel and vice versa.
     */
    protected Zoom zoom;

    /**
     * Maximum width of the panel.
     */
    private double maxWidth = Toolkit.getDefaultToolkit().getScreenSize()
            .getWidth() * 20;

    /**
     * Maximum height of the panel.
     */
    private double maxHeight = Toolkit.getDefaultToolkit().getScreenSize()
            .getHeight() * 20;
    
    public ZoomablePanel()
    {
        zoom = new Zoom();
    }
    
    /**
     * Return the maximum height of the panel till now.
     * 
     * @return maximum height of the panel till now.
     */
    double getMaxHeight() {

        return zoom.panelToScreen(maxHeight);

    }

    /**
     * Return the maximum width of the panel till now.
     * 
     * @return maximum width of the panel till now.
     */
    double getMaxWidth() {

        return zoom.panelToScreen(maxWidth);
    }
    
    /**
     * Returns the value of the zoom.
     * 
     * @return actual value of zoom.
     */
    public double getZoom() {

        return zoom.getZoom();

    }
    
    /**
     * Changes the value of the zoom.
     * 
     * @param value
     *            new zoom.
     */
    public void setZoom(double value) {

        Dimension newDimension = null;
        Double dd = new Double(zoom.getZoom());
        Double dd1 = new Double(value);
        if (dd.compareTo(dd1) != 0) { // jlgozalo. 24/08 fix condition to !=
            zoom.setZoom(value);
            newDimension = new Dimension((int) Math.round(getMaxWidth()),
                    (int) Math.round(getMaxHeight()));
            setPreferredSize(newDimension);
            setSize(newDimension);
            adjustPanelDimension();
            repaint();
        }
    }
    
    /**
     * If the dimensions of the network are greater than the dimensions of the
     * panel, changes the dimensions of the panel in order to accommodate the
     * whole network.
     */
    public void adjustPanelDimension() {

        double[] bounds = getBounds((Graphics2D) getGraphics());
        Dimension newDimension = null;
        maxWidth = Math.max(maxWidth, bounds[1]);
        maxHeight = Math.max(maxHeight, bounds[3]);
        newDimension = new Dimension((int) Math.round(getMaxWidth()),
                (int) Math.round(getMaxHeight()));
        setPreferredSize(newDimension);
        setSize(newDimension);
    }
    
    protected abstract double[] getBounds (Graphics2D graphics);
}
