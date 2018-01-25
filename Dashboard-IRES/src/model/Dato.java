package model;

import java.io.Serializable;
import java.util.HashMap;

public class Dato implements Serializable {
	/**
	 * Serializable object (so it can be sent through sockets
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<String, Cluster> clusters;
	private HashMap<String, Robot> robots;
	
	public Dato(HashMap<String, Cluster> clusters, HashMap<String, Robot> robots){
		this.clusters = clusters;
		this.robots = robots;
	}
	
	public HashMap<String, Cluster> getClusters(){
		return clusters;
	}
	
	public HashMap<String, Robot> getRobots(){
		return robots;
	}
	
}
