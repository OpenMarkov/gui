/*
 * Copyright 2012 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.core.gui.costeffectiveness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.exception.NodeNotFoundException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.gui.localize.StringDatabase;
import org.openmarkov.core.inference.BasicOperations;
import org.openmarkov.core.inference.InferenceAlgorithm;
import org.openmarkov.core.model.network.Criterion.CECriterion;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNetOperations;
import org.openmarkov.core.model.network.TemporalNetOperations;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.CycleLengthShift;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.UniformPotential;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDBranch;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDPotential;
import org.openmarkov.inference.tasks.VariableElimination.VECEADecision;
import org.openmarkov.inference.tasks.VariableElimination.VEResolution;
import org.openmarkov.inference.variableElimination.model.CEP;

/**
 * Cost effectiveness and temporal evolution calculator
 * 
 * @author myebra
 */
public class CostEffectivenessAnalysis {
	
	public static String DECISION_CRITERIA_VARIABLE = "CECriteria";
	
	protected ProbNet probNet;
	protected ProbNet expandedNetwork;
	protected TablePotential costEffectivenessTable;
	protected List<GUIIntervention> guiInterventions;
	protected List<GUIIntervention> frontierGUIInterventions;
	protected EvidenceCase evidence;
	protected Variable decision;
	/**
	 * Constructor for deterministic CEA
	 * 
	 * @param probNet
	 * @param evidence
	 * @param costDiscountRate
	 * @param effectivenessDiscountRate
	 * @param numCycles
	 * @param initialValues
	 * @param transitionTime
	 * @throws NotEvaluableNetworkException 
	 */
	public CostEffectivenessAnalysis(ProbNet probNet, EvidenceCase evidence) 
			throws NotEvaluableNetworkException {
		this.probNet = probNet;
		this.expandedNetwork = TemporalNetOperations.expandNetwork(probNet);
		this.evidence = expandEvidence(expandedNetwork, evidence);
		this.expandedNetwork = adaptMIDforCE(expandedNetwork, this.evidence);
		this.costEffectivenessTable = runFullAnalysis(expandedNetwork, this.evidence);
		this.guiInterventions = createInterventions(costEffectivenessTable);
		this.frontierGUIInterventions = calculateFrontierInterventions(guiInterventions);
		this.frontierGUIInterventions = calculateICERsOfFrontier(this.frontierGUIInterventions);
	}

	public CostEffectivenessAnalysis(ProbNet probNet, Variable decision, EvidenceCase evidence) 
			throws NotEvaluableNetworkException {
		this.probNet = probNet;
		this.decision = decision;

		VECEADecision veCEADecision = new VECEADecision(probNet,decision,evidence);

		this.costEffectivenessTable = createCostEffectivenessTable(veCEADecision.getCEPs());
		this.guiInterventions = createInterventions(costEffectivenessTable);
		this.frontierGUIInterventions = calculateFrontierInterventions(guiInterventions);
		this.frontierGUIInterventions = calculateICERsOfFrontier(this.frontierGUIInterventions);
	}

	private TablePotential createCostEffectivenessTable(CEP[] ceps) {
		TablePotential costEffectivenessTable =
				new TablePotential(Arrays.asList(getCECriteriaVariable(),decision), PotentialRole.UNSPECIFIED);
		int i = 0;
		for (CEP cep : ceps) {
			costEffectivenessTable.values[i] = cep.getCosts()[0];
			i++;
			costEffectivenessTable.values[i] = cep.getEffectivities()[0];
			i++;
		}
		return costEffectivenessTable;
	}

	public List<GUIIntervention> getGuiIntervention() {
		return guiInterventions;
	}

	public ProbNet getExpandedNetwork() {
		return expandedNetwork;
	}

	/**
	 * Returns the probNet.
	 * 
	 * @return the probNet.
	 */
	public ProbNet getProbNet() {
		return probNet;
	}


	public TablePotential getCostEffectivenessTable() {
		return costEffectivenessTable;
	}

