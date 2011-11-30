package org.openmarkov.core.gui.dialog.io;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;


/**
 * This class implements the base code for all the file filters of the
 * application. By default, it accepts all the directories and initialises the
 * string resource.
 * 
 * @author jmendoza
 * @version 1.0
 */
public class FileFilterAll extends FileFilterBasic {
	/**
	 * String resource.
	 */
	private StringResource stringResource;

	/**
	 * Extension of the files that match this filter.
	 */
	private String formatExtension = "" ;

	/**
	 * Create a new instance and create a new string resource.
	 */
	public FileFilterAll(String extension) {
		formatExtension = extension;
		stringResource =
			StringResourceLoader.getUniqueInstance().getBundleDialogs();

	}

	/**
	 * Accepts all the directories (by default in OpenMarkovtFileFilter) and files
	 * whose extension is 'pgmx'.
	 * 
	 * @return true if the file is a directory; false otherwise
	 */
	@Override
	public boolean accept(File file) {

		boolean result = super.accept(file);
		String fileExtension = null;

		if (!result) {
			fileExtension = getExtension(file);
			return (fileExtension.equals(formatExtension));
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
			+ " (*." + formatExtension + ")";

	}

	/**
	 * Returns the extension of the files that match this filter.
	 * 
	 * @return accepted extension by the filter.
	 */
	@Override
	protected String getFilterExtension() {

		return formatExtension;

	}

}
