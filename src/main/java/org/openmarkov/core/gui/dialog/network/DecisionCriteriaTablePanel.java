package org.openmarkov.core.gui.dialog.network;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;

import org.openmarkov.core.action.DecisionCriteriaEdit;
import org.openmarkov.core.action.DecisionCriterionUnitEdit;
import org.openmarkov.core.action.StateAction;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.model.network.Criterion;
import org.openmarkov.core.model.network.ProbNet;

@SuppressWarnings("serial")
public class DecisionCriteriaTablePanel extends AdvancedPropertiesTablePanel {

	private ProbNet probNet;
	/**
	 * Each time an agent has been edited the corresponding edit would be stored
	 */
	//private List<PNEdit> edits = new ArrayList<PNEdit>();

	private StringDatabase stringDatabase = StringDatabase.getUniqueInstance();
	public DecisionCriteriaTablePanel(String[] newColumns, ProbNet probNet) {
		super(newColumns,
				new Object[0][0],
				StringDatabase.getUniqueInstance().
					getString("NetworkAdvancedPanel.DecisionCriteria.ValuesTable.Columns.Id.Prefix"));
		this.probNet = probNet;
	}

	@Override
	public void tableChanged(TableModelEvent tableEvent) {
		int column = tableEvent.getColumn();
		int row = tableEvent.getLastRow();
		if (tableEvent.getType() == TableModelEvent.UPDATE) {
			
			switch(column){
			case 1:
				String criterionName = (String) dataTable[row][column-1];
				String newName = (String) ((AdvancedPropertiesTableModel) tableEvent.getSource())
						.getValueAt(row, column);
				dataTable[row][column-1] = newName;
				if (criterionName != newName) {
					DecisionCriteriaEdit criteriaEdit = new DecisionCriteriaEdit(probNet,
							StateAction.RENAME, newName, criterionName, row);
					try {
						probNet.doEdit(criteriaEdit);
						//edits.add(criteriaEdit);
					}  catch (ConstraintViolationException
	                        | CanNotDoEditException
	                        | NonProjectablePotentialException
	                        | WrongCriterionException
	                        | DoEditException e1) {
	                    JOptionPane.showMessageDialog(this,
	                            stringDatabase.getString(e1.getMessage()),
	                            stringDatabase.getString("ConstraintViolationException"),
	                            JOptionPane.ERROR_MESSAGE);
	                    dataTable[row][column-1] = criterionName;
	                }
				}
				
				break;
			case 2:
				criterionName = (String) dataTable[row][column-2];
				String unitName = (String) dataTable[row][column-1];
				String newUnitName = (String) ((AdvancedPropertiesTableModel) tableEvent.getSource())
						.getValueAt(row, column);
				dataTable[row][column-1] = newUnitName;
				if (unitName != newUnitName) {
					DecisionCriterionUnitEdit criterionUnitEdit = 
							new DecisionCriterionUnitEdit(probNet,criterionName,newUnitName);
					try {
						probNet.doEdit(criterionUnitEdit);
						//edits.add(criterionUnitEdit);
					} catch (DoEditException | ConstraintViolationException | CanNotDoEditException
							| NonProjectablePotentialException | WrongCriterionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
				
			}
			
			setData(dataTable);
			valuesTable.getSelectionModel().setSelectionInterval(row, row);
		}
	}

	@Override
	protected void actionPerformedAddValue() {

		String option = JOptionPane.showInputDialog(this,
				stringDatabase.getString("AddCriterion.Text"),
				stringDatabase.getString("AddCriterion.Title"),
				JOptionPane.QUESTION_MESSAGE);

		if (option != null) {
			int newIndex = valuesTable.getRowCount();

			DecisionCriteriaEdit criteriaEdit = new DecisionCriteriaEdit(probNet, StateAction.ADD,
					option, option, 0);

			// doEdit
			try {
				probNet.doEdit(criteriaEdit);
				//edits.add(criteriaEdit);

			} catch (ConstraintViolationException
                    | CanNotDoEditException
                    | NonProjectablePotentialException
                    | WrongCriterionException
                    | DoEditException e1) {
                JOptionPane.showMessageDialog(this,
                        stringDatabase.getString(e1.getMessage()),
                        stringDatabase.getString("ConstraintViolationException"),
                        JOptionPane.ERROR_MESSAGE);
                
            }
			/*
			 * getTableModel().insertRow(newIndex, new Object[]
			 * {getKeyString(newIndex), option });
			 * valuesTable.getSelectionModel().setSelectionInterval(newIndex,
			 * newIndex);
			 */

			// StringsWithProperties agents = probNet.getAgents();
			// setDataFromNetworkAgents(agents);
			List<Criterion> criteria = probNet.getDecisionCriteria();
			setDataFromCriteria(criteria);
			// getTableModel().insertRow(newIndex, new Object[]
			// {getKeyString(newIndex), option });
			valuesTable.getSelectionModel().setSelectionInterval(newIndex, newIndex);

			dataTable = new Object[valuesTable.getRowCount()][2];
			for (int i = 0; i < valuesTable.getRowCount(); i++) {
				dataTable[i][0] = valuesTable.getValueAt(i, 1);
				dataTable[i][1] = valuesTable.getValueAt(i, 2);
			}
			/*
			 * getTableModel().insertRow(newIndex, new Object[]
			 * {getKeyString(newIndex), option });
			 * //valuesTable.getSelectionModel().setSelectionInterval(newIndex,
			 * newIndex); valuesTable.setValueAt(option, newIndex, 1);
			 */
		}
	}

	@Override
	protected void actionPerformedRemoveValue() {
		int selectedRow = valuesTable.getSelectedRow();
		String criteriaName = (String) valuesTable.getValueAt(selectedRow, 1);

		DecisionCriteriaEdit criteriaEdit = new DecisionCriteriaEdit(probNet, StateAction.REMOVE,
				"", criteriaName, selectedRow);

		try {
			probNet.doEdit(criteriaEdit);
			//edits.add(criteriaEdit);
		} catch (DoEditException | ConstraintViolationException | CanNotDoEditException
				| NonProjectablePotentialException | WrongCriterionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// StringsWithProperties agents = probNet.getAgents();
		List<Criterion> criterias = probNet.getDecisionCriteria();
		setDataFromCriteria(criterias);
		valuesTable.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
		// dataTable = new Object [agents.getNames().size()][1];
		if (criterias != null) {
			dataTable = new Object[criterias.size()][2];
			for (int i = 0; i < valuesTable.getRowCount(); i++) {
				dataTable[i][0] = valuesTable.getValueAt(i, 1);
				dataTable[i][1] = valuesTable.getValueAt(i, 2);
			}
		}
	}

	@Override
	protected void actionPerformedUpValue() {
		int selectedRow = valuesTable.getSelectedRow();
		Object swapName = null, swapUnit = null;
		swapName = dataTable[selectedRow][0];
		dataTable[selectedRow][0] = dataTable[selectedRow - 1][0];
		dataTable[selectedRow - 1][0] = swapName;

		swapUnit = dataTable[selectedRow][1];
		dataTable[selectedRow][1] = dataTable[selectedRow - 1][1];
		dataTable[selectedRow - 1][1] = swapUnit;

		
		DecisionCriteriaEdit criteriaEdit = new DecisionCriteriaEdit(probNet, StateAction.UP, "",
				"", selectedRow);

		try {
			probNet.doEdit(criteriaEdit);
			//edits.add(criteriaEdit);
			setData(dataTable);
			/*
			 * swap = valuesTable.getValueAt(selectedRow, 1);
			 * valuesTable.setValueAt( valuesTable.getValueAt(selectedRow - 1,
			 * 1), selectedRow, 1); valuesTable.setValueAt(swap, selectedRow -
			 * 1, 1);
			 */
			valuesTable.getSelectionModel().setSelectionInterval(selectedRow - 1, selectedRow - 1);
		} catch (DoEditException | ConstraintViolationException | CanNotDoEditException
				| NonProjectablePotentialException | WrongCriterionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < valuesTable.getRowCount(); i++) {
			dataTable[i][0] = valuesTable.getValueAt(i, 1);
			dataTable[i][1] = valuesTable.getValueAt(i, 2);
		}

	}

	@Override
	protected void actionPerformedDownValue() {
		int selectedRow = valuesTable.getSelectedRow();
		Object swapName = null, swapUnit = null;
		swapName = dataTable[selectedRow][0];
		dataTable[selectedRow][0] = dataTable[selectedRow + 1][0];
		dataTable[selectedRow + 1][0] = swapName;
		
		swapUnit = dataTable[selectedRow][1];
		dataTable[selectedRow][1] = dataTable[selectedRow + 1][1];
		dataTable[selectedRow + 1][1] = swapUnit;

		DecisionCriteriaEdit criteriaEdit = new DecisionCriteriaEdit(probNet, StateAction.DOWN, "",
				"", selectedRow);
		try {
			probNet.doEdit(criteriaEdit);
			//edits.add(criteriaEdit);
			setData(dataTable);
			/*
			 * swap = valuesTable.getValueAt(selectedRow, 1);
			 * valuesTable.setValueAt( valuesTable.getValueAt(selectedRow + 1,
			 * 1), selectedRow, 1); valuesTable.setValueAt(swap, selectedRow +
			 * 1, 1);
			 */
			valuesTable.getSelectionModel().setSelectionInterval(selectedRow + 1, selectedRow + 1);
		} catch (DoEditException | ConstraintViolationException | CanNotDoEditException
				| NonProjectablePotentialException | WrongCriterionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < valuesTable.getRowCount(); i++) {
			dataTable[i][0] = valuesTable.getValueAt(i, 1);
			dataTable[i][1] = valuesTable.getValueAt(i, 2);
		}
	}
	
	/*
    Fixing issue https://bitbucket.org/cisiad/org.openmarkov.issues/issue/221/button-delete-in-node-properties-parents
    The remove button was always set to disabled, unless more than two parents were present
    We need to override the method from KeyTablePanel
    as in it we are not able to determine in which panel we are located and thus
    if the button needs to be enabled or not.
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        super.valueChanged(e);

        // If there are two criteria, one can be deleted
        if (valuesTable.getRowCount() == 2) {
        	 removeValueButton.setEnabled(true);
        }
       
    }

}
