package org.openmarkov.core.gui.dialog.network;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.openmarkov.core.action.PNEdit;
import org.openmarkov.core.action.PNUndoableEditListener;
import org.openmarkov.core.action.StateAction;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.action.NetworkAgentEdit;
import org.openmarkov.core.gui.dialog.common.KeyTablePanel;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.StringsWithProperties;
/**
 * 
 * @author myebra
 *
 */
@SuppressWarnings("serial")
public class NetworkAgentsTablePanel extends KeyTablePanel implements TableModelListener,PNUndoableEditListener{

	private StringResource messageStringResource;
	private String keyPrefix;
	private StringResource dialogStringResource;
	private ProbNet probNet;
	private NetworkAgentTableModel netWorkAgentstableModel;
	private Object dataTable [][];
	/**
	 * Each time an agent has been edited the corresponding edit would be stored 
	 */
	private ArrayList<PNEdit> edits = new ArrayList<PNEdit>();

	public NetworkAgentsTablePanel(String[] newColumns, ProbNet probNet){
		this(newColumns, new Object[0][0], "a");
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
			defineTableLookAndFeel();			// define specific listeners
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
			//dataTable = newData;
			data = fillDataKeys(newData);
			//tableModel = new DefaultTableModel(data, columns);
			netWorkAgentstableModel = new NetworkAgentTableModel (data, columns); 
			//valuesTable.setModel(tableModel);
			valuesTable.setModel(netWorkAgentstableModel);
			valuesTable.getModel().addTableModelListener(this);
			this.defineTableLookAndFeel();
		}
	}
	/*private Object [][] getDataTable() {
		return dataTable;
	}*/
	public void setDataTable (Object [][] dataTable){
		this.dataTable = dataTable;
	}
	protected void defineTableLookAndFeel() {

		// center the data in all columns
		DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
		tcr.setHorizontalAlignment(SwingConstants.LEFT);
		
		DefaultTableCellRenderer statesRender = new DefaultTableCellRenderer();
		statesRender.setHorizontalAlignment(SwingConstants.LEFT);
		
		int maxColumn = valuesTable.getColumnModel().getColumnCount();
		
		for (int i = 1; i < maxColumn; i++) {
			TableColumn aColumn = valuesTable.getColumnModel().getColumn(i);
			aColumn.setCellRenderer(tcr);
			valuesTable.getTableHeader().getColumnModel().getColumn(i)
							.setCellRenderer(tcr);
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
	public void tableChanged(TableModelEvent tableEvent) {
		int column = tableEvent.getColumn();
		int row = tableEvent.getLastRow();
		if (tableEvent.getType()== TableModelEvent.UPDATE) {
			String agentName = (String) dataTable[row][0];
			String newName = (String) ((NetworkAgentTableModel)tableEvent.getSource()).
					getValueAt(row, column);
			 dataTable[row][0] = newName;
			if (agentName != newName) {
			NetworkAgentEdit networkAgentEdit = new NetworkAgentEdit(probNet, 
					StateAction.RENAME, newName, agentName, dataTable);
			try {
				probNet.getPNESupport().announceEdit(networkAgentEdit);
				probNet.getPNESupport().doEdit(networkAgentEdit);
				edits.add(networkAgentEdit);
			} catch (DoEditException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (NotEnoughMemoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ConstraintViolationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CanNotDoEditException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NonProjectablePotentialException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WrongCriterionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			 setData(dataTable);
			 valuesTable.getSelectionModel().setSelectionInterval(row, row);
		}
		}
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
					StateAction.ADD, "", option, null);
			//doEdit
			try {
				probNet.getPNESupport().announceEdit(networkAgentEdit);
				probNet.getPNESupport().doEdit(networkAgentEdit);
				edits.add(networkAgentEdit);
			} catch (DoEditException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (NotEnoughMemoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ConstraintViolationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CanNotDoEditException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NonProjectablePotentialException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WrongCriterionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*getTableModel().insertRow(newIndex, new Object[] {getKeyString(newIndex), option });
			valuesTable.getSelectionModel().setSelectionInterval(newIndex, newIndex);*/	
			
			 StringsWithProperties agents = probNet.getAgents();
			 setDataFromNetworkAgents(agents);
			 getTableModel().insertRow(newIndex, new Object[] {getKeyString(newIndex), option });
			 valuesTable.getSelectionModel().setSelectionInterval(newIndex, newIndex);
			 
			 dataTable = new Object [valuesTable.getRowCount()][1];
			 for (int i = 0; i < valuesTable.getRowCount(); i++) {
					dataTable[i][0] = valuesTable.getValueAt(i,	1); 
				}
			/*getTableModel().insertRow(newIndex, new Object[] {getKeyString(newIndex), option });
			//valuesTable.getSelectionModel().setSelectionInterval(newIndex, newIndex);
			valuesTable.setValueAt(option, newIndex, 1);*/
		}
	}
	
	
	private void setDataFromNetworkAgents(StringsWithProperties agents) {
		 Object [][] tableData =new Object [agents.getNames().size()][1];
		 if (agents != null) {
			 Set<String> agentsNames = agents.getNames();
			 Iterator<String> iterator = agentsNames.iterator();
		
			 int i = 0;
			 while (iterator.hasNext()) {
				 String name = (String) iterator.next();
				 if (name != null) {
					 tableData [i][0] = name;
					 i++;
				 }
			 }
			 
			setData(tableData);
		 }
	 } 
	
	/*private void setDataFromNetworkAgentsCorrectOrder (StringsWithProperties agents) {
		Object [][] tableData =new Object [agents.getNames().size()][1];
		 if (agents != null) {
			 Set<String> agentsNames = agents.getNames();
			 Iterator<String> iterator = agentsNames.iterator();
		
			 int i = 0;
			 while (iterator.hasNext()) {
				 String name = (String) iterator.next();
				 if (name != null) {
					 tableData [i][0] = name;
					 i++;
				 }
			 }
			 if (dataTable == null) {
				 dataTable= tableData;
				 setData(dataTable);
			 } else {
			  String newAgent = ""; 
			 for (int j = 0; j < tableData.length; j++) {
				 boolean exists = false;
				 for (int k = 0; k < dataTable.length; k++ ) {
					 if (tableData[j][0] == dataTable[k][0]) {
						 exists = true;
					 } 
				 }
				 if (!exists) {
					 newAgent = (String) tableData[j][0];
					 break;
				 }
				
			 }
			 Object [][] newData = new Object [agents.getNames().size()][1];
			 for (int m = 0 ; m < dataTable.length; m++) {
				 newData [m][0] = dataTable [m][0];
			 }
			 newData[agents.getNames().size()-1][0] = newAgent;
			 setData (newData);
			 }
		 }
	}*/
	
	@Override
	protected void actionPerformedRemoveValue() {
		int selectedRow = valuesTable.getSelectedRow();
		String agentName = (String) valuesTable.getValueAt(selectedRow, 1);
		NetworkAgentEdit networkAgentEdit = new NetworkAgentEdit(probNet, 
				StateAction.REMOVE, "", agentName, null);
		try {
			probNet.getPNESupport().announceEdit(networkAgentEdit);
			probNet.getPNESupport().doEdit(networkAgentEdit);
			edits.add(networkAgentEdit);
		} catch (DoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NotEnoughMemoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConstraintViolationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CanNotDoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NonProjectablePotentialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WrongCriterionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringsWithProperties agents = probNet.getAgents();
		setDataFromNetworkAgents(agents);
		valuesTable.getSelectionModel().setSelectionInterval(
				selectedRow, selectedRow);
		dataTable = new Object [agents.getNames().size()][1];
		for (int i = 0; i < valuesTable.getRowCount(); i++) {
			dataTable[i][0] = valuesTable.getValueAt(i,	1); 
		}
	}
	@Override
	protected void actionPerformedUpValue() {
		int selectedRow = valuesTable.getSelectedRow();
		Object swap = null;
		swap = dataTable[selectedRow][0];
		dataTable[selectedRow][0] = dataTable[selectedRow-1][0];
		dataTable[selectedRow-1][0] = swap; 
		
		NetworkAgentEdit networkAgentEdit = new NetworkAgentEdit(probNet, 
				StateAction.UP, "", "", dataTable);
		try {
			probNet.getPNESupport().announceEdit(networkAgentEdit);
			probNet.getPNESupport().doEdit(networkAgentEdit);
			edits.add(networkAgentEdit);
			setData(dataTable);
			/*swap = valuesTable.getValueAt(selectedRow, 1);
			valuesTable.setValueAt(
				valuesTable.getValueAt(selectedRow - 1, 1), selectedRow, 1);
			valuesTable.setValueAt(swap, selectedRow - 1, 1);*/
			valuesTable.getSelectionModel().setSelectionInterval(
				selectedRow - 1, selectedRow - 1);
		} catch (DoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NotEnoughMemoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConstraintViolationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CanNotDoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NonProjectablePotentialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WrongCriterionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (int i = 0; i < valuesTable.getRowCount(); i++) {
			dataTable[i][0] = valuesTable.getValueAt(i,	1); 
		}
		
	}
	@Override
	protected void actionPerformedDownValue() {
		int selectedRow = valuesTable.getSelectedRow();
		Object swap = null;
		swap = dataTable[selectedRow][0];
		dataTable[selectedRow][0] = dataTable[selectedRow+1][0]; 
		dataTable[selectedRow+1][0] = swap;
		
		
		NetworkAgentEdit networkAgentEdit = new NetworkAgentEdit(probNet, 
				StateAction.DOWN, "", "", dataTable);
		try {
			probNet.getPNESupport().announceEdit(networkAgentEdit);
			probNet.getPNESupport().doEdit(networkAgentEdit);
			edits.add(networkAgentEdit);
			setData(dataTable);
			/*swap = valuesTable.getValueAt(selectedRow, 1);
			valuesTable.setValueAt(
				valuesTable.getValueAt(selectedRow + 1, 1), selectedRow, 1);
			valuesTable.setValueAt(swap, selectedRow + 1, 1);*/
			valuesTable.getSelectionModel().setSelectionInterval(
				selectedRow + 1, selectedRow + 1);
		} catch (DoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NotEnoughMemoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConstraintViolationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CanNotDoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NonProjectablePotentialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WrongCriterionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (int i = 0; i < valuesTable.getRowCount(); i++) {
			dataTable[i][0] = valuesTable.getValueAt(i,	1); 
		}
	}

	@Override
	public void undoableEditHappened(UndoableEditEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void undoableEditWillHappen(UndoableEditEvent event)
			throws ConstraintViolationException, CanNotDoEditException,
			NotEnoughMemoryException, NonProjectablePotentialException,
			WrongCriterionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void undoEditHappened(UndoableEditEvent event) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 
	 * @return
	 */
	public ArrayList<PNEdit> getEdits () {
		return edits;
	}
	
}