	public List<GUIIntervention> getFrontierGUIInterventions() {
		return frontierGUIInterventions;
	}

	/**
	 * If there are temporal nodes within the network that requires evidence
	 * must be retrieved from CostEffectivenessDialog
	 * 
	 * @return EvidenceCase
	 */
	public static EvidenceCase expandEvidence(ProbNet probNet, EvidenceCase evidence) {
		EvidenceCase evidenceCase = new EvidenceCase(evidence);
		
//		for (Node timeDependentNode : getInitialTemporalNodesWithUniformPotentials(probNet)) {
//			Variable timeDependentVariable = timeDependentNode.getVariable();
//			Finding finding = new Finding(timeDependentVariable,
//					initialValues.get(timeDependentVariable));
//			try {
//				evidenceCase.addFinding(finding);
//			} catch (InvalidStateException | IncompatibleEvidenceException e) {
//				e.printStackTrace();
//			}
//		}
		// Extend evidence
		try {
			evidenceCase.extendEvidence(probNet);
		} catch (IncompatibleEvidenceException | InvalidStateException | WrongCriterionException e) {
			e.printStackTrace();
		}
		return evidenceCase;
	}

	protected TablePotential runFullAnalysis(ProbNet expandedNetwork, EvidenceCase evidence) {
		Map<Variable, List<Potential>> networkPotentials = new HashMap<>();
		ProbNet copyNetwork = expandedNetwork.copy();
        for(Node node : copyNetwork.getNodes())
        {
        	networkPotentials.put(node.getVariable(), node.getPotentials());
        }
        List<Node> sortedNodes = ProbNetOperations.sortTopologically(copyNetwork);
        removeIntermediateUtilityNodes(copyNetwork);
        try {
			tableProjectInNetwork(sortedNodes, networkPotentials, evidence);
		} catch (NonProjectablePotentialException | WrongCriterionException e) {
			e.printStackTrace();
		}
        TemporalNetOperations.applyTransitionTime(copyNetwork);
		return runAnalysis(copyNetwork, evidence);
	}
	
	public static void tableProjectInNetwork(List<Node> sortedNodes,
			Map<Variable, List<Potential>> networkPotentials, EvidenceCase evidence)
			throws NonProjectablePotentialException, WrongCriterionException {
		List<TablePotential> projectedPotentials = new ArrayList<>();
		for (Node node : sortedNodes) {
			List<Potential> sampledProjectedPotentials = new ArrayList<>();
			for (Potential originalPotential : networkPotentials.get(node.getVariable())) {
				List<TablePotential> newProjectedPotentials = originalPotential.tableProject(evidence,
						null, projectedPotentials);
				sampledProjectedPotentials.addAll(newProjectedPotentials);
				projectedPotentials.addAll(newProjectedPotentials);
			}
			node.setPotentials(sampledProjectedPotentials);
		}
	}	

	protected void removeIntermediateUtilityNodes(ProbNet network)
	{
		List<Node> utilityNodes = network.getNodes(NodeType.UTILITY);
        List<Node> nodesToDelete = new ArrayList<>();
        for (Node utilityNode : utilityNodes) {
            Variable utilityVariable = utilityNode.getVariable();
            if(BasicOperations.isSuperValueNode(utilityNode)) {
                List<Node> parents = utilityNode.getParents();
                List<Node> grandparents = new ArrayList<>();
                // remove links between supervalue nodes and their utility
                // parents
                for (Node parent : parents) {
                    if (parent.getNodeType() == NodeType.UTILITY) {
                    	network.removeLink(parent.getVariable(), utilityVariable, true);
                    	grandparents.addAll(parent.getParents());
                    	nodesToDelete.add(parent);
                    }
                }
                // add links between of new potential of supervalue nodes
                for (Node grandparent : grandparents) {
                	network.addLink(grandparent, utilityNode, true);
                }
            }
        }
        for(Node nodeToDelete : nodesToDelete)
        {
        	network.removeNode(nodeToDelete);
        }		
	}
	
