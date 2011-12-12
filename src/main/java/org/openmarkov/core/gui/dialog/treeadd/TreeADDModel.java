/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog.treeadd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.potential.Potential;

public class TreeADDModel implements TreeModel {
	/**
	 * 
	 */
	protected Node tree;
	
	/**
	 * 
	 */
	protected Vector<TreeModelListener> treeModelListeners = new Vector<TreeModelListener>();
	
	/**
	 * 
	 */
	// private HashMap<Pair<Node,Node>,SummaryBox> boxesHash= new HashMap< Pair<Node, Node>,SummaryBox>();
	protected HashMap<Link, SummaryBox> boxesHash= new HashMap<Link, SummaryBox>();
	
	public ArrayList<SummaryBox> getBoxes (Object obj) {
		ArrayList<SummaryBox> result= new ArrayList<SummaryBox>();
		
		for (SummaryBox box : boxesHash.values()) {
			if (box.getSource()==obj) {
				result.add(box);
			}
		}
		
		return result;
	}
	
	public void removeBox (SummaryBox obj) {
		ArrayList<Link> linksToErase= new ArrayList<Link>();
		
		for (Entry<Link, SummaryBox> entry : boxesHash.entrySet()) {
			if (entry.getValue()==obj) {
				linksToErase.add (entry.getKey());
			}
		}
		
		for (Link link : linksToErase) {
			boxesHash.remove (link);
		}		
	}
	/**
	 * @param ADD/Tree
	 */
	public TreeADDModel(Node tree) {
		this.tree= tree;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#getRoot()
	 */
	public Object getRoot() {
		return tree;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
	 */
	public int getChildCount(Object parent) {
		if( isLeaf(parent) ) {
			return 0;
		}
		
		// SummaryBoxes have only one child
		if( parent instanceof SummaryBox ) {
			return 1;
		}
		
		if (!(parent instanceof Node)) {
			throw new RuntimeException("Expected Node class: found " + parent.getClass().getName());
		}
		
		return ((Node) parent).getNumChildren();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
	 */
	public boolean isLeaf(Object node) {
		return node instanceof Potential;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	public void addTreeModelListener(TreeModelListener l) {
		treeModelListeners.addElement(l);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	public void removeTreeModelListener(TreeModelListener l) {
		treeModelListeners.removeElement(l);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
	 */
	public Object getChild(Object parent, int index) {
		if( parent instanceof SummaryBox ) {
			return ((SummaryBox) parent).getSource();
		}
		
		if (!(parent instanceof Node)) {
			throw new RuntimeException("Expected InnerNode class: found " + parent.getClass().getName());
		}
		
		// Get the child at this branch
		Node child= ((Node) parent).getChildren().get(index);
		
		Link link= tree.getGraph().getLink ((Node) parent, child, true);
		SummaryBox summaryBox= boxesHash.get(link);

		if( summaryBox == null ) {
			summaryBox= new SummaryBox (child, (Node) parent, index);
			boxesHash.put (link, summaryBox);	// Don't forget to remove this link when a branch is deleted
		}
		
		return summaryBox;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
	 */
	public int getIndexOfChild(Object parent, Object objChild) {
		if( parent==null || objChild==null ) {
			return -1;
		}
		
		// Every summary box must have only one Node child
		if( parent instanceof SummaryBox ) {
			return ((SummaryBox)parent).getSource()== objChild ? 0 : -1;
		}
		
		if (!(parent instanceof Node)) {
			throw new RuntimeException("Expected InnerNode class: found " + parent.getClass().getName());
		}
		
		Node father= (Node) parent;
		for( int i=0; i< father.getNumChildren(); i++ ) {
			Node child= father.getChildren().get(i);

			if (child==objChild) {
				return i;
			}
		}
		
		return -1;
	}
	
	public void fireNodesChanged(TreePath path) {
        int len = treeModelListeners.size();

        TreeModelEvent e = new TreeModelEvent(this, path);
        
        for (int i = 0; i < len; i++) {
            ((TreeModelListener)treeModelListeners.elementAt(i)).treeNodesChanged(e);
        }
	}

	public void fireTreeStructureChanged(TreePath path) {
        int len = treeModelListeners.size();

        TreeModelEvent e = new TreeModelEvent(this, path);
        
        for (int i = 0; i < len; i++) {
        	((TreeModelListener)treeModelListeners.elementAt(i)).treeStructureChanged(e);
        }
	}
	
	public void fireTreeInsert(TreePath path, Object child) {
		  Object[] children = {child};
		  int index = this.getIndexOfChild(path.getLastPathComponent(), child);
		  int[] indicies = {index};
		  TreeModelEvent e = new TreeModelEvent(this, path, indicies, children);
		  
		  int len = treeModelListeners.size();
		  for (int i = 0; i < len; i++) {
			  ((TreeModelListener)treeModelListeners.elementAt(i)).treeNodesInserted(e);
		  }
	}
	
	public void fireTreeRemove(TreePath path, Object child) {
		Object[] children = {child};
		int index = this.getIndexOfChild(path.getLastPathComponent(), child);
		int[] indicies = {index};
		TreeModelEvent e = new TreeModelEvent(this, path, indicies, children);
  
		int len = treeModelListeners.size();
		for (int i = 0; i < len; i++) {
			((TreeModelListener)treeModelListeners.elementAt(i)).treeNodesRemoved (e);
		}
	}	
	
	private void fireRecursiveNodeChanged (Object obj, Object goal, TreePath path) {
		TreePath newPath= path.pathByAddingChild(obj);
		
		if (obj==goal) {
			fireNodesChanged (path);		// maybe the leaf is contracted: we might need to refresh the summarybox node
			fireNodesChanged (newPath);
			return;
		}
		
		for (int i=0; i< getChildCount(obj); i++) {
			Object child= getChild(obj, i);
			fireRecursiveNodeChanged (child, goal, newPath);
		}
	}

	public void fireRecursiveNodeChanged (Node goal) {
		Node root= (Node) getRoot();
		
		TreePath path= new TreePath (root);

		if (root==goal) {
			fireNodesChanged (path);
			return;
		}
		
		for (int i=0; i< getChildCount(root); i++) {
			Object child= getChild (root, i);
			fireRecursiveNodeChanged (child, goal, path);
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
	 */
	public void valueForPathChanged(TreePath path, Object newValue) {
		throw new UnsupportedOperationException();
	}
}
