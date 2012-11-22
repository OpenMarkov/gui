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

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;

/**
 * This class implements a file chooser dialog file to select OpenMarkov files.
 * 
 * @author jmendoza
 * @author m.arias
 * @version 1.0 jmendoza, marias
 * @version 1.1 jlgozalo - set appropriate variables names and redo For loop to
 *          use enhanced loop syntax
 */
public abstract class FileChooser extends JFileChooser {

	/**
	 * Static field for serializable class.
	 */
	private static final long serialVersionUID = 9076351651764305920L;

	/**
	 * Directory where the dialog box searchs the files.
	 */
	private static String directoryPath = System.getProperty("user.home");

	/**
	 * Creates a new file chooser that starts in the current directory,
	 * filtering the files with the file filters.
	 */
	public FileChooser(boolean acceptAllfile) {
		setTextsInLocale();
		setAcceptAllFileFilterUsed(acceptAllfile);
		setCurrentDirectory(new File(directoryPath));
		rescanCurrentDirectory();
	}

	/**
	 * to fix the bug in JFileChooser to display text in different languages the
	 * text of the components must be set explicitly
	 */
	private void setTextsInLocale() {

		StringResource rb = StringResourceLoader.getUniqueInstance().getBundleDialogs();
		UIManager.put("FileChooser.cancelButtonText",
				rb.getString("FileChooser.cancelButtonText"));
		UIManager.put("FileChooser.cancelButtonToolTipText",
				rb.getString("FileChooser.cancelButtonToolTipText"));
		UIManager.put("FileChooser.detailsViewActionLabelText",
				rb.getString("FileChooser.detailsViewActionLabelText"));
		UIManager.put("FileChooser.detailsViewButtonToolTipText",
				rb.getString("FileChooser.detailsViewButtonToolTipText"));
		UIManager.put("FileChooser.fileNameLabelText",
				rb.getString("FileChooser.fileNameLabelText"));
		UIManager.put("FileChooser.filesOfTypeLabelText",
				rb.getString("FileChooser.filesOfTypeLabelText"));
		UIManager.put("FileChooser.helpButtonText",
				rb.getString("FileChooser.helpButtonText"));
		UIManager.put("FileChooser.helpButtonToolTipText",
				rb.getString("FileChooser.helpButtonToolTipText"));
		UIManager.put("FileChooser.homeFolderToolTipText",
				rb.getString("FileChooser.homeFolderToolTipText"));
		UIManager.put("FileChooser.listViewActionLabelText",
				rb.getString("FileChooser.listViewActionLabelText"));
		UIManager.put("FileChooser.listViewButtonToolTipTextlist",
				rb.getString("FileChooser.newFolderToolTipText"));
		UIManager.put("FileChooser.lookInLabelText",
				rb.getString("FileChooser.lookInLabelText"));
		UIManager.put("FileChooser.newFolderActionLabelText",
				rb.getString("FileChooser.newFolderActionLabelText"));
		UIManager.put("FileChooser.newFolderToolTipText",
				rb.getString("FileChooser.newFolderToolTipText"));
		UIManager.put("FileChooser.openButtonTextOpen",
				rb.getString("FileChooser.openButtonTextOpen"));
		UIManager.put("FileChooser.openButtonToolTipText",
				rb.getString("FileChooser.openButtonToolTipText"));
		UIManager.put("FileChooser.refreshActionLabelText",
				rb.getString("FileChooser.refreshActionLabelText"));
		UIManager.put("FileChooser.saveButtonTextSave",
				rb.getString("FileChooser.saveButtonTextSave"));
		UIManager.put("FileChooser.saveButtonToolTipText",
				rb.getString("FileChooser.saveButtonToolTipText"));
		UIManager.put("FileChooser.upFolderToolTipText",
				rb.getString("FileChooser.upFolderToolTipText"));
		UIManager.put("FileChooser.updateButtonText",
				rb.getString("FileChooser.updateButtonText"));
		UIManager.put("FileChooser.updateButtonToolTipText",
				rb.getString("FileChooser.updateButtonToolTipText"));
		UIManager.put("FileChooser.viewMenuLabelText",
				rb.getString("FileChooser.viewMenuLabelText"));

	}

	public void setFileFilter(String extension) {
		for(FileFilter filter : getChoosableFileFilters())
		{
			if(filter instanceof FileFilterBasic &&
					((FileFilterBasic)filter).getFilterExtension().equalsIgnoreCase(extension))
			{
				setFileFilter(filter);
			}
		}
	}
}
