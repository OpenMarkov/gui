/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.gui.dialog.common;

import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.Variable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Panel with a function
 * @author cmyago
 * @version 1
 */
public class FunctionPanel
		extends JPanel {

	/**
	 * Panel with the function
	 */

	protected JTextArea functionTextArea = null;
	/**
	 *
	 */
	protected String function;
	/**
	 * Variables list
	 */

	/**
	 * Parents list
	 */
	protected List<Variable> functionVariables;
	private Node node = null;
//	private FunctionPotential potential = null;

	public FunctionPanel(List<Variable> functionVariables, String function) {
		super();
		this.functionVariables = functionVariables;
		this.function = function;
		Border titledBorder = BorderFactory.createTitledBorder("Function");
		this.setLayout(new FlowLayout());
		this.setPreferredSize(new Dimension(750, 50));
		this.setBorder(titledBorder);
		this.add(getFunctionTextArea());
		functionTextArea.setText(function);
		functionTextArea.addMouseListener(new FunctionTextAreaMouseListener());

	}

	protected JTextArea getFunctionTextArea() {
		if (functionTextArea == null) {
			functionTextArea = new JTextArea();
			functionTextArea.setEditable(true);
		}
		return functionTextArea;
	}


	public String getFunction() {
		return function;
	}

	/**
	 * Sets a valid function in the text area
	 * @param functionStr valid function for variables
	 */
	public void setFunction(String functionStr){
		function = functionStr;
		functionTextArea.setText(function);

	}

	private class FunctionTextAreaMouseListener extends MouseAdapter {
		@Override public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() >= 1) {
				ArithmeticExpressionDialog expressionDialog = new ArithmeticExpressionDialog(null, functionVariables, function);
				expressionDialog.setVisible(true);
				if (expressionDialog.getSelectedButton() == OkCancelHorizontalDialog.OK_BUTTON) {
					function = expressionDialog.getExpression();
					functionTextArea.setText(function);
				}
			}
		}
	}

}
