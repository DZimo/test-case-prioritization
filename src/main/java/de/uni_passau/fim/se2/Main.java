package de.uni_passau.fim.se2;

import static de.uni_passau.fim.se2.Bridge.buildAlgorithm;
import static de.uni_passau.fim.se2.Bridge.buildMaxFitnessEvalsCondition;
import static de.uni_passau.fim.se2.Bridge.buildMaxTimeCondition;
import static de.uni_passau.fim.se2.Bridge.computeAPLC;
import static de.uni_passau.fim.se2.Main.Algorithm.RS;
import static de.uni_passau.fim.se2.Main.Algorithm.SA;
import static de.uni_passau.fim.se2.util.Randomness.random;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.stream.Collectors.joining;

import de.uni_passau.fim.se2.metaheuristics.algorithms.SearchAlgorithm;
import de.uni_passau.fim.se2.metaheuristics.stopping_conditions.OneOf;
import de.uni_passau.fim.se2.metaheuristics.stopping_conditions.StoppingCondition;
import de.uni_passau.fim.se2.util.CSVExporter;
import de.uni_passau.fim.se2.util.CoverageTracker;
import de.uni_passau.fim.se2.util.CoverageTrackerImpl;
import de.uni_passau.fim.se2.util.CoverageTrackerMock;
import de.uni_passau.fim.se2.util.Randomness;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.DoubleStream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Main class of the application, implements the CLI and input/output.
 */
public class Main {

    /**
     * Name of the JAR artefact.
     */
    private static final String jarName = "test-prioritization-1.0-SNAPSHOT.jar";

    /**
     * Command line option names.
     */
    private static final String
            classOpt = "class",
            packageOpt = "package",
            fitnessEvalsOpt = "fitness-evals",
            timeOpt = "time",
            repetitionsOpt = "repetitions",
            algorithmsOpt = "algorithms",
            orderingOpt = "ordering",
            seedOpt = "seed",
            quiteOpt = "quiet",
            matrixOpt = "matrix";

    /**
     * Separator for multiple arguments of a command line option.
     */
    private static final String separator = ":";

    /**
     * The default algorithms to run (when not explicitly specified by the user)
     */
    private static final Set<Algorithm> defaultAlgos = Set.of(SA, RS);

    /**
     * Which algorithms to run. By default, Simulated Annealing and Random Search.
     */
    private final Set<Algorithm> algorithms = new LinkedHashSet<>(defaultAlgos);

    /**
     * The names of the test cases (corresponding to the coverage matrix). That is, for and index
     * {@code i}, {@code testCases[i]} tells the name of the ith test case and
     * {@code coverageMatrix[i]} tells which lines of code are covered by the ith test case.
     */
    private String[] testCases;

    /**
     * The coverage matrix for the analyzed software system.
     */
    private boolean[][] coverageMatrix;

    /**
     * If set, the ordering for which to compute the APLC value, using either a measured coverage
     * matrix (options "-c" and "-p") or a loaded coverage matrix (option "-m").
     */
    private int[] ordering = null;

    /**
     * The name of the analyzed CUT or the loaded coverage matrix.
     */
    private String subject = null;

    /**
     * The default package to use (when not explicitly specified by the user).
     */
    private static final String defaultPackage = "de.uni_passau.fim.se2.examples";

    /**
     * The name of the package containing the CUT. By default, {@code
     * de.uni_passau.fim.se2.examples}.
     */
    private String packageUnderTest = defaultPackage;

    /**
     * The default stopping condition to use (when not explicitly specified by the user).
     */
    private static final StoppingCondition defaultCondition =
            buildMaxFitnessEvalsCondition(1000);

    /**
     * The stopping condition to use for the search. By default, {@code MaxFitnessEvaluations}
     * with 1000 evaluations.
     */
    private StoppingCondition stoppingCondition = defaultCondition;

    /**
     * The default number of repetitions (when not explicitly specified by the user).
     */
    private static final int defaultReps = 30;

    /**
     * How often to repeat the search. By default, 30 times.
     */
    private int repetitions = defaultReps;

