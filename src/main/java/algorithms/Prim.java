package algorithms;

import graph.Graph;

import metrics.Metrics;

import java.util.PriorityQueue;

public final class Prim {
    private Prim() {}

    public static Kruskal.Forest run(Graph graph, Metrics metrics) {
        if (graph.directed()) {
            throw new IllegalArgumentException("MST needs undirected graph");
        }
        boolean[] seen = new boolean[graph.n()];
        long weight = 0;
        int edgeCount = 0;
        int components = 0;
        PriorityQueue<Graph.Edge> frontier =
                new PriorityQueue<>(
                        (first, second) -> {
                            metrics.comparisons++;
                            return Integer.compare(first.weight(), second.weight());
                        });
        for (int start = 0; start < graph.n(); start++) {
            if (seen[start]) {
                continue;
            }
            components++;
            seen[start] = true;
            add(graph, start, seen, frontier, metrics);
            while (!frontier.isEmpty()) {
                Graph.Edge edge = frontier.remove();
                if (seen[edge.v()]) {
                    continue;
                }
                seen[edge.v()] = true;
                weight += edge.weight();
                edgeCount++;
                add(graph, edge.v(), seen, frontier, metrics);
            }
        }
        return new Kruskal.Forest(weight, edgeCount, components);
    }

    private static void add(
            Graph graph,
            int vertex,
            boolean[] seen,
            PriorityQueue<Graph.Edge> frontier,
            Metrics metrics) {
        for (Graph.Edge edge : graph.adjacent(vertex)) {
            metrics.edgeScans++;
            if (!seen[edge.v()]) {
                frontier.add(edge);
            }
        }
    }
}
