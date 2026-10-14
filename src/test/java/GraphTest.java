import graph.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;
class GraphTest {
 @TempDir Path tmp;
 @Test void samplesLoad() throws Exception {
  assertEquals(4,GraphReader.read(Path.of("data/forest.txt")).m());
  assertTrue(GraphReader.read(Path.of("data/cyclic.txt")).directed());
  assertEquals(6,GraphReader.read(Path.of("data/dag.txt")).n());
 }
 @Test void loopsParallelsAndIsolates() {
  Graph g=new Graph(3,false); g.addEdge(0,0,7); g.addEdge(0,1,2); g.addEdge(0,1,3);
  assertEquals(4,g.adjacent(0).size()); assertTrue(g.adjacent(2).isEmpty());
  assertThrows(IllegalArgumentException.class,()->g.addEdge(3,0,1));
 }
 @Test void malformed() throws Exception {
  for(String text:new String[]{"2 1 directed 0","2 0 sideways","2 1 directed 0 2 1","0 0 directed extra","-1 0 directed","2 -1 directed"}) {
   Path p=tmp.resolve("bad.txt"); Files.writeString(p,text);
   assertThrows(IllegalArgumentException.class,()->GraphReader.read(p));
  }
 }
}
