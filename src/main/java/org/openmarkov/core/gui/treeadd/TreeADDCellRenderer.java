package org.openmarkov.core.gui.treeadd;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.Potential;

public class TreeADDCellRenderer extends JPanel implements TreeCellRenderer {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Container of SummaryBox' text or the variable icon 
	 */
	private JLabel leftLabel= new JLabel();
	
	/**
	 * Container of leaf data: Potential description or value 
	 */
	private JLabel rightLabel= new JLabel();
	
	/**
	 * Icon repository for every variable node 
	 */
	private HashMap<Variable,Icon> iconsPool= new HashMap<Variable,Icon>();
	
	/**
	 * Font used in icon text
	 */
	private Font textIconFont;

	/**
	 * Precision Proxy: every node of the tree could have its own precision (number of decimals)
	 */
	protected PrecisionProxy precisionProxy;
	
	/**
	 * TODO: Add a new constructor with font and default precision values
	 */
	public TreeADDCellRenderer() {
		super (new BorderLayout ());

	    this.add (leftLabel, BorderLayout.WEST);
	    this.add (rightLabel, BorderLayout.CENTER);

	    leftLabel.setHorizontalAlignment (JLabel.CENTER);
	    leftLabel.setHorizontalTextPosition (JLabel.LEADING);
	    rightLabel.setHorizontalAlignment (JLabel.RIGHT);

	    setBackground (Color.white);
	    
		// TODO: Add a background color attribute
	    textIconFont=new Font("Helvetica", Font.BOLD, 15);
	    precisionProxy= new PrecisionProxy(2);
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected,
			boolean expanded, boolean leaf,
			int row, boolean hasFocus) {

		leftLabel.setText (null);
		rightLabel.setText (null);

		Component retCode= null;
		
		if( value instanceof SummaryBox ) {
			retCode= getTreeCellRendererComponent(tree,(SummaryBox) value,selected,expanded,leaf,row,hasFocus);
		}
		else if( value instanceof Node ) {			
			retCode= getTreeCellRendererComponent(tree,(Node) value,selected,expanded,leaf,row,hasFocus);
		}
		else {
			throw new RuntimeException("Class not allowed: " + value.getClass().getName());
		}
		
		return retCode;
	}
	
	/** Draws a SummaryBox node
	 * @param tree	
	 * @param r		SummaryBox being painted 
	 * @param selected	Selection Flag: true when this treenode is selected
	 * @param expanded	true when this treenode is expanded
	 * @param leaf		true when this treenode is a leaf
	 * @param row		
	 * @param hasFocus
	 * @return
	 * 
	 */
	public Component getTreeCellRendererComponent(JTree tree, SummaryBox r,
			boolean selected,
			boolean expanded, boolean leaf,
			int row, boolean hasFocus) {

		// This kind of nodes won't display an icon
		leftLabel.setIcon (null);

		// SummaryBox always have only one child: the variable node
		Node child= (Node) tree.getModel().getChild(r,0);
		boolean isLeaf= (child.getObject() instanceof Potential);
		
		// Display the leaf data in the right label, to present information in a compressed way
		if( isLeaf && !expanded ) {
			getTreeCellRendererComponent (tree, child, selected, expanded, leaf, row, hasFocus);			
		}

		// Paint the SummaryBox information
		leftLabel.setText (r.getHTML (precisionProxy));
		
		return this;
	}

	/**
	 * @param tree
	 * @param n			Node of the ADD/Tree
	 * @param selected	Selection Flag: true when this treenode is selected
	 * @param expanded	true when this treenode is expanded
	 * @param leaf		true when this treenode is a leaf
	 * @param row
	 * @param hasFocus
	 * @return
	 * 
	 */
	public Component getTreeCellRendererComponent(JTree tree, Node obj,
			boolean selected,
			boolean expanded, boolean leaf,
			int row, boolean hasFocus) {

		if (!(obj instanceof Node)) {
			throw new RuntimeException("Class Node expected: found " + obj.getClass().getName());
		}
		
		Node n= (Node) obj; 
		
		if (n.getObject() instanceof Variable) {
			Variable var= (Variable) n.getObject();
			
			if (iconsPool.containsKey (var)) {
				leftLabel.setIcon (iconsPool.get (var));
			}
			else {
				String description= var.getName();
				
				Icon icon= createNodeIcon (n, description);
				iconsPool.put (var, icon);
				leftLabel.setIcon (icon);
			}
		}
		else if (n.getObject() instanceof Potential) {
			rightLabel.setText (" " + ((Potential) n.getObject()).treeADDString());
		}
		else {
			throw new RuntimeException("Expected InnerNode or LeafNode class: found " + n.getClass().getName());
		}
		
		return this;
	}
	
	/** Create a new icon for a node of the ADD/Tree
	 * @param n	Node of the ADD/Tree
	 * @param description	Description of the variable/potential
	 * @return
	 */
	protected Icon createNodeIcon(Node n, String description) {
		Icon icon= null;
		
		if( n.getObject() instanceof Variable ) {
			icon= IconFactory.createChanceIcon (description, textIconFont);			
		}
		else if( n.getObject() instanceof Potential ) {
			// TODO: replace for a table icon?
			icon= IconFactory.createUtilityIcon (description, textIconFont);			
		}
		else {
			throw new RuntimeException("Unknown AbstractNode: " + n.getClass().getName());
		}
		
		return icon;
	}
}
