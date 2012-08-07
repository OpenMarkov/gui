package org.openmarkov.core.gui.dialog.costeffectiveness;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


@SuppressWarnings("serial")
public class FrontierInterventionsTablePanel extends JPanel{

	private ArrayList<Intervention> frontierInterventions;
	private JScrollPane valuesTableScrollPane;
	CostEffectivenessAnalysis costEffectivenessAnalysis;
	
	public FrontierInterventionsTablePanel(CostEffectivenessAnalysis costEffectivenessAnalysis) {
		super();
		removeAll();
		this.costEffectivenessAnalysis = costEffectivenessAnalysis;
		this.frontierInterventions = costEffectivenessAnalysis.getFrontierIntervention(
				costEffectivenessAnalysis.getInterventions().toArray(new Intervention[costEffectivenessAnalysis.getInterventions().size()] ));
		
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
			NonEditableModel model = new NonEditableModel();
			JTable table = new JTable(model);
			
			model.setColumnCount(4);
			model.setNumRows(frontierInterventions.size());
			model.setRowCount(frontierInterventions.size());
			
			table.getColumnModel().getColumn(0).setHeaderValue("Strategy");
			table.getColumnModel().getColumn(1).setHeaderValue("Effectiveness");
			table.getColumnModel().getColumn(2).setHeaderValue("Cost");
			table.getColumnModel().getColumn(3).setHeaderValue("ICER");
					
			ArrayList<Intervention> frontierInterventionICER = costEffectivenessAnalysis.calculateIncrementalCERatiosOfFrontier(costEffectivenessAnalysis.getFrontierIntervention(
					costEffectivenessAnalysis.getInterventions().toArray(new Intervention[costEffectivenessAnalysis.getInterventions().size()] )));
						
			for (int i = 0; i < frontierInterventions.size(); i++) {
				model.setValueAt(frontierInterventionICER.get(i).getName(), i, 0);
				model.setValueAt(frontierInterventionICER.get(i).getEffectiveness(), i, 1);
				model.setValueAt(frontierInterventionICER.get(i).getCost(), i, 2);
				if (i != 0) {
					model.setValueAt(frontierInterventionICER.get(i).getICER(), i, 3);
				}
			}
			
			DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
			tcr.setHorizontalAlignment(SwingConstants.CENTER);
			
			for (int i = 0; i < model.getColumnCount(); i++) {
				table.getColumnModel().getColumn(i).setCellRenderer(tcr);
				table.getColumnModel().getColumn(3).setCellRenderer(tcr);
			}
			
			table.getTableHeader().setReorderingAllowed(false); 
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			table.setForeground(Color.blue);
			//table.setBackground(Color.pink);
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
