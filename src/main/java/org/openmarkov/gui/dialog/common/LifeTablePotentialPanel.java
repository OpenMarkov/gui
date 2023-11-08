package org.openmarkov.gui.dialog.common;

import org.openmarkov.core.action.PotentialChangeEdit;
import org.openmarkov.core.exception.ConstraintViolationException;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.FunctionPotential;
import org.openmarkov.core.model.network.potential.LifeTablePotential;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Map;

/**
 * This class implements the Panel for LifeTablePotential. This panel has one combobox for selecting a variable
 *
 * FIXME --> replace the variable with a FunctionPotential;
 * FIXME --> replace the table
 * @author cmyago
 * @version 1.0 - cmyago - 04/11/2023
 */
@SuppressWarnings("serial") @PotentialPanelPlugin( potentialType = "Life Table")
public class LifeTablePotentialPanel
		extends PotentialPanel implements ItemListener{

	FunctionPanel functionPanel ;
	private LifeTablePanel lifeTablePanel;

	protected Node node;
	protected LifeTablePotential lifeTablePotential;


	/**
	 * Creates a DistributionTablePotentialPanel for eventNode. If eventNode stores a DistributionTablePotential, this is displayed.
	 * Otherwise an Exact distribution is displayed.
	 * @param node - event node which contains the DistributionTablePotential
	 */
	public LifeTablePotentialPanel(Node node)
	{
		super();
		this.node = node;

		lifeTablePotential = (LifeTablePotential) node.getPotentials().get(0);
		List<Variable> variables = node.getPotentials().get(0).getVariables();

		Object[][] data = new Object[lifeTablePotential.getLifeTableValues().size()][3];
		int i=0;
		for (Map.Entry<Double,Double> entry: lifeTablePotential.getLifeTableValues().entrySet()){
			data[i][0] = 0;
			//When getting new values from the table, they are Strings
			data[i][1] = String.valueOf( entry.getKey());
			data[i++][2] = String.valueOf( entry.getValue());
		}

		setLayout(new BorderLayout(10,10));
		functionPanel = new FunctionPanel(variables.subList(1,variables.size()),lifeTablePotential.getInitTimeFunction().getFunction());

		lifeTablePanel = new LifeTablePanel(data);

		this.add(functionPanel, BorderLayout.NORTH);

		this.add(lifeTablePanel, BorderLayout.SOUTH);
		repaint();
	}


	@Override public boolean saveChanges() {
		boolean result = super.saveChanges();
		ProbNet probNet = node.getProbNet();
		LifeTablePotential oldPotential = (LifeTablePotential) node.getPotentials().get(0);
		LifeTablePotential newPotential =  new LifeTablePotential(oldPotential.getVariables(),oldPotential.getPotentialRole());
		//FIXME control values
		try {
			for (int i = 0; i < lifeTablePanel.valuesTable.getRowCount(); i++) {
				newPotential.getLifeTableValues().put(Double.parseDouble(lifeTablePanel.valuesTable.getValueAt(i,1).toString()),
						Double.parseDouble(lifeTablePanel.valuesTable.getValueAt(i,2).toString()));
			}
			newPotential.setInitTimeFunction(new FunctionPotential(oldPotential.getVariables(), oldPotential.getPotentialRole(),functionPanel.getFunction()));

		}catch(Exception e){
			e.printStackTrace();
			throw  new RuntimeException(e);
		}

		PotentialChangeEdit edit = new PotentialChangeEdit(probNet, oldPotential, newPotential);
		try {
			probNet.doEdit(edit);
		} catch (ConstraintViolationException | NonProjectablePotentialException | WrongCriterionException |
				 DoEditException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Changes the timeToEventPotential and DistributionTablePotentialPanel according to the new selected distribution
	 * or the new selected parametrization
	 * @param e jcDistribution with the new selected distribution or jcParametrization with the new selected parametrization
	 */
	@Override
	public void itemStateChanged(ItemEvent e)
	{

	}


	@Override
	public void setData(Node node) {

	}

	@Override
	public void close() {

	}
}




