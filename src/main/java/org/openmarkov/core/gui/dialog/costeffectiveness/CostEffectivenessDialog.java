/*
* Copyright 2011 CISIAD, UNED, Spain
*
* Licensed under the European Union Public Licence, version 1.1 (EUPL)
*
* Unless required by applicable law, this code is distributed
* on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
*/

package org.openmarkov.core.gui.dialog.costeffectiveness;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.gui.configuration.OpenMarkovPreferences;
import org.openmarkov.core.gui.dialog.common.OkCancelHorizontalDialog;
import org.openmarkov.core.gui.dialog.io.FileChooser;
import org.openmarkov.core.gui.dialog.io.FileFilterXLS;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;

public class CostEffectivenessDialog extends OkCancelHorizontalDialog {
//TODO internationalization
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JLabel initialAgeLabel;
	private JLabel finalAgeLabel;
	private JTextField finalAgeTextField;
	private JTextField initialAgeTextField;
	private JLabel discountLabel;
	private JTextField discountTextField;
	private JLabel yearsLabel;
	private Integer initialAge;
	private Integer finalAge;
	private Double discount;
	private JLabel lblOutputFile;
	private JLabel yearsLabel2;
	private JTextField outputJTextField;
	private String nameFile;
	private JTextField txtSimulationNumber;
	private JLabel lblSimulationsNumber;
	private Integer simulationsNumber;
	private JButton btnBrowse;
	private boolean isThereNodeAge = false;
	private JLabel numSlicesLabel;
	private JTextField numSlicesJTextField;
	private Integer numSlices;
	private StringResource dialogStringResource;

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
										.addComponent(getDiscountLabel(), 70, 70, 70))
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
									.addComponent(getDiscountTextField(), Alignment.LEADING, 70, 70, Short.MAX_VALUE))))
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
					.addContainerGap(20, Short.MAX_VALUE))
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
						.addComponent(getDiscountTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(getDiscountLabel()))
					.addGap(7)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getLblSimulationsNumber())
						.addComponent(getTxtSimulationsNumber(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getOutputFileLabel())
						.addComponent(getOutputFileJTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(getBtnBrowse()))
					.addGap(128))
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
											.addComponent(getDiscountLabel())
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(getDiscountTextField(),GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
											
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
								.addComponent(getDiscountLabel(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(getDiscountTextField())
								)
								
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(getOutputFileLabel())
								.addComponent(getOutputFileJTextField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(getBtnBrowse())
									)
							.addGap(21)
							.addContainerGap(77, Short.MAX_VALUE))
				);
			getComponentsPanel().setLayout(groupLayout);
			
			Component[] components = new Component [3];
			components[0] = getJLabelNumSlices();
			components[1] = getDiscountLabel();
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
		if (numSlicesLabel == null){
			numSlicesLabel = new JLabel("Number of Slices");
		}
		return numSlicesLabel;
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

	private JLabel getInitialAgeLabel(){
		if ( initialAgeLabel == null ){
			initialAgeLabel = new JLabel("Initial age");
		}
		return initialAgeLabel;
	}
	private JLabel getFinalAgeLabel(){
		if ( finalAgeLabel == null ){
			finalAgeLabel = new JLabel("Final age");
		}
		return finalAgeLabel;
	}
	private JLabel getDiscountLabel(){
		if ( discountLabel == null ){
			discountLabel = new JLabel("Discount");
		}
		return discountLabel;
	}
	private JLabel getYearsLabel(){
		if ( yearsLabel == null ){
			yearsLabel = new JLabel("years");
		}
		return yearsLabel;
	}
	private JTextField getFinalAgeTextField(){
		if ( finalAgeTextField == null ){
			finalAgeTextField = new JTextField("100");
		}
		return finalAgeTextField;
	}
	private JTextField getInitialAgeTextField(){
		if ( initialAgeTextField == null ){
			initialAgeTextField = new JTextField("12");
		}
		return initialAgeTextField;
	}
	private JTextField getDiscountTextField(){
		if ( discountTextField == null ){
			discountTextField = new JTextField("1.03");
		}
		return discountTextField;
	}

	public int requestData(String probNetName, String suffixTypeAnalysis) {
		nameFile = "-"+suffixTypeAnalysis;
		
		if ( probNetName != null ){
			nameFile = getOnlyName(probNetName) + nameFile;
		}
		//getOutputFileJTextField().setText(file);
		
		String title = "";
		if (suffixTypeAnalysis.equals("cea")) {
			title = "Cost Effectiveness Analysis";
		} else if (suffixTypeAnalysis.equals("te")) {
			title = "Temporal Evolution";
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
		
		discount = Double.valueOf(getDiscountTextField().getText());
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
	public double getDiscount(){
		return discount;
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
}
