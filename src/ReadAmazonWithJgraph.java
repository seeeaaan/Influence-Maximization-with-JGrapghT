import java.io.BufferedReader;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import util.Node;

import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.AbstractBaseGraph;

/**
 * @author Xiayu Xiang
 *
 */
public class ReadAmazonWithJgraph {

 public DefaultDirectedWeightedGraph<String,DefaultWeightedEdge> loadGraph() {
	 
		DefaultDirectedWeightedGraph<String,DefaultWeightedEdge> g =
				new DefaultDirectedWeightedGraph<String,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		//Adjacent lists
	    ArrayList<String> nodeList = new ArrayList<String>();
		
	try {
			 FileInputStream fstream = new FileInputStream("data/Amazon0302.txt");  
			 //obtains input bytes from a file in a file system
			 DataInputStream in = new DataInputStream(fstream);
			 //lets an application read primitive Java data types from an underlying input stream
			 BufferedReader br = new BufferedReader(new InputStreamReader(in));
			 //reads text from a character-input stream, buffering characters so as to provide for the efficient reading of characters, arrays, and lines
			 
			 String strLine;
			 
			 ClassBasedEdgeFactory<String, DefaultWeightedEdge> ef = 
					 new ClassBasedEdgeFactory<>(DefaultWeightedEdge.class);
			 //an EdgeFactory for producing edges by using a class as a factory
			 
		try {
				//skip the first line
				strLine = br.readLine();
				
				while ((strLine = br.readLine()) != null) {
					String[] str = strLine.split("	"); //space
//					System.out.println(str[0] + ' ' + str[1]);
					String source = str[0];
					String target = str[1];					
					g.addVertex(source);
					g.addVertex(target);
					DefaultWeightedEdge edge = ef.createEdge(source, target);
					g.addEdge(source, target, edge);
					//add specific weightings to the edges
					//reference to TRIVALENCY model, return a random value from 0.1, 0.01, 0.001
					int p = (int)(Math.random()*3);
					double[] weight = {0.1,0.01,0.001};
					g.setEdgeWeight(edge, weight[p]);
					
//					for(String vertex: g.vertexSet()){
//	        			nodeList.add(vertex);
//	        		}
//					continue;
					
				}
				//closes this stream and releases any system resources associated with this stream
				br.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//optional to handle the exception here
				e.printStackTrace();
			}
			 
	  } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return g;
 }
 
 
    /**
	 * @return the nodeList
	 */
//	public ArrayList<String> getNodeList() {
//		return nodeList;
//	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ReadAmazonWithJgraph readAmazonData1 = new ReadAmazonWithJgraph();
		DefaultDirectedWeightedGraph<String,DefaultWeightedEdge> g = readAmazonData1.loadGraph();
		System.out.println(g.vertexSet().size());
		System.out.println(g.edgeSet().size());
		
		
//		HashSet<String> hashset = new HashSet<>();
//		String a = "1";
//		hashset.add(a);
	}

}
