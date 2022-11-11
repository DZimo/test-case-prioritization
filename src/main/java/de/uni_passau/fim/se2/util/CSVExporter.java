package de.uni_passau.fim.se2.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class CSVExporter {

    public static final String OUT_DIR_NAME = "./csv";
    public static final String FILE_NAME_PREFIX = "results-";
    public static final String FILE_NAME_SUFFIX = ".csv";
    public static final String VALUE_SEPARATOR = ",";
    public static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String linebreak = "\\R";

    private final Path outFile;
    private final String separator;

    public CSVExporter(
            final String separator,
            final String fileNamePrefix,
            final String outDirName,
            final Object... header)
            throws IOException {
        if (separator == null) {
            this.separator = CSVExporter.VALUE_SEPARATOR;
        } else if (separator.isBlank()) {
            throw new IllegalArgumentException("Invalid separator: " + separator);
        } else {
            this.separator = separator;
        }

        if (outDirName == null || outDirName.isBlank()) {
            throw new IllegalArgumentException("Invalid directory name: " + outDirName);
        }

        this.outFile = createFile(fileNamePrefix, Paths.get(outDirName));

        if (header != null) {
            appendLine(header);
        }
    }

    public CSVExporter(final String separator, final String fileNamePrefix,
            final String outDirName)
            throws IOException {
        this(separator, fileNamePrefix, outDirName, (Object[]) null);
    }

    public CSVExporter(final String separator, final String fileNamePrefix) throws IOException {
        this(separator, fileNamePrefix, CSVExporter.OUT_DIR_NAME);
    }

    public CSVExporter(final String separator) throws IOException {
        this(separator, CSVExporter.FILE_NAME_PREFIX);
    }

    public CSVExporter() throws IOException {
        this(CSVExporter.VALUE_SEPARATOR);
    }

    private static Path createFile(final String fileNamePrefix, final Path outDir)
            throws IOException {
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

        try {
            return Files.createTempFile(outDir, fileNamePrefix, CSVExporter.FILE_NAME_SUFFIX);
        } catch (IOException e) {
            throw new IOException("Could not create file in directory: " + outDir);
        }
    }

    public void appendLine(final Object... values) throws IOException, NullPointerException {
        if (values == null) {
            throw new NullPointerException("values are null");
        }

        final String line = join(escape(toString(values)));
        try {
            appendLine(line);
        } catch (IOException e) {
            throw new IOException("Cannot append line \"" + line + "\"", e);
        }
    }

    public void appendLine(final double... values) throws IOException, NullPointerException {
        if (values == null) {
            throw new NullPointerException("values are null");
        }

        final String line = join(escape(toString(values)));
        try {
            appendLine(line);
        } catch (IOException e) {
            throw new IOException("Cannot append line \"" + line + "\"", e);
        }
    }

    public void appendLine(final String desc, final Object... values) throws IOException,
            NullPointerException {
        if (values == null) {
            throw new NullPointerException("values are null");
        }

        final Object[] toAppend = new Object[values.length + 1];
        toAppend[0] = desc;
        System.arraycopy(values, 0, toAppend, 1, values.length);

        appendLine(toAppend);
    }

    public void appendLine(final String desc, final double... values) throws IOException,
            NullPointerException {
        if (values == null) {
            throw new NullPointerException("values are null");
        }

        final Object[] toAppend = new Object[values.length + 1];
        toAppend[0] = desc;
        for (int i = 0; i < values.length; ) {
            final double value = values[i++];
            toAppend[i] = value;
        }

        appendLine(toAppend);
    }

    private String[] toString(final Object[] values) {
        final String[] strings = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            strings[i] = Objects.toString(values[i]);
        }
        return strings;
    }

    private String[] toString(final double[] values) {
        final String[] strings = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            strings[i] = Objects.toString(values[i]);
        }
        return strings;
    }

    private void appendLine(final String content) throws IOException {
        final String line = content + LINE_SEPARATOR;
        try {
            Files.writeString(outFile, line, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new IOException("Cannot append to file: " + outFile, e);
        }
    }

    private String join(final String... values) {
        return String.join(separator, values);
    }

    private String[] escape(final String[] unescaped) {
        final String[] escaped = new String[unescaped.length];
        for (int i = 0; i < unescaped.length; i++) {
            escaped[i] = escape(unescaped[i]);
        }
        return escaped;
    }

    private String escape(final String unescaped) {
        return unescaped
                .replaceAll(linebreak, " ")
                .replaceAll(separator, "\\" + separator);
    }
}
