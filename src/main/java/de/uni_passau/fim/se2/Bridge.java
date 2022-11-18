package de.uni_passau.fim.se2;

import de.uni_passau.fim.se2.metaheuristics.algorithms.RandomWalk;
import de.uni_passau.fim.se2.metaheuristics.algorithms.SearchAlgorithm;
import de.uni_passau.fim.se2.metaheuristics.stopping_conditions.MaxTime;
import de.uni_passau.fim.se2.metaheuristics.stopping_conditions.StoppingCondition;
import de.uni_passau.fim.se2.test_prioritization.Fitness;
import de.uni_passau.fim.se2.test_prioritization.RandomSearch;
import de.uni_passau.fim.se2.test_prioritization.SimulatedAnnealing;
import de.uni_passau.fim.se2.test_prioritization.TestCaseOrdering;

import java.util.Arrays;
import java.util.Random;

/**
 * Bridge between the {@code Main} class and your implementation.
 */
public class Bridge {

    /**
     * Builds the specified search {@code algorithm} using the given {@code random} number
     * generator, {@code stoppingCondition} and {@code coverageMatrix}.
     *
     * @param algorithm         the algorithm to build
     * @param random            the RNG instance to use
     * @param stoppingCondition the stopping condition to use
     * @param coverageMatrix    the coverage matrix to use
     * @return the search algorithm
     */
    static SearchAlgorithm<?> buildAlgorithm(
            final Main.Algorithm algorithm,
            final Random random,
            final StoppingCondition stoppingCondition,
            final boolean[][] coverageMatrix) {
        return switch (algorithm) {
            case SA -> buildSimulatedAnnealing(random, stoppingCondition, coverageMatrix);
            case RW -> buildRandomWalk(random, stoppingCondition, coverageMatrix);
            case RS -> buildRandomSearch(random, stoppingCondition, coverageMatrix);
        };
    }

    /**
     * Returns an implementation of the Random Walk search algorithm to find a solution for the
     * test case prioritization problem.
     *
     * @param random            the RNG instance to use
     * @param stoppingCondition the stopping condition to use
     * @param coverageMatrix    the coverage matrix to use
     * @return the search algorithm
     * @apiNote The return type uses a wildcard type "{@code ?}". This is because your implementing
     * subclass of {@code Configuration} has not existed yet at the time of writing this code, so
     * I couldn't specify it.
     * @implSpec When implementing this method, you can instantiate your implementing subclasses
     * such as of {@code ElementaryTransformation}, {@code Configuration}, {@code SearchAlgorithm}
     * as usual. No need to use the wildcard type "{@code ?}" yourself.
     */
    static SearchAlgorithm<?> buildRandomWalk(
            final Random random,
            final StoppingCondition stoppingCondition,
            final boolean[][] coverageMatrix) {

        TestCaseOrdering testCaseOrdering = new TestCaseOrdering(coverageMatrix, "RW");
        return new RandomWalk<>(testCaseOrdering, testCaseOrdering, testCaseOrdering);
    }

    /**
     * Returns an implementation of the Random Search algorithm to find a solution for the
     * test case prioritization problem.
     *
     * @param random            the RNG instance to use
     * @param stoppingCondition the stopping condition to use
     * @param coverageMatrix    the coverage matrix to use
     * @return the search algorithm
     * @apiNote The return type uses a wildcard type "{@code ?}". This is because your implementing
     * subclass of {@code Configuration} has not existed yet at the time of writing this code, so
     * I couldn't specify it.
     * @implSpec When implementing this method, you can instantiate your implementing subclasses
     * such as of {@code ElementaryTransformation}, {@code Configuration}, {@code SearchAlgorithm}
     * as usual. No need to use the wildcard type "{@code ?}" yourself.
     */
    static SearchAlgorithm<?> buildRandomSearch(
            final Random random,
            final StoppingCondition stoppingCondition,
            final boolean[][] coverageMatrix) {


        TestCaseOrdering testCaseOrdering = new TestCaseOrdering(coverageMatrix, "RS");

        return new RandomSearch(testCaseOrdering, testCaseOrdering, testCaseOrdering);

    }

    /**
     * Returns an implementation of the Simulated Annealing search algorithm to find a solution for
     * the test case prioritization problem.
     *
     * @param random            the RNG instance to use
     * @param stoppingCondition the stopping condition to use
     * @param coverageMatrix    the coverage matrix to use
     * @return the search algorithm
     * @apiNote The return type uses a wildcard type "{@code ?}". This is because your implementing
     * subclass of {@code Configuration} has not existed yet at the time of writing this code, so
     * I couldn't specify it.
     * @implSpec When implementing this method, you can instantiate your implementing subclasses
     * such as of {@code ElementaryTransformation}, {@code Configuration}, {@code SearchAlgorithm}
     * as usual. No need to use the wildcard type "{@code ?}" yourself.
     */
    static SearchAlgorithm<?> buildSimulatedAnnealing(
            final Random random,
            final StoppingCondition stoppingCondition,
            final boolean[][] coverageMatrix) {

        TestCaseOrdering testCaseOrdering = new TestCaseOrdering(coverageMatrix, "SA");

        return new SimulatedAnnealing(testCaseOrdering, testCaseOrdering, testCaseOrdering);
    }

