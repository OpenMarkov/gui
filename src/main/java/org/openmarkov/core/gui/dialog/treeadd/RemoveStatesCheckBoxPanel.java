package org.openmarkov.core.gui.dialog.treeadd;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDBranch;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDPotential;
/**
 * 
 * @author myebra
 *
 */
@SuppressWarnings("serial")
public class RemoveStatesCheckBoxPanel extends JPanel {
	private ArrayList<JCheckBox> checkBoxes = new ArrayList<JCheckBox>();
	private TreeADDBranch branch;
	private TreeADDPotential treeADD;
	
	
	
	public RemoveStatesCheckBoxPanel (TreeADDBranch branch, TreeADDPotential treeADD) {
		this.branch = branch;
		this.treeADD = treeADD;
		initialize();
		repaint();
	}
	
	 public void initialize() {
		setLayout(new BorderLayout());
		ArrayList<State> states = branch.getBranchStates();
		
		for (State state : states) {
			
			JCheckBox checkBox = new JCheckBox (state.getName());
			checkBoxes.add(checkBox);
			add(checkBox);
			
		}
	 }
	public TreeADDBranch getBranch() {
		 return this.branch;
	}
	public TreeADDPotential getTreeADDPotential() {
		 return this.treeADD;
	}
	public ArrayList<JCheckBox> getCheckBoxes () {
		return this.checkBoxes;
	}
}
