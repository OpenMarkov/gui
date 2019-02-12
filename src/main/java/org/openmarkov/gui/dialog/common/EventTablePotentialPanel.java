package org.openmarkov.gui.dialog.common;


import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.potential.EventTablePotential;
import org.openmarkov.core.model.network.potential.TablePotential;

@SuppressWarnings("serial") @PotentialPanelPlugin(potentialType = "EventTable")
public class EventTablePotentialPanel extends PotentialPanel {

    EventTablePotential potential;
    TablePotential tablePotential;
    TablePotentialPanel tablePotentialPanel;

    EventTablePotentialPanel(Node node){
        tablePotentialPanel = new TablePotentialPanel(node);
    }

    EventTablePotentialPanel(){
        tablePotentialPanel = new TablePotentialPanel();
    }

    //From abstrach
    public void setData(Node node){

    }
//From abstrach
    public void close(){}
}
