package org.openmarkov.core.gui.dialog.node;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.plaf.basic.BasicBorders.RadioButtonBorder;

import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.gui.util.GUIDefaultStates;
import org.openmarkov.core.model.network.State;


/**
 * 
 * @author maryebra
 *
 */
@SuppressWarnings("serial")
public class StandarDomainPanel extends JPanel implements ItemListener{
	
	private  ButtonGroup buttonGroup = new ButtonGroup();
	private ArrayList<JRadioButton> radioButtons = new ArrayList<JRadioButton>();
	private StringResource selectableStringResource;
	public StandarDomainPanel() {
		initialize();
		repaint();
	}
	
	 public void initialize() {
		 setLayout (new BoxLayout(this, BoxLayout.Y_AXIS));
		// ButtonGroup buttonGroup = new ButtonGroup();
		 selectableStringResource =
		            StringResourceLoader.getUniqueInstance().getBundleSelectables();
		 String [] defaultStates = {selectableStringResource.getString("defaultStates.absentPresent.Text"),
				 selectableStringResource.getString("defaultStates.noYes.Text"),
				 selectableStringResource.getString("defaultStates.negativePositive.Text"),
				 selectableStringResource.getString("defaultStates.absentMildModerateSevere.Text"),
				 selectableStringResource.getString("defaultStates.lowMediumhigh.Text")};
		 //String  [] defaultStates = GUIDefaultStates.getListStrings();
		// String  [] defaultStates2 = GUIDefaultStates.getStringsLanguageDependent(GUIDefaultStates.getListStrings());
		for (String defaultState : defaultStates) {
			
			JRadioButton radioButton = new JRadioButton (defaultState);
			//radioButton.addItemListener(this);
			radioButtons.add(radioButton);
			buttonGroup.add(radioButton);
			add(radioButton, BorderLayout.CENTER);
		}
	
	 }

	@Override
	public void itemStateChanged(ItemEvent e) {
		String states =  ((JRadioButton)e.getItem()).getName();
		
		
	}
	public ButtonGroup getButtonGroup() {
		return buttonGroup;
	}

	public ArrayList<JRadioButton> getRadioButtons() {
		return radioButtons;
	}

}
