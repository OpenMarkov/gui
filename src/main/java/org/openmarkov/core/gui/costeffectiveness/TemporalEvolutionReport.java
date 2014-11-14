package org.openmarkov.core.gui.costeffectiveness;

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

public class TemporalEvolutionReport {

	// Methods
    /**
     * creates a new book with temporal evolution of a variable
     * @throws IOException 
     */
    public void write (String filename,
                                                    Map<Variable, TablePotential> temporalEvolution,
                                                    ProbNet expandedNetwork,
                                                    int numSlices,
                                                    Variable variableOfInterest) throws IOException
    {
        HSSFWorkbook hwb = new HSSFWorkbook ();
        String sheetName = variableOfInterest.getBaseName ().toString ();
        HSSFSheet sheetTable = hwb.createSheet (sheetName);
        // first row, column names
        HSSFRow rowIndexes = sheetTable.createRow (0);
        rowIndexes.createCell (0).setCellValue ("");
        String basename = variableOfInterest.getBaseName ();
        List<Node> nodes = expandedNetwork.getNodes ();
        List<Node> interestNodes = new ArrayList<> ();
        for (int i = 0; i < nodes.size (); i++)
        {
            if (nodes.get (i).getVariable ().getBaseName ().equals (basename))
            {
                interestNodes.add (nodes.get (i));
            }
        }
        for (int i = 0; i < interestNodes.size (); i++)
        {
            rowIndexes.createCell (i + 1).setCellValue (interestNodes.get (i).getVariable ().getTimeSlice());
        }
        // first column
        for (int i = 0; i < variableOfInterest.getNumStates (); i++)
        {
            HSSFRow row = sheetTable.createRow (i + 1);
            row.createCell (0).setCellValue (variableOfInterest.getStateName (i));
        }
        for (int i = 0; i < variableOfInterest.getNumStates (); i++)
        {
            for (int j = 0; j <= numSlices; j++)
            {
                String basenameInterest = variableOfInterest.getBaseName ();
                List<Node> expandedNodes = expandedNetwork.getNodes ();
                for (int k = 0; k < expandedNodes.size (); k++)
                {
                    if (expandedNodes.get (k).getVariable ().getBaseName ().equals (basenameInterest)
                        && expandedNodes.get (k).getVariable ().getTimeSlice () == j)
                    {
                        double value = temporalEvolution.get (expandedNodes.get (k).getVariable ()).getValues ()[i];
                        // cell(row, column) = cell(i+1, j+1)
                        sheetTable.getRow (i + 1).createCell (j + 1).setCellValue (value);
                    }
                }
            }
        }
        String targetFilename =  filename.endsWith (".xls")? filename : filename +".xls";
        FileOutputStream fileOut = new FileOutputStream (targetFilename);
        hwb.write (fileOut);
        fileOut.close ();
    }
}
