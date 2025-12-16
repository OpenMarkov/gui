/*
 * Copyright (c) CISIAD, UNED, Spain,  2019. Licensed under the GPLv3 licence
 * Unless required by applicable law or agreed to in writing,
 * this code is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.gui.window.decisiontree;

import org.openmarkov.core.exception.*;
import org.openmarkov.gui.window.edition.NetworkPanel;
import org.openmarkov.gui.window.ZoomableContentPanel;

import java.awt.*;

@SuppressWarnings("serial") public class DecisionTreeWindow extends ZoomableContentPanel {
    
    private final DecisionTreePanel decisionTreePanel;
    private final NetworkPanel networkPanel;
    
    public DecisionTreeWindow(NetworkPanel networkPanel) throws IncompatibleEvidenceException, NotEvaluableNetworkException, NonProjectablePotentialException, PotentialOperationException.DifferentSizesInPotentialsAndStates, NotSupportedOperationException {
        this.setLayout(new BorderLayout());
        this.networkPanel = networkPanel;
        this.decisionTreePanel = new DecisionTreePanel(networkPanel.probNet);
        this.networkPanel.setDecisionTreeWindow(this);
        this.add(decisionTreePanel, BorderLayout.CENTER);
        this.setBackground(Color.blue);
    }
    
    @Override public boolean close() {
        this.networkPanel.setDecisionTreeWindow(null);
        return super.close();
    }
    
    @Override public double getZoom() {
        return decisionTreePanel.getZoom();
    }
    
    @Override public void setZoom(double zoom) {
        decisionTreePanel.setZoom(zoom);
    }
    
}
