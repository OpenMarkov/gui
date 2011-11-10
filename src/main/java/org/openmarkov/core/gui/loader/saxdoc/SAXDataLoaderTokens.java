package org.openmarkov.core.gui.loader.saxdoc;


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

	// Main Visual elements and Handlers

	String CONFIGURATION = "configuration";
	String LANGUAGES = "languages";
	String LANGUAGE = "language";
	String MENUS = "menus";
	String MENUBAR = "menubar";
	String POPUPMENU = "popupmenu";
	String MENU = "menu";
	String MENUITEM = "menuitem";
	String MENUSEPARATOR = "menuseparator";
	String CHECKBOXMENUITEM = "checkboxmenuitem";
	String TOOLBAR = "toolbar";
	String TOOLBARITEM = "toolbaritem";
	String TOOLBARSEPARATOR = "toolbarseparator";
	String WINDOWS = "windows";

	// Attributes
	String NAME = "name";
	String TYPE = "type";
	String LABEL = "label";
	String MNEMONIC = "mnemonic";
	String SHORTCUT = "shortcut";
	String ICON = "icon";
	String ACTION = "action";

	// Errors String
	String MENU_NOT_NAMED = "XYZ";
	String END_UNNAMED_MENU = "End unnamed menu";
	String ERROR_UNEXPECTED_ELEMENT = "ERROR: UNEXPECTED ELEMENT=";
	String ERROR_NO_SEPARATORS_FOR_MENUBARS =
		"Can't add separator items to menu bars";
	String ERROR_NO_BAREMENUITEMS_FOR_MENUBARS =
		": Can't add bare menu items to menu bars";
	String ERROR_ICON_NOT_FOUND = "Icon not found ";
	String ERROR_NO_TOOLBARSEPARATORS_FOR_TOOLBARS =
		"Can't add toolbar separator items to non toolbars";
	String ERROR_ADDING_MENUBAR_TO_MENUBAR = "Can't add menubar to a menubar";

	// Other
	String EMPTY = "";

}
