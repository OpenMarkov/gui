package org.openmarkov.core.gui.dialog.network;


import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.LayoutStyle.ComponentPlacement;


import org.openmarkov.core.action.NetworkDefaultStatesEdit;
import org.openmarkov.core.action.VariableTypeConstraintEdit;
import org.openmarkov.core.exception.CanNotDoEditException;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.localize.StringResource;
import org.openmarkov.core.gui.localize.StringResourceLoader;
import org.openmarkov.core.gui.network.GUIDefaultStates;
import org.openmarkov.core.model.network.DefaultStates;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.State;
import org.openmarkov.core.model.network.constraint.OnlyContinuousVariables;
import org.openmarkov.core.model.network.constraint.OnlyDiscreteVariables;
import org.openmarkov.core.model.network.constraint.PNConstraint;


/**
 * Panel to set the definition of the variables of a network. It will have a
 * variable type group selector with two check boxes, and a drop-down list with
 * the default values for the nodes
 * 
 * @author jlgozalo
 * @version 1.0 jlgozalo initial
 */
public class NetworkVariablesPanel extends JPanel implements ItemListener {

	private ProbNet probNet;


	private JComboBox jComboBoxVariableType;

	/**
	 * constructor without construction parameters
	 */
	public NetworkVariablesPanel(ProbNet probNet) {

		dialogStringResource =
			StringResourceLoader.getUniqueInstance().getBundleDialogs();
		this.probNet = probNet;
		setName("NetworkVariablesPanel");
		initialize();

	}

