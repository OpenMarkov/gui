
package org.openmarkov.core.gui.dialog.costeffectiveness;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.commons.io.FilenameUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openmarkov.core.exception.ImposedPoliciesException;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.TablePotential;

/**
 * Plot of temporal evolution of variables in CEA
 * @author myebra
 */
@SuppressWarnings("serial")
public class TraceTemporalEvolutionDialog extends JDialog
{
    private HashMap<Variable, TablePotential> temporalEvolution;
    private CostEffectivenessDialog           costEffectivenessDialog;
    private ChartPanel                        chartPanel;
    private TemporalEvolutionTablePanel       tablePanel;
    private JTabbedPane                       tabbedPane;
    private Variable                          variableOfInterest;
    private ProbNet                           expandedNetwork;
    private boolean                           isUtility;
    private boolean                           checkZeroCycle;
    
    private StringDatabase                    stringDatabase = StringDatabase.getUniqueInstance ();

    public TraceTemporalEvolutionDialog(Window owner, ProbNode node) {
    	super (owner);
    	ProbNet probNet = node.getProbNet ();
        this.isUtility = node.getNodeType () == NodeType.UTILITY;
    	costEffectivenessDialog = new CostEffectivenessDialog (owner, probNet, true);

        if (costEffectivenessDialog.requestData () == CostEffectivenessDialog.OK_BUTTON)    	
        {
            this.checkZeroCycle = costEffectivenessDialog.getZeroCycle ();
            int numSlices;
            if (probNet.checkIfThereIsAgeNode ())
            {
                numSlices = costEffectivenessDialog.getFinalAge ()
                            - costEffectivenessDialog.getInitialAge ();
            }
            else
            {
                numSlices = costEffectivenessDialog.getNumSlices ();
            }
            // evidenceCase and cycleLegth null by the moment
            CostEffectivenessAnalysis costEffectivenessAnalysis = new CostEffectivenessAnalysis (
                                                                                                 probNet,
                                                                                                 costEffectivenessDialog.getCostDiscount (),
                                                                                                 costEffectivenessDialog.getEffectivenessDiscount (),
                                                                                                 numSlices,
                                                                                                 costEffectivenessDialog.getInitialAge (),
                                                                                                 costEffectivenessDialog.getNumericTemporalValues (),
                                                                                                 costEffectivenessDialog.getCycleLength (),
                                                                                                 null,
                                                                                                 checkZeroCycle);

            this.variableOfInterest = node.getVariable ();
            try
            {
                this.temporalEvolution = costEffectivenessAnalysis.traceTemporalEvolution (variableOfInterest);
                this.expandedNetwork = costEffectivenessAnalysis.getExpandedNetwork ();
                initialize ();
                Toolkit toolkit = Toolkit.getDefaultToolkit ();
                Dimension screenSize = toolkit.getScreenSize ();
                Rectangle bounds = owner.getBounds ();
                int width = screenSize.width / 2;
                int height = screenSize.height / 2;
                // center point of the owner window
                int x = bounds.x / 2 - width / 2;
                int y = bounds.y / 2 - height / 2;
                this.setBounds (x, y, width, height);
                setMinimumSize (new Dimension (width, height / 2));
                setLocationRelativeTo (owner);
                setResizable (true);
                repaint ();
                pack ();
                setVisible (true);
            }
            catch (ImposedPoliciesException e)
            {
                JOptionPane.showMessageDialog (owner,
                                               e.getMessage (),
                                               "Error",
                                               JOptionPane.ERROR_MESSAGE);
            }
                
        }
    	
	}

	private void initialize ()
    {
        setTitle (stringDatabase.getString ("TemporalEvolutionResultDialog.Title.Label") + " "
                  + variableOfInterest.getBaseName ());
        setContentPane (getJContentPane ());
        pack ();
    }

    /**
     * This method initialises jContentPane.
     * @return a new content panel.
     */
    private JPanel getJContentPane ()
    {
        JPanel jContentPane = new JPanel ();
        jContentPane.setLayout (new BorderLayout ());
        jContentPane.add (getComponentsPanel (), BorderLayout.CENTER);
        jContentPane.add (getBottomPanel (), BorderLayout.SOUTH);
        return jContentPane;
    }
    
    private JPanel getBottomPanel ()
    {
        JPanel buttonsPanel = new JPanel ();
        JButton jButtonSaveReport = new JButton ();
        jButtonSaveReport.setName ("jButtonSaveReport");
        jButtonSaveReport.setText (stringDatabase.getString ("Dialog.SaveReport.Label"));
        jButtonSaveReport.addActionListener (new ActionListener ()
            {
                public void actionPerformed (ActionEvent e)
                {
                    saveReport();
                }
            });
        buttonsPanel.add (jButtonSaveReport);
        JButton jButtonClose = new JButton ();
        jButtonClose.setName ("jButtonClose");
        jButtonClose.setText (stringDatabase.getString ("Dialog.Close.Label"));
        jButtonClose.addActionListener (new ActionListener ()
            {
                public void actionPerformed (ActionEvent e)
                {
                    setVisible (false);
                    dispose ();
                }
            });
        buttonsPanel.add (jButtonClose);
        return buttonsPanel;
    }
    private Component getComponentsPanel ()
    {
        JPanel panel = new JPanel ();
        panel.setLayout (new BorderLayout (5, 5));
        panel.setMaximumSize (new Dimension (180, 40));
        panel.add (getTabbedPane ());
        pack ();
        return panel;
    }    

