package model;

import java.util.concurrent.ConcurrentHashMap;

public class Data {
	private ConcurrentHashMap<String, Cluster> clusters;
	private ConcurrentHashMap<String, Robot> robots;
	
	public Data(ConcurrentHashMap<String, Cluster> clusters, ConcurrentHashMap<String, Robot> robots){
		this.clusters = clusters;
		this.robots = robots;
	}
	
	public ConcurrentHashMap<String, Cluster> getClusters(){
		return clusters;
	}
	
	public ConcurrentHashMap<String, Robot> getRobots(){
		return robots;
	}
}
