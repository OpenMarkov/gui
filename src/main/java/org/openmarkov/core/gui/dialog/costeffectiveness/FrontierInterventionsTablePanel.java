
package org.openmarkov.core.gui.dialog.costeffectiveness;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Table to show frontiers interventions and ICER
 * @author myebra
 */
@SuppressWarnings("serial")
public class FrontierInterventionsTablePanel extends JScrollPane
{
    private List<Intervention> frontierInterventions;
    CostEffectivenessAnalysis  costEffectivenessAnalysis;

    public FrontierInterventionsTablePanel (CostEffectivenessAnalysis costEffectivenessAnalysis)
    {
        super ();
        this.costEffectivenessAnalysis = costEffectivenessAnalysis;
        this.frontierInterventions = costEffectivenessAnalysis.getFrontierInterventions (costEffectivenessAnalysis.getInterventions ());
        NonEditableModel model = new NonEditableModel ();
        JTable table = new JTable (model);
        model.setColumnCount (4);
        model.setNumRows (frontierInterventions.size ());
        model.setRowCount (frontierInterventions.size ());
        table.getColumnModel ().getColumn (0).setHeaderValue ("Strategy");
        table.getColumnModel ().getColumn (1).setHeaderValue ("Effectiveness");
        table.getColumnModel ().getColumn (2).setHeaderValue ("Cost");
        table.getColumnModel ().getColumn (3).setHeaderValue ("ICER");
        List<Intervention> frontierInterventions = costEffectivenessAnalysis.getFrontierInterventions (costEffectivenessAnalysis.getInterventions ());
        List<Intervention> frontierInterventionICER = costEffectivenessAnalysis.calculateICERsOfFrontier (frontierInterventions);
        for (int i = 0; i < frontierInterventions.size (); i++)
        {
            model.setValueAt (frontierInterventionICER.get (i).getName (), i, 0);
            model.setValueAt (frontierInterventionICER.get (i).getEffectiveness (), i, 1);
            model.setValueAt (frontierInterventionICER.get (i).getCost (), i, 2);
            if (i != 0)
            {
                model.setValueAt (frontierInterventionICER.get (i).getICER (), i, 3);
            }
        }
        DefaultTableCellRenderer tcr = new CEResultsCellRenderer ();
        for (int i = 0; i < model.getColumnCount (); i++)
        {
            table.getColumnModel ().getColumn (i).setCellRenderer (tcr);
        }
        table.getTableHeader ().setReorderingAllowed (false);
        table.setAutoResizeMode (JTable.AUTO_RESIZE_OFF);
        table.setForeground (Color.blue);
        // table.setBackground(Color.pink);
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
