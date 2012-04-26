package org.openmarkov.core.gui.component;

import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;

import org.openmarkov.core.action.StateAction;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.action.RevelationConditionEdit;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNode;

@SuppressWarnings("serial")
public class RevelationArcDiscretizeTablePanel extends DiscretizeTablePanel {
	/****
	 * Link for which the revelation conditions are stores.
	 */
	private Link link;

	/**
	 * default constructor
	 * 
	 * @wbp.parser.constructor
	 */
	public RevelationArcDiscretizeTablePanel(String[] newColumns, Link link) {
		this(newColumns, new Object[0][0], "s", (ProbNode) link.getNode1()
				.getObject());
		this.link = link;

	}

	public RevelationArcDiscretizeTablePanel(String[] newColumns,
			Object[][] noKeyData, String newKeyPrefix, ProbNode probNode) {
		super(newColumns, noKeyData, newKeyPrefix, probNode);
		super.getDownValueButton().setVisible(false);
		super.getUpValueButton().setVisible(false);
		super.getInfiniteNegativeDoubleButton().setVisible(false);
		super.getInfinitePositiveDoubleButton().setVisible(false);
	}

	public void setPartitionedInterval() {

		int subIntervals = 0;
		for (PartitionedInterval partitionInterval : link
				.getRevealingIntervals()) {
			subIntervals += partitionInterval.getNumSubintervals();
		}

		Object[][] allIntervalTable = new Object[subIntervals][6];
		int accumulatedIndex = 0;
		for (PartitionedInterval partitionInterval : link
				.getRevealingIntervals()) {
			Object[][] intervalTable = partitionInterval.convertToTableFormat();
			for (int i = 0; i < intervalTable.length; i++) {
				System.arraycopy(intervalTable[i], 0, allIntervalTable[i
						+ accumulatedIndex], 0, intervalTable[0].length);
			}
			accumulatedIndex = intervalTable.length;
		}
		setData(allIntervalTable);
	}

