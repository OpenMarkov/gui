package org.openmarkov.core.gui.dialog.costeffectiveness;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.model.ConvertAnchor;
import org.apache.poi.hssf.model.DrawingManager;
import org.apache.poi.hssf.model.Sheet;
import org.apache.poi.hssf.model.Workbook;
import org.apache.poi.hssf.record.formula.functions.Cell;
import org.apache.poi.hssf.record.formula.functions.Row;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFChart;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.*;
import org.apache.poi.ss.usermodel.charts.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReport {

	// Attributes
		/** Attribute that points to the unique instance of this object (singleton
		 *  pattern) */
		private static ExcelReport writer = null;
		
		private static final String PATH_EXCEL_FILES = "c:/openmarkov/" ;
		
		// Methods
		/** Singleton pattern.
		 * @return <code>Reader</code> */
		public static ExcelReport getUniqueInstance() {
			if (writer == null) {
				writer = new ExcelReport();
			}
			return writer;
		}
		private int initialAge;
		private double discount;
		private int finalAge;
		private String targetFileName;

		private HSSFWorkbook workBook;
		
		public void writeExcelReportOptimalInterventions(Object [][]data, 
				/*Intervention[] frontier,*/ String fileName) throws IOException{
			
				//HSSFCellStyle style = wb.createCellStyle();
		       //style.setFillForegroundColor(new HSSFColor.GREY_25_PERCENT().getIndex());
		       //style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND );
				workBook = new HSSFWorkbook();
				HSSFSheet input = workBook.createSheet("Input");
				HSSFSheet allInterventions = workBook.createSheet("All interventions");
				HSSFSheet chart = workBook.createSheet("Result Chart");
				
				writeInputSheet();
				writeAllInterventionsSheet(data);
				//writeFrontierSheet(frontier);
				this.targetFileName = checkXLSExtention(fileName);
				//FileOutputStream file = new FileOutputStream(
					//	PATH_EXCEL_FILES + targetFileName);
				FileOutputStream file = new FileOutputStream( targetFileName );
				workBook.write(file);
				file.close();
			
		}
		private void writeInputSheet() {
			HSSFSheet parametersSheet = workBook.getSheet("Input");
			
			HSSFRow row = parametersSheet.getRow(0);
			row.getCell((short)0).setCellValue(new HSSFRichTextString("Initial Age"));
			row.getCell((short)1).setCellValue(initialAge);
			
			row = parametersSheet.getRow(1);
			row.getCell((short)0).setCellValue(new HSSFRichTextString("Final Age"));
			row.getCell((short)1).setCellValue(finalAge);
			
			row = parametersSheet.getRow(2);
			row.getCell((short)0).setCellValue(new HSSFRichTextString("Discount Rate"));
			row.getCell((short)1).setCellValue(discount);
			
		}
		
		private void writeAllInterventionsSheet(Object [][] data) {
			HSSFSheet allInterventionsSheet = workBook.getSheet("All interventions");
			int numDecisions = data.length - 2;//o menos 3 por la fila final que se oculta
			//cuidado con los indices de la tablita!!!!
			//for each intervention
			int rowIndex = 1;
			short cellIndex;
			HSSFRow row;
			HSSFCellStyle style = workBook.createCellStyle();
			HSSFDataFormat format = workBook.createDataFormat();
			style.setDataFormat(format.getFormat("#######.00"));
			for (int i = 2; i < data[1].length; i++) {//o i = 1 no se si hay una columna inicial más
				String intervention = null;
				//for each decision
				for (int j = 0; j < numDecisions; j++) {
					intervention += "Dec: " + data [j][1].toString() + " = " + data[j][i].toString();
					if (j < numDecisions-1) {
						intervention += ", ";
					}
				}
				cellIndex=0;
				row = allInterventionsSheet.createRow(rowIndex++);
				row.createCell(cellIndex++).setCellValue(
						new HSSFRichTextString(intervention));
				//effectiveness
				row.createCell(cellIndex).setCellValue((Double)data[data.length-1][i]);
				row.getCell(cellIndex++).setCellStyle(style);
				//cost
				row.createCell(cellIndex).setCellValue((Double)data[data.length-2][i]);
				row.getCell(cellIndex++).setCellStyle(style);
				//row.createCell(cellIndex++).setCellValue(intervention.iCER);
			}
			
		
	}
		public void drawScatterChart() {
		//	Workbook wb = new XSSFWorkbook();
	      //  Sheet sheet = wb.createSheet("Sheet 1");
			HSSFSheet sheet = workBook.getSheet("All interventions");
	        final int NUM_OF_ROWS = 3;
	        final int NUM_OF_COLUMNS = 10;

	        // Create a row and put some cells in it. Rows are 0 based.
//	        Row row;
//	        Cell cell;
//	        for (int rowIndex = 0; rowIndex < NUM_OF_ROWS; rowIndex++) {
//	            row = sheet.createRow((short) rowIndex);
//	            for (int colIndex = 0; colIndex < NUM_OF_COLUMNS; colIndex++) {
//	                cell = row.createCell((short) colIndex);
//	                cell.setCellValue(colIndex * (rowIndex + 1));
//	            }
//	        }

	        Drawing drawing = (Drawing) sheet.createDrawingPatriarch();
	        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 5, 10, 15);

	        Chart chart = drawing.createChart(anchor);
	        ChartLegend legend = chart.getOrCreateLegend();
	        legend.setPosition(LegendPosition.TOP_RIGHT);

	        ScatterChartData data = chart.getChartDataFactory().createScatterChartData();

	        ValueAxis bottomAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.BOTTOM);
	        ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
	        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

	        ChartDataSource<Number> xs = DataSources.fromNumericCellRange((org.apache.poi.ss.usermodel.Sheet) sheet, new CellRangeAddress(0, 0, 0, NUM_OF_COLUMNS - 1));
	        ChartDataSource<Number> ys1 = DataSources.fromNumericCellRange((org.apache.poi.ss.usermodel.Sheet) sheet, new CellRangeAddress(1, 1, 0, NUM_OF_COLUMNS - 1));
	       // ChartDataSource<Number> ys2 = DataSources.fromNumericCellRange((org.apache.poi.ss.usermodel.Sheet) sheet, new CellRangeAddress(2, 2, 0, NUM_OF_COLUMNS - 1));


	        data.addSerie(xs, ys1);
	        //data.addSerie(xs, ys2);

	        chart.plot(data, bottomAxis, leftAxis);

	        // Write the output to a file
	      //  FileOutputStream fileOut = new FileOutputStream("ooxml-scatter-chart.xlsx");
	        //workBook.write(fileOut);
	        //fileOut.close();
		}
		
		private String checkXLSExtention(String fileName2) {
			if ( !fileName2.endsWith(".xls") ){
				fileName2 += ".xls";
			}
			return fileName2;
		}
		

		public void setInitialData(int initialAge, int finalAge, double discount) {
			this.initialAge = initialAge;
			this.finalAge = finalAge;
			this.discount = discount;
			
		}

}
