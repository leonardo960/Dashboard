package model;
import java.util.*;

public class Cluster {
	private List<Robot> robots;
	private String id;
	private byte IR;
	short robotDown;
	public Cluster(String id){
		this.id = id;
		robots = new LinkedList<Robot>();
		IR = 0;
		robotDown = 0;
	}
	
	public List<Robot> getRobots(){
		return robots;
	}
	
	public String getID(){
		return id;
	}
	
	public byte getIR(){
		return IR;
	}
	
	public void setIR(byte IR){
		this.IR = IR;
	}
	
	public short getRobotDown(){
		return robotDown;
	}
	
	public void incrementRobotDown(){
		robotDown++;
	}
	
	public void decrementRobotDown(){
		robotDown--;
	}
}
