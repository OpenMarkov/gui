/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/
package org.openmarkov.core.gui.dialog.treeadd;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JOptionPane;

import org.openmarkov.core.action.SetPotentialEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.dialog.common.PotentialPanel;
import org.openmarkov.core.gui.dialog.common.PotentialPanelPlugin;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDPotential;

@SuppressWarnings("serial")
@PotentialPanelPlugin(potentialType="TreeADD")
public class TreeADDPanel extends PotentialPanel
{
    
    /**
     * The builder object of Tree - ADDs
     */
    private TreeADDBuilder treeADDBuilder;
    
    /**
     * The node edited
     */
    private ProbNode probNode;
    
    /**
     * Message string resource for i18n
     */
    private StringResource messageStringResource;
    
    
    public TreeADDPanel(ProbNode probNode)
    {
        super();
        setLayout(new BorderLayout());
        treeADDBuilder = new TreeADDBuilder ( 
                (TreeADDPotential)probNode.getPotentials().get( 0 ));
        add( treeADDBuilder, BorderLayout.CENTER );
        setName("nodeTreeADDPotentialPanel");
        setBackground(Color.blue);
        //nodeADDPotentialPanel.setNewNode(newNode);
        //nodeADDPotentialPanel.setNodeProperties(probNode);
        messageStringResource =
                StringResourceLoader.getUniqueInstance().getBundleMessages();        
        
        this.probNode = probNode;
        
    }
    
    public void saveChanges() throws NotEnoughMemoryException
    {
        SetPotentialEdit setPotentialEdit = new SetPotentialEdit (
                                                                  probNode,
                                                                  treeADDBuilder.getTreePotential ());
        try
        {
            probNode.getProbNet ().getPNESupport ().announceEdit (setPotentialEdit);
            probNode.getProbNet ().getPNESupport ().doEdit (setPotentialEdit);
        }
        catch (ConstraintViolationException e1)
        {
            JOptionPane.showMessageDialog (this,
                                          e1.getMessage (),
                                           messageStringResource.getString ("ConstraintViolationException"),
                                           JOptionPane.ERROR_MESSAGE);
            // comboBox.setSelectedIndex(optionDeselected);
            // comboBox.requestFocus();
        }
        catch (CanNotDoEditException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace ();
        }
        catch (DoEditException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace ();
        }
        catch (NonProjectablePotentialException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace ();
        }
        catch (WrongCriterionException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace ();
        }
    }
}
