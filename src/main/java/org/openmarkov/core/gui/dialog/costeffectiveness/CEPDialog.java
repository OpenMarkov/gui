package org.openmarkov.core.gui.dialog.costeffectiveness;

import org.openmarkov.core.model.network.potential.Intervention;
import org.openmarkov.inference.variableElimination.model.CEP;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CEPDialog extends JDialog {

    private JTable table;

    public CEPDialog(CEP cep){
        table = getJTableFromCEP(cep);
        this.add(table);
        this.pack();
        this.setVisible(true);

    }


    // Public method
    /**
     * @param cep <code>CEP</code>
     * @return <code>JTable</code>
     */
    public static JTable getJTableFromCEP(CEP cep) {
        String[] columnNamesInJTable = {"Lambda inf.", "Lambda sup.", "Cost", "Effectiveness", "Intervention"};
        JTable jTableCEP = new JTable(getDataFromCEP(cep), columnNamesInJTable);
        return jTableCEP;
    }

    private enum CEPColumns {
        LAMBDA_INF,
        LAMBDA_SUP,
        COST,
        EFFECTIVENESS,
        INTERVENTION;
    }

    /**
     * @param cep <code>CEP</code>
     * @return Rectangular matrix for a <code>JTable</code>.
     */
    private static Object[][] getDataFromCEP(CEP cep) {
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
            JTextField interventionButton = new JTextField("See Intervention");
//            addInterventionTextToButton(interventionButton, interventions[i]);

            final int finalI = i;
            interventionButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showIntervention(interventions[finalI]);
                }
            });
            data[i][CEPColumns.INTERVENTION.ordinal()] = interventionButton;
        }

        return data;
    }

    private static void showIntervention(Intervention intervention) {
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
    private static String getLambdaLeftEndPoint(CEP cep, int intervalIndex) {
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
    private static String getLambdaRightEndPoint(CEP cep, int intervalIndex, int numIntervals) {
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
    private static void addInterventionTextToButton(
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
    private static JFrame getTextWindow(StringBuilder buffer) {
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
    private static int getMaxCharsInALine(String text) {
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
