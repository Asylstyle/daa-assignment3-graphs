package graph;

import algorithms.Condensation;
import algorithms.CriticalPath;
import algorithms.Kosaraju;
import algorithms.Tarjan;

import metrics.Metrics;

import java.nio.file.Path;
import java.util.Arrays;

/** Small runnable demonstration of SCC scan counts and the condensation critical path. */
public final class BonusMain {
    private BonusMain() {}

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: graph.BonusMain data/critical.txt");
        }
        Graph graph = GraphReader.read(Path.of(args[0]));
        Metrics tarjanMetrics = new Metrics();
        Metrics kosarajuMetrics = new Metrics();
        Kosaraju.Result tarjan = Tarjan.run(graph, tarjanMetrics);
        Kosaraju.Result kosaraju = Kosaraju.run(graph, kosarajuMetrics);
        System.out.println("Tarjan SCC count: " + tarjan.count());
        System.out.println("Kosaraju SCC count: " + kosaraju.count());
        System.out.println("Tarjan edge scans: " + tarjanMetrics.edgeScans);
        System.out.println("Kosaraju edge scans: " + kosarajuMetrics.edgeScans);
        System.out.println("Vertex -> component: " + Arrays.toString(tarjan.component()));

        Condensation.Result condensed = Condensation.build(graph);
        CriticalPath.Result path = CriticalPath.run(condensed.dag(), new Metrics());
        System.out.println("Condensation longest source-to-sink weight: " + path.length());
        System.out.println("Component path: " + path.path());
    }
}
