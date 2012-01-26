/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/
package org.openmarkov.core.gui.dialog.common;

import javax.swing.JPanel;

import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.model.network.ProbNode;

@SuppressWarnings("serial")
public abstract class PotentialPanel extends JPanel
{

    /**
     * Fill the panel with the data from the node
     * @param probNode
     */
    public abstract void setData (ProbNode probNode);
    
    /**
     * Modify the node according to the changes entered by the user in the panel
     * @throws NotEnoughMemoryException
     */
    public abstract void saveChanges() throws NotEnoughMemoryException;
}