	protected TablePotential runAnalysis(ProbNet expandedNetwork, EvidenceCase evidence) {
		TablePotential globalUtility = null;
		try {
//			InferenceAlgorithm inferenceAlgorithm = new VariableElimination(expandedNetwork);
			VEResolution inferenceAlgorithm = new VEResolution(expandedNetwork, evidence, getConditioningVariables(probNet));
//
//			// set evidence
//			inferenceAlgorithm.setPreResolutionEvidence(evidence);
			
			// set decisions and decision criteria as conditioning variables
			inferenceAlgorithm.setConditioningVariables(getConditioningVariables(probNet));

			// set heuristic for variable elimination
			inferenceAlgorithm.setHeuristicFactory(new CostEffectivenessHeuristicFactory());

			// Run inference
//			costEffectivenessTable = getCostEffectivenessTable(expandedNetwork, inferenceAlgorithm);
			globalUtility = inferenceAlgorithm.getGlobalUtility();		
			
			globalUtility = reorderVariables(globalUtility);
			
		} catch (Exception e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog (null,
					e1.getMessage (),
					StringDatabase.getUniqueInstance().getString ("CostEffectiveness.Error"),
                    JOptionPane.ERROR_MESSAGE);
		}  
		return globalUtility;
	}
	
	/**
	 * Get global utility of probNet
	 * @param expandedNetwork
	 * @param inferenceAlgorithm
	 * @return
	 * @throws IncompatibleEvidenceException
	 * @throws UnexpectedInferenceException
	 */
	private TablePotential getGlobalUtility(ProbNet expandedNetwork, InferenceAlgorithm inferenceAlgorithm)
			throws IncompatibleEvidenceException, UnexpectedInferenceException	{

		// Applies discounts
		TemporalNetOperations.applyDiscountToUtilityNodes(expandedNetwork);
		// TODO - Check the code and delete if not required
//		List<Node> utilityNodes = expandedNetwork.getNodes(NodeType.UTILITY);
//		List<TablePotential> utilityPotentials = new ArrayList<>();
//		for(Node node : utilityNodes)
//		{
//			for(Potential potential : node.getPotentials())
//			{
//				utilityPotentials.add((TablePotential)potential);
//			}
//		}
//		applyCEProcessing(utilityPotentials);
		
		return inferenceAlgorithm.getGlobalUtility();		
	}	

	// TODO - Check this method. Change applyDiscount to TemporalNetOperations.applyDiscount. Remove translateMonthly (if this method
	// musn't be removed, fix with (1+i) = (1+i_m)^m
//	protected void applyCEProcessing(List<TablePotential> utilityPotentials)
//	{
//
//		// apply discount 
//		
//		// View the T O D O with the old method 
////		applyDiscount(utilityPotentials);
//		
//		// Hack translate monthly utilities to yearly utilities
//		translateMonthlyUtilityPotentials(utilityPotentials);
//
//	}
	
	// TODO - Remove this method
//	private void applyDiscount(List<TablePotential> utilityPotentials)
//	{
//		for (TablePotential utilityPotential : utilityPotentials) {
//			Variable utilityVariable = utilityPotential.getUtilityVariable();
//			if (utilityVariable.isTemporal()) {
//				boolean isCost = utilityVariable.getDecisionCriterion().getCriterionName()
//						.equalsIgnoreCase("cost");
//				double discount = isCost ? costDiscount : effectivenessDiscount;
//				discount = Math.pow((1.0 + (discount / 100.0)), utilityVariable.getTimeSlice());
//				for (int i = 0; i < utilityPotential.values.length; ++i) {
//					utilityPotential.values[i] /= discount;
//				}
//			}
//		}
//	}
	
