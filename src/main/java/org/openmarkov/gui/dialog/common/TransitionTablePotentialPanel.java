package org.openmarkov.gui.dialog.common;

import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.potential.TableWithEvents;

/**
 * TransitionTablePotentialPanel for TransitionPotential
 * @author cmyago
 * @version 1.0 - cmyago - 24/03/2019 - only one TableWithEventsPanel
 * 04/10/2023 FIXME Check if it complies with OM wiki
 */
@SuppressWarnings("serial") @PotentialPanelPlugin( potentialType = "TransitionTable")
public class TransitionTablePotentialPanel
		extends TableWithEventsPanel {

	public TransitionTablePotentialPanel(Node node) {
		super(node, (TableWithEvents) node.getPotentials().get(0));

	}


}