    /**
     * The default setting for "quiet mode" (when not explicitly specified by the user).
     */
    private static final boolean defaultQuiet = false;

    /**
     * Whether "quiet mode" is enabled or disabled. By default, disabled.
     */
    private boolean quiet = defaultQuiet;

    /**
     * The command line parameters parsed against the {@code options} descriptor.
     */
    private final CommandLine cmd;

    /**
     * The command line parameters given by the user.
     */
    private final String[] args;

    /**
     * The command line options.
     *
     * <pre>{@code
     * usage: java -jar test-prioritization-1.0-SNAPSHOT.jar
     *  -a,--algorithms <arg>      which algorithms to use (any combination of
     *                             "RS:RW:SA"; default: "SA:RS")
     *  -c,--class <arg>           the name of the class under test
     *  -f,--fitness-evals <arg>   maximum number of fitness evaluations per
     *                             repetition
     *  -m,--matrix <arg>          load coverage matrix with the given name
     *  -o,--ordering <arg>        specify an ordering manually (e.g., "1:2:0")
     *  -p,--package <arg>         the package containing the class under test
     *                             (default: "de.uni_passau.fim.se2.examples")
     *  -q,--quiet                 redirect some console output to files
     *                             (default: "false")
     *  -r,--repetitions <arg>     how often to repeat the search (default: "30")
     *  -s,--seed <arg>            use a fixed RNG seed
     *  -t,--time <arg>            maximum search time, in seconds or "HH:MM:SS"
     *                             per repetition
     * }</pre>
     */
    private static final Options options = new Options() {{
        addOption(Option.builder("p")
                .longOpt(packageOpt)
                .desc(format("the package containing the class under test (default: \"%s\")",
                        defaultPackage))
                .hasArg()
                .build());

        addOption(Option.builder("f")
                .longOpt(fitnessEvalsOpt)
                .desc("maximum number of fitness evaluations per repetition")
                .hasArg()
                .build());

        addOption(Option.builder("t")
                .longOpt(timeOpt)
                .desc(format("maximum search time per repetition, in seconds or \"%s\"",
                        join(separator, "HH", "MM", "SS")))
                .hasArg()
                .build());

        addOption(Option.builder("r")
                .longOpt(repetitionsOpt)
                .desc(format("how often to repeat the search (default: \"%s\")", defaultReps))
                .hasArg()
                .build());

        addOption(Option.builder("s")
                .longOpt(seedOpt)
                .desc("use a fixed RNG seed")
                .hasArg()
                .build());

        addOption(Option.builder("q")
                .longOpt(quiteOpt)
                .desc(format("redirect some console output to files (default: \"%s\")",
                        defaultQuiet))
                .build());

        final var cut = Option.builder("c")
                .longOpt(classOpt)
                .desc("the name of the class under test")
                .hasArg()
                .build();

        final var matrix = Option.builder("m")
                .longOpt(matrixOpt)
                .desc("load coverage matrix with the given name")
                .hasArg()
                .build();

        // Can't use both "matrix" and "class" options together. One of the two is required.
        final var cutAndMatrix = new OptionGroup();
        cutAndMatrix.addOption(cut);
        cutAndMatrix.addOption(matrix);
        cutAndMatrix.setRequired(true);
        addOptionGroup(cutAndMatrix);

        final var algo = Option.builder("a")
                .longOpt(algorithmsOpt)
                .desc(format("which algorithms to use (any combination of \"%s\"; default: \"%s\")",
                        Algorithm.abbrevs(),
                        defaultAlgos.stream().map(a -> a.abbrev).collect(joining(separator))))
                .hasArg()
                .build();

        final var ord = Option.builder("o")
                .longOpt(orderingOpt)
                .desc(format("specify an ordering manually (e.g., \"%s\")",
                        join(separator, "1", "2", "0")))
                .hasArg()
                .build();

        // Can't use the "algorithms" option together with "ordering". One of the two is required.
        final var algoAndOrd = new OptionGroup();
        algoAndOrd.addOption(algo);
        algoAndOrd.addOption(ord);
        algoAndOrd.setRequired(true);
        addOptionGroup(algoAndOrd);
    }};

