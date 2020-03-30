/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.dialog.inference.common;

import org.openmarkov.core.inference.MonteCarloOptions;
import org.openmarkov.core.model.network.ProbNet;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Panel with the options/parameters for Monte Carlo Simulations.
 * There are two types of options: those which establish how the simulation is carried out and what is calculated,
 * and those which determine how this simulation is logged (log options)
 * @version 1.0 cyago - 04/01/2019 - In this version Monte Carlo options is only used for DESNets
 */
public class MonteCarloOptionsPanel extends JPanel implements ActionListener {

	/**
	 * ProbNet to simulate
	 */
	private ProbNet probNet;

	/**
	 * Monte Carlo options to carry out the simualtion
	 */
	private MonteCarloOptions monteCarloOptions;

	/**
	 * JPanel when the Monte Carlo simulation options are depicted
	 */
	private JPanel monteCarloOptionsPanel;

	/**
	 * Label for number of series of simulations
	 */
	private JLabel numSeriesLabel;

	/**
	 * Number of series of simulations JTextField
	 */
	private JTextField numSeriesTextField;

	/**
	 * Label for number of simulations per series
	 */
	private JLabel numSimulationsLabel;

	/**
	 * JTextField for  number of simulations per series
	 */
	private JTextField numSimulationsTextField;

	/**
	 * Panel for log options
	 */
	private JPanel desNetLogOptionsPanel;




	/**
	 * JCHeckBox for setting the "only summary" option.  When checked, only a summary of the simulations is recorded
	 */
	private JCheckBox resultsPerSeriesCheckBox;

	/**
	 * JCHeckBox for setting the "state log" option. When checked, the states of the patient are logged
	 */
	private JCheckBox stateLogCheckBox;

	/**
	 * JCHeckBox for setting the "event log" option. When checked, every event is logged
	 */
	private JCheckBox eventLogCheckBox;

	/**
	 * JCHeckBox for setting the "events queue" option. When checked the events queue is logged for every event happened
	 */
	private JCheckBox scheduledEventsCheckBox;


	/**
	 * JCHeckBox for setting the "detailed textual log option"
	 */
	private JCheckBox textualLogCheckBox;



	//Statistics Panel
	/**
	 * Panel with the statistics options
	 */
	private JPanel statisticsPanel;
	/**
	 * JCheckBox for calculating the mean. When checked the simulations mean is computed
	 */
	private JCheckBox meanCheckBox;
	/**
	 * JCheckBox for calculating the sum. When checked the simulations sum is computed
	 */
	private JCheckBox sumCheckBox;
	/**
	 * JCheckBox for calculating the trimmed mean. When checked the simulations trimmed mean is computed
	 */
	private JCheckBox trimmedMeanCheckBox;
	/**
	 * JCheckBox for calculating the median. When checked the simulations median is computed
	 */
	private JCheckBox medianCheckBox;

	//File Panel
	/**
	 * JPanel for setting the input file
	 */
	private JPanel inputFileJPanel = null;
	/**
	 * JButton for setting the input File
	 */
	private JButton inputFileJButton = null;
	/**
	 * JLabel of the JButton for setting the input File
	 */
	private JTextField inputFileJTextField = null;


	/**
	 * Constructor of panel containing the monte carlo options
	 *
	 * @param probNet probNet to be simulated
	 */
	public MonteCarloOptionsPanel(ProbNet probNet) {
		super();

		this.probNet = probNet;
		monteCarloOptions = probNet.getInferenceOptions().getMonteCarloOptions().clone();

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.setBorder(new TitledBorder("Monte Carlo Options"));
		JPanel numSeriesPanel = new JPanel();
		numSeriesPanel.add(getJLabelNumSeries());
		numSeriesPanel.add(getNumSeriesTextField());
		JPanel numSimulationsPanel = new JPanel();
		numSimulationsPanel.add(getJLabelNumSimulations());
		numSimulationsPanel.add(getNumSimulationsTextField());
		JPanel firstLinePanel = new JPanel();
		firstLinePanel.setLayout(new BorderLayout());
		firstLinePanel.add(numSimulationsPanel,BorderLayout.WEST);
		firstLinePanel.add(numSeriesPanel,BorderLayout.EAST);
		this.add(firstLinePanel);
		this.add(Box.createRigidArea(new Dimension()));
		this.add(getDesNetLogOptionsPanel());
		this.add(Box.createRigidArea(new Dimension(0,10)));
		this.add(getStatisticsPanel());
		this.add(Box.createRigidArea(new Dimension(0,10)));
		this.add(getInputFileJPanel());

	}


