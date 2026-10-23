import static org.junit.jupiter.api.Assertions.*;

import algorithms.DisjointSet;

import metrics.Metrics;

import org.junit.jupiter.api.Test;

import java.util.Random;

class DisjointSetTest {
    @Test
    void randomReference() {
        Random r = new Random(17);
        Metrics m = new Metrics();
        DisjointSet d = DisjointSet.makeSet(80, m);
        int[] labels = new int[80];
        for (int i = 0; i < 80; i++) {
            labels[i] = i;
        }
        int components = 80, unions = 0;
        for (int k = 0; k < 2000; k++) {
            int a = r.nextInt(80), b = r.nextInt(80), old = labels[b], to = labels[a];
            boolean expected = old != to;
            assertEquals(expected, d.union(a, b));
            if (expected) {
                for (int i = 0; i < 80; i++)
                    if (labels[i] == old) {
                        labels[i] = to;
                    }
                components--;
                unions++;
            }
            assertEquals(components, d.components());
            for (int i = 0; i < 80; i++)
                assertEquals(labels[a] == labels[i], d.find(a) == d.find(i));
        }
        assertEquals(unions, m.successfulUnions);
        assertTrue(m.findCalls > 0);
        assertEquals(0, new Metrics().dsuOps());
    }
}
