package algorithms;
import java.util.*;
public final class Paths {
 private Paths(){}
 public static List<Integer> restore(DagShortestPath.Result result,int target){
  if(result.distance()[target]==DagShortestPath.INF)return List.of();
  List<Integer> path=new ArrayList<>();
  for(int u=target;u!=-1;u=result.parent()[u]){
   if(path.size()>=result.parent().length)throw new IllegalArgumentException("Cyclic parent array");
   path.add(u);
  }
  Collections.reverse(path);return List.copyOf(path);
 }
 public static String print(DagShortestPath.Result result,int target){
  return String.join(" -> ",restore(result,target).stream().map(Object::toString).toList());
 }
}
