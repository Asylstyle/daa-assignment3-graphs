package metrics;
/** One new instance per invocation. Counters are never shared between runs. */
public final class Metrics {
 public long findCalls, successfulUnions, edgeScans, comparisons, maxDepth;
 public long dsuOps() { return findCalls+successfulUnions; }
 public void depth(long depth) { maxDepth=Math.max(maxDepth,depth); }
}
