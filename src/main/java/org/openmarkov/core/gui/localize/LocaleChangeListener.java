/**
 * 
 */
package org.openmarkov.core.gui.localize;


import java.util.EventListener;


/**
 * Interface for LocaleChangeEvent Listeners
 * 
 * @author jlgozalo
 * @version 1.0 25 Jun 2009
 */
public interface LocaleChangeListener extends EventListener {

	public abstract void processLocaleChange(LocaleChangeEvent event);
}