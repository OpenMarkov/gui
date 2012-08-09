package org.openmarkov.core.gui.dialog.costeffectiveness;

import java.util.ArrayList;
import java.util.HashMap;

import org.openmarkov.core.exception.ImposedPoliciesException;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.inference.FactoryExpandedSMM;
import org.openmarkov.core.inference.InferenceOptions;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.TablePotential;

import org.openmarkov.inference.variableElimination.VariableElimination;

public class CostEffectivenessAnalysis {
	private double discountRate;
	private int numSlices;
	private ProbNet probNet;
	private ProbNet expandedNetwork;
	private ArrayList<Intervention> interventions;
	
 public CostEffectivenessAnalysis (ProbNet probNet, double discountRate, int numSlices) {
	 this.probNet = probNet;
	 this.discountRate = discountRate;
	 this.numSlices = numSlices;
 }
 
 public TablePotential costEffectivenessCalculator() {
	

	 TablePotential globalUtility = null;		  
	 FactoryExpandedSMM expandedNetFactory;
	 try {
		 expandedNetFactory = new FactoryExpandedSMM(probNet, numSlices, null, 200.0);
		 expandedNetFactory.adaptProbNetForCE();
		 InferenceOptions inferenceOptions = new InferenceOptions(probNet, null);
		 expandedNetFactory.applyDiscountToUtilityNodes(discountRate, inferenceOptions);
		 ProbNet expandedNetwork = expandedNetFactory.getExtendedNet();
		 VariableElimination variableElimination;
		 try {
			 variableElimination = new VariableElimination(expandedNetwork);
			 ArrayList<Variable> conditioningVariables = new ArrayList<>();
			 conditioningVariables.add(expandedNetwork.getDecisionCriteriaVariable());
			 ArrayList<ProbNode> decisionNodes = probNet.getProbNodes(NodeType.DECISION);
			 for (ProbNode decisionNode : decisionNodes) {
				 if (!decisionNode.hasPolicy()) {
					 conditioningVariables.add(decisionNode.getVariable());
				 }
			 }
			 variableElimination.setConditioningVariables(conditioningVariables);

			 try {
				 globalUtility =  variableElimination.getGlobalUtility();
			 } catch (IncompatibleEvidenceException e) {
				 // TODO Auto-generated catch block
				 e.printStackTrace();
			 } catch (UnexpectedInferenceException e) {
				 // TODO Auto-generated catch block
				 e.printStackTrace();
			 }
		 } catch (NotEvaluableNetworkException e1) {
			 // TODO Auto-generated catch block
			 e1.printStackTrace();
		 }

	 } catch (NotEnoughMemoryException e) {
		 // TODO Auto-generated catch block
		 e.printStackTrace();
	 }
 	 return globalUtility;
}
 
