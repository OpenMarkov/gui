/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.window;

import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.exception.UnreacheableException;
import org.openmarkov.core.io.format.annotation.NoReaderForFileException;
import org.openmarkov.core.localize.StringDatabase;
import org.openmarkov.gui.component.FrameMirror;
import org.openmarkov.gui.configuration.LocalPreference;
import org.openmarkov.gui.configuration.LocalPreferences;
import org.openmarkov.gui.dialog.SplashScreenLoader;
import org.openmarkov.gui.dialog.common.WindowDimensions;
import org.openmarkov.gui.exception.CorruptNetworkFile;
import org.openmarkov.gui.loader.element.OpenMarkovLogoIcon;
import org.xml.sax.SAXException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * This class constructs the main GUI in a frame with a splash screen during the
 * loading and reading configuration from external preferences.
 *
 * @author mendoza
 * @author jlgozalo
 * @version 1.3 jlgozalo - replacing System.err with JOptionPane
 */
@SuppressWarnings("serial") public class MainGUI extends JFrame {
    
    public static final MainGUI INSTANCE = new MainGUI();
    
    public final MainPanel mainPanel;
    
    private final FrameMirror frameMirror;
    
    /**
     * Launch the MainGUIInit runnable process
     */
    private MainGUI() {
        SplashScreenLoader splash = new SplashScreenLoader();
        configureUI();
        splash.splashScreenInit();
        splash.getSplash().setProgress("Loading preferences", 0);
        doReadPreferences();
        splash.getSplash().setProgress("Loading resources", 25);
        // TODO here will be the plug-in loaders in future
        setIconImage(OpenMarkovLogoIcon.getUniqueInstance().getOpenMarkovLogoIconImage16());
        StringDatabase.getUniqueInstance().getAllBundles();
        splash.getSplash().setProgress("Loading interface", 75);
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        setSize(screenPortionSize(screenInsets));
        setLocation(screenInsets.left, screenInsets.top);
        this.mainPanel = new MainPanel(this);
        setContentPane(this.mainPanel);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("OpenMarkov");
        setName("MainGUI");
        this.frameMirror = new FrameMirror(this);
        if (LocalPreferences.LATEST_MAIN_GUI_DIMENSIONS.isSet()) {
            var dimensions = LocalPreferences.LATEST_MAIN_GUI_DIMENSIONS.get();
            setLocation(dimensions.location());
            setSize(dimensions.size());
            setExtendedState(dimensions.extendedState());
        }
        addComponentListener(new ComponentListener() {
            
            @Override public void componentResized(ComponentEvent e) {
                updatePreferenceDimensions();
            }
            
            @Override public void componentMoved(ComponentEvent e) {
                updatePreferenceDimensions();
            }
            
            @Override public void componentShown(ComponentEvent e) {
            
            }
            
            @Override public void componentHidden(ComponentEvent e) {
            
            }
        });
        
        
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (!LocalPreferences.PRINT_COMPONENTS_OF_MOUSE_LOCATION.get()) return;
            if (!(event instanceof MouseEvent mouseEvent)) return;
            var element = SwingUtilities.getDeepestComponentAt(MainGUI.this, mouseEvent.getX(), mouseEvent.getY());
            System.out.println("- Mouse locator -" + element);
            String prefix = "-";
            while (element != null) {
                System.out.println(prefix + " " + element);
                element = element.getParent();
                prefix += "-";
            }
        }, AWTEvent.MOUSE_MOTION_EVENT_MASK);
        splash.getSplash().setProgress("Completed", 100);
        // loading the application
        splash.splashScreenDestroy();
    }
    
    private void updatePreferenceDimensions() {
        var isMaximized = getExtendedState() == Frame.MAXIMIZED_BOTH;
        var originalDimensions = LocalPreferences.LATEST_MAIN_GUI_DIMENSIONS.get();
        Point location = isMaximized ? originalDimensions.location() : getLocation();
        Dimension size = isMaximized ? originalDimensions.size() : getSize();
        int extendedState = getExtendedState();
        WindowDimensions newDimensions = new WindowDimensions(location, size, extendedState);
        LocalPreferences.LATEST_MAIN_GUI_DIMENSIONS.set(newDimensions);
    }
    
    //Conditionally disabled
    public void freeze() {
        if (true) return;
        this.frameMirror.freeze();
    }
    
    //Conditionally disabled
    public void unfreeze() {
        if (true) return;
        this.frameMirror.unfreeze();
    }
    
    /**
     * This method sets and configures the UI manager.
     */
    private static void configureUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            throw new UnreacheableException(e);
        }
        /*
         * The next line is used to avoid that disabled menuitems are
         * highlighted.
         */
        
        UIManager.put("MenuItem.disabledAreNavigable", Boolean.FALSE);
        
    }
    
    /**
     * read the {@code OpenMarkovPreferences} configuration, and set the
     * LastConnection preference to current Time
     */
    private static void doReadPreferences() {
        LocalPreferences.getAllPreferences().forEach(LocalPreference::initialize);
    }
    
    /**
     * This method returns a dimension that represents the 3/4 size of the
     * screen.
     *
     * @return new dimensions of the window.
     */
    private static Dimension screenPortionSize(Insets screenInsets) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screen.width - screenInsets.right - screenInsets.left;
        int height = screen.height - screenInsets.top - screenInsets.bottom;
        return new Dimension(width, height);
    }
    
    /**
     * Opens net from file
     *
     * @param fileName
     */
    public void openNetwork(String fileName) throws ParserException, IOException, SAXException, NoReaderForFileException, CorruptNetworkFile {
        mainPanel.openNetwork(fileName);
    }
    
}
