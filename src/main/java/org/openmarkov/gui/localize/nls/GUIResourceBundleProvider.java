package org.openmarkov.gui.localize.nls;

import org.jetbrains.annotations.NotNull;
import org.openmarkov.annotation_processing.localization_bindings.BindXML;
import org.openmarkov.gui.localize.spi.LocalizeResourcesProvider;

//@BindXML(filePath = "gui/localize")
public class GUIResourceBundleProvider implements LocalizeResourcesProvider {
    
    @Override
    public @NotNull String getRootOfResources() {
        return "/gui";
    }
    
    //This is only set here so it will be compiled to verify the 'BindXML' works.
    /*
    private void doNotUse(){
        Localize.Add.Text.Label.stringify();
    }*/
    
}
