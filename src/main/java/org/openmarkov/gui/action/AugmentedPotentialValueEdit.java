/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.action;

import org.openmarkov.core.action.core.PotentialChangeEdit;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.modelUncertainty.ProbDensFunction;
import org.openmarkov.core.model.network.modelUncertainty.ProbDensFunctionManager;
import org.openmarkov.core.model.network.potential.AugmentedTable;
import org.openmarkov.core.model.network.potential.AugmentedTablePotential;
import org.openmarkov.core.model.network.potential.UnivariateDistrPotential;
import org.openmarkov.gui.component.PotentialsTablePanelOperations;

import java.util.List;

/**
 * @author carmenyago
 * @version 1.1 22/05/2017 Changed into AugmentedPotentialValueEdit: now is the edit for all the AugmentedPotentials
 */
@SuppressWarnings("serial") public class AugmentedPotentialValueEdit extends PotentialChangeEdit {
    /**
     * The column of the table where is the potential
     */
    private int col;
    /**
     * The row of the table where is the potential
     */
    private int row;
    /**
     * A list that store the edition order
     */
    private List<Integer> priorityList;
    
    /**
     * Index of the value selected
     */
    private int indexSelected;

    
    /**
     * The AugmentedTable potential
     */
    private AugmentedTablePotential newAugmentedTablePotential;
    /**
     * Old table potential
     */
    private AugmentedTablePotential oldAugmentedTablePotential;
    
    /**
     * The UnivariateDistr Potential
     */
    private UnivariateDistrPotential newUnivariateDistrPotential;
    /**
     * Old table potential
     */
    private UnivariateDistrPotential oldUnivariateDistrPotential;
    
    /**
     * Pseudo-util class with common operations used  in potential tables
     */
    private PotentialsTablePanelOperations tablePotentialsPanelOperations;
    
    /**
     * the table potential
     */
    private AugmentedTable newAugmentedTable;
    private String[] newAugmentedValues;
    
    /**
     * Node
     */
    private Node node;

    // Constructor
    
    /**
     * Creates a new {@code NodePotentialEdit} specifying the node to be
     * edited, the new value of the potential, the row and column where is the
     * value to be modified and a priority list for potentials updating.
     *
     * @param node                 the node to be edited
     * @param newValue             the new value
     * @param col                  the column in the edited table
     * @param row                  the row in the edited table
     */
    public AugmentedPotentialValueEdit(Node node, String newValue, int row, int col) {
        super(node, null, null);
        boolean isAugmentedTablePotential = false;
        
        this.node = node;
        oldPotential = node.getPotentials().get(0);
        if (oldPotential instanceof AugmentedTablePotential oldAugmentedPotential) {
            oldAugmentedTablePotential = oldAugmentedPotential;
            newAugmentedTablePotential = new AugmentedTablePotential(oldAugmentedTablePotential);
            newPotential = newAugmentedTablePotential;
            newAugmentedTable = newAugmentedTablePotential.getAugmentedTable();
            newAugmentedValues = newAugmentedTable.getFunctionValues();
            isAugmentedTablePotential = true;
        } else {
            oldUnivariateDistrPotential = (UnivariateDistrPotential) oldPotential;
            newUnivariateDistrPotential = new UnivariateDistrPotential(oldUnivariateDistrPotential);
            newPotential = newUnivariateDistrPotential;
            newAugmentedTable = newUnivariateDistrPotential.getAugmentedTable();
            newAugmentedValues = newAugmentedTable.getFunctionValues();
        }
        this.row = row;
        this.col = col;
        this.tablePotentialsPanelOperations = new PotentialsTablePanelOperations();
        this.setIndexSelected(PotentialsTablePanelOperations.calculateLastEditableRow(newAugmentedTable) - row);
        //Set the entire column to "Complement"
        if (isAugmentedTablePotential) {
            int firstEditableRow = PotentialsTablePanelOperations.calculateFirstEditableRow(newAugmentedTable);
            int lastEditableRow = PotentialsTablePanelOperations.calculateLastEditableRow(newAugmentedTable);
            
            for (int i = firstEditableRow; i <= lastEditableRow; i++) {
                int index = PotentialsTablePanelOperations.getPotentialIndex(i, col, newAugmentedTable);
                newAugmentedValues[index] = "Complement";
            }
        }
        this.indexSelected = PotentialsTablePanelOperations.getPotentialIndex(row, col, newAugmentedTable);
        newAugmentedValues[indexSelected] = newValue;
    }
    
    /**
     * Creates a new {@code UnivariateDistrPotentialEdit} specifying the node to be and the new probability distribution.
     * This is used when the distribution of {@code UnivariateDistrPotential} is changed.
     *
     * @param node             - the node to be edited
     * @param distributionName - the name of the distribution to be created. Represents the attribute name in ProbDensFunctionType which represents the distribution class
     * @see org.openmarkov.core.model.network.potential.UnivariateDistrPotential
     */
    public AugmentedPotentialValueEdit(Node node, String distributionName) {
        super(node, null, null);
        
        this.node = node;
        //The old univariateDistrPotential
        oldPotential = node.getPotentials().get(0);
        oldUnivariateDistrPotential = (UnivariateDistrPotential) oldPotential;
        if (distributionName.equals(oldUnivariateDistrPotential.getProbDensFunctionName())) {
            newUnivariateDistrPotential = new UnivariateDistrPotential(oldUnivariateDistrPotential);
        } else {
            Class<? extends ProbDensFunction> newDistributionClass = ProbDensFunctionManager.getUniqueInstance()
                                                                                            .getProbDensFunctionClass(distributionName);
            newUnivariateDistrPotential = new UnivariateDistrPotential(oldPotential.getVariables(),
                                                                       newDistributionClass, oldPotential.getPotentialRole());
        }
        newPotential = newUnivariateDistrPotential;
        this.indexSelected = -1;
    }
    
    
    /**
     * Gets the row position associated to value edited if priorityList exists
     *
     * @param position position of the value in the array of values
     * @return the position in the table
     */
    public int getRowPosition(int position) {
        int lastRow = PotentialsTablePanelOperations.calculateLastEditableRow(newAugmentedTable);
        return lastRow - position % newAugmentedTable.getDimensions()[0];
    }
    
    /**
     * Gets the row position associated to value edited if priorityList no
     * exists
     *
     * @return the position in the table
     */
    public int getRowPosition() {
        return row;
    }
    
    /**
     * Gets the column where the value is edited
     *
     * @return the column edited
     */
    public int getColumnPosition() {
        return col;
    }
    
    /**
     * @return the indexSelected
     */
    public int getIndexSelected() {
        return indexSelected;
    }
    
    /**
     * @param indexSelected the indexSelected to set
     */
    public void setIndexSelected(int indexSelected) {
        this.indexSelected = indexSelected;
    }
    
}
