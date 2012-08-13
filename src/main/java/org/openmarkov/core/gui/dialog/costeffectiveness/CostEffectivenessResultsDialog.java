package org.openmarkov.core.gui.dialog.costeffectiveness;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.gui.dialog.common.CPTablePanel;
import org.openmarkov.core.gui.dialog.common.OkCancelApplyUndoRedoHorizontalDialog;
import org.openmarkov.core.gui.dialog.common.ProbabilityTablePanel;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
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
	private CostEffectivenessAnalysis costeffectivenessAnalysis;
	private JTabbedPane tabbedPane;
	private static ArrayList<Intervention> interventions;
	private FrontierInterventionsTablePanel frontierInterventionsTablePanel;
	@SuppressWarnings("static-access")
	public CostEffectivenessResultsDialog(Window owner, CostEffectivenessAnalysis costeffectivenessAnalysis, 
			TablePotential globalUtility, CostEffectivenessDialog costEffectivenessDialog) {
		super(owner);
		removeAll();
		this.globalUtility = globalUtility;
		this.costEffectivenessDialog = costEffectivenessDialog;
		this.costeffectivenessAnalysis = costeffectivenessAnalysis;
		this.interventions = new ArrayList<>();
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
        setVisible(true);
        pack();
	}
	
	 private void initialize() {

	       dialogStringResource =
	            StringResourceLoader.getUniqueInstance().getBundleDialogs();
	        messageStringResource =
	            StringResourceLoader.getUniqueInstance().getBundleMessages();
	        setTitle(dialogStringResource
	            .getString("CostEffectivenessResultDialog.Title.Label"));
	        createInterventions();	
	        configureComponentsPanel();
	        pack();
	        
	    }
	 
	 private void configureComponentsPanel() {
		 
		//do not want to see ok cancel buttons
		 	getBottomPanel().setVisible(false);
		 	
	        getComponentsPanel().setLayout(new BorderLayout(5, 5));
	        getComponentsPanel().setMaximumSize(new Dimension( 180,40));
	        
	        getComponentsPanel().add(getTabbedPane());
	        pack();
		 	//do not want to see ok cancel buttons
		 	/*getBottomPanel().setVisible(false);
	        getComponentsPanel().setLayout(new BorderLayout(5, 5));
	        getComponentsPanel().setMaximumSize(new Dimension( 180,40));
	        getComponentsPanel().add(getPotentialPanel (), BorderLayout.CENTER);
	        getComponentsPanel().add(getChartsPanel());
	        createExcel();
	        pack();*/
	        createExcel();
	   }
	 
	 /**
		 * This method initialises tabbedPane.
		 * 
		 * @return a new tabbed pane.
		 */
		protected JTabbedPane getTabbedPane() {

			if (tabbedPane == null) {
				tabbedPane = new JTabbedPane();
				tabbedPane.setName("CostEffectivenessResultTabbedPane");
				
			tabbedPane
					.addTab(
						dialogStringResource
							.getString("AllInterventionsChart.Title.Label"),
						null, getChartsPanel(), null);
			tabbedPane
			.addTab(
				dialogStringResource
					.getString("AllInterventionsTable.Title.Label"),
				null,getPotentialPanel() , null);
			
			tabbedPane
				.addTab(
					dialogStringResource
						.getString("FrontierInterventions.Title.Label"),
					null, ((FrontierInterventionsTablePanel) getFrontierInterventionsPanel()).getValuesTableScrollPane(), null);
		}
			return tabbedPane;
		}
	 private static  ProbabilityTablePanel getPotentialPanel () {
		 	if(cpTablePanel == null){
		 		
		 		//create a dummy probnet
		 		ProbNet dummyProbNet = new ProbNet();
		 		ProbNode dummy = new ProbNode(dummyProbNet, globalUtility.getVariables().get(0), NodeType.CHANCE);
	        	
        		for (int i = 1; i < globalUtility.getVariables().size(); i++) {
        			ProbNode newProbNode = new ProbNode(dummyProbNet, globalUtility.getVariables().get(i), NodeType.CHANCE);
					dummyProbNet.addLink(newProbNode, dummy, true);
				}
		 		
	        	ArrayList<Potential> potentials = new ArrayList<>();
	        	try {
	        		/*ArrayList<Variable> variables = new ArrayList<>();
	        		for (int i = 1; i < globalUtility.getVariables().size(); i++) {
	        			variables.add(globalUtility.getVariables().get(i));
	        		}*/
	        		
					TablePotential aux = new TablePotential(globalUtility.getVariables(), PotentialRole.CONDITIONAL_PROBABILITY);
					//aux.setUtilityVariable(globalUtility.getVariables().get(0));
					aux.setValues(globalUtility.getValues());
					potentials.add(aux);
		        	dummy.setPotentials(potentials);
		        	//not modifiable table potential panel 
		        	cpTablePanel = new CPTablePanel(dummy);
		        	cpTablePanel.getCommentHTMLScrollPaneNodeDefinitionComment().setVisible(false);
				} catch (NotEnoughMemoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	        }
	        return cpTablePanel;
	    }
	
	 private ChartPanel getChartsPanel () {
		 if(chartPanel == null){
			 XYDataset dataset = createDataset();
			 JFreeChart chart = ChartFactory.createScatterPlot("Cost-Effectiveness Analysis Result",
					 "effectiveness", "cost", dataset, PlotOrientation.VERTICAL, true, true, true);
			 //chart.getXYPlot().setRenderer(new XYSplineRenderer());
			 chartPanel = new ChartPanel(chart);
			 chartPanel.setAutoscrolls(true);
			 chartPanel.setDisplayToolTips(true);
			 chartPanel.setMouseZoomable(true);
			 
			 XYPlot plot = (XYPlot) chart.getPlot();
			 XYItemRenderer renderer = plot.getRenderer();
			 XYToolTipGenerator generator = new StandardXYToolTipGenerator("{0}: ({1}, {2})", new DecimalFormat("0.00"), new DecimalFormat("0.00"));
			 renderer.setToolTipGenerator(generator);
			

		 }
		 return chartPanel;
	 } 
	 
	/* private static XYDataset createDataset() {
		    XYSeriesCollection result = new XYSeriesCollection();
		    XYSeries series = new XYSeries("Cost Effectiveness");
		    Object data [][] = ((ProbabilityTablePanel)getPotentialPanel()).getData();
		    for (int i = 1; i < data[1].length; i++) {
		    	double effectiveness = Double.valueOf(data[data.length-3][i].toString()).doubleValue();
		    	double cost = Double.valueOf(data[data.length-2][i].toString()).doubleValue();
		    	//generateToolTip(result, int series, int item);
		        series.add(effectiveness, cost);
		      		        
		    }
		    result.addSeries(series);
		    return result;
		}*/
	 
	 private static XYDataset createDataset() {
		    XYSeriesCollection result = new XYSeriesCollection();
		   Object data [][] = ((ProbabilityTablePanel)getPotentialPanel()).getData();
		    for (int i = 1; i < data[1].length; i++) {
		    	XYSeries series = new XYSeries(interventions.get(i-1).getName());
				double effectiveness = Double.valueOf(data[data.length-3][i].toString()).doubleValue();
		    	double cost = Double.valueOf(data[data.length-2][i].toString()).doubleValue();
		    	//generateToolTip(result, int series, int item);
		        series.add(effectiveness, cost);
		        result.addSeries(series);        
		    }
		    
		    return result;
		}
	 private void createInterventions() {
		 Object data [][] = ((ProbabilityTablePanel)getPotentialPanel()).getData();
		 int numDecisions = data.length - 3;
		 //each column of data is an intervention
		 for (int i = 1; i < data[0].length; i++) {
			 double effectiveness =  Double.valueOf(data[data.length-3][i].toString()).doubleValue();
		     double cost = Double.valueOf(data[data.length-2][i].toString()).doubleValue();
		     String name = null;
		     for (int j = 0; j < numDecisions; j++) {
		    	 name = "Dec: " + data [j][0].toString() + " = " + data[j][i].toString();
		    	 if (j != numDecisions-1) {
		    		 name += "; ";
		    	 }
		     }
		     Intervention intervention = new Intervention(name, cost, effectiveness);
		     interventions.add(intervention);
		 }
		 costeffectivenessAnalysis.setInterventions(interventions);
	 }

	 private JPanel getFrontierInterventionsPanel () {
		 if(frontierInterventionsTablePanel == null){
			 
			 frontierInterventionsTablePanel = new  FrontierInterventionsTablePanel(costeffectivenessAnalysis);
			  //add(tablePanel.getValuesTableScrollPane());
			  //tablePanel.setAutoscrolls(true);
			 }
			 return frontierInterventionsTablePanel;
	 }
	 
	 private void createExcel() {
		 ExcelReport excel = ExcelReport.getUniqueInstance();
		 excel.setInitialData(costEffectivenessDialog);
		 try {
			excel.writeExcelReportOptimalInterventions(interventions, costeffectivenessAnalysis.getFrontierIntervention(
					costeffectivenessAnalysis.getInterventions().toArray(new Intervention[costeffectivenessAnalysis.getInterventions().size()] )), costEffectivenessDialog.getOutputFileName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }

}
