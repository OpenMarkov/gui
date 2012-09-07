/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog.costeffectiveness;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.plot.ThermometerPlot;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.gui.configuration.OpenMarkovPreferences;
import org.openmarkov.core.gui.dialog.common.OkCancelHorizontalDialog;
import org.openmarkov.core.gui.dialog.io.FileChooser;
import org.openmarkov.core.gui.dialog.io.FileFilterXLS;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;

public class CostEffectivenessDialog extends OkCancelHorizontalDialog implements ItemListener  {
//TODO internationalization
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JLabel initialAgeLabel;
	private JLabel cycleLengthLabel;
	private JLabel unitLabel;
	private JTextField cycleLengthTextField;
	private JComboBox unitsCombo;
	private JLabel finalAgeLabel;
	private JTextField finalAgeTextField;
	private JTextField initialAgeTextField;
	private JLabel costDiscountLabel;
	private JLabel effectivenessDiscountLabel;
	private JTextField costDiscountTextField;
	private JTextField effectivenessDiscountTextField;
	private JLabel yearsLabel;
	private Integer initialAge;
	private Integer finalAge;
	private Double costDiscount;
	private Double effectivenessDiscount;
	private JLabel lblOutputFile;
	private JLabel yearsLabel2;
	private JTextField outputJTextField;
	private String nameFile;
	private JTextField txtSimulationNumber;
	private JLabel lblSimulationsNumber;
	private Integer simulationsNumber;
	private JButton btnBrowse;
	private boolean isThereNodeAge = false;
	private JLabel numSlicesLabell;
	private JTextField numSlicesJTextField;
	private Integer numSlices;
	private StringResource dialogStringResource;
	private JRadioButton instantButton;
	private JRadioButton accumulativeButton;
	private ButtonGroup buttonGroup;
	private boolean isUtility;
	private boolean isTemporalEvolution;
	private JPanel nodeAgePanel;
	private JPanel utilityParametersPanel;
	private JPanel instantOrAccumulativePanel;
	private JPanel outputPanel;
	private boolean isAccumulative = false;
	private JPanel numSlicesPanel; 

