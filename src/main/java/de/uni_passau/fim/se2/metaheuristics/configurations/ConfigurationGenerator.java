package de.uni_passau.fim.se2.metaheuristics.configurations;

import java.util.function.Supplier;

/**
 * Common functionality for all generators of {@code Configuration}s.
 *
 * @param <C> the type of configuration generated
 * @author Sebastian Schweikl
 */
public interface ConfigurationGenerator<C extends Configuration<C>> extends Supplier<C> {

    /**
     * Creates and returns a random configuration, which must be a valid and admissible solution of
     * the problem at hand.
     *
     * @return a random configuration
     */
    @Override
    C get();
}
