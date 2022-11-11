package de.uni_passau.fim.se2.metaheuristics.stopping_conditions;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;

/**
 * The union (logical disjunction) of stopping conditions. The union condition is fulfilled when at
 * least one of its "elementary" conditions is satisfied.
 *
 * @author Sebastian Schweikl
 */
public final class OneOf implements StoppingCondition {

    /**
     * The stopping conditions to consider.
     */
    private final StoppingCondition[] stoppingConditions;

    /**
     * Constructs a new {@code OneOf} stopping condition based on the two given conditions.
     *
     * @param condition1 a condition
     * @param condition2 another condition
     * @throws NullPointerException if a condition is {@code null}
     */
    public OneOf(final StoppingCondition condition1, final StoppingCondition condition2)
            throws NullPointerException {
        requireNonNull(condition1);
        requireNonNull(condition2);

        // The given stopping conditions could be OneOf themselves, but we don't flatten them here.
        this.stoppingConditions = new StoppingCondition[]{condition1, condition2};
    }

    /**
     * Constructs a new {@code OneOf} stopping condition based on the given conditions.
     *
     * @param condition1 a condition
     * @param condition2 another condition
     * @param conditions the remaining conditions
     * @throws NullPointerException if a condition is {@code null}
     */
    public OneOf(
            final StoppingCondition condition1,
            final StoppingCondition condition2,
            final StoppingCondition... conditions)
            throws NullPointerException {
        requireNonNull(condition1);
        requireNonNull(condition2);
        requireNonNull(conditions);
        for (final StoppingCondition c : conditions) {
            requireNonNull(c);
        }

        // The given stopping conditions could be OneOf themselves, but we don't flatten them here.
        this.stoppingConditions = new StoppingCondition[conditions.length + 2];
        this.stoppingConditions[0] = condition1;
        this.stoppingConditions[1] = condition2;
        System.arraycopy(conditions, 0, this.stoppingConditions, 2, conditions.length);
    }

    /**
     * Notifies all stopping conditions that the search has started.
     */
    @Override
    public void notifySearchStarted() {
        Arrays.stream(stoppingConditions).forEach(StoppingCondition::notifySearchStarted);
    }

    /**
     * Notifies all stopping conditions that a fitness evaluation took place.
     */
    @Override
    public void notifyFitnessEvaluation() {
        Arrays.stream(stoppingConditions).forEach(StoppingCondition::notifyFitnessEvaluation);
    }

    /**
     * Tells whether the search must stop, i.e., if one of the wrapped stopping conditions is
     * satisfied.
     *
     * @return {@code true} if the search must stop, {@code false} otherwise
     */
    @Override
    public boolean searchMustStop() {
        return Arrays.stream(stoppingConditions).anyMatch(StoppingCondition::searchMustStop);
    }

    /**
     * Tells the overall progress of all wrapped stopping conditions. This operation assumes all
     * wrapped conditions return their progress as percentage. Otherwise, when the progress values
     * are not normalized, it returns the highest unnormalized value.
     *
     * @return the current progress
     */
    @Override
    public double getProgress() {
        //noinspection OptionalGetWithoutIsPresent
        return Arrays.stream(stoppingConditions)
                .mapToDouble(StoppingCondition::getProgress)
                .max()
                .getAsDouble(); // By construction, a value is always present.
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", getClass().getSimpleName(),
                Arrays.deepToString(stoppingConditions));
    }
}
