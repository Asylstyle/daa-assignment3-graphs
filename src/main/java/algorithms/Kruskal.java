package algorithms;

import graph.Graph;

import metrics.Metrics;

import java.util.ArrayList;
import java.util.List;

public final class Kruskal {
    public record Forest(long weight, int edgeCount, int components) {}

    private Kruskal() {}

    public static Forest run(Graph graph, Metrics metrics) {
        if (graph.directed()) {
            throw new IllegalArgumentException("MST needs undirected graph");
        }
        List<Graph.Edge> sorted = new ArrayList<>(graph.edges());
        sorted.sort(
                (first, second) -> {
                    metrics.comparisons++;
                    return Integer.compare(first.weight(), second.weight());
                });
        DisjointSet sets = new DisjointSet(graph.n(), metrics);
        long weight = 0;
        int edgeCount = 0;
        for (Graph.Edge edge : sorted) {
            if (edgeCount == graph.n() - 1) {
                break;
            }
            metrics.edgeScans++;
            if (sets.union(edge.u(), edge.v())) {
                weight += edge.weight();
                edgeCount++;
            }
        }
        return new Forest(weight, edgeCount, sets.components());
    }
}
