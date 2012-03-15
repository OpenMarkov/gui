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
 * Class that filter only the XLS files.
 * 
 * @author mkpalacio

 */
public class FileFilterXLS extends FileFilterBasic {

	/**
	 * String resource.
	 */
	private StringResource stringResource;

	/**
	 * Extension of the files that match this filter.
	 */
	static final String xlsExtension = "xls";

	/**
	 * Create a new instance and create a new string resource.
	 */
	public FileFilterXLS() {

		stringResource =
			StringResourceLoader.getUniqueInstance().getBundleDialogs();

	}

	/**
	 * Accepts all the directories (by default in OpenMarkovtFileFilter) and files
	 * whose extension is 'xls'.
	 * 
	 * @return true if the file is a directory; false otherwise
	 */
	@Override
	public boolean accept(File file) {

		boolean result = super.accept(file);
		String fileExtension = null;

		if (!result) {
			fileExtension = getExtension(file);

			return (fileExtension.equals(xlsExtension));
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
			+ " (*." + xlsExtension + ")";

	}

	/**
	 * Returns the extension of the files that match this filter.
	 * 
	 * @return accepted extension by the filter.
	 */
	@Override
	protected String getFilterExtension() {

		return xlsExtension;

	}

}

