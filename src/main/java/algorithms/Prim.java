package algorithms;
import graph.Graph; import metrics.Metrics; import java.util.*;
public final class Prim {
 private Prim(){}
 public static Kruskal.Forest run(Graph g,Metrics metrics){
  if(g.directed())throw new IllegalArgumentException("MST needs undirected graph");
  boolean[] seen=new boolean[g.n()];long weight=0;int count=0,components=0;
  PriorityQueue<Graph.Edge> q=new PriorityQueue<>((a,b)->{metrics.comparisons++;return Integer.compare(a.weight(),b.weight());});
  for(int start=0;start<g.n();start++){
   if(seen[start])continue;
   components++; seen[start]=true;
   add(g,start,seen,q,metrics);
   while(!q.isEmpty()){
    Graph.Edge e=q.remove();if(seen[e.v()])continue;
    seen[e.v()]=true;weight+=e.weight();count++; add(g,e.v(),seen,q,metrics);
   }
  }
  return new Kruskal.Forest(weight,count,components);
 }
 private static void add(Graph g,int u,boolean[] seen,PriorityQueue<Graph.Edge> q,Metrics m){
  for(Graph.Edge e:g.adjacent(u)){m.edgeScans++;if(!seen[e.v()])q.add(e);}
 }
}
