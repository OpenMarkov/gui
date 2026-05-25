/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.dialog.node;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openmarkov.core.action.base.CloseEditStackOptions;
import org.openmarkov.core.action.base.PNEdit;
import org.openmarkov.core.action.core.SetPotentialEdit;
import org.openmarkov.core.action.core.SetPotentialVariablesEdit;
import org.openmarkov.core.developmentStaticAnalysis.ToCheck;
import org.openmarkov.core.exception.*;
import org.openmarkov.core.inference.InferenceOptions;
import org.openmarkov.core.localize.StringDatabase;
import org.openmarkov.core.model.network.*;
import org.openmarkov.core.model.network.modelUncertainty.ProbDensFunctionManager;
import org.openmarkov.core.model.network.potential.*;
import org.openmarkov.core.model.network.potential.operation.LinkRestrictionPotentialOperations;
import org.openmarkov.core.model.network.potential.plugin.PotentialUtils;
import org.openmarkov.gui.action.AugmentedPotentialValueEdit;
import org.openmarkov.gui.commonComponents.JComboBoxFunctionRender;
import org.openmarkov.gui.dialog.common.*;
import org.openmarkov.gui.exception.BinomialPotentialWrongValueException;
import org.openmarkov.java.classUtils.ClassUtils;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Dialog box to edit all type of potentials ( TablePotential and TreeADDs ). If
 * the potential is a utility role or uniform type, then no Values panel is
 * displayed. If potential is TreeADDpotential, then graphic edition panel is
 * showed.
 *
 * @author mpalacios
 * @author jmendoza
 * @author ibermejo
 * @version 1.3 cmyago 19/06/2016 - adapted the class to the new utility treatment; minor changes
 */
public class PotentialEditDialog extends OkCancelDialog {
    
    protected PotentialEditPanel potentialEditPanel;
    protected final Node node;
    protected final boolean readOnly;
    
    public PotentialEditDialog(Window owner, Node node, boolean readOnly) {
        super(owner);
        this.node = node;
        this.readOnly = readOnly;
        initialize();
        this.getComponentsPanel().setLayout(new BorderLayout());
        this.addComponentListener(new ComponentListener() {
            @Override public void componentResized(ComponentEvent e) {
                PotentialEditDialog.this.potentialEditPanel.setSize(new Dimension(
                        PotentialEditDialog.this.getSize().width - 18,
                        PotentialEditDialog.this.getSize().height - 100)
                );
            }
            
            @Override public void componentMoved(ComponentEvent e) {
            
            }
            
            @Override public void componentShown(ComponentEvent e) {
            
            }
            
            @Override public void componentHidden(ComponentEvent e) {
            
            }
        });
    }
    
    protected void initialize() {
        this.potentialEditPanel = this.generatePotentialEditPanel(node, readOnly);
        this.getComponentsPanel().setLayout(new BorderLayout());
        this.getComponentsPanel().add(this.potentialEditPanel, BorderLayout.CENTER);
        
        this.setResizable(true);
        // Set default title
        this.setTitle(getBaseTitle() +": "+this.node.getName());
        this.setMinimumSize(new Dimension(600, 100));
        this.setSize(1000, 400);
        this.setLocationRelativeTo(this.getOwner());
    }
    
    protected String getBaseTitle() {
        return StringDatabase.getUniqueInstance().getString("NodePropertiesDialog.EditPotentialTab.EditPotentialTitle");
    }
    
    public PotentialEditPanel getPotentialEditPanel() {
        return this.potentialEditPanel;
    }
    
    /**
     * @return An integer indicating the button clicked by the user when closing
     * this dialog
     */
    public ChosenOption requestValues() {
        // Shows the potentials' options table
        if (this.node.getNodeType() == NodeType.DECISION && this.node.getPolicyType() == PolicyType.OPTIMAL && this.readOnly) {
            this.potentialEditPanel.setEnabledDecisionOptions();
        }
        this.setVisible(true);
        return this.getSelectedOption();
    }
    
    PotentialEditPanel generatePotentialEditPanel(Node node, boolean readOnly) {
        return new PotentialEditPanel(node, readOnly, true);
    }
    
    
    @Override
    protected boolean doOkClickBeforeHide() throws BinomialPotentialWrongValueException.ThetaValueIsWrong, BinomialPotentialWrongValueException.NValuesIsWrong, DoEditException {
        this.potentialEditPanel.commitChanges();
        return true;
    }
    
    @Override protected void doCancelClickBeforeHide() {
        this.potentialEditPanel.uncommitChanges();
    }
    
}
