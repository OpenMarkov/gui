package org.openmarkov.core.gui.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.model.network.DefaultStates;
import org.openmarkov.core.model.network.State;


/**
 * This class tests the class {@link openmarkov.gui.networks.DefaultStates}.
 * 
 * @author jmendoza
 */
public class GUIDefaultStatesTest {
	/**
	 * This method initializes the language to English.
	 */
	@Before
	public void setUp() {
		StringResourceLoader.setLanguage("en");
	}


	/**
	 * This method obtains the language-dependent string of a state that is
	 * known in English. Also it gets the language-dependent string of an
	 * unknown state.
	 */
	@Test
	public final void testGetString1() {
		String string;

		string = GUIDefaultStates.getString("mild");
		assertEquals(string, "mild");
		string = GUIDefaultStates.getString("unknown");
		assertEquals(string, "unknown");
	}


	/**
	 * This method obtains the language-dependent strings separated by dashes of
	 * a list of states that are known in English.
	 */
	@Test
	public final void testGetString2() {
		String[] states = {"negative", "positive"};
		String string;

		string = GUIDefaultStates.getString(states);
		assertEquals(string, "Negativo - Positivo");
	}


	/**
	 * This method obtains the language-dependent strings of an array of states
	 * and put them into another array of strings.
	 */
	@Test
	public final void testGetStrings() {
		State[] states = {new State("high"),new State("medium"), new State("low"),
				new State( "unknown")};
		String[] strings;

		strings = GUIDefaultStates.getStrings(states);
		assertEquals(strings[0], "Alto");
		assertEquals(strings[1], "Medio");
		assertEquals(strings[2], "Bajo");
		assertEquals(strings[3], "unknown");
	}


	/**
	 * This method obtains the name of the state using its language-dependent
	 * string.
	 */
	@Test
	public final void testGetStateLanguageDependent() {
		String string;

		string = GUIDefaultStates.getStringLanguageDependent("present");
		assertEquals(string, "present");
		string = GUIDefaultStates.getStringLanguageDependent("absent");
		assertEquals(string, "absent");
		string = GUIDefaultStates.getStringLanguageDependent("yes");
		assertEquals(string, "yes");
		string = GUIDefaultStates.getStringLanguageDependent("no");
		assertEquals(string, "no");
		string = GUIDefaultStates.getStringLanguageDependent("positive");
		assertEquals(string, "positive");
		string = GUIDefaultStates.getStringLanguageDependent("negative");
		assertEquals(string, "negative");
	}


	/**
	 * This method obtains the name of the states of an array using their
	 * language-dependent strings.
	 */
	@Test
	public final void testGetStatesLanguageDependent() {
		String[] strings = {"present", "absent", "yes", "no", "positive",
				"negative"};
		String[] states;

		states = GUIDefaultStates.getStringsLanguageDependent(strings);
		assertEquals(states[0], "present");
		assertEquals(states[1], "absent");
		assertEquals(states[2], "yes");
		assertEquals(states[3], "no");
		assertEquals(states[4], "positive");
		assertEquals(states[5], "negative");
	}


	/**
	 * This method obtains the language-dependent strings separated by dashes of
	 * all groups of the known states.
	 */
	@Test
	public final void testGetListStrings() {
		String[] strings;

		strings = GUIDefaultStates.getListStrings();
		assertEquals(strings.length, 6);
		assertEquals(strings[0], "Ausente - Presente");
		assertEquals(strings[1], "No - Sí");
		assertEquals(strings[2], "Negativo - Positivo");
		assertEquals(strings[3], "Ausente - Leve - Moderado - Severo");
		assertEquals(strings[4], "Bajo - Medio - Alto");
		assertEquals(strings[5], "Otros");
	}


	/**
	 * This method gets all groups of states by their indexes.
	 */
	@Test
	public final void testGetByIndex() {
		String[] states;

		states = DefaultStates.getByIndex(0);
		assertEquals(states.length, 2);
		assertEquals(states[1], "present");
		assertEquals(states[0], "absent");
		states = DefaultStates.getByIndex(1);
		assertEquals(states.length, 2);
		assertEquals(states[1], "yes");
		assertEquals(states[0], "no");
		states = DefaultStates.getByIndex(2);
		assertEquals(states.length, 2);
		assertEquals(states[1], "positive");
		assertEquals(states[0], "negative");
		states = DefaultStates.getByIndex(3);
		assertEquals(states.length, 4);
		assertEquals(states[3], "severe");
		assertEquals(states[2], "moderate");
		assertEquals(states[1], "slight");
		assertEquals(states[0], "absent");
		states = DefaultStates.getByIndex(4);
		assertEquals(states.length, 3);
		assertEquals(states[2], "high");
		assertEquals(states[1], "medium");
		assertEquals(states[0], "low");
		states = DefaultStates.getByIndex(5);
		assertEquals(states.length, 1);
		assertEquals(states[0], "nonamed");
		states = DefaultStates.getByIndex(6);
		assertNull(states);
	}


	/**
	 * This method checks that all the groups of states correspond to their
	 * index. The groups contains the name of the states not the
	 * language-dependent strings.
	 */
	@Test
	public final void testGetIndex() {
		assertEquals(DefaultStates.getIndex(
				new State[] {new State("absent"), new State("present")}), 0);
		assertEquals(DefaultStates.getIndex(new State[] {new State("no"),
				new State("yes")}), 1);
		assertEquals(DefaultStates.getIndex(
				new State[] {new State("negative"),
						new State("positive")}), 2);
		assertEquals(DefaultStates.getIndex(
				new State[] {new State("mild"),
						new State("severe")}), 5); //do not exists
		assertEquals(DefaultStates.getIndex(
				new State[] {new State("low"),
						new State("medium"), new State("high")}), 4);;
		assertEquals(DefaultStates.getIndex(new State []{new State("noname")}),
				5);
		assertEquals(DefaultStates.getIndex(new State[] {}), 5);
	}


	/**
	 * This method checks that all the groups of states correspond to their
	 * index. The groups contains the language-dependent strings not the name of
	 * the states.
	 */
	@Test
	public final void testGetIndexLanguageDependent() {
		assertEquals(GUIDefaultStates.getIndexLanguageDependent(
				new String[] {"Ausente", "Presente"}), 0);
		assertEquals(GUIDefaultStates.getIndexLanguageDependent(
				new String[] {"No", "Sí"}), 1);
		assertEquals(GUIDefaultStates.getIndexLanguageDependent(
				new String[] {"Negativo", "Positivo"}), 2);
		assertEquals(GUIDefaultStates.getIndexLanguageDependent(
				new String[] {"Mild", "Severe"}), -1); //do not exists "others"
		assertEquals(GUIDefaultStates.getIndexLanguageDependent(
				new String[] {"Bajo", "Medio", "Alto"}), 4);
		assertEquals(GUIDefaultStates.getIndexLanguageDependent(
				new String[] {"Nonamed"}), -1);
		assertEquals(GUIDefaultStates.getIndexLanguageDependent(
				new String[] {"Unknown"}), -1);
	}
}
