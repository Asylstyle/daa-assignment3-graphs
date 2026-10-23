import static org.junit.jupiter.api.Assertions.*;

import algorithms.DagShortestPath;
import algorithms.Paths;

import graph.Graph;
import graph.GraphReader;

import metrics.Metrics;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

class DagPathTest {
    @Test
    void againstBellmanFord() {
        Random r = new Random(71);
        for (int t = 0; t < 100; t++) {
            int n = 2 + r.nextInt(60);
            Graph g = new Graph(n, true);
            int[] indegree = new int[n];
            for (int k = 0; k < 5 * n; k++) {
                int a = r.nextInt(n), b = r.nextInt(n);
                if (a == b) {
                    continue;
                }
                if (a > b) {
                    int z = a;
                    a = b;
                    b = z;
                }
                g.addEdge(a, b, r.nextInt(101) - 50);
                indegree[b]++;
            }
            long[] expected = new long[n];
            Arrays.fill(expected, DagShortestPath.INF);
            for (int i = 0; i < n; i++)
                if (indegree[i] == 0) {
                    expected[i] = 0;
                }
            for (int k = 1; k < n; k++)
                for (var e : g.edges())
                    if (expected[e.u()] != DagShortestPath.INF)
                        expected[e.v()] = Math.min(expected[e.v()], expected[e.u()] + e.weight());
            Metrics m = new Metrics();
            var actual = DagShortestPath.run(g, m);
            assertArrayEquals(expected, actual.distance());
            assertEquals(g.m(), m.edgeScans);
        }
    }

    @Test
    void restoresNegativeFixture() throws Exception {
        var result = DagShortestPath.run(GraphReader.read(Path.of("data/dag.txt")), new Metrics());
        assertEquals(-1, result.distance()[3]);
        assertEquals(List.of(1, 2, 3), Paths.restore(result, 3));
        assertEquals("1 -> 2 -> 3", Paths.print(result, 3));
        assertEquals(List.of(5), Paths.restore(result, 5));
        assertEquals(0, DagShortestPath.run(new Graph(0, true), new Metrics()).sum());
    }
}
