import org.openmarkov.gui.localize.GUIResourceBundleProvider;

module org.openmarkov.gui {
    
    requires org.apache.commons.io;
	requires org.jdom2;
	requires org.openmarkov.core;
	requires org.openmarkov.plugin;
	requires org.openmarkov.io;
	requires org.openmarkov.inference.decompositionintosymmetricdans;
	requires org.openmarkov.inference.temporalevaluation;
	requires org.openmarkov.inference.variableelimination;
	requires org.apache.logging.log4j;
	requires org.openmarkov.inference.dlimidevaluation;
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
	exports org.openmarkov.gui.plugin;
	exports org.openmarkov.gui.util;
	exports org.openmarkov.gui.dialog.common;
	exports org.openmarkov.gui.window.edition;
	exports org.openmarkov.gui.dialog.io;
	exports org.openmarkov.gui.dialog.costeffectiveness;
	exports org.openmarkov.gui.dialog.treeadd;
	exports org.openmarkov.gui.configuration;
	exports org.openmarkov.gui.window.mdi;
	exports org.openmarkov.gui.window.dt;
	exports org.openmarkov.gui.localize.spi;
	
	uses org.openmarkov.gui.localize.spi.LocalizeResourcesProvider;
	provides org.openmarkov.gui.localize.spi.LocalizeResourcesProvider with GUIResourceBundleProvider;
	
	opens icons;
    
    
}
