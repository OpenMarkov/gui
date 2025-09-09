/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.configuration;

/**
 * Utility class to store the last open files
 *
 * @author jlgozalo
 * @version 1.0 25 Jul 2009
 */
public class LastOpenFiles {

	/**
	 * maximum number of last open files per OPENMARKOV session
	 */
	// TODO to be configured by an external configuration file
	public static final int MAX_LAST_OPEN_FILES = 9;

	/**
	 * retrieves the name of the file that is located in the position index
	 *
	 * @param index - the position of file in the list of last open files
	 * @return the fileName or empty
	 */
    public static String getFileNameAt(int index) {
        
        return OpenMarkovPreferences.get(OpenMarkovPreferencesKeys.LATEST_OPEN_FILE + index, OpenMarkovPreferences.OPENMARKOV_DIRECTORIES, "");
	}

	/**
	 * @param fileName the FileName to set
	 * @param index    position of the file
	 */
    public static void setFileNameAt(String fileName, int index) {
        
        OpenMarkovPreferences.set(OpenMarkovPreferencesKeys.LATEST_OPEN_FILE + index, fileName, OpenMarkovPreferences.OPENMARKOV_DIRECTORIES);
	}

	/**
	 * reorder the list of last open files considering that if the file was
	 * already open, only some of the files must be reorder
	 *
	 * @param fileName - name of the file to find
	 */
    public static void setLastFileName(String fileName) {
        
        int aux;
        int index;
        int lastIndex;
		if (existLastOpenFiles()) {
			index = getIndexForFilename(fileName);
			lastIndex = getOldestOpenFileIndex();
			if (lastIndex < MAX_LAST_OPEN_FILES) {
				lastIndex++;
			} else {
				lastIndex = MAX_LAST_OPEN_FILES;
			}
			index = (index == -1 ? lastIndex : index);
			for (int i = index; i > 1; i--) {
				aux = i - 1;
                OpenMarkovPreferences.set(OpenMarkovPreferencesKeys.LATEST_OPEN_FILE + i, OpenMarkovPreferences
                                                  .get(OpenMarkovPreferencesKeys.LATEST_OPEN_FILE + aux, OpenMarkovPreferences.OPENMARKOV_DIRECTORIES, ""),
                                          OpenMarkovPreferences.OPENMARKOV_DIRECTORIES);
			}
		}
        OpenMarkovPreferences.set(OpenMarkovPreferencesKeys.LATEST_OPEN_FILE + 1, fileName, OpenMarkovPreferences.OPENMARKOV_DIRECTORIES);
	}

	/**
	 * retrieves the position of a specific file in the list of last open files
	 *
	 * @param fileName - name of the file to find the position
	 * @return index for the filename if exist; otherwise, return -1
	 */
    public static int getIndexForFilename(String fileName) {

		int result = -1;
        int index;

		for (index = 1; index <= MAX_LAST_OPEN_FILES; index++) {
			if (fileName.equals(OpenMarkovPreferences
                                        .get(OpenMarkovPreferencesKeys.LATEST_OPEN_FILE + index, OpenMarkovPreferences.OPENMARKOV_DIRECTORIES, ""))) {
				result = index;
				break;
			}
		}
		return result;
	}

	/**
	 * @return true if there are some last open files; false otherwise
	 */
    public static boolean existLastOpenFiles() {

		boolean result = false;
        String fileName = OpenMarkovPreferences.get(OpenMarkovPreferencesKeys.LATEST_OPEN_FILE + 1, OpenMarkovPreferences.OPENMARKOV_DIRECTORIES, "");
        if (!fileName.isEmpty()) {
			result = true;
		}

		return result;
	}

	/**
	 * @return index the index for the oldest open file
	 */
    public static int getOldestOpenFileIndex() {
        
        int index;

		for (index = 1; index < MAX_LAST_OPEN_FILES; index++) {
            if (OpenMarkovPreferences.get(OpenMarkovPreferencesKeys.LATEST_OPEN_FILE + index, OpenMarkovPreferences.OPENMARKOV_DIRECTORIES, "")
                                     .isEmpty()) {
				index--; // the last one is the previous index
				break;
			}
		}
		return index;
	}
}
