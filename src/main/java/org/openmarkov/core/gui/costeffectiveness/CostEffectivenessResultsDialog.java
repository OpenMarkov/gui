
package org.openmarkov.core.gui.costeffectiveness;

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
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.apache.commons.io.FilenameUtils;
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
import org.openmarkov.core.gui.localize.StringDatabase;

/**
 * Dialog box to show the results from cost-effectiveness analysis
 * @author myebra
 */
@SuppressWarnings("serial")
public class CostEffectivenessResultsDialog extends JDialog
{
    private CostEffectivenessAnalysis    costEffectivenessAnalysis;
    private CostEffectivenessSummaryPane summaryPane;
    private ChartPanel                   chartPanel;
    private JScrollPane                  frontierInterventionsTablePanel;
    private JTabbedPane                  tabbedPane;
    private StringDatabase               stringDatabase = StringDatabase.getUniqueInstance ();

    public CostEffectivenessResultsDialog (Window owner,
                                           CostEffectivenessAnalysis costeffectivenessAnalysis)
    {
        super (owner);
        this.costEffectivenessAnalysis = costeffectivenessAnalysis;
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
        setLocationRelativeTo (owner);
        setMinimumSize (new Dimension (width, height / 2));
        setResizable (true);
        repaint ();
        pack ();
    }

    private void initialize ()
    {
        setTitle (stringDatabase.getString ("CostEffectivenessResultDialog.Title.Label"));
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
                    saveReport ();
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
            tabbedPane.setName ("CostEffectivenessResultTabbedPane");
            tabbedPane.addTab (stringDatabase.getString ("AllInterventionsChart.Title.Label"),
                               null, getChartsPanel (), null);
            tabbedPane.addTab (stringDatabase.getString ("AllInterventionsTable.Title.Label"),
                               null, getSummaryPane (), null);
            tabbedPane.addTab (stringDatabase.getString ("FrontierInterventions.Title.Label"),
                               null, getFrontierInterventionsPanel (), null);
        }
        return tabbedPane;
    }

    private CostEffectivenessSummaryPane getSummaryPane ()
    {
        if (summaryPane == null)
        {
            summaryPane = new CostEffectivenessSummaryPane (this.costEffectivenessAnalysis.getGlobalUtility ());
        }
        return summaryPane;
    }

    private ChartPanel getChartsPanel ()
    {
        if (chartPanel == null)
        {
            XYDataset dataset = createDataset ();
            JFreeChart chart = ChartFactory.createScatterPlot ("Cost-Effectiveness Analysis Result",
                                                               "effectiveness", "cost", dataset,
                                                               PlotOrientation.VERTICAL, true,
                                                               true, true);
            // chart.getXYPlot().setRenderer(new XYSplineRenderer());
            chartPanel = new ChartPanel (chart);
            chartPanel.setAutoscrolls (true);
            chartPanel.setDisplayToolTips (true);
            chartPanel.setMouseZoomable (true);
            XYPlot plot = (XYPlot) chart.getPlot ();
            XYItemRenderer renderer = plot.getRenderer ();
            XYToolTipGenerator generator = new StandardXYToolTipGenerator (
                                                                           "{0}: ({1}, {2})",
                                                                           new DecimalFormat (
                                                                                              "0.00",
                                                                                              new DecimalFormatSymbols (
                                                                                                                        Locale.US)),
                                                                           new DecimalFormat (
                                                                                              "0.00",
                                                                                              new DecimalFormatSymbols (
                                                                                                                        Locale.US)));
            renderer.setBaseToolTipGenerator (generator);
        }
        return chartPanel;
    }

    private XYDataset createDataset ()
    {
        XYSeriesCollection result = new XYSeriesCollection ();
        for (Intervention intervention : costEffectivenessAnalysis.getInterventions())
        {
            XYSeries series = new XYSeries (intervention.getName ());
            series.add (intervention.getEffectiveness (), intervention.getCost ());
            if(intervention instanceof ProbabilisticIntervention)
            {
                ProbabilisticIntervention pIntervention = (ProbabilisticIntervention)intervention;  
                for(int i=0; i < pIntervention.getNumSimulations(); ++i)
                {
                    series.add(pIntervention.getEffectivenesses().get(i), pIntervention.getCosts()
                            .get(i));
                }
            }
            result.addSeries (series);
        }
        return result;
    }

    private JScrollPane getFrontierInterventionsPanel ()
    {
        if (frontierInterventionsTablePanel == null)
        {
            frontierInterventionsTablePanel = new FrontierInterventionsTablePanel (
                                                                                   costEffectivenessAnalysis);
        }
        return frontierInterventionsTablePanel;
    }

    private void saveReport ()
    {
        JFileChooser fileChooser = new JFileChooser ();
        String netName = FilenameUtils.getBaseName (costEffectivenessAnalysis.getProbNet ().getName ());
        fileChooser.setSelectedFile (new File (netName + "-cea.xls"));
        if (fileChooser.showSaveDialog (this) == JFileChooser.APPROVE_OPTION)
        {
            String filename = fileChooser.getSelectedFile ().getAbsolutePath ();
            try
            {
                createExcel (filename);
            }
            catch (IOException e)
            {
                JOptionPane.showMessageDialog (this, "Error when trying to generate report in "
                                                     + filename);
            }
        }
    }

    private void createExcel (String filename)
        throws IOException
    {
        ExcelReport excel = new ExcelReport (costEffectivenessAnalysis);
        excel.writeOptimalInterventionsReport (filename);
    }
}
