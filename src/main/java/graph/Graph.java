package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Vertices are 0..n-1; edges retain input order. Loops and parallel edges are legal. */
public final class Graph {
    public record Edge(int u, int v, int weight) {}

    private final boolean directed;
    private final List<Edge> edges = new ArrayList<>();
    private final List<List<Edge>> adjacency;

    public Graph(int n, boolean directed) {
        if (n < 0) {
            throw new IllegalArgumentException("Negative vertex count");
        }
        this.directed = directed;
        adjacency = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adjacency.add(new ArrayList<>());
        }
    }

    public int n() {
        return adjacency.size();
    }

    public int m() {
        return edges.size();
    }

    public boolean directed() {
        return directed;
    }

    public List<Edge> edges() {
        return Collections.unmodifiableList(edges);
    }

    public List<Edge> adjacent(int u) {
        return Collections.unmodifiableList(adjacency.get(u));
    }

    public void addEdge(int u, int v, int weight) {
        if (u < 0 || v < 0 || u >= n() || v >= n())
            throw new IllegalArgumentException("Vertex outside graph");
        Edge e = new Edge(u, v, weight);
        edges.add(e);
        adjacency.get(u).add(e);
        if (!directed) {
            adjacency.get(v).add(new Edge(v, u, weight));
        }
    }
}
