package org.openmarkov.core.gui.costeffectiveness;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.TablePotential;

/**
 * Table to show temporal evolution of temporal variables
 * 
 * @author myebra
 */
@SuppressWarnings("serial")
public class TemporalEvolutionTablePane extends JScrollPane {
	
	private JTable table;
    /*
	public TemporalEvolutionTablePane(Map<Variable, TablePotential> temporalEvolution,
            ProbNet expandedNetwork, Variable variableOfInterest, List<Variable> conditioningVariables,int numSlices, boolean isUtility,
            boolean isCumulative) {
        super();
        
        Map<Integer, double[]> temporalEvolutionValues = new LinkedHashMap<>();
        Set<Variable> variables = temporalEvolution.keySet();
        int timeSlice = 0;
        for (int i = 0; i < variables.size(); i++) {
            boolean found = false;
            while (!found) {
                for (Variable variable : variables) {
                    if (variable.getTimeSlice() == timeSlice) {
                        found = true;
                        temporalEvolutionValues.put(timeSlice, temporalEvolution.get(variable)
                                .getValues());
                    }
                }
                timeSlice++;
            }
        }        
        int numColumns = timeSlice + 1;
        
        NonEditableModel model = new NonEditableModel();
        JTable table = new JTable(model);
        
        model.setColumnCount(numColumns);
        model.setNumRows(variableOfInterest.getNumStates());
        model.setRowCount(variableOfInterest.getNumStates());
        final Object[][] info = new Object[variableOfInterest.getNumStates()][numColumns];
        // first column
        for (int i = 0; i < variableOfInterest.getNumStates(); i++) {
            if (isUtility) {
                info[i][0] = variableOfInterest.getBaseName();
                model.setValueAt(variableOfInterest.getBaseName(), i, 0);
            } else {
                info[i][0] = variableOfInterest.getStateName(i);
                model.setValueAt(variableOfInterest.getStateName(i), i, 0);
            }
        }
        final String[] columnNames = new String[numColumns];
        columnNames[0] = " ";
        table.getColumnModel().getColumn(0).setHeaderValue("");
        TableCellRenderer cellRenderer = new CEResultsCellRenderer();

        Double value = 0.0;
        for (int cycle = 0; cycle < timeSlice; ++cycle) { // column
            int columnIndex = cycle+1;
            table.getColumnModel().getColumn(columnIndex).setHeaderValue(cycle);
            table.getColumnModel().getColumn(columnIndex).setCellRenderer(cellRenderer);
            for (int i = 0; i < variableOfInterest.getNumStates(); i++) {// row
                if (isUtility && isCumulative) {
                    if(temporalEvolutionValues.containsKey(cycle))
                    {
                        value += temporalEvolutionValues.get(cycle)[i];
                    }
                    // cell(row, column) = cell(i+1, j+1)
                    info[i][columnIndex] = value;
                    model.setValueAt(value, i, columnIndex);
                } else {
                    if(temporalEvolutionValues.containsKey(cycle))
                    {
                        value = temporalEvolutionValues.get(cycle)[i];
                    }else
                    {
                        value = 0.0;
                    }
                    // cell(row, column) = cell(i+1, j+1)
                    info[i][columnIndex] = value;
                    model.setValueAt(value, i, columnIndex);
                }
            }
        }
        // table.setTableHeader(null);
        // table.getTableHeader().setVisible(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setDefaultRenderer(Double.class, new CEResultsCellRenderer());
        setViewportView(table);
        setAutoscrolls(true);
    }*/
	
