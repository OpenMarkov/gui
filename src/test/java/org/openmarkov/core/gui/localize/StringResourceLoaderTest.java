/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.localize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.MissingResourceException;

import org.junit.Test;


/**
 * This class tests the classes
 * {@link openmarkov.gui.localize.StringResourceLoader} and
 * {@link openmarkov.gui.localize.StringResource}.
 * 
 * @author jmendoza
 * @version 1.0
 * @author jlgozalo
 * @version 1.1 jlgozalo. modified as MissingErrorExpectedException is not longer
 * required
 */
public class StringResourceLoaderTest {
	/**
	 * This method gets a correct string identified by its key from a string
	 * resource.
	 * 
	 * @param stringResource string resource from which the string is loaded.
	 * @param key key of the string.
	 * @throws MissingResourceException if the string can't be loaded from the
	 * string resource.
	 */
	private void getCorrectString(StringResource stringResource, String key)
			throws MissingResourceException {
		assertNotNull(stringResource.getString(key));
	}


	/**
	 * This method gets a string identified by its key from the buttons resorce
	 * bundle.
	 * 
	 * @throws MissingResourceException if any string doesn't exist.
	 */
	private void getStringButtons() throws MissingResourceException {
		StringResource stringResource;

		stringResource =
				StringResourceLoader.getUniqueInstance().getBundleButtons();
		getCorrectString(stringResource, "Add.Text.Label");
		getCorrectString(stringResource, "Cancel.Text.Label");
		getCorrectString(stringResource, "Clear.Text.Label");
		getCorrectString(stringResource, "Copy.Text.Label");
		getCorrectString(stringResource, "Delete.Text.Label");
		getCorrectString(stringResource, "Down.Text.Label");
		getCorrectString(stringResource, "Ok.Text.Label");
	}


	/**
	 * This method tests the method getBundleButtons and setLanguage loading
	 * various strings in English and Spanish.
	 * 
	 * @throws MissingResourceException if any of the strings doesn't exist.
	 */
	@Test
	public final void testGetBundleButtons() throws MissingResourceException {
		StringResourceLoader.setLanguage("en");
		getStringButtons();
		StringResourceLoader.setLanguage("es");
		getStringButtons();
	}


	/**
	 * This method tests the method getBundleButtons loading a wrong key.
	 */
	@Test
	public final void testGetBundleButtonsWrong() {
		StringResource bundle;

		StringResourceLoader.setLanguage("en");
		bundle = StringResourceLoader.getUniqueInstance().getBundleButtons();
		String string = bundle.getString("incorrect");
		assertEquals(string, ">>> incorrect <<<");
	}


	/**
	 * This method gets a string identified by its key from the dialogs resorce
	 * bundle.
	 * 
	 * @throws MissingResourceException if any string doesn't exist.
	 */
	private void getStringDialogs() throws MissingResourceException {
		StringResource stringResource;

		stringResource =
				StringResourceLoader.getUniqueInstance().getBundleDialogs();
		getCorrectString(stringResource, "Author.Text.Label");
		getCorrectString(stringResource, "ChainGraph.Text.Mnemonic");
		getCorrectString(stringResource, "Continuous.Text.Label");
		getCorrectString(stringResource, "Defaults.Title.Label");
		getCorrectString(stringResource, "Information.Title.Label");
		getCorrectString(stringResource, "NetworkProperties.Title.Label");
		getCorrectString(stringResource, "Values.Text.Mnemonic");
	}


	/**
	 * This method tests the method getBundleDialogs and setLanguage loading
	 * various strings in English and Spanish.
	 * 
	 * @throws MissingResourceException if any of the strings doesn't exist.
	 */
	@Test
	public final void testGetBundleDialogs() throws MissingResourceException {
		StringResourceLoader.setLanguage("en");
		getStringDialogs();
		StringResourceLoader.setLanguage("es");
		getStringDialogs();
	}


	/**
	 * This method tests the method getBundleDialogs loading a wrong key.
	 */
	@Test
	public final void testGetBundleDialogsWrong() {
		StringResource bundle;

		StringResourceLoader.setLanguage("en");
		bundle = StringResourceLoader.getUniqueInstance().getBundleDialogs();
		String string = bundle.getString("incorrect");
		assertEquals(string, ">>> incorrect <<<");
		
	}


	/**
	 * This method gets a string identified by its key from the menus resorce
	 * bundle.
	 * 
	 * @throws MissingResourceException if any string doesn't exist.
	 */
	private void getStringMenus() throws MissingResourceException {
		StringResource stringResource;

		stringResource =
				StringResourceLoader.getUniqueInstance().getBundleMenus();
		getCorrectString(stringResource, "Edit.ChanceCreation.Label");
		getCorrectString(stringResource, "Edit.Copy.Label");
		getCorrectString(stringResource, "Edit.NodeProperties.Mnemonic");
		getCorrectString(stringResource, "Edit.Paste.Mnemonic");
		getCorrectString(stringResource, "File.Close.Label");
		getCorrectString(stringResource, "File.Mnemonic");
		getCorrectString(stringResource, "View.Label");
	}


	/**
	 * This method tests the method getBundleMenus and setLanguage loading
	 * various strings in English and Spanish.
	 * 
	 * @throws MissingResourceException if any of the strings doesn't exist.
	 */
	@Test
	public final void testGetBundleMenus() throws MissingResourceException {
		StringResourceLoader.setLanguage("en");
		getStringMenus();
		StringResourceLoader.setLanguage("es");
		getStringMenus();
	}


