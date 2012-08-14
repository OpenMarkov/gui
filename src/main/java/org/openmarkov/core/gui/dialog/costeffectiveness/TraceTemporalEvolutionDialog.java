package org.openmarkov.core.gui.dialog.costeffectiveness;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openmarkov.core.gui.dialog.common.OkCancelApplyUndoRedoHorizontalDialog;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.TablePotential;

@SuppressWarnings("serial")
public class TraceTemporalEvolutionDialog  extends OkCancelApplyUndoRedoHorizontalDialog {
	
	private static HashMap<Variable,TablePotential> temporalEvolution;
	private static CostEffectivenessDialog costEffectivenessDialog;
	private StringResource dialogStringResource;
	private StringResource messageStringResource;
	private ChartPanel chartPanel;
	private TemporalEvolutionTablePanel tablePanel;
	private JTabbedPane tabbedPane;
	private static Variable variableOfInterest;
	private static ProbNet expandedNetwork;
	private static boolean isUtility;

	public TraceTemporalEvolutionDialog (Window owner, HashMap<Variable,TablePotential> temporalEvolution,
			CostEffectivenessDialog costEffectivenessDialog, Variable variableOfInterest, ProbNet expandedNetwork, boolean isUtility) {
		super(owner);
		this.temporalEvolution = temporalEvolution;
		this.costEffectivenessDialog = costEffectivenessDialog;
		this.variableOfInterest = variableOfInterest;
		this.expandedNetwork = expandedNetwork;
		this.isUtility = isUtility;
		
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
		
        setMinimumSize(new Dimension( width, height/2 ));
		setLocationRelativeTo(owner);
        setResizable(true);
        repaint();
        createExcel();
        pack();
        setVisible(true);
	}
	
	 private void initialize() {

	       dialogStringResource =
	            StringResourceLoader.getUniqueInstance().getBundleDialogs();
	        messageStringResource =
	            StringResourceLoader.getUniqueInstance().getBundleMessages();
	        setTitle(dialogStringResource
	            .getString("TemporalEvolutionResultDialog.Title.Label")+ " " +variableOfInterest.getBaseName());
	       
	        configureComponentsPanel();
	        pack();
	        
	        
	    }
	 
	 private void configureComponentsPanel() {
		 	//do not want to see ok cancel buttons
		 	getBottomPanel().setVisible(false);
		 	
	        getComponentsPanel().setLayout(new BorderLayout(5, 5));
	        getComponentsPanel().setMaximumSize(new Dimension( 180,40));
	        
	        getComponentsPanel().add(getTabbedPane());
	      //  getComponentsPanel().add(getTablePanel(),BorderLayout.CENTER);
	       // getComponentsPanel().add(getChartsPanel(), BorderLayout.SOUTH);
	        pack();
	   }
	 /**
		 * This method initialises tabbedPane.
		 * 
		 * @return a new tabbed pane.
		 */
		protected JTabbedPane getTabbedPane() {

			if (tabbedPane == null) {
				tabbedPane = new JTabbedPane();
				tabbedPane.setName("TraceTemporalEvolutionTabbedPane");
				
			tabbedPane
					.addTab(
						dialogStringResource
							.getString("TemporalEvolutionChart.Title.Label"),
						null, getChartsPanel(), null);
				tabbedPane
				.addTab(
					dialogStringResource
						.getString("TemporalEvolutionTable.Title.Label"),
					null, ((TemporalEvolutionTablePanel)getTablePanel()).getValuesTableScrollPane(), null);
		}
			return tabbedPane;
		}
	 private ChartPanel getChartsPanel () {
		 if(chartPanel == null){
			 XYDataset dataset = createDataset();
			 JFreeChart chart = ChartFactory.createXYLineChart("Temporal Evolution of: "+variableOfInterest.getBaseName(), "t", "value", dataset, PlotOrientation.VERTICAL, true, true, true);
			// chart.getXYPlot().setRenderer(new XYSplineRenderer());
			 XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
			  for (int i = 0; i < dataset.getSeriesCount(); i++) {
				 renderer.setSeriesLinesVisible(i, true);
				 renderer.setSeriesShapesVisible(i, true);
			 }
			
			 chart.getXYPlot().setRenderer(renderer);
			 // chart.getXYPlot().setRenderer(new XYSplineRenderer());
			 chartPanel = new ChartPanel(chart);
			 chartPanel.setAutoscrolls(true);
			 chartPanel.setDisplayToolTips(true);
			 chartPanel.setMouseZoomable(true);
			 	
			 XYPlot plot = (XYPlot) chart.getPlot();
			 //XYItemRenderer renderer = plot.getRenderer();
			 XYToolTipGenerator generator = new StandardXYToolTipGenerator("{0}: ({1}, {2})", new DecimalFormat("0.00"), new DecimalFormat("0.00"));
			 renderer.setToolTipGenerator(generator);
			 }
	        return chartPanel;
	 } 
	 
	 private static XYDataset createDataset() {
		    XYSeriesCollection result = new XYSeriesCollection();
		    double value = 0.0;
		    for (int i = 0; i < variableOfInterest.getNumStates(); i++) {
		    	XYSeries series = null;
		    	if (isUtility) {
		    		series = new XYSeries(variableOfInterest.getBaseName());
		    	} else {
		    		series = new XYSeries(variableOfInterest.getStateName(i));
				}
			   for (int j = 0; j < costEffectivenessDialog.getNumSlices(); j++) {
			    	String basename = variableOfInterest.getBaseName();
			    	ArrayList<ProbNode> probNodes = expandedNetwork.getProbNodes();
			    	for (int k = 0; k < probNodes.size(); k++) {
			    		if (probNodes.get(k).getVariable().getBaseName().equals(basename) 
			    				&& probNodes.get(k).getVariable().getTimeSlice() == j) {
			    			
			    			if (isUtility && costEffectivenessDialog.isAccumulative()) {
			    				value += temporalEvolution.get(probNodes.get(k).getVariable()).getValues()[i];
				    			int time = j;
						    	series.add(time, value);
							} else {
								value = temporalEvolution.get(probNodes.get(k).getVariable()).getValues()[i];
				    			int time = j;
						    	series.add(time, value);
							}
			    			
					   }
			    	}
			    	
			    }
			    result.addSeries(series);
		    }
		    return result;
		}
	 
	 public JPanel getTablePanel () {
		 if(tablePanel == null){
		  tablePanel = new  TemporalEvolutionTablePanel(temporalEvolution, expandedNetwork, costEffectivenessDialog, variableOfInterest, isUtility/*, isAccumulative*/);
		  //add(tablePanel.getValuesTableScrollPane());
		  //tablePanel.setAutoscrolls(true);
		 }
		 return tablePanel;
	 }
	 
	 private void createExcel() {
		 ExcelReport excel = ExcelReport.getUniqueInstance();
		 excel.createTemporalEvolutionExcelReport(temporalEvolution, expandedNetwork, costEffectivenessDialog, variableOfInterest);
		
	 }
}
