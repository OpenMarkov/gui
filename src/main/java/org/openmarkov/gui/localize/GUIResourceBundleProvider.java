package org.openmarkov.gui.localize;

import org.jetbrains.annotations.NotNull;
import org.openmarkov.gui.localize.spi.LocalizeResourcesProvider;

public class GUIResourceBundleProvider implements LocalizeResourcesProvider {
    
    
    @Override
    public @NotNull String getRootOfResources() {
        return "/gui";
    }
    
    
}
