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
import org.openmarkov.core.exception.ProbNodeNotFoundException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.inference.BasicOperations;
import org.openmarkov.core.inference.InferenceOptions;
import org.openmarkov.core.inference.MPADFactory;
import org.openmarkov.core.inference.TransitionTime;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNetOperations;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.StringWithProperties;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.VariableType;
import org.openmarkov.core.model.network.potential.Potential;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.PotentialType;
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
    protected ProbNet            probNet;
    protected double             costDiscount;
    protected double             effectivenessDiscount;
    protected TransitionTime     transitionTime;
    protected int                numSlices;
    protected ProbNet            expandedNetwork;
    protected TablePotential     globalUtility;
    protected List<Intervention> interventions;
    protected List<Intervention> frontierInterventions;
    protected EvidenceCase       evidence;

    /**
     * Constructor for deterministic CEA
     * 
     * @param probNet
     * @param evidence
     * @param costDiscountRate
     * @param effectivenessDiscountRate
     * @param numSlices
     * @param initialValues
     * @param transitionTime
     */
    public CostEffectivenessAnalysis(ProbNet probNet, EvidenceCase evidence,
            double costDiscountRate, double effectivenessDiscountRate, int numSlices,
            Map<Variable, Double> initialValues, TransitionTime transitionTime) {
        this.probNet = probNet;
        this.costDiscount = costDiscountRate;
        this.effectivenessDiscount = effectivenessDiscountRate;
        this.numSlices = numSlices;
        this.evidence = getEvidenceFromNetwork(probNet, evidence, initialValues);
        this.transitionTime = transitionTime;
        this.expandedNetwork = buildExpandedNetwork();
        this.globalUtility = runAnalysis(expandedNetwork, this.evidence);
        this.interventions = createInterventions(globalUtility);
        this.frontierInterventions = calculateFrontierInterventions(interventions);
        this.frontierInterventions = calculateICERsOfFrontier(this.frontierInterventions);
    }

    public Map<Variable, TablePotential> traceTemporalEvolution(Variable variableOfInterest)
            throws ImposedPoliciesException {
        List<ProbNode> decisionNodes = probNet.getProbNodes(NodeType.DECISION);
        // check if all decision nodes have an imposed policy,
        // potential set in probNode
        for (ProbNode node : decisionNodes) {
            if (node.getPotentials().size() == 0) {
                throw new ImposedPoliciesException("All decision nodes must have an imposed policy");
            }
        }
        HashMap<Variable, TablePotential> probsAndUtilities = null;
        MPADFactory expandedNetFactory = new MPADFactory(probNet, numSlices);
        extendEvidence(expandedNetFactory.getExtendedNetwork());
        this.expandedNetwork = expandedNetFactory.getExtendedNetwork();
        this.expandedNetwork = adaptMPADforCE(expandedNetFactory.getExtendedNetwork(),
                numSlices,
                evidence,
                transitionTime);
        translateMonthlyUtilities(expandedNetwork);
        applyDiscountToUtilityNodes(expandedNetwork, costDiscount, effectivenessDiscount);
        String baseName = variableOfInterest.getBaseName();
        List<Variable> variablesOfInterest = new ArrayList<>();
        List<ProbNode> expandedProbNetProbNodes = expandedNetwork.getProbNodes();
        for (ProbNode node : expandedProbNetProbNodes) {
            if (node.getVariable().getBaseName().equals(baseName)) {
                variablesOfInterest.add(node.getVariable());
            }
        }
        // Impose policy according to interest variable's decision criterion
        if (variableOfInterest.getDecisionCriteria() != null) {
            String decisionCriterion = variableOfInterest.getDecisionCriteria().getString();
            Variable decisionCriteriaVariable = expandedNetwork.getDecisionCriteriaVariable();
            ProbNode decisionCriteriaNode = expandedNetwork.getProbNode(expandedNetwork.getDecisionCriteriaVariable());
            TablePotential decisionCriterionPolicy = new TablePotential(Arrays.asList(decisionCriteriaVariable),
                    PotentialRole.POLICY);
            for (int i = 0; i < decisionCriterionPolicy.values.length; ++i) {
                try {
                    decisionCriterionPolicy.values[i] = (decisionCriteriaVariable.getStateIndex(decisionCriterion) == i) ? 1
                            : 0;
                } catch (InvalidStateException e) {
                    e.printStackTrace();
                }
            }
            decisionCriteriaNode.setPotential(decisionCriterionPolicy);
        }
        try {
            VariableElimination variableElimination = new VariableElimination(expandedNetwork);

            variableElimination.setPreResolutionEvidence(evidence);
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
     */
    private ProbNet buildExpandedNetwork() {
        MPADFactory expandedNetFactory = new MPADFactory(probNet, numSlices);
        ProbNet expandedNetwork = expandedNetFactory.getExtendedNetwork();
        expandedNetwork = adaptMPADforCE(expandedNetwork, numSlices, evidence, transitionTime);
        translateMonthlyUtilities(expandedNetwork);
        applyDiscountToUtilityNodes(expandedNetwork, costDiscount, effectivenessDiscount);
        return expandedNetwork;
    }

    /**
     * If there are temporal nodes within the network that requires evidence
     * must be retrieved from CostEffectivenessDialog
     * 
     * @return EvidenceCase
     */
    private EvidenceCase getEvidenceFromNetwork(ProbNet probNet,
            EvidenceCase evidence,
            Map<Variable, Double> initialValues) {
        EvidenceCase evidenceCase = new EvidenceCase(evidence);

        for (ProbNode timeDependentNode : getShiftingTemporalNodes(probNet)) {
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

    protected TablePotential runAnalysis(ProbNet expandedNetwork, EvidenceCase evidence) {
        TablePotential globalUtility = null;
        VariableElimination variableElimination;
        try {
            variableElimination = new VariableElimination(expandedNetwork);
            variableElimination.setPreResolutionEvidence(evidence);
            List<Variable> conditioningVariables = new ArrayList<>();
            conditioningVariables.add(expandedNetwork.getDecisionCriteriaVariable());
            List<ProbNode> decisionNodes = probNet.getProbNodes(NodeType.DECISION);
            for (ProbNode decisionNode : decisionNodes) {
                if (!decisionNode.hasPolicy()) {
                    conditioningVariables.add(decisionNode.getVariable());
                }
            }
            variableElimination.setConditioningVariables(conditioningVariables);
            try {
                globalUtility = variableElimination.getGlobalUtility();
            } catch (IncompatibleEvidenceException | UnexpectedInferenceException e) {
                e.printStackTrace();
            }
        } catch (NotEvaluableNetworkException e1) {
            e1.printStackTrace();
        }
        return reorderVariables(globalUtility);
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
                String stateName = decisions.get(j).getStateName((i / offsets[j])
                        % decisions.get(j).getNumStates());
                description.append(decisionName + " = " + stateName + "; ");
            }
            if (description.length() == 0) {
                description.append("Baseline");
            }
            Intervention intervention = new Intervention(description.toString(),
                    cost,
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
            if (variable.getName().equals("Decision Criteria")) {
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
     */
    public static ProbNet adaptMPADforCE(ProbNet expandedNetwork,
            int numSlices,
            EvidenceCase evidence,
            TransitionTime transitionTime) {
        // Extend evidence
        extendEvidence(expandedNetwork, evidence);

        // Convert numeric variables 
        expandedNetwork = ProbNetOperations.convertNumericalVariablesToFS(expandedNetwork, evidence);
        
        // Remove super value nodes
        expandedNetwork = BasicOperations.removeSuperValueNodes(expandedNetwork, evidence);

        // Project evidence on temporal nodes
        InferenceOptions inferenceOptions = new InferenceOptions(expandedNetwork, null);
        projectTemporalEvidence(expandedNetwork, inferenceOptions, evidence);

        if (transitionTime == TransitionTime.BEGINNING) {
            pruneZeroCycleUtilities(expandedNetwork);
        } else if (transitionTime == TransitionTime.END) {
            // Prune last cycle utilities
            pruneLastCycleUtilities(expandedNetwork, numSlices);
        } else {
            // Half zero and last cycle utilities
            applyHalfCycleCorrection(expandedNetwork);
        }

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
        ProbNode decisionCriteriaNode = new ProbNode(expandedNetwork,
                expandedNetwork.getDecisionCriteriaVariable(),
                NodeType.DECISION);
        expandedNetwork.addProbNode(decisionCriteriaNode);
        for (ProbNode utilityNode : BasicOperations.getTerminalUtilityNodes(expandedNetwork)) {
            Potential utilityPotential = utilityNode.getPotentials().get(0);
            expandedNetwork.addLink(decisionCriteriaNode, utilityNode, true);
            List<Variable> treeVariables = utilityPotential.getVariables();
            treeVariables.add(decisionCriteriaNode.getVariable());
            String utilityDecisionCriteriaName = utilityNode.getVariable().getDecisionCriteria().getString();
            boolean hasDecisionCriterion = false;
            String otherDecisionCriterion = null;
            TreeADDPotential treeADDPotential = null;
            if (utilityDecisionCriteriaName.equals("cost")) {
                hasDecisionCriterion = true;
                otherDecisionCriterion = "effectiveness";
            } else if (utilityDecisionCriteriaName.equals("effectiveness")) {
                hasDecisionCriterion = true;
                otherDecisionCriterion = "cost";
            }
            if (hasDecisionCriterion) {
                treeADDPotential = constructTreeADDForCE(expandedNetwork,
                        treeVariables,
                        utilityPotential,
                        utilityNode,
                        utilityDecisionCriteriaName,
                        otherDecisionCriterion);
                utilityNode.setPotential(treeADDPotential);
            }
        }
        return expandedNetwork;
    }


    public static void translateMonthlyUtilities(ProbNet probNet) {

        // apply discount rate for all temporal utility nodes in the expanded
        // network
        List<ProbNode> utilityExpandedNodes = probNet.getProbNodes(NodeType.UTILITY);
        for (ProbNode utilityProbNode : utilityExpandedNodes) {
        	if(utilityProbNode.getVariable().getUnit().string.equals("months"))
        	{
        		translateMonthlyUtilityPotential(utilityProbNode.getPotentials().get(0));
        	}
        }
    }

    public static void translateMonthlyUtilityPotential(Potential potential) {
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
     *             It applies the discount to each utility potential
     */
    public static void applyDiscountToUtilityNodes(ProbNet probNet,
            double costDiscount,
            double effectivenessDiscount) {

        // apply discount rate for all temporal utility nodes in the expanded
        // network
        List<ProbNode> utilityExpandedNodes = probNet.getProbNodes(NodeType.UTILITY);
        for (ProbNode utilityProbNode : utilityExpandedNodes) {
            Variable utilityVariable = utilityProbNode.getVariable();

            if (utilityVariable.isTemporal()) {
                Potential potential = utilityProbNode.getPotentials().get(0);
                int timeSlice = utilityVariable.getTimeSlice();
                String decisionCriterion = utilityVariable.getDecisionCriteria().getString();
                double discount = decisionCriterion.equalsIgnoreCase("cost") ? costDiscount
                        : effectivenessDiscount;
                applyDiscountToUtilityPotential(potential, timeSlice, discount);
            }
        }
    }

    public static void applyDiscountToUtilityPotential(Potential potential,
            int timeSlice,
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
    public static List<ProbNode> getShiftingTemporalNodes(ProbNet probNet) {
        // this array includes also Age node if exists
        List<ProbNode> numericTemporalNodes = new ArrayList<>();
        List<ProbNode> probNodes = probNet.getProbNodes();
        // looking for temporal numerical variables in the first slice
        for (ProbNode firstSliceNode : probNodes) {
            Variable firstSliceVariable = firstSliceNode.getVariable();
            if (firstSliceVariable.isTemporal()
                    && firstSliceVariable.getVariableType() == VariableType.NUMERIC
                    && firstSliceVariable.getTimeSlice() == 0
                    && (firstSliceNode.getPotentials().isEmpty() || firstSliceNode.getPotentials().get(0) instanceof UniformPotential)) {
                // look for the second slice to check if it has a
                // CycleLengthShift potential
                for (ProbNode secondSliceNode : probNodes) {
                    Variable secondSliceVariable = secondSliceNode.getVariable();
                    if (secondSliceVariable.isTemporal()
                            && secondSliceVariable.getVariableType() == VariableType.NUMERIC
                            && secondSliceVariable.getTimeSlice() == 1
                            && secondSliceVariable.getBaseName().equals(firstSliceVariable.getBaseName())) {
                        if (secondSliceNode.getPotentials().get(0).getPotentialType() == PotentialType.CYCLE_LENGTH_SHIFT) {
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
     * @param utilProbNode
     * @param decisionCriteriaName
     * @param otherDecisionCriteriaName
     * @return A TreeADD for the utility potential where the branch of the
     *         criteria of the node is the old utility table, and the branch of
     *         the other criteria is 0.
     */
    private static TreeADDPotential constructTreeADDForCE(ProbNet probNet,
            List<Variable> treeVariables,
            Potential utility,
            ProbNode utilProbNode,
            String decisionCriteriaName,
            String otherDecisionCriteriaName) {
        TreeADDPotential treeADDPotential = new TreeADDPotential(treeVariables,
                probNet.getDecisionCriteriaVariable(),
                utility.getPotentialRole(),
                utility.getUtilityVariable());
        List<Variable> variables = new ArrayList<>();
        variables.add(probNet.getDecisionCriteriaVariable());
        for (int j = 0; j < treeADDPotential.getBranches().size(); j++) {
            TreeADDBranch jBranch = treeADDPotential.getBranches().get(j);
            String jBranchName = jBranch.getBranchStates().get(0).getName();
            if (jBranchName.equalsIgnoreCase(decisionCriteriaName)) {
                jBranch.setPotential(utility);
            } else if (jBranchName.equalsIgnoreCase(otherDecisionCriteriaName)) {
                // zero potential
                jBranch.setPotential(new UniformPotential(utility.getVariables(),
                        PotentialRole.UTILITY,
                        utilProbNode.getVariable()));
            }
        }
        return treeADDPotential;
    }

    /**
     * when checkbox zero cycle is unselected utility nodes which decision
     * criteria is cost or effectiveness must be removed from the network this
     * approach is in order to take into account changes taking place at the
     * beginning of the cycle not at the end
     * 
     * @return
     */
    private static void pruneZeroCycleUtilities(ProbNet probNet) {
        List<ProbNode> utilityExpandedNodes = probNet.getProbNodes(NodeType.UTILITY);
        for (int i = 0; i < utilityExpandedNodes.size(); i++) {
            ProbNode iUtilityProbNode = utilityExpandedNodes.get(i);
            int timeSlice = iUtilityProbNode.getVariable().getTimeSlice();
            String decisionCriterion = iUtilityProbNode.getVariable().getDecisionCriteria().getString();
            if (iUtilityProbNode.getVariable().isTemporal() && timeSlice == 0
            // decision criteria of utility nodes must be cost or
            // effectiveness
                    && (decisionCriterion.equalsIgnoreCase("cost") || decisionCriterion.equalsIgnoreCase("effectiveness"))) {
                probNet.removeProbNode(iUtilityProbNode);
            }
        }
    }

    private static void pruneLastCycleUtilities(ProbNet probNet, int numSlices) {
        List<ProbNode> utilityExpandedNodes = probNet.getProbNodes(NodeType.UTILITY);
        for (int i = 0; i < utilityExpandedNodes.size(); i++) {
            ProbNode iUtilityProbNode = utilityExpandedNodes.get(i);
            int timeSlice = iUtilityProbNode.getVariable().getTimeSlice();
            String decisionCriterion = iUtilityProbNode.getVariable().getDecisionCriteria().getString();
            if (iUtilityProbNode.getVariable().isTemporal() && timeSlice == numSlices
            // decision criteria of utility nodes must be cost or
            // effectiveness
                    && (decisionCriterion.equalsIgnoreCase("cost") || decisionCriterion.equalsIgnoreCase("effectiveness"))) {
                probNet.removeProbNode(iUtilityProbNode);
            }
        }
    }

    /**
     * Divide by two the utilities of the zero and last cycle
     */
    private static void applyHalfCycleCorrection(ProbNet probNet) {
        Map<ProbNode, Potential> halfCyclePotentials = new HashMap<>();
        for (ProbNode utilityProbNode : probNet.getProbNodes(NodeType.UTILITY)) {
            Variable utilityVariable = utilityProbNode.getVariable();
            if (utilityVariable.isTemporal() && utilityVariable.getTimeSlice() > 0) {
                StringWithProperties decisionCriteria = utilityVariable.getDecisionCriteria();
                if (decisionCriteria.getString().equalsIgnoreCase("effectiveness")) {
                    try {
                        Variable previousSliceVariable = probNet.getShiftedVariable(utilityVariable, -1);
                        ProbNode previousSliceNode = probNet.getProbNode(previousSliceVariable);
                        // Calculate half cycle potentials adding the potentials
                        // of current and previous time slices and dividing the
                        // result by two
                        List<TablePotential> potentialsToSum = Arrays.asList((TablePotential) utilityProbNode.getPotentials().get(0),
                                (TablePotential) previousSliceNode.getPotentials().get(0));
                        TablePotential sumPotential = DiscretePotentialOperations.sum(potentialsToSum);
                        sumPotential.setUtilityVariable(utilityVariable);
                        double[] values = sumPotential.values;
                        for (int j = 0; j < values.length; ++j) {
                            values[j] /= 2;
                        }
                        halfCyclePotentials.put(utilityProbNode, sumPotential);
                    } catch (ProbNodeNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

        // Update potentials, add parents of previous time slice
        for (ProbNode utilityProbNode : halfCyclePotentials.keySet()) {
            Potential halfCyclePotential = halfCyclePotentials.get(utilityProbNode);
            for (Variable potentialVariable : halfCyclePotential.getVariables()) {
                ProbNode potentialNode = probNet.getProbNode(potentialVariable);
                if (!utilityProbNode.getNode().isParent(potentialNode.getNode())) {
                    probNet.addLink(potentialNode, utilityProbNode, true);
                }
            }
            utilityProbNode.setPotential(halfCyclePotential);
        }

        // Remove utility nodes of cycle zero
        for (ProbNode utilityProbNode : probNet.getProbNodes(NodeType.UTILITY)) {
            Variable utilityVariable = utilityProbNode.getVariable();
            if (utilityVariable.isTemporal() && utilityVariable.getTimeSlice() == 0) {
                probNet.removeProbNode(utilityProbNode);
            }
        }
    }

    /**
     * Projects temporal evidence
     * 
     * @param inferenceOptions
     * @param evidence
     */
    private static void projectTemporalEvidence(ProbNet probNet,
            InferenceOptions inferenceOptions,
            EvidenceCase evidence) {
        List<ProbNode> utilityExpandedNodes = probNet.getProbNodes(NodeType.UTILITY);
        for (ProbNode utilityProbNode : utilityExpandedNodes) {
            Variable utilityVariable = utilityProbNode.getVariable();

            if (utilityVariable.isTemporal()) {
                try {
                    List<Potential> projectedPotentials = new ArrayList<>();
                    List<Potential> potentials = utilityProbNode.getPotentials();
                    for (Potential potential : potentials) {
                        projectedPotentials.addAll(potential.tableProject(evidence,
                                inferenceOptions));
                    }
                    utilityProbNode.setPotentials(projectedPotentials);

                } catch (NonProjectablePotentialException | WrongCriterionException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void extendEvidence(ProbNet extendedNetwork, EvidenceCase evidence) {
        try {
            evidence.extendEvidence(extendedNetwork, 1);
        } catch (IncompatibleEvidenceException | InvalidStateException | WrongCriterionException e) {
            e.printStackTrace();
        }
    }
}
