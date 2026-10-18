import graph.*;import algorithms.*;import metrics.*;import java.nio.file.*;
import org.junit.jupiter.api.Test;import static org.junit.jupiter.api.Assertions.*;
class TopoTest {
 @Test void fixture()throws Exception{
  Graph g=GraphReader.read(Path.of("data/dag.txt"));Metrics m=new Metrics();
  assertTrue(Kahn.valid(g,Kahn.run(g,m)));assertEquals(g.m(),m.edgeScans);
  assertFalse(Kahn.valid(g,new int[]{0,0,1,2,3,4}));
  assertFalse(Kahn.valid(g,new int[]{5,4,3,2,1,0}));
  Graph cycle=GraphReader.read(Path.of("data/cyclic.txt"));
  assertThrows(IllegalArgumentException.class,()->Kahn.run(cycle,new Metrics()));
  Graph loop=new Graph(1,true);loop.addEdge(0,0,1);
  assertThrows(IllegalArgumentException.class,()->Kahn.run(loop,new Metrics()));
  assertTrue(Kahn.valid(new Graph(0,true),new int[0]));
 }
}
