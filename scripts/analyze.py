"""Validate measured CSV and print ratios and log-log slopes (standard library only)."""
import csv
import math
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ALGORITHMS = ("Kruskal", "Prim", "Kosaraju", "Kahn", "DagShortestPath")
SIZES = (1000, 3162, 10000, 31623, 100000)

def load():
    with (ROOT / "results/results.csv").open(newline="") as f:
        rows = list(csv.DictReader(f))
    assert len(rows) == 50
    keys = set()
    for row in rows:
        for key in ("n", "m", "dsu_ops", "edge_scans", "comparisons", "max_depth", "result"):
            row[key] = int(row[key])
        row["time_ms"] = float(row["time_ms"])
        assert row["time_ms"] >= 0 and math.isfinite(row["time_ms"])
        key = row["algorithm"], row["n"], row["m"]
        assert key not in keys
        keys.add(key)
        if row["algorithm"] in ("Kosaraju", "Kahn", "DagShortestPath"):
            assert row["edge_scans"] == row["m"] * (2 if row["algorithm"] == "Kosaraju" else 1)
    assert keys == {(a,n,d*n) for a in ALGORITHMS for n in SIZES for d in (4,20)}
    return rows

def slope(rows, field):
    xs = [math.log(r["n"]) for r in rows]
    ys = [math.log(r[field]) for r in rows]
    xm, ym = sum(xs)/len(xs), sum(ys)/len(ys)
    return sum((x-xm)*(y-ym) for x,y in zip(xs,ys))/sum((x-xm)**2 for x in xs)

def operations(row):
    return row["dsu_ops"] + row["edge_scans"] + row["comparisons"]

if __name__ == "__main__":
    rows = load()
    for algorithm in ALGORITHMS:
        for density in (4,20):
            group = [r for r in rows if r["algorithm"] == algorithm and r["m"] == density*r["n"]]
            print(algorithm, density, "scan slope", round(slope(group,"edge_scans"),4),
                  "scans/(n+m)", [round(r["edge_scans"]/(r["n"]+r["m"]),4) for r in group])
    print("Validated 50 unique measured rows.")
