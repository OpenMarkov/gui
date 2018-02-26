/*
 * Copyright (c) CISIAD, UNED, Spain,  2018. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.localize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.MissingResourceException;

import org.junit.Before;
import org.junit.Test;


/**
 * This class tests the classes
 * {@link StringDatabase.gui.localize.StringResourceLoader} and
 * {@link StringBundle.gui.localize.StringResource}.
 * 
 * @author jmendoza
 * @version 1.0
 * @author jlgozalo
 * @version 1.1 jlgozalo. modified as MissingErrorExpectedException is not longer
 * required
 */
public class StringDatabaseTests {

    StringDatabase stringDatabase = null; 
    
    @Before
    public void setUp() throws Exception {
        stringDatabase = StringDatabase.getUniqueInstance ();
    }
	/**
	 * This method gets a correct string identified by its key from a string
	 * resource.
	 * 
	 * @param stringResource string resource from which the string is loaded.
	 * @param key key of the string.
	 * @throws MissingResourceException if the string can't be loaded from the
	 * string resource.
	 */
	private void getCorrectString(StringDatabase stringDatabase, String key)
			throws MissingResourceException {
		assertNotNull(stringDatabase.getString(key));
	}


	/**
	 * This method gets a string identified by its key from the buttons resorce
	 * bundle.
	 * 
	 * @throws MissingResourceException if any string doesn't exist.
	 */
	private void getStringButtons() throws MissingResourceException {

		getCorrectString(stringDatabase, "Add.Text.Label");
		getCorrectString(stringDatabase, "Cancel.Text.Label");
		getCorrectString(stringDatabase, "Clear.Text.Label");
		getCorrectString(stringDatabase, "Copy.Text.Label");
		getCorrectString(stringDatabase, "Delete.Text.Label");
		getCorrectString(stringDatabase, "Down.Text.Label");
		getCorrectString(stringDatabase, "Ok.Text.Label");
	}


	/**
	 * This method tests the method getBundleButtons and setLanguage loading
	 * various strings in English and Spanish.
	 * 
	 * @throws MissingResourceException if any of the strings doesn't exist.
	 */
	@Test
	public final void testGetBundleButtons() throws MissingResourceException {
	    StringDatabase.getUniqueInstance ().setLanguage("en");
		getStringButtons();
		StringDatabase.getUniqueInstance ().setLanguage("es");
		getStringButtons();
	}


	/**
	 * This method tests the method getBundleButtons loading a wrong key.
	 */
	@Test
	public final void testGetBundleButtonsWrong() {
	    stringDatabase.setLanguage("en");
		String string = stringDatabase.getString("incorrect");
		assertEquals(string, ">>> incorrect <<<");
	}


	/**
	 * This method gets a string identified by its key from the dialogs resorce
	 * bundle.
	 * 
	 * @throws MissingResourceException if any string doesn't exist.
	 */
	private void getStringDialogs() throws MissingResourceException {
		getCorrectString(stringDatabase, "Author.Text.Label");
		getCorrectString(stringDatabase, "ChainGraph.Text.Mnemonic");
		getCorrectString(stringDatabase, "Continuous.Text.Label");
		getCorrectString(stringDatabase, "Defaults.Title.Label");
		getCorrectString(stringDatabase, "Information.Title.Label");
		getCorrectString(stringDatabase, "NetworkProperties.Title.Label");
		getCorrectString(stringDatabase, "Values.Text.Mnemonic");
	}


	/**
	 * This method tests the method getBundleDialogs and setLanguage loading
	 * various strings in English and Spanish.
	 * 
	 * @throws MissingResourceException if any of the strings doesn't exist.
	 */
	@Test
	public final void testGetBundleDialogs() throws MissingResourceException {
	    StringDatabase.getUniqueInstance ().setLanguage("en");
		getStringDialogs();
		StringDatabase.getUniqueInstance ().setLanguage("es");
		getStringDialogs();
	}


	/**
	 * This method tests the method getBundleDialogs loading a wrong key.
	 */
	@Test
	public final void testGetBundleDialogsWrong() {

		stringDatabase.setLanguage("en");
		String string = stringDatabase.getString("incorrect");
		assertEquals(string, ">>> incorrect <<<");
		
	}


	/**
	 * This method gets a string identified by its key from the menus resorce
	 * bundle.
	 * 
	 * @throws MissingResourceException if any string doesn't exist.
	 */
	private void getStringMenus() throws MissingResourceException {

		getCorrectString(stringDatabase, "Edit.ChanceCreation.Label");
		getCorrectString(stringDatabase, "Edit.Copy.Label");
		getCorrectString(stringDatabase, "Edit.NodeProperties.Mnemonic");
		getCorrectString(stringDatabase, "Edit.Paste.Mnemonic");
		getCorrectString(stringDatabase, "File.Close.Label");
		getCorrectString(stringDatabase, "File.Mnemonic");
		getCorrectString(stringDatabase, "View.Label");
	}