	public TemporalEvolutionTablePane(Map<Variable, TablePotential> temporalEvolution,
            ProbNet expandedNetwork, Variable variableOfInterest, List<Variable> conditioningVariables,int numSlices, boolean isUtility,
            boolean isCumulative) {
        super();

        Map<Integer, double[]> temporalEvolutionValues = new LinkedHashMap<>();
        Set<Variable> variables = temporalEvolution.keySet();
        int timeSlice = 0;
        for (int i = 0; i < variables.size(); i++) {
            boolean found = false;
            while (!found) {
                for (Variable variable : variables) {
                    if (variable.getTimeSlice() == timeSlice) {
                        found = true;
                        temporalEvolutionValues.put(timeSlice, temporalEvolution.get(variable)
                                .getValues());
                    }
                }
                timeSlice++;
            }
        }        
        int numColumns = timeSlice + conditioningVariables.size() + 1;
        if(isUtility){
        	numColumns--;
        }
        
        int numRows = variableOfInterest.getNumStates();
        for(int i = 0; i < conditioningVariables.size(); i++){
        	numRows *= conditioningVariables.get(i).getNumStates();
        }
        

        NonEditableModel model = new NonEditableModel();
        table = new JTable(model);
        
        model.setColumnCount(numColumns);
        model.setNumRows(numRows);

        final Object[][] info = new Object[numRows][numColumns];
        
        // Fill conditioning variables
        int lastColumnIndex = 0;
        for(lastColumnIndex = 0; lastColumnIndex < conditioningVariables.size(); lastColumnIndex++){
        	String columnName = conditioningVariables.get(lastColumnIndex).getName();
        	table.getColumnModel().getColumn(lastColumnIndex).setHeaderValue(columnName);
        	
        	for(int i = 0; i < numRows; i++){
        		int stateIndex = (i/variableOfInterest.getNumStates())%conditioningVariables.get(lastColumnIndex).getNumStates();
        		String stateName = conditioningVariables.get(lastColumnIndex).getStateName(stateIndex);
        		model.setValueAt(stateName, i, lastColumnIndex);
        	}
        }
        
        if(!isUtility){
	        // States of the Variable of Interest
	        table.getColumnModel().getColumn(lastColumnIndex).setHeaderValue(StringDatabase.getUniqueInstance().getString("TemporalEvolutionResultDialog.States.Label"));
	        for (int i = 0; i < numRows; i++) {
            	info[i][lastColumnIndex] = variableOfInterest.getStateName(i%variableOfInterest.getNumStates());
                model.setValueAt(variableOfInterest.getStateName(i%variableOfInterest.getNumStates()), i, 1);
	        }
	        lastColumnIndex++;
        }

        
        TableCellRenderer cellRenderer = new CEResultsCellRenderer();

        // Fill data
        Double values[] = new Double[numRows];
        for(int i = 0; i < values.length; i++){
        	values[i] = 0.0;
        }
        for (int cycle = 0; cycle < timeSlice; ++cycle) { // column
            int columnIndex = lastColumnIndex + cycle;
            table.getColumnModel().getColumn(columnIndex).setHeaderValue(cycle);
            table.getColumnModel().getColumn(columnIndex).setCellRenderer(cellRenderer);
            for (int i = 0; i < numRows; i++) {// row
                if (isUtility && isCumulative) {
                    if(temporalEvolutionValues.containsKey(cycle))
                    {
                    	values[i] += temporalEvolutionValues.get(cycle)[i];
                    }
                    // cell(row, column) = cell(i+1, j+1)
                    info[i][columnIndex] = values[i];
                    model.setValueAt(values[i], i, columnIndex);
                } else {
                    if(temporalEvolutionValues.containsKey(cycle))
                    {
                        values[i] = temporalEvolutionValues.get(cycle)[i];
                    }else
                    {
                    	values[i] = 0.0;
                    }
                    // cell(row, column) = cell(i+1, j+1)
                    info[i][columnIndex] = values[i];
                    model.setValueAt(values[i], i, columnIndex);
                }
            }
        }
        
        //table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setDefaultRenderer(Double.class, new CEResultsCellRenderer());
        setViewportView(table);
        setAutoscrolls(true);
    }

    public JTable getTable() {
        return table;
    }

    public class NonEditableModel extends DefaultTableModel {
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}
