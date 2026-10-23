package algorithms;

import metrics.Metrics;

public final class DisjointSet {
    private final int[] parent;
    private final int[] size;
    private int components;
    private final Metrics metrics;

    public DisjointSet(int n, Metrics metrics) {
        if (n < 0) {
            throw new IllegalArgumentException("Negative size");
        }
        this.metrics = metrics;
        parent = new int[n];
        size = new int[n];
        components = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    public static DisjointSet makeSet(int n, Metrics metrics) {
        return new DisjointSet(n, metrics);
    }

    public int find(int vertex) {
        metrics.findCalls++;
        int root = vertex;
        while (parent[root] != root) {
            root = parent[root];
        }
        while (parent[vertex] != vertex) {
            int next = parent[vertex];
            parent[vertex] = root;
            vertex = next;
        }
        return root;
    }

    public boolean union(int first, int second) {
        int firstRoot = find(first);
        int secondRoot = find(second);
        if (firstRoot == secondRoot) {
            return false;
        }
        if (size[firstRoot] < size[secondRoot]) {
            int temporary = firstRoot;
            firstRoot = secondRoot;
            secondRoot = temporary;
        }
        parent[secondRoot] = firstRoot;
        size[firstRoot] += size[secondRoot];
        components--;
        metrics.successfulUnions++;
        return true;
    }

    public int components() {
        return components;
    }
}
