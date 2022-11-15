package de.uni_passau.fim.se2.test_prioritization;

import de.uni_passau.fim.se2.metaheuristics.algorithms.SearchAlgorithm;
import de.uni_passau.fim.se2.metaheuristics.stopping_conditions.StoppingCondition;

public class RandomSearch implements SearchAlgorithm {
    /**
     * Runs the search algorithm and returns a possible admissible solution of the encoded problem.
     * <p>
     * Note: every run must perform a new search and must be independent of the previous one. In
     * particular, it must be possible to call this method multiple times in a row. Implementors
     * must ensure multiple runs do not interfere each other.
     *
     * @return a solution
     */
    @Override
    public Object findSolution() {
        return null;
    }

    /**
     * Returns the stopping condition this algorithm uses.
     *
     * @return the stopping condition
     */
    @Override
    public StoppingCondition getStoppingCondition() {
        return null;
    }
}
