/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog.treeadd;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

abstract public class IconFactory {
	
	/**
	 * @param text
	 * @param f
	 * @return
	 */
	static Icon createChanceIcon( String text, Font f ) {
		FontRenderContext fr= new FontRenderContext(null,false,false);
		TextLayout t= new TextLayout(text, f, fr );
		
		int margenH=6;
		int margenV=6;
		
		Rectangle2D r= t.getBounds();
		int width= (int) r.getWidth() + 2*(margenH+1);
		int height= (int) r.getHeight() + 2*margenV;
		BufferedImage image = new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g= (Graphics2D) image.createGraphics();
				
		int anchoOval= Math.min(height,width);

		g.setColor(new Color(255,255,200));		
		g.fillArc(0,0,anchoOval,height-1,90,180);
		g.fillArc(width-anchoOval-1,0,anchoOval,height-1,270,180);
		g.fillRect(anchoOval/2,0,width-anchoOval, height-1);
		
		g.setColor(Color.black);
		g.drawArc(0,0,anchoOval,height-1,90,180);
		g.drawArc(width-anchoOval-1,0,anchoOval,height-1,270,180);
		g.drawLine(anchoOval/2,0,width-anchoOval/2,0);
		g.drawLine(anchoOval/2,height-1,width-anchoOval/2,height-1);

		t.draw( g, margenH, height-margenV-1 );
		
		return new ImageIcon( image );
	}
	
	/**
	 * @param text
	 * @param f
	 * @return
	 */
	static Icon createDecisionIcon( String text, Font f ) {
		FontRenderContext fr= new FontRenderContext(null,false,false);
		TextLayout t= new TextLayout(text, f, fr );
		
		int margenH=6;
		int margenV=6;
		
		Rectangle2D r= t.getBounds();
		int width= (int) r.getWidth() + 2*margenH;
		int height= (int) r.getHeight() + 2*margenV;
		BufferedImage image = new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g= (Graphics2D) image.getGraphics();
		
		g.setColor(new Color(200,255,255));
		g.fillRect(0,1,width-2,height-2);
		g.setColor(Color.black);
		g.drawRect(0,1,width-2,height-2);
		
		t.draw( g, margenH, height-margenV );
		
		return new ImageIcon( image );
	}
	
	/**
	 * @param text
	 * @param f
	 * @return
	 */
	static Icon createUtilityIcon( String text, Font f ) {
		FontRenderContext fr= new FontRenderContext(null,false,false);
		TextLayout t= new TextLayout(text, f, fr );
		
		Rectangle2D r= t.getBounds();
		
		int margenH= (int) (6+r.getHeight()/2);
		int margenV=6;
		
		int width= (int) (r.getWidth() + 2*margenH);
		int height= (int) (r.getHeight() + 2*margenV);
		
		BufferedImage image = new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g= (Graphics2D) image.getGraphics();
		
		/*
		 g.setColor(new Color(0,255,255));
		 g.fillRect(0,1,width-2,height-2);
		 */
		Polygon poly= new Polygon();
		poly.addPoint(1,height/2);
		poly.addPoint(height/2,height-1);
		poly.addPoint(width-height/2,height-1);
		poly.addPoint(width-1,height/2);
		poly.addPoint(width-height/2,1);
		poly.addPoint(height/2,1);
		
		g.setColor(new Color(200,255,200));
		g.fillPolygon(poly);
		g.setColor(Color.black);
		g.drawPolygon(poly);
		t.draw( g, margenH, height-margenV );
		
		return new ImageIcon( image );
	}
}

