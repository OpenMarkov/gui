package org.openmarkov.core.gui.loader.saxdoc;


import java.io.File;
import java.io.InputStream;
import java.net.URI;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;


/**
 * SAXMenuListLoader implements a Menu Loader from XML external file to help in
 * the Menu definition. It reads from the file the menu structure and create a
 * parser with a specific handler class (SAXDataListDocHandler). In such way, it
 * is not required to implement specific classes for Menus inside the
 * application source code.
 * 
 * @author jlgozalo
 * @version 1.0
 */

public class SaxDataListLoader {

	private SAXParserFactory factory;
	private SAXParser parser;
	private SAXDataListDocHandler handler;

	/** SaxDataListLoader unique instance. Used in singleton pattern. */
	private static SaxDataListLoader saxDataListLoader = null;

	/**
	 * constructor
	 */
	private SaxDataListLoader() {

		createParser();
	}

	/** @return SaxMenuLoader unique instance (singleton pattern). */
	public static SaxDataListLoader getUniqueInstance() {

		if (saxDataListLoader == null) {
			saxDataListLoader = new SaxDataListLoader();
		}
		return saxDataListLoader;
	}

	/**
	 * Load menus from an URI
	 * 
	 * @param uri
	 *            URI of the menu to be loaded
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
			System.err.println("SaxMenuFactory.loadMenus: "
				+ uri.toASCIIString() + ": " + ex.getMessage());

		}
	}

	/**
	 * Load menus from a file
	 * 
	 * @param file
	 *            The file to be loaded
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
			System.err.println("SaxMenuFactory.loadMenus: " + file + ": "
				+ ex.getMessage());
		}
	}

	/**
	 * Load menus from a file
	 * 
	 * @param sFilename
	 *            The name of the file to be loaded
	 */
	public void loadData(String sFilename) {

		loadData(new File(sFilename));
	}

	/**
	 * Load menus from an input stream
	 * 
	 * @param istream
	 *            The Stream where the menu will be loaded
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
			System.err.println("SaxMenuFactory.loadMenus: " + istream + ": "
				+ ex.getMessage());
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
			handler = SAXDataListDocHandler.getUniqueInstance();
		} catch (Exception ex) {
			System.err
				.println("SaxMenuLoader.createParser: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}