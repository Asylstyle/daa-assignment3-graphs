package algorithms;

import graph.Graph;

import metrics.Metrics;

/** Collapse SCCs into zero-duration blocks, keeping weighted inter-component edges. */
public final class Condensation {
    public record Result(Graph dag, int[] component) {}

    private Condensation() {}

    public static Result build(Graph graph) {
        Kosaraju.Result scc = Tarjan.run(graph, new Metrics());
        Graph dag = new Graph(scc.count(), true);
        for (Graph.Edge edge : graph.edges()) {
            int from = scc.component()[edge.u()];
            int to = scc.component()[edge.v()];
            if (from != to) {
                // Parallel edges retain their original weights; no arbitrary aggregation.
                dag.addEdge(from, to, edge.weight());
            }
        }
        return new Result(dag, scc.component());
    }
}
