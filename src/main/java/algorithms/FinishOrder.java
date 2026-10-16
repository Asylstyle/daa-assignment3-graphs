package algorithms;
import graph.Graph;import metrics.Metrics;import java.util.*;
/** Explicit DFS frames retain the next adjacency iterator, avoiding recursion. */
final class FinishOrder {
 private record Frame(int vertex,Iterator<Graph.Edge> edges){}
 static int[] compute(Graph g,Metrics metrics){
  boolean[] seen=new boolean[g.n()];int[] order=new int[g.n()];int used=0;
  Deque<Frame> stack=new ArrayDeque<>();
  for(int s=0;s<g.n();s++){
   if(seen[s])continue; seen[s]=true;
   stack.push(new Frame(s,g.adjacent(s).iterator()));metrics.depth(stack.size());
   while(!stack.isEmpty()){
    Frame f=stack.peek();
    if(!f.edges.hasNext()){order[used++]=f.vertex;stack.pop();continue;}
    Graph.Edge e=f.edges.next();metrics.edgeScans++;
    if(!seen[e.v()]){seen[e.v()]=true;stack.push(new Frame(e.v(),g.adjacent(e.v()).iterator()));metrics.depth(stack.size());}
   }
  }
  return order;
 }
}
