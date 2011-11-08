package openmarkov.core.gui.menutoolbar.sax;


/*
 * @author jlgozalo
 * 
 * @version 1.0
 */
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;


/**
 * A MenuPopper stores a reference to a Component and a Menu retrieved by name
 * from a SaxMenuLoader. The MenuPopper registers itself to listen for
 * MouseEvents on the Component, and when a right-mouse-click occurs, pops up
 * the associated menu item.
 * 
 * @author jlgozalo
 * @version 1.0
 */
public class MenuPopper extends java.awt.event.MouseAdapter {

	JComponent component;
	JPopupMenu popup;

	public MenuPopper(JComponent component, SaxDataLoader sml, String sMenuToPop) {

		component = component;
		popup = sml.popupmenuFind(sMenuToPop);
		if (popup != null) {
			component.add(popup);
		}
	}

	/**
	 * Pop up menu when right mouse click occurs.
	 */
	public void mouseClicked(MouseEvent e) {

		if (popup != null
			&& (e.getModifiers() & (MouseEvent.BUTTON2_MASK | MouseEvent.BUTTON3_MASK)) != 0) {
			popup.show(component, e.getX(), e.getY());
		}
	}
}