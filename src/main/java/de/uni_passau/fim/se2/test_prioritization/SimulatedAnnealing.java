package de.uni_passau.fim.se2.test_prioritization;

import de.uni_passau.fim.se2.metaheuristics.algorithms.SearchAlgorithm;
import de.uni_passau.fim.se2.metaheuristics.configurations.Configuration;
import de.uni_passau.fim.se2.metaheuristics.configurations.ConfigurationGenerator;
import de.uni_passau.fim.se2.metaheuristics.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.metaheuristics.stopping_conditions.StoppingCondition;
import de.uni_passau.fim.se2.util.Pair;

import java.util.function.UnaryOperator;

import static java.util.Objects.requireNonNull;

public class SimulatedAnnealing <C extends Configuration<C>> implements SearchAlgorithm<C> {

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
     * Stepping function to perform the simulated annealing.
     */
    private final UnaryOperator<C> stepper;
    private double p0;

    /**
     * Instantiates a new random walk.
     *
     * @param generator         generator for random configurations, not {@code null}
     * @param fitnessFunction   function with which to compute the fitness, not {@code null}
     * @param stoppingCondition the stopping condition to use, not {@code null}
     * @throws NullPointerException if an argument is {@code null}
     */
    public SimulatedAnnealing(
            final ConfigurationGenerator<C> generator,
            final FitnessFunction<C> fitnessFunction,
            final StoppingCondition stoppingCondition)
            throws NullPointerException {
        this(generator, fitnessFunction, stoppingCondition, C::transform);
    }

    public SimulatedAnnealing(
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
     * Performs a random walk and returns a stream of encountered configurations along with their
     * fitness values (as pairs).
     *
     * @return encountered configurations and their fitness values
     */
    private Pair<C, Double> simulatedAnnealing() {
        notifySearchStarted();

        Pair<C,Double> current = generateSolution();
        Pair<C,Double> bestCandidate =current;

        int step=1;
        float maxStep=500;

        double fraction, actualTemperature,initialTemperature=0.1f;

        Pair<C,Double> neighbour;

        double p0=0.2;

        while (searchCanContinue() && step<maxStep){

            fraction=step/maxStep;
            actualTemperature= getTemperature(fraction);

            neighbour = pickRandomNeighbor(current);
            step++;

            if(acceptanceProbability(current.snd(),neighbour.snd(),actualTemperature)> p0) {
                current = neighbour;

                if (current.snd() > bestCandidate.snd()) {

                    bestCandidate = neighbour;

                }
            }
        }

        return bestCandidate;

    }

    private double acceptanceProbability(Double fitness, Double newFitness, double temperature) {
        if(newFitness> fitness){
            return 1;
        }
        else {
            return Math.exp(-(newFitness-fitness)/temperature);
        }
    }

    private double getTemperature(double fraction) {

        return Math.max(0.01, Math.min(1,1-fraction));

    }


    /**
     * Picks a random starting point for a random walk, and returns it as a pair of a configuration
     * along with its fitness value.
     *
     * @return the starting point
     */
    private Pair<C, Double> generateSolution() {
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
     * Runs the search algorithm and returns a possible admissible solution of the encoded problem.
     * <p>
     * Note: every run must perform a new search and must be independent of the previous one. In
     * particular, it must be possible to call this method multiple times in a row. Implementors
     * must ensure multiple runs do not interfere each other.
     *
     * @return a solution
     */
    @Override
    public C findSolution() {
        return simulatedAnnealing().fst();
    }

    /**
     * Returns the stopping condition this algorithm uses.
     *
     * @return the stopping condition
     */
    @Override
    public StoppingCondition getStoppingCondition() {
        return stoppingCondition;
    }
}
