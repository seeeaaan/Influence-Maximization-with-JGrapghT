import java.util.ArrayList;
import util.*;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.AbstractBaseGraph;

public class InfluenceHeuristics {

	 ArrayList<Node> nodeList;
	 int k;
	 int[] seeds;

	 //initialize
	 public InfluenceHeuristics(int sizeOfSeedSet) {
		super();
		//read hep
//		ReadHep readHepData = new ReadHep();
//		readHepData.run();
//		this.nodeList = readHepData.getNodeList();
		
		//read phy
//		ReadPhy readPhyData = new ReadPhy();
//		readPhyData.run();
//		this.nodeList = readPhyData.getNodeList();
		
		//read dblp
//		ReadDblp readDblpData = new ReadDblp();
//		readDblpData.run();
//		this.nodeList = readDblpData.getNodeList();
		
		//read Epinions
//		ReadEpinions readEpinionsData = new ReadEpinions();
//		readEpinionsData.run();
//		this.nodeList = readEpinionsData.getNodeList();
		
		//read Amazon (Directed Graph)
	    ReadAmazon readAmazonData = new ReadAmazon();
		readAmazonData.run();
		this.nodeList = readAmazonData.getNodeList();
//		ReadAmazonWithJgraph readAmazonData = new ReadAmazonWithJgraph();
//		DefaultDirectedWeightedGraph<String,DefaultWeightedEdge> g = readAmazonData.loadGraph();
//		System.out.println(g.vertexSet().size());
//		System.out.println(g.edgeSet().size());
		
		//number of seeds chosen, input at main
		k = sizeOfSeedSet;
		seeds = new int[k];
		//initialize all the seed set to zero
		for(int i=0; i<k; i++){
			seeds[i] = 0;
		}
	}
	 
	 public void run(){
		 MaxDegreeSelectSeed();
         //RandomSelectSeed();
		 DegreeDiscountIC();
		 //OriginalGreedy();
		 InfluenceSpread();
	 }
	 
	 
	 //a set S with k seeds with maximum degree
	 public void MaxDegreeSelectSeed(){
		 for(int i=0; i<k; i++){
			 int maxDegree = 0;
			 int tempID = 0;
			 for(Node n: nodeList){
				 if(!n.isSeed()&&n.getNeighborList().size()>maxDegree){
					 maxDegree = n.getNeighborList().size();
					 tempID = n.getNodeID(); 
				 }
			 }
			 seeds[i] = tempID;
			 nodeList.get(tempID).setSeed(true);
			 nodeList.get(tempID).setActive(true);
			 System.out.println("Seed: " + i + " : " + tempID + " degree: " + nodeList.get(tempID).getNeighborList().size());
			 //output test
			 System.out.println("Seed : " + i + " " + seeds[i]);
		 }
	 }
	 
	 //random select
//	 public void RandomSelectSeed(){
//		 for(int i=0; i<k; i++){
//			 int tempID = (int)(Math.random()*(nodeList.size()));
//			 while(nodeList.get(tempID).isSeed()){
//				 tempID = (int)(Math.random()*(nodeList.size()));
//			 }
//			 seeds[i] = tempID;
//			 nodeList.get(tempID).setSeed(true);
//			 nodeList.get(tempID).setActive(true);
//			 System.out.println("Seed: " + i + " : " + tempID + " degree: " + nodeList.get(tempID).getNeighborList().size());
//			 System.out.println("seed: " +  i + " "+ tempID );
//		 }
//	 }

	 //Degree Discount IC
	 public void DegreeDiscountIC(){
	   
		 for(Node n:nodeList){
//			 Node node = new Node(vertex);
			//initialize on first iteration
			 n.setDegree(1.0f);
			 n.setActiveNeighbors(0);
		 }
		 for(int i=0; i<k; i++){
			 DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> dirGraph = null;
			int tempID = PageRank(dirGraph);
			 seeds[i] = tempID;
			 nodeList.get(tempID).setSeed(true);
			 nodeList.get(tempID).setActive(true);
//			 System.out.println("Seed: " + i + " : " + tempID + " degree: " + nodeList.get(tempID).getNeighborList().size() + " dd: " +  nodeList.get(tempID).getDegree());
			 DegreeDiscountProcess(tempID);
		 }
	 }
	 
