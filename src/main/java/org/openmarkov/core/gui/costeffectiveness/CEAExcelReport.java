
package org.openmarkov.core.gui.costeffectiveness;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Chart;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.charts.AxisCrosses;
import org.apache.poi.ss.usermodel.charts.AxisPosition;
import org.apache.poi.ss.usermodel.charts.ChartDataSource;
import org.apache.poi.ss.usermodel.charts.ChartLegend;
import org.apache.poi.ss.usermodel.charts.DataSources;
import org.apache.poi.ss.usermodel.charts.LegendPosition;
import org.apache.poi.ss.usermodel.charts.ScatterChartData;
import org.apache.poi.ss.usermodel.charts.ValueAxis;
import org.apache.poi.ss.util.CellRangeAddress;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.TablePotential;

/**
 * This class is used to generate excel reports with cost effectiveness analysis
 * and temporal evolution results
 * @author myebra
 */
public class CEAExcelReport
{
    // Attributes

    private double       costDiscount;
    private double       effectivenessDiscount;
    private int          numSlices;
    private String       targetFilename;
    private HSSFWorkbook workBook;
	private List<Intervention> interventions;
	private List<Intervention> frontierInterventions;

    public CEAExcelReport (CostEffectivenessAnalysis costEffectivenessAnalysis)
    {
        this.numSlices = costEffectivenessAnalysis.getProbNet().getInferenceOptions().getTemporalOptions().getNumberOfSlices();
        // TODO - Check this code
        //this.costDiscount = costEffectivenessAnalysis.getCostDiscountRate ();
        //this.effectivenessDiscount = costEffectivenessAnalysis.getEffectivenessDiscountRate ();
        this.interventions = costEffectivenessAnalysis.getInterventions();
        this.frontierInterventions = costEffectivenessAnalysis.getFrontierInterventions();
    }
    
    public void drawScatterChart ()
    {
        // Workbook wb = new XSSFWorkbook();
        // Sheet sheet = wb.createSheet("Sheet 1");
        HSSFSheet sheet = workBook.getSheet ("All interventions");
        final int NUM_OF_COLUMNS = 10;
        // Create a row and put some cells in it. Rows are 0 based.
        // Row row;
        // Cell cell;
        // for (int rowIndex = 0; rowIndex < NUM_OF_ROWS; rowIndex++) {
        // row = sheet.createRow((short) rowIndex);
        // for (int colIndex = 0; colIndex < NUM_OF_COLUMNS; colIndex++) {
        // cell = row.createCell((short) colIndex);
        // cell.setCellValue(colIndex * (rowIndex + 1));
        // }
        // }
        Drawing drawing = (Drawing) sheet.createDrawingPatriarch ();
        ClientAnchor anchor = drawing.createAnchor (0, 0, 0, 0, 0, 5, 10, 15);
        Chart chart = drawing.createChart (anchor);
        ChartLegend legend = chart.getOrCreateLegend ();
        legend.setPosition (LegendPosition.TOP_RIGHT);
        ScatterChartData data = chart.getChartDataFactory ().createScatterChartData ();
        ValueAxis bottomAxis = chart.getChartAxisFactory ().createValueAxis (AxisPosition.BOTTOM);
        ValueAxis leftAxis = chart.getChartAxisFactory ().createValueAxis (AxisPosition.LEFT);
        leftAxis.setCrosses (AxisCrosses.AUTO_ZERO);
        ChartDataSource<Number> xs = DataSources.fromNumericCellRange ((org.apache.poi.ss.usermodel.Sheet) sheet,
                                                                       new CellRangeAddress (
                                                                                             0,
                                                                                             0,
                                                                                             0,
                                                                                             NUM_OF_COLUMNS - 1));
        ChartDataSource<Number> ys1 = DataSources.fromNumericCellRange ((org.apache.poi.ss.usermodel.Sheet) sheet,
                                                                        new CellRangeAddress (
                                                                                              1,
                                                                                              1,
                                                                                              0,
                                                                                              NUM_OF_COLUMNS - 1));
        // ChartDataSource<Number> ys2 =
        // DataSources.fromNumericCellRange((org.apache.poi.ss.usermodel.Sheet)
        // sheet, new CellRangeAddress(2, 2, 0, NUM_OF_COLUMNS - 1));
        data.addSerie (xs, ys1);
        // data.addSerie(xs, ys2);
        chart.plot (data, bottomAxis, leftAxis);
        // Write the output to a file
        // FileOutputStream fileOut = new
        // FileOutputStream("ooxml-scatter-chart.xlsx");
        // workBook.write(fileOut);
        // fileOut.close();
    }

