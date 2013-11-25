package org.openmarkov.core.gui.costeffectiveness;

import java.util.List;

import org.openmarkov.core.inference.heuristic.EliminationHeuristic;
import org.openmarkov.core.inference.heuristic.HeuristicFactory;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.inference.heuristic.simpleElimination.SimpleElimination;

public class CostEffectivenessHeuristicFactory implements HeuristicFactory {

	@Override
	public EliminationHeuristic getHeuristic(ProbNet probNet, List<List<Variable>> variables) {
		return new SimpleElimination(probNet, variables);
	}

}
