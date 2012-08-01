package org.openmarkov.core.gui.dialog.costeffectiveness;

import java.util.ArrayList;
import java.util.HashMap;

import org.openmarkov.core.exception.ImposedPoliciesException;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NotEnoughMemoryException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
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
		 expandedNetFactory.applyDiscountToUtilityNodes(discountRate);
		 ProbNet expandedNetwork = expandedNetFactory.getExtendedNet();
		 VariableElimination variableElimination;
		 try {
			 variableElimination = new VariableElimination(expandedNetwork);
			 ArrayList<Variable> conditioningVariables = new ArrayList<>();
			 conditioningVariables.add(probNet.getDecisionCriteriaVariable());
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
		expandedNetFactory.applyDiscountToUtilityNodes(discountRate); 
		this.expandedNetwork = expandedNetFactory.getExtendedNet(); 
		String baseName = variableOfInterest.getBaseName();
		ArrayList<Variable> variablesOfInterest = new ArrayList<>();
		ArrayList<ProbNode> expandedProbNetProbNodes = expandedNetwork.getProbNodes();
		for (ProbNode node :expandedProbNetProbNodes) {
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
	
 public ProbNet getExpandedNetwork() {
	 return expandedNetwork;
 }

 }
