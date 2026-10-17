package algorithms;
import graph.Graph;import metrics.Metrics;import java.util.*;
public final class Kosaraju {
 public record Result(int count,int[] component){}
 private Kosaraju(){}
 public static Result run(Graph g,Metrics metrics){
  if(!g.directed())throw new IllegalArgumentException("SCC needs directed graph");
  int[] order=FinishOrder.compute(g,metrics);
  Graph transpose=new Graph(g.n(),true);
  for(Graph.Edge e:g.edges())transpose.addEdge(e.v(),e.u(),e.weight());
  int[] component=new int[g.n()];Arrays.fill(component,-1);
  int count=0;Deque<Integer> stack=new ArrayDeque<>();
  for(int i=order.length-1;i>=0;i--){
   int s=order[i];if(component[s]!=-1)continue;
   component[s]=count;stack.push(s);metrics.depth(stack.size());
   while(!stack.isEmpty()){
    int u=stack.pop();
    for(Graph.Edge e:transpose.adjacent(u)){
     metrics.edgeScans++;
     if(component[e.v()]==-1){component[e.v()]=count;stack.push(e.v());metrics.depth(stack.size());}
    }
   }
   count++;
  }
  return new Result(count,component);
 }
}
