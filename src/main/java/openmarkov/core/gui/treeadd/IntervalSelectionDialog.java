package openmarkov.core.gui.treeadd;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openmarkov.core.model.network.potential.treeadd.BranchInterval;
import org.openmarkov.core.model.network.potential.treeadd.EpsilonValueAproximation;

@SuppressWarnings("serial")
public class IntervalSelectionDialog extends JDialog implements ActionListener, ChangeListener {
	double min;
	double max;
	double midValue;
	
	JSpinner spinner;
	JComboBox unitsCombo;
	JComboBox comboInterval;
	JComboBox comboWhatChange;
	
	JButton okButton;
	JButton cancelButton;
	
	BranchInterval result= null;
	
	boolean minClosed;
	boolean maxClosed;
	boolean canChangeMinType;
	boolean canChangeMaxType;
	boolean canChangeMinValue;
	boolean canChangeMaxValue;

	static double initialRange= 1000.0;
	
	boolean []changeRightValue= new boolean[8];
	
	public IntervalSelectionDialog (double min, double max, boolean minClosed, boolean maxClosed, boolean canChangeMinType, boolean canChangeMaxType, boolean canChangeMinValue, boolean canChangeMaxValue) {
		super();
		
		this.setModal(true);
		
		this.min= min;
		this.max= max;
		midValue= (max+min)/2;
		
		if (min==Double.NEGATIVE_INFINITY && max==Double.POSITIVE_INFINITY) {
			midValue= 0;
		}
		else if (min==Double.NEGATIVE_INFINITY) {
			midValue= Math.max (max-initialRange, (max-Double.MAX_VALUE)/2);
		}
		else if (max==Double.POSITIVE_INFINITY) {
			midValue= Math.min (min+initialRange,(Double.MAX_VALUE-min)/2);
		}

		this.minClosed= minClosed;
		this.maxClosed= maxClosed;
		this.canChangeMinType= canChangeMinType;
		this.canChangeMaxType= canChangeMaxType;
		this.canChangeMinValue= canChangeMinValue;
		this.canChangeMaxValue= canChangeMaxValue;
		
		paintDialog();
	}
	
	public IntervalSelectionDialog (BranchInterval interval) {
		this (interval.getLeft(), interval.getRight(), interval.isLeftClosed(), interval.isRightClosed(), false, false, false, false);
	}
	
	static double epsilon= EpsilonValueAproximation.value;
	
	// Add all the UI components  
	private void paintDialog() {
		JPanel panel= new JPanel();
		panel.setLayout (new FlowLayout());

		ArrayList<String> changeOptions= new ArrayList<String>();
		
		if (canChangeMinValue) {
			changeOptions.add("Left Value");
		}
		
		changeOptions.add ("Split Point");
		
		if (canChangeMaxValue) {
			changeOptions.add("Right Value");
		}

		comboWhatChange = new JComboBox (changeOptions.toArray());
		
		if (changeOptions.size()==1) {
			comboWhatChange.setEnabled (false);
		}

		// panel.add(new JLabel("Split Point"));
		panel.add (comboWhatChange);
		comboWhatChange.addActionListener (this);
	
		SpinnerModel model = new SpinnerNumberModel (midValue, min, max, 1);
		spinner = new JSpinner(model);
		spinner.setPreferredSize (new Dimension (90,spinner.getPreferredSize().height));
		spinner.addChangeListener (this);
		panel.add(spinner);
		
		panel.add(new JLabel("Step"));
		Double[] unitsList= { 0.001, 0.01, 0.1, 1.0, 10.0, 100.0, 1000.0, 10000.0 };
		unitsCombo = new JComboBox(unitsList);
		unitsCombo.setSelectedIndex(3);
		unitsCombo.setPrototypeDisplayValue(new Double("10000.0"));
		
		((JLabel) unitsCombo.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		unitsCombo.addActionListener (this);
		panel.add(unitsCombo);

		ArrayList<BranchInterval> options= new ArrayList<BranchInterval>();
		
		int index= 0;
		if (Math.abs(min-midValue)>epsilon) {
			changeRightValue[index++]= true;
			options.add (new BranchInterval (min, midValue, minClosed, false));
			changeRightValue[index++]= true;
			options.add (new BranchInterval (min, midValue, minClosed, true));

			if (canChangeMinType) {
				changeRightValue[index++]= true;
				options.add (new BranchInterval (min, midValue, !minClosed, false));
				changeRightValue[index++]= true;
				options.add (new BranchInterval (min, midValue, !minClosed, true));			
			}
		}
		else if (minClosed) {
			changeRightValue[index++]= true;
			options.add (new BranchInterval (min, midValue, minClosed, true));			
		}
				
		if (Math.abs(min-max)>epsilon) {
			if (Math.abs(midValue-max)>epsilon) {
				changeRightValue[index++]= false;
				options.add (new BranchInterval (midValue, max, false, maxClosed));
				changeRightValue[index++]= false;
				options.add (new BranchInterval (midValue, max, true, maxClosed));
				
				if (canChangeMaxType) {
					changeRightValue[index++]= false;
					options.add (new BranchInterval (midValue, max, false, !maxClosed));
					changeRightValue[index++]= false;
					options.add (new BranchInterval (midValue, max, true, !maxClosed));
				}
			}
			else if (maxClosed) {
				changeRightValue[index++]= false;
				options.add (new BranchInterval (midValue, max, true, maxClosed));
			}
		}
		
		comboInterval= new JComboBox(options.toArray());
		((JLabel) comboInterval.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		
		JPanel buttonsPanel= new JPanel();
		buttonsPanel.setLayout(new FlowLayout());
		
		okButton= new JButton("Ok");
		okButton.addActionListener(this);
		buttonsPanel.add (okButton);
		
		cancelButton= new JButton("Cancel");
		cancelButton.addActionListener(this);
		buttonsPanel.add (cancelButton);

		getContentPane().setLayout (new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add (comboInterval);
		getContentPane().add (panel);
		getContentPane().add (buttonsPanel);
		
		setLocationRelativeTo (getParent());
		setSize(275, 125);
		setResizable(false);		
	}
	
	public BranchInterval getResult() {
		setVisible(true);

		return result;
	}
	
	public void actionPerformed(ActionEvent ae) {
		Object obj= ae.getSource();
		
		if (obj==unitsCombo) {
	        ((SpinnerNumberModel) spinner.getModel()).setStepSize ((Double) unitsCombo.getSelectedItem());
		}
		else if (obj==okButton) {
			result= (BranchInterval) comboInterval.getSelectedItem();
			setVisible(false);
		}
		else if (obj==cancelButton) {
			result= null;
			setVisible(false);
		}
	}


	
	public void stateChanged(ChangeEvent ae) {
		Object obj= ae.getSource();
		
		if (obj==spinner) {
			midValue= (Double) spinner.getValue();

			for (int i=0; i< comboInterval.getItemCount(); i++) {
				BranchInterval bi= ((BranchInterval) comboInterval.getItemAt(i));
				
				if (changeRightValue[i]) {
					bi.setRight (midValue);
				}
				else {
					bi.setLeft (midValue);					
				}
			}
			
			comboInterval.repaint();
		}		
	}
}
