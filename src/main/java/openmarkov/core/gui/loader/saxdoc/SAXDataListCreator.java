/**
 * 
 */
package openmarkov.core.gui.loader.saxdoc;


import java.util.Hashtable;
import java.util.LinkedList;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

import openmarkov.core.gui.loader.menu.MenuNode;
import openmarkov.core.gui.loader.menu.MenuNodeLinkedList;



/**
 * Creates the real menus based upon the SAXDataListDoc structures
 * 
 * @author jlgozalo
 * @version 1.0 07/12/2008
 */
public class SAXDataListCreator {

	/** structure to retrieve the menuBar lists */
	private static MenuNodeLinkedList menuBarLinkedList;
	/** structure to retrieve the menu node lists */
	private static MenuNodeLinkedList menuNodeLinkedList;
	/** structure to retrieve the Popup menu node lists */
	private static MenuNodeLinkedList popupMenuLinkedList;
	/** structure to store the toolBar lists */
	private MenuNodeLinkedList toolBarLinkedList;
	/** structure to store the menu bars */
	private static Hashtable<String, JMenuBar> htMenuBars;
	/** the menubar to create */
	private JMenuBar menuBar = null;
	/** the popup menu to create */
	private JPopupMenu popupMenu = null;
	/** the toolbars to create */
	private JToolBar toolBar = null;

	/** SaxDataListCreator unique instance. Used in singleton pattern. */
	private static SAXDataListCreator creator = null;

	/** constructor */
	private SAXDataListCreator() {

		menuBarLinkedList =
			SAXDataListDocHandler.getUniqueInstance().getMenuBarLinkedList();
		menuNodeLinkedList =
			SAXDataListDocHandler.getUniqueInstance().getMenuNodeLinkedList();
		popupMenuLinkedList =
			SAXDataListDocHandler.getUniqueInstance().getPopupMenuLinkedList();
		toolBarLinkedList =
			SAXDataListDocHandler.getUniqueInstance().getToolBarLinkedList();
		htMenuBars = SAXDataListDocHandler.getUniqueInstance().getHtMenuBars();
		menuBar = new JMenuBar();
		toolBar = new JToolBar();
		popupMenu = new JPopupMenu();
	}

	/** @return SaxMenuLoader unique instance (singleton pattern). */
	public static SAXDataListCreator getUniqueInstance() {

		if (creator == null) {
			creator = new SAXDataListCreator();
		}
		return creator;
	}

	/**
	 * create the data structures of the application based upon the intermediate
	 * structures that have been read from the XML files: <br>
	 * First, it will create the MenuBar with the menus <br>
	 * Then, the popupmenus and the Toolbars
	 */
	public void createData() {

		createAllMenus();
		createAllToolBars();
		// LinkedList<MenuNode> menuPopUpList = popupMenuLinkedList;

	}

	/** Create all the Menus on a MenuBar */
	private void createAllMenus() {

		LinkedList<MenuNode> menuBarList = menuBarLinkedList.getMenuList();
		if (menuBarList != null) {
			for (MenuNode nodeBar : menuBarList) {
				menuBar = (JMenuBar) nodeBar.getObject();
				MenuNodeLinkedList list = nodeBar.getList();
				LinkedList<MenuNode> listItems = list.getMenuList();
				for (MenuNode nodeMenu : listItems) {
					menuBar.add(createOneMenu(nodeMenu));
				}
			}
		} else {
			// do nothing. It is empty
		}

	}

	/**
	 * Create a proper JMenu from a MenuNode
	 * 
	 * @param aMenuNodeList
	 * @return aMenu the menu that has been created from the MenuNodeList
	 */
	protected JMenu createOneMenu(MenuNode aMenuNode) {

		JMenu aMenu = (JMenu) aMenuNode.getObject();
		LinkedList<MenuNode> menuList = aMenuNode.getList().getMenuList();
		if (menuList != null) {
			for (MenuNode nodeMenu : menuList) {
				JComponent obj = nodeMenu.getObject();
				if (obj instanceof JMenu) {
					// we need to create the menu associated to the element
					aMenu.add(createOneMenu(nodeMenu));
				} else if (obj instanceof JSeparator) {
					aMenu.addSeparator();
				} else {
					// it could be either a MenuItem or a CheckBoxMenuItem
					aMenu.add(obj);
				}
			}
		}
		return aMenu;
	}

	/** Create all the Toolbars */
	private void createAllToolBars() {

		LinkedList<MenuNode> toolBarList = toolBarLinkedList.getMenuList();
		if (toolBarList != null) {
			for (MenuNode nodeBar : toolBarList) {
				menuBar = (JMenuBar) nodeBar.getObject();
				MenuNodeLinkedList list = nodeBar.getList();
				LinkedList<MenuNode> listItems = list.getMenuList();
				for (MenuNode nodeMenu : listItems) {
					menuBar.add(createOneMenu(nodeMenu));
				}
			}
		} else {
			// do nothing. It is empty
		}

	}

	/**
	 * Return MenuBar by name.
	 * 
	 * @param sMenuName
	 *            the name of the menu to find
	 * @return menuBar corresponding to the Menu name
	 */
	public JMenuBar menubarFind(String sMenuName) {

		Object oToReturn = htMenuBars.get(sMenuName);
		JMenuBar menubar = null;
		if (oToReturn != null && oToReturn instanceof JMenuBar) {
			menubar = (JMenuBar) oToReturn;
		}
		return menubar;
	}

	/**
	 * Return MenuBar in the top
	 * 
	 * @return menuBar corresponding to the Top Menu
	 */
	public JMenuBar menubarFindTop() {

		Object oToReturn = htMenuBars.get(0);
		if (oToReturn != null && oToReturn instanceof JMenuBar) {
			return (JMenuBar) oToReturn;
		}
		return null;
	}

	/**
	 * @return the menuBar
	 */
	public JMenuBar getMenuBar() {

		return menuBar;
	}

	/**
	 * @param menuBar
	 *            the menuBar to set
	 */
	public void setMenuBar(JMenuBar menuBar) {

		this.menuBar = menuBar;
	}

}
