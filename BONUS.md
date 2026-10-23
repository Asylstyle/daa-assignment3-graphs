# Bonus: SCC and critical path

## Tarjan compared with Kosaraju

Both algorithms return the same SCC partition (component numbers may differ). Tarjan uses index[], lowlink[], an active-vertex stack and explicit DFS frames. A vertex is removed from the active stack exactly when its SCC is completed. Edges to vertices already removed do not affect lowlink. Tarjan has O(V+E) time and O(V) additional space, whereas this Kosaraju implementation also stores the transpose and uses O(V+E) additional space.

The separate CSV results/bonus-scc.csv contains actual Java runs: median of five runs per algorithm and case, fresh counters for every run, exact assignment generators and reference SCC counts. Every partition is checked against Kosaraju in O(V). Tarjan max_depth measures DFS-frame depth; its separate active-vertex stack can also hold O(V) vertices. The required 50-row results/results.csv is unchanged.

| V | E | SCCs | Kosaraju scans | Tarjan scans |
|---:|---:|---:|---:|---:|
| 1000 | 4000 | 27 | 8000 | 4000 |
| 1000 | 20000 | 1 | 40000 | 20000 |
| 3162 | 12648 | 137 | 25296 | 12648 |
| 3162 | 63240 | 1 | 126480 | 63240 |
| 10000 | 40000 | 373 | 80000 | 40000 |
| 10000 | 200000 | 1 | 400000 | 200000 |
| 31623 | 126492 | 1188 | 252984 | 126492 |
| 31623 | 632460 | 1 | 1264920 | 632460 |
| 100000 | 400000 | 3923 | 800000 | 400000 |
| 100000 | 2000000 | 1 | 4000000 | 2000000 |

Tarjan examines each directed edge once. Kosaraju examines it once in the original DFS and once in the transpose DFS, giving 2E scans under the stated boundary that excludes constructing the transpose. The scan saving does not imply that every machine or input gives a twofold runtime improvement. Both are linear; allocation, lowlink bookkeeping and JVM behavior affect elapsed time.

## Condensation and path semantics

Condensation.build computes SCCs using Tarjan and maps each original vertex to its component. Each SCC becomes one zero-duration vertex. Internal edges are removed. Every crossing edge retains its original integer weight, including parallel edges; no deduplication or weight aggregation is needed. A cycle between different SCCs would make them mutually reachable, so the condensed graph is acyclic. Construction takes O(V+E) time and O(V+E) extra space including the output graph.

CriticalPath.run accepts a DAG and rejects cyclic or undirected input through Kahn. All indegree-zero sources start at zero; other vertices start at UNREACHABLE. Edges are relaxed in topological order with maximum instead of minimum, which is equivalent to negating the weights and computing shortest paths. Direct long addition avoids overflowing int when negating Integer.MIN_VALUE. The maximum is selected among sinks only. Parent pointers restore a source-to-sink vertex sequence; on a condensed graph these vertices are component IDs. Ties keep the first improvement in traversal order, so different equally good paths are acceptable. Empty input returns length zero and an empty path; an isolated vertex is a zero-edge path of length zero.

Negative edges are mathematically valid in a DAG, but ordinary task durations should be nonnegative. A source-to-sink chain with weights -5 and +2 has length -3; the algorithm does not silently restart at the middle vertex. Disconnected components compete for the global maximum sink distance. Distances and sums use long. Path relaxation takes O(V+E) time and O(V) extra space, including order, queue, distances, indegrees and parents.

For scheduling, weights are durations of transitions between blocks, not durations inferred from vertex labels. With nonnegative durations and unlimited resources, the critical path bounds the earliest completion of the dependent chain. Vertex-duration tasks can be modeled by splitting each vertex into an entry and exit connected by its duration; that transformation is not implicit here. Cyclic dependencies remain a scheduling error. Collapsing a cyclic SCC is useful for analysis, but does not produce a schedule inside that SCC.

## Demonstration and limits

Run java -cp target/classes graph.BonusMain data/critical.txt. Vertices 3 and 4 share an SCC; the component path corresponding to 0 -> 1 -> {3,4} -> 5 has length 3 + 6 + 5 = 14. Internal edges of weights 2 and 1 are omitted. Output includes component IDs and the original-vertex mapping, allowing the path to be read without relying on a particular SCC numbering.

This is a longest path in the condensation, as requested by the bonus, not a longest simple path in the original cyclic graph. Longest simple path is hard on general graphs because cycles prevent the topological induction used here. In a DAG, every predecessor is processed before a vertex, so its maximum distance is final when its outgoing edges are relaxed.

Tests compare Tarjan with Kosaraju and brute-force reachability, including a path and a cycle of 100000 vertices. Critical path tests compare small random DAGs with exhaustive source-to-sink path enumeration and cover signed weights, multiple sources and sinks, isolates, parallel edges, large totals, empty graphs, cycles and path restoration.
