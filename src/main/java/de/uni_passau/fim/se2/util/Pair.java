package de.uni_passau.fim.se2.util;

import java.util.Objects;

/**
 * An immutable heterogeneous container class to store two elements of possibly different type.
 *
 * @param <T> the type of the pair's first component
 * @param <U> the type of the pair's second component
 * @author Sebastian Schweikl
 */
public record Pair<T, U>(T fst, U snd) {

    /**
     * Constructs a new pair with the given components.
     *
     * @param fst the first component
     * @param snd the second component
     */
    public Pair {
    }

    /**
     * Static factory method to construct a new pair with the given components.
     *
     * @param fst the first component
     * @param snd the second component
     * @param <T> the type of the first component
     * @param <U> the type of the second component
     * @return the pair of {@code fst} and {@code snd}
     */
    public static <T, U> Pair<T, U> of(final T fst, final U snd) {
        return new Pair<>(fst, snd);
    }

    /**
     * Gets the first component.
     *
     * @return the first component
     */
    public T getFst() {
        return fst;
    }

    /**
     * Gets the second component.
     *
     * @return the second component
     */
    public U getSnd() {
        return snd;
    }

    /**
     * Compares {@code this} to the given other object. Implements component-wise equality for
     * pairs, i.e., two pairs are considered equal if and only if their components are equal.
     *
     * @param other the element with which to compare this to
     * @return {@code true} if {@code this} is equal to {@code other}, {@code false} otherwise
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        final Pair<?, ?> that = (Pair<?, ?>) other;
        return Objects.equals(this.fst, that.fst) &&
                Objects.equals(this.snd, that.snd);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(fst, snd);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Pair(" + fst + ", " + snd + ")";
    }
}
