package org.openmarkov.core.gui.development.tool;


import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * TreeModel for the OpenMarkov Development Window Definition files
 * 
 * @author jlgozalo
 * @version 1.0 jlgozalo 05/04/2009
 */

/**
 * @author jlgozalo
 * @version 1.0 05/04/2009
 */
public class OpenMarkovDevTreeModel implements TreeModel {

	/**
	 * Construct a document tree model.
	 * 
	 * @param doc
	 *            the document
	 */
	public OpenMarkovDevTreeModel(Document doc) {

		this.doc = doc;
	}

	/**
	 * @return the doc
	 */
	public Document getDoc() {

		return doc;
	}

	/**
	 * @param doc
	 *            the doc to set
	 */
	/**
	 * @param doc
	 */
	public void setDoc(Document doc) {

		this.doc = doc;
	}

	/**
	 * @return documentRoot
	 */
	public Object getRoot() {

		return doc.getDocumentElement();
	}

	/**
	 * @param parent
	 * @return number of child
	 */
	public int getChildCount(Object parent) {

		Node node = (Node) parent;
		NodeList list = node.getChildNodes();
		return list.getLength();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
	 */
	public Object getChild(Object parent, int index) {

		Node node = (Node) parent;
		NodeList list = node.getChildNodes();
		return list.item(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object,
	 *      java.lang.Object)
	 */
	public int getIndexOfChild(Object parent, Object child) {

		Node node = (Node) parent;
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (getChild(node, i) == child) {
				return i;
			}
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
	 */
	public boolean isLeaf(Object node) {

		return getChildCount(node) == 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath,
	 *      java.lang.Object)
	 */
	public void valueForPathChanged(TreePath path, Object newValue) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	public void addTreeModelListener(TreeModelListener l) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	public void removeTreeModelListener(TreeModelListener l) {

	}

	/**
	 * The document to be used
	 */
	private Document doc;

}
