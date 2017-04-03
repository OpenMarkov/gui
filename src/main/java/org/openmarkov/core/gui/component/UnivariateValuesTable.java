/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
/**
 * 
 */

package org.openmarkov.core.gui.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.UndoableEditEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoableEdit;

import org.openmarkov.core.action.PNUndoableEditListener;
import org.openmarkov.core.action.UncertainValuesEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.action.TablePotentialValueEdit;
import org.openmarkov.core.gui.action.UnivariateDistrPotentialValueEdit;
import org.openmarkov.core.gui.dialog.common.KeyTable;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.UnivariateDistrPotential;
import org.openmarkov.core.model.network.potential.ExactDistrPotential;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.TablePotential;

/**
 * This table implementation is responsible for the graphical and data model
 * manipulation of the Node Potentials (either in a general family or in a
 * canonical family potential). This table also shows the data in several ways,
 * depending upon the type of user selection:
 * <ul>
 * <Li>Probabilities or states values</li>
 * <li>Probabilistic or Deterministic values allowed</li>
 * <li>All parameters or Only independent parameters</li>
 * <li>TPC or canonical parameters(for the Canonical families)</li>
 * <li>Net or Compound values (for the Canonical families)</li>
 * @author jlgozalo
 * @author mpalacios
 * @version 1.0 7 Jul 2009
 * @version 1.1 15/Nov 2009 - sets the attributes for behaviour: deterministic
 *          (yes/no)
 * @version 2.0 20 Jan 2010 - sets all the behaviours and use of single data
 *          model
 * @version 3.0 May 2016 - eliminates the different treatment for the Utility nodes. 
 * 						 - eliminates the deterministic values
 * 						 	 
 * @author carmenyago        
 *         
 */
public class UnivariateValuesTable extends ValuesTable
    implements
        PNUndoableEditListener
{
    /**
     * default serial ID
     */
    private static final long                  serialVersionUID           = 1L;
 
    
    private boolean                            isSelectAllForMouseEvent   = true;
    private boolean                            isSelectAllForActionEvent  = false;
    private boolean                            isSelectAllForKeyEvent     = false;


    
    
/**
 * Default constructor
 * @param node
 *			- the node with the TablePotential or ExactDistrPotential
 * @param tableModel
 * 			- the model of the TablePotential or ExactDistrPotential
 * @param modifiable
 * 			- true if the table can be edited and modified
 * carmenyago added the initialisation of isExactDistrPotentialPanel
 * @author carmenyago
 */
public UnivariateValuesTable (Node node, ValuesTableModel tableModel, final boolean modifiable)
{
    super (node, tableModel, modifiable);
    if (potential instanceof UnivariateDistrPotential){
    	tablePotential=((UnivariateDistrPotential)(this.potential)).getDistributionTable();
    }
    
}
    
          
/**
 * Constructor for ValuesTable
 * revised-->not changed
 */
public UnivariateValuesTable (ValuesTableModel tableModel, final boolean modifiable)
{
    super (tableModel, modifiable);
 }


/**
 * 
 * check the value to modify in the table and sets
 * carmenyago removed the dependency with the utility type, the use of deterministic tables 
 * and checked if the new can be value converted to a double. She also deleted the use of checkUtilityVariable 
 * 
 * @author carmenyago
 */
@Override
public void setValueAt (Object newValue, int row, int col)
{
    	
	Object oldValue = getValueAt (row, col);
    // The new value has to be transformed to double
    if (!castValue(newValue)) newValue=oldValue;
   
	// Not clear if I have to use equals
    if (oldValue.equals (newValue)) return;

	int firstEditableRow=tableModel.firstEditableRow;
	double[] parameterValues= new double[lastEditableRow-firstEditableRow+1];
	for (int i=firstEditableRow; i<=lastEditableRow; i++){
		if (i==row){
			parameterValues[i-firstEditableRow] = ((Double) newValue).doubleValue();
		} else {
			double value =  ((Double) getValueAt(i,col)).doubleValue();
			parameterValues[i-firstEditableRow] = value;
		}	  		
	}
	try {
		((UnivariateDistrPotential)potential).checkDistributionValues(parameterValues);
	} catch(IllegalArgumentException e){    	
        newValue = oldValue;
        JOptionPane.showMessageDialog (this.getParent (), e.getMessage());    		
	}


    UnivariateDistrPotentialValueEdit nodePotentialEdit = new UnivariateDistrPotentialValueEdit (node,
                                                                                (Double) newValue,
                                                                                row,
                                                                                col,
                                                                                getTableModel().getNotEditablePositions());
    try{
    	probNet.doEdit (nodePotentialEdit);
    }catch (ConstraintViolationException | CanNotDoEditException
                        | NonProjectablePotentialException | WrongCriterionException
                        | DoEditException e)
    {
	  e.printStackTrace ();
      JOptionPane.showMessageDialog (this,
      stringDatabase.getString (e.getMessage ()),
      stringDatabase.getString (e.getMessage ()),
      JOptionPane.ERROR_MESSAGE);
    }
    
    
    //CMI
	//For Univariate
     tableModel.setValueAt(newValue, row, col);
	//CMF
}

    


/**
 * UNCLEAR--> Always called with true
 * 
 * Method to show/hide rows based on the showingAllParameters attribute
 * using a RowFilter mechanism.
 * <ul>
 * <li>If true, the table is shown completely with probabilities values
 * which means that there is no active row filter</li>
 * <li>If not, the row filter is set to show all rows except the one that
 * has the state name equals to the last state name.</li>
 * </ul>
 * @param showingAllParameters if true, show all; if false, show only
 *            independent parameters
 * UNCLEAR????                     
 */
@Override
public void setShowingAllParameters (boolean showingAllParameters)
{
    this.showingAllParameters = showingAllParameters;
    tableRowSorter = new TableRowSorter<ValuesTableModel> (((ValuesTableModel) getModel ()));
    if (showingAllParameters)
    {
            this.setRowSorter (null);
    } else
    {
    	int lastRow = getModel ().getRowCount () - 1 - 1;
        lastRow = (lastRow < 0 ? 0 : lastRow);
        LinkedList<RowFilter<Object, Object>> list = new LinkedList<RowFilter<Object, Object>> ();
        list.add (RowFilter.notFilter (RowFilter.regexFilter ((String) getModel ().getValueAt (lastRow,
                                                                                               0),
                                                              0)));
        list.add (RowFilter.notFilter (RowFilter.regexFilter (getVariable ().getName (), 0)));
        tableRowSorter.setRowFilter (RowFilter.andFilter (list));
        this.setRowSorter (tableRowSorter);
    }
 }

    
   
  


    
}