	/**
	 * This method delivers the monteCarloOptions set in this panel
	 * @return  the monteCarloOptions set in this panel
	 */
	public MonteCarloOptions getMonteCarloOptions() {
		extractMonteCarloOptions();
		return monteCarloOptions;
	}

	/**
	 * This method extract the options set in the panel and stares them in the monteCarloOptions object
	 */
	private void extractMonteCarloOptions() {
		this.monteCarloOptions.setNumSeries(Integer.parseInt(numSeriesTextField.getText()));
		this.monteCarloOptions.setNumSimulations(Integer.parseInt(numSimulationsTextField.getText()));
		this.monteCarloOptions.setExcelResutlsPerSeries(resultsPerSeriesCheckBox.isSelected());
		this.monteCarloOptions.setTextualLog(textualLogCheckBox.isSelected());
		this.monteCarloOptions.setMean(meanCheckBox.isSelected());
		this.monteCarloOptions.setTrimmedMean(trimmedMeanCheckBox.isSelected());
		this.monteCarloOptions.setMedian(medianCheckBox.isSelected());
		this.monteCarloOptions.setSum(sumCheckBox.isSelected());
	}



	/**
	 * This method returns the label for number of simulations
	 * @return the label for number of simulations
	 */
	private JLabel getJLabelNumSimulations() {
		if (numSimulationsLabel == null) {
			//TODO use stringDatabase
			numSimulationsLabel = new JLabel("Number of simulations");
		}
		return numSimulationsLabel;

	}


	/**
	 * This method returns the JTextField for number of simulations
	 * @return the JTextField for number of simulations
	 */
	private JTextField getNumSimulationsTextField() {

		if (numSimulationsTextField == null) {
			numSimulationsTextField = new JTextField();
			numSimulationsTextField.setText("" + this.monteCarloOptions.getNumSimulations());
			numSimulationsTextField.setColumns(10);
			numSimulationsTextField.setName("numSimulationsTextField");
		}
		return numSimulationsTextField;
	}


	/**
	 * This method returns the JLabel for number of series
	 * @return the label for number of series
	 */
	private JLabel getJLabelNumSeries() {
		if (numSeriesLabel == null) {
			//TODO use stringDatabase
			numSeriesLabel = new JLabel("Number of series");
		}
		return numSeriesLabel;

	}

	/**
	 * This method returns the JTextField for number of series
	 * @return the JTextField for number of series
	 */
	private JTextField getNumSeriesTextField() {

		if (numSeriesTextField == null) {
			numSeriesTextField = new JTextField();
			numSeriesTextField.setText("" + this.monteCarloOptions.getNumSeries());
			numSeriesTextField.setColumns(10);
			numSeriesTextField.setName("numSeriesTextField");
		}
		return numSeriesTextField;
	}

	/**
	 * This method returns panel for DesNet Log Options
	 * @return the label for number of series
	 */
	private JPanel getDesNetLogOptionsPanel() {
		if (desNetLogOptionsPanel == null) {
			desNetLogOptionsPanel = new JPanel();
			desNetLogOptionsPanel.setBorder( new TitledBorder("DES Inference Log Options"));
			desNetLogOptionsPanel.add(getJCheckBoxExcelResultsPerSeries());
			desNetLogOptionsPanel.add(getTextualLogCheckBox());
		}
		return desNetLogOptionsPanel;
	}



	/**
	 * Returns the JCheckBox for "Detailed Textual Log"
	 * @return the JCheckBox for "Event Log"
	 */
	private JCheckBox getTextualLogCheckBox() {
		if (textualLogCheckBox == null) {
			//TODO use stringDatabase
			textualLogCheckBox = new JCheckBox("Detailed Textual Log", monteCarloOptions.isTextualLog());
		}

		return textualLogCheckBox;
	}



	/**
	 * This method returns the JCheckBox for "Only Summary"
	 * @return the JCheckBox for "Only Summary"
	 */
	private JCheckBox getJCheckBoxExcelResultsPerSeries() {
		if (resultsPerSeriesCheckBox == null) {
			//TODO use stringDatabase
			resultsPerSeriesCheckBox = new JCheckBox("Results Per Series (.xlsx file)",monteCarloOptions.isExcelResutlsPerSeries());

		}
		return resultsPerSeriesCheckBox;
	}