    /**
     * Creates a new {@code Main} application.
     *
     * @param args the command line arguments, not {@code null}
     * @throws Exception when an error occurs while initializing the application
     */
    Main(final String[] args) throws Exception {
        Objects.requireNonNull(args);
        this.args = args;
        this.cmd = parse();
        handleUnknownOptions();
        query();
    }

    /**
     * Main entry point of the program.
     *
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        if (args.length == 0) {
            printHelp();
            return;
        }

        try {
            new Main(args).start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Prints a help message.
     */
    private static void printHelp() {
        final var syntax = "java -jar " + jarName;
        new HelpFormatter().printHelp(syntax, options);
    }

    /**
     * Parses the given command line strings against the {@code options} descriptor. Returns a
     * queryable {@code CommandLine} object if successful.
     *
     * @return the parsed command line
     * @throws ParseException when the command line cannot be parsed
     */
    private CommandLine parse() throws ParseException {
        return new DefaultParser().parse(options, args);
    }

    /**
     * Runs the application.
     *
     * @throws IOException when an error occurs while writing the results to disk
     */
    void start() throws IOException {
        final boolean runSearch = ordering == null;
        if (runSearch) {
            final var summary = performSearch();
            write(summary);
            csvExport(summary, subject + "-");
        } else {
            write(computeAPLC(coverageMatrix, ordering));
        }
    }

    /**
     * Exports the given execution results in CSV format.
     *
     * @param results the execution results
     * @throws IOException when there was an error while writing the CSV file
     */
    private void csvExport(final Map<Algorithm, double[]> results, final String fileNamePrefix)
            throws IOException {
        final CSVExporter exporter = new CSVExporter(null, fileNamePrefix);

        // Write the CSV header.
        final Object[] columns = new Object[repetitions];
        Arrays.setAll(columns, i -> i + 1);
        exporter.appendLine("Algorithm", columns);

        // Write the APLC values for each algorithm.
        for (final var entry : results.entrySet()) {
            final String algorithm = entry.getKey().toString;
            final double[] aplcValues = entry.getValue();
            exporter.appendLine(algorithm, aplcValues);
        }
    }

    /**
     * Repeatedly executes the given search algorithm. The number of repetitions was specified on
     * the command line, or corresponds to the default value if nothing was specified. Returns an
     * array of the resulting APLC values. The array contains one entry for every repetition. Every
     * entry is the best APLC value of its repetition.
     *
     * @param search the search algorithm to run
     * @return APLC values as search results
     */
    private double[] repeatSearch(final SearchAlgorithm<?> search) {
        final var aplcValues = new double[repetitions];

        for (int i = 0; i < repetitions; i++) {
            System.out.println("   > Repetition " + i);

            final long start = System.currentTimeMillis();
            final var solution = search.findSolution();
            final long totalTime = System.currentTimeMillis() - start;

            final double aplcValue = Bridge.computeAPLC(coverageMatrix, solution);
            aplcValues[i] = aplcValue;

            final String testCaseOrder = Bridge.getTestCaseOrder(testCases, solution);
            System.out.println("      - Ordering: " + testCaseOrder);
            System.out.println("      - APLC: " + aplcValue);
            System.out.printf("      - Time: %fs%n", totalTime / 1000d);
        }

        return aplcValues;
    }

    /**
     * Performs the search, returning a mapping from executed search algorithms to the
     * corresponding results found by the algorithms.
     *
     * @return search results
     */
    private Map<Algorithm, double[]> performSearch() {
        final var random = Randomness.random();
        final var results = new LinkedHashMap<Algorithm, double[]>();

        for (final var algorithm : algorithms) {
            System.out.println(" * Executing " + algorithm.toString);

            final var search = buildAlgorithm(algorithm, random, stoppingCondition, coverageMatrix);
            final double[] solutions = repeatSearch(search);
            results.put(algorithm, solutions);
        }

        return results;
    }

