/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog.treeadd;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.gui.dialog.node.NodePropertiesDialog;
import org.openmarkov.core.gui.dialog.node.PotentialsDialog;
import org.openmarkov.core.gui.util.Utilities;
import org.openmarkov.core.model.graph.Graph;
import org.openmarkov.core.model.graph.LabelledLink;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.treeadd.BranchData;
import org.openmarkov.core.model.network.potential.treeadd.BranchInterval;
import org.openmarkov.core.model.network.potential.treeadd.BranchIntervalComparator;
import org.openmarkov.core.model.network.potential.treeadd.EpsilonValueAproximation;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDBranch;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDPotential;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDPotential2;

/**
 * <code>JScrollPane<code> for creating and modifying <code>TreeADDModel<code>s
 * 
 * @author jfernandez
 * @author myebra
 *
 */
public class TreeADDController extends JScrollPane implements ActionListener {

	protected TreeADDPotential2 treeADDPotentialRoot;	
	protected JTree jTree;
	protected TreeADDCellRenderer cellRenderer;
	
	protected boolean readOnlyMode;
	// Variables of the treeADDPotential root of the tree
	protected ArrayList<Variable> treeVariables;

	private int referenceCount= 0;
	
	/** Shows the tree in read only mode
	 * @param treeADDPotential
	 * @throws CloneNotSupportedException 
	 */
	public TreeADDController (TreeADDPotential2 treeADDPotential) throws CloneNotSupportedException {
		this.treeADDPotentialRoot = (TreeADDPotential2) treeADDPotential.clone();
		readOnlyMode = false;
		treeVariables = treeADDPotential.getVariables();
		setupUserInterface();
	}
	
	void setupUserInterface() {
		TreeADDModel model= new TreeADDModel (treeADDPotentialRoot);
		
		jTree= new JTree (model);//model can be null if we are in a node that don´t have any treeAdd assigned
		
		jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
	    jTree.addTreeExpansionListener(new ADDTreeViewer_tree_treeExpansionAdapter(this));
	    jTree.addTreeWillExpandListener(new ADDTreeViewer_tree_treeWillExpandAdapter(this));
		
		// Allows JTree nodes to accept CR/LF codes
	    jTree.setShowsRootHandles(true);
		jTree.setRowHeight(0);
		
		cellRenderer= new TreeADDCellRenderer(); 
		jTree.setCellRenderer(cellRenderer);
		jTree.setUI(new TreeADDUserInterface());	    
		
		//In case we are opening an existing tree
		for (int i=0; i<jTree.getRowCount(); i++) {
		     jTree.expandRow (i);
		} 

		setViewportView(jTree);
		
		// Menu text initialization
		if (!readOnlyMode) {
			jTree.addMouseListener(new ADDTreeViewer_tree_mouseAdapter(this));
			popupMenu.setInvoker(jTree);
			
			//menu to create the root treeADD start painting the tree
			submenuAddStartTree.setText("Set start node");
			
			//menu to add a subtree to a branch
			submenuAddSubtreeADD.setText("Add subtree");
			submenuSetTopVariable.setText("Set Top Variable");
			
			//menuItem to set a potential to a given branch
			menuItemAddPotential.setText("Add Potential");
			menuItemAddPotential.setActionCommand("AddNewPotential");
			menuItemAddPotential.addActionListener(this);	
			
		}
	}
	
	public TreeADDPotential2 getTreePotential() {
		return treeADDPotentialRoot;
	}
	
	public void tree_treeExpanded(TreeExpansionEvent event) {
		TreePath treepath= event.getPath();
		Object tn1= treepath.getLastPathComponent();
		
		TreeADDModel model= (TreeADDModel) jTree.getModel();
	
		if(!model.isLeaf(tn1)) {//the object is a treeADDPotential
			for (int i = 0; i < model.getChildCount(tn1); i++ ) {//Expand path adding branches to the treeADD path
				Object hijo = model.getChild(tn1,i);//this must be a branch
				TreePath tp2= treepath.pathByAddingChild(hijo);
				jTree.expandPath(tp2);
			}
		} 
		/*
		 * TreePath treepath= event.getPath();
		Object tn1= treepath.getLastPathComponent();
		 * if( tn1 instanceof SummaryBox ) {
			Node n= ((SummaryBox) tn1).getSource();
			
			if( !(n.getObject() instanceof Potential) ) {
				TreeADDModel model= (TreeADDModel) jTree.getModel();
			
				for (int i = 0; i < model.getChildCount(tn1); i++ ) {
					Object hijo = model.getChild(tn1,i);
					TreePath tp2= treepath.pathByAddingChild(hijo);
					jTree.expandPath(tp2);
				}
			}
			else {
				TreeADDModel model= (TreeADDModel) jTree.getModel();
				
				Object hijo = model.getChild(tn1,0);
				TreePath tp2= treepath.pathByAddingChild(hijo);				
				model.fireNodesChanged (tp2);					
			}
		}*/
	}
	
	/**
	 * @param event
	 * @throws ExpandVetoException
	 */
	public void tree_treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
		Object triedToExpand= event.getPath().getLastPathComponent();
		
