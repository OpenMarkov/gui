package org.openmarkov.core.gui.dialog.network;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.openmarkov.core.gui.localize.StringDatabase;

public class StandardCriteriaPanel extends JPanel {
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	* String database
	*/
	protected StringDatabase        stringDatabase = StringDatabase.getUniqueInstance ();
	private ButtonGroup             buttonGroup    = new ButtonGroup ();
	private ArrayList<JRadioButton> radioButtons   = new ArrayList<JRadioButton> ();

    public StandardCriteriaPanel ()
    {
        initialize ();
        repaint ();
    }
	
    public void initialize ()
    {
        setLayout (new BoxLayout (this, BoxLayout.Y_AXIS));
        // ButtonGroup buttonGroup = new ButtonGroup();
        String[] defaultStates = {
        		stringDatabase.getString ("defaultCriteria.costBenefitEuros.Text"),
        		stringDatabase.getString ("defaultCriteria.costBenefitPounds.Text"),
        		stringDatabase.getString ("defaultCriteria.costBenefitDollars.Text"),
                stringDatabase.getString ("defaultCriteria.costEffectivenessEurosQALY.Text"),
                stringDatabase.getString ("defaultCriteria.costEffectivenessPoundsQALY.Text"),
                stringDatabase.getString ("defaultCriteria.costEffectivenessDollarsQALY.Text")
                };
        // String [] defaultStates = GUIDefaultStates.getListStrings();
        // String [] defaultStates2 =
        // GUIDefaultStates.getStringsLanguageDependent(GUIDefaultStates.getListStrings());
        for (String defaultState : defaultStates)
        {
            JRadioButton radioButton = new JRadioButton (defaultState);
            // radioButton.addItemListener(this);
            radioButtons.add (radioButton);
            buttonGroup.add (radioButton);
            add (radioButton, BorderLayout.CENTER);
        }
    }
	
    public ButtonGroup getButtonGroup ()
    {
        return buttonGroup;
    }

    public ArrayList<JRadioButton> getRadioButtons ()
    {
        return radioButtons;
    }
	
}
