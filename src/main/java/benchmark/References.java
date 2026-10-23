package benchmark;

public final class References {
    public static final int[] SIZES = {1000, 3162, 10000, 31623, 100000};
    private static final long[][] VALUES = {
        {147970, 27, 396274}, {30363, 1, 252411},
        {468911, 137, 1271911}, {97589, 1, 723157},
        {1486139, 373, 3941828}, {309550, 1, 2381342},
        {4715515, 1188, 12551891}, {968517, 1, 7376612},
        {15012094, 3923, 40096539}, {3056681, 1, 23512087}
    };

    private References() {}

    public static void check(int caseIndex, String algorithm, long result) {
        int col =
                switch (algorithm) {
                    case "Kruskal", "Prim" -> 0;
                    case "Kosaraju" -> 1;
                    case "DagShortestPath" -> 2;
                    default -> -1;
                };
        long expected = col < 0 ? SIZES[caseIndex / 2] : VALUES[caseIndex][col];
        if (result != expected)
            throw new IllegalStateException(
                    algorithm + " reference mismatch: " + result + " != " + expected);
    }
}
