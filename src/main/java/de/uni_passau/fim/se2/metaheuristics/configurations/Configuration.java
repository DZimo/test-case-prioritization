package de.uni_passau.fim.se2.metaheuristics.configurations;

import static java.util.Objects.requireNonNull;

import de.uni_passau.fim.se2.metaheuristics.fitness_functions.FitnessFunction;
import de.uni_passau.fim.se2.util.SelfTyped;

/**
 * Defines common functionality that every possible solution encoding (also called
 * <em>configuration</em>, esp. in the context of Simulated Annealing) must provide.
 *
 * @param <C> the type of configuration this configuration can be transformed into
 * @author Sebastian Schweikl
 * @apiNote Usually, it is desired that configurations of type {@code C} can only be transformed
 * into configurations of the same type {@code C}. This requirement can be enforced at compile time
 * by specifying a recursive type parameter, here: {@code C extends Configuration<C>}.
 */
public abstract class Configuration<C extends Configuration<C>> implements SelfTyped<C> {

    /**
     * The elementary transformation to use.
     */
    private final ElementaryTransformation<C> elementaryTransformation;

    /**
     * Creates a new configuration that uses the given elementary transformation.
     *
     * @param elementaryTransformation the elementary transformation to use
     */
    protected Configuration(final ElementaryTransformation<C> elementaryTransformation) {
        this.elementaryTransformation = requireNonNull(elementaryTransformation);
    }

    /**
     * Copy constructor.
     *
     * @param other the configuration to copy.
     * @apiNote Can be called by copy constructors of implementing subclasses.
     */
    protected Configuration(final Configuration<C> other) {
        this(other.elementaryTransformation);
    }

    /**
     * Constructs a new configuration that uses the {@link ElementaryTransformation#identity()
     * identity} elementary transformation.
     *
     * @apiNote This constructor primarily intended for use during unit testing, e.g., when aspects
     * of an algorithm are tested that do not rely on a particular transformation operator.
     */
    protected Configuration() {
        this(ElementaryTransformation.identity());
    }

    /**
     * Performs an elementary transformation of this configuration.
     *
     * @return the transformed configuration
     * @implNote equivalent to {@code elementaryTransformation.apply(self())}
     */
    public final C transform() {
        return elementaryTransformation.apply(self());
    }

    /**
     * Creates a copy of this configuration. Implementors should clearly indicate whether a shallow
     * or deep copy is made.
     *
     * @return a copy of this configuration
     */
    public abstract C copy();

    /**
     * Returns the elementary transformation operator used by this configuration.
     *
     * @return the elementary transformation operator
     */
    public final ElementaryTransformation<C> getElementaryTransformation() {
        return elementaryTransformation;
    }

    /**
     * Returns the number of degrees of freedom of the current configuration, i.e., the number of
     * variables that can be freely changed in the solution encoding.
     *
     * @return the number of degrees of freedom, must be non-negative
     */
    public abstract int degreesOfFreedom();

    /**
     * Returns the fitness of this configuration using the given fitness function.
     *
     * @param fitnessFunction the function with which to compute the fitness, not {@code null}
     * @return the fitness of this configuration as computed by the given fitness function
     * @throws NullPointerException if the given fitness function is {@code null}
     * @apiNote This method is primarily intended as syntactic sugar to allow for a more idiomatic,
     * OOP-like use.
     * @implNote The default implementation delegates the fitness computation to the fitness
     * function via {@code fitnessFunction.getFitnessFor(self())}.
     * @implSpec Given a configuration {@code c} and fitness function {@code ff}, both {@code
     * c.getFitnessBy(ff)} and {@code ff.getFitnessFor(c)} must return the same result. This
     * function can be overridden in subclasses to implement caching.
     */
    public double getFitnessBy(final FitnessFunction<C> fitnessFunction) {
        requireNonNull(fitnessFunction);
        return fitnessFunction.getFitnessFor(self());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract boolean equals(final Object other); // enforce custom implementation

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract int hashCode(); // enforce custom implementation
}
