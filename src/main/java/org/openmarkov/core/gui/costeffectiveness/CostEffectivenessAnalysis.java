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

import org.openmarkov.core.exception.ImposedPoliciesException;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.exception.NonProjectablePotentialException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.inference.BasicOperations;
import org.openmarkov.core.inference.InferenceAlgorithm;
import org.openmarkov.core.inference.MPADFactory;
import org.openmarkov.core.inference.TransitionTime;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.Node;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNetOperations;
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
import org.openmarkov.inference.variableElimination.VariableElimination;

/**
 * Cost effectiveness and temporal evolution calculator
 * 
 * @author myebra
 */
public class CostEffectivenessAnalysis {
	protected ProbNet probNet;
	protected double costDiscount;
	protected double effectivenessDiscount;
	protected TransitionTime transitionTime;
	protected int numSlices;
	protected ProbNet expandedNetwork;
	protected TablePotential globalUtility;
	protected List<Intervention> interventions;
	protected List<Intervention> frontierInterventions;
	protected EvidenceCase evidence;

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
	public CostEffectivenessAnalysis(ProbNet probNet, EvidenceCase evidence,
			double costDiscountRate, double effectivenessDiscountRate, int numCycles,
			Map<Variable, Double> initialValues, TransitionTime transitionTime) throws NotEvaluableNetworkException {
		this.probNet = probNet;
		this.costDiscount = costDiscountRate;
		this.effectivenessDiscount = effectivenessDiscountRate;
		this.numSlices = numCycles;
		this.evidence = getEvidenceFromNetwork(probNet, evidence, initialValues);
		this.transitionTime = transitionTime;
		this.expandedNetwork = buildExpandedNetwork();
		this.globalUtility = runFullAnalysis(expandedNetwork, this.evidence, transitionTime);
		this.interventions = createInterventions(globalUtility);
		this.frontierInterventions = calculateFrontierInterventions(interventions);
		this.frontierInterventions = calculateICERsOfFrontier(this.frontierInterventions);
	}
	
	public CostEffectivenessAnalysis(ProbNet probNet, EvidenceCase evidence,
			double costDiscountRate, double effectivenessDiscountRate, int numCycles,
			TransitionTime transitionTime) throws NotEvaluableNetworkException {
		this(probNet, evidence, costDiscountRate, effectivenessDiscountRate, numCycles, new HashMap<Variable, Double>(), transitionTime);
	}

	public Map<Variable, TablePotential> traceTemporalEvolution(Variable variableOfInterest)
			throws ImposedPoliciesException {
		List<Node> decisionNodes = probNet.getNodes(NodeType.DECISION);
		// check if all decision nodes have an imposed policy,
		// potential set in node
		for (Node node : decisionNodes) {
			if (node.getPotentials().size() == 0) {
				throw new ImposedPoliciesException("All decision nodes must have an imposed policy");
			}
		}
		Map<Variable, TablePotential> probsAndUtilities = null;
		try {
			MPADFactory expandedNetFactory = new MPADFactory(probNet, numSlices);
			extendEvidence(expandedNetFactory.getExtendedNetwork());
			this.expandedNetwork = expandedNetFactory.getExtendedNetwork();
			this.expandedNetwork = adaptMPADforCE(expandedNetFactory.getExtendedNetwork(), numSlices,
					evidence);
			// TODO apply changes for transitions at cycle start, end or half cycle
			translateMonthlyUtilities(expandedNetwork);
			applyDiscountToUtilityNodes(expandedNetwork, costDiscount, effectivenessDiscount);
			String baseName = variableOfInterest.getBaseName();
			List<Variable> variablesOfInterest = new ArrayList<>();
			List<Node> expandedProbNetNodes = expandedNetwork.getNodes();
			for (Node node : expandedProbNetNodes) {
				if (node.getVariable().getBaseName().equals(baseName)) {
					variablesOfInterest.add(node.getVariable());
				}
			}
			// Impose policy according to interest variable's decision criterion
			if (variableOfInterest.getDecisionCriterion() != null) {
				String decisionCriterion = variableOfInterest.getDecisionCriterion().getString();
				Variable decisionCriteriaVariable = expandedNetwork.getDecisionCriterionVariable();
				Node decisionCriteriaNode = expandedNetwork.getNode(expandedNetwork
						.getDecisionCriterionVariable());
				TablePotential decisionCriterionPolicy = new TablePotential(
						Arrays.asList(decisionCriteriaVariable), PotentialRole.POLICY);
				for (int i = 0; i < decisionCriterionPolicy.values.length; ++i) {
					try {
						decisionCriterionPolicy.values[i] = (decisionCriteriaVariable
								.getStateIndex(decisionCriterion) == i) ? 1 : 0;
					} catch (InvalidStateException e) {
						e.printStackTrace();
					}
				}
				decisionCriteriaNode.setPotential(decisionCriterionPolicy);
			}
			VariableElimination variableElimination = new VariableElimination(expandedNetwork);

			variableElimination.setPreResolutionEvidence(evidence);
			variableElimination.setHeuristicFactory(new CostEffectivenessHeuristicFactory());
			try {
				probsAndUtilities = variableElimination.getProbsAndUtilities(variablesOfInterest);
			} catch (IncompatibleEvidenceException | UnexpectedInferenceException e) {
				e.printStackTrace();
			}
		} catch (NotEvaluableNetworkException e) {
			e.printStackTrace();
		}
		return probsAndUtilities;
	}

