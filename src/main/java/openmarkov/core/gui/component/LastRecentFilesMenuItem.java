/**
 * 
 */
package openmarkov.core.gui.component;


import javax.swing.JMenuItem;


/**
 * This is a convenience class to distinguish between a normal MenuItem and the
 * LastRecentFile MenuItem lines (that will help for i18n working well).
 * 
 * @author jlgozalo
 * @version 1.0 25 Jul 2009
 */
public class LastRecentFilesMenuItem extends JMenuItem {

	/**
	 * default serial id
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * default constructor
	 */
	public LastRecentFilesMenuItem() {

		setName("LastRecentFilesMenuItem");
	}
}