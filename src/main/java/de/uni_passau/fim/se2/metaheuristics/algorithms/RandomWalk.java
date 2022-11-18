package de.uni_passau.fim.se2.metaheuristics.algorithms;

import static java.util.Objects.requireNonNull;

import de.uni_passau.fim.se2.metaheuristics.configurations.Configuration;
import de.uni_passau.fim.se2.metaheuristics.configurations.ConfigurationGenerator;
import de.uni_passau.fim.se2.metaheuristics.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.metaheuristics.stopping_conditions.StoppingCondition;
import de.uni_passau.fim.se2.test_prioritization.TestCaseOrdering;
import de.uni_passau.fim.se2.util.Pair;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Implements a random walk through the search space. First, a randomly chosen configuration is used
 * as starting point. Next, the search space is explored by taking a number of consecutive steps (in
 * the context of Simulated Annealing we would call these elementary transformations) in some
 * direction. Finally, the best encountered configuration is chosen as the solution.
 *
 * @param <C> the type of configuration
 * @author Sebastian Schweikl
 * @apiNote The recursive type parameter {@code C} ensures that a configuration of type {@code C}
 * can only be transformed into a configuration of the same type {@code C}.
 */
public final class RandomWalk<C extends Configuration<C>> implements SearchAlgorithm<C> {

    /**
     * The stopping condition.
     */
    private final StoppingCondition stoppingCondition;

    /**
     * Generator for random configurations.
     */
    private final ConfigurationGenerator<C> generator;

    /**
     * The fitness function employed by this algorithm.
     */
    private final FitnessFunction<C> fitnessFunction;

    /**
     * Stepping function to perform the random walk.
     */
    private final UnaryOperator<C> stepper;

    /**
     * Instantiates a new random walk.
     *
     * @param generator         generator for random configurations, not {@code null}
     * @param fitnessFunction   function with which to compute the fitness, not {@code null}
     * @param stoppingCondition the stopping condition to use, not {@code null}
     * @throws NullPointerException if an argument is {@code null}
     */
    public RandomWalk(
            final ConfigurationGenerator<C> generator,
            final FitnessFunction<C> fitnessFunction,
            final StoppingCondition stoppingCondition)
            throws NullPointerException {
        this(generator, fitnessFunction, stoppingCondition, C::transform);
    }

    /**
     * Instantiates a new random walk.
     *
     * @param generator         generate for random configurations, not {@code null}
     * @param fitnessFunction   function with which to compute the fitness, not {@code null}
     * @param stoppingCondition the stopping condition to use, not {@code null}
     * @param stepper           stepping function used to go from one configuration to the next
     * @throws NullPointerException if an argument is {@code null}
     */
    public RandomWalk(
            final ConfigurationGenerator<C> generator,
            final FitnessFunction<C> fitnessFunction,
            final StoppingCondition stoppingCondition,
            final UnaryOperator<C> stepper)
            throws NullPointerException, IllegalArgumentException {
        this.generator = requireNonNull(generator);
        this.fitnessFunction = requireNonNull(fitnessFunction);
        this.stoppingCondition = requireNonNull(stoppingCondition);
        this.stepper = requireNonNull(stepper);
    }

    /**
     * Finds a solution to the encoded problem by performing a random walk through the search
     * space.
     *
     * @return the solution
     */
    @Override
    public C findSolution() {
        return randomWalk()              // Perform a random walk,
                .reduce(this::bestOf)    // find the best encountered configuration,
                .orElseThrow().getFst(); // and extract and return it.
    }

    /**
     * Determines the best of two given pairs of configurations and fitness values.
     *
     * @param p1 a pair of configuration and fitness value
     * @param p2 another pair of configuration and fitness value
     * @return the better of the two pairs (as per the fitness value)
     */
    private Pair<C, Double> bestOf(final Pair<C, Double> p1, final Pair<C, Double> p2) {
        final double f1 = p1.getSnd();
        final double f2 = p2.getSnd();
        return (fitnessFunction.isMinimizing() ^ f1 < f2) ? p2 : p1;
    }

    /**
     * Performs a random walk and returns the fitness values of the encountered configurations.
     *
     * @return fitness values of the encountered configurations
     */
    public double[] fitnessValues() {
        return randomWalk().mapToDouble(Pair::getSnd).toArray();
    }

    /**
     * Performs a random walk and returns a stream of encountered configurations along with their
     * fitness values (as pairs).
     *
     * @return encountered configurations and their fitness values
     */
    private Stream<Pair<C, Double>> randomWalk() {
        notifySearchStarted(); // IMPORTANT: Don't forget to notify the stopping condition!

        final var start = pickRandomStart();
        final Predicate<Pair<C, Double>> searchCanContinue = ignored -> searchCanContinue();

        // Given the starting point, we repeatedly pick a random neighbor until the search budget is
        // exhausted. IMPORTANT: must be implemented such that the stopping condition is notified
        // for every fitness evaluation!
        return Stream.iterate(start, searchCanContinue, this::pickRandomNeighbor);
    }

    /**
     * Picks a random starting point for a random walk, and returns it as a pair of a configuration
     * along with its fitness value.
     *
     * @return the starting point
     */
    private Pair<C, Double> pickRandomStart() {
        final C randomConfig = generator.get();
        return makeConfigFitnessPairFor(randomConfig);
    }

    /**
     * Picks a random neighbor for the given configuration using the stepper function.
     *
     * @param configFitnessPair the pair of configuration and its fitness
     * @return a random neighbor
     */
    private Pair<C, Double> pickRandomNeighbor(final Pair<C, Double> configFitnessPair) {
        final C config = configFitnessPair.getFst();
        final Configuration neighbor = ((TestCaseOrdering) config).apply(config);
        return makeConfigFitnessPairFor((C) neighbor);
    }

    /**
     * Creates a pair of the given configuration along with its fitness value. When computing the
     * fitness, the stopping condition is also notified.
     *
     * @param configuration the configuration
     * @return the pair of configuration and its fitness value
     */
    private Pair<C, Double> makeConfigFitnessPairFor(final C configuration) {
        return Pair.of(configuration, getFitness(configuration));
    }

    /**
     * Computes the fitness of the given configuration and notifies the stopping condition.
     *
     * @param configuration the configuration whose fitness to compute
     * @return the fitness of the {@code configuration}
     */
    private double getFitness(final C configuration) {
        notifyFitnessEvaluation();
        return configuration.getFitnessBy(fitnessFunction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StoppingCondition getStoppingCondition() {
        return stoppingCondition;
    }
}
