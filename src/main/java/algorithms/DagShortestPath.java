package algorithms;

import graph.Graph;

import metrics.Metrics;

import java.util.Arrays;

public final class DagShortestPath {
    public static final long INF = Long.MAX_VALUE / 4;

    public record Result(long[] distance, int[] parent) {
        public long sum() {
            long sum = 0;
            for (long d : distance)
                if (d != INF) {
                    sum += d;
                }
            return sum;
        }
    }

    private DagShortestPath() {}

    public static Result run(Graph graph, Metrics metrics) {
        int[] order =
                Kahn.run(graph, new Metrics()); // preprocessing excluded from relaxation counters
        int[] indegree = new int[graph.n()];
        int[] parent = new int[graph.n()];
        Arrays.fill(parent, -1);
        long[] distance = new long[graph.n()];
        Arrays.fill(distance, INF);
        for (var edge : graph.edges()) {
            indegree[edge.v()]++;
        }
        for (int vertex = 0; vertex < graph.n(); vertex++)
            if (indegree[vertex] == 0) {
                distance[vertex] = 0;
            }
        for (int vertex : order)
            for (var edge : graph.adjacent(vertex)) {
                metrics.edgeScans++;
                metrics.comparisons++;
                if (distance[vertex] != INF
                        && distance[vertex] + edge.weight() < distance[edge.v()]) {
                    distance[edge.v()] = distance[vertex] + edge.weight();
                    parent[edge.v()] = vertex;
                }
            }
        return new Result(distance, parent);
    }
}