		if (!(triedToExpand instanceof TreeADDPotential2)) {
			throw new ExpandVetoException(event);//Exception used to stop and expand/collapse from happening.
		}
		
		/*Object o= event.getPath().getLastPathComponent();
		
		if (!(o instanceof SummaryBox)) {
			if (!(o instanceof Node)) {
				throw new RuntimeException("Class Node expected: found " + o.getClass().getName());				
			}
			
			Node n= (Node) o;
			
			if (!(n.getObject() instanceof Potential)) {
				throw new ExpandVetoException(event);//Exception used to stop and expand/collapse from happening.
			}
		}*/
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6230911169585766424L;
	
	protected JPopupMenu popupMenu= new JPopupMenu();
	
	//menu to start painting the treeADD with the panel in blank
	protected JMenu submenuAddStartTree= new JMenu();
	//when clicking a branch you can set a potential or add a subtree to that branch
	protected JMenu submenuAddSubtreeADD= new JMenu();
	protected JMenuItem menuItemAddPotential= new JMenuItem();
	//when adding a subtree
	protected JMenu submenuSetTopVariable= new JMenu();
	
	//submenuAddSubtreeADD.add(submenuSetTopVariable);
	
	
		
	protected void setPopupItemsBranches (MouseEvent e, TreeADDBranch branch) {
		popupMenu.removeAll();
		submenuSetTopVariable.removeAll();
		
		TreeADDPotential2 branchTreeParent = branch.getTreeADDParent();
		ArrayList<Variable> variables = branchTreeParent.getVariables();
		Variable topVariable =  branchTreeParent.getTopVariable();
		
		int count = 0;
		
		for (Variable var : variables) {
			
			if (var != topVariable) {
				count ++;
				}
		}
		
		if (count == 0) {//It means that {variables}-{topVariable} is empty so you can only add to that branch a potential
			popupMenu.add(menuItemAddPotential);
			menuItemAddPotential.addActionListener (this);
			menuItemAddPotential.setActionCommand ("AddPotential");
			
		}else if (count != 0) {//It means that {variables}-{topVariable} is not empty so you can add also a subtree to the branch
			
			for (Variable var : variables) {
				
				if (var != topVariable) {
					JMenuItem posibleTopVariable = new JMenuItem(var.getName());
					posibleTopVariable.addActionListener (this);
					
					posibleTopVariable.setActionCommand ("AddTopVariable");
					submenuSetTopVariable.add (posibleTopVariable);
				}
				
			}
			popupMenu.add(submenuAddSubtreeADD);
			submenuAddSubtreeADD.add(submenuSetTopVariable);
			popupMenu.add(menuItemAddPotential);
			menuItemAddPotential.addActionListener (this);
			menuItemAddPotential.setActionCommand ("AddPotential");
		}
		
			
		
	}
	
	protected void setPopupItemsBlankTree() {
		popupMenu.removeAll();

		submenuAddStartTree.removeAll();
		
		for (Variable v : treeVariables) {
			if (treeADDPotentialRoot.getConditionedVariable()==v) {
				continue;
			}
			
			JMenuItem menu= new JMenuItem(v.getName());
			menu.setActionCommand("SetStartNode");
			menu.addActionListener(this);
			
			submenuAddStartTree.add (menu);
		}
		
		popupMenu.add (submenuAddStartTree);			
	}

	


	// Mouse event detection
	int xx, yy;
	
	/**
	 * 
	 */
	public void actionPerformed(ActionEvent ae) {
		// TODO Auto-generated method stub
		
		String actionComand= ae.getActionCommand();		
		if (actionComand.equals ("SetStartNode")) {
			JMenuItem menu= (JMenuItem) ae.getSource();

			for (Variable v : treeVariables) {
				if (v.getName().equals (menu.getText())) {
					
					//TreeADDPotential2 treeADD = new TreeADDPotential2();
					//En esta acción conservas el treeADD con el que creas el controller
					
					TreeADDModel model= new TreeADDModel (treeADDPotentialRoot);
					jTree.setModel (model); 

					treeADDPotentialRoot.setTopVariable(v);
					return;
				}
			}
			
			throw new RuntimeException("variable not found: " + menu.getText());							
		}
		
		//we can just click on a branch
		
		TreePath path = jTree.getPathForLocation(xx,yy); 
		Object branch= path.getLastPathComponent();
		
		if (actionComand.equals("Addpotential")) {
			actionAddNewPotentialToBranch (ae, branch, path);	
		}else if (actionComand.equals ("AddTopVariable")) {
			actionAddNewTreeTopVariable (ae, branch, path);			
		}
		
		else {
			throw new RuntimeException("Unexpected menu action found: " + actionComand);			
		}
	}
	
