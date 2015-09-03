package org.openmarkov.core.gui.dialog.costeffectiveness;

import org.openmarkov.core.model.network.potential.Intervention;
import org.openmarkov.inference.variableElimination.model.CEP;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** @author Manuel Arias */
public class CEPDialog extends JDialog {

	// Aux enum
    private enum CEPColumns {
        LAMBDA_INF,
        LAMBDA_SUP,
        COST,
        EFFECTIVENESS,
        INTERVENTION;
    }

	// Constructor
    /**
     * @param cep <code>CEP</code>
     */
    public CEPDialog(CEP cep){
        this.add(new JScrollPane(getJTableFromCEP(cep)));
        this.pack();
        this.setVisible(true);
    }

    // Public method
    /**
     * @param cep <code>CEP</code>
     * @return <code>JTable</code>
     */
    public JTable getJTableFromCEP(CEP cep) {
        String[] columnNamesInJTable = {"Lambda inf.", "Lambda sup.", "Cost", "Effectiveness", "Intervention"};
        JTable jTableCEP = new JTable(getDataFromCEP(cep), columnNamesInJTable);
        TableColumnModel columnModel = jTableCEP.getColumnModel();
        setToolTipAndColorColumn(jTableCEP, columnModel.getColumn(CEPColumns.INTERVENTION.ordinal()), 
        		"Click to see intervention", Color.yellow);
        setColumnColorColumn(jTableCEP, columnModel.getColumn(CEPColumns.LAMBDA_INF.ordinal()), Color.cyan);
        setColumnColorColumn(jTableCEP, columnModel.getColumn(CEPColumns.LAMBDA_SUP.ordinal()), Color.cyan);
        return jTableCEP;
    }

    /**
     * @param cep <code>CEP</code>
     * @return Rectangular matrix for a <code>JTable</code>.
     */
    private Object[][] getDataFromCEP(CEP cep) {
        double[] costs = cep.getCosts();
        double[] effectiveness = cep.getEffectivities();
        int numRows = costs.length;
        final Intervention[] interventions = cep.getInterventions();
        Object[][] data = new Object[numRows][CEPColumns.values().length];
        for (int i = 0; i < numRows; i++) {
            data[i][CEPColumns.LAMBDA_INF.ordinal()] = getLambdaLeftEndPoint(cep, i);
            data[i][CEPColumns.LAMBDA_SUP.ordinal()] = getLambdaRightEndPoint(cep, i, numRows);
            data[i][CEPColumns.COST.ordinal()] = Double.toString(costs[i]);
            data[i][CEPColumns.EFFECTIVENESS.ordinal()] = Double.toString(effectiveness[i]);
            JButton interventionButton = new JButton("See Intervention");
            final Intervention intervention = interventions[i];
            addInterventionTextToButton(interventionButton, intervention);
            interventionButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showIntervention(intervention);
                }
            });
            data[i][CEPColumns.INTERVENTION.ordinal()] = interventionButton;
        }

        return data;
    }
    
	private void setToolTipAndColorColumn(JTable table, TableColumn column, String text, Color color) {
    	//Set up tool tips.
    	DefaultTableCellRenderer renderer =	new DefaultTableCellRenderer();
    	renderer.setToolTipText("Click to see intervention");
    	renderer.setBackground(color);
    	column.setCellRenderer(renderer);
    }

	private void setColumnColorColumn(JTable table, TableColumn column, Color color) {
    	//Set up tool tips.
    	DefaultTableCellRenderer renderer =	new DefaultTableCellRenderer();
    	renderer.setBackground(color);
    	column.setCellRenderer(renderer);
    }

    private void showIntervention(Intervention intervention) {
        JDialog interventionDialog = new JDialog();
        //TreeADDEditorPanel treeADDEditorPanel = new TreeADDEditorPanel(intervention);
        //interventionDialog.add(treeADDEditorPanel);
        interventionDialog.pack();
        interventionDialog.setVisible(true);
    }

    /**
     * @param cep
     * @param intervalIndex
     * @return Left end point. <code>String</code>
     */
    private String getLambdaLeftEndPoint(CEP cep, int intervalIndex) {
        Double threshold;
        if (intervalIndex == 0) {
            threshold = cep.getMinThreshold();
        } else {
            threshold = cep.getThreshold(intervalIndex - 1);
        }
        return threshold.toString();
    }

    /**
     * @param cep
     * @param intervalIndex
     * @return Right end point. <code>String</code>
     */
    private String getLambdaRightEndPoint(CEP cep, int intervalIndex, int numIntervals) {
        Double threshold;
        if (intervalIndex == numIntervals - 1) {
            threshold = cep.getMaxThreshold();
        } else {
            threshold = cep.getThreshold(intervalIndex);
        }
        return threshold.toString();
    }

    /**
     * @param interventionButton <code>JButton</code>
     * @param intervention <code>Intervention</code>
     */
    private void addInterventionTextToButton(
            JButton interventionButton,
            final Intervention intervention) {
        interventionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getTextWindow(new StringBuilder(intervention.toString()));
            }
        });
    }

    /**
     * @param buffer <code>StringBuffer</code>
     */
    private JFrame getTextWindow(StringBuilder buffer) {
        JFrame frame = new JFrame("Cost-Effectiveness Partition");
        String text = buffer.toString();
        JTextArea textArea = new JTextArea(40, getMaxCharsInALine(text));
        frame.getContentPane().add(textArea, BorderLayout.CENTER);
        JScrollPane scroll = new JScrollPane(textArea);
        frame.getContentPane().add(scroll, BorderLayout.CENTER);
        textArea.setText(text);
        frame.pack();
        frame.setVisible(true);
        return frame;
    }

    /**
     * @param text <code>String</code>
     * @return <code>int</code>
     */
    private int getMaxCharsInALine(String text) {
        int maxLengthLine = 0;
        if (text != null) {
            int position = 0;
            int nextEndLine;
            int textLength = text.length();
            do {
                nextEndLine = text.indexOf('\n', position);
                if (nextEndLine > 0) {
                    int lengthLine = nextEndLine - position;
                    if (lengthLine > maxLengthLine) {
                        maxLengthLine = lengthLine;
                    }
                    position = nextEndLine + 1;
                }
            } while (nextEndLine != -1 && position < textLength);
        }
        return maxLengthLine;
    }
    
}
