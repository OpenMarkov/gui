package org.openmarkov.core.gui.loader.saxdoc;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;


import org.openmarkov.core.gui.loader.menu.DefaultMenuItemListener;
import org.openmarkov.core.gui.loader.menu.MenuHandlersTable;
import org.openmarkov.core.gui.loader.menu.MenuItemHandler;
import org.openmarkov.core.gui.loader.menu.MenuNode;
import org.openmarkov.core.gui.loader.menu.MenuNodeLinkedList;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;



/**
 * SAXDataListDocHandler implements a XML SAX Handler to be used for reading the
 * OpenMarkov XML configuration files. It is called from the SAXMenuListLoader
 * parser.
 * 
 * @author jlgozalo
 * @version 1.0
 */

public class SAXDataListDocHandler extends DefaultHandler implements
				SAXDataLoaderTokens {

	/** structure to store the menuBar lists */
	private MenuNodeLinkedList menuBarLinkedList;
	/** structure to store the toolBar lists */
	private MenuNodeLinkedList toolBarLinkedList;
	/** structure to store the menu node lists */
	private MenuNodeLinkedList menuNodeLinkedList;
	/** structure to store the Popup menu node lists */
	private MenuNodeLinkedList popupMenuLinkedList;
	/** structure to store the Handlers stack */
	private static ArrayList<MenuItemHandler> menuitemhandlerList;
	/** structure to store the Menu lists stack */
	private static ArrayList<MenuNodeLinkedList> menuNodeLinkedListStack;
	/** structure to supply auxiliar storage of the menu handlers */
	private MenuHandlersTable mhTable;
	/** structure to store the menu bars */
	private static Hashtable<String, JMenuBar> htMenuBars;
	/** structure to store the popup menus */
	private static Hashtable<String, JPopupMenu> htPopupMenus;
	/** structure to store the toolbars */
	private static ArrayList<JToolBar> arToolBars;
	/** current menubar in progress */
	private JMenuBar menubarCurrent;
	/** current popup menu in progress */
	private JPopupMenu popupmenuCurrent;
	/** current toolbar in progress */
	private JToolBar toolbarCurrent;

	/** SaxDataListLoader unique instance. Used in singleton pattern. */
	private static SAXDataListDocHandler saxDataListDocHandler = null;

	/** constructor */
	private SAXDataListDocHandler() {

		menuBarLinkedList = new MenuNodeLinkedList();
		toolBarLinkedList = new MenuNodeLinkedList();
		menuNodeLinkedList = new MenuNodeLinkedList();
		popupMenuLinkedList = new MenuNodeLinkedList();
		htMenuBars = new Hashtable<String, JMenuBar>();
		htPopupMenus = new Hashtable<String, JPopupMenu>();
		arToolBars = new ArrayList<JToolBar>();
		menuitemhandlerList = new ArrayList<MenuItemHandler>();
		menuNodeLinkedListStack = new ArrayList<MenuNodeLinkedList>();
		menubarCurrent = null;
		popupmenuCurrent = null;
		toolbarCurrent = null;
		mhTable = MenuHandlersTable.getUniqueInstance();

	}

	/** @return SaxMenuLoader unique instance (singleton pattern). */
	public static SAXDataListDocHandler getUniqueInstance() {

		if (saxDataListDocHandler == null) {
			saxDataListDocHandler = new SAXDataListDocHandler();
		}
		return saxDataListDocHandler;
	}

	/** Report that a document has begun */
	public void startDocument() {

		// System.out.println("Begin document");
	}

	/**
	 * Handler the beginning of an element.
	 * 
	 * @param namespaceURI
	 *            Place where the elements are located
	 * @param sName
	 *            Name of the element to be parsed
	 * @param attrs
	 *            Element attributes
	 */
	public void startElement(String namespaceURI, String sName, String qName,
								Attributes attrs) {

		// Anything may override handler for its context
		String sHandler = attrs.getValue(ACTION);
		pushMenuItemHandler(sHandler);
		if (sName.equalsIgnoreCase(CONFIGURATION)) {
			// do nothing yet;
		} else if (sName.equalsIgnoreCase(LANGUAGES)) {
			// do nothing yet;
		} else if (sName.equalsIgnoreCase(LANGUAGE)) {
			// do nothing yet;
		} else if (sName.equalsIgnoreCase(MENUS)) {
			// do nothing yet;
		} else if (sName.equalsIgnoreCase(MENUBAR)) {
			defineMenuBar(attrs);
		} else if (sName.equalsIgnoreCase(POPUPMENU)) {
			definePopupMenu(attrs);
		} else if (sName.equalsIgnoreCase(MENU)) {
			defineMenu(attrs);
		} else if (sName.equalsIgnoreCase(MENUITEM)) {
			defineMenuItem(attrs);
		} else if (sName.equalsIgnoreCase(MENUSEPARATOR)) {
			defineMenuSeparator(attrs);
		} else if (sName.equalsIgnoreCase(CHECKBOXMENUITEM)) {
			defineCheckboxMenuItem(attrs);
		} else if (sName.equalsIgnoreCase(TOOLBAR)) {
			defineToolbar(attrs);
		} else if (sName.equalsIgnoreCase(TOOLBARITEM)) {
			defineToolbarItem(attrs);
		} else if (sName.equalsIgnoreCase(TOOLBARSEPARATOR)) {
			defineToolbarSeparator(attrs);
		} else if (sName.equalsIgnoreCase(WINDOWS)) {
			// do nothing yet;
			// }else if ..... otros elementos definibles
		} else {
			System.err.println(ERROR_UNEXPECTED_ELEMENT + sName);
		}
	}

	/**
	 * Handle the end of an element context. This varies by element type.
	 * 
	 * @param sName
	 *            Name of the menu element to be ended
	 */
	public void endElement(String namespaceURI, String sName, String qName) {

		// Every element pushes something, even if it's just its parent's
		// handler
		menuitemhandlerPop();
		if (sName.equalsIgnoreCase(MENU)) {
			MenuNodeLinkedList menuCurrent = menuCurrent();
			if (menuCurrent == null) {
				System.err.println(END_UNNAMED_MENU);
			}
			menuPop();
		}
		if (sName.equalsIgnoreCase(MENUBAR)) {
			menubarCurrent = null;
		}
		if (sName.equalsIgnoreCase(POPUPMENU)) {
			popupmenuCurrent = null;
		}
	}

	/**
	 * Report that the document parse is complete.
	 */
	public void endDocument() {

		LinkedList<MenuNode> lista;

		/*
		 * lista = menuBarLinkedList.getMenuList(); System.out.println("Lista de
		 * elementos del menuBar:"); for (MenuNode node : lista) {
		 * System.out.println(node.toString()); } lista =
		 * menuNodeLinkedList.getMenuList(); System.out.println("Lista de
		 * elementos del menu:"); for (MenuNode node : lista) {
		 * System.out.println(node.toString()); } lista =
		 * popupMenuLinkedList.getMenuList(); System.out.println("Lista de
		 * elementos del popupMenu:"); for (MenuNode node : lista) {
		 * System.out.println(node.toString()); }
		 */
	}

	/**
	 * Add a menuSeparator at the end to the current MenuNodeLinkedList
	 */
	protected void addSeparator() {

		// Add a separator to current menu
		// If we're trying to add a separator to a menu bar, generate an error.
		MenuNodeLinkedList menuCurrent = menuCurrent();

		if (menuCurrent != null) {
			MenuNode menuSeparator = new MenuNode(EMPTY, new JSeparator());
			menuCurrent.getMenuList().addLast(menuSeparator);
		} else {
			if (menubarCurrent != null) {
				System.err.println(ERROR_NO_SEPARATORS_FOR_MENUBARS);
			}
		}
	}

	/**
	 * Add a menubar
	 * 
	 * @param menubar
	 *            the item to add
	 */
	protected void add(JMenuBar menubar) {

		if (menuBarLinkedList.isEmpty()) {
			MenuNode menubarNew = new MenuNode(menubar.getName(), menubar);
			menuBarLinkedList.getMenuList().addLast(menubarNew);
		} else {
			System.out.println(ERROR_ADDING_MENUBAR_TO_MENUBAR);
		}
	}

	/**
	 * Add a menu to the current menu
	 * 
	 * @param menu
	 *            the item to add
	 */
	protected void add(JMenu menu) {

		MenuNodeLinkedList menuCurrent = menuCurrent();
		MenuNode menuNew = new MenuNode(menu.getName(), menu);
		if (menuCurrent != null) {
			menuCurrent.getMenuList().addLast(menuNew);
		} else {
			if (menubarCurrent != null) {
				// menuBarLinkedList.getMenuList().addLast(menuNew);
				int pos = menuBarLinkedList.indexOf(menubarCurrent.getName());
				MenuNode nodeBar = menuBarLinkedList.getElementAt(pos);
				menuBarLinkedList.getElementAt(pos).getList().getMenuList()
					.addLast(menuNew);
			}
			if (popupmenuCurrent != null) {
				popupMenuLinkedList.getMenuList().addLast(menu);
			}
		}
		pushMenu(menuNew.getList());
	}

	/**
	 * Add a JMenuItem to the current JMenu
	 * 
	 * @param menuitem
	 *            the item to add
	 */
	protected void add(JMenuItem menuitem) {

		String sItemName = menuitem.getName();

		// Add this item to current menu, or to popup
		// If we're trying to add an item to a menu bar, generate an error.
		MenuNodeLinkedList menuCurrent = menuCurrent();
		MenuNode menuItemNew = new MenuNode(menuitem.getName(), menuitem);

		if (menuCurrent != null) {
			menuCurrent.getMenuList().addLast(menuItemNew);
		} else {
			if (menubarCurrent != null) {
				System.err.println(sItemName
					+ ERROR_NO_BAREMENUITEMS_FOR_MENUBARS);
			} else if (popupmenuCurrent != null) {
				popupMenuLinkedList.getMenuList().addLast(menuitem);
			}
		}

		// If there's a handler defined for this context, store a reference to
		// it, indexed by the menu item OBJECT.
		MenuItemHandler mih = menuitemhandlerCurrent();
		if (mih != null) {
			registerMenuItemHandler(menuitem, mih);
		}
	}

	/**
	 * Add a Component to the current JToolBar
	 * 
	 * @param menuitem
	 *            the item to add
	 */
	protected void addToolbarItem(JComponent toolbarItem) {

		String sItemName = toolbarItem.getName();

		// Add this item to current toolbar
		toolbarCurrent = toolbarCurrent();
		if (toolbarCurrent != null) {
			toolbarCurrent.add((JButton) toolbarItem);
		}
	}

	/**
	 * Define a checkbox menu item and add it to the current menu
	 * 
	 * @param attrs
	 *            the attributes for the item
	 */
	protected void defineCheckboxMenuItem(Attributes attrs) {

		// Get attributes
		String sItemName = attrs.getValue(NAME);
		String sItemLabel = attrs.getValue(LABEL);
		String sItemAction = attrs.getValue(ACTION);
		// Create new item
		JCheckBoxMenuItem miNew = new JCheckBoxMenuItem(sItemName);

		if (sItemName != null) {
			miNew.setName(sItemName);
		} else {
			sItemName = miNew.getName();
		}

		// Set menu attributes
		if (sItemLabel != null) {
			miNew.setText(sItemLabel);
		} else {
			miNew.setText(sItemName);
		}
		if (sItemAction != null) {
			mhTable.menuitemhandlerFind(sItemAction); // what to do when
			// action is done
		}
		;

		// Add menu item to whatever's currently being built
		add(miNew);
		miNew.addActionListener(new DefaultMenuItemListener());
	}

	/**
	 * Define a menu, add it the the current menu, and then make the new menu
	 * the current menu.
	 * 
	 * @param attrs
	 *            the attributes for the item
	 */
	protected void defineMenu(Attributes attrs) {

		String sMenuName = attrs.getValue(NAME);
		String sMenuLabel = attrs.getValue(LABEL);
		String sItemMnemonic = attrs.getValue(MNEMONIC);

		JMenu menuNew = new JMenu();
		if (sMenuName != null) {
			menuNew.setName(sMenuName);
		} else {
			menuNew.setName(MENU_NOT_NAMED);
		}
		if (sMenuLabel != null) {
			menuNew.setText(sMenuLabel);
		} else {
			menuNew.setText(sMenuName);
		}
		if (sItemMnemonic != null) {
			menuNew.setMnemonic(sItemMnemonic.charAt(0));
		} else {
			// do nothing;
		}
		// Add to current context and make new menu the current menu to build
		add(menuNew);
	}

	/**
	 * Define a menu bar and register it in the hash table
	 * 
	 * @param attrs
	 *            the attributes for the item
	 */
	protected void defineMenuBar(Attributes attrs) {

		String sMenuName = attrs.getValue("name");
		menubarCurrent = new JMenuBar();

		if (sMenuName != null) {
			menubarCurrent.setName(sMenuName);
		}
		add(menubarCurrent);
		register(menubarCurrent);
	}

	/**
	 * Define a menu separator in the menu
	 * 
	 * @param attrs
	 *            the attributes for the item
	 */
	protected void defineMenuSeparator(Attributes attrs) {

		// Add menu separator to whatever's currently being built
		addSeparator();
	}

	/**
	 * Define a new MenuItem and add it to the current menu
	 * 
	 * @param attrs
	 *            the attributes for the item
	 */
	protected void defineMenuItem(Attributes attrs) {

		// Get attributes
		String sItemName = attrs.getValue(NAME);
		String sItemLabel = attrs.getValue(LABEL);
		String sItemMnemonic = attrs.getValue(MNEMONIC);
		String sItemShortCut = attrs.getValue(SHORTCUT);
		String sItemIcon = attrs.getValue(ICON);
		String sItemAction = attrs.getValue(ACTION);

		// Create new item
		JMenuItem miNew = new JMenuItem(sItemName);
		// Set menu attributes
		if (sItemName != null) {
			miNew.setName(sItemName);
		} else {
			sItemName = miNew.getName();
		}
		if (sItemLabel != null) {
			miNew.setText(sItemLabel);
		} else {
			miNew.setText(sItemName);
		}
		if (sItemMnemonic != null) {
			miNew.setMnemonic(sItemMnemonic.charAt(0));
		} else {
			// do nothing;
		}
		if (sItemShortCut != null) {
			sItemShortCut = wellFormedKeyStroke(attrs.getValue(SHORTCUT));
			miNew.setAccelerator(KeyStroke.getKeyStroke(sItemShortCut));
		} else {
			// do nothing;
		}
		if (sItemIcon != null) {
			try {
				miNew.setIcon(new ImageIcon(sItemIcon));
			} catch (Exception ex) {
				System.err.println(ERROR_ICON_NOT_FOUND + sItemIcon);
			}
		} else {
			// do nothing;
		}
		if (sItemAction != null) {
			mhTable.menuitemhandlerFind(sItemAction); // what to do when
			// action is done
		}
		// Add menu item to whatever's currently being built
		add(miNew);
		miNew.addActionListener(new DefaultMenuItemListener());
	}

	/**
	 * Define a new popup menu and register it in the hash table.
	 * 
	 * @param attrs
	 *            the attributes for the item
	 */
	protected void definePopupMenu(Attributes attrs) {

		String sMenuName = attrs.getValue(NAME);
		popupmenuCurrent = new JPopupMenu();
		if (sMenuName != null) {
			popupmenuCurrent.setName(sMenuName);
		}
		register(popupmenuCurrent);
	}

	/**
	 * Define a Toolbar and register it in the hash table
	 * 
	 * @param attrs
	 *            the attributes for the item
	 */
	protected void defineToolbar(Attributes attrs) {

		String sToolbarName = attrs.getValue(NAME);
		toolbarCurrent = new JToolBar();
		if (sToolbarName != null) {
			toolbarCurrent.setName(sToolbarName);
		}
		register(toolbarCurrent);
	}

	/**
	 * Define a toolbar separator in the menu
	 * 
	 * @param attrs
	 *            the attributes for the item
	 */
	protected void defineToolbarSeparator(Attributes attrs) {

		// Add menu separator to whatever's currently being built
		addToolbarSeparator();
	}

	/**
	 * Define a new ToolbarItem and add it to the current toolbar
	 * 
	 * @param attrs
	 *            the attributes for the item
	 */
	protected void defineToolbarItem(Attributes attrs) {

		// Get attributes
		String sItemName = attrs.getValue(NAME);
		String sItemLabel = attrs.getValue(LABEL); // it will be the tooltip
		String sItemIcon = attrs.getValue(ICON);
		String sItemAction = attrs.getValue(ACTION);

		// Create new item
		JButton miNew = new JButton();
		if (sItemName != null) {
			miNew.setName(sItemName);
		} else {
			sItemName = miNew.getName();
		}
		// Set menu attributes
		if (sItemLabel != null) {
			miNew.setToolTipText(sItemLabel);
		} else {
			miNew.setToolTipText(sItemName);
		}
		if (sItemIcon != null) {
			try {
				miNew.setIcon(new ImageIcon(sItemIcon));
			} catch (Exception ex) {
				System.err.println(ERROR_ICON_NOT_FOUND + sItemIcon);
			}
		} else {
			// do nothing;
		}
		if (sItemAction != null) {
			mhTable.menuitemhandlerFind(sItemAction); // what to do when
			// action is done
		}

		// Add menu item to whatever's currently being built
		addToolbarItem(miNew);
		miNew.addActionListener(new DefaultMenuItemListener());
	}

	/**
	 * Add a toolbarSeparator to the current JToolbar
	 */
	protected void addToolbarSeparator() {

		// Add a separator to current toolbar
		toolbarCurrent = toolbarCurrent();

		if (toolbarCurrent != null) {
			toolbarCurrent.addSeparator();

		} else {
			System.err.println(ERROR_NO_TOOLBARSEPARATORS_FOR_TOOLBARS);
		}
	}

	/**
	 * Return MenuBar by name.
	 * 
	 * @param sMenuName
	 *            the name of the menu to find
	 * @return menuBar corresponding to the Menu name
	 */
	protected JMenuBar menubarFind(String sMenuName) {

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
	protected JMenuBar menubarFindTop() {

		Object oToReturn = htMenuBars.get(0);
		if (oToReturn != null && oToReturn instanceof JMenuBar) {
			return (JMenuBar) oToReturn;
		}
		return null;
	}

	/**
	 * Return reference to current menu
	 * 
	 * @return menuToReturn current Menu List in progress
	 */
	protected MenuNodeLinkedList menuCurrent() {

		int iStacksize = menuNodeLinkedListStack.size();
		if (iStacksize == 0) {
			return null;
		}
		MenuNodeLinkedList menuToReturn =
			menuNodeLinkedListStack.get(iStacksize - 1);
		return menuToReturn;
	}

	/**
	 * Return reference to current ToolBar component
	 * 
	 * @return toolbarToReturn the current ToolBar in progress
	 */
	protected JToolBar toolbarCurrent() {

		int iStacksize = arToolBars.size();

		if (iStacksize == 0) {
			return null;
		}
		JToolBar toolbarToReturn = arToolBars.get(iStacksize - 1);
		return toolbarToReturn;
	}

	/**
	 * Return reference to current MenuItemHandler.
	 * 
	 * @return the MenuItemHandler currently in progress
	 */
	protected MenuItemHandler menuitemhandlerCurrent() {

		if (menuitemhandlerList == null || menuitemhandlerList.size() == 0) {
			return null;
		}
		return menuitemhandlerList.get(menuitemhandlerList.size() - 1);
	}

	/**
	 * Push menu onto menu stack
	 * 
	 * @param menuList
	 *            the menu list to add to the stack
	 */
	protected void pushMenu(MenuNodeLinkedList menuList) {

		menuNodeLinkedListStack.add(menuList);
	}

	/**
	 * Pop a menu item handler off of the handler stack
	 * 
	 * @return the menuItemHandler that goes out of the stack
	 */
	protected MenuItemHandler menuitemhandlerPop() {

		MenuItemHandler l = menuitemhandlerCurrent();
		if (l != null) {
			menuitemhandlerList.remove(menuitemhandlerList.size() - 1);
		}
		return l;
	}

	/**
	 * Pop menuNode from current menuNode list.
	 * 
	 * @return menuToReturn the menu that goes out of the stack
	 */
	protected MenuNodeLinkedList menuPop() {

		MenuNodeLinkedList menuToReturn = menuCurrent();
		if (menuToReturn != null) {
			menuNodeLinkedListStack.remove(menuNodeLinkedListStack.size() - 1);
		}
		return menuToReturn;
	}

	/**
	 * Find a popup menu by name
	 * 
	 * @param sMenuName
	 *            the name of the Popup menu to find in the stack
	 * @return oToReturn the PopUpMenu or null if not found
	 */
	public JPopupMenu popupmenuFind(String sMenuName) {

		Object oToReturn = htPopupMenus.get(sMenuName);
		if (oToReturn != null && oToReturn instanceof JPopupMenu) {
			return (JPopupMenu) oToReturn;
		}
		return null;
	}

	/**
	 * Insert menu onto menuNode Linked List
	 * 
	 * @param menuNode
	 *            the node to insert in the list
	 */
	protected void insertMenu(MenuNode menuNode) {

		menuNodeLinkedList.getMenuList().add(menuNode);
	}

	/**
	 * Push menu item handler onto handler stack
	 * 
	 * @param l
	 *            the menuItemHandler to push in the stack
	 */
	protected void pushMenuItemHandler(MenuItemHandler l) {

		menuitemhandlerList.add(l);
	}

	/**
	 * Find menu item handler by name, then push it on the handler stack
	 * 
	 * @param sName
	 *            the name of the menuItemHandler to push in the stack
	 */
	protected void pushMenuItemHandler(String sName) {

		MenuItemHandler l = mhTable.menuitemhandlerFind(sName);
		if (l == null) {
			l = menuitemhandlerCurrent();
		}

		pushMenuItemHandler(l);
	}

	/**
	 * Register a menubar in the menubar hash table.
	 * 
	 * @param menubar
	 *            the menubar to register
	 */
	protected void register(JMenuBar menubar) {

		htMenuBars.put(menubar.getName(), menubar);
	}

	/**
	 * Register a popup menu in the popup menu hash table
	 * 
	 * @param popupmenu
	 *            the popupmenu to register
	 */
	protected void register(JPopupMenu popupmenu) {

		htPopupMenus.put(popupmenu.getName(), popupmenu);
	}

	/**
	 * Register a toolbar in the toolbar hash table.
	 * 
	 * @param toolbar
	 *            the toolbar to register
	 */
	protected void register(JToolBar toolbar) {

		arToolBars.add(toolbar);
	}

	/**
	 * Register a menu item handler in the handler table by menu item
	 * 
	 * @param mi
	 *            the Menuitem to register
	 * @param l
	 *            the Handler of the menuitem to register
	 */
	protected void registerMenuItemHandler(JMenuItem mi, MenuItemHandler l) {

		mhTable.registerMenuItemHandler(mi, l);
	}

	/**
	 * Delete the char '+' in the String Keystroke defined in the external menu
	 * XML file when read by the parser and before to set the keystroke to the
	 * menu
	 * 
	 * @param attrsKeyStroke
	 *            the set of chars to be manipulated
	 */
	private String wellFormedKeyStroke(String attrsKeyStroke) {

		String newString = attrsKeyStroke;
		if (attrsKeyStroke.contains(CHAR_PLUS)) {
			newString = attrsKeyStroke.replace(CHAR_PLUS, CHAR_BLANK);
			if (newString.trim().length() == 0) { // to prevent Keystroke has
				// only one PLUS CHAR
				newString = attrsKeyStroke;
			}
		}
		if (newString.contains(CTRL_UPPERCASE)) {
			newString = newString.replace(CTRL_UPPERCASE, CTRL_LOWERCASE);
		}
		return newString;
	}

	/**
	 * @return the menuBarLinkedList
	 */
	public MenuNodeLinkedList getMenuBarLinkedList() {

		return menuBarLinkedList;
	}

	/**
	 * @return the menuNodeLinkedList
	 */
	public MenuNodeLinkedList getMenuNodeLinkedList() {

		return menuNodeLinkedList;
	}

	/**
	 * @return the popupMenuLinkedList
	 */
	public MenuNodeLinkedList getPopupMenuLinkedList() {

		return popupMenuLinkedList;
	}

	/**
	 * @return the toolBarLinkedList
	 */
	public MenuNodeLinkedList getToolBarLinkedList() {

		return toolBarLinkedList;
	}

	/**
	 * @return the htMenuBars
	 */
	public static Hashtable<String, JMenuBar> getHtMenuBars() {

		return htMenuBars;
	}

}