package org.openmarkov.core.gui.dialog.treeadd;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDBranch;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDPotential;
/**
 * 
 * @author myebra
 *
 */
@SuppressWarnings("serial")
public class RemoveVariablesCheckBoxPanel extends JPanel{
	
	private ArrayList<JCheckBox> checkBoxes = new ArrayList<JCheckBox>();
	private TreeADDBranch branch;
	private TreeADDPotential treeADD;
	private JPanel addVariablesPanel;
	
	
	public RemoveVariablesCheckBoxPanel (TreeADDBranch branch, TreeADDPotential treeADD) {
		this.branch = branch;
		this.treeADD = treeADD;
		initialize();
		repaint();
	}
	 public void initialize() {
		setLayout(new BorderLayout());
		ArrayList<Variable> posibleVariables = branch.getPotential().getVariables();
		
		/*for (Variable variable : posibleVariables) {*/
		for (int i = 1 ; i < posibleVariables.size(); i++) {
			JCheckBox checkBox = new JCheckBox (posibleVariables.get(i).getName());
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
