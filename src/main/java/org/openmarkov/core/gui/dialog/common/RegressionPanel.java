/*
 * Copyright 2013 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.core.gui.dialog.common;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


@SuppressWarnings("serial")
public class RegressionPanel extends KeyTablePanel {

    private List<ActionListener> listeners; 
    
    public RegressionPanel()
    {
        
        super(new String[]{"Covariate", "Coefficient"}, new Object[0][2], true, true, true);
        getValuesTable().setDefaultRenderer(String.class, new CoefficientTableCellRenderer());
        listeners = new ArrayList<>();
        initialize();
    }
    
    /**
     * Invoked when the button 'add' is pressed.
     */
    @Override
    protected void actionPerformedAddValue() {
        notifyActionListeners(new ActionEvent(this, 1, "Add"));
    };
    
    /**
     * Invoked when the button 'remove' is pressed.
     */
    protected void actionPerformedRemoveValue() {
        super.actionPerformedRemoveValue();
        notifyActionListeners(new ActionEvent(this, 2, "Remove"));
    };

    /**
     * Invoked when the button 'up' is pressed.
     */
    protected void actionPerformedUpValue() {
        super.actionPerformedUpValue();
        notifyActionListeners(new ActionEvent(this, 3, "Up"));
    };

    /**
     * Invoked when the button 'down' is pressed.
     */
    protected void actionPerformedDownValue() {
        super.actionPerformedDownValue();
        notifyActionListeners(new ActionEvent(this, 4, "Down"));
    };    

    private class CoefficientTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {
            Color backgroundColor = Color.WHITE;
            if (column == 0) {
                backgroundColor = new Color(207, 227, 253);
            }
            setBackground(backgroundColor);

            return super.getTableCellRendererComponent(table,
                    value,
                    isSelected,
                    hasFocus,
                    row,
                    column);
        }
    }
    
    public void addActionListener(ActionListener listener)
    {
        listeners.add(listener);
    }
    
    public boolean removeActionListener(ActionListener listener)
    {
        return listeners.remove(listener);
    }
    
    private void notifyActionListeners(ActionEvent event)
    {
        for(ActionListener listener : listeners)
        {
            listener.actionPerformed(event);
        }
    }
    
}
