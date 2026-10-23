package algorithms;

import graph.Graph;

import metrics.Metrics;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;

/** One-pass SCC search. DFS frames and the active-vertex stack are explicit. */
public final class Tarjan {
    private record Frame(int vertex, int parent, Iterator<Graph.Edge> edges) {}

    private Tarjan() {}

    public static Kosaraju.Result run(Graph graph, Metrics metrics) {
        if (!graph.directed()) {
            throw new IllegalArgumentException("SCC needs directed graph");
        }

        int[] index = new int[graph.n()];
        int[] lowlink = new int[graph.n()];
        int[] component = new int[graph.n()];
        boolean[] onStack = new boolean[graph.n()];
        Arrays.fill(index, -1);
        Arrays.fill(component, -1);

        Deque<Frame> dfs = new ArrayDeque<>();
        Deque<Integer> active = new ArrayDeque<>();
        int nextIndex = 0;
        int componentCount = 0;

        for (int start = 0; start < graph.n(); start++) {
            if (index[start] != -1) {
                continue;
            }
            index[start] = lowlink[start] = nextIndex++;
            active.push(start);
            onStack[start] = true;
            dfs.push(new Frame(start, -1, graph.adjacent(start).iterator()));
            metrics.depth(dfs.size());

            while (!dfs.isEmpty()) {
                Frame frame = dfs.peek();
                int vertex = frame.vertex();

                if (frame.edges().hasNext()) {
                    Graph.Edge edge = frame.edges().next();
                    metrics.edgeScans++;
                    int neighbor = edge.v();

                    if (index[neighbor] == -1) {
                        index[neighbor] = lowlink[neighbor] = nextIndex++;
                        active.push(neighbor);
                        onStack[neighbor] = true;
                        dfs.push(new Frame(neighbor, vertex, graph.adjacent(neighbor).iterator()));
                        metrics.depth(dfs.size());
                    } else if (onStack[neighbor]) {
                        // Only an edge to an active vertex can connect this unfinished SCC.
                        lowlink[vertex] = Math.min(lowlink[vertex], index[neighbor]);
                    }
                    continue;
                }

                dfs.pop();
                if (lowlink[vertex] == index[vertex]) {
                    int member;
                    do {
                        member = active.pop();
                        onStack[member] = false;
                        component[member] = componentCount;
                    } while (member != vertex);
                    componentCount++;
                }

                if (frame.parent() != -1) {
                    // Returning from a DFS child propagates its reachable active ancestor.
                    int parent = frame.parent();
                    lowlink[parent] = Math.min(lowlink[parent], lowlink[vertex]);
                }
            }
        }
        return new Kosaraju.Result(componentCount, component);
    }
}
