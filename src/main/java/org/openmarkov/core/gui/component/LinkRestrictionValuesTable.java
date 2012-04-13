package org.openmarkov.core.gui.component;

import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoableEdit;

import org.openmarkov.core.action.PNUndoableEditListener;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.action.LinkRestrictionPotentialValueEdit;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialType;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.operation.LinkRestrictionPotentialOperations;

/**
 * This table implementation is responsible for the graphical and data model
 * manipulation of the Link restriction potential.
 * 
 * **/

@SuppressWarnings("serial")
public class LinkRestrictionValuesTable extends ValuesTable implements
		PNUndoableEditListener {
	/****
	 * The link with the link restriction.
	 * **/
	private Link link;

	/****
	 * The parent node of the link
	 * 
	 */
	private ProbNode node1;
	/****
	 * The child node of the link
	 */
	private ProbNode node2;
	/***
	 * The ProbNet containing the link.
	 */
	private ProbNet net;

	/***
	 * Constant value to describe compatibility of a position of the link
	 * restriction potential.
	 */
	private final String COMPATIBILITY_VALUE = "1";

	/***
	 * Constant value to describe incompatibility of a position of the link
	 * restriction potential.
	 */
	private final String INCOMPATIBILITY_VALUE = "0";

	private StringResource messageStringResource;

	public LinkRestrictionValuesTable(Link link, ValuesTableModel tableModel,
			final boolean modifiable) {
		super(tableModel, modifiable);
		this.link = link;
		node1 = (ProbNode) link.getNode1().getObject();
		node2 = (ProbNode) link.getNode2().getObject();
		net = node1.getProbNet();
		
		messageStringResource =	StringResourceLoader.getUniqueInstance().getBundleMessages();
	}

	/**
	 * This method checks the value to modify in the table and sets the new
	 * value.
	 ***/
	public void setValueAt(Object newValue, int row, int col) {
		if (newValue != null) {
			Integer newNumericValue = new Integer(INCOMPATIBILITY_VALUE);
			try {
				newNumericValue = (Integer) newValue;
				if (!newNumericValue.equals(Integer
						.valueOf(INCOMPATIBILITY_VALUE))
						&& !newNumericValue.equals(Integer
								.valueOf(COMPATIBILITY_VALUE))) {
					newValue = new Integer(INCOMPATIBILITY_VALUE);
				}
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, messageStringResource
						.getString( e.getMessage() ),
					messageStringResource.getString( e.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			}
			LinkRestrictionPotentialValueEdit linkPotentialEdit = new LinkRestrictionPotentialValueEdit(
					link, (Integer) newValue, row, col);

			try {
				net.getPNESupport().announceEdit(linkPotentialEdit);
				net.getPNESupport().doEdit(linkPotentialEdit);
				super.getModel().setValueAt(newValue, row, col);
				int variable1Index = col - 1;
				int variable2Index = node2.getVariable().getNumStates() - row;
				if ((Integer) newValue == 0) {
					if (!node2.getPotentials().isEmpty()
							&& node2.getPotentials().get(0).getPotentialType() == PotentialType.TABLE) {
						Potential potential = LinkRestrictionPotentialOperations
								.updatePotentialByAddLinkRestriction(node2,
										(TablePotential) link
												.getRestrictionsPotential(),
										variable1Index, variable2Index);
						ArrayList<Potential> potentials = new ArrayList<Potential>();
						potentials.add(potential);
						node2.setPotentials(potentials);
					}
				}
			} catch (ConstraintViolationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, messageStringResource
						.getString( e.getMessage() ),
					messageStringResource.getString( e.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			} catch (CanNotDoEditException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, messageStringResource
						.getString( e.getMessage() ),
					messageStringResource.getString( e.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			} catch (DoEditException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, messageStringResource
						.getString( e.getMessage() ),
					messageStringResource.getString( e.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			} catch (NotEnoughMemoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, messageStringResource
						.getString( e.getMessage() ),
					messageStringResource.getString( e.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			} catch (NonProjectablePotentialException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, messageStringResource
						.getString( e.getMessage() ),
					messageStringResource.getString( e.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			} catch (WrongCriterionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, messageStringResource
						.getString( e.getMessage() ),
					messageStringResource.getString( e.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			}

		}
	}

	public void undoableEditHappened(UndoableEditEvent event) {
		UndoableEdit unEdit = event.getEdit();
		if (unEdit instanceof LinkRestrictionPotentialValueEdit) {
			if (event.getEdit() instanceof LinkRestrictionPotentialValueEdit) {

				LinkRestrictionPotentialValueEdit edit = (LinkRestrictionPotentialValueEdit) event
						.getEdit();

				super.getModel().setValueAt(edit.getNewValue(),
						edit.getRowPosition(), edit.getColumnPosition());
			}
		}
	}

	public void undoEditHappened(UndoableEditEvent event) {
		if (event.getEdit() instanceof LinkRestrictionPotentialValueEdit) {

			LinkRestrictionPotentialValueEdit edit = (LinkRestrictionPotentialValueEdit) event
					.getEdit();

			super.getModel().setValueAt(edit.getNewValue(),
					edit.getRowPosition(), edit.getColumnPosition());
		}

	}

}
