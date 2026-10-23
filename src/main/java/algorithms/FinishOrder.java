package algorithms;

import graph.Graph;

import metrics.Metrics;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/** Explicit DFS frames retain the next adjacency iterator, avoiding recursion. */
final class FinishOrder {
    private record Frame(int vertex, Iterator<Graph.Edge> edges) {}

    static int[] compute(Graph graph, Metrics metrics) {
        boolean[] seen = new boolean[graph.n()];
        int[] order = new int[graph.n()];
        int finishedCount = 0;
        Deque<Frame> stack = new ArrayDeque<>();
        for (int start = 0; start < graph.n(); start++) {
            if (seen[start]) {
                continue;
            }
            seen[start] = true;
            stack.push(new Frame(start, graph.adjacent(start).iterator()));
            metrics.depth(stack.size());
            while (!stack.isEmpty()) {
                Frame frame = stack.peek();
                if (!frame.edges.hasNext()) {
                    order[finishedCount++] = frame.vertex;
                    stack.pop();
                    continue;
                }
                Graph.Edge edge = frame.edges.next();
                metrics.edgeScans++;
                if (!seen[edge.v()]) {
                    seen[edge.v()] = true;
                    stack.push(new Frame(edge.v(), graph.adjacent(edge.v()).iterator()));
                    metrics.depth(stack.size());
                }
            }
        }
        return order;
    }
}
