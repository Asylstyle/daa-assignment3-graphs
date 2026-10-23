"""Package committed release files only; never include local build/cache artifacts."""
from pathlib import Path
import subprocess
import zipfile

ROOT = Path(__file__).resolve().parents[1]
OUTPUT = ROOT.parent / "DAA_Assignment3_Assylzhan_Amangeldi_SE-2526.zip"

def git(*args):
    return subprocess.check_output(["git", "-C", str(ROOT), *args], text=True).strip()

if __name__ == "__main__":
    assert git("branch", "--show-current") == "main", "Use main"
    assert not git("status", "--porcelain"), "Commit changes first"
    assert git("rev-list", "--count", "HEAD") == "35", "Expected exactly 35 commits"
    assert git("rev-parse", "v1.0^{}") == git("rev-parse", "HEAD"), "Tag v1.0 must name HEAD"
    subprocess.run(["git", "-C", str(ROOT), "archive", "--format=zip",
                    "--output=" + str(OUTPUT), "v1.0"], check=True)
    with zipfile.ZipFile(OUTPUT) as z:
        names = z.namelist()
        assert z.testzip() is None
        assert {"pom.xml", "README.md", "REPORT.md", "results/results.csv"} <= set(names)
        assert len([n for n in names if n.endswith(".png")]) == 3
        assert not any(any(p in {"target", "__pycache__", ".git", ".library-transfer"} for p in Path(n).parts) for n in names)
        assert not any(n.lower().endswith((".pdf", ".zip", ".class")) for n in names)
        assert all(not Path(n).is_absolute() and ".." not in Path(n).parts for n in names)
    print(OUTPUT)
    print(f"Verified {len(names)} archive entries")
