package de.uni_passau.fim.se2.util;

import static de.uni_passau.fim.se2.util.Randomness.random;
import static java.time.format.DateTimeFormatter.ofPattern;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.BoxChart;
import org.knowm.xchart.BoxChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

public abstract class Plotter {

    private Plotter() {
        // prevent instantiation of class
    }

    public static Plotter newGraphicalPlotter() {
        return new Plotter.Graphical();
    }

    public static Plotter newHeadlessPlotter() {
        return new Plotter.Headless();
    }

    private static void exampleUsage() {
        final String[] algorithmNames = {"SA", "RW", "RS"};

        final double[] fitnessValuesSA = random().doubles(1000).toArray();
        final double[] fitnessValuesRW = random().doubles(1000).toArray();
        final double[] fitnessValuesRS = random().doubles(1000).toArray();
        final double[][] fitnessValues = {fitnessValuesSA, fitnessValuesRW, fitnessValuesRS};

        final Plotter plotter = newGraphicalPlotter();
        plotter.fitnessBoxplot(algorithmNames, fitnessValues);
        plotter.fitnessTimeline(algorithmNames, fitnessValues);
    }

    private static XYChart timeline(final String[] algorithms, final double[][] fitnessValues) {
        assert fitnessValues.length == algorithms.length;

        final XYChart chart = new XYChartBuilder()
                .title("Fitness Timeline")
                .xAxisTitle("Time")
                .yAxisTitle("Fitness")
                .build();

        for (int i = 0; i < fitnessValues.length; i++) {
            chart.addSeries(algorithms[i], fitnessValues[i]);
        }

        return chart;
    }

    private static BoxChart boxplot(final String[] algorithms, final double[][] fitnessValues) {
        assert fitnessValues.length == algorithms.length;

        final BoxChart chart = new BoxChartBuilder()
                .title("Fitness Boxplot")
                .xAxisTitle("Algorithm")
                .yAxisTitle("Fitness")
                .build();

        for (int i = 0; i < fitnessValues.length; i++) {
            chart.addSeries(algorithms[i], fitnessValues[i]);
        }

        return chart;
    }

    /**
     * Plots fitness over time for the given algorithms using the specified fitness values.
     *
     * @param algorithms algorithm names, used as x-axis title
     * @param values     fitness values over, per algorithm
     */
    public abstract void fitnessTimeline(final String[] algorithms, final double[][] values);

    /**
     * Plots fitness box charts for the given algorithms using the specified fitness values.
     *
     * @param algorithms algorithm names, used as x-axis title
     * @param values     fitness values, per algorithm
     */
    public abstract void fitnessBoxplot(final String[] algorithms, final double[][] values);

    /**
     * Headless plotter that writes its charts to PNG files.
     */
    private static class Headless extends Plotter {

        private static final String outDirName = "./png";

        private Headless() {
            try {
                createOutDir(Paths.get(outDirName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static void createOutDir(final Path outDir) throws IOException {
            if (!Files.exists(outDir)) {
                try {
                    Files.createDirectory(outDir);
                } catch (IOException e) {
                    throw new IOException("Could not create directory: " + outDir, e);
                }
            } else if (!Files.isDirectory(outDir)) {
                throw new IOException("Cannot create directory because file with same name already "
                        + "exists: " + outDir);
            }
        }

        private static String now() {
            return LocalDateTime.now()
                    .truncatedTo(ChronoUnit.MINUTES)
                    .format(ofPattern("yyyyMMdd-HHmmss"));
        }

        @Override
        public void fitnessTimeline(final String[] algorithms, final double[][] values) {
            final var timeline = timeline(algorithms, values);
            final String filename = outDirName + "/fitnessTimeline-" + now() + ".png";
            try {
                BitmapEncoder.saveBitmapWithDPI(timeline, filename, BitmapFormat.PNG, 72);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void fitnessBoxplot(final String[] algorithms, final double[][] values) {
            final var boxplot = boxplot(algorithms, values);
            final String filename =
                    outDirName + "/fitnessBoxplot" + now() + ".png";

            try {
                BitmapEncoder.saveBitmapWithDPI(boxplot, filename, BitmapFormat.PNG, 72);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Plotter that displays its chart in a graphical window.
     */
    private static class Graphical extends Plotter {

        private Graphical() {
            // prevent instantiation of class
        }

        @Override
        public void fitnessTimeline(final String[] algorithms, final double[][] values) {
            new SwingWrapper<>(timeline(algorithms, values)).displayChart();
        }

        @Override
        public void fitnessBoxplot(final String[] algorithms, final double[][] values) {
            new SwingWrapper<>(boxplot(algorithms, values)).displayChart();
        }
    }
}
