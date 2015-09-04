package org.openmarkov.core.gui.dialog.costeffectiveness;

import org.openmarkov.core.model.network.Util;
import org.openmarkov.core.model.network.potential.Intervention;
import org.openmarkov.inference.variableElimination.model.CEP;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** @author Manuel Arias */
@SuppressWarnings("serial")
public class CEPDialog extends JDialog {

	// Constants
    private final int DEFAULT_NUM_DECIMALS = 6;
    
	private final String SEA_SHELL_COLOR = "#FFF5EE";

	private final String LIGHT_SEA_GREEN_COLOR ="#20B2AA";

	// Attributes
	private int numDecimals = DEFAULT_NUM_DECIMALS;
	
	// Constructor
    /**
     * @param cep <code>CEP</code>
     */
    public CEPDialog(CEP cep){
        this.add(new JScrollPane(getJTableFromCEP(cep)));
        this.pack();
        
        // Center dialog
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int x = (screenSize.width - this.getWidth()) / 2;
        int y = (screenSize.height - this.getHeight()) / 2;
        this.setLocation(x, y);
        
        this.setVisible(true);
    }

    // Public method
    /**
     * @param cep <code>CEP</code>
     * @return <code>JTable</code>
     */
    public JTable getJTableFromCEP(final CEP cep) {
    	// Set data in jTable
        final JTable jTableCEP = new JTable(getDataFromCEP(cep), getColumnsStrings());
        
        // Set colors in jTable
        TableColumnModel columnModel = jTableCEP.getColumnModel();
        setToolTipAndColorColumn(jTableCEP, columnModel.getColumn(CEPColumns.INTERVENTION.ordinal()), 
        		"Click to see intervention", Color.decode(SEA_SHELL_COLOR));
        setColumnColorColumn(jTableCEP, columnModel.getColumn(CEPColumns.LAMBDA_INF.ordinal()), Color.decode(LIGHT_SEA_GREEN_COLOR));
        setColumnColorColumn(jTableCEP, columnModel.getColumn(CEPColumns.LAMBDA_SUP.ordinal()), Color.decode(LIGHT_SEA_GREEN_COLOR));
        
        jTableCEP.addMouseListener(new MouseAdapter() 
        {
           public void mouseClicked(MouseEvent event) 
           {
              int row = jTableCEP.rowAtPoint(event.getPoint());
              int column = jTableCEP.columnAtPoint(event.getPoint());
              if (column == CEPColumns.INTERVENTION.ordinal()) {
            	  JFrame interventionFrame = new JFrame();
            	  interventionFrame.add(new JScrollPane(new JTextArea(cep.getInterventions()[row].toString())));
            	  interventionFrame.pack();
            	  interventionFrame.setVisible(true);
              }
           }
        });


        return jTableCEP;
    }

	/** Enumerate to use in JTable columns */
    private enum CEPColumns {
        LAMBDA_INF(0, "Lambda inf."),
        LAMBDA_SUP(1, "Lambda sup."),
        COST(2, "Cost"),
        EFFECTIVENESS(3, "Effectiveness"),
        INTERVENTION(4, "Intervention");
    	
    	private String text;
    	private int index;
    	
    	private CEPColumns(int index, String text) {
    		this.index = index;
    		this.text = text;
    	}
    	private int getIndex() {
    		return index;
    	}
    	public String getText() {
    		return text;
    	}
    }
    
	/**
	 * @return Array of <code>String</code>s with the columns headings
	 */
	private String[] getColumnsStrings() {
		int numColumns = CEPColumns.values().length;
		String[] columnsNames = new String[numColumns];
		for (int i = 0; i < numColumns; i++) {
			columnsNames[i] = CEPColumns.values()[i].getText();
		}
		return columnsNames;
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
            data[i][CEPColumns.LAMBDA_INF.getIndex()] = getLambdaLeftEndPoint(cep, i);
            data[i][CEPColumns.LAMBDA_SUP.getIndex()] = getLambdaRightEndPoint(cep, i, numRows);
            data[i][CEPColumns.COST.getIndex()] = new Double(Util.roundWithSignificantFigures(costs[i], numDecimals)).toString();
            data[i][CEPColumns.EFFECTIVENESS.getIndex()] = new Double(Util.roundWithSignificantFigures(effectiveness[i], numDecimals)).toString();
            data[i][CEPColumns.INTERVENTION.getIndex()] = getFirstLine(interventions[i].toString());
        }

        return data;
    }
    
	private String getFirstLine(String string) {
		int indexEOL = string.indexOf("\n");
		return indexEOL == -1 ? string : string.substring(0, indexEOL); 
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
        return new Double(Util.roundWithSignificantFigures(threshold, numDecimals)).toString();
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
        String lambdaRight;
        if (threshold == Double.POSITIVE_INFINITY) {
        	lambdaRight = "+\u221E"; // +Inifinite
        } else {
        	lambdaRight = new Double(Util.roundWithSignificantFigures(threshold, numDecimals)).toString();
        }
        return lambdaRight;
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
