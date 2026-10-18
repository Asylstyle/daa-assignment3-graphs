package algorithms;
import graph.Graph;import metrics.Metrics;import java.util.*;
public final class DagShortestPath {
 public static final long INF=Long.MAX_VALUE/4;
 public record Result(long[] distance,int[] parent){
  public long sum(){long sum=0;for(long d:distance)if(d!=INF)sum+=d;return sum;}
 }
 private DagShortestPath(){}
 public static Result run(Graph g,Metrics metrics){
  int[] order=Kahn.run(g,new Metrics()); // preprocessing excluded from relaxation counters
  int[] indegree=new int[g.n()],parent=new int[g.n()];Arrays.fill(parent,-1);
  long[] distance=new long[g.n()];Arrays.fill(distance,INF);
  for(var e:g.edges())indegree[e.v()]++;
  for(int u=0;u<g.n();u++)if(indegree[u]==0)distance[u]=0;
  for(int u:order)for(var e:g.adjacent(u)){
   metrics.edgeScans++;metrics.comparisons++;
   if(distance[u]!=INF && distance[u]+e.weight()<distance[e.v()]){
    distance[e.v()]=distance[u]+e.weight();parent[e.v()]=u;
   }
  }
  return new Result(distance,parent);
 }
}
