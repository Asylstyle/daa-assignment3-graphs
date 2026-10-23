# Release verification

Local validation on 23 October 2026:

- Clean Maven build: 21 JUnit tests in 10 suites, zero failures/errors/skips.
- All 50 benchmark rows match the assignment's numeric references.
- A second full run from a clean build compares every result and counter with the recorded CSV; time is deliberately excluded from equality checks.
- Three PNGs were checked visually. Labels, density legends, log axes and ratios were checked against CSV.
- The 100000-vertex SCC path completes using an explicit stack.
- Negative DAG fixture prints path 1 -> 2 -> 3 with distance -1.
- Feature branches were fast-forward integrated; no extra merge commits.
- Exactly 35 nonempty commits; timestamps were approximately reconstructed for 13-23 October 2026, not recovered from the original history. The first commit contains the implementation plan.
- Annotated tag v1.0 points to final main.
- ZIP packaging uses only committed files and checks contents for excluded build/cache/source-PDF files.

Recheck locally:

```sh
mvn clean test
java -Xmx4g -cp target/classes benchmark.Benchmark ../benchmark-recheck.csv
python scripts/verify_release.py
python scripts/package_submission.py
```

GitHub visibility and remote state must be verified separately after publication. GitHub Actions is supplied but a local test run is not represented as a remote CI result. No Moodle submission is part of this release.

Bonus validation: Tarjan agrees with Kosaraju and all-source BFS on random graphs and handles a 100000-vertex path and cycle. Critical path agrees with exhaustive enumeration on 100 random small DAGs. Separate measured SCC CSV has 20 rows and confirms E versus 2E scans.
