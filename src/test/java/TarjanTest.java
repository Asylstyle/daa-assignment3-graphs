import static org.junit.jupiter.api.Assertions.*;

import algorithms.Kosaraju;
import algorithms.Tarjan;

import graph.Graph;
import graph.GraphReader;

import metrics.Metrics;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Random;

class TarjanTest {
    @Test
    void randomGraphsAgreeWithBothKosarajuAndReachability() {
        Random random = new Random(164);
        for (int trial = 0; trial < 35; trial++) {
            int size = 1 + random.nextInt(200);
            Graph graph = new Graph(size, true);
            for (int edge = 0; edge < 4 * size; edge++) {
                graph.addEdge(random.nextInt(size), random.nextInt(size), random.nextInt(11) - 5);
            }
            Metrics metrics = new Metrics();
            var actual = Tarjan.run(graph, metrics);
            var expected = Kosaraju.run(graph, new Metrics());
            assertEquals(expected.count(), actual.count());
            assertEquals(graph.m(), metrics.edgeScans);

            boolean[][] reachable = new boolean[size][size];
            for (int source = 0; source < size; source++) {
                ArrayDeque<Integer> queue = new ArrayDeque<>();
                queue.add(source);
                reachable[source][source] = true;
                while (!queue.isEmpty()) {
                    for (var edge : graph.adjacent(queue.remove())) {
                        if (!reachable[source][edge.v()]) {
                            reachable[source][edge.v()] = true;
                            queue.add(edge.v());
                        }
                    }
                }
            }
            for (int first = 0; first < size; first++) {
                for (int second = 0; second < size; second++) {
                    boolean same = actual.component()[first] == actual.component()[second];
                    assertEquals(expected.component()[first] == expected.component()[second], same);
                    assertEquals(reachable[first][second] && reachable[second][first], same);
                }
            }
        }
    }

    @Test
    void handlesDeepPathAndDeepCycleWithoutRecursion() {
        Graph graph = new Graph(100000, true);
        for (int vertex = 1; vertex < graph.n(); vertex++) {
            graph.addEdge(vertex - 1, vertex, 1);
        }
        Metrics metrics = new Metrics();
        assertEquals(100000, Tarjan.run(graph, metrics).count());
        assertEquals(100000, metrics.maxDepth);
        assertEquals(99999, metrics.edgeScans);
        graph.addEdge(99999, 0, 1);
        assertEquals(1, Tarjan.run(graph, new Metrics()).count());
    }

    @Test
    void emptyLoopsParallelsAndFinishedComponents() throws Exception {
        assertEquals(0, Tarjan.run(new Graph(0, true), new Metrics()).count());
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 0, 1);
        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 1, 2);
        graph.addEdge(2, 1, 3); // edge into an SCC already removed from active stack
        assertEquals(4, Tarjan.run(graph, new Metrics()).count());
        assertEquals(
                2, Tarjan.run(GraphReader.read(Path.of("data/cyclic.txt")), new Metrics()).count());
        assertThrows(
                IllegalArgumentException.class,
                () -> Tarjan.run(new Graph(1, false), new Metrics()));
    }
}
