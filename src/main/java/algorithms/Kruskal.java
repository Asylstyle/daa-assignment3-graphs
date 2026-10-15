package algorithms;
import graph.Graph; import metrics.Metrics; import java.util.*;
public final class Kruskal {
 public record Forest(long weight,int edgeCount,int components) {}
 private Kruskal(){}
 public static Forest run(Graph g,Metrics metrics){
  if(g.directed())throw new IllegalArgumentException("MST needs undirected graph");
  List<Graph.Edge> sorted=new ArrayList<>(g.edges());
  sorted.sort((a,b)->{metrics.comparisons++;return Integer.compare(a.weight(),b.weight());});
  DisjointSet d=new DisjointSet(g.n(),metrics);long weight=0;int count=0;
  for(Graph.Edge e:sorted){
   if(count==g.n()-1)break;
   metrics.edgeScans++;
   if(d.union(e.u(),e.v())){weight+=e.weight();count++;}
  }
  return new Forest(weight,count,d.components());
 }
}
