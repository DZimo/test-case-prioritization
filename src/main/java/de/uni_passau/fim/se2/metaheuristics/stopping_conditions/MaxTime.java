package de.uni_passau.fim.se2.metaheuristics.stopping_conditions;

import static java.util.Objects.requireNonNull;

import java.text.NumberFormat;
import java.util.function.LongSupplier;

/**
 * A stopping condition that defines the search budget in terms of wall time. That is, a search is
 * allowed to run for at most the given amount of time. Often, this stopping condition is used when
 * one does not have an in-depth understanding of an algorithm (and thus cannot judge e.g. how many
 * fitness evaluations are appropriate). But usually, people do know how long they are prepared to
 * wait for a result, making {@code MaxTime} useful for this case.
 *
 * @author Sebastian Schweikl
 */
public final class MaxTime implements StoppingCondition {

    /**
     * To format numbers as percentage.
     */
    private static final NumberFormat percentFormat = NumberFormat.getPercentInstance();

    /**
     * The search budget (in terms of time units, usually milliseconds).
     */
    private final long maxTime;

    /**
     * A supplier that gives the current wall time (usually, in terms of milliseconds).
     */
    private final LongSupplier timeSupplier;

    /**
     * The time when the search was started (usually, in terms of milliseconds).
     */
    private long startTime;

    /**
     * Creates a new stopping condition using the given number of milliseconds as search budget.
     *
     * @param maxTimeMillis the search budget in milliseconds
     */
    private MaxTime(final long maxTimeMillis) {
        this(maxTimeMillis, System::currentTimeMillis);
    }

    /**
     * Constructs a new stopping condition using the given number of time units as search budget,
     * and the given {@code timeSupplier} to determine the current time.
     *
     * @param maxTime      the search budget
     * @param timeSupplier the supplier that tells the current time
     * @throws NullPointerException     when an argument is {@code null}
     * @throws IllegalArgumentException when the search budget is negative
     */
    MaxTime(final long maxTime, final LongSupplier timeSupplier)
            throws IllegalArgumentException, NullPointerException {
        if (maxTime < 0) {
            throw new IllegalArgumentException("time must not be negative");
        }

        this.maxTime = maxTime;
        this.startTime = Long.MAX_VALUE;
        this.timeSupplier = requireNonNull(timeSupplier);
    }

    /**
     * Factory function to create a new stopping condition using the given number of seconds as
     * budget.
     *
     * @param seconds the budget in seconds
     * @return a new stopping condition with the given search budget
     */
    public static MaxTime seconds(final int seconds) {
        return new MaxTime(seconds * 1_000L);
    }

    /**
     * Factory function to create a new stopping condition using the given number of minutes as
     * budget.
     *
     * @param minutes the budget in minutes
     * @return a new stopping condition with the given search budget
     */
    public static MaxTime minutes(final int minutes) {
        return seconds(minutes * 60);
    }

    /**
     * Factory function to create a new stopping condition using the given number of hours as
     * budget.
     *
     * @param hours the budget in hours
     * @return a new stopping condition with the given search budget
     */
    public static MaxTime hours(final int hours) {
        return minutes(hours * 60);
    }

    /**
     * Factory function to create a new stopping condition using the given wall time as budget. The
     * budget is specified in terms of hours, minutes and seconds.
     *
     * @param hours   the hours
     * @param minutes the minutes
     * @param seconds the seconds
     * @return a new stopping condition with the given search budget
     * @throws IllegalArgumentException if an argument represents an invalid time
     */
    public static MaxTime hms(final int hours, final int minutes, final int seconds) {
        if (!(0 <= seconds && seconds < 60) || !(0 <= minutes && minutes < 60) || !(0 <= hours)) {
            throw new IllegalArgumentException(String.format("Invalid time %d:%d:%d",
                    hours, minutes, seconds));
        }

        return hours(hours).plus(minutes(minutes)).plus(seconds(seconds));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifySearchStarted() {
        startTime = currentTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyFitnessEvaluation() {
        // Not relevant for this stopping condition, so do nothing.
    }

    @Override
    public void notifyFitnessEvaluations(final int evaluations) throws IllegalArgumentException {
        // Fitness evaluations are irrelevant for this stopping condition. However, we keep the
        // check that the number of evaluations must not be negative.
        if (evaluations < 0) {
            throw new IllegalArgumentException("Negative number of evaluations: " + evaluations);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean searchMustStop() {
        return elapsedTime() > maxTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getProgress() {
        final double percent = elapsedTime() / (double) maxTime;
        assert 0 <= percent : String.format("Negative progress: %f", percent);
        // Even though stopping conditions must be queried periodically, a search algorithm might
        // sometimes use slightly more search budget than allotted. Small deviations are permitted,
        // but we make sure we never report more progress than 100%.
        return Math.min(percent, 1);
    }

    /**
     * Returns the time elapsed since the search has been started.
     *
     * @return the elapsed time
     */
    private long elapsedTime() {
        return currentTime() - startTime;
    }

    /**
     * Tells the current time.
     *
     * @return the current time
     */
    private long currentTime() {
        return timeSupplier.getAsLong();
    }

    /**
     * The sum of two condition. That is, adds the search budget of the given condition to this
     * condition and returns a new condition with the sum.
     *
     * @param that the other condition
     * @return the sum of {@code this} and {@code that}
     */
    private MaxTime plus(final MaxTime that) {
        requireNonNull(that);
        return new MaxTime(this.maxTime + that.maxTime);
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", getClass().getSimpleName(),
                percentFormat.format(getProgress()));
    }
}