	 public int PageRank(DefaultDirectedWeightedGraph<String,DefaultWeightedEdge> g){
	     int tempID = 0;
	     float tempDegree = 0;
	     /* 
	     On other iterations, set my value to be the weighted
         average of my in-coming neighbors PageRanks.
         */
	     for(String vertex : g.vertexSet()){
	      float sum = 0.f;
	      
	      Node node = new Node(vertex);
	      
	      //Create an inNeighborList from set
	      
	      for(Neighbor n : node.getNeighborList()){
	    	 sum += n.getWeight();
	      }
	      node.setDegree(0.15f + 0.85f * sum);//set directed edge weight
	      
		  //Write my value (divided by my out-degree) to my out-edges so neighbors can read it.
	      float outValue = node.getDegree() / g.outgoingEdgesOf(vertex).size();
	      
	      //Create an outNeighborList from set
	      
	      for(Neighbor n : node.getNeighborList()){
	    	    n.setWeight(outValue);
	      }
	      
	      //Select the node with highest PageRank
		  if( !node.isSeed() && node.getDegree() > tempDegree){
				 tempDegree = node.getDegree();
				 tempID = node.getNodeID();
		  }
	     }
		return tempID;
	 }
	 
	 
	 public void DegreeDiscountProcess(int nodeId){
		 for(Neighbor e: nodeList.get(nodeId).getNeighborList()){
			 Node n = nodeList.get(e.getNodeId());
			 if(!n.isSeed()){
				 n.setActiveNeighbors(n.getActiveNeighbors()+1);
				 n.setDegree((int)(n.getNeighborList().size() - 2 * n.getActiveNeighbors() -(n.getNeighborList().size() -  n.getActiveNeighbors())* n.getActiveNeighbors()*n.getNeighborList().get(0).getWeight()));
			 }
		 }
	 }
	 
	 
	 //original greedy
//	 public void OriginalGreedy(){
//		 for(int i=0; i<k; i++){
//			 int tempID = 0;
//			 tempID = MaxIncreaseSpread(i);
//			 seeds[i] = tempID;
//			 nodeList.get(tempID).setSeed(true);
//			 System.out.println("Seed: " + i + " : " + tempID + " degree: " + nodeList.get(tempID).getNeighborList().size());
//			 //Spread(tempID);
//		 }
//		 clearActive();
//		 for(int i=0; i<k; i++){
//			 nodeList.get(seeds[i]).setSeed(true);
//			 nodeList.get(seeds[i]).setActive(true);
//		 }
//	 }
//	 
//	 
//	 public int MaxIncreaseSpread(int number){
//		 int tempID = 0;
//		 int repetition = 100;
//		 long spreadNum = 0;
//		 for(Node n: nodeList){
//			 if(!n.isSeed()){
//				 long temp = 0;
//				 for(int i=0; i<repetition; i++){
//					 clearTempActive();
//					 temp += InfluenceSpreadTrial(n.getNodeID(), number);
//				 }
//				 temp = temp / repetition;
//				 if(temp > spreadNum){
//					 tempID = n.getNodeID();
//					 spreadNum = temp;
//				 }
//			 }
//		 }
//		 System.out.println("spreadNum: " + spreadNum);
//		 return tempID;
//	 }
	 
	 public int InfluenceSpreadTrial(int nodeID, int num){
		 for(int i=0; i<num; i++){
			 SpreadTrial(seeds[i]);
		 }
		 SpreadTrial(nodeID);
		 
		 //Statistics
		 int activeNum = 0;
		 int inactiveNum = 0;
		 for(Node n: nodeList){
			 if(n.isTempActive()&&!n.isActive()){
				 activeNum++;
			 }else{
				 inactiveNum++;
			 }
		 }
		 return activeNum;
	 }
	 
	 public void SpreadTrial(int nodeId){
		 for(Neighbor e: nodeList.get(nodeId).getNeighborList()){
			 double r = Math.random();
			 //if the neighbor is not active and r<e then activate it.
			 if(!nodeList.get(e.getNodeId()).isTempActive()&&!nodeList.get(e.getNodeId()).isActive()&&r<=e.getWeight()){
				 nodeList.get(e.getNodeId()).setTempActive(true);
				 SpreadTrial(e.getNodeId());
			 }
		 }
	 }
	 
	 public void clearActive(){
		 for(Node n: nodeList){
			 n.setActive(false);
		 }
	 }
	 
	 public void clearTempActive(){
		 for(Node n: nodeList){
			 n.setTempActive(false);
		 }
	 }
	 
	 public int InfluenceSpread(){
		 for(int i=0; i<k; i++){
			 Spread(seeds[i]);
		 }
		 
		 //Statistics
		 int activeNum = 0;
		 int inactiveNum = 0;
		 for(Node n: nodeList){
			 if(n.isActive()){
				 activeNum++;
			 }else{
				 inactiveNum++;
			 }
		 }
		 
		 double ratio = (double)activeNum/(activeNum + inactiveNum);
		 System.out.println("Spread: " + activeNum);
		 return activeNum;
	 }
	 
	 
	 public void Spread(int nodeId){
		 for(Neighbor neighbor: nodeList.get(nodeId).getNeighborList()){
			 double random = Math.random();
			 //if the neighbor is not active and random < neighbor then activate it.
			 if(!nodeList.get(neighbor.getNodeId()).isActive() && random <= neighbor.getWeight())
             {
				 nodeList.get(neighbor.getNodeId()).setActive(true);
				 Spread(neighbor.getNodeId());
			 }
		 }
	 }

    //IC Model
	//
	 public static void main(String args[])
     {

		 int spread = 0;
		 int reps = 10;//number of iterations
		 for(int i=0; i<reps; i++){
		 	InfluenceHeuristics go = new InfluenceHeuristics(30);//number of seeds chosen
		 	go.run();//run() method to activate degree discount process
			spread += go.InfluenceSpread();
		 } 
		 spread = spread/reps;
		 System.out.println("Influence Spread: " + spread);
	}
}
