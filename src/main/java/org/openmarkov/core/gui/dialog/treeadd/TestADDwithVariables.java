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
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;

/** For testing purposes: must be relocated to the tests package
 * 
 * @author Jorge
 *
 */
public class TestADDwithVariables {
	static ArrayList<Variable> createTest01 () {
		ArrayList<Variable> variables= new ArrayList<Variable> ();
		
		variables.add (new Variable ("A", 2));		
		variables.add (new Variable ("B", 2));
		variables.add (new Variable ("C", 4));
		variables.add (new Variable ("D", 3));
		
		Variable cont= new Variable ("X");
		variables.add (cont);
		
		return variables;
	}
	
	public static void main(String args[]) throws NotEnoughMemoryException {
		try {
			UIManager.setLookAndFeel (UIManager.getSystemLookAndFeelClassName ());
		}
		catch (Exception exception) {
			exception.printStackTrace ();
		}

		JFrame frame;
		frame = new JFrame ();
		frame.setTitle ("Applet Frame");
		
		// Close app when close button is touched
		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		frame.setLayout (new BorderLayout ());
		
		ArrayList<Variable> variables= createTest01();
		
		/*
		TablePotential dummyPot= new TablePotential (variables, PotentialRole.CONDITIONAL_PROBABILITY);
		*/

		TablePotential dummyPot= new TablePotential (variables, PotentialRole.UTILITY);
		dummyPot.setUtilityVariable(new Variable("U"));
		
		TreeADDBuilder treeBuilder= new TreeADDBuilder (dummyPot);
		frame.add (treeBuilder, BorderLayout.CENTER);
		
		frame.setSize (500, 620);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation ((d.width - frame.getSize().width) / 2, (d.height - frame.getSize().height) / 2);
		frame.setVisible (true);
	}		
}
