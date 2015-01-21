package org.openmarkov.core.gui.multicriteria;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JTable;

import org.openmarkov.core.gui.component.ValuesTableCellRenderer;
import org.openmarkov.core.model.network.Criterion;

/**
 * Renderer for the Multi criteria table
 * 
 * @author Jorge
 *
 */
public class MultiCriteriaComboBoxRenderer extends ValuesTableCellRenderer {

	public MultiCriteriaComboBoxRenderer() {
		super(1);
	}

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 7294499626818840525L;


	@SuppressWarnings("unchecked")
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		if(row == 0){
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}else{
			JComboBox<String> comboBox = new JComboBox<String>();
			comboBox.addItem(Criterion.CostEffectivenessType.Null.toString());
			comboBox.addItem(Criterion.CostEffectivenessType.Cost.toString());
			comboBox.addItem(Criterion.CostEffectivenessType.Effectiveness.toString());
			
			if(value.getClass().equals(JComboBox.class)){
				comboBox.setSelectedItem(((JComboBox<String>) value).getSelectedItem());
			}else if(value.getClass().equals(String.class)){
				comboBox.setSelectedItem((String) value);
			}

			return comboBox;
		}
		

	}

}
