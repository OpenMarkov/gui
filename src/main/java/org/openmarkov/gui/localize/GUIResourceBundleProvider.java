package org.openmarkov.gui.localize;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.openmarkov.gui.localize.spi.LocalizeResourcesProvider;

public class GUIResourceBundleProvider implements LocalizeResourcesProvider {

	@Override
	public ResourceBundle getBundle(String baseName, Locale locale) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public String getInfixForPathGetBundles() {
		return getInfixForPathGetBundles("org.openmarkov.gui");
	}


	@Override
	public InputStream auxGetResourceAsStream(String name) throws IOException {
		Module m = this.getClass().getModule();
		return m.getResourceAsStream(name);
	}
	

	@Override
	public URL auxGetResource(String infix) {
		return this.getClass().getResource(infix);
	}
	
	@Override public Class<? extends LocalizeResourcesProvider> auxClass() {
		return GUIResourceBundleProvider.class;
	}
	
	
}
