//package org.openmarkov.core.gui.dialog.inference.costeffectiveness;
//
//import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartPanel;
//import org.jfree.chart.JFreeChart;
//import org.jfree.chart.labels.StandardXYToolTipGenerator;
//import org.jfree.chart.labels.XYToolTipGenerator;
//import org.jfree.chart.plot.PlotOrientation;
//import org.jfree.chart.plot.XYPlot;
//import org.jfree.chart.renderer.xy.XYItemRenderer;
//import org.jfree.data.xy.XYSeries;
//import org.jfree.data.xy.XYSeriesCollection;
//import org.jfree.ui.RectangleEdge;
//import org.openmarkov.core.exception.IncompatibleEvidenceException;
//import org.openmarkov.core.exception.NotEvaluableNetworkException;
//import org.openmarkov.core.exception.UnexpectedInferenceException;
//import org.openmarkov.core.gui.loader.element.OpenMarkovLogoIcon;
//import org.openmarkov.core.gui.localize.StringDatabase;
//import org.openmarkov.core.model.network.EvidenceCase;
//import org.openmarkov.core.model.network.ProbNet;
//import org.openmarkov.core.model.network.State;
//import org.openmarkov.core.model.network.Variable;
//import org.openmarkov.core.model.network.potential.GTablePotential;
//import org.openmarkov.core.model.network.potential.Intervention;
//import org.openmarkov.inference.tasks.VariableElimination.VECEADecision;
//import org.openmarkov.core.model.network.CEP;
//
//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import javax.swing.border.TitledBorder;
//import javax.swing.table.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.text.DecimalFormat;
//import java.text.DecimalFormatSymbols;
//import java.text.NumberFormat;
//import java.util.*;
//import java.awt.*;
//import java.util.List;
//
//
///**
// * @author jperez-martin
// */
//public class CEDecisionResults extends JDialog {
//
//    /**
//     * ProbNet
//     */
//    private ProbNet probNet;
//
//    /**
//     * Cost-effectiveness task (conditioned on a DecisionVariable)
//     */
//    private VECEADecision veceaDecision;
//
//    /**
//     * Conditioning decision variable
//     */
//    private Variable decisionVariable;
//
//    /**
//     * Tabbed pane
//     */
//    private JTabbedPane tabbedPane;
//
//    /**
//     * Localized stringDatabase
//     */
//    private StringDatabase stringDatabase = StringDatabase.getUniqueInstance();
//
//    /**
//     * Selected minimal threshold of the cost-effectiveness partition
//     */
//    private double selectedMinThreshold;
//
//    /**
//     * Selected maximal threshold of the cost-effectiveness partition
//     */
//    private double selectedMaxThreshold;
//
//    /**
//     * Resulting cepsForDecision (one per decision state)
//     */
//    private CEP[] cepsForDecision;
//
//    /**
//     * Resulting GTablePotential
//     */
//    private GTablePotential gtablePotentialResult;
//
//    /**
//     * Compacted? Thresholds? TODO - Check
//     */
//    private List<Double> thresholdList;
//
//    /**
//     * GUI controls
//     */
//    private JRadioButton absoluteRadioButton;
//    private JRadioButton relativeRadioButton;
//    private JComboBox<State> relativeDecisionSelector;
//    private List<JCheckBox> showHideCheckBoxes;
//    private ChartPanel ceChartPanel;
//    private JPanel cePlanePanel;
//    private List<JRadioButton> analysisThresholdsRadioButtons;
//    private List<JRadioButton> cePlanethresholdsRadioButtons;
//    private JPanel tablePanel;
//    private JPanel analysisPanel;
//    private final int COLUMN_STATE_NAME = 0;
//    private final int COLUMN_COST = 1;
//    private final int COLUMN_EFFECTIVENESS = 2;
//    private final int COLUMN_INTERVENTION = 3;
//    private final String CLICKABLE_COLUMN_COLOR ="#DDF5D8";
//    private boolean hasInterventions;
//
//    private enum AnalysisTab {
//        ANALYSIS,
//        CEPLANE
//    }
//
//    public CEDecisionResults(Window owner, ProbNet probNet, EvidenceCase evidenceCase, Variable decisionVariable){
//        super(owner);
//        this.probNet = probNet;
//        this.decisionVariable = decisionVariable;
//
//        // Run the task
//        try {
//            veceaDecision = new VECEADecision(probNet, evidenceCase, decisionVariable);
//            gtablePotentialResult = veceaDecision.getCEPPotential();
//
//            hasInterventions = false;
//            for (Object cep : veceaDecision.getCEPPotential().elementTable) {
//                Intervention [] interventions = ((CEP) cep).getInterventions();
//                if (interventions != null && interventions.length != 0 && interventions[0] != null) {
//                    hasInterventions = true;
//                    break;
//                }
//            }
//
//        } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
//            e.printStackTrace();
//        }
//        initialize();
//
//        Toolkit toolkit = Toolkit.getDefaultToolkit ();
//        Dimension screenSize = toolkit.getScreenSize ();
//        Rectangle bounds = owner.getBounds ();
//        int width = screenSize.width / 2;
//        int height = screenSize.height / 2;
//        // center point of the owner window
//        int x = bounds.x / 2 - width / 2;
//        int y = bounds.y / 2 - height / 2;
//        this.setBounds (x, y, width, height);
//        setLocationRelativeTo (owner);
//        setMinimumSize (new Dimension (width, height / 2));
//        setResizable (true);
//        repaint ();
//        pack ();
//        this.setVisible(true);
//    }
//
//    /**
//     * Set title, icon and contentPane
//     */
//    private void initialize() {
//        // TODO - Localize
//        this.setTitle("OpenMarkov - " + StringDatabase.getUniqueInstance().getString("SensitivityAnalysis.Title") + " - " + probNet.getName());
//        this.setIconImage(OpenMarkovLogoIcon.getUniqueInstance().getOpenMarkovLogoIconImage16());
//        setContentPane(getJContentPane());
//        pack();
//    }
//
//    /**
//     * Gets the content pane with the tabbed pane
//     * @return content pane
//     */
//    private JPanel getJContentPane() {
//        JPanel jContentPane = new JPanel ();
//        jContentPane.setLayout (new BorderLayout ());
//        jContentPane.add (getTabbedPane (), BorderLayout.CENTER);
//        return jContentPane;
//    }
//
//    /**
//     * Gets the tabbed pane with tornado and spider panels
//     * @return tabbed pane
//     */
//    private JTabbedPane getTabbedPane() {
//        if (tabbedPane == null) {
//            tabbedPane = new JTabbedPane();
//            // TODO - LOCALIZE
//            tabbedPane.addTab("ANALISIS!!!", null, getAnalysisPanel(), null);
//            tabbedPane.addTab("PLANO_CE",null, getCEPlane(), null);
//        }
//        return tabbedPane;
//    }
//
//    /**
//     * Gets a JPanel with the cost-effectiveness results
//     * @return cost-effectiveness results
//     */
//    private JPanel getAnalysisPanel() {
//        analysisPanel = new JPanel();
//        analysisPanel.setLayout(new BorderLayout());
//        analysisPanel.add(getIntervalsPanel(AnalysisTab.ANALYSIS), BorderLayout.WEST);
//        analysisPanel.add(getTablePanel(), BorderLayout.CENTER);
//        return analysisPanel;
//    }
//
//    /**
//     * Gets the cost-effectiveness plane
//     * @return
//     */
//    public JPanel getCEPlane() {
//        cePlanePanel = new JPanel();
//        cePlanePanel.setLayout(new BorderLayout());
//        cePlanePanel.add(getIntervalsPanel(AnalysisTab.CEPLANE), BorderLayout.WEST);
//        cePlanePanel.add(getAbsRelShowHidePanel(), BorderLayout.EAST);
//        cePlanePanel.add(getCEPlaneChartPanel(), BorderLayout.CENTER);
//
//        return cePlanePanel;
//    }
//
//    /**
//     * Get the intervals panel with all the compact intervals
//     * @return
//     */
//    public JScrollPane getIntervalsPanel(AnalysisTab analysisTab) {
//
//        JPanel intervalsPanel = new JPanel();
//
//
//        cepsForDecision = new CEP[gtablePotentialResult.elementTable.size()];
//
//        boolean moreThanOneInterval = false;
//
//        LinkedHashSet<Double> thresholds = new LinkedHashSet<>();
//        for (int i = 0; i < gtablePotentialResult.elementTable.size(); i++) {
//            CEP cep = (CEP) gtablePotentialResult.elementTable.get(i);
//            cepsForDecision[i] = cep;
//            if (cep.getNumIntervals() != 1) {
//                moreThanOneInterval = true;
//                for (double threshold : cep.getThresholds()) {
//                    thresholds.add(threshold);
//                }
//            }
//        }
//
//        // If there are more than one interval, is necessary get the compact intervals and paint it into the panel
//        if (moreThanOneInterval) {
//            // TODO - Localize
//            intervalsPanel.setBorder(new TitledBorder("Intervals"));
//            intervalsPanel.setLayout(new BoxLayout(intervalsPanel, BoxLayout.PAGE_AXIS));
//
//            thresholdList = new ArrayList<>(thresholds);
//            Collections.sort(thresholdList);
//            List<JRadioButton> thresholdsRadioButtons = new ArrayList<>();
//            ButtonGroup buttonGroup = new ButtonGroup();
//            double lowerBound = 0;
//            for (double threshold : thresholdList) {
//                JRadioButton intervalRadioButton = new JRadioButton(lowerBound + " - " + threshold);
//                buttonGroup.add(intervalRadioButton);
//                thresholdsRadioButtons.add(intervalRadioButton);
//                intervalsPanel.add(intervalRadioButton);
//                intervalRadioButton.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        thresholdChanged();
//                    }
//                });
//                lowerBound = threshold;
//            }
//            JRadioButton intervalRadioButton = new JRadioButton(lowerBound + " - " + Double.POSITIVE_INFINITY);
//            buttonGroup.add(intervalRadioButton);
//            intervalRadioButton.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    thresholdChanged();
//                }
//            });
//            thresholdsRadioButtons.add(intervalRadioButton);
//            intervalsPanel.add(intervalRadioButton);
//
//            thresholdsRadioButtons.get(0).setSelected(true);
//
//            // If the selected tab is "Analysis" are the radio buttons for analysis
//            if(analysisTab == AnalysisTab.ANALYSIS){
//                analysisThresholdsRadioButtons = thresholdsRadioButtons;
//            } else if ( analysisTab == AnalysisTab.CEPLANE){
//                cePlanethresholdsRadioButtons = thresholdsRadioButtons;
//            }
//
//        } else {
//            selectedMinThreshold = 0;
//            selectedMaxThreshold = Double.POSITIVE_INFINITY;
//        }
//
//        JScrollPane scrollPane = new JScrollPane(intervalsPanel);
//        scrollPane.setBorder(new EmptyBorder(2,2,2,2));
//        return scrollPane;
//    }
//
//    /**
//     * Get the Panel with the JTable
//     * @return
//     */
//    public JPanel getTablePanel() {
//        tablePanel = new JPanel();
//        tablePanel.setName("tablePanel");
//        tablePanel.add(new JScrollPane(getTable()));
//        return tablePanel;
//    }
//
//    /**
//     * Get the table with the CEPs
//     * @return
//     */
//    public JTable getTable(){
//        // Add one for the header
//        int numRows = decisionVariable.getNumStates();
//        int numColumns = getColumns().length;
//        // Set data in jTable
//        Object[][] values = new Object[numRows][numColumns];
//
////        //Set header values
////        for (int column = 0; column < numColumns; column++){
////            values[0][column] = getColumns()[column];
////        }
//
//        for (int row = 0; row < numRows; row++){
//            // Set decision variable state name
//            values[row][COLUMN_STATE_NAME] = decisionVariable.getStateName(row);
//
//            // Set costs and effectiveness for that decision state
//            values[row][COLUMN_COST] = cepsForDecision[row].getCost(selectedMinThreshold + (selectedMaxThreshold-selectedMinThreshold)/2);
//            values[row][COLUMN_EFFECTIVENESS] = cepsForDecision[row].getEffectiveness(selectedMinThreshold + (selectedMaxThreshold-selectedMinThreshold)/2);
//            if (hasInterventions) {
//                values[row][COLUMN_INTERVENTION] = cepsForDecision[row].getIntervention(selectedMinThreshold + (selectedMaxThreshold - selectedMinThreshold) / 2);
//            }
//        }
//
//        final JTable jtable = new JTable(values, getColumns()) {
//            @Override
//            public void doLayout()
//            {
//                if (tableHeader != null)
//                {
//                    TableColumn resizingColumn = tableHeader.getResizingColumn();
//                    //  Viewport size changed. Increase last columns width
//
//                    if (resizingColumn == null)
//                    {
//                        TableColumnModel tcm = getColumnModel();
//                        int lastColumn = tcm.getColumnCount() - 1;
//                        tableHeader.setResizingColumn( tcm.getColumn( lastColumn ) ) ;
//                    }
//                }
//
//                super.doLayout();
//            }
//
//            public boolean getScrollableTracksViewportWidth()
//            {
//                return getPreferredSize().width < getParent().getWidth();
//            }
//        };
//
//        jtable.addMouseListener(new MouseAdapter() {
//            public void mouseClicked(MouseEvent event) {
//                int row = jtable.rowAtPoint(event.getPoint());
//                int column = jtable.columnAtPoint(event.getPoint());
//                if (column == COLUMN_INTERVENTION) {
//                    Intervention intervention = cepsForDecision[row].getIntervention(
//                            selectedMinThreshold + (selectedMaxThreshold - selectedMinThreshold) / 2);
//
//                    if (intervention != null) {
//                        InterventionDialog interventionDialog = null;
//                        try {
//                            interventionDialog = new InterventionDialog(getOwner(),
//                                    probNet,
//                                    intervention);
//                        } catch (IncompatibleEvidenceException e) {
//                            e.printStackTrace();
//                        } catch (UnexpectedInferenceException e) {
//                            e.printStackTrace();
//                        }
//                        interventionDialog.setVisible(true);
//                    }
//                }
//            }
//        });
//
////        DefaultTableModel tableModel = new DefaultTableModel() {
////
////            @Override
////            public boolean isCellEditable(int row, int column) {
////                return false;
////            }
////        };
////
////        jtable.setModel(tableModel);
//
//        DefaultCellEditor notEditableCellEditor = new DefaultCellEditor(new JTextField()){
//            @Override
//            public boolean isCellEditable(EventObject anEvent) {
//                return false;
//            }
//        };
//
//        for (int columnIndex = 0 ; columnIndex < jtable.getColumnModel().getColumnCount(); columnIndex++){
//            jtable.getColumnModel().getColumn(columnIndex).setCellEditor(notEditableCellEditor);
//        }
//
//        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
//        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
//        jtable.getTableHeader().setDefaultRenderer(headerRenderer);
//        // Set colors in jTable
//        DefaultTableCellRenderer renderer =	new DefaultTableCellRenderer();
//        renderer.setBackground(Color.decode(CLICKABLE_COLUMN_COLOR));
//
//        if (hasInterventions) {
//            jtable.getColumnModel().getColumn(COLUMN_INTERVENTION).setCellRenderer(renderer);
//        }
//
//
//        return jtable;
//    }
//
//    /**
//     * Get column names
//     * @return
//     */
//    public String[] getColumns() {
//        String[] columnNames;
//        // TODO - Localize
//        if (hasInterventions) {
//            columnNames = new String[4];
//            columnNames[0] =  decisionVariable.getBaseName();
//            columnNames[1] = "cost";
//            columnNames[2] = "effectiveness";
//            columnNames[3] = "intervention";
//        } else {
//            columnNames = new String[3];
//            columnNames[0] =  decisionVariable.getBaseName();
//            columnNames[1] = "cost";
//            columnNames[2] = "effectiveness";
//        }
//
//        return columnNames;
//    }
//
//    /**
//     * Build the right column with both panels
//     * @return
//     */
//    public JPanel getAbsRelShowHidePanel() {
//        JPanel absRelShowHidePanel = new JPanel();
//        absRelShowHidePanel.setLayout(new BorderLayout());
//        absRelShowHidePanel.add(getAbsoluteRelativePanel(), BorderLayout.NORTH);
//        absRelShowHidePanel.add(getShowHidePanel(),BorderLayout.SOUTH);
//
//        return absRelShowHidePanel;
//    }
//
//    /**
//     * Returns the scroll pane with the absolute/relative functionality
//     * @return
//     */
//    public JPanel getAbsoluteRelativePanel(){
//        JPanel absoluteRelativePanel = new JPanel();
//        // TODO - LOCALIZE
//        absoluteRelativePanel.setBorder(new TitledBorder("Display:"));
//        absoluteRelativePanel.setLayout(new BoxLayout(absoluteRelativePanel, BoxLayout.PAGE_AXIS));
//
//        ButtonGroup buttonGroup = new ButtonGroup();
//        absoluteRadioButton = new JRadioButton("Absolute");
//        relativeRadioButton = new JRadioButton("Relative to:");
//        absoluteRadioButton.setSelected(true);
//        absoluteRadioButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                relativeDecisionSelector.setEnabled(false);
//                refreshChartPanel();
//            }
//        });
//
//        relativeRadioButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                relativeDecisionSelector.setEnabled(true);
//                refreshChartPanel();
//            }
//        });
//
//        JPanel absoluteRadioButtonPanel = new JPanel();
//        absoluteRadioButtonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
//        absoluteRadioButtonPanel.add(absoluteRadioButton);
//        absoluteRelativePanel.add(absoluteRadioButtonPanel);
//
//        JPanel relativeRadioButtonPanel = new JPanel();
//        relativeRadioButtonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
//        relativeRadioButtonPanel.add(relativeRadioButton);
//        absoluteRelativePanel.add(relativeRadioButtonPanel);
//
//        buttonGroup.add(absoluteRadioButton);
//        buttonGroup.add(relativeRadioButton);
//
//        relativeDecisionSelector = new JComboBox<>();
//        for(State state :decisionVariable.getStates()){
//            relativeDecisionSelector.addItem(state);
//        }
//        relativeDecisionSelector.setEnabled(false);
//        relativeDecisionSelector.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                refreshChartPanel();
//            }
//        });
//
//        absoluteRelativePanel.add(relativeDecisionSelector);
//        absoluteRelativePanel.add(new JPanel());
//
//        return absoluteRelativePanel;
//    }
//
//    /**
//     * Returns the scroll pane with the show/hide functionality
//     * @return
//     */
//    public JScrollPane getShowHidePanel(){
//        JPanel showHidePanel = new JPanel();
//        // TODO - LOCALIZE
//        showHidePanel.setBorder(new TitledBorder("Show/hide interventions"));
//        showHidePanel.setLayout(new BoxLayout(showHidePanel, BoxLayout.PAGE_AXIS));
//
//        showHideCheckBoxes = new ArrayList();
//        for(State state :decisionVariable.getStates()){
//            JCheckBox stateCheckbox = new JCheckBox(state.getName());
//            stateCheckbox.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    refreshChartPanel();
//                }
//            });
//            stateCheckbox.setSelected(true);
//            showHideCheckBoxes.add(stateCheckbox);
//            showHidePanel.add(stateCheckbox);
//        }
//
//        JScrollPane scrollPane = new JScrollPane(showHidePanel);
//        scrollPane.setBorder(new EmptyBorder(2,2,2,2));
//        return scrollPane;
//    }
//
//    /**
//     * Action performed when a threshold has changed
//     */
//    private void thresholdChanged() {
//        List<JRadioButton> currentTabRadioButtons;
//        List<JRadioButton> otherTabRadioButtons;
//
//        // If the selected tab is "Analysis", the selected radio button is from analysis
//        if(tabbedPane.getSelectedIndex() == 0){
//            currentTabRadioButtons = analysisThresholdsRadioButtons;
//            otherTabRadioButtons = cePlanethresholdsRadioButtons;
//        } else {
//            currentTabRadioButtons = cePlanethresholdsRadioButtons;
//            otherTabRadioButtons = analysisThresholdsRadioButtons;
//        }
//
//        for(int i = 0; i < currentTabRadioButtons.size(); i++){
//            if(currentTabRadioButtons.get(i).isSelected()){
//                // Update the selected threshold
//                if(i == 0){
//                    this.selectedMinThreshold = 0;
//                    this.selectedMaxThreshold = thresholdList.get(0);
//                } else if (i == currentTabRadioButtons.size() - 1){
//                    this.selectedMinThreshold = thresholdList.get(i-1);
//                    this.selectedMaxThreshold = Double.POSITIVE_INFINITY;
//                } else {
//                    this.selectedMinThreshold = thresholdList.get(i-1);
//                    this.selectedMaxThreshold = thresholdList.get(i);
//                }
//
//                // Mark as selected the same thresholds of the other tab
//                otherTabRadioButtons.get(i).setSelected(true);
//
//                break;
//            }
//        }
//
//        refreshChartPanel();
//        refreshTablePanel();
//    }
//
//    /**
//     * Repaint and refresh the chart panel and its components
//     */
//    private void refreshChartPanel() {
//        this.setVisible(false);
//        cePlanePanel.remove(ceChartPanel);
//        cePlanePanel.add(getCEPlaneChartPanel(), BorderLayout.CENTER);
//        this.setVisible(true);
//    }
//
//    /**
//     * Repaint and refresh the tablePanel and its components
//     */
//    private void refreshTablePanel(){
//        this.setVisible(false);
//        analysisPanel.remove(tablePanel);
//        analysisPanel.add(getTablePanel(), BorderLayout.CENTER);
//        this.setVisible(true);
//    }
//
//    /**
//     * Get cost-effectiveness plane chart
//     * @return
//     */
//    public ChartPanel getCEPlaneChartPanel() {
//        // JFreeChart attributes definition
//        XYSeriesCollection dataset = new XYSeriesCollection();
//
//        double baseCost;
//        double baseEffectiveness;
//
//        // Relative
//        if(relativeRadioButton.isSelected()){
//            int indexSelected = relativeDecisionSelector.getSelectedIndex();
//            baseCost = cepsForDecision[indexSelected].getCost(selectedMinThreshold + (selectedMaxThreshold-selectedMinThreshold)/2);;
//            baseEffectiveness = cepsForDecision[indexSelected].getEffectiveness(selectedMinThreshold + (selectedMaxThreshold-selectedMinThreshold)/2);;
//
//            // Absolute
//        } else {
//            baseCost = 0;
//            baseEffectiveness = 0;
//        }
//
//        for(int cepIndex = 0 ; cepIndex < cepsForDecision.length; cepIndex++){
//
//            // If the serie is hidden, skip it from JFreeChart
//            if(!showHideCheckBoxes.get(cepIndex).isSelected()){
//                continue;
//            }
//            XYSeries series = new XYSeries(decisionVariable.getStateName(cepIndex));
//
//            double cost = cepsForDecision[cepIndex].getCost(selectedMinThreshold + (selectedMaxThreshold-selectedMinThreshold)/2);
//            cost -= baseCost;
//
//            double effectiveness = cepsForDecision[cepIndex].getEffectiveness(selectedMinThreshold + (selectedMaxThreshold-selectedMinThreshold)/2);
//            effectiveness -= baseEffectiveness;
//
//            series.add(effectiveness,cost);
//
//            dataset.addSeries(series);
//        }
//
//        // Set the JFreeChart parameters call
//        // TODO - LOCALIZE
//        JFreeChart chart = ChartFactory.createScatterPlot(
//                "Cost-effectiveness plane", // Chart title
//                "Effectiveness",                                                    // X axis label
//                "Cost",                                               // Y axis label
//                dataset,                                            // data
//                PlotOrientation.VERTICAL,                            // Orientation
//                true,                                                // Legend
//                true,                                                // ToolTips
//                false                                                // Urls
//        );
//
//        // Set general aspects
//        chart.getLegend().setPosition(RectangleEdge.RIGHT);
//
//        ceChartPanel = new ChartPanel (chart);
//        ceChartPanel.setAutoscrolls (true);
//        ceChartPanel.setDisplayToolTips (true);
//        ceChartPanel.setMouseZoomable (true);
//        XYPlot plot = (XYPlot) chart.getPlot ();
//        XYItemRenderer renderer = plot.getRenderer ();
//        NumberFormat format = new DecimalFormat("0.00",new DecimalFormatSymbols(Locale.US));
//        XYToolTipGenerator generator = new StandardXYToolTipGenerator("{0}: ({1}, {2})",
//                format, format);
//        renderer.setBaseToolTipGenerator (generator);
//
//        return ceChartPanel;
//    }
//
//
//}
