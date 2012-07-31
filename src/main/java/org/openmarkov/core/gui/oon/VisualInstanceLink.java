/*
 * Copyright 2011 CISIAD, UNED, Spain
 *
 * Licensed under the European Union Public Licence, version 1.1 (EUPL)
 *
 * Unless required by applicable law, this code is distributed
 * on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.oon;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;

import org.openmarkov.core.gui.graphic.VisualArrow;

public class VisualInstanceLink extends VisualArrow {
    
    /**
     * Used to paint normal lines.
     */
    protected static final BasicStroke NORMAL_INSTANCE_LINK_STROKE = new BasicStroke(3.0f);

    /**
     * Used to paint wide lines.
     */
    protected static final BasicStroke SELECTED_INSTANCE_LINK_STROKE = new BasicStroke(5.0f);    

	private VisualInstance sourceInstance;
	private VisualInstance destinationInstance;
	
	public VisualInstanceLink(VisualInstance sourceInstance, VisualInstance destinationInstance) {
		super(sourceInstance.getPosition(), destinationInstance.getPosition());
		this.sourceInstance = sourceInstance;
		this.destinationInstance = destinationInstance;
	}
	
	/**
	 * Returns the shape of the arrow so that it can be selected with the mouse.
	 * 
	 * @return shape of the arrow.
	 */
	@Override
	public Shape getShape(Graphics2D g) {

		setStartPoint(sourceInstance.getCutPoint(destinationInstance.getCenter()));
		setEndPoint(destinationInstance.getCutPoint(sourceInstance.getCenter()));
		return super.getShape(g);
	}

	/**
	 * Paints the visual link into the graphics object.
	 * 
	 * @param g
	 *            graphics object where paint the link.
	 */
	@Override
	public void paint(Graphics2D g) {
		
		setStartPoint(sourceInstance.getCutPoint(destinationInstance.getCenter()));
		setEndPoint(destinationInstance.getCutPoint(sourceInstance.getCenter()));
		
		super.paint(g);
	}	
	
    @Override
    protected Stroke getStroke ()
    {
        return (isSelected ())? WIDE_STROKE : NORMAL_STROKE;
    }
	
	

}