	public List<Intervention> getInterventions() {
		return interventions;
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

	/**
	 * Returns the costDiscountRate.
	 * 
	 * @return the costDiscountRate.
	 */
	public double getCostDiscountRate() {
		return costDiscount;
	}

	/**
	 * Returns the effectivenessDiscountRate.
	 * 
	 * @return the effectivenessDiscountRate.
	 */
	public double getEffectivenessDiscountRate() {
		return effectivenessDiscount;
	}

	/**
	 * Returns the numSlices.
	 * 
	 * @return the numSlices.
	 */
	public int getNumSlices() {
		return numSlices;
	}

	public TablePotential getGlobalUtility() {
		return globalUtility;
	}

	public List<Intervention> getFrontierInterventions() {
		return frontierInterventions;
	}

	/**
	 * Build expanded network, adapt for CE and apply discount
	 * 
	 * @return
	 * @throws NotEvaluableNetworkException 
	 */
	private ProbNet buildExpandedNetwork() throws NotEvaluableNetworkException {
		MPADFactory expandedNetFactory = new MPADFactory(probNet, numSlices);
		ProbNet expandedNetwork = expandedNetFactory.getExtendedNetwork();
		expandedNetwork = adaptMPADforCE(expandedNetwork, numSlices, evidence);
		translateMonthlyUtilities(expandedNetwork);
		return expandedNetwork;
	}

	/**
	 * If there are temporal nodes within the network that requires evidence
	 * must be retrieved from CostEffectivenessDialog
	 * 
	 * @return EvidenceCase
	 */
	private EvidenceCase getEvidenceFromNetwork(ProbNet probNet, EvidenceCase evidence,
			Map<Variable, Double> initialValues) {
		EvidenceCase evidenceCase = new EvidenceCase(evidence);

		for (Node timeDependentNode : getShiftingTemporalNodes(probNet)) {
			Variable timeDependentVariable = timeDependentNode.getVariable();
			Finding finding = new Finding(timeDependentVariable,
					initialValues.get(timeDependentVariable));
			try {
				evidenceCase.addFinding(finding);
			} catch (InvalidStateException | IncompatibleEvidenceException e) {
				e.printStackTrace();
			}
		}
		return evidenceCase;
	}

	protected TablePotential runFullAnalysis(ProbNet expandedNetwork, EvidenceCase evidence, TransitionTime transitionTime) {
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
        
        applyTransitionTime(copyNetwork, transitionTime, numSlices);
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
			InferenceAlgorithm inferenceAlgorithm = new VariableElimination(expandedNetwork);
			
			// set evidence
			inferenceAlgorithm.setPreResolutionEvidence(evidence);
			
			// set decisions and decision criteria as conditioning variables
			inferenceAlgorithm.setConditioningVariables(getConditioningVariables(probNet));

			// set heuristic for variable elimination
			inferenceAlgorithm.setHeuristicFactory(new CostEffectivenessHeuristicFactory());

			// Run inference
			globalUtility = getGlobalUtility(expandedNetwork, inferenceAlgorithm);
			
		} catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException  e1) {
			e1.printStackTrace();
		}
		return reorderVariables(globalUtility);
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
		
