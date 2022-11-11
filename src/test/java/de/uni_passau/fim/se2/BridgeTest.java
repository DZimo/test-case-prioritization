package de.uni_passau.fim.se2;

import static com.google.common.truth.Truth.assert_;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Checks that the APLC metric is computed correctly. You need to pass all of these tests!
 */
class BridgeTest {

    private static final double TOLERANCE = 10e-9;

    private static final boolean XX = true;
    private static final boolean __ = false;

    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
    }

    @Test
    void test_aplcComputation_withRegularMatrix() throws Exception {
        // A coverage matrix without empty rows or columns: n = 5, m = 10.
        @SuppressWarnings("unused") final boolean[][] coverageMatrix = {
                // 1  2   3   4   5   6   7   8   9  10    m / n
                {XX, __, XX, __, __, __, __, __, __, __},   // 1
                {XX, XX, XX, __, XX, __, __, __, __, __},   // 2
                {XX, XX, XX, XX, XX, XX, __, __, __, __},   // 3
                {__, XX, __, XX, __, __, __, __, __, __},   // 4
                {__, __, __, __, __, __, XX, XX, XX, XX}    // 5
        };

        final var args = new String[]{"-o", "0:1:2:3:4", "-m", "coverageMatrix"};
        new Main(args).start();
        final double actual = Double.parseDouble(outputStreamCaptor.toString());
        final double expected =
                1 - (1d + 2 + 1 + 3 + 2 + 3 + 5 + 5 + 5 + 5) / (5 * 10) + 1d / (2 * 5);
        assert_().that(actual).isWithin(TOLERANCE).of(expected);
    }

    @Test
    void test_aplcComputation_withRegularMatrix2() throws Exception {
        // A coverage matrix without empty rows or columns: n = 5, m = 10.
        @SuppressWarnings("unused") final boolean[][] coverageMatrix = {
                // 1  2   3   4   5   6   7   8   9  10    m / n
                {XX, __, XX, __, __, __, __, __, __, __},   // 1
                {XX, XX, XX, __, XX, __, __, __, __, __},   // 2
                {XX, XX, XX, XX, XX, XX, __, __, __, __},   // 3
                {__, XX, __, XX, __, __, __, __, __, __},   // 4
                {__, __, __, __, __, __, XX, XX, XX, XX}    // 5
        };

        final var args = new String[]{"-o", "2:4:1:0:3", "-m", "coverageMatrix"};
        new Main(args).start();
        final double actual = Double.parseDouble(outputStreamCaptor.toString());
        final double expected = 1d - (6d * 1 + 4d * 2) / 50 + 1d / 10;
        assert_().that(actual).isWithin(TOLERANCE).of(expected);
    }

    @Test
    void test_aplcComputation_matrixWithUncoveredLine() throws Exception {
        // A coverage matrix with an empty 11th column, meaning that LOC 11 is not covered by any of
        // the test cases. We only consider non-empty columns for the APLC computation: n = 5,
        // m = 10.
        @SuppressWarnings("unused") final boolean[][] coverageMatrixWithUncoveredLine = {
                // 1  2   3   4   5   6   7   8   9  10, 11    m / n
                {XX, __, XX, __, __, __, __, __, __, __, __},   // 1
                {XX, XX, XX, __, XX, __, __, __, __, __, __},   // 2
                {XX, XX, XX, XX, XX, XX, __, __, __, __, __},   // 3
                {__, XX, __, XX, __, __, __, __, __, __, __},   // 4
                {__, __, __, __, __, __, XX, XX, XX, XX, __}    // 5
                //                                       ↑↑
                //                                     ignored
        };

        final var args = new String[]{"-o", "0:1:2:3:4", "-m", "coverageMatrixWithUncoveredLine"};
        new Main(args).start();
        final double actual = Double.parseDouble(outputStreamCaptor.toString());
        final double expected =
                1 - (1d + 2 + 1 + 3 + 2 + 3 + 5 + 5 + 5 + 5) / (5 * 10) + 1d / (2 * 5);
        assert_().that(actual).isWithin(TOLERANCE).of(expected);
    }

    @Test
    void test_aplcComputaiton_matrixWithEmptyTestCase() throws Exception {
        // A coverage matrix with an empty 6th row, meaning that test case 6 does not cover
        // anything. The APLC is computed as usual. n = 6, m = 10.
        @SuppressWarnings("unused") final boolean[][] coverageMatrixWithEmptyTestCase = {
                // 1  2   3   4   5   6   7   8   9  10    m / n
                {XX, __, XX, __, __, __, __, __, __, __},   // 1
                {XX, XX, XX, __, XX, __, __, __, __, __},   // 2
                {XX, XX, XX, XX, XX, XX, __, __, __, __},   // 3
                {__, XX, __, XX, __, __, __, __, __, __},   // 4
                {__, __, __, __, __, __, XX, XX, XX, XX},   // 5
                {__, __, __, __, __, __, __, __, __, __}    // 6 (not ignored)
        };

        final var args = new String[]{"-o", "0:1:2:3:4:5", "-m", "coverageMatrixWithEmptyTestCase"};
        new Main(args).start();
        final double actual = Double.parseDouble(outputStreamCaptor.toString());
        final double expected =
                1 - (1d + 2 + 1 + 3 + 2 + 3 + 5 + 5 + 5 + 5) / (6 * 10) + 1d / (2 * 6);
        assert_().that(actual).isWithin(TOLERANCE).of(expected);
    }

    @Test
    void test_aplcComputation_matrixWithUncoveredLineAndEmptyTestCase() throws Exception {
        // A coverage matrix with an empty 11th column and empty 6th row. We remove the empty column
        // but not the empty row. n = 6, m = 10.
        @SuppressWarnings("unused") final boolean[][] coverageMatrixWithUncoveredLineAndEmptyTestCase = {
                // 1  2   3   4   5   6   7   8   9  10, 11    m / n
                {XX, __, XX, __, __, __, __, __, __, __, __},   // 1
                {XX, XX, XX, __, XX, __, __, __, __, __, __},   // 2
                {XX, XX, XX, XX, XX, XX, __, __, __, __, __},   // 3
                {__, XX, __, XX, __, __, __, __, __, __, __},   // 4
                {__, __, __, __, __, __, XX, XX, XX, XX, __},   // 5
                {__, __, __, __, __, __, __, __, __, __, __},   // 6 (not ignored)
                //                                       ↑↑
                //                                     ignored
        };

        final var args = new String[]{"-o", "0:1:2:3:4:5", "-m",
                "coverageMatrixWithUncoveredLineAndEmptyTestCase"};
        new Main(args).start();
        final double actual = Double.parseDouble(outputStreamCaptor.toString());
        final double expected =
                1 - (1d + 2 + 1 + 3 + 2 + 3 + 5 + 5 + 5 + 5) / (6 * 10) + 1d / (2 * 6);
        assert_().that(actual).isWithin(TOLERANCE).of(expected);
    }
}
