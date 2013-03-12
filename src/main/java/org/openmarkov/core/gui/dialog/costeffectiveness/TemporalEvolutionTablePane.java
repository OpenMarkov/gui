
package org.openmarkov.core.gui.dialog.costeffectiveness;

import java.awt.Component;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.TablePotential;

/**
 * Table to show temporal evolution of temporal variables
 * @author myebra
 */
@SuppressWarnings("serial")
public class TemporalEvolutionTablePane extends JScrollPane
{
    public TemporalEvolutionTablePane (HashMap<Variable, TablePotential> temporalEvolution,
                                        ProbNet expandedNetwork,
                                        Variable variableOfInterest,
                                        int numSlices,
                                        boolean isUtility,
                                        boolean isCumulative)
    {
        super ();
        NonEditableModel model = new NonEditableModel ();
        JTable table = new JTable (model);
        model.setColumnCount (temporalEvolution.size () + 1);
        model.setNumRows (variableOfInterest.getNumStates ());
        model.setRowCount (variableOfInterest.getNumStates ());
        final Object[][] info = new Object[variableOfInterest.getNumStates ()][temporalEvolution.size () + 1];
        // first column
        for (int i = 0; i < variableOfInterest.getNumStates (); i++)
        {
            if (isUtility)
            {
                info[i][0] = variableOfInterest.getBaseName ();
                model.setValueAt (variableOfInterest.getBaseName (), i, 0);
            }
            else
            {
                info[i][0] = variableOfInterest.getStateName (i);
                // states[i] = variableOfInterest.getStateName(i);
                // model.addColumn("", states);
                model.setValueAt (variableOfInterest.getStateName (i), i, 0);
            }
        }
        final String[] columnNames = new String[temporalEvolution.size () + 1];
        columnNames[0] = " ";
        table.getColumnModel ().getColumn (0).setHeaderValue ("");
        String basename = variableOfInterest.getBaseName ();
        List<ProbNode> probNodes = expandedNetwork.getProbNodes ();
        TableCellRenderer cellRenderer = new CEResultsCellRenderer ();
        for (int i = 0; i < columnNames.length; i++)
        {
            for (int j = 0; j < probNodes.size (); j++)
            {
                Variable variable = probNodes.get (j).getVariable ();
                if (variable.getBaseName ().equals (basename) && variable.getTimeSlice () == i)
                {
                    columnNames[i + 1] = variable.getName ();
                    table.getColumnModel ().getColumn (i + 1).setHeaderValue (variable.getName ());
                    table.getColumnModel ().getColumn (i + 1).setCellRenderer (cellRenderer);
                }
            }
        }
        Double value = 0.0;
        List<ProbNode> expandedProbNodes = expandedNetwork.getProbNodes ();
        for (int i = 0; i < variableOfInterest.getNumStates (); i++)
        {// row
            String basenameInterest = variableOfInterest.getBaseName ();
            for (int j = 0; j < numSlices; j++)
            { // column
                for (ProbNode expandedProbNode : expandedProbNodes)
                {
                    Variable variable = expandedProbNode.getVariable ();
                    if (variable.getBaseName ().equals (basenameInterest)
                        && variable.getTimeSlice () == j)
                    {
                        if (isUtility && isCumulative)
                        {
                            value += temporalEvolution.get (variable).getValues ()[i];
                            // cell(row, column) = cell(i+1, j+1)
                            info[i][j + 1] = value;
                            model.setValueAt (value, i, j + 1);
                        }
                        else
                        {
                            value = temporalEvolution.get (variable).getValues ()[i];
                            // cell(row, column) = cell(i+1, j+1)
                            info[i][j + 1] = value;
                            model.setValueAt (value, i, j + 1);
                        }
                    }
                }
            }
        }
        // table.setTableHeader(null);
        // table.getTableHeader().setVisible(false);
        table.getTableHeader ().setReorderingAllowed (false);
        table.setAutoResizeMode (JTable.AUTO_RESIZE_OFF);
        table.setDefaultRenderer (Double.class, new CEResultsCellRenderer ());
        setViewportView (table);
        setAutoscrolls (true);
    }
    public class NonEditableModel extends DefaultTableModel
    {
        public boolean isCellEditable (int row, int column)
        {
            return false;
        }
    }
}
