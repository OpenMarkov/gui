package org.openmarkov.gui.localize;

import org.jetbrains.annotations.NotNull;
import org.openmarkov.annotation_processing.localization_bindings.BindXML;
import org.openmarkov.gui.localize.spi.LocalizeResourcesProvider;

@BindXML(filePath = {"gui/localize"})
public class GUIResourceBundleProvider implements LocalizeResourcesProvider {
    
    
    @Override
    public @NotNull String getRootOfResources() {
        return "/gui";
    }
    
    
}
