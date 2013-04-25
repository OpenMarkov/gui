/*
 * Copyright 2012 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.core.gui.costeffectiveness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmarkov.core.exception.ImposedPoliciesException;
import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.exception.WrongCriterionException;
import org.openmarkov.core.inference.MPADFactory;
import org.openmarkov.core.inference.TransitionTime;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;
import org.openmarkov.inference.variableElimination.VariableElimination;

/**
 * Cost effectiveness and temporal evolution calculator
 * 
 * @author myebra
 */
public class CostEffectivenessAnalysis {
    protected ProbNet probNet;
    protected double costDiscountRate;
    protected double effectivenessDiscountRate;
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
     * @param numSlices
     * @param initialValues
     * @param transitionTime
     */
    public CostEffectivenessAnalysis(ProbNet probNet, EvidenceCase evidence,
            double costDiscountRate, double effectivenessDiscountRate, int numSlices,
            Map<Variable, Double> initialValues, TransitionTime transitionTime) {
        this.probNet = probNet;
        this.costDiscountRate = costDiscountRate;
        this.effectivenessDiscountRate = effectivenessDiscountRate;
        this.numSlices = numSlices;
        this.evidence = getEvidenceFromNetwork(probNet, evidence, initialValues);
        this.transitionTime = transitionTime;
        this.globalUtility = costEffectivenessCalculator();
        this.interventions = createInterventions(globalUtility);
        this.frontierInterventions = calculateFrontierInterventions(interventions);
        this.frontierInterventions = calculateICERsOfFrontier(this.frontierInterventions);
    }

    public HashMap<Variable, TablePotential> traceTemporalEvolution(Variable variableOfInterest)
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
        expandedNetFactory.adaptMPADforCE(numSlices, costDiscountRate, effectivenessDiscountRate, evidence, transitionTime);
        this.expandedNetwork = expandedNetFactory.getExtendedNetwork();
        String baseName = variableOfInterest.getBaseName();
        List<Variable> variablesOfInterest = new ArrayList<>();
        List<ProbNode> expandedProbNetProbNodes = expandedNetwork.getProbNodes();
        for (ProbNode node : expandedProbNetProbNodes) {
            if (node.getVariable().getBaseName().equals(baseName)) {
                variablesOfInterest.add(node.getVariable());
            }
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
        return costDiscountRate;
    }

    /**
     * Returns the effectivenessDiscountRate.
     * 
     * @return the effectivenessDiscountRate.
     */
    public double getEffectivenessDiscountRate() {
        return effectivenessDiscountRate;
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
     * If there are temporal nodes within the network that requires evidence
     * must be retrieved from CostEffectivenessDialog
     * 
     * @return EvidenceCase
     */
    private EvidenceCase getEvidenceFromNetwork(ProbNet probNet, EvidenceCase evidence,
            Map<Variable, Double> initialValues) {
        EvidenceCase evidenceCase = new EvidenceCase(evidence);

        for (ProbNode timeDependentNode : probNet.getSpecialTimeDependentNodes()) {
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

    private TablePotential costEffectivenessCalculator() {
        expandedNetwork = buildExpandedNetwork(probNet, numSlices, costDiscountRate,
                effectivenessDiscountRate, evidence, transitionTime);
        return runAnalysis(expandedNetwork, evidence);
    }

    protected ProbNet buildExpandedNetwork(ProbNet probNet, int numSlices, double costDiscountRate,
            double effectivenessDiscountRate, EvidenceCase evidence, TransitionTime transitionTime) {
        MPADFactory expandedNetFactory = new MPADFactory(probNet, numSlices);
        expandedNetFactory.adaptMPADforCE(numSlices, costDiscountRate, effectivenessDiscountRate, evidence, transitionTime);
        return expandedNetFactory.getExtendedNetwork();
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
        return globalUtility;
    }

    private List<Intervention> createInterventions(TablePotential globalUtility) {
        // Reorder variables to force decision criteria to be the conditioned
        // variable
        globalUtility = reorderVariables(globalUtility);
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
            if(description.length()==0)
            {
                description.append("Baseline");
            }
            Intervention intervention = new Intervention(description.toString(), cost, effectiveness);
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

    private void extendEvidence(ProbNet extendedNetwork) {
        try {
            evidence.extendEvidence(extendedNetwork, 1);
        } catch (IncompatibleEvidenceException | InvalidStateException | WrongCriterionException e) {
            e.printStackTrace();
        }
    }
}
