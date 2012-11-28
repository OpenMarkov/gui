package org.openmarkov.core.gui.action;

import java.util.ArrayList;
import java.util.List;

import org.openmarkov.core.action.SimplePNEdit;
import org.openmarkov.core.action.StateAction;
import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.StringWithProperties;

@SuppressWarnings("serial")
public class DecisionCriteriaEdit extends SimplePNEdit {
		
		private String criteriaName;
		private String newName;
		private int criteriaIndex;
		private StateAction stateAction;
		//private StringsWithProperties lastAgents;
		private List<StringWithProperties> lastCriteria;
		private Object [][]dataTable;
		private List<ProbNode> oldNodes;

		public DecisionCriteriaEdit(ProbNet probnet, StateAction stateAction, String newName, String agentName, Object [][]dataTable) {
			super(probnet);
			//probNet.getPNESupport().setWithUndo(true);
			this.criteriaName = agentName;
			this.stateAction = stateAction;
			this.newName = newName;
			if(probnet.getAgents() != null){
				//StringsWithProperties agents =  probnet.getAgents();
				List<StringWithProperties> decisionCriteria =  probnet.getDecisionCriteria();
				//this.lastAgents = probnet.getAgents().copy();
				this.lastCriteria = new ArrayList<StringWithProperties>(probnet.getDecisionCriteria());
			}else {
				this.lastCriteria = probnet.getDecisionCriteria();
			}
			this.dataTable = dataTable;
			this.oldNodes = new ArrayList<ProbNode>(probNet.getProbNodes());
		}

		@Override
		public void doEdit() throws DoEditException, NotEnoughMemoryException {
			//StringsWithProperties agents = probNet.getAgents();
			List<StringWithProperties> criterias = probNet.getDecisionCriteria();
			StringWithProperties criteria = null;
			switch (stateAction){
			case ADD:
				if (criterias == null) {
					//agents = new StringsWithProperties();
					criterias = new ArrayList<StringWithProperties>();
				}
				criteria = new StringWithProperties(criteriaName);
				//agents.put(agentName);
				criterias.add(criteria);
				probNet.setDecisionCriteria2(criterias);
				break;
			case REMOVE:
				for (StringWithProperties criterio : criterias) {
					if (criterio.getString().equals(criteriaName)) {
						criteria = criterio;
					}
				}
				criterias.remove(criteria);
				//TODO assign criteria to node 
				//it is also necessary to delete this criteria from the node it was assigned to
				/*if (criteria != null) {
					for (ProbNode node : probNet.getProbNodes()) {
						if (node.getVariable().getDecisionCriteria().getString().equals(criteriaName)) {
							node.getVariable().setDecisionCriteria(null);
						} 
					}
				}*/
				
				if (criterias.size() == 0) {
					criterias = null;
				}
				probNet.setDecisionCriteria2(criterias);
				break;
			case DOWN:
				//StringsWithProperties newAgentsDown = new StringsWithProperties();
				ArrayList<StringWithProperties> newCriteriasDown = new ArrayList<StringWithProperties>();
				for (int i = 0; i < dataTable.length; i++) {
					//newAgentsDown.put((String)dataTable[i][0]);
					newCriteriasDown.add(new StringWithProperties((String)dataTable[i][0]));
				}
				probNet.setDecisionCriteria2(newCriteriasDown);
				break;
			case UP:
				//StringsWithProperties newAgentsUp = new StringsWithProperties();
				ArrayList<StringWithProperties> newCriteriasUp = new ArrayList<StringWithProperties>();
				for (int i = 0; i < dataTable.length; i++) {
					//newAgentsUp.put((String)dataTable[i][0]);
					newCriteriasUp.add(new StringWithProperties((String)dataTable[i][0]));
				}
				probNet.setDecisionCriteria2(newCriteriasUp);
				break;
			case RENAME:
				//agents.rename(agentName, newName);
				//StringsWithProperties newAgentsRename = new StringsWithProperties();
				ArrayList<StringWithProperties> newCriteriasRename = new ArrayList<StringWithProperties>();
				for (int i = 0; i < dataTable.length; i++) {
					//newAgentsRename.put((String)dataTable[i][0]);
					newCriteriasRename.add(new StringWithProperties((String)dataTable[i][0]));
				}
				probNet.setDecisionCriteria2(newCriteriasRename);
				break;
				
			}
			
			
		}
		@Override
		public void undo() {
			super.undo();
			probNet.setDecisionCriteria2(lastCriteria);
			//TODO restaurate criteria in nodes
		}

}
