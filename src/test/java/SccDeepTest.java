import graph.*;import algorithms.*;import metrics.*;
import org.junit.jupiter.api.Test;import static org.junit.jupiter.api.Assertions.*;
class SccDeepTest {
 @Test void hundredThousandPath(){
  Graph g=new Graph(100000,true);for(int i=1;i<g.n();i++)g.addEdge(i-1,i,1);
  Metrics m=new Metrics();assertEquals(100000,Kosaraju.run(g,m).count());
  assertEquals(100000,m.maxDepth);assertEquals(199998,m.edgeScans);
 }
}
