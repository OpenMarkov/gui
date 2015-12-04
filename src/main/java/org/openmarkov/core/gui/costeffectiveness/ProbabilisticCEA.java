/*
 * Copyright 2013 CISIAD, UNED, Spain Licensed under the European Union Public
 * Licence, version 1.1 (EUPL) Unless required by applicable law, this code is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OF ANY KIND.
 */
package org.openmarkov.core.gui.costeffectiveness;

import org.openmarkov.core.exception.IncompatibleEvidenceException;
import org.openmarkov.core.exception.NotEvaluableNetworkException;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.PotentialRole;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.inference.tasks.VariableElimination.VECEPSA;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProbabilisticCEA extends CostEffectivenessAnalysis implements Runnable{

    private int numSimulations;
    private boolean useMultithreading;
    private List<TablePotential> ceaResults;
    private volatile int progress;
    private Variable decision;

    private VECEPSA vecepsa;
//    public ProbabilisticCEA(ProbNet probNet, EvidenceCase evidence, int numSimulations,
//            boolean useMultithreading) throws NotEvaluableNetworkException {
//        super(probNet, evidence);
//        this.numSimulations = numSimulations;
//        this.useMultithreading = useMultithreading;
//    }

    public ProbabilisticCEA(ProbNet probNet, Variable decision, EvidenceCase evidence, int numSimulations,
                            boolean useMultithreading) throws NotEvaluableNetworkException {
        super(probNet,decision,evidence);
        this.decision = decision;
        this.numSimulations = numSimulations;
        this.useMultithreading = useMultithreading;
    }
    
    public void run()
    {
        this.ceaResults = runProbabilisticAnalysis(probNet, evidence,  numSimulations, useMultithreading);
        this.costEffectivenessTable = calculateMeanUtility(this.ceaResults);
        this.guiInterventions = buildProbabilisticInterventions(this.ceaResults);
        this.frontierGUIInterventions = calculateFrontierInterventions(guiInterventions);
    }    

	private TablePotential calculateMeanUtility(List<TablePotential> ceaResults) {
        TablePotential globalUtility = new TablePotential(this.costEffectivenessTable.getVariables(), PotentialRole.UTILITY);
        double[] values = globalUtility.values;
        for(TablePotential simulationResult : ceaResults)
        {
            for(int i=0; i < simulationResult.values.length; ++i)
            {
                values[i] += simulationResult.values[i];
            }
        }
        for(int i=0; i < values.length; ++i)
        {
            values[i] /= ceaResults.size();
        }
        return globalUtility;
    }

    private List<GUIIntervention> buildProbabilisticInterventions(List<TablePotential> results) {

        List<String> interventionNames = new ArrayList<>();
        List<List<Double>> costs = new ArrayList<>();
        List<List<Double>> effectivenesses = new ArrayList<>();

        // Gather intervention names
        TablePotential exampleResult = reorderVariables(results.get(0));
        List<Variable> decisions = exampleResult.getVariables();
        int[] offsets = exampleResult.getOffsets();
        for (int i = 0; i < exampleResult.values.length; i += 2) {
            StringBuilder description = new StringBuilder();
            for (int j = 1; j < decisions.size(); ++j) {
                String decisionName = decisions.get(j).getName();
                String stateName = decisions.get(j).getStateName(
                        (i / offsets[j]) % decisions.get(j).getNumStates());
                description.append(decisionName + " = " + stateName + "; ");
            }
            interventionNames.add((description.length()>0)?description.toString():"Baseline");
            costs.add(new ArrayList<Double>(results.size()));
            effectivenesses.add(new ArrayList<Double>(results.size()));
        }

        // Gather data
        for(TablePotential simulationResult : results)
        {
            // Gather cost-effectiveness data
            double[] values = simulationResult.values;
            for (int i = 0; i*2 < values.length; i++) {
                costs.get(i).add(values[i*2]);
                effectivenesses.get(i).add(values[i*2 + 1]);
            }
        }

        guiInterventions.clear();
        for(int i=0; i < interventionNames.size(); ++i)
        {
            guiInterventions.add(new ProbabilisticGUIIntervention(interventionNames.get(i), costs.get(i), effectivenesses.get(i)));
        }
        return guiInterventions;
    }

    public int getNumSimulations() {
        return numSimulations;
    }    
    
    public int getProgress() {
        if(vecepsa == null){
            return 0;
        }
        return vecepsa.getProgress();
    }

	private List<TablePotential> runProbabilisticAnalysis(ProbNet expandedNetwork,
			EvidenceCase evidence, int numSimulations,
			boolean useMultithreading)    
	{
        try {
            vecepsa = new VECEPSA(expandedNetwork, decision, evidence, numSimulations, useMultithreading);
            return vecepsa.getCeaResults();
        } catch (NotEvaluableNetworkException e) {
            e.printStackTrace();
        } catch (IncompatibleEvidenceException e) {
            e.printStackTrace();
        }

        return null;
//        progress = 0;
//        List<TablePotential> results = new ArrayList<>(numSimulations);
//        if(useMultithreading)
//        {
//	        int numThreads = Runtime.getRuntime().availableProcessors();
//	        boolean success = false;
//			while (!success && numThreads > 0) {
//				ExecutorService executor = Executors.newFixedThreadPool(numThreads);
//				List<Future<TablePotential>> list = new ArrayList<>();
//				for (int i = 0; i < numSimulations; ++i) {
//                    Simulation simulation = new Simulation(expandedNetwork);
//					list.add(executor.submit(simulation));
//				}
//				int simulationIndex = 0;
//				try {
//					for (Future<TablePotential> result : list) {
//						results.add(result.get());
//						progress = simulationIndex * 100 / numSimulations;
//						simulationIndex++;
//					}
//					success = true;
//				} catch (InterruptedException | ExecutionException e) {
//					System.out.println("WARNING: PSA failed with " + numThreads + " threads.");
//					e.printStackTrace();
//					System.out.println(e.getMessage());
//					results.clear();
//					numThreads /= 2;
//				}
//			}
//        }else
//        {
//    		try {
//    			Map<Variable, List<Potential>> networkPotentials = new HashMap<>();
//    	        for(Node node : expandedNetwork.getNodes())
//    	        {
//    	        	networkPotentials.put(node.getVariable(), node.getPotentials());
//    	        }
//    	        List<Node> sortedNodes = ProbNetOperations.sortTopologically(expandedNetwork);
//    			removeIntermediateUtilityNodes(expandedNetwork);
//    			TemporalNetOperations.applyTransitionTime(expandedNetwork);
//	        	for(int i=0; i < numSimulations; ++i)
//	        	{
//	                sampleAndTableProject(sortedNodes, networkPotentials, evidence);
//                    VECEADecision veceaDecision = null;
//                    try {
//                        veceaDecision = new VECEADecision(expandedNetwork,decision, evidence);
//                    } catch (NotEvaluableNetworkException e) {
//                        e.printStackTrace();
//                    } catch (IncompatibleEvidenceException e) {
//                        e.printStackTrace();
//                    }
//					results.add(veceaDecision.getGlobalUtility());
//					progress = i * 100 / numSimulations;
//	        	}
//			} catch (NonProjectablePotentialException | WrongCriterionException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IncompatibleEvidenceException e) {
//                e.printStackTrace();
//            } catch (UnexpectedInferenceException e) {
//                e.printStackTrace();
//            }
//        }
//        progress = 100;
//        return results;
    }
	

    public Map<Integer, double[]> calculateCEAC(int maxRatio)
    {
        if(ceaResults == null)
        {
            run();
        }
        
        Map<Integer, double[]> results = new LinkedHashMap<>();
        int numInterventions = guiInterventions.size();
        int numSimulations = ((ProbabilisticGUIIntervention) guiInterventions.get(0)).getNumSimulations();
        for(int i=0; i<=1000; ++i)
        {
            int ratio = maxRatio * i / 1000; 
            // calculate CE probability for ratio
            double[] ceProbabilities = new double[numInterventions];
            // Initialize with zeros
            for(int k=0; k< numInterventions; ++k)
            {
                ceProbabilities[k] = 0;
            }               
            for(int j=0; j< numSimulations; ++j)
            {
                double maxNetBenefit = Double.NEGATIVE_INFINITY;
                int maxBenefitInterventionIndex = -1; 
                for(int k=0; k< numInterventions; ++k)
                {
                    ProbabilisticGUIIntervention intervention = (ProbabilisticGUIIntervention) guiInterventions.get(k);
                    double netBenefit = ratio * intervention.getEffectivenesses().get(j) - intervention.getCosts().get(j);
                    if(netBenefit > maxNetBenefit)
                    {
                        maxNetBenefit = netBenefit;
                        maxBenefitInterventionIndex = k;
                    }
                }
                for(int k=0; k< numInterventions; ++k)
                {
                    ceProbabilities[k] += (maxBenefitInterventionIndex == k)? 1 : 0;
                }                
            }
            for(int k=0; k< numInterventions; ++k)
            {
                ceProbabilities[k] /= numSimulations;
            }                
            results.put(ratio, ceProbabilities);
        }
        return results;
    }

    public Map<Integer, Double> calculateEVPI(int maxRatio,
            int patientsPerAnnum, int lifetime, double discountRate)    {
        Map<Integer, Double> results = new LinkedHashMap<>();

        if(ceaResults == null)
        {
            run();
        }

        // Calculate effective population
        int effectivePopulation = 0;
        for(int i=0; i<lifetime; ++i)
        {
            effectivePopulation += patientsPerAnnum / Math.pow(1 + discountRate, i);
        }
        int numInterventions = guiInterventions.size();
        int numSimulations = ((ProbabilisticGUIIntervention) guiInterventions.get(0)).getNumSimulations();
        double[][] netBenefits = new double[numInterventions][numSimulations];
        // Max benefit in each simulation
        double[] maxNetBenefits = new double[numSimulations];
        // Average net benefits for each intervention
        double[] avgNetBenefits = new double[numInterventions];

        for(int i=0; i<=1000; ++i)
        {
            // Calculate netBenefits and maxBenefit
            int ratio = maxRatio * i / 1000;
            for(int j=0; j< numSimulations; ++j)
            {
                maxNetBenefits[j] = Double.NEGATIVE_INFINITY;
                for(int k=0; k< numInterventions; ++k)
                {
                    ProbabilisticGUIIntervention intervention = (ProbabilisticGUIIntervention) guiInterventions.get(k);
                    double netBenefit = ratio * intervention.getEffectivenesses().get(j) - intervention.getCosts().get(j);
                    netBenefits[k][j] = netBenefit;
                    if(netBenefit > maxNetBenefits[j])
                    {
                        maxNetBenefits[j] = netBenefit;
                    }
                }
            }
            // Calculate average net benefit for each intervention
            for(int k=0; k< numInterventions; ++k)
            {
                avgNetBenefits[k] = 0;
                for(int j=0; j< numSimulations; ++j)
                {
                    avgNetBenefits[k] += netBenefits[k][j];
                }
                avgNetBenefits[k] /= numSimulations;
            }
            // Calculate the maximum average net benefit across interventions
            double maxAverage = Double.NEGATIVE_INFINITY;
            for(int k=0; k< numInterventions; ++k)
            {
                if(maxAverage < avgNetBenefits[k])
                {
                    maxAverage = avgNetBenefits[k];
                }
            }
            // Calculate the average of maximum net benefits for each simulation
            double averageMax = 0;
            for(int j=0; j< numSimulations; ++j)
            {
                averageMax += maxNetBenefits[j];
            }
            averageMax /= numSimulations;
            double popEVPI = effectivePopulation * (averageMax - maxAverage);
            results.put(ratio, popEVPI);
        }
        return results;
    }

}
