package org.openmarkov.core.gui.dialog.io;


import java.io.File;

import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;




/**
 * Class that filter only the files that contain XML nets.
 * 
 * @author manuel
 * @version 1.0 manuel
 * @version 1.1 jlgozalo Fix comments
 */
public class FileFilterXML extends FileFilterBasic {

	/**
	 * String resource.
	 */
	private StringResource stringResource;

	/**
	 * Extension of the files that match this filter.
	 */
	static final String xmlExtension = "xml";

	/**
	 * Create a new instance and create a new string resource.
	 */
	public FileFilterXML() {

		stringResource =
			StringResourceLoader.getUniqueInstance().getBundleDialogs();

	}

	/**
	 * Accepts all the directories (by default in OpenMarkovtFileFilter) and files
	 * whose extension is 'xlm'.
	 * 
	 * @return true if the file is a directory; false otherwise
	 */
	@Override
	public boolean accept(File file) {

		boolean result = super.accept(file);
		String fileExtension = null;

		if (!result) {
			fileExtension = getExtension(file);

			return (fileExtension.equals(xmlExtension));
		}

		return true;

	}

	/**
	 * Returns the description of the OpenMarkov files
	 * 
	 * @return a string representing the description of the files type
	 */
	@Override
	public String getDescription() {

		return stringResource.getString("OpenMarkovFileExtension.Description")
			+ " (*." + xmlExtension + ")";

	}

	/**
	 * Returns the extension of the files that match this filter.
	 * 
	 * @return accepted extension by the filter.
	 */
	@Override
	protected String getFilterExtension() {

		return xmlExtension;

	}

}