	private List<Variable> getConditioningVariables(ProbNet probNet)
	{
		List<Variable> conditioningVariables = new ArrayList<>();
		try {
			conditioningVariables.add(expandedNetwork.getVariable(DECISION_CRITERIA_VARIABLE));
		} catch (NodeNotFoundException e) {
			e.printStackTrace();
		}
		List<Node> decisionNodes = probNet.getNodes(NodeType.DECISION);
		for (Node decisionNode : decisionNodes) {
			if (!decisionNode.hasPolicy()) {
				conditioningVariables.add(decisionNode.getVariable());
			}
		}
		return conditioningVariables;
	}
	
	

	
	private List<GUIIntervention> createInterventions(TablePotential globalUtility) {
		// Reorder variables to force decision criteria to be the conditioned
		// variable
		List<GUIIntervention> guiIntervention = new ArrayList<>();
		int[] dimensions = TablePotential.calculateDimensions(globalUtility.getVariables());
		List<Variable> decisions = globalUtility.getVariables();
		int[] offsets = TablePotential.calculateOffsets(dimensions);
		double[] values = globalUtility.values;
		// each column of data is an intervention
		for (int i = 0; i < values.length; i += 2) {
			double cost = values[i];
			double effectiveness = values[i + 1];
			StringBuilder description = new StringBuilder();
			for (int j = 1; j < decisions.size(); ++j) {
				String decisionName = decisions.get(j).getName();
				String stateName = decisions.get(j).getStateName(
						(i / offsets[j]) % decisions.get(j).getNumStates());
				description.append(decisionName + " = " + stateName + "; ");
			}
			if (description.length() == 0) {
				description.append("Baseline");
			}
			GUIIntervention guiIntervention2 = new GUIIntervention(description.toString(), cost,
					effectiveness);
			guiIntervention.add(guiIntervention2);
		}
		return guiIntervention;
	}

	/**
	 * Reorder variables to make sure decision criteria is the conditioned
	 * variable
	 * 
	 * @param analysisResult
	 * @return
	 */
	protected TablePotential reorderVariables(TablePotential analysisResult) {
		List<Variable> newOrderVariables = new ArrayList<>();
		List<Variable> chanceVariables = new ArrayList<>();
		List<Variable> decisionVariables = new ArrayList<>();
		Variable decisionCriteriaVariable = null;
		for (Variable variable : analysisResult.getVariables()) {
			if (variable.getName().equals(DECISION_CRITERIA_VARIABLE)) {
				decisionCriteriaVariable = variable;
			} else {
				NodeType nodeType = expandedNetwork.getNode(variable).getNodeType();
				if(nodeType == NodeType.CHANCE)
					chanceVariables.add(variable);
				else if (nodeType == NodeType.DECISION)
					decisionVariables.add(variable);
			}
		}
		newOrderVariables.add(decisionCriteriaVariable);
		newOrderVariables.addAll(decisionVariables);
		newOrderVariables.addAll(chanceVariables);
		return DiscretePotentialOperations.reorder(analysisResult, newOrderVariables);
	}

