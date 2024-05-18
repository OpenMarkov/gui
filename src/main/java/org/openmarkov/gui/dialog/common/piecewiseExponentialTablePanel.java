package org.openmarkov.gui.dialog.common;


import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;

public class piecewiseExponentialTablePanel extends KeyTablePanel {


    public piecewiseExponentialTablePanel(Object[][] data) {
        //KeyTablePanel always consider that the first column of Key Table is hidden.
        //The method can be overridden but KeyTable always considers the first column as not editable
        super(new String[] {"Hidden","Time","Probability"}, data, true, true,true);
        Object[][] newData = new Object[data.length][3];
//        for (int i = 0;  i< data.length ; i++) {
//            Object[] dataRow= data[i];
//            Arrays.parallelSetAll(newData[i], j->j==0?0:dataRow[j-1]);
//        }
        initialize();
        this.setBorder(new TitledBorder("Life Table"));
        setEnabledRemoveValue(true);

    }

    /**
     * Invoked when the button 'add' is pressed.
     */
    @Override
    protected void actionPerformedAddValue() {
        tableModel.addRow(new Object[]{0,0,0});
    }

    /**
     * Invoked when the button 'remove' is pressed.
     */
    protected void actionPerformedRemoveValue() {
        int selectedRowIndex = valuesTable.getSelectedRow();
        if (selectedRowIndex > -1)  tableModel.removeRow(selectedRowIndex);
    }
    /**
     * Invoked when the row selection changes.
     *
     * @param e selection event information.
     */
    public void valueChanged(ListSelectionEvent e) {

        int index = valuesTable.getSelectedRow();
        int rowCount = valuesTable.getRowCount();
        if ((rowCount == 0) || (index == -1)) {
            removeValueButton.setEnabled(false);
            upValueButton.setEnabled(false);
            downValueButton.setEnabled(false);
        } else {
            removeValueButton.setEnabled(true);
            if (index == 0) {
                upValueButton.setEnabled(false);
                if (index == (rowCount - 1)) {
                    downValueButton.setEnabled(false);
                } else {
                    downValueButton.setEnabled(true);
                }
            } else if (index == (valuesTable.getRowCount() - 1)) {
                downValueButton.setEnabled(false);
                if (index == 0) {
                    upValueButton.setEnabled(false);
                } else {
                    upValueButton.setEnabled(true);
                }
                upValueButton.setEnabled(true);
            } else {
                upValueButton.setEnabled(true);
                downValueButton.setEnabled(true);
            }
        }
        if (rowCount <= 1) {
            removeValueButton.setEnabled(false);
        } else {
            removeValueButton.setEnabled(true);
        }
    }




}
