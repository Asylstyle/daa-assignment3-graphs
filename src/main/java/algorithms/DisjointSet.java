package algorithms;
import metrics.Metrics;
public final class DisjointSet {
 private final int[] parent,size;
 private int components;
 private final Metrics metrics;
 public DisjointSet(int n, Metrics metrics) {
  if(n<0) throw new IllegalArgumentException("Negative size");
  this.metrics=metrics; parent=new int[n]; size=new int[n]; components=n;
  for(int i=0;i<n;i++){parent[i]=i;size[i]=1;}
 }
 public static DisjointSet makeSet(int n,Metrics metrics){return new DisjointSet(n,metrics);}
 public int find(int x) {
  metrics.findCalls++;
  int root=x;
  while(parent[root]!=root) root=parent[root];
  while(parent[x]!=x){int next=parent[x];parent[x]=root;x=next;}
  return root;
 }
 public boolean union(int a,int b) {
  int ra=find(a),rb=find(b); if(ra==rb)return false;
  if(size[ra]<size[rb]){int t=ra;ra=rb;rb=t;}
  parent[rb]=ra;size[ra]+=size[rb];components--;metrics.successfulUnions++;return true;
 }
 public int components(){return components;}
}
