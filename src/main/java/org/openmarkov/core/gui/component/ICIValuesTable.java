package org.openmarkov.core.gui.component;



import java.util.ListIterator;

import javax.swing.event.UndoableEditEvent;


import org.openmarkov.core.action.PNUndoableEditEvent;
import org.openmarkov.core.action.PNUndoableEditListener;

import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.action.ICITablePotentialValueEdit;

import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.potential.canonical.ICIPotential;


@SuppressWarnings("serial")
public class ICIValuesTable extends ValuesTable implements PNUndoableEditListener{
	
	public ICIValuesTable (ProbNode probNode, ValuesTableModel tableModel,
			final boolean modifiable) { 
		super (probNode, tableModel, modifiable) ;

	}

	
	/**
	 * check the value to modify in the table and sets
	 */
	public void setValueAt(Object newValue, int row, int col) {
	 	Object oldValue = getValueAt( row, col );
	 	//TODO Verificar si la ubicación del siguiente código es 
		//adecuada
		if (!oldValue.equals( newValue )) {
			if (nodeType == NodeType.CHANCE || nodeType == NodeType.DECISION ) {
				
					ICITablePotentialValueEdit nodePotentialEdit = new ICITablePotentialValueEdit(
							probNode, (Double)newValue, row, col, priorityList);
					try {
						probNode.getProbNet().getPNESupport().announceEdit(
								nodePotentialEdit);
						probNode.getProbNet().getPNESupport().doEdit(
							nodePotentialEdit);
						
					} catch (ConstraintViolationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
		} // else it is not required to update values
	}
	
	/**
	 * set the number of columns in the table for canonical models adding one column per parent state
	 *  and adding one more for the id column (hidden)
	 * 
	 * @param parents -
	 *            parents of the variable
	 * @return the number of columns in the table
	 */
	public static int howManyCanonicalColumns(ProbNode properties) {

		int numColumns = 0;
		if (properties.getNode().getParents() != null) {
			int aux = 1;// first column for child states
			for (Node parent : properties.getNode().getParents()) {
				State[] parentStates = ((ProbNode)parent.getObject()).
					getVariable().getStates();
				aux += parentStates.length;
			}
			numColumns = aux + 1; //last column for the leak potential
		} else {
			numColumns = 1;
		}
		//numColumns = FIRST_EDITABLE_COLUMN + numColumns;

		return numColumns;
	}
	
	
	public static int toPositionOnJtable(int index, int col, int numOfStates, 
			int numOfParents){
		
		return numOfParents -1 + numOfStates + (numOfStates * ( col - 1 ) ) - 
			index;
		
	}

	public void undoableEditHappened(UndoableEditEvent arg0) {
		
		int priorityListPosition = 0;
		
		ICITablePotentialValueEdit edit =(ICITablePotentialValueEdit) arg0.getEdit();
		if (edit instanceof ICITablePotentialValueEdit){
			
			priorityList = edit.getPriorityList();
			double [] newNoisyPotential = edit.getNewNoisyValues();
			
			ListIterator<Integer> listIterator = priorityList.listIterator();
			while (listIterator.hasNext()== true){
				priorityListPosition = (Integer)listIterator.next();
				super.getModel().setValueAt( newNoisyPotential[priorityListPosition], 
						edit.getRowPosition(priorityListPosition), edit.getColumnPosition());

			}
			
			
		}
	}
	
	
	public void undoableEditWillHappen(PNUndoableEditEvent event)
			throws ConstraintViolationException, CanNotDoEditException {
		// TODO Auto-generated method stub
		
	}

	
	public void undoEditHappened(PNUndoableEditEvent event) {

		ICITablePotentialValueEdit edit =(ICITablePotentialValueEdit) event.getEdit();
		if (edit instanceof ICITablePotentialValueEdit){
			super.getModel().setValueAt( edit.getNewValue(), 
					edit.getRowPosition(), edit.getColumnPosition());
		}
		
	}
}
