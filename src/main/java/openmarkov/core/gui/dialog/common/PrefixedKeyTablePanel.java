package openmarkov.core.gui.dialog.common;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.action.NodeStateEdit;
import org.openmarkov.core.action.StateAction;
import openmarkov.core.gui.localize.StringResource;
import openmarkov.core.gui.localize.StringResourceLoader;
import openmarkov.core.gui.network.GUIDefaultStates;
import openmarkov.core.gui.utils.Util;
import org.openmarkov.core.model.network.ProbNode;




/**
 * This class implements a key table with the following features:
 * <ul>
 * <li>Its elements, except the first column, are modifiable.</li>
 * <li>New elements can be added, creating a new key row with empty data.</li>
 * <li>The key data (first column) consist of a key string following of the
 * index of the row.</li>
 * <li>The information of a row (except the first column) can be taken up or
 * down.</li>
 * <li>The rows can be removed.</li>
 * </ul>
 * 
 * @author jmendoza
 * @version 1.0 jmendoza
 */
public class PrefixedKeyTablePanel extends KeyTablePanel implements 
	TableModelListener{

	/**
	 * Static field for serializable class.
	 */
	private static final long serialVersionUID = 8550762264755243008L;

	/**
	 * Key prefix.
	 */
	private String keyPrefix = null;
	
	private StringResource messageStringResource;

	private ProbNode probNode;
	
	private boolean renameAction = true;

	/*
	 * this a default constructor with no construction parameters
	 */
	public PrefixedKeyTablePanel() {
		messageStringResource =
			StringResourceLoader.getUniqueInstance().getBundleMessages();

		keyPrefix = "";
		initialize();
		getValuesTable().getModel().addTableModelListener(this);
	}

	/**
	 * This is the default constructor
	 * 
	 * @param newColumns
	 *            array of texts that appear in the header of the columns.
	 * @param noKeyData
	 *            content of the cells except the first column.
	 * @param newKeyPrefix
	 *            prefix of the keys of each row that appear in the first
	 *            column.
	 */
	public PrefixedKeyTablePanel(String[] newColumns, Object[][] noKeyData,
									String newKeyPrefix,
									boolean firstColumnHidden){//, ElementObservable notifier) {
		
		super(newColumns, new Object[0][0], true, true);//, notifier);
		
		messageStringResource =
			StringResourceLoader.getUniqueInstance().getBundleMessages();
		keyPrefix = newKeyPrefix;
		initialize();
		getValuesTable().getModel().addTableModelListener(this);
		getValuesTable().setFirstColumnHidden(firstColumnHidden);
		setData(noKeyData);
		


	}

	public PrefixedKeyTablePanel(String[] newColumns, Object[][] noKeyData,
			String newKeyPrefix, boolean firstColumnHidden,	ProbNode probNode) {
		
		super(newColumns, new Object[0][0], true, true);//, notifier);

		
		this.probNode = probNode;

		messageStringResource =
			StringResourceLoader.getUniqueInstance().getBundleMessages();
		keyPrefix = newKeyPrefix;
		initialize();
		getValuesTable().setFirstColumnHidden(firstColumnHidden);
		setData(noKeyData);



	}

	/**
	 * Sets a new table model with new data.
	 * 
	 * @param newData
	 *            new data for the table without the key column.
	 */
	@Override
	public void setData(Object[][] newData) {

		data = fillDataKeys(newData);
		tableModel = null;
		valuesTable.setModel(getTableModel());
		tableModel.addTableModelListener(this);
		
	}

	/**
	 * This method takes a data object and creates a new column that content a
	 * row key. This key begins with the key prefix following a number that
	 * starts at 0.
	 * 
	 * @param oldData
	 *            data to add a key column.
	 * @return a data object with one more column that contains the keys.
	 */
	private Object[][] fillDataKeys(Object[][] oldData) {

		Object[][] newData = null;
		int i1 = 0;
		int i2 = 0;
		int l1 = 0;
		int l2 = 0;

		l1 = oldData.length;
		if (l1 > 0) {
			l2 = oldData[0].length + 1;
			newData = new Object[l1][l2];
			for (i1 = 0; i1 < l1; i1++) {
				newData[i1][0] = getKeyString(i1);
				for (i2 = 1; i2 < l2; i2++) {
					newData[i1][i2] = oldData[i1][i2 - 1];
				}
			}

			return newData;
		}

		return new Object[0][0];

	}

	/**
	 * Returns a key represented by an index.
	 * 
	 * @param index
	 *            index of the key which will be returned
	 * @return the string that content the key.
	 */
	private String getKeyString(int index) {

		return keyPrefix + index;

	}

	/**
	 * Invoked when the button 'add' is pressed.
	 */
	@Override
	protected void actionPerformedAddValue() {
		//TODO warning esto afecta a la tabla de propiedades adicionales
		@SuppressWarnings("unused")
		String option= JOptionPane.showInputDialog(this, 
				"Proporcione el nuevo estado", "Agregar estado", 
				JOptionPane.QUESTION_MESSAGE);
				
		if (option != null){
			int newIndex = 0;
			
			newIndex = valuesTable.getRowCount();
			
			NodeStateEdit nodeStateEdit = new NodeStateEdit(probNode, 
					StateAction.ADD,newIndex, option);
			
			try {
				probNode.getProbNet().getPNESupport().announceEdit(nodeStateEdit);
				probNode.getProbNet().getPNESupport().doEdit(nodeStateEdit);
				renameAction = false;
				tableModel.insertRow(0, new Object[] { 
						getKeyString(newIndex),	option });
				valuesTable.getSelectionModel()
					.setSelectionInterval(0, 0);
				renameAction = false;
				
			} catch (ConstraintViolationException e) {
				JOptionPane
				.showMessageDialog(
					this, messageStringResource
						.getString( e.getMessage() ),
					messageStringResource
						.getString( e.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
				
				//jTextFieldNodeName.setText( this.nodeProperties.getName() );
				//jTextFieldNodeName.requestFocus();
				
			} catch (CanNotDoEditException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DoEditException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotEnoughMemoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NonProjectablePotentialException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WrongCriterionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}
	}

	/**
	 * Invoked when the button 'remove' is pressed.
	 */
	@Override
	protected void actionPerformedRemoveValue() {

		int selectedRow = valuesTable.getSelectedRow();
		int rowCount = 0;
		
		
		NodeStateEdit nodeStateEdit = new NodeStateEdit(probNode, 
				StateAction.REMOVE, selectedRow, "");
		
		try {
			probNode.getProbNet().getPNESupport().announceEdit(nodeStateEdit);
			probNode.getProbNet().getPNESupport().doEdit(nodeStateEdit);
			
			cancelCellEditing();
			renameAction = false;
			tableModel.removeRow(selectedRow);
			rowCount = valuesTable.getRowCount();
			if (rowCount > 0) {
				if (selectedRow < rowCount) {
					valuesTable.getSelectionModel().setSelectionInterval(
						selectedRow, selectedRow);
					while (selectedRow < rowCount) {
						renameAction = false;
						tableModel.setValueAt(
							getKeyString(selectedRow), selectedRow, 0);
						selectedRow++;
					}
				} else {
					valuesTable.getSelectionModel().setSelectionInterval(
						selectedRow - 1, selectedRow - 1);
				}
			}
			
			renameAction = false;
		} catch (ConstraintViolationException e) {
			JOptionPane
			.showMessageDialog(
				this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource
					.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
			
			//jTextFieldNodeName.setText( this.nodeProperties.getName() );
			//jTextFieldNodeName.requestFocus();
			//e.printStackTrace();
		} catch (CanNotDoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotEnoughMemoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NonProjectablePotentialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WrongCriterionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		
		

		

	}

	/**
	 * Invoked when the button 'up' is pressed.
	 */
	@Override
	protected void actionPerformedUpValue() {

		int selectedRow = valuesTable.getSelectedRow();
		Object swap = null;
		
		NodeStateEdit nodeStateEdit = new NodeStateEdit(probNode, 
				StateAction.UP, selectedRow, "");
		
		try {
			probNode.getProbNet().getPNESupport().announceEdit(nodeStateEdit);
			probNode.getProbNet().getPNESupport().doEdit(nodeStateEdit);
			
			stopCellEditing();
			swap = valuesTable.getValueAt(selectedRow, 1);
			renameAction = false;
			valuesTable.setValueAt(
				valuesTable.getValueAt(selectedRow - 1, 1), selectedRow, 1);
			renameAction = false;
			valuesTable.setValueAt(swap, selectedRow - 1, 1);
			valuesTable.getSelectionModel().setSelectionInterval(
				selectedRow - 1, selectedRow - 1);
			renameAction = false;
			
		} catch (ConstraintViolationException e) {
			JOptionPane
			.showMessageDialog(
				this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource
					.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
			
		} catch (CanNotDoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotEnoughMemoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NonProjectablePotentialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WrongCriterionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

		

	}

	/**
	 * Invoked when the button 'down' is pressed.
	 */
	@Override
	protected void actionPerformedDownValue() {

		int selectedRow = valuesTable.getSelectedRow();
		Object swap = null;
		
		NodeStateEdit nodeStateEdit = new NodeStateEdit(probNode, 
				StateAction.DOWN, selectedRow, "");
		
		try {
			probNode.getProbNet().getPNESupport().announceEdit(nodeStateEdit);
			probNode.getProbNet().getPNESupport().doEdit(nodeStateEdit);
			
			stopCellEditing();
			swap = valuesTable.getValueAt(selectedRow, 1);
			renameAction = false;
			valuesTable.setValueAt(
				valuesTable.getValueAt(selectedRow + 1, 1), selectedRow, 1);
			renameAction = false;
			valuesTable.setValueAt(swap, selectedRow + 1, 1);
			valuesTable.getSelectionModel().setSelectionInterval(
				selectedRow + 1, selectedRow + 1);
			renameAction = false;
								
		} catch (ConstraintViolationException e) {
			JOptionPane
			.showMessageDialog(
				this, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource
					.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
			
		} catch (CanNotDoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotEnoughMemoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NonProjectablePotentialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WrongCriterionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

		

	}

	/**
	 * Returns the content of the table except the first column. This column
	 * contains the keys generated automatically by this class and is only used
	 * to display it not to manage it.
	 * 
	 * @return the content of the table except the first column.
	 */
	@Override
	public Object[][] getData() {

		Object[][] content = super.getData();
		Object[][] result = null;
		int rowCount = content.length;
		int columnCount = 0;
		int i = 0;
		int j = 0;

		if (rowCount > 0) {
			columnCount = content[0].length;
			result = new Object[rowCount][columnCount - 1];
			for (i = 0; i < rowCount; i++) {
				for (j = 1; j < columnCount; j++) {
					result[i][j - 1] = content[i][j];
				}
			}
		} else {
			result = new Object[0][0];
		}

		return result;

	}

	@Override
	public void tableChanged(TableModelEvent e) {
		int row = e.getLastRow();
		
		
		if ( e.getType()!= TableModelEvent.DELETE  && e.getType()!= 
			TableModelEvent.INSERT && renameAction  ){
			
			String newName = (String)((DefaultTableModel)e.getSource()).
			getValueAt(row, e.getColumn());	
			
			NodeStateEdit nodeStateEdit = new NodeStateEdit (probNode, 
				StateAction.RENAME, row, newName );
			try {
				probNode.getProbNet().getPNESupport().announceEdit(nodeStateEdit);
				probNode.getProbNet().getPNESupport().doEdit(nodeStateEdit);
			} catch (ConstraintViolationException e1) {
				JOptionPane
				.showMessageDialog(
					this, messageStringResource
						.getString( e1.getMessage() ),
					messageStringResource
						.getString( e1.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
				//valuesTable.getSelectionModel().setSelectionInterval(row, 
					//	e.getColumn());
				int i = Util.toPositionOnPotentialReordered(row,e.getColumn(), 
						probNode.getVariable().getNumStates(), probNode.
						getNode().getNumParents()) ;
				valuesTable.setValueAt(probNode.getVariable().getStates()[i].
						getName(), row, e.getColumn());
				
				
			} catch (CanNotDoEditException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (DoEditException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NotEnoughMemoryException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NonProjectablePotentialException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (WrongCriterionException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			
		}
		renameAction = true;
	}

	

	

		
		
		
	
}
