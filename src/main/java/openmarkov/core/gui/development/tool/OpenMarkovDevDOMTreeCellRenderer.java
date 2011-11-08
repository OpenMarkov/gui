package openmarkov.core.gui.development.tool;


import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultTreeCellRenderer;

import openmarkov.core.gui.OpenMarkov;

import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;


// ESCA-JAVA0234:
/**
 * This class renders an XML node.
 * 
 * @author jlgozalo
 * @version 1.0 jlgozalo
 */
@SuppressWarnings("serial")
public class OpenMarkovDevDOMTreeCellRenderer extends DefaultTreeCellRenderer {

	// ESCA-JAVA0138: supprising warning for serial id
	// ESCA-JAVA0075: supprssing warning for overriding names of fields
	/**
	 * new getTreeCellRendererComponent for OPENMARKOV Dev Environment
	 * 
	 * @return a TreeCellRendererComponent
	 */
	// Overrides
	public Component getTreeCellRendererComponent(JTree tree, Object value,
													boolean selected,
													boolean expanded,
													boolean leaf, int row,
													boolean hasFocus) {

		Node node = (Node) value;
		if (node instanceof Element) {
			return elementPanel((Element) node);
		}
		super.getTreeCellRendererComponent(
			tree, value, selected, expanded, leaf, row, hasFocus);
		if (node instanceof CharacterData) {
			String text = characterString((CharacterData) node);
			setText(text);

		} else {
			setText(node.getClass() + ":" + node.toString());
		}
		this.setLeafIcon(new ImageIcon(OpenMarkov.class
			.getResource(LEAF_ICON)));
		this.setOpenIcon(new ImageIcon(OpenMarkov.class
			.getResource(OPEN_ICON)));
		this.setClosedIcon(new ImageIcon(OpenMarkov.class
			.getResource(CLOSED_ICON)));
		return this;
	}

	/**
	 * displays the elements
	 * 
	 * @param e
	 *            the element to read
	 * @return panel to display the file in a menu format
	 */
	public static JPanel elementPanel(Element e) {

		JPanel panel = new JPanel();
		panel.add(new JLabel("Element: " + e.getTagName()));
		final NamedNodeMap map = e.getAttributes();
		panel.add(new JTable(new AbstractTableModel() {

			public int getRowCount() {

				return map.getLength();
			}

			public int getColumnCount() {

				return 2;
			}

			public Object getValueAt(int r, int c) {

				return c == 0 ? map.item(r).getNodeName() : map.item(r)
					.getNodeValue();
			}
		}));
		return panel;
	}

	/**
	 * displays the value of the element
	 * 
	 * @param node
	 *            the node to display the value
	 * @return string with the value of the element
	 */
	public static String characterString(CharacterData node) {

		StringBuilder builder = new StringBuilder(node.getData());
		int i = 0;
		for (i = 0; i < builder.length(); i++) {
			if (builder.charAt(i) == '\r') {
				builder.replace(i, i + 1, "\\r");
				// ESCA-JAVA0119: the "i" must not be added within the
				// loop->allow
				i++;
			} else if (builder.charAt(i) == '\n') {
				builder.replace(i, i + 1, "\\n");
				// ESCA-JAVA0119: the "i" must not be added within the
				// loop->allow
				i++;
			} else if (builder.charAt(i) == '\t') {
				builder.replace(i, i + 1, "\\t");
			}
		}
		if (node instanceof CDATASection) {
			builder.insert(0, "CDATASection: ");

		} else if (node instanceof Text) {
			// builder.insert(0, "Text: ");
			builder.delete(0, i);
			builder.insert(0, "");
		} else if (node instanceof Comment) {
			builder.insert(0, "Comment: ");
		}

		return builder.toString();
	}

	private static final String LEAF_ICON =
		"/openmarkov/gui/development/resources/white-component.gif";
	private static final String CLOSED_ICON =
		"/openmarkov/gui/development/resources/book-closed.gif";
	private static final String OPEN_ICON =
		"/openmarkov/gui/development/resources/blue-book-closed.gif";
}
