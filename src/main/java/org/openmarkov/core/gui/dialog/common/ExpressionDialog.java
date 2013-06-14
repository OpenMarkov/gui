/*
 * Copyright 2013 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.dialog.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import org.openmarkov.core.model.network.Variable;

@SuppressWarnings("serial")
public class ExpressionDialog extends OkCancelHorizontalDialog implements DocumentListener {

    private static final Color VALID_EXPRESSION_COLOR   = new Color(180, 215, 170);
    private static final Color INVALID_EXPRESSION_COLOR = new Color(250, 170, 170);
    private JTextField         expressionTextField;
    private String             expression;
    private Evaluator          evaluator;
    private List<String>       variableNames;

    public ExpressionDialog(Window owner, List<Variable> variables, String expression) {
        super(owner);
        setTitle("Enter an expression");
        setIconImage(null);
        this.expression = expression;
        evaluator = new Evaluator();
        Map<String, String> variableValues = new HashMap<>();
        for (Variable variable : variables) {
            variableValues.put(variable.getName(), "1.0");
        }
        evaluator.setVariables(variableValues);
        variableNames = new ArrayList<String>(variableValues.keySet());
        Collections.sort(variableNames);
        initializeComponents();
        setLocationRelativeTo(null);
        expressionTextField.getDocument().addDocumentListener(this);
        expressionTextField.setBackground((isValidExpression()) ? VALID_EXPRESSION_COLOR
                : INVALID_EXPRESSION_COLOR);
    }

    public ExpressionDialog(Window owner, List<Variable> variables) {
        this(owner, variables, null);
    }

    private void initializeComponents() {
        JPanel expressionPanel = new JPanel();
        expressionTextField = new JTextField();
        expressionTextField.setPreferredSize(new Dimension(250, 20));
        if (expression != null) {
            expressionTextField.setText(expression);
        }
        expressionPanel.add(expressionTextField);
        getComponentsPanel().add(expressionPanel, BorderLayout.NORTH);
        pack();
    }

    @Override
    protected boolean doOkClickBeforeHide() {
        expression = expressionTextField.getText();
        return isValidExpression();
    }

    @Override
    protected void doCancelClickBeforeHide() {
        expression = null;
    }

    public String getExpression() {
        return expression;
    }

    private boolean isValidExpression() {
        String processedExpression = processExpression(expressionTextField.getText());
        boolean result = false;
        try {
            evaluator.evaluate(processedExpression);
            result = true;
        } catch (EvaluationException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String processExpression(String expression) {
        String processedExpression = expression;
        for (String variableName : variableNames) {
            processedExpression = processedExpression.replace(variableName, "#{"
                    + variableName
                    + "}");
        }
        return processedExpression;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        expressionTextField.setBackground((isValidExpression()) ? VALID_EXPRESSION_COLOR
                : INVALID_EXPRESSION_COLOR);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        expressionTextField.setBackground((isValidExpression()) ? VALID_EXPRESSION_COLOR
                : INVALID_EXPRESSION_COLOR);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        expressionTextField.setBackground((isValidExpression()) ? VALID_EXPRESSION_COLOR
                : INVALID_EXPRESSION_COLOR);
    }
}