	/**
	 * This method tests the method getBundleMenus loading a wrong key.
	 */
	@Test
	public final void testGetBundleMenusWrong() {
		StringResource bundle;

		StringResourceLoader.setLanguage("en");
		bundle = StringResourceLoader.getUniqueInstance().getBundleMenus();
		String string = bundle.getString("incorrect");
		assertEquals(string, ">>> incorrect <<<");
	}


	/**
	 * This method gets a string identified by its key from the messages resorce
	 * bundle.
	 * 
	 * @throws MissingResourceException if any string doesn't exist.
	 */
	private void getStringMessages() throws MissingResourceException {
		StringResource stringResource;

		stringResource =
				StringResourceLoader.getUniqueInstance().getBundleMessages();
		getCorrectString(stringResource, "Action.MoveNodes.Label");
		getCorrectString(stringResource, "ClipboardNotSet.Text.Label");
		getCorrectString(stringResource, "EmptyState.Text.Label");
		getCorrectString(stringResource, "IconificationVetoed.Text.Label");
		getCorrectString(stringResource, "LoadingNetwork.Text.Label");
		getCorrectString(stringResource, "NodeNotCreated.Text.Label");
		getCorrectString(stringResource, "SelectionVetoed.Text.Label");
	}


	/**
	 * This method tests the method getBundleMessages and setLanguage loading
	 * various strings in English and Spanish.
	 * 
	 * @throws MissingResourceException if any of the strings doesn't exist.
	 */
	@Test
	public final void testGetBundleMessages() throws MissingResourceException {
		StringResourceLoader.setLanguage("en");
		getStringMessages();
		StringResourceLoader.setLanguage("es");
		getStringMessages();
	}


	/**
	 * This method tests the method getBundleMessages loading a wrong key.
	 */
	@Test
	public final void testGetBundleMessagesWrong() {
		StringResource bundle;

		StringResourceLoader.setLanguage("en");
		bundle = StringResourceLoader.getUniqueInstance().getBundleMessages();
		String string = bundle.getString("incorrect");
		assertEquals(string, ">>> incorrect <<<");
	}


	/**
	 * This method gets a string identified by its key from the selectables
	 * resorce bundle.
	 * 
	 * @throws MissingResourceException if any string doesn't exist.
	 */
	private void getStringSelectables() throws MissingResourceException {
		StringResource stringResource;

		stringResource =
				StringResourceLoader.getUniqueInstance().getBundleSelectables();
		getCorrectString(stringResource, "absent.Text.Label");
		getCorrectString(stringResource, "high.Text.Label");
		getCorrectString(stringResource, "mild.Text.Label");
		getCorrectString(stringResource, "other.Text.Label");
		getCorrectString(stringResource, "present.Text.Label");
		getCorrectString(stringResource, "sign.Text.Label");
		getCorrectString(stringResource, "yes.Text.Label");
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
		StringResourceLoader.setLanguage("en");
		getStringSelectables();
		StringResourceLoader.setLanguage("es");
		getStringSelectables();
	}


	/**
	 * This method tests the method getBundleSelectables loading a wrong key.
	 */
	@Test
	public final void testGetBundleSelectablesWrong() {
		StringResource bundle;

		StringResourceLoader.setLanguage("en");
		bundle =
			StringResourceLoader.getUniqueInstance().getBundleSelectables();
		String string = bundle.getString("incorrect");
		assertEquals(string, ">>> incorrect <<<");
	}


	/**
	 * This method gets a string identified by its key from the toolbars resorce
	 * bundle.
	 * 
	 * @throws MissingResourceException if any string doesn't exist.
	 */
	private void getStringToolBars() throws MissingResourceException {
		StringResource stringResource;

		stringResource =
				StringResourceLoader.getUniqueInstance().getBundleToolBars();
		getCorrectString(stringResource, "ChanceCreation.ToolTip.Label");
		getCorrectString(stringResource, "ClipboardCut.ToolTip.Label");
		getCorrectString(stringResource, "DecisionCreation.ToolTip.Label");
		getCorrectString(stringResource, "NewNetwork.ToolTip.Label");
		getCorrectString(stringResource, "ObjectSelection.ToolTip.Label");
		getCorrectString(stringResource, "Redo.ToolTip.Label");
		getCorrectString(stringResource, "UtilityCreation.ToolTip.Label");
	}


	/**
	 * This method tests the method getBundleToolBars and setLanguage loading
	 * various strings in English and Spanish.
	 * 
	 * @throws MissingResourceException if any of the strings doesn't exist.
	 */
	@Test
	public final void testGetBundleToolBars() throws MissingResourceException {
		StringResourceLoader.setLanguage("en");
		getStringToolBars();
		StringResourceLoader.setLanguage("es");
		getStringToolBars();
	}


	/**
	 * This method tests the method getBundleToolBars loading a wrong key.
	 */
	@Test
	public final void testGetBundleToolBarsWrong() {
		StringResource bundle;

		StringResourceLoader.setLanguage("en");
		bundle = StringResourceLoader.getUniqueInstance().getBundleToolBars();
		String string = bundle.getString("incorrect");
		assertEquals(string, ">>> incorrect <<<");
	}
}
