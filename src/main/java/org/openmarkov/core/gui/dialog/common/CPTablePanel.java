package org.openmarkov.core.gui.dialog.common;

import java.awt.BorderLayout;

import org.openmarkov.core.gui.dialog.node.ICIOptionsPanel;
import org.openmarkov.core.model.network.ProbNode;

@SuppressWarnings("serial")
public class CPTablePanel extends TablePotentialPanel{
	
	private ICIOptionsPanel iciOptionPanel;
	
	public CPTablePanel(ProbNode probNode, ICIOptionsPanel iciOptionPanel) {
		super(probNode);
		add(iciOptionPanel,BorderLayout.NORTH);
	}
	
	
}
