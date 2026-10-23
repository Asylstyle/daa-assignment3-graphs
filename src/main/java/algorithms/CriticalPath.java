package algorithms;

import graph.Graph;

import metrics.Metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Maximum-weight source-to-sink path in a DAG, including signed edge weights. */
public final class CriticalPath {
    public static final long UNREACHABLE = Long.MIN_VALUE / 4;

    public record Result(long length, int end, long[] distance, int[] parent) {
        public List<Integer> path() {
            List<Integer> vertices = new ArrayList<>();
            for (int vertex = end; vertex != -1; vertex = parent[vertex]) {
                vertices.add(vertex);
            }
            Collections.reverse(vertices);
            return List.copyOf(vertices);
        }
    }

    private CriticalPath() {}

    public static Result run(Graph dag, Metrics metrics) {
        int[] order = Kahn.run(dag, new Metrics());
        long[] distance = new long[dag.n()];
        int[] parent = new int[dag.n()];
        int[] indegree = new int[dag.n()];
        Arrays.fill(distance, UNREACHABLE);
        Arrays.fill(parent, -1);

        for (Graph.Edge edge : dag.edges()) {
            indegree[edge.v()]++;
        }
        for (int vertex = 0; vertex < dag.n(); vertex++) {
            if (indegree[vertex] == 0) {
                distance[vertex] = 0;
            }
        }

        for (int vertex : order) {
            for (Graph.Edge edge : dag.adjacent(vertex)) {
                metrics.edgeScans++;
                metrics.comparisons++;
                if (distance[vertex] == UNREACHABLE) {
                    continue;
                }
                long candidate = distance[vertex] + edge.weight();
                if (candidate > distance[edge.v()]) {
                    distance[edge.v()] = candidate;
                    parent[edge.v()] = vertex;
                }
            }
        }

        int end = -1;
        long length = 0;
        for (int vertex = 0; vertex < dag.n(); vertex++) {
            if (dag.adjacent(vertex).isEmpty() && (end == -1 || distance[vertex] > length)) {
                end = vertex;
                length = distance[vertex];
            }
        }
        return new Result(length, end, distance, parent);
    }
}
