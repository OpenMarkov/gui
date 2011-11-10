/**
 * 
 */
package org.openmarkov.core.gui.localize;


import java.util.EventListener;


/**
 * Interface for StringResourceLocaleChangeEvent Listeners
 * 
 * @author jlgozalo
 * @version 1.0 25 Jun 2009
 */
public interface StringResourceLocaleChangeListener extends EventListener {

	public abstract void processStringResourceLocaleChange(
															LocaleChangeEvent event);
}