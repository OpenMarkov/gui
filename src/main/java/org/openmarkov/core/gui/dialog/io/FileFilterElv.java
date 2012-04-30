/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog.io;


import java.io.File;

import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;




/**
 * Class that filter only the files that contain Elvira nets.
 * 
 * @author jmendoza
 * @version 1.0 jmendoza
 * @version 1.1 jlgozalo - fix public methods
 */
public class FileFilterElv extends FileFilterBasic {

	/**
	 * String resource.
	 */
	private StringResource stringResource;

	/**
	 * Extension of the files that match this filter.
	 */
	public static final String elviraExtension = "elv";

	/**
	 * Create a new instance and create a new string resource.
	 */
	public FileFilterElv() {

		stringResource =
			StringResourceLoader.getUniqueInstance().getBundleDialogs();

	}

	/**
	 * Accepts all the directories (by default in OpenMarkovtFileFilter) and files
	 * whose extension is 'elv'.
	 * 
	 * @return true if the file is a directory; false otherwise
	 */
	@Override
	public boolean accept(File file) {

		boolean result = super.accept(file);
		String fileExtension = null;

		if (!result) {
			fileExtension = getExtension(file);

			return (fileExtension.equals(elviraExtension));
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

		return stringResource.getString("FileExtension.Elvira.Description")
			+ " (*." + elviraExtension + ")";

	}

	/**
	 * Returns the extension of the files that match this filter.
	 * 
	 * @return accepted extension by the filter.
	 */
	@Override
	public String getFilterExtension() {

		return elviraExtension;

	}
}
