"""Refresh measured sections in REPORT.md without changing hand-written explanations."""
import math
import re
from pathlib import Path
from analyze import load, slope, ALGORITHMS
root=Path(__file__).resolve().parents[1]
rows=load()
report=(root/"REPORT.md").read_text(encoding="utf-8")
measured=["Sizes in every row below: 1000, 3162, 10000, 31623, 100000.","","| Algorithm / density | Counter / formula at the five sizes |","|---|---|"]
for a in ALGORITHMS:
 for d in (4,20):
  group=[r for r in rows if r["algorithm"]==a and r["m"]==d*r["n"]]
  if a=="Kruskal":
   vals=[r["comparisons"]/(r["m"]*math.log2(r["m"])) for r in group]
   label="comparisons/(E log2 E)"
  elif a=="Prim":
   vals=[r["comparisons"]/(r["m"]*math.log2(r["m"])) for r in group]
   label="comparisons/(E log2 E)"
  else:
   vals=[r["edge_scans"]/(r["n"]+r["m"]) for r in group]
   label="scans/(V+E)"
  measured.append(f"| {a}, {d}V: {label} | "+", ".join(f"{v:.3f}" for v in vals)+" |")
measured.append("")
measured.append("Traversal ratios are exactly constant. Sorting/heap ratios stay bounded in a narrow band, consistent with E log E work; finite experiments support rather than prove an asymptotic bound. Kruskal DSU work depends on the stopping point, so dsu_ops/E need not be constant.")
slopes=["| Algorithm | Scan slope, E=4V | Scan slope, E=20V |","|---|---:|---:|"]
for a in ALGORITHMS:
 vals=[]
 for d in (4,20):
  vals.append(slope([r for r in rows if r["algorithm"]==a and r["m"]==d*r["n"]],"edge_scans"))
 slopes.append(f"| {a} | {vals[0]:.4f} | {vals[1]:.4f} |")
kr=[]
for d in (4,20):
 kr.append(slope([r for r in rows if r["algorithm"]=="Kruskal" and r["m"]==d*r["n"]],"comparisons"))
slopes.append("")
slopes.append(f"OLS fits use natural logs of the five measured points. The PDF incorrectly attributes the sorting log factor to Kruskal edgeScans: sorting comparisons carry that factor, not scans. Kruskal comparison slopes are {kr[0]:.4f} (4V) and {kr[1]:.4f} (20V). Its scan slopes reflect the changing fraction before early termination and are not evidence of an E log E scan bound.")
times=[r["time_ms"] for r in rows]
timing=f"Recorded median times range from {min(times):.3f} to {max(times):.3f} ms on Windows with OpenJDK 25, one JVM and a 4 GiB maximum heap. Timing includes each algorithm's preparation (including DAG topological sorting), while scan counters use the stated narrower boundaries. The two smallest cases are strongly affected by JIT warmup, allocation and background activity; no dedicated warmup was performed. Hardware, JVM and load change timings, whereas operation counts remain reproducible. Therefore conclusions use counters, not a universal speed ranking."
def section(text, name, value):
    block = f"<!-- {name}_START -->\n{value}\n<!-- {name}_END -->"
    pattern = rf"<!-- {name}_START -->.*?<!-- {name}_END -->"
    return re.sub(pattern, lambda match: block, text, flags=re.S)
report=section(report,"MEASUREMENTS","\n".join(measured))
report=section(report,"SLOPES","\n".join(slopes))
report=section(report,"TIMING",timing)
(root/"REPORT.md").write_text(report,encoding="utf-8")
