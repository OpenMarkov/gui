/**
 * OpenMarkov - NodeDiscretizeValuesTablePanelListener.java
 */
package org.openmarkov.core.gui.dialog.node;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.RoundingMode;
import java.text.NumberFormat;

import org.openmarkov.core.action.PrecisionEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.WrongCriterionException;


/**
 * Auxiliary class to handle events for the primary class
 * NodeDiscretizeValuesTablePanel
 * 
 * @author jlgozalo
 * @version 1.0 5 Feb 2010
 */
public class NodeDiscretizeValuesTablePanelListener implements ActionListener,
				ItemListener, FocusListener, PropertyChangeListener {

	/**
	 * the panel to handle the events
	 */
	private NodeDomainValuesTablePanel panel;

	/**
	 * Constructor
	 * 
	 * @param panel -
	 *            the panel to handle the events
	 */
	public NodeDiscretizeValuesTablePanelListener(
								NodeDomainValuesTablePanel panel) {

		this.panel = panel;
	}

	/**
	 * @return the panel
	 */
	public NodeDomainValuesTablePanel getPanel() {

		return panel;
	}

	/**
	 * @param panel
	 *            the panel to set
	 */
	public void setPanel(NodeDomainValuesTablePanel panel) {

		this.panel = panel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event) {

		String actionCommand = event.getActionCommand();
        // do something
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	public void itemStateChanged(ItemEvent e) {
		if (e.getItem().equals( getPanel().getJRadioButtonMonotonyUp() )) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				// set table behaviour to be UP
				getPanel().getNodeDiscretizedStatesTablePanel().setUpMonotony( true );
			}
		}
		if (e.getItem().equals( getPanel().getJRadioButtonMonotonyDown() )) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				// set table behaviour to be DOWN
				getPanel().getNodeDiscretizedStatesTablePanel().setUpMonotony( false );
			}
		}
		subItemStateChanged( e );
	}

	/**
	 * Invoked when an item of the type of states of the node has been selected.
	 * 
	 * @param e
	 *            event information.
	 */
	protected void subItemStateChanged(ItemEvent e) {

		if (e.getItemSelectable()
			.equals( getPanel().getJComboBoxStatesValues() )) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				//getPanel().getNodeDiscretizedValuesTablePanel().setNewDataInTable(
					//getPanel().getJComboBoxStatesValues().getSelectedIndex() );
			}
		}
	}

	public void focusGained (FocusEvent e) {
		if (e.getSource().equals( getPanel().getJFormattedTextFieldPrecision())) {
			System.out.println( "precision focus gained");
			//getPanel().getJFormattedTextFieldPrecision().selectAll();
		}
	}
	
	public void focusLost(FocusEvent e) {

		if (e.getSource().equals( getPanel().getJFormattedTextFieldPrecision() )) {
			/*try {
				getPanel().getJFormattedTextFieldPrecision().commitEdit();
			} catch (ParseException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}*/
			PrecisionEdit precisionEdit = new PrecisionEdit (panel.getProbNode(), 
					((Double) getPanel().getJFormattedTextFieldPrecision().
					getValue()).doubleValue());
			
			try {
				panel.getProbNode().getProbNet().
					getPNESupport().announceEdit(precisionEdit);
				panel.getProbNode().getProbNet().
					getPNESupport().doEdit(precisionEdit);
			} catch (ConstraintViolationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (CanNotDoEditException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (DoEditException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (NotEnoughMemoryException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (NonProjectablePotentialException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (WrongCriterionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println( "precision set to "
				+ ((Double) getPanel().getJFormattedTextFieldPrecision()
					.getValue()).toString() );
		}
	}

	
	public void propertyChange(PropertyChangeEvent evt) {
		String us= evt.getPropertyName();
		//if evt.getPropertyName().contentEquals(cs)
		if (evt.getSource().equals( getPanel().getJFormattedTextFieldPrecision() )) {
			PrecisionEdit precisionEdit = new PrecisionEdit (panel.getProbNode(), 
					(Double) getPanel().getJFormattedTextFieldPrecision().
					getValue());
			try {
				panel.getProbNode().getProbNet().
					getPNESupport().announceEdit(precisionEdit);
				panel.getProbNode().getProbNet().
					getPNESupport().doEdit(precisionEdit);
			} catch (ConstraintViolationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (CanNotDoEditException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (DoEditException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
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
			
			NumberFormat nf = NumberFormat.getNumberInstance() ;
		      nf.setGroupingUsed(false) ;     // don't group by threes
		      nf.setMaximumFractionDigits(2) ;
		      nf.setMinimumFractionDigits(2) ;
		      nf.setRoundingMode(RoundingMode.FLOOR);
		      
			System.out.println( "precision set to "
				+ nf.format((Double) getPanel().getJFormattedTextFieldPrecision()
					.getValue()) ) ;
		}
	}
}
