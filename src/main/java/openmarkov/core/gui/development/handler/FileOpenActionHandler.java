package openmarkov.core.gui.development.handler;


import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import openmarkov.core.gui.development.environment.OpenMarkovDevEnv;
import openmarkov.core.gui.development.tool.OpenMarkovDevDOMTreeCellRenderer;
import openmarkov.core.gui.development.tool.OpenMarkovDevTreeModel;
import openmarkov.core.gui.menutoolbar.sax.MenuItemDevAdapter;

import org.w3c.dom.Document;



/**
 * Handles the dynamic activation inside the File Open Action in the menu
 * 
 * @author jlgozalo
 * @version 1.0 05/04/2009
 */
public class FileOpenActionHandler extends MenuItemDevAdapter {

	/**
	 * This method is called when the File New Action in the Menu is activated.
	 */
	public void itemActivated(JMenuItem item, ActionEvent event, String sCommand) {

		setAParentFrame(OpenMarkovDevEnv.getOpenMarkovDevEnvFrame());
		setAOpenMarkovDevEnvProperties(OpenMarkovDevEnv.getOpenMarkovDevEnvProperties());
		openFile();
	}

	/**
	 * Open a file and load the document.
	 */
	private void openFile() {

		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));

		chooser.setFileFilter(new FileFilter() {

			public boolean accept(File f) {

				return f.isDirectory()
					|| f.getName().toLowerCase().endsWith(".xml");
			}

			public String getDescription() {

				return "XML files";
			}
		});

		int r = chooser.showOpenDialog(getAParentFrame());
		if (r != JFileChooser.APPROVE_OPTION) {
			return;
		}
		final File file = chooser.getSelectedFile();
		getAOpenMarkovDevEnvProperties().getProperties().put(
			"WorkingFileName", file);
		// System.out.println(getACarmenDevEnvProperties().toString());
		new SwingWorker<Document, Void>() {

			protected Document doInBackground() throws Exception {

				/**
				 * builder for the XML Document
				 */
				DocumentBuilder aDocumentBuilder = null;
				DocumentBuilderFactory factory =
					DocumentBuilderFactory.newInstance();
				aDocumentBuilder = factory.newDocumentBuilder();

				return aDocumentBuilder.parse(file);
			}

			protected void done() {

				try {
					Document doc = get();
					JTree tree = new JTree(new OpenMarkovDevTreeModel(doc));
					tree.setCellRenderer(new OpenMarkovDevDOMTreeCellRenderer());
					// tree.addTreeSelectionListener(getAparentFrame().getRightPanel());
					getAParentFrame().getLeftPanel().setViewportView(
						new JScrollPane(tree));

				} catch (Exception e) {
					JOptionPane.showMessageDialog(getAParentFrame(), e);
				}
			}
		}.execute();
	}

}