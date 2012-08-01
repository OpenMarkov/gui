package org.openmarkov.core.gui.dialog.costeffectiveness;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.util.HashMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openmarkov.core.gui.dialog.common.OkCancelApplyUndoRedoHorizontalDialog;
import org.openmarkov.core.gui.dialog.common.ProbabilityTablePanel;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.TablePotential;

@SuppressWarnings("serial")
public class TraceTemporalEvolutionDialog  extends OkCancelApplyUndoRedoHorizontalDialog {
	
	private static HashMap<Variable,TablePotential> temporalEvolution;
	private CostEffectivenessDialog costEffectivenessDialog;
	private StringResource dialogStringResource;
	private StringResource messageStringResource;
	private ChartPanel chartPanel;

	public TraceTemporalEvolutionDialog (Window owner, HashMap<Variable,TablePotential> temporalEvolution, CostEffectivenessDialog costEffectivenessDialog) {
		super(owner);
		this.temporalEvolution = temporalEvolution;
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
	        getComponentsPanel().add(getChartsPanel());
	        pack();
	   }
	 private ChartPanel getChartsPanel () {
		 if(chartPanel == null){
			 XYDataset dataset = createDataset();
			 JFreeChart chart = ChartFactory.createScatterPlot("Temporal Evolution Result", "t", "value", dataset, PlotOrientation.VERTICAL, true, true, false);
			 chartPanel = new ChartPanel(chart);
	        }
	        return chartPanel;
	 } 
	 
	 private static XYDataset createDataset() {
		    XYSeriesCollection result = new XYSeriesCollection();
		    for (int i = 0; i < temporalEvolution.size(); i++) {
			    XYSeries series = new XYSeries("");
			   // Object data [][] = ((ProbabilityTablePanel)getPotentialPanel()).getData();
			    
			    //para cada estado tienes que formarte la serie entera
			   /* for (int j = 0; j < data[1].length; j++) {
			        double time = (Double) data[data.length-1][i];
			        double value = (Double) data[data.length][i];
			        series.add(time, value);
			    }
			    result.addSeries(series);*/
		    }
		    return result;
		}
}
