import static org.junit.jupiter.api.Assertions.*;

import algorithms.Kruskal;
import algorithms.Prim;

import graph.Graph;
import graph.GraphReader;

import metrics.Metrics;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

class MstEdgeTest {
    @Test
    void forestFixture() throws Exception {
        Graph g = GraphReader.read(Path.of("data/forest.txt"));
        var a = Kruskal.run(g, new Metrics());
        assertEquals(new Kruskal.Forest(8, 3, 3), a);
        assertEquals(a, Prim.run(g, new Metrics()));
    }

    @Test
    void loopsParallelAndEmpty() {
        Graph g = new Graph(3, false);
        g.addEdge(0, 0, -99);
        g.addEdge(0, 1, 8);
        g.addEdge(0, 1, 2);
        assertEquals(new Kruskal.Forest(2, 1, 2), Kruskal.run(g, new Metrics()));
        assertEquals(Kruskal.run(g, new Metrics()), Prim.run(g, new Metrics()));
        assertEquals(new Kruskal.Forest(0, 0, 0), Prim.run(new Graph(0, false), new Metrics()));
        assertEquals(new Kruskal.Forest(0, 0, 0), Kruskal.run(new Graph(0, false), new Metrics()));
        assertThrows(
                IllegalArgumentException.class, () -> Prim.run(new Graph(1, true), new Metrics()));
    }
}