	/**
	 * Invoked when the button 'add' is pressed.
	 */
	@Override
	protected void actionPerformedAddValue() {

		int rowCount = 0;
		rowCount = valuesTable.getRowCount();
		int newIndex = 0;
		newIndex = valuesTable.getRowCount();
		RevelationConditionEdit revelationArcStateEdit = new RevelationConditionEdit(
				link, StateAction.ADD, newIndex, 0, false);
		try {
			probNode.getProbNet().getPNESupport()
					.announceEdit(revelationArcStateEdit);
			probNode.getProbNet().getPNESupport()
					.doEdit(revelationArcStateEdit);

			setPartitionedInterval();
		} catch (ConstraintViolationException e) {
			JOptionPane.showMessageDialog(this,
					messageStringResource.getString(e.getMessage()),
					messageStringResource.getString(e.getMessage()),
					JOptionPane.ERROR_MESSAGE);

		} catch (CanNotDoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,
					messageStringResource.getString(e.getMessage()),
					messageStringResource.getString(e.getMessage()),
					JOptionPane.ERROR_MESSAGE);
		} catch (DoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,
					messageStringResource.getString(e.getMessage()),
					messageStringResource.getString(e.getMessage()),
					JOptionPane.ERROR_MESSAGE);
		} catch (NotEnoughMemoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,
					messageStringResource.getString(e.getMessage()),
					messageStringResource.getString(e.getMessage()),
					JOptionPane.ERROR_MESSAGE);
		} catch (NonProjectablePotentialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,
					messageStringResource.getString(e.getMessage()),
					messageStringResource.getString(e.getMessage()),
					JOptionPane.ERROR_MESSAGE);
		} catch (WrongCriterionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,
					messageStringResource.getString(e.getMessage()),
					messageStringResource.getString(e.getMessage()),
					JOptionPane.ERROR_MESSAGE);
		}

		valuesTable.getSelectionModel()
				.setSelectionInterval(rowCount, rowCount);

	}

	/**
	 * Invoked when the button 'remove' is pressed.
	 */
	@Override
	protected void actionPerformedRemoveValue() {
		int selectedRow = valuesTable.getSelectedRow();

		RevelationConditionEdit revelationArcStateEdit = new RevelationConditionEdit(
				link, StateAction.REMOVE, selectedRow, 0, false);
		try {
			probNode.getProbNet().getPNESupport()
					.announceEdit(revelationArcStateEdit);
			probNode.getProbNet().getPNESupport()
					.doEdit(revelationArcStateEdit);
			cancelCellEditing();
			setPartitionedInterval();
		} catch (ConstraintViolationException e) {
			JOptionPane.showMessageDialog(this,
					messageStringResource.getString(e.getMessage()),
					messageStringResource.getString(e.getMessage()),
					JOptionPane.ERROR_MESSAGE);

		
		} catch (CanNotDoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,
					messageStringResource.getString(e.getMessage()),
					messageStringResource.getString(e.getMessage()),
					JOptionPane.ERROR_MESSAGE);
		} catch (DoEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,
					messageStringResource.getString(e.getMessage()),
					messageStringResource.getString(e.getMessage()),
					JOptionPane.ERROR_MESSAGE);
		} catch (NotEnoughMemoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,
					messageStringResource.getString(e.getMessage()),
					messageStringResource.getString(e.getMessage()),
					JOptionPane.ERROR_MESSAGE);
		} catch (NonProjectablePotentialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,
					messageStringResource.getString(e.getMessage()),
					messageStringResource.getString(e.getMessage()),
					JOptionPane.ERROR_MESSAGE);
		} catch (WrongCriterionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,
					messageStringResource.getString(e.getMessage()),
					messageStringResource.getString(e.getMessage()),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void tableChanged(TableModelEvent arg0) {
		int column = arg0.getColumn();
		int row = arg0.getLastRow();

		boolean lower = (column - 1 == lowerLimitSymbolColumnNum ? true : false);
		if (arg0.getType() == TableModelEvent.UPDATE
				&& ((DiscretizeTableModel) arg0.getSource()).getValueAt(row,
						column) instanceof Double) {
			double newValue = (Double) ((DiscretizeTableModel) arg0.getSource())
					.getValueAt(row, column);
			RevelationConditionEdit nodePartitionedIntervalEdit = new RevelationConditionEdit(
					link, StateAction.MODIFYVALUEINTERVAL, row, newValue, lower);
			try {
				probNode.getProbNet().getPNESupport()
						.announceEdit(nodePartitionedIntervalEdit);
				probNode.getProbNet().getPNESupport()
						.doEdit(nodePartitionedIntervalEdit);


			} catch (ConstraintViolationException e) {
				JOptionPane.showMessageDialog(this,
						messageStringResource.getString(e.getMessage()),
						messageStringResource.getString(e.getMessage()),
						JOptionPane.ERROR_MESSAGE);

			} catch (CanNotDoEditException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(this,
						messageStringResource.getString(e.getMessage()),
						messageStringResource.getString(e.getMessage()),
						JOptionPane.ERROR_MESSAGE);
			} catch (DoEditException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(this,
						messageStringResource.getString(e.getMessage()),
						messageStringResource.getString(e.getMessage()),
						JOptionPane.ERROR_MESSAGE);
			} catch (NotEnoughMemoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(this,
						messageStringResource.getString(e.getMessage()),
						messageStringResource.getString(e.getMessage()),
						JOptionPane.ERROR_MESSAGE);
			} catch (NonProjectablePotentialException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(this,
						messageStringResource.getString(e.getMessage()),
						messageStringResource.getString(e.getMessage()),
						JOptionPane.ERROR_MESSAGE);
			} catch (WrongCriterionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(this,
						messageStringResource.getString(e.getMessage()),
						messageStringResource.getString(e.getMessage()),
						JOptionPane.ERROR_MESSAGE);
			}
		}

	}
	
	private void changeIntervalDiscretize(int fila, int columna) {
	
	}

}
