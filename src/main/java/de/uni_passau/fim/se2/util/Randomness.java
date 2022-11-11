package de.uni_passau.fim.se2.util;

import java.util.Random;

/**
 * The default and only source of randomness for this Java application. Must be used throughout the
 * entirety of this Java application instead of creating one's own instance of {@link Random}.
 * <p>
 * Using a static import
 * <pre>{@code
 * import static de.uni_passau.fim.se2.util.Randomness.random;
 * }</pre>
 * in client code this class can be used as follows
 * <pre>{@code
 * int randomInt = random().nextInt();
 * }</pre>
 * to generate a random {@code int}, etc.
 */
public class Randomness {

    /**
     * Internal source of randomness.
     */
    private static final Random random = new Random();

    private Randomness() {
        // private constructor to prevent instantiation
    }

    /**
     * Returns the source of randomness.
     *
     * @return randomness
     */
    public static Random random() {
        return random;
    }
}