    /**
     * @param interventions
     * @param frontierInterventions
     * @param filename
     * @throws IOException
     */
    public void writeReport (String filename)
        throws IOException
    {
        // HSSFCellStyle style = wb.createCellStyle();
        // style.setFillForegroundColor(new
        // HSSFColor.GREY_25_PERCENT().getIndex());
        // style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND );
        workBook = new HSSFWorkbook ();
        writeInputSheet ();
        writeAllInterventionsSheet (interventions);
        writeFrontierSheet (frontierInterventions);
        this.targetFilename = checkXlsExtension (filename);
        // FileOutputStream file = new FileOutputStream(
        // PATH_EXCEL_FILES + targetFileName);
        FileOutputStream file = new FileOutputStream (targetFilename);
        workBook.write (file);
        file.close ();
    }

    public String getPathFile ()
    {
        // return PATH_EXCEL_FILES + targetFileName;
        return targetFilename;
    }

    private void writeInputSheet ()
    {
        HSSFSheet parametersSheet = workBook.createSheet ("Input");
        HSSFRow row = parametersSheet.createRow (0);
        row.createCell (0).setCellValue (new HSSFRichTextString ("Number of slices"));
        row.createCell (1).setCellValue (numSlices);
        row = parametersSheet.createRow (1);
        row.createCell (0).setCellValue (new HSSFRichTextString ("Cost Discount Rate"));
        row.createCell (1).setCellValue (costDiscount);
        row = parametersSheet.createRow (2);
        row.createCell (0).setCellValue (new HSSFRichTextString ("Effectiveness Discount Rate"));
        row.createCell (1).setCellValue (effectivenessDiscount);
    }

    private void writeAllInterventionsSheet (List<Intervention> interventions)
    {
        HSSFSheet allInterventionsSheet = workBook.createSheet ("All interventions");
        int rowIndex = 1;
        int cellIndex;
        HSSFRow row;
        HSSFCellStyle style = workBook.createCellStyle ();
        HSSFDataFormat format = workBook.createDataFormat ();
        style.setDataFormat (format.getFormat ("#######.00"));
        row = allInterventionsSheet.createRow (0);
        row.createCell (0).setCellValue ("Strategy");
        row.createCell (1).setCellValue ("Effectiveness");
        row.createCell (2).setCellValue ("Cost");
        for (Intervention intervention : interventions)
        {
            cellIndex = 0;
            row = allInterventionsSheet.createRow (rowIndex++);
            row.createCell (cellIndex++).setCellValue (new HSSFRichTextString (intervention.name));
            row.createCell (cellIndex).setCellValue (intervention.effectiveness);
            row.getCell (cellIndex++).setCellStyle (style);
            row.createCell (cellIndex).setCellValue (intervention.cost);
            row.getCell (cellIndex++).setCellStyle (style);
            // row.createCell(cellIndex++).setCellValue(intervention.iCER);
        }
    }

    private void writeFrontierSheet (List<Intervention> interventions)
    {
        HSSFSheet frontierInterventionsSheet = workBook.createSheet ("Frontier");
        int rowIndex = 1;
        int cellIndex;
        HSSFRow row;
        HSSFCellStyle style = workBook.createCellStyle ();
        HSSFDataFormat format = workBook.createDataFormat ();
        style.setDataFormat (format.getFormat ("#.00"));
        row = frontierInterventionsSheet.createRow (0);
        row.createCell (0).setCellValue ("Strategy");
        row.createCell (1).setCellValue ("Effectiveness");
        row.createCell (2).setCellValue ("Cost");
        row.createCell (2).setCellValue ("ICER");
        for (int i = 0; i < interventions.size (); i++)
        {
            Intervention intervention = interventions.get (i);
            cellIndex = 0;
            row = frontierInterventionsSheet.createRow (rowIndex++);
            row.createCell (cellIndex++).setCellValue (new HSSFRichTextString (intervention.name));
            row.createCell (cellIndex).setCellValue (intervention.effectiveness);
            row.getCell (cellIndex++).setCellStyle (style);
            row.createCell (cellIndex).setCellValue (intervention.cost);
            row.getCell (cellIndex++).setCellStyle (style);
            if (i > 0)
            {
                row.createCell (cellIndex).setCellValue (intervention.iCER);
                row.getCell (cellIndex++).setCellStyle (style);
            }
        }
    }

    private String checkXlsExtension (String filename)
    {
        if (!filename.endsWith (".xls"))
        {
            filename += ".xls";
        }
        return filename;
    }

    public void useTemplate (String templateFilename)
        throws IOException
    {
        InputStream inp;// = new FileInputStream(templateFileName);
        Class<? extends CEAExcelReport> class1 = getClass ();
        inp = class1.getResourceAsStream (templateFilename);
        /*
         * URL path = getClass().getResource(templateFileName); String realPath=
         * path.toString().replaceFirst("file:/", ""); InputStream inp = new
         * FileInputStream(realPath);
         */
        workBook = new HSSFWorkbook (inp);
        inp.close ();
    }
}
