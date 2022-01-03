/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.dialog.common;

import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.potential.TableWithEvents;

/**
 * TransitionTablePotentialPanel for TransitionPotential
 *
 * @version 1.0 - cyago - 24/03/2019 - only one TableWithEventsPanel
 */
@SuppressWarnings("serial") @PotentialPanelPlugin( potentialType = "TransitionTable")
public class TransitionTablePotentialPanel
		extends TableWithEventsPanel {

	public TransitionTablePotentialPanel(Node node) {
		super(node, (TableWithEvents) node.getPotentials().get(0));

	}


}