		List<Node> utilityNodes = expandedNetwork.getNodes(NodeType.UTILITY);
		List<TablePotential> utilityPotentials = new ArrayList<>();
		for(Node node : utilityNodes)
		{
			for(Potential potential : node.getPotentials())
			{
				utilityPotentials.add((TablePotential)potential);
			}
		}
		
		applyCEProcessing(utilityPotentials);
		
		return inferenceAlgorithm.getGlobalUtility();		
	}	

	protected void applyCEProcessing(List<TablePotential> utilityPotentials)
	{
		// apply discount
		applyDiscount(utilityPotentials, costDiscount, effectivenessDiscount);
		
		// Hack translate monthly utilities to yearly utilities
		translateMonthlyUtilityPotentials(utilityPotentials);

	}
	
	private List<Variable> getConditioningVariables(ProbNet probNet)
	{
		List<Variable> conditioningVariables = new ArrayList<>();
		conditioningVariables.add(expandedNetwork.getDecisionCriterionVariable());
		List<Node> decisionNodes = probNet.getNodes(NodeType.DECISION);
		for (Node decisionNode : decisionNodes) {
			if (!decisionNode.hasPolicy()) {
				conditioningVariables.add(decisionNode.getVariable());
			}
		}
		return conditioningVariables;
	}
	
	public static void applyTransitionTime(ProbNet network, TransitionTime transitionTime, int numSlices)
	{
		List<Node> utilityNodes = network.getNodes(NodeType.UTILITY);
		List<Node> nodesToRemove = new ArrayList<>();
		if (transitionTime == TransitionTime.HALF) {
			// Half cycle correction
			Map<String, Node[]> temporalNodes = new HashMap<>(); 
			for(Node utilityNode : utilityNodes)
			{
				Variable utilityVariable = utilityNode.getVariable();
				if(utilityVariable.isTemporal() && 
						utilityVariable.getTimeSlice() > 0 &&
						utilityVariable.getDecisionCriterion().getString()
						.equalsIgnoreCase("effectiveness"))
				{
					if(!temporalNodes.containsKey(utilityVariable.getBaseName()))
						temporalNodes.put(utilityVariable.getBaseName(), new Node[numSlices + 1]);
					temporalNodes.get(utilityVariable.getBaseName())[utilityVariable.getTimeSlice()] = utilityNode; 
				}
			}
			for(Node[] tempNodes : temporalNodes.values())
			{
				for(int k = tempNodes.length - 1; k > 0; --k)
				{
					if(tempNodes[k] != null && tempNodes[k-1] != null)
					{
						Node utilityNode = tempNodes[k]; 
						Node previousCycleNode = tempNodes[k-1];
						List<Potential> currentCyclePotentials = utilityNode.getPotentials();
						List<Potential> previousCyclePotentials = previousCycleNode.getPotentials();
						List<Potential> newPotentials = new ArrayList<>();
						for(int i=0; i < utilityNode.getNumPotentials();++i)
						{
							TablePotential currentCyclePotential = (TablePotential) currentCyclePotentials.get(i);
							TablePotential previousCyclePotential = (TablePotential) previousCyclePotentials.get(i);
							TablePotential sumPotential = DiscretePotentialOperations.sum(Arrays.asList(currentCyclePotential, previousCyclePotential));
							sumPotential.setUtilityVariable(utilityNode.getVariable());
							for(int j=0; j<sumPotential.values.length;++j)
								sumPotential.values[j] /= 2;
							newPotentials.add(sumPotential);
						}
						
						utilityNode.setPotentials(newPotentials);
						for(Node parent : previousCycleNode.getParents())
						{
							network.addLink(parent, utilityNode, true);
						}
						
					}
				}
			}
			
		}
		if (transitionTime == TransitionTime.BEGINNING || transitionTime == TransitionTime.HALF) {
			// prune zero cycle utilities
			for (Node utilityNode : utilityNodes) {
				if (utilityNode.getVariable().getTimeSlice() == 0) {
					nodesToRemove.add(utilityNode);
				}
			}
		} else if (transitionTime == TransitionTime.END) {
			// Prune last cycle utilities
			for (Node utilityNode : utilityNodes) {
				if (utilityNode.getVariable().getTimeSlice() == numSlices) {
					nodesToRemove.add(utilityNode);
				}
			}
		}
		for(Node nodeToRemove : nodesToRemove)
		{
			network.removeNode(nodeToRemove);
		}
	}	
	
	private void applyDiscount(List<TablePotential> utilityPotentials, double costDiscount, double effectivenessDiscount)
	{
		for (TablePotential utilityPotential : utilityPotentials) {
			Variable utilityVariable = utilityPotential.getUtilityVariable();
			if (utilityVariable.isTemporal()) {
				boolean isCost = utilityVariable.getDecisionCriterion().getString()
						.equalsIgnoreCase("cost");
				double discount = isCost ? costDiscount : effectivenessDiscount;
				discount = Math.pow((1.0 + (discount / 100.0)), utilityVariable.getTimeSlice());
				for (int i = 0; i < utilityPotential.values.length; ++i) {
					utilityPotential.values[i] /= discount;
				}
			}
		}
	}
	
	private List<Intervention> createInterventions(TablePotential globalUtility) {
		// Reorder variables to force decision criteria to be the conditioned
		// variable
		List<Intervention> interventions = new ArrayList<>();
		int[] dimensions = TablePotential.calculateDimensions(globalUtility.getVariables());
		List<Variable> decisions = globalUtility.getVariables();
		int[] offsets = TablePotential.calculateOffsets(dimensions);
		double[] values = globalUtility.values;
		// each column of data is an intervention
		for (int i = 0; i < values.length; i += 2) {
			double cost = values[i];
			double effectiveness = values[i + 1];
			StringBuffer description = new StringBuffer();
			for (int j = 1; j < decisions.size(); ++j) {
				String decisionName = decisions.get(j).getName();
				String stateName = decisions.get(j).getStateName(
						(i / offsets[j]) % decisions.get(j).getNumStates());
				description.append(decisionName + " = " + stateName + "; ");
			}
			if (description.length() == 0) {
				description.append("Baseline");
			}
			Intervention intervention = new Intervention(description.toString(), cost,
					effectiveness);
			interventions.add(intervention);
		}
		return interventions;
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
		for (Variable variable : analysisResult.getVariables()) {
			if (variable.getName().equals("Decision Criterion")) {
				newOrderVariables.add(0, variable);
			} else {
				newOrderVariables.add(variable);
			}
		}
		return DiscretePotentialOperations.reorder(analysisResult, newOrderVariables);
	}

	/**
	 * @param allInterventions
	 * @return
	 */
	protected List<Intervention> calculateFrontierInterventions(List<Intervention> allInterventions) {
		// 0) Create auxiliar variables
		List<Intervention> remainingInterventions = new ArrayList<Intervention>(allInterventions);
		List<Intervention> frontierInterventions = new ArrayList<Intervention>();
		// 1) Get minor cost intervention
		Intervention minorIntervention = allInterventions.get(0);
		for (int i = 1; i < allInterventions.size(); i++) {
			Intervention intervention = allInterventions.get(i);
			if ((intervention.cost < minorIntervention.cost)
					|| (intervention.cost == minorIntervention.cost && intervention.effectiveness > minorIntervention.effectiveness)) {
				minorIntervention = intervention;
			}
		}
		frontierInterventions.add(minorIntervention);
		remainingInterventions.remove(minorIntervention);
		while (!remainingInterventions.isEmpty()) {
			// Remove interventions with minor effectiveness
			List<Intervention> toRemove = new ArrayList<Intervention>();
			for (Intervention intervention : remainingInterventions) {
				if (intervention.effectiveness <= minorIntervention.effectiveness) {
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
				double ICER = (intervention.cost - minorIntervention.cost)
						/ (intervention.effectiveness - minorIntervention.effectiveness);
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

	/**
	 * @param frontierInterventions
	 *            . <code>ArrayList</code> of <code>Intervention</code>
	 * @return Interventions with incremental CE ratio. <code>ArrayList</code>
	 *         of <code>Intervention</code>
	 */
	private List<Intervention> calculateICERsOfFrontier(List<Intervention> frontierInterventions) {
		List<Intervention> interventionsWithICERs = null;
		if (frontierInterventions != null) {
			int size = frontierInterventions.size();
			interventionsWithICERs = new ArrayList<Intervention>();
			interventionsWithICERs.add(frontierInterventions.get(0));
			// calculates the ICER of each intervention except the first one
			for (int i = 1; i < size; i++) {
				Intervention previousIntervention = frontierInterventions.get(i - 1);
				Intervention intervention = frontierInterventions.get(i);
				intervention.calculateICER(previousIntervention);
				interventionsWithICERs.add(intervention);
			}
		}
		return interventionsWithICERs;
	}

	protected void extendEvidence(ProbNet extendedNetwork) {
		try {
			evidence.extendEvidence(extendedNetwork, 1);
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
	public static ProbNet adaptMPADforCE(ProbNet expandedNetwork, int numSlices,
			EvidenceCase evidence) throws NotEvaluableNetworkException {
		// Extend evidence
		extendEvidence(expandedNetwork, evidence);

		// Convert numeric variables
		expandedNetwork = ProbNetOperations
				.convertNumericalVariablesToFS(expandedNetwork, evidence);

		List<String> decisionCriteriaNames = new ArrayList<>();
		for (int i = 0; i < expandedNetwork.getDecisionCriteria().size(); i++) {
			String decisionCriterion = expandedNetwork.getDecisionCriteria().get(i).getString();
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
		expandedNetwork.setDecisionCriteria(decisionCriteriaNames);
		// make all utility nodes of the expanded probNet children of the
		// decision criteria node
		Node decisionCriteriaNode = new Node(expandedNetwork,
				expandedNetwork.getDecisionCriterionVariable(), NodeType.DECISION);
		expandedNetwork.addNode(decisionCriteriaNode);
		for (Node utilityNode : BasicOperations.getTerminalUtilityNodes(expandedNetwork)) {
			expandedNetwork.addLink(decisionCriteriaNode, utilityNode, true);
			if(utilityNode.getVariable().getDecisionCriterion() == null)
			{
				throw new NotEvaluableNetworkException("Utility node " + utilityNode.getName() + " does not have a decision criterion");
			}
			String decisionCriterion = utilityNode.getVariable().getDecisionCriterion().getString();
			if (decisionCriterion.equalsIgnoreCase("cost")
					|| decisionCriterion.equalsIgnoreCase("effectiveness")) {
				TreeADDPotential treeADDPotential = buildCETree(expandedNetwork, utilityNode,
						decisionCriteriaNode.getVariable());
				utilityNode.setPotential(treeADDPotential);
			}
		}
		return expandedNetwork;
	}

	public static void translateMonthlyUtilities(ProbNet probNet) {

		// apply discount rate for all temporal utility nodes in the expanded
		// network
		List<Node> utilityExpandedNodes = probNet.getNodes(NodeType.UTILITY);
		for (Node utilityNode : utilityExpandedNodes) {
			if (utilityNode.getVariable().getUnit().string.equals("months")) {
				translateMonthlyUtilityPotential(utilityNode.getPotentials().get(0));
			}
		}
	}

	private static void translateMonthlyUtilityPotentials(List<TablePotential> utilityPotentials) {
		for (TablePotential utilityPotential : utilityPotentials) {
			Variable utilityVariable = utilityPotential.getUtilityVariable();
			if (utilityVariable.getUnit().string.equals("months")) {
				translateMonthlyUtilityPotential(utilityPotential);
			}
		}
	}
	
	private static void translateMonthlyUtilityPotential(Potential potential) {
		if (potential instanceof TablePotential) {
			double[] potentialValues = ((TablePotential) potential).getValues();
			for (int j = 0; j < potentialValues.length; j++) {
				potentialValues[j] = potentialValues[j] * 12;
			}
		} else if (potential instanceof TreeADDPotential) {
			TreeADDPotential treeADD = (TreeADDPotential) potential;
			for (TreeADDBranch branch : treeADD.getBranches()) {
				translateMonthlyUtilityPotential(branch.getPotential());
			}
		}
	}

	/**
	 * @param costDiscount
	 * @param inferenceOptions
	 *            It applies the discount to each utility potential
	 */
	public static void applyDiscountToUtilityNodes(ProbNet probNet, double costDiscount,
			double effectivenessDiscount) {

		// apply discount rate for all temporal utility nodes in the expanded
		// network
		List<Node> utilityExpandedNodes = probNet.getNodes(NodeType.UTILITY);
		for (Node utilityNode : utilityExpandedNodes) {
			Variable utilityVariable = utilityNode.getVariable();

			if (utilityVariable.isTemporal()) {
				Potential potential = utilityNode.getPotentials().get(0);
				int timeSlice = utilityVariable.getTimeSlice();
				String decisionCriterion = utilityVariable.getDecisionCriterion().getString();
				double discount = decisionCriterion.equalsIgnoreCase("cost") ? costDiscount
						: effectivenessDiscount;
				applyDiscountToUtilityPotential(potential, timeSlice, discount);
			}
		}
	}

	public static void applyDiscountToUtilityPotential(Potential potential, int timeSlice,
			double discount) {
		double discountRate = 1.0 / (Math.pow((1.0 + (discount / 100.0)), timeSlice));
		if (potential instanceof TablePotential) {
			double[] potentialValues = ((TablePotential) potential).getValues();
			for (int j = 0; j < potentialValues.length; j++) {
				potentialValues[j] = potentialValues[j] * discountRate;
			}
		} else if (potential instanceof TreeADDPotential) {
			TreeADDPotential treeADD = (TreeADDPotential) potential;
			for (TreeADDBranch branch : treeADD.getBranches()) {
				applyDiscountToUtilityPotential(branch.getPotential(), timeSlice, discount);
			}
		}
	}

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
	public static List<Node> getShiftingTemporalNodes(ProbNet probNet) {
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
			Variable decisionCriteriaVariable) {
		Potential utilityPotential = utilNode.getPotentials().get(0);
		List<Variable> treeVariables = utilityPotential.getVariables();
		treeVariables.add(decisionCriteriaVariable);
		String decisionCriterion = utilNode.getVariable().getDecisionCriterion().getString();
		String otherDecisionCriterion = decisionCriterion.equalsIgnoreCase("cost") ? "effectiveness"
				: "cost";

		TreeADDPotential treeADDPotential = new TreeADDPotential(utilityPotential.getUtilityVariable(), treeVariables,
				probNet.getDecisionCriterionVariable());
		List<Variable> variables = new ArrayList<>();
		variables.add(probNet.getDecisionCriterionVariable());
		for (int j = 0; j < treeADDPotential.getBranches().size(); j++) {
			TreeADDBranch branch = treeADDPotential.getBranches().get(j);
			String branchName = branch.getBranchStates().get(0).getName();
			if (branchName.equalsIgnoreCase(decisionCriterion)) {
				branch.setPotential(utilityPotential);
			} else if (branchName.equalsIgnoreCase(otherDecisionCriterion)) {
				// zero potential
				branch.setPotential(new TablePotential(utilNode.getVariable(), new ArrayList<Variable>()));
			}
		}
		return treeADDPotential;
	}

	private static void extendEvidence(ProbNet extendedNetwork, EvidenceCase evidence) {
		try {
			evidence.extendEvidence(extendedNetwork, 1);
		} catch (IncompatibleEvidenceException | InvalidStateException | WrongCriterionException e) {
			e.printStackTrace();
		}
	}
}
