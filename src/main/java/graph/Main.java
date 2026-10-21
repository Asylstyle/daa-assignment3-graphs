package graph;
import algorithms.*;import metrics.Metrics;import java.nio.file.Path;
public final class Main {
 public static void main(String[] args)throws Exception{
  if(args.length!=1)throw new IllegalArgumentException("Usage: java -cp target/classes graph.Main data/dag.txt");
  Graph g=GraphReader.read(Path.of(args[0]));
  if(!g.directed()){
   System.out.println("Kruskal: "+Kruskal.run(g,new Metrics()));
   System.out.println("Prim: "+Prim.run(g,new Metrics()));
  }else{
   System.out.println("SCC count: "+Kosaraju.run(g,new Metrics()).count());
   try{
    int[] order=Kahn.run(g,new Metrics());System.out.println("Topological order: "+java.util.Arrays.toString(order));
    var paths=DagShortestPath.run(g,new Metrics());
    for(int i=0;i<g.n();i++)System.out.println(i+": distance="+paths.distance()[i]+" path="+Paths.print(paths,i));
   }catch(IllegalArgumentException cycle){System.out.println(cycle.getMessage());}
  }
 }
}

