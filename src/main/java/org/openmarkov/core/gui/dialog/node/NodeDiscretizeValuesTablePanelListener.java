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
import org.openmarkov.core.gui.action.PartitionedIntervalEdit;
import org.openmarkov.core.gui.component.DiscretizeTablePanel;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.gui.util.Utilities;
import org.openmarkov.core.model.network.PartitionedInterval;
import org.openmarkov.core.model.network.ProbNode;
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
	
	private int previousMonotony = -1;
	private static int DOWN = 0;
	private static int UP = 1;	
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
	
	

	
	//button initially selected down
	public void itemStateChanged(ItemEvent e) {
		
		getPanel().getNodeDiscretizedStatesTablePanel().getInfiniteNegativeDoubleButton().setVisible(false);
		getPanel().getNodeDiscretizedStatesTablePanel().getInfinitePositiveDoubleButton().setVisible(false);
		getPanel().getNodeDiscretizedStatesTablePanel().getInfiniteNegativeDoubleButton().setEnabled(false);
		getPanel().getNodeDiscretizedStatesTablePanel().getInfinitePositiveDoubleButton().setEnabled(false);
		
		if (e.getItem().equals( getPanel().getJRadioButtonMonotonyUp() )) {
			itemStateChangedUp(e);
			
		}
		if (e.getItem().equals( getPanel().getJRadioButtonMonotonyDown() )) {
			itemStateChangedDown(e);
		}
	}
	
	private void itemStateChangedUp(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.DESELECTED){
			previousMonotony = UP; //deselected up 
			((DiscretizeTablePanel)getPanel().getNodeStatesTablePanel()).setUpMonotony(false);
		}else if (e.getStateChange() == ItemEvent.SELECTED ){
			if ( previousMonotony == UP) { //UP --> UP
				//do nothing
				((DiscretizeTablePanel)getPanel().getNodeStatesTablePanel()).setUpMonotony(true);
			} else if (previousMonotony == DOWN) { // DOWN --> UP
					
				((DiscretizeTablePanel)getPanel().getNodeStatesTablePanel()).setUpMonotony(true);
				
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
				
			} 
		}
	}
	
	private void itemStateChangedDown(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.DESELECTED){
			previousMonotony = DOWN;//deselected down
			((DiscretizeTablePanel)getPanel().getNodeStatesTablePanel()).setUpMonotony(true);
		}else if (e.getStateChange() == ItemEvent.SELECTED ){
			if ( previousMonotony == UP) { // UP --> DOWN
				((DiscretizeTablePanel)getPanel().getNodeStatesTablePanel()).setUpMonotony(false);
				
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
			
			} else if (previousMonotony == DOWN) { // DOWN --> DOWN 
				//do nothing	
				((DiscretizeTablePanel)getPanel().getNodeStatesTablePanel()).setUpMonotony(false);
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
			getPanel().getJFormattedTextFieldPrecision().selectAll();
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
		getPanel().getJFormattedTextFieldPrecision().setValue( Double.valueOf( getPanel().getProbNode().
				getVariable().getPrecision() ) );
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
		      /*nf.setMaximumFractionDigits(2) ;
		      nf.setMinimumFractionDigits(2) ;
		      nf.setRoundingMode(RoundingMode.FLOOR);*/
		      
			System.out.println( "precision set to "
				+ nf.format((Double) getPanel().getJFormattedTextFieldPrecision()
					.getValue()) ) ;
			//after setting this new value to precision to variable, table values must be updated with 
			//the corresponding decimal numbers
			 DiscretizeTablePanel panel = (DiscretizeTablePanel) getPanel().getNodeStatesTablePanel();
			 
			 if (getPanel().getProbNode().getVariable().getVariableType() == VariableType.DISCRETIZED ||
					 getPanel().getProbNode().getVariable().getVariableType() == VariableType.NUMERIC ) {
			 
				double precision = (Double) getPanel().getJFormattedTextFieldPrecision().getValue();
				double [] limits = getPanel().getProbNode().getVariable().getPartitionedInterval().getLimits();
				boolean [] belongs =  getPanel().getProbNode().getVariable().getPartitionedInterval().getBelongsToLeftSide();
				
				for (int i = 0 ; i < limits.length; i++) {
					if (limits[i] != Double.POSITIVE_INFINITY && limits[i] != Double.NEGATIVE_INFINITY) {
						double newLimit = Utilities.roundWithPrecision(limits[i], Double.toString(precision));
						if (limits[i] != newLimit) {
							limits[i] = newLimit;
							int j = i;
							while (j+1 <= limits.length-1 && limits[j] >= limits[j+1]) {
								
								if (belongs[j] == false && belongs[j+1] == true) {
									limits[j+1] = limits[j];
								} else {
									if (j+1 == limits.length-1){
										limits[j+1] = Double.POSITIVE_INFINITY;
										break;
									} else 
										limits[j+1] = limits[j] + precision;
								}
									
									j++;
								}
						
							
						//previous limits
							int k = i;
							while (k-1 >=0 && limits[k] <= limits[k-1]) {
								if (belongs[k] == true && belongs[k-1] == false) {
									limits[k-1] = limits[k];
								}  else {
									if (k-1 == 0){
										limits[k-1] = Double.NEGATIVE_INFINITY;
										break;
									} else 
										limits[k-1] = limits[k] - precision;
								}
								k--;
							}
						} else {
							limits[i] = newLimit;
						}
					}
				}
				
				for (int m = 0 ; m < limits.length; m++) {
					if (limits[m] != Double.POSITIVE_INFINITY && limits[m] != Double.NEGATIVE_INFINITY) {
						limits[m] = Utilities.roundWithPrecision(limits[m], Double.toString(precision));
					}
				}
				PartitionedInterval newPartitionedInterval = new PartitionedInterval(limits, belongs);
				
				PartitionedIntervalEdit partitionedIntervalEdit = new PartitionedIntervalEdit(getPanel().getProbNode(), newPartitionedInterval);
				try {
					getPanel().getProbNode().getProbNet().getPNESupport().announceEdit(
							partitionedIntervalEdit);
				
					getPanel().getProbNode().getProbNet().getPNESupport().doEdit(
								partitionedIntervalEdit);
				} catch (DoEditException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				} catch (NotEnoughMemoryException e) {
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

				PartitionedInterval newPartitionInterval = getPanel().getProbNode().getVariable().
						getPartitionedInterval();
				((DiscretizeTablePanel)getPanel().getNodeStatesTablePanel()).setDataFromPartitionedInterval(newPartitionInterval);	
						
				/* Object [][] data =  panel.getData();
				 for (int i = 0; i < data.length; i++) {
					 for (int j = 3; j < data[0].length; j++) {
						 if (j==3 || j==5) {
						
							 double value = (Double) data [i][j];
							
						
						if (value != Double.NEGATIVE_INFINITY && value != Double.POSITIVE_INFINITY ) {
							
							String roundedValue = Utilities.roundedString(value,
								Double.toString((Double) getPanel().getJFormattedTextFieldPrecision().getValue()));
							
							panel.getValuesTable().setValueAt(roundedValue, i, j);
									
						 }
						 }
					 }
					 
				 }*/
				 
			 }
				/*getPanel().getJFormattedTextFieldPrecision().setValue( Double.valueOf( getPanel().getProbNode().
						getVariable().getPrecision() ) );*/
			
		}
	}
	}
