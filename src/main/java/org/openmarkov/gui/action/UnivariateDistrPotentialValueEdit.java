/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.action;

import org.openmarkov.core.action.core.PotentialChangeEdit;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.ThereIsNoPotentialsInNodeException;
import org.openmarkov.core.exception.UnreacheableException;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.modelUncertainty.ProbDensFunction;
import org.openmarkov.core.model.network.modelUncertainty.ProbDensFunctionManager;
import org.openmarkov.core.model.network.potential.AugmentedTable;
import org.openmarkov.core.model.network.potential.UnivariateDistrPotential;
import org.openmarkov.gui.component.PotentialsTablePanelOperations;

/**
 * {@code UnivariateDistrPotentialEdit} is a simple edit that allows to modify the
 * node's {@code Potential} values when its potential is an UnivariateDistrPotential.
 *
 * @author carmenyago
 * @version 1.0  03/04/2017
 */
@SuppressWarnings("serial") public class UnivariateDistrPotentialValueEdit extends PotentialChangeEdit {
    /**
     * The column of the table where is the potential
     */
    private int col;
    /**
     * The row of the table where is the potential
     */
    private int row;
    /**
     * Index of the value selected
     */
    private int potentialSelected;
    
    /**
     * The new class of the distribution
     */
    private Class<? extends ProbDensFunction> newDistributionClass;
    
    /**
     * The new distributionTable
     */
    private AugmentedTable newDistributionTable;
    
    /**
     * Pseudo-util class with common operations used  in potential tables
     */
    private PotentialsTablePanelOperations tablePotentialsPanelOperations;
    
    /**
     * The node which potential is changed
     */
    private Node node;
    
    // Constructor
    
    /**
     * Creates a new {@code UnivariateDistrPotentialEdit} specifying the node to be and the new probability distribution.
     * This is used when the distribution of {@code UnivariateDistrPotential} is changed.
     *
     * @param node             - the node to be edited
     * @param distributionName - the name of the distribution to be created. Represents the attribute name in ProbDensFunctionType which represents the distribution class
     *
     * @see org.openmarkov.core.model.network.potential.UnivariateDistrPotential
     */
    public UnivariateDistrPotentialValueEdit(Node node, String distributionName) {
        super(node, null, null);
        this.node = node;
        //The old univariateDistrPotential
        UnivariateDistrPotential potential = (UnivariateDistrPotential) node.getPotentials().get(0);
        this.oldPotential = potential;
        if (distributionName.equals(potential.getProbDensFunctionName())) {
            this.newPotential = new UnivariateDistrPotential(potential);
        } else {
            this.newDistributionClass = ProbDensFunctionManager.getUniqueInstance()
                                                               .getProbDensFunctionClass(distributionName);
            this.newPotential = new UnivariateDistrPotential(potential.getVariables(), newDistributionClass,
                                                             potential.getPotentialRole());
        }
    }
    
    /**
     * Gets the row position associated to value edited if priorityList exists
     *
     * @param position position of the value in the array of values
     *
     * @return the position in the table
     */
    public int getRowPosition(int position) {
        try {
            int lastRow = tablePotentialsPanelOperations.calculateLastEditableRow(node);
            return lastRow - position % newDistributionTable.getDimensions()[0];
        } catch (ThereIsNoPotentialsInNodeException e) {
            throw new UnreacheableException(e);
        }
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
    
    public UnivariateDistrPotential getNewUnivariateDistrPotential() {
        return (UnivariateDistrPotential) super.getNewPotential();
    }
    
    public UnivariateDistrPotential getOldUnivariateDistrPotential() {
        return (UnivariateDistrPotential) super.getNewPotential();
    }
}
