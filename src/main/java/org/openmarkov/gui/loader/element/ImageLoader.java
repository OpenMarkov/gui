/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.loader.element;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.MissingResourceException;

/**
 * This class is used to load icons from a folder.
 *
 * @author jlgozalo
 * @version 1.0 jlgozalo 25/08 based on IconLoader
 */
public class ImageLoader {
    
    /**
     * This method loads an image resource.
     *
     * @param imageName name of the image to load.
     * @return a reference to the image resource.
     * @throws MissingResourceException if the resource doesn't exist.
     */
    public static ImageIcon load(String imageName) throws MissingResourceException {
        return new ImageIcon(ImageLoader.class.getResource(imageName)) {
            
            @Override
            public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
                super.paintIcon(c, ImageLoader.generateGraphicsWithIconHints(g), x, y);
            }
        };
    }
    
    /**
     * This method loads an image resource.
     *
     * @param imageName name of the image to load.
     *
     * @return a reference to the image resource.
     *
     * @throws MissingResourceException if the resource doesn't exist.
     */
    public static ImageIcon load(URL location) throws MissingResourceException {
        return new ImageIcon(location) {
            
            @Override
            public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
                super.paintIcon(c, ImageLoader.generateGraphicsWithIconHints(g), x, y);
            }
        };
    }
    
    
    public static Icon of(Image image) {
        return new ImageIcon(image) {
            @Override
            public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
                super.paintIcon(c, ImageLoader.generateGraphicsWithIconHints(g), x, y);
            }
        };
    }
    
    private static Graphics2D generateGraphicsWithIconHints(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        return g2d;
    }
    
}
