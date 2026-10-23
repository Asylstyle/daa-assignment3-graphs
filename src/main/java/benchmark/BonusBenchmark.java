package benchmark;

import algorithms.Kosaraju;
import algorithms.Tarjan;

import graph.Graph;

import metrics.Metrics;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Locale;

/** Separate measured SCC comparison; the required 50-row CSV is never modified. */
public final class BonusBenchmark {
    private BonusBenchmark() {}

    public static void main(String[] args) throws IOException {
        Path output = Path.of(args.length == 0 ? "results/bonus-scc.csv" : args[0]);
        Files.createDirectories(output.toAbsolutePath().getParent());
        Path temporary = output.resolveSibling(output.getFileName() + ".tmp");
        try (PrintWriter csv = new PrintWriter(Files.newBufferedWriter(temporary))) {
            csv.println("algorithm,n,m,time_ms,edge_scans,max_depth,scc_count");
            int caseIndex = 0;
            for (int size : References.SIZES) {
                for (int density : new int[] {4, 20}) {
                    Graph graph = Generators.generate(size, size * density, "directed");
                    Kosaraju.Result expected = Kosaraju.run(graph, new Metrics());
                    References.check(caseIndex, "Kosaraju", expected.count());
                    for (String algorithm : new String[] {"Kosaraju", "Tarjan"}) {
                        double[] times = new double[5];
                        Metrics metrics = null;
                        for (int run = 0; run < times.length; run++) {
                            metrics = new Metrics();
                            long start = System.nanoTime();
                            Kosaraju.Result result =
                                    algorithm.equals("Tarjan")
                                            ? Tarjan.run(graph, metrics)
                                            : Kosaraju.run(graph, metrics);
                            times[run] = (System.nanoTime() - start) / 1e6;
                            checkPartition(expected, result);
                            long expectedScans = (algorithm.equals("Tarjan") ? 1L : 2L) * graph.m();
                            if (metrics.edgeScans != expectedScans) {
                                throw new IllegalStateException("Unexpected SCC scan count");
                            }
                        }
                        Arrays.sort(times);
                        csv.printf(
                                Locale.ROOT,
                                "%s,%d,%d,%.6f,%d,%d,%d%n",
                                algorithm,
                                size,
                                graph.m(),
                                times[2],
                                metrics.edgeScans,
                                metrics.maxDepth,
                                expected.count());
                        System.out.println(
                                algorithm
                                        + " "
                                        + size
                                        + "/"
                                        + graph.m()
                                        + ": scans="
                                        + metrics.edgeScans
                                        + ", SCCs="
                                        + expected.count());
                    }
                    caseIndex++;
                }
            }
            if (csv.checkError()) {
                throw new IOException("Bonus CSV write failed");
            }
        }
        Files.move(temporary, output, StandardCopyOption.REPLACE_EXISTING);
    }

    private static void checkPartition(Kosaraju.Result expected, Kosaraju.Result actual) {
        if (expected.count() != actual.count()) {
            throw new IllegalStateException("SCC counts disagree");
        }
        int[] forward = new int[expected.count()];
        int[] reverse = new int[actual.count()];
        Arrays.fill(forward, -1);
        Arrays.fill(reverse, -1);
        for (int vertex = 0; vertex < expected.component().length; vertex++) {
            int first = expected.component()[vertex];
            int second = actual.component()[vertex];
            if ((forward[first] != -1 && forward[first] != second)
                    || (reverse[second] != -1 && reverse[second] != first)) {
                throw new IllegalStateException("SCC partitions disagree");
            }
            forward[first] = second;
            reverse[second] = first;
        }
    }
}