	/**
	 * Launch the application.
	 * 
	 */
	public static void main(String[] args) {
	StringResource messageStringResource =	StringResourceLoader.getUniqueInstance().getBundleMessages();
		try {
			CostEffectivenessDialog dialog = new CostEffectivenessDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, messageStringResource
					.getString( e.getMessage() ),
				messageStringResource.getString( e.getMessage() ),
				JOptionPane.ERROR_MESSAGE );
		}
	}

	/**
	 * Create the dialog.
	 */
	public CostEffectivenessDialog() {
		super(null);
		initialize();
		/*getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}*/
	}
	
	/**
	 * Creates a CostEffectivenessDialog for expansion only
	 * @param owner
	 * 		The parent of the dialog
	 */
	public CostEffectivenessDialog(Window owner) {
		super(owner);
		setLocationRelativeTo(owner);
		dialogStringResource =
	            StringResourceLoader.getUniqueInstance().getBundleDialogs();
		
		//setMinimumSize(new Dimension(250 , 150));
		BorderLayout layout = new BorderLayout(5, 5);
		getComponentsPanel().setLayout(layout);
		getComponentsPanel().add(getNumSlicesPanel(), BorderLayout.NORTH);
		setResizable(true);
		pack();
		repaint();
	}
	
	private JPanel getNumSlicesPanel() {

		if (numSlicesPanel == null){

			numSlicesPanel = new JPanel();

			GroupLayout groupLayout = new GroupLayout(numSlicesPanel);

			groupLayout.setHorizontalGroup(

				groupLayout.createParallelGroup(Alignment.LEADING)

					.addGroup(groupLayout.createSequentialGroup()

						.addContainerGap()

						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)

							.addGroup(groupLayout.createSequentialGroup()

								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)

									.addGroup(groupLayout.createSequentialGroup()

										.addComponent(getJLabelNumSlices())

										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)

										.addComponent(getNumSlicesJTextField(), GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)

										))

							.addContainerGap())

			)));

			groupLayout.setVerticalGroup(

				groupLayout.createParallelGroup(Alignment.LEADING)

					.addGroup(groupLayout.createSequentialGroup()

						.addContainerGap()

						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)

							.addComponent(getJLabelNumSlices())

							.addComponent(getNumSlicesJTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)

							)

						.addContainerGap())

			);
			numSlicesPanel.setLayout(groupLayout);
		}

		return numSlicesPanel;

	}

	/**
	 * Creates a CostEffectivenessDialog
	 * @param owner
	 * 		The parent of the dialog
	 */
	public CostEffectivenessDialog(Window owner, boolean isThereNodeAge) {
		super(owner);
		setLocationRelativeTo(owner);
		this.isThereNodeAge = isThereNodeAge;
		
		dialogStringResource =
	            StringResourceLoader.getUniqueInstance().getBundleDialogs();
		initialize();
	}
		
	/**
	 * Creates a CostEffectivenessDialog for temporal evolution
	 * @param owner
	 * 		The parent of the dialog
	 */
	public CostEffectivenessDialog(Window owner, boolean isThereNodeAge, boolean isUtility, boolean isTemporalEvolution) {
		super(owner);
		setLocationRelativeTo(owner);
		this.isThereNodeAge = isThereNodeAge;
		this.isUtility = isUtility;
		this.isTemporalEvolution = isTemporalEvolution;
		dialogStringResource =
	            StringResourceLoader.getUniqueInstance().getBundleDialogs();
		initialize(isTemporalEvolution);
		setResizable(true);
		pack();
		repaint();
		
	}
	private void initialize(boolean isTemporalEvolution) {

		setMinimumSize(new Dimension(250 , 150));
		
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		if (isTemporalEvolution) {
		if (isUtility && isThereNodeAge) {
			GroupLayout groupLayout = new GroupLayout(getComponentsPanel());
			groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
					.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
							.addGroup(groupLayout.createSequentialGroup()
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
									.addGroup(groupLayout.createSequentialGroup()
										.addComponent(getCycleLengthLabel())
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(getCycleLengthTextField(), GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
										.addGap(18)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(getUnitsJComboBox())
										)
									.addGroup(groupLayout.createSequentialGroup()
										.addComponent(getInitialAgeLabel())
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(getInitialAgeTextField(), GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
										.addGap(18)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(getFinalAgeLabel())
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(getFinalAgeTextField(),GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
										)
									.addGroup(groupLayout.createSequentialGroup()
										.addComponent(getCostDiscountLabel())
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(getCostDiscountTextField(), GroupLayout.PREFERRED_SIZE,  75, GroupLayout.PREFERRED_SIZE)
										.addGap(18)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(getEffectivenessDiscountLabel())
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(getEffectivenessDiscountTextField(), GroupLayout.PREFERRED_SIZE,  75, GroupLayout.PREFERRED_SIZE)
										)
									.addGroup(groupLayout.createSequentialGroup()
										.addComponent(getJPanelInstantOrAccumulative()) 
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										)
									.addGroup(groupLayout.createSequentialGroup()
										.addComponent(getOutputFileLabel())
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(getOutputFileJTextField(), GroupLayout.PREFERRED_SIZE,  203, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(getBtnBrowse())
										)
										)
							
							.addContainerGap())
			)));
			groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
					.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(getCycleLengthLabel())
							.addComponent(getCycleLengthTextField(), GroupLayout.PREFERRED_SIZE, /*20*/GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(getUnitsJComboBox())
							)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(getInitialAgeLabel())
							.addComponent(getInitialAgeTextField(), GroupLayout.PREFERRED_SIZE, /*20*/GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(getFinalAgeLabel())
							.addComponent(getFinalAgeTextField())
							)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(getCostDiscountLabel(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(getCostDiscountTextField())
							.addComponent(getEffectivenessDiscountLabel(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(getEffectivenessDiscountTextField())
							)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(getJPanelInstantOrAccumulative())
							)
						.addGap(21)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(getOutputFileLabel())
							.addComponent(getOutputFileJTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(getBtnBrowse()))
						.addContainerGap())
			);
			Component[] components = new Component [6];
			components[0] = getInitialAgeLabel();
			components[1] = getCostDiscountLabel();
			components[2] = getOutputFileLabel();
			components[3] = getFinalAgeLabel();
			components[4] = getCycleLengthLabel();
			components[5] = getUnitsJComboBox();
			groupLayout.linkSize(components);
			getComponentsPanel().setLayout(groupLayout);
			
		} else if (isUtility && !isThereNodeAge) {
			GroupLayout groupLayout = new GroupLayout(getComponentsPanel());
			groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
					.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
							.addGroup(groupLayout.createSequentialGroup()
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
									.addGroup(groupLayout.createSequentialGroup()
										.addComponent(getCycleLengthLabel())
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(getCycleLengthTextField(), GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
										.addGap(18)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(getUnitsJComboBox())
										)
									.addGroup(groupLayout.createSequentialGroup()
										.addComponent(getJLabelNumSlices())
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(getNumSlicesJTextField(), GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
										)
									.addGroup(groupLayout.createSequentialGroup()
											.addComponent(getCostDiscountLabel())
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(getCostDiscountTextField(), GroupLayout.PREFERRED_SIZE,  75, GroupLayout.PREFERRED_SIZE)
											.addGap(18)
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(getEffectivenessDiscountLabel())
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(getEffectivenessDiscountTextField(), GroupLayout.PREFERRED_SIZE,  75, GroupLayout.PREFERRED_SIZE)
											)
									.addGroup(groupLayout.createSequentialGroup()
										.addComponent(getJPanelInstantOrAccumulative()) 
										)
									.addGroup(groupLayout.createSequentialGroup()
										.addComponent(getOutputFileLabel())
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(getOutputFileJTextField(), GroupLayout.PREFERRED_SIZE, 203,	Short.MAX_VALUE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(getBtnBrowse())
										)
										)
							
							.addContainerGap())
			)));
			groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
					.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(getCycleLengthLabel())
							.addComponent(getCycleLengthTextField(), GroupLayout.PREFERRED_SIZE, /*20*/GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(getUnitsJComboBox())
							)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(getJLabelNumSlices())
							.addComponent(getNumSlicesJTextField(), GroupLayout.PREFERRED_SIZE, /*20*/GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(getCostDiscountLabel(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(getCostDiscountTextField())
							.addComponent(getEffectivenessDiscountLabel(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(getEffectivenessDiscountTextField())
							)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(getJPanelInstantOrAccumulative())
							)
						.addGap(21)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(getOutputFileLabel())
							.addComponent(getOutputFileJTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(getBtnBrowse()))
						.addContainerGap())
			);
			
			Component[] components = new Component [5];
			components[0] = getJLabelNumSlices();
			components[1] = getCostDiscountLabel();
			components[2] = getOutputFileLabel();
			components[3] = getCycleLengthLabel();
			components[4] = getUnitsJComboBox();
			groupLayout.linkSize(components);
			getComponentsPanel().setLayout(groupLayout);
			
		} else if (!isUtility && isThereNodeAge) {
			GroupLayout groupLayout = new GroupLayout(getComponentsPanel());
			groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
					.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
							.addGroup(groupLayout.createSequentialGroup()
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
									.addGroup(groupLayout.createSequentialGroup()
										.addComponent(getCycleLengthLabel())
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(getCycleLengthTextField(), GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
										.addGap(18)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(getUnitsJComboBox())
										)
									.addGroup(groupLayout.createSequentialGroup()
										.addComponent(getInitialAgeLabel())
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(getInitialAgeTextField(), GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
										.addGap(18)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(getFinalAgeLabel())
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(getFinalAgeTextField(),GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
										)
									.addGroup(groupLayout.createSequentialGroup()
										.addComponent(getOutputFileLabel())
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(getOutputFileJTextField(), GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(getBtnBrowse())
										)
										)
							
							.addContainerGap())
			)));
			groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
					.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(getCycleLengthLabel())
							.addComponent(getCycleLengthTextField(), GroupLayout.PREFERRED_SIZE, /*20*/GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(getUnitsJComboBox())
							)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(getInitialAgeLabel())
							.addComponent(getInitialAgeTextField(), GroupLayout.PREFERRED_SIZE, /*20*/GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(getFinalAgeLabel())
							.addComponent(getFinalAgeTextField())
							)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(getOutputFileLabel())
							.addComponent(getOutputFileJTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(getBtnBrowse()))
						.addContainerGap())
			);
			Component[] components = new Component [5];
			components[0] = getJLabelNumSlices();
			components[1] = getCostDiscountLabel();
			components[2] = getOutputFileLabel();
			components[3] = getCycleLengthLabel();
			components[4] = getUnitsJComboBox();
			groupLayout.linkSize(components);
			getComponentsPanel().setLayout(groupLayout);
			
		} else if (!isUtility && !isThereNodeAge) {
			GroupLayout groupLayout = new GroupLayout(getComponentsPanel());
			groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
					.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
							.addGroup(groupLayout.createSequentialGroup()
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
									.addGroup(groupLayout.createSequentialGroup()
										.addComponent(getCycleLengthLabel())
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(getCycleLengthTextField(), GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
										.addGap(18)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(getUnitsJComboBox())
												)
									.addGroup(groupLayout.createSequentialGroup()
										.addComponent(getJLabelNumSlices())
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(getNumSlicesJTextField(), GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
										)
									.addGroup(groupLayout.createSequentialGroup()
										.addComponent(getOutputFileLabel())
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(getOutputFileJTextField(), GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(getBtnBrowse())
										)
										)
							
							.addContainerGap())
			)));
			groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
					.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(getCycleLengthLabel())
							.addComponent(getCycleLengthTextField(), GroupLayout.PREFERRED_SIZE, /*20*/GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(getUnitsJComboBox())
							)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(getJLabelNumSlices())
							.addComponent(getNumSlicesJTextField(), GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(getOutputFileLabel())
							.addComponent(getOutputFileJTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(getBtnBrowse()))
						.addContainerGap())
			);
			Component[] components = new Component [4];
			components[0] = getJLabelNumSlices();
			components[1] = getOutputFileLabel();
			components[2] = getCycleLengthLabel();
			components[3] = getUnitsJComboBox();
			groupLayout.linkSize(components);
			getComponentsPanel().setLayout(groupLayout);
		}
		} else if (!isTemporalEvolution) {
			if (isThereNodeAge) {

				GroupLayout groupLayout = new GroupLayout(getComponentsPanel());
				groupLayout.setHorizontalGroup(
					groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
										.addGroup(groupLayout.createSequentialGroup()
											.addComponent(getCycleLengthLabel())
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(getCycleLengthTextField(), GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
											.addGap(18)
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(getUnitsJComboBox())
											)
										.addGroup(groupLayout.createSequentialGroup()
											.addComponent(getInitialAgeLabel())
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(getInitialAgeTextField(), GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
											.addGap(18)
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(getFinalAgeLabel())
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(getFinalAgeTextField(),GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
											)
										.addGroup(groupLayout.createSequentialGroup()
											.addComponent(getCostDiscountLabel())
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(getCostDiscountTextField(), GroupLayout.PREFERRED_SIZE,  75, GroupLayout.PREFERRED_SIZE)
											.addGap(18)
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(getEffectivenessDiscountLabel())
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(getEffectivenessDiscountTextField(), GroupLayout.PREFERRED_SIZE,  75, GroupLayout.PREFERRED_SIZE)
											)
										.addGroup(groupLayout.createSequentialGroup()
											.addComponent(getOutputFileLabel())
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(getOutputFileJTextField(), GroupLayout.PREFERRED_SIZE,  203, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(getBtnBrowse())
											)
											)
								
								.addContainerGap())
				)));
				groupLayout.setVerticalGroup(
					groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(getCycleLengthLabel())
							.addComponent(getCycleLengthTextField(), GroupLayout.PREFERRED_SIZE, /*20*/GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(getUnitsJComboBox())
							)
						.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(getInitialAgeLabel())
								.addComponent(getInitialAgeTextField(), GroupLayout.PREFERRED_SIZE, /*20*/GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(getFinalAgeLabel())
								.addComponent(getFinalAgeTextField())
								)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(getCostDiscountLabel(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(getCostDiscountTextField())
								.addComponent(getEffectivenessDiscountLabel(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(getEffectivenessDiscountTextField())
								)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGap(21)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(getOutputFileLabel())
								.addComponent(getOutputFileJTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(getBtnBrowse()))
							.addContainerGap())
				);
				Component[] components = new Component [6];
				components[0] = getInitialAgeLabel();
				components[1] = getCostDiscountLabel();
				components[2] = getOutputFileLabel();
				components[3] = getFinalAgeLabel();
				components[4] = getCycleLengthLabel();
				components[5] = getUnitsJComboBox();
				groupLayout.linkSize(components);
				getComponentsPanel().setLayout(groupLayout);
				
			
			} else if (!isThereNodeAge) {

				GroupLayout groupLayout = new GroupLayout(getComponentsPanel());
				groupLayout.setHorizontalGroup(
					groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
										.addGroup(groupLayout.createSequentialGroup()
											.addComponent(getCycleLengthLabel())
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(getCycleLengthTextField(), GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
											.addGap(18)
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(getUnitsJComboBox())
											)
										.addGroup(groupLayout.createSequentialGroup()
											.addComponent(getJLabelNumSlices())
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(getNumSlicesJTextField(), GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
											)
										.addGroup(groupLayout.createSequentialGroup()
												.addComponent(getCostDiscountLabel())
												.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(getCostDiscountTextField(), GroupLayout.PREFERRED_SIZE,  75, GroupLayout.PREFERRED_SIZE)
												.addGap(18)
												.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(getEffectivenessDiscountLabel())
												.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(getEffectivenessDiscountTextField(), GroupLayout.PREFERRED_SIZE,  75, GroupLayout.PREFERRED_SIZE)
												)
										.addGroup(groupLayout.createSequentialGroup()
											.addComponent(getOutputFileLabel())
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(getOutputFileJTextField(), GroupLayout.PREFERRED_SIZE, 203,	Short.MAX_VALUE)
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(getBtnBrowse())
											)
											)
								
								.addContainerGap())
				)));
				groupLayout.setVerticalGroup(
					groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(getCycleLengthLabel())
							.addComponent(getCycleLengthTextField(), GroupLayout.PREFERRED_SIZE, /*20*/GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(getUnitsJComboBox())
							)
						.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(getJLabelNumSlices())
								.addComponent(getNumSlicesJTextField(), GroupLayout.PREFERRED_SIZE, /*20*/GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(getCostDiscountLabel(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(getCostDiscountTextField())
								.addComponent(getEffectivenessDiscountLabel(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(getEffectivenessDiscountTextField())
								)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGap(21)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(getOutputFileLabel())
								.addComponent(getOutputFileJTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(getBtnBrowse()))
							.addContainerGap())
				);
				
				Component[] components = new Component [5];
				components[0] = getJLabelNumSlices();
				components[1] = getCostDiscountLabel();
				components[2] = getOutputFileLabel();
				components[3] = getCycleLengthLabel();
				components[4] = getUnitsJComboBox();
				groupLayout.linkSize(components);
				getComponentsPanel().setLayout(groupLayout);
				
			
			}
		}
		
		 pack();
		 repaint();
		
		
		}
		
	private void initialize(){
		
		setMinimumSize(new Dimension(380, 230));
		
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		if (isThereNodeAge) {				
		GroupLayout groupLayout = new GroupLayout(getComponentsPanel());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addGroup(groupLayout.createSequentialGroup()
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(getInitialAgeLabel(), 70, 70, 70)
										.addComponent(getCostDiscountLabel(), 70, 70, 70))
									.addGap(26))
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(getLblSimulationsNumber())
									.addPreferredGap(ComponentPlacement.RELATED)))
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(getInitialAgeTextField(), 70, 70, 70)
									.addGap(28)
									.addComponent(getYearsLabel(), 70, 70, 70))
								.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
									.addComponent(getTxtSimulationsNumber(), Alignment.LEADING, 0, 0, Short.MAX_VALUE)
									.addComponent(getCostDiscountTextField(), Alignment.LEADING, 70, 70, Short.MAX_VALUE))))
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(getOutputFileLabel())
									.addGap(14)
									.addComponent(getOutputFileJTextField()))
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(getFinalAgeLabel(), 70, 70, 70)
									.addGap(26)
									.addComponent(getFinalAgeTextField(), 70, 70, 70)
									.addGap(28)
									.addComponent(getYearsLabel2(), 70, 70, 70)))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(getBtnBrowse())))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getYearsLabel())
						.addComponent(getInitialAgeLabel())
						.addComponent(getInitialAgeTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getFinalAgeLabel())
						.addComponent(getYearsLabel2())
						.addComponent(getFinalAgeTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getCostDiscountTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(getCostDiscountLabel()))
					.addGap(7)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getLblSimulationsNumber())
						.addComponent(getTxtSimulationsNumber(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getOutputFileLabel())
						.addComponent(getOutputFileJTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(getBtnBrowse()))
					.addContainerGap())
		);
		getComponentsPanel().setLayout(groupLayout);
		} else if (!isThereNodeAge) {
			GroupLayout groupLayout = new GroupLayout(getComponentsPanel());
			groupLayout.setHorizontalGroup(
					groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
										.addGroup(groupLayout.createSequentialGroup()
											.addComponent(getJLabelNumSlices())
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(getNumSlicesJTextField(), GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
											)
										.addGroup(groupLayout.createSequentialGroup()
											.addComponent(getCostDiscountLabel())
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(getCostDiscountTextField(),GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
											
											)
										.addGroup(groupLayout.createSequentialGroup()
											.addComponent(getOutputFileLabel()) 
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(getOutputFileJTextField(), GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(getBtnBrowse())
											)
											)
								
								.addContainerGap())
				)));
				groupLayout.setVerticalGroup(
					groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(getJLabelNumSlices())
								.addComponent(getNumSlicesJTextField(), GroupLayout.PREFERRED_SIZE, /*20*/GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(getCostDiscountLabel(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(getCostDiscountTextField())
								)
								
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(getOutputFileLabel())
								.addComponent(getOutputFileJTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(getBtnBrowse())
									)
							.addGap(21)
							.addContainerGap())
				);
			getComponentsPanel().setLayout(groupLayout);
			
			Component[] components = new Component [3];
			components[0] = getJLabelNumSlices();
			components[1] = getCostDiscountLabel();
			components[2] = getOutputFileLabel();
			groupLayout.linkSize(components);
		}
		
	}
	
	public JButton getBtnBrowse(){
		if (btnBrowse == null){
			btnBrowse = new JButton("Browse ...");
			btnBrowse.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					String tempNameFile = requestNetworkFileToSave(nameFile);
					if ( tempNameFile != null ){
						nameFile = tempNameFile; 
						getOutputFileJTextField().setText(nameFile);
					}
						
					
				}
			});
		}
		return btnBrowse;
	}
	


	private JLabel getJLabelNumSlices() {
		if (numSlicesLabell == null){
			numSlicesLabell = new JLabel("Number of Cycles");
		}
		return numSlicesLabell;
	}
	
	private JTextField getNumSlicesJTextField(){
		if (numSlicesJTextField == null){
			numSlicesJTextField = new JTextField();
			numSlicesJTextField.setColumns(10);
		}
		return numSlicesJTextField;
	}
	private JTextField getTxtSimulationsNumber(){
		if (txtSimulationNumber == null){
			txtSimulationNumber = new JTextField("20");
			txtSimulationNumber.setColumns(10);
			txtSimulationNumber.setVisible(false);
		}
		return txtSimulationNumber;
	}
	private JLabel getLblSimulationsNumber(){
		if (lblSimulationsNumber == null){
			lblSimulationsNumber = new JLabel("Simulations number");
			lblSimulationsNumber.setVisible(false);
		}
		return lblSimulationsNumber;
	}
	public JTextField getOutputFileJTextField(){
		if (outputJTextField == null){
			outputJTextField = new JTextField();
			outputJTextField.setColumns(10);
		}
		return outputJTextField;
	}
	public JLabel getOutputFileLabel(){
		if (lblOutputFile == null){
			lblOutputFile = new JLabel("Output file name");
		}
		return lblOutputFile;
	}
	private JLabel getYearsLabel2(){
		if ( yearsLabel2 == null ){
			yearsLabel2 = new JLabel("years");
		}
		return yearsLabel2;
	}

	public JLabel getInitialAgeLabel(){
		if ( initialAgeLabel == null ){
			initialAgeLabel = new JLabel("Initial age");
		}
		return initialAgeLabel;
	}
	public JLabel unitLabel(){
		if ( unitLabel == null ){
			unitLabel = new JLabel("Cycle Length");
		}
		return unitLabel;
	}
	public JComboBox<String> getUnitsJComboBox() {
		if (unitsCombo == null) {
			String units[] = {"months", "years"}; 
			unitsCombo = new JComboBox<>(units);
			//unitsCombo.addItemListener(this);
		}
		return unitsCombo;
	}
	
	public JLabel getCycleLengthLabel(){
		if ( cycleLengthLabel == null ){
			cycleLengthLabel = new JLabel("Cycle Length");
		}
		return cycleLengthLabel;
	}
	public JTextField getCycleLengthTextField(){
		if ( cycleLengthTextField == null ){
			cycleLengthTextField = new JTextField("1");
		}
		return cycleLengthTextField;
	}
	public JLabel getFinalAgeLabel(){
		if ( finalAgeLabel == null ){
			finalAgeLabel = new JLabel("Final age");
		}
		return finalAgeLabel;
	}
	public JLabel getCostDiscountLabel(){
		if ( costDiscountLabel == null ){
			costDiscountLabel = new JLabel("Cost discount (%)");
		}
		return costDiscountLabel;
	}
	public JLabel getEffectivenessDiscountLabel(){
		if ( effectivenessDiscountLabel == null ){
			effectivenessDiscountLabel = new JLabel("Effectiveness discount (%)");
		}
		return effectivenessDiscountLabel;
	}
	private JLabel getYearsLabel(){
		if ( yearsLabel == null ){
			yearsLabel = new JLabel("years");
		}
		return yearsLabel;
	}
	public JTextField getFinalAgeTextField(){
		if ( finalAgeTextField == null ){
			finalAgeTextField = new JTextField("100");
		}
		return finalAgeTextField;
	}
	public JTextField getInitialAgeTextField(){
		if ( initialAgeTextField == null ){
			initialAgeTextField = new JTextField("12");
		}
		return initialAgeTextField;
	}
	public JTextField getCostDiscountTextField(){
		if ( costDiscountTextField == null ){
			costDiscountTextField = new JTextField("3.0");
			costDiscountTextField.setColumns(10);
		}
		return costDiscountTextField;
	}
	public JTextField getEffectivenessDiscountTextField(){
		if ( effectivenessDiscountTextField == null ){
			effectivenessDiscountTextField = new JTextField("3.0");
			effectivenessDiscountTextField.setColumns(10);
		}
		return effectivenessDiscountTextField;
	}
	
	public JRadioButton getInstanValuesButton() {
		if ( instantButton == null ){
			instantButton = new JRadioButton("Instant values", true);
			instantButton.addItemListener(this);
		}
		return instantButton;
	}
	public JRadioButton getAccumulativeValuesButton() {
		if ( accumulativeButton == null ){
			accumulativeButton = new JRadioButton("Cumulative values", false);
			accumulativeButton.addItemListener(this);
		}
		return accumulativeButton;
	}
	
	public void initButtonGroup() {
		
			buttonGroup = new ButtonGroup();
			buttonGroup.add(getInstanValuesButton());
			buttonGroup.add(getAccumulativeValuesButton());
		
	}
	
	/**
	 * @return the panel with the two buttons
	 */
	protected JPanel getJPanelInstantOrAccumulative() {

		if (instantOrAccumulativePanel == null) {
			instantOrAccumulativePanel = new JPanel();
			instantOrAccumulativePanel.setLayout( new GridLayout(2, 1));
			//jPanelTpcOrCanonical.setSize( 152, 58 );
			//instantOrAccumulativePanel.setBorder( new LineBorder( UIManager
				//.getColor( "List.dropLineColor" ), 1, false ) );
			instantOrAccumulativePanel.setBorder(BorderFactory.createTitledBorder(
			           BorderFactory.createEtchedBorder(), "Temporal display"));
			instantOrAccumulativePanel.setName( "instantOrAccumulativePanel" );
			initButtonGroup();
			instantOrAccumulativePanel.add( getInstanValuesButton());
			instantOrAccumulativePanel.add( getAccumulativeValuesButton());
			//instantOrAccumulativePanel.setEnabled( true);
			//instantOrAccumulativePanel.setVisible(true);
		}
		return instantOrAccumulativePanel;
	}

	public int requestData(String probNetName, String suffixTypeAnalysis) {
		nameFile = "-"+suffixTypeAnalysis;
		
		if ( probNetName != null ){
			nameFile = getOnlyName(probNetName) + nameFile;
		}
		//getOutputFileJTextField().setText(file);
		
		//TODO internationalization
		String title = "";
		if (suffixTypeAnalysis.equals("cea")) {
			title = "Cost Effectiveness Analysis";
		} else if (suffixTypeAnalysis.equals("te")) {
			title = "Temporal Evolution";
		} else if (suffixTypeAnalysis.equals("expanded")) {
			title = "Network expansion";
		}
		setTitle(title);
		
		setVisible(true);
		return selectedButton;
	}
	private String getOnlyName(String file) {
		if ( file.endsWith(".pgmx")){
			int index= file.lastIndexOf(".pgmx");
			file = file.substring(0, index);
		}
		return file;
	}

	@Override
	protected boolean doOkClickBeforeHide() throws NotEnoughMemoryException {
		//TODO realizar la validación de los datos capturados
		if (isThereNodeAge) {
			initialAge = Integer.valueOf(getInitialAgeTextField().getText());
			finalAge = Integer.valueOf(getFinalAgeTextField().getText());
		} else {
			numSlices = Integer.valueOf(getNumSlicesJTextField().getText());
		}
		
		costDiscount = Double.valueOf(getCostDiscountTextField().getText());
		effectivenessDiscount = Double.valueOf(getEffectivenessDiscountTextField().getText());
		nameFile = getOutputFileJTextField().getText();
		simulationsNumber = Integer.valueOf(getTxtSimulationsNumber().getText());
		
		return true;
	}
	public int getInitialAge(){
		return initialAge;
	}
	public int getFinalAge(){
		return finalAge;
	}
	public double getCostDiscount(){
		return costDiscount;
	}
	public double getEffectivenessDiscount(){
		return effectivenessDiscount;
	}
	public String getOutputFileName(){
		return nameFile;
	}
	public int getNumSlices(){
		return numSlices;
	}
	public void showSimulationsNumberElements(boolean isProbabilistic){
		getLblSimulationsNumber().setVisible(isProbabilistic);
		getTxtSimulationsNumber().setVisible(isProbabilistic);
	}

	public int getSimulationsNumber() {
		return simulationsNumber;
	}
	
	public boolean isThereNodeAge() {
		return isThereNodeAge;
	}
	/**
	 * It asks the user to choose a file by means of a save-file dialog box.
	 * 
	 * @param suggestedFileName
	 *            name of the file where the net can be saved as default.
	 * @return complete path of the file, or null if the user selects cancel.
	 */
	private String requestNetworkFileToSave(String suggestedFileName) {

		FileChooser fileChooser = new FileChooser();

		fileChooser.setDialogTitle( dialogStringResource
			.getString( "SaveNetwork.Title.Label" ) );
		File currentDirectory =
			new File( OpenMarkovPreferences.get(
				OpenMarkovPreferences.LAST_OPEN_DIRECTORY,
				OpenMarkovPreferences.OPENMARKOV_DIRECTORIES, "." ) );
		fileChooser.setCurrentDirectory( currentDirectory );
		fileChooser.setFileFilter(new FileFilterXLS());
		fileChooser.setSelectedFile( new File( suggestedFileName ) );

		return (fileChooser.showSaveDialog( this ) == 
			JFileChooser.APPROVE_OPTION)
			? fileChooser.getSelectedFile().getAbsolutePath() : null;

	}
	public boolean isAccumulative () {
		return this.isAccumulative;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getItem().equals(getInstanValuesButton())) {
			this.isAccumulative = false;
		}
		if (e.getItem().equals(getAccumulativeValuesButton())) {
			this.isAccumulative = true;
		}
		
	}
}
