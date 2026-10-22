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
The benchmark uses the exact assignment generator, five runs and median time. It checks all references, equal MST weights, topological validity, deterministic counters and exact traversal scan counts before installing the CSV. An alternate CSV output path may be supplied as the first argument. About 4 GiB maximum heap is recommended for the largest cases. Timings will vary; counters reproduce with this implementation. The report states precisely which preparation is excluded from counters. Figure 1 sums DSU calls, scans and comparisons; overlapping curves are explained in the report. All three PNGs were inspected as pixels and checked against the CSV.

The detailed specification requires **50 rows** across ten size/density combinations and three graph kinds. Its later "four generated graphs" phrase conflicts with that detailed requirement. The report also explains why Kruskal's sorting log factor belongs to comparison counts rather than edge scans.

## Project map
- `graph`: shared adjacency list, strict input parser, sample CLI.
- `algorithms`: DSU, Kruskal, Prim, iterative Kosaraju, Kahn and DAG paths.
- `metrics`: independent long counters for each invocation.
- `benchmark`: seeded generators, references and median harness.
- `src/test/java`: JUnit random reference checks and edge cases.
- `data`: disconnected, cyclic and negative-weight DAG fixtures.
- `results/results.csv`, `results/plots`: real measured data and three reproducible PNGs.
- [REPORT.md](REPORT.md): complexity, proofs, ratios, slopes and algorithm choice.
- [PLAN.md](PLAN.md): 35 meaningful stages recorded before the first commit.

## Submission archive
```sh
python scripts/package_submission.py
```
Run from a clean main checkout after release tagging. The script archives committed files only, validates the ZIP paths, and excludes build output, caches, secrets, Git internals and the source instructor PDF. The ZIP is written beside the repository as `DAA_Assignment3_Assylzhan_Amangeldi_SE-2526.zip`. Do not submit to Moodle without reviewing the deliverables.

## AI use
OpenAI Codex assisted with implementation, tests, benchmark tooling, plots and documentation. An independent read-only agent reviewed the algorithms, RNG sequence, CSV and PNGs. All benchmark values are actual Java execution results checked against the assignment references; no results or commit dates were fabricated. The student should run the commands and understand the code before defending it.
