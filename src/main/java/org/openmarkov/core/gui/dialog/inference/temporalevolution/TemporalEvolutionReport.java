package org.openmarkov.core.gui.dialog.inference.temporalevolution;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.TablePotential;

import javax.swing.*;

public class TemporalEvolutionReport {

    // Methods
    /**
     * creates a new book with temporal evolution of a variable
     * @throws IOException
     */
    public void write (String filename,
                       JTable jtable) throws IOException
    {
        HSSFWorkbook hwb = new HSSFWorkbook ();
        String sheetName = filename;
        HSSFSheet sheetTable = hwb.createSheet ("Temporal Evolution Report");
        // first row, column names
        HSSFRow rowIndexes = sheetTable.createRow (0);
        rowIndexes.createCell (0).setCellValue ("");

        for (int i = 1; i < jtable.getColumnCount(); i++)
        {
            rowIndexes.createCell (i + 1).setCellValue (jtable.getColumnModel().getColumn(i).getHeaderValue().toString());
        }
        // fill data
        for (int i = 0; i < jtable.getRowCount(); i++)
        {
            HSSFRow row = sheetTable.createRow (i + 1);
            for (int j = 0; j < jtable.getColumnCount(); j++) {
                if(jtable.getValueAt(i, j) instanceof String) {
                    row.createCell(j).setCellValue((String) jtable.getValueAt(i, j));
                } else if (jtable.getValueAt(i, j) instanceof Integer) {
                    row.createCell(j).setCellValue((Integer) jtable.getValueAt(i, j));
                }else {
                    row.createCell(j).setCellValue((Double) jtable.getValueAt(i, j));
                }
            }

        }

        String targetFilename =  filename.endsWith (".xls")? filename : filename +".xls";
        FileOutputStream fileOut = new FileOutputStream (targetFilename);
        hwb.write (fileOut);
        fileOut.close ();
    }
}
