package algorithms;
import graph.Graph;import metrics.Metrics;import java.util.*;
public final class Kahn {
 private Kahn(){}
 public static int[] run(Graph g,Metrics metrics){
  if(!g.directed())throw new IllegalArgumentException("Topological order needs directed graph");
  int[] degree=new int[g.n()],order=new int[g.n()];
  for(var e:g.edges())degree[e.v()]++;
  Deque<Integer> q=new ArrayDeque<>();
  for(int i=0;i<g.n();i++)if(degree[i]==0)q.add(i);
  int used=0;
  while(!q.isEmpty()){
   int u=q.remove();order[used++]=u;
   for(var e:g.adjacent(u)){metrics.edgeScans++;if(--degree[e.v()]==0)q.add(e.v());}
  }
  if(used!=g.n())throw new IllegalArgumentException("Directed cycle");
  return order;
 }
 public static boolean valid(Graph g,int[] order){
  if(order.length!=g.n())return false;
  int[] pos=new int[g.n()];Arrays.fill(pos,-1);
  for(int i=0;i<order.length;i++){
   int u=order[i];if(u<0||u>=g.n()||pos[u]!=-1)return false;pos[u]=i;
  }
  for(var e:g.edges())if(pos[e.u()]>=pos[e.v()])return false;
  return true;
 }
}