 public HashMap<Variable,TablePotential> traceTemporalEvolution(Variable variableOfInterest) throws ImposedPoliciesException {
	 ArrayList<ProbNode> decisionNodes = probNet.getProbNodes(NodeType.DECISION);
	 //check if all decision nodes has an imposed policy, potential set in probNode
	 for (ProbNode node : decisionNodes) {
		 if (node.getPotentials().size() == 0) {
			 throw new ImposedPoliciesException("All decision nodes must have an imposed policy");
		 }
	 }
	 HashMap<Variable,TablePotential> probsAndUtilities = null;
	 try {
		FactoryExpandedSMM expandedNetFactory =  new FactoryExpandedSMM(probNet, numSlices, null, 200.0);
		//InferenceOptions inferenceOptions = new InferenceOptions(probNet, null);
		//boolean isUtility = false;
		expandedNetFactory.applyDiscountToUtilityNodes(discountRate, null); 
		this.expandedNetwork = expandedNetFactory.getExtendedNet(); 
		String baseName = variableOfInterest.getBaseName();
		ArrayList<Variable> variablesOfInterest = new ArrayList<>();
		ArrayList<ProbNode> expandedProbNetProbNodes = expandedNetwork.getProbNodes();
		for (ProbNode node :expandedProbNetProbNodes) {
			/*if (node.getVariable().getBaseName().equals(baseName) &&
					node.getVariable().getTimeSlice()==0) {
				if (node.getNodeType() == NodeType.UTILITY) {
					isUtility = true;
				}
			}*/
			if (node.getVariable().getBaseName().equals(baseName)) {
				variablesOfInterest.add(node.getVariable());
			}
		}
		try {
			VariableElimination variableElimination = new VariableElimination(expandedNetwork);
			try {
				probsAndUtilities =  variableElimination.getProbsAndUtilities(variablesOfInterest);
				
			} catch (IncompatibleEvidenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnexpectedInferenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NotEvaluableNetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
	} catch (NotEnoughMemoryException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return probsAndUtilities;
 }
 
 /**
	 * @param allInterventions
	 * @return
	 */
	public ArrayList<Intervention> getFrontierIntervention(
			Intervention[] allInterventions) {
		// 0) Create auxiliar variables
		ArrayList<Intervention> remainingInterventions = 
			new ArrayList<Intervention>(allInterventions.length);
		for (int i = 0; i < allInterventions.length; i++) {
			remainingInterventions.add(allInterventions[i]);
		}
		ArrayList<Intervention> frontierInterventions = 
			new ArrayList<Intervention>();
		
		// 1) Get minor cost intervention
		Intervention minorIntervention = allInterventions[0];
		for (int i = 1; i < allInterventions.length; i++) {
			if ((allInterventions[i].cost < minorIntervention.cost) || 
					(allInterventions[i].cost == minorIntervention.cost &&
						allInterventions[i].effectiveness > 
							minorIntervention.effectiveness)) {
				minorIntervention = allInterventions[i]; 
			}
		}
		frontierInterventions.add(minorIntervention);
		remainingInterventions.remove(minorIntervention);
		
		while (!remainingInterventions.isEmpty()) {
			// Remove interventions with minor effectiveness
			ArrayList<Intervention> toRemove = new ArrayList<Intervention>();
			for (Intervention intervention : remainingInterventions) {
				if (intervention.effectiveness <= 
						minorIntervention.effectiveness) {
					toRemove.add(intervention);
				}
			}
			for (Intervention intervention : toRemove) {
				remainingInterventions.remove(intervention);
			}
			// Get minor ICER from minor intervention
			double bestICER = Double.POSITIVE_INFINITY;
			Intervention candidateIntervention = null;
			for (Intervention intervention : remainingInterventions) {
				double ICER = (intervention.cost - minorIntervention.cost) / 
						(intervention.effectiveness - 
								minorIntervention.effectiveness);
				if (ICER < bestICER) {
					candidateIntervention = intervention;
					bestICER = ICER;
				}
			}
			if (!remainingInterventions.isEmpty()) {
				candidateIntervention.iCER = bestICER;
				frontierInterventions.add(candidateIntervention);
				remainingInterventions.remove(candidateIntervention);
				minorIntervention = candidateIntervention;
			}
		}
		return frontierInterventions;
	}
	
	/** @param frontierInterventions. <code>ArrayList</code> of <code>Intervention</code>
	 * @return Interventions with incremental CE ratio.
	 * 	<code>ArrayList</code> of <code>Intervention</code> */
	ArrayList<Intervention> calculateIncrementalCERatiosOfFrontier(
			ArrayList<Intervention> frontierInterventions) {
		ArrayList<Intervention> interventionsWithICERs;
		if (frontierInterventions == null){
			interventionsWithICERs = null;
		}
		else{
			int size = frontierInterventions.size();
			interventionsWithICERs = new ArrayList<Intervention>();
			interventionsWithICERs.add(frontierInterventions.get(0));
			//calculates the ICER of each intervention except the first one
			for (int i=1; i < size; i++){
				Intervention previousAuxIntervention = frontierInterventions.get(i-1);
				Intervention auxIntervention = frontierInterventions.get(i);
				auxIntervention.calculateIncrementalCERatio(previousAuxIntervention);
				interventionsWithICERs.add(auxIntervention);
			}
		}
		return interventionsWithICERs;
		
	}

	
 public ArrayList<Intervention> getInterventions() {
	 return interventions;
 }
 
 public void setInterventions(ArrayList<Intervention> interventions) {
	this.interventions = interventions; 
 }
 public ProbNet getExpandedNetwork() {
	 return expandedNetwork;
 }

 }
