/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog.treeadd;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.UniformPotential;
import org.openmarkov.core.model.network.potential.treeadd.Threshold;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDBranch;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDPotential;

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
	
	private StringResource messageStringResource;

	/**
	 * Precision Proxy: every node of the tree could have its own precision (number of decimals)
	 */
	//protected PrecisionProxy precisionProxy;
	
	/**
	 * TODO: Add a new constructor with font and default precision values
	 */
	public TreeADDCellRenderer() {
		super (new BorderLayout ());

		
		messageStringResource =	
				StringResourceLoader.getUniqueInstance().getBundleMessages();
		
	    this.add (leftLabel, BorderLayout.WEST);
	    this.add (rightLabel, BorderLayout.CENTER);

	    leftLabel.setHorizontalAlignment (JLabel.CENTER);
	    leftLabel.setHorizontalTextPosition (JLabel.LEADING);
	    rightLabel.setHorizontalAlignment (JLabel.RIGHT);

	    setBackground (Color.white);
	    
		// TODO: Add a background color attribute
	    textIconFont=new Font("Helvetica", Font.BOLD, 15);
	   // precisionProxy= new PrecisionProxy(2);
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
		
		if( value instanceof TreeADDBranch ) {
			retCode= getTreeCellRendererComponent(tree,(TreeADDBranch) value,selected,expanded,leaf,row,hasFocus);
		}
		else if( value instanceof TreeADDPotential ) {			
			try {
				retCode= getTreeCellRendererComponent(tree,(TreeADDPotential) value,selected,expanded,leaf,row,hasFocus);
			} catch (NotEnoughMemoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, messageStringResource
						.getString( e.getMessage() ),
					messageStringResource.getString( e.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
				
			}
		} else if ( value instanceof UniformPotential ) {
			try {
				retCode= getTreeCellRendererComponent(tree,(UniformPotential) value,selected,expanded,leaf,row,hasFocus);
			} catch (NotEnoughMemoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, messageStringResource
						.getString( e.getMessage() ),
					messageStringResource.getString( e.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			}
			
		}
		else if ( value instanceof TablePotential ) {
			try {
				retCode= getTreeCellRendererComponent(tree,(TablePotential) value,selected,expanded,leaf,row,hasFocus);
			} catch (NotEnoughMemoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, messageStringResource
						.getString( e.getMessage() ),
					messageStringResource.getString( e.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			}
		}
		else {
			throw new RuntimeException("Class not allowed: " + value.getClass().getName());
		}
		
		return retCode;
	}
	
	/** Draws a TreeADDBranch node
	 * @param tree	
	 * @param branch		TreeADDBranch being painted 
	 * @param selected	Selection Flag: true when this treenode is selected
	 * @param expanded	true when this treenode is expanded
	 * @param leaf		true when this treenode is a leaf
	 * @param row		
	 * @param hasFocus
	 * @return
	 * 
	 */
	public Component getTreeCellRendererComponent(JTree tree, TreeADDBranch branch,
			boolean selected,
			boolean expanded, boolean leaf,
			int row, boolean hasFocus) {

		// This kind of nodes won't display an icon
		leftLabel.setIcon (null);

		// TreeADDBranch always have only one child: a potential that it would be a TreeADD or a Potential
		Object child = tree.getModel().getChild(branch,0);
		boolean isLeaf = tree.getModel().isLeaf(child);
		if (!leaf && child instanceof TreeADDPotential && !expanded) {
			rightLabel.setText (" " + ((Potential)child).treeADDString());
		}
		if( isLeaf && !expanded ) {
			getTreeCellRendererComponent (tree, child, selected, expanded, leaf, row, hasFocus);			
		}
		
		// Paint branch information
		if (branch.getTopVariable().getVariableType() == VariableType.FINITE_STATES ||branch.getTopVariable().getVariableType() == VariableType.DISCRETIZED ) {
			leftLabel.setText (getHTML (branch));
		} else if (branch.getTopVariable().getVariableType() == VariableType.NUMERIC ) {
			leftLabel.setText (getHTMLNumeric (branch));
		}
		return this;
		
	}
		
		
	/**Draws a TreeADDPotential or a TablePotential node 
	 * @param tree
	 * @param obj			 Potential Node of the ADD/Tree
	 * @param selected	Selection Flag: true when this treenode is selected
	 * @param expanded	true when this treenode is expanded
	 * @param leaf		true when this treenode is a leaf
	 * @param row
	 * @param hasFocus
	 * @return
	 * @throws NotEnoughMemoryException 
	 * 
	 */
	public Component getTreeCellRendererComponent(JTree tree, Potential obj,
			boolean selected,
			boolean expanded, boolean leaf,
			int row, boolean hasFocus) throws NotEnoughMemoryException {

		if (obj instanceof TreeADDPotential) {
			TreeADDPotential treeADD= (TreeADDPotential)obj;
			/*if (expanded) {*/
				Variable topVariable = treeADD.getTopVariable();
				if (iconsPool.containsKey (topVariable)) {
					leftLabel.setIcon (iconsPool.get (topVariable));
				}
				else {
					String description= topVariable.getName();

					Icon icon= createNodeIcon (treeADD, description);
					iconsPool.put (topVariable, icon);
					leftLabel.setIcon (icon);
				}
			/*} else if (!expanded) {
				rightLabel.setText (" " + ((Potential)treeADD).treeADDString());
			}	*/
		
		} else if (obj instanceof TablePotential) {	
			TablePotential tablePotential = (TablePotential)obj;
			rightLabel.setText (" " + ((Potential)tablePotential).treeADDString());
			/*if (iconsPool.containsKey (variable)) {
				leftLabel.setIcon (iconsPool.get (variable));
			}
			else {
				String description= variable.getName();
				
				Icon icon= createNodeIcon (tablePotential, description);
				iconsPool.put (variable, icon);
				leftLabel.setIcon (icon);
			}*/
					
		} else if (obj instanceof UniformPotential) {
			UniformPotential uniformPotential = (UniformPotential) obj;
			//Variable variable = uniformPotential.getVariable(0);
			//TablePotential tablePotential = new TablePotential(uniformPotential.getVariables(), uniformPotential.getPotentialRole());
			rightLabel.setText (" " + ((Potential)uniformPotential).treeADDString());
			/*if (iconsPool.containsKey (variable)) {
				leftLabel.setIcon (iconsPool.get (variable));
			}
			else {
				String description= variable.getName();
				
				Icon icon= createNodeIcon (uniformPotential, description);
				iconsPool.put (variable, icon);
				leftLabel.setIcon (icon);
				leftLabel.setText(" " + ((Potential)uniformPotential).treeADDString());
				//rightLabel.setText (" " + ((Potential)uniformPotential).treeADDString());
			}*/
			
		}
		
		return this;
	}
	
	/** Create a new icon for a node of the ADD/Tree
	 * @param n	Node of the ADD/Tree
	 * @param description	Description of the variable/potential
	 * @return
	 */
	protected Icon createNodeIcon(Potential potential, String description) {
		Icon icon= null;
		if (potential instanceof TreeADDPotential) {
			TreeADDPotential tree = (TreeADDPotential)potential;
			if( tree.getTopVariable() instanceof Variable ) {
				icon= IconFactory.createChanceIcon (description, textIconFont);			
			}
		}
		
		else if (potential instanceof TablePotential) {
			TablePotential tablePotential = (TablePotential)potential;
			if (tablePotential.getPotentialRole() == PotentialRole.CONDITIONAL_PROBABILITY){
				if (tablePotential.getVariable(0) instanceof Variable) {
					icon= IconFactory.createChanceIcon (description, textIconFont);	
				}
			} else if (tablePotential.getPotentialRole() == PotentialRole.UTILITY) {
				if (tablePotential.getUtilityVariable() instanceof Variable) {
					icon= IconFactory.createChanceIcon (description, textIconFont);	
				}
			}
		}
		else if (potential instanceof UniformPotential) {
			UniformPotential uniformPotential = (UniformPotential)potential;
			if (uniformPotential.getPotentialRole() == PotentialRole.CONDITIONAL_PROBABILITY){
				if (uniformPotential.getVariable(0) instanceof Variable) {
					icon= IconFactory.createChanceIcon (description, textIconFont);	
				}
			} else if (uniformPotential.getPotentialRole() == PotentialRole.UTILITY) {
				if (uniformPotential.getUtilityVariable() instanceof Variable) {
					icon= IconFactory.createChanceIcon (description, textIconFont);	
				}
			}
		}
		/*else if( tree.getObject() instanceof Potential ) {
			// TODO: replace for a table icon?
			icon= IconFactory.createUtilityIcon (description, textIconFont);			
		}*/
		else {
			throw new RuntimeException("Unknown AbstractNode: " + potential.getClass().getName());
		}
		
		return icon;
	}
	
	/**
	 * Creates what is displayed in a branch for discretized or finite states variables
	 */
	public String getHTML(TreeADDBranch treeBranch) {

		String txtIzq="<html><table border=1>";
		Variable topVariable = treeBranch.getTopVariable();

		if (topVariable == null || topVariable.getVariableType() != VariableType.FINITE_STATES ) {
			throw new RuntimeException();
		}
		else {
			String varName= topVariable.getName();
			
					ArrayList<State> branchStates= treeBranch.getBranchStates();
					String varStateNames= "";
					
					if (branchStates.size()>1) {
						varStateNames += "{";
					}
					
				boolean bFirst= true;
					for (State state : branchStates) {
									
						if (bFirst) {
							bFirst= false;
						}
						else {
							varStateNames += ", ";
						}
						
						varStateNames += state.getName();						
					}
					
					if (branchStates.size()>1) {
						varStateNames += "}";
					}
					
					txtIzq+= "<td align=center border=0>" + varName + "=" + varStateNames +"</td>"; 					
				}
			
		txtIzq+="</table></html>";
			
		return txtIzq;
	}
		

	/**
	 * Creates information displayed in a branch for numeric variables
	 */
	public String getHTMLNumeric(TreeADDBranch treeBranch) {

		String txtIzq="<html><table border=1>";
		Variable topVariable = treeBranch.getTopVariable();

		if (topVariable == null || topVariable.getVariableType() != VariableType.NUMERIC ) {
			throw new RuntimeException();
		}
		else {
			
			String varName= topVariable.getName();
			
			Threshold min = treeBranch. getMinThreshold ();
			Threshold max = treeBranch. getMaxThreshold ();
			
			String intervalString= "";
			intervalString += !min.belongsToLeft() ? "[" : "(";
					
			intervalString += min.getLimit();
				
			intervalString += ", ";
						
			intervalString += max.getLimit();
						
			intervalString += max.belongsToLeft() ? "]" : ")";
			
			txtIzq+= "<td align=center border=0>" + varName + "=" + intervalString +"</td>"; 										
			
		}	
		txtIzq+="</table></html>";
		return txtIzq;
	}

}

