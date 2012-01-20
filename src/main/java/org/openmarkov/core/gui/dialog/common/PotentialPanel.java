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
import javax.swing.event.UndoableEditEvent;

import org.openmarkov.core.action.PNUndoableEditEvent;
import org.openmarkov.core.action.PNUndoableEditListener;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.component.ValuesTable;
import org.openmarkov.core.model.network.ProbNode;

@SuppressWarnings("serial")
public  class PotentialPanel extends JPanel implements PNUndoableEditListener 
{

    public ValuesTable getValuesTable ()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void setFieldsFromNode (ProbNode probNode)
    {
        // TODO Auto-generated method stub
        
    }
    
    public void saveChanges() throws NotEnoughMemoryException
    {
        
    }
    

    @Override
    public void undoableEditHappened (UndoableEditEvent e)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void undoableEditWillHappen (PNUndoableEditEvent event)
        throws ConstraintViolationException,
        CanNotDoEditException,
        NotEnoughMemoryException,
        NonProjectablePotentialException,
        WrongCriterionException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void undoEditHappened (PNUndoableEditEvent event)
    {
        // TODO Auto-generated method stub
        
    }
}
