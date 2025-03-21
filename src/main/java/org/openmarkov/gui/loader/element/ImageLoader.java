/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.loader.element;

import org.openmarkov.gui.localize.StringDatabase;

import javax.swing.*;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.MissingResourceException;
import java.util.Objects;

/**
 * This class is used to load icons from a folder.
 *
 * @author jlgozalo
 * @version 1.0 jlgozalo 25/08 based on IconLoader
 */
public class ImageLoader {
    /**
     * Folder where icons are saved.
     */
    // TODO must be externalize in a property
    private static final String RESOURCE_IMAGES_PATH = "images/";
    
    /**
     * This method loads an image resource.
     *
     * @param imageName name of the image to load.
     * @return a reference to the image resource.
     * @throws MissingResourceException if the resource doesn't exist.
     */
    public ImageIcon load(String imageName) throws MissingResourceException {
        var resourcesDir = ImageLoader.class.getProtectionDomain().getCodeSource().getLocation();
        String resourcesDirFile = resourcesDir.getFile().substring(1);
        var imageLocation = Path.of(resourcesDirFile).resolve(RESOURCE_IMAGES_PATH).resolve(imageName);
        //URL icon = this.getClass().getClassLoader().getResource(RESOURCE_IMAGES_PATH + imageName);
        if (!imageLocation.toFile().exists()) {
            throw new MissingResourceException(
                    StringDatabase.getUniqueInstance().getString("ImageResourceNotExists.Text.Label") + " "
                            + RESOURCE_IMAGES_PATH + imageName, getClass().getName(), imageName);
        }
        return new ImageIcon(imageLocation.toString());
    }
    
    
    
}
