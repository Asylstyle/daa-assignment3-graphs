package benchmark;
import graph.Graph;import java.util.Random;
public final class Generators {
 private Generators(){}
 private static int[] permutation(int n,Random r){
  int[] a=new int[n];for(int i=0;i<n;i++)a[i]=i;
  for(int i=n-1;i>0;i--){int j=r.nextInt(i+1),t=a[i];a[i]=a[j];a[j]=t;}return a;
 }
 public static Graph generate(int n,int m,String kind){
  if(n<2||m<0)throw new IllegalArgumentException("Generator requires n>=2 and m>=0");
  Random r=new Random(42);boolean undirected=kind.equals("undirected");
  if(!undirected&&!kind.equals("directed")&&!kind.equals("dag"))throw new IllegalArgumentException("Unknown graph kind");
  Graph g=new Graph(n,!undirected);int[] rank=null;
  if(undirected){
   if(m<n-1)throw new IllegalArgumentException("Connected generator needs n-1 edges");
   int[] perm=permutation(n,r);
   for(int i=1;i<n;i++){int a=perm[r.nextInt(i)];g.addEdge(a,perm[i],1+r.nextInt(1000));}
  }else if(kind.equals("dag"))rank=permutation(n,r);
  while(g.m()<m){
   int a=r.nextInt(n),b=r.nextInt(n);
   while(b==a)b=r.nextInt(n);
   if(rank!=null&&rank[a]>rank[b]){int t=a;a=b;b=t;}
   g.addEdge(a,b,1+r.nextInt(1000));
  }
  return g;
 }
}