    /**
     * Prints the results to stdout if quiet mode is off, otherwise prints to a file {@code
     * results.txt} in the current working directory.
     *
     * @param results the results to print
     * @throws FileNotFoundException when the results cannot be written
     */
    private void write(final Map<Algorithm, double[]> results) throws FileNotFoundException {
        if (quiet) {
            final var fileName = format("./results-%s.txt", subject);
            try (final PrintStream out = new PrintStream(new FileOutputStream(fileName))) {
                write(out, results);
            }
        } else {
            write(System.out, results);
        }
    }

    /**
     * Writes the given results to the given {@code PrintStream}.
     * @param out the {@code PrintStream} to write to
     * @param results the results to write
     */
    private void write(final PrintStream out, final Map<Algorithm, double[]> results) {
        out.println("Command line args: " + join(" ", args));
        out.println("Summary: " + subject);
        for (final var entry : results.entrySet()) {
            final Algorithm algorithm = entry.getKey();
            final double[] values = entry.getValue();
            final var stats = DoubleStream.of(values).summaryStatistics();
            out.println(" * " + algorithm.toString);
            out.println("   > min: " + stats.getMin());
            out.println("   > avg: " + stats.getAverage());
            out.println("   > max: " + stats.getMax());
        }
    }

    /**
     * Prints the results to stdout if quiet mode is off, otherwise prints to a file {@code
     * results.txt} in the current working directory.
     *
     * @param aplcValue the result to print
     * @throws FileNotFoundException when the results cannot be written
     */
    private void write(final double aplcValue) throws FileNotFoundException {
        if (quiet) {
            final var fileName = format("./aplc-%s.txt", subject);
            try (final PrintStream out = new PrintStream(new FileOutputStream(fileName))) {
                out.println(Arrays.toString(args));
                out.println(aplcValue);
            }
        } else {
            System.out.println(aplcValue);
        }
    }

    /**
     * Queries the given command line object and applies the options set by the user.
     *
     * @throws Exception when an error occurs
     */
    private void query() throws Exception {
        setAlgorithms(); // -a
        setStoppingCondition(); // -f, -t
        setCoverageMatrixAndTestCaseNames(); // -c, -p, -m
        setRepetitions(); // -r
        setSeed(); // -s
        setOrdering(); // -o
        setQuiet(); // -q
    }

    /**
     * Handles unknown command line options.
     */
    private void handleUnknownOptions() {
        final String[] leftOver = cmd.getArgs();
        if (leftOver.length > 0) {
            throw new IllegalArgumentException("unknown options: " + Arrays.toString(leftOver));
        }
    }

    /**
     * Sets the algorithms to the ones specified by the user on the command line.
     */
    private void setAlgorithms() {
        if (cmd.hasOption(algorithmsOpt)) {
            algorithms.clear();
            final String[] algos = cmd.getOptionValue(algorithmsOpt).split(separator);
            for (final String algo : algos) {
                switch (algo.strip()) {
                    case "RS" -> algorithms.add(RS);
                    case "RW" -> algorithms.add(Algorithm.RW);
                    case "SA" -> algorithms.add(Algorithm.SA);
                    default -> throw new IllegalArgumentException("Unknown algorithm: " + algo);
                }
            }
        }
    }

    /**
     * Using either package and class name, or the matrix given on the command line, tries to
     * initialize a coverage matrix along with the names of the test cases.
     *
     * @throws Exception if an error occurred while constructing the coverage matrix
     */
    private void setCoverageMatrixAndTestCaseNames() throws Exception {
        final CoverageTracker tracker;

        if (cmd.hasOption(classOpt)) {
            final String classUnderTest = cmd.getOptionValue(classOpt);
            subject = classUnderTest;

            if (cmd.hasOption(packageOpt)) {
                packageUnderTest = cmd.getOptionValue(packageOpt);
            }

            final String fullyQualifiedClassName = packageUnderTest + "." + classUnderTest;
            tracker = new CoverageTrackerImpl(fullyQualifiedClassName);
        } else {
            final String matrixName = cmd.getOptionValue(matrixOpt);
            subject = matrixName;
            tracker = new CoverageTrackerMock(matrixName);
        }

        this.coverageMatrix = tracker.getCoverageMatrix();
        this.testCases = tracker.getTestCases();
    }