	/**
	 * This method tests the method getBundleMenus and setLanguage loading
	 * various strings in English and Spanish.
	 * 
	 * @throws MissingResourceException if any of the strings doesn't exist.
	 */
	@Test
	public final void testGetBundleMenus() throws MissingResourceException {
	    StringDatabase.getUniqueInstance ().setLanguage("en");
		getStringMenus();
		StringDatabase.getUniqueInstance ().setLanguage("es");
		getStringMenus();
	}


	/**
	 * This method tests the method getBundleMenus loading a wrong key.
	 */
	@Test
	public final void testGetBundleMenusWrong() {

		stringDatabase.setLanguage("en");
		String string = stringDatabase.getString("incorrect");
		assertEquals(string, ">>> incorrect <<<");
	}


	/**
	 * This method gets a string identified by its key from the messages resorce
	 * bundle.
	 * 
	 * @throws MissingResourceException if any string doesn't exist.
	 */
	private void getStringMessages() throws MissingResourceException {

		getCorrectString(stringDatabase, "Action.MoveNodes.Label");
		getCorrectString(stringDatabase, "ClipboardNotSet.Text.Label");
		getCorrectString(stringDatabase, "EmptyState.Text.Label");
		getCorrectString(stringDatabase, "IconificationVetoed.Text.Label");
		getCorrectString(stringDatabase, "LoadingNetwork.Text.Label");
		getCorrectString(stringDatabase, "NodeNotCreated.Text.Label");
		getCorrectString(stringDatabase, "SelectionVetoed.Text.Label");
	}


	/**
	 * This method tests the method getBundleMessages and setLanguage loading
	 * various strings in English and Spanish.
	 * 
	 * @throws MissingResourceException if any of the strings doesn't exist.
	 */
	@Test
	public final void testGetBundleMessages() throws MissingResourceException {
	    stringDatabase.setLanguage("en");
		getStringMessages();
		stringDatabase.setLanguage("es");
		getStringMessages();
	}


	/**
	 * This method tests the method getBundleMessages loading a wrong key.
	 */
	@Test
	public final void testGetBundleMessagesWrong() {
	    stringDatabase.setLanguage("en");
		String string = stringDatabase.getString("incorrect");
		assertEquals(string, ">>> incorrect <<<");
	}


	/**
	 * This method gets a string identified by its key from the selectables
	 * resorce bundle.
	 * 
	 * @throws MissingResourceException if any string doesn't exist.
	 */
	private void getStringSelectables() throws MissingResourceException {
		
		getCorrectString(stringDatabase, "absent.Text.Label");
		getCorrectString(stringDatabase, "high.Text.Label");
		getCorrectString(stringDatabase, "mild.Text.Label");
		getCorrectString(stringDatabase, "other.Text.Label");
		getCorrectString(stringDatabase, "present.Text.Label");
		getCorrectString(stringDatabase, "sign.Text.Label");
		getCorrectString(stringDatabase, "yes.Text.Label");
	}


	/**
	 * This method tests the method getBundleSelectables and setLanguage loading
	 * various strings in English and Spanish.
	 * 
	 * @throws MissingResourceException if any of the strings doesn't exist.
	 */
	@Test
	public final void testGetBundleSelectables()
			throws MissingResourceException {
	    stringDatabase.setLanguage("en");
		getStringSelectables();
		stringDatabase.setLanguage("es");
		getStringSelectables();
	}


	/**
	 * This method tests the method getBundleSelectables loading a wrong key.
	 */
	@Test
	public final void testGetBundleSelectablesWrong() {
		
	    stringDatabase.setLanguage("en");
		String string = stringDatabase.getString("incorrect");
		assertEquals(string, ">>> incorrect <<<");
	}


	/**
	 * This method gets a string identified by its key from the toolbars resorce
	 * bundle.
	 * 
	 * @throws MissingResourceException if any string doesn't exist.
	 */
	private void getStringToolBars() throws MissingResourceException {

		getCorrectString(stringDatabase, "ChanceCreation.ToolTip.Label");
		getCorrectString(stringDatabase, "ClipboardCut.ToolTip.Label");
		getCorrectString(stringDatabase, "DecisionCreation.ToolTip.Label");
		getCorrectString(stringDatabase, "NewNetwork.ToolTip.Label");
		getCorrectString(stringDatabase, "ObjectSelection.ToolTip.Label");
		getCorrectString(stringDatabase, "Redo.ToolTip.Label");
		getCorrectString(stringDatabase, "UtilityCreation.ToolTip.Label");
	}


	/**
	 * This method tests the method getBundleToolBars and setLanguage loading
	 * various strings in English and Spanish.
	 * 
	 * @throws MissingResourceException if any of the strings doesn't exist.
	 */
	@Test
	public final void testGetBundleToolBars() throws MissingResourceException {
	    stringDatabase.setLanguage("en");
		getStringToolBars();
		stringDatabase.setLanguage("es");
		getStringToolBars();
	}


	/**
	 * This method tests the method getBundleToolBars loading a wrong key.
	 */
	@Test
	public final void testGetBundleToolBarsWrong() {
		stringDatabase.setLanguage("en");
		String string = stringDatabase.getString("incorrect");
		assertEquals(string, ">>> incorrect <<<");
	}
}
