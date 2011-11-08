package openmarkov.core.gui.menutoolbar.jdom;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * JDomDataLoader implements a Data Loader from XML external file to help in the
 * Menu definition. It reads from the file the menu structure, handlers and
 * Toolbars and other elements In such way, it is not required to implement
 * specific classes for Menus, Toolbars and HandlerAction in the application.
 * 
 * @author jlgozalo
 * @version 1.0
 */

public class JdomDataLoader extends DefaultHandler implements ActionListener,
				ItemListener, JdomDataLoaderTokens {

	private SAXParserFactory factory;
	private SAXParser parser;
	private DefaultHandler handler;
	private static Vector menuStack;
	private static Vector toolbarStack;
	private static Vector menuitemhandlerStack;
	private static Vector toolbaritemhandlerStack;
	private static Hashtable htMenuBars;
	private static Hashtable htToolBars;
	private static Hashtable htPopupMenus;
	private static Hashtable htMenuItemHandlers;
	private JMenuBar menubarCurrent;
	private JPopupMenu popupmenuCurrent;
	private JToolBar toolbarCurrent;

	/** SaxMenuLoader unique instance. Used in singleton pattern. */
	private static JdomDataLoader jdomDataLoader = null;

	// Constructor
	private JdomDataLoader() {

		createParser();
		menuStack = new Vector();
		toolbarStack = new Vector();
		menuitemhandlerStack = new Vector();
		toolbaritemhandlerStack = new Vector();
		htMenuBars = new Hashtable();
		htToolBars = new Hashtable();
		htPopupMenus = new Hashtable();
		htMenuItemHandlers = new Hashtable();
		menubarCurrent = null;
		popupmenuCurrent = null;
		toolbarCurrent = null;
	}

	/** @return JdomDataLoader unique instance (singleton pattern). */
	public static JdomDataLoader getUniqueInstance() {

		if (jdomDataLoader == null) {
			jdomDataLoader = new JdomDataLoader();
		}
		return jdomDataLoader;
	}

	/**
	 * Load data from an URI
	 */
	public void loadData(URI uri) {

		if (parser == null) {
			System.err.println("No parser created");
			return;
		}
		try {
			parser.parse(uri.toASCIIString(), handler);
		} catch (SAXException ex) {
			System.err.println("Parse error: " + ex.getMessage());
		} catch (Exception ex) {
			System.err.println("SaxDataFactory.loadDat: " + uri.toASCIIString()
				+ ": " + ex.getMessage());

		}
	}

	/**
	 * Load data from a file
	 */
	public void loadData(File file) {

		if (parser == null) {
			System.err.println("No parser created");
			return;
		}
		try {
			parser.parse(file, handler);
		} catch (SAXException ex) {
			System.err.println("Parse error: " + ex.getMessage());
		} catch (Exception ex) {
			System.err.println("SaxDataFactory.loadData: " + file + ": "
				+ ex.getMessage());
		}
	}

	/**
	 * Load data from a file
	 */
	public void loadData(String sFilename) {

		loadData(new File(sFilename));
	}

	/**
	 * Load data from an input stream
	 */
	public void loadData(InputStream istream) {

		if (parser == null) {
			System.err.println("No parser created");
			return;
		}
		try {
			parser.parse(istream, handler);
		} catch (SAXException ex) {
			System.err.println("Parse error: " + ex.getMessage());
		} catch (Exception ex) {
			System.err.println("SaxDataFactory.loadData: " + istream + ": "
				+ ex.getMessage());
		}
	}

	/**
	 * When a MenuItem is activated, this method finds and calls the MenuItem's
	 * MenuItemHandler.
	 */
	public void actionPerformed(ActionEvent e) {

		Object oSource = e.getSource();
		if (oSource instanceof JMenuItem) {
			JMenuItem mi = (JMenuItem) oSource;
			MenuItemHandler mih = menuitemhandlerFind(mi);
			if (mih != null) {
				mih.itemActivated(mi, e, mi.getActionCommand());
			}
		}
	}

	/**
	 * Register a menu item handler in the handler table by name
	 */
	public void registerMenuItemHandler(String sHandlerName, MenuItemHandler l) {

		htMenuItemHandlers.put(sHandlerName, l);
	}

	/**
	 * Report that a document has begun
	 */
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
		if (sName.equalsIgnoreCase(MENUS)) {
			// do nothing;
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
			// }else if ..... otros elementos definibles
		} else {
			System.err.println("ERROR: UNEXPECTED ELEMENT=" + sName);
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
			JMenu menuCurrent = menuCurrent();
			if (menuCurrent == null) {
				System.err.println("End unnamed menu");
			}
			menuPop();
		}
		if (sName.equalsIgnoreCase(MENUBAR)) {
			menubarCurrent = null;
		}
		if (sName.equalsIgnoreCase(POPUPMENU)) {
			popupmenuCurrent = null;
		}
		if (sName.equalsIgnoreCase(TOOLBAR)) {
			toolbarCurrent = null;
		}
	}

	/**
	 * Report that the document parse is complete.
	 */
	public void endDocument() {

		// System.out.println("End document");
	}

	/**
	 * Dispatch check/uncheck events from CheckboxMenuItems.
	 */
	public void itemStateChanged(ItemEvent e) {

		Object oSource = e.getSource();
		if (oSource instanceof JMenuItem) {
			JMenuItem mi = (JMenuItem) oSource;
			MenuItemHandler mih = menuitemhandlerFind(mi);
			if (mih != null) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					mih.itemSelected(mi, e, mi.getActionCommand());
				} else {
					mih.itemDeselected(mi, e, mi.getActionCommand());
				}
			}
		}
	}

	/**
	 * Initialize the internal parser.
	 */
	protected void createParser() {

		parser = null;
		try {
			factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			parser = factory.newSAXParser();
			handler = (DefaultHandler) this;
		} catch (Exception ex) {
			System.err
				.println("SaxMenuLoader.createParser: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	/**
	 * Add a menuSeparator to the current JMenu
	 */
	protected void addMenuSeparator() {

		// Add a separator to current menu
		// If we're trying to add a separator to a menu bar, generate an error.
		JMenu menuCurrent = menuCurrent();

		if (menuCurrent != null) {
			menuCurrent.addSeparator();
		} else {
			if (menubarCurrent != null) {
				System.err.println("Can't add separator items to menu bars");
			}
		}
	}

	/**
	 * Add a menu to the current menu >to-do >>>> to read now more than one
	 * menu, this method must use LinkList
	 */
	protected void add(JMenu menu) {

		JMenu menuCurrent = menuCurrent();
		if (menuCurrent != null) {
			menuCurrent.add(menu);
		} else {
			if (menubarCurrent != null) {
				menubarCurrent.add(menu);
			}
			if (popupmenuCurrent != null) {
				popupmenuCurrent.add(menu);
			}
		}
	}

	/**
	 * Add a JMenuItem to the current JMenu
	 */
	protected void add(JMenuItem menuitem) {

		String sItemName = menuitem.getName();

		// Add this item to current menu, or to popup
		// If we're trying to add an item to a menu bar, generate an error.
		JMenu menuCurrent = menuCurrent();

		if (menuCurrent != null) {
			menuCurrent.add(menuitem);
		} else {

			if (menubarCurrent != null) {
				System.err.println(sItemName
					+ ": Can't add bare menu items to menu bars");
			}

			else if (popupmenuCurrent != null) {
				popupmenuCurrent.add(menuitem);
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
	 * Define a checkbox menu item and add it to the current menu
	 */
	protected void defineCheckboxMenuItem(Attributes attrs) {

		// Get attributes
		String sItemName = attrs.getValue(NAME);
		String sItemLabel = attrs.getValue(LABEL);

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

		// Add menu item to whatever's currently being built
		add(miNew);
		miNew.addItemListener(this);
	}

	/**
	 * Define a menu, add it the the current menu, and then make the new menu
	 * the current menu.
	 */
	protected void defineMenu(Attributes attrs) {

		String sMenuName = attrs.getValue(NAME);
		String sItemMnemonic = attrs.getValue(MNEMONIC);

		JMenu menuNew = new JMenu(sMenuName);
		if (sMenuName != null) {
			menuNew.setName(sMenuName);
		} else {
			sMenuName = menuNew.getName();
		}
		if (sItemMnemonic != null) {
			menuNew.setMnemonic(sItemMnemonic.charAt(0));
		} else {
			// do nothing;
		}
		// Add to current context and make new menu the current menu to build
		add(menuNew);
		pushMenu(menuNew);
	}

	/**
	 * Define a menu bar and register it in the hash table
	 */
	protected void defineMenuBar(Attributes attrs) {

		String sMenuName = attrs.getValue(NAME);
		menubarCurrent = new JMenuBar();
		if (sMenuName != null) {
			menubarCurrent.setName(sMenuName);
		}
		register(menubarCurrent);
	}

	/**
	 * Define a menu separator in the menu
	 */
	protected void defineMenuSeparator(Attributes attrs) {

		// Add menu separator to whatever's currently being built
		addMenuSeparator();
	}

	/**
	 * Define a new MenuItem and add it to the current menu
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
				System.err.println("icon not found" + sItemIcon);
			}
		} else {
			// do nothing;
		}

		// Add menu item to whatever's currently being built
		add(miNew);
		miNew.addActionListener(this);
	}

	/**
	 * Define a new popup menu and register it in the hash table.
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
	 * Define a menu separator in the menu
	 */
	protected void defineToolbarSeparator(Attributes attrs) {

		// Add menu separator to whatever's currently being built
		addToolbarSeparator();
	}

	/**
	 * Define a new ToolbarItem and add it to the current toolbar
	 */
	protected void defineToolbarItem(Attributes attrs) {

		// Get attributes
		String sItemName = attrs.getValue(NAME);
		String sItemType = attrs.getValue(TYPE);
		String sItemLabel = attrs.getValue(LABEL);
		String sItemMnemonic = attrs.getValue(MNEMONIC);
		String sItemShortCut = attrs.getValue(SHORTCUT);
		String sItemIcon = attrs.getValue(ICON);
		String sItemAction = attrs.getValue(ACTION);

		// Create new item
		JMenuItem miNew = new JMenuItem(sItemName);
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
				System.err.println("icon not found" + sItemIcon);
			}
		} else {
			// do nothing;
		}

		// Add menu item to whatever's currently being built
		add(miNew);
		miNew.addActionListener(this);
	}

	/**
	 * Add a toolbarSeparator to the current JToolbar
	 */
	protected void addToolbarSeparator() {

		// Add a separator to current toolbar
		// If we're trying to add a separator to a non toolbar, generate an
		// error.
		JToolBar toolbarCurrent = toolbarCurrent();

		if (toolbarCurrent != null) {
			toolbarCurrent.addSeparator();
		} else {
			if (toolbarCurrent != null) {
				System.err
					.println("Can't add toolbar separator items to non toolbars");
			}
		}
	}

	/**
	 * Return MenuBar by name.
	 * 
	 * @param menuName
	 * @return menuBar corresponding to the Menu name
	 */
	protected JMenuBar menubarFind(String sMenuName) {

		Object oToReturn = htMenuBars.get(sMenuName);
		if (oToReturn != null && oToReturn instanceof JMenuBar) {
			return (JMenuBar) oToReturn;
		}
		return null;
	}

	/**
	 * Return reference to current menu
	 */
	protected JMenu menuCurrent() {

		int iStacksize = menuStack.size();
		if (iStacksize == 0) {
			return null;
		}

		JMenu menuToReturn = (JMenu) menuStack.elementAt(iStacksize - 1);
		return menuToReturn;
	}

	/**
	 * Return reference to current menu
	 */
	protected JToolBar toolbarCurrent() {

		int iStacksize = toolbarStack.size();
		if (iStacksize == 0) {
			return null;
		}

		JToolBar toolbarToReturn =
			(JToolBar) toolbarStack.elementAt(iStacksize - 1);
		return toolbarToReturn;
	}

	/**
	 * Return reference to current MenuItemHandler.
	 */
	protected MenuItemHandler menuitemhandlerCurrent() {

		if (menuitemhandlerStack == null || menuitemhandlerStack.size() == 0) {
			return null;
		}
		return (MenuItemHandler) menuitemhandlerStack
			.elementAt(menuitemhandlerStack.size() - 1);
	}

	/**
	 * Given a MenuItem, return its handler
	 */
	protected MenuItemHandler menuitemhandlerFind(JMenuItem mi) {

		Object oHandler = htMenuItemHandlers.get(mi);
		return (MenuItemHandler) oHandler;
	}

	/**
	 * Get a MenuItemHandler by name
	 */
	protected MenuItemHandler menuitemhandlerFind(String sName) {

		if (sName == null)
			return null;
		MenuItemHandler mih = (MenuItemHandler) htMenuItemHandlers.get(sName);

		// Not registered. See if it's a class name, and if it is, create an
		// instance of that class and register it.
		if (mih == null) {
			try {
				Class classOfHandler = Class.forName(sName);
				MenuItemHandler newHandler =
					(MenuItemHandler) classOfHandler.newInstance();
				registerMenuItemHandler(sName, newHandler);
				mih = newHandler;
			} catch (Exception ex) {
				System.err.println("Couldn't find menu item handler '" + sName
					+ ": no such registered handler, and couldn't create");
				System.err.println(sName + ": " + ex.getClass().getName()
					+ ": " + ex.getMessage());
			}
		}
		return mih;
	}

	/**
	 * Pop a menu item handler off of the handler stack
	 */
	protected MenuItemHandler menuitemhandlerPop() {

		MenuItemHandler l = menuitemhandlerCurrent();
		if (l != null) {
			menuitemhandlerStack
				.removeElementAt(menuitemhandlerStack.size() - 1);
		}
		return l;
	}

	/**
	 * Pop menu from current menu stack.
	 */
	protected JMenu menuPop() {

		JMenu menuToReturn = menuCurrent();
		if (menuToReturn != null) {
			menuStack.removeElementAt(menuStack.size() - 1);
		}
		return menuToReturn;
	}

	/**
	 * Find a popup menu by name
	 */
	protected JPopupMenu popupmenuFind(String sMenuName) {

		Object oToReturn = htPopupMenus.get(sMenuName);
		if (oToReturn != null && oToReturn instanceof JPopupMenu) {
			return (JPopupMenu) oToReturn;
		}
		return null;
	}

	/**
	 * Push menu onto menu stack
	 */
	protected void pushMenu(JMenu menu) {

		menuStack.addElement(menu);
	}

	/**
	 * Push menu item handler onto handler stack
	 */
	protected void pushMenuItemHandler(MenuItemHandler l) {

		menuitemhandlerStack.addElement(l);
	}

	/**
	 * Find menu item handler by name, then push it on the handler stack
	 */
	protected void pushMenuItemHandler(String sName) {

		MenuItemHandler l = menuitemhandlerFind(sName);
		if (l == null)
			l = menuitemhandlerCurrent();
		pushMenuItemHandler(l);
	}

	/**
	 * Register a menubar in the menubar hash table.
	 */
	protected void register(JMenuBar menubar) {

		htMenuBars.put(menubar.getName(), menubar);
	}

	/**
	 * Register a popup menu in the popup menu hash table
	 */
	protected void register(JPopupMenu popupmenu) {

		htPopupMenus.put(popupmenu.getName(), popupmenu);
	}

	/**
	 * Register a toolbar in the toolbar hash table.
	 */
	protected void register(JToolBar toolbar) {

		htToolBars.put(toolbar.getName(), toolbar);
	}

	/**
	 * Register a menu item handler in the handler table by menu item
	 */
	protected void registerMenuItemHandler(JMenuItem mi, MenuItemHandler l) {

		htMenuItemHandlers.put(mi, l);
	}

	/**
	 * Delete the char '+' in the String Keystroke defined in the external menu
	 * XML file when read by the parser and before to set the keystroke to the
	 * menu
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
	 * Print the information of this object
	 */
	public String toString() {

		StringBuffer buf = new StringBuffer();
		String name = this.getClass().getName();
		buf.append("[" + this.getClass().getName() + ":" + "\n");
		try {
			Object obj = Class.forName(name);
			for (Field fields : this.getClass().getDeclaredFields()) {
				try {
					name = fields.getName();
					String valor = (String) fields.get(obj);
					buf.append(fields.getName() + "=" + valor + "\n");
				} catch (ClassCastException e) {
					System.out.println("error al ver " + name);
				} catch (IllegalArgumentException e) {
					System.out.println("error al ver " + name);
				}
			}
		} catch (IllegalAccessException e) {
			System.out.println("Error al instanciar el objeto. " + e);
		} catch (ClassNotFoundException e) {
			System.out.println("Error al instanciar el objeto. " + e);
		}
		buf.append("]");
		return buf.toString();
	}
}