	void actionAddNewPotentialToBranch (ActionEvent ae, Object branch, TreePath path) {
		if (!(branch instanceof TreeADDBranch)) {
			throw new RuntimeException("Expected TreeADDBranch class, found: " + branch.getClass().getName());											
		}
		TablePotential newPotential= null;
		try {
			
			TreeADDPotential2 parentTree = ((TreeADDBranch)branch).getTreeADDParent();
			ArrayList<Variable> parentVariables = parentTree.getVariables();
			Variable parentTopVariable = parentTree.getTopVariable();
			PotentialRole role= parentTree.getPotentialRole();
			
			if (parentTree.isUtility()) {
				if (role!=PotentialRole.UTILITY) {
					throw new RuntimeException("Expected UTILITY PotentialRole, found: " + role);					
				}
				
				newPotential= new TablePotential (null, role);
				newPotential.setUtilityVariable(parentTree.getUtilityVariable());
			}
			else if (role==PotentialRole.CONDITIONAL_PROBABILITY) {
				//ArrayList<Variable> varList= new ArrayList<Variable>();
				ArrayList<Variable> variables = new ArrayList<Variable>();
				for (Variable var : parentVariables ) {
					if (var == parentTopVariable) {
						continue;
					}else {
						variables.add(var);
					}
				}
				
				//varList.add (parentTree.getConditionedVariable());
				//newPotential= new TablePotential (varList, role);
				newPotential= new TablePotential (variables, role);
			}
			else {
				throw new RuntimeException("Unexpected PotentialRole, found: " + role);					
			}

		} catch (NotEnoughMemoryException e) {
			e.printStackTrace();											
		}
		
		TreeADDModel model= (TreeADDModel) jTree.getModel();			
		model.fireTreeInsert(path, newPotential);
		
		jTree.expandPath(path);		
	}
	
	void actionAddNewTreeTopVariable (ActionEvent ae, Object branch, TreePath path) {
		if (!(branch instanceof TreeADDBranch)) {
			throw new RuntimeException("Expected TreeADDBranch class, found: " + branch.getClass().getName());											
		}	
		//find the top variable to create a new tree with that topVariable and expand tree whith new tree
		JMenuItem menuTopVariable= (JMenuItem) ae.getSource();//topVariable
		ArrayList<Variable> parentVariables = ((TreeADDBranch)branch).getTreeADDParent().getVariables();
		Variable parentTopVariable = ((TreeADDBranch)branch).getTreeADDParent().getTopVariable();
		ArrayList<Variable> variables = new ArrayList<Variable>();
		for (Variable var : parentVariables ) {
			if (var == parentTopVariable) {
				continue;
			}else {
				variables.add(var);
			}
		}
		Variable newTreeTopVariable = null;
		for (Variable var : parentVariables ) {
			if (var.getName() == menuTopVariable.getText()) {
				newTreeTopVariable = var;
			} 
		
		}
		TreeADDPotential2 newTree = new TreeADDPotential2(variables, newTreeTopVariable, treeADDPotentialRoot.getPotentialRole());
		TreeADDModel model= (TreeADDModel) jTree.getModel();
		model.fireTreeInsert(path, newTree);
		
		jTree.expandPath(path.pathByAddingChild(newTree));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
/**
 * TODO: Convert to Inner Class of the Viewer?
 */
class ADDTreeViewer_tree_treeExpansionAdapter implements TreeExpansionListener {
	private TreeADDController treeADDController;
	ADDTreeViewer_tree_treeExpansionAdapter (TreeADDController adaptee) {
		this.treeADDController = adaptee;
	}
	
	public void treeExpanded(TreeExpansionEvent event) {
		treeADDController.tree_treeExpanded(event);
	}
	
	public void treeCollapsed(TreeExpansionEvent event) {
	}
}

/**
 * TODO: Convert to Inner Class of the Viewer?
 */
class ADDTreeViewer_tree_treeWillExpandAdapter implements TreeWillExpandListener {
	private TreeADDController treeADDController;
	ADDTreeViewer_tree_treeWillExpandAdapter (TreeADDController adaptee) {
		this.treeADDController = adaptee;
	}
	
	public void treeWillExpand(TreeExpansionEvent event) {
	}
	
	public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
		treeADDController.tree_treeWillCollapse(event);
	}
}

/**
 * @author jfernandez 
 * @author myebra
 *
 */
class ADDTreeViewer_tree_mouseAdapter extends MouseAdapter {
	private TreeADDController treeADDController;
	ADDTreeViewer_tree_mouseAdapter(TreeADDController adaptee) {
		this.treeADDController = adaptee;
	}
	
	public void mousePressed(MouseEvent e) {
		maybeShowPopup(e);
	}
	
	public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e);
	}
	
	private void maybeShowPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			treeADDController.xx= e.getX();
			treeADDController.yy= e.getY();
			
			TreePath path = treeADDController.jTree.getPathForLocation (treeADDController.xx, treeADDController.yy);
			if( path != null ) {
				Object node = path.getLastPathComponent();
				
				if ( node instanceof TreeADDBranch) {
					treeADDController.setPopupItemsBranches(e, (TreeADDBranch) node);
				}
				
				treeADDController.popupMenu.show (e.getComponent(), treeADDController.xx, treeADDController.yy);
			}
			else {
				if (treeADDController.jTree.getModel()==null) {
					treeADDController.setPopupItemsBlankTree();				
					treeADDController.popupMenu.show(e.getComponent(), treeADDController.xx, treeADDController.yy);
				}
			}
		}
	}
}
}