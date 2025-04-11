package org.openmarkov.gui.localize;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class GUIResourceBundleProviderTest {
    
    @Test
    void testBindings() {
        String bindingResolution = org.openmarkov.gui.localize.Localize.Add.Text.Label.stringify();
        String databaseResolution = StringDatabase.getUniqueInstance().getString("Add.Text.Label");
        Assertions.assertEquals(bindingResolution, databaseResolution);
    }
    
}