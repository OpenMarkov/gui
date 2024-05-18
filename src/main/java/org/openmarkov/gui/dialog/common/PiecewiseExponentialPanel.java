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
import org.openmarkov.core.model.network.potential.PiecewiseExponentialPotential;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class implements the Panel for PiecewiseExponentialPotential. This panel has one combobox for selecting a variable
 *
 * FIXME --> replace the variable with a FunctionPotential;
 * FIXME --> replace the table
 * @author cmyago
 * @version 1.0 - cmyago - 04/11/2023
 */
@SuppressWarnings("serial") @PotentialPanelPlugin( potentialType = "Piecewise Exponential")
public class PiecewiseExponentialPanel
		extends PotentialPanel implements ItemListener{
	JPanel upperPanel;
	FunctionPanel functionPanel ;

	/**
	 * When checked the initial value is the first interval value
	 */
	JCheckBox initValueCheck;

	JCheckBox ratesCheck;


	private piecewiseExponentialTablePanel piecewiseExponentialTablePanel;

	protected Node node;
	protected PiecewiseExponentialPotential piecewiseExponentialPotential;


	/**
	 * Creates a DistributionTablePotentialPanel for eventNode. If eventNode stores a DistributionTablePotential, this is displayed.
	 * Otherwise an Exact distribution is displayed.
	 * @param node - event node which contains the DistributionTablePotential
	 */
	public PiecewiseExponentialPanel(Node node)
	{
		super();
		this.node = node;

		piecewiseExponentialPotential = (PiecewiseExponentialPotential) node.getPotentials().get(0);
		List<Variable> variables = node.getPotentials().get(0).getVariables();

		Object[][] data = new Object[piecewiseExponentialPotential.getPiecewiseTable().size()][3];
		int i=0;
		for (Map.Entry<Double,Double> entry: piecewiseExponentialPotential.getPiecewiseTable().entrySet()){
			data[i][0] = 0;
			//When getting new values from the table, they are Strings
			data[i][1] = String.valueOf( entry.getKey());
			data[i++][2] = String.valueOf( entry.getValue());
		}

		setLayout(new BorderLayout(10,10));
		upperPanel = new JPanel(new BorderLayout(10,10));
		upperPanel.setBorder(new EmptyBorder(10,100,10,100));
		functionPanel = new FunctionPanel(variables.subList(1,variables.size()), piecewiseExponentialPotential.getInitTimeFunction().getFunction());
		//FIXME text hardcoded
		initValueCheck = new JCheckBox("Use first interval");
		initValueCheck.addActionListener(actionEvent -> {if (initValueCheck.isSelected())
		functionPanel.setFunction( piecewiseExponentialTablePanel.valuesTable.getValueAt(0,1).toString());}
		);
		ratesCheck = new JCheckBox("Use rates");
		ratesCheck.setSelected(piecewiseExponentialPotential.isUseRates());
		JPanel checksPanel = new JPanel(new FlowLayout());
		checksPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		checksPanel.add(initValueCheck);
		checksPanel.add(ratesCheck);
		upperPanel.add(checksPanel, BorderLayout.NORTH);
		upperPanel.add(functionPanel, BorderLayout.SOUTH);
		this.add(upperPanel, BorderLayout.NORTH);
		piecewiseExponentialTablePanel = new piecewiseExponentialTablePanel(data);
		this.add(piecewiseExponentialTablePanel, BorderLayout.SOUTH);
		repaint();
	}


	@Override public boolean saveChanges() {
		boolean result = super.saveChanges();
		ProbNet probNet = node.getProbNet();
		PiecewiseExponentialPotential oldPotential = (PiecewiseExponentialPotential) node.getPotentials().get(0);
		PiecewiseExponentialPotential newPotential =  new PiecewiseExponentialPotential(oldPotential.getVariables(),oldPotential.getPotentialRole());
		//FIXME control values
		try {
			TreeMap<Double,Double> treeMapTable = new TreeMap<>();
			for (int i = 0; i < piecewiseExponentialTablePanel.valuesTable.getRowCount(); i++) {
				treeMapTable.put(Double.parseDouble(piecewiseExponentialTablePanel.valuesTable.getValueAt(i,1).toString()),
						Double.parseDouble(piecewiseExponentialTablePanel.valuesTable.getValueAt(i,2).toString()));
			}
			newPotential.setPiecewiseTable(treeMapTable);
			newPotential.setInitTimeFunction(new FunctionPotential(oldPotential.getVariables(), oldPotential.getPotentialRole(),functionPanel.getFunction()));
			newPotential.setUseRates(ratesCheck.isSelected());
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
	 *
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