	/**
	 * @param allGUIInterventions
	 * @return
	 */
	protected List<GUIIntervention> calculateFrontierInterventions(List<GUIIntervention> allGUIInterventions) {
		// 0) Create auxiliar variables
		List<GUIIntervention> remainingGUIInterventions = new ArrayList<GUIIntervention>(allGUIInterventions);
		List<GUIIntervention> frontierGUIInterventions = new ArrayList<GUIIntervention>();
		// 1) Get cheapest intervention
		GUIIntervention cheapestGUIIntervention = allGUIInterventions.get(0);
		for (int i = 1; i < allGUIInterventions.size(); i++) {
			GUIIntervention guiIntervention = allGUIInterventions.get(i);
			if ((guiIntervention.cost < cheapestGUIIntervention.cost)
					|| (guiIntervention.cost == cheapestGUIIntervention.cost && guiIntervention.effectiveness > cheapestGUIIntervention.effectiveness)) {
				cheapestGUIIntervention = guiIntervention;
			}
		}
		frontierGUIInterventions.add(cheapestGUIIntervention);
		remainingGUIInterventions.remove(cheapestGUIIntervention);
		while (!remainingGUIInterventions.isEmpty()) {
			// Remove interventions with minor effectiveness
			List<GUIIntervention> toRemove = new ArrayList<GUIIntervention>();
			for (GUIIntervention guiIntervention : remainingGUIInterventions) {
				if (guiIntervention.effectiveness <= cheapestGUIIntervention.effectiveness) {
					toRemove.add(guiIntervention);
				}
			}
			for (GUIIntervention guiIntervention : toRemove) {
				remainingGUIInterventions.remove(guiIntervention);
			}
			// Get smallest ICER from minor intervention
			double smallestICER = Double.POSITIVE_INFINITY;
			GUIIntervention candidateGUIIntervention = null;
			for (GUIIntervention guiIntervention : remainingGUIInterventions) {
				double ICER = (guiIntervention.cost - cheapestGUIIntervention.cost)
						/ (guiIntervention.effectiveness - cheapestGUIIntervention.effectiveness);
				if (ICER < smallestICER) {
					candidateGUIIntervention = guiIntervention;
					smallestICER = ICER;
				}
			}
			if (!remainingGUIInterventions.isEmpty()) {
				candidateGUIIntervention.iCER = smallestICER;
				frontierGUIInterventions.add(candidateGUIIntervention);
				remainingGUIInterventions.remove(candidateGUIIntervention);
				cheapestGUIIntervention = candidateGUIIntervention;
			}
		}
		return frontierGUIInterventions;
	}

	/**
	 * @param frontierGUIInterventions
	 *            . <code>ArrayList</code> of <code>Intervention</code>
	 * @return Interventions with incremental CE ratio. <code>ArrayList</code>
	 *         of <code>Intervention</code>
	 */
	private List<GUIIntervention> calculateICERsOfFrontier(List<GUIIntervention> frontierGUIInterventions) {
		List<GUIIntervention> interventionsWithICERs = null;
		if (frontierGUIInterventions != null) {
			int size = frontierGUIInterventions.size();
			interventionsWithICERs = new ArrayList<GUIIntervention>();
			interventionsWithICERs.add(frontierGUIInterventions.get(0));
			// calculates the ICER of each intervention except the first one
			for (int i = 1; i < size; i++) {
				GUIIntervention previousGUIIntervention = frontierGUIInterventions.get(i - 1);
				GUIIntervention guiIntervention = frontierGUIInterventions.get(i);
				guiIntervention.calculateICER(previousGUIIntervention);
				interventionsWithICERs.add(guiIntervention);
			}
		}
		return interventionsWithICERs;
	}

