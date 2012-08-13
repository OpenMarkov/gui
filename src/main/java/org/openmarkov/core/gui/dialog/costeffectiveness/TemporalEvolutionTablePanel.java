package org.openmarkov.core.gui.dialog.costeffectiveness;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import org.apache.poi.ss.formula.functions.Columns;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.TablePotential;

@SuppressWarnings("serial")
public class TemporalEvolutionTablePanel extends JPanel {

	private JScrollPane valuesTableScrollPane;
	private HashMap<Variable,TablePotential> temporalEvolution;
	private Variable variableOfInterest;
	private ProbNet expandedNetwork;
	private CostEffectivenessDialog costEffectivenessDialog;
	private boolean isUtility;
	private boolean isAccumulative;
	
	public TemporalEvolutionTablePanel(HashMap<Variable,TablePotential> temporalEvolution, ProbNet expandedNetwork,
			CostEffectivenessDialog costEffectivenessDialog, Variable variableOfInterest, boolean isUtility/*, boolean isAcumulative*/) {
		super();
		removeAll();
		this.temporalEvolution = temporalEvolution;
		this.variableOfInterest = variableOfInterest;
		this.expandedNetwork = expandedNetwork;
		this.costEffectivenessDialog = costEffectivenessDialog;
		this.isUtility = isUtility;
		this.isAccumulative = costEffectivenessDialog.isAccumulative();
		//this.isAcumulative = isAcumulative;
		
		setLayout(new BorderLayout());
		//add(getValuesTableScrollPane(), BorderLayout.CENTER);
		repaint();
		
	}
	/**
	 * This method initializes valuesTableScrollPane.
	 * 
	 * @return a new values table scroll pane.
	 */
	public JScrollPane getValuesTableScrollPane() {

		if (valuesTableScrollPane == null) {
			valuesTableScrollPane = new JScrollPane();
			//valuesTableScrollPane.setName( "ICIPotentialsTablePanel.valuesTableScrollPane" );
			NonEditableModel model = new NonEditableModel();
			JTable table = new JTable(model);
			//JTable table = new JTable(info, columnNames);
			//table.setModel();
			model.setColumnCount(temporalEvolution.size()+1);
			model.setNumRows(variableOfInterest.getNumStates());
			model.setRowCount(variableOfInterest.getNumStates());
			final Object[][] info = new Object[variableOfInterest.getNumStates()][temporalEvolution.size()+1];
			final Object[] states = new Object[variableOfInterest.getNumStates()];
			//first column
			for (int i = 0; i < variableOfInterest.getNumStates(); i++) {
				if (isUtility){
					info [i][0] = variableOfInterest.getBaseName();
					model.setValueAt(variableOfInterest.getBaseName(), i, 0);
				} else {
					info [i][0] = variableOfInterest.getStateName(i);
					//states[i] = variableOfInterest.getStateName(i);
					//model.addColumn("", states);
					model.setValueAt(variableOfInterest.getStateName(i), i, 0);
				}
				
				
			}
			
			final String[] columnNames = new String[temporalEvolution.size()+1];
				columnNames[0] = " ";
				table.getColumnModel().getColumn(0).setHeaderValue("");
				//model.setValueAt("", 0, 0);
				String basename = variableOfInterest.getBaseName();
		    	ArrayList<ProbNode> probNodes = expandedNetwork.getProbNodes();
		    	
		    	for (int i = 0; i < columnNames.length; i++) {
		    		for (int j = 0; j < probNodes.size(); j++ ) {
		    			if (probNodes.get(j).getVariable().getBaseName().equals(basename) && 
		    					probNodes.get(j).getVariable().getTimeSlice() == i) {
		    				columnNames[i+1] = probNodes.get(j).getVariable().getName();
			    			//model.setValueAt( probNodes.get(i).getVariable().getName(), 0, i+1);
			    			table.getColumnModel().getColumn(i+1).setHeaderValue(probNodes.get(j).getVariable().getName());
		    			}
		    		}
		    	}
		    	
				/*for (int i = 0; i < probNodes.size(); i++) {
		    		if (probNodes.get(i).getVariable().getBaseName().equals(basename)) {
		    			columnNames[i+1] = probNodes.get(i).getVariable().getName();
		    			//model.setValueAt( probNodes.get(i).getVariable().getName(), 0, i+1);
		    			table.getColumnModel().getColumn(i+1).setHeaderValue(probNodes.get(i).getVariable().getName());
				   }
				}*/
				//model.addRow(columnNames);
				//model.addColumn(columnNames);
				
		    double value = 0.0;
			for (int i = 0; i < variableOfInterest.getNumStates(); i++) {//row
				for (int j = 0; j < costEffectivenessDialog.getNumSlices(); j++) { //column
					String basenameInterest = variableOfInterest.getBaseName();
					ArrayList<ProbNode> expandedProbNodes = expandedNetwork.getProbNodes();
					for (int k = 0; k < expandedProbNodes.size(); k++) {
						if (expandedProbNodes.get(k).getVariable().getBaseName().equals(basenameInterest) 
								&& expandedProbNodes.get(k).getVariable().getTimeSlice() == j) {
							
							if (isUtility && isAccumulative) {
								value += temporalEvolution.get(expandedProbNodes.get(k).getVariable()).getValues()[i];
								//cell(row, column) = cell(i+1, j+1)
								info[i][j+1] = value;
								model.setValueAt(value, i, j+1);
							} else {
								value = temporalEvolution.get(expandedProbNodes.get(k).getVariable()).getValues()[i];
								//cell(row, column) = cell(i+1, j+1)
								info[i][j+1] = value;
								model.setValueAt(value, i, j+1);
							}
							
						}
					}

				}
			}
			//table.setTableHeader(null);
			//table.getTableHeader().setVisible(false);
			table.getTableHeader().setReorderingAllowed(false); 
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			table.setForeground(Color.blue);
			table.setBackground(Color.pink);
			valuesTableScrollPane.setViewportView( table );
			valuesTableScrollPane.setAutoscrolls(true);
		}
		return valuesTableScrollPane;
	}
	
	public class NonEditableModel extends DefaultTableModel
	{
	   public boolean isCellEditable (int row, int column)
	   {
		   return false;
	   }
	}

}
