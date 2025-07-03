import org.openmarkov.gui.localize.GUIResourceBundleProvider;

open module org.openmarkov.gui {
    
    requires org.apache.commons.io;
    requires org.jdom2;
    requires org.openmarkov.core;
    requires org.openmarkov.io;
    requires org.openmarkov.inference;
    requires org.apache.logging.log4j;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires org.jfree.jfreechart;
    
    //requires com.hexidec.ekit;
    requires org.openmarkov.annotation_processing;
    requires org.jetbrains.annotations;
    requires java.desktop;
    requires java.prefs;
    requires jeval;
    
    
    exports org.openmarkov.gui.action;
    exports org.openmarkov.gui.localize;
    exports org.openmarkov.gui.window;
    exports org.openmarkov.gui.dialog.inference.common;
    exports org.openmarkov.gui.loader.element;
    exports org.openmarkov.gui.toolplugin;
    exports org.openmarkov.gui.util;
    exports org.openmarkov.gui.dialog.common;
    exports org.openmarkov.gui.window.edition;
    exports org.openmarkov.gui.dialog.io;
    exports org.openmarkov.gui.dialog.costeffectiveness;
    exports org.openmarkov.gui.dialog.treeadd;
    exports org.openmarkov.gui.configuration;
    exports org.openmarkov.gui.window.mdi;
    exports org.openmarkov.gui.window.dt;
    
    uses org.openmarkov.core.localize.spi.LocalizeResourcesProvider;
    provides org.openmarkov.core.localize.spi.LocalizeResourcesProvider with GUIResourceBundleProvider;
    
}
