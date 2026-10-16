import graph.*;import algorithms.*;import metrics.*;import java.util.*;
import org.junit.jupiter.api.Test;import static org.junit.jupiter.api.Assertions.*;
class MstRandomTest {
 @Test void hundredConnectedGraphs(){
  Random r=new Random(27);
  for(int t=0;t<100;t++){
   int n=2+r.nextInt(80);Graph g=new Graph(n,false);
   for(int i=1;i<n;i++)g.addEdge(i,r.nextInt(i),r.nextInt(101)-50);
   for(int i=0;i<5*n;i++)g.addEdge(r.nextInt(n),r.nextInt(n),r.nextInt(101)-50);
   var a=Kruskal.run(g,new Metrics());var b=Prim.run(g,new Metrics());
   assertEquals(a,b);assertEquals(n-1,a.edgeCount());assertEquals(1,a.components());
  }
 }
}
