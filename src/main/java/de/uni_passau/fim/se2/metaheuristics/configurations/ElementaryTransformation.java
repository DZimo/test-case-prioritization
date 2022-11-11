package de.uni_passau.fim.se2.metaheuristics.configurations;

import java.util.function.UnaryOperator;

/**
 * An elementary transformation for Simulated Annealing (or other local search algorithms) that
 * allows the current configuration to reach one of its neighbors in the search space.
 *
 * @param <C> the type of configuration supported by this transformation
 */
public interface ElementaryTransformation<C extends Configuration<C>> extends UnaryOperator<C> {

    /**
     * The identity transformation, returns a copy when applied to a configuration.
     *
     * @param <C> the type of configuration
     * @return an elementary transformation that always returns a copy of its input
     */
    static <C extends Configuration<C>> ElementaryTransformation<C> identity() {
        return new ElementaryTransformation<>() {
            @Override
            public C transform(final C configuration) {
                return configuration.copy();
            }

            @Override
            public String toString() {
                return "Identity";
            }
        };
    }

    /**
     * Performs an elementary transformation of the given configuration. An alias for {@link
     * #transform}.
     *
     * @param configuration the configuration to transform
     * @return a new configuration derived from the current one
     */
    @Override
    default C apply(final C configuration) {
        return this.transform(configuration);
    }

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
    C transform(final C configuration);
}
