# Implementation plan (written before the first commit)

Exactly 35 substantive commits; feature branches are integrated by fast-forward so no extra merge commits arise. Real current timestamps only.

1. Maven/JUnit project and this plan
2. Graph edge model and adjacency representation
3. Strict graph text reader
4. Three sample graph inputs
5. Graph reader and representation tests
6. Operation metrics
7. DSU compression and union by size
8. Randomized DSU reference tests
9. Kruskal minimum spanning forest
10. Prim minimum spanning forest
11. Randomized MST cross checks
12. MST edge case tests
13. Iterative Kosaraju first pass
14. Kosaraju transpose traversal
15. SCC brute reachability tests
16. SCC deep path regression
17. Kahn topological sorting
18. Topological order validation tests
19. Multisource DAG shortest paths
20. Path restoration
21. Bellman Ford randomized path tests
22. Command line sample runner
23. Exact seeded benchmark graph generators
24. Benchmark reference checks
25. Five-run median benchmark harness
26. CSV validation and ratio analysis
27. Real benchmark results
28. Reproducible plotting script
29. Three measured PNG charts
30. Complexity and correctness report
31. Measured ratios and fitted slopes report
32. Usage and AI disclosure README
33. CI build and tests
34. Reproducible submission ZIP script
35. Final verification documentation

Branches: feature/dsu (6–8), feature/mst (9–12), feature/scc (13–16), feature/paths (17–22), feature/metrics (23–31). Final main, annotated v1.0 tag.

The instructor PDF is excluded. No invented benchmark outcomes. Detailed benchmark specification requires 50 rows (10 combinations × 5 algorithms), despite the contradictory brief deliverables wording.
