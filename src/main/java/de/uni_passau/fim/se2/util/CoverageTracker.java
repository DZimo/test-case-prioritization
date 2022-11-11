package de.uni_passau.fim.se2.util;

/**
 * Common interface for coverage trackers.
 */
public interface CoverageTracker {

    /**
     * Returns a coverage matrix. Hereby, every row in the matrix represents a test case and every
     * column represents a line in the system under test. An entry {@code matrix[i][j] == true}
     * indicates that test case {@code i} covers a code fragment {@code j} (e.g., a line of code).
     * The matrix is rectangular.
     *
     * @return coverage matrix
     * @throws Exception if an error occurs while retrieving the matrix
     */
    boolean[][] getCoverageMatrix() throws Exception;

    /**
     * Returns the names of the test cases. For every index {@code i} the name {@code
     * getTestCases()[i]} corresponds to the test case {@code getCoverageMatrix()[i]}.
     *
     * @return names of the test cases
     */
    String[] getTestCases();
}
