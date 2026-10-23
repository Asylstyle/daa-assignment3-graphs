import static org.junit.jupiter.api.Assertions.*;

import algorithms.Condensation;
import algorithms.CriticalPath;
import algorithms.Kahn;

import graph.Graph;
import graph.GraphReader;

import metrics.Metrics;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Random;

class CriticalPathTest {
    @Test
    void condensationKeepsCrossingWeightsAndRestoresComponentPath() throws Exception {
        Graph graph = GraphReader.read(Path.of("data/critical.txt"));
        var condensed = Condensation.build(graph);
        assertEquals(5, condensed.dag().n());
        assertEquals(5, condensed.dag().m());
        assertEquals(condensed.component()[3], condensed.component()[4]);
        assertTrue(Kahn.valid(condensed.dag(), Kahn.run(condensed.dag(), new Metrics())));
        Metrics metrics = new Metrics();
        var path = CriticalPath.run(condensed.dag(), metrics);
        assertEquals(14, path.length());
        assertEquals(
                List.of(
                        condensed.component()[0],
                        condensed.component()[1],
                        condensed.component()[3],
                        condensed.component()[5]),
                path.path());
        assertEquals(condensed.dag().m(), metrics.edgeScans);
    }

    @Test
    void negativeEdgesAreNotResetAtIntermediateVertices() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, -5);
        graph.addEdge(1, 2, 2);
        var path = CriticalPath.run(graph, new Metrics());
        assertEquals(-3, path.length());
        assertEquals(List.of(0, 1, 2), path.path());
        assertEquals(-5, path.distance()[1]);
    }

    @Test
    void multipleSourcesSinksIsolatesParallelsAndLongTotals() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 2, -8);
        graph.addEdge(1, 2, -3);
        graph.addEdge(1, 2, -2);
        var path = CriticalPath.run(graph, new Metrics());
        assertEquals(0, path.length()); // isolated source/sink 3 is a valid zero-edge path
        assertEquals(List.of(3), path.path());
        assertEquals(-2, path.distance()[2]);
        Graph large = new Graph(3, true);
        large.addEdge(0, 1, Integer.MAX_VALUE);
        large.addEdge(1, 2, Integer.MAX_VALUE);
        assertEquals(2L * Integer.MAX_VALUE, CriticalPath.run(large, new Metrics()).length());
        Graph minimum = new Graph(2, true);
        minimum.addEdge(0, 1, Integer.MIN_VALUE);
        assertEquals((long) Integer.MIN_VALUE, CriticalPath.run(minimum, new Metrics()).length());
    }

    @Test
    void emptySingletonCycleAndUndirectedInputs() {
        var empty = CriticalPath.run(new Graph(0, true), new Metrics());
        assertEquals(0, empty.length());
        assertTrue(empty.path().isEmpty());
        assertEquals(List.of(0), CriticalPath.run(new Graph(1, true), new Metrics()).path());
        Graph cycle = new Graph(2, true);
        cycle.addEdge(0, 1, 1);
        cycle.addEdge(1, 0, 1);
        assertThrows(IllegalArgumentException.class, () -> CriticalPath.run(cycle, new Metrics()));
        assertEquals(0, CriticalPath.run(Condensation.build(cycle).dag(), new Metrics()).length());
        assertThrows(
                IllegalArgumentException.class,
                () -> CriticalPath.run(new Graph(1, false), new Metrics()));
    }

    @Test
    void randomDagAgreesWithEnumeratedSourceToSinkPaths() {
        Random random = new Random(77);
        for (int trial = 0; trial < 100; trial++) {
            int size = 1 + random.nextInt(9);
            Graph graph = new Graph(size, true);
            int[] indegree = new int[size];
            for (int from = 0; from < size; from++) {
                for (int to = from + 1; to < size; to++) {
                    if (random.nextBoolean()) {
                        graph.addEdge(from, to, random.nextInt(21) - 10);
                        indegree[to]++;
                    }
                }
            }
            long expected = Long.MIN_VALUE;
            for (int source = 0; source < size; source++) {
                if (indegree[source] == 0) {
                    expected = Math.max(expected, enumerate(graph, source));
                }
            }
            assertEquals(expected, CriticalPath.run(graph, new Metrics()).length());
        }
    }

    // Brute force is intentionally recursive only on these tiny test DAGs.
    private long enumerate(Graph graph, int vertex) {
        if (graph.adjacent(vertex).isEmpty()) {
            return 0;
        }
        long best = Long.MIN_VALUE;
        for (var edge : graph.adjacent(vertex)) {
            best = Math.max(best, edge.weight() + enumerate(graph, edge.v()));
        }
        return best;
    }
}
