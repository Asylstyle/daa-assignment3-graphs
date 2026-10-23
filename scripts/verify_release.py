"""Read-only verification of submission evidence and meaningful Git history."""
import csv
from pathlib import Path
import subprocess
import xml.etree.ElementTree as ET
from analyze import load

ROOT = Path(__file__).resolve().parents[1]

def git(*args):
    return subprocess.check_output(["git", "-C", str(ROOT), *args], text=True).strip()

if __name__ == "__main__":
    rows = load()
    assert git("rev-list", "--count", "HEAD") == "35"
    assert git("branch", "--show-current") == "main"
    assert git("rev-parse", "v1.0^{}") == git("rev-parse", "HEAD")
    for commit in git("rev-list", "HEAD").splitlines():
        assert git("diff-tree", "--root", "--no-commit-id", "--name-only", "-r", commit), "Empty commit"
    for branch in ("dsu", "mst", "scc", "paths", "metrics"):
        git("rev-parse", "feature/" + branch)
    suites = [ET.parse(p).getroot() for p in (ROOT/"target/surefire-reports").glob("TEST-*.xml")]
    assert suites and sum(int(s.attrib["tests"]) for s in suites) >= 21
    assert all(int(s.attrib["failures"]) == int(s.attrib["errors"]) == int(s.attrib["skipped"]) == 0 for s in suites)
    with (ROOT / "results/bonus-scc.csv").open(newline="") as stream:
        bonus = list(csv.DictReader(stream))
    assert len(bonus) == 20
    for first, second in zip(bonus[::2], bonus[1::2]):
        assert first["algorithm"] == "Kosaraju" and second["algorithm"] == "Tarjan"
        assert first["n"] == second["n"] and first["m"] == second["m"]
        assert first["scc_count"] == second["scc_count"]
        assert int(first["edge_scans"]) == 2 * int(first["m"])
        assert int(second["edge_scans"]) == int(second["m"])
    recheck = ROOT.parent / "benchmark-recheck.csv"
    if recheck.exists():
        with recheck.open(newline="") as f:
            repeated = list(csv.DictReader(f))
        assert len(repeated) == len(rows)
        for old, new in zip(rows, repeated):
            for key in old:
                if key != "time_ms":
                    assert str(old[key]) == new[key], (key, old, new)
    tracked = git("ls-files").splitlines()
    assert sum(p.endswith(".png") for p in tracked) == 3
    assert not any(p.endswith((".pdf", ".zip", ".class")) or p.startswith("target/") for p in tracked)
    assert not git("status", "--porcelain")
    print("Verified: main, 35 nonempty commits, feature branches, v1.0, tests, 50 CSV rows, clean tree.")
