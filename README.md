# DAA Assignment 3 - Graphs
Assylzhan Amangeldi, SE-2526

Java 17+ and Maven 3.9+ are required. Algorithms use only the Java standard library; JUnit 5 is a test dependency. No graph library is used.

## Build and test
```sh
mvn clean test
mvn package
java -cp target/classes graph.Main data/forest.txt
java -cp target/classes graph.Main data/cyclic.txt
java -cp target/classes graph.Main data/dag.txt
```
Input: `n m directed|undirected`, followed by exactly m triples `u v weight`. Vertices are 0..n-1. MST returns a forest for disconnected inputs; cycles are reported for topological ordering and DAG paths. Negative DAG weights and parent-path restoration are supported.

## Reproduce benchmarks and figures
```sh
java -Xmx4g -cp target/classes benchmark.Benchmark
python -m pip install -r requirements.txt
python scripts/analyze.py
python scripts/plot.py
```
The benchmark uses the exact assignment generator, five runs and median time. It checks all references, equal MST weights, topological validity, deterministic counters and exact traversal scan counts before installing the CSV. An alternate CSV output path may be supplied as the first argument. About 4 GiB maximum heap is recommended for the largest cases. Timings will vary; counters reproduce with this implementation. The report states precisely which preparation is excluded from counters. Figure 1 sums DSU calls, scans and comparisons; overlapping curves are explained in the report.

The detailed specification requires **50 rows** across ten size/density combinations and three graph kinds. Its later "four generated graphs" phrase conflicts with that detailed requirement. The report also explains why Kruskal's sorting log factor belongs to comparison counts rather than edge scans.

## Bonus algorithms
```sh
java -cp target/classes graph.BonusMain data/critical.txt
java -Xmx4g -cp target/classes benchmark.BonusBenchmark
```
Tarjan uses explicit DFS frames and an active-vertex stack, so deep graphs do not use JVM recursion. The separate bonus benchmark writes measured Kosaraju/Tarjan comparisons to `results/bonus-scc.csv`; the required 50-row CSV is unchanged.

Condensation replaces each SCC with a zero-duration block, removes internal edges and keeps every weighted crossing edge, including parallels. CriticalPath finds a maximum-weight path from any source to any sink in this DAG, keeps parents and prints the component path. Edge weights represent transition durations; vertex durations are not inferred. Negative weights are accepted mathematically, but ordinary scheduling durations should be nonnegative. Only sources start at zero, so a negative prefix cannot be discarded. Empty input gives length zero and an empty path; an isolated component is a zero-length source-to-sink path. This does not solve longest simple path inside an original cyclic SCC or make cyclic job dependencies schedulable.

For the example, vertices 3 and 4 form one SCC. The best component path follows 0 -> 1 -> {3,4} -> 5, with weight 3 + 6 + 5 = 14; internal SCC weights are omitted.

See [DEFENSE.md](DEFENSE.md) for a short Russian guide to the code, invariants and defense questions.

## Project map
- `graph`: shared adjacency list, strict input parser, sample CLI.
- `algorithms`: DSU, Kruskal, Prim, iterative Kosaraju/Tarjan, Kahn, DAG paths, condensation and critical path.
- `metrics`: independent long counters for each invocation.
- `benchmark`: seeded generators, references and median harness.
- `src/test/java`: JUnit random reference checks and edge cases.
- `data`: disconnected, cyclic and negative-weight DAG fixtures.
- `results/results.csv`, `results/plots`: real measured data and three reproducible PNGs.
- [REPORT.md](REPORT.md): complexity, proofs, ratios, slopes and algorithm choice.
- [PLAN.md](PLAN.md): 35 meaningful stages recorded before the first commit.
