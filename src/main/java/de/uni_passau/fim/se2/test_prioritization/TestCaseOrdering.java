package de.uni_passau.fim.se2.test_prioritization;

import de.uni_passau.fim.se2.metaheuristics.configurations.Configuration;
import de.uni_passau.fim.se2.metaheuristics.configurations.ConfigurationGenerator;
import de.uni_passau.fim.se2.metaheuristics.configurations.ElementaryTransformation;
import de.uni_passau.fim.se2.metaheuristics.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.metaheuristics.stopping_conditions.StoppingCondition;
import de.uni_passau.fim.se2.util.Randomness;
import de.uni_passau.fim.se2.util.SelfTyped;


import java.util.*;

public class TestCaseOrdering extends Configuration implements ElementaryTransformation, FitnessFunction, StoppingCondition, ConfigurationGenerator {

    final boolean[][] coverageMatrix;

    final String chosenAlgorithm;



    private static boolean isMinimizing = false;

    public TestCaseOrdering(boolean[][] coverageMatrix, String algorithm) {
        this.coverageMatrix = coverageMatrix;
        chosenAlgorithm = algorithm;
        isMinimizing = this.isMinimizing();
    }


    public TestCaseOrdering() {
        coverageMatrix = new boolean[0][];
        chosenAlgorithm = null;
    }

    private int evalCounter = 0;

    private boolean isMaxFitnessReached = false;

    public Set<Integer> randomSolution;

    public static void setMaxEvals(int maxEvals) {
        TestCaseOrdering.maxEvals = maxEvals;
    }

    static int maxEvals=1000;

    /**
     * Performs an elementary transformation of the given configuration.
     * <p>
     * Implementations must ensure that the following contract is never violated: If the current
     * configuration is a valid admissible solution to the problem at hand, then the returned
     * configuration must represent a valid and admissible solution as well.
     * <p>
     * Furthermore, elementary transformations should obey the following rules to increase the
     * likelihood for the algorithm to converge towards an optimal solution:
     * <ul>
     *     <li>
     *          They should be <em>reversible</em>, that is, if we go from configuration A to
     *          configuration B via an elementary transformation it should also be possible
     *          to go back from B to A using another elementary transformation.
     *     </li>
     *     <li>
     *         Any feasible system configuration should be reachable in a <em>finite</em>
     *         number of transformations.
     *     </li>
     *     <li>
     *         They should not have any <em>fixed points</em>, that is, no configuration can
     *         be its own neighbor. Furthermore, elementary transformations should choose a
     *         <em>random</em> neighbor among all admissible ones every time they are invoked.
     *     </li>
     * </ul>
     * <p>
     * Note that the elementary transformation determines the neighborhood of a configuration.
     * Together with a fitness function, it defines the fitness landscape. Usually, smooth fitness
     * landscapes benefit the search, whereas rugged fitness landscapes tend to hinder the search.
     * <p>
     * An elementary transformation should not exhibit any side effects, such as changing the
     * internal state of the current configuration.
     *
     * @param configuration the configuration to transform
     * @return a new configuration derived from the current one
     */
    @Override
    public Configuration transform(Configuration configuration) {
        TestCaseOrdering neighbour = new TestCaseOrdering(this.coverageMatrix, this.chosenAlgorithm);
        TestCaseOrdering solution = (TestCaseOrdering) configuration;

        Random randomness = Randomness.random();
        Set<Integer> ordersGenerated = new LinkedHashSet<Integer>();
        while (ordersGenerated.size() < solution.randomSolution.size()) {
            Integer nextRandom = randomness.nextInt(solution.randomSolution.size());
            ordersGenerated.add(nextRandom);
        }
        neighbour.randomSolution = ordersGenerated;
        return neighbour;

    }

    /**
     * Applies this function to the given argument.
     *
     * @param o the function argument
     * @return the function result
     */
    @Override
    public Object apply(Object o) {
        TestCaseOrdering o1 = (TestCaseOrdering) o;

        return this.transform(o1);
    }

    /**
     * Creates a copy of this configuration. Implementors should clearly indicate whether a shallow
     * or deep copy is made.
     *
     * @return a copy of this configuration
     */
    @Override
    public Configuration copy() {
        return this;
    }

    /**
     * Returns the number of degrees of freedom of the current configuration, i.e., the number of
     * variables that can be freely changed in the solution encoding.
     *
     * @return the number of degrees of freedom, must be non-negative
     */
    @Override
    public int degreesOfFreedom() {

        return this.randomSolution.size();
    }