    /**
     * Returns a stopping condition that measures the search budget in terms of the given maximum
     * number of fitness evaluations.
     *
     * @param maxEvals the maximum number of fitness evaluations
     * @return a stopping condition that respects {@code maxEvals}
     */
    static StoppingCondition buildMaxFitnessEvalsCondition(final int maxEvals) {
        // TODO: please implement
        return new StoppingCondition() {
            /**
             * Notifies this stopping condition that the search has started. Intended to be called by the
             * search algorithm the stopping condition is subscribed to.
             */
            @Override
            public void notifySearchStarted() {

            }

            /**
             * Notifies this stopping condition that a fitness evaluation took place. Intended to be called
             * by the search algorithm the stopping condition is subscribed to.
             */
            @Override
            public void notifyFitnessEvaluation() {

            }

            /**
             * Tells whether the search algorithm must stop, i.e., the search budget has been exhausted. The
             * inverse of {@code searchCanContinue()}.
             *
             * @return {@code true} if the search must stop, {@code false} otherwise
             */
            @Override
            public boolean searchMustStop() {
                return false;
            }

            /**
             * Returns how much search budget has already been consumed by the search. The returned value
             * should be a percentage, i.e., a value in the interval [0,1]. But this is not an absolute
             * requirement, and implementations might choose to return different values if it makes sense
             * for them. In this case, however, it is recommended to clearly document their behavior.
             *
             * @return the amount of search budget consumed
             */
            @Override
            public double getProgress() {
                return 0;
            }
        };
    }

    /**
     * Returns a stopping condition that measures the search budget in terms of the given number of
     * seconds the search is allowed to run for.
     *
     * @param seconds maximum runtime in terms of seconds
     * @return a stopping condition
     */
    static StoppingCondition buildMaxTimeCondition(final int seconds) {
        return MaxTime.seconds(seconds);
    }

    /**
     * Returns a stopping condition that measures the search budget in terms of the given number of
     * hours, minutes, and seconds the search is allowed to run for.
     *
     * @param hours   the hours
     * @param minutes the minutes
     * @param seconds the seconds
     * @return a stopping condition
     */
    static StoppingCondition buildMaxTimeCondition(
            final int hours,
            final int minutes,
            final int seconds) {
        return MaxTime.hms(hours, minutes, seconds);
    }

    /**
     * Computes the APLC value for the given coverage matrix and ordering of test cases. The
     * ordering is encoded as solution to a search problem, and has been returned by a search
     * algorithm.
     *
     * @param coverageMatrix the coverage matrix
     * @param solution       the solution encoding an ordering of test cases
     * @return the APLC value
     * @apiNote The method uses the {@code Object} type for {@code solution} because at the time of
     * writing this code your implementation has not existed yet.
     */
    static double computeAPLC(final boolean[][] coverageMatrix, final Object solution) {
        return computeAPLC(coverageMatrix, extractOrderingFromSolution(solution));
    }

    /**
     * Computes the APLC value for the given coverage matrix and ordering of test cases. The
     * ordering is given as an array of indices. Each index refers to one of the test cases in
     * the coverage matrix. The indices range between 0 and an exclusive upper bound n. The
     * coverage matrix contains n rows, one for every test case. You can assume
     * {@code coverageMatrix.length == ordering.length}.
     *
     * @param coverageMatrix the coverage matrix
     * @param ordering       the ordering of test cases
     * @return the resulting APLC value
     */
    static double computeAPLC(final boolean[][] coverageMatrix, final int[] ordering) {
        assert coverageMatrix.length == ordering.length;

        double m = coverageMatrix[0].length;
        double n = coverageMatrix.length;

        return Fitness.getFitness(m, n, coverageMatrix, ordering);

    }

    /**
     * Extracts an ordering of test cases from the given solution object as an array of indices.
     * These indices are expected to be 0-based, and the range of indices must be contiguous
     * (there must not be "holes").
     *
     * @param solution the solution encoding and ordering
     * @return the ordering represented as an array of indices
     * @apiNote The method uses the {@code Object} type for {@code solution} because at the time of
     * writing this code your implementation has not existed yet.
     * @implSpec You can simply down cast the {@code Object} to your implementing subclass of
     * {@code Configuration} to further process it.
     */
    static int[] extractOrderingFromSolution(final Object solution) {
        TestCaseOrdering testCaseOrdering = (TestCaseOrdering) solution;

        return testCaseOrdering.randomSolution.stream().mapToInt(Integer::intValue).toArray();
    }

    static String getTestCaseOrder(final String[] testCases, final Object solution) {
        final int[] ordering = extractOrderingFromSolution(solution);
        assert testCases.length == ordering.length;
        final String[] testCaseOrder = new String[testCases.length];
        for (int i = 0; i < ordering.length; i++) {
            testCaseOrder[i] = testCases[ordering[i]];
        }
        return Arrays.toString(testCaseOrder);
    }
}
