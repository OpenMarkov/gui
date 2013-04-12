package org.openmarkov.core.gui.costeffectiveness;

import java.util.List;

public class ProbabilisticIntervention extends Intervention {

    private int numSimulations;
    private List<Double> costs;
    private List<Double> effectivenesses;
    
    public ProbabilisticIntervention(String name, List<Double> costs, List<Double> effectivenesses) {
        super(name, calculateCost(costs), calculateEffectiveness(effectivenesses));
        this.costs = costs;
        this.effectivenesses = effectivenesses;
        this.numSimulations = costs.size();
    }

    private static double calculateCost(List<Double> costs) {
        double cost = 0.0;
        for (Double simulatedCost : costs) {
            cost += simulatedCost;
        }
        if (!costs.isEmpty()) {
            cost /= costs.size();
        }
        return cost;
    }

    private static double calculateEffectiveness(List<Double> effectivenesses) {
        double effectiveness = 0.0;
        for (Double simulatedEffectiveness : effectivenesses) {
            effectiveness += simulatedEffectiveness;
        }
        if (!effectivenesses.isEmpty()) {
            effectiveness /= effectivenesses.size();
        }
        return effectiveness;
    }

    public List<Double> getCosts() {
        return costs;
    }

    public List<Double> getEffectivenesses() {
        return effectivenesses;
    }

    public int getNumSimulations() {
        return numSimulations;
    }    
}
