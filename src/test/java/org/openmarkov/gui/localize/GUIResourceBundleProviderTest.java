package org.openmarkov.gui.localize;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GUIResourceBundleProviderTest {
    
	
    @Test
    void testBindings() {
        String bindingResolution = org.openmarkov.gui.localize.Nls.Buttons.Add.Text.Label.stringify();
        String databaseResolution = StringDatabase.getUniqueInstance().getString("Buttons", "Add.Text.Label");
        Assertions.assertEquals(bindingResolution, databaseResolution);
    }
    
}