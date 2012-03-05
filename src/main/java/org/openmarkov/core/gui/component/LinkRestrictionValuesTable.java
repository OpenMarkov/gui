package org.openmarkov.core.gui.component;

import java.awt.Color;

import javax.swing.JTable;
import javax.swing.ToolTipManager;

import org.openmarkov.core.action.PNUndoableEditListener;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.action.ICITablePotentialValueEdit;
import org.openmarkov.core.gui.action.LinkRestrictionPotentialValueEdit;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;

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

	public LinkRestrictionValuesTable(Link link, ValuesTableModel tableModel,
			final boolean modifiable) {
		super(tableModel, modifiable);
		this.link = link;
		ProbNode node = (ProbNode) link.getNode1().getObject();
		net = node.getProbNet();
		net.getPNESupport().addUndoableEditListener(this);
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
			}
			LinkRestrictionPotentialValueEdit linkPotentialEdit = new LinkRestrictionPotentialValueEdit(
					link, (Integer) newValue, row, col);

			try {
				net.getPNESupport().announceEdit(linkPotentialEdit);
				net.getPNESupport().doEdit(linkPotentialEdit);
				super.getModel().setValueAt(newValue, row, col);
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
	}
}
