package org.openmarkov.core.gui.dialog.costeffectiveness;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.IOException;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openmarkov.core.gui.dialog.common.CPTablePanel;
import org.openmarkov.core.gui.dialog.common.OkCancelApplyUndoRedoHorizontalDialog;
import org.openmarkov.core.gui.dialog.common.PotentialPanel;
import org.openmarkov.core.gui.dialog.common.ProbabilityTablePanel;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.TablePotential;
	
/**
 * Dialog box to show the results from cost-effectiveness analysis
 * 
 * @author myebra
 *
 */
@SuppressWarnings("serial")
public class CostEffectivenessResultsDialog extends OkCancelApplyUndoRedoHorizontalDialog {

	private static TablePotential globalUtility;
	private StringResource dialogStringResource;
	private StringResource messageStringResource;
	private static CPTablePanel cpTablePanel;
	private ChartPanel chartPanel;
	private CostEffectivenessDialog costEffectivenessDialog;
	@SuppressWarnings("static-access")
	public CostEffectivenessResultsDialog(Window owner, TablePotential globalUtility, CostEffectivenessDialog costEffectivenessDialog) {
		super(owner);
		this.globalUtility = globalUtility;
		this.costEffectivenessDialog = costEffectivenessDialog;
		initialize();
        
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
       
        Rectangle bounds = owner.getBounds();
        int width = screenSize.width/2;
		int height = screenSize.height/2;
        //center point of the owner window
		int x = bounds.x/2 - width/2;
		int y = bounds.y/2 - height/2;
		this.setBounds(x, y, width, height);
		setLocationRelativeTo(owner);
        setMinimumSize(new Dimension( width, height/2 ));
        setResizable(true);
        pack();
	}
	
	 private void initialize() {

	       dialogStringResource =
	            StringResourceLoader.getUniqueInstance().getBundleDialogs();
	        messageStringResource =
	            StringResourceLoader.getUniqueInstance().getBundleMessages();
	        setTitle(dialogStringResource
	            .getString("CostEffectivenessResultDialog.Title.Label"));
	       
	        configureComponentsPanel();
	        pack();
	        setVisible(true);
	    }
	 
	 private void configureComponentsPanel() {
		 	//do not want to see ok cancel buttons
		 	getBottomPanel().setVisible(false);
	        getComponentsPanel().setLayout(new BorderLayout(5, 5));
	        getComponentsPanel().setMaximumSize(new Dimension( 180,40));
	        getComponentsPanel().add(getPotentialPanel (), BorderLayout.CENTER);
	        getComponentsPanel().add(getChartsPanel());
	        createExcel();
	        //TODO mostrar un mensaje avisando de que se ha creado un fichero excel y donde
	        pack();
	   }
	 
	 private static  ProbabilityTablePanel getPotentialPanel () {
		 	if(cpTablePanel == null){
	        	ProbNode dummy = new ProbNode(new ProbNet(), globalUtility.getVariables().get(0), NodeType.CHANCE);
	        	ArrayList<Potential> potentials = new ArrayList<>();
	        	potentials.add(globalUtility);
	        	dummy.setPotentials(potentials);
	           //tablePotentialPanel = new TablePotentialPanel(dummy);
	        	//not modifiable table potential panel 
	        	cpTablePanel = new CPTablePanel(dummy);
	        }
	        return cpTablePanel;
	    }
	
	 private ChartPanel getChartsPanel () {
		 if(chartPanel == null){
			 XYDataset dataset = createDataset();
			 JFreeChart chart = ChartFactory.createScatterPlot("Cost-Effectiveness Analysis Result", "effectiveness", "cost", dataset, PlotOrientation.VERTICAL, true, true, false);
			 chartPanel = new ChartPanel(chart);
	        }
	        return chartPanel;
	 } 
	 
	 private static XYDataset createDataset() {
		    XYSeriesCollection result = new XYSeriesCollection();
		    XYSeries series = new XYSeries("CostEffectiveness");
		    Object data [][] = ((ProbabilityTablePanel)getPotentialPanel()).getData();
		    for (int i = 2; i < data[1].length; i++) {
		        double effectiveness = (Double) data[data.length-1][i];
		        double cost = (Double) data[data.length][i];
		        series.add(effectiveness, cost);
		    }
		    result.addSeries(series);
		    return result;
		}
	 
	 private void createExcel() {
		 ExcelReport excel = ExcelReport.getUniqueInstance();
		 excel.setInitialData(costEffectivenessDialog.getInitialAge(), costEffectivenessDialog.getFinalAge(), costEffectivenessDialog.getDiscount());
		 try {
			excel.writeExcelReportOptimalInterventions(((ProbabilityTablePanel)getPotentialPanel()).getData(), costEffectivenessDialog.getOutputFileName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }

}
