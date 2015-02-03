package org.openmarkov.core.gui.multicriteria;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.JTextComponent;

import org.openmarkov.core.action.MulticriteriaEdit;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.component.ValuesTableCellRenderer;
import org.openmarkov.core.gui.dialog.common.OkCancelHorizontalDialog;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.inference.MulticriteriaOptions;
import org.openmarkov.core.model.network.Criterion;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.TemporalUnit;
import org.openmarkov.core.model.network.TemporalUnit.Unit;
import org.openmarkov.core.model.network.constraint.OnlyAtemporalVariables;

public class MulticriteriaDialog extends OkCancelHorizontalDialog {

	/**
	 * Reference to the localize object
	 */
	StringDatabase stringDatabase = StringDatabase.getUniqueInstance();
	
	/**
	 * Scroll Pane for the table
	 */
	private JScrollPane tableScrollPane;
	
	/**
	 * Table
	 */
	private JTable table;
	
	/**
	 * Unicriterion radio button
	 */
	private JRadioButton unicriterion;
	
	/**
	 * Cost Effectiveness radio button
	 */
	private JRadioButton costEffectiveness;
	
	/**
	 * Temporal copy of the decisionCriteria
	 */
	private List<Criterion> decisionCriteria;
	
	/**
	 * Main panel of the layout
	 */
	private JPanel mainPanel;
	
	/**
	 * Panel in which the user can select the main unit of the unicriterion conversion
	 */
	private JPanel unitsPanel;
	
	/**
	 * ProbNet in which we are working
	 */
	private ProbNet probNet;
	
	/**
	 * Combobox with all the possible units of the decision criteria
	 */
	private JComboBox<String> existingUnits;
	
	/**
	 * Boolean attribute that indicates if the probnet is temporal or not
	 */
	private boolean isTemporal;
	
	/**
	 * Temporal copy of Multicriteria options
	 */
	private MulticriteriaOptions multicriteriaOptions;
	
	/**
	 * Constant for Criteria column 
	 */
	public static final int CRITERION_COLUMN = 0;
	
	/**
	 * Constant for Scales column 
	 */
	public static final int SCALE_COLUMN = 1;
	/**
	 * Constant for Uses column 
	 */
	public static final int USE_COLUMN = 1;
	/**
	 * Constant for Discounts column 
	 */
	public static final int DISCOUNT_COLUMN = 2;
	
	/**
	 * Constant for Discounts column 
	 */
	public static final int DISCOUNT_UNIT__COLUMN = 3;

	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Dialog constructor
	 * @param probNet
	 * @param owner
	 */
	public MulticriteriaDialog(ProbNet probNet, Window owner) {
		super(owner);
		
		this.probNet = probNet;
		
		// If the net has more than atemporal variables, the net would be temporal
		if(!probNet.hasConstraint(OnlyAtemporalVariables.class)){
			isTemporal = true;
		}else{
			isTemporal = false;
		}
		
		// Center the dialog
		setLocationRelativeTo (owner);
		
		// Make a working copy of the criteria
		this.decisionCriteria = new ArrayList<Criterion>();
		
		for(Criterion criterion : probNet.getDecisionCriteria()){
			this.decisionCriteria.add(criterion.clone());
		};
		
		// Make a working copy of the multicriteria options
		this.multicriteriaOptions = probNet.getInferenceOptions().getMultiCriteriaOptions().clone();
		
		mainPanel = new JPanel();
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

		mainPanel.add(getUnitsAndSelectPanels());
		
		tableScrollPane = getTablePanel();
		mainPanel.add(tableScrollPane);

		mainPanel.setVisible(true);
		this.add(mainPanel);

		this.setTitle(stringDatabase.getString("MulticriteriaDialog.Title.Label"));
		
		this.setIconImage(null);
		this.setResizable(false);
		this.pack();
		if(probNet.getInferenceOptions().getMultiCriteriaOptions().getMulticriteriaType() != null){
			if(multicriteriaOptions.getMulticriteriaType().equals(MulticriteriaOptions.Type.UNICRITERION)){
				unicriterion.doClick();
			}else if (multicriteriaOptions.getMulticriteriaType().equals(MulticriteriaOptions.Type.COST_EFFECTIVENESS)){
				costEffectiveness.doClick();
			}
		}
	}

