package algorithms;

import graph.Graph;

import metrics.Metrics;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public final class Kosaraju {
    public record Result(int count, int[] component) {}

    private Kosaraju() {}

    public static Result run(Graph graph, Metrics metrics) {
        if (!graph.directed()) {
            throw new IllegalArgumentException("SCC needs directed graph");
        }
        // First pass records vertices after all descendants have finished.
        int[] order = FinishOrder.compute(graph, metrics);
        Graph transpose = new Graph(graph.n(), true);
        for (Graph.Edge edge : graph.edges()) {
            transpose.addEdge(edge.v(), edge.u(), edge.weight());
        }
        int[] component = new int[graph.n()];
        Arrays.fill(component, -1);
        int count = 0;
        Deque<Integer> stack = new ArrayDeque<>();
        for (int position = order.length - 1; position >= 0; position--) {
            int start = order[position];
            if (component[start] != -1) {
                continue;
            }
            component[start] = count;
            stack.push(start);
            metrics.depth(stack.size());
            while (!stack.isEmpty()) {
                int vertex = stack.pop();
                for (Graph.Edge edge : transpose.adjacent(vertex)) {
                    metrics.edgeScans++;
                    if (component[edge.v()] == -1) {
                        component[edge.v()] = count;
                        stack.push(edge.v());
                        metrics.depth(stack.size());
                    }
                }
            }
            count++;
        }
        return new Result(count, component);
    }
}