    /**
     * Sets the stopping condition to the one specified by the user on the command line.
     */
    private void setStoppingCondition() {
        if (cmd.hasOption(fitnessEvalsOpt) && cmd.hasOption(timeOpt)) {
            final var fitnessSc = parseMaxFitnessEvals();
            final var timeSc = parseTime();
            this.stoppingCondition = new OneOf(fitnessSc, timeSc);
        } else if (cmd.hasOption(fitnessEvalsOpt)) {
            this.stoppingCondition = parseMaxFitnessEvals();
        } else if (cmd.hasOption(timeOpt)) {
            this.stoppingCondition = parseTime();
        }
    }

    private StoppingCondition parseMaxFitnessEvals() {
        final String value = cmd.getOptionValue(fitnessEvalsOpt);

        final int fitnessEvals = parseInt(value);
        if (fitnessEvals < 0) {
            throw new IllegalArgumentException("Negative fitness evaluations: " + fitnessEvals);
        }

        return buildMaxFitnessEvalsCondition(fitnessEvals);
    }

    private StoppingCondition parseTime() {
        final String value = cmd.getOptionValue(timeOpt);

        final String[] parts = value.split(separator);
        if (parts.length == 1) {
            final int seconds = parseInt(parts[0]);
            return buildMaxTimeCondition(seconds);
        } else if (parts.length == 3) {
            final int hours = parseInt(parts[0]);
            final int minutes = parseInt(parts[1]);
            final int seconds = parseInt(parts[2]);
            return buildMaxTimeCondition(hours, minutes, seconds);
        } else {
            throw new IllegalArgumentException("Invalid time format: " + value);
        }
    }

    /**
     * Sets the number of repetitions to the one specified by the user on the command line.
     */
    private void setRepetitions() {
        if (cmd.hasOption(repetitionsOpt)) {
            final String value = cmd.getOptionValue(repetitionsOpt);
            repetitions = parseInt(value);
            if (repetitions < 0) {
                throw new IllegalArgumentException("Negative repetitions count: " + repetitions);
            }
        }
    }

    /**
     * Sets the RNG seed to the one specified by the user on the command line.
     */
    private void setSeed() {
        if (cmd.hasOption(seedOpt)) {
            final String value = cmd.getOptionValue("seed");
            final long seed = Long.parseLong(value);
            random().setSeed(seed);
        }
    }

    /**
     * Sets the test case ordering to the one specified by the user on the command line.
     */
    private void setOrdering() {
        if (cmd.hasOption(orderingOpt)) {
            final String[] ordering = cmd.getOptionValue(orderingOpt).split(separator);
            this.ordering = new int[ordering.length];
            for (int i = 0; i < ordering.length; i++) {
                final String s = ordering[i].strip();
                this.ordering[i] = parseInt(s);
            }
        }
    }

    /**
     * Sets the "quiet" option as per the command line.
     */
    private void setQuiet() {
        if (cmd.hasOption(quiteOpt)) {
            quiet = true;
        }
    }

    /**
     * Symbolic representation of search algorithm implementations.
     */
    enum Algorithm {

        /**
         * Random Search.
         */
        RS("RS", "Random Search"),

        /**
         * Random Walk.
         */
        RW("RW", "Random Walk"),

        /**
         * Simulated Annealing.
         */
        SA("SA", "Simulated Annealing");

        /**
         * The abbreviated name of an algorithm (used on the command line).
         */
        private final String abbrev;

        /**
         * The full name of an algorithm (used for pretty printing).
         */
        private final String toString;

        Algorithm(final String abbrev, final String toString) {
            this.abbrev = abbrev;
            this.toString = toString;
        }

        /**
         * Returns all abbreviated names, separated by the command line value separator.
         *
         * @return abbreviated names
         */
        private static String abbrevs() {
            final var s = new StringJoiner(separator);
            for (final Algorithm algo : Algorithm.values()) {
                s.add(algo.abbrev);
            }
            return s.toString();
        }

        @Override
        public String toString() {
            return toString;
        }
    }
}
