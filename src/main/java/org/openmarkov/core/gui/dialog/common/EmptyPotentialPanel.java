/*
 * Copyright 2011 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.core.gui.dialog.common;

import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.model.network.ProbNode;

@SuppressWarnings("serial")
@PotentialPanelPlugin(potentialType="Uniform")
public class EmptyPotentialPanel extends PotentialPanel 
{
    public EmptyPotentialPanel(ProbNode probNode)
    {
        
    }

    @Override
    public void setData (ProbNode probNode)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void saveChanges ()
        throws NotEnoughMemoryException
    {
        // TODO Auto-generated method stub
        
    }
}
