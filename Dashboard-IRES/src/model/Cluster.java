package model;
import java.util.*;

public class Cluster {
	private List<Robot> robots;
	private char[] id;
	private byte IR;
	
	public Cluster(char[] id){
		this.id = id;
		robots = new LinkedList<Robot>();
		IR = 0;
	}
	
	public List<Robot> getRobots(){
		return robots;
	}
	
	public char[] getID(){
		return id;
	}
	
	public byte getIR(){
		return IR;
	}
	
	public void setIR(byte IR){
		this.IR = IR;
	}
}