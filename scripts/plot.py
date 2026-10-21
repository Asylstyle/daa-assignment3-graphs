"""Regenerate all three PNGs from the validated CSV. No synthetic data."""
from pathlib import Path
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
from analyze import load, operations, ALGORITHMS

ROOT = Path(__file__).resolve().parents[1]
DEST = ROOT / "results/plots"
COLORS = dict(zip(ALGORITHMS, ("#1765ad", "#d56b16", "#2c8750", "#8b4cad", "#bd3d67")))

def make(mode):
    rows = load()
    fig, ax = plt.subplots(figsize=(11, 6.4), layout="constrained")
    algorithms = ("Kruskal",) if mode == "kruskal_share" else ALGORITHMS
    for algorithm in algorithms:
        for density in (4, 20):
            group = sorted((r for r in rows if r["algorithm"] == algorithm and r["m"] == density*r["n"]), key=lambda r:r["n"])
            y = [operations(r) if mode == "operations" else r["edge_scans"]/r["m"] for r in group]
            ax.plot([r["n"] for r in group], y, color=COLORS[algorithm],
                    linestyle="-" if density == 4 else "--",
                    marker="o" if density == 4 else "s",
                    markersize=5, label=f"{algorithm}, m={density}n")
    ax.set_xscale("log")
    ax.set_xlabel("Vertices n (log scale)")
    if mode == "operations":
        ax.set_yscale("log")
        ax.set_ylabel("Counted operations (log scale)")
        ax.set_title("Measured operations: DSU calls + edge scans + weight comparisons")
    else:
        ax.set_ylabel("Edge scans / input edges m")
        ax.set_title("Fraction of edges examined by Kruskal" if mode == "kruskal_share" else "Measured edge scans per input edge")
        ax.set_ylim(0, 1.08 if mode == "kruskal_share" else 2.16)
    ax.grid(True, which="both", alpha=.2)
    # Identical traversal counts overlap exactly; the legend retains every series.
    ax.legend(loc="upper left", bbox_to_anchor=(1.01, 1), fontsize=9, frameon=False)
    fig.text(.01, .005, "Source: results/results.csv | Random(42) | 5 runs per case | solid: m=4n; dashed: m=20n", fontsize=8)
    DEST.mkdir(parents=True, exist_ok=True)
    fig.savefig(DEST / f"{mode}.png", dpi=170)
    plt.close(fig)

if __name__ == "__main__":
    for mode in ("operations", "scans_per_edge", "kruskal_share"):
        make(mode)