	/**
	 * This method returns the statistics JPanel
	 * @return the statistics JPanel
	 */
	private JPanel getStatisticsPanel(){
		if (statisticsPanel == null) {
			statisticsPanel = new JPanel();
			statisticsPanel.setBorder( new TitledBorder("DES Inference Statistics"));
			statisticsPanel.add(getJCheckBoxMean());
			statisticsPanel.add(getJCheckBoxSum());
			statisticsPanel.add(getJCheckBoxTrimmedMean());
			statisticsPanel.add(getJCheckBoxMedian());
		}
		return statisticsPanel;
		
	}
	/**
	 * This method returns the JCheckBox for "Mean"
	 * @return the JCheckBox for "Mean"
	 */
	private JCheckBox getJCheckBoxMean() {
		if (meanCheckBox == null) {
			//TODO use stringDatabase
			meanCheckBox = new JCheckBox("Mean", monteCarloOptions.isMean());
			meanCheckBox.setEnabled(true);
			meanCheckBox.setSelected(true);		
		}
		return meanCheckBox;
	}

	/**
	 * This method returns the JCheckBox for "Sum"
	 * @return the JCheckBox for "Only Summary"
	 */
	private JCheckBox getJCheckBoxSum() {
		if (sumCheckBox == null) {
			//TODO use stringDatabase
			sumCheckBox = new JCheckBox("Sum", monteCarloOptions.isMean());
			sumCheckBox.setEnabled(false);
			sumCheckBox.setSelected(false);
		}
		return sumCheckBox;
	}

	/**
	 * This method returns the JCheckBox for "Trimmed Mean"
	 * @return the JCheckBox for "Trimmed Mean"
	 */
	private JCheckBox getJCheckBoxTrimmedMean() {
		if (trimmedMeanCheckBox == null) {
			//TODO use stringDatabase
			trimmedMeanCheckBox = new JCheckBox("Trimmed Mean", monteCarloOptions.isMean());
			trimmedMeanCheckBox.setEnabled(false);
			trimmedMeanCheckBox.setSelected(false);
		}
		return trimmedMeanCheckBox;
	}

	/**
	 * This method returns the JCheckBox for "Median"
	 * @return the JCheckBox for "Median"
	 */
	private JCheckBox getJCheckBoxMedian() {
		if (medianCheckBox == null) {
			//TODO use stringDatabase
			medianCheckBox = new JCheckBox("Median", monteCarloOptions.isMean());
			medianCheckBox.setEnabled(false);
			medianCheckBox.setSelected(false);
		}
		return medianCheckBox;
	}


	/**
	 * This method returns the JPanel for the simulation input file
	 * @return the JPanel for the simulation input file
	 */
	private JPanel getInputFileJPanel(){
		if (inputFileJPanel == null) {
			inputFileJPanel = new JPanel();
			inputFileJPanel.setBorder( new TitledBorder("DES Input File"));
			inputFileJPanel.setLayout(new BorderLayout(0,5));
			inputFileJPanel.add(getJButtonAddInputFile(), BorderLayout.NORTH);
			inputFileJPanel.add(getJTextFieldInputFile(), BorderLayout.SOUTH);

		}
		return inputFileJPanel;
	}

	/**
	 * This method returns the JButton for selecting the simulation input file
	 * @return the JButton to select the simulation input file
	 */
	private JButton getJButtonAddInputFile() {
		if (inputFileJButton==null) {
			inputFileJButton = new JButton("Add Input File");
			inputFileJButton.setActionCommand("AddInputFile");
			inputFileJButton.addActionListener(this);
		}
		return inputFileJButton;
	}

	/**
	 * This method returns the JLabel for selecting the simulation input file
	 * @return the JLabel to select the simulation input file
	 */
	private JTextField getJTextFieldInputFile() {
		if (inputFileJTextField ==null){
			inputFileJTextField = new JTextField();
			inputFileJTextField.setText("");
//			inputFileJTextField.setVisible(false);
			inputFileJTextField.setEnabled(false);

		}
		return inputFileJTextField;
	}

	/**
	 * Action for inputFileJButton. Selects the input file, stores it in monteCarloOptions and write the name in the panel
	 * @param e actionEvent for inputFileButton
	 */
	public void actionPerformed(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser("C:\\Users\\Carmen María\\OneDrive - Consejería de Educación, Formación y Empleo\\Tesis\\DESNetFiles\\InputFile") ;

		FileNameExtensionFilter filter = new FileNameExtensionFilter("*.xlsx", "xlsx");
		fileChooser.setFileFilter(filter);

		//Handle open button action.
		if (e.getActionCommand() == "AddInputFile") {
			int returnVal = fileChooser.showOpenDialog(MonteCarloOptionsPanel.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				monteCarloOptions.setInputFile(fileChooser.getSelectedFile());
					inputFileJTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());

			}
		}
	}


}
