package org.openmarkov.core.gui.dialog.costeffectiveness;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
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
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTDrawing;



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
			row.getCell(0).setCellValue(new HSSFRichTextString("Initial Age"));
			row.getCell(1).setCellValue(initialAge);
			
			row = parametersSheet.getRow(1);
			row.getCell(0).setCellValue(new HSSFRichTextString("Final Age"));
			row.getCell(1).setCellValue(finalAge);
			
			row = parametersSheet.getRow(2);
			row.getCell(0).setCellValue(new HSSFRichTextString("Discount Rate"));
			row.getCell(1).setCellValue(discount);
			
		}
		
		private void writeAllInterventionsSheet(Object [][] data) {
			HSSFSheet allInterventionsSheet = workBook.getSheet("All interventions");
			int numDecisions = data.length - 2;//o menos 3 por la fila final que se oculta
			//cuidado con los indices de la tablita!!!!
			//for each intervention
			int rowIndex = 1;
			int cellIndex;
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
		/**
		 * creates a new book with temporal evolution of a variable
		 */
		public void createTemporalEvolutionExcelReport( HashMap<Variable,TablePotential> temporalEvolution, ProbNet expandedNetwork,
				CostEffectivenessDialog costEffectivenessDialog, Variable variableOfInterest) {
			String filename = costEffectivenessDialog.getOutputFileName();
			HSSFWorkbook hwb = new HSSFWorkbook();
			
			String sheetName = "Temporal evolution for "+ variableOfInterest.getBaseName().toString();
			
			HSSFSheet sheetTable = hwb.createSheet(sheetName);

			//first row, column names
			HSSFRow rowIndexes = sheetTable.createRow(0);
			rowIndexes.createCell(0).setCellValue("");
			String basename = variableOfInterest.getBaseName();
	    	ArrayList<ProbNode> probNodes = expandedNetwork.getProbNodes();
			for (int i = 0; i < probNodes.size(); i++) {
	    		if (probNodes.get(i).getVariable().getBaseName().equals(basename)) {
	    			rowIndexes.createCell(i+1).setCellValue(probNodes.get(i).getVariable().getName());
			   }
			}
			
			//first column
			for (int i = 0; i < variableOfInterest.getNumStates(); i++) {
				HSSFRow rowi=   sheetTable.createRow(i+1);
				rowi.createCell(0).setCellValue(variableOfInterest.getStateName(i));
			}
			
			for (int i = 0; i < variableOfInterest.getNumStates(); i++) {
				for (int j = 0; j < costEffectivenessDialog.getNumSlices(); j++) {
					String basenameInterest = variableOfInterest.getBaseName();
					ArrayList<ProbNode> expandedProbNodes = expandedNetwork.getProbNodes();
					for (int k = 0; k < expandedProbNodes.size(); k++) {
						if (expandedProbNodes.get(k).getVariable().getBaseName().equals(basenameInterest) 
								&& expandedProbNodes.get(k).getVariable().getTimeSlice() == j) {
							double value = temporalEvolution.get(expandedProbNodes.get(k).getVariable()).getValues()[i];
							//cell(row, column) = cell(i+1, j+1)
							sheetTable.getRow(i+1).createCell(j+1).setCellValue(value);
							
						}
					}

				}
			}
			
			
			
/*			
			CTDrawing drawing = CTDrawing.Factory.newInstance();
			//XSSFChart chart =  createChart(XSSFClientAnchor anchor) 
			
			//HSSFPatriarch drawing = (HSSFPatriarch) sheetTable.createDrawingPatriarch();
			//ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 5, 10, 15);
			XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, 1, variableOfInterest.getNumStates()+5, 10, 10);
			
			XSSFChart chart =  (XSSFChart) ((Drawing) drawing).createChart(anchor); 
			//Chart chart = drawing.createChart(anchor);
			ChartLegend legend = chart.getOrCreateLegend();
			legend.setPosition(LegendPosition.TOP_RIGHT);

			ScatterChartData data = chart.getChartDataFactory().createScatterChartData();

			ValueAxis bottomAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.BOTTOM);
			ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
			leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

					
			
			ChartDataSource<Number> xs = DataSources.fromNumericCellRange((org.apache.poi.ss.usermodel.Sheet) sheetTable,
					new CellRangeAddress(0, 0, 1, costEffectivenessDialog.getNumSlices()));
			
			for (int i = 0; i < variableOfInterest.getNumStates(); i++) {
				ChartDataSource<Number> ysi = DataSources.fromNumericCellRange((org.apache.poi.ss.usermodel.Sheet) sheetTable,
						new CellRangeAddress(i+1, i+1, 1, costEffectivenessDialog.getNumSlices()));
				data.addSerie(xs, ysi);
			}
			

			//data.addSerie(xs, ys1);
			//data.addSerie(xs, ys2);

			chart.plot(data, bottomAxis, leftAxis);
*/
			
			
			FileOutputStream fileOut;
			try {
				fileOut = new FileOutputStream(filename);
				try {
					hwb.write(fileOut);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					fileOut.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
