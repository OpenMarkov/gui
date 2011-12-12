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
 * Class that filter only the files that contain XML nets.
 * 
 * @author mkpalacio
 
 */
public class FileFilterPGMX extends FileFilterBasic {

	/**
	 * String resource.
	 */
	private StringResource stringResource;

	/**
	 * Extension of the files that match this filter.
	 */
	static final String pgmxExtension = "pgmx";

	/**
	 * Create a new instance and create a new string resource.
	 */
	public FileFilterPGMX() {

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

			return (fileExtension.equals(pgmxExtension));
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
			+ " (*." + pgmxExtension + ")";

	}

	/**
	 * Returns the extension of the files that match this filter.
	 * 
	 * @return accepted extension by the filter.
	 */
	@Override
	protected String getFilterExtension() {

		return pgmxExtension;

	}

}
