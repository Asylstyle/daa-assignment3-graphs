package graph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Scanner;

public final class GraphReader {
    private GraphReader() {}

    public static Graph read(Path path) throws IOException {
        try (Scanner s = new Scanner(Files.newBufferedReader(path))) {
            int n = s.nextInt();
            int m = s.nextInt();
            String kind = s.next();
            if (m < 0 || !(kind.equals("directed") || kind.equals("undirected")))
                throw new IllegalArgumentException("Expected n m directed|undirected");
            Graph g = new Graph(n, kind.equals("directed"));
            for (int i = 0; i < m; i++) {
                g.addEdge(s.nextInt(), s.nextInt(), s.nextInt());
            }
            if (s.hasNext()) {
                throw new IllegalArgumentException("Trailing input");
            }
            return g;
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Incomplete or non-integer graph input", e);
        }
    }
}
