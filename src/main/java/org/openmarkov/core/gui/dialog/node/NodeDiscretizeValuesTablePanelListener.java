/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

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

import javax.swing.JOptionPane;

import org.openmarkov.core.action.PrecisionEdit;
import org.openmarkov.core.action.StateAction;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.action.NodePartitionedIntervalEdit;
import org.openmarkov.core.gui.component.DiscretizeTablePanel;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.gui.util.Utilities;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.VariableType;


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
	
	private StringResource messageStringResource;

	/**
	 * Constructor
	 * 
	 * @param panel -
	 *            the panel to handle the events
	 */
	public NodeDiscretizeValuesTablePanelListener(
								NodeDomainValuesTablePanel panel) {

		this.panel = panel;
		

		messageStringResource =	
				StringResourceLoader.getUniqueInstance().getBundleMessages();
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
	
	
	
	private int previousMonotony = -1;
	private static int DOWN = 0;
	private static int UP = 1;	
	
	//button initially selected down
	public void itemStateChanged(ItemEvent e) {
		//to identify what is the panel container it could be CPTTablePanel or ICIPotentialsTablePanel
		
		if (e.getItem().equals( getPanel().getJRadioButtonMonotonyUp() )) {
			itemStateChangedUp(e);
		}
		if (e.getItem().equals( getPanel().getJRadioButtonMonotonyDown() )) {
			itemStateChangedDown(e);
		}
	}
	
	private void itemStateChangedUp(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.DESELECTED){
			//has been deselected canonical
			previousMonotony = UP;
		}else if (e.getStateChange() == ItemEvent.SELECTED ){
			if ( previousMonotony == UP) { //UP --> UP
				//do nothing
			} else if (previousMonotony == DOWN) { // DOWN --> UP
				
				//if (getPanel().getNodeStatesTablePanel() instanceof DiscretizeTablePanel){//Discretized
					DiscretizeTablePanel panel = (DiscretizeTablePanel) getPanel().getNodeStatesTablePanel();
					Object [][]data = panel.getData();
					
					Object [][]intermediateRows = new Object[data.length][data[0].length-2];
					for (int i= 0; i < data.length; i++) {//for each row
						for (int j = 2; j < data[0].length; j++) {
							intermediateRows[i] [j-2]  = data [i][j];
						}
						
					}
					for (int i=0; i < intermediateRows.length; i++) {
						for (int j = 0; j <intermediateRows[0].length ; j++) {
							data [i][j+2] =intermediateRows [intermediateRows.length-1-i][j];
						}
					}
					
					Object [][] newData = new Object[data.length][data[0].length-1];
					for (int i=0; i < data.length; i++) {
						for (int j = 1; j <data[0].length ; j++) {
							newData [i][j-1] =data [i][j];
						}
					}
					
					panel.setData(newData); //set data fill the first key column
					
				//}
			} 
		}
	}
	
	private void itemStateChangedDown(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.DESELECTED){
			//has been deselected canonical
			previousMonotony = DOWN;
		}else if (e.getStateChange() == ItemEvent.SELECTED ){
			if ( previousMonotony == UP) { // UP --> DOWN
				//if (getPanel().getNodeStatesTablePanel() instanceof DiscretizeTablePanel){//Discretized
					DiscretizeTablePanel panel = (DiscretizeTablePanel) getPanel().getNodeStatesTablePanel();
					Object [][]data = panel.getData();
					
					Object [][]intermediateRows = new Object[data.length][data[0].length-2];
					for (int i= 0; i < data.length; i++) {//for each row
						for (int j = 2; j < data[0].length; j++) {
							intermediateRows[i] [j-2]  = data [i][j];
						}
						
					}
					for (int i=0; i < intermediateRows.length; i++) {
						for (int j = 0; j <intermediateRows[0].length ; j++) {
							data [i][j+2] =intermediateRows [intermediateRows.length-1-i][j];
						}
					}
					
					Object [][] newData = new Object[data.length][data[0].length-1];
					for (int i=0; i < data.length; i++) {
						for (int j = 1; j <data[0].length ; j++) {
							newData [i][j-1] =data [i][j];
						}
					}
					
					panel.setData(newData); //set data fill the first key column
				//}
			} else if (previousMonotony == DOWN) { // DOWN --> DOWN 
				//do nothing	
			} 
		}
	}
		

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	/*public void itemStateChanged(ItemEvent e) {
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
	}*/

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
				JOptionPane.showMessageDialog(null, messageStringResource
						.getString( e1.getMessage() ),
					messageStringResource.getString( e1.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			} catch (CanNotDoEditException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, messageStringResource
						.getString( e1.getMessage() ),
					messageStringResource.getString( e1.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			} catch (DoEditException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				JOptionPane.showMessageDialog(null, messageStringResource
						.getString( e2.getMessage() ),
					messageStringResource.getString( e2.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			} catch (NotEnoughMemoryException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				JOptionPane.showMessageDialog(null, messageStringResource
						.getString( e2.getMessage() ),
					messageStringResource.getString( e2.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			} catch (NonProjectablePotentialException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, messageStringResource
						.getString( e1.getMessage() ),
					messageStringResource.getString( e1.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			} catch (WrongCriterionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, messageStringResource
						.getString( e1.getMessage() ),
					messageStringResource.getString( e1.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			}
			System.out.println( "precision set to "
				+ ((Double) getPanel().getJFormattedTextFieldPrecision()
					.getValue()).toString() );
		}
	}

	
	public void propertyChange(PropertyChangeEvent evt) {
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
				JOptionPane.showMessageDialog(null, messageStringResource
						.getString( e1.getMessage() ),
					messageStringResource.getString( e1.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			} catch (CanNotDoEditException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, messageStringResource
						.getString( e1.getMessage() ),
					messageStringResource.getString( e1.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			} catch (DoEditException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				JOptionPane.showMessageDialog(null, messageStringResource
						.getString( e2.getMessage() ),
					messageStringResource.getString( e2.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			} catch (NotEnoughMemoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, messageStringResource
						.getString( e.getMessage() ),
					messageStringResource.getString( e.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			} catch (NonProjectablePotentialException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, messageStringResource
						.getString( e.getMessage() ),
					messageStringResource.getString( e.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			} catch (WrongCriterionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, messageStringResource
						.getString( e.getMessage() ),
					messageStringResource.getString( e.getMessage() ),
					JOptionPane.ERROR_MESSAGE );
			}
			
			NumberFormat nf = NumberFormat.getNumberInstance() ;
		      nf.setGroupingUsed(false) ;     // don't group by threes
		      nf.setMaximumFractionDigits(2) ;
		      nf.setMinimumFractionDigits(2) ;
		      nf.setRoundingMode(RoundingMode.FLOOR);
		      
			System.out.println( "precision set to "
				+ nf.format((Double) getPanel().getJFormattedTextFieldPrecision()
					.getValue()) ) ;
			//after setting this new value to precision to variable, table values must be updated with 
			//the corresponding decimal numbers
			 DiscretizeTablePanel panel = (DiscretizeTablePanel) getPanel().getNodeStatesTablePanel();
			 
			 if (getPanel().getProbNode().getVariable().getVariableType() == VariableType.DISCRETIZED ||
					 getPanel().getProbNode().getVariable().getVariableType() == VariableType.NUMERIC ) {
				 
				// PartitionedInterval partitionedInterval = getPanel().getProbNode().getVariable().getPartitionedInterval();
				 
				 Object [][] data =  panel.getData();
				 for (int i = 0; i < data.length; i++) {
					 for (int j = 3; j < data[0].length; j++) {
						 if (j==3 || j==5) {
						//boolean lower = (j - 1 == panel.getLowerLimitSymbolColumnNum()? true: false);
						double value = (Double) data [i][j];
						//double value = Double.parseDouble(data [i][j]) ;
						if (value != Double.NEGATIVE_INFINITY && value != Double.POSITIVE_INFINITY ) {
							//double valor = (Double) getPanel().getJFormattedTextFieldPrecision().getValue();
							//String s = Double.toString(valor);
							String roundedValue = Utilities.roundedString(value,
									Double.toString((Double) getPanel().getJFormattedTextFieldPrecision().getValue()));
							panel.getValuesTable().setValueAt(roundedValue, i, j);
						//double roundedValue = round(value,(Double) getPanel().getJFormattedTextFieldPrecision().getValue());
						/*NodePartitionedIntervalEdit nodePartitionedIntervalEdit = 
								new NodePartitionedIntervalEdit(getPanel().getProbNode(), StateAction.
										MODIFYVALUEINTERVAL, i, Double.parseDouble(roundedValue), lower);
						
						try {
							getPanel().getProbNode().getProbNet().getPNESupport().announceEdit(
									nodePartitionedIntervalEdit);
							getPanel().getProbNode().getProbNet().getPNESupport().doEdit(
									nodePartitionedIntervalEdit);
						} catch (ConstraintViolationException e) {
							JOptionPane.showMessageDialog(null, messageStringResource
									.getString( e.getMessage() ),
								messageStringResource.getString( e.getMessage() ),
								JOptionPane.ERROR_MESSAGE );
						
							if (nodePartitionedIntervalEdit.getLower()){
								panel.getValuesTable().setValueAt(getPanel().getProbNode().getVariable().
										getPartitionedInterval().getLimit(i), i, 
										panel.getLowerLimitSymbolColumnNum());
							}else				
								panel.getValuesTable().setValueAt(getPanel().getProbNode().getVariable().
										getPartitionedInterval().getLimit(i + 1 ), i, 
										panel.getLowerLimitSymbolColumnNum());
						} catch (CanNotDoEditException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							JOptionPane.showMessageDialog(null, messageStringResource
									.getString( e.getMessage() ),
								messageStringResource.getString( e.getMessage() ),
								JOptionPane.ERROR_MESSAGE );
						} catch (DoEditException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							JOptionPane.showMessageDialog(null, messageStringResource
									.getString( e.getMessage() ),
								messageStringResource.getString( e.getMessage() ),
								JOptionPane.ERROR_MESSAGE );
						} catch (NotEnoughMemoryException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							JOptionPane.showMessageDialog(null, messageStringResource
									.getString( e.getMessage() ),
								messageStringResource.getString( e.getMessage() ),
								JOptionPane.ERROR_MESSAGE );
						} catch (NonProjectablePotentialException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							JOptionPane.showMessageDialog(null, messageStringResource
									.getString( e.getMessage() ),
								messageStringResource.getString( e.getMessage() ),
								JOptionPane.ERROR_MESSAGE );
						} catch (WrongCriterionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							JOptionPane.showMessageDialog(null, messageStringResource
									.getString( e.getMessage() ),
								messageStringResource.getString( e.getMessage() ),
								JOptionPane.ERROR_MESSAGE );
						}
						
						//set new rounded value in the table panel
						/*panel.getValuesTable().setValueAt(getPanel().getProbNode().getVariable().
							getPartitionedInterval().getLimit(i), i, 
								panel.getLowerLimitSymbolColumnNum());*/
						
						
						 }
						 }
					 }
					 
				 }
				 
			 }
			
			
		}
	}
	}