    /**
     * This method initialises tabbedPane.
     * @return a new tabbed pane.
     */
    protected JTabbedPane getTabbedPane ()
    {
        if (tabbedPane == null)
        {
            tabbedPane = new JTabbedPane ();
            tabbedPane.setName ("TraceTemporalEvolutionTabbedPane");
            tabbedPane.addTab (stringDatabase.getString ("TemporalEvolutionChart.Title.Label"),
                               null, getChartsPanel (), null);
            tabbedPane.addTab (stringDatabase.getString ("TemporalEvolutionTable.Title.Label"),
                               null,
                               ((TemporalEvolutionTablePanel) getTablePanel ()).getValuesTableScrollPane (),
                               null);
        }
        return tabbedPane;
    }

    private ChartPanel getChartsPanel ()
    {
        if (chartPanel == null)
        {
            XYDataset dataset = createDataset ();
            JFreeChart chart = ChartFactory.createXYLineChart ("Temporal Evolution of: "
                                                                       + variableOfInterest.getBaseName (),
                                                               "t", "value", dataset,
                                                               PlotOrientation.VERTICAL, true,
                                                               true, true);
            // chart.getXYPlot().setRenderer(new XYSplineRenderer());
            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer ();
            for (int i = 0; i < dataset.getSeriesCount (); i++)
            {
                renderer.setSeriesLinesVisible (i, true);
                renderer.setSeriesShapesVisible (i, true);
            }
            chart.getXYPlot ().setRenderer (renderer);
            // chart.getXYPlot().setRenderer(new XYSplineRenderer());
            chartPanel = new ChartPanel (chart);
            chartPanel.setAutoscrolls (true);
            chartPanel.setDisplayToolTips (true);
            chartPanel.setMouseZoomable (true);
            // XYPlot plot = (XYPlot) chart.getPlot ();
            // XYItemRenderer renderer = plot.getRenderer();
            XYToolTipGenerator generator = new StandardXYToolTipGenerator (
                                                                           "{0}: ({1}, {2})",
                                                                           new DecimalFormat (
                                                                                              "0.00"),
                                                                           new DecimalFormat (
                                                                                              "0.00"));
            renderer.setBaseToolTipGenerator (generator);
        }
        return chartPanel;
    }

    private XYDataset createDataset ()
    {
        XYSeriesCollection result = new XYSeriesCollection ();
        double value = 0.0;
        // int numSlices = (checkZeroCycle) ?
        // costEffectivenessDialog.getNumSlices() :
        // costEffectivenessDialog.getNumSlices() -1;
        for (int i = 0; i < variableOfInterest.getNumStates (); i++)
        {
            XYSeries series = null;
            if (isUtility)
            {
                series = new XYSeries (variableOfInterest.getBaseName ());
            }
            else
            {
                series = new XYSeries (variableOfInterest.getStateName (i));
            }
            for (int j = 0; j < costEffectivenessDialog.getNumSlices (); j++)
            {
                String basename = variableOfInterest.getBaseName ();
                List<ProbNode> probNodes = expandedNetwork.getProbNodes ();
                for (int k = 0; k < probNodes.size (); k++)
                {
                    if (probNodes.get (k).getVariable ().getBaseName ().equals (basename)
                        && probNodes.get (k).getVariable ().getTimeSlice () == j)
                    {
                        if (isUtility && costEffectivenessDialog.isAccumulative ())
                        {
                            value += temporalEvolution.get (probNodes.get (k).getVariable ()).getValues ()[i];
                            int time = j;
                            series.add (time, value);
                        }
                        else
                        {
                            value = temporalEvolution.get (probNodes.get (k).getVariable ()).getValues ()[i];
                            int time = j;
                            series.add (time, value);
                        }
                    }
                }
            }
            result.addSeries (series);
        }
        return result;
    }

    public JPanel getTablePanel ()
    {
        if (tablePanel == null)
        {
            tablePanel = new TemporalEvolutionTablePanel (temporalEvolution, expandedNetwork,
                                                          costEffectivenessDialog,
                                                          variableOfInterest, isUtility/*
                                                                                        * ,
                                                                                        * isAccumulative
                                                                                        */);
            // add(tablePanel.getValuesTableScrollPane());
            // tablePanel.setAutoscrolls(true);
        }
        return tablePanel;
    }
    
    /**
     * Returns the checkZeroCycle.
     * @return the checkZeroCycle.
     */
    public boolean isCheckZeroCycle ()
    {
        return checkZeroCycle;
    }    

    private void saveReport()
    {
        JFileChooser fileChooser = new JFileChooser ();
        String netName = FilenameUtils.getBaseName (expandedNetwork.getName ());
        fileChooser.setSelectedFile(new File(netName +"-"+ variableOfInterest.getBaseName () +"-temporalEvolution.xls"));        
        if(fileChooser.showSaveDialog (this) == JFileChooser.APPROVE_OPTION)
        {
            String filename = fileChooser.getSelectedFile ().getAbsolutePath ();
            try
            {
                createExcel (filename);
            }
            catch (IOException e)
            {
                JOptionPane.showMessageDialog (this, "Error when trying to generate report in " + filename);
            }
        }
    }
    
    private void createExcel (String filename) throws IOException
    {
        ExcelReport excel = ExcelReport.getUniqueInstance ();
        excel.createTemporalEvolutionExcelReport (filename,
                                                  temporalEvolution, expandedNetwork,
                                                  costEffectivenessDialog.getNumSlices (), 
                                                  variableOfInterest);
    }
}
