import graph.*;import algorithms.*;import metrics.*;import java.util.*;import java.nio.file.*;
import org.junit.jupiter.api.Test;import static org.junit.jupiter.api.Assertions.*;
class SccTest {
 @Test void referenceReachability(){
  Random r=new Random(44);
  for(int t=0;t<25;t++){
   int n=1+r.nextInt(200);Graph g=new Graph(n,true);
   for(int i=0;i<4*n;i++)g.addEdge(r.nextInt(n),r.nextInt(n),1);
   boolean[][] reach=new boolean[n][n];
   for(int s=0;s<n;s++){
    Deque<Integer> q=new ArrayDeque<>();q.add(s);reach[s][s]=true;
    while(!q.isEmpty())for(var e:g.adjacent(q.remove()))
     if(!reach[s][e.v()]){reach[s][e.v()]=true;q.add(e.v());}
   }
   Metrics m=new Metrics();var result=Kosaraju.run(g,m);
   for(int a=0;a<n;a++)for(int b=0;b<n;b++)
    assertEquals(reach[a][b]&&reach[b][a],result.component()[a]==result.component()[b]);
   assertEquals(2L*g.m(),m.edgeScans);
  }
 }
 @Test void cyclicFixture()throws Exception{
  assertEquals(2,Kosaraju.run(GraphReader.read(Path.of("data/cyclic.txt")),new Metrics()).count());
  assertEquals(0,Kosaraju.run(new Graph(0,true),new Metrics()).count());
 }
}