	/**
	 * This method initialises this instance.
	 * 
	 * @param newNetwork
	 *            true if the network to show is new, otherwise false
	 * @wbp.parser.constructor
	 */
	public NetworkVariablesPanel(final boolean newNetwork) {

		dialogStringResource =
			StringResourceLoader.getUniqueInstance().getBundleDialogs();
		this.newNetwork = newNetwork;
		setName("NetworkVariablesPanel");
		initialize();

	}

	
	/**
	 * initialises the layout for this panel.
	 */
	private void initialize() {

		final GroupLayout groupLayout = new GroupLayout((JComponent) this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(26)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(getJLabelVariablesType(), GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(getJComboBoxVariableType(), GroupLayout.PREFERRED_SIZE, 194, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(getJLabelDefaultStates(), GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(getJComboBoxDefaultStates(), GroupLayout.PREFERRED_SIZE, 194, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(17, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(11)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getJLabelVariablesType(), GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
						.addComponent(getJComboBoxVariableType(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(6)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(getJLabelDefaultStates())
						.addComponent(getJComboBoxDefaultStates(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(224, Short.MAX_VALUE))
		);
		setLayout(groupLayout);

	}

	/**
	 * This method initialises jLabelVariablesType
	 * 
	 * @return the Variables Type JLabel
	 */
	private JLabel getJLabelVariablesType() {

		if (jLabelVariablesType == null) {
			jLabelVariablesType = new JLabel();
			jLabelVariablesType.setName("jLabelVariablesType");
			jLabelVariablesType.setText("a Label : ");
			jLabelVariablesType.setText(dialogStringResource
				.getString("NetworkVariablesPanel.jLabelVariablesType.Text"));
		}
		return jLabelVariablesType;
	}

	
	/**
	 * This method initialises jLabelDefaultStates.
	 * 
	 * @return the Default States JLabel
	 */
	private JLabel getJLabelDefaultStates() {

		if (jLabelDefaultStates == null) {
			jLabelDefaultStates = new JLabel();
			jLabelDefaultStates.setName("jLabelDefaultStates");
			jLabelDefaultStates.setText("a Label : ");
			jLabelDefaultStates.setText(dialogStringResource
				.getString("NetworkVariablesPanel.jLabelDefaultStates.Text"));
			jLabelDefaultStates.setLabelFor(jComboBoxDefaultStates);

		}
		return jLabelDefaultStates;
	}

	/**
	 * This method initialises jComboBoxDefaultStates.
	 * 
	 * @return the Default States JCombo Box
	 */
	private JComboBox getJComboBoxDefaultStates() {

		if (jComboBoxDefaultStates == null) {
			jComboBoxDefaultStates =
				new JComboBox(GUIDefaultStates.getListStrings());
			jComboBoxDefaultStates.setName("jComboBoxDefaultStates");
			//jComboBoxDefaultStates.addItemListener(this);
			
		}
		return jComboBoxDefaultStates;

	}
	
	/**
	 * This method initialises jComboBoxDefaultStates.
	 * 
	 * @return the Default States JCombo Box
	 */
	private JComboBox getJComboBoxVariableType() {

		if (jComboBoxVariableType == null) {
			
			jComboBoxVariableType =
				new JComboBox(getListOfTypes());
				jComboBoxVariableType.setName("jComboBoxVariableType");
				
				
		}
		
		return jComboBoxVariableType;

	}
	

	private String [] getListOfTypes() {
		//TODO only discrete variable are enable 
		String[] types = { dialogStringResource
		.getString("NetworkVariablesPanel.ConstraintVariableType.Items." +
				"onlydiscrete"), 
				dialogStringResource.getString( 
						"NetworkVariablesPanel.ConstraintVariableType." +
				"items.discreteandcontinuous")};
				
				
				/*, dialogStringResource.getString(
						"NetworkVariablesPanel.ConstraintVariableType.Items." +
						"onlycontinuous"),dialogStringResource.getString(
								"NetworkVariablesPanel.ConstraintVariableType." +
								"items.discreteandcontinuous")};*/
		return types;
	}


	/**
	 * This method fills the content of the fields from a network Properties
	 * (ProbNet object)
	 * 
	 */
	public void setFieldsFromProperties() {

		State [] states =  probNet.getDefaultStates();
				
		jComboBoxVariableType.removeItemListener(this);
		jComboBoxDefaultStates.removeItemListener(this);
		//TODO modificar el indice a 2 cuando la creacion de variables continuas
		//este implementado
		int index = 1;
		
		for (PNConstraint constraint:probNet.getConstraints()){
			if (constraint instanceof OnlyDiscreteVariables) {
				index = 0;
				break;
				}
			/*}else if (constraint instanceof OnlyContinuousVariables){
				index=1;
				break;
			}*/
		}
		
		
		jComboBoxVariableType.setSelectedIndex(index);
		
	
		jComboBoxDefaultStates.setSelectedIndex(DefaultStates
					.getIndex(states));
			
			jComboBoxVariableType.addItemListener(this);
			jComboBoxDefaultStates.addItemListener(this);
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5183671164848473079L;

	/**
	 * Label of the variables Type checkboxes
	 */
	private JLabel jLabelVariablesType = null;

	/**
	 * CheckBox to set the network to use Discrete Variable
	 */
	private JRadioButton jRadioButtonDiscreteVariable = null;

	/**
	 * CheckBox to set the network to use Continuous Variable
	 */
	private JRadioButton jRadioButtonContinuousVariable = null;

	/**
	 * Label of the default states field.
	 */
	private JLabel jLabelDefaultStates = null;

	/**
	 * Combobox where the user can choose the default states.
	 */
	private JComboBox jComboBoxDefaultStates = null;

	/**
	 * Dialog string resource.
	 */
	private final StringResource dialogStringResource;

	/**
	 * Specifies if the network whose adittionalProperties are edited is new.
	 */
	private boolean newNetwork = false;
	/**
	 * @return
	 */

	
	public void itemStateChanged(ItemEvent arg0) {
		
		VariableTypeConstraintEdit variableTypeCE=null;
		ItemSelectable itemSelectable = arg0.getItemSelectable();
		Object selected[] = itemSelectable.getSelectedObjects();
		String itemSelected = selected.length == 0 ? "null" :
			(String)selected[0];
		JComboBox comboBox= (JComboBox)arg0.getSource();
			
		if (comboBox.getName().equals("jComboBoxVariableType")){
			if (!(itemSelected==null) && arg0.getStateChange() == ItemEvent.
					SELECTED){
				if (itemSelected.equals(dialogStringResource
					.getString("NetworkVariablesPanel.ConstraintVariableType." +
							"Items.onlydiscrete"))) {
				
					variableTypeCE =
					new VariableTypeConstraintEdit(probNet,
							new OnlyDiscreteVariables());
					try {
						probNet.getPNESupport().announceEdit(variableTypeCE);
						probNet.getPNESupport().doEdit(variableTypeCE);
					} catch (ConstraintViolationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (CanNotDoEditException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (DoEditException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NotEnoughMemoryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NonProjectablePotentialException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (WrongCriterionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
								
				
				}else if (itemSelected.equals(dialogStringResource
					.getString("NetworkVariablesPanel.ConstraintVariableType." +
							"Items.onlycontinuous"))) {
				
					variableTypeCE =
					new VariableTypeConstraintEdit(probNet,
							new OnlyContinuousVariables());
					try {
						probNet.getPNESupport().announceEdit(variableTypeCE);
						probNet.getPNESupport().doEdit(variableTypeCE);
					} catch (ConstraintViolationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (CanNotDoEditException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (DoEditException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NotEnoughMemoryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NonProjectablePotentialException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (WrongCriterionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			
			}
		}else if (comboBox.getName().equals("jComboBoxDefaultStates")){
			//warning mpalacios relative function to options position. 
			//Review "otros" option		
				
			if (!(itemSelected==null) && arg0.getStateChange() == ItemEvent.
					SELECTED){
				int i= 0;
				State [] defaultStates = new State[DefaultStates.getByIndex(
						comboBox.getSelectedIndex()).length];
				for (String str : DefaultStates.getByIndex(
						comboBox.getSelectedIndex())){
					defaultStates[i] = new State(str);
					i++;
				}
				
				NetworkDefaultStatesEdit networkDefaultStatesEdit = 
					new NetworkDefaultStatesEdit(probNet, 
							defaultStates);
				try {
					probNet.getPNESupport().announceEdit(networkDefaultStatesEdit);
					probNet.getPNESupport().doEdit(networkDefaultStatesEdit);
				} catch (ConstraintViolationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CanNotDoEditException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DoEditException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotEnoughMemoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NonProjectablePotentialException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WrongCriterionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
				
		}
		
	}

}
