/*
 * Copyright 2013 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */

package org.openmarkov.core.gui.costeffectiveness;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.exception.UnexpectedInferenceException;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.inference.variableElimination.VariableElimination;

public class Simulation implements Callable<TablePotential> {

    private ProbNet expandedNetwork;
    private EvidenceCase evidence;

    public Simulation(ProbNet expandedNet, EvidenceCase evidence) {
        this.expandedNetwork = expandedNet;
        this.evidence = evidence;
    }

    @Override
    public TablePotential call() throws Exception {

        TablePotential globalUtility = null;
        VariableElimination variableElimination;
        try {
            variableElimination = new VariableElimination(expandedNetwork);
            variableElimination.setPreResolutionEvidence(evidence);
            List<Variable> conditioningVariables = new ArrayList<>();
            conditioningVariables.add(expandedNetwork.getDecisionCriteriaVariable());
            List<ProbNode> decisionNodes = expandedNetwork.getProbNodes(NodeType.DECISION);
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
}
