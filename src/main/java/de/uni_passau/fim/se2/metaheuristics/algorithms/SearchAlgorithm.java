package de.uni_passau.fim.se2.metaheuristics.algorithms;

import de.uni_passau.fim.se2.metaheuristics.stopping_conditions.StoppingCondition;

/**
 * Represents a strategy to search for an (approximated) solution to a given problem.
 *
 * @param <C> the type of solution encoding for the problem at hand
 * @author Sebastian Schweikl
 */
public interface SearchAlgorithm<C> {

    /**
     * Runs the search algorithm and returns a possible admissible solution of the encoded problem.
     * <p>
     * Note: every run must perform a new search and must be independent of the previous one. In
     * particular, it must be possible to call this method multiple times in a row. Implementors
     * must ensure multiple runs do not interfere each other.
     *
     * @return a solution
     */
    C findSolution();

    /**
     * Returns the stopping condition this algorithm uses.
     *
     * @return the stopping condition
     */
    StoppingCondition getStoppingCondition();

    /**
     * Tells whether the search is allowed to continue. The opposite of {@code searchMustStop()}.
     *
     * @return {@code true} if the search can continue, {@code false} otherwise
     * @implNote an alias for {@code getStoppingCondition().searchCanContinue()}
     */
    default boolean searchCanContinue() {
        return getStoppingCondition().searchCanContinue();
    }

    /**
     * Tells whether the search algorithm must stop. The opposite of {@code searchCanContinue()}.
     *
     * @return {@code true} if the search must stop, {@code false} otherwise
     * @implNote an alias for {@code getStoppingCondition().searchMustStop()}
     */
    default boolean searchMustStop() {
        return getStoppingCondition().searchMustStop();
    }

    /**
     * Returns how much search budget has already been consumed by the search.
     *
     * @return the amount of search budget consumed
     * @implNote an alias for {@code getStoppingCondition().getProgress()}
     */
    default double getProgress() {
        return getStoppingCondition().getProgress();
    }

    /**
     * Notifies the stopping condition that the search has started.
     *
     * @implNote an alias for {@code getStoppingCondition().notifySearchStarted()}
     */
    default void notifySearchStarted() {
        getStoppingCondition().notifySearchStarted();
    }

    /**
     * Notifies the stopping condition that a fitness evaluation took place.
     *
     * @implNote an alias for {@code getStoppingCondition().notifyFitnessEvaluation()}
     */
    default void notifyFitnessEvaluation() {
        getStoppingCondition().notifyFitnessEvaluation();
    }
}
