package algorithms;

import graph.Graph;

import metrics.Metrics;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public final class Kahn {
    private Kahn() {}

    public static int[] run(Graph graph, Metrics metrics) {
        if (!graph.directed())
            throw new IllegalArgumentException("Topological order needs directed graph");
        int[] indegree = new int[graph.n()];
        int[] order = new int[graph.n()];
        for (var edge : graph.edges()) {
            indegree[edge.v()]++;
        }
        Deque<Integer> ready = new ArrayDeque<>();
        for (int i = 0; i < graph.n(); i++)
            if (indegree[i] == 0) {
                ready.add(i);
            }
        int processedCount = 0;
        while (!ready.isEmpty()) {
            int vertex = ready.remove();
            order[processedCount++] = vertex;
            for (var edge : graph.adjacent(vertex)) {
                metrics.edgeScans++;
                if (--indegree[edge.v()] == 0) {
                    ready.add(edge.v());
                }
            }
        }
        if (processedCount != graph.n()) {
            throw new IllegalArgumentException("Directed cycle");
        }
        return order;
    }

    public static boolean valid(Graph graph, int[] order) {
        if (order.length != graph.n()) {
            return false;
        }
        int[] position = new int[graph.n()];
        Arrays.fill(position, -1);
        for (int i = 0; i < order.length; i++) {
            int vertex = order[i];
            if (vertex < 0 || vertex >= graph.n() || position[vertex] != -1) {
                return false;
            }
            position[vertex] = i;
        }
        for (var edge : graph.edges())
            if (position[edge.u()] >= position[edge.v()]) {
                return false;
            }
        return true;
    }
}