	protected void extendEvidence(ProbNet extendedNetwork) {
		try {
			evidence.extendEvidence(extendedNetwork);
		} catch (IncompatibleEvidenceException | InvalidStateException | WrongCriterionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adapts the concise network for performing cost-effectiveness analysis.
	 * Adds decisionCriteria node to the network and makes all utility nodes
	 * children of it
	 * 
	 * @param expandedNetwork
	 * @throws Exception 
	 */
	public static ProbNet adaptMIDforCE(ProbNet expandedNetwork,
			EvidenceCase evidence) throws NotEvaluableNetworkException {

		// Convert numeric variables
		expandedNetwork = ProbNetOperations
				.convertNumericalVariablesToFS(expandedNetwork, evidence);

		List<String> decisionCriteriaNames = new ArrayList<>();
		for (int i = 0; i < expandedNetwork.getDecisionCriteria().size(); i++) {
			String decisionCriterion = expandedNetwork.getDecisionCriteria().get(i).getCriterionName();
			if (decisionCriterion.equalsIgnoreCase("cost")
					|| decisionCriterion.equalsIgnoreCase("effectiveness")) {
				decisionCriteriaNames.add(decisionCriterion);
			}
		}
		if (decisionCriteriaNames.size() != 2) {
			// TODO propagate exception
			// throw new
			// Exception("For cost effectiveness analysis performance network's decision criteria must be cost and effectiveness");
		}
		
		// make all utility nodes of the expanded probNet children of the
		// decision criteria node
		Variable decisionCriteriaVariable = getCECriteriaVariable();
		Node decisionCriteriaNode = expandedNetwork.addNode(decisionCriteriaVariable, NodeType.DECISION);
		for (Node utilityNode : BasicOperations.getTerminalUtilityNodes(expandedNetwork)) {
			expandedNetwork.addLink(decisionCriteriaNode, utilityNode, true);
			if(utilityNode.getVariable().getDecisionCriterion() == null)
			{
				throw new NotEvaluableNetworkException("Utility node " + utilityNode.getName() + " does not have a decision criterion");
			}
			
			// TODO - This is an old method used in old probNets for Cost-Effectiveness Analysis
			String decisionCriterion = utilityNode.getVariable().getDecisionCriterion().getCriterionName();
			if (decisionCriterion.equalsIgnoreCase("cost")
					|| decisionCriterion.equalsIgnoreCase("effectiveness")) {
				TreeADDPotential treeADDPotential = buildCETree(expandedNetwork, utilityNode,
						decisionCriteriaNode.getVariable());
				utilityNode.setPotential(treeADDPotential);
			}

			if(utilityNode.getVariable().getDecisionCriterion().getCECriterion().equals(CECriterion.Cost) ||
					utilityNode.getVariable().getDecisionCriterion().getCECriterion().equals(CECriterion.Effectiveness)){
				TreeADDPotential treeADDPotential = buildCETree(expandedNetwork, utilityNode,
						decisionCriteriaNode.getVariable());
				utilityNode.setPotential(treeADDPotential);
			}
		}
		
		return expandedNetwork;
	}
	

	/**
	 * Creates a variable with two states, "cost" and "effectiveness", to be used when performing CEA.
	 * It will act as a conditioning variable in utility potentials
	 * @return CE criteria variable
	 */
	private static Variable getCECriteriaVariable() {
		return new Variable(DECISION_CRITERIA_VARIABLE, CECriterion.Cost.toString(), CECriterion.Effectiveness.toString());
    }
//  TODO - Remove unused method
	
//	private static void translateMonthlyUtilityPotentials(List<TablePotential> utilityPotentials) {
//		for (TablePotential utilityPotential : utilityPotentials) {
//			Variable utilityVariable = utilityPotential.getUtilityVariable();
//			if (utilityVariable.getUnit().string.equals("months")) {
//				translateMonthlyUtilityPotential(utilityPotential);
//			}
//		}
//	}
//	
//	private static void translateMonthlyUtilityPotential(Potential potential) {
//		if (potential instanceof TablePotential) {
//			double[] potentialValues = ((TablePotential) potential).getValues();
//			for (int j = 0; j < potentialValues.length; j++) {
//				potentialValues[j] = potentialValues[j] * 12;
//			}
//		} else if (potential instanceof TreeADDPotential) {
//			TreeADDPotential treeADD = (TreeADDPotential) potential;
//			for (TreeADDBranch branch : treeADD.getBranches()) {
//				translateMonthlyUtilityPotential(branch.getPotential());
//			}
//		}
//	}

	/**
	 * Within a Markov process for CE purposes it is important to detect whether
	 * there are or not numerical temporal variables with a CycleLengthShift
	 * potential in their second slice. These special nodes represent a temporal
	 * dependency that might be a relaxation of Markov assumption for SemiMarkov
	 * models or just a time dependence to introduce time varying transition
	 * from a life table.
	 * 
	 * @return a List with these special nodes in first slice of the compact
	 *         network
	 */
	public static List<Node> getInitialTemporalNodesWithUniformPotentials(ProbNet probNet) {
		// this array includes also Age node if exists
		List<Node> numericTemporalNodes = new ArrayList<>();
		List<Node> nodes = probNet.getNodes();
		// looking for temporal numerical variables in the first slice
		for (Node firstSliceNode : nodes) {
			Variable firstSliceVariable = firstSliceNode.getVariable();
			if (firstSliceVariable.isTemporal()
					&& firstSliceVariable.getVariableType() == VariableType.NUMERIC
					&& firstSliceVariable.getTimeSlice() == 0
					&& (firstSliceNode.getPotentials().isEmpty() || firstSliceNode.getPotentials()
							.get(0) instanceof UniformPotential)) {
				// look for the second slice to check if it has a
				// CycleLengthShift potential
				for (Node secondSliceNode : nodes) {
					Variable secondSliceVariable = secondSliceNode.getVariable();
					if (secondSliceVariable.isTemporal()
							&& secondSliceVariable.getVariableType() == VariableType.NUMERIC
							&& secondSliceVariable.getTimeSlice() == 1
							&& secondSliceVariable.getBaseName().equals(
									firstSliceVariable.getBaseName())) {
						if (secondSliceNode.getPotentials().get(0) instanceof CycleLengthShift) {
							numericTemporalNodes.add(firstSliceNode);
							break;
						}
					}
				}
			}
		}
		return numericTemporalNodes;
	}

	/**
	 * @param decisionCriteria
	 * @param treeVariables
	 * @param utility
	 * @param utilNode
	 * @param decisionCriteriaName
	 * @param otherDecisionCriteriaName
	 * @return A TreeADD for the utility potential where the branch of the
	 *         criteria of the node is the old utility table, and the branch of
	 *         the other criteria is 0.
	 */
	private static TreeADDPotential buildCETree(ProbNet probNet, Node utilNode,
			Variable ceCriteriaVariable) {
		
		CECriterion ceCriterion = utilNode.getVariable().getDecisionCriterion().getCECriterion();
		
//		if (ceCriterion != CECriterion.Null){
//			
//		}
		
		Potential utilityPotential = utilNode.getPotentials().get(0);
		List<Variable> treeVariables = utilityPotential.getVariables();
		treeVariables.add(ceCriteriaVariable);
		String decisionCriterion = null;
		String otherDecisionCriterion = null;
		// TODO - Check old method used in cost-effectiveness analysis for old probNets
		if(utilNode.getVariable().getDecisionCriterion().getCriterionName().equals("cost") ||
				utilNode.getVariable().getDecisionCriterion().getCriterionName().equals("effectiveness")){
			decisionCriterion = utilNode.getVariable().getDecisionCriterion().getCriterionName();
			otherDecisionCriterion = decisionCriterion.equalsIgnoreCase("cost") ? "effectiveness"
					: "cost";	
		}else{
			// New method used in cost-effectiveness analysis for new probNets
			if(utilNode.getVariable().getDecisionCriterion().getCECriterion().equals(CECriterion.Cost)){
				decisionCriterion =CECriterion.Cost.toString();
				otherDecisionCriterion = CECriterion.Effectiveness.toString();
			} else if(utilNode.getVariable().getDecisionCriterion().getCECriterion().equals(CECriterion.Effectiveness)){
				decisionCriterion = CECriterion.Effectiveness.toString();
				otherDecisionCriterion = CECriterion.Cost.toString();
			}
		}

		TreeADDPotential treeADDPotential = new TreeADDPotential(utilityPotential.getUtilityVariable(), treeVariables,
				ceCriteriaVariable);
		List<Variable> variables = new ArrayList<>();
		variables.add(ceCriteriaVariable);
		for (int j = 0; j < treeADDPotential.getBranches().size(); j++) {
			TreeADDBranch branch = treeADDPotential.getBranches().get(j);
			String branchStateName = branch.getBranchStates().get(0).getName();
			if (branchStateName.equalsIgnoreCase(decisionCriterion)) {
				branch.setPotential(utilityPotential);
			} else if (branchStateName.equalsIgnoreCase(otherDecisionCriterion)) {
				// zero potential
				branch.setPotential(new TablePotential(utilNode.getVariable(), new ArrayList<Variable>()));
			}
		}
		return treeADDPotential;
	}
}
