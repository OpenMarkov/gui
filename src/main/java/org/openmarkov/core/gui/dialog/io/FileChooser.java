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
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.io.format.annotation.FormatManager;

/**
 * This class implements a file chooser dialog file to select OpenMarkov files.
 * 
 * @author jmendoza
 * @author m.arias
 * @version 1.0 jmendoza, marias
 * @version 1.1 jlgozalo - set appropiated variables names and redo For loop to
 *          use enhanced loop syntax
 */
public class FileChooser extends JFileChooser {

	// Attributes
	private final String[] fileExtensions = { "elv", "xml" };

	/**
	 * Static field for serializable class.
	 */
	private static final long serialVersionUID = 9076351651764305920L;

	/**
	 * Directory where the dialog box searchs the files.
	 */
	private static String directoryPath = System.getProperty("user.home");

	/**
	 * String resource.
	 */
	private StringResource stringResource;

	/**
	 * Creates a new file chooser that starts in the current directory,
	 * filtering the files with the file filters.
	 */
	public FileChooser(boolean acceptAllfile) {

		stringResource = StringResourceLoader.getUniqueInstance()
				.getBundleMessages();
		setTextsInLocale();

		FormatManager formatManager = FormatManager.getInstance();
		HashMap<String, String> items = formatManager.getWriters();

		setAcceptAllFileFilterUsed(acceptAllfile);
		for (String item : items.keySet()) {
			addChoosableFileFilter(new FileFilterAll(items.get(item), item));
		}
		setCurrentDirectory(new File(directoryPath));
		rescanCurrentDirectory();

	}

	public FileChooser() {
		this(true);
	}

	/**
	 * Adds the selected file extension to the file name.
	 * 
	 * @param fileName
	 *            name of the file to which add the extension.
	 * @param filter
	 *            selected filter.
	 * @return the file name with the extension.
	 */
	private String addFilterExtension(String fileName, Object filter) {

		String extension = fileName.substring(fileName.lastIndexOf('.') + 1,
				fileName.length());
		for (String element : fileExtensions) {
			if (element.contentEquals(extension)) {

				return fileName;
			}
		}

		return (filter instanceof FileFilterBasic) ? ((FileFilterBasic) filter)
				.addExtension(fileName) : fileName;

	}

	/**
	 * Called when the user opens a file. If the dialog is a save dialog, then
	 * it checks if the file exists. If so, if offers the user to choose if
	 * overwrite the file. Also it saves the directory path of the file opened
	 * or saved.
	 */
	@Override
	public void approveSelection() {

		boolean approved = false;

		setSelectedFile(new File(addFilterExtension(getSelectedFile()
				.getAbsolutePath(), getFileFilter())));

		if (getDialogType() == SAVE_DIALOG) {
			if (getSelectedFile().exists()) {
				if (JOptionPane
						.showConfirmDialog(
								this,
								getSelectedFile().getName()
										+ " "
										+ stringResource
												.getString("OverwriteNetwork.Text.Label"),
								stringResource
										.getString("OverwriteNetwork.Title.Label"),
								JOptionPane.YES_NO_OPTION,
								JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
					approved = true;
				}
			} else {
				approved = true;
			}
		} else if (!getSelectedFile().exists()) {
			JOptionPane.showMessageDialog(
					this,
					getSelectedFile().getName()
							+ " "
							+ stringResource
									.getString("FileNotExists.Text.Label"),
					stringResource.getString("FileNotExists.Title.Label"),
					JOptionPane.WARNING_MESSAGE);
		} else {
			approved = true;
		}
		if (approved) {
			directoryPath = getSelectedFile().getParent();
			super.approveSelection();
		}

	}

	/**
	 * to fix the bug in JFileChooser to display text in different languages the
	 * text of the components must be set explicitily
	 */
	private void setTextsInLocale() {

		StringResource rb = StringResourceLoader.getUniqueInstance()
				.getBundleDialogs();
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
