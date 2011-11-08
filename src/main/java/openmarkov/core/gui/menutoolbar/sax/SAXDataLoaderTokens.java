package openmarkov.core.gui.menutoolbar.sax;


/**
 * Definitions of Tokens to be used in the SAXDataLoader
 * 
 * @author jlgozalo
 * @version 1.0 23/11/2008
 */
public interface SAXDataLoaderTokens {

	// chars
	String CHAR_PLUS = "+";
	String CHAR_BLANK = " ";
	String CTRL_UPPERCASE = "CTRL";
	String CTRL_LOWERCASE = "ctrl";
	String ALT_UPPERCASE = "ALT";
	String ALT_LOWERCASE = "alt";

	// Main Visual elements and Handlers

	String CONFIGURATION = "Configuration";
	String LANGUAGES = "Languages";
	String LANGUAGE = "Language";
	String MENUS = "Menus";
	String MENUBAR = "MenuBar";
	String POPUPMENU = "PopupMenu";
	String MENU = "Menu";
	String MENUITEM = "MenuItem";
	String MENUSEPARATOR = "MenuSeparator";
	String CHECKBOXMENUITEM = "CheckboxMenuItem";
	String TOOLBAR = "ToolBar";
	String TOOLBARITEM = "ToolBarItem";
	String TOOLBARSEPARATOR = "ToolBarSeparator";
	String WINDOWS = "Windows";

	// Attributes
	String NAME = "name";
	String TYPE = "type";
	String LABEL = "label";
	String MNEMONIC = "mnemonic";
	String SHORTCUT = "shortcut";
	String ICON = "icon";
	String ACTION = "action";

}