    /**
     * {@inheritDoc}
     *
     * @param other
     */
    @Override
    public boolean equals(Object other) {
        return other.equals(self());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return self().hashCode();
    }


    /**
     * <p>
     * Returns the runtime type of the implementor (a.k.a. "self-type"). This method must only be
     * implemented in concrete, non-abstract subclasses by returning a reference to {@code this},
     * and nothing else. Returning a reference to any other runtime type other than {@code this}
     * breaks the contract.
     * <p>
     * In other words, every concrete subclass {@code Foo} that implements the interface {@code
     * SelfTyped} must implement this method as follows:
     * <pre>{@code
     * public final class Foo implements SelfTyped<Foo> {
     *     @Override
     *     public Foo self() {
     *         return this;
     *     }
     * }
     * }</pre>
     *
     * @return a reference to the self-type
     */
    @Override
    public SelfTyped self() {
        return this;
    }

    /**
     * <p>
     * Computes and returns the fitness value of the given solution {@code c}. Minimizing fitness
     * functions must return lower values for better solutions, whereas maximizing fitness functions
     * are expected to return higher values. Implementations must ensure that the returned value is
     * always non-negative and never {@code NaN}.
     * </p>
     * <p>
     * When two solutions {@code c1} and {@code c2} are equal it is generally recommended to return
     * the same fitness value for both of them. That is, {@code c1.equals(c2)} implies {@code
     * getFitnessFor(c1) == getFitnessFor(c2)}. While this is not an absolute requirement
     * implementations that do not conform to this should clearly indicate this fact.
     * </p>
     *
     * @param o the solution to rate
     * @return the fitness value of the given solutions
     * @throws NullPointerException if {@code null} is given
     */
    @Override
    public double getFitnessFor(Object o) {
        TestCaseOrdering testCase = (TestCaseOrdering) o;
        Set<Integer> randomSolution = testCase.randomSolution;
        boolean[][] coverageMatrix = testCase.coverageMatrix;
        double m = coverageMatrix[0].length;
        double n = coverageMatrix.length;

        return Fitness.getFitness(m, n, coverageMatrix, randomSolution.stream().mapToInt(Integer::intValue).toArray());


    }

    /**
     * Tells whether this function is a minimizing fitness function. The opposite of {@link
     * #isMaximizing()}.
     *
     * @return {@code true} if this is a minimizing fitness function, {@code false} if this is a
     * maximizing fitness function
     */
    @Override
    public boolean isMinimizing() {
        return switch (chosenAlgorithm) {
            case "SA" -> true;
            default -> false;
        };
    }

    public static boolean isFitnessMinimizing() {
        return isMinimizing;
    }

    /**
     * Notifies this stopping condition that the search has started. Intended to be called by the
     * search algorithm the stopping condition is subscribed to.
     */
    @Override
    public void notifySearchStarted() {
        evalCounter = 0;
        isMaxFitnessReached = false;
    }

    /**
     * Notifies this stopping condition that a fitness evaluation took place. Intended to be called
     * by the search algorithm the stopping condition is subscribed to.
     */
    @Override
    public void notifyFitnessEvaluation() {
        evalCounter++;
    }

    /**
     * Tells whether the search algorithm must stop, i.e., the search budget has been exhausted. The
     * inverse of {@code searchCanContinue()}.
     *
     * @return {@code true} if the search must stop, {@code false} otherwise
     */
    @Override
    public boolean searchMustStop() {

        return (isMaxFitnessReached || evalCounter > maxEvals);
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
        return evalCounter/maxEvals;
    }


    /**
     * Creates and returns a random configuration, which must be a valid and admissible solution of
     * the problem at hand.
     *
     * @return a random configuration
     */
    @Override
    public Configuration get() {

        TestCaseOrdering c = new TestCaseOrdering(this.coverageMatrix, chosenAlgorithm);
        if (this.coverageMatrix.length > 0) {
            int n = this.coverageMatrix.length;
            Random randomness = Randomness.random();
            Set<Integer> ordersGenerated = new LinkedHashSet<Integer>();
            while (ordersGenerated.size() < n) {
                Integer nextRandom = randomness.nextInt(n);
                ordersGenerated.add(nextRandom);
            }
            c.randomSolution = ordersGenerated;
            return c;
        } else {
            return null;
        }
    }

}
