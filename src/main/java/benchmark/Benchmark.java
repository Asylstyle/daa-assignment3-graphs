package benchmark;
import algorithms.*;import graph.Graph;import metrics.Metrics;
import java.nio.file.*;import java.io.*;import java.util.*;
public final class Benchmark {
 private record Run(double milliseconds,Metrics metrics,long result){}
 private static Run run(String algorithm,Graph g){
  Metrics m=new Metrics();long started=System.nanoTime();
  long result=switch(algorithm){
   case "Kruskal"->Kruskal.run(g,m).weight();
   case "Prim"->Prim.run(g,m).weight();
   case "Kosaraju"->Kosaraju.run(g,m).count();
   case "Kahn"->{int[] order=Kahn.run(g,m);yield order.length;}
   case "DagShortestPath"->DagShortestPath.run(g,m).sum();
   default->throw new IllegalArgumentException(algorithm);
  };
  double elapsed=(System.nanoTime()-started)/1e6;
  return new Run(elapsed,m,result);
 }
 public static void main(String[] args)throws IOException{
  Path output=Path.of(args.length==0?"results/results.csv":args[0]);
  Files.createDirectories(output.toAbsolutePath().getParent());
  Path temporary=output.resolveSibling(output.getFileName()+".tmp");
  try(PrintWriter csv=new PrintWriter(Files.newBufferedWriter(temporary))){
   csv.println("algorithm,graph,n,m,time_ms,dsu_ops,edge_scans,comparisons,max_depth,result");
   int caseIndex=0;
   for(int n:References.SIZES)for(int density:new int[]{4,20}){
    int edgeCount=n*density;long mstWeight=Long.MIN_VALUE;
    for(String kind:new String[]{"undirected","directed","dag"}){
     Graph g=Generators.generate(n,edgeCount,kind);
     String[] algorithms=switch(kind){
      case "undirected"->new String[]{"Kruskal","Prim"};
      case "directed"->new String[]{"Kosaraju"};
      default->new String[]{"Kahn","DagShortestPath"};
     };
     if(kind.equals("dag")&&!Kahn.valid(g,Kahn.run(g,new Metrics())))throw new IllegalStateException("Invalid topological order");
     for(String algorithm:algorithms){
      Run[] runs=new Run[5];
      for(int i=0;i<5;i++){
       runs[i]=run(algorithm,g);References.check(caseIndex,algorithm,runs[i].result);
       if(i>0&&!sameCounters(runs[0].metrics,runs[i].metrics))throw new IllegalStateException("Nonreproducible counters");
      }
      Arrays.sort(runs,Comparator.comparingDouble(Run::milliseconds));Run median=runs[2];Metrics m=median.metrics;
      if(algorithm.equals("Kruskal"))mstWeight=median.result;
      if(algorithm.equals("Prim")&&mstWeight!=median.result)throw new IllegalStateException("MST disagreement");
      long expectedScans=switch(algorithm){case "Kosaraju"->2L*edgeCount;case "Kahn","DagShortestPath"->edgeCount;default->-1;};
      if(expectedScans>=0&&m.edgeScans!=expectedScans)throw new IllegalStateException("Wrong scan count");
      csv.printf(Locale.ROOT,"%s,%s,%d,%d,%.6f,%d,%d,%d,%d,%d%n",algorithm,kind,n,edgeCount,median.milliseconds,m.dsuOps(),m.edgeScans,m.comparisons,m.maxDepth,median.result);
      System.out.printf("%d/%d %s = %d%n",n,edgeCount,algorithm,median.result);
     }
    }
    caseIndex++;
   }
   if(csv.checkError())throw new IOException("CSV write failed");
  }
  Files.move(temporary,output,StandardCopyOption.REPLACE_EXISTING);
 }
 private static boolean sameCounters(Metrics a,Metrics b){
  return a.findCalls==b.findCalls&&a.successfulUnions==b.successfulUnions&&a.edgeScans==b.edgeScans&&a.comparisons==b.comparisons&&a.maxDepth==b.maxDepth;
 }
}
