/*
 * Copyright 2013 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.core.gui.costeffectiveness;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.core.model.network.potential.operation.DiscretePotentialOperations;

public class ProbabilisticCEA extends CostEffectivenessAnalysis {

    private int numSimulations;
    private List<TablePotential> ceaResults;

    public ProbabilisticCEA(ProbNet probNet, EvidenceCase evidence, double costDiscountRate,
            double effectivenessDiscountRate, int numSlices, int numSimulations,
            Map<Variable, Double> initialValues, TransitionTime transitionTime) {
        super(probNet, evidence, costDiscountRate, effectivenessDiscountRate, numSlices,
                initialValues, transitionTime);
        this.numSimulations = numSimulations;
        this.ceaResults = runProbabilisticAnalysis(expandedNetwork, this.evidence, numSimulations);
        this.interventions = buildProbabilisticInterventions(ceaResults);
    }

    private List<Intervention> buildProbabilisticInterventions(List<TablePotential> results) {

        List<String> interventionNames = new ArrayList<>();
        List<List<Double>> costs = new ArrayList<>();
        List<List<Double>> effectivenesses = new ArrayList<>();

        // Gather intervention names
        TablePotential exampleResult = reorderVariables(results.get(0));
        List<Variable> decisions = exampleResult.getVariables();
        int[] offsets = exampleResult.getOffsets();
        for (int i = 0; i < exampleResult.values.length; i += 2) {
            String name = "Baseline";
            for (int j = 1; j < decisions.size(); ++j) {
                String decisionName = decisions.get(j).getName();
                String stateName = decisions.get(j).getStateName(
                        (i / offsets[j]) % decisions.get(j).getNumStates());
                name = "Dec: " + decisionName + " = " + stateName + "; ";
            }
            interventionNames.add(name);
            costs.add(new ArrayList<Double>(results.size()));
            effectivenesses.add(new ArrayList<Double>(results.size()));
        }

        // Gather data
        for(TablePotential simulationResult : results)
        {
            List<Variable> newOrderVariables = new ArrayList<>();
            for (Variable variable : simulationResult.getVariables()) {
                if (variable.getName().equals("Decision Criteria")) {
                    newOrderVariables.add(0, variable);
                } else {
                    newOrderVariables.add(variable);
                }
            }
            simulationResult = reorderVariables(simulationResult);
 
            // Gather cost-effectiveness data
            double[] values = simulationResult.values;
            for (int i = 0; i*2 < values.length; i++) {
                costs.get(i).add(values[i*2]);
                effectivenesses.get(i).add(values[i*2 + 1]);
            }
        }
        
        interventions.clear();
        for(int i=0; i < interventionNames.size(); ++i)
        {
            interventions.add(new ProbabilisticIntervention(interventionNames.get(i), costs.get(i), effectivenesses.get(i)));
        }
        return interventions;
    }

    public int getNumSimulations() {
        return numSimulations;
    }    
    
    private List<TablePotential> runProbabilisticAnalysis(ProbNet expandedNetwork, EvidenceCase evidence, int numSimulations)
    {
        List<TablePotential> results = new ArrayList<>(numSimulations);
        for (int i = 0; i < numSimulations; ++i)
        {
            sampleProbNet(expandedNetwork);
            TablePotential simulationResult = runAnalysis(expandedNetwork, evidence);
            results.add(simulationResult);
        }
        return results;
    }

    /**
     * @param simulationIndexVariable
     *            . <code>Variable</code>
     * @throws NotEnoughMemoryException
     */
    private void sampleProbNet(ProbNet probNet) {
        for (ProbNode probNode : probNet.getProbNodes()) {
            probNode.samplePotentials();
        }
    }
}
