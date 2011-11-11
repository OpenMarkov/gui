package org.openmarkov.core.gui.treeadd;

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
import org.openmarkov.core.gui.util.Util;
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
import org.openmarkov.core.model.network.potential.treeadd.TreeADDPotential;

public class TreeADDBuilder extends JScrollPane implements ActionListener {

	protected TreeADDPotential treePotential;	
	protected JTree tree;
	protected TreeADDCellRenderer cellRenderer;
	
	protected boolean readOnlyMode;
	protected ArrayList<Variable> treeVariables;

	private int referenceCount= 0;
	
	/** Shows the tree in read only mode
	 * @param treePotential
	 */
	public TreeADDBuilder (Potential potential) {
		readOnlyMode= false;
		treeVariables= potential.getVariables();
		
		if (potential instanceof TreeADDPotential) {
			try {
				treePotential= new TreeADDPotential ((TreeADDPotential) potential);

			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			treePotential= new TreeADDPotential (potential.getVariables(), potential.getPotentialRole());
			treePotential.setUtilityVariable (potential.getUtilityVariable());
		}

		setupUserInterface();
	}
	
	void setupUserInterface() {
		TreeADDModel model= null;
		
		if (treePotential.getGraph().getNumNodes()>0) {
			model= new TreeADDModel (treePotential.getRoot());
		}
		tree= new JTree (model);

		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
	    tree.addTreeExpansionListener(new ADDTreeViewer_tree_treeExpansionAdapter(this));
	    tree.addTreeWillExpandListener(new ADDTreeViewer_tree_treeWillExpandAdapter(this));
		
		// Allows JTree nodes to accept CR/LF codes
	    tree.setShowsRootHandles(true);
		tree.setRowHeight(0);
		
		cellRenderer= new TreeADDCellRenderer(); 
		tree.setCellRenderer(cellRenderer);
		tree.setUI(new TreeADDUserInterface());	    
		
		for (int i=0; i<tree.getRowCount(); i++) {
		     tree.expandRow (i);
		} 

		setViewportView(tree);
		
		// Menu text initialization
		if (!readOnlyMode) {
			tree.addMouseListener(new ADDTreeViewer_tree_mouseAdapter(this));
			popupMenu.setInvoker(tree);
			
			submenuAddStartNode.setText("Set start node");
			submenuAddNewBranch.setText("Add new branch");
			menuitemAddNewTablePotential.setText("Add New Leaf/TablePotential");
			menuitemAddNewTablePotential.setActionCommand("AddNewTablePotential");
			menuitemAddNewTablePotential.addActionListener(this);			

			menuitemSetTablePotentialValues.setText("Set TablePotential Values");
			menuitemSetTablePotentialValues.setActionCommand("SetTablePotentialValues");
			menuitemSetTablePotentialValues.addActionListener(this);

			menuitemChangePotentialReferenceName.setText("Change Potential Reference Name");
			menuitemChangePotentialReferenceName.setActionCommand("ChangePotentialReferenceName");
			menuitemChangePotentialReferenceName.addActionListener(this);
			
			submenuReferencePotential.setText("Add Potential Reference");
			
			submenuChangeVariable.setText("Change variable");
			
			submenuAddNewState.setText("Add new state");
			submenuRemoveState.setText("Remove state");
			
			submenuSplitInterval.setText("Split interval");
			submenuAssignInterval.setText("Assign interval");
			
			menuitemRemoveInterval.setText("Remove interval");
			
			menuitemRemoveBranch.setText("Remove branch");
			menuitemRemoveBranch.setActionCommand("RemoveBranch");
			menuitemRemoveBranch.addActionListener(this);			

			submenuAddNewPotentialVariable.setText("Add New Potential Var");
			submenuRemovePotentialVariable.setText("Remove Potential Var");
		}
	}
	
	public TreeADDPotential getTreePotential() {
		return treePotential;
	}
	
	public void tree_treeExpanded(TreeExpansionEvent event) {
		TreePath tp1= event.getPath();
		Object tn1= tp1.getLastPathComponent();
		
		if( tn1 instanceof SummaryBox ) {
			Node n= ((SummaryBox) tn1).getSource();
			
			if( !(n.getObject() instanceof Potential) ) {
				TreeADDModel model= (TreeADDModel) tree.getModel();
			
				for( int i=0; i< model.getChildCount(tn1); i++ ) {
					Object hijo = model.getChild(tn1,i);
					TreePath tp2= tp1.pathByAddingChild(hijo);
					tree.expandPath(tp2);
				}
			}
			else {
				TreeADDModel model= (TreeADDModel) tree.getModel();
				
				Object hijo = model.getChild(tn1,0);
				TreePath tp2= tp1.pathByAddingChild(hijo);				
				model.fireNodesChanged (tp2);					
			}
		}
	}
	
	/**
	 * @param event
	 * @throws ExpandVetoException
	 */
	public void tree_treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
		Object o= event.getPath().getLastPathComponent();
		
		if (!(o instanceof SummaryBox)) {
			if (!(o instanceof Node)) {
				throw new RuntimeException("Class Node expected: found " + o.getClass().getName());				
			}
			
			Node n= (Node) o;
			
			if (!(n.getObject() instanceof Potential)) {
				throw new ExpandVetoException(event);
			}
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6230911169585766424L;
	
	protected JPopupMenu popupMenu= new JPopupMenu();

	protected JMenu submenuAddStartNode= new JMenu();
	protected JMenu submenuAddNewBranch= new JMenu();
	protected JMenuItem menuitemAddNewTablePotential= new JMenuItem();
	protected JMenuItem menuitemSetTablePotentialValues= new JMenuItem();

	protected JMenu submenuReferencePotential= new JMenu();
	protected JMenuItem menuitemChangePotentialReferenceName= new JMenuItem();
	
	protected JMenu submenuChangeVariable= new JMenu();
	
	protected JMenu submenuAddNewState = new JMenu();
	protected JMenu submenuRemoveState = new JMenu();
	
	protected JMenu submenuSplitInterval = new JMenu();
	protected JMenu submenuAssignInterval = new JMenu();
	
	protected JMenuItem menuitemRemoveInterval = new JMenuItem();
	protected JMenuItem menuitemRemoveBranch= new JMenuItem();
	
	protected JMenu submenuAddNewPotentialVariable = new JMenu();
	protected JMenu submenuRemovePotentialVariable = new JMenu();
	
	protected void setPopupItemsFiniteStates (MouseEvent e, SummaryBox r) {
		Variable var= (Variable) r.parent.getObject();
		
		HashSet<State> statesUsed= new HashSet<State>();
		HashSet<State> statesThisBranch= null;
		
		int numberOfOtherBranchesWithoutStates= 0;
		
		Graph graph= treePotential.getGraph();
		for (Node child : r.parent.getChildren()) {
			Link link= graph.getLink(r.parent, child, true);
			
			if (!(link instanceof LabelledLink)) {
				throw new RuntimeException("LabelledLink class expected, found: " + link.getClass().getName());											
			}
			
			LabelledLink labelledLink= (LabelledLink) link;
			if (!(labelledLink.getLabel() instanceof BranchData)) {
				throw new RuntimeException("Expected BranchData class: found " + labelledLink.getLabel().getClass().getName());
			}
			
			// TODO: test cardinality of variable states and InnerNode
			BranchData branchData= (BranchData) labelledLink.getLabel();
			HashSet<State> branchStates= branchData.getBranchStates();
			
			if (child==r.getSource()) {
				statesThisBranch= branchStates;
				statesUsed.addAll (statesThisBranch);
			}
			else if (branchStates.isEmpty()) {
				numberOfOtherBranchesWithoutStates++;
			}
			else {
				statesUsed.addAll (branchStates);
			}
		}
		
		if (statesThisBranch==null) {
			throw new RuntimeException("Tree structured is corrupted");											
		}

		// The states election might be conditioned by previous branch values
		HashSet<State> hashStates= new HashSet<State>();
		HashSet<Node> visited= new HashSet<Node>();
		findConditionVarStates(r.parent, var, hashStates, visited);
		
		int maxStates= var.getNumStates();
		if (!hashStates.isEmpty()) {
			maxStates= hashStates.size();
		}
		
		submenuAddNewState.removeAll();
		for (State state : var.getStates()) {				
			JMenuItem menu= new JMenuItem (state.getName());
			menu.setActionCommand("AddBranchState");
			menu.addActionListener(this);

			// Disables those states that are not eligibles to be added
			if (hashStates.isEmpty()) {
				menu.setEnabled (!statesUsed.contains (state));
			}
			else if (!hashStates.contains(state)) {
				menu.setEnabled (false);
			}
			else {
				menu.setEnabled (!statesUsed.contains (state));
			}
			
			submenuAddNewState.add (menu);				
		}
		
		if (statesThisBranch.isEmpty()) {
			submenuAddNewState.setEnabled(true);
		}
		else if (numberOfOtherBranchesWithoutStates<maxStates-statesUsed.size()) {
			submenuAddNewState.setEnabled(true);				
		}
		else {
			submenuAddNewState.setEnabled(false);
		}
		
		popupMenu.add (submenuAddNewState);
		
		submenuRemoveState.removeAll();			
		submenuRemoveState.setEnabled (!statesThisBranch.isEmpty());

		// Depending nodes on the same variable under this branch
		hashStates= new HashSet<State>();
		visited= new HashSet<Node>();
		boolean dependingStatesFound= findDependingStates(r.getSource(), var, hashStates, visited);
		
		if (dependingStatesFound) {			
			if (hashStates.isEmpty() || statesThisBranch.size()<=2) {
				// 1. Exists a node in a subtree without any stated add to its branches.
				// In this case, no state will be eligible for deletion
				// 2. If exists a depending node under this branch, it must have at least two states (so the depending node can branch)
				submenuRemoveState.setEnabled (false);
			}
			else {
				for (State state : var.getStates()) {
					JMenuItem menu= new JMenuItem (state.getName());
					menu.setActionCommand("RemoveBranchState");
					menu.addActionListener(this);
		
					// Disables those states that are not eligibles to be deleted
					if (statesThisBranch.contains (state) && !hashStates.contains(state)) {
						menu.setEnabled (true);	
					}
					else {
						menu.setEnabled (false);						
					}
		
					submenuRemoveState.add (menu);				
				}				
			}
		}
		else {
			for (State state : var.getStates()) {
				JMenuItem menu= new JMenuItem (state.getName());
				menu.setActionCommand("RemoveBranchState");
				menu.addActionListener(this);
	
				// Disables those states that are not eligibles to be deleted
				menu.setEnabled (statesThisBranch.contains (state));
	
				submenuRemoveState.add (menu);				
			}				
		}
		
		popupMenu.add (submenuRemoveState);		
	}
	
	protected void setPopupItems(MouseEvent e, SummaryBox r) {
		popupMenu.removeAll();
		
		Variable var= (Variable) r.parent.getObject();
		if (var.getVariableType()==VariableType.FINITE_STATES) {
			setPopupItemsFiniteStates (e, r);
		}
		else if (var.getVariableType()==VariableType.NUMERIC) {
			submenuSplitInterval.removeAll();
			submenuAssignInterval.removeAll();
			
			HashSet<Node> visited= new HashSet<Node>();
			HashSet<BranchInterval> hashIntervals= new HashSet<BranchInterval>();
			findConditionVarInterval (r.getParent(), var, hashIntervals, visited);

			if (hashIntervals.isEmpty()) {
				PartitionedInterval rangeOfVariable= var.getPartitionedInterval();
				BranchInterval fullRangeInterval= new BranchInterval (rangeOfVariable.getMin(), rangeOfVariable.getMax(), rangeOfVariable.isLeftClosed(), rangeOfVariable.isRightClosed());
				
				hashIntervals.add (fullRangeInterval);
			}
			
			for (BranchInterval rangeInterval : hashIntervals) {
				ArrayList<BranchInterval> unassignedIntervals= new ArrayList<BranchInterval>();
				boolean hasPartition= findUnassignedIntervals (rangeInterval, unassignedIntervals, r.parent);
	
				if (!hasPartition) {
					if (unassignedIntervals.size()!=1) {
						throw new RuntimeException("Interval structure is corrupted");					
					}
					
					for (BranchInterval interval : unassignedIntervals) {
						JMenuItem menuSplit= new JMenuItem (interval.toString());
						menuSplit.putClientProperty("interval", interval);
						menuSplit.setActionCommand("SplitInterval");
						menuSplit.addActionListener(this);
						submenuSplitInterval.add (menuSplit);					
					}
				}
				else {
					for (BranchInterval interval : unassignedIntervals) {
						JMenuItem menuSplit= new JMenuItem (interval.toString());
						menuSplit.putClientProperty("interval", interval);
						menuSplit.setActionCommand("SplitInterval");
						menuSplit.addActionListener(this);
						submenuSplitInterval.add (menuSplit);					
						
						JMenuItem menuSet= new JMenuItem (interval.toString());
						menuSet.putClientProperty("interval", interval);
						menuSet.setActionCommand("SetInterval");
						menuSet.addActionListener(this);					
						submenuAssignInterval.add (menuSet);					
					}
				}
			}
			
			submenuSplitInterval.setEnabled (submenuSplitInterval.getItemCount()>0);
			submenuAssignInterval.setEnabled (submenuAssignInterval.getItemCount()>0);
			
			popupMenu.add (submenuSplitInterval);
			popupMenu.add (submenuAssignInterval);
			
			popupMenu.add (menuitemRemoveInterval);
		}
		else {
			throw new RuntimeException("Unexpect variable type found: " + var.getVariableType().toString());							
		}
		
		popupMenu.add (menuitemRemoveBranch);

		TreePath path = tree.getPathForLocation (xx, yy);
		// Option active only if the SummaryBox is collapsed and its child is a TablePotential
		if (tree.isCollapsed(path) && r.getSource().getObject() instanceof TablePotential) {
			popupMenu.add (new JSeparator());
			setPopupItemsTablePotentialNode (e, r.getSource(), "AddNewVariableToPotentialFromSummaryBox", "RemoveVariableFromPotentialFromSummaryBox");
		}
	}	

	void findConditionVars (Node n, HashSet<Variable> hashSet, HashSet<Node> visited) {
		visited.add (n);

		if (n.getObject() instanceof Variable) {
			hashSet.add ((Variable) n.getObject());
		}
		else if (n.getObject() instanceof TablePotential){
			TablePotential tablePotential= (TablePotential) n.getObject();
			hashSet.addAll (tablePotential.getVariables());			
		}
		else {
			throw new RuntimeException("Unexpected node class found: " + n.getObject().getClass().getName());			
		}
		
		for (Node parent : n.getParents()) {
			if (!visited.contains(parent)) {
				findConditionVars (parent, hashSet, visited);
			}
		}
	}

	void findConditionVarStates (Node n, Variable variable, HashSet<State> hashSet, HashSet<Node> visited) {
		if (variable.getVariableType()!=VariableType.FINITE_STATES) {
			throw new RuntimeException("Expected FINITE_STATES Variable type: found " + variable.getVariableType());
		}
		
		visited.add (n);

		Graph graph= treePotential.getGraph();
		
		for (Node parent : n.getParents()) {
			if (visited.contains(parent)) {
				continue;
			}
			
			Link link= graph.getLink(parent, n, true);
			if (link==null) {
				throw new RuntimeException("Expected link not found");
			}
			
			if (!(link instanceof LabelledLink)) {
				throw new RuntimeException("Expected LabelledLink class: found " + link.getClass().getName());				
			}
			
			LabelledLink labelledLink= (LabelledLink) link;
			if (!(labelledLink.getLabel() instanceof BranchData)) {
				throw new RuntimeException("Expected BranchData class: found " + labelledLink.getLabel().getClass().getName());
			}
			
			BranchData branchData= (BranchData) labelledLink.getLabel();
			if (branchData.getBranchVariable()==variable) {
				hashSet.addAll (branchData.getBranchStates());
				
				/*
				for (State state : branchData.getBranchStates()) {
					// Check for structural damage of the tree
					if (hashSet.contains(state)) {
						throw new RuntimeException("Tree structured is corrupted");											
					}
					hashSet.add (state);
				}
				*/
				
				return;
			}
			
			findConditionVarStates (parent, variable, hashSet, visited);
		}
	}
	
	boolean findDependingStates (Node node, Variable variable, HashSet<State> hashStates, HashSet<Node> visited) {
		Graph graph= treePotential.getGraph();
		visited.add (node);

		if (!(node.getObject() instanceof Variable)) {
			return false;
		}

		Variable nodeVar= (Variable) node.getObject();
		if (nodeVar==variable) {
			// Find what states of the variable are used in the subtrees of this node
			for (Node child : node.getChildren()) {
				Link link= graph.getLink(node, child, true);
				if (link==null) {
					throw new RuntimeException("Expected link not found");
				}
				
				if (!(link instanceof LabelledLink)) {
					throw new RuntimeException("Expected LabelledLink class: found " + link.getClass().getName());				
				}
				
				LabelledLink labelledLink= (LabelledLink) link;
				if (!(labelledLink.getLabel() instanceof BranchData)) {
					throw new RuntimeException("Expected BranchData class: found " + labelledLink.getLabel().getClass().getName());
				}
				
				BranchData branchData= (BranchData) labelledLink.getLabel();
				hashStates.addAll (branchData.getBranchStates());
			}
			
			return true;
		}
		
		boolean variableFound= false;
		for (Node child : node.getChildren()) {
			variableFound |= findDependingStates (child, variable, hashStates, visited);
		}
		
		return variableFound;
	}

	protected void setPopupItemsFSVariableNode (MouseEvent e, Node n) {
		Variable variable= (Variable) n.getObject();
		
		submenuAddNewBranch.removeAll();		
		if (variable.getVariableType()!=VariableType.FINITE_STATES) {
			throw new RuntimeException("Expected FINITE_STATES Variable: found " + variable.getVariableType());				
		}

		int numStatesUsed= 0;
		// Compute number of states used in the existing branches
		for (Link link : n.getLinks()) {
			if (link.getNode1()!=n) {
				continue;
			}

			if (!(link instanceof LabelledLink)) {
				throw new RuntimeException("Expected LabelledLink class: found " + link.getClass().getName());
			}
			
			LabelledLink labelledLink= (LabelledLink) link;
			if (!(labelledLink.getLabel() instanceof BranchData)) {
				throw new RuntimeException("Expected BranchData class: found " + labelledLink.getLabel().getClass().getName());
			}
			
			BranchData branchData= (BranchData) labelledLink.getLabel();
			numStatesUsed += branchData.getBranchStates().size();
		}
		
		HashSet<State> hashSetStatesBranchingVar= new HashSet<State>();
		HashSet<Node> visited= new HashSet<Node>();
		findConditionVarStates(n, variable, hashSetStatesBranchingVar, visited);
		
		int maxBranchesAllowed= variable.getNumStates();
		if (!hashSetStatesBranchingVar.isEmpty()) {
			// Limit the number of branches of this node if the states set is reduced (due to conditioning on the same variable upwards)
			maxBranchesAllowed= hashSetStatesBranchingVar.size();
		}
		
		// TODO: check its children:
		// If this node have branches for every state of a variable, it won't be eligible
		// Same condition if it's a numeric var with all its range covered.
		HashSet<Variable> hashSet= new HashSet<Variable>();
		visited= new HashSet<Node>();
		findConditionVars (n, hashSet, visited);

		for (Variable v : treeVariables) {
			if (v==variable || v==treePotential.getConditionedVariable()) {
				continue;
			}
			
			if (hashSet.contains (v)) {
				if (v.getVariableType()==VariableType.FINITE_STATES) {
					visited= new HashSet<Node>();		
					HashSet<State> hashSetStates= new HashSet<State>();				
					findConditionVarStates(n, v, hashSetStates, visited);

					if (hashSetStates.size()<2) {
						continue;
					}
				}
				else if (v.getVariableType()==VariableType.NUMERIC) {
					visited= new HashSet<Node>();		
					HashSet<BranchInterval> hashSetIntervals= new HashSet<BranchInterval>();
					findConditionVarInterval (n, v, hashSetIntervals, visited);
	
					boolean skipVariable= hashSetIntervals.isEmpty();
					if (hashSetIntervals.size()==1) {
						BranchInterval tmp= (BranchInterval) hashSetIntervals.toArray()[0];
						skipVariable= tmp.isSinglePointInterval();
					}
					
					if (skipVariable) {
						continue;
					}
				}
				else {
					// Check whether the region not covered by previous branch is wider than a point
					throw new RuntimeException("Unexpected type of variable: found " + variable.getVariableType());				
				}
			}
			
			JMenuItem menu= new JMenuItem(v.getName());
			menu.setActionCommand("AddNewBranchToVariable");
			menu.addActionListener(this);
			
			submenuAddNewBranch.add (menu);
		}
		
		submenuAddNewBranch.add (new JSeparator());
		
		submenuReferencePotential.removeAll();
		for (String reference : treePotential.getHashReferences().keySet()) {
			JMenuItem menu= new JMenuItem (reference);
			menu.setActionCommand("AddNewBranchToReference");
			menu.addActionListener(this);
			
			submenuReferencePotential.add (menu);
		}
		
		if (submenuReferencePotential.getMenuComponentCount()>0) {
			submenuAddNewBranch.add (submenuReferencePotential);
		}

		submenuAddNewBranch.add (menuitemAddNewTablePotential);
		
		if (Math.max (numStatesUsed, n.getNumChildren())<maxBranchesAllowed) {
			submenuAddNewBranch.setEnabled (true);
		}
		else {
			submenuAddNewBranch.setEnabled (false);			
		}
				
		submenuChangeVariable.removeAll();
		for (Variable v : treeVariables) {
			if (hashSet.contains (v)) {
				continue;
			}
			
			if (treePotential.getConditionedVariable()==v) {
				continue;
			}
			
			JMenuItem menu= new JMenuItem(v.getName());
			menu.setActionCommand("ChangeVariable");
			menu.addActionListener(this);
			
			submenuChangeVariable.add (menu);
		}
			
		popupMenu.add (submenuAddNewBranch);			
		popupMenu.add (submenuChangeVariable);
	}

	static private double epsilon= EpsilonValueAproximation.value;
	
	void findConditionVarInterval (Node n, Variable variable, HashSet<BranchInterval> hashSet, HashSet<Node> visited) {
		if (variable.getVariableType()!=VariableType.NUMERIC) {
			throw new RuntimeException("Expected NUMERIC Variable type: found " + variable.getVariableType());
		}
		
		visited.add (n);

		Graph graph= treePotential.getGraph();
		
		for (Node parent : n.getParents()) {
			if (visited.contains(parent)) {
				continue;
			}
			
			Link link= graph.getLink(parent, n, true);
			if (link==null) {
				throw new RuntimeException("Expected link not found");
			}
			
			if (!(link instanceof LabelledLink)) {
				throw new RuntimeException("Expected LabelledLink class: found " + link.getClass().getName());				
			}
			
			LabelledLink labelledLink= (LabelledLink) link;
			if (!(labelledLink.getLabel() instanceof BranchData)) {
				throw new RuntimeException("Expected BranchData class: found " + labelledLink.getLabel().getClass().getName());
			}
			
			BranchData branchData= (BranchData) labelledLink.getLabel();
			if (branchData.getBranchVariable()==variable) {
				hashSet.addAll (branchData.getBranchIntervals());

				for (BranchInterval bi : branchData.getBranchIntervals()) {				
					for (BranchInterval bj : hashSet) {
						if (bi.overlaps(bj)) {
							hashSet.remove(bj);
							hashSet.add (BranchInterval.join (bi, bj));
							break;
						}
					}
				}
				
				return;
			}
			
			findConditionVarInterval (parent, variable, hashSet, visited);
		}
	}
	
	/**
	 * 
	 */
	boolean findUnassignedIntervals (BranchInterval rangeInterval, ArrayList<BranchInterval> unassignedIntervals, Node n) {
		unassignedIntervals.clear();
		
		ArrayList<BranchInterval> intervalCovered= new ArrayList<BranchInterval>();
		for (Link link : n.getLinks()) {
			if (link.getNode1()!=n) {
				continue;
			}
			
			LabelledLink labelledLink= (LabelledLink) link;
			if (!(labelledLink.getLabel() instanceof BranchData)) {
				throw new RuntimeException("Expected BranchData class: found " + labelledLink.getLabel().getClass().getName());
			}
			
			BranchData branchData= (BranchData) labelledLink.getLabel();
			intervalCovered.addAll ((branchData.getBranchIntervals()));
		}
		
		if (intervalCovered.isEmpty()) {
			// Only splitting of initial interval allowed
			unassignedIntervals.add (new BranchInterval (rangeInterval.getLeft(), rangeInterval.getRight(), rangeInterval.isLeftClosed(), rangeInterval.isRightClosed()));
			return false;
		}

		Collections.sort (intervalCovered, new BranchIntervalComparator());
		
		double lastValue= rangeInterval.getLeft();
		boolean lastClosed= rangeInterval.isLeftClosed();
		
		for (BranchInterval bi : intervalCovered) {
			if (Math.abs(bi.getLeft()-lastValue)>epsilon) {
				if (lastValue>bi.getLeft()) {
					throw new RuntimeException("Variable Range Violated: found " + bi.getLeft() + " <= " + lastValue);						
				}

				BranchInterval newInterval= new BranchInterval (lastValue, bi.getLeft(), lastClosed, !bi.isLeftClosed());
				
				/* if (!intervalCovered.contains(newInterval))*/ {
					unassignedIntervals.add (newInterval);
				}
			}
			else if (!lastClosed && bi.isLeftClosed()) {
				throw new RuntimeException("Variable Range Violated on same value " + lastValue + ", this interval can't be left-closed");											
			}
			else if (lastClosed && !bi.isLeftClosed()) {
				BranchInterval newInterval= new BranchInterval (lastValue, lastValue, lastClosed, lastClosed);
				
				// One point interval
				/* if (!intervalCovered.contains(newInterval))*/ {
					unassignedIntervals.add (newInterval);	
				}
			}
			else {
				// No hole: Skip to the next interval
			}

			lastValue= bi.getRight();
			lastClosed= !bi.isRightClosed();
		}

		double rightmostValue= rangeInterval.getRight();
		boolean rightmostClosed= rangeInterval.isRightClosed();
		
		if (Math.abs(rightmostValue-lastValue)>epsilon) {
			if (lastValue>rightmostValue) {
				throw new RuntimeException("Variable Range Violated: found " + rightmostValue + " <= " + lastValue);						
			}
			
			BranchInterval newInterval= new BranchInterval (lastValue, rightmostValue, lastClosed, rightmostClosed);
			
			// One point interval
			/* if (!intervalCovered.contains(newInterval))*/ {
				unassignedIntervals.add (newInterval);
			}
			
		}
		else {
			// restore the type of rightmost value of the last defined interval
			lastClosed= !lastClosed;
			
			if (lastClosed && !rightmostClosed) {
			// On the rightmost point, it's the opposite case
				throw new RuntimeException("Variable Range Violated on same value " + lastValue + ", this interval can't be right-closed");											
			}
			else if (!lastClosed && rightmostClosed) {
				// One point interval
				BranchInterval newInterval= new BranchInterval (lastValue, lastValue, lastClosed, lastClosed);
				
				// One point interval
				/* if (!intervalCovered.contains(newInterval)) */ {
					unassignedIntervals.add (newInterval);
				}
			}
			else {
				// No hole: all interval range has been scanned
			}		
		}
		
		return true;
	}
	
	/**
	 * @param e
	 * @param n
	 */
	protected void setPopupItemsNumericVariableNode (MouseEvent e, Node n) {
		Variable variable= (Variable) n.getObject();
		
		submenuAddNewBranch.removeAll();		
		if (variable.getVariableType()!=VariableType.NUMERIC) {
			throw new RuntimeException("Expected NUMERIC Variable: found " + variable.getVariableType());				
		}

		// Is this numeric variable restricted to a set of intervals?		
		HashSet<Node> visited= new HashSet<Node>();
		HashSet<BranchInterval> hashBranchingIntervals= new HashSet<BranchInterval>();
		findConditionVarInterval (n, variable, hashBranchingIntervals, visited);
		
		boolean allNumericIntervalCovered= false;
		
		// TODO: check its children:
		// If this node have branches covering all the range of a variable, it won't be eligible
		/*
		for (Link link : n.getLinks()) {
			if (link.getNode1()!=n) {
				continue;
			}
			
			LabelledLink labelledLink= (LabelledLink) link;
			if (!(labelledLink.getLabel() instanceof BranchData)) {
				throw new RuntimeException("Expected BranchData class: found " + labelledLink.getLabel().getClass().getName());
			}
			
			BranchData branchData= (BranchData) labelledLink.getLabel();
		}			
		*/
		
		if (hashBranchingIntervals.size()==1) {
			BranchInterval tmp= (BranchInterval) hashBranchingIntervals.toArray()[0];
			allNumericIntervalCovered= tmp.isSinglePointInterval();
		}
		
		visited= new HashSet<Node>();
		HashSet<Variable> hashSet= new HashSet<Variable>();
		findConditionVars (n, hashSet, visited);

		for (Variable v : treeVariables) {
			if (v==variable || v==treePotential.getConditionedVariable()) {
				continue;
			}
			
			if (hashSet.contains (v)) {
				if (v.getVariableType()==VariableType.FINITE_STATES) {
					visited= new HashSet<Node>();		
					HashSet<State> hashSetStates= new HashSet<State>();				
					findConditionVarStates(n, v, hashSetStates, visited);

					if (hashSetStates.size()<2) {
						continue;
					}
				}
				else if (v.getVariableType()==VariableType.NUMERIC) {
					visited= new HashSet<Node>();		
					HashSet<BranchInterval> hashSetIntervals= new HashSet<BranchInterval>();
					findConditionVarInterval (n, v, hashSetIntervals, visited);

					boolean skipVariable= hashSetIntervals.isEmpty();
					if (hashSetIntervals.size()==1) {
						BranchInterval tmp= (BranchInterval) hashSetIntervals.toArray()[0];
						skipVariable= tmp.isSinglePointInterval();
					}
					
					if (skipVariable) {
						continue;
					}
				}
				else {
					// Check whether the region not covered by previous branch is wider than a point
					throw new RuntimeException("Unexpected type of variable: found " + variable.getVariableType());				
				}
			}
			
			JMenuItem menu= new JMenuItem(v.getName());
			menu.setActionCommand("AddNewBranchToVariable");
			menu.addActionListener(this);
			
			submenuAddNewBranch.add (menu);
		}
		
		submenuAddNewBranch.add (new JSeparator());
		submenuAddNewBranch.add (menuitemAddNewTablePotential);
		
		// No sera posible si ya esta repartido todo el intervalo
		submenuAddNewBranch.setEnabled (!allNumericIntervalCovered);			
		
		submenuChangeVariable.removeAll();
		for (Variable v : treeVariables) {
			if (hashSet.contains (v)) {
				continue;
			}
			
			JMenuItem menu= new JMenuItem(v.getName());
			menu.setActionCommand("ChangeVariable");
			menu.addActionListener(this);
			
			submenuChangeVariable.add (menu);
		}
			
		popupMenu.add (submenuAddNewBranch);			
		popupMenu.add (submenuChangeVariable);	}
	
	protected void setPopupItemsVariableNode (MouseEvent e, Node n) {
		Variable variable= (Variable) n.getObject();
		
		if (variable.getVariableType()==VariableType.FINITE_STATES) {
			setPopupItemsFSVariableNode (e, n);
		}
		else if (variable.getVariableType()==VariableType.NUMERIC) {
			setPopupItemsNumericVariableNode (e, n);
		}
		else {
			throw new RuntimeException("Unexpected type of Variable: found " + variable.getVariableType());			
		}
	}

	protected void setPopupItemsTablePotentialNode (MouseEvent e, Node n, String actionAdd, String actionDel) {
		TablePotential tablePotential= (TablePotential) n.getObject();
		
		HashSet<Variable> hashSet= new HashSet<Variable>();
		HashSet<Node> visited= new HashSet<Node>();

		findConditionVars (n, hashSet, visited);
		hashSet.addAll (tablePotential.getVariables());

		submenuAddNewPotentialVariable.removeAll();
		submenuRemovePotentialVariable.removeAll();
		
		for (Variable var : treePotential.getVariables()) {
			if (hashSet.contains(var)) {
				continue;
			}
			
			JMenuItem menu= new JMenuItem(var.getName());
			menu.addActionListener (this);
			
			menu.setActionCommand (actionAdd);
			submenuAddNewPotentialVariable.add (menu);
		}

		submenuAddNewPotentialVariable.setEnabled (submenuAddNewPotentialVariable.getMenuComponentCount()>0);
		
		for (Variable var : tablePotential.getVariables()) {
			if (!hashSet.contains(var)) {
				continue;
			}
			
			// If this is a Conditional Potential, the conditioned var can't be removed
			if (treePotential.getConditionedVariable()==var) {
				continue;
			}
			
			JMenuItem menu= new JMenuItem(var.getName());
			menu.addActionListener (this);
			
			menu.setActionCommand (actionDel);
			submenuRemovePotentialVariable.add (menu);
		}
		
		submenuRemovePotentialVariable.setEnabled (submenuRemovePotentialVariable.getMenuComponentCount()>0);
		
		popupMenu.add (submenuAddNewPotentialVariable);
		popupMenu.add (submenuRemovePotentialVariable);
		popupMenu.add (menuitemChangePotentialReferenceName);
		popupMenu.add (menuitemSetTablePotentialValues);
	}
	
	protected void setPopupItems(MouseEvent e, Node n) {
		popupMenu.removeAll();

		if (n.getObject() instanceof Variable) {
			setPopupItemsVariableNode (e, n);
		}
		else if (n.getObject() instanceof TablePotential){
			setPopupItemsTablePotentialNode (e, n, "AddNewVariableToPotential", "RemoveVariableFromPotential");
		}
		else {
			throw new RuntimeException("Unexpected node class found: " + n.getObject().getClass().getName());			
		}
	}

	protected void setPopupItemsBlankTree() {
		popupMenu.removeAll();

		submenuAddStartNode.removeAll();
		
		for (Variable v : treeVariables) {
			if (treePotential.getConditionedVariable()==v) {
				continue;
			}
			
			JMenuItem menu= new JMenuItem(v.getName());
			menu.setActionCommand("SetStartNode");
			menu.addActionListener(this);
			
			submenuAddStartNode.add (menu);
		}
		
		popupMenu.add (submenuAddStartNode);			
	}

	// Mouse event detection
	int xx, yy;
	

	public void actionPerformed(ActionEvent ae) {
		// TODO Auto-generated method stub
		
		String cmd= ae.getActionCommand();		
		if (cmd.equals ("SetStartNode")) {
			JMenuItem menu= (JMenuItem) ae.getSource();

			for (Variable v : treeVariables) {
				if (v.getName().equals (menu.getText())) {
					Graph graph= treePotential.getGraph();
					Node n= new Node (graph, v);
					
					TreeADDModel model= new TreeADDModel (n);
					tree.setModel (model); 

					treePotential.setStartNode();
					return;
				}
			}
			
			throw new RuntimeException("variable not found: " + menu.getText());							
		}
		
		TreePath path = tree.getPathForLocation(xx,yy); 
		Object r= path.getLastPathComponent();
		
		if (cmd.equals ("ChangeVariable")) {
			actionChangeVariable (ae, r, path);
		}
		else if (cmd.equals ("AddNewBranchToVariable")) {
			actionAddNewBranchToVariable (ae, r, path);			
		}
		else if (cmd.equals ("AddBranchState")) {
			actionNewBranchState (ae, r, path);
		}
		else if (cmd.equals ("RemoveBranchState")) {
			actionRemoveBranchState (ae, r, path);
		}
		else if (cmd.equals ("SplitInterval")) {
			actionSplitInterval (ae, r, path);			
		}
		else if (cmd.equals("SetInterval")) {
			actionSetInterval (ae, r, path);			
		}
		else if (cmd.equals ("RemoveBranch")) {
			actionRemoveBranch (ae, r, path);
		}
		else if (cmd.equals ("AddNewTablePotential")) {
			actionAddNewTablePotential (ae, r, path);			
		}
		else if (cmd.equals ("AddNewVariableToPotential")) {
			if (!(r instanceof Node)) {
				throw new RuntimeException("Expected Node class, found: " + r.getClass().getName());											
			}
			
			actionNewVariableToPotential (ae, (Node) r, path);
		}
		else if (cmd.equals ("AddNewVariableToPotentialFromSummaryBox")) {
			if (!(r instanceof SummaryBox)) {
				throw new RuntimeException("Expected SummaryBox class, found: " + r.getClass().getName());											
			}
			
			SummaryBox summaryBox= (SummaryBox) r;
			actionNewVariableToPotential (ae, summaryBox.getSource(), path);
		}
		else if (cmd.equals ("RemoveVariableFromPotential")) {
			if (!(r instanceof Node)) {
				throw new RuntimeException("Expected Node class, found: " + r.getClass().getName());											
			}
			
			actionRemoveVariableFromPotential (ae, (Node) r, path);
		}
		else if (cmd.equals ("RemoveVariableFromPotentialFromSummaryBox")) {
			if (!(r instanceof SummaryBox)) {
				throw new RuntimeException("Expected SummaryBox class, found: " + r.getClass().getName());											
			}
			
			SummaryBox summaryBox= (SummaryBox) r;			
			actionRemoveVariableFromPotential (ae, summaryBox.getSource(), path);
		}
		else if (cmd.equals("SetTablePotentialValues")) {
			Node node= null;
			
			if (r instanceof SummaryBox) {
				// TODO: checks antes de cada casting
				SummaryBox summaryBox= ((SummaryBox) r);
				node= summaryBox.getSource();
			}
			else if (r instanceof Node) {
				node= (Node) r;
			}
			else {
				throw new RuntimeException("Expected SummaryBox class, found: " + r.getClass().getName());											
			}
			
			actionSetTablePotentialValues (node, path);
		}
		else if (cmd.equals("ChangePotentialReferenceName")) {
			Node node= null;
			
			if (r instanceof SummaryBox) {
				// TODO: checks antes de cada casting
				SummaryBox summaryBox= ((SummaryBox) r);
				node= summaryBox.getSource();
			}
			else if (r instanceof Node) {
				node= (Node) r;
			}
			else {
				throw new RuntimeException("Expected SummaryBox class, found: " + r.getClass().getName());											
			}
			
			actionChangePotentialReferenceName (node, path);			
		}
		else if (cmd.equals("AddNewBranchToReference")) {
			actionAddNewBranchToReference (ae, r, path);			
		}
		else {
			throw new RuntimeException("Unexpected menu action found: " + cmd);			
		}
	}

	void actionSetTablePotentialValues (Node node, TreePath path) {
		
		TablePotential tablePotential= (TablePotential) node.getObject();
		//TODO the  probNet constructor receives a networkTypeconstraint as parameter
		ProbNet dummyProbNet= new ProbNet ();

		dummyProbNet.addPotential (tablePotential);
		ProbNode dummy= null;
		
		boolean isScalar= tablePotential.getVariables().isEmpty();
		
		if (treePotential.getPotentialRole()==PotentialRole.UTILITY) {
			Variable utilityVariable= treePotential.getUtilityVariable();
			dummy= dummyProbNet.getProbNode (utilityVariable);

			for (Variable v : tablePotential.getVariables()) {
				if (v==utilityVariable) {
					continue;
				}
				
				try {
					dummyProbNet.addLink (v, utilityVariable, true);
				} catch (NodeNotFoundException e) {
					throw new RuntimeException("Node not found: " + e.getMessage());
				}
			}
		}
		else if (treePotential.getPotentialRole()==PotentialRole.CONDITIONAL_PROBABILITY) {
			Variable conditionedVariable= treePotential.getConditionedVariable();
			dummy= dummyProbNet.getProbNode (conditionedVariable);

			for (Variable v : tablePotential.getVariables()) {
				if (v==conditionedVariable) {
					continue;
				}
				
				try {
					dummyProbNet.addLink (v, conditionedVariable, true);
				} catch (NodeNotFoundException e) {
					throw new RuntimeException("Node not found: " + e.getMessage());
				}
			}
		}
		else {
			throw new RuntimeException("Unexpected Potential Role found: " + treePotential.getPotentialRole());
		}
		
		PotentialsDialog dialog= new PotentialsDialog(Util.getOwner(this), dummy, false);
		if (dialog.requestValues()==NodePropertiesDialog.OK_BUTTON) {
			Potential retPotential= dummy.getPotentials().get(0);

			if (!(retPotential instanceof TablePotential)) {
				throw new RuntimeException("Expected TablePotential found: " + retPotential.getClass().getSimpleName());
			}
			
			if (treePotential.getPotentialRole()!=retPotential.getPotentialRole()) {
				throw new RuntimeException("Expected role " + treePotential.getPotentialRole() +", found: " + retPotential.getPotentialRole());
			}
			
			node.setObject (retPotential);
			
			if (node.getParents().size()>1) {
				TreeADDModel model= (TreeADDModel) tree.getModel();
				model.fireRecursiveNodeChanged (node);				
			}
			else if (isScalar) {
				TreeADDModel model= (TreeADDModel) tree.getModel();
				model.fireNodesChanged (path);				
			}
		}
	}
	
	void actionChangePotentialReferenceName (Node node, TreePath path) {
		String referenceName= treePotential.getReference (node);
		if (referenceName==null) {
			throw new RuntimeException("Reference not found for node: " + node);
		}
		
		String result = (String)JOptionPane.showInputDialog(
		                    null,
		                    "Enter new name for this potential",
		                    "Change reference name",
		                    JOptionPane.PLAIN_MESSAGE,
		                    null,
		                    null,
		                    referenceName);
		
		if (result!=null && !result.equals(referenceName)) {
			if (treePotential.getHashReferences().containsKey(result)) {
				throw new RuntimeException("Can't change reference name: Duplicated reference: " + result);
			}
			
			treePotential.getHashReferences().remove(referenceName);
			treePotential.getHashReferences().put(result, node);
		}
	}
	
	
	void actionChangeVariable (ActionEvent ae, Object r, TreePath path) {
		if (!(r instanceof Node)) {
			throw new RuntimeException("Expected Node class, found: " + r.getClass().getName());											
		}
		
		Node node= (Node) r;
		Graph graph= treePotential.getGraph();
		
		Variable newVariable= null;
		JMenuItem menu= (JMenuItem) ae.getSource();
		for (Variable v : treeVariables) {
			if (v.getName().equals (menu.getText())) {
				newVariable= v;
				break;
			}
		}
		
		if (newVariable==null) {
			throw new RuntimeException("Selected variable not found: " + menu.getText());
		}
		
		node.setObject (newVariable);		

		TreeADDModel model= (TreeADDModel) tree.getModel();
		
		// Delete depending subtrees
		for (Node child : node.getChildren()) {
			graph.removeLink (node, child, true);
			deleteUnusedGraphNodes (graph, child);
		}

		// TODO: include this into TreeADDModel, maybe inside fireTreeStructureChanged method		
		ArrayList<Link> linksToErase= new ArrayList<Link>();
		for (Link link : model.boxesHash.keySet()) {
			if (!graph.getLinks().contains(link)) {
				linksToErase.add (link);
			}
		}
		
		for (Link link : linksToErase) {
			model.boxesHash.remove(link);
		}
		// TODO: <-----
		
		model.fireTreeStructureChanged(path);
	}

	void actionAddNewBranchToVariable (ActionEvent ae, Object r, TreePath path) {
		if (!(r instanceof Node)) {
			throw new RuntimeException("Expected Node class, found: " + r.getClass().getName());											
		}

		Node node= (Node) r;
		Graph graph= treePotential.getGraph();
		Node newNode= null;

		// Find the new variable
		JMenuItem menu= (JMenuItem) ae.getSource();
		for (Variable v : treeVariables) {
			if (v.getName().equals (menu.getText())) {
				newNode= new Node (graph, v);
				break;
			}
		}
		
		if (newNode==null) {
			throw new RuntimeException("variable not found: " + menu.getText());							
		}

		new LabelledLink (node, newNode, true, new BranchData ((Variable) node.getObject()));
		
		TreeADDModel model= (TreeADDModel) tree.getModel();			
		model.fireTreeInsert(path, newNode);
		
		tree.expandPath(path.pathByAddingChild(newNode));
	}
	
	void actionNewBranchState (ActionEvent ae, Object r, TreePath path) {
		if (!(r instanceof SummaryBox)) {
			throw new RuntimeException("Expected SummaryBox class, found: " + r.getClass().getName());											
		}
		
		SummaryBox summaryBox= (SummaryBox) r;
		Node parent= summaryBox.getParent();
		Node child=  summaryBox.getSource();
		
		Graph graph= treePotential.getGraph();
		Link link= graph.getLink(parent, child, true);
		
		if (!(link instanceof LabelledLink)) {
			throw new RuntimeException("LabelledLink class expected, found: " + link.getClass().getName());											
		}
		
		LabelledLink labelledLink= (LabelledLink) link;
		if (!(labelledLink.getLabel() instanceof BranchData)) {
			throw new RuntimeException("Expected BranchData class: found " + labelledLink.getLabel().getClass().getName());
		}
		
		// TODO: test cardinality of variable states and InnerNode
		BranchData branchData= (BranchData) labelledLink.getLabel();
		
		Variable variable= branchData.getBranchVariable();
		if (variable.getVariableType()!= VariableType.FINITE_STATES) {
			throw new RuntimeException("Expected FINITE_STATES variable: found " + variable.getVariableType());			
		}
		
		JMenuItem menu= (JMenuItem) ae.getSource();
		int index;
		try {
			index = variable.getStateIndex (menu.getText());
		} catch (InvalidStateException e) {
			throw new RuntimeException("State unknow: " + menu.getText());
		}
		
		branchData.add (variable.getStates()[index]);
		TreeADDModel model= (TreeADDModel) tree.getModel();			

		model.fireNodesChanged(path);
	}

	/**
	 * @param ae
	 * @param r
	 * @param path
	 */
	/**
	 * @param ae
	 * @param r
	 * @param path
	 */
	void actionSplitInterval (ActionEvent ae, Object r, TreePath path) {		
		if (!(r instanceof SummaryBox)) {
			throw new RuntimeException("Expected SummaryBox class, found: " + r.getClass().getName());											
		}

		JMenuItem menu= (JMenuItem) ae.getSource();
		BranchInterval intervalToSplit= (BranchInterval) menu.getClientProperty("interval");
		
		SummaryBox summaryBox= (SummaryBox) r;
		Node parent= summaryBox.getParent();
		Node child=  summaryBox.getSource();
		
		Graph graph= treePotential.getGraph();
		Link link= graph.getLink(parent, child, true);
		
		if (!(link instanceof LabelledLink)) {
			throw new RuntimeException("LabelledLink class expected, found: " + link.getClass().getName());											
		}
		
		LabelledLink labelledLink= (LabelledLink) link;
		if (!(labelledLink.getLabel() instanceof BranchData)) {
			throw new RuntimeException("Expected BranchData class: found " + labelledLink.getLabel().getClass().getName());
		}
		
		// TODO: test cardinality of variable states and InnerNode
		BranchData branchData= (BranchData) labelledLink.getLabel();
		
		Variable variable= branchData.getBranchVariable();
		if (variable.getVariableType()!= VariableType.NUMERIC) {
			throw new RuntimeException("Expected NUMERIC variable: found " + variable.getVariableType());			
		}

		// -------------------
		
		IntervalSelectionDialog dlg= new IntervalSelectionDialog (intervalToSplit);
		BranchInterval interval= dlg.getResult();
		
		if (interval!=null) {
			branchData.getBranchIntervals().clear();
			branchData.add(interval);
			TreeADDModel model= (TreeADDModel) tree.getModel();			

			model.fireNodesChanged(path);
		}
	}

	void actionSetInterval (ActionEvent ae, Object r, TreePath path) {		
		if (!(r instanceof SummaryBox)) {
			throw new RuntimeException("Expected SummaryBox class, found: " + r.getClass().getName());											
		}

		JMenuItem menu= (JMenuItem) ae.getSource();
		BranchInterval intervalToSet= (BranchInterval) menu.getClientProperty("interval");
		
		SummaryBox summaryBox= (SummaryBox) r;
		Node parent= summaryBox.getParent();
		Node child=  summaryBox.getSource();
		
		Graph graph= treePotential.getGraph();
		Link link= graph.getLink(parent, child, true);
		
		if (!(link instanceof LabelledLink)) {
			throw new RuntimeException("LabelledLink class expected, found: " + link.getClass().getName());											
		}
		
		LabelledLink labelledLink= (LabelledLink) link;
		if (!(labelledLink.getLabel() instanceof BranchData)) {
			throw new RuntimeException("Expected BranchData class: found " + labelledLink.getLabel().getClass().getName());
		}
		
		// TODO: test cardinality of variable states and InnerNode
		BranchData branchData= (BranchData) labelledLink.getLabel();
		
		Variable variable= branchData.getBranchVariable();
		if (variable.getVariableType()!= VariableType.NUMERIC) {
			throw new RuntimeException("Expected NUMERIC variable: found " + variable.getVariableType());			
		}

		// -------------------
		
		branchData.getBranchIntervals().clear();
		branchData.add (intervalToSet);
		TreeADDModel model= (TreeADDModel) tree.getModel();			

		model.fireNodesChanged(path);
	}
	
	void actionRemoveBranchState (ActionEvent ae, Object r, TreePath path) {
		if (!(r instanceof SummaryBox)) {
			throw new RuntimeException("Expected Summary class, found: " + r.getClass().getName());											
		}
		
		SummaryBox summaryBox= (SummaryBox) r;
		Node parent= summaryBox.getParent();
		Node child=  summaryBox.getSource();
		
		Graph graph= treePotential.getGraph();
		Link link= graph.getLink(parent, child, true);
		
		if (!(link instanceof LabelledLink)) {
			throw new RuntimeException("LabelledLink class expected, found: " + link.getClass().getName());											
		}
		
		LabelledLink labelledLink= (LabelledLink) link;
		if (!(labelledLink.getLabel() instanceof BranchData)) {
			throw new RuntimeException("Expected BranchData class: found " + labelledLink.getLabel().getClass().getName());
		}
		
		// TODO: test cardinality of variable states and InnerNode
		BranchData branchData= (BranchData) labelledLink.getLabel();
		
		Variable variable= branchData.getBranchVariable();
		
		JMenuItem menu= (JMenuItem) ae.getSource();
		int index;
		try {
			index = variable.getStateIndex (menu.getText());
		} catch (InvalidStateException e) {
			throw new RuntimeException("State unknow: " + menu.getText());
		}
		
		State stateToRemove= variable.getStates()[index]; 
				
		// TODO: comprobar que la variable es FiniteStates
		branchData.remove (stateToRemove);
		
		TreeADDModel model= (TreeADDModel) tree.getModel();			
		model.fireNodesChanged (path);			
	}

	void actionRemoveBranch (ActionEvent ae, Object r, TreePath path) {
		if (!(r instanceof SummaryBox)) {
			throw new RuntimeException("Expected Summary class, found: " + r.getClass().getName());											
		}
		
		SummaryBox summaryBox= (SummaryBox) r;
		Node parent= summaryBox.getParent();
		Node child=  summaryBox.getSource();
		
		Graph graph= treePotential.getGraph();
		
		deleteUnusedGraphNodes (graph, child);
		
		TreeADDModel model= (TreeADDModel) tree.getModel();
		
		model.fireTreeRemove (path.getParentPath(), child);
		model.removeBox (summaryBox);
		
		graph.removeLink (parent, child, true);
		
		// Delete depending subtrees
		for (Node subchild : child.getChildren()) {
			graph.removeLink (child, subchild, true);
			deleteUnusedGraphNodes (graph, subchild);
		}

		// TODO: include this into TreeADDModel, maybe inside fireTreeRemove method
		ArrayList<Link> linksToErase= new ArrayList<Link>();
		for (Link link : model.boxesHash.keySet()) {
			if (!graph.getLinks().contains(link)) {
				linksToErase.add (link);
			}
		}
		
		for (Link link : linksToErase) {
			model.boxesHash.remove(link);
		}
		// TODO: <----
		
		if (child.getParents().isEmpty()) {
			graph.removeNode (child);
			
			if (child.getObject() instanceof Potential) {
				Potential potential= (Potential) child.getObject();
				treePotential.removePotential(potential);
				
				String referenceName= treePotential.getReference(child);
				if (referenceName==null) {
					throw new RuntimeException("Reference unknown: " + referenceName);			
				}
				
				treePotential.removeNodeReference (referenceName);
			}			
		}
	}
	
	void actionAddNewTablePotential (ActionEvent ae, Object r, TreePath path) {
		if (!(r instanceof Node)) {
			throw new RuntimeException("Expected Node class, found: " + r.getClass().getName());											
		}

		Graph graph= treePotential.getGraph();
		Node newNode= null;
		try {
			TablePotential newPotential= null;
			PotentialRole role= treePotential.getPotentialRole();
			
			if (treePotential.isUtility()) {
				if (role!=PotentialRole.UTILITY) {
					throw new RuntimeException("Expected UTILITY PotentialRole, found: " + role);					
				}
				
				newPotential= new TablePotential (null, role);
				newPotential.setUtilityVariable(treePotential.getUtilityVariable());
			}
			else if (role==PotentialRole.CONDITIONAL_PROBABILITY) {
				ArrayList<Variable> varList= new ArrayList<Variable>();
				varList.add (treePotential.getConditionedVariable());
				newPotential= new TablePotential (varList, role);
			}
			else {
				throw new RuntimeException("Unexpected PotentialRole, found: " + role);					
			}

			newNode= new Node (graph, newPotential);
			
			// Assign reference to this new potential
			String referenceName= newPotential.getClass().getSimpleName() + "@@" + referenceCount;
			referenceCount++;
			
			treePotential.addPotential (newPotential);
			treePotential.addNodeReference (referenceName, newNode);
			
		} catch (NotEnoughMemoryException e) {
			e.printStackTrace();											
		}
		
		if (newNode==null) {
			throw new RuntimeException("Error creating new TablePotential node");							
		}
		
		Node parent= (Node) r;			
		new LabelledLink (parent, newNode, true, new BranchData ((Variable) parent.getObject()));
		
		TreeADDModel model= (TreeADDModel) tree.getModel();			
		model.fireTreeInsert(path, newNode);
		
		tree.expandPath(path);		
	}
	
	void actionAddNewBranchToReference (ActionEvent ae, Object r, TreePath path) {
		if (!(r instanceof Node)) {
			throw new RuntimeException("Expected Node class, found: " + r.getClass().getName());											
		}

		JMenuItem menu= (JMenuItem) ae.getSource();
		String referenceName= menu.getText();
		
		if (!treePotential.getHashReferences().containsKey(referenceName)) {
			throw new RuntimeException("Reference unknown: " + referenceName);			
		}
		
		Node repeatedNode= treePotential.getHashReferences().get(referenceName);

		Node parent= (Node) r;			
		new LabelledLink (parent, repeatedNode, true, new BranchData ((Variable) parent.getObject()));
		
		TreeADDModel model= (TreeADDModel) tree.getModel();			
		model.fireTreeInsert(path, repeatedNode);
		
		tree.expandPath(path);		
	}
	
	void actionNewVariableToPotential (ActionEvent ae, Node node, TreePath path) {
		// Find the new variable
		Variable varToAdd= null;
		JMenuItem menu= (JMenuItem) ae.getSource();
		for (Variable v : treeVariables) {
			if (v.getName().equals (menu.getText())) {
				varToAdd= v;
				break;
			}
		}
		
		if (varToAdd==null) {
			throw new RuntimeException("variable not found: " + menu.getText());							
		}

		if(!(node.getObject() instanceof TablePotential)) {
			throw new RuntimeException("Expected TablePotential class, found: " + node.getObject().getClass().getName());				
		}
		
		TablePotential oldTablePotential= (TablePotential) node.getObject();
		ArrayList<Variable> newPotentialVars= oldTablePotential.getVariables();
		
		if (newPotentialVars.contains(varToAdd)) {
			throw new RuntimeException("TablePotential Variable is already present");								
		}
		
		newPotentialVars.add (varToAdd);
		
		try {
			TablePotential newPotential= new TablePotential (newPotentialVars, treePotential.getPotentialRole());
			newPotential.setUtilityVariable(oldTablePotential.getUtilityVariable());
			
			node.setObject (newPotential);
			
		} catch (NotEnoughMemoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TreeADDModel model= (TreeADDModel) tree.getModel();

		if (node.getParents().size()>1) {
			model.fireRecursiveNodeChanged (node);				
		}
		else {
			model.fireNodesChanged (path);
		}
	}

	void actionRemoveVariableFromPotential (ActionEvent ae, Node node, TreePath path) {
		// Find the new variable
		Variable varToRemove= null;
		JMenuItem menu= (JMenuItem) ae.getSource();
		for (Variable v : treeVariables) {
			if (v.getName().equals (menu.getText())) {
				varToRemove= v;
				break;
			}
		}
		
		if (varToRemove==null) {
			throw new RuntimeException("variable not found: " + menu.getText());							
		}

		if(!(node.getObject() instanceof TablePotential)) {
			throw new RuntimeException("Expected TablePotential class, found: " + node.getObject().getClass().getName());				
		}
		
		TablePotential oldTablePotential= (TablePotential) node.getObject();
		ArrayList<Variable> newPotentialVars= oldTablePotential.getVariables();
		
		if (!newPotentialVars.contains(varToRemove)) {
			throw new RuntimeException("Variable to be deleted is not present in the Potential");								
		}
		
		newPotentialVars.remove (varToRemove);
		
		try {
			TablePotential newPotential= new TablePotential (newPotentialVars, treePotential.getPotentialRole()); 
			newPotential.setUtilityVariable(oldTablePotential.getUtilityVariable());
			
			node.setObject (newPotential);

		} catch (NotEnoughMemoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TreeADDModel model= (TreeADDModel) tree.getModel();
		
		if (node.getParents().size()>1) {
			model.fireRecursiveNodeChanged (node);				
		}
		else {
			model.fireNodesChanged (path);
		}
	}
	
	void deleteUnusedGraphNodes (Graph graph, Node node) {
		if (!node.getParents().isEmpty()) {
			// This node have another active branch
			return;
		}
		
		ArrayList<Node> children= (ArrayList<Node>) node.getChildren();
		graph.removeNode (node);
		
		if (node.getObject() instanceof Potential) {
			Potential potential= (Potential) node.getObject();
			treePotential.removePotential(potential);
			
			String referenceName= treePotential.getReference(node);
			if (referenceName==null) {
				throw new RuntimeException("Reference unknown: " + referenceName);			
			}
			
			treePotential.removeNodeReference (referenceName);
		}
		
		for (Node child : children) {
			deleteUnusedGraphNodes (graph, child);
		}
	}
}

/**
 * TODO: Convert to Inner Class of the Viewer?
 */
class ADDTreeViewer_tree_treeExpansionAdapter implements TreeExpansionListener {
	private TreeADDBuilder adaptee;
	ADDTreeViewer_tree_treeExpansionAdapter (TreeADDBuilder adaptee) {
		this.adaptee = adaptee;
	}
	
	public void treeExpanded(TreeExpansionEvent event) {
		adaptee.tree_treeExpanded(event);
	}
	
	public void treeCollapsed(TreeExpansionEvent event) {
	}
}

/**
 * TODO: Convert to Inner Class of the Viewer?
 */
class ADDTreeViewer_tree_treeWillExpandAdapter implements TreeWillExpandListener {
	private TreeADDBuilder adaptee;
	ADDTreeViewer_tree_treeWillExpandAdapter (TreeADDBuilder adaptee) {
		this.adaptee = adaptee;
	}
	
	public void treeWillExpand(TreeExpansionEvent event) {
	}
	
	public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
		adaptee.tree_treeWillCollapse(event);
	}
}

class ADDTreeViewer_tree_mouseAdapter extends MouseAdapter {
	private TreeADDBuilder adaptee;
	ADDTreeViewer_tree_mouseAdapter(TreeADDBuilder adaptee) {
		this.adaptee = adaptee;
	}
	
	public void mousePressed(MouseEvent e) {
		maybeShowPopup(e);
	}
	
	public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e);
	}
	
	private void maybeShowPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			adaptee.xx= e.getX();
			adaptee.yy= e.getY();
			
			TreePath path = adaptee.tree.getPathForLocation (adaptee.xx, adaptee.yy);
			if( path != null ) {
				Object node = path.getLastPathComponent();
				
				if( node instanceof SummaryBox ) {
					adaptee.setPopupItems(e, (SummaryBox) node);
				}
				else if( node instanceof Node ) {
					adaptee.setPopupItems(e, (Node) node);				
				}

				adaptee.popupMenu.show (e.getComponent(), adaptee.xx, adaptee.yy);
			}
			else {
				if (adaptee.tree.getModel()==null) {
					adaptee.setPopupItemsBlankTree();				
					adaptee.popupMenu.show(e.getComponent(), adaptee.xx, adaptee.yy);
				}
			}
		}
	}
}