	/**
	 * Gets the Panel in which we have the conversion unit and the panel in which
	 * we have the multicriteria type to be applied
	 * @return
	 */
	private JPanel getUnitsAndSelectPanels(){
		JPanel mixedPanel = new JPanel();
		mixedPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		JPanel selectTypePanel = getSelectTypePanel(); 
		mixedPanel.add(selectTypePanel);
		
		unitsPanel = getUnitsPanel();
		unitsPanel.setPreferredSize(selectTypePanel.getPreferredSize());
		mixedPanel.add(unitsPanel);
		
		return mixedPanel;
	}
	
	/**
	 * Panel in which we have the Table with the criteria data
	 * @return
	 */
	private JScrollPane getTablePanel() {
		
		JComboBox<String> comboBoxUse = null;
		JComboBox<String> comboBoxDiscountUnits = null;
		MultiCriteriaTableModel model;
		ValuesTableCellRenderer renderer = new ValuesTableCellRenderer(1);


		model = new MultiCriteriaTableModel();		
		// Construction of the TableModel		
		if (costEffectiveness.isSelected()) {
			model.addColumn("criterion");
			model.addColumn("use");
			if(isTemporal){
				model.addColumn("discount");
				model.addColumn("discountUnit");
				model.addRow(new Object[] { 
						stringDatabase.getString("MulticriteriaDialog.TableHeader.Criterion"),
						stringDatabase.getString("MulticriteriaDialog.TableHeader.Use"),
						stringDatabase.getString("MulticriteriaDialog.TableHeader.Discount"),
						stringDatabase.getString("MulticriteriaDialog.TableHeader.Unit")
						});
			}else{
				model.addRow(new Object[] {
						stringDatabase.getString("MulticriteriaDialog.TableHeader.Criterion"),
						stringDatabase.getString("MulticriteriaDialog.TableHeader.Use")});
			}
		} else {
			model.addColumn("criterion");
			model.addColumn("scale");
			if(isTemporal){
				model.addColumn("discount");
				model.addColumn("discountUnit");
				model.addRow(new Object[] {
						stringDatabase.getString("MulticriteriaDialog.TableHeader.Criterion"),
						stringDatabase.getString("MulticriteriaDialog.TableHeader.Scale"),
						stringDatabase.getString("MulticriteriaDialog.TableHeader.Discount"),
						stringDatabase.getString("MulticriteriaDialog.TableHeader.Unit")
						});	
			}else{
				model.addRow(new Object[] { 
						stringDatabase.getString("MulticriteriaDialog.TableHeader.Criterion"),
						stringDatabase.getString("MulticriteriaDialog.TableHeader.Scale")});
			}
			
		}

		// Fill the rows with the criteria
		if (costEffectiveness.isSelected()) {
			
			for (Criterion criterion : decisionCriteria) {
				comboBoxUse = new JComboBox<String>();
				comboBoxUse.addItem(Criterion.CostEffectivenessType.Null.toString());
				comboBoxUse.addItem(Criterion.CostEffectivenessType.Cost.toString());
				comboBoxUse.addItem(Criterion.CostEffectivenessType.Effectiveness.toString());
				
				if(criterion.getCe_criterion().equals(Criterion.CostEffectivenessType.Cost)){
					comboBoxUse.setSelectedItem(Criterion.CostEffectivenessType.Cost.toString());
				} else if(criterion.getCe_criterion().equals(Criterion.CostEffectivenessType.Effectiveness)){
					comboBoxUse.setSelectedItem(Criterion.CostEffectivenessType.Effectiveness.toString());
				} else {
					comboBoxUse.setSelectedItem(Criterion.CostEffectivenessType.Null.toString());
				}
				
				
				if(isTemporal){
					comboBoxDiscountUnits = new JComboBox<String>();
					
					for(TemporalUnit.Unit unit: TemporalUnit.Unit.values()){
						String newUnit = StringDatabase.getUniqueInstance().getString("NetworkAdvancedPanel.TemporalOptions.Unit." + unit.toString());
						comboBoxDiscountUnits.addItem(newUnit);
					}
					
					if(criterion.getDiscountUnit() == null){
						comboBoxDiscountUnits.setSelectedItem(StringDatabase.getUniqueInstance().getString("NetworkAdvancedPanel.TemporalOptions.Unit.YEAR"));
						criterion.setDiscountUnit(Unit.YEAR);
					}else{
						for(TemporalUnit.Unit unit: TemporalUnit.Unit.values()){

							if(criterion.getDiscountUnit().equals(unit)){
								String newUnit = StringDatabase.getUniqueInstance().getString("NetworkAdvancedPanel.TemporalOptions.Unit." + unit.toString());
								comboBoxDiscountUnits.setSelectedItem(newUnit);
							}
						}
					}
					model.addRow(new Object[] {
							criterion.getCriterionName(),
							comboBoxUse,
							criterion.getDiscount() + " %",
							comboBoxDiscountUnits
							});
				}else{
					model.addRow(new Object[] {
							criterion.getCriterionName(),
							comboBoxUse
							});
				}
			}
		} else {
			for (Criterion criterion : decisionCriteria) {
				String scale = String.valueOf(criterion.getScale());
				if(criterion.getCriterionUnit() != null && !criterion.getCriterionUnit().equals(multicriteriaOptions.getMainUnit())){
					scale += " " + multicriteriaOptions.getMainUnit() + "/" + criterion.getCriterionUnit();
				}
				
				if(isTemporal){
					comboBoxDiscountUnits = new JComboBox<String>();
					
					for(TemporalUnit.Unit unit: TemporalUnit.Unit.values()){
						String newUnit = StringDatabase.getUniqueInstance().getString("NetworkAdvancedPanel.TemporalOptions.Unit." + unit.toString());
						comboBoxDiscountUnits.addItem(newUnit);
					}
					
					if(criterion.getDiscountUnit() == null){
						comboBoxDiscountUnits.setSelectedItem(StringDatabase.getUniqueInstance().getString("NetworkAdvancedPanel.TemporalOptions.Unit.YEAR"));
						criterion.setDiscountUnit(Unit.YEAR);
					}else{
						for(TemporalUnit.Unit unit: TemporalUnit.Unit.values()){

							if(criterion.getDiscountUnit().equals(unit)){
								String newUnit = StringDatabase.getUniqueInstance().getString("NetworkAdvancedPanel.TemporalOptions.Unit." + unit.toString());
								comboBoxDiscountUnits.setSelectedItem(newUnit);
							}
						}
					}
					
					model.addRow(new Object[] {
							criterion.getCriterionName(),
							scale,
							criterion.getDiscount() + " %",
							comboBoxDiscountUnits
							});
				}else{
					model.addRow(new Object[] {
							criterion.getCriterionName(),
							scale
							});
				}
				
			}
		}

		table = new JTable(model) {
			/**
			 * Serial UID
			 */
			private static final long serialVersionUID = 1L;
			

			// Adjust the size of the table
			@Override
			public Component prepareRenderer(TableCellRenderer renderer,
					int row, int column) {
				Component component = super.prepareRenderer(renderer, row,
						column);
				int rendererWidth = component.getPreferredSize().width;
				TableColumn tableColumn = getColumnModel().getColumn(column);
				tableColumn.setPreferredWidth(Math.max(rendererWidth
						+ getIntercellSpacing().width + 20,
						tableColumn.getPreferredWidth()));
				return component;
			}
			
			// Gets only the numerical value and select all the text in editing mode			
			@Override
			public boolean editCellAt(int row, int column, EventObject e) {
		        boolean result = super.editCellAt(row, column, e);
		        final Component editor = getEditorComponent();
		        if (editor == null || !(editor instanceof JTextComponent) || 
		        		(isTemporal && costEffectiveness.isSelected() && column == USE_COLUMN) ||
		        		(isTemporal && column == DISCOUNT_UNIT__COLUMN)) {
		            return result;
		        }
		        
		        if (e instanceof MouseEvent) {
		            EventQueue.invokeLater(new Runnable() {
						
						@Override
						public void run() {
					        JTextComponent text = ((JTextComponent) editor);
					        if(text.getText().indexOf(" ") != -1){
					        	text.setText(text.getText().substring(0, 
					        			text.getText().indexOf(" ")));
					        }
					        text.selectAll();
						}
					});
		        	
		        } else {
			        JTextComponent text = ((JTextComponent) editor);
			        if(text.getText().indexOf(" ") != -1){
			        	text.setText(text.getText().substring(0, 
			        			text.getText().indexOf(" ")));
			        }
			        text.selectAll();
		        }
		        return result;
			}
			
			

			@Override
			public void setValueAt(Object aValue, int row, int column) {
				if((unicriterion.isSelected() && (column == SCALE_COLUMN || column == DISCOUNT_COLUMN))
						|| (costEffectiveness.isSelected() && column == DISCOUNT_COLUMN)){
					if (aValue instanceof String) {
						try{
							double value = Double.parseDouble((String) aValue);
							super.setValueAt(value, row, column);
						} catch(NumberFormatException e){
							// TODO - Extend JOptionPane and translate
							JOptionPane.showMessageDialog(
								this,
								stringDatabase.getString("NumberFormatException.Text.Label"),
								stringDatabase.getString("NumberFormatException.Title.Label"),
								JOptionPane.ERROR_MESSAGE
								);
							
						}
				    }
				} else {
			    	super.setValueAt(aValue, row, column);
			    }
			    
			}

			// If any cell is changed, save the new value in the temporal object
			@Override
			public void tableChanged(TableModelEvent e) {
				super.tableChanged(e);
				int row = e.getFirstRow();
				int column = e.getColumn();
				
				if(column == USE_COLUMN && costEffectiveness.isSelected()){
					String use = table.getValueAt(row, USE_COLUMN).toString();
					if(use.equals(Criterion.CostEffectivenessType.Null.toString())){
						decisionCriteria.get(row - 1).setCe_criterion(Criterion.CostEffectivenessType.Null);
					} else if(use.equals(Criterion.CostEffectivenessType.Cost.toString())){
						decisionCriteria.get(row - 1).setCe_criterion(Criterion.CostEffectivenessType.Cost);
					} else if(use.equals(Criterion.CostEffectivenessType.Effectiveness.toString())){
						decisionCriteria.get(row - 1).setCe_criterion(Criterion.CostEffectivenessType.Effectiveness);
					}
				} else if(column == SCALE_COLUMN && unicriterion.isSelected()){
					String scale = table.getValueAt(row, SCALE_COLUMN).toString();
					if(scale.indexOf(" ") != -1){
						scale = scale.substring(0, scale.indexOf(" "));
					}
					DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance(Locale.ENGLISH);
					format.applyLocalizedPattern("#.###");
					scale = format.format(Double.parseDouble(scale));
					decisionCriteria.get(row - 1).setScale(Double.parseDouble(scale));
				}
				
				if(isTemporal && column == DISCOUNT_COLUMN){
					String discount = table.getValueAt(row, DISCOUNT_COLUMN).toString();
					if(discount.indexOf(" ") != -1){
						discount = discount.substring(0, discount.indexOf(" "));
					}
					DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance(Locale.ENGLISH);
					format.applyLocalizedPattern("#.###");
					discount = format.format(Double.parseDouble(discount));
					decisionCriteria.get(row - 1).setDiscount(Double.parseDouble(discount));
				}
				
				if(isTemporal & column == DISCOUNT_UNIT__COLUMN){
					TemporalUnit.Unit unitSelected = TemporalUnit.Unit.YEAR;
					for(Unit unit: Unit.values()){
						if(StringDatabase.getUniqueInstance().getString("NetworkAdvancedPanel.TemporalOptions.Unit." + unit.toString())
								.equals(table.getValueAt(row, DISCOUNT_UNIT__COLUMN).toString())){
							unitSelected = unit;
							break;
						}
					}
					decisionCriteria.get(row - 1).setDiscountUnit(unitSelected);
				}
			}
			
			
			// If the editing mode is stopped, refresh the table to show the units again
			@Override
	        public void editingStopped(ChangeEvent e) {
	            super.editingStopped(e);
	            typeChanged();
	        }
		};
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		

		// Set the model and renderer of columns
		if (costEffectiveness.isSelected()) {
			table.getColumnModel().getColumn(USE_COLUMN)
					.setCellRenderer(new MultiCriteriaComboBoxRenderer(MultiCriteriaComboBoxRenderer.USE_RENDERER));
			table.getColumnModel()
					.getColumn(USE_COLUMN)
					.setCellEditor(
							new DefaultCellEditor(comboBoxUse));
		} else {
			table.getColumnModel().getColumn(SCALE_COLUMN).setCellRenderer(renderer);
		}

		table.getColumnModel().getColumn(CRITERION_COLUMN).setCellRenderer(renderer);
		
		if(isTemporal){
			table.getColumnModel().getColumn(DISCOUNT_COLUMN).setCellRenderer(renderer);		
			
			table.getColumnModel().getColumn(DISCOUNT_UNIT__COLUMN).setCellRenderer(new MultiCriteriaComboBoxRenderer(MultiCriteriaComboBoxRenderer.DISCOUNT_UNIT_RENDERER));
			table.getColumnModel().getColumn(DISCOUNT_UNIT__COLUMN).setCellEditor(new DefaultCellEditor(comboBoxDiscountUnits));
		}

		// Put the table in a scroll pane
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(table);
		tableScrollPane = new JScrollPane(panel);
		tableScrollPane.setPreferredSize(new Dimension(400, 100));
		tableScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));

		return tableScrollPane;
	}


	/**
	 * Gets the Panel with the units
	 * @return
	 */
	private JPanel getUnitsPanel() {
		JPanel unitsPanel = new JPanel();

		unitsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		unitsPanel.setBorder(new TitledBorder(stringDatabase.getString("MulticriteriaDialog.Unit.Select")));
		
		JLabel unitsLabel = new JLabel(stringDatabase.getString("MulticriteriaDialog.Unit.Title"));
		unitsPanel.add(unitsLabel, BorderLayout.LINE_START);
		unitsLabel.setSize(new Dimension(50, 50));


		existingUnits = new JComboBox<String>();
		
		HashMap<String, String> criteriaUnits = new HashMap<String, String>();
		for(Criterion criterion : decisionCriteria){
			criteriaUnits.put(criterion.getCriterionUnit() , criterion.getCriterionUnit());
		}
		
		for(String unitKey : criteriaUnits.keySet()){
			existingUnits.addItem(criteriaUnits.get(unitKey));
		}
		
		if(criteriaUnits.get(multicriteriaOptions.getMainUnit()) != null){
			existingUnits.setSelectedItem(multicriteriaOptions.getMainUnit());
		}else{
			existingUnits.setSelectedIndex(0);
			if(existingUnits.getSelectedItem() != null){
				multicriteriaOptions.setMainUnit(existingUnits.getSelectedItem().toString());
			}
		}
		
		existingUnits.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				changeMainUnit();
				
			}
		});
		unitsPanel.add(existingUnits);

		return unitsPanel;
	}
	
	/**
	 * Set a new main conversion unit and refresh the table 
	 */
	private void changeMainUnit(){
		if(existingUnits.getSelectedItem() != null){
			multicriteriaOptions.setMainUnit(existingUnits.getSelectedItem().toString());
		}
		typeChanged();
	}

	/**
	 * Gets the panel with the selection of the multicriteria type
	 * @return
	 */
	private JPanel getSelectTypePanel() {
		JPanel selectTypePanel = new JPanel();
		selectTypePanel.setBorder(new TitledBorder(stringDatabase.getString("MulticriteriaDialog.Type.Title")));
		selectTypePanel.setLayout(new GridLayout(0, 1));

		ButtonGroup group = new ButtonGroup();

		unicriterion = new JRadioButton();
		unicriterion.setText(stringDatabase.getString("MulticriteriaDialog.Type.Unicriterion"));
		unicriterion.setSelected(true);
		unicriterion.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				typeChanged();
			}
		});
		group.add(unicriterion);

		costEffectiveness = new JRadioButton();
		costEffectiveness.setText(stringDatabase.getString("MulticriteriaDialog.Type.CostEffectiveness"));
		group.add(costEffectiveness);
		costEffectiveness.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				typeChanged();
			}
		});

		selectTypePanel.add(unicriterion);
		selectTypePanel.add(costEffectiveness);

		return selectTypePanel;
	}

	/**
	 * If the type of multicriteria analysis is changed, save the new type and refresh the table
	 */
	protected void typeChanged() {
		if (costEffectiveness.isSelected()) {
			multicriteriaOptions.setMulticriteriaType(MulticriteriaOptions.Type.COST_EFFECTIVENESS);
			mainPanel.remove(tableScrollPane);
			tableScrollPane = getTablePanel();
			mainPanel.add(tableScrollPane);
			unitsPanel.setVisible(false);
			tableScrollPane.setVisible(true);
			mainPanel.setVisible(true);

		} else if (unicriterion.isSelected()) {
			multicriteriaOptions.setMulticriteriaType(MulticriteriaOptions.Type.UNICRITERION);
			mainPanel.remove(tableScrollPane);
			tableScrollPane = getTablePanel();
			mainPanel.add(tableScrollPane);
			mainPanel.setVisible(true);
			unitsPanel.setVisible(true);
			tableScrollPane.setVisible(true);
			mainPanel.repaint();
		}
		this.pack();
		this.repaint();
	}

	@Override
	protected boolean doOkClickBeforeHide() {
		// If the is user is editing a cell, stop the edition to save the data
		if(table.getCellEditor() != null){
			table.getCellEditor().stopCellEditing();
		}

		MulticriteriaEdit edit = new MulticriteriaEdit(probNet, decisionCriteria, multicriteriaOptions);
		try {
			probNet.getPNESupport().doEdit(edit);
		} catch (DoEditException | NonProjectablePotentialException
				| WrongCriterionException e) {
			e.printStackTrace();
		}
				
		return super.doOkClickBeforeHide();
	}

	
}
