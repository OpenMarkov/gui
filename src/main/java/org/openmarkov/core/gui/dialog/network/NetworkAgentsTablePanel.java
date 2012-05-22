package org.openmarkov.core.gui.dialog.network;

import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.openmarkov.core.action.StateAction;
import org.openmarkov.core.gui.action.NetworkAgentEdit;
import org.openmarkov.core.gui.action.NodeStateEdit;
import org.openmarkov.core.gui.dialog.common.KeyTablePanel;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
/**
 * 
 * @author myebra
 *
 */
public class NetworkAgentsTablePanel extends KeyTablePanel implements TableModelListener{

	private StringResource messageStringResource;
	private String keyPrefix;
	private StringResource dialogStringResource;
	private ProbNet probNet;

	public NetworkAgentsTablePanel(String[] newColumns, ProbNet probNet){
		this(newColumns, new Object[0][0], "s");
		this.probNet = probNet;
	}
	
	public NetworkAgentsTablePanel(String[] newColumns, Object[][] noKeyData,
			String newKeyPrefix) {
		super(newColumns, new Object[0][0], true, true);
		messageStringResource =
				StringResourceLoader.getUniqueInstance().getBundleMessages();
			keyPrefix = newKeyPrefix;
			dialogStringResource = StringResourceLoader.getUniqueInstance()
							.getBundleDialogs();
			initialize();
			setData(noKeyData);
			//defineTableLookAndFeel();			// define specific listeners
			//defineTableSpecificListeners();
			//getTableModel().addTableModelListener(this);
	}
	/**
	 * Sets a new table model with new data.
	 * 
	 * @param newData
	 *            new data for the table without the key column.
	 */
	@Override
	public void setData(Object[][] newData) {

		if (newData != null) {
			data = fillDataKeys(newData);
			tableModel = new DefaultTableModel(data, columns);
			valuesTable.setModel(tableModel);
			valuesTable.getModel().addTableModelListener(this);
			
		}
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
		int i1 = 0; // aux int
		int i2 = 0; // aux int
		int l1 = 0; // num of rows
		int l2 = 0; // num of columns

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
	@Override
	public void tableChanged(TableModelEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void actionPerformedAddValue() {
		int rowCount = 0;
		rowCount = valuesTable.getRowCount();
		
		
		String option= JOptionPane.showInputDialog(this, 
				"Proporcione el nuevo agente", "Agregar agente", 
				JOptionPane.QUESTION_MESSAGE);
				
		if (option != null){
			int newIndex = 0;
			newIndex = valuesTable.getRowCount();

			NetworkAgentEdit networkAgentEdit = new NetworkAgentEdit(probNet, 
					StateAction.ADD, newIndex, option);
			//doEdit
			getTableModel().insertRow(newIndex, new Object[] {getKeyString(newIndex), option });
			valuesTable.getSelectionModel().setSelectionInterval(newIndex, newIndex);
		}
	}
	@Override
	protected void actionPerformedRemoveValue() {
		
	}
	@Override
	protected void actionPerformedUpValue() {
		
	}
	@Override
	protected void actionPerformedDownValue() {
		
	}
}
