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
import java.util.ArrayList;

import javax.swing.BoxLayout;
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
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		ArrayList<State> states = branch.getBranchStates();
		
		for (State state : states) {
			
			JCheckBox checkBox = new JCheckBox (state.getName());
			checkBoxes.add(checkBox);
			//checkBox.setAlignmentX((float) 0.5);
			add(checkBox, BorderLayout.CENTER);
			
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
